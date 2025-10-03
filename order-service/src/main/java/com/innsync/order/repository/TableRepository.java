package com.innsync.order.repository;

import com.innsync.order.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    
    List<RestaurantTable> findAllByTenantId(Long tenantId);
    
    Optional<RestaurantTable> findByQrCodeIdentifier(String qrCodeIdentifier);

    
}