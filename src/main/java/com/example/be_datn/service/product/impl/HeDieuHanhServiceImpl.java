package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.HeDieuHanhRequest;
import com.example.be_datn.dto.product.response.HeDieuHanhResponse;
import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.repository.product.HeDieuHanhRepository;
import com.example.be_datn.service.product.HeDieuHanhService;
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
public class HeDieuHanhServiceImpl implements HeDieuHanhService {

    private final HeDieuHanhRepository repository;

    @Override
    public Page<HeDieuHanhResponse> getAllHeDieuHanh(Pageable pageable) {
        log.info("Getting all operating systems with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<HeDieuHanhResponse> getAllHeDieuHanhList() {
        log.info("Getting all operating systems as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HeDieuHanhResponse getHeDieuHanhById(Integer id) {
        log.info("Getting operating system by id: {}", id);
        HeDieuHanh entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Operating system not found with id: {}", id);
                    return new RuntimeException("Hệ điều hành không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public HeDieuHanhResponse createHeDieuHanh(HeDieuHanhRequest request) {
        log.info("Creating new operating system");

        // Kiểm tra trùng lặp: cả tên và phiên bản giống hệt nhau
        boolean exists = repository.existsByHeDieuHanhAndPhienBanAndDeletedFalse(
                request.getHeDieuHanh().trim(),
                request.getPhienBan().trim());

        if (exists) {
            log.error("Operating system already exists with same name and version");
            throw new RuntimeException("Hệ điều hành với tên và phiên bản này đã tồn tại!");
        }

        // Tìm hệ điều hành đã bị xóa mềm có cùng thông số
        Optional<HeDieuHanh> existingDeleted = repository.findByHeDieuHanhAndPhienBanAndDeletedTrue(
                request.getHeDieuHanh().trim(),
                request.getPhienBan().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted operating system with same specs");
            HeDieuHanh entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        HeDieuHanh entity = HeDieuHanh.builder()
                .ma("") // Không sử dụng mã nữa
                .heDieuHanh(request.getHeDieuHanh().trim())
                .phienBan(request.getPhienBan().trim())
                .deleted(false)
                .build();

        HeDieuHanh savedEntity = repository.save(entity);
        log.info("Created new operating system with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public HeDieuHanhResponse updateHeDieuHanh(Integer id, HeDieuHanhRequest request) {
        log.info("Updating operating system with id: {}", id);

        HeDieuHanh entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Operating system not found for update with id: {}", id);
                    return new RuntimeException("Hệ điều hành không tồn tại hoặc đã bị xóa!");
                });

        String newHeDieuHanh = request.getHeDieuHanh().trim();
        String newPhienBan = request.getPhienBan().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getHeDieuHanh().equals(newHeDieuHanh) &&
                entity.getPhienBan().equals(newPhienBan);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với hệ điều hành khác
            boolean existsOther = repository.existsByHeDieuHanhAndPhienBanAndDeletedFalseAndIdNot(
                    newHeDieuHanh, newPhienBan, id);

            if (existsOther) {
                log.error("Operating system already exists with same name and version during update");
                throw new RuntimeException("Hệ điều hành với tên và phiên bản này đã tồn tại!");
            }
        }

        entity.setHeDieuHanh(newHeDieuHanh);
        entity.setPhienBan(newPhienBan);

        HeDieuHanh updatedEntity = repository.save(entity);
        log.info("Updated operating system with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<HeDieuHanhResponse> searchHeDieuHanh(String keyword, Pageable pageable) {
        log.info("Searching operating systems with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    private HeDieuHanhResponse convertToResponse(HeDieuHanh entity) {
        return HeDieuHanhResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .heDieuHanh(entity.getHeDieuHanh())
                .phienBan(entity.getPhienBan())
                .deleted(entity.getDeleted())
                .build();
    }
}