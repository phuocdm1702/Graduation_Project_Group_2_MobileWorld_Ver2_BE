package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.SimRequest;
import com.example.be_datn.dto.product.response.SimResponse;
import com.example.be_datn.service.product.SimService;
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
@RequestMapping("/api/sim")
@RequiredArgsConstructor
@Slf4j
public class SimController {

    private final SimService service;

    @GetMapping
    public ResponseEntity<Page<SimResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all SIMs - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<SimResponse> result = service.getAllSim(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SimResponse>> getAllList() {
        log.info("Getting all SIMs as list");
        List<SimResponse> result = service.getAllSimList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting SIM by id: {}", id);
        try {
            SimResponse sim = service.getSimById(id);
            return ResponseEntity.ok(sim);
        } catch (RuntimeException e) {
            log.error("Error getting SIM by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody SimRequest request,
            BindingResult result) {
        log.info("Creating new SIM with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            SimResponse created = service.createSim(request);
            log.info("Successfully created SIM with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating SIM: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody SimRequest request,
            BindingResult result) {
        log.info("Updating SIM with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            SimResponse updated = service.updateSim(id, request);
            log.info("Successfully updated SIM with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating SIM with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting SIM with id: {}", id);
        try {
            service.deleteSim(id);
            log.info("Successfully deleted SIM with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting SIM with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SimResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer soLuongSimHoTro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching SIMs - keyword: {}, soLuongSimHoTro: {}, page: {}, size: {}",
                keyword, soLuongSimHoTro, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (soLuongSimHoTro != null) {
            return ResponseEntity.ok(service.filterBySoLuongSimHoTro(soLuongSimHoTro, pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchSim(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllSim(pageable));
    }

    @GetMapping("/all-types")
    public ResponseEntity<List<String>> getAllSimTypes() {
        log.info("Getting all SIM types");
        List<String> types = service.getAllSimTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if SIM code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting SIM statistics");
        try {
            List<SimResponse> allSims = service.getAllSimList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allSims.size());
            stats.put("active", allSims.stream()
                    .filter(s -> !s.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting SIM statistics: {}", e.getMessage());
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