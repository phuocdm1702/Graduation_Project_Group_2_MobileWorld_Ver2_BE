package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoTroCongNgheSacRequest {

    @NotBlank(message = "Mã công nghệ sạc không được để trống")
    @Size(max = 255, message = "Mã công nghệ sạc không được vượt quá 255 ký tự")
    private String ma;

    @Size(max = 50, message = "Cổng sạc không được vượt quá 50 ký tự")
    private String congSac;

    private String congNgheHoTro;
}