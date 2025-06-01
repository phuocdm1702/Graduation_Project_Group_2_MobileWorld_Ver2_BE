package com.example.be_datn.entity.discount;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chi_tiet_dot_giam_gia")
public class ChiTietDotGiamGia {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dot_giam_gia", nullable = false)
    private DotGiamGia idDotGiamGia;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_san_pham", nullable = false)
    private ChiTietSanPham idChiTietSanPham;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @NotNull
    @Column(name = "gia_ban_dau", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBanDau;

    @NotNull
    @Column(name = "gia_sau_khi_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaSauKhiGiam;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}