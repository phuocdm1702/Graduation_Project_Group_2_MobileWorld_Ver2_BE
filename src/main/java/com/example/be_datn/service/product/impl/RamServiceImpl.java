package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.RamRequest;
import com.example.be_datn.dto.product.response.RamResponse;
import com.example.be_datn.entity.product.Ram;
import com.example.be_datn.repository.product.RamRepository;
import com.example.be_datn.service.product.RamService;
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
public class RamServiceImpl implements RamService {

    private final RamRepository repository;

    @Override
    public Page<RamResponse> getAllRam(Pageable pageable) {
        log.info("Getting all RAMs with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<RamResponse> getAllRamList() {
        log.info("Getting all RAMs as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RamResponse getRamById(Integer id) {
        log.info("Getting RAM by id: {}", id);
        Ram entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("RAM not found with id: {}", id);
                    return new RuntimeException("RAM không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public RamResponse createRam(RamRequest request) {
        log.info("Creating new RAM with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("RAM code already exists: {}", request.getMa());
            throw new RuntimeException("Mã RAM đã tồn tại!");
        }

        if (repository.existsByDungLuongRamAndDeletedFalse(request.getDungLuongRam())) {
            log.error("RAM capacity already exists: {}", request.getDungLuongRam());
            throw new RuntimeException("Dung lượng RAM đã tồn tại!");
        }

        Optional<Ram> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<Ram> existingByName = repository.findByDungLuongRamAndDeletedTrue(request.getDungLuongRam());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted RAM by code: {}", request.getMa());
            Ram entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setDungLuongRam(request.getDungLuongRam());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted RAM by capacity: {}", request.getDungLuongRam());
            Ram entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        Ram entity = Ram.builder()
                .ma(request.getMa())
                .dungLuongRam(request.getDungLuongRam())
                .deleted(false)
                .build();

        Ram savedEntity = repository.save(entity);
        log.info("Created new RAM with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public RamResponse updateRam(Integer id, RamRequest request) {
        log.info("Updating RAM with id: {}", id);

        Ram entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("RAM not found for update with id: {}", id);
                    return new RuntimeException("RAM không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("RAM code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã RAM đã tồn tại!");
        }

        if (!entity.getDungLuongRam().equals(request.getDungLuongRam()) &&
                repository.existsByDungLuongRamAndDeletedFalse(request.getDungLuongRam(), id)) {
            log.error("RAM capacity already exists during update: {}", request.getDungLuongRam());
            throw new RuntimeException("Dung lượng RAM đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setDungLuongRam(request.getDungLuongRam());

        Ram updatedEntity = repository.save(entity);
        log.info("Updated RAM with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteRam(Integer id) {
        log.info("Soft deleting RAM with id: {}", id);

        Ram entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("RAM not found for deletion with id: {}", id);
                    return new RuntimeException("RAM không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted RAM with id: {}", id);
    }

    @Override
    public Page<RamResponse> searchRam(String keyword, Pageable pageable) {
        log.info("Searching RAMs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<RamResponse> filterByDungLuongRam(String dungLuongRam, Pageable pageable) {
        log.info("Filtering RAMs by capacity: {}", dungLuongRam);
        return repository.findByDungLuongRamIgnoreCase(dungLuongRam, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllRamCapacities() {
        log.info("Getting all RAM capacities");
        return repository.findByDeletedFalse()
                .stream()
                .map(Ram::getDungLuongRam)
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
    public boolean existsByDungLuongRam(String dungLuongRam, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByDungLuongRamAndDeletedFalse(dungLuongRam, excludeId);
        }
        return repository.existsByDungLuongRamAndDeletedFalse(dungLuongRam);
    }

    private RamResponse convertToResponse(Ram entity) {
        return RamResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .dungLuongRam(entity.getDungLuongRam())
                .deleted(entity.getDeleted())
                .build();
    }
}