package com.example.be_datn.entity.product;

import jakarta.persistence.*;
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
@Table(name = "anh_san_pham")
public class AnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @ColumnDefault("'ASP'+right('000000'+CONVERT([nvarchar](6), NEXT VALUE FOR [dbo].[ASPSeq]), 6)")
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ten_anh", nullable = false)
    private String tenAnh;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "duong_dan", nullable = false)
    private String duongDan;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Size(max = 255)
    @Column(name = "hash")
    private String hash;

    @Size(max = 255)
    @Column(name = "product_group_key")
    private String productGroupKey;

}