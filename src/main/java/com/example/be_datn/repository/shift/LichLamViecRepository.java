package com.example.be_datn.repository.shift;

import com.example.be_datn.entity.giao_ca.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;



public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {
    List<LichLamViec> findByDeletedFalse();

    @Query("SELECT l FROM LichLamViec l WHERE (:idNhanVien IS NULL OR l.idNhanVien.id = :idNhanVien) AND (:ngayLam IS NULL OR l.ngayLam = :ngayLam) AND l.deleted = false")
    List<LichLamViec> findWithFilters(@Param("idNhanVien") Integer idNhanVien, @Param("ngayLam") LocalDate ngayLam);
}
