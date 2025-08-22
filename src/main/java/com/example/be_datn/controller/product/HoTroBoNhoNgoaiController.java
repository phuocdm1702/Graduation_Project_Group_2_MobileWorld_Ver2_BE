package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.HoTroBoNhoNgoaiRequest;
import com.example.be_datn.dto.product.response.HoTroBoNhoNgoaiResponse;
import com.example.be_datn.service.product.HoTroBoNhoNgoaiService;
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
@RequestMapping("/api/ho-tro-bo-nho-ngoai")
@RequiredArgsConstructor
@Slf4j
public class HoTroBoNhoNgoaiController {

    private final HoTroBoNhoNgoaiService service;

    @GetMapping
    public ResponseEntity<Page<HoTroBoNhoNgoaiResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all external memory supports - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<HoTroBoNhoNgoaiResponse> result = service.getAllHoTroBoNhoNgoai(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HoTroBoNhoNgoaiResponse>> getAllList() {
        log.info("Getting all external memory supports as list");
        List<HoTroBoNhoNgoaiResponse> result = service.getAllHoTroBoNhoNgoaiList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting external memory support by id: {}", id);
        try {
            HoTroBoNhoNgoaiResponse hoTroBoNhoNgoai = service.getHoTroBoNhoNgoaiById(id);
            return ResponseEntity.ok(hoTroBoNhoNgoai);
        } catch (RuntimeException e) {
            log.error("Error getting external memory support by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody HoTroBoNhoNgoaiRequest request,
            BindingResult result) {
        log.info("Creating new external memory support");

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            HoTroBoNhoNgoaiResponse created = service.createHoTroBoNhoNgoai(request);
            log.info("Successfully created external memory support with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating external memory support: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody HoTroBoNhoNgoaiRequest request,
            BindingResult result) {
        log.info("Updating external memory support with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            HoTroBoNhoNgoaiResponse updated = service.updateHoTroBoNhoNgoai(id, request);
            log.info("Successfully updated external memory support with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating external memory support with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HoTroBoNhoNgoaiResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching external memory supports - keyword: {} page: {}, size: {}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchHoTroBoNhoNgoai(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllHoTroBoNhoNgoai(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting external memory support statistics");
        try {
            List<HoTroBoNhoNgoaiResponse> allExternalMemorySupports = service.getAllHoTroBoNhoNgoaiList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allExternalMemorySupports.size());
            stats.put("active", allExternalMemorySupports.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting external memory support statistics: {}", e.getMessage());
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