package com.example.be_datn.controller.sale;

import com.example.be_datn.dto.sale.request.AddDotGiamGiaDTO;
import com.example.be_datn.dto.sale.respone.CombinedResponse;
import com.example.be_datn.dto.sale.respone.RequestDTO;
import com.example.be_datn.dto.sale.respone.ViewCTSPDTO;
import com.example.be_datn.dto.sale.respone.ViewSanPhamDTO;
import com.example.be_datn.entity.discount.ChiTietDotGiamGia;
import com.example.be_datn.entity.discount.DotGiamGia;
import com.example.be_datn.entity.product.HeDieuHanh;
import com.example.be_datn.entity.product.NhaSanXuat;
import com.example.be_datn.service.sale.DotGiamGiaExporter;
import com.example.be_datn.service.sale.DotGiamGiaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173/")
@RestController
@RequestMapping("/api/dotGiamGia")
public class DotGiamGiaController {
    @Autowired
    private DotGiamGiaService sr;

    @GetMapping("/dotGiamGia")
    public Page<DotGiamGia> hienThi(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    Model model) {
        Pageable pageable = PageRequest.of(page, size);
        return sr.HienThi(pageable);
    }

    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Sửa Content-Type
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=dotGiamGia.xlsx";
        response.setHeader(headerKey, headerValue);

        List<DotGiamGia> listDGG = sr.forExcel();
        List<ChiTietDotGiamGia> listCT = sr.ForExcelCTDGG();
        DotGiamGiaExporter excelExporter = new DotGiamGiaExporter(listDGG,listCT);
        excelExporter.export(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DotGiamGia>> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String maDGG,
            @RequestParam(required = false) String tenDGG,
            @RequestParam(required = false) String loaiGiamGiaApDung,
            @RequestParam(required = false) BigDecimal giaTriGiamGia,
            @RequestParam(required = false) BigDecimal soTienGiamToiDa,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(required = false) Boolean deleted
    ) {
        System.out.println("Search params: maDGG=" + maDGG + ", tenDGG=" + tenDGG +
                ", loaiGiamGiaApDung=" + loaiGiamGiaApDung + ", giaTriGiamGia=" + giaTriGiamGia +
                ", soTienGiamToiDa=" + soTienGiamToiDa + ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc + ", trangThai=" + trangThai + ", deleted=" + deleted);
        Pageable pageable = PageRequest.of(page, size);
        Date sqlNgayBatDau = ngayBatDau != null ? java.sql.Date.valueOf(ngayBatDau) : null;
        Date sqlNgayKetThuc = ngayKetThuc != null ? java.sql.Date.valueOf(ngayKetThuc) : null;

        Page<DotGiamGia> result = sr.timKiem(
                pageable, maDGG, tenDGG, loaiGiamGiaApDung, giaTriGiamGia, soTienGiamToiDa, sqlNgayBatDau, sqlNgayKetThuc, trangThai, deleted);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/showFinish")
    public Page<DotGiamGia> showFinish(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sr.hienThiFinish(pageable);
    }

    @PostMapping("/form")
    public ResponseEntity<CombinedResponse> hienThiAdd(
            @RequestBody RequestDTO request,
            @RequestParam(defaultValue = "0") int pageDSP,
            @RequestParam(defaultValue = "5") int sizeDSP,
            @RequestParam(defaultValue = "0") int pageCTSP,
            @RequestParam(defaultValue = "5") int sizeCTSP) {

        System.out.println("Nhận được RequestDTO: " + request);
        System.out.println("idDSPs: " + request.getIdDSPs());
        System.out.println("mauSac: " + request.getMauSac());
        System.out.println("idBoNhoTrongs: " + request.getIdBoNhoTrongs());
        System.out.println("idHeDieuHanh: " + request.getIdHeDieuHanh());
        System.out.println("idNhaSanXuat: " + request.getIdNhaSanXuat());

        String keyword = request.getKeyword();
        List<Integer> idDSPs = request.getIdDSPs();
        List<Integer> idBoNhoTrongs = request.getIdBoNhoTrongs();
        List<Integer> idMauSac = request.getMauSac();
        List<Integer> idHeDieuHanh = request.getIdHeDieuHanh();
        List<Integer> idNhaSanXuat = request.getIdNhaSanXuat();

        List<ViewSanPhamDTO> dspList = sr.getDSP(keyword, idHeDieuHanh, idNhaSanXuat);

        Pageable pageableCTSP = PageRequest.of(pageCTSP, sizeCTSP);
        List<ViewCTSPDTO> ctspList = idDSPs != null && !idDSPs.isEmpty()
                ? sr.getAllCTSP(idDSPs, idBoNhoTrongs, idMauSac)
                : Collections.emptyList();

        List<HeDieuHanh> heDieuHanhList = sr.getAllHeDieuHanh();
        List<NhaSanXuat> nhaSanXuatList = sr.getAllNhaSanXuat();

        CombinedResponse response = new CombinedResponse(
                dspList,                   // Không phân trang
                ctspList,                  // Không phân trang
                1,                         // totalPagesDSP = 1
                0,                         // currentPageDSP = 0
                dspList.size(),
                1,                         // totalPagesCTSP = 1
                0,                         // currentPageCTSP = 0
                ctspList.size(),           // totalElementsCTSP
                heDieuHanhList,
                nhaSanXuatList
        );
        return ResponseEntity.ok(response);
    }


//
//    @GetMapping("/he-dieu-hanh")
//    public ResponseEntity<List<HeDieuHanh>> getAllHeDieuHanh() {
//        List<HeDieuHanh> heDieuHanhList = sr.getAllHeDieuHanh();
//        return ResponseEntity.ok(heDieuHanhList);
//    }
//
//    @GetMapping("/nha-san-xuat")
//    public ResponseEntity<List<NhaSanXuat>> getAllNhaSanXuat() {
//        List<NhaSanXuat> nhaSanXuatList = sr.getAllNhaSanXuat();
//        return ResponseEntity.ok(nhaSanXuatList);
//    }

    @PostMapping("/AddDotGiamGia")
    public ResponseEntity<?> addData(@RequestBody AddDotGiamGiaDTO request) {
        if (request == null || request.getDotGiamGia() == null) {
            return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
        }
        try {
            System.out.println("Dữ liệu nhận được: " + request.toString());
            System.out.println("ctspList nhận được: " + request.getCtspList());
            sr.addDotGiamGia(request.getDotGiamGia(), request.getIdDSPs(), request.getCtspList());
            return ResponseEntity.ok("Thêm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/ViewAddDotGiamGia/exists/ma")
    public ResponseEntity<Boolean> checkMa(@RequestParam String ma) {
        System.out.println(ma);
        return ResponseEntity.ok(sr.existByMa(ma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDotGiamGia(@PathVariable("id") Integer id) {
        try {
            sr.deleteDotGiamGiaById(id);  // Gọi service để xóa
            return ResponseEntity.noContent().build(); // Trả về trạng thái thành công
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Trả về lỗi nếu có vấn đề
        }
    }

    @GetMapping("/viewUpdate")
    public ResponseEntity<?> viewUpdateDotGiamGia(@RequestParam Integer id) {
        try {
            Map<String, Object> data = sr.getDataForUpdate(id);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/AddDotGiamGia/{id}")
    public ResponseEntity<?> updateData(@RequestBody AddDotGiamGiaDTO request, @PathVariable("id") Integer id) {
        if (request == null || request.getDotGiamGia() == null) {
            return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
        }
        try {
            System.out.println("Dữ liệu nhận được: " + request.toString());
            sr.updateDotGiamGia(id, request.getDotGiamGia(), request.getIdDSPs(), request.getCtspList());
            return ResponseEntity.ok("Thêm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
}
