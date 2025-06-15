package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.CongNgheManHinhRequest;
import com.example.be_datn.dto.product.response.CongNgheManHinhResponse;
import com.example.be_datn.entity.product.CongNgheManHinh;
import com.example.be_datn.repository.product.CongNgheManHinhRepository;
import com.example.be_datn.service.product.CongNgheManHinhService;
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
public class CongNgheManHinhServiceImpl implements CongNgheManHinhService {

    private final CongNgheManHinhRepository repository;

    @Override
    public Page<CongNgheManHinhResponse> getAllCongNgheManHinh(Pageable pageable) {
        log.info("Getting all display technologies with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repository.findByDeletedFalse(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<CongNgheManHinhResponse> getAllCongNgheManHinhList() {
        log.info("Getting all display technologies as list");
        return repository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CongNgheManHinhResponse getCongNgheManHinhById(Integer id) {
        log.info("Getting display technology by id: {}", id);
        CongNgheManHinh entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Display technology not found with id: {}", id);
                    return new RuntimeException("Công nghệ màn hình không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public CongNgheManHinhResponse createCongNgheManHinh(CongNgheManHinhRequest request) {
        log.info("Creating new display technology with code: {}", request.getMa());

        if (repository.existsByMaAndDeletedFalse(request.getMa())) {
            log.error("Display technology code already exists: {}", request.getMa());
            throw new RuntimeException("Mã công nghệ màn hình đã tồn tại!");
        }

        if (repository.existsByCongNgheManHinhAndDeletedFalse(request.getCongNgheManHinh())) {
            log.error("Display technology name already exists: {}", request.getCongNgheManHinh());
            throw new RuntimeException("Tên công nghệ màn hình đã tồn tại!");
        }

        Optional<CongNgheManHinh> existingByCode = repository.findByMaAndDeletedTrue(request.getMa());
        Optional<CongNgheManHinh> existingByName = repository.findByCongNgheManHinhAndDeletedTrue(request.getCongNgheManHinh());

        if (existingByCode.isPresent()) {
            log.info("Restoring soft-deleted display technology by code: {}", request.getMa());
            CongNgheManHinh entity = existingByCode.get();
            entity.setDeleted(false);
            entity.setCongNgheManHinh(request.getCongNgheManHinh());
            entity.setChuanManHinh(request.getChuanManHinh());
            entity.setKichThuoc(request.getKichThuoc());
            entity.setDoPhanGiai(request.getDoPhanGiai());
            entity.setDoSangToiDa(request.getDoSangToiDa());
            entity.setTanSoQuet(request.getTanSoQuet());
            entity.setKieuManHinh(request.getKieuManHinh());
            return convertToResponse(repository.save(entity));
        }

        if (existingByName.isPresent()) {
            log.info("Restoring soft-deleted display technology by name: {}", request.getCongNgheManHinh());
            CongNgheManHinh entity = existingByName.get();
            entity.setDeleted(false);
            entity.setMa(request.getMa());
            entity.setChuanManHinh(request.getChuanManHinh());
            entity.setKichThuoc(request.getKichThuoc());
            entity.setDoPhanGiai(request.getDoPhanGiai());
            entity.setDoSangToiDa(request.getDoSangToiDa());
            entity.setTanSoQuet(request.getTanSoQuet());
            entity.setKieuManHinh(request.getKieuManHinh());
            return convertToResponse(repository.save(entity));
        }

        CongNgheManHinh entity = CongNgheManHinh.builder()
                .ma(request.getMa())
                .congNgheManHinh(request.getCongNgheManHinh())
                .chuanManHinh(request.getChuanManHinh())
                .kichThuoc(request.getKichThuoc())
                .doPhanGiai(request.getDoPhanGiai())
                .doSangToiDa(request.getDoSangToiDa())
                .tanSoQuet(request.getTanSoQuet())
                .kieuManHinh(request.getKieuManHinh())
                .deleted(false)
                .build();

        CongNgheManHinh savedEntity = repository.save(entity);
        log.info("Created new display technology with id: {}", savedEntity.getId());
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public CongNgheManHinhResponse updateCongNgheManHinh(Integer id, CongNgheManHinhRequest request) {
        log.info("Updating display technology with id: {}", id);

        CongNgheManHinh entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Display technology not found for update with id: {}", id);
                    return new RuntimeException("Công nghệ màn hình không tồn tại hoặc đã bị xóa!");
                });

        if (!entity.getMa().equals(request.getMa()) &&
                repository.existsByMaAndDeletedFalse(request.getMa(), id)) {
            log.error("Display technology code already exists during update: {}", request.getMa());
            throw new RuntimeException("Mã công nghệ màn hình đã tồn tại!");
        }

        if (!entity.getCongNgheManHinh().equals(request.getCongNgheManHinh()) &&
                repository.existsByCongNgheManHinhAndDeletedFalse(request.getCongNgheManHinh(), id)) {
            log.error("Display technology name already exists during update: {}", request.getCongNgheManHinh());
            throw new RuntimeException("Tên công nghệ màn hình đã tồn tại!");
        }

        entity.setMa(request.getMa());
        entity.setCongNgheManHinh(request.getCongNgheManHinh());
        entity.setChuanManHinh(request.getChuanManHinh());
        entity.setKichThuoc(request.getKichThuoc());
        entity.setDoPhanGiai(request.getDoPhanGiai());
        entity.setDoSangToiDa(request.getDoSangToiDa());
        entity.setTanSoQuet(request.getTanSoQuet());
        entity.setKieuManHinh(request.getKieuManHinh());

        CongNgheManHinh updatedEntity = repository.save(entity);
        log.info("Updated display technology with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteCongNgheManHinh(Integer id) {
        log.info("Soft deleting display technology with id: {}", id);

        CongNgheManHinh entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Display technology not found for deletion with id: {}", id);
                    return new RuntimeException("Công nghệ màn hình không tồn tại hoặc đã bị xóa!");
                });

        entity.setDeleted(true);
        repository.save(entity);
        log.info("Soft deleted display technology with id: {}", id);
    }

    @Override
    public Page<CongNgheManHinhResponse> searchCongNgheManHinh(String keyword, Pageable pageable) {
        log.info("Searching display technologies with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public Page<CongNgheManHinhResponse> filterByCongNgheManHinh(String congNgheManHinh, Pageable pageable) {
        log.info("Filtering display technologies by name: {}", congNgheManHinh);
        return repository.findByCongNgheManHinhIgnoreCase(congNgheManHinh, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<String> getAllCongNgheManHinhNames() {
        log.info("Getting all display technology names");
        return repository.findByDeletedFalse()
                .stream()
                .map(CongNgheManHinh::getCongNgheManHinh)
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
    public boolean existsByCongNgheManHinh(String congNgheManHinh, Integer excludeId) {
        if (excludeId != null) {
            return repository.existsByCongNgheManHinhAndDeletedFalse(congNgheManHinh, excludeId);
        }
        return repository.existsByCongNgheManHinhAndDeletedFalse(congNgheManHinh);
    }

    private CongNgheManHinhResponse convertToResponse(CongNgheManHinh entity) {
        return CongNgheManHinhResponse.builder()
                .id(entity.getId())
                .ma(entity.getMa())
                .congNgheManHinh(entity.getCongNgheManHinh())
                .chuanManHinh(entity.getChuanManHinh())
                .kichThuoc(entity.getKichThuoc())
                .doPhanGiai(entity.getDoPhanGiai())
                .doSangToiDa(entity.getDoSangToiDa())
                .tanSoQuet(entity.getTanSoQuet())
                .kieuManHinh(entity.getKieuManHinh())
                .deleted(entity.getDeleted())
                .build();
    }
}