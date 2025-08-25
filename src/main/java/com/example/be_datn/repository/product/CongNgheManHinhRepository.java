package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.CongNgheManHinh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CongNgheManHinhRepository extends JpaRepository<CongNgheManHinh, Integer> {

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.deleted = false ORDER BY c.id DESC")
    List<CongNgheManHinh> findByDeletedFalseOrderByIdDesc();

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.deleted = false ORDER BY c.id DESC")
    Page<CongNgheManHinh> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Optional<CongNgheManHinh> findByIdAndDeletedFalse(Integer id);

    @Query("SELECT c FROM CongNgheManHinh c WHERE c.deleted = false AND " +
            "(LOWER(c.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.congNgheManHinh) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY c.id DESC")
    Page<CongNgheManHinh> searchByKeywordOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByCongNgheManHinhAndChuanManHinhAndKichThuocAndDoPhanGiaiAndDoSangToiDaAndTanSoQuetAndKieuManHinhAndDeletedFalse(
            String congNgheManHinh,
            String chuanManHinh,
            String kichThuoc,
            String doPhanGiai,
            String doSangToiDa,
            String tanSoQuet,
            String kieuManHinh);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CongNgheManHinh c " +
            "WHERE c.congNgheManHinh = :congNgheManHinh " +
            "AND c.chuanManHinh = :chuanManHinh " +
            "AND c.kichThuoc = :kichThuoc " +
            "AND c.doPhanGiai = :doPhanGiai " +
            "AND c.doSangToiDa = :doSangToiDa " +
            "AND c.tanSoQuet = :tanSoQuet " +
            "AND c.kieuManHinh = :kieuManHinh " +
            "AND c.deleted = false " +
            "AND c.id != :excludeId")
    boolean existsByAllFieldsAndDeletedFalseAndIdNot(
            @Param("congNgheManHinh") String congNgheManHinh,
            @Param("chuanManHinh") String chuanManHinh,
            @Param("kichThuoc") String kichThuoc,
            @Param("doPhanGiai") String doPhanGiai,
            @Param("doSangToiDa") String doSangToiDa,
            @Param("tanSoQuet") String tanSoQuet,
            @Param("kieuManHinh") String kieuManHinh,
            @Param("excludeId") Integer excludeId);

    Optional<CongNgheManHinh> findByCongNgheManHinhAndChuanManHinhAndKichThuocAndDoPhanGiaiAndDoSangToiDaAndTanSoQuetAndKieuManHinhAndDeletedTrue(
            String congNgheManHinh,
            String chuanManHinh,
            String kichThuoc,
            String doPhanGiai,
            String doSangToiDa,
            String tanSoQuet,
            String kieuManHinh);
}