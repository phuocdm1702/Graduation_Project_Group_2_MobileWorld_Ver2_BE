package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SanPhamRequest(
        @NotBlank(message = "Mã sản phẩm không được để trống")
        String ma,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        String tenSanPham,

        @NotNull(message = "ID nhà sản xuất là bắt buộc")
        Integer idNhaSanXuat,

        @NotNull(message = "ID hệ điều hành là bắt buộc")
        Integer idHeDieuHanh,

        @NotNull(message = "ID công nghệ màn hình là bắt buộc")
        Integer congNgheManHinhId,

        @NotNull(message = "ID pin là bắt buộc")
        Integer idPin,

        @NotNull(message = "ID CPU là bắt buộc")
        Integer idCpu,

        @NotNull(message = "ID GPU là bắt buộc")
        Integer idGpu,

        @NotNull(message = "ID cụm camera là bắt buộc")
        Integer idCumCamera,

        @NotNull(message = "ID thiết kế là bắt buộc")
        Integer idThietKe,

        @NotNull(message = "ID SIM là bắt buộc")
        Integer idSim,

        @NotNull(message = "ID công nghệ mạng là bắt buộc")
        Integer idCongNgheMang,

        Integer idChiSoKhangBuiVaNuoc, // Optional
        Integer idHoTroBoNhoNgoai, // Optional
        Integer hoTroCongNgheSacId // Optional
) {}