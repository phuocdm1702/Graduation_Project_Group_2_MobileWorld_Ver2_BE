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
                    return new RuntimeException("Hỗ trợ công nghệ sạc không tồn tại hoặc đã bị xóa!");
                });
        return convertToResponse(entity);
    }

    @Override
    @Transactional
    public HoTroCongNgheSacResponse createHoTroCongNgheSac(HoTroCongNgheSacRequest request) {
        log.info("Creating new charging technology");

        boolean exists = repository.existsByCongSacAndCongNgheHoTroAndDeletedFalse(
                request.getCongSac().trim(),
                request.getCongNgheHoTro().trim());

        if (exists) {
            log.error("Charging technology already exists with same port and support technology");
            throw new RuntimeException("Cổng sạc với Công nghệ hỗ trợ này đã tồn tại!");
        }

        Optional<HoTroCongNgheSac> existingDeleted = repository.findByCongSacAndCongNgheHoTroAndDeletedTrue(
                request.getCongSac().trim(),
                request.getCongNgheHoTro().trim());

        if (existingDeleted.isPresent()) {
            log.info("Restoring soft-deleted charging technology with same specs");
            HoTroCongNgheSac entity = existingDeleted.get();
            entity.setDeleted(false);
            return convertToResponse(repository.save(entity));
        }

        HoTroCongNgheSac entity = HoTroCongNgheSac.builder()
                .ma("")
                .congSac(request.getCongSac().trim())
                .congNgheHoTro(request.getCongNgheHoTro().trim())
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
                    return new RuntimeException("Hỗ trợ công nghệ sạc không tồn tại hoặc đã bị xóa!");
                });

        String newCongSac = request.getCongSac().trim();
        String newCongNgheHoTro = request.getCongNgheHoTro().trim();

        boolean isUnchanged = entity.getCongSac().equals(newCongSac) &&
                entity.getCongNgheHoTro().equals(newCongNgheHoTro);

        if (!isUnchanged) {
            boolean existsOther = repository.existsByCongSacAndCongNgheHoTroAndDeletedFalseAndIdNot(
                    newCongSac, newCongNgheHoTro, id);

            if (existsOther) {
                log.error("Charging technology already exists with same port and support technology during update");
                throw new RuntimeException("Cổng sạc với Công nghệ hỗ trợ này đã tồn tại!");
            }
        }

        entity.setCongSac(newCongSac);
        entity.setCongNgheHoTro(newCongNgheHoTro);

        HoTroCongNgheSac updatedEntity = repository.save(entity);
        log.info("Updated charging technology with id: {}", id);
        return convertToResponse(updatedEntity);
    }

    @Override
    public Page<HoTroCongNgheSacResponse> searchHoTroCongNgheSac(String keyword, Pageable pageable) {
        log.info("Searching charging technologies with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
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