package com.example.be_datn.controller.product;

import com.example.be_datn.repository.product.ImelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/imel")
public class ImelController {

    private final ImelRepository imelRepository;

    public ImelController(ImelRepository imelRepository) {
        this.imelRepository = imelRepository;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkImelExists(@RequestParam String imel) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=== IMEL CHECK REQUEST ===");
            System.out.println("Received IMEL: [" + imel + "]");
            System.out.println("IMEL length: " + (imel != null ? imel.length() : "null"));

            // Kiểm tra null hoặc empty
            if (imel == null || imel.trim().isEmpty()) {
                System.out.println("ERROR: IMEL is null or empty");
                response.put("error", "IMEL không được để trống");
                response.put("exists", false);
                response.put("imel", imel);
                return ResponseEntity.badRequest().body(response);
            }

            // Trim whitespace
            imel = imel.trim();
            System.out.println("Trimmed IMEL: [" + imel + "]");
            System.out.println("Trimmed length: " + imel.length());

            // Kiểm tra độ dài
            if (imel.length() != 15) {
                System.out.println("ERROR: IMEL length invalid - " + imel.length());
                response.put("error", "IMEL phải có đúng 15 chữ số (hiện tại có " + imel.length() + " chữ số)");
                response.put("exists", false);
                response.put("imel", imel);
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra chỉ chứa số
            if (!imel.matches("\\d{15}")) {
                System.out.println("ERROR: IMEL contains non-digits");
                response.put("error", "IMEL chỉ được chứa các chữ số từ 0-9");
                response.put("exists", false);
                response.put("imel", imel);
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("IMEL validation passed, checking database...");

            // Kiểm tra trong database
            boolean exists = imelRepository.existsByImel(imel);
            System.out.println("Database check result: " + exists);

            response.put("exists", exists);
            response.put("imel", imel);
            response.put("message", exists ? "IMEL đã tồn tại trong hệ thống" : "IMEL chưa tồn tại");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();

            response.put("error", "Lỗi server khi kiểm tra IMEL: " + e.getMessage());
            response.put("exists", false);
            response.put("imel", imel);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}