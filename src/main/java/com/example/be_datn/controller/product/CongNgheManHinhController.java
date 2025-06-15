package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.CongNgheManHinhRequest;
import com.example.be_datn.dto.product.response.CongNgheManHinhResponse;
import com.example.be_datn.service.product.CongNgheManHinhService;
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
@RequestMapping("/api/cong-nghe-man-hinh")
@RequiredArgsConstructor
@Slf4j
public class CongNgheManHinhController {

    private final CongNgheManHinhService service;

    @GetMapping
    public ResponseEntity<Page<CongNgheManHinhResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all display technologies - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<CongNgheManHinhResponse> result = service.getAllCongNgheManHinh(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CongNgheManHinhResponse>> getAllList() {
        log.info("Getting all display technologies as list");
        List<CongNgheManHinhResponse> result = service.getAllCongNgheManHinhList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting display technology by id: {}", id);
        try {
            CongNgheManHinhResponse congNgheManHinh = service.getCongNgheManHinhById(id);
            return ResponseEntity.ok(congNgheManHinh);
        } catch (RuntimeException e) {
            log.error("Error getting display technology by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CongNgheManHinhRequest request,
            BindingResult result) {
        log.info("Creating new display technology with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CongNgheManHinhResponse created = service.createCongNgheManHinh(request);
            log.info("Successfully created display technology with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating display technology: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody CongNgheManHinhRequest request,
            BindingResult result) {
        log.info("Updating display technology with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CongNgheManHinhResponse updated = service.updateCongNgheManHinh(id, request);
            log.info("Successfully updated display technology with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating display technology with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting display technology with id: {}", id);
        try {
            service.deleteCongNgheManHinh(id);
            log.info("Successfully deleted display technology with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting display technology with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CongNgheManHinhResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String congNgheManHinh,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching display technologies - keyword: {}, congNgheManHinh: {}, page: {}, size: {}",
                keyword, congNgheManHinh, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (congNgheManHinh != null && !congNgheManHinh.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByCongNgheManHinh(congNgheManHinh.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchCongNgheManHinh(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllCongNgheManHinh(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllCongNgheManHinhNames() {
        log.info("Getting all display technology names");
        List<String> names = service.getAllCongNgheManHinhNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if display technology code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/cong-nghe-man-hinh")
    public ResponseEntity<Boolean> checkCongNgheManHinhExists(
            @RequestParam String congNgheManHinh,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if display technology name exists: {}, excludeId: {}", congNgheManHinh, excludeId);
        boolean exists = service.existsByCongNgheManHinh(congNgheManHinh, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting display technology statistics");
        try {
            List<CongNgheManHinhResponse> allDisplayTechnologies = service.getAllCongNgheManHinhList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allDisplayTechnologies.size());
            stats.put("active", allDisplayTechnologies.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting display technology statistics: {}", e.getMessage());
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