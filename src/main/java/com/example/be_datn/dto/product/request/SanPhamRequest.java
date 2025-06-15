package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SanPhamRequest(
        @Size(max = 255, message = "Ma must not exceed 255 characters")
        String ma,

        @NotNull(message = "TenSanPham is required")
        @Size(max = 255, message = "TenSanPham must not exceed 255 characters")
        String tenSanPham,

        Integer idChiSoKhangBuiVaNuoc,

        @NotNull(message = "ID CongNgheMang is required")
        Integer idCongNgheMang,

        @NotNull(message = "ID Cpu is required")
        Integer idCpu,

        @NotNull(message = "ID CumCamera is required")
        Integer idCumCamera,

        @NotNull(message = "ID Gpu is required")
        Integer idGpu,

        @NotNull(message = "ID HeDieuHanh is required")
        Integer idHeDieuHanh,

        Integer idHoTroBoNhoNgoai,

        @NotNull(message = "ID NhaSanXuat is required")
        Integer idNhaSanXuat,

        @NotNull(message = "ID Pin is required")
        Integer idPin,

        @NotNull(message = "ID Sim is required")
        Integer idSim,

        @NotNull(message = "ID ThietKe is required")
        Integer idThietKe,

        Integer hoTroCongNgheSacId,

        Integer congNgheManHinhId
) {}