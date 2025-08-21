package com.example.be_datn.common.Email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailServices {
    private final JavaMailSender mailSender;

    public EmailServices(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    @Async
    public void sendWelcomeEmail(String to, String employeeName, String email, String password) throws MessagingException {
        try {
            // Tạo MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Thiết lập thông tin email
            helper.setTo(to);
            helper.setSubject("🌿 Chào mừng bạn gia nhập MobileWorld!");
            helper.setFrom("no-reply@mobileworld.com.vn");

            // Nội dung HTML của email với tông xanh lá + đen
            String htmlContent = """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Chào Mừng Nhân Viên Mới</title>
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    font-family: 'Poppins', Arial, sans-serif;
                    background-color: #f0f2f5;
                    color: #222;
                }
                .container {
                    width: 100%;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .header {
                    background: linear-gradient(135deg, #28a745, #1e7e34);
                    padding: 25px;
                    text-align: center;
                    border-radius: 12px 12px 0 0;
                }
                .header h1 {
                    margin: 0;
                    font-size: 26px;
                    color: #fff;
                    font-weight: 600;
                }
                .content {
                    background-color: #ffffff;
                    padding: 25px;
                    border-radius: 0 0 12px 12px;
                    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.08);
                    text-align: center;
                }
                .thank-you-section h2 {
                    font-size: 28px;
                    color: #28a745;
                    margin: 0 0 10px;
                    font-weight: 600;
                }
                .thank-you-section p {
                    font-size: 14px;
                    color: #444;
                    margin-bottom: 20px;
                }
                .highlight-box {
                    background: #e8f5e9;
                    color: #155724;
                    padding: 15px;
                    border-radius: 8px;
                    margin: 20px 0;
                    font-size: 16px;
                    font-weight: 500;
                }
                .account-info {
                    background: #f9f9f9;
                    border: 1px solid #e0e0e0;
                    padding: 15px;
                    border-radius: 8px;
                    margin-bottom: 20px;
                    text-align: left;
                }
                .account-info p {
                    margin: 8px 0;
                    font-size: 14px;
                    color: #333;
                }
                .account-info strong {
                    color: #28a745;
                }
                .cta-button {
                    display: inline-block;
                    padding: 12px 24px;
                    background: linear-gradient(135deg, #28a745, #218838);
                    color: #fff !important;
                    text-decoration: none;
                    border-radius: 8px;
                    font-size: 15px;
                    font-weight: 500;
                    transition: background 0.3s ease;
                }
                .cta-button:hover {
                    background: linear-gradient(135deg, #218838, #1e7e34);
                }
                .footer {
                    text-align: center;
                    padding: 15px 0;
                    font-size: 12px;
                    color: #666;
                }
                .footer a {
                    color: #28a745;
                    text-decoration: none;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>MobileWorld</h1>
                </div>
                <div class="content">
                    <div class="thank-you-section">
                        <h2>Chào mừng!</h2>
                        <p>Xin chào {employeeName}, bạn đã chính thức gia nhập đội ngũ MobileWorld.</p>
                    </div>
                    <div class="highlight-box">
                        🎉 Chúc mừng bạn trở thành một phần của đại gia đình MobileWorld!
                    </div>
                    <div class="account-info">
                        <p><strong>Tên đăng nhập:</strong> {email}</p>
                        <p><strong>Mật khẩu:</strong> {password}</p>
                        <p><em>⚠️ Vui lòng đăng nhập để kích hoạt tài khoản của bạn.</em></p>
                    </div>
                    <p>
                        <a href="http://localhost:3000/login" class="cta-button">ĐĂNG NHẬP NGAY</a>
                    </p>
                </div>
                <div class="footer">
                    <p>Chúng tôi rất mong chờ được làm việc cùng bạn!</p>
                    <p>Trân trọng, <strong>MobileWorld</strong></p>
                    <p>Liên hệ: <a href="mailto:lequangphuc2872006@gmail.com">lequangphuc2872006@gmail.com</a></p>
                </div>
            </div>
        </body>
        </html>
        """;

            // Thay thế placeholder
            String finalHtmlContent = htmlContent
                    .replace("{employeeName}", employeeName)
                    .replace("{email}", email)
                    .replace("{password}", password);

            // Nội dung plain-text fallback
            String plainTextContent = """
                Chào mừng bạn gia nhập MobileWorld!

                Xin chào %s,

                Bạn đã chính thức trở thành Thành viên của MobileWorld. 
                - Tên đăng nhập: %s
                - Mật khẩu: %s

                Vui lòng đăng nhập để kích hoạt tài khoản của bạn.
                Đăng nhập ngay: http://localhost:3000/login

                Trân trọng,
                MobileWorld
                Liên hệ: lequangphuc2872006@gmail.com
                """.formatted(employeeName, email, password);

            helper.setText(plainTextContent, finalHtmlContent);

            // Gửi email
            mailSender.send(message);
            System.out.println("✅ Email chào mừng đã được gửi tới: " + to);

        } catch (MessagingException e) {
            System.err.println("❌ Lỗi khi gửi email chào mừng tới " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void EmailKH(String to, String customerName, String email, String password) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🎉 Chào mừng bạn trở thành khách hàng của MobileWorld!");
            helper.setFrom("no-reply@mobileworld.com.vn");

            String htmlContent = """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Chào Mừng Khách Hàng Mới</title>
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    font-family: 'Poppins', Arial, sans-serif;
                    background-color: #f0f2f5;
                    color: #222;
                }
                .container {
                    width: 100%;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .header {
                    background: linear-gradient(135deg, #28a745, #1e7e34);
                    padding: 25px;
                    text-align: center;
                    border-radius: 12px 12px 0 0;
                }
                .header h1 {
                    margin: 0;
                    font-size: 26px;
                    color: #fff;
                    font-weight: 600;
                }
                .content {
                    background-color: #ffffff;
                    padding: 25px;
                    border-radius: 0 0 12px 12px;
                    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.08);
                    text-align: center;
                }
                .thank-you-section h2 {
                    font-size: 28px;
                    color: #28a745;
                    margin: 0 0 10px;
                    font-weight: 600;
                }
                .thank-you-section p {
                    font-size: 14px;
                    color: #444;
                    margin-bottom: 20px;
                }
                .highlight-box {
                    background: #e8f5e9;
                    color: #155724;
                    padding: 15px;
                    border-radius: 8px;
                    margin: 20px 0;
                    font-size: 16px;
                    font-weight: 500;
                }
                .account-info {
                    background: #f9f9f9;
                    border: 1px solid #e0e0e0;
                    padding: 15px;
                    border-radius: 8px;
                    margin-bottom: 20px;
                    text-align: left;
                }
                .account-info p {
                    margin: 8px 0;
                    font-size: 14px;
                    color: #333;
                }
                .account-info strong {
                    color: #28a745;
                }
                .cta-button {
                    display: inline-block;
                    padding: 12px 24px;
                    background: linear-gradient(135deg, #28a745, #218838);
                    color: #fff !important;
                    text-decoration: none;
                    border-radius: 8px;
                    font-size: 15px;
                    font-weight: 500;
                    transition: background 0.3s ease;
                }
                .cta-button:hover {
                    background: linear-gradient(135deg, #218838, #1e7e34);
                }
                .footer {
                    text-align: center;
                    padding: 15px 0;
                    font-size: 12px;
                    color: #666;
                }
                .footer a {
                    color: #28a745;
                    text-decoration: none;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>MobileWorld</h1>
                </div>
                <div class="content">
                    <div class="thank-you-section">
                        <h2>Xin chào {customerName}!</h2>
                        <p>Cảm ơn bạn đã trở thành khách hàng của MobileWorld 🎉</p>
                    </div>
                    <div class="highlight-box">
                        🎊 Tài khoản của bạn đã được tạo thành công!
                    </div>
                    <div class="account-info">
                        <p><strong>Email đăng nhập:</strong> {email}</p>
                        <p><strong>Mật khẩu:</strong> {password}</p>
                        <p><em>⚠️ Vui lòng đăng nhập để kích hoạt tài khoản.</em></p>
                    </div>
                    <p>
                        <a href="http://localhost:3000/login" class="cta-button">ĐĂNG NHẬP NGAY</a>
                    </p>
                </div>
                <div class="footer">
                    <p>Chúng tôi rất hân hạnh được phục vụ bạn ❤️</p>
                    <p>Trân trọng, <strong>MobileWorld</strong></p>
                    <p>Liên hệ: <a href="mailto:support@mobileworld.com.vn">support@mobileworld.com.vn</a></p>
                </div>
            </div>
        </body>
        </html>
        """;

            String finalHtmlContent = htmlContent
                    .replace("{customerName}", customerName)
                    .replace("{email}", email)
                    .replace("{password}", password);

            String plainTextContent = """
            Xin chào %s!

            Cảm ơn bạn đã trở thành khách hàng của MobileWorld 🎉

            Tài khoản của bạn đã được tạo thành công:
            - Email đăng nhập: %s
            - Mật khẩu: %s

            Vui lòng đăng nhập để kích hoạt tài khoản tại: http://localhost:3000/login

            Trân trọng,
            MobileWorld
            Liên hệ: lequangphuc2872006@gmail.com
            """.formatted(customerName, email, password);

            helper.setText(plainTextContent, finalHtmlContent);

            mailSender.send(message);
            System.out.println("✅ Email chào mừng khách hàng đã được gửi tới: " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Lỗi khi gửi email khách hàng tới " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
