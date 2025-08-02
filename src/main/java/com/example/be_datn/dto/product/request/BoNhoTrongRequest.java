package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoNhoTrongRequest {

    private String ma;

    @NotBlank(message = "Dung lượng bộ nhớ trong không được để trống")
    @Size(max = 255, message = "Dung lượng bộ nhớ trong không được vượt quá 255 ký tự")
    private String dungLuongBoNhoTrong;
}