// SanPhamServiceImpl.java
package com.example.be_datn.service.product.impl;

import com.example.be_datn.dto.product.request.SanPhamRequest;
import com.example.be_datn.dto.product.response.SanPhamResponse;
import com.example.be_datn.entity.product.*;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.repository.product.SanPhamRepository;
import com.example.be_datn.service.product.SanPhamService;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SanPhamServiceImpl implements SanPhamService {
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    public SanPhamServiceImpl(SanPhamRepository sanPhamRepository, ChiTietSanPhamRepository chiTietSanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
    }

    @Override
    public Page<SanPhamResponse> getAllSanPham(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> sanPhamPage = sanPhamRepository.findByDeletedFalse(pageable);
        return mapToDTOPage(sanPhamPage, pageable);
    }

    @Override
    public List<SanPhamResponse> getAllSanPhamList() {
        List<SanPham> sanPhams = sanPhamRepository.findAllByDeletedFalse();
        return sanPhams.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SanPhamResponse> searchSanPham(
            String keyword,
            Integer idNhaSanXuat,
            Integer idHeDieuHanh,
            String heDieuHanh,
            String phienBan,
            Integer idCongNgheManHinh,
            String congNgheManHinh,
            String chuanManHinh,
            Integer idPin,
            String loaiPin,
            String dungLuongPin,
            Integer idCpu,
            Integer idGpu,
            Integer idCumCamera,
            Integer idThietKe,
            Integer idSim,
            Integer idHoTroCongNgheSac,
            Integer idCongNgheMang,
            Boolean inStock,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<SanPham> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("tenSanPham")), "%" + keyword.toLowerCase() + "%"));
            }
            if (idNhaSanXuat != null) {
                predicates.add(cb.equal(root.get("idNhaSanXuat").get("id"), idNhaSanXuat));
            }
            if (idHeDieuHanh != null) {
                predicates.add(cb.equal(root.get("idHeDieuHanh").get("id"), idHeDieuHanh));
            }
            if (heDieuHanh != null && !heDieuHanh.isEmpty()) {
                predicates.add(cb.equal(root.get("idHeDieuHanh").get("heDieuHanh"), heDieuHanh));
            }
            if (phienBan != null && !phienBan.isEmpty()) {
                predicates.add(cb.equal(root.get("idHeDieuHanh").get("phienBan"), phienBan));
            }
            if (idCongNgheManHinh != null) {
                predicates.add(cb.equal(root.get("congNgheManHinh").get("id"), idCongNgheManHinh));
            }
            if (congNgheManHinh != null && !congNgheManHinh.isEmpty()) {
                predicates.add(cb.equal(root.get("congNgheManHinh").get("congNgheManHinh"), congNgheManHinh));
            }
            if (chuanManHinh != null && !chuanManHinh.isEmpty()) {
                predicates.add(cb.equal(root.get("congNgheManHinh").get("chuanManHinh"), chuanManHinh));
            }
            if (idPin != null) {
                predicates.add(cb.equal(root.get("idPin").get("id"), idPin));
            }
            if (loaiPin != null && !loaiPin.isEmpty()) {
                predicates.add(cb.equal(root.get("idPin").get("loaiPin"), loaiPin));
            }
            if (dungLuongPin != null && !dungLuongPin.isEmpty()) {
                predicates.add(cb.equal(root.get("idPin").get("dungLuongPin"), dungLuongPin));
            }
            if (idCpu != null) {
                predicates.add(cb.equal(root.get("idCpu").get("id"), idCpu));
            }
            if (idGpu != null) {
                predicates.add(cb.equal(root.get("idGpu").get("id"), idGpu));
            }
            if (idCumCamera != null) {
                predicates.add(cb.equal(root.get("idCumCamera").get("id"), idCumCamera));
            }
            if (idThietKe != null) {
                predicates.add(cb.equal(root.get("idThietKe").get("id"), idThietKe));
            }
            if (idSim != null) {
                predicates.add(cb.equal(root.get("idSim").get("id"), idSim));
            }
            if (idHoTroCongNgheSac != null) {
                predicates.add(cb.equal(root.get("hoTroCongNgheSac").get("id"), idHoTroCongNgheSac));
            }
            if (idCongNgheMang != null) {
                predicates.add(cb.equal(root.get("idCongNgheMang").get("id"), idCongNgheMang));
            }

            if (inStock != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                var subRoot = subquery.from(ChiTietSanPham.class);
                subquery.select(cb.count(subRoot));
                subquery.where(
                        cb.equal(subRoot.get("idSanPham").get("id"), root.get("id")),
                        cb.equal(subRoot.get("deleted"), false)
                );

                if (inStock) {
                    predicates.add(cb.greaterThan(subquery, 0L));
                } else {
                    predicates.add(cb.equal(subquery, 0L));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(spec, pageable);
        return mapToDTOPage(sanPhamPage, pageable);
    }

    private Page<SanPhamResponse> mapToDTOPage(Page<SanPham> sanPhamPage, Pageable pageable) {
        List<SanPhamResponse> dtos = sanPhamPage.getContent().stream().map(this::mapToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, sanPhamPage.getTotalElements());
    }

    @Override
    public SanPhamResponse mapToDTO(SanPham sanPham) {
        return new SanPhamResponse(
                sanPham.getId(),
                sanPham.getMa(),
                sanPham.getTenSanPham(),
                sanPham.getIdChiSoKhangBuiVaNuoc() != null ? sanPham.getIdChiSoKhangBuiVaNuoc().getId() : null,
                sanPham.getIdChiSoKhangBuiVaNuoc() != null ? sanPham.getIdChiSoKhangBuiVaNuoc().getTenChiSo() : "N/A",
                sanPham.getIdCongNgheMang() != null ? sanPham.getIdCongNgheMang().getId() : null,
                sanPham.getIdCongNgheMang() != null ? sanPham.getIdCongNgheMang().getTenCongNgheMang() : "N/A",
                sanPham.getIdCpu() != null ? sanPham.getIdCpu().getId() : null,
                sanPham.getIdCpu() != null ? sanPham.getIdCpu().getTenCpu() : "N/A",
                sanPham.getIdCumCamera() != null ? sanPham.getIdCumCamera().getId() : null,
                sanPham.getIdCumCamera() != null ? sanPham.getIdCumCamera().getThongSoCameraSau() : "N/A",
                sanPham.getIdCumCamera() != null ? sanPham.getIdCumCamera().getThongSoCameraTruoc() : "N/A",
                sanPham.getIdGpu() != null ? sanPham.getIdGpu().getId() : null,
                sanPham.getIdGpu() != null ? sanPham.getIdGpu().getTenGpu() : "N/A",
                sanPham.getIdHeDieuHanh() != null ? sanPham.getIdHeDieuHanh().getId() : null,
                sanPham.getIdHeDieuHanh() != null ? sanPham.getIdHeDieuHanh().getHeDieuHanh() : "N/A",
                sanPham.getIdHeDieuHanh() != null ? sanPham.getIdHeDieuHanh().getPhienBan() : "N/A",
                sanPham.getIdHoTroBoNhoNgoai() != null ? sanPham.getIdHoTroBoNhoNgoai().getId() : null,
                sanPham.getIdHoTroBoNhoNgoai() != null ? sanPham.getIdHoTroBoNhoNgoai().getHoTroBoNhoNgoai() : "N/A",
                sanPham.getIdNhaSanXuat() != null ? sanPham.getIdNhaSanXuat().getId() : null,
                sanPham.getIdNhaSanXuat() != null ? sanPham.getIdNhaSanXuat().getNhaSanXuat() : "N/A",
                sanPham.getIdPin() != null ? sanPham.getIdPin().getId() : null,
                sanPham.getIdPin() != null ? sanPham.getIdPin().getDungLuongPin() : "N/A",
                sanPham.getIdSim() != null ? sanPham.getIdSim().getId() : null,
                sanPham.getIdSim() != null ? sanPham.getIdSim().getCacLoaiSimHoTro() : "N/A",
                sanPham.getIdThietKe() != null ? sanPham.getIdThietKe().getId() : null,
                sanPham.getIdThietKe() != null ? sanPham.getIdThietKe().getChatLieuKhung() : "N/A",
                sanPham.getIdThietKe() != null ? sanPham.getIdThietKe().getChatLieuMatLung() : "N/A",
                sanPham.getHoTroCongNgheSac() != null ? sanPham.getHoTroCongNgheSac().getId() : null,
                sanPham.getHoTroCongNgheSac() != null ? sanPham.getHoTroCongNgheSac().getCongNgheHoTro() : "N/A",
                sanPham.getHoTroCongNgheSac() != null ? sanPham.getHoTroCongNgheSac().getCongSac() : "N/A",
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getId() : null,
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getCongNgheManHinh() : "N/A",
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getKichThuoc() : "N/A",
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getDoPhanGiai() : "N/A",
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getDoSangToiDa() : "N/A",
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getTanSoQuet() : "N/A",
                sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getKieuManHinh() : "N/A",
                sanPham.getDeleted(),
                sanPham.getCreatedAt().toInstant(),
                sanPham.getCreatedBy(),
                sanPham.getUpdatedAt().toInstant(),
                sanPham.getUpdatedBy(),
                chiTietSanPhamRepository.countByIdSanPhamIdAndDeletedFalse(sanPham.getId(), false),
                chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(sanPham.getId(), false)
                        .stream()
                        .map(ChiTietSanPham::getGiaBan)
                        .filter(price -> price != null)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO),
                chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(sanPham.getId(), false)
                        .stream()
                        .map(ChiTietSanPham::getGiaBan)
                        .filter(price -> price != null)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO)
        );
    }

    @Override
    public Optional<SanPham> getSanPhamById(Integer id) {
        return Optional.ofNullable(sanPhamRepository.findByIdAndDeletedFalse(id));
    }

    @Override
    @Transactional
    public SanPham createSanPham(SanPhamRequest requestDto) {
        SanPham sanPham = SanPham.builder()
                .ma(requestDto.ma())
                .tenSanPham(requestDto.tenSanPham())
                .idChiSoKhangBuiVaNuoc(requestDto.idChiSoKhangBuiVaNuoc() != null ? ChiSoKhangBuiVaNuoc.builder().id(requestDto.idChiSoKhangBuiVaNuoc()).build() : null)
                .idCongNgheMang(CongNgheMang.builder().id(requestDto.idCongNgheMang()).build())
                .idCpu(Cpu.builder().id(requestDto.idCpu()).build())
                .idCumCamera(CumCamera.builder().id(requestDto.idCumCamera()).build())
                .idGpu(Gpu.builder().id(requestDto.idGpu()).build())
                .idHeDieuHanh(HeDieuHanh.builder().id(requestDto.idHeDieuHanh()).build())
                .idHoTroBoNhoNgoai(requestDto.idHoTroBoNhoNgoai() != null ? HoTroBoNhoNgoai.builder().id(requestDto.idHoTroBoNhoNgoai()).build() : null)
                .idNhaSanXuat(NhaSanXuat.builder().id(requestDto.idNhaSanXuat()).build())
                .idPin(Pin.builder().id(requestDto.idPin()).build())
                .idSim(Sim.builder().id(requestDto.idSim()).build())
                .idThietKe(ThietKe.builder().id(requestDto.idThietKe()).build())
                .hoTroCongNgheSac(requestDto.hoTroCongNgheSacId() != null ? HoTroCongNgheSac.builder().id(requestDto.hoTroCongNgheSacId()).build() : null)
                .congNgheManHinh(requestDto.congNgheManHinhId() != null ? CongNgheManHinh.builder().id(requestDto.congNgheManHinhId()).build() : null)
                .createdAt(Date.from(Instant.now()))
                .deleted(false)
                .build();
        return sanPhamRepository.save(sanPham);
    }

    @Override
    @Transactional
    public SanPham updateSanPham(Integer id, SanPhamRequest requestDto) {
        SanPham sanPham = sanPhamRepository.findByIdAndDeletedFalse(id);
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã bị xóa");
        }
        sanPham.setMa(requestDto.ma());
        sanPham.setTenSanPham(requestDto.tenSanPham());
        sanPham.setIdChiSoKhangBuiVaNuoc(requestDto.idChiSoKhangBuiVaNuoc() != null ? ChiSoKhangBuiVaNuoc.builder().id(requestDto.idChiSoKhangBuiVaNuoc()).build() : null);
        sanPham.setIdCongNgheMang(CongNgheMang.builder().id(requestDto.idCongNgheMang()).build());
        sanPham.setIdCpu(Cpu.builder().id(requestDto.idCpu()).build());
        sanPham.setIdCumCamera(CumCamera.builder().id(requestDto.idCumCamera()).build());
        sanPham.setIdGpu(Gpu.builder().id(requestDto.idGpu()).build());
        sanPham.setIdHeDieuHanh(HeDieuHanh.builder().id(requestDto.idHeDieuHanh()).build());
        sanPham.setIdHoTroBoNhoNgoai(requestDto.idHoTroBoNhoNgoai() != null ? HoTroBoNhoNgoai.builder().id(requestDto.idHoTroBoNhoNgoai()).build() : null);
        sanPham.setIdNhaSanXuat(NhaSanXuat.builder().id(requestDto.idNhaSanXuat()).build());
        sanPham.setIdPin(Pin.builder().id(requestDto.idPin()).build());
        sanPham.setIdSim(Sim.builder().id(requestDto.idSim()).build());
        sanPham.setIdThietKe(ThietKe.builder().id(requestDto.idThietKe()).build());
        sanPham.setHoTroCongNgheSac(requestDto.hoTroCongNgheSacId() != null ? HoTroCongNgheSac.builder().id(requestDto.hoTroCongNgheSacId()).build() : null);
        sanPham.setCongNgheManHinh(requestDto.congNgheManHinhId() != null ? CongNgheManHinh.builder().id(requestDto.congNgheManHinhId()).build() : null);
        sanPham.setUpdatedAt(Date.from(Instant.now()));
        return sanPhamRepository.save(sanPham);
    }

}