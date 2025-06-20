package com.example.be_datn.config.Redis;

import com.example.be_datn.dto.sale.GioHangTam;
import com.example.be_datn.entity.order.HoaDon;
import com.example.be_datn.entity.product.Imel;
import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.repository.order.GioHangTamRepository;
import com.example.be_datn.repository.order.HoaDonRepository;
import com.example.be_datn.repository.product.ImelRepository;
import com.example.be_datn.repository.product.ImelDaBanRepository;
import com.example.be_datn.repository.product.ChiTietSanPhamRepository;
import com.example.be_datn.repository.discount.PhieuGiamGiaRepository;
import com.example.be_datn.repository.sale.saleDetail.ChiTietDotGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);
    private static final String GH_PREFIX = "gh:hd:";

    private final ImelRepository imelRepository;
    private final ImelDaBanRepository imelDaBanRepository;
    private final GioHangTamRepository gioHangTamRepository;
    private final ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final HoaDonRepository hoaDonRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    public RedisKeyExpirationListener(
            RedisMessageListenerContainer listenerContainer,
            ImelRepository imelRepository,
            ImelDaBanRepository imelDaBanRepository,
            GioHangTamRepository gioHangTamRepository,
            ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository,
            ChiTietSanPhamRepository chiTietSanPhamRepository,
            HoaDonRepository hoaDonRepository,
            PhieuGiamGiaRepository phieuGiamGiaRepository) {
        super(listenerContainer);
        this.imelRepository = imelRepository;
        this.imelDaBanRepository = imelDaBanRepository;
        this.gioHangTamRepository = gioHangTamRepository;
        this.chiTietDotGiamGiaRepository = chiTietDotGiamGiaRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
    }

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        logger.info("Key hết hạn: {}", expiredKey);

        if (expiredKey.startsWith(GH_PREFIX)) {
            try {
                Integer idHD = Integer.parseInt(expiredKey.replace(GH_PREFIX, ""));
                List<GioHangTam> gioHangTamList = gioHangTamRepository.findByIdHoaDonAndDeletedFalse(idHD);

                for (GioHangTam gioHangTam : gioHangTamList) {
                    String imei = gioHangTam.getImei();
                    Integer chiTietSanPhamId = gioHangTam.getChiTietSanPhamId();
                    Integer idPhieuGiamGia = gioHangTam.getIdPhieuGiamGia();

                    // Khôi phục Imel
                    Imel imel = imelRepository.findByImelAndDeleted(imei, true).orElse(null);
                    if (imel != null && !imelDaBanRepository.existsByMa(imei)) {
                        imel.setDeleted(false);
                        imelRepository.save(imel);
                        logger.info("Khôi phục Imel: {}", imei);
                    }

                    // Khôi phục ChiTietSanPham
                    ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietSanPhamId).orElse(null);
                    if (chiTietSanPham != null && chiTietSanPham.getDeleted()) {
                        chiTietSanPham.setDeleted(false);
                        chiTietSanPhamRepository.save(chiTietSanPham);
                        logger.info("Khôi phục ChiTietSanPham id: {}", chiTietSanPhamId);
                    }

                    // Cập nhật ChiTietDotGiamGia
                    ChiTietDotGiamGia chiTietDotGiamGia = chiTietDotGiamGiaRepository
                            .findByIdChiTietSanPham_IdAndDeletedFalse(chiTietSanPhamId).orElse(null);
                    if (chiTietDotGiamGia != null) {
                        chiTietDotGiamGia.setDeleted(true);
                        chiTietDotGiamGiaRepository.save(chiTietDotGiamGia);
                        logger.info("Cập nhật ChiTietDotGiamGia cho chiTietSanPhamId: {}", chiTietSanPhamId);
                    }

                    // Khôi phục PhieuGiamGia
                    if (idPhieuGiamGia != null) {
                        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia).orElse(null);
                        if (pgg != null) {
                            pgg.setSoLuongDung(pgg.getSoLuongDung() + 1);
                            phieuGiamGiaRepository.save(pgg);
                            logger.info("Khôi phục PhieuGiamGia id: {}", idPhieuGiamGia);
                        }
                    }
                }

                // Đánh dấu GioHangTam là deleted
                gioHangTamRepository.markAsDeletedByIdHoaDon(idHD);

                // Đánh dấu HoaDon là deleted nếu trangThai = 0
                HoaDon hoaDon = hoaDonRepository.findById(idHD).orElse(null);
                if (hoaDon != null && hoaDon.getTrangThai() == 0) {
                    hoaDon.setDeleted(true);
                    hoaDonRepository.save(hoaDon);
                    logger.info("Đánh dấu HoaDon id: {} là deleted", idHD);
                }
            } catch (Exception e) {
                logger.error("Lỗi khi xử lý key hết hạn {}: {}", expiredKey, e.getMessage());
                throw new RuntimeException("Lỗi xử lý key hết hạn: " + expiredKey, e);
            }
        }
    }
}