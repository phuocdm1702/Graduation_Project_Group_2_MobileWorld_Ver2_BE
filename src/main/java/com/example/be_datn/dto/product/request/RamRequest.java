package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamRequest {

    private String ma;

    @NotBlank(message = "Dung lượng RAM không được để trống")
    @Size(max = 255, message = "Dung lượng RAM không được vượt quá 255 ký tự")
    private String dungLuongRam;
}