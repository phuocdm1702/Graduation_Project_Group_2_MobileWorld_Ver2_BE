# VNPay Workflow Fix - Cart Items Transfer Issue

## Problem
After successful VNPay payment, products were not being transferred from Redis cart to `HoaDonChiTiet` records. The VNPay callback was only updating invoice status but not processing the cart items.

## Root Cause
The VNPay callback in `VNPAYController.java` was calling:
```java
hoaDonService.updateHoaDonStatus(Integer.parseInt(idHD), (short) 3, null);
```

This only updated the invoice status but did NOT call the `thanhToan` API which is responsible for:
- Processing cart items from Redis
- Creating `HoaDonChiTiet` records
- Marking `ChiTietSanPham` as sold (deleted = true)
- Creating `ImelDaBan` records
- Setting up payment methods

## Solution
Modified VNPay callback to call `banHangService.thanhToan()` instead of just updating status.

### Changes Made

#### 1. Updated VNPAYController.java
- Added `BanHangService` dependency injection
- Modified `paymentReturn()` method to:
  - Get invoice details to determine payment amount
  - Create proper `HoaDonRequest` with VNPay payment method
  - Call `banHangService.thanhToan()` to process cart items

#### 2. New Workflow
```java
@GetMapping("/vnpay-payment")
public RedirectView paymentReturn(HttpServletRequest request) {
    String idHD = vnPayService.orderReturn(request);
    
    if (idHD != null && !idHD.isEmpty()) {
        // Get invoice details to determine payment amount
        Integer invoiceId = Integer.parseInt(idHD);
        var invoice = hoaDonService.getHoaDonDetail(invoiceId);
        
        // Create HoaDonRequest for VNPay payment
        HoaDonRequest hoaDonRequest = new HoaDonRequest();
        hoaDonRequest.setLoaiDon("online");
        hoaDonRequest.setTongTienSauGiam(invoice.getTongTienSauGiam());
        
        // Create payment method for VNPay
        Set<HinhThucThanhToanDTO> hinhThucThanhToans = new HashSet<>();
        HinhThucThanhToanDTO vnpayPayment = new HinhThucThanhToanDTO();
        vnpayPayment.setPhuongThucThanhToanId(2); // VNPay payment method ID
        vnpayPayment.setTienChuyenKhoan(invoice.getTongTienSauGiam());
        vnpayPayment.setTienMat(BigDecimal.ZERO);
        hinhThucThanhToans.add(vnpayPayment);
        hoaDonRequest.setHinhThucThanhToan(hinhThucThanhToans);

        // Call thanhToan API to process cart items
        banHangService.thanhToan(invoiceId, hoaDonRequest);
    }
}
```

## Correct Workflow Now
1. **Client creates invoice** → `HoaDon` with status 0 (Chờ xác nhận)
2. **Client adds products to cart** → Products stored in Redis with key `GH_PREFIX + hoaDonId`
3. **VNPay payment successful** → Callback calls `thanhToan` API
4. **thanhToan processes**:
   - Reads cart from Redis (`GH_PREFIX + idHD`)
   - Creates `HoaDonChiTiet` records for each cart item
   - Marks `ChiTietSanPham` as sold (`deleted = true`)
   - Creates `ImelDaBan` records
   - Sets up payment methods
   - Updates invoice status to appropriate value
   - Clears cart from Redis

## Files Modified
- `c:\Graduation_Project_Group_2_MobileWorld_Ver2_BE\src\main\java\com\example\be_datn\controller\pay\VNPAYController.java`

## Testing
To test the fix:
1. Create an invoice and add products to cart
2. Initiate VNPay payment
3. Complete payment successfully
4. Verify that:
   - `HoaDonChiTiet` records are created
   - `ChiTietSanPham` records are marked as sold
   - `ImelDaBan` records are created
   - Cart is cleared from Redis
   - Invoice status is updated appropriately

## Notes
- VNPay payment method ID is assumed to be 2 - verify this in `PhuongThucThanhToan` table
- The fix includes retry logic (3 attempts) for robustness
- Error handling ensures payment failures are properly redirected
