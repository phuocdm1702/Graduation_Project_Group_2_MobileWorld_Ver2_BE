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
        log.info("Getting all external memory supports with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<HoTroBoNhoNgoaiResponse> getAllHoTroBoNhoNgoaiList() {
        log.info("Getting all external memory supports as list");
        return repository.findByDeletedFalse().stream()
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
        log.info("Creating new external memory support with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("External memory support code already exists: {}", request.getMa());
            throw new RuntimeException("Mã hỗ trợ bộ nhớ ngoài đã tồn tại!");
        }

        if (repository.existsByHoTroBoNhoNgoaiAndDeletedFalse(request.getHoTroBoNhoNgoai())) {
            log.error("External memory support name already exists: {}", request.getHoTroBoNhoNgoai());
            throw new RuntimeException("Hỗ trợ bộ nhớ ngoài đã tồn tại!");
        }

        Optional<HoTroBoNhoNgoai> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<HoTroBoNhoNgoai> existingByName = repository.findByHoTroBoNhoNgoaiAndDeletedTrue(request.getHoTroBoNhoNgoai());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted external memory support by code: {}", request.getMa());
            HoTroBoNhoNgoai entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setHoTroBoNhoNgoai(request.getHoTroBoNhoNgoai());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted external memory support by name: {}", request.getHoTroBoNhoNgoai());
            HoTroBoNhoNgoai entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            return convertToResponse(repository.save(entity));
        }

        HoTroBoNhoNgoai entity = HoTroBoNhoNgoai.builder()
                .ma(request.getMa())
                .hoTroBoNhoNgoai(request.getHoTroBoNhoNgoai())
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

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("External memory support code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã hỗ trợ bộ nhớ ngoài đã tồn tại!");
        }

        if (!entity.getHoTroBoNhoNgoai().equals(request.getHoTroBoNhoNgoai()) &&
                repository.existsByHoTroBoNhoNgoaiAndDeletedFalse(request.getHoTroBoNhoNgoai(), id)) {
            log.error("External memory support name already exists during update: {}", request.getHoTroBoNhoNgoai());
            throw new RuntimeException("Hỗ trợ bộ nhớ ngoài đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setHoTroBoNhoNgoai(request.getHoTroBoNhoNgoai());

        HoTroBoNhoNgoai updatedEntity = repository.save(entity);
        log.info("Updated external memory support with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteHoTroBoNhoNgoai(Integer id) {
        log.info("Soft deleting external memory support with id: {}", id);

        HoTroBoNhoNgoai entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("External memory support not found for deletion with id: {}", id);
                    return new RuntimeException("Hỗ trợ bộ nhớ ngoài không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted external memory support with id: {}", id);
    }

    @Override
    public Page<HoTroBoNhoNgoaiResponse> searchHoTroBoNhoNgoai(String keyword, Pageable pageable) {
        log.info("Searching external memory supports with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<HoTroBoNhoNgoaiResponse> filterByHoTroBoNhoNgoai(String hoTroBoNhoNgoai, Pageable pageable) {
        log.info("Filtering external memory supports by name: {}", hoTroBoNhoNgoai);
        return repository.findByHoTroBoNhoNgoaiIgnoreCase(hoTroBoNhoNgoai, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllHoTroBoNhoNgoaiNames() {
        log.info("Getting all external memory support names");
        return repository.findByDeletedFalse()
                .stream()
                .map(HoTroBoNhoNgoai::getHoTroBoNhoNgoai)
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
    public boolean existsByHoTroBoNhoNgoai(String hoTroBoNhoNgoai, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByHoTroBoNhoNgoaiAndDeletedFalse(hoTroBoNhoNgoai, excludeId);
        }
        return repository.existsByHoTroBoNhoNgoaiAndDeletedFalse(hoTroBoNhoNgoai);
    }

    private HoTroBoNhoNgoaiResponse convertToResponse(HoTroBoNhoNgoai entity) {
        return HoTroBoNhoNgoaiResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .hoTroBoNhoNgoai(entity.getHoTroBoNhoNgoai())
                .deleted(entity.getDeleted())
                .build();
    }
}