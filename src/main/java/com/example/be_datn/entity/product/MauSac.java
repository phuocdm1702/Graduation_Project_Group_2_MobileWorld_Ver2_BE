package com.example.be_datn.entity.product;

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
@Table(name = "mau_sac")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "mau_sac")
    private String mauSac;

    @Size(max = 20) // Giả sử mã màu là hex (7 ký tự: #RRGGBB)
    @Column(name = "ma_mau")
    private String maMau; // Thêm cột mã màu

    @Column(name = "deleted")
    private Boolean deleted;
}