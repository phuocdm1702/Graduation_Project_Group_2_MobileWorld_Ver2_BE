package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiSoKhangBuiVaNuocRequest {

    private String ma;

    @NotBlank(message = "Tên chỉ số không được để trống")
    @Size(max = 255, message = "Tên chỉ số không được vượt quá 255 ký tự")
    private String tenChiSo;
}