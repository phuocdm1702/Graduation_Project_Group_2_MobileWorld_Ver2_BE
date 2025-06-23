package com.example.be_datn.dto.order.request;

import com.example.be_datn.dto.pay.request.HinhThucThanhToanDTO;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.entity.account.DiaChiKhachHang;
import com.example.be_datn.entity.pay.HinhThucThanhToan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class HoaDonRequest {
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
    private DiaChiKhachHang diaChiKhachHang;
    private String soDienThoaiKhachHang;
    private String email;
    private Date ngayTao;
    private LocalDate ngayThanhToan;
    private Short trangThai;
    private Boolean deleted;
    private Date createdAt;
    private Integer createdBy;
    private Date updatedAt;
    private Integer updatedBy;

    private boolean bypassPriceCheck;

    private List<ChiTietGioHangDTO> chiTietGioHangDTOS;
    private Set<HinhThucThanhToanDTO> hinhThucThanhToan;

    public HoaDonRequest() {
    }

    public HoaDonRequest(Integer id, Integer idKhachHang, Integer idPhieuGiamGia, Integer idNhanVien, String ma, BigDecimal tienSanPham, String loaiDon, BigDecimal phiVanChuyen, BigDecimal tongTien, BigDecimal tongTienSauGiam, String ghiChu, String tenKhachHang, DiaChiKhachHang diaChiKhachHang, String soDienThoaiKhachHang, String email, Date ngayTao, LocalDate ngayThanhToan, Short trangThai, Boolean deleted, Date createdAt, Integer createdBy, Date updatedAt, Integer updatedBy, List<ChiTietGioHangDTO> chiTietGioHangDTOS, Set<HinhThucThanhToanDTO> hinhThucThanhToan) {
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
        this.ngayThanhToan = ngayThanhToan;
        this.trangThai = trangThai;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.chiTietGioHangDTOS = chiTietGioHangDTOS;
        this.hinhThucThanhToan = hinhThucThanhToan;
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

    public DiaChiKhachHang getDiaChiKhachHang() {
        return diaChiKhachHang;
    }

    public void setDiaChiKhachHang(DiaChiKhachHang diaChiKhachHang) {
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

    public LocalDate getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDate ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public Short getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Short trangThai) {
        this.trangThai = trangThai;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<ChiTietGioHangDTO> getChiTietGioHangDTOS() {
        return chiTietGioHangDTOS;
    }

    public void setChiTietGioHangDTOS(List<ChiTietGioHangDTO> chiTietGioHangDTOS) {
        this.chiTietGioHangDTOS = chiTietGioHangDTOS;
    }

    public Set<HinhThucThanhToanDTO> getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(Set<HinhThucThanhToanDTO> hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public boolean isBypassPriceCheck() {
        return bypassPriceCheck;
    }

    public void setBypassPriceCheck(boolean bypassPriceCheck) {
        this.bypassPriceCheck = bypassPriceCheck;
    }
}
