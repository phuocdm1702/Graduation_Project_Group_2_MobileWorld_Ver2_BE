package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.GpuRequest;
import com.example.be_datn.dto.product.response.GpuResponse;
import com.example.be_datn.service.product.GpuService;
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
@RequestMapping("/api/gpu")
@RequiredArgsConstructor
@Slf4j
public class GpuController {

    private final GpuService service;

    @GetMapping
    public ResponseEntity<Page<GpuResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all GPUs - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<GpuResponse> result = service.getAllGpu(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GpuResponse>> getAllList() {
        log.info("Getting all GPUs as list");
        List<GpuResponse> result = service.getAllGpuList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting GPU by id: {}", id);
        try {
            GpuResponse gpu = service.getGpuById(id);
            return ResponseEntity.ok(gpu);
        } catch (RuntimeException e) {
            log.error("Error getting GPU by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody GpuRequest request,
            BindingResult result) {
        log.info("Creating new GPU with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            GpuResponse created = service.createGpu(request);
            log.info("Successfully created GPU with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating GPU: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody GpuRequest request,
            BindingResult result) {
        log.info("Updating GPU with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            GpuResponse updated = service.updateGpu(id, request);
            log.info("Successfully updated GPU with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating GPU with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting GPU with id: {}", id);
        try {
            service.deleteGpu(id);
            log.info("Successfully deleted GPU with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting GPU with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GpuResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tenGpu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching GPUs - keyword: {}, tenGpu: {}, page: {}, size: {}",
                keyword, tenGpu, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (tenGpu != null && !tenGpu.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByTenGpu(tenGpu.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchGpu(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllGpu(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllTenGpuNames() {
        log.info("Getting all GPU names");
        List<String> names = service.getAllTenGpuNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if GPU code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/ten-gpu")
    public ResponseEntity<Boolean> checkTenGpuExists(
            @RequestParam String tenGpu,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if GPU name exists: {}, excludeId: {}", tenGpu, excludeId);
        boolean exists = service.existsByTenGpu(tenGpu, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting GPU statistics");
        try {
            List<GpuResponse> allGpus = service.getAllGpuList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allGpus.size());
            stats.put("active", allGpus.stream()
                    .filter(g -> !g.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting GPU statistics: {}", e.getMessage());
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