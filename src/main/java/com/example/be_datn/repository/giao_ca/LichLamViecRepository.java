package com.example.be_datn.repository.giao_ca;

import com.example.be_datn.entity.giao_ca.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {
    List<LichLamViec> findByDeletedFalse();
}
