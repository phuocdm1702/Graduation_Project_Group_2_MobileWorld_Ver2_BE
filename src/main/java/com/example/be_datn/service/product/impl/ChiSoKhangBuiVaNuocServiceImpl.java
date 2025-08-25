package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.ChiSoKhangBuiVaNuocRequest;
import com.example.be_datn.dto.product.response.ChiSoKhangBuiVaNuocResponse;
import com.example.be_datn.entity.product.ChiSoKhangBuiVaNuoc;
import com.example.be_datn.repository.product.ChiSoKhangBuiVaNuocRepository;
import com.example.be_datn.service.product.ChiSoKhangBuiVaNuocService;
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
public class ChiSoKhangBuiVaNuocServiceImpl implements ChiSoKhangBuiVaNuocService {

    private final ChiSoKhangBuiVaNuocRepository repository;

    @Override
    public Page<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuoc(Pageable pageable) {
        log.info("Getting all dust and water resistance indices with pagination: page={}, size={} (newest first)",
                pageable.getPageNumber(), pageable.getPageSize());
        // Sử dụng method mới để sắp xếp theo ID giảm dần (mới nhất lên đầu)
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuocList() {
        log.info("Getting all dust and water resistance indices as list (newest first)");
        // Sử dụng method mới để sắp xếp theo ID giảm dần (mới nhất lên đầu)
        return repository.findByDeletedFalseOrderByIdDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChiSoKhangBuiVaNuocResponse getChiSoKhangBuiVaNuocById(Integer id) {
        log.info("Getting dust and water resistance index by id: {}", id);
        ChiSoKhangBuiVaNuoc entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Dust and water resistance index not found with id: {}", id);
                    return new RuntimeException("Chỉ số kháng bụi và nước không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public ChiSoKhangBuiVaNuocResponse createChiSoKhangBuiVaNuoc(ChiSoKhangBuiVaNuocRequest request) {
        log.info("Creating new dust and water resistance index");

        // Kiểm tra trùng lặp: tên chỉ số
        boolean exists = repository.existsByTenChiSoAndDeletedFalse(
                request.getTenChiSo().trim());

        if (exists) {
            log.error("Dust and water resistance index already exists with same name");
            throw new RuntimeException("Chỉ số kháng bụi và nước với tên này đã tồn tại!");
        }

        // Tìm chỉ số đã bị xóa mềm có cùng tên
        Optional<ChiSoKhangBuiVaNuoc> existingDeleted = repository.findByTenChiSoAndDeletedTrue(
                request.getTenChiSo().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted dust and water resistance index with same name");
            ChiSoKhangBuiVaNuoc entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        ChiSoKhangBuiVaNuoc entity = ChiSoKhangBuiVaNuoc.builder()
                .ma("") // Không sử dụng mã nữa
                .tenChiSo(request.getTenChiSo().trim())
                .deleted(false)
                .build();

        ChiSoKhangBuiVaNuoc savedEntity = repository.save(entity);
        log.info("Created new dust and water resistance index with id: {} (will appear at top of list)", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public ChiSoKhangBuiVaNuocResponse updateChiSoKhangBuiVaNuoc(Integer id, ChiSoKhangBuiVaNuocRequest request) {
        log.info("Updating dust and water resistance index with id: {}", id);

        ChiSoKhangBuiVaNuoc entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Dust and water resistance index not found for update with id: {}", id);
                    return new RuntimeException("Chỉ số kháng bụi và nước không tồn tại hoặc đã bị xóa!");
                });

        String newTenChiSo = request.getTenChiSo().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getTenChiSo().equals(newTenChiSo);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với chỉ số khác
            boolean existsOther = repository.existsByTenChiSoAndDeletedFalseAndIdNot(
                    newTenChiSo, id);

            if (existsOther) {
                log.error("Dust and water resistance index already exists with same name during update");
                throw new RuntimeException("Chỉ số kháng bụi và nước với tên này đã tồn tại!");
            }
        }

        entity.setTenChiSo(newTenChiSo);

        ChiSoKhangBuiVaNuoc updatedEntity = repository.save(entity);
        log.info("Updated dust and water resistance index with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<ChiSoKhangBuiVaNuocResponse> searchChiSoKhangBuiVaNuoc(String keyword, Pageable pageable) {
        log.info("Searching dust and water resistance indices with keyword: {} (newest first)", keyword);
        // Sử dụng method mới để sắp xếp theo ID giảm dần (mới nhất lên đầu)
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    // Chuyển đổi Entity sang Response DTO
    private ChiSoKhangBuiVaNuocResponse convertToResponse(ChiSoKhangBuiVaNuoc entity) {
        return ChiSoKhangBuiVaNuocResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenChiSo(entity.getTenChiSo())
                .deleted(entity.getDeleted())
                .build();
    }
}