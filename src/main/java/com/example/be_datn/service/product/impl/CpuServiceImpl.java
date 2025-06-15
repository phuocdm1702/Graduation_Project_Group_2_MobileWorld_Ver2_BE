package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.CpuRequest;
import com.example.be_datn.dto.product.response.CpuResponse;
import com.example.be_datn.entity.product.Cpu;
import com.example.be_datn.repository.product.CpuRepository;
import com.example.be_datn.service.product.CpuService;
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
public class CpuServiceImpl implements CpuService {

    private final CpuRepository repository;

    @Override
    public Page<CpuResponse> getAllCpu(Pageable pageable) {
        log.info("Getting all CPUs with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<CpuResponse> getAllCpuList() {
        log.info("Getting all CPUs as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CpuResponse getCpuById(Integer id) {
        log.info("Getting CPU by id: {}", id);
        Cpu entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("CPU not found with id: {}", id);
                    return new RuntimeException("CPU không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public CpuResponse createCpu(CpuRequest request) {
        log.info("Creating new CPU with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("CPU code already exists: {}", request.getMa());
            throw new RuntimeException("Mã CPU đã tồn tại!");
        }

        if (repository.existsByTenCpuAndDeletedFalse(request.getTenCpu())) {
            log.error("CPU name already exists: {}", request.getTenCpu());
            throw new RuntimeException("Tên CPU đã tồn tại!");
        }

        Optional<Cpu> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<Cpu> existingByName = repository.findByTenCpuAndDeletedTrue(request.getTenCpu());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted CPU by code: {}", request.getMa());
            Cpu entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setTenCpu(request.getTenCpu());
            entity.setSoNhan(request.getSoNhan());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted CPU by name: {}", request.getTenCpu());
            Cpu entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            entity.setSoNhan(request.getSoNhan());
            return convertToResponse(repository.save(entity));
        }

        Cpu entity = Cpu.builder()
                .ma(request.getMa())
                .tenCpu(request.getTenCpu())
                .soNhan(request.getSoNhan())
                .deleted(false)
                .build();

        Cpu savedEntity = repository.save(entity);
        log.info("Created new CPU with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public CpuResponse updateCpu(Integer id, CpuRequest request) {
        log.info("Updating CPU with id: {}", id);

        Cpu entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("CPU not found for update with id: {}", id);
                    return new RuntimeException("CPU không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("CPU code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã CPU đã tồn tại!");
        }

        if (!entity.getTenCpu().equals(request.getTenCpu()) &&
                repository.existsByTenCpuAndDeletedFalse(request.getTenCpu(), id)) {
            log.error("CPU name already exists during update: {}", request.getTenCpu());
            throw new RuntimeException("Tên CPU đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setTenCpu(request.getTenCpu());
        entity.setSoNhan(request.getSoNhan());

        Cpu updatedEntity = repository.save(entity);
        log.info("Updated CPU with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCpu(Integer id) {
        log.info("Soft deleting CPU with id: {}", id);

        Cpu entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("CPU not found for deletion with id: {}", id);
                    return new RuntimeException("CPU không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted CPU with id: {}", id);
    }

    @Override
    public Page<CpuResponse> searchCpu(String keyword, Pageable pageable) {
        log.info("Searching CPUs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<CpuResponse> filterByTenCpu(String tenCpu, Pageable pageable) {
        log.info("Filtering CPUs by name: {}", tenCpu);
        return repository.findByTenCpuIgnoreCase(tenCpu, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllTenCpuNames() {
        log.info("Getting all CPU names");
        return repository.findByDeletedFalse()
                .stream()
                .map(Cpu::getTenCpu)
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
    public boolean existsByTenCpu(String tenCpu, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByTenCpuAndDeletedFalse(tenCpu, excludeId);
        }
        return repository.existsByTenCpuAndDeletedFalse(tenCpu);
    }

    private CpuResponse convertToResponse(Cpu entity) {
        return CpuResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenCpu(entity.getTenCpu())
                .soNhan(entity.getSoNhan())
                .deleted(entity.getDeleted())
                .build();
    }
}