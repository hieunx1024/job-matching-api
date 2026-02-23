<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" />
</p>

# 🚀 JobHunter – Backend API

**Hệ thống quản lý tuyển dụng** (Job Recruitment Platform) – RESTful API Backend được xây dựng bằng **Spring Boot 3**, **Spring Security (JWT)**, **Spring Data JPA** và **MySQL**.

Dự án phục vụ đồ án tốt nghiệp, mô phỏng hệ thống tuyển dụng trực tuyến hoàn chỉnh với đa vai trò: **Admin**, **HR (Nhà tuyển dụng)** và **Candidate (Ứng viên)**.

---

## 📋 Mục lục

- [Tính năng](#-tính-năng)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Cài đặt & Chạy dự án](#-cài-đặt--chạy-dự-án)
- [API Endpoints](#-api-endpoints)
- [Bảo mật](#-bảo-mật)
- [Cơ sở dữ liệu](#-cơ-sở-dữ-liệu)

---

## ✨ Tính năng

### 🔐 Authentication & Authorization
- Đăng ký, Đăng nhập bằng email/password
- Đăng nhập bằng **Google OAuth2**
- JWT Access Token + Refresh Token (HttpOnly Cookie)
- Phân quyền theo vai trò (Role-Based Access Control): `SUPER_ADMIN`, `HR`, `CANDIDATE`
- Permission-based Authorization (gán quyền chi tiết cho từng Role)

### 👤 Quản lý Người dùng
- CRUD Users (Admin)
- Chọn vai trò khi đăng ký (Social Login Onboarding)
- Cập nhật profile cá nhân

### 🏢 Quản lý Công ty
- HR đăng ký công ty mới → Admin duyệt
- CRUD thông tin công ty
- Liên kết User-Company

### 💼 Quản lý Việc làm (Jobs)
- CRUD Jobs (HR tạo, Admin quản lý)
- Tìm kiếm Job theo tên, kỹ năng, địa điểm
- Phân trang, lọc, sắp xếp với Spring Filter
- Gắn kỹ năng (Skills) cho từng Job

### 📄 Quản lý Hồ sơ ứng tuyển (Resumes)
- Candidate nộp CV (upload file PDF)
- HR xem danh sách hồ sơ theo công ty
- Cập nhật trạng thái: `PENDING` → `REVIEWING` → `APPROVED` / `REJECTED`
- Candidate theo dõi lịch sử ứng tuyển & thống kê

### 💳 Thanh toán & Gói dịch vụ
- Tích hợp **VNPAY** thanh toán trực tuyến
- Quản lý gói đăng ký (Subscription Packages)
- Giới hạn số tin đăng theo gói (Free: 2 bài, Pro/Enterprise: tuỳ gói)
- Lưu lịch sử thanh toán (Payment History)

### 📊 Dashboard & Thống kê
- Admin Dashboard: Tổng users, companies, jobs, resumes, doanh thu
- HR Dashboard: Số tin đã đăng, còn lại, gói hiện tại
- Candidate Dashboard: Thống kê ứng tuyển cá nhân

### 📁 Upload & Quản lý Files
- Upload CV (PDF/DOC)
- Upload logo công ty
- Lưu trữ file trên server

---

## 🏗 Kiến trúc hệ thống

```
┌─────────────────┐     HTTP/REST      ┌──────────────────────┐
│   React Frontend │ ◄──────────────── │  Spring Boot Backend  │
│   (Port 5173)    │                   │   (Port 8080)         │
└─────────────────┘                    └──────────┬───────────┘
                                                  │
                                       ┌──────────▼───────────┐
                                       │     MySQL Database    │
                                       │     (Port 3306)       │
                                       └──────────────────────┘
```

**Mô hình phân lớp (Layered Architecture):**

```
Controller  →  Service  →  Repository  →  Database
     ↕              ↕
   DTO/Response    Domain Entity
```

---

## 🛠 Công nghệ sử dụng

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **Java** | 17 | Ngôn ngữ chính |
| **Spring Boot** | 3.2.4 | Framework backend |
| **Spring Security** | 6.x | Authentication & Authorization |
| **Spring Data JPA** | 3.x | ORM & Database Access |
| **MySQL** | 8.0 | Cơ sở dữ liệu |
| **Lombok** | 1.18.x | Giảm boilerplate code |
| **JWT (OAuth2 Resource Server)** | – | Token-based Authentication |
| **Spring Filter** | 3.1.7 | Dynamic query filtering |
| **SpringDoc OpenAPI** | 2.5.0 | API Documentation (Swagger UI) |
| **Google API Client** | 2.2.0 | Google OAuth2 Login |
| **Thymeleaf** | – | Email Templates |
| **Gradle (Kotlin DSL)** | 8.x | Build tool |

---

## 📂 Cấu trúc dự án

```
src/main/java/vn/hieu/jobhunter/
├── config/                 # Cấu hình hệ thống
│   ├── SecurityConfiguration.java      # Spring Security, JWT, CORS
│   ├── DatabaseInitializer.java        # Khởi tạo dữ liệu mặc định
│   ├── PermissionInterceptor.java      # Kiểm tra quyền truy cập API
│   └── ...
├── controller/             # REST API Controllers
│   ├── AuthController.java             # Login, Register, OAuth2, Refresh Token
│   ├── JobController.java              # CRUD Jobs, Search, Posting Stats
│   ├── CompanyController.java          # CRUD Companies, Register Company
│   ├── ResumeController.java           # CRUD Resumes, Status Update
│   ├── UserController.java             # CRUD Users, Profile
│   ├── PaymentController.java          # VNPAY Integration
│   ├── DashboardController.java        # Admin Dashboard Stats
│   ├── SkillController.java            # CRUD Skills
│   ├── RoleController.java             # CRUD Roles
│   ├── PermissionController.java       # CRUD Permissions
│   ├── FileController.java             # Upload/Download Files
│   └── ...
├── domain/                 # JPA Entities
│   ├── User.java
│   ├── Job.java
│   ├── Company.java
│   ├── Resume.java
│   ├── Skill.java
│   ├── Role.java / Permission.java
│   ├── SubscriptionPackage.java
│   ├── UserSubscription.java
│   ├── PaymentHistory.java
│   ├── CompanyRegistration.java
│   └── response/           # DTO Response classes
├── repository/             # Spring Data JPA Repositories
├── service/                # Business Logic Layer
│   ├── UserService.java
│   ├── JobService.java
│   ├── ResumeService.java
│   ├── PaymentService.java
│   ├── JobPostingService.java          # Posting limit validation
│   └── ...
└── util/                   # Utilities
    ├── SecurityUtil.java               # JWT Token creation/validation
    ├── error/                          # Custom Exceptions
    └── annotation/                     # Custom Annotations (@ApiMessage)
```

---

## ⚙ Cài đặt & Chạy dự án

### Yêu cầu hệ thống

- **Java** >= 17 (`java --version`)
- **MySQL** >= 8.0
- **Gradle** (đã tích hợp Gradle Wrapper)

### 1. Clone dự án

```bash
git clone https://github.com/hieunx1024/tttn.git
cd tttn
```

### 2. Cấu hình Database

Tạo database MySQL:

```sql
CREATE DATABASE jobhunter;
```

Cập nhật file `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jobhunter
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Cấu hình VNPAY (Tuỳ chọn)

```properties
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.hash-secret=YOUR_HASH_SECRET
vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:5173/hr/payment/return
```

### 4. Chạy dự án

```bash
./gradlew bootRun
```

Server sẽ khởi động tại: `http://localhost:8080`

### 5. Truy cập Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Đăng nhập |
| `POST` | `/api/v1/auth/register` | Đăng ký |
| `POST` | `/api/v1/auth/google` | Đăng nhập Google |
| `GET` | `/api/v1/auth/account` | Lấy thông tin tài khoản |
| `GET` | `/api/v1/auth/refresh` | Làm mới Access Token |
| `POST` | `/api/v1/auth/logout` | Đăng xuất |

### Jobs
| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/v1/jobs` | Danh sách jobs (phân quyền) |
| `GET` | `/api/v1/jobs/all` | Tất cả jobs (public) |
| `GET` | `/api/v1/jobs/{id}` | Chi tiết job |
| `GET` | `/api/v1/jobs/search` | Tìm kiếm jobs |
| `GET` | `/api/v1/jobs/posting-stats` | Thống kê đăng tin HR |
| `POST` | `/api/v1/jobs` | Tạo job (HR) |
| `PUT` | `/api/v1/jobs` | Cập nhật job |
| `DELETE` | `/api/v1/jobs/{id}` | Xóa job |

### Companies
| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/v1/companies/public` | Danh sách công ty (public) |
| `GET` | `/api/v1/companies/{id}` | Chi tiết công ty |
| `POST` | `/api/v1/companies/register` | HR đăng ký công ty |
| `PUT` | `/api/v1/companies` | Cập nhật công ty |

### Resumes
| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/v1/resumes` | Danh sách hồ sơ (theo role) |
| `GET` | `/api/v1/resumes/my-history` | Lịch sử ứng tuyển |
| `GET` | `/api/v1/resumes/my-stats` | Thống kê ứng tuyển |
| `POST` | `/api/v1/resumes` | Nộp hồ sơ |
| `PATCH` | `/api/v1/resumes/{id}/status` | Cập nhật trạng thái |

### Payments
| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/api/v1/payments/create` | Tạo link thanh toán VNPAY |
| `GET` | `/api/v1/payments/vnpay-return` | Callback VNPAY |

### Dashboard
| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/v1/dashboard/admin` | Thống kê Admin Dashboard |

---

## 🔒 Bảo mật

- **JWT Access Token**: Gửi qua header `Authorization: Bearer <token>`
- **Refresh Token**: Lưu trong HttpOnly Cookie, tự động refresh khi hết hạn
- **CORS**: Chỉ chấp nhận request từ `http://localhost:5173`
- **Permission Interceptor**: Kiểm tra quyền truy cập API dựa trên Role-Permission mapping
- **Password Encryption**: BCrypt hashing
- **CSRF**: Được disable cho REST API (stateless)

---

## 🗄 Cơ sở dữ liệu

### ERD tổng quan (Các bảng chính)

```
users ──────────── roles ──────────── permissions
  │                   │
  ├── resumes         │
  │     └── jobs ─────┤
  │           └── skills (Many-to-Many)
  │           └── companies
  │
  ├── user_subscriptions ── subscription_packages
  └── payment_history
```

### Các bảng chính

| Bảng | Mô tả |
|---|---|
| `users` | Người dùng (Admin, HR, Candidate) |
| `roles` | Vai trò (SUPER_ADMIN, HR, CANDIDATE) |
| `permissions` | Quyền hạn chi tiết |
| `companies` | Thông tin công ty |
| `company_registrations` | Yêu cầu đăng ký công ty (chờ duyệt) |
| `jobs` | Tin tuyển dụng |
| `skills` | Kỹ năng |
| `job_skill` | Liên kết Job-Skill (N-N) |
| `resumes` | Hồ sơ ứng tuyển |
| `subscription_packages` | Gói đăng ký dịch vụ |
| `user_subscriptions` | Gói đã mua của user |
| `payment_history` | Lịch sử thanh toán |

---

## 👨‍💻 Tác giả

- **Nguyễn Xuân Hiếu**
- Đồ án tốt nghiệp – Hệ thống tuyển dụng trực tuyến (JobHunter)

---

## 📄 License

Dự án được phát triển phục vụ mục đích học tập và đồ án tốt nghiệp.
