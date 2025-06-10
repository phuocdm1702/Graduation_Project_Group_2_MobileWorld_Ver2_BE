package com.example.be_datn.common.order;

import com.example.be_datn.dto.order.response.HoaDonResponse;
import com.example.be_datn.entity.order.HoaDon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HoaDonMapper {
    HoaDonMapper INSTANCE = Mappers.getMapper(HoaDonMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "ma", target = "ma")
    @Mapping(source = "tenKhachHang", target = "tenKhachHang")
    @Mapping(source = "soDienThoaiKhachHang", target = "soDienThoaiKhachHang")
    @Mapping(source = "tongTienSauGiam", target = "tongTienSauGiam")
    @Mapping(source = "phiVanChuyen", target = "phiVanChuyen")
    @Mapping(source = "ngayTao", target = "ngayTao")
    @Mapping(source = "loaiDon", target = "loaiDon")
    @Mapping(source = "trangThai", target = "trangThai")
    @Mapping(source = "deleted", target = "deleted")
    HoaDonResponse mapToDto(HoaDon hoaDon);

//    @Named("formatDate")
//    default String formatDate(java.util.Date date) {
//        if (date == null) return null;
//        return new java.text.SimpleDateFormat("dd-MM-yyyy").format(date);
//    }

}
