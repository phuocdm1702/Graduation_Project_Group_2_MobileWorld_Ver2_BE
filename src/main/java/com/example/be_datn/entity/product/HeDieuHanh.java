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
@Table(name = "he_dieu_hanh")
public class HeDieuHanh {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "he_dieu_hanh", nullable = false)
    private String heDieuHanh;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "phien_ban", nullable = false)
    private String phienBan;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}