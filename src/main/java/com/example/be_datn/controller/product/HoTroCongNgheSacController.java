package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.HoTroCongNgheSacRequest;
import com.example.be_datn.dto.product.response.HoTroCongNgheSacResponse;
import com.example.be_datn.service.product.HoTroCongNgheSacService;
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
@RequestMapping("/api/ho-tro-cong-nghe-sac")
@RequiredArgsConstructor
@Slf4j
public class HoTroCongNgheSacController {

    private final HoTroCongNgheSacService service;

    @GetMapping
    public ResponseEntity<Page<HoTroCongNgheSacResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all charging technologies - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<HoTroCongNgheSacResponse> result = service.getAllHoTroCongNgheSac(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HoTroCongNgheSacResponse>> getAllList() {
        log.info("Getting all charging technologies as list");
        List<HoTroCongNgheSacResponse> result = service.getAllHoTroCongNgheSacList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting charging technology by id: {}", id);
        try {
            HoTroCongNgheSacResponse hoTroCongNgheSac = service.getHoTroCongNgheSacById(id);
            return ResponseEntity.ok(hoTroCongNgheSac);
        } catch (RuntimeException e) {
            log.error("Error getting charging technology by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody HoTroCongNgheSacRequest request,
            BindingResult result) {
        log.info("Creating new charging technology");

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            HoTroCongNgheSacResponse created = service.createHoTroCongNgheSac(request);
            log.info("Successfully created charging technology with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating charging technology: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody HoTroCongNgheSacRequest request,
            BindingResult result) {
        log.info("Updating charging technology with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            HoTroCongNgheSacResponse updated = service.updateHoTroCongNgheSac(id, request);
            log.info("Successfully updated charging technology with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating charging technology with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HoTroCongNgheSacResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching charging technologies - keyword: {} page: {}, size: {}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchHoTroCongNgheSac(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllHoTroCongNgheSac(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting charging technology statistics");
        try {
            List<HoTroCongNgheSacResponse> allChargingTechnologies = service.getAllHoTroCongNgheSacList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allChargingTechnologies.size());
            stats.put("active", allChargingTechnologies.stream()
                    .filter(h -> !h.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting charging technology statistics: {}", e.getMessage());
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