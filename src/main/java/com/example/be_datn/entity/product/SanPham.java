package com.example.be_datn.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "san_pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ten_san_pham", nullable = false)
    private String tenSanPham;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_so_khang_bui_va_nuoc", referencedColumnName = "id")
    private ChiSoKhangBuiVaNuoc idChiSoKhangBuiVaNuoc;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cong_nghe_mang", referencedColumnName = "id")
    private CongNgheMang idCongNgheMang;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cpu", referencedColumnName = "id")
    private Cpu idCpu;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cum_camera", referencedColumnName = "id")
    private CumCamera idCumCamera;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_gpu", nullable = false, referencedColumnName = "id")
    private Gpu idGpu;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_he_dieu_hanh", referencedColumnName = "id")
    private HeDieuHanh idHeDieuHanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ho_tro_bo_nho_ngoai", referencedColumnName = "id")
    private HoTroBoNhoNgoai idHoTroBoNhoNgoai;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nha_san_xuat", referencedColumnName = "id")
    private NhaSanXuat idNhaSanXuat;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pin", referencedColumnName = "id")
    private Pin idPin;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sim", referencedColumnName = "id")
    private Sim idSim;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_thiet_ke", referencedColumnName = "id")
    private ThietKe idThietKe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_tro_cong_nghe_sac_id", referencedColumnName = "id")
    private HoTroCongNgheSac hoTroCongNgheSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_nghe_man_hinh_id", referencedColumnName = "id")
    private CongNgheManHinh congNgheManHinh;

    @OneToMany(mappedBy = "idSanPham")
    @JsonBackReference // Ngăn serialize danh sách ChiTietSanPham
    private Collection<ChiTietSanPham> chiTietSanPhams;
}