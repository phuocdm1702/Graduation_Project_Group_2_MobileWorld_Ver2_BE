package com.example.be_datn.service.giao_ca;

import com.example.be_datn.entity.giao_ca.GiaoCa;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface GiaoCaService {
    GiaoCa startShift(Integer nhanVienId, BigDecimal tienMatBanDau);
    GiaoCa endShift(Integer nhanVienId, BigDecimal tienMatCuoiCa);
    ByteArrayInputStream generateExcelReport(Map<String, Object> reportData);
    Optional<GiaoCa> getActiveShift(Integer nhanVienId);
    long getPendingOrdersCount();
    Optional<BigDecimal> getLastEndedShiftCash();
}