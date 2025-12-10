# BÀI THỰC HÀNH SỐ 6: KIỂM THỬ QUY TRÌNH HỆ THỐNG BÁN HÀNG

## Tổng Quan Hệ Thống SOA

```
┌─────────────────────────────────────────────────────────────────┐
│                    SOA SALES SYSTEM - 4 MICROSERVICES            │
├─────────────────────────────────────────────────────────────────┤
│  1. Auth Service (Port 9090)      - JWT Authentication          │
│  2. Product Service (Port 9091)   - Quản lý sản phẩm             │
│  3. Order Service (Port 9092)     - Quản lý đơn hàng             │
│  4. Reporting Service (Port 9093) - Báo cáo & Thống kê           │
└─────────────────────────────────────────────────────────────────┘
```

---

## PHẦN 1: KHỞI ĐỘNG HỆ THỐNG

### 1.1 Khởi động tất cả services

Chạy 4 PowerShell terminals riêng biệt:

**Terminal 1 - Auth Service:**
```powershell
cd "d:\Bth_SOA\hello-spring"
java -jar target\hello-spring-1.0-SNAPSHOT.jar --server.port=9090
```

**Terminal 2 - Product Service:**
```powershell
cd "d:\Bth_SOA\product-service"
java -jar target\product-service-1.0-SNAPSHOT.jar --server.port=9091
```

**Terminal 3 - Order Service:**
```powershell
cd "d:\Bth_SOA\order-service"
java -jar target\order-service-1.0-SNAPSHOT.jar --server.port=9092
```

**Terminal 4 - Reporting Service:**
```powershell
cd "d:\Bth_SOA\reporting-service"
java -jar target\reporting-service-1.0-SNAPSHOT.jar --server.port=9093
```

### 1.2 Kiểm tra tất cả services đang chạy

```powershell
netstat -ano | Select-String "9090|9091|9092|9093"
```

**Kết quả mong đợi:**
```
TCP    0.0.0.0:9090    0.0.0.0:0    LISTENING    [PID]
TCP    0.0.0.0:9091    0.0.0.0:0    LISTENING    [PID]
TCP    0.0.0.0:9092    0.0.0.0:0    LISTENING    [PID]
TCP    0.0.0.0:9093    0.0.0.0:0    LISTENING    [PID]
```

---

## PHẦN 2: QUY TRÌNH MUA HÀNG HOÀN CHỈNH

### QUY TRÌNH TỔNG QUAN

```
1. ĐĂNG NHẬP & LẤY TOKEN
   ↓
2. XEM DANH SÁCH SẢN PHẨM
   ↓
3. TẠO ĐƠN HÀNG MỚI
   ↓
4. THÊM CHI TIẾT HÀNG (ORDER ITEMS)
   ↓
5. CẬP NHẬT SỐ LƯỢNG SẢN PHẨM
   ↓
6. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG (HOÀN THÀNH)
   ↓
7. TẠO BÁO CÁO ĐƠN HÀNG & SẢN PHẨM
   ↓
8. XEM THỐNG KÊ BÁN HÀNG
```

---

## PHẦN 3: CHI TIẾT CÁC BƯỚC

### BƯỚC 1: Đăng nhập & Lấy JWT Token

**API Endpoint:**
```
POST http://localhost:9090/api/login
```

**Request Body (JSON):**
```json
{
  "userName": "admin",
  "password": "admin123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6ImFkbWluIiwidXNlcklkIjoxLCJpYXQiOjE3NjAwMDAwMDAsImV4cCI6MTc2MDA4NjQwMH0.XXX",
  "userId": 1,
  "userName": "admin"
}
```

**Lưu Token:** Sao chép giá trị `token` để sử dụng cho các request tiếp theo

---

### BƯỚC 2: Xem Danh Sách Sản Phẩm (Public - Không cần Token)

**API Endpoint:**
```
GET http://localhost:9091/products
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Laptop HP Pavilion",
      "description": "High-performance laptop",
      "price": 12500000,
      "quantity": 15,
      "created_at": 1760000000000,
      "updated_at": 1760000000000
    },
    {
      "id": 2,
      "name": "Samsung Galaxy S24",
      "description": "Latest smartphone",
      "price": 22990000,
      "quantity": 25,
      "created_at": 1760000000000,
      "updated_at": 1760000000000
    }
  ]
}
```

---

### BƯỚC 3: Tạo Đơn Hàng Mới

**API Endpoint:**
```
POST http://localhost:9092/orders
```

**Headers:**
```
Authorization: Bearer [YOUR_TOKEN]
Content-Type: application/json
```

**Request Body:**
```json
{
  "customerName": "Nguyễn Văn A",
  "customerEmail": "customer@example.com"
}
```

---

### BƯỚC 4: Thêm Chi Tiết Hàng (Order Items)

**API Endpoint:**
```
POST http://localhost:9092/order_items/{orderId}
```

**Request Body:**
```json
{
  "productId": 1,
  "productName": "Laptop HP Pavilion",
  "quantity": 1,
  "unitPrice": 12500000
}
```

---

### BƯỚC 5: Cập Nhật Trạng Thái Đơn Hàng

**API Endpoint:**
```
PUT http://localhost:9092/orders/{id}?status=completed
```

---

### BƯỚC 6: Tạo Báo Cáo

**Order Report:**
```
POST http://localhost:9093/reports/orders?orderId=3&totalRevenue=40370000&totalCost=27000000
```

**Product Report:**
```
POST http://localhost:9093/reports/products?productId=1&totalSold=1&revenue=12500000&cost=8000000
```

---

### BƯỚC 7: Xem Thống Kê

```
GET http://localhost:9093/reports/orders
GET http://localhost:9093/reports/products
```

---

## PHẦN 4: CURL COMMANDS

```bash
# 1. Login
curl -X POST http://localhost:9090/api/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"admin","password":"admin123"}'

# 2. Get Products
curl http://localhost:9091/products

# 3. Create Order
curl -X POST http://localhost:9092/orders \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Nguyễn Văn A","customerEmail":"customer@example.com"}'

# 4. Get Reports
curl http://localhost:9093/reports/orders
```

---

## KIẾN TRÚC HỆ THỐNG

| Service | Port | Chức năng | Database |
|---------|------|----------|----------|
| Auth | 9090 | JWT Authentication | testdb (H2) |
| Product | 9091 | Quản lý sản phẩm | product_db (H2) |
| Order | 9092 | Quản lý đơn hàng | order_db (H2) |
| Reporting | 9093 | Báo cáo & Thống kê | reporting_db (H2) |

**Đặc điểm:**
- ✓ Hoàn toàn độc lập, cổng và database riêng
- ✓ Giao tiếp qua REST API + JWT
- ✓ Không liên kết trực tiếp cơ sở dữ liệu
- ✓ Dễ mở rộng và bảo trì

---

**Hoàn thành Bài 6: Kiểm thử quy trình hệ thống bán hàng SOA! ✓**
