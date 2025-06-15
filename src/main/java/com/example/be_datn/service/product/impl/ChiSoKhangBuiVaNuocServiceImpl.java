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
        log.info("Getting all dust and water resistance indices with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<ChiSoKhangBuiVaNuocResponse> getAllChiSoKhangBuiVaNuocList() {
        log.info("Getting all dust and water resistance indices as list");
        return repository.findByDeletedFalse().stream()
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
        log.info("Creating new dust and water resistance index with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Dust and water resistance index code already exists: {}", request.getMa());
            throw new RuntimeException("Mã chỉ số kháng bụi và nước đã tồn tại!");
        }

        if (repository.existsByTenChiSoAndDeletedFalse(request.getTenChiSo())) {
            log.error("Dust and water resistance index name already exists: {}", request.getTenChiSo());
            throw new RuntimeException("Tên chỉ số kháng bụi và nước đã tồn tại!");
        }

        Optional<ChiSoKhangBuiVaNuoc> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<ChiSoKhangBuiVaNuoc> existingByName = repository.findByTenChiSoAndDeletedTrue(request.getTenChiSo());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted dust and water resistance index by code: {}", request.getMa());
            ChiSoKhangBuiVaNuoc entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setTenChiSo(request.getTenChiSo());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted dust and water resistance index by name: {}", request.getTenChiSo());
            ChiSoKhangBuiVaNuoc entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        ChiSoKhangBuiVaNuoc entity = ChiSoKhangBuiVaNuoc.builder()
                .ma(request.getMa())
                .tenChiSo(request.getTenChiSo())
                .deleted(false)
                .build();

        ChiSoKhangBuiVaNuoc savedEntity = repository.save(entity);
        log.info("Created new dust and water resistance index with id: {}", savedEntity.getId());
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

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Dust and water resistance index code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã chỉ số kháng bụi và nước đã tồn tại!");
        }

        if (!entity.getTenChiSo().equals(request.getTenChiSo()) &&
                repository.existsByTenChiSoAndDeletedFalse(request.getTenChiSo(), id)) {
            log.error("Dust and water resistance index name already exists during update: {}", request.getTenChiSo());
            throw new RuntimeException("Tên chỉ số kháng bụi và nước đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setTenChiSo(request.getTenChiSo());

        ChiSoKhangBuiVaNuoc updatedEntity = repository.save(entity);
        log.info("Updated dust and water resistance index with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteChiSoKhangBuiVaNuoc(Integer id) {
        log.info("Soft deleting dust and water resistance index with id: {}", id);

        ChiSoKhangBuiVaNuoc entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Dust and water resistance index not found for deletion with id: {}", id);
                    return new RuntimeException("Chỉ số kháng bụi và nước không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted dust and water resistance index with id: {}", id);
    }

    @Override
    public Page<ChiSoKhangBuiVaNuocResponse> searchChiSoKhangBuiVaNuoc(String keyword, Pageable pageable) {
        log.info("Searching dust and water resistance indices with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<ChiSoKhangBuiVaNuocResponse> filterByTenChiSo(String tenChiSo, Pageable pageable) {
        log.info("Filtering dust and water resistance indices by name: {}", tenChiSo);
        return repository.findByTenChiSoIgnoreCase(tenChiSo, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllTenChiSoNames() {
        log.info("Getting all dust and water resistance index names");
        return repository.findByDeletedFalse()
                .stream()
                .map(ChiSoKhangBuiVaNuoc::getTenChiSo)
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
    public boolean existsByTenChiSo(String tenChiSo, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByTenChiSoAndDeletedFalse(tenChiSo, excludeId);
        }
        return repository.existsByTenChiSoAndDeletedFalse(tenChiSo);
    }

    private ChiSoKhangBuiVaNuocResponse convertToResponse(ChiSoKhangBuiVaNuoc entity) {
        return ChiSoKhangBuiVaNuocResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenChiSo(entity.getTenChiSo())
                .deleted(entity.getDeleted())
                .build();
    }
}