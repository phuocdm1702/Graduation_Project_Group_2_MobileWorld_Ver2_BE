package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThietKeRequest {

    private String ma;

    @Size(max = 255, message = "Chất liệu khung không được vượt quá 255 ký tự")
    private String chatLieuKhung;

    @Size(max = 255, message = "Chất liệu mặt lưng không được vượt quá 255 ký tự")
    private String chatLieuMatLung;
}