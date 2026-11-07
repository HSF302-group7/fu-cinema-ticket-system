# FU Cinema Ticket System (FUCinemaTicketSystem)

> **Dự án:** Hệ thống quản lý bán vé xem phim trực tuyến — Assignment 01  
> **Môn học:** HSF302 — Advanced Java / Web Application Development with Spring Boot & Spring Data JPA  
> **Nhóm thực hiện:** Nhóm 7 (HSF302-group7)  
> **Kiến trúc:** 3-Layer Architecture (Controller – Service – Repository) + Repository Pattern  

---

## 1. Giới thiệu dự án

**FU Cinema Ticket System** là ứng dụng web quản lý và bán vé xem phim trực tuyến, hỗ trợ tự động hoá toàn diện quy trình kinh doanh của rạp chiếu phim:
- **Khách hàng (Customer):** Tra cứu thông tin phim đang chiếu / sắp chiếu, xem lịch chiếu theo ngày, chọn ghế trên sơ đồ ma trận thời gian thực, đặt vé trực tuyến, quản lý lịch sử giao dịch và đánh giá phim sau khi xem.
- **Quản trị viên (Admin):** Quản lý danh mục phim, thể loại, phòng chiếu, lịch chiếu (chống trùng giờ thông minh), quản lý khách hàng, duyệt / huỷ đơn đặt vé và theo dõi báo cáo doanh thu chi tiết theo mốc thời gian.

---

## 2. Công nghệ sử dụng

- **Ngôn ngữ:** Java 17 / 21
- **Framework nền tảng:** Spring Boot 3.3.x (Spring MVC, Spring Data JPA, Spring Validation)
- **Bảo mật:** Spring Security Crypto (`BCryptPasswordEncoder`), Session-based Authentication & `HandlerInterceptor` (Role-Based Access Control)
- **Cơ sở dữ liệu:** Microsoft SQL Server 2019/2022 (quản lý qua Spring Data JPA, Hibernate ORM)
- **Giao diện:** Thymeleaf Template Engine, Bootstrap 5, Font Awesome, HTML5, Vanilla JavaScript
- **Công cụ build & quản lý:** Apache Maven, Git, GitHub Projects & GitHub Flow

---

## 3. Kiến trúc hệ thống (3-Layer Architecture)

```
                    ┌─────────────────────────┐
                    │       Web Browser       │
                    └────────────┬────────────┘
                                 │ HTTP (Thymeleaf UI)
                                 ▼
                    ┌─────────────────────────┐
                    │    Controller Layer     │
                    │  (REST / Spring MVC)    │
                    └────────────┬────────────┘
                                 │ DTO / Form Validation
                                 ▼
                    ┌─────────────────────────┐
                    │      Service Layer      │
                    │  (@Service, Interfaces) │
                    └────────────┬────────────┘
                                 │ Entities / JPQL / Native
                                 ▼
                    ┌─────────────────────────┐
                    │    Repository Layer     │
                    │    (Spring Data JPA)    │
                    └────────────┬────────────┘
                                 │ JDBC / SQL Server
                                 ▼
                    ┌─────────────────────────┐
                    │    Database (MSSQL)     │
                    │  `FUCinemaTicketDB`     │
                    └─────────────────────────┘
```

---

## 4. Hướng dẫn cài đặt và khởi chạy

### Yêu cầu môi trường
- **JDK:** Java Development Kit 17 hoặc 21 trở lên
- **Database:** Microsoft SQL Server (đã kích hoạt SQL Server Authentication)
- **Maven:** Đã tích hợp sẵn qua Maven Wrapper (`mvnw` / `mvnw.cmd`)

### Các bước cài đặt:

1. **Clone repository về máy:**
   ```bash
   git clone https://github.com/HSF302-group7/fu-cinema-ticket-system.git
   cd fu-cinema-ticket-system
   ```

2. **Tạo Database trên SQL Server:**
   Mở SQL Server Management Studio (SSMS) hoặc Azure Data Studio và chạy lệnh:
   ```sql
   CREATE DATABASE FUCinemaTicketDB;
   ```

3. **Cấu hình kết nối cơ sở dữ liệu:**
   - Copy file cấu hình mẫu:
     ```bash
     # Windows (cmd/powershell):
     copy src\main\resources\application-local.properties.example src\main\resources\application-local.properties
     ```
   - Mở file `src/main/resources/application-local.properties` và cập nhật username / password SQL Server của bạn:
     ```properties
     spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FUCinemaTicketDB;encrypt=true;trustServerCertificate=true;
     spring.datasource.username=sa
     spring.datasource.password=YourPassword123
     ```

4. **Chạy ứng dụng:**
   - **Windows:**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```

5. **Truy cập ứng dụng:**
   - Mở trình duyệt web và truy cập địa chỉ: **[http://localhost:8080](http://localhost:8080)**
   - *Ghi chú:* Ở lần khởi động đầu tiên, hệ thống sẽ tự động khởi tạo 9 bảng và nạp đầy đủ dữ liệu mẫu (Seed Data) sẵn sàng để kiểm thử.

---

## 5. Danh sách tài khoản mẫu (Seed Data)

Hệ thống đã nạp sẵn các tài khoản kiểm thử tương ứng với từng vai trò:

| Vai trò | Email đăng nhập | Mật khẩu | Mục đích kiểm thử |
|---|---|---|---|
| **ADMIN** | `admin@fucinema.vn` | `Admin@123` | Quản trị toàn bộ rạp: Phim, Phòng, Suất chiếu, Đơn vé, Khách hàng, Báo cáo |
| **CUSTOMER** | `an.nv@gmail.com` | `Customer@123` | Khách hàng chuẩn: Đặt vé, xem lịch sử, huỷ vé, đổi mật khẩu, viết đánh giá |
| **CUSTOMER** | `binh.tt@gmail.com` | `Customer@123` | Khách hàng dưới 16 tuổi: Kiểm thử chặn đặt vé phim giới hạn độ tuổi (BR-11) |

---

## 6. Thành viên nhóm & Phân công công việc (5 Thành viên)

| STT | Thành viên | Vai trò | GitHub Account | Chức năng phụ trách chính (TODO) |
|:---:|---|:---:|:---:|---|
| 1 | **Nguyễn Ngọc Thạch** | Leader / Maintainer | [`ngocthach324`](https://github.com/ngocthach324) | **Nền tảng & Xác thực (TODO 1–4, 7–15, 32):** Khởi tạo project, cấu hình, layout Thymeleaf, toàn bộ 9 Entity & JPA Repositories khung, Seed data, xử lý lỗi tập trung, Đăng nhập / Đăng ký / Phân quyền Interceptor, README & Release v1.0.0. |
| 2 | **Nguyễn Văn Nhật** | Developer | [`vietnhatsun`](https://github.com/vietnhatsun) | **Khách hàng & Hồ sơ (TODO 16, 17, 30):** Admin quản lý khách hàng (tìm kiếm, phân trang, khoá/mở khoá, xoá BR-04), Khách hàng xem/cập nhật hồ sơ cá nhân và đổi mật khẩu. |
| 3 | **Hoàng Ngọc Nam** | Developer | [`nemz6406`](https://github.com/nemz6406) | **Thể loại, Phim & Đánh giá (TODO 18–21, 27, 31):** Admin quản lý thể loại (BR-05) & Phim (Soft delete BR-06), Trang chủ & Chi tiết phim, chức năng Bonus Đánh giá phim (BR-16). |
| 4 | **Lại Bằng Long** | Developer | [`long1616`](https://github.com/long1616) | **Phòng, Suất chiếu & Đặt vé (TODO 22, 23, 28):** Admin quản lý phòng chiếu & suất chiếu (chống trùng lịch BR-07, BR-08, huỷ đơn BR-09), Khách hàng chọn ghế ma trận thời gian thực và tạo đơn vé. |
| 5 | **Nguyễn Trí Anh Khoa** | Developer | [`NguyenTriAnhKhoaDE200261`](https://github.com/NguyenTriAnhKhoaDE200261) | **Đơn hàng & Báo cáo (TODO 24, 25, 26, 29):** Admin duyệt/huỷ đơn đặt vé (BR-13), Báo cáo thống kê doanh thu theo phim & danh sách đơn CONFIRMED (BR-15), Khách hàng xem lịch sử và huỷ đơn (BR-14). |

---

## 7. Các quy tắc nghiệp vụ cốt lõi (Business Rules)

- **BR-01 & BR-02:** Ràng buộc duy nhất Email, SĐT, CCCD; Khách hàng đăng ký phải từ đủ 13 tuổi; Mật khẩu tối thiểu 8 ký tự gồm chữ hoa, chữ thường và chữ số.
- **BR-03:** Tài khoản ở trạng thái `LOCKED` tuyệt đối không được phép đăng nhập.
- **BR-04:** Xoá khách hàng: chưa có đơn đặt vé $\rightarrow$ Xoá hẳn; đã có đơn $\rightarrow$ Chuyển trạng thái sang `LOCKED`.
- **BR-05:** Chỉ được phép xoá thể loại khi không còn phim nào thuộc thể loại đó.
- **BR-06:** Xoá phim: nếu phim đã có suất chiếu $\rightarrow$ Soft delete (chuyển sang `STOPPED`). Phim `STOPPED` tự động ẩn khỏi trang chủ.
- **BR-07 & BR-08:** Hai suất chiếu trong cùng một phòng không được phép trùng giờ giao thoa; Tự động cộng 15 phút dọn phòng giữa các suất chiếu.
- **BR-09:** Xoá suất chiếu: nếu đã có đơn đặt vé $\rightarrow$ Chuyển suất sang `CANCELLED` và tự động huỷ toàn bộ các đơn vé liên quan.
- **BR-10 & BR-12:** Đặt vé: mỗi đơn cho phép chọn từ 1 đến 8 ghế; Ghế đã bán bị khoá thời gian thực; Tạo đơn vé và các chi tiết vé trong cùng một transaction.
- **BR-11:** Phim giới hạn độ tuổi (`T13`, `T16`, `T18`): Khách hàng phải đủ tuổi tính đến ngày chiếu mới được đặt vé.
- **BR-13:** Luồng trạng thái đơn: `PENDING` $\rightarrow$ `CONFIRMED` (Admin duyệt) hoặc `CANCELLED`. Đơn đã huỷ không thể phục hồi.
- **BR-14:** Khách hàng chỉ được phép tự huỷ vé trước giờ chiếu ít nhất 2 giờ.
- **BR-15:** Báo cáo doanh thu: Lọc theo khoảng ngày hợp lệ (`StartDate <= EndDate`), sắp xếp doanh thu giảm dần, danh sách đơn CONFIRMED giảm dần theo ngày.
- **BR-16 (Bonus):** Chỉ khách hàng đã mua vé và xem xong phim mới được quyền chấm điểm 1–5 sao và viết nhận xét.

---

## 8. Quy chuẩn làm việc nhóm trên Git & GitHub

- **Mô hình phân nhánh:** Git Flow rút gọn (`main` $\leftarrow$ `develop` $\leftarrow$ `feature/*`, `docs/*`, `chore/*`).
- **Quy tắc bảo vệ:** Tuyệt đối không commit/push trực tiếp vào `main` và `develop`. Mọi thay đổi đều thông qua Issue $\rightarrow$ Branch $\rightarrow$ Pull Request (Squash and Merge) $\rightarrow$ Review $\rightarrow$ Merge.
- **Quy ước Commit:** Tuân thủ chuẩn [Conventional Commits](https://www.conventionalcommits.org/):
  - `feat(<module>): <mô tả>` (Thêm chức năng)
  - `fix(<module>): <mô tả>` (Sửa lỗi)
  - `docs(<module>): <mô tả>` (Tài liệu)
  - `chore(<module>): <mô tả>` (Cấu hình, build)
