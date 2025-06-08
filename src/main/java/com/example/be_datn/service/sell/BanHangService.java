package com.example.be_datn.service.sell;

import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.entity.order.HoaDon;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BanHangService {
    List<HoaDon> getHDCho();

    void huyHDCho(Integer idHD) throws Exception;


    @Transactional
    HoaDonDTO taoHD(Integer khachHangId);

    GioHangDTO themVaoGH(Integer idHD, ChiTietGioHangDTO chiTietGioHangDTO);

    GioHangDTO layGioHang(Integer idHD);

    HoaDonDTO layChiTietHoaDonCho(Integer idHD);

    void xoaGioHang(Integer idHD);
}
