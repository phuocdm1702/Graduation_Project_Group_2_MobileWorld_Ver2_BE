package com.example.be_datn.entity.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dia_chi_khach_hang")
public class DiaChiKhachHang {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private KhachHang idKhachHang;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "thanh_pho", nullable = false)
    private String thanhPho;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "quan", nullable = false)
    private String quan;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "phuong", nullable = false)
    private String phuong;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "dia_chi_cu_the", nullable = false)
    private String diaChiCuThe;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "mac_dinh", nullable = false)
    private Boolean macDinh = false;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}