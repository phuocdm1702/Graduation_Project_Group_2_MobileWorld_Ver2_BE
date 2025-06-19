package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ImelDaBan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImelDaBanRepository extends JpaRepository<ImelDaBan, Integer> {
    boolean existsByMa(String maImel);
}
