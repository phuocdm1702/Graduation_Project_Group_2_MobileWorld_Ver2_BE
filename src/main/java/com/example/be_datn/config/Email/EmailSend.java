package com.example.be_datn.config.Email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSend {
    private final JavaMailSender mailSender;

    public EmailSend(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // CSS chung cho tất cả email
    private static final String EMAIL_CSS = """
            body {
                margin: 0;
                padding: 0;
                font-family: Arial, sans-serif;
                background-color: #f5f6fa;
            }
            .container {
                width: 100%;
                max-width: 600px;
                margin: 0 auto;
                padding: 20px;
            }
            .header {
                background-color: #34d399;
                padding: 20px;
                text-align: center;
                border-radius: 8px 8px 0 0;
            }
            .header h1 {
                margin: 0;
                font-size: 24px;
                color: #ffffff;
                font-weight: bold;
            }
            .content {
                background-color: #ffffff;
                padding: 20px;
                border-radius: 0 0 8px 8px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                text-align: center;
            }
            .thank-you-section {
                text-align: center;
            }
            .thank-you-section h2 {
                font-family: 'Dancing Script', cursive;
                font-size: 36px;
                color: #34d399;
                margin: 0;
                line-height: 1.2;
            }
            .thank-you-section p {
                color: #4a4a4a;
                font-size: 14px;
                margin: 8px 0 20px;
            }
            .coupon-details {
                background-color: #fff5e6;
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
                text-align: left;
            }
            .coupon-details p {
                margin: 8px 0;
                color: #4a4a4a;
                font-size: 14px;
            }
            .coupon-details .coupon-code {
                font-size: 18px;
                font-weight: bold;
            }
            .coupon-details p strong {
                color: #34d399;
                font-weight: bold;
            }
            .discount-box {
                background: linear-gradient(135deg, #34d399, #10b981);
                color: #ffffff;
                padding: 10px;
                border-radius: 8px;
                font-size: 18px;
                font-weight: bold;
                margin-bottom: 20px;
            }
            .cta-button {
                display: inline-block;
                padding: 12px 24px;
                background-color: #34d399;
                color: #ffffff;
                text-decoration: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 500;
            }
            .footer {
                text-align: center;
                padding: 20px 0;
                color: #4a4a4a;
                font-size: 12px;
            }
            .footer a {
                color: #34d399;
                text-decoration: none;
            }
            @media only screen and (max-width: 768px) {
                .container {
                    padding: 15px;
                }
                .header h1 {
                    font-size: 20px;
                }
                .thank-you-section h2 {
                    font-size: 30px;
                }
                .thank-you-section p {
                    font-size: 13px;
                }
                .coupon-details p {
                    font-size: 13px;
                }
                .coupon-details .coupon-code {
                    font-size: 16px;
                }
                .discount-box {
                    font-size: 16px;
                }
                .cta-button {
                    padding: 10px 20px;
                    font-size: 14px;
                }
            }
            @media only screen and (max-width: 575px) {
                .container {
                    padding: 10px;
                }
                .header h1 {
                    font-size: 18px;
                }
                .thank-you-section h2 {
                    font-size: 24px;
                }
                .thank-you-section p {
                    font-size: 12px;
                }
                .coupon-details p {
                    font-size: 12px;
                }
                .coupon-details .coupon-code {
                    font-size: 14px;
                }
                .discount-box {
                    font-size: 14px;
                    padding: 8px;
                }
                .cta-button {
                    padding: 8px 16px;
                    font-size: 12px;
                }
                .footer {
                    font-size: 10px;
                }
            }
            """;

    public void sendDiscountEmail(String toEmail, String maPhieu, String tenPhieu, String ngayHetHan, String loaiPhieuGiamGia, double phanTram, double soTienGiamToiDa, double hoaDonToiThieu, String moTa) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎉 MobileWorld Tặng Bạn Phiếu Giảm Giá Đặc Biệt!");

            String discountText;
            if ("Phần trăm".equals(loaiPhieuGiamGia)) {
                double maxDiscount = (phanTram / 100.0) * hoaDonToiThieu;
                discountText = String.format("Tặng quý khách ưu đãi %.0f%% (Tối đa %,.0fđ)", phanTram, maxDiscount);
            } else { // Loại tiền mặt
                discountText = String.format("Tặng quý khách ưu đãi giảm %,.0fđ", soTienGiamToiDa);
            }

            System.out.println("hoaDonToiThieu: " + hoaDonToiThieu);

            String htmlContent = String.format("""
                    <!DOCTYPE html>
                    <html lang="vi">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Phiếu Giảm Giá</title>
                        <link href="https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&display=swap" rel="stylesheet">
                        <style>
                            %s
                        </style>
                    </head>
                    <div class="container">
                        <div class="header">
                            <h1>Mobile World</h1>
                        </div>
                        <div class="content">
                            <div class="thank-you-section">
                                <h2>Cảm ơn!</h2>
                                <p>MobileWorld gửi tặng quý khách phiếu giảm giá cho lần mua sắm tiếp theo.</p>
                            </div>
                            <div class="discount-box">
                                %s
                            </div>
                            <div class="coupon-details">
                                <p><strong>Tên phiếu:</strong> %s</p>
                                <p><strong>Mã phiếu:</strong> <span class="coupon-code">%s</span></p>
                                <p><strong>Hạn sử dụng:</strong> %s</p>
                                <p><strong>Áp dụng cho hóa đơn tối thiểu:</strong> %,.0f</p>
                                <p><strong>Mô tả:</strong> %s</p>
                                <p><strong>Lưu ý:</strong> Mã chỉ sử dụng được 1 lần cho khách hàng có đăng ký nhận tin email từ MobileWorld (ứng với 1 số điện thoại đã đăng ký). Sử dụng mã giảm giá để được giảm giá trực tiếp, và tất cả mã giảm giá đều không có giá trị quy đổi thành tiền mặt.</p>
                            </div>
                            <a href="http://localhost:3000/" class="cta-button">MUA SẮM NGAY</a>
                        </div>
                        <div class="footer">
                            <p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>
                            <p>Trân trọng, MobileWorld</p>
                            <p>Liên hệ: <a href="mailto:support@mobileworld.com.vn">support@mobileworld.com.vn</a></p>
                        </div>
                    </div>
                    """, EMAIL_CSS, discountText, tenPhieu, maPhieu, ngayHetHan, hoaDonToiThieu, moTa);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Email đã được gửi tới: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email tới " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendUpdateDiscountEmail(String toEmail, String maPhieu, String ngayHetHan, String loaiPhieuGiamGia, double phanTram, double STGTD) {
        sendDiscountEmail(toEmail, maPhieu, "Phiếu giảm giá của bạn đã được cập nhật", ngayHetHan, loaiPhieuGiamGia, phanTram, STGTD, 0, "Nội dung phiếu đã được cập nhật.");
    }

    public void sendRevokeDiscountEmail(String toEmail, String maPhieu) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("❌ Thông báo thu hồi phiếu giảm giá từ MobileWorld");

            String htmlContent = """
                    <!DOCTYPE html>
                    <html lang="vi">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Thu hồi Phiếu Giảm Giá</title>
                        <link href="https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&display=swap" rel="stylesheet">
                        <style>
                            %s
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>Mobile World</h1>
                            </div>
                            <div class="content">
                                <div class="thank-you-section">
                                    <h2>Thông báo</h2>
                                    <p>Phiếu giảm giá của bạn đã bị thu hồi bởi MobileWorld.</p>
                                </div>
                                <div class="coupon-details">
                                    <p class="coupon-code"><strong>Mã phiếu:</strong> %s</p>
                                    <p>Chúng tôi rất tiếc phải thông báo rằng phiếu giảm giá này không còn hiệu lực nữa.</p>
                                </div>
                                <p>
                                    <a href="http://localhost:3000" class="cta-button">Xem các ưu đãi khác</a>
                                </p>
                            </div>
                            <div class="footer">
                                <p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>
                                <p>Trân trọng, <strong>MobileWorld</strong></p>
                                <p>Liên hệ: <a href="mailto:support@mobileworld.com.vn">support@mobileworld.com.vn</a></p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """.formatted(EMAIL_CSS, maPhieu);

            String plainTextContent = """
                    Thông báo từ MobileWorld!

                    Phiếu giảm giá của bạn đã bị thu hồi:
                    - Mã phiếu: %s

                    Chúng tôi rất tiếc phải thông báo rằng phiếu giảm giá này không còn hiệu lực nữa.
                    Nhấn vào liên kết để xem các ưu đãi khác: http://localhost:3000/phieu-giam-gia

                    Trân trọng,
                    MobileWorld
                    Liên hệ: support@mobileworld.com.vn
                    """.formatted(maPhieu);

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(message);
            System.out.println("Email thu hồi đã được gửi tới: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email thu hồi tới " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
