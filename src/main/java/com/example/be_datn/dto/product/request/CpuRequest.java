package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CpuRequest {

    private String ma;

    @NotBlank(message = "Tên CPU không được để trống")
    @Size(max = 255, message = "Tên CPU không được vượt quá 255 ký tự")
    private String tenCpu;

    @NotNull(message = "Số nhân không được để trống")
    @Min(value = 1, message = "Số nhân phải lớn hơn hoặc bằng 1")
    private Integer soNhan;
}