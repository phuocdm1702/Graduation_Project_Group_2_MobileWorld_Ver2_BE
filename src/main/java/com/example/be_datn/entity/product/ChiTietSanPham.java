package com.example.be_datn.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chi_tiet_san_pham")
public class ChiTietSanPham {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_imel", nullable = false)
    private Imel idImel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham idSanPham;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_anh_san_pham", nullable = false)
    private AnhSanPham idAnhSanPham;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mau_sac", nullable = false)
    private MauSac idMauSac;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ram", nullable = false)
    private Ram idRam;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_bo_nho_trong", nullable = false)
    private BoNhoTrong idBoNhoTrong;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @NotNull
    @Column(name = "gia_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

}