
package com.example.be_datn.service.giao_ca;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Map;

public interface GiaoCaService {
    Map<String, Object> startShift(Integer nhanVienId);
    Map<String, Object> endShift(Integer nhanVienId, BigDecimal tienMatCuoiCa, BigDecimal tienMatBanDau);
    ByteArrayInputStream generateExcelReport(Map<String, Object> reportData);
}
