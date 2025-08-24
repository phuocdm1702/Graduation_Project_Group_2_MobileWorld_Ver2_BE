package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.GpuRequest;
import com.example.be_datn.dto.product.response.GpuResponse;
import com.example.be_datn.entity.product.Gpu;
import com.example.be_datn.repository.product.GpuRepository;
import com.example.be_datn.service.product.GpuService;
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
public class GpuServiceImpl implements GpuService {

    private final GpuRepository repository;

    @Override
    public Page<GpuResponse> getAllGpu(Pageable pageable) {
        log.info("Getting all GPUs with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<GpuResponse> getAllGpuList() {
        log.info("Getting all GPUs as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GpuResponse getGpuById(Integer id) {
        log.info("Getting GPU by id: {}", id);
        Gpu entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("GPU not found with id: {}", id);
                    return new RuntimeException("GPU không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public GpuResponse createGpu(GpuRequest request) {
        log.info("Creating new GPU");

        // Kiểm tra trùng lặp: tên GPU
        boolean exists = repository.existsByTenGpuAndDeletedFalse(
                request.getTenGpu().trim());

        if (exists) {
            log.error("GPU already exists with same name");
            throw new RuntimeException("GPU với tên này đã tồn tại!");
        }

        // Tìm GPU đã bị xóa mềm có cùng tên
        Optional<Gpu> existingDeleted = repository.findByTenGpuAndDeletedTrue(
                request.getTenGpu().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted GPU with same name");
            Gpu entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        Gpu entity = Gpu.builder()
                .ma("") // Không sử dụng mã nữa
                .tenGpu(request.getTenGpu().trim())
                .deleted(false)
                .build();

        Gpu savedEntity = repository.save(entity);
        log.info("Created new GPU with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public GpuResponse updateGpu(Integer id, GpuRequest request) {
        log.info("Updating GPU with id: {}", id);

        Gpu entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("GPU not found for update with id: {}", id);
                    return new RuntimeException("GPU không tồn tại hoặc đã bị xóa!");
                });

        String newTenGpu = request.getTenGpu().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getTenGpu().equals(newTenGpu);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với GPU khác
            boolean existsOther = repository.existsByTenGpuAndDeletedFalseAndIdNot(
                    newTenGpu, id);

            if (existsOther) {
                log.error("GPU already exists with same name during update");
                throw new RuntimeException("GPU với tên này đã tồn tại!");
            }
        }

        entity.setTenGpu(newTenGpu);

        Gpu updatedEntity = repository.save(entity);
        log.info("Updated GPU with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<GpuResponse> searchGpu(String keyword, Pageable pageable) {
        log.info("Searching GPUs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    // Chuyển đổi Entity sang Response DTO
    private GpuResponse convertToResponse(Gpu entity) {
        return GpuResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenGpu(entity.getTenGpu())
                .deleted(entity.getDeleted())
                .build();
    }
}