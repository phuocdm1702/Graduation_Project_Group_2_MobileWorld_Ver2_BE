package com.example.be_datn.entity.account;

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
@Table(name = "tai_khoan")
public class TaiKhoan {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quyen_han")
    private QuyenHan idQuyenHan;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ten_dang_nhap")
    private String tenDangNhap;

    @Size(max = 255)
    @Nationalized
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Nationalized
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Size(max = 255)
    @Nationalized
    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "deleted")
    private Boolean deleted;

}