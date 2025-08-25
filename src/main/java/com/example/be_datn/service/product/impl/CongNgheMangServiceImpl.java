package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.CongNgheMangRequest;
import com.example.be_datn.dto.product.response.CongNgheMangResponse;
import com.example.be_datn.entity.product.CongNgheMang;
import com.example.be_datn.repository.product.CongNgheMangRepository;
import com.example.be_datn.service.product.CongNgheMangService;
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
public class CongNgheMangServiceImpl implements CongNgheMangService {

    private final CongNgheMangRepository repository;

    @Override
    public Page<CongNgheMangResponse> getAllCongNgheMang(Pageable pageable) {
        log.info("Getting all network technologies with pagination: page={}, size={} (newest first)",
                pageable.getPageNumber(), pageable.getPageSize());
        // Sử dụng method mới để sắp xếp theo ID giảm dần (mới nhất lên đầu)
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<CongNgheMangResponse> getAllCongNgheMangList() {
        log.info("Getting all network technologies as list (newest first)");
        // Sử dụng method mới để sắp xếp theo ID giảm dần (mới nhất lên đầu)
        return repository.findByDeletedFalseOrderByIdDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CongNgheMangResponse getCongNgheMangById(Integer id) {
        log.info("Getting network technology by id: {}", id);
        CongNgheMang entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Network technology not found with id: {}", id);
                    return new RuntimeException("Công nghệ mạng không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public CongNgheMangResponse createCongNgheMang(CongNgheMangRequest request) {
        log.info("Creating new network technology");

        // Kiểm tra trùng lặp: tên công nghệ mạng
        boolean exists = repository.existsByTenCongNgheMangAndDeletedFalse(
                request.getTenCongNgheMang().trim());

        if (exists) {
            log.error("Network technology already exists with same name");
            throw new RuntimeException("Công nghệ mạng với tên này đã tồn tại!");
        }

        // Tìm công nghệ mạng đã bị xóa mềm có cùng tên
        Optional<CongNgheMang> existingDeleted = repository.findByTenCongNgheMangAndDeletedTrue(
                request.getTenCongNgheMang().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted network technology with same name");
            CongNgheMang entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        CongNgheMang entity = CongNgheMang.builder()
                .ma("") // Không sử dụng mã nữa
                .tenCongNgheMang(request.getTenCongNgheMang().trim())
                .deleted(false)
                .build();

        CongNgheMang savedEntity = repository.save(entity);
        log.info("Created new network technology with id: {} (will appear at top of list)", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public CongNgheMangResponse updateCongNgheMang(Integer id, CongNgheMangRequest request) {
        log.info("Updating network technology with id: {}", id);

        CongNgheMang entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Network technology not found for update with id: {}", id);
                    return new RuntimeException("Công nghệ mạng không tồn tại hoặc đã bị xóa!");
                });

        String newTenCongNgheMang = request.getTenCongNgheMang().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getTenCongNgheMang().equals(newTenCongNgheMang);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với công nghệ mạng khác
            boolean existsOther = repository.existsByTenCongNgheMangAndDeletedFalseAndIdNot(
                    newTenCongNgheMang, id);

            if (existsOther) {
                log.error("Network technology already exists with same name during update");
                throw new RuntimeException("Công nghệ mạng với tên này đã tồn tại!");
            }
        }

        entity.setTenCongNgheMang(newTenCongNgheMang);

        CongNgheMang updatedEntity = repository.save(entity);
        log.info("Updated network technology with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<CongNgheMangResponse> searchCongNgheMang(String keyword, Pageable pageable) {
        log.info("Searching network technologies with keyword: {} (newest first)", keyword);
        // Sử dụng method mới để sắp xếp theo ID giảm dần (mới nhất lên đầu)
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    // Chuyển đổi Entity sang Response DTO
    private CongNgheMangResponse convertToResponse(CongNgheMang entity) {
        return CongNgheMangResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .tenCongNgheMang(entity.getTenCongNgheMang())
                .deleted(entity.getDeleted())
                .build();
    }
}