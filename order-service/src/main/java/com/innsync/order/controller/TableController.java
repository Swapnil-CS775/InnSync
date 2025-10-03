package com.innsync.order.controller;

import com.innsync.order.dto.TableRequestDto;
import com.innsync.order.dto.TableResponseDto;
import com.innsync.order.service.TableService;
import com.innsync.order.util.JwtUtil; // Assuming you copied this from another service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    @Autowired
    private JwtUtil jwtUtil; // Assuming you copied and configured this

    @PostMapping
    public ResponseEntity<TableResponseDto> createTable(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TableRequestDto requestDto) {
        
        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        TableResponseDto createdTable = tableService.createTable(tenantId, requestDto);
        return new ResponseEntity<>(createdTable, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TableResponseDto>> getTablesForTenant(
            @RequestHeader("Authorization") String token) {
        
        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        List<TableResponseDto> tables = tableService.getTablesByTenant(tenantId);
        return ResponseEntity.ok(tables);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        
        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        tableService.deleteTable(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}