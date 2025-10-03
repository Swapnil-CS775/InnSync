package com.innsync.order.entity;

public enum OrderStatus {
    OPEN,        // The customer has an active tab but hasn't placed items yet
    IN_PROGRESS, // The customer has placed items, kitchen is working
    BILLED,      // The customer has requested the bill
    PAID,        // The bill has been successfully paid
    CANCELLED    // The order was cancelled
}