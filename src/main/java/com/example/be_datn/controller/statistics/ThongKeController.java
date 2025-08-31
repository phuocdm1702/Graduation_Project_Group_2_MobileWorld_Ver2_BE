package com.example.be_datn.controller.statistics;

import com.example.be_datn.dto.statistics.respone.HangBanChayDTO;
import com.example.be_datn.dto.statistics.respone.SanPhamHetHangDTO;
import com.example.be_datn.dto.statistics.respone.SoLieuDTO;
import com.example.be_datn.dto.statistics.respone.TopSellingProductDTO;
import com.example.be_datn.service.statistics.ThongKeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/thongKe")
public class ThongKeController {
    @Autowired
    private ThongKeService sr;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> hienThi(
            @RequestParam(required = false, defaultValue = "month") String filterType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int sanPhamHetHangPage,
            @RequestParam(defaultValue = "8") int sanPhamHetHangSize) {
        Map<String, Object> response = new HashMap<>();

        // Validate filterType
        if (!Arrays.asList("day", "week", "month", "year", "custom").contains(filterType)) {
            throw new IllegalArgumentException("Invalid filterType: " + filterType);
        }

        // Dữ liệu thống kê hiện tại
        Map<String, Object> ngay = sr.getThongKeTheoNgay();
        Map<String, Object> tuan = sr.getThongKeTheoTuan();
        Map<String, Object> thang = sr.getThongKeTheoThang();
        Map<String, Object> nam = sr.getThongKeTheoNam();

        // Chuyển đổi số an toàn
        SoLieuDTO ngayDTO = new SoLieuDTO(
                (BigDecimal) ngay.get("doanhThu"),
                convertToLong(ngay.get("sanPhamDaBan")),
                convertToInt(ngay.get("tongSoDonHang"))
        );
        SoLieuDTO tuanDTO = new SoLieuDTO(
                (BigDecimal) tuan.get("doanhThu"),
                convertToLong(tuan.get("sanPhamDaBan")),
                convertToInt(tuan.get("tongSoDonHang"))
        );
        SoLieuDTO thangDTO = new SoLieuDTO(
                (BigDecimal) thang.get("doanhThu"),
                convertToLong(thang.get("sanPhamDaBan")),
                convertToInt(thang.get("tongSoDonHang"))
        );
        SoLieuDTO namDTO = new SoLieuDTO(
                (BigDecimal) nam.get("doanhThu"),
                convertToLong(nam.get("sanPhamDaBan")),
                convertToInt(nam.get("tongSoDonHang"))
        );

        response.put("ngay", Collections.singletonList(ngayDTO));
        response.put("tuan", Collections.singletonList(tuanDTO));
        response.put("thang", Collections.singletonList(thangDTO));
        response.put("nam", Collections.singletonList(namDTO));

        // Dữ liệu thống kê theo khoảng thời gian tùy chọn
        if ("custom".equals(filterType) && startDate != null && endDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                BigDecimal customRevenue = sr.doanhThuTheoKhoangThoiGian(start, end);
                SoLieuDTO customDTO = new SoLieuDTO(
                        customRevenue,
                        0L, // Không có dữ liệu sản phẩm đã bán
                        0 // Không có dữ liệu tổng số đơn hàng
                );
                response.put("custom", Collections.singletonList(customDTO));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format for startDate or endDate", e);
            }
        }

        // Dữ liệu thống kê hàng bán chạy
        response.put("hangBanChay", sr.thongKeHangBanChay());

        // Dữ liệu thống kê loại hóa đơn
        response.put("loaiHoaDon", sr.thongKeLoaiHoaDon());

        // Dữ liệu thống kê sản phẩm hết hàng
        Pageable sanPhamHetHangPageable = PageRequest.of(sanPhamHetHangPage, sanPhamHetHangSize);
        Page<SanPhamHetHangDTO> sanPhamHetHangPageData = sr.thongKeSanPhamHetHang(sanPhamHetHangPageable);
        response.put("sanPhamHetHang", sanPhamHetHangPageData.getContent());
        response.put("sanPhamHetHangTotalPages", sanPhamHetHangPageData.getTotalPages());
        response.put("sanPhamHetHangCurrentPage", sanPhamHetHangPageData.getNumber());

        // Dữ liệu thống kê sản phẩm bán chạy
        Page<TopSellingProductDTO> topProductsPage = sr.getTopSellingProducts(filterType, startDate, endDate, page, size);
        response.put("topProducts", topProductsPage.getContent());
        response.put("totalPages", topProductsPage.getTotalPages());
        response.put("currentPage", topProductsPage.getNumber());

        // Dữ liệu tăng trưởng
        Map<String, Object> growthData = sr.getGrowthData();
        response.put("growthData", growthData);

        return ResponseEntity.ok(response);
    }

    // Hàm hỗ trợ chuyển đổi sang Long
    private Long convertToLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        throw new IllegalArgumentException("Unsupported type for conversion to Long: " + value.getClass());
    }

    // Hàm hỗ trợ chuyển đổi sang Integer
    private Integer convertToInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        throw new IllegalArgumentException("Unsupported type for conversion to Integer: " + value.getClass());
    }


    @GetMapping("/revenue-chart")
    public ResponseEntity<Map<String, Object>> getRevenueChartData(
            @RequestParam(defaultValue = "month") String filterType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> response = new HashMap<>();
        List<BigDecimal> data = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        switch (filterType) {
            case "day":
                List<Map<String, Object>> dailyData = sr.thongKeDoanhThuTheoKhungGio(new Date());
                labels = Arrays.asList("0-6h", "6-9h", "9-12h", "12-15h", "15-18h", "18-24h");
                data = labels.stream().map(label -> {
                    Optional<Map<String, Object>> matchingData = dailyData.stream()
                            .filter(d -> label.equals(d.get("khungGio")))
                            .findFirst();
                    return matchingData.map(d -> (BigDecimal) d.get("doanhThu")).orElse(BigDecimal.ZERO);
                }).collect(Collectors.toList());
                break;
            case "week":
                Date startOfWeek;
                Date endOfWeek;
                if (startDate != null && endDate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        startOfWeek = sdf.parse(startDate);
                        cal.setTime(sdf.parse(endDate));
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        cal.set(Calendar.MILLISECOND, 999);
                        endOfWeek = cal.getTime();
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid date format for startDate or endDate", e);
                    }
                } else {
                    // Tính tuần hiện tại (Thứ Hai đến Chủ Nhật)
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    startOfWeek = cal.getTime();
                    cal.add(Calendar.DAY_OF_WEEK, 6);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    cal.set(Calendar.MILLISECOND, 999);
                    endOfWeek = cal.getTime();
                }
                List<Map<String, Object>> weeklyData = sr.thongKeDoanhThuTheoNgayTrongTuan(startOfWeek, endOfWeek);
                labels = Arrays.asList("T2", "T3", "T4", "T5", "T6", "T7", "CN");
                data = labels.stream().map(label -> {
                    Optional<Map<String, Object>> matchingData = weeklyData.stream()
                            .filter(d -> label.equals(d.get("ngayTrongTuan")))
                            .findFirst();
                    return matchingData.map(d -> (BigDecimal) d.get("doanhThu")).orElse(BigDecimal.ZERO);
                }).collect(Collectors.toList());
                break;
            case "month":
                Calendar calMonth = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                int thang = calMonth.get(Calendar.MONTH) + 1;
                int nam = calMonth.get(Calendar.YEAR);
                List<Map<String, Object>> monthlyData = sr.thongKeDoanhThuTheoTuanTrongThang(thang, nam);
                labels = Arrays.asList("Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5");
                data = labels.stream().map(label -> {
                    int weekNum = Integer.parseInt(label.split(" ")[1]);
                    Optional<Map<String, Object>> matchingData = monthlyData.stream()
                            .filter(d -> weekNum == ((Number) d.get("tuan")).intValue())
                            .findFirst();
                    return matchingData.map(d -> (BigDecimal) d.get("doanhThu")).orElse(BigDecimal.ZERO);
                }).collect(Collectors.toList());
                break;
            case "year":
                int year = cal.get(Calendar.YEAR); // Lấy năm từ cal đã khởi tạo
                List<Map<String, Object>> yearlyData = sr.thongKeDoanhThuTheoQuy(year);
                labels = Arrays.asList("Q1", "Q2", "Q3", "Q4");
                data = labels.stream().map(label -> {
                    Optional<Map<String, Object>> matchingData = yearlyData.stream()
                            .filter(d -> ("Q" + d.get("quy")).equals(label))
                            .findFirst();
                    return matchingData.map(d -> {
                        Object doanhThu = d.get("doanhThu");
                        if (doanhThu instanceof BigDecimal) {
                            return (BigDecimal) doanhThu;
                        } else if (doanhThu instanceof Number) {
                            return new BigDecimal(doanhThu.toString());
                        } else {
                            return BigDecimal.ZERO;
                        }
                    }).orElse(BigDecimal.ZERO);
                }).collect(Collectors.toList());
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start date and end date are required for custom filter");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date start = sdf.parse(startDate);
                    Date end = sdf.parse(endDate);
                    List<Map<String, Object>> customData = sr.thongKeDoanhThuTheoThangTrongKhoangThoiGian(start, end);
                    labels = customData.stream().map(d -> (String) d.get("thangNam")).collect(Collectors.toList());
                    data = customData.stream().map(d -> (BigDecimal) d.get("doanhThu")).collect(Collectors.toList());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format for startDate or endDate", e);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid filterType: " + filterType);
        }

        response.put("labels", labels);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/order-status-stats")
    public ResponseEntity<Map<String, Long>> getOrderStatusStats(
            @RequestParam(defaultValue = "month") String filterType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.util.Date date
    ) {
        // Kiểm tra filterType hợp lệ
        if (!Arrays.asList("day", "month", "year").contains(filterType)) {
            throw new IllegalArgumentException("Invalid filterType: " + filterType);
        }

        // Chuẩn hóa date theo múi giờ Asia/Ho_Chi_Minh

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        if (date == null) {
            date = new java.util.Date();
        }
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();

        Map<String, Long> statusStats = sr.getOrderStatusStats(filterType, date);
        System.out.println("Order status stats: " + statusStats);
        return ResponseEntity.ok(statusStats);
    }

    @GetMapping("/top-brands")
    public ResponseEntity<List<HangBanChayDTO>> getTopBrands() {
        List<HangBanChayDTO> topBrands = sr.thongKeHangBanChay();
        return ResponseEntity.ok(topBrands);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<Resource> exportExcel(
            @RequestParam(defaultValue = "year") String filterType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Map<String, Object> ngay = sr.getThongKeTheoNgay();
        Map<String, Object> tuan = sr.getThongKeTheoTuan();
        Map<String, Object> thang = sr.getThongKeTheoThang();
        Map<String, Object> nam = sr.getThongKeTheoNam();
        Map<String, Object> custom = new HashMap<>();
        if ("custom".equals(filterType) && startDate != null && endDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                custom.put("doanhThu", sr.doanhThuTheoKhoangThoiGian(start, end));
                custom.put("sanPhamDaBan", 0L);
                custom.put("tongSoDonHang", 0);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format for startDate or endDate", e);
            }
        }
        List<Map<String, Object>> topProducts = sr.getAllTopSellingProducts(filterType, startDate, endDate);
        Map<String, Long> orderStatusStats = sr.getOrderStatusStats(filterType, new Date());
        List<Map<String, Object>> loaiHoaDon = sr.getAllLoaiHoaDon();
        List<Map<String, Object>> sanPhamHetHang = sr.getAllSanPhamHetHang();

        // Tạo workbook Excel
        Workbook workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);

        // Sheet 1: Thống Kê Tổng Quan
        Sheet sheetTongQuan = workbook.createSheet("Thống Kê Tổng Quan");
        createHeaderRow(sheetTongQuan, new String[]{"Khoảng Thời Gian", "Doanh Thu (VND)", "Số Sản Phẩm Bán", "Tổng Số Đơn Hàng"}, headerStyle);
        addTongQuanRow(sheetTongQuan, 1, "Hôm Nay", ngay);
        addTongQuanRow(sheetTongQuan, 2, "Tuần Này", tuan);
        addTongQuanRow(sheetTongQuan, 3, "Tháng Này", thang);
        addTongQuanRow(sheetTongQuan, 4, "Năm Nay", nam);
        if ("custom".equals(filterType)) {
            addTongQuanRow(sheetTongQuan, 5, "Tùy Chọn", custom);
        }

        // Sheet 2: Sản Phẩm Bán Chạy
        Sheet sheetTopProducts = workbook.createSheet("Sản Phẩm Bán Chạy");
        createHeaderRow(sheetTopProducts, new String[]{"STT", "Tên Sản Phẩm", "Giá Bán (VND)", "Số Lượng Đã Bán"}, headerStyle);
        for (int i = 0; i < topProducts.size(); i++) {
            Map<String, Object> product = topProducts.get(i);
            Row row = sheetTopProducts.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(getStringValue(product.get("productName")));
            row.createCell(2).setCellValue(getBigDecimalValue(product.get("price")).doubleValue());
            row.createCell(3).setCellValue(getLongValue(product.get("soldQuantity")));
        }

        // Sheet 3: Trạng Thái Đơn Hàng
        Sheet sheetOrderStatus = workbook.createSheet("Trạng Thái Đơn Hàng");
        Row headerRowOrderStatus = sheetOrderStatus.createRow(0);
        String[] headersOrderStatus = {"Trạng Thái", "Số Lượng"};
        for (int i = 0; i < headersOrderStatus.length; i++) {
            Cell cell = headerRowOrderStatus.createCell(i);
            cell.setCellValue(headersOrderStatus[i]);
            cell.setCellStyle(headerStyle);
        }
        String[] statuses = {"Chờ xác nhận", "Chờ giao hàng", "Đang giao", "Hoàn thành", "Đã hủy"};
        for (int i = 0; i < statuses.length; i++) {
            Row row = sheetOrderStatus.createRow(i + 1);
            row.createCell(0).setCellValue(statuses[i]);
            row.createCell(1).setCellValue(orderStatusStats.getOrDefault(statuses[i], 0L));
        }

        // Sheet 4: Phân Phối Đa Kênh
        Sheet sheetLoaiHoaDon = workbook.createSheet("Phân Phối Đa Kênh");
        createHeaderRow(sheetLoaiHoaDon, new String[]{"Loại Hóa Đơn", "Số Lượng"}, headerStyle);
        for (int i = 0; i < loaiHoaDon.size(); i++) {
            Map<String, Object> loaiDon = loaiHoaDon.get(i);
            Row row = sheetLoaiHoaDon.createRow(i + 1);
            row.createCell(0).setCellValue(getStringValue(loaiDon.get("loaiDon")));
            row.createCell(1).setCellValue(getLongValue(loaiDon.get("soLuong")));
        }

        // Sheet 5: Sản Phẩm Sắp Hết Hàng
        Sheet sheetSanPhamHetHang = workbook.createSheet("Sản Phẩm Sắp Hết Hàng");
        createHeaderRow(sheetSanPhamHetHang, new String[]{"STT", "Tên Sản Phẩm", "Số Lượng"}, headerStyle);
        for (int i = 0; i < sanPhamHetHang.size(); i++) {
            Map<String, Object> product = sanPhamHetHang.get(i);
            Row row = sheetSanPhamHetHang.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(getStringValue(product.get("tenSanPham")));
            row.createCell(2).setCellValue(getLongValue(product.get("soLuong")));
        }

        // Tự động điều chỉnh kích thước cột
        for (Sheet sheet : workbook) {
            int columnCount = sheet.getRow(0) != null ? sheet.getRow(0).getPhysicalNumberOfCells() : 0;
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Xuất file Excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file Excel", e);
        }

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=thong_ke_day_du.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputStream.size())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Helper methods
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    private void createHeaderRow(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void addTongQuanRow(Sheet sheet, int rowNum, String period, Map<String, Object> data) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(period);
        row.createCell(1).setCellValue(getBigDecimalValue(data.get("doanhThu")).doubleValue());
        row.createCell(2).setCellValue(getLongValue(data.get("sanPhamDaBan")));
        row.createCell(3).setCellValue(getLongValue(data.get("tongSoDonHang")));
    }

    private String getStringValue(Object value) {
        return value != null ? value.toString() : "";
    }

    private BigDecimal getBigDecimalValue(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return BigDecimal.ZERO;
    }

    private long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

}
