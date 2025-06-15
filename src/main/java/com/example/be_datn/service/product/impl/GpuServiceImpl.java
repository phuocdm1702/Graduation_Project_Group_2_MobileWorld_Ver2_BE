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
        log.info("Getting all GPUs with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
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
        log.info("Creating new GPU with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("GPU code already exists: {}", request.getMa());
            throw new RuntimeException("Mã GPU đã tồn tại!");
        }

        if (repository.existsByTenGpuAndDeletedFalse(request.getTenGpu())) {
            log.error("GPU name already exists: {}", request.getTenGpu());
            throw new RuntimeException("Tên GPU đã tồn tại!");
        }

        Optional<Gpu> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<Gpu> existingByName = repository.findByTenGpuAndDeletedTrue(request.getTenGpu());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted GPU by code: {}", request.getMa());
            Gpu entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setTenGpu(request.getTenGpu());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted GPU by name: {}", request.getTenGpu());
            Gpu entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        Gpu entity = Gpu.builder()
                .ma(request.getMa())
                .tenGpu(request.getTenGpu())
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

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("GPU code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã GPU đã tồn tại!");
        }

        if (!entity.getTenGpu().equals(request.getTenGpu()) &&
                repository.existsByTenGpuAndDeletedFalse(request.getTenGpu(), id)) {
            log.error("GPU name already exists during update: {}", request.getTenGpu());
            throw new RuntimeException("Tên GPU đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setTenGpu(request.getTenGpu());

        Gpu updatedEntity = repository.save(entity);
        log.info("Updated GPU with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteGpu(Integer id) {
        log.info("Soft deleting GPU with id: {}", id);

        Gpu entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("GPU not found for deletion with id: {}", id);
                    return new RuntimeException("GPU không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted GPU with id: {}", id);
    }

    @Override
    public Page<GpuResponse> searchGpu(String keyword, Pageable pageable) {
        log.info("Searching GPUs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<GpuResponse> filterByTenGpu(String tenGpu, Pageable pageable) {
        log.info("Filtering GPUs by name: {}", tenGpu);
        return repository.findByTenGpuIgnoreCase(tenGpu, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllTenGpuNames() {
        log.info("Getting all GPU names");
        return repository.findByDeletedFalse()
                .stream()
                .map(Gpu::getTenGpu)
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
    public boolean existsByTenGpu(String tenGpu, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByTenGpuAndDeletedFalse(tenGpu, excludeId);
        }
        return repository.existsByTenGpuAndDeletedFalse(tenGpu);
    }

    private GpuResponse convertToResponse(Gpu entity) {
        return GpuResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenGpu(entity.getTenGpu())
                .deleted(entity.getDeleted())
                .build();
    }
}