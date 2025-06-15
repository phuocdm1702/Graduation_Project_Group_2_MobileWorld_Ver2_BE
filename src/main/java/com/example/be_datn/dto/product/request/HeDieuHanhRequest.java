package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeDieuHanhRequest {

    @NotBlank(message = "Mã hệ điều hành không được để trống")
    @Size(max = 255, message = "Mã hệ điều hành không được vượt quá 255 ký tự")
    private String ma;

    @NotBlank(message = "Tên hệ điều hành không được để trống")
    @Size(max = 255, message = "Tên hệ điều hành không được vượt quá 255 ký tự")
    private String heDieuHanh;

    @NotBlank(message = "Phiên bản không được để trống")
    @Size(max = 255, message = "Phiên bản không được vượt quá 255 ký tự")
    private String phienBan;
}