package com.example.be_datn.service.shift;

import com.example.be_datn.dto.giao_ca.GiaoCaDTO;
import com.example.be_datn.entity.giao_ca.GiaoCa;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface GiaoCaService {
    GiaoCa startShift(Integer nhanVienId, BigDecimal tienMatBanDau);
    GiaoCaDTO endShift(Integer nhanVienId);
    ByteArrayInputStream generateExcelReport(Map<String, Object> reportData);
    Optional<GiaoCa> getActiveShift(Integer nhanVienId);
    long getPendingOrdersCount();
    Optional<BigDecimal> getLastEndedShiftCash();
}