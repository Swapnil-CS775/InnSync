package com.innsync.order.service.impl;

import com.innsync.order.dto.*;
import com.innsync.order.entity.Order;
import com.innsync.order.entity.OrderItem;
import com.innsync.order.entity.OrderStatus;
import com.innsync.order.entity.RestaurantTable;
import com.innsync.order.exception.ResourceNotFoundException;
import com.innsync.order.exception.UnauthorizedAccessException;
import com.innsync.order.repository.OrderRepository;
import com.innsync.order.repository.TableRepository;
import com.innsync.order.service.OrderService;
import com.innsync.order.service.SmsService;
import com.innsync.order.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- CORRECT IMPORT
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${menu.service.url}")
    private String menuServiceUrl;

    @Override
    @Transactional
    public StartOrderResponseDto startOrder(String qrCodeIdentifier) {
        RestaurantTable table = tableRepository.findByQrCodeIdentifier(qrCodeIdentifier)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR Code: Table not found."));

        Order newOrder = new Order();
        newOrder.setTable(table);
        newOrder.setStatus(OrderStatus.OPEN);
        newOrder.setTenantId(table.getTenantId());
        newOrder.setTotalAmount(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(newOrder);
        String guestToken = jwtUtil.generateGuestToken(savedOrder);

        return new StartOrderResponseDto(savedOrder.getId(), guestToken);
    }

    @Override
    @Transactional
    public void requestOtp(Long orderId, String phoneNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        String otp = new DecimalFormat("000000").format(new SecureRandom().nextInt(999999));
        
        order.setOtp(otp);
        order.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        order.setCustomerPhone(phoneNumber);
        orderRepository.save(order);
        
        smsService.sendOtp(phoneNumber, otp);
    }

    @Override
    @Transactional
    public boolean verifyOtp(Long orderId, String otp) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getOtp() != null && order.getOtp().equals(otp) && order.getOtpExpiry().isAfter(LocalDateTime.now())) {
            order.setOtp(null);
            order.setOtpExpiry(null);
            orderRepository.save(order);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public void addItemToOrder(Long orderId, Long tenantId, AddItemRequestDto addItemRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getTenantId().equals(tenantId)) {
            throw new UnauthorizedAccessException("Not authorized for this order.");
        }
        
        // Construct the new, secure URL to call the menu-service
        String url = menuServiceUrl + "/items/public/" + tenantId + "/items/" + addItemRequestDto.getMenuItemId();
        ItemResponseDto itemFromMenu = restTemplate.getForObject(url, ItemResponseDto.class);

        if (itemFromMenu == null) {
            throw new ResourceNotFoundException("Menu item with id " + addItemRequestDto.getMenuItemId() + " not found.");
        }

        OrderItem newItem = new OrderItem();
        newItem.setMenuItemId(addItemRequestDto.getMenuItemId());
        newItem.setQuantity(addItemRequestDto.getQuantity());
        newItem.setItemName(itemFromMenu.getName());
        newItem.setPriceAtOrder(BigDecimal.valueOf(itemFromMenu.getPrice()));
        newItem.setOrder(order);

        order.getItems().add(newItem);

        // Calculate the new total amount
        BigDecimal newTotal = order.getItems().stream()
                .map(item -> item.getPriceAtOrder().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(newTotal);
        System.out.println("price is "+newTotal);
        // No need to call save() here, @Transactional handles it.
        orderRepository.save(order);

    }
    
    @Override
    public OrderResponseDto getOrderById(Long orderId, Long tenantId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getTenantId().equals(tenantId)) {
            throw new UnauthorizedAccessException("Not authorized to view this order.");
        }
        return mapToOrderResponseDto(order);
    }
    
    @Override
    @Transactional
    public void requestBill(Long orderId, Long tenantId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getTenantId().equals(tenantId)) {
            throw new UnauthorizedAccessException("Not authorized for this order.");
        }

        order.setStatus(OrderStatus.BILLED);
    }
    
    @Override
    public List<OrderResponseDto> getLiveOrdersByTenant(Long tenantId) {
        return orderRepository.findAllByTenantId(tenantId).stream()
                .filter(order -> order.getStatus() == OrderStatus.IN_PROGRESS || order.getStatus() == OrderStatus.BILLED)
                .map(this::mapToOrderResponseDto)
                .collect(Collectors.toList());
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTableId(order.getTable().getId());

        List<OrderItemResponseDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setId(item.getId());
            itemDto.setItemName(item.getItemName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtOrder(item.getPriceAtOrder());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}