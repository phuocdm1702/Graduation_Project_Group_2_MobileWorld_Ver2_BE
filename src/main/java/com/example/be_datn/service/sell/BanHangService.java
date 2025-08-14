package com.example.be_datn.service.sell;

import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.product.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface BanHangService {
    List<HoaDon> getHDCho();

    void huyHD(Integer idHD);

    @Transactional

    GioHangDTO themVaoGH(Integer idHD, ChiTietGioHangDTO chiTietGioHangDTO);

    GioHangDTO xoaSanPhamKhoiGioHang(Integer idHD, Integer spId, String maImel);

    GioHangDTO layGioHang(Integer idHD);

    HoaDonDTO layChiTietHoaDonCho(Integer idHD);

    void xoaGioHang(Integer idHD);

    @Transactional
    HoaDonDTO thanhToan(Integer idHD, HoaDonRequest hoaDonRequest);

    Page<ChiTietSanPhamGroupDTO> getAllCTSP(int page, int size, String keyword);

    List<String> getIMEIsBySanPhamIdAndAttributes(Integer sanPhamId, String mauSac, String dungLuongRam, String dungLuongBoNhoTrong);

    ChiTietSanPham getChiTietSanPhamByIMEI(String imei);

    List<PhieuGiamGiaCaNhan> findByKhachHangId(Integer idKhachHang);

    Integer getChiTietSanPhamId(Integer sanPhamId, String mauSac, String dungLuongRam, String dungLuongBoNhoTrong);

    // Thêm phương thức mới để lấy 1 hóa đơn cụ thể
    HoaDonDTO getSingleHoaDon(Integer idHD);

    Map<String, Object> findProductByBarcodeOrImei(String code);

    HoaDonDTO taoHD(Integer idKhachHangToUse, Integer idNhanVienToUse);
}
