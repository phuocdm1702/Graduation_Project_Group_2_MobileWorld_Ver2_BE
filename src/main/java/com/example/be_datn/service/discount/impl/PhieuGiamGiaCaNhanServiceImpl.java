package com.example.be_datn.service.discount.impl;

import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.repository.discount.PhieuGiamGiaCaNhanRepository;
import com.example.be_datn.service.discount.PhieuGiamGiaCaNhanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PhieuGiamGiaCaNhanServiceImpl implements PhieuGiamGiaCaNhanService {

    @Autowired
    private PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
        List<PhieuGiamGiaCaNhan> result = phieuGiamGiaCaNhanRepository.findAll();

        // Gửi realtime update cho danh sách tất cả phiếu giảm giá cá nhân
        sendAllPhieuGiamGiaCaNhanUpdate(result);

        return result;
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

        // Gửi realtime update cho việc kiểm tra mã giảm giá
        sendDiscountCodeCheckUpdate(ma, pgg, isValid);

        return isValid ? optional : Optional.empty();
    }

    // WebSocket methods
    private void sendAllPhieuGiamGiaCaNhanUpdate(List<PhieuGiamGiaCaNhan> phieuGiamGias) {
        try {
            Map<String, Object> allPggCaNhanUpdate = new HashMap<>();
            allPggCaNhanUpdate.put("action", "GET_ALL_PGG_CA_NHAN");
            allPggCaNhanUpdate.put("phieuGiamGias", phieuGiamGias);
            allPggCaNhanUpdate.put("count", phieuGiamGias.size());
            allPggCaNhanUpdate.put("timestamp", Instant.now());
            messagingTemplate.convertAndSend("/topic/all-pgg-ca-nhan", allPggCaNhanUpdate);
            System.out.println("Đã gửi danh sách tất cả phiếu giảm giá cá nhân qua WebSocket: " + phieuGiamGias.size() + " phiếu");
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi danh sách tất cả phiếu giảm giá cá nhân qua WebSocket: " + e.getMessage());
        }
    }

    private void sendDiscountCodeCheckUpdate(String ma, PhieuGiamGiaCaNhan phieuGiamGia, boolean isValid) {
        try {
            Map<String, Object> discountCheckUpdate = new HashMap<>();
            discountCheckUpdate.put("action", "CHECK_DISCOUNT_CODE");
            discountCheckUpdate.put("ma", ma);
            discountCheckUpdate.put("isValid", isValid);
            discountCheckUpdate.put("phieuGiamGia", phieuGiamGia);
            discountCheckUpdate.put("message", isValid ? "Mã giảm giá hợp lệ" : "Mã giảm giá không hợp lệ hoặc đã hết hạn");
            discountCheckUpdate.put("timestamp", Instant.now());
            messagingTemplate.convertAndSend("/topic/discount-code-check", discountCheckUpdate);
            System.out.println("Đã gửi kết quả kiểm tra mã giảm giá qua WebSocket: " + ma + " - " + (isValid ? "Hợp lệ" : "Không hợp lệ"));
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi kết quả kiểm tra mã giảm giá qua WebSocket: " + e.getMessage());
        }
    }
}