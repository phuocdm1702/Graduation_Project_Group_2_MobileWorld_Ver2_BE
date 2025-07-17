package com.example.be_datn.service.clientService;
import com.example.be_datn.dto.order.request.HoaDonRequest;
import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.sell.request.ChiTietGioHangDTO;
import com.example.be_datn.dto.sell.request.GioHangDTO;
import com.example.be_datn.dto.sell.request.HoaDonDTO;
import com.example.be_datn.dto.sell.response.ChiTietSanPhamGroupDTO;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
public interface BanHangClientService {
    HoaDonDetailResponse taoHoaDonCho(Integer khachHangId); // Sử dụng HoaDonDetailResponse
    GioHangDTO themSanPhamVaoGioHang(Integer idHD, ChiTietGioHangDTO chiTietGioHangDTO);
    GioHangDTO layGioHang(Integer idHD);
    GioHangDTO xoaSanPhamKhoiGioHang(Integer idHD, Integer spId, String maImel);
    HoaDonDetailResponse thanhToan(Integer idHD, HoaDonRequest hoaDonRequest); // Sử dụng HoaDonDetailResponse
    Page<ChiTietSanPhamGroupDTO> layDanhSachSanPham(int page, int size, String keyword);
    List<PhieuGiamGiaCaNhan> layPhieuGiamGiaCaNhan(Integer idKhachHang);
    Map<String, Object> timSanPhamTheoBarcodeHoacImei(String code);
    KhachHang layThongTinKhachHang(Integer idKhachHang);
    void guiEmailThongTinDonHang(HoaDonDetailResponse hoaDonDetailResponse, String email);
    void xoaHoaDonCho(Integer idHD); // Thêm phương thức mới
}
