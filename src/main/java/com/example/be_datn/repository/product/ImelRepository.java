package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Imel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImelRepository extends JpaRepository<Imel, Integer> {

    Optional<Imel> findByImelAndDeleted(String imel, boolean deleted);

}
