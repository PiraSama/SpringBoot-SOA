# Hướng Dẫn Test API với Postman

## 1. Cài Đặt Postman

### Tải Postman
- Truy cập: https://www.postman.com/downloads/
- Chọn phiên bản cho Windows
- Cài đặt theo hướng dẫn

### Hoặc Dùng Web Version
- Mở: https://web.postman.co/
- Đăng nhập (hoặc tạo tài khoản miễn phí)
- Không cần cài đặt

---

## 2. Tạo Collection Để Lưu Các Request

### Bước 1: Tạo Collection Mới
1. Click **"+"** ở tab mới
2. Hoặc click **"Collections"** → **"+"**
3. Nhập tên: `JWT Authentication API`
4. Click **"Create"**

### Bước 2: Tạo Folder Trong Collection
1. Hover vào tên collection
2. Click **"..."** → **"Add Folder"**
3. Tên folder: `Authentication`

---

## 3. Test 1: Đăng Nhập (POST /api/login)

### Bước 1: Tạo Request Mới
1. Click **"+"** để tạo request mới
2. Hoặc trong collection, click **"Add Request"**
3. Tên request: `Login - Admin`
4. Chọn folder: `Authentication`

### Bước 2: Cấu Hình Request

**Method:**
```
POST
```

**URL:**
```
http://localhost:9090/api/login
```

### Bước 3: Cấu Hình Headers

1. Click tab **"Headers"**
2. Thêm header:
   - Key: `Content-Type`
   - Value: `application/json`

| Key | Value |
|-----|-------|
| Content-Type | application/json |

### Bước 4: Cấu Hình Body

1. Click tab **"Body"**
2. Chọn **"raw"**
3. Chọn format **"JSON"** (dropdown bên phải)
4. Nhập JSON:

```json
{
  "userName": "admin",
  "password": "admin123"
}
```

**Hình minh họa:**
```
┌─ POST ─────────────── http://localhost:9090/api/login ──────────────┐
│                                                                        │
│ Params | Headers | Body | Pre-request Script | Tests | Settings      │
│        |         |      |                      |       |               │
│        │ ┌──────────────────────┐                      │               │
│        │ │ Content-Type         │                      │               │
│        │ │ application/json     │                      │               │
│        └─┴──────────────────────┘                      │               │
│                                                         │               │
│  ┌─ Body (raw, JSON) ──────────────────────────────────┐              │
│  │ {                                                    │              │
│  │   "userName": "admin",                              │              │
│  │   "password": "admin123"                            │              │
│  │ }                                                    │              │
│  └────────────────────────────────────────────────────┘              │
│                                                                        │
│  [Send]                                                                │
└────────────────────────────────────────────────────────────────────┘
```

### Bước 5: Gửi Request

1. Click nút **"Send"** (màu xanh)
2. Xem Response ở phía dưới

### Bước 6: Kiểm Tra Response

**Status:** Nên là **200 OK** (xanh)

**Response Body:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlck5hbWUiOiJhZG1pbiIsImlhdCI6MTczMzA3MzE5MiwiZXhwIjoxNzMzMTU5NTkyLCJ1c2VySWQiOjF9.xxx",
  "user_id": 1,
  "user_name": "admin"
}
```

---

## 4. Lưu Token Để Dùng Lại (Environment Variable)

### Tại Sao Cần?
- Token rất dài, dễ sai chép
- Có thể dùng lại trong các request khác
- Tự động lưu token mới mỗi lần đăng nhập

### Bước 1: Tạo Environment

1. Click **"Environments"** (bên trái)
2. Click **"+"** để tạo mới
3. Tên: `Development`
4. Thêm biến:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| base_url | http://localhost:9090 | http://localhost:9090 |
| token | (để trống) | (để trống) |

5. Click **"Save"**

### Bước 2: Dùng Environment Variable Trong URL

Trong request `/api/login`:

**URL cũ:**
```
http://localhost:9090/api/login
```

**URL mới (dùng variable):**
```
{{base_url}}/api/login
```

Click **"Send"** → phải vẫn hoạt động

### Bước 3: Tự Động Lưu Token Sau Khi Đăng Nhập

1. Trong request `/api/login`, click tab **"Tests"**
2. Nhập script:

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
    console.log("✓ Token saved: " + jsonData.token.substring(0, 50) + "...");
} else {
    console.log("✗ Login failed: " + pm.response.code);
}
```

**Giải thích:**
- Nếu response code là 200 → lấy token từ response
- Lưu token vào environment variable `token`
- In log để xác nhận

3. Click **"Send"** lần nữa
4. Scroll xuống, mở **"Console"** (ở góc dưới trái) để xem log

---

## 5. Test 2: Xác Thực Token (POST /api/auth)

### Bước 1: Tạo Request Mới

1. Click **"+"** để tạo request
2. Tên: `Authenticate Token`
3. Folder: `Authentication`

### Bước 2: Cấu Hình

**Method:**
```
POST
```

**URL:**
```
{{base_url}}/api/auth
```

### Bước 3: Cấu Hình Headers

| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Authorization | Bearer {{token}} |

**Lưu ý:** `{{token}}` sẽ được thay bằng token thực từ environment variable

### Bước 4: Gửi Request

1. Trước tiên, chạy request `/api/login` để lấy token
2. Sau đó chạy request `/api/auth`
3. Status nên là **200 OK**

### Bước 5: Kiểm Tra Response

```json
{
  "valid": true,
  "message": "Token is valid",
  "userId": 1,
  "userName": "admin"
}
```

---

## 6. Test 3: Đăng Xuất (POST /api/logout)

### Tạo Request Mới

**Method:**
```
POST
```

**URL:**
```
{{base_url}}/api/logout
```

**Headers:**

| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Authorization | Bearer {{token}} |

### Gửi Request

Status nên là **200 OK**

**Response:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

---

## 7. Test 4: Test Lỗi - Sai Mật Khẩu

### Tạo Request Mới

**Method:**
```
POST
```

**URL:**
```
{{base_url}}/api/login
```

**Headers:**

| Key | Value |
|-----|-------|
| Content-Type | application/json |

**Body (raw, JSON):**
```json
{
  "userName": "admin",
  "password": "wrongpassword"
}
```

### Kiểm Tra Response

**Status:** **401 Unauthorized** (đỏ)

**Response:**
```json
{
  "success": false,
  "message": "Invalid password",
  "token": null,
  "user_id": null,
  "user_name": null
}
```

---

## 8. Test 5: Test Lỗi - User Không Tồn Tại

### Tạo Request Mới

**Method:**
```
POST
```

**URL:**
```
{{base_url}}/api/login
```

**Body (raw, JSON):**
```json
{
  "userName": "notexist",
  "password": "password123"
}
```

### Kiểm Tra Response

**Status:** **401 Unauthorized**

**Response:**
```json
{
  "success": false,
  "message": "User not found",
  "token": null,
  "user_id": null,
  "user_name": null
}
```

---

## 9. Test 6: Test Token Sai (Invalid Token)

### Tạo Request Mới

**Method:**
```
POST
```

**URL:**
```
{{base_url}}/api/auth
```

**Headers:**

| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Authorization | Bearer invalidtoken123 |

### Kiểm Tra Response

**Status:** **401 Unauthorized**

**Response:**
```json
{
  "valid": false,
  "message": "Invalid token",
  "userId": null,
  "userName": null
}
```

---

## 10. Tổng Hợp Collection

Sau khi hoàn tất, collection của bạn nên có:

```
📦 JWT Authentication API
├── 📁 Authentication
│   ├── 📄 Login - Admin
│   ├── 📄 Login - User
│   ├── 📄 Login - Test
│   ├── 📄 Login - Wrong Password
│   ├── 📄 Login - User Not Found
│   ├── 📄 Authenticate Token
│   ├── 📄 Authenticate - Invalid Token
│   └── 📄 Logout
```

---

## 11. Chạy Tất Cả Requests Theo Thứ Tự (Collection Runner)

### Bước 1: Mở Collection Runner

1. Click **"Collections"**
2. Hover vào collection **"JWT Authentication API"**
3. Click **"▶"** (icon play)

### Bước 2: Cấu Hình

- **Environment:** Chọn `Development`
- **Iterations:** `1` (chạy 1 lần)
- **Delay:** `100 ms` (chờ 100ms giữa các request)

### Bước 3: Chạy

Click **"Run JWT Authentication API"**

### Bước 4: Xem Kết Quả

- ✓ Xanh = thành công
- ✗ Đỏ = thất bại

---

## 12. Export/Import Collection

### Export Collection
1. Hover vào collection
2. Click **"..."** → **"Export"**
3. Chọn format **"Collection v2.1"**
4. Lưu file `.json`

### Import Collection
1. Click **"Import"** (góc trên trái)
2. Chọn file `.json` vừa export
3. Collection sẽ được import lại

**Lợi ích:** Có thể chia sẻ collection với teammate!

---

## 13. Troubleshooting

| Lỗi | Nguyên Nhân | Giải Pháp |
|-----|-----------|---------|
| **Can't connect** | Ứng dụng không chạy | Kiểm tra Java process đang chạy port 9090 |
| **401 Unauthorized** | Token sai/hết hạn | Chạy lại `/api/login` để lấy token mới |
| **Invalid JSON** | Body format sai | Kiểm tra dấu ngoặc, dấu phẩy, dấu nháy |
| **Token not saved** | Script Tests sai | Kiểm tra console log (F12 hoặc Postman console) |
| **{{token}} hiển thị như text** | Không chọn environment | Chọn `Development` ở dropdown bên phải |

---

## 14. Tips & Tricks

### Tip 1: Tạo Request Từ Response
1. Chạy `/api/login`
2. Click **"Save as Example"** (dưới response)
3. Tự động tạo request copy từ response

### Tip 2: Format JSON Response
1. Chạy request
2. Click **"Pretty"** (dưới response)
3. JSON sẽ được format đẹp

### Tip 3: Dùng Pre-request Script
1. Tab **"Pre-request Script"** trong request
2. Viết code chạy **trước** khi gửi request
3. Ví dụ: generate timestamp, create hash, v.v.

### Tip 4: Sử Dụng Variables Loại Khác
```javascript
// Dynamic variable (mỗi lần request sẽ khác)
{{$timestamp}}    // Timestamp hiện tại
{{$uuid}}         // UUID random
{{$randomInt}}    // Số ngẫu nhiên
```

### Tip 5: Kiểm Tra Status Code
Trong tab **"Tests"**:
```javascript
pm.test("Status is 200", function () {
    pm.response.to.have.status(200);
});
```

---

## 15. Keyboard Shortcuts

| Phím | Chức Năng |
|-----|----------|
| `Ctrl + Enter` | Gửi request |
| `Ctrl + S` | Lưu request |
| `Ctrl + Shift + M` | Tạo mock server |
| `Ctrl + Alt + E` | Quản lý environment |
| `Ctrl + E` | Tìm environment |

---

## 16. Video Hướng Dẫn (Nếu Cần)

Nếu muốn xem video:
1. Truy cập: https://www.youtube.com/results?search_query=postman+tutorial
2. Tìm: "Postman API Testing Tutorial"
3. Hoặc trong Postman → **"Learning Center"**

---

## Tóm Tắt Các Bước

### 1️⃣ **Cài Đặt**
   - Tải Postman

### 2️⃣ **Tạo Collection**
   - New → Collection
   - Tên: "JWT Authentication API"

### 3️⃣ **Tạo Environment**
   - New → Environment
   - Biến: `base_url`, `token`

### 4️⃣ **Tạo Request: POST /api/login**
   - Method: POST
   - URL: `{{base_url}}/api/login`
   - Body: `{userName, password}`
   - Tests: Lưu token vào environment

### 5️⃣ **Tạo Request: POST /api/auth**
   - Method: POST
   - URL: `{{base_url}}/api/auth`
   - Header: `Authorization: Bearer {{token}}`

### 6️⃣ **Gửi Request & Kiểm Tra Response**

### 7️⃣ **Export Collection Để Chia Sẻ**

---

**Bây giờ bạn có thể test API một cách chuyên nghiệp! 🚀**
