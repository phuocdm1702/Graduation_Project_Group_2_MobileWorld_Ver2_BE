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
        log.info("Creating new camera cluster");

        // Kiểm tra trùng lặp: cả camera sau và camera trước giống hệt nhau
        boolean exists = repository.existsByThongSoCameraSauAndThongSoCameraTruocAndDeletedFalse(
                request.getThongSoCameraSau().trim(),
                request.getThongSoCameraTruoc().trim());

        if (exists) {
            log.error("Camera cluster already exists with same front and rear camera specs");
            throw new RuntimeException("Cụm camera với thông số camera trước và sau này đã tồn tại!");
        }

        // Tìm cụm camera đã bị xóa mềm có cùng thông số
        Optional<CumCamera> existingDeleted = repository.findByThongSoCameraSauAndThongSoCameraTruocAndDeletedTrue(
                request.getThongSoCameraSau().trim(),
                request.getThongSoCameraTruoc().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted camera cluster with same specs");
            CumCamera entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        CumCamera entity = CumCamera.builder()
                .ma("") // Không sử dụng mã nữa
                .thongSoCameraSau(request.getThongSoCameraSau().trim())
                .thongSoCameraTruoc(request.getThongSoCameraTruoc().trim())
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

        String newCameraSau = request.getThongSoCameraSau().trim();
        String newCameraTruoc = request.getThongSoCameraTruoc().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getThongSoCameraSau().equals(newCameraSau) &&
                entity.getThongSoCameraTruoc().equals(newCameraTruoc);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với cụm camera khác
            boolean existsOther = repository.existsByThongSoCameraSauAndThongSoCameraTruocAndDeletedFalseAndIdNot(
                    newCameraSau, newCameraTruoc, id);

            if (existsOther) {
                log.error("Camera cluster already exists with same front and rear camera specs during update");
                throw new RuntimeException("Cụm camera với thông số camera trước và sau này đã tồn tại!");
            }
        }

        entity.setThongSoCameraSau(newCameraSau);
        entity.setThongSoCameraTruoc(newCameraTruoc);

        CumCamera updatedEntity = repository.save(entity);
        log.info("Updated camera cluster with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<CumCameraResponse> searchCumCamera(String keyword, Pageable pageable) {
        log.info("Searching camera clusters with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
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