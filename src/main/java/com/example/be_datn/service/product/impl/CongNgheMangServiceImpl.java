package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.CongNgheMangRequest;
import com.example.be_datn.dto.product.response.CongNgheMangResponse;
import com.example.be_datn.entity.product.CongNgheMang;
import com.example.be_datn.repository.product.CongNgheMangRepository;
import com.example.be_datn.service.product.CongNgheMangService;
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
public class CongNgheMangServiceImpl implements CongNgheMangService {

    private final CongNgheMangRepository repository;

    @Override
    public Page<CongNgheMangResponse> getAllCongNgheMang(Pageable pageable) {
        log.info("Getting all network technologies with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<CongNgheMangResponse> getAllCongNgheMangList() {
        log.info("Getting all network technologies as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CongNgheMangResponse getCongNgheMangById(Integer id) {
        log.info("Getting network technology by id: {}", id);
        CongNgheMang entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Network technology not found with id: {}", id);
                    return new RuntimeException("Công nghệ mạng không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public CongNgheMangResponse createCongNgheMang(CongNgheMangRequest request) {
        log.info("Creating new network technology with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Network technology code already exists: {}", request.getMa());
            throw new RuntimeException("Mã công nghệ mạng đã tồn tại!");
        }

        if (repository.existsByTenCongNgheMangAndDeletedFalse(request.getTenCongNgheMang())) {
            log.error("Network technology name already exists: {}", request.getTenCongNgheMang());
            throw new RuntimeException("Tên công nghệ mạng đã tồn tại!");
        }

        Optional<CongNgheMang> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<CongNgheMang> existingByName = repository.findByTenCongNgheMangAndDeletedTrue(request.getTenCongNgheMang());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted network technology by code: {}", request.getMa());
            CongNgheMang entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setTenCongNgheMang(request.getTenCongNgheMang());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted network technology by name: {}", request.getTenCongNgheMang());
            CongNgheMang entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        CongNgheMang entity = CongNgheMang.builder()
                .ma(request.getMa())
                .tenCongNgheMang(request.getTenCongNgheMang())
                .deleted(false)
                .build();

        CongNgheMang savedEntity = repository.save(entity);
        log.info("Created new network technology with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public CongNgheMangResponse updateCongNgheMang(Integer id, CongNgheMangRequest request) {
        log.info("Updating network technology with id: {}", id);

        CongNgheMang entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Network technology not found for update with id: {}", id);
                    return new RuntimeException("Công nghệ mạng không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Network technology code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã công nghệ mạng đã tồn tại!");
        }

        if (!entity.getTenCongNgheMang().equals(request.getTenCongNgheMang()) &&
                repository.existsByTenCongNgheMangAndDeletedFalse(request.getTenCongNgheMang(), id)) {
            log.error("Network technology name already exists during update: {}", request.getTenCongNgheMang());
            throw new RuntimeException("Tên công nghệ mạng đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setTenCongNgheMang(request.getTenCongNgheMang());

        CongNgheMang updatedEntity = repository.save(entity);
        log.info("Updated network technology with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCongNgheMang(Integer id) {
        log.info("Soft deleting network technology with id: {}", id);

        CongNgheMang entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Network technology not found for deletion with id: {}", id);
                    return new RuntimeException("Công nghệ mạng không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted network technology with id: {}", id);
    }

    @Override
    public Page<CongNgheMangResponse> searchCongNgheMang(String keyword, Pageable pageable) {
        log.info("Searching network technologies with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<CongNgheMangResponse> filterByTenCongNgheMang(String tenCongNgheMang, Pageable pageable) {
        log.info("Filtering network technologies by name: {}", tenCongNgheMang);
        return repository.findByTenCongNgheMangIgnoreCase(tenCongNgheMang, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllTenCongNgheMangNames() {
        log.info("Getting all network technology names");
        return repository.findByDeletedFalse()
                .stream()
                .map(CongNgheMang::getTenCongNgheMang)
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
    public boolean existsByTenCongNgheMang(String tenCongNgheMang, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByTenCongNgheMangAndDeletedFalse(tenCongNgheMang, excludeId);
        }
        return repository.existsByTenCongNgheMangAndDeletedFalse(tenCongNgheMang);
    }

    private CongNgheMangResponse convertToResponse(CongNgheMang entity) {
        return CongNgheMangResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenCongNgheMang(entity.getTenCongNgheMang())
                .deleted(entity.getDeleted())
                .build();
    }
}