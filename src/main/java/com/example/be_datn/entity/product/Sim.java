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
@Table(name = "sim")
public class Sim {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "so_luong_sim_ho_tro")
    private Integer soLuongSimHoTro;

    @Size(max = 255)
    @Nationalized
    @Column(name = "cac_loai_sim_ho_tro")
    private String cacLoaiSimHoTro;

    @Column(name = "deleted")
    private Boolean deleted;

}