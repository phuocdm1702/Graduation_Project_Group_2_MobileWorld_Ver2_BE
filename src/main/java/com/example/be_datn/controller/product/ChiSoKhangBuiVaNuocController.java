package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.ChiSoKhangBuiVaNuocRequest;
import com.example.be_datn.dto.product.response.ChiSoKhangBuiVaNuocResponse;
import com.example.be_datn.service.product.ChiSoKhangBuiVaNuocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chi-so-khang-bui-va-nuoc")
@RequiredArgsConstructor
@Slf4j
public class ChiSoKhangBuiVaNuocController {

    private final ChiSoKhangBuiVaNuocService service;

    @GetMapping
    public ResponseEntity<Page<ChiSoKhangBuiVaNuocResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all dust and water resistance indices - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ChiSoKhangBuiVaNuocResponse> result = service.getAllChiSoKhangBuiVaNuoc(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChiSoKhangBuiVaNuocResponse>> getAllList() {
        log.info("Getting all dust and water resistance indices as list");
        List<ChiSoKhangBuiVaNuocResponse> result = service.getAllChiSoKhangBuiVaNuocList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting dust and water resistance index by id: {}", id);
        try {
            ChiSoKhangBuiVaNuocResponse chiSo = service.getChiSoKhangBuiVaNuocById(id);
            return ResponseEntity.ok(chiSo);
        } catch (RuntimeException e) {
            log.error("Error getting dust and water resistance index by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody ChiSoKhangBuiVaNuocRequest request,
            BindingResult result) {
        log.info("Creating new dust and water resistance index");

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            ChiSoKhangBuiVaNuocResponse created = service.createChiSoKhangBuiVaNuoc(request);
            log.info("Successfully created dust and water resistance index with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating dust and water resistance index: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody ChiSoKhangBuiVaNuocRequest request,
            BindingResult result) {
        log.info("Updating dust and water resistance index with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            ChiSoKhangBuiVaNuocResponse updated = service.updateChiSoKhangBuiVaNuoc(id, request);
            log.info("Successfully updated dust and water resistance index with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating dust and water resistance index with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ChiSoKhangBuiVaNuocResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching dust and water resistance indices - keyword: {}, page: {}, size: {}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchChiSoKhangBuiVaNuoc(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllChiSoKhangBuiVaNuoc(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting dust and water resistance index statistics");
        try {
            List<ChiSoKhangBuiVaNuocResponse> allIndices = service.getAllChiSoKhangBuiVaNuocList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allIndices.size());
            stats.put("active", allIndices.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting dust and water resistance index statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể lấy thống kê"));
        }
    }

    private Map<String, String> getErrorMap(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        result.getGlobalErrors().forEach(error ->
                errors.put("global", error.getDefaultMessage()));
        return errors;
    }
}