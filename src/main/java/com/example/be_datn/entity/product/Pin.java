package com.example.be_datn.entity.product;

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
@Table(name = "pin")
public class Pin {
    @Id
    @Column(name = "id", nullable = false)
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