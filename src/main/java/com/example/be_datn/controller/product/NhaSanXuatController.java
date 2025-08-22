package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.NhaSanXuatRequest;
import com.example.be_datn.dto.product.response.NhaSanXuatResponse;
import com.example.be_datn.service.product.NhaSanXuatService;
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
@RequestMapping("/api/nha-san-xuat")
@RequiredArgsConstructor
@Slf4j
public class NhaSanXuatController {

    private final NhaSanXuatService service;

    @GetMapping
    public ResponseEntity<Page<NhaSanXuatResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all manufacturers - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<NhaSanXuatResponse> result = service.getAllNhaSanXuat(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<NhaSanXuatResponse>> getAllList() {
        log.info("Getting all manufacturers as list");
        List<NhaSanXuatResponse> result = service.getAllNhaSanXuatList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting manufacturer by id: {}", id);
        try {
            NhaSanXuatResponse nhaSanXuat = service.getNhaSanXuatById(id);
            return ResponseEntity.ok(nhaSanXuat);
        } catch (RuntimeException e) {
            log.error("Error getting manufacturer by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody NhaSanXuatRequest request,
            BindingResult result) {
        log.info("Creating new manufacturer");

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            NhaSanXuatResponse created = service.createNhaSanXuat(request);
            log.info("Successfully created manufacturer with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating manufacturer: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody NhaSanXuatRequest request,
            BindingResult result) {
        log.info("Updating manufacturer with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            NhaSanXuatResponse updated = service.updateNhaSanXuat(id, request);
            log.info("Successfully updated manufacturer with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating manufacturer with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NhaSanXuatResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching manufacturers - keyword: {} page: {}, size: {}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchNhaSanXuat(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllNhaSanXuat(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting manufacturer statistics");
        try {
            List<NhaSanXuatResponse> allManufacturers = service.getAllNhaSanXuatList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allManufacturers.size());
            stats.put("active", allManufacturers.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting manufacturer statistics: {}", e.getMessage());
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