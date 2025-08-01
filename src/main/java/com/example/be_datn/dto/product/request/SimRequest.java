package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimRequest {

    private String ma;

    @NotNull(message = "Số lượng SIM hỗ trợ không được để trống")
    @Min(value = 1, message = "Số lượng SIM hỗ trợ phải lớn hơn hoặc bằng 1")
    private Integer soLuongSimHoTro;

    @Size(max = 255, message = "Các loại SIM hỗ trợ không được vượt quá 255 ký tự")
    private String cacLoaiSimHoTro;
}