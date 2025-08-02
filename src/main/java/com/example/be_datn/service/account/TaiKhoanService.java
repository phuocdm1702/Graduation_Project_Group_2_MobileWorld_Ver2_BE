package com.example.be_datn.service.account;


import com.example.be_datn.entity.account.TaiKhoan;

import java.util.List;
import java.util.Map;

public interface TaiKhoanService {
    String findById(Integer idTK);

    TaiKhoan trangthaiKH(Integer id);

    TaiKhoan trangthaiNV(Integer id);



    List<TaiKhoan> getall();

    Integer getCustomerIdByTaiKhoan(String login);
  
    TaiKhoan findByUsername(String username);

    Map<String, Object> dangnhap(String tenDangNhap, String matKhau);

    Map<String, Object> dangnhapWeb(String login, String matKhau);
}
