# Smoking Cessation Support Platforms 

### 💡 Tính Năng Nổi Bật

- **Tư vấn cá nhân hóa:** Gợi ý lộ trình cai thuốc phù hợp với từng người dùng dựa trên dữ liệu cá nhân và thói quen.
- **Theo dõi tiến trình:** Ghi nhận, thống kê và đánh giá tiến trình cai thuốc qua từng ngày.
- **Phân tích & Báo cáo:** Cung cấp báo cáo chi tiết giúp người dùng hiểu rõ tiến bộ và động viên tiếp tục hành trình.
- **Cộng đồng hỗ trợ:** Nơi chia sẻ kinh nghiệm, nhận tư vấn và động viên từ cộng đồng cùng mục tiêu.
- **Bảo mật & riêng tư:** Đảm bảo thông tin cá nhân được bảo mật tuyệt đối.

### 🚀 Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java (Spring Boot)
- **Cơ sở dữ liệu:** SQL Server
- **Containerization:** Docker
- **Frontend:** NextJS
- **Các thư viện, framework nổi bật:**
  - Spring Boot & Spring Ecosystem: 
    - spring-boot-starter-actuator
(Giám sát, health check, metrics cho ứng dụng Spring Boot)
    - spring-boot-starter-data-jpa
(ORM, thao tác database với JPA/Hibernate)
    - spring-boot-starter-security
(Bảo mật, xác thực, phân quyền)
    - spring-boot-starter-web
(Xây dựng RESTful API, web server)
    - spring-boot-starter-mail
(Gửi email qua SMTP)
    - spring-boot-devtools
(Hỗ trợ phát triển, tự động reload)
    - spring-boot-starter-test
(Thư viện test cho Spring Boot)
    - spring-security-test
(Test các tính năng bảo mật)
  - Database Drivers:
    - com.microsoft.sqlserver:mssql-jdbc
(Driver kết nối SQL Server)
    - com.h2database:h2
(Database H2 in-memory, thường dùng cho test/dev)
  - API Documentation
    - org.springdoc:springdoc-openapi-starter-webmvc-ui
(Tạo tài liệu API tự động với Swagger UI/OpenAPI)
  - Utilities & Others
    - org.projectlombok:lombok
(Tự động sinh getter/setter, giảm code boilerplate)
    - org.modelmapper:modelmapper
(Map dữ liệu giữa entity và DTO)
    - vn.payos:payos-java
(Tích hợp thanh toán PayOS) 

### ⚡️ Hướng Dẫn Cài Đặt Nhanh

### Yêu cầu
- Java 11+
- Docker
- Maven

### Cách chạy dự án

```bash
# Clone source
git clone https://github.com/se183119/se1834-swp391-gr04-smoking-cessation-support-platforms.git
cd se1834-swp391-gr04-smoking-cessation-support-platforms

# Build project
mvn clean install

# Chạy bằng Docker
docker-compose up -d

# Hoặc chạy trực tiếp bằng Maven
mvn spring-boot:run
```

### 💪 Đóng Góp

Chúng tôi chào đón mọi ý kiến đóng góp từ cộng đồng! Hãy mở Issue hoặc Pull Request nếu bạn có sáng kiến, phát hiện lỗi hoặc muốn phát triển thêm tính năng.

### 🧑‍💻 Nhóm Phát Triển

- **Team 04** | **SE1834**

### ⭐️ Cùng nhau xây dựng cộng đồng không khói thuốc!
