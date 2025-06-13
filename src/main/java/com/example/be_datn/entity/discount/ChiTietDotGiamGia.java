package com.example.be_datn.entity.discount;

import com.example.be_datn.entity.product.ChiTietSanPham;
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
@Builder
@Table(name = "chi_tiet_dot_giam_gia")
public class ChiTietDotGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_dot_giam_gia", nullable = false)
    private DotGiamGia idDotGiamGia;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_chi_tiet_san_pham", nullable = false)
    private ChiTietSanPham idChiTietSanPham;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma", nullable = false)
    private String ma;

    @NotNull
    @Column(name = "gia_ban_dau", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBanDau;

    @NotNull
    @Column(name = "gia_sau_khi_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaSauKhiGiam;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public ChiTietDotGiamGia() {
    }

    public ChiTietDotGiamGia(Integer id, DotGiamGia idDotGiamGia, ChiTietSanPham idChiTietSanPham, String ma, BigDecimal giaBanDau, BigDecimal giaSauKhiGiam, Boolean deleted) {
        this.id = id;
        this.idDotGiamGia = idDotGiamGia;
        this.idChiTietSanPham = idChiTietSanPham;
        this.ma = ma;
        this.giaBanDau = giaBanDau;
        this.giaSauKhiGiam = giaSauKhiGiam;
        this.deleted = deleted;
    }
}