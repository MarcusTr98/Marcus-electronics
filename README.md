# Marcus Electronics 🛒

> Nền tảng thương mại điện tử bán thiết bị điện tử tiêu dùng — xử lý đơn hàng đầu cuối và tích hợp thanh toán tự động.

[![Java](https://img.shields.io/badge/Java_21-%23ED8B00.svg?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js_3-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)](https://vuejs.org/)
[![SQL Server](https://img.shields.io/badge/SQL_Server-CC2927?style=flat&logo=microsoftsqlserver&logoColor=white)](https://www.microsoft.com/sql-server)

---

## ✨ Features

- **Danh mục sản phẩm** với quản lý biến thể / SKU (màu sắc, kích thước, dung lượng...)
- **Xác thực JWT** + phân quyền RBAC qua Spring Security 6 Filter Chain
- **Tích hợp cổng thanh toán VNPAY** với đảm bảo giao dịch ACID
- **Quản lý đơn hàng** — giỏ hàng, checkout, theo dõi đơn, dashboard admin
- **Bảng quản trị admin** — thống kê doanh thu, báo cáo đơn hàng, quản lý sản phẩm & người dùng
- **Global Exception Handler** — xử lý lỗi tập trung toàn bộ API
- **Schema chuẩn 3NF** — thiết kế quan hệ sạch, không dư thừa dữ liệu

---

## 🏗️ Architecture & Technical Highlights

### Security

- Xác thực stateless JWT với refresh token
- Phân quyền CUSTOMER / ADMIN tại tầng filter chain
- Mã hóa mật khẩu với BCrypt

### Thiết kế API

- RESTful API với cấu trúc response nhất quán
- Xử lý ngoại lệ tập trung qua `@RestControllerAdvice`
- Kiểm thử API bằng Postman collection (xem thư mục `/docs`)

### Database

- Schema biến thể sản phẩm (SKU) — hỗ trợ kết hợp nhiều thuộc tính
- Chuẩn hóa 3NF — loại bỏ dị thường khi cập nhật / thêm dữ liệu
- Tối ưu truy vấn với Spring Data JPA + native query khi cần

### Payment

- Tích hợp VNPAY sandbox
- Xác minh webhook đảm bảo tính toàn vẹn giao dịch
- Trạng thái đơn hàng chỉ cập nhật sau khi nhận callback xác nhận thanh toán

### Testing

- Kiểm thử tích hợp thủ công qua Postman
- Unit test với JUnit 5 + Mockito cho tầng service

---

## 🗂️ Cấu trúc Project

```
marcus-electronics/
├── src/
│   ├── main/
│   │   ├── java/com/marcus/electronics/
│   │   │   ├── config/          # Cấu hình Security, CORS, ứng dụng
│   │   │   ├── controller/      # REST controllers — nhận và trả về HTTP request
│   │   │   ├── service/         # Business logic — xử lý nghiệp vụ chính
│   │   │   ├── repository/      # Spring Data JPA — giao tiếp với database
│   │   │   ├── entity/          # JPA entities — ánh xạ bảng database
│   │   │   ├── dto/             # Data Transfer Objects — request/response
│   │   │   ├── exception/       # Global Exception Handler — xử lý lỗi tập trung
│   │   │   └── util/            # Tiện ích: JWT helper, VNPAY helper
│   │   └── resources/
│   │       └── application.yml  # Cấu hình database, JWT, VNPAY
│   └── test/                    # Unit test với JUnit 5 + Mockito
│
├── frontend/                    # Vue.js 3 SPA
│   └── src/
│       ├── assets/              # CSS, JS, fonts
│       ├── components/          # Header, Footer, ProductCard...
│       ├── layouts/             # Khung giao diện chung
│       ├── router/
│       │   └── index.js         # Định nghĩa route + phân quyền
│       ├── views/
│       │   ├── admin/           # Trang dành cho Admin (dashboard, báo cáo...)
│       │   ├── client/          # Trang dành cho khách hàng
│       │   └── auth/            # Đăng nhập / Đăng ký
│       ├── App.vue              # Root component
│       └── main.js              # Entry point
│
└── pom.xml
```

---

## ⚙️ Tech Stack

| Layer    | Technology                                  |
| -------- | ------------------------------------------- |
| Backend  | Java 21, Spring Boot 3.2, Spring Security 6 |
| ORM      | Spring Data JPA, Hibernate                  |
| Database | Microsoft SQL Server                        |
| Frontend | Vue.js 3, Axios, Bootstrap 5                |
| Auth     | JWT (Access + Refresh token)                |
| Payment  | VNPAY                                       |
| Testing  | JUnit 5, Mockito, Postman                   |
| Build    | Maven                                       |
| CI/CD    | GitHub Actions                              |

---

## 🚀 Hướng dẫn chạy dự án

### Yêu cầu môi trường

- Java 21+
- Maven 3.8+
- SQL Server (hoặc Docker)
- Node.js 18+ (cho frontend)

### Backend

```bash
git clone https://github.com/MarcusTr98/marcus-electronics.git
cd marcus-electronics

# Cấu hình database và thông tin VNPAY
cp src/main/resources/application.yml.example src/main/resources/application.yml
# Chỉnh sửa application.yml với thông tin kết nối DB và VNPAY sandbox

mvn clean install
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Mặc định: API chạy tại `http://localhost:8080`, frontend tại `http://localhost:5173`.

---

## 👤 Author

**Marcus Tran** · [github.com/MarcusTr98](https://github.com/MarcusTr98) · [marcus.tran2202@gmail.com](mailto:marcus.tran2202@gmail.com)
