package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.MauSacRequest;
import com.example.be_datn.dto.product.response.MauSacResponse;
import com.example.be_datn.service.product.MauSacService;
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
@RequestMapping("/api/mau-sac")
@RequiredArgsConstructor
@Slf4j
public class MauSacController {

    private final MauSacService service;

    @GetMapping
    public ResponseEntity<Page<MauSacResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all colors - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<MauSacResponse> result = service.getAllMauSac(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MauSacResponse>> getAllList() {
        log.info("Getting all colors as list");
        List<MauSacResponse> result = service.getAllMauSacList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting color by id: {}", id);
        try {
            MauSacResponse mauSac = service.getMauSacById(id);
            return ResponseEntity.ok(mauSac);
        } catch (RuntimeException e) {
            log.error("Error getting color by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody MauSacRequest request,
            BindingResult result) {
        log.info("Creating new color with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            MauSacResponse created = service.createMauSac(request);
            log.info("Successfully created color with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating color: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody MauSacRequest request,
            BindingResult result) {
        log.info("Updating color with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            MauSacResponse updated = service.updateMauSac(id, request);
            log.info("Successfully updated color with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating color with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting color with id: {}", id);
        try {
            service.deleteMauSac(id);
            log.info("Successfully deleted color with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting color with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MauSacResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String mauSac,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching colors - keyword: {}, mauSac: {}, page: {}, size: {}",
                keyword, mauSac, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (mauSac != null && !mauSac.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByMauSac(mauSac.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchMauSac(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllMauSac(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllColorNames() {
        log.info("Getting all color names");
        List<String> names = service.getAllColorNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if color code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/mau-sac")
    public ResponseEntity<Boolean> checkMauSacExists(
            @RequestParam String mauSac,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if color name exists: {}, excludeId: {}", mauSac, excludeId);
        boolean exists = service.existsByMauSac(mauSac, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting color statistics");
        try {
            List<MauSacResponse> allColors = service.getAllMauSacList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allColors.size());
            stats.put("active", allColors.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting color statistics: {}", e.getMessage());
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