# API Test Examples

## 🔐 Authentication Flow

### 1. Đăng ký tài khoản
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phone": "0123456789"
  }'
```

**Response:**
```json
{
  "message": "User registered successfully. Please check your email for verification.",
  "userId": 1
}
```

### 2. Đăng nhập
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "MEMBER",
    "emailVerified": true
  }
}
```

## 👤 User Profile Management

### 3. Cập nhật thông tin cá nhân
```bash
curl -X PUT http://localhost:8080/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Smith",
    "age": 30,
    "gender": "MALE",
    "occupation": "Software Engineer"
  }'
```

### 4. Upload avatar
```bash
curl -X POST http://localhost:8080/users/avatar \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@avatar.jpg"
```

## 🚬 Smoking Profile

### 5. Tạo smoking profile
```bash
curl -X POST http://localhost:8080/smoking-profiles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cigarettesPerDay": 15,
    "brand": "Marlboro",
    "pricePerPack": 2.5,
    "cigarettesPerPack": 20,
    "yearsOfSmoking": 8,
    "previousQuitAttempts": 3,
    "smokingTriggers": "[\"stress\", \"coffee\", \"alcohol\"]",
    "healthConcerns": "[\"cough\", \"shortness of breath\"]",
    "motivations": "[\"health\", \"family\", \"money\"]",
    "currentWeight": 75.5,
    "stressLevel": 7
  }'
```

## 📋 Quit Plans

### 6. Tạo kế hoạch thủ công
```bash
curl -X POST http://localhost:8080/quit-plans \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Quit Journey",
    "description": "Personal quit plan for better health",
    "quitMethod": "GRADUAL_REDUCTION",
    "quitDate": "2024-01-15",
    "targetDate": "2024-04-15",
    "personalReasons": "[\"Better health\", \"Save money\", \"Family\"]",
    "copingStrategies": "[\"Exercise\", \"Meditation\", \"Deep breathing\"]"
  }'
```

### 7. Tạo kế hoạch bằng AI (Premium)
```bash
curl -X POST http://localhost:8080/quit-plans/ai-generate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "preferredMethod": "GRADUAL_REDUCTION",
    "preferredQuitDate": "2024-01-20",
    "personalReasons": "For my children"
  }'
```

**Response:**
```json
{
  "id": 1,
  "title": "AI-Generated Personalized Quit Plan",
  "description": "Based on your smoking pattern (15 cigarettes/day, 8 years of smoking), we recommend gradual reduction to minimize withdrawal symptoms. This approach will help your body adjust gradually while reducing cravings. We've considered your previous quit attempts and tailored this plan accordingly.",
  "quitMethod": "GRADUAL_REDUCTION",
  "quitDate": "2024-01-20",
  "targetDate": "2024-04-20",
  "isAiGenerated": true,
  "copingStrategies": "[\"Deep breathing exercises\", \"Physical exercise\", \"Healthy snacking\", \"Meditation\", \"Call a friend\", \"Track daily reduction\", \"Celebrate small wins\", \"Drink plenty of water\"]",
  "personalReasons": "[\"Better health\", \"Save money\", \"Save $112/month\", \"For my children\", \"Family\", \"Self-improvement\"]"
}
```

## 📊 Daily Logs

### 8. Tạo daily log
```bash
curl -X POST http://localhost:8080/daily-logs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cigarettesSmoked": 5,
    "cravingsCount": 8,
    "moodLevel": 6,
    "stressLevel": 4,
    "energyLevel": 7,
    "weight": 75.2,
    "notes": "Had a stressful day but managed to reduce smoking"
  }'
```

### 9. Lấy thống kê
```bash
curl -X GET http://localhost:8080/daily-logs/statistics \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "smokeFreeDays": 12,
  "moneySaved": 60.0,
  "averageCigarettesPerDay": 3.2,
  "totalLogsCount": 25,
  "healthTrends": {
    "averageMood": 6.8,
    "averageStress": 4.2,
    "averageEnergy": 7.1,
    "weightChange": -0.3
  },
  "cravingPatterns": {
    "averageCravingsPerDay": 6.5,
    "highCravingDays": 3
  },
  "predictions": {
    "nextMilestone": "30 days smoke-free",
    "daysToNextMilestone": 18,
    "predictedMonthlySavings": 150
  },
  "summaries": {
    "weeklySmokeFreeDays": 5,
    "monthlySmokeFreeDays": 12,
    "weeklyAverageCigarettes": 2.8
  }
}
```

## 🏆 Achievements

### 10. Lấy achievements
```bash
curl -X GET http://localhost:8080/achievements \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 11. Kiểm tra achievements mới
```bash
curl -X POST http://localhost:8080/achievements/check \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 👥 Forum (Community)

### 12. Lấy danh sách bài viết (FREE)
```bash
curl -X GET http://localhost:8080/forum/posts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 13. Tạo bài viết mới (Premium)
```bash
curl -X POST http://localhost:8080/forum/posts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My 30-day smoke-free journey",
    "content": "Today marks 30 days since I quit smoking. The first week was tough, but with the support of this community and the AI-generated plan, I made it through. Here are my tips...",
    "category": "SUCCESS_STORIES"
  }'
```

### 14. Like bài viết (Premium)
```bash
curl -X POST http://localhost:8080/forum/posts/1/like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 👨‍⚕️ Coach Support (Premium)

### 15. Lấy danh sách clients (Coach)
```bash
curl -X GET http://localhost:8080/coach/clients \
  -H "Authorization: Bearer COACH_JWT_TOKEN"
```

### 16. Gửi tin nhắn cho client (Coach)
```bash
curl -X POST http://localhost:8080/coach/clients/1/message \
  -H "Authorization: Bearer COACH_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Great progress on your quit journey! How are you feeling today?"
  }'
```

## 🌐 Public APIs

### 17. Lấy thông tin platform
```bash
curl -X GET http://localhost:8080/public/platform-info
```

**Response:**
```json
{
  "platformName": "Smoking Cessation Support Platform",
  "description": "Nền tảng hỗ trợ cai nghiện thuốc lá toàn diện với AI, cộng đồng và huấn luyện viên chuyên nghiệp",
  "version": "1.0.0",
  "features": [
    "Theo dõi tiến trình cai thuốc hàng ngày",
    "Kế hoạch cai thuốc được tạo bởi AI",
    "Hệ thống gamification với huy hiệu thành tích",
    "Cộng đồng hỗ trợ và chia sẻ kinh nghiệm",
    "Tư vấn 1-1 với huấn luyện viên chuyên nghiệp",
    "Thống kê và phân tích chi tiết"
  ]
}
```

### 18. Lấy gói subscription
```bash
curl -X GET http://localhost:8080/public/subscription-plans
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "FREE",
    "description": "Basic features for getting started",
    "monthlyPrice": 0,
    "yearlyPrice": 0,
    "features": "Basic account management and forum read-only access",
    "popular": false
  },
  {
    "id": 2,
    "name": "PREMIUM",
    "description": "Full access to all features with AI support",
    "monthlyPrice": 9.99,
    "yearlyPrice": 99,
    "features": "AI-powered quit plans, advanced analytics, coach support, unlimited forum access",
    "popular": true
  }
]
```

## 🔧 Error Handling Examples

### 400 Bad Request
```json
{
  "timestamp": "2024-01-07T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/auth/register"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-07T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid JWT token",
  "path": "/users/profile"
}
```

### 403 Forbidden (Premium feature)
```json
{
  "timestamp": "2024-01-07T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Premium feature. Please upgrade your subscription.",
  "path": "/quit-plans/ai-generate"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-07T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Quit plan not found",
  "path": "/quit-plans/999"
}
```

## 📱 Testing với Postman

1. **Import Collection**: Import file `docs/postman-collection.json`
2. **Set Environment Variables**:
   - `baseUrl`: `http://localhost:8080`
   - `jwtToken`: (sẽ được set sau khi login)
3. **Test Flow**:
   - Register → Login → Get JWT Token
   - Test các endpoints theo thứ tự

## 🧪 Testing với Swagger UI

1. Truy cập: `http://localhost:8080/swagger-ui/index.html`
2. Click "Authorize" → Nhập JWT token
3. Test các endpoints trực tiếp trên UI

## 📊 Performance Testing

### Load Testing với Apache Bench
```bash
# Test 1000 requests với 10 concurrent users
ab -n 1000 -c 10 http://localhost:8080/public/platform-info

# Test authenticated endpoint
ab -n 100 -c 5 -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8080/users/dashboard
```

### Database Performance
```sql
-- Check slow queries
SELECT * FROM information_schema.processlist WHERE time > 5;

-- Check table sizes
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'smoking_cessation';
``` 