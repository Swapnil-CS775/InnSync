package com.innsync.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurant_tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The name/number of the table, e.g., "Table 5" or "Patio-2".
    @Column(nullable = false, name = "table_number")
    private String tableNumber;

    // A unique identifier (like a UUID) that will be encoded into the QR code URL.
    @Column(unique = true, nullable = false, name = "qr_code_identifier")
    private String qrCodeIdentifier;

    // The multi-tenancy key, linking this table to a specific business.
    @Column(nullable = false, name = "tenant_id")
    private Long tenantId;
}