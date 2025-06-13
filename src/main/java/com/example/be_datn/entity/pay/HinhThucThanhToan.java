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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_hoa_don",referencedColumnName = "id")
    private HoaDon hoaDon;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_phuong_thuc_thanh_toan", nullable = false)
    private PhuongThucThanhToan idPhuongThucThanhToan;

    @NotNull
    @Column(name = "tien_chuyen_khoan", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienChuyenKhoan;

    @NotNull
    @Column(name = "tien_mat", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienMat;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ma", nullable = false)
    private String ma;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}