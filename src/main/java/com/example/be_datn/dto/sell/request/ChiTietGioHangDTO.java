package com.example.be_datn.dto.sell.request;

import java.math.BigDecimal;

public class ChiTietGioHangDTO {
    private Integer chiTietSanPhamId;
    private String maImel;
    private String tenSanPham;
    private String mauSac;


    private String ram;

    private String boNhoTrong;

    private Integer soLuong;
    private BigDecimal giaBan;

    private BigDecimal giaBanGoc;

    private String ghiChuGia;

    private BigDecimal tongTien;

    private Integer idPhieuGiamGia; // Thêm trường để lưu idPhieuGiamGia

    private String image;

    public ChiTietGioHangDTO() {
    }

    public ChiTietGioHangDTO(Integer chiTietSanPhamId, String maImel, String tenSanPham, String mauSac, String ram, String boNhoTrong, Integer soLuong, BigDecimal giaBan, BigDecimal giaBanGoc, String ghiChuGia, BigDecimal tongTien, Integer idPhieuGiamGia) {
        this.chiTietSanPhamId = chiTietSanPhamId;
        this.maImel = maImel;
        this.tenSanPham = tenSanPham;
        this.mauSac = mauSac;
        this.ram = ram;
        this.boNhoTrong = boNhoTrong;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.giaBanGoc = giaBanGoc;
        this.ghiChuGia = ghiChuGia;
        this.tongTien = tongTien;
        this.idPhieuGiamGia = idPhieuGiamGia;
    }

    public ChiTietGioHangDTO(Integer chiTietSanPhamId, String maImel, String tenSanPham, String mauSac, String ram, String boNhoTrong, Integer soLuong, BigDecimal giaBan, BigDecimal giaBanGoc, String ghiChuGia, BigDecimal tongTien, Integer idPhieuGiamGia, String image) {
        this.chiTietSanPhamId = chiTietSanPhamId;
        this.maImel = maImel;
        this.tenSanPham = tenSanPham;
        this.mauSac = mauSac;
        this.ram = ram;
        this.boNhoTrong = boNhoTrong;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.giaBanGoc = giaBanGoc;
        this.ghiChuGia = ghiChuGia;
        this.tongTien = tongTien;
        this.idPhieuGiamGia = idPhieuGiamGia;
        this.image = image;
    }

    public Integer getChiTietSanPhamId() {
        return chiTietSanPhamId;
    }

    public void setChiTietSanPhamId(Integer chiTietSanPhamId) {
        this.chiTietSanPhamId = chiTietSanPhamId;
    }

    public String getMaImel() {
        return maImel;
    }

    public void setMaImel(String maImel) {
        this.maImel = maImel;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getBoNhoTrong() {
        return boNhoTrong;
    }

    public void setBoNhoTrong(String boNhoTrong) {
        this.boNhoTrong = boNhoTrong;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getGiaBanGoc() {
        return giaBanGoc;
    }

    public void setGiaBanGoc(BigDecimal giaBanGoc) {
        this.giaBanGoc = giaBanGoc;
    }

    public String getGhiChuGia() {
        return ghiChuGia;
    }

    public void setGhiChuGia(String ghiChuGia) {
        this.ghiChuGia = ghiChuGia;
    }

    public Integer getIdPhieuGiamGia() {
        return idPhieuGiamGia;
    }

    public void setIdPhieuGiamGia(Integer idPhieuGiamGia) {
        this.idPhieuGiamGia = idPhieuGiamGia;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
