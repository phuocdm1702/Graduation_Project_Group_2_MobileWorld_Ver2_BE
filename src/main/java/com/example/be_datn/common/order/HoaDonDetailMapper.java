package com.example.be_datn.common.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.entity.order.HoaDonChiTiet;
import com.example.be_datn.entity.order.LichSuHoaDon;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HoaDonDetailMapper {
    HoaDonDetailMapper INSTANCE = Mappers.getMapper(HoaDonDetailMapper.class);

    @Mapping(source = "hoaDon.id", target = "idHoaDon") // Ánh xạ idHoaDon
    @Mapping(source = "idChiTietSanPham.idSanPham.ma", target = "maSanPham")
    @Mapping(source = "idChiTietSanPham.idSanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "idImelDaBan.imel", target = "imel")
    @Mapping(source = "gia", target = "giaBan")
    @Mapping(source = "ghiChu", target = "ghiChu")
    @Mapping(source = "idChiTietSanPham.idMauSac.mauSac", target = "mauSac") // Thêm ánh xạ cho màu sắc
    @Mapping(source = "idChiTietSanPham.idBoNhoTrong.dungLuongBoNhoTrong", target = "boNho") // Thêm ánh xạ cho bộ nhớ
    HoaDonDetailResponse.SanPhamChiTietInfo mapToSanPhamChiTietInfo(HoaDonChiTiet hoaDonChiTiet);

    @Mapping(source = "idPhuongThucThanhToan.ma", target = "maHinhThucThanhToan")
    @Mapping(source = "idPhuongThucThanhToan.kieuThanhToan", target = "kieuThanhToan")
    @Mapping(source = "tienChuyenKhoan", target = "tienChuyenKhoan")
    @Mapping(source = "tienMat", target = "tienMat")
    HoaDonDetailResponse.ThanhToanInfo mapToThanhToanInfo(HinhThucThanhToan hinhThucThanhToan);

    @Mapping(source = "ma", target = "ma")
    @Mapping(source = "hanhDong", target = "hanhDong")
    @Mapping(source = "thoiGian", target = "thoiGian")
    @Mapping(source = "idNhanVien.tenNhanVien", target = "tenNhanVien")
    HoaDonDetailResponse.LichSuHoaDonInfo mapToLichSuHoaDonInfo(LichSuHoaDon lichSuHoaDon);
}
