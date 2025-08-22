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
        log.info("Creating new manufacturer");

        // Kiểm tra trùng lặp: tên nhà sản xuất
        boolean exists = repository.existsByNhaSanXuatAndDeletedFalse(
                request.getNhaSanXuat().trim());

        if (exists) {
            log.error("Manufacturer already exists with same name");
            throw new RuntimeException("Nhà sản xuất với tên này đã tồn tại!");
        }

        // Tìm nhà sản xuất đã bị xóa mềm có cùng tên
        Optional<NhaSanXuat> existingDeleted = repository.findByNhaSanXuatAndDeletedTrue(
                request.getNhaSanXuat().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted manufacturer with same name");
            NhaSanXuat entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        NhaSanXuat entity = NhaSanXuat.builder()
                .ma("") // Không sử dụng mã nữa
                .nhaSanXuat(request.getNhaSanXuat().trim())
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

        String newNhaSanXuat = request.getNhaSanXuat().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getNhaSanXuat().equals(newNhaSanXuat);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với nhà sản xuất khác
            boolean existsOther = repository.existsByNhaSanXuatAndDeletedFalseAndIdNot(
                    newNhaSanXuat, id);

            if (existsOther) {
                log.error("Manufacturer already exists with same name during update");
                throw new RuntimeException("Nhà sản xuất với tên này đã tồn tại!");
            }
        }

        entity.setNhaSanXuat(newNhaSanXuat);

        NhaSanXuat updatedEntity = repository.save(entity);
        log.info("Updated manufacturer with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<NhaSanXuatResponse> searchNhaSanXuat(String keyword, Pageable pageable) {
        log.info("Searching manufacturers with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
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