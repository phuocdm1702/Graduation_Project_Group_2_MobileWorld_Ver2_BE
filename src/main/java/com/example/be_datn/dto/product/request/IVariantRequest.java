package com.example.be_datn.dto.product.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public interface IVariantRequest {
    Integer getIdMauSac();
    Integer getIdRam();
    Integer getIdBoNhoTrong();
    BigDecimal getDonGia();
    List<String> getImeiList();
}