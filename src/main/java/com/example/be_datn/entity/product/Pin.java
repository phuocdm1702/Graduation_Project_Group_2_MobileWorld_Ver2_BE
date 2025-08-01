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
@Table(name = "pin")
public class Pin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "loai_pin")
    private String loaiPin;

    @Size(max = 255)
    @Nationalized
    @Column(name = "dung_luong_pin")
    private String dungLuongPin;

    @Column(name = "deleted")
    private Boolean deleted;

}