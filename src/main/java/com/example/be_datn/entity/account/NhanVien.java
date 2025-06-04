package com.example.be_datn.entity.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan idTaiKhoan;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_nhan_vien")
    private String tenNhanVien;

    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Size(max = 255)
    @Nationalized
    @Column(name = "anh_nhan_vien")
    private String anhNhanVien;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Size(max = 255)
    @Nationalized
    @Column(name = "thanh_pho")
    private String thanhPho;

    @Size(max = 255)
    @Nationalized
    @Column(name = "quan")
    private String quan;

    @Size(max = 255)
    @Nationalized
    @Column(name = "phuong")
    private String phuong;

    @Size(max = 255)
    @Nationalized
    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;

    @Size(max = 255)
    @Nationalized
    @Column(name = "cccd")
    private String cccd;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

}