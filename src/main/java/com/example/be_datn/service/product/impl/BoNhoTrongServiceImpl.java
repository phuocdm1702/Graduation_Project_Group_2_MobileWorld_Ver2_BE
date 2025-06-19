package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.BoNhoTrongRequest;
import com.example.be_datn.dto.product.response.BoNhoTrongResponse;
import com.example.be_datn.entity.product.BoNhoTrong;
import com.example.be_datn.repository.product.BoNhoTrongRepository;
import com.example.be_datn.service.product.BoNhoTrongService;
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
public class BoNhoTrongServiceImpl implements BoNhoTrongService {

    private final BoNhoTrongRepository repository;

    @Override
    public Page<BoNhoTrongResponse> getAllBoNhoTrong(Pageable pageable) {
        log.info("Getting all storage capacities with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<BoNhoTrongResponse> getAllBoNhoTrongList() {
        log.info("Getting all storage capacities as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BoNhoTrongResponse getBoNhoTrongById(Integer id) {
        log.info("Getting storage capacity by id: {}", id);
        BoNhoTrong entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Storage capacity not found with id: {}", id);
                    return new RuntimeException("Bộ nhớ trong không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public BoNhoTrongResponse createBoNhoTrong(BoNhoTrongRequest request) {
        log.info("Creating new storage capacity with code: {}", request.getMa());

        // Kiểm tra trùng lặp với các bản ghi chưa xóa mềm
        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Storage capacity code already exists: {}", request.getMa());
            throw new RuntimeException("Mã bộ nhớ trong đã tồn tại!");
        }

        if (repository.existsByDungLuongBoNhoTrongAndDeletedFalse(request.getDungLuongBoNhoTrong())) {
            log.error("Storage capacity already exists: {}", request.getDungLuongBoNhoTrong());
            throw new RuntimeException("Dung lượng bộ nhớ trong đã tồn tại!");
        }

        // Kiểm tra và khôi phục bản ghi đã xóa mềm
        Optional<BoNhoTrong> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<BoNhoTrong> existingByCapacity = repository.findByDungLuongBoNhoTrongAndDeletedTrue(request.getDungLuongBoNhoTrong());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted storage capacity by code: {}", request.getMa());
            BoNhoTrong entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setDungLuongBoNhoTrong(request.getDungLuongBoNhoTrong());
            return convertToResponse(repository.save(entity));
        }

        if (existingByCapacity.isPresent()) {
            log.info("Restoring soft-deleted storage capacity by capacity: {}", request.getDungLuongBoNhoTrong());
            BoNhoTrong entity = existingByCapacity.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        // Tạo mới
        BoNhoTrong entity = BoNhoTrong.builder()
                .ma(request.getMa())
                .dungLuongBoNhoTrong(request.getDungLuongBoNhoTrong())
                .deleted(false)
                .build();

        BoNhoTrong savedEntity = repository.save(entity);
        log.info("Created new storage capacity with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public BoNhoTrongResponse updateBoNhoTrong(Integer id, BoNhoTrongRequest request) {
        log.info("Updating storage capacity with id: {}", id);

        BoNhoTrong entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Storage capacity not found for update with id: {}", id);
                    return new RuntimeException("Bộ nhớ trong không tồn tại hoặc đã bị xóa!");
                });

        // Kiểm tra trùng lặp mã, loại trừ bản ghi hiện tại
        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Storage capacity code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã bộ nhớ trong đã tồn tại!");
        }

        // Kiểm tra trùng lặp dung lượng, loại trừ bản ghi hiện tại
        if (!entity.getDungLuongBoNhoTrong().equals(request.getDungLuongBoNhoTrong()) &&
                repository.existsByDungLuongBoNhoTrongAndDeletedFalse(request.getDungLuongBoNhoTrong(), id)) {
            log.error("Storage capacity already exists during update: {}", request.getDungLuongBoNhoTrong());
            throw new RuntimeException("Dung lượng bộ nhớ trong đã tồn tại!");
        }

        // Cập nhật thông tin
        entity.setMa(request.getMa());
        entity.setDungLuongBoNhoTrong(request.getDungLuongBoNhoTrong());

        BoNhoTrong updatedEntity = repository.save(entity);
        log.info("Updated storage capacity with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteBoNhoTrong(Integer id) {
        log.info("Soft deleting storage capacity with id: {}", id);

        BoNhoTrong entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Storage capacity not found for deletion with id: {}", id);
                    return new RuntimeException("Bộ nhớ trong không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted storage capacity with id: {}", id);
    }

    @Override
    public Page<BoNhoTrongResponse> searchBoNhoTrong(String keyword, Pageable pageable) {
        log.info("Searching storage capacities with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<BoNhoTrongResponse> filterByDungLuongBoNhoTrong(String dungLuongBoNhoTrong, Pageable pageable) {
        log.info("Filtering storage capacities by capacity: {}", dungLuongBoNhoTrong);
        return repository.findByDungLuongBoNhoTrongIgnoreCase(dungLuongBoNhoTrong, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllStorageCapacities() {
        log.info("Getting all storage capacity names");
        return repository.findByDeletedFalse()
                .stream()
                .map(BoNhoTrong::getDungLuongBoNhoTrong)
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
    public boolean existsByDungLuongBoNhoTrong(String dungLuongBoNhoTrong, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByDungLuongBoNhoTrongAndDeletedFalse(dungLuongBoNhoTrong, excludeId);
        }
        return repository.existsByDungLuongBoNhoTrongAndDeletedFalse(dungLuongBoNhoTrong);
    }

    private BoNhoTrongResponse convertToResponse(BoNhoTrong entity) {
        return BoNhoTrongResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .dungLuongBoNhoTrong(entity.getDungLuongBoNhoTrong())
                .deleted(entity.getDeleted())
                .build();
    }
}