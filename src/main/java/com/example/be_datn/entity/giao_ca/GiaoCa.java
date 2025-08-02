package com.example.be_datn.entity.giao_ca;

import com.example.be_datn.entity.account.NhanVien;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "giao_ca")
@Builder
public class GiaoCa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nhan_vien", nullable = false)
    private NhanVien idNhanVien;

    @NotNull
    @Column(name = "tien_mat_ban_dau", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienMatBanDau;

    @Column(name = "tien_mat_cuoi_ca", precision = 18, scale = 2)
    private BigDecimal tienMatCuoiCa;

    @Column(name = "tien_mat_ca_truoc", precision = 18, scale = 2)
    private BigDecimal tienMatCaTruoc;

    @Column(name = "don_hang_cho_xu_ly_ca_truoc")
    private Integer donHangChoXuLyCaTruoc;

    @Column(name = "tong_tien_mat", precision = 18, scale = 2)
    private BigDecimal tongTienMat;

    @Column(name = "tong_tien_chuyen_khoan", precision = 18, scale = 2)
    private BigDecimal tongTienChuyenKhoan;

    @Column(name = "tong_doanh_thu", precision = 18, scale = 2)
    private BigDecimal tongDoanhThu;

    @NotNull
    @Column(name = "trang_thai", nullable = false)
    private Short trangThai;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;

    public GiaoCa() {

    }

    public GiaoCa(Integer id, NhanVien idNhanVien, BigDecimal tienMatBanDau, BigDecimal tienMatCuoiCa, BigDecimal tienMatCaTruoc, Integer donHangChoXuLyCaTruoc, BigDecimal tongTienMat, BigDecimal tongTienChuyenKhoan, BigDecimal tongDoanhThu, Short trangThai, LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc) {
        this.id = id;
        this.idNhanVien = idNhanVien;
        this.tienMatBanDau = tienMatBanDau;
        this.tienMatCuoiCa = tienMatCuoiCa;
        this.tienMatCaTruoc = tienMatCaTruoc;
        this.donHangChoXuLyCaTruoc = donHangChoXuLyCaTruoc;
        this.tongTienMat = tongTienMat;
        this.tongTienChuyenKhoan = tongTienChuyenKhoan;
        this.tongDoanhThu = tongDoanhThu;
        this.trangThai = trangThai;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NhanVien getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(NhanVien idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public BigDecimal getTienMatBanDau() {
        return tienMatBanDau;
    }

    public void setTienMatBanDau(BigDecimal tienMatBanDau) {
        this.tienMatBanDau = tienMatBanDau;
    }

    public BigDecimal getTienMatCuoiCa() {
        return tienMatCuoiCa;
    }

    public void setTienMatCuoiCa(BigDecimal tienMatCuoiCa) {
        this.tienMatCuoiCa = tienMatCuoiCa;
    }

    public BigDecimal getTienMatCaTruoc() {
        return tienMatCaTruoc;
    }

    public void setTienMatCaTruoc(BigDecimal tienMatCaTruoc) {
        this.tienMatCaTruoc = tienMatCaTruoc;
    }

    public Integer getDonHangChoXuLyCaTruoc() {
        return donHangChoXuLyCaTruoc;
    }

    public void setDonHangChoXuLyCaTruoc(Integer donHangChoXuLyCaTruoc) {
        this.donHangChoXuLyCaTruoc = donHangChoXuLyCaTruoc;
    }

    public BigDecimal getTongTienMat() {
        return tongTienMat;
    }

    public void setTongTienMat(BigDecimal tongTienMat) {
        this.tongTienMat = tongTienMat;
    }

    public BigDecimal getTongTienChuyenKhoan() {
        return tongTienChuyenKhoan;
    }

    public void setTongTienChuyenKhoan(BigDecimal tongTienChuyenKhoan) {
        this.tongTienChuyenKhoan = tongTienChuyenKhoan;
    }

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public Short getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Short trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }
}