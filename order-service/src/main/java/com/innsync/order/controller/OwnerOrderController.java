package com.innsync.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innsync.order.dto.OrderResponseDto;
import com.innsync.order.service.OrderService;
import com.innsync.order.util.JwtUtil;

// ... imports ...

@RestController
@RequestMapping("/api/owner/orders") // Note the new "/owner" path
public class OwnerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/live")
    public ResponseEntity<List<OrderResponseDto>> getLiveOrders(
            @RequestHeader("Authorization") String ownerToken) {

        String jwt = ownerToken.substring(7);
        // This token is from an OWNER, so it has the tenantId directly
        Long tenantId = jwtUtil.extractTenantId(jwt);

        List<OrderResponseDto> liveOrders = orderService.getLiveOrdersByTenant(tenantId);
        return ResponseEntity.ok(liveOrders);
    }
}