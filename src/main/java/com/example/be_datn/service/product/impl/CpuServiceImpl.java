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
        log.info("Creating new CPU");

        // Kiểm tra trùng lặp: cả tên CPU và số nhân giống hệt nhau
        boolean exists = repository.existsByTenCpuAndSoNhanAndDeletedFalse(
                request.getTenCpu().trim(),
                request.getSoNhan());

        if (exists) {
            log.error("CPU already exists with same name and core count");
            throw new RuntimeException("CPU với tên và số nhân này đã tồn tại!");
        }

        // Tìm CPU đã bị xóa mềm có cùng tên và số nhân
        Optional<Cpu> existingDeleted = repository.findByTenCpuAndSoNhanAndDeletedTrue(
                request.getTenCpu().trim(),
                request.getSoNhan());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted CPU with same name and core count");
            Cpu entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        Cpu entity = Cpu.builder()
                .ma("") // Không sử dụng mã nữa
                .tenCpu(request.getTenCpu().trim())
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

        String newTenCpu = request.getTenCpu().trim();
        Integer newSoNhan = request.getSoNhan();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getTenCpu().equals(newTenCpu) &&
                entity.getSoNhan().equals(newSoNhan);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với CPU khác
            boolean existsOther = repository.existsByTenCpuAndSoNhanAndDeletedFalseAndIdNot(
                    newTenCpu, Integer.valueOf(newSoNhan), id);

            if (existsOther) {
                log.error("CPU already exists with same name and core count during update");
                throw new RuntimeException("CPU với tên và số nhân này đã tồn tại!");
            }
        }

        entity.setTenCpu(newTenCpu);
        entity.setSoNhan(newSoNhan);

        Cpu updatedEntity = repository.save(entity);
        log.info("Updated CPU with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<CpuResponse> searchCpu(String keyword, Pageable pageable) {
        log.info("Searching CPUs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
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