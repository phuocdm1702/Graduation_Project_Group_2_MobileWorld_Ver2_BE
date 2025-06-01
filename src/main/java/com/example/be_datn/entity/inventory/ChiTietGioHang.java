package com.example.be_datn.entity.inventory;

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
@Table(name = "chi_tiet_gio_hang")
public class ChiTietGioHang {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_gio_hang", nullable = false)
    private GioHang idGioHang;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_san_pham", nullable = false)
    private ChiTietSanPham idChiTietSanPham;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @ColumnDefault("0")
    @Column(name = "trang_thai", columnDefinition = "tinyint not null")
    private Short trangThai;

    @NotNull
    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}