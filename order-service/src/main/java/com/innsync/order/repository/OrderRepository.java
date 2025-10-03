package com.innsync.order.repository;

import com.innsync.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findAllByTenantId(Long tenantId);
}