package com.example.be_datn.repository.order.impl;

import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.repository.order.HoaDonCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;


@Repository
public class HoaDonRepositoryImpl implements HoaDonCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<HoaDon> getHoaDon(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageableSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        String jpql = "SELECT h FROM HoaDon h ORDER BY h.id DESC"; // Thêm ORDER BY
        TypedQuery<HoaDon> query = entityManager.createQuery(jpql, HoaDon.class);
        query.setFirstResult((int) pageableSort.getOffset());
        query.setMaxResults(pageableSort.getPageSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(h) FROM HoaDon h", Long.class);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(query.getResultList(), pageableSort, total);
    }
}
