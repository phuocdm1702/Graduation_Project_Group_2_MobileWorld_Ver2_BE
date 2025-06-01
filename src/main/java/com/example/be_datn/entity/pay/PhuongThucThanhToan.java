package com.example.be_datn.entity.pay;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "phuong_thuc_thanh_toan")
public class PhuongThucThanhToan {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "kieu_thanh_toan")
    private String kieuThanhToan;

    @Column(name = "deleted")
    private Boolean deleted;

}