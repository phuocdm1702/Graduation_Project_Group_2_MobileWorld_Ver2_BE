package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoTroBoNhoNgoaiRequest {

    private String ma;

    @NotBlank(message = "Hỗ trợ bộ nhớ ngoài không được để trống")
    @Size(max = 255, message = "Hỗ trợ bộ nhớ ngoài không được vượt quá 255 ký tự")
    private String hoTroBoNhoNgoai;
}