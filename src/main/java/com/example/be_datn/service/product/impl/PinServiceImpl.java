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
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<PinResponse> getAllPinList() {
        log.info("Getting all batteries as list");
        return repository.findByDeletedFalse().stream()
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
            log.info("Creating new battery with code: {}", request.getMa());

            if (repository.existsByMaAndDeletedFalse(request.getMa())) {
                log.error("Battery code already exists: {}", request.getMa());
                throw new RuntimeException("Mã pin đã tồn tại!");
            }

            if (repository.existsByLoaiPinAndDeletedFalse(request.getLoaiPin())) {
                log.error("Battery type already exists: {}", request.getLoaiPin());
                throw new RuntimeException("Loại pin đã tồn tại!");
            }

            Optional<Pin> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
            Optional<Pin> existingByName = repository.findByLoaiPinAndDeletedTrue(request.getLoaiPin());

            if (existingByCode.isPresent()) {
                log.info("Restoring soft-deleted battery by code: {}", request.getMa());
                Pin entity = existingByCode.get();
                entity.setDeleted(false);
                entity.setLoaiPin(request.getLoaiPin());
                entity.setDungLuongPin(request.getDungLuongPin());
                return convertToResponse(repository.save(entity));
            }

            if (existingByName.isPresent()) {
                log.info("Restoring soft-deleted battery by type: {}", request.getLoaiPin());
                Pin entity = existingByName.get();
                entity.setDeleted(false);
                entity.setMa(request.getMa());
                entity.setDungLuongPin(request.getDungLuongPin());
                return convertToResponse(repository.save(entity));
            }

            Pin entity = Pin.builder()
                    .ma(request.getMa())
                    .loaiPin(request.getLoaiPin())
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

            if (!entity.getMa().equals(request.getMa()) &&
                    repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
                log.error("Battery code already exists during update: {}", request.getMa());
                throw new RuntimeException("Mã pin đã tồn tại!");
            }

            if (!entity.getLoaiPin().equals(request.getLoaiPin()) &&
                    repository.existsByLoaiPinAndDeletedFalse(request.getLoaiPin(), id)) {
                log.error("Battery type already exists during update: {}", request.getLoaiPin());
                throw new RuntimeException("Loại pin đã tồn tại!");
            }

            entity.setMa(request.getMa());
            entity.setLoaiPin(request.getLoaiPin());
            entity.setDungLuongPin(request.getDungLuongPin());

            Pin updatedEntity = repository.save(entity);
            log.info("Updated battery with id: {}", id);
            return convertToResponse(updatedEntity);
        }

        @Override
        @Transactional
        public void deletePin(Integer id) {
            log.info("Soft deleting battery with id: {}", id);

            Pin entity = repository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> {
                        log.error("Battery not found for deletion with id: {}", id);
                        return new RuntimeException("Pin không tồn tại hoặc đã bị xóa!");
                    });

            entity.setDeleted(true);
            repository.save(entity);
            log.info("Soft deleted battery with id: {}", id);
        }

        @Override
        public Page<PinResponse> searchPin(String keyword, Pageable pageable) {
            log.info("Searching batteries with keyword: {}", keyword);
            return repository.searchByKeyword(keyword, pageable)
                    .map(this::convertToResponse);
        }

        @Override
        public Page<PinResponse> filterByLoaiPin(String loaiPin, Pageable pageable) {
            log.info("Filtering batteries by type: {}", loaiPin);
            return repository.findByLoaiPinIgnoreCase(loaiPin, pageable)
                    .map(this::convertToResponse);
        }

        @Override
        public List<String> getAllLoaiPinNames() {
            log.info("Getting all battery type names");
            return repository.findByDeletedFalse()
                    .stream()
                    .map(Pin::getLoaiPin)
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
        public boolean existsByLoaiPin(String loaiPin, Integer excludeId) {
            if (excludeId != null) {
                return repository.existsByLoaiPinAndDeletedFalse(loaiPin, excludeId);
            }
            return repository.existsByLoaiPinAndDeletedFalse(loaiPin);
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