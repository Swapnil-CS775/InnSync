package com.innsync.order.service;

import com.innsync.order.dto.TableRequestDto;
import com.innsync.order.dto.TableResponseDto;
import java.util.List;

public interface TableService {
    TableResponseDto createTable(Long tenantId, TableRequestDto requestDto);
    List<TableResponseDto> getTablesByTenant(Long tenantId);
    void deleteTable(Long tenantId, Long tableId);
}