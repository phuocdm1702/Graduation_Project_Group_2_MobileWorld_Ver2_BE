package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.CumCameraRequest;
import com.example.be_datn.dto.product.response.CumCameraResponse;
import com.example.be_datn.service.product.CumCameraService;
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
@RequestMapping("/api/cum-camera")
@RequiredArgsConstructor
@Slf4j
public class CumCameraController {

    private final CumCameraService service;

    @GetMapping
    public ResponseEntity<Page<CumCameraResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all camera clusters - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<CumCameraResponse> result = service.getAllCumCamera(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CumCameraResponse>> getAllList() {
        log.info("Getting all camera clusters as list");
        List<CumCameraResponse> result = service.getAllCumCameraList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting camera cluster by id: {}", id);
        try {
            CumCameraResponse cumCamera = service.getCumCameraById(id);
            return ResponseEntity.ok(cumCamera);
        } catch (RuntimeException e) {
            log.error("Error getting camera cluster by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CumCameraRequest request,
            BindingResult result) {
        log.info("Creating new camera cluster with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CumCameraResponse created = service.createCumCamera(request);
            log.info("Successfully created camera cluster with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating camera cluster: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody CumCameraRequest request,
            BindingResult result) {
        log.info("Updating camera cluster with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CumCameraResponse updated = service.updateCumCamera(id, request);
            log.info("Successfully updated camera cluster with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating camera cluster with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting camera cluster with id: {}", id);
        try {
            service.deleteCumCamera(id);
            log.info("Successfully deleted camera cluster with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting camera cluster with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CumCameraResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String thongSoCameraSau,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching camera clusters - keyword: {}, thongSoCameraSau: {}, page: {}, size: {}",
                keyword, thongSoCameraSau, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (thongSoCameraSau != null && !thongSoCameraSau.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByThongSoCameraSau(thongSoCameraSau.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchCumCamera(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllCumCamera(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllThongSoCameraSauNames() {
        log.info("Getting all rear camera specs names");
        List<String> names = service.getAllThongSoCameraSauNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if camera cluster code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting camera cluster statistics");
        try {
            List<CumCameraResponse> allCameraClusters = service.getAllCumCameraList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allCameraClusters.size());
            stats.put("active", allCameraClusters.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting camera cluster statistics: {}", e.getMessage());
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