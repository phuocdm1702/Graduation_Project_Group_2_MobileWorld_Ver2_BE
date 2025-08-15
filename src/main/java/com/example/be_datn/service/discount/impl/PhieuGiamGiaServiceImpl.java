package com.example.be_datn.service.discount.impl;

import com.example.be_datn.dto.discount.request.PhieuGiamGiaCaNhanRequest;
import com.example.be_datn.dto.discount.request.PhieuGiamGiaRequest;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaCaNhanRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaRepository;
import com.example.be_datn.service.discount.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhieuGiamGiaServiceImpl implements PhieuGiamGiaService {

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private final PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;
    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    public PhieuGiamGiaServiceImpl(PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository) {
        this.phieuGiamGiaCaNhanRepository = phieuGiamGiaCaNhanRepository;
    }

    @Override
    public Page<PhieuGiamGia> getPGG(Pageable pageable) {
        Date now = new Date();
        return phieuGiamGiaRepository.findByNgayKetThucGreaterThanEqual(now, pageable);
    }

    @Scheduled(fixedRate = 200000)
    public void updateHanPGG() {
        List<PhieuGiamGia> listPgg = phieuGiamGiaRepository.findAll();
        Date now = new Date();

        for (PhieuGiamGia pgg : listPgg) {
            if (pgg.getNgayBatDau() != null && pgg.getNgayKetThuc() != null) {
                boolean isActive = (pgg.getNgayBatDau().before(now) || pgg.getNgayBatDau().equals(now)) &&
                        (pgg.getNgayKetThuc().after(now) || pgg.getNgayKetThuc().equals(now));
                boolean currentStatus = pgg.getTrangThai() != null && pgg.getTrangThai();

                if (isActive && !currentStatus) {
                    pgg.setTrangThai(true); // Cập nhật thành Hoạt động
                    phieuGiamGiaRepository.save(pgg);
                } else if (!isActive && currentStatus) {
                    pgg.setTrangThai(false); // Cập nhật thành Không hoạt động
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        }
    }

    @Override
    public Page<PhieuGiamGia> searchData(String keyword, Pageable pageable) {
        Date now = new Date();
        if (keyword == null || keyword.trim().isEmpty()) {
            return (Page<PhieuGiamGia>) phieuGiamGiaRepository.findAll();
        }
        return phieuGiamGiaRepository.search(keyword, now, pageable);
    }

    @Override
    public Page<PhieuGiamGia> filterByLoaiPhieu(String loaiPhieu, Pageable pageable) {
        Date now = new Date();
        String loai = loaiPhieu != null && loaiPhieu.trim().isEmpty() ? null : loaiPhieu;
        System.out.println("Filter by loaiPhieu: " + loai);
        Page<PhieuGiamGia> result = phieuGiamGiaRepository.filterByLoaiPhieu(loai, now, pageable);
        logResult(result);
        return result;
    }

    @Override
    public Page<PhieuGiamGia> filterByTrangThai(String trangThai, Pageable pageable) {
        Date now = new Date();
        Page<PhieuGiamGia> result;

        // Nếu trạng thái rỗng, trả về tất cả phiếu giảm giá
        if (trangThai == null || trangThai.trim().isEmpty()) {
            result = phieuGiamGiaRepository.findAll(pageable);
        } else {
            switch (trangThai) {
                case "Hoạt động":
                    // Phiếu đang hoạt động: trangThai = true và ngày hiện tại nằm giữa ngày bắt đầu và kết thúc
                    result = phieuGiamGiaRepository.filterByTrangThai(true, now, pageable);
                    break;
                case "Không hoạt động":
                    // Phiếu không hoạt động: trangThai = false hoặc ngày kết thúc đã qua
                    result = phieuGiamGiaRepository.filterByTrangThai(false, now, pageable);
                    break;
                case "Chưa diễn ra":
                    // Phiếu chưa diễn ra: ngày bắt đầu lớn hơn ngày hiện tại
                    result = phieuGiamGiaRepository.filterByChuaDienRa(now, pageable);
                    break;
                default:
                    throw new IllegalArgumentException("Trạng thái không hợp lệ: " + trangThai);
            }
        }

        logResult(result);
        return result;
    }

    @Override
    public Page<PhieuGiamGia> filterByDateRange(Date ngayBatDau, Date ngayKetThuc, Pageable pageable) {
        if (ngayBatDau != null && ngayKetThuc != null && ngayBatDau.after(ngayKetThuc)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }
        Date now = new Date();
        System.out.println("Filter by date range - ngayBatDau: " + ngayBatDau + ", ngayKetThuc: " + ngayKetThuc);
        Page<PhieuGiamGia> result = phieuGiamGiaRepository.filterByDateRange(ngayBatDau, ngayKetThuc, now, pageable);
        logResult(result);
        return result;
    }

    @Override
    public Page<PhieuGiamGia> filterByMinOrder(Double minOrder, Pageable pageable) {
        Date now = new Date();
        if (minOrder != null && minOrder < 0) {
            throw new IllegalArgumentException("Hóa đơn tối thiểu không thể nhỏ hơn 0");
        }
        System.out.println("Filter by minOrder: " + minOrder);
        Page<PhieuGiamGia> result = phieuGiamGiaRepository.filterByMinOrder(minOrder, now, pageable);
        logResult(result);
        return result;
    }

    @Override
    public Page<PhieuGiamGia> filterByValue(Double valueFilter, Pageable pageable) {
        Date now = new Date();
        if (valueFilter != null && valueFilter < 0) {
            throw new IllegalArgumentException("Giá trị phiếu không thể nhỏ hơn 0");
        }
        System.out.println("Filter by valueFilter: " + valueFilter);
        Page<PhieuGiamGia> result = phieuGiamGiaRepository.filterByValue(valueFilter, now, pageable);
        logResult(result);
        return result;
    }

    @Override
    public Page<PhieuGiamGia> filterPhieuGiamGia(String loaiPhieuGiamGia, String trangThai, Date ngayBatDau,
                                                 Date ngayKetThuc, Double minOrder, Double valueFilter, Pageable pageable) {
        String loaiPhieu = loaiPhieuGiamGia != null && loaiPhieuGiamGia.trim().isEmpty() ? null : loaiPhieuGiamGia;

        Boolean trangThaiBoolean = null;
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            if ("Hoạt động".equals(trangThai)) {
                trangThaiBoolean = true;
            } else if ("Không hoạt động".equals(trangThai)) {
                trangThaiBoolean = false;
            }
        }

        if (ngayBatDau != null && ngayKetThuc != null && ngayBatDau.after(ngayKetThuc)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }
        if (minOrder != null && minOrder < 0) {
            throw new IllegalArgumentException("Hóa đơn tối thiểu không thể nhỏ hơn 0");
        }
        if (valueFilter != null && valueFilter < 0) {
            throw new IllegalArgumentException("Giá trị phiếu không thể nhỏ hơn 0");
        }

        System.out.println("Filter params - loaiPhieu: " + loaiPhieu + ", trangThai: " + trangThaiBoolean +
                ", ngayBatDau: " + ngayBatDau + ", ngayKetThuc: " + ngayKetThuc +
                ", minOrder: " + minOrder + ", valueFilter: " + valueFilter);

        Date now = new Date();
        Page<PhieuGiamGia> result = phieuGiamGiaRepository.filterPhieuGiamGia(
                loaiPhieu, trangThaiBoolean, ngayBatDau, ngayKetThuc, minOrder, valueFilter, now, pageable
        );

        logResult(result);
        return result;
    }

    @Override
    public Optional<PhieuGiamGia> getById(Integer id) {
        return phieuGiamGiaRepository.findById(id);
    }

    @Override
    public PhieuGiamGia addPGG(PhieuGiamGia phieuGiamGia) {
        return phieuGiamGiaRepository.save(phieuGiamGia);
    }

    private PhieuGiamGiaRequest convertToDTO(PhieuGiamGia pgg) {
        PhieuGiamGiaRequest dto = new PhieuGiamGiaRequest();
        dto.setId(pgg.getId());
        dto.setMa(pgg.getMa());
        dto.setTenPhieuGiamGia(pgg.getTenPhieuGiamGia());
        dto.setLoaiPhieuGiamGia(pgg.getLoaiPhieuGiamGia());
        dto.setPhanTramGiamGia(pgg.getPhanTramGiamGia());
        dto.setSoTienGiamToiDa(pgg.getSoTienGiamToiDa());
        dto.setHoaDonToiThieu(pgg.getHoaDonToiThieu());
        dto.setSoLuongDung(pgg.getSoLuongDung());
        dto.setNgayBatDau(pgg.getNgayBatDau());
        dto.setNgayKetThuc(pgg.getNgayKetThuc());
        dto.setTrangThai(pgg.getTrangThai());
        return dto;
    }

    @Override
    public PhieuGiamGiaRequest updateTrangthai(Integer id, Boolean trangThai) {
        Optional<PhieuGiamGia> optionalPgg = phieuGiamGiaRepository.findById(id);
        if (!optionalPgg.isPresent()) {
            throw new RuntimeException("Phiếu giảm giá không tồn tại với ID: " + id);
        }

        PhieuGiamGia pgg = optionalPgg.get();
        pgg.setTrangThai(trangThai); // true = Hoạt động, false = Không hoạt động
        PhieuGiamGia updatePGG = phieuGiamGiaRepository.save(pgg);
        return convertToDTO(updatePGG);
    }

    @Override
    public PhieuGiamGiaRequest getDetailPGG(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ!");
        }

        Optional<PhieuGiamGia> detailPGG = phieuGiamGiaRepository.findById(id);
        if (!detailPGG.isPresent()) {
            throw new RuntimeException("Phiếu giảm giá không tồn tại!");
        }

        PhieuGiamGia pgg = detailPGG.get();

        List<PhieuGiamGiaCaNhan> pggCNList = phieuGiamGiaCaNhanRepository.findByIdPhieuGiamGia(pgg);
        List<KhachHang> allCustomers = khachHangRepository.findAll();

        PhieuGiamGiaRequest pggDTO = new PhieuGiamGiaRequest();
        pggDTO.setId(pgg.getId());
        pggDTO.setMa(pgg.getMa());
        pggDTO.setTenPhieuGiamGia(pgg.getTenPhieuGiamGia());
        pggDTO.setLoaiPhieuGiamGia(pgg.getLoaiPhieuGiamGia());
        pggDTO.setPhanTramGiamGia(pgg.getPhanTramGiamGia());
        pggDTO.setSoTienGiamToiDa(pgg.getSoTienGiamToiDa());
        pggDTO.setHoaDonToiThieu(pgg.getHoaDonToiThieu());
        pggDTO.setSoLuongDung(pgg.getSoLuongDung());
        pggDTO.setNgayBatDau(pgg.getNgayBatDau());
        pggDTO.setNgayKetThuc(pgg.getNgayKetThuc());
        pggDTO.setMoTa(pgg.getMoTa());
        pggDTO.setTrangThai(pgg.getTrangThai() ? true : false);
        pggDTO.setRiengTu(pgg.getRiengTu() ? 1 : 0);

        List<PhieuGiamGiaCaNhanRequest> selectedCustomers = pggCNList.stream()
                .map(pggCN -> {
                    KhachHang kh = pggCN.getIdKhachHang();
                    return new PhieuGiamGiaCaNhanRequest(kh.getId(), kh.getMa(), kh.getTen(), kh.getNgaySinh());
                })
                .collect(Collectors.toList());
        pggDTO.setSelectedCustomers(selectedCustomers);

        List<Integer> customerIds = pggCNList.stream()
                .map(pggCN -> pggCN.getIdKhachHang().getId())
                .collect(Collectors.toList());
        pggDTO.setCustomerIds(customerIds);

        List<PhieuGiamGiaCaNhanRequest> allCustomersDTO = allCustomers.stream()
                .map(kh -> new PhieuGiamGiaCaNhanRequest(kh.getId(), kh.getMa(), kh.getTen(), kh.getNgaySinh()))
                .collect(Collectors.toList());
        pggDTO.setAllCustomers(allCustomersDTO);

        return pggDTO;
    }

    @Override
    public PhieuGiamGia updatePGG(PhieuGiamGia editPGG) {
        return phieuGiamGiaRepository.save(editPGG);
    }

    @Override
    public List<PhieuGiamGia> getall() {
        return phieuGiamGiaRepository.findAll();
    }

    @Override
    public List<PhieuGiamGia> getallPGG() {
        return phieuGiamGiaRepository.findAll();
    }

    private void logResult(Page<PhieuGiamGia> result) {
        System.out.println("Filter result size: " + result.getContent().size());
        result.getContent().forEach(voucher ->
                System.out.println("Voucher: " + voucher.getMa() + ", TrangThai: " +
                        (voucher.getTrangThai() ? "Đang diễn ra" : "Không hoạt động") +
                        ", NgayBatDau: " + voucher.getNgayBatDau() + ", NgayKetThuc: " + voucher.getNgayKetThuc())
        );
    }
}
