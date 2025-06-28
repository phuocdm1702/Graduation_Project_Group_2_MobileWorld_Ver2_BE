package com.example.be_datn.dto.order.response;

import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.product.ImelDaBan;
import jdk.jshell.Snippet;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonDetailResponse {
    // Thông tin hóa đơn
    private Integer id;
    private String maHoaDon;
    private String loaiDon;
    private Short trangThai;
    private String maGiamGia;
    private BigDecimal tienGiam; // Thêm trường này
    private Double phanTramGiam; // Thêm trường này
    private Date ngayTao;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String diaChiKhachHang;
    private String email;
    private BigDecimal tongTienSauGiam;
    private BigDecimal phiVanChuyen;

    // Thông tin nhân viên
    private String maNhanVien;
    private String tenNhanVien;

    // Thông tin thanh toán
    private List<ThanhToanInfo> thanhToanInfos;

    // Thông tin chi tiết sản phẩm
    private List<SanPhamChiTietInfo> sanPhamChiTietInfos;

    // Lịch sử hóa đơn
    private List<LichSuHoaDonInfo> lichSuHoaDonInfos;

    // DTO con cho thanh toán
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThanhToanInfo {
        private String maHinhThucThanhToan;
        private String kieuThanhToan;
        private BigDecimal tienChuyenKhoan;
        private BigDecimal tienMat;
    }

    // DTO con cho sản phẩm chi tiết
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SanPhamChiTietInfo {
        private Integer idHoaDon;
        private String maSanPham;
        private String tenSanPham;
        private String imel;
        private BigDecimal giaBan;
        private String ghiChu;
        private String mauSac; // Thêm nếu cần
        private String boNho; // Thêm nếu cần
    }

    // DTO con cho lịch sử hóa đơn
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LichSuHoaDonInfo {
        private String ma;
        private String hanhDong;
        private Instant thoiGian;
        private String tenNhanVien;
        private Integer idHoaDon;
    }

    // Builder Pattern
    public static class Builder {
        private final HoaDonDetailResponse response;

        public Builder() {
            response = new HoaDonDetailResponse();
        }

        public Builder withHoaDonInfo(HoaDon hoaDon, PhieuGiamGia phieuGiamGia) {
            response.id = hoaDon.getId();
            response.maHoaDon = hoaDon.getMa();
            response.loaiDon = hoaDon.getLoaiDon();
            response.trangThai = hoaDon.getTrangThai();
            response.maGiamGia = phieuGiamGia != null ? phieuGiamGia.getMa() : null;
            response.tienGiam = phieuGiamGia != null && phieuGiamGia.getSoTienGiamToiDa() != null
                    ? BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa()) : BigDecimal.ZERO;
            response.phanTramGiam = phieuGiamGia != null ? phieuGiamGia.getPhanTramGiamGia() : 0.0;
            response.ngayTao = hoaDon.getNgayTao();
            response.tenKhachHang = hoaDon.getTenKhachHang();
            response.soDienThoaiKhachHang = hoaDon.getSoDienThoaiKhachHang();
            response.diaChiKhachHang = hoaDon.getDiaChiKhachHang();
            response.email = hoaDon.getEmail();
            response.tongTienSauGiam = hoaDon.getTongTienSauGiam();
            response.phiVanChuyen = hoaDon.getPhiVanChuyen();
            return this;
        }

        public Builder withNhanVienInfo(NhanVien nhanVien) {
            response.maNhanVien = nhanVien.getMa();
            response.tenNhanVien = nhanVien.getTenNhanVien();
            return this;
        }

        public Builder withThanhToanInfos(List<ThanhToanInfo> thanhToanInfos) {
            response.thanhToanInfos = thanhToanInfos;
            return this;
        }

        public Builder withSanPhamChiTietInfos(List<SanPhamChiTietInfo> sanPhamChiTietInfos) {
            response.sanPhamChiTietInfos = sanPhamChiTietInfos;
            return this;
        }

        public Builder withLichSuHoaDonInfos(List<LichSuHoaDonInfo> lichSuHoaDonInfos) {
            response.lichSuHoaDonInfos = lichSuHoaDonInfos;
            return this;
        }

        public HoaDonDetailResponse build() {
            return response;
        }
    }
}
