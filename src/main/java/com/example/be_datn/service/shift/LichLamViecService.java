package com.example.be_datn.service.shift;

import com.example.be_datn.dto.giao_ca.LichLamViecDTO;
import com.example.be_datn.entity.account.NhanVien;
import com.example.be_datn.entity.giao_ca.LichLamViec;
import com.example.be_datn.repository.account.NhanVien.NhanVienRepository;
import com.example.be_datn.repository.shift.LichLamViecRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LichLamViecService {

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    public LichLamViecDTO convertToDTO(LichLamViec lichLamViec) {
        LichLamViecDTO dto = new LichLamViecDTO();
        dto.setId(lichLamViec.getId());
        if (lichLamViec.getIdNhanVien() != null) {
            dto.setIdNhanVien(lichLamViec.getIdNhanVien().getId());
        }
        dto.setCaLam(lichLamViec.getCaLam());
        dto.setNgayLam(lichLamViec.getNgayLam());
        dto.setDeleted(lichLamViec.getDeleted());
        return dto;
    }

    public LichLamViec convertToEntity(LichLamViecDTO dto) {
        LichLamViec lichLamViec = new LichLamViec();
        lichLamViec.setId(dto.getId());
        if (dto.getIdNhanVien() != null) {
            NhanVien nhanVien = nhanVienRepository.findById(dto.getIdNhanVien()).orElse(null);
            lichLamViec.setIdNhanVien(nhanVien);
        }
        lichLamViec.setCaLam(dto.getCaLam());
        lichLamViec.setNgayLam(dto.getNgayLam());
        lichLamViec.setDeleted(dto.getDeleted());
        return lichLamViec;
    }

    public List<LichLamViecDTO> getAll(Integer idNhanVien, LocalDate ngayLam) {
        return lichLamViecRepository.findWithFilters(idNhanVien, ngayLam).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public LichLamViecDTO create(LichLamViecDTO lichLamViecDTO) {
        LichLamViec lichLamViec = convertToEntity(lichLamViecDTO);
        return convertToDTO(lichLamViecRepository.save(lichLamViec));
    }

    public LichLamViecDTO update(Integer id, LichLamViecDTO lichLamViecDTO) {
        LichLamViec lichLamViec = convertToEntity(lichLamViecDTO);
        lichLamViec.setId(id);
        return convertToDTO(lichLamViecRepository.save(lichLamViec));
    }

    public void delete(Integer id) {
        LichLamViec lichLamViec = lichLamViecRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch làm việc không tìm thấy với id: " + id));
        lichLamViec.setDeleted(true);
        lichLamViecRepository.save(lichLamViec);
    }

    public void importExcel(MultipartFile file) throws IOException {
        List<LichLamViec> lichLamViecList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Get first sheet

            // Assuming first row is header, skip it
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;

                LichLamViec lichLamViec = new LichLamViec();

                // Read employee ID (assuming column 0)
                Cell employeeIdCell = row.getCell(0);
                if (employeeIdCell != null && employeeIdCell.getCellType() == CellType.NUMERIC) {
                    Integer employeeId = (int) employeeIdCell.getNumericCellValue();
                    NhanVien nhanVien = nhanVienRepository.findById(employeeId).orElse(null);
                    lichLamViec.setIdNhanVien(nhanVien);
                }

                // Read shift (assuming column 1)
                Cell shiftCell = row.getCell(1);
                if (shiftCell != null) {
                    lichLamViec.setCaLam(shiftCell.getStringCellValue());
                }

                // Read work date (assuming column 2)
                Cell workDateCell = row.getCell(2);
                if (workDateCell != null && workDateCell.getCellType() == CellType.NUMERIC) {
                    Date date = DateUtil.getJavaDate(workDateCell.getNumericCellValue());
                    lichLamViec.setNgayLam(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }

                // Set default values for deleted, createdAt, updatedAt if not in Excel
                lichLamViec.setDeleted(false); // Default to false
                // createdAt and updatedAt will be handled by JPA annotations or database defaults

                lichLamViecList.add(lichLamViec);
            }
        }
        lichLamViecRepository.saveAll(lichLamViecList);
    }
}