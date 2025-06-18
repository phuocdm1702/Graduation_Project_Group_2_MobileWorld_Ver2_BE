package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpuRequest {

    @NotBlank(message = "Mã GPU không được để trống")
    @Size(max = 255, message = "Mã GPU không được vượt quá 255 ký tự")
    private String ma;

    @NotBlank(message = "Tên GPU không được để trống")
    @Size(max = 255, message = "Tên GPU không được vượt quá 255 ký tự")
    private String tenGpu;
}