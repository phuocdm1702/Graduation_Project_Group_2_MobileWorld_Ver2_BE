package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.ThietKeRequest;
import com.example.be_datn.dto.product.response.ThietKeResponse;
import com.example.be_datn.entity.product.ThietKe;
import com.example.be_datn.repository.product.ThietKeRepository;
import com.example.be_datn.service.product.ThietKeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThietKeServiceImpl implements ThietKeService {

    private final ThietKeRepository repository;

    @Override
    public Page<ThietKeResponse> getAllThietKe(Pageable pageable) {
        log.info("Getting all designs with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<ThietKeResponse> getAllThietKeList() {
        log.info("Getting all designs as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ThietKeResponse getThietKeById(Integer id) {
        log.info("Getting design by id: {}", id);
        ThietKe entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Design not found with id: {}", id);
                    return new RuntimeException("Thiết kế không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public ThietKeResponse createThietKe(ThietKeRequest request) {
        log.info("Creating new design");

        // Kiểm tra trùng lặp: cả chatLieuKhung và chatLieuMatLung giống hệt nhau
        boolean exists = repository.existsByChatLieuKhungAndChatLieuMatLungAndDeletedFalse(
                request.getChatLieuKhung().trim(),
                request.getChatLieuMatLung().trim());

        if (exists) {
            log.error("Design already exists with same frame and back material");
            throw new RuntimeException("Thiết kế với chất liệu khung và chất liệu mặt lưng này đã tồn tại!");
        }

        // Tìm thiết kế đã bị xóa mềm có cùng thông số
        Optional<ThietKe> existingDeleted = repository.findByChatLieuKhungAndChatLieuMatLungAndDeletedTrue(
                request.getChatLieuKhung().trim(),
                request.getChatLieuMatLung().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted design with same specs");
            ThietKe entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        ThietKe entity = ThietKe.builder()
                .ma("") // Không sử dụng mã nữa
                .chatLieuKhung(request.getChatLieuKhung().trim())
                .chatLieuMatLung(request.getChatLieuMatLung().trim())
                .deleted(false)
                .build();

        ThietKe savedEntity = repository.save(entity);
        log.info("Created new design with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public ThietKeResponse updateThietKe(Integer id, ThietKeRequest request) {
        log.info("Updating design with id: {}", id);

        ThietKe entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Design not found for update with id: {}", id);
                    return new RuntimeException("Thiết kế không tồn tại hoặc đã bị xóa!");
                });

        String newChatLieuKhung = request.getChatLieuKhung().trim();
        String newChatLieuMatLung = request.getChatLieuMatLung().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getChatLieuKhung().equals(newChatLieuKhung) &&
                entity.getChatLieuMatLung().equals(newChatLieuMatLung);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với thiết kế khác
            boolean existsOther = repository.existsByChatLieuKhungAndChatLieuMatLungAndDeletedFalseAndIdNot(
                    newChatLieuKhung, newChatLieuMatLung, id);

            if (existsOther) {
                log.error("Design already exists with same frame and back material during update");
                throw new RuntimeException("Thiết kế với chất liệu khung và chất liệu mặt lưng này đã tồn tại!");
            }
        }

        entity.setChatLieuKhung(newChatLieuKhung);
        entity.setChatLieuMatLung(newChatLieuMatLung);

        ThietKe updatedEntity = repository.save(entity);
        log.info("Updated design with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<ThietKeResponse> searchThietKe(String keyword, Pageable pageable) {
        log.info("Searching designs with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    private ThietKeResponse convertToResponse(ThietKe entity) {
        return ThietKeResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .chatLieuKhung(entity.getChatLieuKhung())
                .chatLieuMatLung(entity.getChatLieuMatLung())
                .deleted(entity.getDeleted())
                .build();
    }
}