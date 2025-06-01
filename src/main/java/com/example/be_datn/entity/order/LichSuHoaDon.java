package com.example.be_datn.entity.order;

import com.example.be_datn.entity.account.NhanVien;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lich_su_hoa_don")
public class LichSuHoaDon {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon idHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien idNhanVien;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "hanh_dong")
    private String hanhDong;

    @Column(name = "thoi_gian")
    private Instant thoiGian;

    @Column(name = "deleted")
    private Boolean deleted;

}