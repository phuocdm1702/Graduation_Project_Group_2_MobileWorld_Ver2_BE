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
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<HeDieuHanhResponse> getAllHeDieuHanhList() {
        log.info("Getting all operating systems as list");
        return repository.findByDeletedFalse().stream()
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
        log.info("Creating new operating system with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Operating system code already exists: {}", request.getMa());
            throw new RuntimeException("Mã hệ điều hành đã tồn tại!");
        }

        if (repository.existsByHeDieuHanhAndDeletedFalse(request.getHeDieuHanh())) {
            log.error("Operating system name already exists: {}", request.getHeDieuHanh());
            throw new RuntimeException("Tên hệ điều hành đã tồn tại!");
        }

        Optional<HeDieuHanh> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<HeDieuHanh> existingByName = repository.findByHeDieuHanhAndDeletedTrue(request.getHeDieuHanh());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted operating system by code: {}", request.getMa());
            HeDieuHanh entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setHeDieuHanh(request.getHeDieuHanh());
            entity.setPhienBan(request.getPhienBan());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted operating system by name: {}", request.getHeDieuHanh());
            HeDieuHanh entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            entity.setPhienBan(request.getPhienBan());
            return convertToResponse(repository.save(entity));
        }

        HeDieuHanh entity = HeDieuHanh.builder()
                .ma(request.getMa())
                .heDieuHanh(request.getHeDieuHanh())
                .phienBan(request.getPhienBan())
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

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Operating system code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã hệ điều hành đã tồn tại!");
        }

        if (!entity.getHeDieuHanh().equals(request.getHeDieuHanh()) &&
                repository.existsByHeDieuHanhAndDeletedFalse(request.getHeDieuHanh(), id)) {
            log.error("Operating system name already exists during update: {}", request.getHeDieuHanh());
            throw new RuntimeException("Tên hệ điều hành đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setHeDieuHanh(request.getHeDieuHanh());
        entity.setPhienBan(request.getPhienBan());

        HeDieuHanh updatedEntity = repository.save(entity);
        log.info("Updated operating system with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteHeDieuHanh(Integer id) {
        log.info("Soft deleting operating system with id: {}", id);

        HeDieuHanh entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Operating system not found for deletion with id: {}", id);
                    return new RuntimeException("Hệ điều hành không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted operating system with id: {}", id);
    }

    @Override
    public Page<HeDieuHanhResponse> searchHeDieuHanh(String keyword, Pageable pageable) {
        log.info("Searching operating systems with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<HeDieuHanhResponse> filterByHeDieuHanh(String heDieuHanh, Pageable pageable) {
        log.info("Filtering operating systems by name: {}", heDieuHanh);
        return repository.findByHeDieuHanhIgnoreCase(heDieuHanh, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllHeDieuHanhNames() {
        log.info("Getting all operating system names");
        return repository.findByDeletedFalse()
                .stream()
                .map(HeDieuHanh::getHeDieuHanh)
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
    public boolean existsByHeDieuHanh(String heDieuHanh, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByHeDieuHanhAndDeletedFalse(heDieuHanh, excludeId);
        }
        return repository.existsByHeDieuHanhAndDeletedFalse(heDieuHanh);
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