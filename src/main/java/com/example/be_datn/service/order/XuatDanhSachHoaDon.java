package com.example.be_datn.service.order;

import com.example.be_datn.dto.order.response.HoaDonDetailResponse;
import com.example.be_datn.dto.order.response.HoaDonResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class XuatDanhSachHoaDon {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet1; // Sheet cho danh sách hóa đơn
    private List<HoaDonResponse> listHD;
    private List<HoaDonDetailResponse.SanPhamChiTietInfo> listCT;
    private List<HoaDonDetailResponse.LichSuHoaDonInfo> listLSHD; // Danh sách lịch sử hóa đơn
    private Map<Integer, String> hoaDonToDetailSheetMap; // Map cho sheet chi tiết
    private Map<Integer, String> hoaDonToHistorySheetMap; // Map cho sheet lịch sử

    public XuatDanhSachHoaDon(XSSFWorkbook workbook, XSSFSheet sheet1, List<HoaDonResponse> listHD,
                              List<HoaDonDetailResponse.SanPhamChiTietInfo> listCT,
                              List<HoaDonDetailResponse.LichSuHoaDonInfo> listLSHD) {
        this.workbook = workbook;
        this.sheet1 = sheet1;
        this.listHD = listHD;
        this.listCT = listCT != null ? listCT : new ArrayList<>();
        this.listLSHD = listLSHD != null ? listLSHD : new ArrayList<>();
        this.hoaDonToDetailSheetMap = new HashMap<>();
        this.hoaDonToHistorySheetMap = new HashMap<>();
    }

    // Ghi tiêu đề cho sheet1 (danh sách hóa đơn)
    private void writeHeaderRow1() {
        Row row = sheet1.createRow(0);
        String[] headers = {"ID", "Mã", "Mã Nhân viên", "Tên Khách hàng", "SĐT", "Tổng tiền sau giảm", "Phí vận chuyển",
                "Ngày tạo", "Loại đơn", "Trạng thái", "Đã xóa", "Lịch sử"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    // Ghi tiêu đề cho sheet chi tiết hóa đơn
    private void writeHeaderRowForDetailSheet(XSSFSheet detailSheet) {
        Row row = detailSheet.createRow(0);
        String[] headers = {"Mã sản phẩm", "Tên sản phẩm", "Imel", "Giá bán", "Ghi chú", "Màu sắc", "Bộ nhớ"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    // Ghi tiêu đề cho sheet lịch sử hóa đơn
    private void writeHeaderRowForHistorySheet(XSSFSheet historySheet) {
        Row row = historySheet.createRow(0);
        String[] headers = {"Mã", "Hành động", "Thời gian", "Tên Nhân viên"}; // Sửa "Mã Nhân viên" thành "Tên Nhân viên"
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    // Ghi dữ liệu cho sheet1 (danh sách hóa đơn) và thêm hyperlink
    private void writeDataRow() {
        int rowNum = 1;

        // Tạo hyperlink style
        CellStyle hyperlinkStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        hyperlinkStyle.setFont(font);

        for (HoaDonResponse dto : listHD) {
            Row row = sheet1.createRow(rowNum++);

            // Cột ID: Hyperlink đến sheet chi tiết
            Cell idCell = row.createCell(0);
            idCell.setCellValue(dto.getId());
            if (hoaDonToDetailSheetMap.containsKey(dto.getId())) {
                String sheetName = hoaDonToDetailSheetMap.get(dto.getId());
                String address = "'" + sheetName + "'!A1";
                Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
                hyperlink.setAddress(address);
                idCell.setHyperlink(hyperlink);
                idCell.setCellStyle(hyperlinkStyle);
            }

            // Các cột dữ liệu khác
            row.createCell(1).setCellValue(dto.getMa());
            row.createCell(2).setCellValue(dto.getMaNhanVien() != null ? dto.getMaNhanVien() : "N/A");
            row.createCell(3).setCellValue(dto.getTenKhachHang() != null ? dto.getTenKhachHang() : "N/A");
            row.createCell(4).setCellValue(dto.getSoDienThoaiKhachHang());
            row.createCell(5).setCellValue(dto.getTongTienSauGiam() != null ? dto.getTongTienSauGiam().doubleValue() : 0.0);
            row.createCell(6).setCellValue(dto.getPhiVanChuyen() != null ? dto.getPhiVanChuyen().doubleValue() : 0.0);
            row.createCell(7).setCellValue(dto.getNgayTao() != null ? dto.getNgayTao().toString() : "N/A");
            row.createCell(8).setCellValue(dto.getLoaiDon());
            row.createCell(9).setCellValue(dto.getTrangThai());
            row.createCell(10).setCellValue(dto.getDeleted() != null ? dto.getDeleted().toString() : "N/A");

            // Cột Lịch sử: Hyperlink đến sheet lịch sử
            Cell historyCell = row.createCell(11);
            historyCell.setCellValue("Xem lịch sử");
            if (hoaDonToHistorySheetMap.containsKey(dto.getId())) {
                String sheetName = hoaDonToHistorySheetMap.get(dto.getId());
                String address = "'" + sheetName + "'!A1";
                Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
                hyperlink.setAddress(address);
                historyCell.setHyperlink(hyperlink);
                historyCell.setCellStyle(hyperlinkStyle);
            }
        }
    }

    // Ghi dữ liệu cho các sheet chi tiết và lịch sử hóa đơn
    private void writeDetailAndHistorySheets() {
        // Nhóm chi tiết hóa đơn theo idHoaDon
        Map<Integer, List<HoaDonDetailResponse.SanPhamChiTietInfo>> chiTietByHoaDon = listCT.stream()
                .filter(dto -> dto.getIdHoaDon() != null) // Lọc bỏ các dto có idHoaDon null
                .collect(Collectors.groupingBy(HoaDonDetailResponse.SanPhamChiTietInfo::getIdHoaDon));

        // Nhóm lịch sử hóa đơn theo idHoaDon
        Map<Integer, List<HoaDonDetailResponse.LichSuHoaDonInfo>> lichSuByHoaDon = listLSHD.stream()
                .filter(dto -> dto.getIdHoaDon() != null) // Lọc bỏ các dto có idHoaDon null
                .collect(Collectors.groupingBy(HoaDonDetailResponse.LichSuHoaDonInfo::getIdHoaDon));

        // Tạo sheet chi tiết cho từng hóa đơn
        for (Map.Entry<Integer, List<HoaDonDetailResponse.SanPhamChiTietInfo>> entry : chiTietByHoaDon.entrySet()) {
            Integer hoaDonId = entry.getKey();
            List<HoaDonDetailResponse.SanPhamChiTietInfo> chiTietList = entry.getValue();

            // Tạo sheet chi tiết
            String detailSheetName = "CTHD_" + hoaDonId; // Rút gọn tên sheet
            XSSFSheet detailSheet = workbook.createSheet(detailSheetName);
            hoaDonToDetailSheetMap.put(hoaDonId, detailSheetName);

            // Ghi tiêu đề cho sheet chi tiết
            writeHeaderRowForDetailSheet(detailSheet);

            // Ghi dữ liệu chi tiết hóa đơn
            int rowNum = 1;
            for (HoaDonDetailResponse.SanPhamChiTietInfo dtoHDCT : chiTietList) {
                Row row = detailSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dtoHDCT.getMaSanPham() != null ? dtoHDCT.getMaSanPham() : "N/A");
                row.createCell(1).setCellValue(dtoHDCT.getTenSanPham() != null ? dtoHDCT.getTenSanPham() : "N/A");
                row.createCell(2).setCellValue(dtoHDCT.getImel() != null ? dtoHDCT.getImel() : "N/A");
                row.createCell(3).setCellValue(dtoHDCT.getGiaBan() != null ? dtoHDCT.getGiaBan().doubleValue() : 0.0);
                row.createCell(4).setCellValue(dtoHDCT.getGhiChu() != null ? dtoHDCT.getGhiChu() : "N/A");
                row.createCell(5).setCellValue(dtoHDCT.getMauSac() != null ? dtoHDCT.getMauSac() : "N/A");
                row.createCell(6).setCellValue(dtoHDCT.getBoNho() != null ? dtoHDCT.getBoNho() : "N/A");
            }
        }

        // Tạo sheet lịch sử cho từng hóa đơn
        for (Map.Entry<Integer, List<HoaDonDetailResponse.LichSuHoaDonInfo>> entry : lichSuByHoaDon.entrySet()) {
            Integer hoaDonId = entry.getKey();
            List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuList = entry.getValue();

            // Tạo sheet lịch sử
            String historySheetName = "LSHD_" + hoaDonId; // Rút gọn tên sheet
            XSSFSheet historySheet = workbook.createSheet(historySheetName);
            hoaDonToHistorySheetMap.put(hoaDonId, historySheetName);

            // Ghi tiêu đề cho sheet lịch sử
            writeHeaderRowForHistorySheet(historySheet);

            // Ghi dữ liệu lịch sử hóa đơn
            int rowNum = 1;
            for (HoaDonDetailResponse.LichSuHoaDonInfo dtoLSHD : lichSuList) {
                Row row = historySheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dtoLSHD.getMa() != null ? dtoLSHD.getMa() : "N/A");
                row.createCell(1).setCellValue(dtoLSHD.getHanhDong() != null ? dtoLSHD.getHanhDong() : "N/A");
                row.createCell(2).setCellValue(dtoLSHD.getThoiGian() != null ? dtoLSHD.getThoiGian().toString() : "N/A");
                row.createCell(3).setCellValue(dtoLSHD.getTenNhanVien() != null ? dtoLSHD.getTenNhanVien() : "N/A");
            }
        }
    }

    // Phương thức export: Ghi file Excel trực tiếp vào response
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow1();
        writeDetailAndHistorySheets(); // Tạo các sheet chi tiết và lịch sử trước
        writeDataRow(); // Ghi sheet1 sau để thêm hyperlink

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
