package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.SimRequest;
import com.example.be_datn.dto.product.response.SimResponse;
import com.example.be_datn.entity.product.Sim;
import com.example.be_datn.repository.product.SimRepository;
import com.example.be_datn.service.product.SimService;
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
public class SimServiceImpl implements SimService {

    private final SimRepository repository;

    @Override
    public Page<SimResponse> getAllSim(Pageable pageable) {
        log.info("Getting all SIMs with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<SimResponse> getAllSimList() {
        log.info("Getting all SIMs as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SimResponse getSimById(Integer id) {
        log.info("Getting SIM by id: {}", id);
        Sim entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("SIM not found with id: {}", id);
                    return new RuntimeException("SIM không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public SimResponse createSim(SimRequest request) {
        log.info("Creating new SIM with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("SIM code already exists: {}", request.getMa());
            throw new RuntimeException("Mã SIM đã tồn tại!");
        }

        Optional<Sim> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted SIM by code: {}", request.getMa());
            Sim entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setSoLuongSimHoTro(request.getSoLuongSimHoTro());
            entity.setCacLoaiSimHoTro(request.getCacLoaiSimHoTro());
            return convertToResponse(repository.save(entity));
        }

        Sim entity = Sim.builder()
                .ma(request.getMa())
                .soLuongSimHoTro(request.getSoLuongSimHoTro())
                .cacLoaiSimHoTro(request.getCacLoaiSimHoTro())
                .deleted(false)
                .build();

        Sim savedEntity = repository.save(entity);
        log.info("Created new SIM with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public SimResponse updateSim(Integer id, SimRequest request) {
        log.info("Updating SIM with id: {}", id);

        Sim entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("SIM not found for update with id: {}", id);
                    return new RuntimeException("SIM không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("SIM code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã SIM đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setSoLuongSimHoTro(request.getSoLuongSimHoTro());
        entity.setCacLoaiSimHoTro(request.getCacLoaiSimHoTro());

        Sim updatedEntity = repository.save(entity);
        log.info("Updated SIM with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteSim(Integer id) {
        log.info("Soft deleting SIM with id: {}", id);

        Sim entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("SIM not found for deletion with id: {}", id);
                    return new RuntimeException("SIM không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted SIM with id: {}", id);
    }

    @Override
    public Page<SimResponse> searchSim(String keyword, Pageable pageable) {
        log.info("Searching SIMs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<SimResponse> filterBySoLuongSimHoTro(Integer soLuongSimHoTro, Pageable pageable) {
        log.info("Filtering SIMs by supported SIM count: {}", soLuongSimHoTro);
        return repository.findBySoLuongSimHoTro(soLuongSimHoTro, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllSimTypes() {
        log.info("Getting all SIM types");
        return repository.findByDeletedFalse()
                .stream()
                .map(Sim::getCacLoaiSimHoTro)
                .filter(type -> type != null && !type.isEmpty())
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

    private SimResponse convertToResponse(Sim entity) {
        return SimResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .soLuongSimHoTro(entity.getSoLuongSimHoTro())
                .cacLoaiSimHoTro(entity.getCacLoaiSimHoTro())
                .deleted(entity.getDeleted())
                .build();
    }
}