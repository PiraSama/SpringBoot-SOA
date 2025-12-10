# Hướng Dẫn Test Product Management Service

## 1. Kiến Trúc SOA

```
┌─────────────────────────────────────────────────┐
│         Client (Postman / Browser)              │
└───────────────────┬───────────────────────────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
        ▼           ▼           ▼
    Port 9090   Port 9091   Port 9092...
 ┌──────────┐ ┌──────────┐ ┌──────────┐
 │ Auth     │ │ Product  │ │ Order    │
 │ Service  │ │ Service  │ │ Service  │
 └──────────┘ └──────────┘ └──────────┘
```

**Product Service Details:**
- **URL:** http://localhost:9091
- **Database:** H2 In-Memory (product_db)
- **Timeout:** Không có (chạy độc lập)
- **Authentication:** JWT từ Auth Service

---

## 2. Endpoints Available

### Public Endpoints (Không cần JWT)

| Method | Endpoint | Mô Tả |
|--------|----------|-------|
| GET | `/products` | Lấy danh sách tất cả sản phẩm |
| GET | `/products/{id}` | Lấy chi tiết sản phẩm |
| GET | `/products?search=keyword` | Tìm kiếm sản phẩm |

### Protected Endpoints (Cần JWT Token)

| Method | Endpoint | Mô Tả |
|--------|----------|-------|
| POST | `/products` | Thêm sản phẩm mới |
| PUT | `/products/{id}` | Cập nhật sản phẩm |
| DELETE | `/products/{id}` | Xóa sản phẩm |

---

## 3. Test 1: Lấy Danh Sách Sản Phẩm (Public)

### Request
```
GET http://localhost:9091/products
```

### Postman Setup
1. **Method:** GET
2. **URL:** http://localhost:9091/products
3. **Headers:** (Không cần)
4. Click **"Send"**

### Response Mong Đợi (200 OK)
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Laptop HP Pavilion",
      "description": "Laptop văn phòng cao cấp",
      "price": 12500000,
      "quantity": 15,
      "created_at": 1733789560921,
      "updated_at": 1733789560921
    },
    {
      "id": 2,
      "name": "Samsung Galaxy S24",
      "description": "Điện thoại cao cấp 2024",
      "price": 22990000,
      "quantity": 25,
      "created_at": 1733789560924,
      "updated_at": 1733789560924
    }
    // ... 3 sản phẩm khác
  ],
  "timestamp": 1733789600000
}
```

---

## 4. Test 2: Lấy Chi Tiết Sản Phẩm (Public)

### Request
```
GET http://localhost:9091/products/1
```

### Postman Setup
1. **Method:** GET
2. **URL:** http://localhost:9091/products/1
3. Click **"Send"**

### Response Mong Đợi (200 OK)
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "data": {
    "id": 1,
    "name": "Laptop HP Pavilion",
    "description": "Laptop văn phòng cao cấp",
    "price": 12500000,
    "quantity": 15,
    "created_at": 1733789560921,
    "updated_at": 1733789560921
  },
  "timestamp": 1733789601000
}
```

---

## 5. Test 3: Tìm Kiếm Sản Phẩm (Public)

### Request
```
GET http://localhost:9091/products?search=Samsung
```

### Postman Setup
1. **Method:** GET
2. **URL:** http://localhost:9091/products?search=Samsung
3. Click **"Send"**

### Response Mong Đợi (200 OK)
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 2,
      "name": "Samsung Galaxy S24",
      "description": "Điện thoại cao cấp 2024",
      "price": 22990000,
      "quantity": 25,
      "created_at": 1733789560924,
      "updated_at": 1733789560924
    }
  ],
  "timestamp": 1733789602000
}
```

---

## 6. Test 4: Tạo Sản Phẩm Mới (Protected - Cần JWT)

### Trước tiên: Lấy JWT Token từ Auth Service

Chạy trên **Auth Service (port 9090)**:

```powershell
$response = Invoke-WebRequest -Uri "http://localhost:9090/api/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body (@{userName="admin"; password="admin123"} | ConvertTo-Json)

$token = ($response.Content | ConvertFrom-Json).token
Write-Host "Token: $token"
```

### Request
```
POST http://localhost:9091/products
Content-Type: application/json
Authorization: Bearer <TOKEN_HERE>

{
  "name": "MacBook Pro M3",
  "description": "Laptop cao cấp Apple",
  "price": 35990000,
  "quantity": 8
}
```

### Postman Setup
1. **Method:** POST
2. **URL:** http://localhost:9091/products
3. **Headers:**
   - Key: `Content-Type` → Value: `application/json`
   - Key: `Authorization` → Value: `Bearer <token_here>`
4. **Body (raw, JSON):**
   ```json
   {
     "name": "MacBook Pro M3",
     "description": "Laptop cao cấp Apple",
     "price": 35990000,
     "quantity": 8
   }
   ```
5. Click **"Send"**

### Response Mong Đợi (201 Created)
```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 6,
    "name": "MacBook Pro M3",
    "description": "Laptop cao cấp Apple",
    "price": 35990000,
    "quantity": 8,
    "created_at": 1733789650000,
    "updated_at": 1733789650000
  },
  "timestamp": 1733789650000
}
```

### Error Cases

**1. Không có JWT token:**
```json
{
  "success": false,
  "message": "Missing or invalid Authorization header"
}
```
**Status:** 401 Unauthorized

**2. JWT token sai:**
```json
{
  "success": false,
  "message": "Invalid or expired token"
}
```
**Status:** 401 Unauthorized

**3. Missing required field:**
```json
{
  "success": false,
  "message": "Product name is required"
}
```
**Status:** 400 Bad Request

**4. Price <= 0:**
```json
{
  "success": false,
  "message": "Product price must be greater than 0"
}
```
**Status:** 400 Bad Request

---

## 7. Test 5: Cập Nhật Sản Phẩm (Protected - Cần JWT)

### Request
```
PUT http://localhost:9091/products/6
Content-Type: application/json
Authorization: Bearer <TOKEN_HERE>

{
  "price": 32990000,
  "quantity": 10
}
```

### Postman Setup
1. **Method:** PUT
2. **URL:** http://localhost:9091/products/6
3. **Headers:**
   - Key: `Authorization` → Value: `Bearer <token>`
4. **Body (raw, JSON):**
   ```json
   {
     "price": 32990000,
     "quantity": 10
   }
   ```
5. Click **"Send"**

### Response Mong Đợi (200 OK)
```json
{
  "success": true,
  "message": "Product updated successfully",
  "data": {
    "id": 6,
    "name": "MacBook Pro M3",
    "description": "Laptop cao cấp Apple",
    "price": 32990000,
    "quantity": 10,
    "created_at": 1733789650000,
    "updated_at": 1733789660000
  },
  "timestamp": 1733789660000
}
```

---

## 8. Test 6: Xóa Sản Phẩm (Protected - Cần JWT)

### Request
```
DELETE http://localhost:9091/products/6
Authorization: Bearer <TOKEN_HERE>
```

### Postman Setup
1. **Method:** DELETE
2. **URL:** http://localhost:9091/products/6
3. **Headers:**
   - Key: `Authorization` → Value: `Bearer <token>`
4. Click **"Send"**

### Response Mong Đợi (200 OK)
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null,
  "timestamp": 1733789670000
}
```

### Error Case - Product Not Found (404)
```json
{
  "success": false,
  "message": "Product not found"
}
```

---

## 9. PowerShell Test Script

Tạo file `test-product-api.ps1`:

```powershell
# ============================================
# Product Service API Test Script
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Product Service API Test"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$authService = "http://localhost:9090"
$productService = "http://localhost:9091"

# Bước 1: Lấy JWT Token từ Auth Service
Write-Host "[1] Getting JWT Token from Auth Service..." -ForegroundColor Yellow

try {
    $loginResponse = Invoke-WebRequest -Uri "$authService/api/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body (@{userName="admin"; password="admin123"} | ConvertTo-Json) `
        -TimeoutSec 5
    
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    
    Write-Host "✓ Token obtained" -ForegroundColor Green
    Write-Host "  Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get token: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Make sure Auth Service is running on port 9090" -ForegroundColor Yellow
    exit 1
}

# Bước 2: GET /products (Public)
Write-Host "[2] Test GET /products (List all products)" -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$productService/products" `
        -Method Get `
        -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode) OK" -ForegroundColor Green
    Write-Host "  Total products: $($data.data.Count)" -ForegroundColor Cyan
    
    if ($data.data.Count -gt 0) {
        Write-Host "  First product: $($data.data[0].name)" -ForegroundColor Cyan
    }
    Write-Host ""
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Bước 3: GET /products/{id} (Public)
Write-Host "[3] Test GET /products/1 (Get product details)" -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$productService/products/1" `
        -Method Get `
        -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode) OK" -ForegroundColor Green
    Write-Host "  Product: $($data.data.name)" -ForegroundColor Cyan
    Write-Host "  Price: $($data.data.price) VND" -ForegroundColor Cyan
    Write-Host ""
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Bước 4: POST /products (Protected)
Write-Host "[4] Test POST /products (Create new product)" -ForegroundColor Yellow

$newProduct = @{
    name = "Google Pixel 9"
    description = "Điện thoại Google cao cấp"
    price = 21990000
    quantity = 12
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$productService/products" `
        -Method Post `
        -ContentType "application/json" `
        -Headers @{"Authorization" = "Bearer $token"} `
        -Body $newProduct `
        -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode) Created" -ForegroundColor Green
    Write-Host "  Product ID: $($data.data.id)" -ForegroundColor Cyan
    Write-Host "  Product name: $($data.data.name)" -ForegroundColor Cyan
    
    $newProductId = $data.data.id
    Write-Host ""
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Bước 5: PUT /products/{id} (Protected)
Write-Host "[5] Test PUT /products/$newProductId (Update product)" -ForegroundColor Yellow

$updateData = @{
    price = 19990000
    quantity = 20
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$productService/products/$newProductId" `
        -Method Put `
        -ContentType "application/json" `
        -Headers @{"Authorization" = "Bearer $token"} `
        -Body $updateData `
        -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode) OK" -ForegroundColor Green
    Write-Host "  Updated price: $($data.data.price) VND" -ForegroundColor Cyan
    Write-Host "  Updated quantity: $($data.data.quantity)" -ForegroundColor Cyan
    Write-Host ""
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Bước 6: DELETE /products/{id} (Protected)
Write-Host "[6] Test DELETE /products/$newProductId (Delete product)" -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$productService/products/$newProductId" `
        -Method Delete `
        -Headers @{"Authorization" = "Bearer $token"} `
        -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode) OK" -ForegroundColor Green
    Write-Host "  Message: $($data.message)" -ForegroundColor Cyan
    Write-Host ""
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Bước 7: Test Search
Write-Host "[7] Test GET /products?search=Samsung (Search products)" -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$productService/products?search=Samsung" `
        -Method Get `
        -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode) OK" -ForegroundColor Green
    Write-Host "  Found: $($data.data.Count) products" -ForegroundColor Cyan
    
    if ($data.data.Count -gt 0) {
        Write-Host "  First result: $($data.data[0].name)" -ForegroundColor Cyan
    }
    Write-Host ""
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "========================================" -ForegroundColor Green
Write-Host "All Tests Completed!"
Write-Host "========================================" -ForegroundColor Green
```

### Chạy Test Script
```powershell
& "d:\Bth_SOA\product-service\test-product-api.ps1"
```

---

## 10. Sơ Đồ Flow Test

```
┌─────────────────────────────────────────────────────┐
│            Start Test Script                         │
└──────────────────┬──────────────────────────────────┘
                   │
        ┌──────────▼──────────┐
        │ Auth Service:9090    │
        │ POST /api/login      │
        │ ↓                    │
        │ Get JWT Token ✓      │
        └──────────┬──────────┘
                   │
           ┌───────┼───────────────┬──────────┬─────────────┐
           │       │               │          │             │
           ▼       ▼               ▼          ▼             ▼
    GET /products  GET /products/1  POST    PUT /products/1 DELETE
    (Public)       (Public)       /products (Protected)   /products/1
    ✓              ✓              (Protected)            (Protected)
                                  ✓ Create               ✓ Delete
                                  ↓
                                  ▼
                           PUT /products/{id}
                           (Protected)
                           ✓ Update
                           ↓
                           ▼
                       Search ?search=
                       (Public)
                       ✓ Find
```

---

## 11. Database H2 Console

Truy cập H2 Console để xem dữ liệu:

```
URL: http://localhost:9091/h2-console
JDBC URL: jdbc:h2:mem:product_db
User: sa
Password: (để trống)
```

**SQL Queries:**

```sql
-- Xem tất cả sản phẩm
SELECT * FROM products;

-- Xem số lượng sản phẩm
SELECT COUNT(*) as total FROM products;

-- Tìm sản phẩm theo tên
SELECT * FROM products WHERE name LIKE '%Samsung%';

-- Sắp xếp theo giá
SELECT * FROM products ORDER BY price DESC;
```

---

## 12. Troubleshooting

| Lỗi | Nguyên Nhân | Giải Pháp |
|-----|-----------|---------|
| **Cannot connect to localhost:9091** | Product Service không chạy | Khởi động service: `java -jar target/product-service-1.0-SNAPSHOT.jar --server.port=9091` |
| **401 Unauthorized** | Token sai hoặc hết hạn | Lấy token mới từ Auth Service (port 9090) |
| **Missing Authorization header** | Không có JWT token | Thêm header: `Authorization: Bearer <token>` |
| **404 Not Found** | Product ID không tồn tại | Kiểm tra ID sản phẩm tồn tại |
| **400 Bad Request** | Dữ liệu input sai | Kiểm tra name, price > 0, quantity >= 0 |

---

## Tóm Tắt

✓ **Product Service Setup Hoàn Tất**
- ✅ Port: 9091
- ✅ Database: H2 product_db
- ✅ 5 sản phẩm test được tạo
- ✅ CRUD endpoints đầy đủ
- ✅ JWT authentication
- ✅ Search functionality

**Ready to test! 🚀**
