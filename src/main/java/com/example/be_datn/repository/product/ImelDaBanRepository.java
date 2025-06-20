package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.ImelDaBan;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ImelDaBanRepository extends JpaRepository<ImelDaBan, Integer> {
    boolean existsByMa(String maImel);
    Page<ImelDaBan> findByDeletedFalse(Pageable pageable);

    @Query("SELECT MAX(i.ma) FROM ImelDaBan i WHERE i.ma LIKE 'IMDB%'")
    String findMaxMa();
}

