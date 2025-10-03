package com.innsync.order.repository;

import com.innsync.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // We don't need custom methods here yet, as we will access items through the Order entity.
}