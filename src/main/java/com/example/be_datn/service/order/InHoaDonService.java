package com.example.be_datn.service.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonPDFResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InHoaDonService {
    private static final Logger logger = LoggerFactory.getLogger(InHoaDonService.class);

    public byte[] generateHoaDonPdf(HoaDonDetailResponse hoaDon) throws Exception {
        logger.info("Starting to generate PDF for HoaDon: {}", hoaDon.getId());
        if (hoaDon == null) {
            logger.error("HoaDonDetailResponse is null");
            throw new IllegalArgumentException("Hóa đơn không được null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Chọn template JRXML dựa trên loaiDon
            String reportFileName = "hoa_don.jrxml"; // Mặc định cho trực tiếp
            if (hoaDon.getLoaiDon() != null && hoaDon.getLoaiDon().equalsIgnoreCase("online")) {
                reportFileName = "hoa_don_client.jrxml"; // Cho online
            }
            ClassPathResource reportResource = new ClassPathResource("reports/" + reportFileName);
            if (!reportResource.exists()) {
                logger.error("File {} không tồn tại trong thư mục resources/reports", reportFileName);
                throw new Exception("File " + reportFileName + " không tồn tại trong thư mục resources/reports");
            }

            try (InputStream reportStream = reportResource.getInputStream()) {
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("maHoaDon", hoaDon.getMaHoaDon() != null ? hoaDon.getMaHoaDon() : "N/A");
                parameters.put("tenKhachHang", hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : "N/A");
                parameters.put("tenNhanVien", hoaDon.getTenNhanVien() != null ? hoaDon.getTenNhanVien() : "N/A");

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                parameters.put("ngayBan", hoaDon.getNgayTao() != null ? dateFormat.format(hoaDon.getNgayTao()) : "N/A");
                parameters.put("soDienThoai", hoaDon.getSoDienThoaiKhachHang() != null ? hoaDon.getSoDienThoaiKhachHang() : "N/A");
                parameters.put("diaChi", hoaDon.getDiaChiKhachHang() != null ? hoaDon.getDiaChiKhachHang() : "N/A");

                // Xử lý hình thức thanh toán
                String hinhThucThanhToanStr = "N/A";
                if (hoaDon.getThanhToanInfos() != null && !hoaDon.getThanhToanInfos().isEmpty()) {
                    List<String> paymentMethods = new ArrayList<>();
                    for (HoaDonDetailResponse.ThanhToanInfo thanhToan : hoaDon.getThanhToanInfos()) {
                        String method = thanhToan.getKieuThanhToan() != null ? thanhToan.getKieuThanhToan() : "Không xác định";
                        paymentMethods.add(method);
                    }
                    hinhThucThanhToanStr = String.join(", ", paymentMethods);
                }
                parameters.put("hinhThucThanhToan", hinhThucThanhToanStr);

                // Tính tổng tiền từ chi tiết sản phẩm
                BigDecimal tongTien = BigDecimal.ZERO;
                if (hoaDon.getSanPhamChiTietInfos() != null) {
                    tongTien = hoaDon.getSanPhamChiTietInfos().stream()
                            .map(HoaDonDetailResponse.SanPhamChiTietInfo::getGiaBan)
                            .filter(gia -> gia != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                parameters.put("tongTien", tongTien);
                parameters.put("tongTienSauGiam", hoaDon.getTongTienSauGiam() != null ? hoaDon.getTongTienSauGiam() : BigDecimal.ZERO);
                parameters.put("tienGiam", hoaDon.getTienGiam() != null ? hoaDon.getTienGiam() : BigDecimal.ZERO);
                parameters.put("phanTramGiam", hoaDon.getPhanTramGiam() != null ? hoaDon.getPhanTramGiam() : 0.0);

                // Tạo mã QR với logo
                BufferedImage logoQrImage = generateQRWithLogo(hoaDon.getMaHoaDon());
                parameters.put("logoQrImage", logoQrImage);

                // Chuẩn bị danh sách chi tiết sản phẩm cho JasperReports
                List<HoaDonChiTietDTO> chiTietListWithStt = new ArrayList<>();
                if (hoaDon.getSanPhamChiTietInfos() != null && !hoaDon.getSanPhamChiTietInfos().isEmpty()) {
                    int stt = 1;
                    for (HoaDonDetailResponse.SanPhamChiTietInfo sp : hoaDon.getSanPhamChiTietInfos()) {
                        HoaDonChiTietDTO chiTiet = new HoaDonChiTietDTO();
                        chiTiet.setStt(stt++);
                        chiTiet.setTenSanPham(sp.getTenSanPham() != null ? sp.getTenSanPham() : "N/A");
                        chiTiet.setImel(sp.getImel() != null ? sp.getImel() : "N/A");
                        chiTiet.setMauSac(sp.getMauSac() != null ? sp.getMauSac() : "N/A");
                        chiTiet.setBoNho(sp.getDungLuongBoNhoTrong() != null ? sp.getDungLuongBoNhoTrong() : "N/A");
                        chiTiet.setGia(sp.getGiaBan() != null ? sp.getGiaBan() : BigDecimal.ZERO);
                        chiTietListWithStt.add(chiTiet);
                    }
                } else {
                    logger.warn("Danh sách chi tiết hóa đơn trống cho HoaDon: {}", hoaDon.getId());
                }

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(chiTietListWithStt);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

                logger.info("PDF generated successfully for HoaDon: {}", hoaDon.getId());
                return baos.toByteArray();
            }

        } catch (JRException e) {
            logger.error("Lỗi khi tạo PDF với JasperReports: {}", e.getMessage(), e);
            throw new Exception("Không thể tạo PDF: " + e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                logger.error("Lỗi khi đóng ByteArrayOutputStream: {}", e.getMessage());
            }
        }
    }

    private BufferedImage generateQRWithLogo(String maHoaDon) throws Exception {
        String qrCodeText = maHoaDon != null ? maHoaDon : "N/A";
        int qrSize = 250;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 4);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);

        int logoSize = qrSize / 4;
        int padding = 1;
        int whiteAreaSize = logoSize + padding * 2;
        int whiteAreaXOffset = (qrSize - whiteAreaSize) / 2;
        int whiteAreaYOffset = (qrSize - whiteAreaSize) / 2;

        for (int x = 0; x < qrSize; x++) {
            for (int y = 0; y < qrSize; y++) {
                int centerX = whiteAreaXOffset + whiteAreaSize / 2;
                int centerY = whiteAreaYOffset + whiteAreaSize / 2;
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= whiteAreaSize / 2) {
                    bitMatrix.unset(x, y);
                }
            }
        }

        BufferedImage qrImage = new BufferedImage(qrSize, qrSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dQr = qrImage.createGraphics();
        g2dQr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int x = 0; x < qrSize; x++) {
            for (int y = 0; y < qrSize; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
        BufferedImage logoImage = ImageIO.read(logoResource.getInputStream());

        BufferedImage circularLogo = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dCircular = circularLogo.createGraphics();
        g2dCircular.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dCircular.setColor(Color.BLACK);
        g2dCircular.fillOval(0, 0, logoSize, logoSize);

        int logoInnerSize = (int) (logoSize * 0.9);
        int logoInnerOffset = (logoSize - logoInnerSize) / 2;
        g2dCircular.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2dCircular.drawImage(logoImage, logoInnerOffset, logoInnerOffset, logoInnerSize, logoInnerSize, null);
        g2dCircular.dispose();

        int xOffset = (qrSize - logoSize) / 2;
        int yOffset = (qrSize - logoSize) / 2;
        g2dQr.drawImage(circularLogo, xOffset, yOffset, null);
        g2dQr.dispose();

        logger.info("Generated QR code with logo for maHoaDon: {}", qrCodeText);
        return qrImage;
    }

    // DTO tạm thời cho JasperReports
    public static class HoaDonChiTietDTO {
        private Integer stt;
        private String tenSanPham;
        private String imel;
        private String mauSac;
        private String boNho;
        private BigDecimal gia;

        // Getter và Setter
        public Integer getStt() { return stt; }
        public void setStt(Integer stt) { this.stt = stt; }
        public String getTenSanPham() { return tenSanPham; }
        public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
        public String getImel() { return imel; }
        public void setImel(String imel) { this.imel = imel; }
        public String getMauSac() { return mauSac; }
        public void setMauSac(String mauSac) { this.mauSac = mauSac; }
        public String getBoNho() { return boNho; }
        public void setBoNho(String boNho) { this.boNho = boNho; }
        public BigDecimal getGia() { return gia; }
        public void setGia(BigDecimal gia) { this.gia = gia; }
    }
}
