package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CumCameraRequest {

    @NotBlank(message = "Mã cụm camera không được để trống")
    @Size(max = 255, message = "Mã cụm camera không được vượt quá 255 ký tự")
    private String ma;

    @Size(max = 255, message = "Thông số camera sau không được vượt quá 255 ký tự")
    private String thongSoCameraSau;

    @Size(max = 255, message = "Thông số camera trước không được vượt quá 255 ký tự")
    private String thongSoCameraTruoc;
}