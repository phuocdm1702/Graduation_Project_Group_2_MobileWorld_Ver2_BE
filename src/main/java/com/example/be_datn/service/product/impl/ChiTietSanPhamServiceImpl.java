package com.example.be_datn.service.product.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamDetailResponse;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import com.example.be_datn.entity.product.*;
import com.example.be_datn.repository.product.*;
import com.example.be_datn.service.product.ChiTietSanPhamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChiTietSanPhamServiceImpl implements ChiTietSanPhamService {

    private static final Logger logger = LoggerFactory.getLogger(ChiTietSanPhamServiceImpl.class);

    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final AnhSanPhamRepository anhSanPhamRepository;
    private final RamRepository ramRepository;
    private final BoNhoTrongRepository boNhoTrongRepository;
    private final MauSacRepository mauSacRepository;
    private final ImelRepository imelRepository;
    private final HeDieuHanhRepository heDieuHanhRepository;
    private final CongNgheManHinhRepository congNgheManHinhRepository;
    private final NhaSanXuatRepository nhaSanXuatRepository;
    private final CumCameraRepository cumCameraRepository;
    private final SimRepository simRepository;
    private final ThietKeRepository thietKeRepository;
    private final PinRepository pinRepository;
    private final CpuRepository cpuRepository;
    private final GpuRepository gpuRepository;
    private final CongNgheMangRepository congNgheMangRepository;
    private final HoTroCongNgheSacRepository hoTroCongNgheSacRepository;
    private final ChiSoKhangBuiVaNuocRepository chiSoKhangBuiVaNuocRepository;
    private final HoTroBoNhoNgoaiRepository hoTroBoNhoNgoaiRepository;
    private final Cloudinary cloudinary;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ChiTietSanPhamServiceImpl(
            HeDieuHanhRepository heDieuHanhRepository,
            SanPhamRepository sanPhamRepository,
            PinRepository pinRepository,
            ChiTietSanPhamRepository chiTietSanPhamRepository,
            AnhSanPhamRepository anhSanPhamRepository,
            ChiSoKhangBuiVaNuocRepository chiSoKhangBuiVaNuocRepository,
            RamRepository ramRepository,
            BoNhoTrongRepository boNhoTrongRepository,
            CongNgheMangRepository congNgheMangRepository,
            HoTroBoNhoNgoaiRepository hoTroBoNhoNgoaiRepository,
            CpuRepository cpuRepository,
            MauSacRepository mauSacRepository,
            ThietKeRepository thietKeRepository,
            GpuRepository gpuRepository,
            ImelRepository imelRepository,
            SimRepository simRepository,
            CongNgheManHinhRepository congNgheManHinhRepository,
            NhaSanXuatRepository nhaSanXuatRepository,
            HoTroCongNgheSacRepository hoTroCongNgheSacRepository,
            CumCameraRepository cumCameraRepository,
            Cloudinary cloudinary) {
        this.heDieuHanhRepository = heDieuHanhRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.pinRepository = pinRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.anhSanPhamRepository = anhSanPhamRepository;
        this.chiSoKhangBuiVaNuocRepository = chiSoKhangBuiVaNuocRepository;
        this.ramRepository = ramRepository;
        this.boNhoTrongRepository = boNhoTrongRepository;
        this.congNgheMangRepository = congNgheMangRepository;
        this.hoTroBoNhoNgoaiRepository = hoTroBoNhoNgoaiRepository;
        this.cpuRepository = cpuRepository;
        this.mauSacRepository = mauSacRepository;
        this.thietKeRepository = thietKeRepository;
        this.gpuRepository = gpuRepository;
        this.imelRepository = imelRepository;
        this.simRepository = simRepository;
        this.congNgheManHinhRepository = congNgheManHinhRepository;
        this.nhaSanXuatRepository = nhaSanXuatRepository;
        this.hoTroCongNgheSacRepository = hoTroCongNgheSacRepository;
        this.cumCameraRepository = cumCameraRepository;
        this.cloudinary = cloudinary;
    }

    private AnhSanPham uploadImageToCloudinary(MultipartFile image, String fileName) {
        try {
            String publicId = "product_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "image"
            ));
            String imageUrl = (String) uploadResult.get("secure_url");
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                throw new RuntimeException("Cloudinary trả về URL ảnh rỗng");
            }
            AnhSanPham anhSanPham = AnhSanPham.builder()
                    .tenAnh(fileName)
                    .duongDan(imageUrl)
                    .deleted(false)
                    .build();
            anhSanPham = anhSanPhamRepository.save(anhSanPham);
            logger.info("Successfully uploaded image to Cloudinary: {} -> {}", fileName, imageUrl);
            return anhSanPham;
        } catch (IOException e) {
            logger.error("Failed to upload image {} to Cloudinary: {}", fileName, e.getMessage());
            throw new RuntimeException("Lỗi khi tải ảnh " + fileName + " lên Cloudinary: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during image upload for {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Lỗi không mong muốn khi tải ảnh " + fileName + ": " + e.getMessage(), e);
        }
    }

    private Map<Integer, String> processImageUploads(List<MultipartFile> images, List<ChiTietSanPhamRequest.VariantRequest> variants) {
        logger.info("Starting image upload process");
        Map<Integer, String> colorImageUrls = new HashMap<>();
        List<AnhSanPham> savedImages = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            logger.error("No images provided for upload");
            throw new IllegalArgumentException("Phải có ít nhất một ảnh sản phẩm");
        }
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            if (image.isEmpty()) {
                logger.error("Empty image file detected at index {}", i);
                throw new IllegalArgumentException("File ảnh tại vị trí " + (i + 1) + " không được để trống");
            }
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            if (fileName == null || fileName.trim().isEmpty()) {
                logger.error("Invalid filename at index {}", i);
                throw new IllegalArgumentException("Tên file ảnh không hợp lệ tại vị trí " + (i + 1));
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.error("Invalid file type: {} at index {}", contentType, i);
                throw new IllegalArgumentException("File tại vị trí " + (i + 1) + " không phải là ảnh hợp lệ");
            }
            if (image.getSize() > 10 * 1024 * 1024) {
                logger.error("File too large: {} bytes at index {}", image.getSize(), i);
                throw new IllegalArgumentException("File ảnh tại vị trí " + (i + 1) + " quá lớn (tối đa 10MB)");
            }
            AnhSanPham anhSanPham = uploadImageToCloudinary(image, fileName);
            savedImages.add(anhSanPham);
            if (i < variants.size()) {
                Integer mauSacId = variants.get(i).getIdMauSac();
                colorImageUrls.put(mauSacId, anhSanPham.getDuongDan());
                logger.debug("Mapped image {} to color ID: {}", anhSanPham.getDuongDan(), mauSacId);
            }
        }
        if (variants.size() > images.size() && !savedImages.isEmpty()) {
            String lastImageUrl = savedImages.get(savedImages.size() - 1).getDuongDan();
            for (int i = images.size(); i < variants.size(); i++) {
                Integer mauSacId = variants.get(i).getIdMauSac();
                if (!colorImageUrls.containsKey(mauSacId)) {
                    colorImageUrls.put(mauSacId, lastImageUrl);
                    logger.debug("Mapped additional variant color ID {} to last image: {}", mauSacId, lastImageUrl);
                }
            }
        }
        logger.info("Completed image upload process. Processed {} images, mapped {} color variants",
                savedImages.size(), colorImageUrls.size());
        return colorImageUrls;
    }

    private Map<Integer, String> processImageUpdates(List<MultipartFile> images, List<String> existingImageUrls,
                                                     List<ChiTietSanPhamRequest.VariantRequest> variants) {
        logger.info("Starting image update process");
        Map<Integer, String> colorImageUrls = new HashMap<>();
        List<AnhSanPham> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            colorImageUrls = processImageUploads(images, variants);
        } else {
            logger.info("No new images provided, using existing images");
            if (existingImageUrls == null || existingImageUrls.isEmpty()) {
                logger.error("No existing images found and no new images provided");
                throw new IllegalArgumentException("Không tìm thấy ảnh hiện tại và không có ảnh mới được cung cấp");
            }
            for (int i = 0; i < variants.size(); i++) {
                Integer mauSacId = variants.get(i).getIdMauSac();
                String imageUrl = existingImageUrls.get(0);
                Optional<AnhSanPham> anhSanPhamOpt = anhSanPhamRepository.findByDuongDan(imageUrl);
                if (anhSanPhamOpt.isPresent()) {
                    savedImages.add(anhSanPhamOpt.get());
                    colorImageUrls.put(mauSacId, imageUrl);
                    logger.debug("Mapped existing image {} to color ID: {}", imageUrl, mauSacId);
                } else {
                    logger.error("Existing image URL {} not found in database", imageUrl);
                    throw new IllegalArgumentException("Ảnh hiện tại không tồn tại: " + imageUrl);
                }
            }
        }
        logger.info("Completed image update process");
        return colorImageUrls;
    }

    private SanPham createOrFindSanPham(ChiTietSanPhamRequest request) {
        return sanPhamRepository.findByTenSanPhamAndDeletedFalse(request.getTenSanPham())
                .orElseGet(() -> {
                    logger.info("Creating new SanPham with name: {}", request.getTenSanPham());
                    SanPham newSanPham = SanPham.builder()
                            .tenSanPham(request.getTenSanPham())
                            .idNhaSanXuat(nhaSanXuatRepository.findById(request.getIdNhaSanXuat())
                                    .orElseThrow(() -> new IllegalArgumentException("Nhà sản xuất không tồn tại")))
                            .idPin(pinRepository.findById(request.getIdPin())
                                    .orElseThrow(() -> new IllegalArgumentException("Pin không tồn tại")))
                            .congNgheManHinh(congNgheManHinhRepository.findById(request.getIdCongNgheManHinh())
                                    .orElseThrow(() -> new IllegalArgumentException("Công nghệ màn hình không tồn tại")))
                            .idHoTroBoNhoNgoai(request.getIdHoTroBoNhoNgoai() != null ?
                                    hoTroBoNhoNgoaiRepository.findById(request.getIdHoTroBoNhoNgoai())
                                            .orElseThrow(() -> new IllegalArgumentException("Hỗ trợ bộ nhớ ngoài không tồn tại")) : null)
                            .idCpu(cpuRepository.findById(request.getIdCpu())
                                    .orElseThrow(() -> new IllegalArgumentException("CPU không tồn tại")))
                            .idGpu(gpuRepository.findById(request.getIdGpu())
                                    .orElseThrow(() -> new IllegalArgumentException("GPU không tồn tại")))
                            .idCumCamera(cumCameraRepository.findById(request.getIdCumCamera())
                                    .orElseThrow(() -> new IllegalArgumentException("Cụm camera không tồn tại")))
                            .idHeDieuHanh(heDieuHanhRepository.findById(request.getIdHeDieuHanh())
                                    .orElseThrow(() -> new IllegalArgumentException("Hệ điều hành không tồn tại")))
                            .idChiSoKhangBuiVaNuoc(chiSoKhangBuiVaNuocRepository.findById(request.getIdChiSoKhangBuiVaNuoc())
                                    .orElseThrow(() -> new IllegalArgumentException("Chỉ số kháng bụi nước không tồn tại")))
                            .idThietKe(thietKeRepository.findById(request.getIdThietKe())
                                    .orElseThrow(() -> new IllegalArgumentException("Thiết kế không tồn tại")))
                            .idSim(simRepository.findById(request.getIdSim())
                                    .orElseThrow(() -> new IllegalArgumentException("SIM không tồn tại")))
                            .hoTroCongNgheSac(hoTroCongNgheSacRepository.findById(request.getIdHoTroCongNgheSac())
                                    .orElseThrow(() -> new IllegalArgumentException("Hỗ trợ công nghệ sạc không tồn tại")))
                            .idCongNgheMang(congNgheMangRepository.findById(request.getIdCongNgheMang())
                                    .orElseThrow(() -> new IllegalArgumentException("Công nghệ mạng không tồn tại")))
                            .createdAt(new Date())
                            .createdBy(1)
                            .updatedAt(new Date())
                            .updatedBy(1)
                            .deleted(false)
                            .build();
                    return sanPhamRepository.save(newSanPham);
                });
    }

    private SanPham updateExistingSanPham(Integer id, ChiTietSanPhamRequest request) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));
        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setIdNhaSanXuat(nhaSanXuatRepository.findById(request.getIdNhaSanXuat())
                .orElseThrow(() -> new IllegalArgumentException("Nhà sản xuất không tồn tại")));
        sanPham.setIdPin(pinRepository.findById(request.getIdPin())
                .orElseThrow(() -> new IllegalArgumentException("Pin không tồn tại")));
        sanPham.setCongNgheManHinh(congNgheManHinhRepository.findById(request.getIdCongNgheManHinh())
                .orElseThrow(() -> new IllegalArgumentException("Công nghệ màn hình không tồn tại")));
        sanPham.setIdHoTroBoNhoNgoai(request.getIdHoTroBoNhoNgoai() != null ?
                hoTroBoNhoNgoaiRepository.findById(request.getIdHoTroBoNhoNgoai())
                        .orElseThrow(() -> new IllegalArgumentException("Hỗ trợ bộ nhớ ngoài không tồn tại")) : null);
        sanPham.setIdCpu(cpuRepository.findById(request.getIdCpu())
                .orElseThrow(() -> new IllegalArgumentException("CPU không tồn tại")));
        sanPham.setIdGpu(gpuRepository.findById(request.getIdGpu())
                .orElseThrow(() -> new IllegalArgumentException("GPU không tồn tại")));
        sanPham.setIdCumCamera(cumCameraRepository.findById(request.getIdCumCamera())
                .orElseThrow(() -> new IllegalArgumentException("Cụm camera không tồn tại")));
        sanPham.setIdHeDieuHanh(heDieuHanhRepository.findById(request.getIdHeDieuHanh())
                .orElseThrow(() -> new IllegalArgumentException("Hệ điều hành không tồn tại")));
        sanPham.setIdChiSoKhangBuiVaNuoc(chiSoKhangBuiVaNuocRepository.findById(request.getIdChiSoKhangBuiVaNuoc())
                .orElseThrow(() -> new IllegalArgumentException("Chỉ số kháng bụi nước không tồn tại")));
        sanPham.setIdThietKe(thietKeRepository.findById(request.getIdThietKe())
                .orElseThrow(() -> new IllegalArgumentException("Thiết kế không tồn tại")));
        sanPham.setIdSim(simRepository.findById(request.getIdSim())
                .orElseThrow(() -> new IllegalArgumentException("SIM không tồn tại")));
        sanPham.setHoTroCongNgheSac(hoTroCongNgheSacRepository.findById(request.getIdHoTroCongNgheSac())
                .orElseThrow(() -> new IllegalArgumentException("Hỗ trợ công nghệ sạc không tồn tại")));
        sanPham.setIdCongNgheMang(congNgheMangRepository.findById(request.getIdCongNgheMang())
                .orElseThrow(() -> new IllegalArgumentException("Công nghệ mạng không tồn tại")));
        sanPham.setUpdatedAt(new Date());
        sanPham.setUpdatedBy(1);
        return sanPhamRepository.save(sanPham);
    }

    private List<ChiTietSanPham> createProductVariants(ChiTietSanPhamRequest request, SanPham sanPham, Map<Integer, String> colorImageUrls) {
        List<ChiTietSanPham> savedVariants = new ArrayList<>();
        for (ChiTietSanPhamRequest.VariantRequest variant : request.getVariants()) {
            MauSac mauSac = mauSacRepository.findById(variant.getIdMauSac())
                    .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
            Ram ram = ramRepository.findById(variant.getIdRam())
                    .orElseThrow(() -> new IllegalArgumentException("RAM không tồn tại"));
            BoNhoTrong boNhoTrong = boNhoTrongRepository.findById(variant.getIdBoNhoTrong())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ nhớ trong không tồn tại"));
            String imageUrl = colorImageUrls.getOrDefault(variant.getIdMauSac(), colorImageUrls.values().iterator().next());
            Optional<AnhSanPham> anhSanPhamOpt = anhSanPhamRepository.findByDuongDan(imageUrl);
            AnhSanPham anhSanPham = anhSanPhamOpt.orElseThrow(() -> new IllegalArgumentException("Ảnh sản phẩm không tồn tại"));
            for (String imei : variant.getImeiList()) {
                if (imelRepository.existsByImelAndDeletedFalse(imei)) {
                    logger.warn("Duplicate IMEI detected: {}", imei);
                    throw new IllegalArgumentException("IMEI đã tồn tại: " + imei);
                }
                Imel imel = Imel.builder()
                        .imel(imei)
                        .deleted(false)
                        .build();
                imel = imelRepository.save(imel);
                ChiTietSanPham chiTietSanPham = ChiTietSanPham.builder()
                        .idSanPham(sanPham)
                        .idMauSac(mauSac)
                        .idRam(ram)
                        .idBoNhoTrong(boNhoTrong)
                        .idImel(imel)
                        .idAnhSanPham(anhSanPham)
                        .giaBan(variant.getDonGia())
                        .ghiChu(request.getGhiChu())
                        .createdAt(new Date())
                        .createdBy(1)
                        .updatedAt(new Date())
                        .updatedBy(1)
                        .deleted(false)
                        .build();
                chiTietSanPham = chiTietSanPhamRepository.save(chiTietSanPham);
                savedVariants.add(chiTietSanPham);
                logger.info("Saved ChiTietSanPham for IMEI: {}", imei);
            }
        }
        return savedVariants;
    }

    private List<ChiTietSanPham> updateProductVariants(Integer id, ChiTietSanPhamRequest request, SanPham sanPham, Map<Integer, String> colorImageUrls) {
        List<ChiTietSanPham> existingVariants = chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(id, false);
        List<ChiTietSanPham> savedVariants = new ArrayList<>();
        for (ChiTietSanPhamRequest.VariantRequest variant : request.getVariants()) {
            MauSac mauSac = mauSacRepository.findById(variant.getIdMauSac())
                    .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
            Ram ram = ramRepository.findById(variant.getIdRam())
                    .orElseThrow(() -> new IllegalArgumentException("RAM không tồn tại"));
            BoNhoTrong boNhoTrong = boNhoTrongRepository.findById(variant.getIdBoNhoTrong())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ nhớ trong không tồn tại"));
            String imageUrl = colorImageUrls.getOrDefault(variant.getIdMauSac(), colorImageUrls.values().iterator().next());
            Optional<AnhSanPham> anhSanPhamOpt = anhSanPhamRepository.findByDuongDan(imageUrl);
            AnhSanPham anhSanPham = anhSanPhamOpt.orElseThrow(() -> new IllegalArgumentException("Ảnh sản phẩm không tồn tại"));
            for (String imei : variant.getImeiList()) {
                Optional<ChiTietSanPham> existingChiTietSanPham = existingVariants.stream()
                        .filter(ctsp -> ctsp.getIdImel().getImel().equals(imei))
                        .findFirst();
                if (existingChiTietSanPham.isPresent()) {
                    ChiTietSanPham chiTietSanPham = existingChiTietSanPham.get();
                    chiTietSanPham.setIdMauSac(mauSac);
                    chiTietSanPham.setIdRam(ram);
                    chiTietSanPham.setIdBoNhoTrong(boNhoTrong);
                    chiTietSanPham.setIdAnhSanPham(anhSanPham);
                    chiTietSanPham.setGiaBan(variant.getDonGia());
                    chiTietSanPham.setGhiChu(request.getGhiChu());
                    chiTietSanPham.setUpdatedAt(new Date());
                    chiTietSanPham.setUpdatedBy(1);
                    chiTietSanPhamRepository.save(chiTietSanPham);
                    savedVariants.add(chiTietSanPham);
                    logger.info("Updated ChiTietSanPham for IMEI: {}", imei);
                } else {
                    if (imelRepository.existsByImelAndDeletedFalse(imei)) {
                        logger.warn("Duplicate IMEI detected: {}", imei);
                        throw new IllegalArgumentException("IMEI đã tồn tại: " + imei);
                    }
                    Imel imel = Imel.builder()
                            .imel(imei)
                            .deleted(false)
                            .build();
                    imel = imelRepository.save(imel);
                    ChiTietSanPham chiTietSanPham = ChiTietSanPham.builder()
                            .idSanPham(sanPham)
                            .idMauSac(mauSac)
                            .idRam(ram)
                            .idBoNhoTrong(boNhoTrong)
                            .idImel(imel)
                            .idAnhSanPham(anhSanPham)
                            .giaBan(variant.getDonGia())
                            .ghiChu(request.getGhiChu())
                            .createdAt(new Date())
                            .createdBy(1)
                            .updatedAt(new Date())
                            .updatedBy(1)
                            .deleted(false)
                            .build();
                    chiTietSanPham = chiTietSanPhamRepository.save(chiTietSanPham);
                    savedVariants.add(chiTietSanPham);
                    logger.info("Created new ChiTietSanPham for IMEI: {}", imei);
                }
            }
        }
        for (ChiTietSanPham existingVariant : existingVariants) {
            boolean existsInRequest = request.getVariants().stream()
                    .anyMatch(variant -> variant.getImeiList().contains(existingVariant.getIdImel().getImel()));
            if (!existsInRequest) {
                existingVariant.setDeleted(true);
                chiTietSanPhamRepository.save(existingVariant);
                logger.info("Marked ChiTietSanPham as deleted for IMEI: {}", existingVariant.getIdImel().getImel());
            }
        }
        return savedVariants;
    }

    private ChiTietSanPhamResponse buildChiTietSanPhamResponse(SanPham sanPham, ChiTietSanPhamRequest request, Map<Integer, String> colorImageUrls) {
        return ChiTietSanPhamResponse.builder()
                .id(sanPham.getId())
                .tenSanPham(sanPham.getTenSanPham())
                .idNhaSanXuat(sanPham.getIdNhaSanXuat().getId())
                .idPin(sanPham.getIdPin().getId())
                .idCongNgheManHinh(sanPham.getCongNgheManHinh().getId())
                .idHoTroBoNhoNgoai(sanPham.getIdHoTroBoNhoNgoai() != null ? sanPham.getIdHoTroBoNhoNgoai().getId() : null)
                .idCpu(sanPham.getIdCpu().getId())
                .idGpu(sanPham.getIdGpu().getId())
                .idCumCamera(sanPham.getIdCumCamera().getId())
                .idHeDieuHanh(sanPham.getIdHeDieuHanh().getId())
                .idChiSoKhangBuiVaNuoc(sanPham.getIdChiSoKhangBuiVaNuoc().getId())
                .idThietKe(sanPham.getIdThietKe().getId())
                .idSim(sanPham.getIdSim().getId())
                .idHoTroCongNgheSac(sanPham.getHoTroCongNgheSac().getId())
                .idCongNgheMang(sanPham.getIdCongNgheMang().getId())
                .ghiChu(request.getGhiChu())
                .variants(request.getVariants().stream().map(variant -> {
                    String imageUrl = colorImageUrls.getOrDefault(variant.getIdMauSac(), colorImageUrls.values().iterator().next());
                    return ChiTietSanPhamResponse.VariantResponse.builder()
                            .idMauSac(variant.getIdMauSac())
                            .idRam(variant.getIdRam())
                            .idBoNhoTrong(variant.getIdBoNhoTrong())
                            .donGia(variant.getDonGia())
                            .imeiList(variant.getImeiList())
                            .imageUrl(imageUrl)
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }

    private void validateRequest(ChiTietSanPhamRequest request) {
        logger.info("Validating ChiTietSanPhamRequest");
        if (request.getTenSanPham() == null || request.getTenSanPham().trim().isEmpty()) {
            logger.error("Validation failed: Tên sản phẩm không được để trống");
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (request.getVariants() == null || request.getVariants().isEmpty()) {
            logger.error("Validation failed: Danh sách biến thể không được để trống");
            throw new IllegalArgumentException("Danh sách biến thể không được để trống");
        }
        for (ChiTietSanPhamRequest.VariantRequest variant : request.getVariants()) {
            if (variant.getIdMauSac() == null) {
                logger.error("Validation failed: Màu sắc không được để trống");
                throw new IllegalArgumentException("Màu sắc không được để trống");
            }
            if (variant.getIdRam() == null) {
                logger.error("Validation failed: RAM không được để trống");
                throw new IllegalArgumentException("RAM không được để trống");
            }
            if (variant.getIdBoNhoTrong() == null) {
                logger.error("Validation failed: Bộ nhớ trong không được để trống");
                throw new IllegalArgumentException("Bộ nhớ trong không được để trống");
            }
            if (variant.getDonGia() == null || variant.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Validation failed: Đơn giá phải lớn hơn 0");
                throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
            }
            if (variant.getImeiList() == null || variant.getImeiList().isEmpty()) {
                logger.error("Validation failed: IMEI không được để trống cho biến thể");
                throw new IllegalArgumentException("IMEI không được để trống cho biến thể");
            }
            for (String imei : variant.getImeiList()) {
                if (imei == null || imei.length() != 15) {
                    logger.error("Validation failed: IMEI phải có đúng 15 chữ số: {}", imei);
                    throw new IllegalArgumentException("IMEI phải có đúng 15 chữ số: " + imei);
                }
                if (imelRepository.existsByImelAndDeletedFalse(imei)) {
                    logger.error("Validation failed: IMEI đã tồn tại: {}", imei);
                    throw new IllegalArgumentException("IMEI đã tồn tại: " + imei);
                }
            }
        }
    }

    @Override
    public List<ChiTietSanPhamDetailResponse> getProductDetailsBySanPhamId(Integer idSanPham) {
        logger.info("Fetching product details for idSanPham: {}", idSanPham);
        if (idSanPham == null || idSanPham <= 0) {
            logger.error("Invalid idSanPham: {}", idSanPham);
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        List<Object[]> results = chiTietSanPhamRepository.findProductDetailsBySanPhamId(idSanPham);
        if (results.isEmpty()) {
            logger.warn("No product details found for idSanPham: {}", idSanPham);
            throw new IllegalArgumentException("Không tìm thấy chi tiết sản phẩm cho ID: " + idSanPham);
        }
        return results.stream().map(result -> ChiTietSanPhamDetailResponse.builder()
                .tenSanPham((String) result[0])
                .maSanPham((String) result[1])
                .imei((String) result[2])
                .mauSac((String) result[3])
                .dungLuongRam((String) result[4])
                .dungLuongBoNhoTrong((String) result[5])
                .donGia((BigDecimal) result[6])
                .deleted((Boolean) result[7])
                .imageUrl((String) result[8]) // Ánh xạ trường imageUrl
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ChiTietSanPhamResponse createChiTietSanPham(ChiTietSanPhamRequest request, List<MultipartFile> images,
                                                       List<String> existingImageUrls) {
        logger.info("Processing createChiTietSanPham with request: {}", request);
        validateRequest(request);
        SanPham sanPham = createOrFindSanPham(request);
        Map<Integer, String> colorImageUrls = processImageUploads(images, request.getVariants());
        createProductVariants(request, sanPham, colorImageUrls);
        return buildChiTietSanPhamResponse(sanPham, request, colorImageUrls);
    }

    @Transactional
    @Override
    public ChiTietSanPhamResponse updateChiTietSanPham(Integer id, ChiTietSanPhamRequest request,
                                                       List<MultipartFile> images, List<String> existingImageUrls) {
        logger.info("Processing updateChiTietSanPham with id: {} and request: {}", id, request);
        validateRequest(request);
        SanPham sanPham = updateExistingSanPham(id, request);
        Map<Integer, String> colorImageUrls = processImageUpdates(images, existingImageUrls, request.getVariants());
        updateProductVariants(id, request, sanPham, colorImageUrls);
        return buildChiTietSanPhamResponse(sanPham, request, colorImageUrls);
    }
}