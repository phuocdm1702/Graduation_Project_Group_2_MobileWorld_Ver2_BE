package com.example.be_datn.entity.order;

import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.account.TaiKhoan;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.pay.HinhThucThanhToan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hoa_don")
public class HoaDon {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private KhachHang idKhachHang;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nhan_vien", nullable = false)
    private NhanVien idNhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", referencedColumnName = "id")
    private PhieuGiamGia idPhieuGiamGia;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @NotNull
    @Column(name = "tien_san_pham", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienSanPham;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "loai_don", nullable = false)
    private String loaiDon;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "phi_van_chuyen", nullable = false, precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_tien", precision = 38, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", precision = 38, scale = 2)
    private BigDecimal tongTienSauGiam;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ten_khach_hang", nullable = false)
    private String tenKhachHang;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "dia_chi_khach_hang", nullable = false)
    private String diaChiKhachHang;

    @Size(max = 255)
    @Column(name = "so_dien_thoai_khach_hang")
    private String soDienThoaiKhachHang;

    @Size(max = 255)
    @Nationalized
    @Column(name = "email")
    private String email;

    @NotNull
    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao", nullable = false)
    private Date ngayTao;

    @Column(name = "ngay_thanh_toan")
    private Instant ngayThanhToan;

    @ColumnDefault("1")
    @Column(name = "trang_thai", columnDefinition = "tinyint not null")
    private Short trangThai;

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

    @OneToMany(mappedBy = "hoaDon")
    private List<LichSuHoaDon> lichSuHoaDon;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private Set<HoaDonChiTiet> chiTietHoaDon = new HashSet<>();

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private Set<HinhThucThanhToan> hinhThucThanhToan = new HashSet<>();
}