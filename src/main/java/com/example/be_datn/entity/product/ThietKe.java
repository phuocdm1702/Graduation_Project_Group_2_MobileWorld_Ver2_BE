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
@Table(name = "thiet_ke")
public class ThietKe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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