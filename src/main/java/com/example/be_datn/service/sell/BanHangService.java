package com.example.be_datn.service.sell;

import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.entity.order.HoaDon;

import java.util.List;

public interface BanHangService {
    List<HoaDon> getHDCho();

    void huyHDCho(Integer idHD) throws Exception;

    HoaDonDTO taoHD();
}
