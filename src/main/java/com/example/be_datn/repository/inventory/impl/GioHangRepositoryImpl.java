package com.example.be_datn.repository.inventory.impl;

import com.example.be_datn.entity.inventory.GioHang;
import com.example.be_datn.repository.inventory.GioHangCustomRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GioHangRepositoryImpl implements GioHangCustomRepository {
    private final EntityManager entityManager;

    public GioHangRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
