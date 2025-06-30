# Smoking Cessation Support Platform

Nền tảng hỗ trợ cai nghiện thuốc lá - một ứng dụng web giúp người dùng theo dõi và quản lý quá trình cai thuốc lá của họ.

## Tính năng chính

### 🏠 Trang chủ & Thông tin
- Giới thiệu nền tảng
- Bảng xếp hạng thành tích
- Blog chia sẻ kinh nghiệm

### 👥 Quản lý người dùng
- Đăng ký thành viên
- Xác thực và phân quyền
- Quản lý hồ sơ cá nhân

### 🚬 Theo dõi tình trạng hút thuốc
- Ghi nhận thói quen hút thuốc hiện tại
- Thống kê số lượng và chi phí

### 📋 Kế hoạch cai thuốc
- Tạo kế hoạch cai thuốc cá nhân
- Hỗ trợ tạo kế hoạch tự động
- Tùy chỉnh theo nhu cầu

### 📊 Theo dõi tiến trình
- Ghi nhận tiến trình hàng ngày
- Thống kê ngày không hút thuốc
- Tính toán tiền tiết kiệm

### 🏆 Hệ thống thành tích
- Huy hiệu cho các mốc quan trọng
- Bảng xếp hạng cộng đồng
- Động lực gamification

### 💬 Cộng đồng & Tư vấn
- Chia sẻ kinh nghiệm
- Tư vấn với chuyên gia
- Hỗ trợ từ cộng đồng

## Công nghệ sử dụng

- **Backend**: Spring Boot 3.2, Java 17
- **Database**: MySQL, Flyway Migration
- **Security**: Spring Security, JWT
- **Documentation**: OpenAPI 3, Swagger UI
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

## Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- MySQL 8.0+

## Cài đặt và chạy

### 1. Clone repository
```bash
git clone https://github.com/se183119/smoking-cessation-platform.git
cd smoking-cessation-platform
```

### 2. Cấu hình database
```sql
CREATE DATABASE smoking_cessation_db;
```

### 3. Cấu hình environment variables
```bash
cp .env.example .env
# Chỉnh sửa file .env với thông tin cấu hình của bạn
```

### 4. Chạy ứng dụng
```bash
mvn spring-boot:run
```

### 5. Truy cập ứng dụng
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health Check: http://localhost:8080/api/actuator/health

## API Documentation

### Authentication Endpoints
- `POST /auth/register` - Đăng ký tài khoản
- `POST /auth/login` - Đăng nhập
- `POST /auth/refresh` - Làm mới token

### User Management
- `GET /users` - Lấy danh sách người dùng
- `GET /users/{id}` - Lấy thông tin người dùng
- `PUT /users/{id}` - Cập nhật thông tin
- `DELETE /users/{id}` - Xóa người dùng

### Quit Plan Management
- `POST /quit-plans` - Tạo kế hoạch cai thuốc
- `GET /quit-plans` - Lấy danh sách kế hoạch
- `PUT /quit-plans/{id}` - Cập nhật kế hoạch
- `DELETE /quit-plans/{id}` - Xóa kế hoạch

### Progress Tracking
- `POST /progress` - Ghi nhận tiến trình
- `GET /progress/stats` - Thống kê tiến trình
- `GET /progress/dashboard` - Dashboard cá nhân

Chi tiết API có thể xem tại Swagger UI sau khi chạy ứng dụng.

## Cấu trúc dự án

```
src/
├── main/
│   ├── java/com/smokingcessation/platform/
│   │   ├── controller/          # REST Controllers
│   │   ├── service/             # Business Logic
│   │   ├── repository/          # Data Access Layer
│   │   ├── entity/              # JPA Entities
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── config/              # Configuration Classes
│   │   ├── security/            # Security Components
│   │   ├── exception/           # Exception Handling
│   │   └── util/                # Utilities
│   └── resources/
│       ├── application.yml      # Main Configuration
│       └── db/migration/        # Database Migrations
└── test/                        # Test Classes
```

## Testing

```bash
# Chạy tất cả tests
mvn test

# Chạy tests với coverage
mvn test jacoco:report

# Chạy integration tests
mvn verify
```

## Deployment

### Docker
```bash
# Build image
docker build -t smoking-cessation-platform .

# Run container
docker-compose up -d
```

### Production
```bash
# Build production JAR
mvn clean package -Pprod

# Run with production profile
java -jar target/smoking-cessation-platform-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Contributing

1. Fork the project
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

Nếu bạn có bất kỳ câu hỏi nào, vui lòng tạo issue hoặc liên hệ:
- Email: support@smokingcessation.com
- GitHub Issues: [Issues](https://github.com/se183119/smoking-cessation-platform/issues)
