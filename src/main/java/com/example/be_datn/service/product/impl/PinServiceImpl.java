package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.PinRequest;
import com.example.be_datn.dto.product.response.PinResponse;
import com.example.be_datn.entity.product.Pin;
import com.example.be_datn.repository.product.PinRepository;
import com.example.be_datn.service.product.PinService;
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
public class PinServiceImpl implements PinService {

    private final PinRepository repository;

    @Override
    public Page<PinResponse> getAllPin(Pageable pageable) {
        log.info("Getting all batteries with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<PinResponse> getAllPinList() {
        log.info("Getting all batteries as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PinResponse getPinById(Integer id) {
        log.info("Getting battery by id: {}", id);
        Pin entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Battery not found with id: {}", id);
                    return new RuntimeException("Pin không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public PinResponse createPin(PinRequest request) {
        log.info("Creating new battery");

        // Kiểm tra trùng lặp: loại pin và dung lượng pin giống hệt nhau
        boolean exists = repository.existsByLoaiPinAndDungLuongPinAndDeletedFalse(
                request.getLoaiPin().trim(),
                request.getDungLuongPin());

        if (exists) {
            log.error("Battery already exists with same type and capacity");
            throw new RuntimeException("Pin với loại pin và dung lượng này đã tồn tại!");
        }

        // Tìm pin đã bị xóa mềm có cùng thông số
        Optional<Pin> existingDeleted = repository.findByLoaiPinAndDungLuongPinAndDeletedTrue(
                request.getLoaiPin().trim(),
                request.getDungLuongPin());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted battery with same specs");
            Pin entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        Pin entity = Pin.builder()
                .ma("") // Không sử dụng mã nữa
                .loaiPin(request.getLoaiPin().trim())
                .dungLuongPin(request.getDungLuongPin())
                .deleted(false)
                .build();

        Pin savedEntity = repository.save(entity);
        log.info("Created new battery with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public PinResponse updatePin(Integer id, PinRequest request) {
        log.info("Updating battery with id: {}", id);

        Pin entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Battery not found for update with id: {}", id);
                    return new RuntimeException("Pin không tồn tại hoặc đã bị xóa!");
                });

        String newLoaiPin = request.getLoaiPin().trim();
        String newDungLuongPin = request.getDungLuongPin();

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getLoaiPin().equals(newLoaiPin) &&
                entity.getDungLuongPin().equals(newDungLuongPin);

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với pin khác
            boolean existsOther = repository.existsByLoaiPinAndDungLuongPinAndDeletedFalseAndIdNot(
                    newLoaiPin, newDungLuongPin, id);

            if (existsOther) {
                log.error("Battery already exists with same type and capacity during update");
                throw new RuntimeException("Pin với loại pin và dung lượng này đã tồn tại!");
            }
        }

        entity.setLoaiPin(newLoaiPin);
        entity.setDungLuongPin(newDungLuongPin);

        Pin updatedEntity = repository.save(entity);
        log.info("Updated battery with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<PinResponse> searchPin(String keyword, Pageable pageable) {
        log.info("Searching batteries with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
    }

    private PinResponse convertToResponse(Pin entity) {
        return PinResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .loaiPin(entity.getLoaiPin())
                .dungLuongPin(entity.getDungLuongPin())
                .deleted(entity.getDeleted())
                .build();
    }
}