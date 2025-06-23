package com.example.be_datn.dto.sell.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class HoaDonDTO {
    private Integer id;
    private Integer idKhachHang;
    private Integer idPhieuGiamGia;
    private Integer idNhanVien;
    private String ma;
    private BigDecimal tienSanPham;
    private String loaiDon;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private String ghiChu;
    private String tenKhachHang;
    private String diaChiKhachHang;
    private String soDienThoaiKhachHang;
    private String email;
    private Date ngayTao;
    private Short trangThai;

    private List<ChiTietGioHangDTO> chiTietGioHangDTOS;

    private String ghiChuGia;

    public HoaDonDTO() {
    }

    public HoaDonDTO(Integer id, Integer idKhachHang, Integer idPhieuGiamGia, Integer idNhanVien, String ma, BigDecimal tienSanPham, String loaiDon, BigDecimal phiVanChuyen, BigDecimal tongTien, BigDecimal tongTienSauGiam, String ghiChu, String tenKhachHang, String diaChiKhachHang, String soDienThoaiKhachHang, String email, Date ngayTao, Short trangThai, List<ChiTietGioHangDTO> chiTietGioHangDTOS, String ghiChuGia) {
        this.id = id;
        this.idKhachHang = idKhachHang;
        this.idPhieuGiamGia = idPhieuGiamGia;
        this.idNhanVien = idNhanVien;
        this.ma = ma;
        this.tienSanPham = tienSanPham;
        this.loaiDon = loaiDon;
        this.phiVanChuyen = phiVanChuyen;
        this.tongTien = tongTien;
        this.tongTienSauGiam = tongTienSauGiam;
        this.ghiChu = ghiChu;
        this.tenKhachHang = tenKhachHang;
        this.diaChiKhachHang = diaChiKhachHang;
        this.soDienThoaiKhachHang = soDienThoaiKhachHang;
        this.email = email;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.chiTietGioHangDTOS = chiTietGioHangDTOS;
        this.ghiChuGia = ghiChuGia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdKhachHang() {
        return idKhachHang;
    }

    public void setIdKhachHang(Integer idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    public Integer getIdPhieuGiamGia() {
        return idPhieuGiamGia;
    }

    public void setIdPhieuGiamGia(Integer idPhieuGiamGia) {
        this.idPhieuGiamGia = idPhieuGiamGia;
    }

    public Integer getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(Integer idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public BigDecimal getTienSanPham() {
        return tienSanPham;
    }

    public void setTienSanPham(BigDecimal tienSanPham) {
        this.tienSanPham = tienSanPham;
    }

    public String getLoaiDon() {
        return loaiDon;
    }

    public void setLoaiDon(String loaiDon) {
        this.loaiDon = loaiDon;
    }

    public BigDecimal getPhiVanChuyen() {
        return phiVanChuyen;
    }

    public void setPhiVanChuyen(BigDecimal phiVanChuyen) {
        this.phiVanChuyen = phiVanChuyen;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getTongTienSauGiam() {
        return tongTienSauGiam;
    }

    public void setTongTienSauGiam(BigDecimal tongTienSauGiam) {
        this.tongTienSauGiam = tongTienSauGiam;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getDiaChiKhachHang() {
        return diaChiKhachHang;
    }

    public void setDiaChiKhachHang(String diaChiKhachHang) {
        this.diaChiKhachHang = diaChiKhachHang;
    }

    public String getSoDienThoaiKhachHang() {
        return soDienThoaiKhachHang;
    }

    public void setSoDienThoaiKhachHang(String soDienThoaiKhachHang) {
        this.soDienThoaiKhachHang = soDienThoaiKhachHang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Short getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Short trangThai) {
        this.trangThai = trangThai;
    }

    public List<ChiTietGioHangDTO> getChiTietGioHangDTOS() {
        return chiTietGioHangDTOS;
    }

    public void setChiTietGioHangDTOS(List<ChiTietGioHangDTO> chiTietGioHangDTOS) {
        this.chiTietGioHangDTOS = chiTietGioHangDTOS;
    }

    public String getGhiChuGia() {
        return ghiChuGia;
    }

    public void setGhiChuGia(String ghiChuGia) {
        this.ghiChuGia = ghiChuGia;
    }
}
