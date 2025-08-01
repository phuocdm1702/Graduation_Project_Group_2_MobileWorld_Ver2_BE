package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSacRequest {

    private String ma;

    @NotBlank(message = "Tên màu sắc không được để trống")
    @Size(max = 255, message = "Tên màu sắc không được vượt quá 255 ký tự")
    private String mauSac;

    @Size(max = 20, message = "Mã màu không được vượt quá 20 ký tự") // Thêm trường mã màu
    private String maMau; // Có thể để rỗng hoặc null nếu không bắt buộc
}