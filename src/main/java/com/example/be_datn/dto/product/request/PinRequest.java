package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinRequest {

    private String ma;

    @NotBlank(message = "Loại pin không được để trống")
    @Size(max = 255, message = "Loại pin không được vượt quá 255 ký tự")
    private String loaiPin;

    @Size(max = 255, message = "Dung lượng pin không được vượt quá 255 ký tự")
    private String dungLuongPin;
}