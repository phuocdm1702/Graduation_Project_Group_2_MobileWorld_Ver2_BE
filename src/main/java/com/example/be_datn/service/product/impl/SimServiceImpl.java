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
        log.info("Getting all SIMs with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<SimResponse> getAllSimList() {
        log.info("Getting all SIMs as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
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
        log.info("Creating new SIM");

        // Kiểm tra trùng lặp: cả soLuongSimHoTro và cacLoaiSimHoTro giống hệt nhau
        boolean exists = repository.existsBySoLuongSimHoTroAndCacLoaiSimHoTroAndDeletedFalse(
                request.getSoLuongSimHoTro(),
                request.getCacLoaiSimHoTro().trim());

        if (exists) {
            log.error("SIM already exists with same soLuongSimHoTro and cacLoaiSimHoTro");
            throw new RuntimeException("SIM với số lượng SIM hỗ trợ và các loại SIM hỗ trợ này đã tồn tại!");
        }

        // Tìm SIM đã bị xóa mềm có cùng thông số
        Optional<Sim> existingDeleted = repository.findBySoLuongSimHoTroAndCacLoaiSimHoTroAndDeletedTrue(
                request.getSoLuongSimHoTro(),
                request.getCacLoaiSimHoTro().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted SIM with same specs");
            Sim entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        Sim entity = Sim.builder()
                .ma("") // Không sử dụng mã nữa
                .soLuongSimHoTro(request.getSoLuongSimHoTro())
                .cacLoaiSimHoTro(request.getCacLoaiSimHoTro().trim())
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

        Integer newSoLuongSimHoTro = request.getSoLuongSimHoTro();
        String newCacLoaiSimHoTro = request.getCacLoaiSimHoTro().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getSoLuongSimHoTro().equals(newSoLuongSimHoTro) &&
                entity.getCacLoaiSimHoTro().equals(newCacLoaiSimHoTro);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với SIM khác
            boolean existsOther = repository.existsBySoLuongSimHoTroAndCacLoaiSimHoTroAndDeletedFalseAndIdNot(
                    newSoLuongSimHoTro, newCacLoaiSimHoTro, id);

            if (existsOther) {
                log.error("SIM already exists with same soLuongSimHoTro and cacLoaiSimHoTro during update");
                throw new RuntimeException("SIM với số lượng SIM hỗ trợ và các loại SIM hỗ trợ này đã tồn tại!");
            }
        }

        entity.setSoLuongSimHoTro(newSoLuongSimHoTro);
        entity.setCacLoaiSimHoTro(newCacLoaiSimHoTro);

        Sim updatedEntity = repository.save(entity);
        log.info("Updated SIM with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<SimResponse> searchSim(String keyword, Pageable pageable) {
        log.info("Searching SIMs with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
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