package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.CumCameraRequest;
import com.example.be_datn.dto.product.response.CumCameraResponse;
import com.example.be_datn.entity.product.CumCamera;
import com.example.be_datn.repository.product.CumCameraRepository;
import com.example.be_datn.service.product.CumCameraService;
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
public class CumCameraServiceImpl implements CumCameraService {

    private final CumCameraRepository repository;

    @Override
    public Page<CumCameraResponse> getAllCumCamera(Pageable pageable) {
        log.info("Getting all camera clusters with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<CumCameraResponse> getAllCumCameraList() {
        log.info("Getting all camera clusters as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CumCameraResponse getCumCameraById(Integer id) {
        log.info("Getting camera cluster by id: {}", id);
        CumCamera entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Camera cluster not found with id: {}", id);
                    return new RuntimeException("Cụm camera không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public CumCameraResponse createCumCamera(CumCameraRequest request) {
        log.info("Creating new camera cluster with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Camera cluster code already exists: {}", request.getMa());
            throw new RuntimeException("Mã cụm camera đã tồn tại!");
        }

        Optional<CumCamera> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted camera cluster by code: {}", request.getMa());
            CumCamera entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setThongSoCameraSau(request.getThongSoCameraSau());
            entity.setThongSoCameraTruoc(request.getThongSoCameraTruoc());
            return convertToResponse(repository.save(entity));
        }

        CumCamera entity = CumCamera.builder()
                .ma(request.getMa())
                .thongSoCameraSau(request.getThongSoCameraSau())
                .thongSoCameraTruoc(request.getThongSoCameraTruoc())
                .deleted(false)
                .build();

        CumCamera savedEntity = repository.save(entity);
        log.info("Created new camera cluster with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public CumCameraResponse updateCumCamera(Integer id, CumCameraRequest request) {
        log.info("Updating camera cluster with id: {}", id);

        CumCamera entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Camera cluster not found for update with id: {}", id);
                    return new RuntimeException("Cụm camera không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Camera cluster code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã cụm camera đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setThongSoCameraSau(request.getThongSoCameraSau());
        entity.setThongSoCameraTruoc(request.getThongSoCameraTruoc());

        CumCamera updatedEntity = repository.save(entity);
        log.info("Updated camera cluster with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCumCamera(Integer id) {
        log.info("Soft deleting camera cluster with id: {}", id);

        CumCamera entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Camera cluster not found for deletion with id: {}", id);
                    return new RuntimeException("Cụm camera không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted camera cluster with id: {}", id);
    }

    @Override
    public Page<CumCameraResponse> searchCumCamera(String keyword, Pageable pageable) {
        log.info("Searching camera clusters with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<CumCameraResponse> filterByThongSoCameraSau(String thongSoCameraSau, Pageable pageable) {
        log.info("Filtering camera clusters by rear camera specs: {}", thongSoCameraSau);
        return repository.findByThongSoCameraSauIgnoreCase(thongSoCameraSau, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllThongSoCameraSauNames() {
        log.info("Getting all rear camera specs names");
        return repository.findByDeletedFalse()
                .stream()
                .map(CumCamera::getThongSoCameraSau)
                .filter(spec -> spec != null)
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

    private CumCameraResponse convertToResponse(CumCamera entity) {
        return CumCameraResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .thongSoCameraSau(entity.getThongSoCameraSau())
                .thongSoCameraTruoc(entity.getThongSoCameraTruoc())
                .deleted(entity.getDeleted())
                .build();
    }
}