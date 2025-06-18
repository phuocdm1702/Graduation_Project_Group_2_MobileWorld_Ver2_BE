package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.HeDieuHanhRequest;
import com.example.be_datn.dto.product.response.HeDieuHanhResponse;
import com.example.be_datn.service.product.HeDieuHanhService;
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
@RequestMapping("/api/he-dieu-hanh")
@RequiredArgsConstructor
@Slf4j
public class HeDieuHanhController {

    private final HeDieuHanhService service;

    @GetMapping
    public ResponseEntity<Page<HeDieuHanhResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all operating systems - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<HeDieuHanhResponse> result = service.getAllHeDieuHanh(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HeDieuHanhResponse>> getAllList() {
        log.info("Getting all operating systems as list");
        List<HeDieuHanhResponse> result = service.getAllHeDieuHanhList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting operating system by id: {}", id);
        try {
            HeDieuHanhResponse heDieuHanh = service.getHeDieuHanhById(id);
            return ResponseEntity.ok(heDieuHanh);
        } catch (RuntimeException e) {
            log.error("Error getting operating system by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody HeDieuHanhRequest request,
            BindingResult result) {
        log.info("Creating new operating system with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            HeDieuHanhResponse created = service.createHeDieuHanh(request);
            log.info("Successfully created operating system with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating operating system: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody HeDieuHanhRequest request,
            BindingResult result) {
        log.info("Updating operating system with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            HeDieuHanhResponse updated = service.updateHeDieuHanh(id, request);
            log.info("Successfully updated operating system with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating operating system with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting operating system with id: {}", id);
        try {
            service.deleteHeDieuHanh(id);
            log.info("Successfully deleted operating system with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting operating system with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HeDieuHanhResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String heDieuHanh,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching operating systems - keyword: {}, heDieuHanh: {}, page: {}, size: {}",
                keyword, heDieuHanh, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (heDieuHanh != null && !heDieuHanh.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByHeDieuHanh(heDieuHanh.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchHeDieuHanh(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllHeDieuHanh(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllHeDieuHanhNames() {
        log.info("Getting all operating system names");
        List<String> names = service.getAllHeDieuHanhNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if operating system code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/he-dieu-hanh")
    public ResponseEntity<Boolean> checkHeDieuHanhExists(
            @RequestParam String heDieuHanh,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if operating system name exists: {}, excludeId: {}", heDieuHanh, excludeId);
        boolean exists = service.existsByHeDieuHanh(heDieuHanh, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting operating system statistics");
        try {
            List<HeDieuHanhResponse> allOperatingSystems = service.getAllHeDieuHanhList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allOperatingSystems.size());
            stats.put("active", allOperatingSystems.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting operating system statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể lấy thống kê"));
        }
    }

    private Map<String, String> getErrorMap(BindingResult result) {
        Map< String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        result.getGlobalErrors().forEach(error ->
                errors.put("global", error.getDefaultMessage()));
        return errors;
    }
}