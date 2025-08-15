package com.example.be_datn.service.discount.impl;

import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.repository.discount.PhieuGiamGiaCaNhanRepository;
import com.example.be_datn.service.discount.PhieuGiamGiaCaNhanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PhieuGiamGiaCaNhanServiceImpl implements PhieuGiamGiaCaNhanService {

    @Autowired
    private PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;

    public PhieuGiamGiaCaNhanServiceImpl(PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository) {
        this.phieuGiamGiaCaNhanRepository = phieuGiamGiaCaNhanRepository;
    }

    @Override
    public List<PhieuGiamGiaCaNhan> getPGGCN() {
        return phieuGiamGiaCaNhanRepository.findAll();
    }

    @Override
    @Transactional
    public PhieuGiamGiaCaNhan addPGGCN(PhieuGiamGiaCaNhan phieuGiamGiaCaNhan) {
        return phieuGiamGiaCaNhanRepository.save(phieuGiamGiaCaNhan);
    }

    @Override
    @Transactional
    public void deleteByPhieuGiamGiaId(Integer phieuGiamGiaId) {
        phieuGiamGiaCaNhanRepository.deleteByIdPhieuGiamGia(phieuGiamGiaId);
    }

    @Override
    public List<PhieuGiamGiaCaNhan> findByPhieuGiamGiaId(Integer pggId) {
        return phieuGiamGiaCaNhanRepository.findByIdPhieuGiamGia_Id(pggId);
    }

    @Override
    public List<PhieuGiamGiaCaNhan> getall() {
        return phieuGiamGiaCaNhanRepository.findAll();
    }

    @Override
    public Optional<PhieuGiamGiaCaNhan> checkDiscountCode(String ma) {
        Optional<PhieuGiamGiaCaNhan> optional = phieuGiamGiaCaNhanRepository.findByMa(ma);
        boolean isValid = false;
        PhieuGiamGiaCaNhan pgg = null;

        if (optional.isPresent()) {
            pgg = optional.get();
            if (pgg.getTrangThai() && pgg.getIdPhieuGiamGia().getTrangThai() &&
                    pgg.getNgayHetHan().after(new Date())) {
                isValid = true;
            }
        }

        return isValid ? optional : Optional.empty();
    }
}