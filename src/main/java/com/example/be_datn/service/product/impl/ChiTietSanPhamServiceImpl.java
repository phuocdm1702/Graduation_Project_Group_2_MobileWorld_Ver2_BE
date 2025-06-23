package com.example.be_datn.service.product.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamDetailResponse;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import com.example.be_datn.entity.product.*;
import com.example.be_datn.repository.product.*;
import com.example.be_datn.service.product.ChiTietSanPhamService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private String generateImageHash(byte[] imageBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(imageBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate image hash: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi tạo hash cho ảnh: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public ChiTietSanPhamResponse createChiTietSanPham(ChiTietSanPhamRequest request, List<MultipartFile> images, List<String> existingImageUrls, List<String> imageHashes) {
        logger.info("Processing createChiTietSanPham with request: {}", request);

        // Validate input
        validateRequest(request);

        // Create or find existing SanPham
        SanPham sanPham = sanPhamRepository.findByTenSanPhamAndDeletedFalse(request.getTenSanPham())
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

        // Prepare product group key
        String productGroupKey = "SP" + sanPham.getId();

        // Process images
        Map<Integer, String> colorImageUrls = new HashMap<>();
        List<AnhSanPham> savedImages = new ArrayList<>();
        int hashIndex = 0;

        if (images != null && !images.isEmpty() && existingImageUrls != null && imageHashes != null) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                String providedHash = imageHashes.get(hashIndex);
                String existingUrl = existingImageUrls.size() > i ? existingImageUrls.get(i) : null;

                if (image.isEmpty()) {
                    logger.warn("Empty image file detected at index {}", i);
                    throw new IllegalArgumentException("File ảnh không được để trống");
                }

                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                AnhSanPham anhSanPham;

                if (existingUrl != null) {
                    // Use existing image
                    Optional<AnhSanPham> existingImage = anhSanPhamRepository.findByProductGroupKeyAndHash(productGroupKey, providedHash);
                    if (existingImage.isPresent()) {
                        anhSanPham = existingImage.get();
                        logger.info("Reusing existing image with hash: {} for productGroupKey: {}", providedHash, productGroupKey);
                    } else {
                        // Create new AnhSanPham for existing URL
                        anhSanPham = AnhSanPham.builder()
                                .tenAnh(fileName)
                                .duongDan(existingUrl)
                                .hash(providedHash)
                                .productGroupKey(productGroupKey)
                                .deleted(false)
                                .build();
                        anhSanPham = anhSanPhamRepository.save(anhSanPham);
                        logger.info("Saved new AnhSanPham for existing URL: {}", existingUrl);
                    }
                } else {
                    // Validate hash
                    String computedHash;
                    try {
                        computedHash = generateImageHash(image.getBytes());
                    } catch (IOException e) {
                        logger.error("Failed to read image bytes: {}", e.getMessage());
                        throw new RuntimeException("Lỗi khi đọc file ảnh: " + e.getMessage());
                    }

                    if (!computedHash.equals(providedHash)) {
                        logger.warn("Hash mismatch for image: {} (provided: {}, computed: {})", fileName, providedHash, computedHash);
                        throw new IllegalArgumentException("Hash ảnh không khớp: " + fileName);
                    }

                    // Check if image exists
                    Optional<AnhSanPham> existingImage = anhSanPhamRepository.findByProductGroupKeyAndHash(productGroupKey, computedHash);
                    if (existingImage.isPresent()) {
                        anhSanPham = existingImage.get();
                        logger.info("Reusing existing image with hash: {} for productGroupKey: {}", computedHash, productGroupKey);
                    } else {
                        // Upload to Cloudinary
                        try {
                            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                                    "public_id", "product_" + UUID.randomUUID().toString(),
                                    "resource_type", "image",
                                    "quality", "auto:low"
                            ));
                            String imageUrl = uploadResult.get("secure_url").toString();
                            anhSanPham = AnhSanPham.builder()
                                    .tenAnh(fileName)
                                    .duongDan(imageUrl)
                                    .hash(computedHash)
                                    .productGroupKey(productGroupKey)
                                    .deleted(false)
                                    .build();
                            anhSanPham = anhSanPhamRepository.save(anhSanPham);
                            logger.info("Uploaded new image: {} with URL: {}", fileName, imageUrl);
                        } catch (IOException e) {
                            logger.error("Failed to upload image to Cloudinary: {}", e.getMessage());
                            throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
                        }
                    }
                }
                savedImages.add(anhSanPham);
                Integer mauSacId = request.getVariants().get(hashIndex).getIdMauSac();
                colorImageUrls.put(mauSacId, anhSanPham.getDuongDan());
                hashIndex++;
            }
        } else {
            logger.info("No images provided, using default image");
            String defaultHash = UUID.randomUUID().toString();
            Optional<AnhSanPham> existingDefaultImage = anhSanPhamRepository.findByProductGroupKeyAndHash(productGroupKey, defaultHash);
            AnhSanPham defaultAnhSanPham;
            if (existingDefaultImage.isPresent()) {
                defaultAnhSanPham = existingDefaultImage.get();
                logger.info("Reusing existing default image for productGroupKey: {}", productGroupKey);
            } else {
                defaultAnhSanPham = AnhSanPham.builder()
                        .tenAnh("default_image.jpg")
                        .duongDan("https://default-image-url.com/default.jpg")
                        .hash(defaultHash)
                        .productGroupKey(productGroupKey)
                        .deleted(false)
                        .build();
                defaultAnhSanPham = anhSanPhamRepository.save(defaultAnhSanPham);
            }
            savedImages.add(defaultAnhSanPham);
            colorImageUrls.put(request.getVariants().get(0).getIdMauSac(), defaultAnhSanPham.getDuongDan());
        }

        // Save variants and associated data
        ChiTietSanPhamResponse response = null;
        for (ChiTietSanPhamRequest.VariantRequest variant : request.getVariants()) {
            MauSac mauSac = mauSacRepository.findById(variant.getIdMauSac())
                    .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
            Ram ram = ramRepository.findById(variant.getIdRam())
                    .orElseThrow(() -> new IllegalArgumentException("RAM không tồn tại"));
            BoNhoTrong boNhoTrong = boNhoTrongRepository.findById(variant.getIdBoNhoTrong())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ nhớ trong không tồn tại"));

            String imageUrl = colorImageUrls.getOrDefault(variant.getIdMauSac(), savedImages.get(0).getDuongDan());
            Optional<AnhSanPham> anhSanPhamOpt = savedImages.stream()
                    .filter(img -> img.getDuongDan().equals(imageUrl))
                    .findFirst();
            AnhSanPham anhSanPham = anhSanPhamOpt.orElse(savedImages.get(0));

            String imei = variant.getImeiList().get(0); // Take first IMEI
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
            logger.info("Saved ChiTietSanPham for IMEI: {}", imei);
        }

        logger.info("Successfully created ChiTietSanPham with SanPham ID: {}", sanPham.getId());
        return response;
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
            String imei = variant.getImeiList().get(0);
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
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChiTietSanPhamResponse updateChiTietSanPham(Integer id, ChiTietSanPhamRequest request, List<MultipartFile> images, List<String> existingImageUrls, List<String> imageHashes) {
        logger.info("Processing updateChiTietSanPham with id: {} and request: {}", id, request);

        // Validate input
        validateRequest(request);

        // Find existing SanPham
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));

        // Update SanPham details
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
        sanPham.setUpdatedBy(1); // Assuming a default user ID for now
        sanPhamRepository.save(sanPham);

        // Prepare product group key
        String productGroupKey = "SP" + sanPham.getId();

        // Process images
        Map<Integer, String> colorImageUrls = new HashMap<>();
        List<AnhSanPham> savedImages = new ArrayList<>();
        int hashIndex = 0;

        if (images != null && !images.isEmpty() && existingImageUrls != null && imageHashes != null) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                String providedHash = imageHashes.get(hashIndex);
                String existingUrl = existingImageUrls.size() > i ? existingImageUrls.get(i) : null;

                if (image.isEmpty()) {
                    logger.warn("Empty image file detected at index {}", i);
                    throw new IllegalArgumentException("File ảnh không được để trống");
                }

                String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                AnhSanPham anhSanPham;

                if (existingUrl != null) {
                    Optional<AnhSanPham> existingImage = anhSanPhamRepository.findByProductGroupKeyAndHash(productGroupKey, providedHash);
                    if (existingImage.isPresent()) {
                        anhSanPham = existingImage.get();
                        logger.info("Reusing existing image with hash: {} for productGroupKey: {}", providedHash, productGroupKey);
                    } else {
                        anhSanPham = AnhSanPham.builder()
                                .tenAnh(fileName)
                                .duongDan(existingUrl)
                                .hash(providedHash)
                                .productGroupKey(productGroupKey)
                                .deleted(false)
                                .build();
                        anhSanPham = anhSanPhamRepository.save(anhSanPham);
                        logger.info("Saved new AnhSanPham for existing URL: {}", existingUrl);
                    }
                } else {
                    String computedHash;
                    try {
                        computedHash = generateImageHash(image.getBytes());
                    } catch (IOException e) {
                        logger.error("Failed to read image bytes: {}", e.getMessage());
                        throw new RuntimeException("Lỗi khi đọc file ảnh: " + e.getMessage());
                    }

                    if (!computedHash.equals(providedHash)) {
                        logger.warn("Hash mismatch for image: {} (provided: {}, computed: {})", fileName, providedHash, computedHash);
                        throw new IllegalArgumentException("Hash ảnh không khớp: " + fileName);
                    }

                    Optional<AnhSanPham> existingImage = anhSanPhamRepository.findByProductGroupKeyAndHash(productGroupKey, computedHash);
                    if (existingImage.isPresent()) {
                        anhSanPham = existingImage.get();
                        logger.info("Reusing existing image with hash: {} for productGroupKey: {}", computedHash, productGroupKey);
                    } else {
                        try {
                            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                                    "public_id", "product_" + UUID.randomUUID().toString(),
                                    "resource_type", "image",
                                    "quality", "auto:low"
                            ));
                            String imageUrl = uploadResult.get("secure_url").toString();
                            anhSanPham = AnhSanPham.builder()
                                    .tenAnh(fileName)
                                    .duongDan(imageUrl)
                                    .hash(computedHash)
                                    .productGroupKey(productGroupKey)
                                    .deleted(false)
                                    .build();
                            anhSanPham = anhSanPhamRepository.save(anhSanPham);
                            logger.info("Uploaded new image: {} with URL: {}", fileName, imageUrl);
                        } catch (IOException e) {
                            logger.error("Failed to upload image to Cloudinary: {}", e.getMessage());
                            throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
                        }
                    }
                }
                savedImages.add(anhSanPham);
                Integer mauSacId = request.getVariants().get(hashIndex).getIdMauSac();
                colorImageUrls.put(mauSacId, anhSanPham.getDuongDan());
                hashIndex++;
            }
        } else {
            logger.info("No new images provided, checking existing images");
            List<AnhSanPham> existingImages = anhSanPhamRepository.findByProductGroupKeyAndDeletedFalse(productGroupKey);
            if (existingImages.isEmpty()) {
                String defaultHash = UUID.randomUUID().toString();
                AnhSanPham defaultAnhSanPham = AnhSanPham.builder()
                        .tenAnh("default_image.jpg")
                        .duongDan("https://default-image-url.com/default.jpg")
                        .hash(defaultHash)
                        .productGroupKey(productGroupKey)
                        .deleted(false)
                        .build();
                defaultAnhSanPham = anhSanPhamRepository.save(defaultAnhSanPham);
                savedImages.add(defaultAnhSanPham);
                colorImageUrls.put(request.getVariants().get(0).getIdMauSac(), defaultAnhSanPham.getDuongDan());
            } else {
                savedImages.addAll(existingImages);
                colorImageUrls.put(request.getVariants().get(0).getIdMauSac(), existingImages.get(0).getDuongDan());
            }
        }

        // Update variants
        List<ChiTietSanPham> existingVariants = chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(id, false);
        for (ChiTietSanPhamRequest.VariantRequest variant : request.getVariants()) {
            MauSac mauSac = mauSacRepository.findById(variant.getIdMauSac())
                    .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
            Ram ram = ramRepository.findById(variant.getIdRam())
                    .orElseThrow(() -> new IllegalArgumentException("RAM không tồn tại"));
            BoNhoTrong boNhoTrong = boNhoTrongRepository.findById(variant.getIdBoNhoTrong())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ nhớ trong không tồn tại"));

            String imageUrl = colorImageUrls.getOrDefault(variant.getIdMauSac(), savedImages.get(0).getDuongDan());
            Optional<AnhSanPham> anhSanPhamOpt = savedImages.stream()
                    .filter(img -> img.getDuongDan().equals(imageUrl))
                    .findFirst();
            AnhSanPham anhSanPham = anhSanPhamOpt.orElse(savedImages.get(0));

            String imei = variant.getImeiList().get(0);
            Optional<ChiTietSanPham> existingChiTietSanPham = existingVariants.stream()
                    .filter(ctsp -> ctsp.getIdImel().getImel().equals(imei))
                    .findFirst();

            if (existingChiTietSanPham.isPresent()) {
                // Update existing variant
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
                logger.info("Updated ChiTietSanPham for IMEI: {}", imei);
            } else {
                // Create new variant if IMEI doesn't exist
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
                chiTietSanPhamRepository.save(chiTietSanPham);
                logger.info("Created new ChiTietSanPham for IMEI: {}", imei);
            }
        }

        // Mark removed variants as deleted
        for (ChiTietSanPham existingVariant : existingVariants) {
            boolean existsInRequest = request.getVariants().stream()
                    .anyMatch(variant -> variant.getImeiList().contains(existingVariant.getIdImel().getImel()));
            if (!existsInRequest) {
                existingVariant.setDeleted(true);
                chiTietSanPhamRepository.save(existingVariant);
                logger.info("Marked ChiTietSanPham as deleted for IMEI: {}", existingVariant.getIdImel().getImel());
            }
        }

        // Build response
        ChiTietSanPhamResponse response = ChiTietSanPhamResponse.builder()
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
                    String imageUrl = colorImageUrls.getOrDefault(variant.getIdMauSac(), savedImages.get(0).getDuongDan());
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

        logger.info("Successfully updated ChiTietSanPham with SanPham ID: {}", sanPham.getId());
        return response;
    }
}