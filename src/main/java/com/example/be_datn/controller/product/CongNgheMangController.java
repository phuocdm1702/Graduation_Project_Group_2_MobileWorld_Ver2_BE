package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.CongNgheMangRequest;
import com.example.be_datn.dto.product.response.CongNgheMangResponse;
import com.example.be_datn.service.product.CongNgheMangService;
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
@RequestMapping("/api/cong-nghe-mang")
@RequiredArgsConstructor
@Slf4j
public class CongNgheMangController {

    private final CongNgheMangService service;

    @GetMapping
    public ResponseEntity<Page<CongNgheMangResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all network technologies - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<CongNgheMangResponse> result = service.getAllCongNgheMang(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CongNgheMangResponse>> getAllList() {
        log.info("Getting all network technologies as list");
        List<CongNgheMangResponse> result = service.getAllCongNgheMangList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting network technology by id: {}", id);
        try {
            CongNgheMangResponse congNgheMang = service.getCongNgheMangById(id);
            return ResponseEntity.ok(congNgheMang);
        } catch (RuntimeException e) {
            log.error("Error getting network technology by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CongNgheMangRequest request,
            BindingResult result) {
        log.info("Creating new network technology");

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CongNgheMangResponse created = service.createCongNgheMang(request);
            log.info("Successfully created network technology with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating network technology: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody CongNgheMangRequest request,
            BindingResult result) {
        log.info("Updating network technology with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CongNgheMangResponse updated = service.updateCongNgheMang(id, request);
            log.info("Successfully updated network technology with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating network technology with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CongNgheMangResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching network technologies - keyword: {} page: {}, size: {}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchCongNgheMang(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllCongNgheMang(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting network technology statistics");
        try {
            List<CongNgheMangResponse> allNetworkTechnologies = service.getAllCongNgheMangList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allNetworkTechnologies.size());
            stats.put("active", allNetworkTechnologies.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting network technology statistics: {}", e.getMessage());
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