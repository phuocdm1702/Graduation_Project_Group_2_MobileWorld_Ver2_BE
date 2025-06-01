package com.example.be_datn.entity.account;

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
@Table(name = "quyen_han")
public class QuyenHan {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "cap_quyen_han")
    private Integer capQuyenHan;

    @Column(name = "deleted")
    private Boolean deleted;

}