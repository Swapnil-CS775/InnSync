package com.innsync.order.controller;

import com.innsync.order.dto.AddItemRequestDto;
import com.innsync.order.dto.OrderResponseDto;
import com.innsync.order.dto.OtpRequestDto;
import com.innsync.order.dto.OtpVerificationRequestDto;
import com.innsync.order.dto.StartOrderResponseDto;
import com.innsync.order.service.OrderService;
import com.innsync.order.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/start/{qrCodeIdentifier}")
    public ResponseEntity<StartOrderResponseDto> startOrder(@PathVariable String qrCodeIdentifier) {
        StartOrderResponseDto response = orderService.startOrder(qrCodeIdentifier);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/request-otp")
    public ResponseEntity<Void> requestOtp(
            @RequestHeader("Authorization") String guestToken,
            @RequestBody OtpRequestDto otpRequestDto) {
        
        String jwt = guestToken.substring(7);
        Long orderId = jwtUtil.extractClaim(jwt, claims -> claims.get("orderId", Long.class));
        
        orderService.requestOtp(orderId, otpRequestDto.getPhoneNumber());
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(
            @RequestHeader("Authorization") String guestToken,
            @RequestBody OtpVerificationRequestDto verificationRequestDto) {
            
        String jwt = guestToken.substring(7);
        Long orderId = jwtUtil.extractClaim(jwt, claims -> claims.get("orderId", Long.class));
        
        boolean isVerified = orderService.verifyOtp(orderId, verificationRequestDto.getOtp());
        
        return ResponseEntity.ok(isVerified);
        
    }
    
    @PostMapping("/add-item")
    public ResponseEntity<Void> addItemToOrder(
            @RequestHeader("Authorization") String guestToken,
            @RequestBody AddItemRequestDto addItemRequestDto) {

        String jwt = guestToken.substring(7);
        Long orderId = jwtUtil.extractClaim(jwt, claims -> claims.get("orderId", Long.class));
        Long tenantId = jwtUtil.extractClaim(jwt, claims -> claims.get("tenantId", Long.class));

        orderService.addItemToOrder(orderId, tenantId, addItemRequestDto);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/my-order")
    public ResponseEntity<OrderResponseDto> getMyOrder(
            @RequestHeader("Authorization") String guestToken) {

        String jwt = guestToken.substring(7);
        Long orderId = jwtUtil.extractClaim(jwt, claims -> claims.get("orderId", Long.class));
        Long tenantId = jwtUtil.extractClaim(jwt, claims -> claims.get("tenantId", Long.class));

        OrderResponseDto orderDetails = orderService.getOrderById(orderId, tenantId);
        return ResponseEntity.ok(orderDetails);
    }
    
    @PostMapping("/request-bill")
    public ResponseEntity<Void> requestBill(@RequestHeader("Authorization") String guestToken) {
        String jwt = guestToken.substring(7);
        Long orderId = jwtUtil.extractClaim(jwt, claims -> claims.get("orderId", Long.class));
        Long tenantId = jwtUtil.extractClaim(jwt, claims -> claims.get("tenantId", Long.class));

        orderService.requestBill(orderId, tenantId);
        return ResponseEntity.ok().build();
    }
}