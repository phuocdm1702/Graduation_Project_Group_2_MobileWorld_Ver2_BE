# Email API Usage Guide

## API Endpoints

### 1. Gửi email thông báo trạng thái hóa đơn
```
POST /api/email/send-invoice-status/{hoaDonId}
```

**Response:**
```json
{
  "success": true,
  "message": "Đã gửi email thông báo trạng thái đơn hàng thành công",
  "email": "customer@example.com",
  "hoaDonId": 123,
  "maHoaDon": "HD001"
}
```

### 2. Gửi email với địa chỉ tùy chỉnh
```
POST /api/email/send-invoice-status/{hoaDonId}/custom-email?email=custom@example.com
```

### 3. Kiểm tra email khách hàng
```
GET /api/email/check-email/{hoaDonId}
```

**Response:**
```json
{
  "success": true,
  "hasEmail": true,
  "email": "customer@example.com",
  "hoaDonId": 123,
  "maHoaDon": "HD001",
  "tenKhachHang": "Nguyễn Văn A"
}
```

## Frontend Integration

### JavaScript Example (HoaDonChiTiet.js)

```javascript
// Hàm chuyển trạng thái với gửi email
async function changeStatusWithEmail(hoaDonId, newStatus, idNhanVien) {
  try {
    // 1. Chuyển trạng thái trước
    const statusResponse = await apiService.put(`/api/hoa-don/${hoaDonId}/update-status`, {
      trangThai: newStatus,
      idNhanVien: idNhanVien
    });

    if (statusResponse.success) {
      // 2. Gửi email thông báo sau khi chuyển trạng thái thành công
      try {
        const emailResponse = await apiService.post(`/api/email/send-invoice-status/${hoaDonId}`);
        
        if (emailResponse.success) {
          toast.success(`Đã chuyển trạng thái và gửi email thông báo đến ${emailResponse.email}`);
        } else {
          toast.warning(`Đã chuyển trạng thái thành công nhưng không thể gửi email: ${emailResponse.message}`);
        }
      } catch (emailError) {
        console.error('Lỗi gửi email:', emailError);
        toast.warning('Đã chuyển trạng thái thành công nhưng không thể gửi email thông báo');
      }

      // 3. Reload data
      await loadInvoiceData();
    }
  } catch (error) {
    console.error('Lỗi chuyển trạng thái:', error);
    toast.error('Không thể chuyển trạng thái đơn hàng');
  }
}

// Hàm kiểm tra và gửi email riêng biệt
async function sendEmailNotification(hoaDonId) {
  try {
    // Kiểm tra email khách hàng trước
    const checkResponse = await apiService.get(`/api/email/check-email/${hoaDonId}`);
    
    if (!checkResponse.hasEmail) {
      // Hiển thị modal nhập email tùy chỉnh
      const customEmail = prompt('Khách hàng chưa có email. Vui lòng nhập email để gửi thông báo:');
      if (customEmail && customEmail.includes('@')) {
        const response = await apiService.post(`/api/email/send-invoice-status/${hoaDonId}/custom-email?email=${customEmail}`);
        if (response.success) {
          toast.success(`Đã gửi email thông báo đến ${customEmail}`);
        }
      }
    } else {
      // Gửi email tự động
      const response = await apiService.post(`/api/email/send-invoice-status/${hoaDonId}`);
      if (response.success) {
        toast.success(`Đã gửi email thông báo đến ${response.email}`);
      }
    }
  } catch (error) {
    console.error('Lỗi gửi email:', error);
    toast.error('Không thể gửi email thông báo');
  }
}
```

### Vue.js Template Example

```vue
<template>
  <div class="invoice-actions">
    <!-- Button chuyển trạng thái với email -->
    <button 
      @click="changeStatusWithEmail(invoice.id, 1, currentUserId)"
      class="btn btn-primary">
      <i class="bi bi-arrow-right-circle"></i>
      Chuyển trạng thái & Gửi email
    </button>
    
    <!-- Button gửi email riêng -->
    <button 
      @click="sendEmailNotification(invoice.id)"
      class="btn btn-outline-info">
      <i class="bi bi-envelope"></i>
      Gửi email thông báo
    </button>
  </div>
</template>
```

## Workflow mới

1. **Admin chuyển trạng thái** → API `/api/hoa-don/{id}/update-status`
2. **Frontend nhận response thành công** → Gọi API `/api/email/send-invoice-status/{id}`
3. **Backend gửi email** → Khách hàng nhận email với timeline
4. **Frontend hiển thị thông báo** → "Đã chuyển trạng thái và gửi email"

## Lợi ích

- ✅ **Kiểm soát từ frontend**: Có thể chọn khi nào gửi email
- ✅ **Error handling tốt hơn**: Lỗi email không ảnh hưởng chuyển trạng thái
- ✅ **Linh hoạt**: Có thể gửi email tùy chỉnh hoặc kiểm tra trước
- ✅ **User experience**: Feedback rõ ràng cho admin
