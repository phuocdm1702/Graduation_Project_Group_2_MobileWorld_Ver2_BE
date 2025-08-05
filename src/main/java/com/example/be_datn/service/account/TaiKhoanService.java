package com.example.be_datn.service.account;


import com.example.be_datn.entity.account.TaiKhoan;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface TaiKhoanService {
    String findById(Integer idTK);

    TaiKhoan trangthaiKH(Integer id);

    TaiKhoan trangthaiNV(Integer id);



    List<TaiKhoan> getall();

    Integer getCustomerIdByTaiKhoan(String login);
  
    TaiKhoan findByUsername(String username);

    public Map<String, Object> dangnhap(String login, String matKhau, HttpServletRequest request);

    Map<String, Object> dangnhapWeb(String login, String matKhau);
}
