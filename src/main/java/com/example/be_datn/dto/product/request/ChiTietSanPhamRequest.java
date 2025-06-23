package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamRequest {
    private Integer id; // Add ID field for updating

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @NotNull(message = "Nhà sản xuất không được để trống")
    private Integer idNhaSanXuat;

    @NotNull(message = "Pin không được để trống")
    private Integer idPin;

    @NotNull(message = "Công nghệ màn hình không được để trống")
    private Integer idCongNgheManHinh;

    private Integer idHoTroBoNhoNgoai;

    @NotNull(message = "CPU không được để trống")
    private Integer idCpu;

    @NotNull(message = "GPU không được để trống")
    private Integer idGpu;

    @NotNull(message = "Cụm camera không được để trống")
    private Integer idCumCamera;

    @NotNull(message = "Hệ điều hành không được để trống")
    private Integer idHeDieuHanh;

    @NotNull(message = "Chỉ số kháng bụi nước không được để trống")
    private Integer idChiSoKhangBuiVaNuoc;

    @NotNull(message = "Thiết kế không được để trống")
    private Integer idThietKe;

    @NotNull(message = "SIM không được để trống")
    private Integer idSim;

    @NotNull(message = "Hỗ trợ công nghệ sạc không được để trống")
    private Integer idHoTroCongNgheSac;

    @NotNull(message = "Công nghệ mạng không được để trống")
    private Integer idCongNgheMang;

    private String ghiChu;

    @NotEmpty(message = "Danh sách biến thể không được để trống")
    private List<VariantRequest> variants;

    private List<String> imageUrls;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantRequest {
        @NotNull(message = "Màu sắc không được để trống")
        private Integer idMauSac;

        @NotNull(message = "RAM không được để trống")
        private Integer idRam;

        @NotNull(message = "Bộ nhớ trong không được để trống")
        private Integer idBoNhoTrong;

        @NotNull(message = "Đơn giá không được để trống")
        @DecimalMin(value = "0.01", message = "Đơn giá phải lớn hơn 0")
        private BigDecimal donGia;

        private List<String> imeiList;
    }
}