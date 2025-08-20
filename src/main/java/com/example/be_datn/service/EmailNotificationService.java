package com.example.be_datn.service;

import com.example.be_datn.config.Email.EmailSend;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.entity.discount.PhieuGiamGia;
import com.example.be_datn.entity.discount.PhieuGiamGiaCaNhan;
import com.example.be_datn.service.account.KhachHangServices;
import com.example.be_datn.service.account.TaiKhoanService;
import com.example.be_datn.service.discount.PhieuGiamGiaCaNhanService;
import com.example.be_datn.service.discount.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class EmailNotificationService {

    @Autowired
    private KhachHangServices khachHangService;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private PhieuGiamGiaCaNhanService phieuGiamGiaCaNhanService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private EmailSend emailSend;

    @Async
    public void sendVoucherEmailAsync(Integer khachHangID, Integer phieuGiamGiaID) {
        try {
            KhachHang kh = khachHangService.findById(khachHangID);
            PhieuGiamGia pgg = phieuGiamGiaService.getById(phieuGiamGiaID).orElse(null);
            
            if (kh != null && pgg != null) {
                PhieuGiamGiaCaNhan pggcn = phieuGiamGiaCaNhanService.findByKhachHangAndPhieuGiamGia(kh, pgg);
                if (pggcn == null) {
                    // If personal voucher not found, it might be a public voucher or an error.
                    // For now, we'll assume it's a public voucher and try to send email with general voucher code.
                    // Or, if it's a private voucher and pggcn is null, it means something went wrong.
                    // For this task, we'll proceed with sending email if pggcn is found.
                    // If it's a public voucher, the email sending logic might be different or not needed here.
                    // For now, we'll only send for private vouchers where pggcn is created.
                    System.err.println("PhieuGiamGiaCaNhan not found for customer " + khachHangID + " and voucher " + phieuGiamGiaID + ". Skipping email.");
                    return;
                }

                String email = taiKhoanService.findById(khachHangID);
                if (email != null && !email.trim().isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String ngayHH = dateFormat.format(pgg.getNgayKetThuc());
                    emailSend.sendDiscountEmail(email, pggcn.getMa(), pgg.getTenPhieuGiamGia(), ngayHH, pgg.getPhanTramGiamGia(), pgg.getSoTienGiamToiDa(), pgg.getHoaDonToiThieu(), pgg.getMoTa());
                }
            } else {
                System.err.println("Customer or Voucher not found for email notification. Customer ID: " + khachHangID + ", Voucher ID: " + phieuGiamGiaID);
            }
        } catch (Exception e) {
            System.err.println("Failed to send email asynchronously to customer " + khachHangID + " for voucher " + phieuGiamGiaID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
