package com.example.be_datn.dto.sell.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GioHangDTO {
    private String gioHangId;
    private Integer khachHangId;
    private List<ChiTietGioHangDTO> chiTietGioHangDTOS = new ArrayList<>();
    private BigDecimal tongTien;

    public GioHangDTO() {
    }

    public GioHangDTO(String gioHangId, Integer khachHangId, List<ChiTietGioHangDTO> chiTietGioHangDTOS, BigDecimal tongTien) {
        this.gioHangId = gioHangId;
        this.khachHangId = khachHangId;
        this.chiTietGioHangDTOS = chiTietGioHangDTOS;
        this.tongTien = tongTien;
    }

    public String getGioHangId() {
        return gioHangId;
    }

    public void setGioHangId(String gioHangId) {
        this.gioHangId = gioHangId;
    }

    public Integer getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(Integer khachHangId) {
        this.khachHangId = khachHangId;
    }

    public List<ChiTietGioHangDTO> getChiTietGioHangDTOS() {
        return chiTietGioHangDTOS;
    }

    public void setChiTietGioHangDTOS(List<ChiTietGioHangDTO> chiTietGioHangDTOS) {
        this.chiTietGioHangDTOS = chiTietGioHangDTOS;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }
}
