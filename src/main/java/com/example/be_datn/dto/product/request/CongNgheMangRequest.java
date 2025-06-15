package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongNgheMangRequest {

    @NotBlank(message = "Mã công nghệ mạng không được để trống")
    @Size(max = 255, message = "Mã công nghệ mạng không được vượt quá 255 ký tự")
    private String ma;

    @NotBlank(message = "Tên công nghệ mạng không được để trống")
    @Size(max = 255, message = "Tên công nghệ mạng không được vượt quá 255 ký tự")
    private String tenCongNgheMang;
}