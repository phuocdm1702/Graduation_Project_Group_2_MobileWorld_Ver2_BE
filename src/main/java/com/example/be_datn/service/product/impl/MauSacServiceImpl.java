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
        log.info("Getting all colors with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<MauSacResponse> getAllMauSacList() {
        log.info("Getting all colors as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
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
        log.info("Creating new color");

        // Kiểm tra trùng lặp: cả tên màu sắc và mã màu
        boolean exists = repository.existsByMauSacAndMaMauAndDeletedFalse(
                request.getMauSac().trim(),
                request.getMaMau().trim());

        if (exists) {
            log.error("Color already exists with same name and color code");
            throw new RuntimeException("Màu sắc với tên màu và mã màu này đã tồn tại!");
        }

        // Tìm màu sắc đã bị xóa mềm có cùng tên màu và mã màu
        Optional<MauSac> existingDeleted = repository.findByMauSacAndMaMauAndDeletedTrue(
                request.getMauSac().trim(),
                request.getMaMau().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted color with same name and color code");
            MauSac entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        MauSac entity = MauSac.builder()
                .ma("") // Không sử dụng mã nữa
                .mauSac(request.getMauSac().trim())
                .maMau(request.getMaMau())
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

        String newMauSac = request.getMauSac().trim();
        String newMaMau = request.getMaMau().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getMauSac().equals(newMauSac) &&
                entity.getMaMau().equals(newMaMau);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với màu sắc khác
            boolean existsOther = repository.existsByMauSacAndMaMauAndDeletedFalseAndIdNot(
                    newMauSac, newMaMau, id);

            if (existsOther) {
                log.error("Color already exists with same name and color code during update");
                throw new RuntimeException("Màu sắc với tên màu và mã màu này đã tồn tại!");
            }
        }

        entity.setMauSac(newMauSac);
        entity.setMaMau(newMaMau);

        MauSac updatedEntity = repository.save(entity);
        log.info("Updated color with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<MauSacResponse> searchMauSac(String keyword, Pageable pageable) {
        log.info("Searching colors with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    private MauSacResponse convertToResponse(MauSac entity) {
        return MauSacResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .mauSac(entity.getMauSac())
                .maMau(entity.getMaMau())
                .deleted(entity.getDeleted())
                .build();
    }
}