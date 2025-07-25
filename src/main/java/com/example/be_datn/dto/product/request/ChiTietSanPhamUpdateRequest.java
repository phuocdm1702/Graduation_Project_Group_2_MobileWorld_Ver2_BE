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
public class ChiTietSanPhamUpdateRequest {
    @NotNull(message = "ID không được để trống")
    private Integer id;

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

        @NotEmpty(message = "Danh sách IMEI không được để trống")
        private List<String> imeiList;
    }
}