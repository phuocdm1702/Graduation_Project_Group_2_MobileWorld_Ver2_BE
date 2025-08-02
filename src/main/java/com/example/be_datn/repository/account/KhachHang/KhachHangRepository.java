package com.example.be_datn.repository.account.KhachHang;

import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.account.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang,Integer>,KhachHangCustomRepository {
    boolean existsByMa(String finalCode);

    @Query("SELECT MAX(CAST(SUBSTRING(n.ma, 5, LEN(n.ma) - 4) AS int)) FROM DiaChiKhachHang n WHERE LEN(n.ma) >= 5")
    Integer findMaxMa();

    Optional<KhachHang> findByMa(String ma);

    @Query("SELECT k FROM KhachHang k WHERE CONCAT(k.ma, k.ten) LIKE %?1% ")
    List<KhachHang> searchFormAdd(String keyword);

    Optional<KhachHang> findByTen(String ten);

    @Query("SELECT kh FROM KhachHang kh WHERE (:gioiTinh IS NULL OR kh.gioiTinh = :gioiTinh)")
    List<KhachHang> findByGioiTinh(@Param("gioiTinh") Boolean gioiTinh);

    KhachHang findByIdTaiKhoan_Id(Integer taiKhoanId);

    KhachHang findByIdTaiKhoan(TaiKhoan taiKhoan);
}
