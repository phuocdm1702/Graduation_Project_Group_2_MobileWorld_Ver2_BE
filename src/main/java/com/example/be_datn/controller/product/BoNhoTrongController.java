package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.BoNhoTrongRequest;
import com.example.be_datn.dto.product.response.BoNhoTrongResponse;
import com.example.be_datn.service.product.BoNhoTrongService;
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
@RequestMapping("/api/bo-nho-trong")
@RequiredArgsConstructor
@Slf4j
public class BoNhoTrongController {

    private final BoNhoTrongService service;

    @GetMapping
    public ResponseEntity<Page<BoNhoTrongResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all storage capacities - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<BoNhoTrongResponse> result = service.getAllBoNhoTrong(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BoNhoTrongResponse>> getAllList() {
        log.info("Getting all storage capacities as list");
        List<BoNhoTrongResponse> result = service.getAllBoNhoTrongList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting storage capacity by id: {}", id);
        try {
            BoNhoTrongResponse boNhoTrong = service.getBoNhoTrongById(id);
            return ResponseEntity.ok(boNhoTrong);
        } catch (RuntimeException e) {
            log.error("Error getting storage capacity by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody BoNhoTrongRequest request,
            BindingResult result) {
        log.info("Creating new storage capacity");

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            BoNhoTrongResponse created = service.createBoNhoTrong(request);
            log.info("Successfully created storage capacity with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating storage capacity: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody BoNhoTrongRequest request,
            BindingResult result) {
        log.info("Updating storage capacity with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            BoNhoTrongResponse updated = service.updateBoNhoTrong(id, request);
            log.info("Successfully updated storage capacity with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating storage capacity with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BoNhoTrongResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching storage capacities - keyword: {} page: {}, size: {}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchBoNhoTrong(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllBoNhoTrong(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting storage capacity statistics");
        try {
            List<BoNhoTrongResponse> allStorageCapacities = service.getAllBoNhoTrongList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allStorageCapacities.size());
            stats.put("active", allStorageCapacities.stream()
                    .filter(b -> !b.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting storage capacity statistics: {}", e.getMessage());
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