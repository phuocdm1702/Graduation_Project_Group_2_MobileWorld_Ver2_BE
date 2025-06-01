package com.example.be_datn.entity.guarantee;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lich_su_bao_hanh")
public class LichSuBaoHanh {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_bao_hanh")
    private PhieuBaoHanh idPhieuBaoHanh;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "mo_ta_loi")
    private String moTaLoi;

    @Size(max = 255)
    @Nationalized
    @Column(name = "phuong_thuc_sua_chua")
    private String phuongThucSuaChua;

    @Column(name = "chi_phi")
    private Double chiPhi;

    @Size(max = 255)
    @Nationalized
    @Column(name = "trang_thai")
    private String trangThai;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "deleted")
    private Boolean deleted;

}