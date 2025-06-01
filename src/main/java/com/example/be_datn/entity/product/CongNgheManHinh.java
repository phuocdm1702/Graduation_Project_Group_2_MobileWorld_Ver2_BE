package com.example.be_datn.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "cong_nghe_man_hinh")
public class CongNgheManHinh {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ma", nullable = false)
    private String ma;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "cong_nghe_man_hinh", nullable = false)
    private String congNgheManHinh;

    @Size(max = 255)
    @Nationalized
    @Column(name = "chuan_man_hinh")
    private String chuanManHinh;

    @Size(max = 50)
    @Nationalized
    @Column(name = "kich_thuoc", length = 50)
    private String kichThuoc;

    @Size(max = 50)
    @Nationalized
    @Column(name = "do_phan_giai", length = 50)
    private String doPhanGiai;

    @Size(max = 50)
    @Nationalized
    @Column(name = "do_sang_toi_da", length = 50)
    private String doSangToiDa;

    @Size(max = 50)
    @Nationalized
    @Column(name = "tan_so_quet", length = 50)
    private String tanSoQuet;

    @Size(max = 50)
    @Nationalized
    @Column(name = "kieu_man_hinh", length = 50)
    private String kieuManHinh;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}