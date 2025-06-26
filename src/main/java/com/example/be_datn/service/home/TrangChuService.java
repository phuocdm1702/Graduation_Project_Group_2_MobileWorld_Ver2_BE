package com.example.be_datn.service.home;

import com.example.be_datn.dto.home.LichSuHoaDonDTOForHome;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.repository.home.TrangChuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrangChuService {
    @Autowired
    private TrangChuRepository trangChuRepository;

    public TrangChuService(TrangChuRepository trangChuRepository) {
        this.trangChuRepository = trangChuRepository;
    }

    public static class ThongKeDTO {
        private Number currentValue;
        private Number previousValue;
        private Double growthRate;

        public ThongKeDTO(Number current, Number previous, Number currentMonthNew, Number previousMonthNew) {
            this.currentValue = current;
            this.previousValue = previous;
            this.growthRate = calculateGrowthRate(currentMonthNew, previousMonthNew);
        }

        public Number getCurrentValue() {
            return currentValue;
        }

        public Number getPreviousValue() {
            return previousValue;
        }

        public Double getGrowthRate() {
            return growthRate;
        }

        private Double calculateGrowthRate(Number current, Number previous) {
            if (previous == null || current == null || previous.doubleValue() == 0) {
                return current != null && current.doubleValue() > 0 ? 100.0 : 0.0;
            }
            return ((current.doubleValue() - previous.doubleValue()) / previous.doubleValue()) * 100;
        }
    }

    public ThongKeDTO getSoLuongHoaDonHomNayVaHomQua() {
        Object[] result = trangChuRepository.getSoLuongHoaDonHomNayVaHomQua();
        Long todayCount = 0L;
        Long yesterdayCount = 0L;

        if (result != null && result.length > 0) {
            if (result.length == 1 && result[0] instanceof Object[]) {
                Object[] nestedResult = (Object[]) result[0];
                todayCount = nestedResult[0] != null ? convertToLong(nestedResult[0]) : 0L;
                yesterdayCount = nestedResult.length > 1 && nestedResult[1] != null ? convertToLong(nestedResult[1]) : 0L;
            } else if (result.length >= 2) {
                todayCount = result[0] != null ? convertToLong(result[0]) : 0L;
                yesterdayCount = result[1] != null ? convertToLong(result[1]) : 0L;
            }
        }

        return new ThongKeDTO(todayCount, yesterdayCount, todayCount, yesterdayCount);
    }

    public ThongKeDTO getTongTienThangNayVaThangTruoc() {
        Object[] result = trangChuRepository.getTongTienThangNayVaThangTruoc();
        BigDecimal currentMonth = BigDecimal.ZERO;
        BigDecimal previousMonth = BigDecimal.ZERO;

        if (result != null && result.length > 0) {
            if (result.length == 1 && result[0] instanceof Object[]) {
                Object[] nestedResult = (Object[]) result[0];
                currentMonth = nestedResult[0] != null ? convertToBigDecimal(nestedResult[0]) : BigDecimal.ZERO;
                previousMonth = nestedResult.length > 1 && nestedResult[1] != null ? convertToBigDecimal(nestedResult[1]) : BigDecimal.ZERO;
            } else if (result.length >= 2) {
                currentMonth = result[0] != null ? convertToBigDecimal(result[0]) : BigDecimal.ZERO;
                previousMonth = result[1] != null ? convertToBigDecimal(result[1]) : BigDecimal.ZERO;
            }
        }

        return new ThongKeDTO(currentMonth, previousMonth, currentMonth, previousMonth);
    }

    public ThongKeDTO thongKeKhachHang() {
        Object[] result = trangChuRepository.thongKeKhachHang();
        Long totalCustomers = 0L;
        Long currentMonthNew = 0L;
        Long previousMonthNew = 0L;

        if (result != null && result.length > 0) {
            if (result.length == 1 && result[0] instanceof Object[]) {
                Object[] nestedResult = (Object[]) result[0];
                totalCustomers = nestedResult[0] != null ? convertToLong(nestedResult[0]) : 0L;
                currentMonthNew = nestedResult.length > 1 && nestedResult[1] != null ? convertToLong(nestedResult[1]) : 0L;
                previousMonthNew = nestedResult.length > 2 && nestedResult[2] != null ? convertToLong(nestedResult[2]) : 0L;
            } else if (result.length >= 3) {
                totalCustomers = result[0] != null ? convertToLong(result[0]) : 0L;
                currentMonthNew = result[1] != null ? convertToLong(result[1]) : 0L;
                previousMonthNew = result[2] != null ? convertToLong(result[2]) : 0L;
            }
        }

        return new ThongKeDTO(totalCustomers, previousMonthNew, currentMonthNew, previousMonthNew);
    }

    public ThongKeDTO thongKeChiTietSanPham() {
        Object[] result = trangChuRepository.thongKeChiTietSanPham();
        Long totalProducts = 0L;
        Long currentMonthNew = 0L;
        Long previousMonthNew = 0L;

        if (result != null && result.length > 0) {
            if (result.length == 1 && result[0] instanceof Object[]) {
                Object[] nestedResult = (Object[]) result[0];
                totalProducts = nestedResult[0] != null ? convertToLong(nestedResult[0]) : 0L;
                currentMonthNew = nestedResult.length > 1 && nestedResult[1] != null ? convertToLong(nestedResult[1]) : 0L;
                previousMonthNew = nestedResult.length > 2 && nestedResult[2] != null ? convertToLong(nestedResult[2]) : 0L;
            } else if (result.length >= 3) {
                totalProducts = result[0] != null ? convertToLong(result[0]) : 0L;
                currentMonthNew = result[1] != null ? convertToLong(result[1]) : 0L;
                previousMonthNew = result[2] != null ? convertToLong(result[2]) : 0L;
            }
        }

        return new ThongKeDTO(totalProducts, previousMonthNew, currentMonthNew, previousMonthNew);
    }

    public List<LichSuHoaDonDTOForHome> getAllLichSuHoaDon() {
        Pageable pageable = PageRequest.of(0, 6);
        List<LichSuHoaDon> lichSuHoaDons = trangChuRepository.getAllLichSuHoaDon(pageable);
        return lichSuHoaDons.stream()
                .map(lshd -> {
                    LichSuHoaDonDTOForHome dto = new LichSuHoaDonDTOForHome();
                    dto.setId(lshd.getId());
                    dto.setMaHoaDon(lshd.getMa());
                    dto.setMoTa(lshd.getHanhDong());
                    dto.setThoiGian(lshd.getThoiGian());
                    dto.setTrangThai(lshd.getHoaDon() != null ? lshd.getHoaDon().getTrangThai() : (short) 0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Long convertToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
}