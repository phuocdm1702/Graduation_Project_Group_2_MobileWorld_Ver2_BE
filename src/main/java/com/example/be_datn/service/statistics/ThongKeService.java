package com.example.be_datn.service.statistics;

import com.example.be_datn.dto.statistics.respone.HangBanChayDTO;
import com.example.be_datn.dto.statistics.respone.LoaiHoaDonDTO;
import com.example.be_datn.dto.statistics.respone.SanPhamHetHangDTO;
import com.example.be_datn.dto.statistics.respone.TopSellingProductDTO;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.repository.statistics.CTSPForThongKe;
import com.example.be_datn.repository.statistics.ThongKeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThongKeService {
    private ThongKeRepository tkRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CTSPForThongKe chiTietSanPhamRepository;

    @Autowired
    public ThongKeService(ThongKeRepository tkRepo) {
        this.tkRepo = tkRepo;
    }

    public Map<String, Object> getThongKeTheoNgay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date ngayHienTai = cal.getTime();
        Map<String, Object> result = tkRepo.thongKeTheoNgay(ngayHienTai);
        return convertNumberValuesToInteger(result);
    }

    public Map<String, Object> getThongKeTheoTuan() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date startOfWeek = cal.getTime();
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        Date endOfWeek = cal.getTime();
        Map<String, Object> result = tkRepo.thongKeTheoTuan(startOfWeek, endOfWeek);
        return convertNumberValuesToInteger(result);
    }

    public Map<String, Object> getThongKeTheoThang() {
        Calendar cal = Calendar.getInstance();
        int thang = cal.get(Calendar.MONTH) + 1; // +1 vì tháng bắt đầu từ 0
        int nam = cal.get(Calendar.YEAR);
        Map<String, Object> result = tkRepo.thongKeTheoThang(thang, nam);
        return convertNumberValuesToInteger(result);
    }

    public Map<String, Object> getThongKeTheoNam() {
        Calendar cal = Calendar.getInstance();
        int nam = cal.get(Calendar.YEAR);
        Map<String, Object> result = tkRepo.thongKeTheoNam(nam);
        return convertNumberValuesToInteger(result);
    }


    public List<Map<String, Object>> thongKeDoanhThuTheoKhungGio(Date ngayHienTai) {
        return tkRepo.thongKeDoanhThuTheoKhungGio(ngayHienTai);
    }

    public List<Map<String, Object>> thongKeDoanhThuTheoNgayTrongTuan(Date startOfWeek, Date endOfWeek) {
        return tkRepo.thongKeDoanhThuTheoNgayTrongTuan(startOfWeek, endOfWeek);
    }

    public List<Map<String, Object>> thongKeDoanhThuTheoTuanTrongThang(int thang, int nam) {
        return tkRepo.thongKeDoanhThuTheoTuanTrongThang(thang, nam);
    }


    public List<Map<String, Object>> thongKeDoanhThuTheoQuy(int nam) {
        return tkRepo.thongKeDoanhThuTheoQuy(nam);
    }


    public List<Map<String, Object>> thongKeDoanhThuTheoThangTrongKhoangThoiGian(Date startDate, Date endDate) {
        return tkRepo.thongKeDoanhThuTheoThangTrongKhoangThoiGian(startDate, endDate);
    }


    public BigDecimal doanhThuTheoKhoangThoiGian(Date startDate, Date endDate) {
        BigDecimal revenue = tkRepo.doanhThuTheoKhoangThoiGian(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }



    public Page<TopSellingProductDTO> getTopSellingProducts(String filterType, String startDateStr, String endDateStr, int page, int size) {
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        try {
            if ("day".equals(filterType)) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            } else if ("week".equals(filterType)) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
                cal.add(Calendar.DAY_OF_WEEK, 6);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            } else if ("month".equals(filterType)) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
                cal.setTime(new Date());
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            } else if ("year".equals(filterType)) {
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
                cal.setTime(new Date());
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            } else if ("custom".equals(filterType)) {
                if (startDateStr == null || startDateStr.isEmpty() || endDateStr == null || endDateStr.isEmpty()) {
                    throw new IllegalArgumentException("Start date and end date are required for custom filter");
                }
                startDate = sdf.parse(startDateStr);
                endDate = sdf.parse(endDateStr);

                // Đảm bảo endDate bao gồm cả ngày
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            } else {
                throw new IllegalArgumentException("Invalid filter type: " + filterType);
            }
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format for startDateStr=" + startDateStr + ", endDateStr=" + endDateStr, e);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("soLuongBan").descending());
        Page<Object[]> results = tkRepo.findTopSellingProducts(startDate, endDate, pageable);

        List<TopSellingProductDTO> dtos = results.getContent().stream().map(result -> {
            Integer chiTietSanPhamId = (Integer) result[0];
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(chiTietSanPhamId).orElse(null);
            if (ctsp != null) {
                String productName = String.format("%s - %s - %s",
                        ctsp.getIdSanPham().getTenSanPham(),
                        ctsp.getIdMauSac().getMauSac(),
                        ctsp.getIdBoNhoTrong().getDungLuongBoNhoTrong());
                return new TopSellingProductDTO(
                        ctsp.getIdAnhSanPham().getDuongDan(),
                        productName,
                        ctsp.getGiaBan(),
                        ((Number) result[1]).intValue()
                );
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    public List<Map<String, Object>> getAllTopSellingProducts(String filterType, String startDateStr, String endDateStr) {
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        if ("day".equals(filterType)) {
            startDate = new Date();
            endDate = startDate;
        } else if ("month".equals(filterType)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
            endDate = new Date();
        } else if ("year".equals(filterType)) {
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
            endDate = new Date();
        } else if ("custom".equals(filterType) && startDateStr != null && endDateStr != null) {
            try {
                startDate = sdf.parse(startDateStr);
                endDate = sdf.parse(endDateStr);
            } catch (ParseException e) {
                throw new RuntimeException("Invalid date format");
            }
        }

        List<Object[]> results = tkRepo.findAllTopSellingProducts(startDate, endDate);

        return results.stream().map(result -> {
            Integer chiTietSanPhamId = (Integer) result[0];
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(chiTietSanPhamId).orElse(null);
            if (ctsp != null) {
                String productName = String.format("%s - %s - %s",
                        ctsp.getIdSanPham().getTenSanPham(),
                        ctsp.getIdMauSac().getMauSac(),
                        ctsp.getIdBoNhoTrong().getDungLuongBoNhoTrong());
                Map<String, Object> map = new HashMap<>();
                map.put("imageUrl", ctsp.getIdAnhSanPham().getDuongDan());
                map.put("productName", productName);
                map.put("price", ctsp.getGiaBan());
                map.put("soldQuantity", ((Number) result[1]).intValue());
                return map;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Map<String, Object> getGrowthData() {
        Map<String, Object> growthData = new HashMap<>();

        Map<String, Object> ngayHienTai = tkRepo.tangTruongTheoNgay(new Date());
        Map<String, Object> thangHienTai = tkRepo.tangTruongTheoThang(new Date());
        Map<String, Object> namHienTai = tkRepo.tangTruongTheoNam(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Map<String, Object> ngayTruoc = tkRepo.tangTruongTheoNgay(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Map<String, Object> thangTruoc = tkRepo.tangTruongTheoThang(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        Map<String, Object> namTruoc = tkRepo.tangTruongTheoNam(cal.getTime());

        growthData.put("doanhThuNgay", getDoubleValue(ngayHienTai, "doanhThu"));
        growthData.put("doanhThuThang", getDoubleValue(thangHienTai, "doanhThu"));
        growthData.put("doanhThuNam", getDoubleValue(namHienTai, "doanhThu"));
        growthData.put("sanPhamDaBanThang", getIntegerValue(thangHienTai, "sanPhamDaBan"));
        growthData.put("hoaDonTheoNgay", getIntegerValue(ngayHienTai, "tongSoDonHang"));
        growthData.put("hoaDonTheoNam", getIntegerValue(namHienTai, "tongSoDonHang"));

        growthData.put("growthDoanhThuNgay", calculateGrowth(getDoubleValue(ngayHienTai, "doanhThu"), getDoubleValue(ngayTruoc, "doanhThu")));
        growthData.put("growthDoanhThuThang", calculateGrowth(getDoubleValue(thangHienTai, "doanhThu"), getDoubleValue(thangTruoc, "doanhThu")));
        growthData.put("growthDoanhThuNam", calculateGrowth(getDoubleValue(namHienTai, "doanhThu"), getDoubleValue(namTruoc, "doanhThu")));
        growthData.put("growthSanPhamDaBanThang", calculateGrowth(getDoubleValue(thangHienTai, "sanPhamDaBan"), getDoubleValue(thangTruoc, "sanPhamDaBan")));
        growthData.put("growthHoaDonTheoNgay", calculateGrowth(getDoubleValue(ngayHienTai, "tongSoDonHang"), getDoubleValue(ngayTruoc, "tongSoDonHang")));
        growthData.put("growthHoaDonTheoNam", calculateGrowth(getDoubleValue(namHienTai, "tongSoDonHang"), getDoubleValue(namTruoc, "tongSoDonHang")));

        return growthData;
    }

    private double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    private int getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    private double calculateGrowth(double current, double previous) {
        if (previous == 0) return 0;
        return ((current - previous) / previous) * 100;
    }

    public Page<SanPhamHetHangDTO> thongKeSanPhamHetHang(Pageable pageable) {
        return tkRepo.thongKeSanPhamHetHang(pageable);
    }

    public List<Map<String, Object>> getAllSanPhamHetHang() {
        List<SanPhamHetHangDTO> results = tkRepo.thongKeSanPhamHetHangNoPage();
        return results.stream().map(dto -> {
            Map<String, Object> map = new HashMap<>();
            map.put("tenSanPham", dto.getTenSanPham());
            map.put("soLuong", dto.getSoLuong());
            return map;
        }).collect(Collectors.toList());
    }

    public List<LoaiHoaDonDTO> thongKeLoaiHoaDon() {
        return tkRepo.thongKeLoaiHoaDon();
    }

    public List<Map<String, Object>> getAllLoaiHoaDon() {
        List<LoaiHoaDonDTO> results = tkRepo.thongKeLoaiHoaDon();
        return results.stream().map(dto -> {
            Map<String, Object> map = new HashMap<>();
            map.put("loaiDon", dto.getLoaiDon());
            map.put("soLuong", dto.getSoLuong());
            return map;
        }).collect(Collectors.toList());
    }

    public List<HangBanChayDTO> thongKeHangBanChay() {
        return tkRepo.thongKeHangBanChay();
    }

    public Map<String, Long> getOrderStatusStats(String filterType, Date date) {
        List<Map<String, Object>> result = tkRepo.thongKeTrangThaiDonHang(filterType, date);
        Map<String, Long> statusStats = new HashMap<>();

        // Khởi tạo giá trị mặc định
        statusStats.put("Chờ xác nhận", 0L);
        statusStats.put("Chờ giao hàng", 0L);
        statusStats.put("Đang giao", 0L);
        statusStats.put("Hoàn thành", 0L);
        statusStats.put("Đã hủy", 0L);

        for (Map<String, Object> entry : result) {
            Object trangThaiObj = entry.get("trangThai");
            Integer trangThai = null;
            try {
                if (trangThaiObj instanceof Number) {
                    trangThai = ((Number) trangThaiObj).intValue();
                } else if (trangThaiObj instanceof String) {
                    trangThai = Integer.parseInt((String) trangThaiObj);
                }
            } catch (Exception e) {
                System.err.println("Invalid trangThai value: " + trangThaiObj);
                continue;
            }

            Object soLuongObj = entry.get("soLuong");
            Long soLuong = 0L;
            try {
                if (soLuongObj instanceof Number) {
                    soLuong = ((Number) soLuongObj).longValue();
                }
            } catch (Exception e) {
                System.err.println("Invalid soLuong value: " + soLuongObj);
                continue;
            }

            if (trangThai != null) {
                switch (trangThai) {
                    case 0: statusStats.put("Chờ xác nhận", soLuong); break;
                    case 1: statusStats.put("Chờ giao hàng", soLuong); break;
                    case 2: statusStats.put("Đang giao", soLuong); break;
                    case 3: statusStats.put("Hoàn thành", soLuong); break;
                    case 4: statusStats.put("Đã hủy", soLuong); break;
                }
            }
        }

        return statusStats;
    }

    private Map<String, Object> convertNumberValuesToInteger(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>(input);
        if (result.get("sanPhamDaBan") instanceof Number) {
            result.put("sanPhamDaBan", ((Number) result.get("sanPhamDaBan")).intValue());
        }
        if (result.get("tongSoDonHang") instanceof Number) {
            result.put("tongSoDonHang", ((Number) result.get("tongSoDonHang")).intValue());
        }
        return result;
    }
}