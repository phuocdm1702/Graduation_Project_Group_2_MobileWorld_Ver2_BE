package com.example.be_datn.service.product.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.be_datn.dto.product.request.ChiTietSanPhamRequest;
import com.example.be_datn.dto.product.response.ChiTietSanPhamResponse;
import com.example.be_datn.entity.product.*;
import com.example.be_datn.repository.product.*;
import com.example.be_datn.service.product.ChiTietSanPhamService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class ChiTietSanPhamServiceImpl implements ChiTietSanPhamService {

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

    @Override
    public ChiTietSanPhamResponse createChiTietSanPham(ChiTietSanPhamRequest dto, List<MultipartFile> images) throws IOException {
        validateInput(dto, images);
        SanPham sanPham = createOrUpdateProduct(dto);
        if (sanPham == null || sanPham.getId() == null) {
            throw new IllegalStateException("Không thể tạo hoặc cập nhật sản phẩm");
        }

        String productGroupKey = generateProductGroupKey(dto);
        System.out.println("Generated productGroupKey: " + productGroupKey);

        List<AnhSanPham> anhSanPhams = uploadAndSaveImages(images, productGroupKey);
        if (anhSanPhams == null || anhSanPhams.isEmpty()) {
            throw new IllegalStateException("Không có ảnh nào được lưu");
        }
        List<Imel> imels = createAndSaveImels(dto.getVariants());
        List<ChiTietSanPham> chiTietSanPhams = createVariants(dto, sanPham, anhSanPhams, imels);
        List<ChiTietSanPham> savedChiTietSanPhams = chiTietSanPhamRepository.saveAll(chiTietSanPhams);
        if (savedChiTietSanPhams == null || savedChiTietSanPhams.isEmpty()) {
            throw new IllegalStateException("Không thể lưu các biến thể sản phẩm");
        }
        return new ChiTietSanPhamResponse(
                sanPham.getId(),
                savedChiTietSanPhams.stream().map(ChiTietSanPham::getId).collect(Collectors.toList()),
                anhSanPhams.stream().map(AnhSanPham::getId).collect(Collectors.toList())
        );
    }

    @Override
    public void updateChiTietSanPham(Integer id, ChiTietSanPhamRequest dto) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm với ID: " + id));

        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            ChiTietSanPhamRequest.VariantRequestDTO variant = dto.getVariants().get(0);
            if (variant.getIdMauSac() != null) {
                chiTietSanPham.setIdMauSac(getEntity(mauSacRepository, variant.getIdMauSac(), "Màu sắc"));
            }
            if (variant.getIdRam() != null) {
                chiTietSanPham.setIdRam(getEntity(ramRepository, variant.getIdRam(), "RAM"));
            }
            if (variant.getIdBoNhoTrong() != null) {
                chiTietSanPham.setIdBoNhoTrong(getEntity(boNhoTrongRepository, variant.getIdBoNhoTrong(), "Bộ nhớ trong"));
            }
            if (variant.getDonGia() != null) {
                chiTietSanPham.setGiaBan(variant.getDonGia());
            }
        }
        if (dto.getGhiChu() != null) {
            chiTietSanPham.setGhiChu(dto.getGhiChu());
        }
        chiTietSanPham.setUpdatedAt(new Date());
        chiTietSanPham.setUpdatedBy(1);

        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public void updatePrice(Integer id, BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá không hợp lệ: " + newPrice);
        }
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(id, false);
        if (chiTietSanPhams.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        for (ChiTietSanPham chiTietSanPham : chiTietSanPhams) {
            chiTietSanPham.setGiaBan(newPrice);
            chiTietSanPham.setUpdatedAt(new Date());
            chiTietSanPham.setUpdatedBy(1);
        }
        chiTietSanPhamRepository.saveAll(chiTietSanPhams);
    }

    @Override
    public List<ChiTietSanPhamResponse> getChiTietSanPhamBySanPhamId(Integer sanPhamId) {
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(sanPhamId, false);
        return mapToResponseDTOList(chiTietSanPhams);
    }

    @Override
    public Page<ChiTietSanPhamResponse> getChiTietSanPhamDetails(Integer sanPhamId, String keyword, String status,
                                                                    Integer idMauSac, Integer idBoNhoTrong, Integer idRam,
                                                                    BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<ChiTietSanPham> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("idSanPham").get("id"), sanPhamId));
            predicates.add(cb.equal(root.get("deleted"), status == null || "active".equals(status) ? false : true));

            if (keyword != null && !keyword.isEmpty()) {
                String keywordPattern = "%" + keyword.toLowerCase() + "%";
                List<Predicate> keywordPredicates = new ArrayList<>();
                keywordPredicates.add(cb.like(cb.lower(root.get("ma")), keywordPattern));
                keywordPredicates.add(cb.like(cb.lower(root.get("idSanPham").get("tenSanPham")), keywordPattern));
                keywordPredicates.add(cb.like(cb.lower(root.get("idMauSac").get("mauSac")), keywordPattern));
                keywordPredicates.add(cb.like(cb.lower(root.get("idRam").get("dungLuongRam")), keywordPattern));
                keywordPredicates.add(cb.like(cb.lower(root.get("idBoNhoTrong").get("dungLuongBoNhoTrong")), keywordPattern));
                keywordPredicates.add(cb.like(cb.lower(root.get("giaBan").as(String.class)), keywordPattern));
                predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
            }

            if (idMauSac != null) {
                predicates.add(cb.equal(root.get("idMauSac").get("id"), idMauSac));
            }
            if (idBoNhoTrong != null) {
                predicates.add(cb.equal(root.get("idBoNhoTrong").get("id"), idBoNhoTrong));
            }
            if (idRam != null) {
                predicates.add(cb.equal(root.get("idRam").get("id"), idRam));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("giaBan"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("giaBan"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<ChiTietSanPham> allChiTietSanPhams = chiTietSanPhamRepository.findAll((Sort) spec);

        Map<String, List<ChiTietSanPham>> grouped = allChiTietSanPhams.stream()
                .collect(Collectors.groupingBy(ctsp ->
                        ctsp.getIdRam().getId() + "_" +
                                ctsp.getIdBoNhoTrong().getId() + "_" +
                                ctsp.getIdMauSac().getId() + "_" +
                                ctsp.getGiaBan().toString()
                ));

        List<ChiTietSanPhamResponse> dtos = grouped.entrySet().stream().map(entry -> {
            List<ChiTietSanPham> group = entry.getValue();
            ChiTietSanPham first = group.get(0);
            ChiTietSanPhamResponse dto = new ChiTietSanPhamResponse();
            dto.setId(first.getId());
            SanPham sanPham = first.getIdSanPham();
            dto.setIdSanPham(sanPham.getId());
            dto.setMaSanPham(sanPham.getMa());
            dto.setIdNhaSanXuat(sanPham.getIdNhaSanXuat().getId());
            dto.setIdPin(sanPham.getIdPin().getId());
            dto.setCongNgheManHinh(sanPham.getCongNgheManHinh().getId());
            dto.setIdCpu(sanPham.getIdCpu().getId());
            dto.setIdGpu(sanPham.getIdGpu().getId());
            dto.setIdCumCamera(sanPham.getIdCumCamera().getId());
            dto.setIdHeDieuHanh(sanPham.getIdHeDieuHanh().getId());
            dto.setIdThietKe(sanPham.getIdThietKe().getId());
            dto.setIdSim(sanPham.getIdSim().getId());
            dto.setHoTroCongNgheSac(sanPham.getHoTroCongNgheSac().getId());
            dto.setIdCongNgheMang(sanPham.getIdCongNgheMang().getId());
            dto.setTenSanPham(sanPham.getTenSanPham());
            dto.setMa(first.getMa());
            dto.setIdHoTroBoNhoNgoai(sanPham.getIdHoTroBoNhoNgoai() != null ? sanPham.getIdHoTroBoNhoNgoai().getId() : null);
            dto.setIdChiSoKhangBuiVaNuoc(sanPham.getIdChiSoKhangBuiVaNuoc() != null ? sanPham.getIdChiSoKhangBuiVaNuoc().getId() : null);
            dto.setGhiChu(first.getGhiChu());
            dto.setGiaBan(first.getGiaBan());
            dto.setCreatedAt(first.getCreatedAt());
            dto.setCreatedBy(first.getCreatedBy());
            dto.setUpdatedAt(first.getUpdatedAt());
            dto.setUpdatedBy(first.getUpdatedBy());
            dto.setDeleted(first.getDeleted());
            ChiTietSanPhamResponse.VariantResponseDTO variantDTO = new ChiTietSanPhamResponse.VariantResponseDTO();
            variantDTO.setIdMauSac(first.getIdMauSac().getId());
            variantDTO.setMauSac(first.getIdMauSac().getMauSac());
            variantDTO.setIdRam(first.getIdRam().getId());
            variantDTO.setDungLuongRam(first.getIdRam().getDungLuongRam());
            variantDTO.setIdBoNhoTrong(first.getIdBoNhoTrong().getId());
            variantDTO.setDungLuongBoNhoTrong(first.getIdBoNhoTrong().getDungLuongBoNhoTrong());
            variantDTO.setDonGia(first.getGiaBan());
            variantDTO.setImageIndex(first.getIdAnhSanPham() != null ? anhSanPhamRepository.findAll().indexOf(first.getIdAnhSanPham()) : 0);
            variantDTO.setQuantity(group.size());
            List<String> imeiList = group.stream()
                    .map(ctsp -> ctsp.getIdImel().getImel())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            variantDTO.setImeiList(imeiList);
            if (imeiList.size() != group.size()) {
                System.err.println("Lỗi: Số lượng IMEI (" + imeiList.size() + ") không khớp với số lượng bản ghi (" + group.size() + ") trong nhóm!");
            }
            dto.setVariants(List.of(variantDTO));
            return dto;
        }).collect(Collectors.toList());

        int start = Math.min(page * size, dtos.size());
        int end = Math.min(start + size, dtos.size());
        List<ChiTietSanPhamResponse> pagedDtos = dtos.subList(start, end);
        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

    @Override
    public Map<String, BigDecimal> getPriceRange(Integer sanPhamId) {
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findByIdSanPhamIdAndDeletedFalse(sanPhamId, false);
        if (chiTietSanPhams.isEmpty()) {
            return Map.of(
                    "minPrice", BigDecimal.ZERO,
                    "maxPrice", BigDecimal.valueOf(10000000)
            );
        }
        BigDecimal minPrice = chiTietSanPhams.stream()
                .map(ChiTietSanPham::getGiaBan)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = chiTietSanPhams.stream()
                .map(ChiTietSanPham::getGiaBan)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.valueOf(10000000));
        return Map.of(
                "minPrice", minPrice,
                "maxPrice", maxPrice
        );
    }

    private void validateInput(ChiTietSanPhamRequest dto, List<MultipartFile> images) {
        if (images == null || images.isEmpty() || images.stream().anyMatch(MultipartFile::isEmpty)) {
            throw new IllegalArgumentException("Phải cung cấp ít nhất một ảnh hợp lệ cho chi tiết sản phẩm");
        }
        if (dto.getVariants() == null || dto.getVariants().isEmpty()) {
            throw new IllegalArgumentException("Phải cung cấp ít nhất một biến thể cho chi tiết sản phẩm");
        }
        if (dto.getTenSanPham() == null || dto.getTenSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (dto.getIdNhaSanXuat() == null || !nhaSanXuatRepository.existsById(dto.getIdNhaSanXuat())) {
            throw new IllegalArgumentException("Nhà sản xuất không hợp lệ");
        }
        if (dto.getIdPin() == null || !pinRepository.existsById(dto.getIdPin())) {
            throw new IllegalArgumentException("Pin không hợp lệ");
        }
        if (dto.getCongNgheManHinh() == null || !congNgheManHinhRepository.existsById(dto.getCongNgheManHinh())) {
            throw new IllegalArgumentException("Công nghệ màn hình không hợp lệ");
        }
        if (dto.getIdCpu() == null || !cpuRepository.existsById(dto.getIdCpu())) {
            throw new IllegalArgumentException("CPU không hợp lệ");
        }
        if (dto.getIdGpu() == null || !gpuRepository.existsById(dto.getIdGpu())) {
            throw new IllegalArgumentException("GPU không hợp lệ");
        }
        if (dto.getIdCumCamera() == null || !cumCameraRepository.existsById(dto.getIdCumCamera())) {
            throw new IllegalArgumentException("Cụm camera không hợp lệ");
        }
        if (dto.getIdHeDieuHanh() == null || !heDieuHanhRepository.existsById(dto.getIdHeDieuHanh())) {
            throw new IllegalArgumentException("Hệ điều hành không hợp lệ");
        }
        if (dto.getIdThietKe() == null || !thietKeRepository.existsById(dto.getIdThietKe())) {
            throw new IllegalArgumentException("Thiết kế không hợp lệ");
        }
        if (dto.getIdSim() == null || !simRepository.existsById(dto.getIdSim())) {
            throw new IllegalArgumentException("Sim không hợp lệ");
        }
        if (dto.getHoTroCongNgheSac() == null || !hoTroCongNgheSacRepository.existsById(dto.getHoTroCongNgheSac())) {
            throw new IllegalArgumentException("Hỗ trợ công nghệ sạc không hợp lệ");
        }
        if (dto.getIdCongNgheMang() == null || !congNgheMangRepository.existsById(dto.getIdCongNgheMang())) {
            throw new IllegalArgumentException("Công nghệ mạng không hợp lệ");
        }
        for (ChiTietSanPhamRequest.VariantRequestDTO variant : dto.getVariants()) {
            if (variant.getIdMauSac() == null || !mauSacRepository.existsById(variant.getIdMauSac())) {
                throw new IllegalArgumentException("Màu sắc không hợp lệ");
            }
            if (variant.getIdRam() == null || !ramRepository.existsById(variant.getIdRam())) {
                throw new IllegalArgumentException("RAM không hợp lệ");
            }
            if (variant.getIdBoNhoTrong() == null || !boNhoTrongRepository.existsById(variant.getIdBoNhoTrong())) {
                throw new IllegalArgumentException("Bộ nhớ trong không hợp lệ");
            }
            if (variant.getDonGia() == null) {
                throw new IllegalArgumentException("Đơn giá không được để trống");
            }
        }
    }

    private SanPham createOrUpdateProduct(ChiTietSanPhamRequest dto) {
        Optional<SanPham> existingSanPham = sanPhamRepository.findByTenSanPhamAndDeletedFalse(dto.getTenSanPham());
        SanPham sanPham;
        if (existingSanPham.isPresent()) {
            sanPham = existingSanPham.get();
            updateSanPhamFields(sanPham, dto);
        } else {
            sanPham = new SanPham();
            sanPham.setTenSanPham(dto.getTenSanPham());
            sanPham.setMa(null);
            updateSanPhamFields(sanPham, dto);
        }
        return sanPhamRepository.save(sanPham);
    }

    private void updateSanPhamFields(SanPham sanPham, ChiTietSanPhamRequest dto) {
        sanPham.setIdNhaSanXuat(getEntity(nhaSanXuatRepository, dto.getIdNhaSanXuat(), "Nhà sản xuất"));
        sanPham.setIdPin(getEntity(pinRepository, dto.getIdPin(), "Pin"));
        sanPham.setCongNgheManHinh(getEntity(congNgheManHinhRepository, dto.getCongNgheManHinh(), "Công nghệ màn hình"));
        sanPham.setIdCpu(getEntity(cpuRepository, dto.getIdCpu(), "CPU"));
        sanPham.setIdGpu(getEntity(gpuRepository, dto.getIdGpu(), "GPU"));
        sanPham.setIdCumCamera(getEntity(cumCameraRepository, dto.getIdCumCamera(), "Cụm camera"));
        sanPham.setIdHeDieuHanh(getEntity(heDieuHanhRepository, dto.getIdHeDieuHanh(), "Hệ điều hành"));
        sanPham.setIdThietKe(getEntity(thietKeRepository, dto.getIdThietKe(), "Thiết kế"));
        sanPham.setIdSim(getEntity(simRepository, dto.getIdSim(), "Sim"));
        sanPham.setHoTroCongNgheSac(getEntity(hoTroCongNgheSacRepository, dto.getHoTroCongNgheSac(), "Hỗ trợ công nghệ sạc"));
        sanPham.setIdCongNgheMang(getEntity(congNgheMangRepository, dto.getIdCongNgheMang(), "Công nghệ mạng"));
        sanPham.setIdHoTroBoNhoNgoai(dto.getIdHoTroBoNhoNgoai() != null ?
                getEntity(hoTroBoNhoNgoaiRepository, dto.getIdHoTroBoNhoNgoai(), "Hỗ trợ bộ nhớ ngoài") : null);
        sanPham.setIdChiSoKhangBuiVaNuoc(dto.getIdChiSoKhangBuiVaNuoc() != null ?
                getEntity(chiSoKhangBuiVaNuocRepository, dto.getIdChiSoKhangBuiVaNuoc(), "Chỉ số kháng bụi nước") : null);
        sanPham.setDeleted(false);
        sanPham.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : new Date());
        sanPham.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : 1);
        sanPham.setUpdatedAt(new Date());
        sanPham.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
    }

    private <T> T getEntity(JpaRepository<T, Integer> repository, Integer id, String entityName) {
        if (id == null) {
            throw new IllegalArgumentException(entityName + " ID không được null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(entityName + " với ID " + id + " không tồn tại"));
    }

    private List<AnhSanPham> uploadAndSaveImages(List<MultipartFile> images, String productGroupKey) throws IOException {
        List<AnhSanPham> anhSanPhams = new ArrayList<>();

        if (images == null || images.isEmpty()) {
            System.out.println("No images provided for upload.");
            return anhSanPhams;
        }

        MultipartFile representativeImage = images.get(0);
        if (representativeImage.isEmpty()) {
            System.out.println("Representative image is empty.");
            return anhSanPhams;
        }

        validateImage(representativeImage);

        String imageHash = calculateImageHash(representativeImage.getBytes());
        System.out.println("Calculated image hash: " + imageHash);

        String imageUrl = checkExistingImage(imageHash, productGroupKey);

        if (imageUrl == null) {
            imageUrl = checkCloudinaryForExistingImage(productGroupKey);
        }

        if (imageUrl == null) {
            String originalFilename = StringUtils.cleanPath(representativeImage.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            try {
                Map<String, Object> uploadParams = ObjectUtils.asMap(
                        "public_id", "products/" + uniqueFilename,
                        "resource_type", "image",
                        "tags", Arrays.asList(productGroupKey)
                );

                Map<String, Object> uploadResult = cloudinary.uploader().upload(representativeImage.getBytes(), uploadParams);
                imageUrl = (String) uploadResult.get("secure_url");
                System.out.println("Uploaded new image to Cloudinary: " + imageUrl);

                AnhSanPham anh = new AnhSanPham();
                anh.setMa(UUID.randomUUID().toString());
                anh.setTenAnh(originalFilename);
                anh.setDuongDan(imageUrl);
                anh.setHash(imageHash);
                anh.setProductGroupKey(productGroupKey);
                anh.setDeleted(false);
                anhSanPhams.add(anh);
            } catch (Exception e) {
                System.err.println("Error while uploading image to Cloudinary: " + e.getMessage());
                e.printStackTrace();
                throw new IOException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage(), e);
            }
        } else {
            Optional<AnhSanPham> existingAnhSanPham = anhSanPhamRepository.findByDuongDan(imageUrl);
            AnhSanPham anh;
            if (existingAnhSanPham.isPresent()) {
                anh = existingAnhSanPham.get();
                System.out.println("Reused existing AnhSanPham entity: " + anh.getId());
            } else {
                anh = new AnhSanPham();
                anh.setMa(UUID.randomUUID().toString());
                anh.setTenAnh(representativeImage.getOriginalFilename());
                anh.setDuongDan(imageUrl);
                anh.setHash(imageHash);
                anh.setProductGroupKey(productGroupKey);
                anh.setDeleted(false);
            }
            anhSanPhams.add(anh);
            System.out.println("Reused existing image: " + imageUrl);
        }

        return anhSanPhamRepository.saveAll(anhSanPhams);
    }

    private String calculateImageHash(byte[] imageBytes) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(imageBytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Lỗi khi tính hash ảnh: " + e.getMessage(), e);
        }
    }

    private String checkExistingImage(String imageHash, String productGroupKey) throws IOException {
        try {
            Optional<AnhSanPham> existingImage = anhSanPhamRepository.findByHashAndProductGroupKey(imageHash, productGroupKey);
            if (existingImage.isPresent()) {
                String imageUrl = existingImage.get().getDuongDan();
                System.out.println("Found existing image in database: " + imageUrl);
                return imageUrl;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error while checking image in database: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Lỗi khi kiểm tra ảnh trong cơ sở dữ liệu: " + e.getMessage(), e);
        }
    }

    private String checkCloudinaryForExistingImage(String productGroupKey) throws IOException {
        try {
            Map<String, Object> searchParams = ObjectUtils.asMap(
                    "expression", "tags:" + productGroupKey,
                    "max_results", 1
            );

            Map<String, Object> searchResult = cloudinary.search().expression("tags:" + productGroupKey).maxResults(1).execute();
            List<Map<String, Object>> resources = (List<Map<String, Object>>) searchResult.get("resources");

            if (!resources.isEmpty()) {
                String imageUrl = (String) resources.get(0).get("secure_url");
                System.out.println("Found existing image in Cloudinary: " + imageUrl);
                return imageUrl;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error while searching Cloudinary: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Lỗi khi tìm kiếm ảnh trên Cloudinary: " + e.getMessage(), e);
        }
    }

    private void validateImage(MultipartFile image) throws IOException {
        if (!image.getContentType().startsWith("image/")) {
            System.err.println("Invalid image file: " + image.getOriginalFilename() + ", content type: " + image.getContentType());
            throw new IOException("File không phải là ảnh hợp lệ");
        }
    }

    private List<Imel> createAndSaveImels(List<ChiTietSanPhamRequest.VariantRequestDTO> variants) {
        List<Imel> imels = new ArrayList<>();
        for (ChiTietSanPhamRequest.VariantRequestDTO variant : variants) {
            if (variant.getImeiList() != null && !variant.getImeiList().isEmpty()) {
                for (String imei : variant.getImeiList()) {
                    Optional<Imel> existingImel = imelRepository.findByImel(imei);
                    if (existingImel.isPresent()) {
                        imels.add(existingImel.get());
                    } else {
                        Imel imel = new Imel();
                        imel.setMa(null);
                        imel.setImel(imei);
                        imel.setDeleted(false);
                        imels.add(imel);
                    }
                }
            }
        }
        List<Imel> newImels = imels.stream()
                .filter(imel -> imel.getId() == null)
                .collect(Collectors.toList());
        if (!newImels.isEmpty()) {
            imelRepository.saveAll(newImels);
        }
        return imels;
    }

    private List<ChiTietSanPham> createVariants(ChiTietSanPhamRequest dto, SanPham sanPham,
                                                List<AnhSanPham> anhSanPhams, List<Imel> imels) {
        List<ChiTietSanPham> chiTietSanPhams = new ArrayList<>();
        int imelIndex = 0;
        for (ChiTietSanPhamRequest.VariantRequestDTO variant : dto.getVariants()) {
            if (variant.getImeiList() != null && !variant.getImeiList().isEmpty()) {
                for (String imei : variant.getImeiList()) {
                    ChiTietSanPham chiTiet = new ChiTietSanPham();
                    chiTiet.setIdSanPham(sanPham);
                    Imel imelToAssign;
                    if (imelIndex < imels.size()) {
                        imelToAssign = imels.get(imelIndex);
                        imelIndex++;
                    } else {
                        imelToAssign = new Imel();
                        imelToAssign.setMa("DEFAULT-" + UUID.randomUUID().toString());
                        imelToAssign.setImel("N/A");
                        imelToAssign.setDeleted(false);
                        imelToAssign = imelRepository.save(imelToAssign);
                    }
                    chiTiet.setIdImel(imelToAssign);
                    chiTiet.setIdMauSac(getEntity(mauSacRepository, variant.getIdMauSac(), "Màu sắc"));
                    chiTiet.setIdRam(getEntity(ramRepository, variant.getIdRam(), "RAM"));
                    chiTiet.setIdBoNhoTrong(getEntity(boNhoTrongRepository, variant.getIdBoNhoTrong(), "Bộ nhớ trong"));
                    chiTiet.setMa(null);
                    chiTiet.setGiaBan(variant.getDonGia() != null ? variant.getDonGia() : dto.getGiaBan());
                    chiTiet.setGhiChu(dto.getGhiChu());
                    chiTiet.setDeleted(false);
                    chiTiet.setCreatedAt(new Date());
                    chiTiet.setCreatedBy(1);
                    chiTiet.setUpdatedAt(new Date());
                    chiTiet.setUpdatedBy(1);
                    Integer imageIndex = variant.getImageIndex();
                    if (imageIndex != null && imageIndex >= 0 && imageIndex < anhSanPhams.size()) {
                        chiTiet.setIdAnhSanPham(anhSanPhams.get(imageIndex));
                    } else {
                        chiTiet.setIdAnhSanPham(anhSanPhams.get(0));
                    }
                    chiTietSanPhams.add(chiTiet);
                }
            } else {
                ChiTietSanPham chiTiet = new ChiTietSanPham();
                chiTiet.setIdSanPham(sanPham);
                Imel imelToAssign = new Imel();
                imelToAssign.setMa(null);
                imelToAssign.setImel("N/A");
                imelToAssign.setDeleted(false);
                imelToAssign = imelRepository.save(imelToAssign);
                chiTiet.setIdImel(imelToAssign);
                chiTiet.setIdMauSac(getEntity(mauSacRepository, variant.getIdMauSac(), "Màu sắc"));
                chiTiet.setIdRam(getEntity(ramRepository, variant.getIdRam(), "RAM"));
                chiTiet.setIdBoNhoTrong(getEntity(boNhoTrongRepository, variant.getIdBoNhoTrong(), "Bộ nhớ trong"));
                chiTiet.setMa(null);
                chiTiet.setGiaBan(variant.getDonGia() != null ? variant.getDonGia() : dto.getGiaBan());
                chiTiet.setGhiChu(dto.getGhiChu());
                chiTiet.setDeleted(false);
                chiTiet.setCreatedAt(new Date());
                chiTiet.setCreatedBy(1);
                chiTiet.setUpdatedAt(new Date());
                chiTiet.setUpdatedBy(1);
                Integer imageIndex = variant.getImageIndex();
                if (imageIndex != null && imageIndex >= 0 && imageIndex < anhSanPhams.size()) {
                    chiTiet.setIdAnhSanPham(anhSanPhams.get(imageIndex));
                } else {
                    chiTiet.setIdAnhSanPham(anhSanPhams.get(0));
                }
                chiTietSanPhams.add(chiTiet);
            }
        }
        return chiTietSanPhams;
    }

    private String generateProductGroupKey(ChiTietSanPhamRequest dto) {
        String tenSanPham = dto.getTenSanPham() != null ? dto.getTenSanPham().toLowerCase().replace(" ", "_") : "unknown";
        String mauSac = "";
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            Integer idMauSac = dto.getVariants().get(0).getIdMauSac();
            if (idMauSac != null) {
                MauSac mauSacEntity = mauSacRepository.findById(idMauSac).orElse(null);
                mauSac = mauSacEntity != null ? mauSacEntity.getMauSac().toLowerCase().replace(" ", "_") : "unknown";
            }
        }
        return tenSanPham + "_" + mauSac;
    }

    private List<ChiTietSanPhamResponse> mapToResponseDTOList(List<ChiTietSanPham> chiTietSanPhams) {
        return chiTietSanPhams.stream().map(chiTiet -> {
            ChiTietSanPhamResponse dto = new ChiTietSanPhamResponse();
            dto.setId(chiTiet.getId());
            SanPham sanPham = chiTiet.getIdSanPham();
            dto.setIdSanPham(sanPham.getId());
            dto.setMaSanPham(sanPham.getMa());
            dto.setIdNhaSanXuat(sanPham.getIdNhaSanXuat().getId());
            dto.setIdPin(sanPham.getIdPin().getId());
            dto.setCongNgheManHinh(sanPham.getCongNgheManHinh().getId());
            dto.setIdCpu(sanPham.getIdCpu().getId());
            dto.setIdGpu(sanPham.getIdGpu().getId());
            dto.setIdCumCamera(sanPham.getIdCumCamera().getId());
            dto.setIdHeDieuHanh(sanPham.getIdHeDieuHanh().getId());
            dto.setIdThietKe(sanPham.getIdThietKe().getId());
            dto.setIdSim(sanPham.getIdSim().getId());
            dto.setHoTroCongNgheSac(sanPham.getHoTroCongNgheSac().getId());
            dto.setIdCongNgheMang(sanPham.getIdCongNgheMang().getId());
            dto.setTenSanPham(sanPham.getTenSanPham());
            dto.setMa(chiTiet.getMa());
            dto.setIdHoTroBoNhoNgoai(sanPham.getIdHoTroBoNhoNgoai() != null ? sanPham.getIdHoTroBoNhoNgoai().getId() : null);
            dto.setIdChiSoKhangBuiVaNuoc(sanPham.getIdChiSoKhangBuiVaNuoc() != null ? sanPham.getIdChiSoKhangBuiVaNuoc().getId() : null);
            dto.setGhiChu(chiTiet.getGhiChu());
            dto.setGiaBan(chiTiet.getGiaBan());
            dto.setCreatedAt(chiTiet.getCreatedAt());
            dto.setCreatedBy(chiTiet.getCreatedBy());
            dto.setUpdatedAt(chiTiet.getUpdatedAt());
            dto.setUpdatedBy(chiTiet.getUpdatedBy());
            dto.setDeleted(chiTiet.getDeleted());
            ChiTietSanPhamResponse.VariantResponseDTO variantDTO = new ChiTietSanPhamResponse.VariantResponseDTO();
            variantDTO.setIdImel(chiTiet.getIdImel());
            variantDTO.setIdMauSac(chiTiet.getIdMauSac() != null ? chiTiet.getIdMauSac().getId() : null);
            variantDTO.setMauSac(chiTiet.getIdMauSac() != null ? chiTiet.getIdMauSac().getMauSac() : null);
            variantDTO.setIdRam(chiTiet.getIdRam() != null ? chiTiet.getIdRam().getId() : null);
            variantDTO.setDungLuongRam(chiTiet.getIdRam() != null ? chiTiet.getIdRam().getDungLuongRam() : null);
            variantDTO.setIdBoNhoTrong(chiTiet.getIdBoNhoTrong() != null ? chiTiet.getIdBoNhoTrong().getId() : null);
            variantDTO.setDungLuongBoNhoTrong(chiTiet.getIdBoNhoTrong() != null ? chiTiet.getIdBoNhoTrong().getDungLuongBoNhoTrong() : null);
            variantDTO.setImageIndex(chiTiet.getIdAnhSanPham() != null ? anhSanPhamRepository.findAll().indexOf(chiTiet.getIdAnhSanPham()) : 0);
            variantDTO.setDonGia(chiTiet.getGiaBan());
            variantDTO.setImeiList(chiTiet.getIdImel() != null ? List.of(chiTiet.getIdImel().getImel()) : List.of());
            variantDTO.setQuantity(1); // Single item for this mapping
            dto.setVariants(List.of(variantDTO));
            return dto;
        }).collect(Collectors.toList());
    }
}
