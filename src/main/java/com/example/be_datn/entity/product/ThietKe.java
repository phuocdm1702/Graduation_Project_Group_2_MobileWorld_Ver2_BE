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
@Table(name = "thiet_ke")
public class ThietKe {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @Nationalized
    @Column(name = "chat_lieu_khung")
    private String chatLieuKhung;

    @Size(max = 255)
    @Nationalized
    @Column(name = "chat_lieu_mat_lung")
    private String chatLieuMatLung;

    @Column(name = "deleted")
    private Boolean deleted;

}