package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.CpuRequest;
import com.example.be_datn.dto.product.response.CpuResponse;
import com.example.be_datn.service.product.CpuService;
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
@RequestMapping("/api/cpu")
@RequiredArgsConstructor
@Slf4j
public class CpuController {

    private final CpuService service;

    @GetMapping
    public ResponseEntity<Page<CpuResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all CPUs - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<CpuResponse> result = service.getAllCpu(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CpuResponse>> getAllList() {
        log.info("Getting all CPUs as list");
        List<CpuResponse> result = service.getAllCpuList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting CPU by id: {}", id);
        try {
            CpuResponse cpu = service.getCpuById(id);
            return ResponseEntity.ok(cpu);
        } catch (RuntimeException e) {
            log.error("Error getting CPU by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CpuRequest request,
            BindingResult result) {
        log.info("Creating new CPU with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CpuResponse created = service.createCpu(request);
            log.info("Successfully created CPU with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating CPU: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody CpuRequest request,
            BindingResult result) {
        log.info("Updating CPU with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            CpuResponse updated = service.updateCpu(id, request);
            log.info("Successfully updated CPU with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating CPU with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting CPU with id: {}", id);
        try {
            service.deleteCpu(id);
            log.info("Successfully deleted CPU with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting CPU with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CpuResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tenCpu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching CPUs - keyword: {}, tenCpu: {}, page: {}, size: {}",
                keyword, tenCpu, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (tenCpu != null && !tenCpu.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByTenCpu(tenCpu.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchCpu(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllCpu(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllTenCpuNames() {
        log.info("Getting all CPU names");
        List<String> names = service.getAllTenCpuNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if CPU code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/ten-cpu")
    public ResponseEntity<Boolean> checkTenCpuExists(
            @RequestParam String tenCpu,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if CPU name exists: {}, excludeId: {}", tenCpu, excludeId);
        boolean exists = service.existsByTenCpu(tenCpu, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting CPU statistics");
        try {
            List<CpuResponse> allCpus = service.getAllCpuList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allCpus.size());
            stats.put("active", allCpus.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting CPU statistics: {}", e.getMessage());
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