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
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<RamResponse> getAllRamList() {
        log.info("Getting all RAMs as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
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
        log.info("Creating new RAM");

        // Kiểm tra trùng lặp: dung lượng RAM
        boolean exists = repository.existsByDungLuongRamAndDeletedFalse(
                request.getDungLuongRam().trim());

        if (exists) {
            log.error("RAM already exists with same capacity");
            throw new RuntimeException("RAM với dung lượng này đã tồn tại!");
        }

        // Tìm RAM đã bị xóa mềm có cùng dung lượng
        Optional<Ram> existingDeleted = repository.findByDungLuongRamAndDeletedTrue(
                request.getDungLuongRam().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted RAM with same capacity");
            Ram entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        Ram entity = Ram.builder()
                .ma("") // Không sử dụng mã nữa
                .dungLuongRam(request.getDungLuongRam().trim())
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

        String newDungLuongRam = request.getDungLuongRam().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getDungLuongRam().equals(newDungLuongRam);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với RAM khác
            boolean existsOther = repository.existsByDungLuongRamAndDeletedFalseAndIdNot(
                    newDungLuongRam, id);

            if (existsOther) {
                log.error("RAM already exists with same capacity during update");
                throw new RuntimeException("RAM với dung lượng này đã tồn tại!");
            }
        }

        entity.setDungLuongRam(newDungLuongRam);

        Ram updatedEntity = repository.save(entity);
        log.info("Updated RAM with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<RamResponse> searchRam(String keyword, Pageable pageable) {
        log.info("Searching RAMs with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    // Chuyển đổi Entity sang Response DTO
    private RamResponse convertToResponse(Ram entity) {
        return RamResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .dungLuongRam(entity.getDungLuongRam())
                .deleted(entity.getDeleted())
                .build();
    }
}