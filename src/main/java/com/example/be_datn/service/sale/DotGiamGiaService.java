package com.example.be_datn.service.sale;

import com.example.be_datn.dto.sale.request.DotGiamGiaDTO;
import com.example.be_datn.dto.sale.respone.ViewCTSPDTO;
import com.example.be_datn.dto.sale.respone.ViewSanPhamDTO;
import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.discount.DotGiamGia;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.entity.product.NhaSanXuat;
import com.example.be_datn.entity.product.SanPham;
import com.example.be_datn.repository.sale.DotGiamGiaRepository;
import com.example.be_datn.repository.sale.Product.HDHForDGGRepo;
import com.example.be_datn.repository.sale.Product.NSXForDGGRepo;
import com.example.be_datn.repository.sale.RepoDongSanPhamDGG;
import com.example.be_datn.repository.sale.saleDetail.CTSPForCTDGG;
import com.example.be_datn.repository.sale.saleDetail.ChiTietDotGiamGiaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DotGiamGiaService {
    private DotGiamGiaRepository repository;
    private ChiTietDotGiamGiaRepository repo2;
    private RepoDongSanPhamDGG sanPhamRepository;
    private CTSPForCTDGG chiTietSanPhamRepository;

    private HDHForDGGRepo hdhForDGGRepo;

    private NSXForDGGRepo nsxForDGGRepo;

    public DotGiamGiaService(DotGiamGiaRepository repository, ChiTietDotGiamGiaRepository repo2, RepoDongSanPhamDGG sanPhamRepository, CTSPForCTDGG chiTietSanPhamRepository, HDHForDGGRepo hdhForDGGRepo, NSXForDGGRepo nsxForDGGRepo) {
        this.repository = repository;
        this.repo2 = repo2;
        this.sanPhamRepository = sanPhamRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.hdhForDGGRepo = hdhForDGGRepo;
        this.nsxForDGGRepo = nsxForDGGRepo;
    }

    public Page<DotGiamGia> HienThi(Pageable pageable) {
        return repository.hienThi(pageable);
    }

    public List<DotGiamGia> forExcel() {
        return repository.ForExcel();
    }

    public List<ChiTietDotGiamGia> ForExcelCTDGG() {
        return repo2.xuatExcel();
    }

    public Page<DotGiamGia> hienThiFinish(Pageable pageable) {
        return repository.hienThiFinish(pageable);
    }

    public List<ViewSanPhamDTO> getDSP(String timKiem, List<Integer> idHeDieuHanh, List<Integer> idNhaSanXuat) {
        return repository.getAllSanPham(timKiem, idHeDieuHanh, idNhaSanXuat);
    }

    public BigDecimal maxGiaTriGiamGia(){
        return repository.maxGiaTriGiamGia();
    }

    public BigDecimal maxSoTienGiamToiDa(){
        return repository.maxSoTienGiamToiDa();
    }

    public List<HeDieuHanh> getAllHeDieuHanh() {
        try {
            List<HeDieuHanh> result = hdhForDGGRepo.findAllByDeletedFalse();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch HeDieuHanh list", e);
        }
    }

    public List<NhaSanXuat> getAllNhaSanXuat() {
        return nsxForDGGRepo.findAllByDeletedFalse();
    }

    public List<ViewCTSPDTO> getAllCTSP(List<Integer> ids, List<Integer> idBoNhoTrongs, List<Integer> idMauSacs, Integer excludeDotGiamGiaId) {
        return repository.getAllCTSP(ids, idBoNhoTrongs, idMauSacs, excludeDotGiamGiaId);
    }
    public Boolean existByMa(String ma) {
        return repository.existsByMaAndDeletedTrue(ma);
    }


    private BigDecimal calculateGiaSauKhiGiam(BigDecimal giaBanDau, BigDecimal giaTriGiamGia, BigDecimal soTienGiamToiDa) {
        BigDecimal newGiaSauKhiGiam;
        if (giaTriGiamGia.compareTo(BigDecimal.ZERO) == 0) {
            newGiaSauKhiGiam = giaBanDau.subtract(soTienGiamToiDa);
        } else {
            BigDecimal giamTheoPhanTram = giaBanDau.multiply(giaTriGiamGia)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (giamTheoPhanTram.compareTo(soTienGiamToiDa) > 0) {
                newGiaSauKhiGiam = giaBanDau.subtract(soTienGiamToiDa);
            } else {
                newGiaSauKhiGiam = giaBanDau.subtract(giamTheoPhanTram);
            }
        }
        return newGiaSauKhiGiam.max(BigDecimal.ZERO);
    }


    @Transactional
    public void addDotGiamGia(DotGiamGiaDTO dotGiamGiaDTO, List<Integer> idSanPham, List<ViewCTSPDTO> dsCTSP) {
        try {
            Date ngayBatDau = new Date(dotGiamGiaDTO.getNgayBatDau().getTime());
            Date ngayKetThuc = new Date(dotGiamGiaDTO.getNgayKetThuc().getTime());
            DotGiamGia dotGiamGia = new DotGiamGia();
            dotGiamGia.setTenDotGiamGia(dotGiamGiaDTO.getTenDotGiamGia());
            dotGiamGia.setLoaiGiamGiaApDung(dotGiamGiaDTO.getLoaiGiamGiaApDung());
            dotGiamGia.setGiaTriGiamGia(dotGiamGiaDTO.getGiaTriGiamGia());
            dotGiamGia.setSoTienGiamToiDa(dotGiamGiaDTO.getSoTienGiamToiDa());
            dotGiamGia.setNgayBatDau(ngayBatDau);
            dotGiamGia.setNgayKetThuc(ngayKetThuc);
            dotGiamGia.setDeleted(false);
            dotGiamGia.setTrangThai(ngayBatDau.after(Date.valueOf(LocalDate.now())));
            repository.save(dotGiamGia);

            List<SanPham> dsSanPham = sanPhamRepository.findAllById(idSanPham);
            Map<Integer, ViewCTSPDTO> selectedCTSPMap = dsCTSP.stream()
                    .filter(ctsp -> ctsp.getSelected() != null && ctsp.getSelected())
                    .collect(Collectors.toMap(ctsp -> ctsp.getCtsp().getId(), ctsp -> ctsp, (e, r) -> e));
            List<ChiTietSanPham> dsChiTietSanPham = chiTietSanPhamRepository.findAllByIdSanPhamIn(idSanPham);

            Set<String> addedCTSP = new HashSet<>();
            Date today = Date.valueOf(LocalDate.now());

            for (ViewCTSPDTO ctspDTO : dsCTSP) {
                if (ctspDTO.getSelected() == null || !ctspDTO.getSelected()) continue;
                Integer idCTSP = ctspDTO.getCtsp().getId();
                ChiTietSanPham selectedChiTietSanPham = dsChiTietSanPham.stream()
                        .filter(ctsp -> ctsp.getId().equals(idCTSP))
                        .findFirst().orElse(null);
                if (selectedChiTietSanPham == null) continue;

                List<ChiTietSanPham> matchingChiTietSanPhams = dsChiTietSanPham.stream()
                        .filter(ctsp -> ctsp.getIdSanPham().getId().equals(selectedChiTietSanPham.getIdSanPham().getId()) &&
                                ctsp.getIdMauSac().getId().equals(selectedChiTietSanPham.getIdMauSac().getId()) &&
                                ctsp.getIdBoNhoTrong().getId().equals(selectedChiTietSanPham.getIdBoNhoTrong().getId()) &&
                                !ctsp.getDeleted())
                        .collect(Collectors.toList());

                BigDecimal giaBanDau = selectedChiTietSanPham.getGiaBan();
                BigDecimal giaSauKhiGiamMoi = calculateGiaSauKhiGiam(giaBanDau, dotGiamGia.getGiaTriGiamGia(), dotGiamGia.getSoTienGiamToiDa());

                for (ChiTietSanPham chiTietSanPham : matchingChiTietSanPhams) {
                    Integer idCTSPInGroup = chiTietSanPham.getId();
                    String key = idCTSPInGroup + "_" + giaBanDau;
                    if (addedCTSP.contains(key)) continue;

                    List<ChiTietDotGiamGia> activeCtggList = repo2.findActiveChiTietDotGiamGiaByCtspId(idCTSPInGroup, today);
                    BigDecimal finalGiaSauKhiGiam = giaSauKhiGiamMoi;

                    if (!activeCtggList.isEmpty()) {
                        boolean isOverlappingAndActive = false;
                        for (ChiTietDotGiamGia existingCtgg : activeCtggList) {
                            DotGiamGia existingDot = existingCtgg.getIdDotGiamGia();
                            if (isOverlapping(dotGiamGia.getNgayBatDau(), dotGiamGia.getNgayKetThuc(),
                                    existingDot.getNgayBatDau(), existingDot.getNgayKetThuc()) &&
                                    today.compareTo(dotGiamGia.getNgayBatDau()) >= 0 &&
                                    today.compareTo(existingDot.getNgayBatDau()) >= 0) {
                                isOverlappingAndActive = true;
                                break;
                            }
                        }

                        if (isOverlappingAndActive) {
                            List<DotGiamGia> overlappingDots = new ArrayList<>();
                            overlappingDots.add(dotGiamGia);
                            for (ChiTietDotGiamGia ctgg : activeCtggList) {
                                overlappingDots.add(ctgg.getIdDotGiamGia());
                            }
                            BigDecimal avgGiaTriGiamGia = overlappingDots.stream()
                                    .map(DotGiamGia::getGiaTriGiamGia)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .divide(BigDecimal.valueOf(overlappingDots.size()), 2, RoundingMode.HALF_UP);
                            BigDecimal avgSoTienGiamToiDa = overlappingDots.stream()
                                    .map(DotGiamGia::getSoTienGiamToiDa)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .divide(BigDecimal.valueOf(overlappingDots.size()), 2, RoundingMode.HALF_UP);
                            finalGiaSauKhiGiam = calculateGiaSauKhiGiam(giaBanDau, avgGiaTriGiamGia, avgSoTienGiamToiDa);

                            for (ChiTietDotGiamGia ctgg : activeCtggList) {
                                ctgg.setGiaSauKhiGiam(finalGiaSauKhiGiam);
                                repo2.save(ctgg);
                            }
                        }
                    }

                    ChiTietDotGiamGia chiTiet = new ChiTietDotGiamGia();
                    chiTiet.setIdDotGiamGia(dotGiamGia);
                    chiTiet.setIdChiTietSanPham(chiTietSanPham);
                    chiTiet.setGiaBanDau(giaBanDau);
                    chiTiet.setGiaSauKhiGiam(finalGiaSauKhiGiam);
                    chiTiet.setDeleted(false);
                    repo2.save(chiTiet);
                    addedCTSP.add(key);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm đợt giảm giá: " + e.getMessage());
            throw e;
        }
    }

    private boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && start2.before(end1);
    }

    @Modifying
    @Transactional
    public void deleteDotGiamGiaById(Integer id) {
        try {
            // 1. Đánh dấu DotGiamGia là deleted
            repository.updateDotGiamGiaDeleted(id);

            // 2. Đánh dấu tất cả ChiTietDotGiamGia liên quan là deleted
            repo2.updateChiTietDotGiamGiaDeleted(id);

            // 3. Lấy các ChiTietDotGiamGia vừa bị xóa
            List<ChiTietDotGiamGia> deletedRecords = repo2.findByIdDotGiamGiaIdAndDeleted(id, true);

            // 4. Xử lý từng ChiTietSanPham bị ảnh hưởng
            for (ChiTietDotGiamGia deletedRecord : deletedRecords) {
                ChiTietSanPham chiTietSanPham = deletedRecord.getIdChiTietSanPham();
                DotGiamGia deletedDotGiamGia = deletedRecord.getIdDotGiamGia();
                BigDecimal giaBanDau = deletedRecord.getGiaBanDau();

                // 5. Tìm các ChiTietDotGiamGia còn hiệu lực (chưa bị xóa)
                List<ChiTietDotGiamGia> activeRecords = repo2.findByIdChiTietSanPhamAndDeleted(chiTietSanPham, false);

                if (!activeRecords.isEmpty()) {
                    // 6. Lấy DotGiamGia còn hiệu lực đầu tiên (hoặc logic khác nếu có nhiều)
                    ChiTietDotGiamGia activeRecord = activeRecords.get(0);
                    DotGiamGia activeDotGiamGia = activeRecord.getIdDotGiamGia();

                    // 7. Kiểm tra xem có trùng thời gian không
                    if (isOverlapping3(
                            activeDotGiamGia.getNgayBatDau(),
                            activeDotGiamGia.getNgayKetThuc(),
                            deletedDotGiamGia.getNgayBatDau(),
                            deletedDotGiamGia.getNgayKetThuc())) {
                        // 8. Tính lại giaSauKhiGiam theo DotGiamGia còn lại
                        BigDecimal newGiaSauKhiGiam;
                        if ("Phần trăm".equals(activeDotGiamGia.getLoaiGiamGiaApDung())) {
                            BigDecimal giamTheoPhanTram = giaBanDau.multiply(activeDotGiamGia.getGiaTriGiamGia())
                                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                            newGiaSauKhiGiam = giaBanDau.subtract(giamTheoPhanTram.min(activeDotGiamGia.getSoTienGiamToiDa()));
                        } else { // Tiền mặt
                            newGiaSauKhiGiam = giaBanDau.subtract(activeDotGiamGia.getSoTienGiamToiDa());
                        }
                        newGiaSauKhiGiam = newGiaSauKhiGiam.max(BigDecimal.ZERO);

                        // 9. Cập nhật bản ghi còn hiệu lực
                        activeRecord.setGiaSauKhiGiam(newGiaSauKhiGiam);
                        repo2.save(activeRecord);
//                        System.out.println("Cập nhật giá sau khi giảm cho idChiTietSanPham: " + chiTietSanPham.getId() +
//                                " theo DotGiamGia còn lại: " + activeDotGiamGia.getId());
                    }
                } else {
//                    System.out.println("Không còn DotGiamGia hiệu lực cho idChiTietSanPham: " + chiTietSanPham.getId());
                }
            }
//            System.out.println("Xóa và cập nhật (nếu cần) thành công cho DotGiamGia với ID: " + id);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa DotGiamGia: " + e.getMessage());
            throw e;
        }
    }

    // Hàm kiểm tra thời gian trùng lặp
    private boolean isOverlapping3(Date start1, Date end1, Date start2, Date end2) {
        return !start1.after(end2) && !start2.after(end1);  // start1 <= end2 && start2 <= end1
    }

    public List<SanPham> getThatDongSanPham(Integer id) {
        return repository.getThatDongSanPham(id);
    }

    public List<ChiTietSanPham> getChiTietSanPhamByDotGiamGia(Integer id) {
        List<ChiTietSanPham> ctspList = repo2.getChiTietSanPhamByDotGiamGia(id);
//        System.out.println("getChiTietSanPhamByDotGiamGia trả về: " + (ctspList != null ? ctspList.size() : 0) + " bản ghi");
        return ctspList;
    }

    public Map<String, Object> getDataForUpdate(Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Fetch the DotGiamGia object
            DotGiamGia dotGiamGia = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Đợt giảm giá không tồn tại"));

            // Fetch the list of SanPham (dspList)
            List<SanPham> dspList = getThatDongSanPham(id);
            List<Integer> dspIds = dspList.stream()
                    .map(SanPham::getId)
                    .collect(Collectors.toList());

            // Fetch the list of ChiTietSanPham with overlap count, excluding the current DotGiamGia
            List<ViewCTSPDTO> ctspList = getAllCTSP(dspIds, null, null, id);

            // Fetch ctspIds for compatibility with frontend
            List<Integer> ctspIds = getChiTietSanPhamByDotGiamGia(id).stream()
                    .map(ChiTietSanPham::getId)
                    .collect(Collectors.toList());

            // Add data to response
            response.put("dotGiamGia", dotGiamGia);
            response.put("dspList", dspList != null ? dspList : Collections.emptyList());
            response.put("ctspList", ctspList != null ? ctspList : Collections.emptyList());
            response.put("ctspIds", ctspIds != null ? ctspIds : Collections.emptyList());

            return response;
        } catch (Exception e) {
            System.err.println("Lỗi trong getDataForUpdate: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy dữ liệu cập nhật: " + e.getMessage());
        }
    }
    public Optional<DotGiamGia> findOne(Integer id) {
        return repository.findById(id);
    }


    @Transactional
    public void updateDotGiamGia(Integer dotGiamGiaId, DotGiamGiaDTO dotGiamGiaDTO, List<Integer> idSanPham, List<ViewCTSPDTO> dsCTSP) {
        try {
            DotGiamGia dotGiamGia = repository.findById(dotGiamGiaId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá với id: " + dotGiamGiaId));
            Date today = Date.valueOf(LocalDate.now());

            LocalDateTime ngayBatDauFormatted = dotGiamGiaDTO.getNgayBatDau().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            Date ngayBatDau = new Date(Timestamp.valueOf(ngayBatDauFormatted).getTime());
            LocalDateTime ngayKetThucFormatted = dotGiamGiaDTO.getNgayKetThuc().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            Date ngayKetThuc = new Date(Timestamp.valueOf(ngayKetThucFormatted).getTime());

            dotGiamGia.setNgayBatDau(ngayBatDau);
            dotGiamGia.setNgayKetThuc(ngayKetThuc);
            dotGiamGia.setTenDotGiamGia(dotGiamGiaDTO.getTenDotGiamGia());
            dotGiamGia.setLoaiGiamGiaApDung(dotGiamGiaDTO.getLoaiGiamGiaApDung());
            dotGiamGia.setGiaTriGiamGia(dotGiamGiaDTO.getGiaTriGiamGia());
            dotGiamGia.setSoTienGiamToiDa(dotGiamGiaDTO.getSoTienGiamToiDa());
            dotGiamGia.setTrangThai(ngayBatDau.after(today));
            repository.save(dotGiamGia);

            List<SanPham> dsSanPham = sanPhamRepository.findAllById(idSanPham);
            Map<Integer, ViewCTSPDTO> selectedCTSPMap = dsCTSP.stream()
                    .filter(ctsp -> ctsp.getSelected() != null && ctsp.getSelected())
                    .collect(Collectors.toMap(ctsp -> ctsp.getCtsp().getId(), ctsp -> ctsp, (e, r) -> e));
            List<ChiTietSanPham> dsChiTietSanPham = chiTietSanPhamRepository.findAllByIdSanPhamIn(idSanPham);

            Set<String> addedCTSP = new HashSet<>();
            Set<Integer> selectedCTSPIds = selectedCTSPMap.keySet();

            // Bước 1: Xóa bản ghi không được chọn (theo nhóm)
            List<ChiTietDotGiamGia> existingChiTietList = repo2.findByIdDotGiamGia(dotGiamGia);
            Map<String, List<ChiTietDotGiamGia>> groupedByAttributes = existingChiTietList.stream()
                    .collect(Collectors.groupingBy(ctdg ->
                            ctdg.getIdChiTietSanPham().getIdSanPham().getId() + "_" +
                                    ctdg.getIdChiTietSanPham().getIdMauSac().getId() + "_" +
                                    ctdg.getIdChiTietSanPham().getIdBoNhoTrong().getId()));

            for (List<ChiTietDotGiamGia> group : groupedByAttributes.values()) {
                boolean groupSelected = group.stream()
                        .anyMatch(ctdg -> selectedCTSPIds.contains(ctdg.getIdChiTietSanPham().getId()));
                if (!groupSelected) {
                    for (ChiTietDotGiamGia chiTiet : group) {
                        chiTiet.setDeleted(true);
                        repo2.save(chiTiet);
                    }
                }
            }

            // Bước 2: Thêm hoặc cập nhật bản ghi
            for (ViewCTSPDTO ctspDTO : dsCTSP) {
                if (ctspDTO.getSelected() == null || !ctspDTO.getSelected()) continue;
                Integer idCTSP = ctspDTO.getCtsp().getId();
                ChiTietSanPham selectedChiTietSanPham = dsChiTietSanPham.stream()
                        .filter(ctsp -> ctsp.getId().equals(idCTSP))
                        .findFirst()
                        .orElse(null);
                if (selectedChiTietSanPham == null) continue;

                List<ChiTietSanPham> matchingChiTietSanPhams = dsChiTietSanPham.stream()
                        .filter(ctsp -> ctsp.getIdSanPham().getId().equals(selectedChiTietSanPham.getIdSanPham().getId()) &&
                                ctsp.getIdMauSac().getId().equals(selectedChiTietSanPham.getIdMauSac().getId()) &&
                                ctsp.getIdBoNhoTrong().getId().equals(selectedChiTietSanPham.getIdBoNhoTrong().getId()) &&
                                !ctsp.getDeleted())
                        .collect(Collectors.toList());

                BigDecimal giaBanDau = selectedChiTietSanPham.getGiaBan();
                BigDecimal giaSauKhiGiamMoi = calculateGiaSauKhiGiam(giaBanDau, dotGiamGia.getGiaTriGiamGia(), dotGiamGia.getSoTienGiamToiDa());

                for (ChiTietSanPham chiTietSanPham : matchingChiTietSanPhams) {
                    Integer idCTSPInGroup = chiTietSanPham.getId();
                    String key = idCTSPInGroup + "_" + giaBanDau;
                    if (addedCTSP.contains(key)) continue;

                    // Tìm bản ghi hiện có (bao gồm cả deleted = true/false)
                    List<ChiTietDotGiamGia> existingRecords = existingChiTietList.stream()
                            .filter(ctdg -> ctdg.getIdChiTietSanPham().getId().equals(idCTSPInGroup))
                            .collect(Collectors.toList());

                    if (!existingRecords.isEmpty()) {
                        for (ChiTietDotGiamGia existing : existingRecords) {
                            existing.setGiaSauKhiGiam(giaSauKhiGiamMoi);
                            if (existing.getDeleted()) { // Khôi phục nếu đã bị xóa
                                existing.setDeleted(false);
                            }
                            repo2.save(existing);
                        }
                    } else {
                        ChiTietDotGiamGia chiTiet = new ChiTietDotGiamGia();
                        chiTiet.setIdDotGiamGia(dotGiamGia);
                        chiTiet.setIdChiTietSanPham(chiTietSanPham);
                        chiTiet.setGiaBanDau(giaBanDau);
                        chiTiet.setGiaSauKhiGiam(giaSauKhiGiamMoi);
                        chiTiet.setDeleted(false);
                        repo2.save(chiTiet);
                    }
                    addedCTSP.add(key);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật đợt giảm giá: " + e.getMessage());
            throw e;
        }
    }


    public Page<DotGiamGia> timKiem(Pageable pageable, String maDGG, String tenDGG, String loaiGiamGiaApDung, BigDecimal giaTriGiamGia, BigDecimal soTienGiamToiDa, Date ngayBatDau, Date ngayKetThuc, Boolean trangThai, Boolean deleted) {
        return repository.timKiem(pageable, maDGG, tenDGG, loaiGiamGiaApDung, giaTriGiamGia, soTienGiamToiDa, ngayBatDau, ngayKetThuc, trangThai, deleted);
    }

    @PostConstruct
    public void initUpdate() {
        updateStatusAutomatically();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateStatusAutomatically() {
        Date today = Date.valueOf(LocalDate.now());

        // Cập nhật trạng thái và xóa các DotGiamGia hết hạn
        int deletedCount = repository.updateDeletedIfEndDatePassed(today);
        if (deletedCount > 0) {
//            System.out.println("Đã đánh dấu " + deletedCount + " DotGiamGia là deleted.");
            int deletedChiTietCount = repo2.updateDeletedChiTietDotGiamGia();
//            System.out.println("Đã đánh dấu " + deletedChiTietCount + " ChiTietDotGiamGia là deleted.");
        }
        repository.updateStatusIfStartDatePassed(today);

        // Cập nhật giá cho tất cả ChiTietSanPham
        List<ChiTietSanPham> allCtsp = chiTietSanPhamRepository.findAll();
        for (ChiTietSanPham ctsp : allCtsp) {
            List<ChiTietDotGiamGia> activeCtggList = repo2.findActiveChiTietDotGiamGiaByCtspId(ctsp.getId(), today);

            if (activeCtggList.isEmpty()) {
                continue;
            }

            BigDecimal giaBanDau = activeCtggList.get(0).getGiaBanDau();
            BigDecimal newGiaSauKhiGiam;

            if (activeCtggList.size() >= 2) { // Có trùng lặp
                BigDecimal avgGiaTriGiamGia = activeCtggList.stream()
                        .map(ctgg -> ctgg.getIdDotGiamGia().getGiaTriGiamGia())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(activeCtggList.size()), 2, RoundingMode.HALF_UP);
                BigDecimal avgSoTienGiamToiDa = activeCtggList.stream()
                        .map(ctgg -> ctgg.getIdDotGiamGia().getSoTienGiamToiDa())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(activeCtggList.size()), 2, RoundingMode.HALF_UP);

                newGiaSauKhiGiam = calculateGiaSauKhiGiam(giaBanDau, avgGiaTriGiamGia, avgSoTienGiamToiDa);
//                System.out.println("Cập nhật giá trung bình cho ChiTietSanPham " + ctsp.getId() + ": " + newGiaSauKhiGiam);
            } else { // Chỉ có 1 đợt
                ChiTietDotGiamGia activeCtgg = activeCtggList.get(0);
                DotGiamGia activeDot = activeCtgg.getIdDotGiamGia();
                newGiaSauKhiGiam = calculateGiaSauKhiGiam(giaBanDau, activeDot.getGiaTriGiamGia(), activeDot.getSoTienGiamToiDa());
//                System.out.println("Cập nhật giá cho ChiTietSanPham " + ctsp.getId() + " theo DotGiamGia " + activeDot.getId() + ": " + newGiaSauKhiGiam);
            }

            for (ChiTietDotGiamGia ctgg : activeCtggList) {
                ctgg.setGiaSauKhiGiam(newGiaSauKhiGiam);
                repo2.save(ctgg);
            }
        }
    }
}