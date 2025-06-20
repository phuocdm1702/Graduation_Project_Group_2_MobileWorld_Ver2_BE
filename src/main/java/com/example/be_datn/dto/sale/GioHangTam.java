package com.example.be_datn.dto.sale;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "gio_hang_tam")
public class GioHangTam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    @Size(max = 50)
    @NotNull
    @Column(name = "imei", nullable = false, length = 50)
    private String imei;

    @NotNull
    @Column(name = "chi_tiet_san_pham_id", nullable = false)
    private Integer chiTietSanPhamId;

    @Column(name = "id_phieu_giam_gia")
    private Integer idPhieuGiamGia;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}