package com.example.be_datn.controller.product;

import com.example.be_datn.entity.product.ChiTietSanPham;
import com.example.be_datn.service.clientService.ProductService.ProductClientService;
import com.example.be_datn.service.clientService.impl.Product.ProductDetailClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class ProductController {
@Autowired
    private ProductClientService productClientService;

    @Autowired
    private ProductDetailClientServiceImpl productDetailClientService;


    @GetMapping("/chi-tiet-san-pham")
    public List<Map<String, Object>> getProductVariants(@RequestParam("sanPhamId") Integer sanPhamId) {
        List<Object[]> results = productDetailClientService.findChiTietSanPhamBySanPhamId(sanPhamId);
        return results.stream().map(record -> {
            Map<String, Object> variant = new HashMap<>();
            variant.put("sp_id", record[0]);
            variant.put("ten_san_pham", record[1]);
            variant.put("sp_ma", record[2]);
            variant.put("sp_created_at", record[3]);
            variant.put("nha_san_xuat", record[4]);
            variant.put("ten_cpu", record[5]);
            variant.put("ten_gpu", record[6]);
            variant.put("thong_so_camera_sau", record[7]);
            variant.put("thong_so_camera_truoc", record[8]);
            variant.put("ctsp_id", record[9]);
            variant.put("gia_ban", record[10] != null ? record[10] : 0);
            variant.put("ctsp_ma", record[11]);
            variant.put("id_imel", record[12]);
            variant.put("mau_sac", record[13]);
            variant.put("ram_dung_luong", record[14]);
            variant.put("bo_nho_trong_dung_luong", record[15]);
            variant.put("anh_san_pham_url", record[16] != null ? record[16] : "/assets/images/placeholder.jpg");
            variant.put("ghi_chu", record[17] != null ? record[17] : "Không có mô tả chi tiết.");
            variant.put("gia_sau_khi_giam", record[18] != null ? record[18] : record[10] != null ? record[10] : 0);
            variant.put("gia_ban_dau", record[19] != null ? record[19] : record[10] != null ? record[10] : 0);
            variant.put("has_discount", record[20] != null && ((Number) record[20]).intValue() == 1);
            variant.put("giam_phan_tram", record[21] != null ? record[21] : 0);
            variant.put("giam_toi_da", record[22] != null ? record[22] : 0);
            variant.put("loai_giam_gia_ap_dung", record[23] != null ? record[23] : "NONE");
            variant.put("chi_so_khang_bui_nuoc", record[24] != null ? record[24] : "Không có thông tin");
            variant.put("ten_cong_nghe_mang", record[25] != null ? record[25] : "Không có thông tin");
            variant.put("he_dieu_hanh", record[26] != null ? record[26] : "Không có thông tin");
            variant.put("phien_ban", record[27] != null ? record[27] : "Không có thông tin");
            variant.put("ho_tro_bo_nho_ngoai", record[28] != null ? record[28] : "Không có thông tin");
            variant.put("dung_luong_pin", record[29] != null ? record[29] : "Không có thông tin");
            variant.put("cac_loai_sim_ho_tro", record[30] != null ? record[30] : "Không có thông tin");
            variant.put("chat_lieu_khung", record[31] != null ? record[31] : "Không có thông tin");
            variant.put("chat_lieu_mat_lung", record[32] != null ? record[32] : "Không có thông tin");
            variant.put("cong_nghe_ho_tro", record[33] != null ? record[33] : "Không có thông tin");
            variant.put("ten_cong_nghe_man_hinh", record[34] != null ? record[34] : "Không có thông tin");
            variant.put("chuan_man_hinh", record[35] != null ? record[35] : "Không có thông tin");
            variant.put("kich_thuoc", record[36] != null ? record[36] : "Không có thông tin");
            variant.put("do_phan_giai", record[37] != null ? record[37] : "Không có thông tin");
            variant.put("do_sang_toi_da", record[38] != null ? record[38] : "Không có thông tin");
            variant.put("tan_so_quet", record[39] != null ? record[39] : "Không có thông tin");
            variant.put("kieu_man_hinh", record[40] != null ? record[40] : "Không có thông tin");
            variant.put("imel_value", record[41] != null ? record[41] : "Không có thông tin");
            return variant;
        }).collect(Collectors.toList());
    }
    // Get suggested products (top 6)
    @GetMapping("/suggested-products")
    public List<Map<String, Object>> getSuggestedProducts() {
        List<Object[]> results = productClientService.suggestProductTop6();
        return results.stream().map(record -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", record[0]); // sp.id
            product.put("tenSanPham", record[1]); // sp.ten_san_pham
            product.put("createdAt", record[2]); // sp.created_at
            product.put("tenNhaSanXuat", record[3]); // nsx.id or nsx.ten_nha_san_xuat
            product.put("giaBan", record[4] != null ? record[4] : 0); // ctsp.gia_ban
            product.put("imageUrl", record[5] != null ? record[5] : "/assets/images/placeholder.jpg"); // asp.duong_dan
            return product;
        }).collect(Collectors.toList());
    }

    // Get product variants
    @GetMapping("/san-pham-with-variants")
    public List<Map<String, Object>> getProductsWithLatestVariant(@RequestParam(required = false) Integer idNhaSanXuat) {
        List<Object[]> results = productClientService.findProductsWithLatestVariant(idNhaSanXuat);
        return results.stream().map(record -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", record[0]); // sp.id
            product.put("tenSanPham", record[1]); // sp.ten_san_pham
            product.put("createdAt", record[2]); // sp.created_at
            product.put("tenNhaSanXuat", record[3]); // nsx.id
            product.put("giaBan", record[4] != null ? record[4] : 0); // ctsp.gia_ban
            product.put("giaSauKhiGiam", record[5] != null ? record[5] : record[4] != null ? record[4] : 0); // giaSauKhiGiam
            product.put("hasDiscount", record[7] != null && ((Number) record[7]).intValue() == 1); // hasDiscount
            product.put("imageUrl", record[6] != null ? record[6] : "/assets/images/placeholder.jpg"); // asp.duong_dan
            product.put("giamPhanTram", record[8] != null ? record[8] : 0); // gia_tri_giam_gia
            product.put("giamToiDa", record[9] != null ? record[9] : 0); // so_tien_giam_toi_da
            product.put("loaiGiamGiaApDung", record[10] != null ? record[10] : "NONE"); // loai_giam_gia_ap_dung
            return product;
        }).collect(Collectors.toList());
    }

    @GetMapping("/show-new-product")
    public List<Map<String, Object>> getNewProducts(@RequestParam(required = false) Integer idNhaSanXuat) {
        List<Object[]> results = productClientService.showNewProduct(idNhaSanXuat);
        return results.stream().map(record -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", record[0]); // sp.id
            product.put("tenSanPham", record[1]); // sp.ten_san_pham
            product.put("createdAt", record[2]); // sp.created_at
            product.put("tenNhaSanXuat", record[3]); // nsx.id (ID nhà sản xuất)
            product.put("giaBan", record[4] != null ? record[4] : 0); // ctsp.gia_ban
            product.put("imageUrl", record[5] != null ? record[5] : "/assets/images/placeholder.jpg"); // asp.duong_dan
            return product;
        }).collect(Collectors.toList());
    }

    @GetMapping("/show-best-product")
    public List<Map<String, Object>> getBestProducts(@RequestParam(required = false) String sortBy) {
        List<Object[]> results = productClientService.showBestProduct(sortBy != null ? sortBy : "RATING");
        return results.stream().map(record -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", record[0]); // sp.id
            product.put("tenSanPham", record[1]); // sp.ten_san_pham
            product.put("createdAt", record[2]); // sp.created_at
            product.put("tenNhaSanXuat", record[3]); // nsx.id
            product.put("giaBan", record[4] != null ? record[4] : 0); // ctsp.gia_ban
            product.put("giaSauKhiGiam", record[5] != null ? record[5] : record[4] != null ? record[4] : 0); // giaSauKhiGiam
            product.put("imageUrl", record[6] != null ? record[6] : "/assets/images/placeholder.jpg"); // asp.duong_dan
            product.put("hasDiscount", record[7] != null && ((Number) record[7]).intValue() == 1); // hasDiscount
            product.put("giamPhanTram", record[8] != null ? record[8] : 0); // gia_tri_giam_gia
            product.put("giamToiDa", record[9] != null ? record[9] : 0); // so_tien_giam_toi_da
            product.put("loaiGiamGiaApDung", record[10] != null ? record[10] : "NONE"); // loai_giam_gia_ap_dung
            return product;
        }).collect(Collectors.toList());
    }
    // Get all products with pagination
    @GetMapping("/products")
    public Map<String, Object> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "popularity") String sortBy,
            @RequestParam(defaultValue = "") String useCases,
            @RequestParam(defaultValue = "") String colors,
            @RequestParam(defaultValue = "") String brands,
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = "0") double maxPrice) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> productPage = productClientService.showAllProduct(pageable, sortBy, useCases, colors, brands, minPrice, maxPrice);

        List<Map<String, Object>> products = productPage.getContent().stream().map(record -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", record[0]);
            product.put("tenSanPham", record[1]);
            product.put("createdAt", record[2]);
            product.put("tenNhaSanXuat", record[3]);
            product.put("giaBan", record[4] != null ? record[4] : 0); // Giá gốc
            product.put("giaSauKhiGiam", record[5] != null ? record[5] : record[4] != null ? record[4] : 0); // Giá sau giảm
            product.put("imageUrl", record[6] != null ? record[6] : "/assets/images/placeholder.jpg");
            product.put("mauSacList", record[7] != null ? Arrays.asList(((String) record[7]).split(",")) : Collections.emptyList());
            product.put("hasDiscount", record[8] != null && ((Number) record[8]).intValue() == 1);
            product.put("giamPhanTram", record[9] != null ? record[9] : 0);
            product.put("giamToiDa", record[10] != null ? record[10] : 0);
            product.put("loaiGiamGiaApDung", record[11] != null ? record[11] : "NONE");
            return product;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());

        return response;
    }

    @GetMapping("/price-range")
    public Map<String, Object> getPriceRange() {
        Map<String, Object> response = new HashMap<>();
        Double minPrice = productDetailClientService.findMinPrice();
        Double maxPrice = productDetailClientService.findMaxPrice();
        response.put("minPrice", minPrice != null ? minPrice : 0);
        response.put("maxPrice", maxPrice != null ? maxPrice : 0);
        return response;
    }

    @GetMapping("/colors")
    public Map<String, Object> getColors() {
        List<String> colors = productDetailClientService.findDistinctColors();
        Map<String, Object> response = new HashMap<>();
        response.put("colors", colors);
        return response;
    }


    // New endpoint for fetching products for comparison (combobox)
    @GetMapping("/products/compare")
    public List<Map<String, Object>> getProductsForCompare() {
        List<Object[]> results = productClientService.getProductForCompare();
        return results.stream().map(record -> {
            Map<String, Object> product = new HashMap<>();
            product.put("id", record[0]); // sp.id
            product.put("tenSanPham", record[1]); // sp.ten_san_pham
            return product;
        }).collect(Collectors.toList());
    }

    // New endpoint for fetching detailed product information

    @GetMapping("/products/details/{id}")
    public Map<String, Object> getProductDetails(@PathVariable Integer id) {
        Map<String, Object> product = new HashMap<>();
        productClientService.findSanPhamWithDetailsById(id).ifPresent(sanPham -> {
            product.put("tenSanPham", sanPham.getTenSanPham() != null ? sanPham.getTenSanPham() : "Không có thông tin");
            ChiTietSanPham chiTiet = sanPham.getChiTietSanPhams().stream().findFirst().orElse(null);
            product.put("anhSanPhamUrl", chiTiet != null && chiTiet.getIdAnhSanPham() != null ? chiTiet.getIdAnhSanPham().getDuongDan() : "/assets/images/placeholder.jpg");
            product.put("giaBan", chiTiet != null ? chiTiet.getGiaBan() : 0.0);
            product.put("ram", chiTiet != null && chiTiet.getIdRam() != null ? chiTiet.getIdRam().getDungLuongRam() : "Không có thông tin");
            product.put("storage", chiTiet != null && chiTiet.getIdBoNhoTrong() != null ? chiTiet.getIdBoNhoTrong().getDungLuongBoNhoTrong() : "Không có thông tin");
            product.put("battery", sanPham.getIdPin() != null ? sanPham.getIdPin().getDungLuongPin() : "Không có thông tin");
            product.put("waterResistance", sanPham.getIdChiSoKhangBuiVaNuoc() != null ? sanPham.getIdChiSoKhangBuiVaNuoc().getTenChiSo() : "Không có thông tin");
            product.put("networkTech", sanPham.getIdCongNgheMang() != null ? sanPham.getIdCongNgheMang().getTenCongNgheMang() : "Không có thông tin");
            product.put("os", sanPham.getIdHeDieuHanh() != null ? sanPham.getIdHeDieuHanh().getHeDieuHanh() : "Không có thông tin");
            product.put("osVersion", sanPham.getIdHeDieuHanh() != null ? sanPham.getIdHeDieuHanh().getPhienBan() : "Không có thông tin");
            product.put("externalStorage", sanPham.getIdHoTroBoNhoNgoai() != null ? sanPham.getIdHoTroBoNhoNgoai().getHoTroBoNhoNgoai() : "Không có thông tin");
            product.put("simType", sanPham.getIdSim() != null ? sanPham.getIdSim().getSoLuongSimHoTro() : "Không có thông tin");
            product.put("frameMaterial", sanPham.getIdThietKe() != null ? sanPham.getIdThietKe().getChatLieuKhung() : "Không có thông tin");
            product.put("backMaterial", sanPham.getIdThietKe() != null ? sanPham.getIdThietKe().getChatLieuMatLung() : "Không có thông tin");
            product.put("chargingTech", sanPham.getHoTroCongNgheSac() != null ? sanPham.getHoTroCongNgheSac().getCongNgheHoTro() : "Không có thông tin");
            product.put("displayTech", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getCongNgheManHinh() : "Không có thông tin");
            product.put("resolution", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getChuanManHinh() : "Không có thông tin");
            product.put("screenSize", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getKichThuoc() : "Không có thông tin");
            product.put("brightnessStandard", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getDoPhanGiai() : "Không có thông tin");
            product.put("brightnessHDR", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getDoSangToiDa() : "Không có thông tin");
            product.put("brightnessOutdoor", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getTanSoQuet() : "Không có thông tin");
            product.put("typeOfScreen", sanPham.getCongNgheManHinh() != null ? sanPham.getCongNgheManHinh().getKieuManHinh() : "Không có thông tin"); // Sửa lỗi đánh máy
        });
        return product;
    }
}
