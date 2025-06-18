package com.example.be_datn.entity.pay;

import com.example.be_datn.entity.order.HoaDon;
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
@Table(name = "hinh_thuc_thanh_toan")
public class HinhThucThanhToan {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_phuong_thuc_thanh_toan", nullable = false)
    private PhuongThucThanhToan idPhuongThucThanhToan;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "tien_chuyen_khoan", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienChuyenKhoan;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "tien_mat", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienMat;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}