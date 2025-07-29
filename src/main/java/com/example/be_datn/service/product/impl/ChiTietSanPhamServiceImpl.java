package com.example.be_datn.service.product.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.request.ChiTietSanPhamUpdateRequest;
import com.example.be_datn.dto.product.request.IVariantRequest;
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

    private Map<Integer, String> processImageUploads(List<MultipartFile> images, List<? extends IVariantRequest> variants) {
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
                .imageUrl((String) result[8])
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
    public ChiTietSanPhamResponse updateChiTietSanPham(Integer id, ChiTietSanPhamUpdateRequest request,
                                                       List<MultipartFile> images, List<String> existingImageUrls) {
        logger.info("Processing updateChiTietSanPham with id: {} and request: {}", id, request);

        // Validate request
        if (request.getVariants() == null || request.getVariants().isEmpty()) {
            logger.error("Validation failed: Danh sách biến thể không được để trống");
            throw new IllegalArgumentException("Danh sách biến thể không được để trống");
        }
        if (request.getVariants().size() != 1) {
            logger.error("Validation failed: Chỉ được cung cấp một biến thể để cập nhật chi tiết sản phẩm");
            throw new IllegalArgumentException("Chỉ được cung cấp một biến thể để cập nhật chi tiết sản phẩm");
        }

        ChiTietSanPhamUpdateRequest.VariantRequest variant = request.getVariants().get(0);

        // Validate variant
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
            logger.error("Validation failed: IMEI không được để trống");
            throw new IllegalArgumentException("IMEI không được để trống");
        }
        if (variant.getImeiList().size() != 1) {
            logger.error("Validation failed: Chỉ được cung cấp một IMEI để cập nhật chi tiết sản phẩm");
            throw new IllegalArgumentException("Chỉ được cung cấp một IMEI để cập nhật chi tiết sản phẩm");
        }

        // Tìm chi tiết sản phẩm cần cập nhật
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Chi tiết sản phẩm không tồn tại với ID: {}", id);
                    return new IllegalArgumentException("Chi tiết sản phẩm không tồn tại với ID: " + id);
                });

        // Xử lý IMEI
        String imei = variant.getImeiList().get(0);
        if (!imei.equals(chiTietSanPham.getIdImel().getImel())) {
            logger.info("IMEI thay đổi: từ {} thành {}", chiTietSanPham.getIdImel().getImel(), imei);
            // Kiểm tra định dạng IMEI
            if (imei == null || imei.length() != 15 || !imei.matches("\\d{15}")) {
                logger.error("Validation failed: IMEI phải là 15 chữ số: {}", imei);
                throw new IllegalArgumentException("IMEI phải là 15 chữ số: " + imei);
            }
            // Kiểm tra trùng lặp IMEI
            if (imelRepository.existsByImelAndDeletedFalse(imei)) {
                logger.error("Validation failed: IMEI đã tồn tại: {}", imei);
                throw new IllegalArgumentException("IMEI đã tồn tại: " + imei);
            }
            // Cập nhật IMEI
            Imel imel = chiTietSanPham.getIdImel();
            imel.setImel(imei);
            imelRepository.save(imel);
        } else {
            logger.info("IMEI không thay đổi, giữ nguyên: {}", imei);
        }

        // Xử lý ảnh
        Map<Integer, String> colorImageUrls = new HashMap<>();
        if (images != null && !images.isEmpty()) {
            logger.info("Processing new image uploads");
            colorImageUrls = processImageUploads(images, request.getVariants());
        } else if (existingImageUrls != null && !existingImageUrls.isEmpty()) {
            logger.info("Using existing image URLs");
            colorImageUrls.put(variant.getIdMauSac(), existingImageUrls.get(0));
        } else {
            logger.info("No new images or existing URLs provided, keeping current image");
            colorImageUrls.put(variant.getIdMauSac(), chiTietSanPham.getIdAnhSanPham().getDuongDan());
        }

        // Cập nhật thông tin chi tiết sản phẩm
        MauSac mauSac = mauSacRepository.findById(variant.getIdMauSac())
                .orElseThrow(() -> {
                    logger.error("Màu sắc không tồn tại: {}", variant.getIdMauSac());
                    return new IllegalArgumentException("Màu sắc không tồn tại");
                });
        Ram ram = ramRepository.findById(variant.getIdRam())
                .orElseThrow(() -> {
                    logger.error("RAM không tồn tại: {}", variant.getIdRam());
                    return new IllegalArgumentException("RAM không tồn tại");
                });
        BoNhoTrong boNhoTrong = boNhoTrongRepository.findById(variant.getIdBoNhoTrong())
                .orElseThrow(() -> {
                    logger.error("Bộ nhớ trong không tồn tại: {}", variant.getIdBoNhoTrong());
                    return new IllegalArgumentException("Bộ nhớ trong không tồn tại");
                });

        // Cập nhật ảnh sản phẩm
        String imageUrl = colorImageUrls.get(variant.getIdMauSac());
        if (imageUrl != null) {
            AnhSanPham anhSanPham = anhSanPhamRepository.findByDuongDan(imageUrl)
                    .orElseThrow(() -> {
                        logger.error("Ảnh sản phẩm không tồn tại: {}", imageUrl);
                        return new IllegalArgumentException("Ảnh sản phẩm không tồn tại");
                    });
            chiTietSanPham.setIdAnhSanPham(anhSanPham);
        }

        // Cập nhật thông tin chi tiết sản phẩm
        chiTietSanPham.setIdMauSac(mauSac);
        chiTietSanPham.setIdRam(ram);
        chiTietSanPham.setIdBoNhoTrong(boNhoTrong);
        chiTietSanPham.setGiaBan(variant.getDonGia());
        chiTietSanPham.setGhiChu(request.getGhiChu());
        chiTietSanPham.setUpdatedAt(new Date());
        chiTietSanPham.setUpdatedBy(1); // Thay bằng ID người dùng thực tế

        // Lưu thay đổi
        chiTietSanPham = chiTietSanPhamRepository.save(chiTietSanPham);
        logger.info("Successfully updated ChiTietSanPham with id: {}", id);

        // Xây dựng response
        return ChiTietSanPhamResponse.builder()
                .id(chiTietSanPham.getId())
                .ghiChu(chiTietSanPham.getGhiChu())
                .variants(List.of(
                        ChiTietSanPhamResponse.VariantResponse.builder()
                                .idMauSac(chiTietSanPham.getIdMauSac().getId())
                                .idRam(chiTietSanPham.getIdRam().getId())
                                .idBoNhoTrong(chiTietSanPham.getIdBoNhoTrong().getId())
                                .donGia(chiTietSanPham.getGiaBan())
                                .imeiList(List.of(chiTietSanPham.getIdImel().getImel()))
                                .imageUrl(chiTietSanPham.getIdAnhSanPham().getDuongDan())
                                .build()
                ))
                .build();
    }

    @Override
    public Integer findChiTietSanPhamIdByImei(String imei) {
        logger.info("Finding ChiTietSanPham ID for IMEI: {}", imei);
        if (imei == null || imei.length() != 15 || !imei.matches("\\d{15}")) {
            logger.error("Invalid IMEI format: {}", imei);
            throw new IllegalArgumentException("IMEI phải là 15 chữ số: " + imei);
        }
        Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepository.findByImel(imei);
        if (chiTietSanPhamOpt.isEmpty()) {
            logger.warn("No ChiTietSanPham found for IMEI: {}", imei);
            throw new IllegalArgumentException("Không tìm thấy chi tiết sản phẩm cho IMEI: " + imei);
        }
        return chiTietSanPhamOpt.get().getId();
    }
}