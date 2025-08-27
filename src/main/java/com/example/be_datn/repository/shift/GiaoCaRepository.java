package com.example.be_datn.repository.shift;

import com.example.be_datn.entity.giao_ca.GiaoCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiaoCaRepository extends JpaRepository<GiaoCa, Integer> {
    Optional<GiaoCa> findByidNhanVien_IdAndTrangThai(Integer nhanVienId, Short trangThai);
    Optional<GiaoCa> findTopByTrangThaiOrderByIdDesc(Short trangThai);
}
