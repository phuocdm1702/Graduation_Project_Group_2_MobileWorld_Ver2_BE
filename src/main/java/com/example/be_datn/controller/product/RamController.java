package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.RamRequest;
import com.example.be_datn.dto.product.response.RamResponse;
import com.example.be_datn.service.product.RamService;
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
@RequestMapping("/api/ram")
@RequiredArgsConstructor
@Slf4j
public class RamController {

    private final RamService service;

    @GetMapping
    public ResponseEntity<Page<RamResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all RAMs - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<RamResponse> result = service.getAllRam(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RamResponse>> getAllList() {
        log.info("Getting all RAMs as list");
        List<RamResponse> result = service.getAllRamList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting RAM by id: {}", id);
        try {
            RamResponse ram = service.getRamById(id);
            return ResponseEntity.ok(ram);
        } catch (RuntimeException e) {
            log.error("Error getting RAM by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody RamRequest request,
            BindingResult result) {
        log.info("Creating new RAM with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            RamResponse created = service.createRam(request);
            log.info("Successfully created RAM with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating RAM: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody RamRequest request,
            BindingResult result) {
        log.info("Updating RAM with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            RamResponse updated = service.updateRam(id, request);
            log.info("Successfully updated RAM with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating RAM with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting RAM with id: {}", id);
        try {
            service.deleteRam(id);
            log.info("Successfully deleted RAM with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting RAM with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RamResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dungLuongRam,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching RAMs - keyword: {}, dungLuongRam: {}, page: {}, size: {}",
                keyword, dungLuongRam, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (dungLuongRam != null && !dungLuongRam.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByDungLuongRam(dungLuongRam.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchRam(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllRam(pageable));
    }

    @GetMapping("/all-capacities")
    public ResponseEntity<List<String>> getAllRamCapacities() {
        log.info("Getting all RAM capacities");
        List<String> capacities = service.getAllRamCapacities();
        return ResponseEntity.ok(capacities);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if RAM code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/dung-luong-ram")
    public ResponseEntity<Boolean> checkDungLuongRamExists(
            @RequestParam String dungLuongRam,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if RAM capacity exists: {}, excludeId: {}", dungLuongRam, excludeId);
        boolean exists = service.existsByDungLuongRam(dungLuongRam, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting RAM statistics");
        try {
            List<RamResponse> allRams = service.getAllRamList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allRams.size());
            stats.put("active", allRams.stream()
                    .filter(r -> !r.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting RAM statistics: {}", e.getMessage());
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