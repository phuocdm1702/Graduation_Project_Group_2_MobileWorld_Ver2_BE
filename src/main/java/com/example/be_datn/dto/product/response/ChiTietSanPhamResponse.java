package com.example.be_datn.dto.product.response;

import com.example.be_datn.entity.product.Imel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ChiTietSanPhamResponse {
    private Integer id;
    private Integer idSanPham;
    private String maSanPham;
    private Integer idNhaSanXuat;
    private Integer idPin;
    private Integer congNgheManHinh;
    private Integer idHoTroBoNhoNgoai;
    private Integer idCpu;
    private Integer idGpu;
    private Integer idCumCamera;
    private Integer idHeDieuHanh;
    private Integer idChiSoKhangBuiVaNuoc;
    private Integer idThietKe;
    private Integer idSim;
    private Integer hoTroCongNgheSac;
    private Integer idCongNgheMang;
    private String ma;
    private String tenSanPham;
    private Boolean deleted;
    private Date createdAt;
    private Integer createdBy;
    private Date updatedAt;
    private Integer updatedBy;
    private BigDecimal giaBan;
    private String ghiChu;
    private List<VariantResponseDTO> variants;

    // Constructor mặc định
    public ChiTietSanPhamResponse() {
    }

    // Constructor mới để khắc phục lỗi
    public ChiTietSanPhamResponse(Integer idSanPham, List<Integer> chiTietIds, List<Integer> anhSanPhamIds) {
        this.idSanPham = idSanPham;
        this.id = chiTietIds != null && !chiTietIds.isEmpty() ? chiTietIds.get(0) : null;
        // Nếu cần xử lý anhSanPhamIds, bạn có thể thêm logic ở đây
    }

    @Getter
    @Setter
    public static class VariantResponseDTO {
        private Imel idImel;
        private Integer idMauSac;
        private String mauSac;
        private Integer idRam;
        private String dungLuongRam;
        private Integer idBoNhoTrong;
        private String dungLuongBoNhoTrong;
        private BigDecimal donGia;
        private Integer imageIndex;
        private List<String> imeiList;
        private Integer quantity;

        public void setDonGia(BigDecimal donGia) {
            if (donGia != null && donGia.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
            }
            this.donGia = donGia;
        }
    }
}