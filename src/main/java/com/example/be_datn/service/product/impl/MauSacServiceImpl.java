package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.MauSacRequest;
import com.example.be_datn.dto.product.response.MauSacResponse;
import com.example.be_datn.entity.product.MauSac;
import com.example.be_datn.repository.product.MauSacRepository;
import com.example.be_datn.service.product.MauSacService;
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
public class MauSacServiceImpl implements MauSacService {

    private final MauSacRepository repository;

    @Override
    public Page<MauSacResponse> getAllMauSac(Pageable pageable) {
        log.info("Getting all colors with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<MauSacResponse> getAllMauSacList() {
        log.info("Getting all colors as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MauSacResponse getMauSacById(Integer id) {
        log.info("Getting color by id: {}", id);
        MauSac entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Color not found with id: {}", id);
                    return new RuntimeException("Màu sắc không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public MauSacResponse createMauSac(MauSacRequest request) {
        log.info("Creating new color with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Color code already exists: {}", request.getMa());
            throw new RuntimeException("Mã màu sắc đã tồn tại!");
        }

        if (repository.existsByMauSacAndDeletedFalse(request.getMauSac())) {
            log.error("Color name already exists: {}", request.getMauSac());
            throw new RuntimeException("Tên màu sắc đã tồn tại!");
        }

        Optional<MauSac> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<MauSac> existingByName = repository.findByMauSacAndDeletedTrue(request.getMauSac());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted color by code: {}", request.getMa());
            MauSac entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setMauSac(request.getMauSac());
            entity.setMaMau(request.getMaMau()); // Cập nhật mã màu
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted color by name: {}", request.getMauSac());
            MauSac entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            entity.setMaMau(request.getMaMau()); // Cập nhật mã màu
            return convertToResponse(repository.save(entity));
        }

        MauSac entity = MauSac.builder()
                .ma(request.getMa())
                .mauSac(request.getMauSac())
                .maMau(request.getMaMau()) // Thêm mã màu
                .deleted(false)
                .build();

        MauSac savedEntity = repository.save(entity);
        log.info("Created new color with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public MauSacResponse updateMauSac(Integer id, MauSacRequest request) {
        log.info("Updating color with id: {}", id);

        MauSac entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Color not found for update with id: {}", id);
                    return new RuntimeException("Màu sắc không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Color code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã màu sắc đã tồn tại!");
        }

        if (!entity.getMauSac().equals(request.getMauSac()) &&
                repository.existsByMauSacAndDeletedFalse(request.getMauSac(), id)) {
            log.error("Color name already exists during update: {}", request.getMauSac());
            throw new RuntimeException("Tên màu sắc đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setMauSac(request.getMauSac());
        entity.setMaMau(request.getMaMau()); // Cập nhật mã màu

        MauSac updatedEntity = repository.save(entity);
        log.info("Updated color with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteMauSac(Integer id) {
        log.info("Soft deleting color with id: {}", id);

        MauSac entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Color not found for deletion with id: {}", id);
                    return new RuntimeException("Màu sắc không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted color with id: {}", id);
    }

    @Override
    public Page<MauSacResponse> searchMauSac(String keyword, Pageable pageable) {
        log.info("Searching colors with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<MauSacResponse> filterByMauSac(String mauSac, Pageable pageable) {
        log.info("Filtering colors by name: {}", mauSac);
        return repository.findByMauSacIgnoreCase(mauSac, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllColorNames() {
        log.info("Getting all color names");
        return repository.findByDeletedFalse()
                .stream()
                .map(MauSac::getMauSac)
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

    @Override
    public boolean existsByMauSac(String mauSac, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByMauSacAndDeletedFalse(mauSac, excludeId);
        }
        return repository.existsByMauSacAndDeletedFalse(mauSac);
    }

    private MauSacResponse convertToResponse(MauSac entity) {
        return MauSacResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .mauSac(entity.getMauSac())
                .maMau(entity.getMaMau()) // Thêm mã màu vào response
                .deleted(entity.getDeleted())
                .build();
    }
}