package com.example.be_datn.dto.statistics.respone;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TopSellingProductDTO {
    private String imageUrl;
    private String productName;
    private BigDecimal price;
    private int soldQuantity;
}
