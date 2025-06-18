package com.example.be_datn.dto.discount.request;

import java.util.Date;
import java.util.List;

public class PhieuGiamGiaRequest {
    private Integer id;
    private String ma;
    private String tenPhieuGiamGia;
    private String loaiPhieuGiamGia;
    private Double phanTramGiamGia;
    private Double soTienGiamToiDa;
    private Double hoaDonToiThieu;
    private Integer soLuongDung;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Boolean trangThai;
    private Integer riengTu;
    private String moTa;
    private List<PhieuGiamGiaCaNhanRequest> selectedCustomers;
    private List<PhieuGiamGiaCaNhanRequest> allCustomers;
    private List<Integer> customerIds;
    private List<Integer> restoredCustomerIds;

    public PhieuGiamGiaRequest() {
    }

    public PhieuGiamGiaRequest(Integer id, String ma, String tenPhieuGiamGia, String loaiPhieuGiamGia, Double phanTramGiamGia, Double soTienGiamToiDa, Double hoaDonToiThieu, Integer soLuongDung, Date ngayBatDau, Date ngayKetThuc, Boolean trangThai, Integer riengTu, String moTa, List<PhieuGiamGiaCaNhanRequest> selectedCustomers, List<PhieuGiamGiaCaNhanRequest> allCustomers, List<Integer> customerIds, List<Integer> restoredCustomerIds) {
        this.id = id;
        this.ma = ma;
        this.tenPhieuGiamGia = tenPhieuGiamGia;
        this.loaiPhieuGiamGia = loaiPhieuGiamGia;
        this.phanTramGiamGia = phanTramGiamGia;
        this.soTienGiamToiDa = soTienGiamToiDa;
        this.hoaDonToiThieu = hoaDonToiThieu;
        this.soLuongDung = soLuongDung;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.riengTu = riengTu;
        this.moTa = moTa;
        this.selectedCustomers = selectedCustomers;
        this.allCustomers = allCustomers;
        this.customerIds = customerIds;
        this.restoredCustomerIds = restoredCustomerIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTenPhieuGiamGia() {
        return tenPhieuGiamGia;
    }

    public void setTenPhieuGiamGia(String tenPhieuGiamGia) {
        this.tenPhieuGiamGia = tenPhieuGiamGia;
    }

    public String getLoaiPhieuGiamGia() {
        return loaiPhieuGiamGia;
    }

    public void setLoaiPhieuGiamGia(String loaiPhieuGiamGia) {
        this.loaiPhieuGiamGia = loaiPhieuGiamGia;
    }

    public Double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public void setPhanTramGiamGia(Double phanTramGiamGia) {
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public Double getSoTienGiamToiDa() {
        return soTienGiamToiDa;
    }

    public void setSoTienGiamToiDa(Double soTienGiamToiDa) {
        this.soTienGiamToiDa = soTienGiamToiDa;
    }

    public Double getHoaDonToiThieu() {
        return hoaDonToiThieu;
    }

    public void setHoaDonToiThieu(Double hoaDonToiThieu) {
        this.hoaDonToiThieu = hoaDonToiThieu;
    }

    public Integer getSoLuongDung() {
        return soLuongDung;
    }

    public void setSoLuongDung(Integer soLuongDung) {
        this.soLuongDung = soLuongDung;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getRiengTu() {
        return riengTu;
    }

    public void setRiengTu(Integer riengTu) {
        this.riengTu = riengTu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public List<PhieuGiamGiaCaNhanRequest> getSelectedCustomers() {
        return selectedCustomers;
    }

    public void setSelectedCustomers(List<PhieuGiamGiaCaNhanRequest> selectedCustomers) {
        this.selectedCustomers = selectedCustomers;
    }

    public List<PhieuGiamGiaCaNhanRequest> getAllCustomers() {
        return allCustomers;
    }

    public void setAllCustomers(List<PhieuGiamGiaCaNhanRequest> allCustomers) {
        this.allCustomers = allCustomers;
    }

    public List<Integer> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(List<Integer> customerIds) {
        this.customerIds = customerIds;
    }

    public List<Integer> getRestoredCustomerIds() {
        return restoredCustomerIds;
    }

    public void setRestoredCustomerIds(List<Integer> restoredCustomerIds) {
        this.restoredCustomerIds = restoredCustomerIds;
    }
}
