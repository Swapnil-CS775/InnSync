package com.innsync.order.service.impl;

import com.innsync.order.dto.TableRequestDto;
import com.innsync.order.dto.TableResponseDto;
import com.innsync.order.entity.RestaurantTable;
import com.innsync.order.repository.TableRepository;
import com.innsync.order.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableRepository tableRepository;

    @Override
    public TableResponseDto createTable(Long tenantId, TableRequestDto requestDto) {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(requestDto.getTableNumber());
        table.setTenantId(tenantId);
        // Generate a unique identifier for the QR code
        table.setQrCodeIdentifier(UUID.randomUUID().toString());

        RestaurantTable savedTable = tableRepository.save(table);
        return mapToDto(savedTable);
    }

    @Override
    public List<TableResponseDto> getTablesByTenant(Long tenantId) {
        return tableRepository.findAllByTenantId(tenantId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTable(Long tenantId, Long tableId) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        if (!table.getTenantId().equals(tenantId)) {
            throw new SecurityException("User not authorized to delete this table.");
        }
        tableRepository.delete(table);
    }

    private TableResponseDto mapToDto(RestaurantTable table) {
        TableResponseDto dto = new TableResponseDto();
        dto.setId(table.getId());
        dto.setTableNumber(table.getTableNumber());
        dto.setQrCodeIdentifier(table.getQrCodeIdentifier());
        dto.setTenantId(table.getTenantId());
        return dto;
    }
}