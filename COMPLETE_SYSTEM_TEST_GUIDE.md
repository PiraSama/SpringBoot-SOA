# 🔧 SOA MICROSERVICES - COMPLETE SYSTEM TEST GUIDE

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     COMPLETE SOA SYSTEM                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐   ┌──────────────────┐                  │
│  │  Auth Service    │   │  Product Service │                  │
│  │  Port: 9090      │   │  Port: 9091      │                  │
│  │  - JWT Auth      │   │  - CRUD Products │                  │
│  │  - Login/Token   │   │  - Search        │                  │
│  └──────────────────┘   └──────────────────┘                  │
│          │                       │                             │
│          └───────────────────────┘                             │
│                                                                 │
│  ┌──────────────────┐   ┌──────────────────┐                  │
│  │  Order Service   │   │ Reporting Service│                  │
│  │  Port: 9092      │   │  Port: 9093      │                  │
│  │  - CRUD Orders   │   │  - Analytics     │                  │
│  │  - Order Items   │   │  - Reports       │                  │
│  └──────────────────┘   └──────────────────┘                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Prerequisites & Startup

### 1. Ensure All JAR Files Built

```bash
# Check if JAR files exist
ls -la d:\Bth_SOA\*/target/*.jar

# Expected:
# - hello-spring\target\hello-spring-1.0-SNAPSHOT.jar
# - product-service\target\product-service-1.0-SNAPSHOT.jar
# - order-service\target\order-service-1.0-SNAPSHOT.jar
# - reporting-service\target\reporting-service-1.0-SNAPSHOT.jar
```

### 2. Start All Services (PowerShell)

```powershell
# Kiill any existing Java processes
Get-Process java | Stop-Process -Force -ErrorAction SilentlyContinue

# Start Auth Service (Port 9090)
cd "d:\Bth_SOA\hello-spring"
Start-Process java -ArgumentList "-jar", "target\hello-spring-1.0-SNAPSHOT.jar", "--server.port=9090"

# Start Product Service (Port 9091)
cd "d:\Bth_SOA\product-service"
Start-Process java -ArgumentList "-jar", "target\product-service-1.0-SNAPSHOT.jar", "--server.port=9091"

# Start Order Service (Port 9092)
cd "d:\Bth_SOA\order-service"
Start-Process java -ArgumentList "-jar", "target\order-service-1.0-SNAPSHOT.jar", "--server.port=9092"

# Start Reporting Service (Port 9093)
cd "d:\Bth_SOA\reporting-service"
Start-Process java -ArgumentList "-jar", "target\reporting-service-1.0-SNAPSHOT.jar", "--server.port=9093"

# Wait for startup
Start-Sleep -Seconds 5

# Verify all services are running
netstat -ano | Select-String "9090|9091|9092|9093"
```

**Expected Output:**
```
TCP    0.0.0.0:9090    0.0.0.0:0    LISTENING    [PID]
TCP    0.0.0.0:9091    0.0.0.0:0    LISTENING    [PID]
TCP    0.0.0.0:9092    0.0.0.0:0    LISTENING    [PID]
TCP    0.0.0.0:9093    0.0.0.0:0    LISTENING    [PID]
```

---

## 📝 Test Scenarios

### Scenario 1: Authentication Flow

**1.1 Login & Get JWT Token**
```bash
curl -X POST http://localhost:9090/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6ImFkbWluIiwid...",
  "data": {
    "userId": 1,
    "userName": "admin"
  }
}
```

**Test Users:**
- username: `admin`, password: `admin123`
- username: `user`, password: `user123`
- username: `test`, password: `test123`

---

### Scenario 2: Product Service Testing

**2.1 Get All Products (Public - No Token Required)**
```bash
curl -X GET http://localhost:9091/products
```

**Response:**
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Laptop HP Pavilion",
      "description": "HP Pavilion 15 inch",
      "price": 12500000,
      "quantity": 15,
      "created_at": 1702209600000,
      "updated_at": 1702209600000
    },
    ...
  ]
}
```

**2.2 Get Product by ID (Public)**
```bash
curl -X GET http://localhost:9091/products/1
```

**2.3 Search Products (Public)**
```bash
curl -X GET "http://localhost:9091/products?search=Samsung"
```

**2.4 Create Product (Protected - Requires JWT Token)**
```bash
# First get token
TOKEN="<token_from_login>"

curl -X POST http://localhost:9091/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16",
    "description": "High performance laptop",
    "price": 45990000,
    "quantity": 5
  }'
```

**2.5 Update Product (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X PUT http://localhost:9091/products/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop HP Pavilion Updated",
    "description": "Updated model",
    "price": 13000000,
    "quantity": 20
  }'
```

**2.6 Delete Product (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X DELETE http://localhost:9091/products/5 \
  -H "Authorization: Bearer $TOKEN"
```

---

### Scenario 3: Order Service Testing

**3.1 Get All Orders (Public)**
```bash
curl -X GET http://localhost:9092/orders
```

**3.2 Get Order by ID (Public)**
```bash
curl -X GET http://localhost:9092/orders/1
```

**3.3 Create Order (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X POST http://localhost:9092/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Nguyễn Văn C",
    "customerEmail": "customer3@example.com"
  }'
```

**3.4 Update Order Status (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X PUT "http://localhost:9092/orders/1?status=completed" \
  -H "Authorization: Bearer $TOKEN"
```

**3.5 Delete Order (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X DELETE http://localhost:9092/orders/3 \
  -H "Authorization: Bearer $TOKEN"
```

**3.6 Get Order Items by Order ID (Public)**
```bash
curl -X GET http://localhost:9092/order_items/order/1
```

**3.7 Add Item to Order (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X POST http://localhost:9092/order_items/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "productName": "Laptop HP Pavilion",
    "quantity": 2,
    "unitPrice": 12500000
  }'
```

---

### Scenario 4: Reporting Service Testing

**4.1 Get All Order Reports (Public)**
```bash
curl -X GET http://localhost:9093/reports/orders
```

**4.2 Get Order Report by ID (Public)**
```bash
curl -X GET http://localhost:9093/reports/orders/1
```

**4.3 Create Order Report (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X POST "http://localhost:9093/reports/orders?orderId=1&totalRevenue=58480000&totalCost=40000000" \
  -H "Authorization: Bearer $TOKEN"
```

**4.4 Delete Order Report (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X DELETE http://localhost:9093/reports/orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

**4.5 Get All Product Reports (Public)**
```bash
curl -X GET http://localhost:9093/reports/products
```

**4.6 Create Product Report (Protected)**
```bash
TOKEN="<token_from_login>"

curl -X POST "http://localhost:9093/reports/products?productId=1&totalSold=5&revenue=62500000&cost=40000000" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Complete Test Script (PowerShell)

Save as `full-system-test.ps1`:

```powershell
Write-Host "====== Complete SOA System Test ======" -ForegroundColor Cyan
Write-Host ""

$authService = "http://localhost:9090"
$productService = "http://localhost:9091"
$orderService = "http://localhost:9092"
$reportingService = "http://localhost:9093"
$token = $null

# Step 1: Get JWT Token
Write-Host "[1] Getting JWT Token from Auth Service..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-WebRequest -Uri "$authService/api/login" `
        -Method Post -ContentType "application/json" `
        -Body "{`"userName`":`"admin`",`"password`":`"admin123`"}" `
        -TimeoutSec 5
    
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    Write-Host "✓ Token obtained" -ForegroundColor Green
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Test Product Service
Write-Host ""
Write-Host "[2] Testing Product Service..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$productService/products" -Method Get -TimeoutSec 5
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ GET /products: Found $($data.data.Count) products" -ForegroundColor Green
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

# Step 3: Test Order Service
Write-Host ""
Write-Host "[3] Testing Order Service..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$orderService/orders" -Method Get -TimeoutSec 5
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ GET /orders: Found $($data.data.Count) orders" -ForegroundColor Green
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

# Step 4: Test Reporting Service
Write-Host ""
Write-Host "[4] Testing Reporting Service..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "$reportingService/reports/orders" -Method Get -TimeoutSec 5
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ GET /reports/orders: Found $($data.data.Count) reports" -ForegroundColor Green
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Create Order (Protected)
Write-Host ""
Write-Host "[5] Creating new order (Protected)..." -ForegroundColor Yellow

try {
    $newOrder = "{`"customerName`":`"Test Customer`",`"customerEmail`":`"test@example.com`"}"
    $response = Invoke-WebRequest -Uri "$orderService/orders" `
        -Method Post -ContentType "application/json" `
        -Headers @{"Authorization"="Bearer $token"} `
        -Body $newOrder -TimeoutSec 5
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Order created with ID: $($data.data.id)" -ForegroundColor Green
} catch {
    Write-Host "✗ FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "====== Test Complete ======" -ForegroundColor Green
Write-Host "All 4 services responding correctly!" -ForegroundColor Cyan
```

**Run Test:**
```powershell
powershell -ExecutionPolicy Bypass -File "d:\Bth_SOA\full-system-test.ps1"
```

---

## 📊 Expected HTTP Status Codes

| Operation | Status | Meaning |
|-----------|--------|---------|
| GET /orders | 200 | Success |
| GET /orders/{id} | 200 | Found |
| GET /orders/{id} | 404 | Not found |
| POST /orders | 201 | Created |
| POST /orders (no token) | 401 | Unauthorized |
| PUT /orders/{id} | 200 | Updated |
| DELETE /orders/{id} | 200 | Deleted |

---

## 🔒 Security Notes

- **Public Endpoints:** GET /products, GET /orders, GET /reports/* (no token required)
- **Protected Endpoints:** POST, PUT, DELETE (require Bearer token)
- **Token Format:** `Authorization: Bearer <jwt_token>`
- **Token Expiration:** 24 hours (86400000 ms)

---

## 🐛 Troubleshooting

### Port Already in Use
```powershell
# Kill process on specific port
Get-NetTCPConnection -LocalPort 9091 | ForEach-Object {
    Stop-Process -Id $_.OwningProcess -Force
}
```

### Services Not Responding
```powershell
# Check service status
Test-NetConnection -ComputerName localhost -Port 9090
Test-NetConnection -ComputerName localhost -Port 9091
Test-NetConnection -ComputerName localhost -Port 9092
Test-NetConnection -ComputerName localhost -Port 9093
```

### H2 Console Access
- Auth Service: http://localhost:9090/h2-console
- Product Service: http://localhost:9091/h2-console
- Order Service: http://localhost:9092/h2-console
- Reporting Service: http://localhost:9093/h2-console

**Login:**
- Driver: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:<db_name>` (e.g., `jdbc:h2:mem:testdb`)
- User: `sa`
- Password: (leave blank)

---

## 📈 Learning Outcomes

After completing these tests, you will understand:

✅ SOA (Service-Oriented Architecture) principles
✅ Microservices communication patterns
✅ JWT-based authentication across services
✅ Database per service pattern
✅ RESTful API design
✅ Spring Boot framework
✅ H2 in-memory database
✅ Service deployment and testing

---

**Happy Testing! 🎉**
