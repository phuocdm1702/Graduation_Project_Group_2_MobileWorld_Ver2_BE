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
@Table(name = "bo_nho_trong")
public class BoNhoTrong {
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
    @Column(name = "dung_luong_bo_nho_trong", nullable = false)
    private String dungLuongBoNhoTrong;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}