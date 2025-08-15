package com.example.be_datn.controller.discount;

import com.example.be_datn.config.Email.EmailSend;
import com.example.be_datn.dto.discount.request.PhieuGiamGiaRequest;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.repository.discount.PhieuGiamGiaCaNhanRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaRepository;
import com.example.be_datn.service.account.KhachHangServices;
import com.example.be_datn.service.account.TaiKhoanService;
import com.example.be_datn.service.discount.PhieuGiamGiaCaNhanService;
import com.example.be_datn.service.discount.PhieuGiamGiaService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api/phieu-giam-gia")
@RestController
@CrossOrigin("http://localhost:5173")
public class PhieuGiamGiaControlller {

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private PhieuGiamGiaCaNhanService phieuGiamGiaCaNhanService;

    @Autowired
    private KhachHangServices khachHangServices;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private EmailSend emailSend;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @GetMapping("/data")
    public ResponseEntity<Page<PhieuGiamGia>> fetchData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PhieuGiamGia> listPGG = phieuGiamGiaService.getPGG(pageable);
        return ResponseEntity.ok(listPGG);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PhieuGiamGia>> searchData(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PhieuGiamGia> listSearch = phieuGiamGiaService.searchData(keyword, pageable);
        return ResponseEntity.ok(listSearch);
    }

    @GetMapping("/filter/loai-phieu")
    public ResponseEntity<?> filterByLoaiPhieu(
            @RequestParam(required = false) String loaiPhieu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PhieuGiamGia> result = phieuGiamGiaService.filterByLoaiPhieu(loaiPhieu, pageable);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter/trang-thai")
    public ResponseEntity<?> filterByTrangThai(
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PhieuGiamGia> result = phieuGiamGiaService.filterByTrangThai(trangThai, pageable);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter/date-range")
    public ResponseEntity<?> filterByDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PhieuGiamGia> result = phieuGiamGiaService.filterByDateRange(startDate, endDate, pageable);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter/min-order")
    public ResponseEntity<?> filterByMinOrder(
            @RequestParam(required = false) Double minOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PhieuGiamGia> result = phieuGiamGiaService.filterByMinOrder(minOrder, pageable);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter/value")
    public ResponseEntity<?> filterByValue(
            @RequestParam(required = false) Double valueFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PhieuGiamGia> result = phieuGiamGiaService.filterByValue(valueFilter, pageable);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPhieuGiamGia(
            @RequestParam(required = false) String loaiPhieuGiamGia,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) Double minOrder,
            @RequestParam(required = false) Double valueFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<PhieuGiamGia> result = phieuGiamGiaService.filterPhieuGiamGia(
                    loaiPhieuGiamGia, trangThai, startDate, endDate, minOrder, valueFilter, pageable);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhieuGiamGiaRequest> getDetail(@PathVariable Integer id) {
        try {
            PhieuGiamGiaRequest dto = phieuGiamGiaService.getDetailPGG(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update-phieu-giam-gia/{id}")
    public ResponseEntity<?> updatePGG(@PathVariable Integer id, @RequestBody PhieuGiamGiaRequest dtoPGG) {
        try {
            Optional<PhieuGiamGia> pggExist = phieuGiamGiaService.getById(id);
            if (!pggExist.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phiếu giảm giá không tồn tại");
            }

            // Validation
            if (dtoPGG.getMa() == null || dtoPGG.getMa().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã phiếu không được để trống");
            }
            if (dtoPGG.getTenPhieuGiamGia() == null || dtoPGG.getTenPhieuGiamGia().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên phiếu không được để trống");
            }
            if (dtoPGG.getLoaiPhieuGiamGia() == null || (!dtoPGG.getLoaiPhieuGiamGia().equals("Phần trăm") && !dtoPGG.getLoaiPhieuGiamGia().equals("Tiền mặt"))) {
                return ResponseEntity.badRequest().body("Loại phiếu không hợp lệ");
            }
            if ("Phần trăm".equals(dtoPGG.getLoaiPhieuGiamGia()) && (dtoPGG.getPhanTramGiamGia() == null || dtoPGG.getPhanTramGiamGia() <= 0)) {
                return ResponseEntity.badRequest().body("Phần trăm giảm giá phải lớn hơn 0");
            }
            if ("Tiền mặt".equals(dtoPGG.getLoaiPhieuGiamGia()) && (dtoPGG.getSoTienGiamToiDa() == null || dtoPGG.getSoTienGiamToiDa() <= 0)) {
                return ResponseEntity.badRequest().body("Số tiền giảm tối đa phải lớn hơn 0");
            }
            if (dtoPGG.getHoaDonToiThieu() == null || dtoPGG.getHoaDonToiThieu() < 0) {
                return ResponseEntity.badRequest().body("Hóa đơn tối thiểu không được âm");
            }
            if (dtoPGG.getSoLuongDung() == null || (dtoPGG.getSoLuongDung() != -1 && dtoPGG.getSoLuongDung() < 0)) {
                return ResponseEntity.badRequest().body("Số lượng không hợp lệ");
            }
            if (dtoPGG.getNgayBatDau() == null || dtoPGG.getNgayKetThuc() == null || dtoPGG.getNgayBatDau().after(dtoPGG.getNgayKetThuc())) {
                return ResponseEntity.badRequest().body("Ngày bắt đầu và kết thúc không hợp lệ");
            }
            if (Objects.equals(dtoPGG.getRiengTu(), 1) && (dtoPGG.getCustomerIds() == null || dtoPGG.getCustomerIds().isEmpty())) {
                return ResponseEntity.badRequest().body("Danh sách khách hàng không được trống khi phiếu riêng tư");
            }
            if (Objects.equals(dtoPGG.getRiengTu(), 1) && dtoPGG.getSoLuongDung() != -1 && dtoPGG.getSoLuongDung() < dtoPGG.getCustomerIds().size()) {
                return ResponseEntity.badRequest().body("Số lượng phải lớn hơn hoặc bằng số khách hàng được chọn");
            }

            // Lấy danh sách khách hàng hiện tại trước khi cập nhật
            List<PhieuGiamGiaCaNhan> existingPggcnList = phieuGiamGiaCaNhanService.findByPhieuGiamGiaId(id);
            List<Integer> oldCustomerIds = existingPggcnList.stream()
                    .map(pggcn -> pggcn.getIdKhachHang().getId())
                    .collect(Collectors.toList());

            // Cập nhật thông tin phiếu giảm giá
            PhieuGiamGia existingPgg = pggExist.get();
            existingPgg.setMa(dtoPGG.getMa());
            existingPgg.setTenPhieuGiamGia(dtoPGG.getTenPhieuGiamGia());
            existingPgg.setLoaiPhieuGiamGia(dtoPGG.getLoaiPhieuGiamGia());
            existingPgg.setPhanTramGiamGia(dtoPGG.getPhanTramGiamGia() != null ? dtoPGG.getPhanTramGiamGia() : 0);
            existingPgg.setSoTienGiamToiDa(dtoPGG.getSoTienGiamToiDa() != null ? dtoPGG.getSoTienGiamToiDa() : 0);
            existingPgg.setHoaDonToiThieu(dtoPGG.getHoaDonToiThieu());
            existingPgg.setSoLuongDung(dtoPGG.getSoLuongDung());
            existingPgg.setNgayBatDau(dtoPGG.getNgayBatDau());
            existingPgg.setNgayKetThuc(dtoPGG.getNgayKetThuc());
            existingPgg.setMoTa(dtoPGG.getMoTa());
            existingPgg.setRiengTu(Objects.equals(dtoPGG.getRiengTu(), 1));

            PhieuGiamGia updatedPgg = phieuGiamGiaService.updatePGG(existingPgg);
            if (updatedPgg == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật phiếu giảm giá");
            }

            // Xử lý khách hàng nếu phiếu là riêng tư
            if (Objects.equals(dtoPGG.getRiengTu(), 1)) {
                List<Integer> newCustomerIds = dtoPGG.getCustomerIds();
                List<Integer> restoredCustomerIds = dtoPGG.getRestoredCustomerIds() != null ? dtoPGG.getRestoredCustomerIds() : new ArrayList<>();

                // Xác định khách hàng mới, khách hàng bị xóa, và khách hàng được khôi phục
                List<Integer> addedCustomerIds = new ArrayList<>(newCustomerIds);
                addedCustomerIds.removeAll(oldCustomerIds); // Khách hàng mới được thêm

                List<Integer> removedCustomerIds = new ArrayList<>(oldCustomerIds);
                removedCustomerIds.removeAll(newCustomerIds); // Khách hàng bị xóa

                // Loại khách hàng được khôi phục ra khỏi danh sách khách hàng mới
                addedCustomerIds.removeAll(restoredCustomerIds);

                // Xóa tất cả khách hàng hiện tại
                phieuGiamGiaCaNhanService.deleteByPhieuGiamGiaId(id);

                // Thêm lại khách hàng mới
                for (Integer khachHangID : newCustomerIds) {
                    KhachHang kh = khachHangServices.findById(khachHangID);
                    if (kh != null) {
                        PhieuGiamGiaCaNhan pggcn = new PhieuGiamGiaCaNhan();
                        pggcn.setIdPhieuGiamGia(updatedPgg);
                        pggcn.setIdKhachHang(kh);
                        pggcn.setMa(dtoPGG.getMa() + "-" + khachHangID);
                        pggcn.setNgayNhan(new Date());
                        pggcn.setNgayHetHan(updatedPgg.getNgayKetThuc());
                        pggcn.setTrangThai(true);
                        pggcn.setDeleted(false);

                        phieuGiamGiaCaNhanService.addPGGCN(pggcn);

                        // Lấy email từ TaiKhoan thay vì KhachHang
                        String email = (kh.getIdTaiKhoan() != null) ? kh.getIdTaiKhoan().getEmail() : null;

                        // Gửi email cho khách hàng mới được thêm
                        if (addedCustomerIds.contains(khachHangID)) {
                            if (email != null && !email.isEmpty()) {
                                emailSend.sendDiscountEmail(
                                        email,
                                        dtoPGG.getMa(),
                                        updatedPgg.getTenPhieuGiamGia(),
                                        dateFormat.format(updatedPgg.getNgayKetThuc()),
                                        updatedPgg.getPhanTramGiamGia(),
                                        updatedPgg.getSoTienGiamToiDa(),
                                        updatedPgg.getHoaDonToiThieu(),
                                        updatedPgg.getMoTa()
                                );
                            }
                        }
                        // Gửi email cho khách hàng được khôi phục
                        else if (restoredCustomerIds.contains(khachHangID)) {
                            if (email != null && !email.isEmpty()) {
                                emailSend.sendDiscountEmail(
                                        email,
                                        dtoPGG.getMa(),
                                        updatedPgg.getTenPhieuGiamGia(),
                                        dateFormat.format(updatedPgg.getNgayKetThuc()),
                                        updatedPgg.getPhanTramGiamGia(),
                                        updatedPgg.getSoTienGiamToiDa(),
                                        updatedPgg.getHoaDonToiThieu(),
                                        updatedPgg.getMoTa()
                                );
                            }
                        }
                    }
                }

                // Gửi email thông báo cập nhật cho khách hàng cũ (không bao gồm khách hàng mới và khách hàng được khôi phục)
                List<Integer> unchangedCustomerIds = new ArrayList<>(newCustomerIds);
                unchangedCustomerIds.removeAll(addedCustomerIds);
                unchangedCustomerIds.removeAll(restoredCustomerIds);

                for (Integer khachHangID : unchangedCustomerIds) {
                    KhachHang kh = khachHangServices.findById(khachHangID);
                    if (kh != null) {
                        // Lấy email từ TaiKhoan thay vì KhachHang
                        String email = (kh.getIdTaiKhoan() != null) ? kh.getIdTaiKhoan().getEmail() : null;
                        if (email != null && !email.isEmpty()) {
                            emailSend.sendUpdateDiscountEmail(
                                    email,
                                    dtoPGG.getMa(),
                                    dateFormat.format(updatedPgg.getNgayKetThuc()),
                                    updatedPgg.getPhanTramGiamGia(),
                                    updatedPgg.getSoTienGiamToiDa()
                            );
                        }
                    }
                }

                // Gửi email thông báo thu hồi cho khách hàng bị xóa
                for (Integer khachHangID : removedCustomerIds) {
                    KhachHang kh = khachHangServices.findById(khachHangID);
                    if (kh != null) {
                        // Lấy email từ TaiKhoan thay vì KhachHang
                        String email = (kh.getIdTaiKhoan() != null) ? kh.getIdTaiKhoan().getEmail() : null;
                        if (email != null && !email.isEmpty()) {
                            emailSend.sendRevokeDiscountEmail(email, dtoPGG.getMa());
                        }
                    }
                }
            } else {
                // Nếu chuyển từ riêng tư sang công khai, xóa tất cả PhieuGiamGiaCaNhan và gửi email thu hồi
                List<PhieuGiamGiaCaNhan> existPggcnList = phieuGiamGiaCaNhanService.findByPhieuGiamGiaId(id);
                for (PhieuGiamGiaCaNhan pggcn : existPggcnList) {
                    KhachHang kh = pggcn.getIdKhachHang();
                    // Lấy email từ TaiKhoan thay vì KhachHang
                    String email = (kh.getIdTaiKhoan() != null) ? kh.getIdTaiKhoan().getEmail() : null;
                    if (email != null && !email.isEmpty()) {
                        emailSend.sendRevokeDiscountEmail(email, dtoPGG.getMa());
                    }
                }
                phieuGiamGiaCaNhanService.deleteByPhieuGiamGiaId(id);
            }

            return ResponseEntity.ok(updatedPgg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<PhieuGiamGiaRequest> updateTrangThai(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> requestBody) {
        Boolean trangThai = requestBody.get("trangThai");
        if (trangThai == null) {
            return ResponseEntity.badRequest().body(null);
        }
        PhieuGiamGiaRequest updatedPgg = phieuGiamGiaService.updateTrangthai(id, trangThai);
        return ResponseEntity.ok(updatedPgg);
    }
    @GetMapping("/phieu-giam-gia")
    public List<PhieuGiamGia> getall(){
        return phieuGiamGiaService.getall();
    }

    @GetMapping("/check-public")
    public ResponseEntity<?> checkPublicDiscountCode(@RequestParam("ma") String ma) {
        Optional<PhieuGiamGia> optionalPGG = phieuGiamGiaRepository.findValidPublicVoucherByMa(ma, new Date());
        if (!optionalPGG.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mã giảm giá công khai không tồn tại hoặc không hợp lệ.");
        }
        return ResponseEntity.ok(optionalPGG.get());
    }

    @GetMapping("/validate-at-checkout")
    public ResponseEntity<?> validateDiscountAtCheckout(
            @RequestParam("ma") String ma,
            @RequestParam("totalPrice") BigDecimal totalPrice,
            @RequestParam(value = "khachHangId", required = false) Integer khachHangId) {

        Date currentDate = new Date();

        // ✅ Kiểm tra trước ở bảng cá nhân
        Optional<PhieuGiamGiaCaNhan> optionalCaNhan = phieuGiamGiaCaNhanRepository.findByMa(ma);

        if (optionalCaNhan.isPresent()) {
            PhieuGiamGiaCaNhan pggCaNhan = optionalCaNhan.get();

            // ⚠ Nếu không có khách hàng hoặc không đúng người thì báo lỗi
            if (khachHangId == null || !pggCaNhan.getIdKhachHang().getId().equals(khachHangId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phiếu giảm giá này không áp dụng cho khách hàng.");
            }

            // Các điều kiện kiểm tra
            PhieuGiamGia pgg = pggCaNhan.getIdPhieuGiamGia();

            if (!Boolean.TRUE.equals(pggCaNhan.getTrangThai()) || Boolean.TRUE.equals(pggCaNhan.getDeleted())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã giảm giá đã bị vô hiệu hóa.");
            }

            if (pggCaNhan.getNgayHetHan() != null && pggCaNhan.getNgayHetHan().before(currentDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã giảm giá đã hết hạn.");
            }

            if (!Boolean.TRUE.equals(pgg.getTrangThai()) || Boolean.TRUE.equals(pgg.getDeleted())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã giảm giá không còn hiệu lực.");
            }

            BigDecimal hoaDonToiThieu = pgg.getHoaDonToiThieu() != null
                    ? BigDecimal.valueOf(pgg.getHoaDonToiThieu())
                    : BigDecimal.ZERO;

            if (totalPrice.compareTo(hoaDonToiThieu) < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tổng tiền chưa đủ để áp dụng mã giảm giá.");
            }

            return ResponseEntity.ok(pgg);
        }

        // ❌ Không tìm thấy ở bảng cá nhân → kiểm tra ở bảng công khai
        Optional<PhieuGiamGia> optionalPGG = phieuGiamGiaRepository.findByMa(ma);
        if (optionalPGG.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mã giảm giá không tồn tại.");
        }

        PhieuGiamGia pgg = optionalPGG.get();
        // (các kiểm tra khác như bạn đang làm)

        return ResponseEntity.ok(pgg);
    }


    @GetMapping("/find-better-discount")
    public ResponseEntity<?> findBetterDiscount(
            @RequestParam("currentDiscountMa") String currentDiscountMa,
            @RequestParam("totalPrice") BigDecimal totalPrice,
            @RequestParam(value = "khachHangId", required = false) Integer khachHangId) {
        Optional<PhieuGiamGia> currentPGG = phieuGiamGiaRepository.findByma(currentDiscountMa);
        Double currentDiscountValue = currentPGG.map(PhieuGiamGia::getSoTienGiamToiDa).orElse(0.0);
        Date currentDate = new Date();
        Double tongTienDouble = totalPrice.doubleValue();

        // Lấy tất cả phiếu giảm giá công khai hợp lệ
        List<PhieuGiamGia> validPublicDiscounts = phieuGiamGiaRepository.findValidPublicVouchers(tongTienDouble, currentDate)
                .stream()
                .filter(pgg -> pgg.getSoTienGiamToiDa() > currentDiscountValue)
                .collect(Collectors.toList());

        // Lấy tất cả phiếu giảm giá riêng tư hợp lệ (nếu có khachHangId)
        List<PhieuGiamGia> validPrivateDiscounts = new ArrayList<>();
        if (khachHangId != null && khachHangId > 0) {
            List<PhieuGiamGiaCaNhan> privateVouchers = phieuGiamGiaCaNhanRepository.findValidPrivateVouchersByKhachHang(
                    khachHangId, tongTienDouble, currentDate);
            validPrivateDiscounts = privateVouchers.stream()
                    .map(PhieuGiamGiaCaNhan::getIdPhieuGiamGia)
                    .filter(pgg -> pgg.getSoTienGiamToiDa() > currentDiscountValue)
                    .collect(Collectors.toList());
        }

        // Kết hợp danh sách phiếu giảm giá hợp lệ
        List<PhieuGiamGia> allValidDiscounts = new ArrayList<>();
        allValidDiscounts.addAll(validPublicDiscounts);
        allValidDiscounts.addAll(validPrivateDiscounts);

        if (allValidDiscounts.isEmpty()) {
            return ResponseEntity.ok().body(null); // Không có phiếu nào tốt hơn
        }

        // Chọn phiếu có giá trị giảm lớn nhất
        PhieuGiamGia bestDiscount = allValidDiscounts.stream()
                .max(Comparator.comparingDouble(PhieuGiamGia::getSoTienGiamToiDa))
                .orElse(null);

        return ResponseEntity.ok(bestDiscount);
    }

}
