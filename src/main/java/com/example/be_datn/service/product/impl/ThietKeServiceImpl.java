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
        log.info("Getting all designs with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<ThietKeResponse> getAllThietKeList() {
        log.info("Getting all designs as list");
        return repository.findByDeletedFalse().stream()
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
        log.info("Creating new design with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Design code already exists: {}", request.getMa());
            throw new RuntimeException("Mã thiết kế đã tồn tại!");
        }

        Optional<ThietKe> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted design by code: {}", request.getMa());
            ThietKe entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setChatLieuKhung(request.getChatLieuKhung());
            entity.setChatLieuMatLung(request.getChatLieuMatLung());
            return convertToResponse(repository.save(entity));
        }

        ThietKe entity = ThietKe.builder()
                .ma(request.getMa())
                .chatLieuKhung(request.getChatLieuKhung())
                .chatLieuMatLung(request.getChatLieuMatLung())
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

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Design code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã thiết kế đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setChatLieuKhung(request.getChatLieuKhung());
        entity.setChatLieuMatLung(request.getChatLieuMatLung());

        ThietKe updatedEntity = repository.save(entity);
        log.info("Updated design with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteThietKe(Integer id) {
        log.info("Soft deleting design with id: {}", id);

        ThietKe entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Design not found for deletion with id: {}", id);
                    return new RuntimeException("Thiết kế không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted design with id: {}", id);
    }

    @Override
    public Page<ThietKeResponse> searchThietKe(String keyword, Pageable pageable) {
        log.info("Searching designs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<ThietKeResponse> filterByChatLieuKhung(String chatLieuKhung, Pageable pageable) {
        log.info("Filtering designs by frame material: {}", chatLieuKhung);
        return repository.findByChatLieuKhungIgnoreCase(chatLieuKhung, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllFrameMaterials() {
        log.info("Getting all frame materials");
        return repository.findByDeletedFalse()
                .stream()
                .map(ThietKe::getChatLieuKhung)
                .filter(material -> material != null && !material.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByMa(String ma, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByMaAndDeletedFalse(ma, excludeId);
        }
        return repository.existsByMaAndDeletedFalse(ma);
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