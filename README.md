# Website Bán Sách Công Ty FBC (FBC Bookstore Service)

> **Dự án thực hành môn Kiểm thử phần mềm (Software Testing)**  
> **Trường Đại học Công nghiệp Hà Nội (HaUI) - Khoa Công nghệ Thông tin**

---

## 📌 Thông tin Đề tài & Nhóm thực hiện

- **Đề tài:** Kiểm thử Website bán sách công ty FBC (FBC Book Store Service)
- **Học phần:** Kiểm thử phần mềm (IT6013.2 - K18)
- **Giảng viên hướng dẫn:** ThS. Trần Tiến Dũng
- **Nhóm sinh viên thực hiện:** Nhóm 5
  - **Đào Văn Công** (MSSV: 2023601884) - Trưởng nhóm / Quản lý dự án / Tester
  - **Nguyễn Văn Mạnh** (MSSV: 2023604645) - Quản trị viên & Phân tích hệ thống
  - **Đàm Minh Hiếu** (MSSV: 2023604325) - Lập kế hoạch & Tester
  - **Đỗ Đức Thụ** (MSSV: 2023602619) - Hỗ trợ kỹ thuật & Báo cáo

---

## 🛠️ Công nghệ Sử dụng (Tech Stack)

- **Ngôn ngữ & Nền tảng:** Java 21, Spring Boot 3.5.6
- **Cơ sở dữ liệu:** MySQL 8.0 (kết nối qua Spring Data JPA / Hibernate)
- **Công cụ tìm kiếm:** Elasticsearch 8.x (tìm kiếm sách theo từ khóa, danh mục)
- **Bảo mật & Phân quyền:** Spring Security 6, OAuth2 Resource Server, Nimbus Jose JWT
- **Thư viện phụ trợ:** Lombok, MapStruct (DTO Mapping), Hibernate Validator
- **Công cụ kiểm thử:**
  - **Postman:** Kiểm thử chức năng API (Functional Testing - 95 Test Cases)
  - **Burp Suite:** Kiểm thử an toàn thông tin & lỗ hổng bảo mật (Security Testing - 15 Test Cases)

---

## 📂 Cấu trúc Dự án

```text
├── .mvn/                          # Cấu hình Maven Wrapper
├── postman/                       # Kịch bản kiểm thử API Postman
│   ├── FBC_Bookstore_Postman_Collection.json
│   └── FBC_Local.postman_environment.json
├── src/
│   ├── main/
│   │   ├── java/com/bookstore/book_sell_service/
│   │   │   ├── configuration/     # Cấu hình Security, JWT, Khởi tạo dữ liệu mẫu
│   │   │   ├── controller/        # REST Controllers (API Endpoints)
│   │   │   ├── dto/               # Data Transfer Objects (Request/Response)
│   │   │   ├── entity/            # Các thực thể dữ liệu JPA
│   │   │   ├── enums/             # Enums trạng thái, vai trò người dùng
│   │   │   ├── exception/         # Xử lý ngoại lệ toàn cục (Global Exception Handling)
│   │   │   ├── mapper/            # MapStruct mappers
│   │   │   ├── repositories/      # JPA Repositories
│   │   │   ├── search/            # Elasticsearch Document & Repositories
│   │   │   ├── services/          # Business logic services
│   │   │   └── specification/     # Dynamic query specifications
│   │   └── resources/
│   │       ├── application.yaml   # Cấu hình kết nối DB, Port, JWT
│   │       └── elasticsearch-analyzer.json
│   └── test/                      # Unit & Integration Tests
├── mvnw / mvnw.cmd                # Maven wrapper script
├── pom.xml                        # File cấu hình phụ thuộc Maven
└── README.md                      # Tài liệu dự án
```

---

## 🚀 Hướng dẫn Cài đặt & Khởi chạy

### 1. Yêu cầu môi trường
- **JDK:** Java Development Kit 21 trở lên
- **MySQL:** Phiên bản 8.0 trở lên
- **Elasticsearch:** Phiên bản 8.x (tùy chọn cho tính năng tìm kiếm nâng cao)

### 2. Cấu hình Cơ sở dữ liệu
Tạo database MySQL trên máy cục bộ:
```sql
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Cấu hình thông tin tài khoản MySQL trong file `src/main/resources/application.yaml`:
```yaml
server:
  port: 8081
  servlet:
    context-path: /store

spring:
  datasource:
    url: "jdbc:mysql://localhost:3306/bookstore"
    username: root
    password: ""
```

*(Lưu ý: Hibernate tự động sinh bảng dữ liệu nhờ cấu hình `ddl-auto: update`)*

### 3. Khởi chạy Ứng dụng
Sử dụng Maven Wrapper để chạy trực tiếp:
- **Trên Windows (cmd / PowerShell):**
  ```bash
  .\mvnw.cmd spring-boot:run
  ```
- **Trên Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

Sau khi ứng dụng khởi động thành công:
- **Base URL:** `http://localhost:8081/store`

---

## 👥 Dữ liệu Khởi tạo Mặc định (Seed Data)

Khi khởi chạy lần đầu, hệ thống tự động tạo các tài khoản và dữ liệu mẫu:

| Loại tài khoản | Username | Password | Vai trò (Role) |
|---|---|---|---|
| **Quản trị viên (Admin)** | `admin` | `admin` | `ADMIN` |
| **Nhân viên (Staff)** | `nhanvien1` đến `nhanvien5` | `password123` | `STAFF`, `USER` |
| **Khách hàng (User)** | `khachhang1` đến `khachhang5` | `password123` | `USER` |

Ngoài ra, hệ thống tự động tạo mẫu 10 cuốn sách và các danh mục tiểu thuyết ban đầu.

---

## 🧪 Kế hoạch & Kịch bản Kiểm thử (Testing)

Dự án đi kèm bộ kịch bản kiểm thử toàn diện **110 Test Cases** được thiết kế chi tiết:

1. **Bộ kịch bản Postman (95 ca kiểm thử chức năng):**
   - Xác thực & Phân quyền JWT (Đăng ký, Đăng nhập, Refresh Token, Introspect, Logout).
   - Quản lý Sách, Thể loại, Tác giả, Nhà xuất bản (CRUD, Lọc, Tìm kiếm).
   - Giỏ hàng & Đặt hàng (Thêm/Sửa/Xóa giỏ hàng, Thanh toán đơn hàng, Cập nhật trạng thái).
   - Quản lý Khách hàng & Nhân viên (Thông tin cá nhân, Đổi mật khẩu, Phân quyền).
   - Quản lý Mã giảm giá (Voucher) & Đánh giá bình luận sách.
   - Thống kê & Báo cáo doanh thu.

2. **Bộ kịch bản Burp Suite (15 ca kiểm thử an toàn bảo mật):**
   - Kiểm tra xác thực Token JWT giả mạo/hết hạn.
   - Kiểm thử lỗ hổng Broken Access Control (IDOR, leo thang đặc quyền ngang/dọc).
   - Kiểm tra SQL Injection, XSS trong các trường nhập liệu.
   - Kiểm tra tấn công Brute-force và rò rỉ dữ liệu nhạy cảm.

> File kịch bản kiểm thử nằm tại thư mục `postman/`:
> - `postman/FBC_Bookstore_Postman_Collection.json`
> - `postman/FBC_Local.postman_environment.json`
