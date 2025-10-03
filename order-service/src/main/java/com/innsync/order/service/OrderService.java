package com.innsync.order.service;

import java.util.List;

import com.innsync.order.dto.AddItemRequestDto;
import com.innsync.order.dto.OrderResponseDto;
import com.innsync.order.dto.StartOrderResponseDto;

public interface OrderService {
    StartOrderResponseDto startOrder(String qrCodeIdentifier);
    void requestOtp(Long orderId, String phoneNumber);
    boolean verifyOtp(Long orderId, String otp);
    void addItemToOrder(Long orderId, Long tenantId, AddItemRequestDto addItemRequestDto);
    OrderResponseDto getOrderById(Long orderId, Long tenantId);
    void requestBill(Long orderId, Long tenantId);
    List<OrderResponseDto> getLiveOrdersByTenant(Long tenantId);
}