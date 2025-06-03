package com.example.be_datn.repository.discount;

import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {
}
