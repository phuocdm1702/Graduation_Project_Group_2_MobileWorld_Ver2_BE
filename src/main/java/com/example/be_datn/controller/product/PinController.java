package com.example.be_datn.controller.product;

import com.example.be_datn.dto.product.request.PinRequest;
import com.example.be_datn.dto.product.response.PinResponse;
import com.example.be_datn.service.product.PinService;
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
@RequestMapping("/api/pin")
@RequiredArgsConstructor
@Slf4j
public class PinController {

    private final PinService service;

    @GetMapping
    public ResponseEntity<Page<PinResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all batteries - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<PinResponse> result = service.getAllPin(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PinResponse>> getAllList() {
        log.info("Getting all batteries as list");
        List<PinResponse> result = service.getAllPinList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        log.info("Getting battery by id: {}", id);
        try {
            PinResponse pin = service.getPinById(id);
            return ResponseEntity.ok(pin);
        } catch (RuntimeException e) {
            log.error("Error getting battery by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody PinRequest request,
            BindingResult result) {
        log.info("Creating new battery with code: {}", request.getMa());

        if (result.hasErrors()) {
            log.warn("Validation errors in create request: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            PinResponse created = service.createPin(request);
            log.info("Successfully created battery with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Error creating battery: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody PinRequest request,
            BindingResult result) {
        log.info("Updating battery with id: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors in update request for id {}: {}", id, result.getAllErrors());
            return ResponseEntity.badRequest().body(getErrorMap(result));
        }

        try {
            PinResponse updated = service.updatePin(id, request);
            log.info("Successfully updated battery with id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating battery with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.info("Deleting battery with id: {}", id);
        try {
            service.deletePin(id);
            log.info("Successfully deleted battery with id: {}", id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            log.error("Error deleting battery with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PinResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String loaiPin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching batteries - keyword: {}, loaiPin: {}, page: {}, size: {}",
                keyword, loaiPin, page, size);

        Pageable pageable = PageRequest.of(page, size);

        if (loaiPin != null && !loaiPin.trim().isEmpty()) {
            return ResponseEntity.ok(service.filterByLoaiPin(loaiPin.trim(), pageable));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchPin(keyword.trim(), pageable));
        }

        return ResponseEntity.ok(service.getAllPin(pageable));
    }

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllLoaiPinNames() {
        log.info("Getting all battery type names");
        List<String> names = service.getAllLoaiPinNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/exists/ma")
    public ResponseEntity<Boolean> checkMaExists(
            @RequestParam String ma,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if battery code exists: {}, excludeId: {}", ma, excludeId);
        boolean exists = service.existsByMa(ma, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/loai-pin")
    public ResponseEntity<Boolean> checkLoaiPinExists(
            @RequestParam String loaiPin,
            @RequestParam(required = false) Integer excludeId) {
        log.info("Checking if battery type exists: {}, excludeId: {}", loaiPin, excludeId);
        boolean exists = service.existsByLoaiPin(loaiPin, excludeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting battery statistics");
        try {
            List<PinResponse> allBatteries = service.getAllPinList();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allBatteries.size());
            stats.put("active", allBatteries.stream()
                    .filter(m -> !m.getDeleted())
                    .count());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting battery statistics: {}", e.getMessage());
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