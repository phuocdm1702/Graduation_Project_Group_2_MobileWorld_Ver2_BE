package com.example.be_datn.repository.account.KhachHang;

import com.example.be_datn.entity.account.KhachHang;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KhachHangCustomRepository {
    @Query("SELECT k from KhachHang k where k.ten like %?1% OR k.idTaiKhoan.soDienThoai like %?1%")
    List<KhachHang> searchBh(String keyword);
}
