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
import java.util.Objects;
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
        return repository.findByDeletedFalseOrderByIdDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<CongNgheManHinhResponse> getAllCongNgheManHinhList() {
        log.info("Getting all display technologies as list");
        return repository.findByDeletedFalseOrderByIdDesc().stream()
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
        log.info("Creating new display technology");

        // Kiểm tra trùng lặp: tất cả 7 trường giống hệt nhau
        boolean exists = repository.existsByCongNgheManHinhAndChuanManHinhAndKichThuocAndDoPhanGiaiAndDoSangToiDaAndTanSoQuetAndKieuManHinhAndDeletedFalse(
                request.getCongNgheManHinh().trim(),
                request.getChuanManHinh(),
                request.getKichThuoc(),
                request.getDoPhanGiai(),
                request.getDoSangToiDa(),
                request.getTanSoQuet(),
                request.getKieuManHinh());

        if (exists) {
            log.error("Display technology already exists with same specifications");
            throw new RuntimeException("Công nghệ màn hình với thông số này đã tồn tại!");
        }

        // Tìm công nghệ màn hình đã bị xóa mềm có cùng thông số
        Optional<CongNgheManHinh> existingDeleted = repository.findByCongNgheManHinhAndChuanManHinhAndKichThuocAndDoPhanGiaiAndDoSangToiDaAndTanSoQuetAndKieuManHinhAndDeletedTrue(
                request.getCongNgheManHinh().trim(),
                request.getChuanManHinh(),
                request.getKichThuoc(),
                request.getDoPhanGiai(),
                request.getDoSangToiDa(),
                request.getTanSoQuet(),
                request.getKieuManHinh());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted display technology with same specifications");
            CongNgheManHinh entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        CongNgheManHinh entity = CongNgheManHinh.builder()
                .ma("") // Không sử dụng mã nữa
                .congNgheManHinh(request.getCongNgheManHinh().trim())
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

        // Nếu không thay đổi gì thì cho phép update
        boolean isUnchanged = entity.getCongNgheManHinh().equals(request.getCongNgheManHinh().trim()) &&
                Objects.equals(entity.getChuanManHinh(), request.getChuanManHinh()) &&
                Objects.equals(entity.getKichThuoc(), request.getKichThuoc()) &&
                Objects.equals(entity.getDoPhanGiai(), request.getDoPhanGiai()) &&
                Objects.equals(entity.getDoSangToiDa(), request.getDoSangToiDa()) &&
                Objects.equals(entity.getTanSoQuet(), request.getTanSoQuet()) &&
                Objects.equals(entity.getKieuManHinh(), request.getKieuManHinh());

        if (!isUnchanged) {
            // Nếu có thay đổi, kiểm tra trùng lặp với công nghệ màn hình khác
            boolean existsOther = repository.existsByAllFieldsAndDeletedFalseAndIdNot(
                    request.getCongNgheManHinh().trim(),
                    request.getChuanManHinh(),
                    request.getKichThuoc(),
                    request.getDoPhanGiai(),
                    request.getDoSangToiDa(),
                    request.getTanSoQuet(),
                    request.getKieuManHinh(),
                    id);

            if (existsOther) {
                log.error("Display technology already exists with same specifications during update");
                throw new RuntimeException("Công nghệ màn hình với thông số này đã tồn tại!");
            }
        }

        entity.setCongNgheManHinh(request.getCongNgheManHinh().trim());
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
    public Page<CongNgheManHinhResponse> searchCongNgheManHinh(String keyword, Pageable pageable) {
        log.info("Searching display technologies with keyword: {}", keyword);
        return repository.searchByKeywordOrderByIdDesc(keyword, pageable)
                .map(this::convertToResponse);
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