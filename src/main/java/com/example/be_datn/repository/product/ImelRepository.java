package com.example.be_datn.repository.product;

import com.example.be_datn.entity.product.Imel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImelRepository extends JpaRepository<Imel, Integer> {

    // Check IMEL exists (không bao gồm deleted records)
    @Query("SELECT COUNT(i) > 0 FROM Imel i WHERE i.imel = :imel AND i.deleted = false")
    boolean existsByImelAndDeletedFalse(@Param("imel") String imel);

    // Check IMEL exists (bao gồm cả deleted records)
    @Query("SELECT COUNT(i) > 0 FROM Imel i WHERE i.imel = :imel")
    boolean existsByImel(@Param("imel") String imel);

    // Existing methods
    Optional<Imel> findByImelAndDeleted(String imel, boolean deleted);

    Optional<Imel> findByIdAndDeletedFalse(Integer id);

    Optional<Imel> findByImel(String imel);
}