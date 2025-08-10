package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongNgheManHinhRequest {

    private String ma;

    @NotBlank(message = "Tên công nghệ màn hình không được để trống")
    @Size(max = 255, message = "Tên công nghệ màn hình không được vượt quá 255 ký tự")
    private String congNgheManHinh;

    @Size(max = 255, message = "Chuẩn màn hình không được vượt quá 255 ký tự")
    private String chuanManHinh;

    @Size(max = 50, message = "Kích thước không được vượt quá 50 ký tự")
    private String kichThuoc;

    @Size(max = 50, message = "Độ phân giải không được vượt quá 50 ký tự")
    private String doPhanGiai;

    @Size(max = 50, message = "Độ sáng tối đa không được vượt quá 50 ký tự")
    private String doSangToiDa;

    @Size(max = 50, message = "Tần số quét không được vượt quá 50 ký tự")
    private String tanSoQuet;

    @Size(max = 50, message = "Kiểu màn hình không được vượt quá 50 ký tự")
    private String kieuManHinh;
}