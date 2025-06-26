package com.example.be_datn.dto.home;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MucDoTangTruongDTO {
    private Double current;
    private Double previous;
    private Double growthRate;
}
