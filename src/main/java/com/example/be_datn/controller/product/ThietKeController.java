package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.ThietKeRequest;
import com.example.be_datn.dto.product.response.ThietKeResponse;
import com.example.be_datn.service.product.ThietKeService;
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
@RequestMapping("/api/thiet-ke")
@RequiredArgsConstructor
@Slf4j
public class ThietKeController {

    private final ThietKeService service;

    @GetMapping
    public ResponseEntity<Page<ThietKeResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all designs - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ThietKeResponse> result = service.getAllThietKe(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ThietKeResponse>> getAllList() {
        log.info("Getting all designs as list");
        List<ThietKeResponse> result = service.getAllThietKeList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting design by id: {}", id);
        try {
            ThietKeResponse thietKe = service.getThietKeById(id);
            return ResponseEntity.ok(thietKe);
        } catch (RuntimeException e) {
            log.error("Error getting design by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody ThietKeRequest request,
            BindingResult result) {
        log.info("Creating new design with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            ThietKeResponse created = service.createThietKe(request);
            log.info("Successfully created design with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating design: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody ThietKeRequest request,
            BindingResult result) {
        log.info("Updating design with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            ThietKeResponse updated = service.updateThietKe(id, request);
            log.info("Successfully updated design with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating design with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting design with id: {}", id);
        try {
            service.deleteThietKe(id);
            log.info("Successfully deleted design with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting design with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ThietKeResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String chatLieuKhung,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching designs - keyword: {}, chatLieuKhung: {}, page: {}, size: {}",
                keyword, chatLieuKhung, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (chatLieuKhung != null && !chatLieuKhung.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByChatLieuKhung(chatLieuKhung.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchThietKe(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllThietKe(pageable));
    }

    @GetMapping("/all-frame-materials")
    public ResponseEntity<List<String>> getAllFrameMaterials() {
        log.info("Getting all frame materials");
        List<String> materials = service.getAllFrameMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if design code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting design statistics");
        try {
            List<ThietKeResponse> allDesigns = service.getAllThietKeList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allDesigns.size());
            stats.put("active", allDesigns.stream()
                    .filter(t -> !t.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting design statistics: {}", e.getMessage());
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