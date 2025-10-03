package com.innsync.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The ID from the menu-service, stored for reference
    @Column(nullable = false, name = "menu_item_id")
    private Long menuItemId;

    // The name of the item at the time of order
    @Column(nullable = false, name = "item_name")
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    // The price of a single item at the time of order
    @Column(nullable = false, name = "price_at_order", precision = 10, scale = 2)
    private BigDecimal priceAtOrder;

    // Many order items belong to one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}