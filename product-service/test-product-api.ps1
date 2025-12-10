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
