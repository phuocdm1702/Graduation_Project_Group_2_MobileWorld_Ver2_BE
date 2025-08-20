package com.example.be_datn.service.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.product.Imel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

public interface HoaDonService {
    Page<HoaDonResponse> getHoaDon(Pageable pageable);

    Page<HoaDonResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount,
                                             Timestamp startDate, Timestamp endDate, Short trangThai, String loaiDon, Pageable pageable);

    Page<HoaDonResponse> getHoaDonOfCustomerAndFilters(Integer idKhachHang, Timestamp startDate, Timestamp endDate, Short trangThai, Pageable pageable);

    HoaDonDetailResponse getHoaDonDetail(Integer id);

    HoaDonResponse getHoaDonByMa(String maHD);

    void exportHoaDonToExcel(HttpServletResponse response) throws IOException;

    HoaDonResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien);

    Page<Imel> getAllImelBySanPhamId(Pageable pageable, Boolean deleted, Integer idSanPham, Integer chiTietSanPhamId);

    HoaDonResponse confirmAndAssignIMEI(Integer idHD, Map<Integer, String> imelMap);

    HoaDonResponse updateHoaDonKH(Integer id, String tenKH, String sdt, String diaChi, String email);

    HoaDonResponse updateHoaDon(Integer id, String maHD, String loaHD);

    HoaDon updatePhieuGiamGia(Integer hoaDonId, Integer idPhieuGiamGia);

    HoaDonResponse getHoaDonByMaForLookup(String maHD);

    HoaDonResponse cancelOrder(Integer orderId);
}