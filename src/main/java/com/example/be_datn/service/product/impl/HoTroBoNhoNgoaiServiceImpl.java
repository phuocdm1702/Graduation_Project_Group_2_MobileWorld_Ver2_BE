package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.HoTroBoNhoNgoaiRequest;
import com.example.be_datn.dto.product.response.HoTroBoNhoNgoaiResponse;
import com.example.be_datn.entity.product.HoTroBoNhoNgoai;
import com.example.be_datn.repository.product.HoTroBoNhoNgoaiRepository;
import com.example.be_datn.service.product.HoTroBoNhoNgoaiService;
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
public class HoTroBoNhoNgoaiServiceImpl implements HoTroBoNhoNgoaiService {

    private final HoTroBoNhoNgoaiRepository repository;

    @Override
    public Page<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoai(Pageable pageable) {
        log.info("Getting all external memory supports with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoaiList() {
        log.info("Getting all external memory supports as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HoTroBoNhoNgoaiResponse getHoTroBoNhoNgoaiById(Integer id) {
        log.info("Getting external memory support by id: {}", id);
        HoTroBoNhoNgoai entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("External memory support not found with id: {}", id);
                    return new RuntimeException("Hỗ trợ bộ nhớ ngoài không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public HoTroBoNhoNgoaiResponse createHoTroBoNhoNgoai(HoTroBoNhoNgoaiRequest request) {
        log.info("Creating new external memory support");

        // Kiểm tra trùng lặp: tên hỗ trợ bộ nhớ ngoài
        boolean exists = repository.existsByHoTroBoNhoNgoaiAndDeletedFalse(
                request.getHoTroBoNhoNgoai().trim());

        if (exists) {
            log.error("External memory support already exists with same name");
            throw new RuntimeException("Hỗ trợ bộ nhớ ngoài với tên này đã tồn tại!");
        }

        // Tìm hỗ trợ bộ nhớ ngoài đã bị xóa mềm có cùng tên
        Optional<HoTroBoNhoNgoai> existingDeleted = repository.findByHoTroBoNhoNgoaiAndDeletedTrue(
                request.getHoTroBoNhoNgoai().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted external memory support with same name");
            HoTroBoNhoNgoai entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        HoTroBoNhoNgoai entity = HoTroBoNhoNgoai.builder()
                .ma("") // Không sử dụng mã nữa
                .hoTroBoNhoNgoai(request.getHoTroBoNhoNgoai().trim())
                .deleted(false)
                .build();

        HoTroBoNhoNgoai savedEntity = repository.save(entity);
        log.info("Created new external memory support with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public HoTroBoNhoNgoaiResponse updateHoTroBoNhoNgoai(Integer id, HoTroBoNhoNgoaiRequest request) {
        log.info("Updating external memory support with id: {}", id);

        HoTroBoNhoNgoai entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("External memory support not found for update with id: {}", id);
                    return new RuntimeException("Hỗ trợ bộ nhớ ngoài không tồn tại hoặc đã bị xóa!");
                });

        String newHoTroBoNhoNgoai = request.getHoTroBoNhoNgoai().trim();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getHoTroBoNhoNgoai().equals(newHoTroBoNhoNgoai);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với hỗ trợ bộ nhớ ngoài khác
            boolean existsOther = repository.existsByHoTroBoNhoNgoaiAndDeletedFalseAndIdNot(
                    newHoTroBoNhoNgoai, id);

            if (existsOther) {
                log.error("External memory support already exists with same name during update");
                throw new RuntimeException("Hỗ trợ bộ nhớ ngoài với tên này đã tồn tại!");
            }
        }

        entity.setHoTroBoNhoNgoai(newHoTroBoNhoNgoai);

        HoTroBoNhoNgoai updatedEntity = repository.save(entity);
        log.info("Updated external memory support with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<HoTroBoNhoNgoaiResponse> searchHoTroBoNhoNgoai(String keyword, Pageable pageable) {
        log.info("Searching external memory supports with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    // Chuyển đổi Entity sang Response DTO
    private HoTroBoNhoNgoaiResponse convertToResponse(HoTroBoNhoNgoai entity) {
        return HoTroBoNhoNgoaiResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .hoTroBoNhoNgoai(entity.getHoTroBoNhoNgoai())
                .deleted(entity.getDeleted())
                .build();
    }
}