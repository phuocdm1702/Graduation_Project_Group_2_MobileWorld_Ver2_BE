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
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<BoNhoTrongResponse> getAllBoNhoTrongList() {
        log.info("Getting all storage capacities as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
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
        log.info("Creating new storage capacity");

        // Kiểm tra trùng lặp: dung lượng bộ nhớ trong
        boolean exists = repository.existsByDungLuongBoNhoTrongAndDeletedFalse(
                request.getDungLuongBoNhoTrong().trim());

        if (exists) {
            log.error("Storage capacity already exists with same capacity");
            throw new RuntimeException("Bộ nhớ trong với dung lượng này đã tồn tại!");
        }

        // Tìm bộ nhớ trong đã bị xóa mềm có cùng dung lượng
        Optional<BoNhoTrong> existingDeleted = repository.findByDungLuongBoNhoTrongAndDeletedTrue(
                request.getDungLuongBoNhoTrong().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted storage capacity with same capacity");
            BoNhoTrong entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        BoNhoTrong entity = BoNhoTrong.builder()
                .ma("") // Không sử dụng mã nữa
                .dungLuongBoNhoTrong(request.getDungLuongBoNhoTrong().trim())
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

        String newDungLuongBoNhoTrong = request.getDungLuongBoNhoTrong().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getDungLuongBoNhoTrong().equals(newDungLuongBoNhoTrong);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với bộ nhớ trong khác
            boolean existsOther = repository.existsByDungLuongBoNhoTrongAndDeletedFalseAndIdNot(
                    newDungLuongBoNhoTrong, id);

            if (existsOther) {
                log.error("Storage capacity already exists with same capacity during update");
                throw new RuntimeException("Bộ nhớ trong với dung lượng này đã tồn tại!");
            }
        }

        entity.setDungLuongBoNhoTrong(newDungLuongBoNhoTrong);

        BoNhoTrong updatedEntity = repository.save(entity);
        log.info("Updated storage capacity with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<BoNhoTrongResponse> searchBoNhoTrong(String keyword, Pageable pageable) {
        log.info("Searching storage capacities with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    // Chuyển đổi Entity sang Response DTO
    private BoNhoTrongResponse convertToResponse(BoNhoTrong entity) {
        return BoNhoTrongResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .dungLuongBoNhoTrong(entity.getDungLuongBoNhoTrong())
                .deleted(entity.getDeleted())
                .build();
    }
}