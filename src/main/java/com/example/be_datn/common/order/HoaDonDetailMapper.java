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

    @Mapping(source = "idChiTietSanPham.id", target = "chiTietSanPhamId") // Ánh xạ ID của ChiTietSanPham
    @Mapping(source = "hoaDon.id", target = "idHoaDon")
    @Mapping(source = "idChiTietSanPham.ma", target = "maHinhSanPhamChiTiet") // Ánh xạ ma của ChiTietSanPham
    @Mapping(source = "idChiTietSanPham.idSanPham.ma", target = "maSanPham")
    @Mapping(source = "idChiTietSanPham.idSanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "idImelDaBan.imel", target = "imel")
    @Mapping(source = "gia", target = "giaBan")
    @Mapping(source = "ghiChu", target = "ghiChu")
    @Mapping(source = "idChiTietSanPham.idMauSac.mauSac", target = "mauSac")
    @Mapping(source = "idChiTietSanPham.idBoNhoTrong.dungLuongBoNhoTrong", target = "dungLuongBoNhoTrong")
    @Mapping(source = "idChiTietSanPham.idRam.dungLuongRam", target = "dungLuongRam")
    @Mapping(source = "idChiTietSanPham.idAnhSanPham.duongDan", target = "duongDan") // ánh xạ đường dẫn của ảnh SP
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
