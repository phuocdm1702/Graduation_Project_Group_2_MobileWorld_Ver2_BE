package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.HoTroCongNgheSacRequest;
import com.example.be_datn.dto.product.response.HoTroCongNgheSacResponse;
import com.example.be_datn.entity.product.HoTroCongNgheSac;
import com.example.be_datn.repository.product.HoTroCongNgheSacRepository;
import com.example.be_datn.service.product.HoTroCongNgheSacService;
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
public class HoTroCongNgheSacServiceImpl implements HoTroCongNgheSacService {

    private final HoTroCongNgheSacRepository repository;

    @Override
    public Page<HoTroCongNgheSacResponse> getAllHoTroCongNgheSac(Pageable pageable) {
        log.info("Getting all charging technologies with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<HoTroCongNgheSacResponse> getAllHoTroCongNgheSacList() {
        log.info("Getting all charging technologies as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HoTroCongNgheSacResponse getHoTroCongNgheSacById(Integer id) {
        log.info("Getting charging technology by id: {}", id);
        HoTroCongNgheSac entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Charging technology not found with id: {}", id);
                    return new RuntimeException("Công nghệ sạc không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public HoTroCongNgheSacResponse createHoTroCongNgheSac(HoTroCongNgheSacRequest request) {
        log.info("Creating new charging technology with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Charging technology code already exists: {}", request.getMa());
            throw new RuntimeException("Mã công nghệ sạc đã tồn tại!");
        }

        if (request.getCongSac() != null && !request.getCongSac().isEmpty() &&
                repository.existsByCongSacAndDeletedFalse(request.getCongSac())) {
            log.error("Charging port already exists: {}", request.getCongSac());
            throw new RuntimeException("Cổng sạc đã tồn tại!");
        }

        Optional<HoTroCongNgheSac> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<HoTroCongNgheSac> existingByPort = request.getCongSac() != null && !request.getCongSac().isEmpty()
                ? repository.findByCongSacAndDeletedTrue(request.getCongSac())
                : Optional.empty();

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted charging technology by code: {}", request.getMa());
            HoTroCongNgheSac entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setCongSac(request.getCongSac());
            entity.setCongNgheHoTro(request.getCongNgheHoTro());
            return convertToResponse(repository.save(entity));
        }

        if (existingByPort.isPresent()) {
            log.info("Restoring soft-deleted charging technology by port: {}", request.getCongSac());
            HoTroCongNgheSac entity = existingByPort.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            entity.setCongNgheHoTro(request.getCongNgheHoTro());
            return convertToResponse(repository.save(entity));
        }

        HoTroCongNgheSac entity = HoTroCongNgheSac.builder()
                .ma(request.getMa())
                .congSac(request.getCongSac())
                .congNgheHoTro(request.getCongNgheHoTro())
                .deleted(false)
                .build();

        HoTroCongNgheSac savedEntity = repository.save(entity);
        log.info("Created new charging technology with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public HoTroCongNgheSacResponse updateHoTroCongNgheSac(Integer id, HoTroCongNgheSacRequest request) {
        log.info("Updating charging technology with id: {}", id);

        HoTroCongNgheSac entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Charging technology not found for update with id: {}", id);
                    return new RuntimeException("Công nghệ sạc không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Charging technology code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã công nghệ sạc đã tồn tại!");
        }

        if (request.getCongSac() != null && !request.getCongSac().isEmpty() &&
                !entity.getCongSac().equals(request.getCongSac()) &&
                repository.existsByCongSacAndDeletedFalse(request.getCongSac())) {
            log.error("Charging port already exists during update: {}", request.getCongSac());
            throw new RuntimeException("Cổng sạc đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setCongSac(request.getCongSac());
        entity.setCongNgheHoTro(request.getCongNgheHoTro());

        HoTroCongNgheSac updatedEntity = repository.save(entity);
        log.info("Updated charging technology with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteHoTroCongNgheSac(Integer id) {
        log.info("Soft deleting charging technology with id: {}", id);

        HoTroCongNgheSac entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Charging technology not found for deletion with id: {}", id);
                    return new RuntimeException("Công nghệ sạc không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted charging technology with id: {}", id);
    }

    @Override
    public Page<HoTroCongNgheSacResponse> searchHoTroCongNgheSac(String keyword, Pageable pageable) {
        log.info("Searching charging technologies with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<HoTroCongNgheSacResponse> filterByCongSac(String congSac, Pageable pageable) {
        log.info("Filtering charging technologies by port: {}", congSac);
        return repository.findByCongSacIgnoreCase(congSac, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllCongSacNames() {
        log.info("Getting all charging port names");
        return repository.findByDeletedFalse()
                .stream()
                .map(HoTroCongNgheSac::getCongSac)
                .filter(port -> port != null)
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
    public boolean existsByCongSac(String congSac, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByCongSacAndDeletedFalse(congSac, excludeId);
        }
        return repository.existsByCongSacAndDeletedFalse(congSac);
    }

    private HoTroCongNgheSacResponse convertToResponse(HoTroCongNgheSac entity) {
        return HoTroCongNgheSacResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .congSac(entity.getCongSac())
                .congNgheHoTro(entity.getCongNgheHoTro())
                .deleted(entity.getDeleted())
                .build();
    }
}