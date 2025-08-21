package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.NhaSanXuatRequest;
import com.example.be_datn.dto.product.response.NhaSanXuatResponse;
import com.example.be_datn.entity.product.NhaSanXuat;
import com.example.be_datn.repository.product.NhaSanXuatRepository;
import com.example.be_datn.service.product.NhaSanXuatService;
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
public class NhaSanXuatServiceImpl implements NhaSanXuatService {

    private final NhaSanXuatRepository repository;

    @Override
    public Page<NhaSanXuatResponse> getAllNhaSanXuat(Pageable pageable) {
        log.info("Getting all manufacturers with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<NhaSanXuatResponse> getAllNhaSanXuatList() {
        log.info("Getting all manufacturers as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NhaSanXuatResponse getNhaSanXuatById(Integer id) {
        log.info("Getting manufacturer by id: {}", id);
        NhaSanXuat entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Manufacturer not found with id: {}", id);
                    return new RuntimeException("Nhà sản xuất không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public NhaSanXuatResponse createNhaSanXuat(NhaSanXuatRequest request) {
        log.info("Creating new manufacturer with code: {}", request.getMa());

        // Kiểm tra trùng lặp với các bản ghi chưa xóa mềm
        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Manufacturer code already exists: {}", request.getMa());
            throw new RuntimeException("Mã nhà sản xuất đã tồn tại!");
        }

        if (repository.existsByNhaSanXuatAndDeletedFalse(request.getNhaSanXuat())) {
            log.error("Manufacturer name already exists: {}", request.getNhaSanXuat());
            throw new RuntimeException("Tên nhà sản xuất đã tồn tại!");
        }

        // Kiểm tra và khôi phục bản ghi đã xóa mềm
        Optional<NhaSanXuat> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<NhaSanXuat> existingByName = repository.findByNhaSanXuatAndDeletedTrue(request.getNhaSanXuat());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted manufacturer by code: {}", request.getMa());
            NhaSanXuat entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setNhaSanXuat(request.getNhaSanXuat());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted manufacturer by name: {}", request.getNhaSanXuat());
            NhaSanXuat entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        // Tạo mới
        NhaSanXuat entity = NhaSanXuat.builder()
                .ma("")
                .nhaSanXuat(request.getNhaSanXuat())
                .deleted(false)
                .build();

        NhaSanXuat savedEntity = repository.save(entity);
        log.info("Created new manufacturer with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public NhaSanXuatResponse updateNhaSanXuat(Integer id, NhaSanXuatRequest request) {
        log.info("Updating manufacturer with id: {}", id);

        NhaSanXuat entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Manufacturer not found for update with id: {}", id);
                    return new RuntimeException("Nhà sản xuất không tồn tại hoặc đã bị xóa!");
                });

        // Kiểm tra trùng lặp mã, loại trừ bản ghi hiện tại
        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Manufacturer code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã nhà sản xuất đã tồn tại!");
        }

        // Kiểm tra trùng lặp tên, loại trừ bản ghi hiện tại
        if (!entity.getNhaSanXuat().equals(request.getNhaSanXuat()) &&
                repository.existsByNhaSanXuatAndDeletedFalse(request.getNhaSanXuat(), id)) {
            log.error("Manufacturer name already exists during update: {}", request.getNhaSanXuat());
            throw new RuntimeException("Tên nhà sản xuất đã tồn tại!");
        }

        // Cập nhật thông tin
        entity.setMa(request.getMa());
        entity.setNhaSanXuat(request.getNhaSanXuat());

        NhaSanXuat updatedEntity = repository.save(entity);
        log.info("Updated manufacturer with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteNhaSanXuat(Integer id) {
        log.info("Soft deleting manufacturer with id: {}", id);

        NhaSanXuat entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Manufacturer not found for deletion with id: {}", id);
                    return new RuntimeException("Nhà sản xuất không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted manufacturer with id: {}", id);
    }

    @Override
    public Page<NhaSanXuatResponse> searchNhaSanXuat(String keyword, Pageable pageable) {
        log.info("Searching manufacturers with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<NhaSanXuatResponse> filterByNhaSanXuat(String nhaSanXuat, Pageable pageable) {
        log.info("Filtering manufacturers by name: {}", nhaSanXuat);
        return repository.findByNhaSanXuatIgnoreCase(nhaSanXuat, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllManufacturerNames() {
        log.info("Getting all manufacturer names");
        return repository.findByDeletedFalse()
                .stream()
                .map(NhaSanXuat::getNhaSanXuat)
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
    public boolean existsByNhaSanXuat(String nhaSanXuat, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByNhaSanXuatAndDeletedFalse(nhaSanXuat, excludeId);
        }
        return repository.existsByNhaSanXuatAndDeletedFalse(nhaSanXuat);
    }

    // Chuyển đổi Entity sang Response DTO
    private NhaSanXuatResponse convertToResponse(NhaSanXuat entity) {
        return NhaSanXuatResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .nhaSanXuat(entity.getNhaSanXuat())
                .deleted(entity.getDeleted())
                .build();
    }
}