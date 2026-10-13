# Assignment 01 — Solution: Hướng dẫn code step-by-step

> **Hệ thống:** FUCinemaTicketSystem — Spring Boot 3.3 · Spring Data JPA · Thymeleaf · SQL Server
> **Đi kèm:** `Assignment1_ReadMe.md` (đề bài, 32 TODO) · `Assignment1_teamwork.md` (quy trình Git/GitHub)
>
> Mỗi TODO gồm: **Mục tiêu → Các bước → Code đầy đủ → Giải thích & lưu ý → Kiểm thử → ✅ Checklist làm việc nhóm**.
> Code trong tài liệu là **một lời giải hoàn chỉnh** (≈ 90 file Java + 25 template); nhóm có thể làm khác miễn đạt tiêu chí chấp nhận.

> ⚠️ **Trạng thái kiểm chứng:** bộ code được viết và rà soát tĩnh (cú pháp Java, import, chữ ký method giữa interface – implementation – repository, thuộc tính dùng trong template) nhưng **chưa được build/chạy** trong môi trường soạn tài liệu (không tải được thư viện Maven). Hãy chạy `mvn verify` và kiểm thử theo từng TODO; nếu gặp lỗi, mở issue `type: bug` theo quy trình.

## Mục lục

- [0. Chuẩn bị chung](#0-chuẩn-bị-chung)
- **[Part 0 — Khởi tạo dự án](#part-0--khởi-tạo-dự-án)**
  - [TODO 1 — Tạo project Spring Boot](#todo-1--tạo-project-spring-boot)
  - [TODO 2 — Cấu hình ứng dụng & kết nối SQL Server](#todo-2--cấu-hình-ứng-dụng--kết-nối-sql-server)
  - [TODO 3 — Cấu trúc package & layout Thymeleaf](#todo-3--cấu-trúc-package--layout-thymeleaf)
- **[Part A — Entity & cơ sở dữ liệu](#part-a--entity--cơ-sở-dữ-liệu)**
  - [TODO 4 — Enum, Account, Customer (1–1)](#todo-4--enum-account-customer-11)
  - [TODO 5 — Genre, Movie (1–N)](#todo-5--genre-movie-1n)
  - [TODO 6 — Room, Showtime](#todo-6--room-showtime)
  - [TODO 7 — Booking, Ticket, Review (khoá kép)](#todo-7--booking-ticket-review-khoá-kép)
  - [TODO 8 — Seed dữ liệu mẫu](#todo-8--seed-dữ-liệu-mẫu)
- **[Part B — Repository](#part-b--repository)**
  - [TODO 9 — Repository & derived query](#todo-9--repository--derived-query)
  - [TODO 10 — Custom query (Q1–Q12)](#todo-10--custom-query-q1q12)
- **[Part C — Service & xử lý lỗi](#part-c--service--xử-lý-lỗi)**
  - [TODO 11 — Service interface, exception, tiện ích](#todo-11--service-interface-exception-tiện-ích)
  - [TODO 12 — Xử lý lỗi tập trung & trang lỗi](#todo-12--xử-lý-lỗi-tập-trung--trang-lỗi)
- **[Part D — Xác thực & phân quyền](#part-d--xác-thực--phân-quyền)**
  - [TODO 13 — Đăng nhập / đăng xuất](#todo-13--đăng-nhập--đăng-xuất)
  - [TODO 14 — Đăng ký tài khoản khách hàng](#todo-14--đăng-ký-tài-khoản-khách-hàng)
  - [TODO 15 — Phân quyền bằng HandlerInterceptor](#todo-15--phân-quyền-bằng-handlerinterceptor)
- **[Part E — Admin: Quản lý khách hàng](#part-e--admin-quản-lý-khách-hàng)**
  - [TODO 16 — Danh sách & tìm kiếm khách hàng](#todo-16--danh-sách--tìm-kiếm-khách-hàng)
  - [TODO 17 — Thêm / sửa / khoá / xoá khách hàng](#todo-17--thêm--sửa--khoá--xoá-khách-hàng)
- **[Part F — Admin: Thể loại & phim](#part-f--admin-thể-loại--phim)**
  - [TODO 18 — CRUD thể loại](#todo-18--crud-thể-loại)
  - [TODO 19 — Danh sách phim: tìm, lọc, phân trang, sắp xếp](#todo-19--danh-sách-phim-tìm-lọc-phân-trang-sắp-xếp)
  - [TODO 20 — Thêm / sửa phim](#todo-20--thêm--sửa-phim)
  - [TODO 21 — Xoá phim (soft delete)](#todo-21--xoá-phim-soft-delete)
- **[Part G — Admin: Phòng & suất chiếu](#part-g--admin-phòng--suất-chiếu)**
  - [TODO 22 — CRUD phòng chiếu](#todo-22--crud-phòng-chiếu)
  - [TODO 23 — CRUD suất chiếu & kiểm tra trùng lịch](#todo-23--crud-suất-chiếu--kiểm-tra-trùng-lịch)
- **[Part H — Admin: Đặt vé & báo cáo](#part-h--admin-đặt-vé--báo-cáo)**
  - [TODO 24 — Danh sách & chi tiết đơn đặt vé (Admin)](#todo-24--danh-sách--chi-tiết-đơn-đặt-vé-admin)
  - [TODO 25 — Xác nhận / huỷ đơn](#todo-25--xác-nhận--huỷ-đơn)
  - [TODO 26 — Báo cáo doanh thu & Dashboard](#todo-26--báo-cáo-doanh-thu--dashboard)
- **[Part I — Customer](#part-i--customer)**
  - [TODO 27 — Trang chủ & chi tiết phim](#todo-27--trang-chủ--chi-tiết-phim)
  - [TODO 28 — Đặt vé chọn ghế](#todo-28--đặt-vé-chọn-ghế)
  - [TODO 29 — Lịch sử đặt vé & huỷ đơn](#todo-29--lịch-sử-đặt-vé--huỷ-đơn)
  - [TODO 30 — Hồ sơ cá nhân & đổi mật khẩu](#todo-30--hồ-sơ-cá-nhân--đổi-mật-khẩu)
  - [TODO 31 — (Bonus) Đánh giá phim](#todo-31--bonus-đánh-giá-phim)
- **[Part J — Hoàn thiện](#part-j--hoàn-thiện)**
  - [TODO 32 — README, test, release v1.0.0](#todo-32--readme-test-release-v100)
- [Phụ lục A — Bảng tra file → TODO](#phụ-lục-a--bảng-tra-file--todo)
- [Phụ lục B — Lỗi thường gặp](#phụ-lục-b--lỗi-thường-gặp)
- [Phụ lục C — Checklist tổng trước khi release](#phụ-lục-c--checklist-tổng-trước-khi-release)

---

## 0. Chuẩn bị chung

### 0.1 Công cụ
| Công cụ | Phiên bản |
|---|---|
| JDK | 21 trở lên |
| Maven | 3.9+ (hoặc Maven Wrapper `mvnw`) |
| SQL Server | 2019+ (Developer/Express), bật SQL Authentication, TCP 1433 |
| IDE | IntelliJ IDEA / STS (bật Annotation Processing cho Lombok) |
| Git | 2.40+, tài khoản GitHub |

### 0.2 Phân công & issue (theo `Assignment1_teamwork.md`)
| TODO | Issue | Người làm | Nhánh |
|---|---|---|---|
| 1, 2, 3 | #2 | A (Leader) | `feature/2-project-setup` |
| 4, 5, 6, 7, 9, 10 | #3 | A (Leader) — B review | `feature/3-entities-repositories` |
| 8 | #4 | A (Leader) | `feature/4-seed-data` |
| 11, 12 | #5 | A (Leader) | `feature/5-service-contracts-error-handling` |
| 13, 14, 15 | #6 | A (Leader) | `feature/6-auth` |
| 16, 17 | #7 | B | `feature/7-customer-admin` |
| 18 | #8 | C | `feature/8-genre-crud` |
| 19, 20, 21 | #9 | C | `feature/9-movie-crud` |
| 22 | #10 | D | `feature/10-room-crud` |
| 23 | #11 | D | `feature/11-showtime-crud` |
| 24, 25 | #12 | E | `feature/12-booking-admin` |
| 26 | #17 | E | `feature/17-revenue-report` |
| 27 | #13 | C | `feature/13-home-movie-detail` |
| 28 | #14 | D | `feature/14-seat-booking` |
| 29 | #15 | E | `feature/15-booking-history` |
| 30 | #16 | B | `feature/16-profile` |
| 31 | #18 | C | `feature/18-movie-review` |
| 32 | #19 | A (Leader) | `docs/19-readme → release/1.0.0` |

> TODO 11 (service interface) và TODO 12 (xử lý lỗi) gộp chung issue **#5**. TODO 10 (custom query) nằm trong issue **#3** cùng entity/repository — mỗi chủ module **review** query của module mình.

### 0.3 Thứ tự phụ thuộc
```
TODO 1–3 ─► 4–7 ─► 9–10 ─► 8 (seed) ─► 11–12 ─► 13–15 (auth)
                                              ├─► 16–17 (B)   ─► 30 (B)
                                              ├─► 18–21 (C)   ─► 27 (C) ─► 31 (C, bonus)
                                              ├─► 22–23 (D)   ─► 28 (D)
                                              └─► 24–25 (E)   ─► 29 (E), 26 (E)
                                                                      └─► 32 (A, release)
```

### 0.4 Quy trình chung cho mỗi TODO
```bash
# 1. Bắt đầu issue (TODO đầu tiên của issue)
git switch develop && git pull
git switch -c <nhánh-của-issue>

# 2. Code từng bước → commit nhỏ theo Conventional Commits
git add <file>
git commit -m "<type>(<scope>): <subject>" -m "Refs #<issue>"

# 3. Chạy & kiểm thử
mvn -q compile
mvn spring-boot:run          # đi qua bảng "Kiểm thử" của TODO

# 4. Đồng bộ develop, đẩy lên, mở/ cập nhật PR (TODO cuối của issue)
git fetch origin && git rebase origin/develop
git push -u origin <nhánh>   # sau rebase: git push --force-with-lease
```

### 0.5 Dữ liệu test
Mọi bảng **Kiểm thử** giả định dữ liệu seed ban đầu (TODO 8, N = ngày chạy app). Muốn làm lại từ đầu: xoá và tạo lại database `FUCinemaTicketDB` rồi chạy app.

---

# Part 0 — Khởi tạo dự án

## TODO 1 — Tạo project Spring Boot

**Issue:** #2 · **Người làm:** A (Leader) · **Nhánh:** `feature/2-project-setup`

**Mục tiêu:** Có project Spring Boot chạy được với đủ dependency theo TECH-01, có `.gitignore`.

### Các bước

1. Vào https://start.spring.io → **Maven**, **Java 17**, Spring Boot **4.x.x**.
   - Group: `com.fu` · Artifact: `fu-cinema-ticket-system` · Package name: `com.fu.cinema`.
   - Dependencies: *Spring Web, Thymeleaf, Spring Data JPA, Validation, MS SQL Server Driver, Lombok, Spring Boot DevTools, H2 Database*.
2. Giải nén, mở bằng IntelliJ/STS, bật **Annotation Processing** (Lombok).
3. Thêm dependency `spring-security-crypto` (chỉ dùng `BCryptPasswordEncoder`, **không** kéo cả Spring Security) và đổi scope H2 thành `test`.
4. Bổ sung `.gitignore` (bỏ qua `target/`, `.idea/`, `application-local.properties`).
5. Đẩy commit đầu tiên lên `main`, tạo nhánh `develop` (xem `Assignment1_teamwork.md` mục 4 bước 2).

### Code

📄 **`pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.1</version>
        <relativePath/>
    </parent>

    <groupId>com.fu</groupId>
    <artifactId>fu-cinema-ticket-system</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>fu-cinema-ticket-system</name>
    <description>FU Cinema Ticket System - Spring Boot, Spring Data JPA, Thymeleaf, SQL Server</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-crypto</artifactId>
        </dependency>
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope> <!--Thay test bằng runtime chạy ở TODO 1 khi chưa cấu hình db -->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

📄 **`src/main/java/com/fu/cinema/FuCinemaTicketSystemApplication.java`**

```java
package com.fu.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FuCinemaTicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuCinemaTicketSystemApplication.class, args);
    }
}
```

📄 **`.gitignore`**

```gitignore
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
.idea/
*.iml
*.iws
*.ipr
.vscode/
.DS_Store
*.log
application-local.properties
.env
```

### Giải thích & lưu ý

- `spring-security-crypto` không cấu hình gì thêm → không bật màn hình login mặc định của Spring Security.
- `h2` chỉ dùng cho test (scope `test`) → `mvn verify` chạy được trên mọi máy mà không cần SQL Server.
- `devtools` để `optional` → không đóng gói vào bản build.

### Kiểm thử

| Lệnh | Kết quả mong đợi |
|---|---|
| `mvn -v` | Maven 3.9+, Java 17+ |
| `mvn clean compile` | `BUILD SUCCESS` |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#2** đã được assign cho **A (Leader)**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/2-project-setup` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `chore(setup): init spring boot project` + `Refs #2`
  - [ ] `chore(setup): add dependencies for web, thymeleaf, jpa, validation and mssql` + `Refs #2`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #2.

## TODO 2 — Cấu hình ứng dụng & kết nối SQL Server

**Issue:** #2 · **Người làm:** A (Leader) · **Nhánh:** `feature/2-project-setup`

**Mục tiêu:** Kết nối database `FUCinemaTicketDB`; thông tin đăng nhập DB **không** nằm trong Git.

### Các bước

1. Tạo database trên SQL Server: `CREATE DATABASE FUCinemaTicketDB;`
2. Viết `application.properties` (được commit): bật profile `local`, cấu hình JPA, Thymeleaf, trang lỗi.
3. Tạo `application-local.properties.example` (được commit) — file mẫu.
4. Mỗi thành viên copy thành `application-local.properties` (**không** commit) và điền mật khẩu của máy mình.
5. Tạo `application-test.properties` cho test dùng H2.

### Code

📄 **`src/main/resources/application.properties`**

```properties
spring.application.name=fu-cinema-ticket-system

# Profile "local" chứa thông tin kết nối DB (file application-local.properties KHÔNG commit)
spring.profiles.active=local

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# Thymeleaf
spring.thymeleaf.cache=false

# Hiển thị trang lỗi tuỳ biến, không lộ stack trace
server.error.include-stacktrace=never
server.error.whitelabel.enabled=false
```

📄 **`src/main/resources/application-local.properties.example`**

```properties
# Copy file này thành application-local.properties (đã .gitignore) và điền thông tin máy bạn
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FUCinemaTicketDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=CHANGE_ME
```

📄 **`src/test/resources/application-test.properties`**

```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MSSQLServer;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

### Giải thích & lưu ý

- `spring.jpa.open-in-view=false`: bắt buộc lấy đủ dữ liệu trong service (JOIN FETCH / EntityGraph) → phát hiện sớm lỗi lazy, giống môi trường production.
- `ddl-auto=update`: Hibernate tự tạo/cập nhật bảng từ entity. **Không** dùng `create`/`create-drop` với DB dùng chung (mất dữ liệu).
- `server.error.include-stacktrace=never`: không lộ stack trace ra giao diện.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| `mvn spring-boot:run` | Log có `HikariPool-1 - Start completed`, không lỗi kết nối |
| `git status` | **Không** thấy `application-local.properties` |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/2-project-setup` (issue #2); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `chore(config): add application properties with local profile` + `Refs #2`
  - [ ] `chore(config): add test profile with h2` + `Refs #2`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #2.

## TODO 3 — Cấu trúc package & layout Thymeleaf

**Issue:** #2 · **Người làm:** A (Leader) · **Nhánh:** `feature/2-project-setup`

**Mục tiêu:** Thống nhất cấu trúc package cho cả nhóm; layout dùng chung có menu theo vai trò, flash message, phân trang.

### Các bước

1. Tạo các package (cấu trúc bên dưới).
2. Tạo `templates/fragments/layout.html` gồm các fragment: `head(title)`, `navbar`, `flash`, `pagination(page, url)`, `footer`.
3. Tạo `static/css/app.css`.
4. Mỗi trang sau này dùng khung:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Tiêu đề')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <!-- nội dung trang -->
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```
5. *(Tạm thời)* để kiểm tra layout trước khi có TODO 27, tạo controller tạm trả về một trang đơn giản — **xoá trước khi merge**.

**Cấu trúc package:**
```
com.fu.cinema
├── config        PasswordConfig, WebConfig, AuthInterceptor, DataSeeder
├── controller    AuthController, HomeController, GlobalModelAttributes
│   ├── admin     Admin*Controller
│   └── customer  Customer*Controller
├── dto           *Form, *DTO, SessionUser, SeatMap, ReportResult...
├── entity        Account, Customer, Genre, Movie, Room, Showtime, Booking, Ticket, Review, ReviewId
├── enums         Role, AccountStatus, AgeRating, MovieStatus, RoomType, RoomStatus, ShowtimeStatus, BookingStatus
├── exception     BusinessException, ResourceNotFoundException, GlobalExceptionHandler
├── repository    *Repository
├── service       *Service (interface)
│   └── impl      *ServiceImpl
└── util          SessionUtils, PageUtils, ValidationUtils, DateUtils
```

### Code

📄 **`src/main/resources/templates/fragments/layout.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">

<!-- ===== <head> dùng chung: th:replace="~{fragments/layout :: head('Tiêu đề')}" ===== -->
<head th:fragment="head(title)">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title th:text="${title} + ' | FU Cinema'">FU Cinema</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" th:href="@{/css/app.css}">
</head>

<body>

<!-- ===== Menu theo vai trò ===== -->
<nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand fw-bold" th:href="@{/}">🎬 FU Cinema</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" th:href="@{/}">Phim</a></li>

                <th:block th:if="${currentUser != null and currentUser.admin}">
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin}">Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/customers}">Khách hàng</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/genres}">Thể loại</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/movies}">Quản lý phim</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/rooms}">Phòng chiếu</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/showtimes}">Suất chiếu</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/bookings}">Đơn đặt vé</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/admin/reports}">Báo cáo</a></li>
                </th:block>

                <th:block th:if="${currentUser != null and currentUser.customer}">
                    <li class="nav-item"><a class="nav-link" th:href="@{/customer/bookings}">Vé của tôi</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/customer/profile}">Hồ sơ</a></li>
                </th:block>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <th:block th:if="${currentUser == null}">
                    <a class="btn btn-outline-light btn-sm" th:href="@{/login}">Đăng nhập</a>
                    <a class="btn btn-warning btn-sm" th:href="@{/register}">Đăng ký</a>
                </th:block>
                <th:block th:if="${currentUser != null}">
                    <span class="text-light small" th:text="'Xin chào, ' + ${currentUser.displayName}">Xin chào</span>
                    <form th:action="@{/logout}" method="post" class="m-0">
                        <button class="btn btn-outline-light btn-sm" type="submit">Đăng xuất</button>
                    </form>
                </th:block>
            </div>
        </div>
    </div>
</nav>

<!-- ===== Flash message: biến "success" / "error" (flash attribute hoặc model) ===== -->
<div th:fragment="flash">
    <div th:if="${success}" class="alert alert-success alert-dismissible fade show" role="alert">
        <span th:text="${success}"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <div th:if="${error}" class="alert alert-danger alert-dismissible fade show" role="alert">
        <span th:text="${error}"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</div>

<!-- ===== Thanh phân trang: page = Page<?>, url = URL hiện tại đã bỏ tham số page ===== -->
<nav th:fragment="pagination(page, url)" th:if="${page.totalPages > 1}">
    <ul class="pagination justify-content-center">
        <li class="page-item" th:classappend="${page.first} ? 'disabled'">
            <a class="page-link" th:href="${url + (url.contains('?') ? '&' : '?') + 'page=' + (page.number - 1)}">«</a>
        </li>
        <li class="page-item" th:each="i : ${#numbers.sequence(0, page.totalPages - 1)}"
            th:classappend="${i == page.number} ? 'active'">
            <a class="page-link" th:href="${url + (url.contains('?') ? '&' : '?') + 'page=' + i}" th:text="${i + 1}">1</a>
        </li>
        <li class="page-item" th:classappend="${page.last} ? 'disabled'">
            <a class="page-link" th:href="${url + (url.contains('?') ? '&' : '?') + 'page=' + (page.number + 1)}">»</a>
        </li>
    </ul>
    <p class="text-center text-muted small"
       th:text="|Trang ${page.number + 1}/${page.totalPages} · ${page.totalElements} bản ghi|"></p>
</nav>

<footer th:fragment="footer" class="border-top py-3 mt-5 text-center text-muted small">
    © FU Cinema Ticket System — HSF302
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</footer>

</body>
</html>
```

📄 **`src/main/resources/static/css/app.css`**

```css
.poster { width: 100%; aspect-ratio: 2 / 3; object-fit: cover; }
.seat-grid { display: inline-block; }
.seat-row { display: flex; align-items: center; gap: 4px; margin-bottom: 4px; }
.seat-row .row-label { width: 24px; font-weight: 600; }
.seat input { display: none; }
.seat label { width: 36px; height: 32px; display: flex; align-items: center; justify-content: center;
              border: 1px solid #198754; border-radius: 6px; font-size: 11px; cursor: pointer; }
.seat input:checked + label { background: #198754; color: #fff; }
.seat.sold label { background: #dee2e6; border-color: #adb5bd; color: #6c757d; cursor: not-allowed; }
.screen { background: #343a40; color: #fff; text-align: center; border-radius: 4px; margin-bottom: 16px; }
```

### Giải thích & lưu ý

- Menu dựa vào biến `currentUser` (được đưa vào mọi view ở TODO 13). Khi chưa có, `currentUser` là `null` → menu khách.
- Fragment `pagination(page, url)` dùng chung cho mọi danh sách: `url` là URL hiện tại **đã bỏ tham số `page`** (`PageUtils.currentUrlWithoutPage()`) → giữ nguyên từ khoá/bộ lọc khi chuyển trang.
- Bootstrap lấy từ CDN; không commit thư viện vào repo.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| Mở trang tạm | Có thanh menu, footer, nút Đăng nhập/Đăng ký |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/2-project-setup` (issue #2); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `chore(setup): create layered package structure` + `Refs #2`
  - [ ] `feat(layout): add base thymeleaf layout with role-based menu` + `Refs #2`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`chore(setup): bootstrap spring boot project structure and configuration`** → base `develop`, mô tả theo PR template, ghi `Closes #2`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 1, 2, 3; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Đúng groupId/package `com.fu.cinema`
  - [ ] Không có dependency thừa (ví dụ `spring-boot-starter-security`)
  - [ ] `.gitignore` có `application-local.properties`
  - [ ] Không có mật khẩu thật trong `application.properties`
  - [ ] `open-in-view=false`
  - [ ] Mọi thành viên đồng ý cấu trúc package (sau PR này không tự ý đổi)
  - [ ] Fragment đặt tên rõ ràng, dùng `th:replace`
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #2 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part A — Entity & cơ sở dữ liệu

## TODO 4 — Enum, `Account`, `Customer` (1–1)

**Issue:** #3 · **Người làm:** A (Leader) — B review · **Nhánh:** `feature/3-entities-repositories`

**Mục tiêu:** Tạo 8 enum và quan hệ 1–1 `Customer` → `Account`.

### Các bước

1. Tạo 8 enum trong package `enums`. `AgeRating` có `minAge`, `RoomType` có `label` để hiển thị.
2. Tạo entity `Account`: `username` unique, `password` (BCrypt hash), `role`, `status` lưu dạng `STRING`.
3. Tạo entity `Customer`: các cột unique (`mobile`, `identityCard`, `email`), `@OneToOne(fetch = LAZY)` + `@JoinColumn(name = "account_id", unique = true)`.

### Code

📄 **`src/main/java/com/fu/cinema/enums/Role.java`**

```java
package com.fu.cinema.enums;

public enum Role {
    ADMIN, CUSTOMER
}
```

📄 **`src/main/java/com/fu/cinema/enums/AccountStatus.java`**

```java
package com.fu.cinema.enums;

public enum AccountStatus {
    ACTIVE, LOCKED
}
```

📄 **`src/main/java/com/fu/cinema/enums/AgeRating.java`**

```java
package com.fu.cinema.enums;

public enum AgeRating {
    P(0), T13(13), T16(16), T18(18);

    private final int minAge;

    AgeRating(int minAge) {
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge;
    }
}
```

📄 **`src/main/java/com/fu/cinema/enums/MovieStatus.java`**

```java
package com.fu.cinema.enums;

public enum MovieStatus {
    COMING_SOON, SHOWING, STOPPED
}
```

📄 **`src/main/java/com/fu/cinema/enums/RoomType.java`**

```java
package com.fu.cinema.enums;

public enum RoomType {
    TWO_D("2D"), THREE_D("3D"), IMAX("IMAX");

    private final String label;

    RoomType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
```

📄 **`src/main/java/com/fu/cinema/enums/RoomStatus.java`**

```java
package com.fu.cinema.enums;

public enum RoomStatus {
    ACTIVE, MAINTENANCE
}
```

📄 **`src/main/java/com/fu/cinema/enums/ShowtimeStatus.java`**

```java
package com.fu.cinema.enums;

public enum ShowtimeStatus {
    OPEN, CANCELLED
}
```

📄 **`src/main/java/com/fu/cinema/enums/BookingStatus.java`**

```java
package com.fu.cinema.enums;

public enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED
}
```

📄 **`src/main/java/com/fu/cinema/entity/Account.java`**

```java
package com.fu.cinema.entity;

import com.fu.cinema.enums.AccountStatus;
import com.fu.cinema.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;                 // BCrypt hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    public Account(String username, String password, Role role, AccountStatus status) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}
```

📄 **`src/main/java/com/fu/cinema/entity/Customer.java`**

```java
package com.fu.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 10)
    private String mobile;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false, unique = true, length = 12)
    private String identityCard;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 1–1: Customer là owning side, giữ FK account_id (unique)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;
}
```

### Giải thích & lưu ý

- **Customer giữ FK** (`account_id`) → `Customer` là owning side; `unique = true` đảm bảo quan hệ 1–1 ở mức DB.
- `@Enumerated(EnumType.STRING)`: lưu `"ADMIN"` thay vì `0` → thêm/đổi thứ tự enum không làm hỏng dữ liệu.
- Không dùng `@Data` cho entity: `@Data` sinh `equals/hashCode/toString` duyệt quan hệ → vòng lặp, lazy exception.

### Kiểm thử

| Kiểm tra trong SSMS | Kết quả mong đợi |
|---|---|
| Bảng `accounts`, `customers` | Có PK identity, `customers.account_id` có FK + UNIQUE |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#3** đã được assign cho **A (Leader)**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/3-entities-repositories` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(entity): add enums for roles and statuses` + `Refs #3`
  - [ ] `feat(entity): add account and customer with one-to-one mapping` + `Refs #3`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #3.

## TODO 5 — `Genre`, `Movie` (1–N)

**Issue:** #3 · **Người làm:** A (Leader) — C review · **Nhánh:** `feature/3-entities-repositories`

**Mục tiêu:** Quan hệ 1–N thể loại – phim.

### Các bước

1. `Genre`: `genreName` unique, `@OneToMany(mappedBy = "genre") List<Movie> movies` (phía inverse, không cascade).
2. `Movie`: `@ManyToOne(fetch = LAZY, optional = false) @JoinColumn(name = "genre_id")`, `ageRating`, `status` dạng enum.

### Code

📄 **`src/main/java/com/fu/cinema/entity/Genre.java`**

```java
package com.fu.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @Column(nullable = false, unique = true, length = 50)
    private String genreName;

    @Column(nullable = false, length = 500)
    private String description;

    @OneToMany(mappedBy = "genre")
    private List<Movie> movies = new ArrayList<>();

    public Genre(String genreName, String description) {
        this.genreName = genreName;
        this.description = description;
    }
}
```

📄 **`src/main/java/com/fu/cinema/entity/Movie.java`**

```java
package com.fu.cinema.entity;

import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String director;

    @Column(nullable = false)
    private Integer duration;                // phút

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AgeRating ageRating;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 500)
    private String posterUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovieStatus status;
}
```

### Giải thích & lưu ý

- `mappedBy = "genre"` trỏ tới **tên field** trong `Movie`. Phía `mappedBy` không tạo cột.
- **Không cascade REMOVE** từ Genre sang Movie: xoá thể loại phải bị chặn khi còn phim (BR-05) chứ không xoá luôn phim.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| Bảng `movies` | Có `genre_id` NOT NULL, FK → `genres` |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/3-entities-repositories` (issue #3); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(entity): add genre and movie entities` + `Refs #3`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #3.

## TODO 6 — `Room`, `Showtime`

**Issue:** #3 · **Người làm:** A (Leader) — D review · **Nhánh:** `feature/3-entities-repositories`

**Mục tiêu:** Phòng chiếu và suất chiếu (N–1 tới Movie và Room).

### Các bước

1. `Room`: `roomName` unique, `totalRows`, `seatsPerRow`, method `getCapacity()` (không lưu DB).
2. `Showtime`: `@ManyToOne` tới `Movie` và `Room`, `LocalDateTime startTime/endTime`, `BigDecimal ticketPrice`.

### Code

📄 **`src/main/java/com/fu/cinema/entity/Room.java`**

```java
package com.fu.cinema.entity;

import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, unique = true, length = 50)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RoomType roomType;

    @Column(nullable = false)
    private Integer totalRows;               // 1..26 (A..Z)

    @Column(nullable = false)
    private Integer seatsPerRow;             // 1..30

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status;

    public Room(String roomName, RoomType roomType, int totalRows, int seatsPerRow, RoomStatus status) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
        this.status = status;
    }

    public int getCapacity() {
        return totalRows * seatsPerRow;
    }
}
```

📄 **`src/main/java/com/fu/cinema/entity/Showtime.java`**

```java
package com.fu.cinema.entity;

import com.fu.cinema.enums.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showtimeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal ticketPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShowtimeStatus status;
}
```

### Giải thích & lưu ý

- Tiền dùng **`BigDecimal`** (không dùng `double` → sai số làm tròn).
- `getCapacity()` là method tính toán → Thymeleaf gọi được bằng `${r.capacity}`.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| Bảng `showtimes` | Có `movie_id`, `room_id`, `start_time`, `end_time`, `ticket_price` |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/3-entities-repositories` (issue #3); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(entity): add room and showtime entities` + `Refs #3`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #3.

## TODO 7 — `Booking`, `Ticket`, `Review` (khoá kép)

**Issue:** #3 · **Người làm:** A (Leader) — E, C review · **Nhánh:** `feature/3-entities-repositories`

**Mục tiêu:** Đơn đặt vé 1–N vé (cascade) và đánh giá có khoá chính kép.

### Các bước

1. `Booking`: `@OneToMany(mappedBy = "booking", cascade = ALL, orphanRemoval = true) List<Ticket> tickets` + helper `addTicket()` đồng bộ 2 chiều.
2. `Ticket`: `@ManyToOne` tới `Booking` và `Showtime`, `seatCode`, `price`.
3. `ReviewId` (`@Embeddable`, `Serializable`, `equals/hashCode`) gồm `customerId`, `movieId`.
4. `Review`: `@EmbeddedId ReviewId id` + `@MapsId("customerId")`, `@MapsId("movieId")` trên 2 quan hệ `@ManyToOne`.

### Code

📄 **`src/main/java/com/fu/cinema/entity/Booking.java`**

```java
package com.fu.cinema.entity;

import com.fu.cinema.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    // Booking 1–N Ticket: lưu/xoá Booking thì lưu/xoá luôn Ticket
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seatCode ASC")
    private List<Ticket> tickets = new ArrayList<>();

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setBooking(this);
    }
}
```

📄 **`src/main/java/com/fu/cinema/entity/Ticket.java`**

```java
package com.fu.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(nullable = false, length = 5)
    private String seatCode;                 // A1, B12...

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal price;

    public Ticket(Showtime showtime, String seatCode, BigDecimal price) {
        this.showtime = showtime;
        this.seatCode = seatCode;
        this.price = price;
    }
}
```

📄 **`src/main/java/com/fu/cinema/entity/ReviewId.java`**

```java
package com.fu.cinema.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Khoá chính kép (CustomerID, MovieID): bắt buộc Serializable + equals/hashCode
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ReviewId implements Serializable {

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "movie_id")
    private Long movieId;

    public ReviewId(Long customerId, Long movieId) {
        this.customerId = customerId;
        this.movieId = movieId;
    }
}
```

📄 **`src/main/java/com/fu/cinema/entity/Review.java`**

```java
package com.fu.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @EmbeddedId
    private ReviewId id;

    @MapsId("customerId")                    // lấy giá trị customerId của khoá kép từ quan hệ này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @MapsId("movieId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(nullable = false)
    private Integer rating;                  // 1..5

    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime reviewDate;
}
```

### Giải thích & lưu ý

- **Cascade ALL + orphanRemoval** chỉ dùng cho quan hệ "sở hữu hoàn toàn" (Ticket không tồn tại nếu không có Booking). Lưu `Booking` → tự lưu các `Ticket`.
- Lớp khoá kép **bắt buộc** `implements Serializable` và có `equals/hashCode` (Lombok `@EqualsAndHashCode`).
- `@MapsId("customerId")`: giá trị `id.customerId` được lấy từ `customer.customerId` → không cần set 2 lần, không bị trùng cột.
- `Ticket` lưu `showtime` riêng (dư thừa có chủ đích) để truy vấn ghế đã bán nhanh, không phải JOIN qua Booking.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| Bảng `reviews` | PK kép (`customer_id`, `movie_id`) |
| Bảng `tickets` | FK `booking_id`, `showtime_id` |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/3-entities-repositories` (issue #3); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(entity): add booking and ticket with cascade` + `Refs #3`
  - [ ] `feat(entity): add review with composite key` + `Refs #3`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #3.

## TODO 8 — Seed dữ liệu mẫu

**Issue:** #4 · **Người làm:** A (Leader) · **Nhánh:** `feature/4-seed-data`

**Mục tiêu:** Dữ liệu mẫu đủ để test mọi chức năng; mật khẩu mã hoá BCrypt.

### Các bước

> ⚠️ **Phụ thuộc:** cần các interface repository ở **TODO 9** (ít nhất phần `extends JpaRepository`). Làm TODO 9 trước rồi quay lại TODO 8.

1. Tạo bean `PasswordEncoder` (`BCryptPasswordEncoder`).
2. Tạo `DataSeeder implements CommandLineRunner`, chỉ seed khi `accountRepository.count() == 0`.
3. Dữ liệu suất chiếu/đơn đặt vé tính **tương đối theo ngày hiện tại** (`LocalDate.now()`) → luôn có suất "đã qua" và "sắp tới" mỗi khi chạy.

### Code

📄 **`src/main/java/com/fu/cinema/config/PasswordConfig.java`**

```java
package com.fu.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

📄 **`src/main/java/com/fu/cinema/config/DataSeeder.java`**

```java
package com.fu.cinema.config;

import com.fu.cinema.entity.*;
import com.fu.cinema.enums.*;
import com.fu.cinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            return;                                                          // chỉ seed khi DB rỗng
        }
        log.info(">>> Seeding sample data...");

        // ---- Accounts & Customers ----
        accountRepository.save(new Account("admin@fucinema.vn", passwordEncoder.encode("Admin@123"),
                Role.ADMIN, AccountStatus.ACTIVE));
        Customer an = customer("Nguyen Van An", "an.nv@gmail.com", "0901234567", LocalDate.of(2000, 5, 12), "079200001234");
        Customer binh = customer("Tran Thi Binh", "binh.tt@gmail.com", "0912345678", LocalDate.now().minusYears(15), "079209005678");

        // ---- Genres ----
        Genre action = genreRepository.save(new Genre("Hành động", "Phim hành động, kịch tính"));
        Genre anime = genreRepository.save(new Genre("Hoạt hình", "Phim hoạt hình cho mọi lứa tuổi"));
        Genre horror = genreRepository.save(new Genre("Kinh dị", "Phim kinh dị, giật gân"));
        Genre romance = genreRepository.save(new Genre("Tình cảm", "Phim tình cảm, lãng mạn"));
        genreRepository.save(new Genre("Khoa học viễn tưởng", "Phim khoa học viễn tưởng"));

        // ---- Movies ----
        Movie m1 = movie("Biệt Đội Siêu Tốc", "Lê Minh", 120, AgeRating.T13, action, MovieStatus.SHOWING, -10);
        Movie m2 = movie("Chú Mèo Phiêu Lưu", "Trần Hà", 95, AgeRating.P, anime, MovieStatus.SHOWING, -20);
        Movie m3 = movie("Ngôi Nhà Bí Ẩn", "Phạm Quân", 105, AgeRating.T18, horror, MovieStatus.SHOWING, -5);
        movie("Mùa Hè Năm Ấy", "Đỗ Lan", 110, AgeRating.T16, romance, MovieStatus.COMING_SOON, 14);
        movie("Vũ Trụ Song Song", "Ngô Tùng", 140, AgeRating.T13, action, MovieStatus.COMING_SOON, 30);
        Movie m6 = movie("Huyền Thoại Cũ", "Vũ Bình", 100, AgeRating.P, anime, MovieStatus.STOPPED, -120);

        // ---- Rooms ----
        Room r1 = roomRepository.save(new Room("Room 01", RoomType.TWO_D, 8, 12, RoomStatus.ACTIVE));
        Room r2 = roomRepository.save(new Room("Room 02", RoomType.THREE_D, 10, 14, RoomStatus.ACTIVE));
        roomRepository.save(new Room("Room 03", RoomType.IMAX, 12, 16, RoomStatus.MAINTENANCE));

        // ---- Showtimes ----
        LocalDate today = LocalDate.now();
        Showtime past = showtime(m6, r1, today.minusDays(3).atTime(19, 0), 70000, ShowtimeStatus.OPEN);
        Showtime pastAction = showtime(m1, r2, today.minusDays(2).atTime(20, 0), 90000, ShowtimeStatus.OPEN);
        Showtime s1 = showtime(m1, r1, today.plusDays(1).atTime(9, 0), 80000, ShowtimeStatus.OPEN);
        Showtime s2 = showtime(m2, r1, today.plusDays(1).atTime(14, 0), 70000, ShowtimeStatus.OPEN);
        Showtime s3 = showtime(m3, r2, today.plusDays(1).atTime(21, 0), 95000, ShowtimeStatus.OPEN);
        showtime(m1, r2, today.plusDays(2).atTime(18, 30), 90000, ShowtimeStatus.OPEN);
        showtime(m2, r2, today.plusDays(3).atTime(10, 0), 70000, ShowtimeStatus.OPEN);
        showtime(m3, r1, today.plusDays(3).atTime(20, 0), 85000, ShowtimeStatus.CANCELLED);

        // ---- Bookings (đủ 3 trạng thái, trải nhiều ngày) ----
        booking(an, past, today.minusDays(5), BookingStatus.CONFIRMED, "C5", "C6");
        booking(an, pastAction, today.minusDays(4), BookingStatus.CONFIRMED, "D7");
        booking(binh, s2, today.minusDays(1), BookingStatus.CONFIRMED, "A1", "A2", "A3");
        booking(an, s1, today, BookingStatus.PENDING, "E5", "E6");
        booking(binh, s1, today, BookingStatus.CANCELLED, "F1");
        booking(an, s3, today, BookingStatus.PENDING, "B4");

        log.info(">>> Seed completed");
    }

    private Customer customer(String name, String email, String mobile, LocalDate birthday, String cccd) {
        Account acc = accountRepository.save(new Account(email, passwordEncoder.encode("Customer@123"),
                Role.CUSTOMER, AccountStatus.ACTIVE));
        Customer c = new Customer();
        c.setFullName(name);
        c.setEmail(email);
        c.setMobile(mobile);
        c.setBirthday(birthday);
        c.setIdentityCard(cccd);
        c.setAccount(acc);
        return customerRepository.save(c);
    }

    private Movie movie(String title, String director, int duration, AgeRating rating, Genre genre,
                        MovieStatus status, int releaseOffsetDays) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setDirector(director);
        m.setDuration(duration);
        m.setAgeRating(rating);
        m.setGenre(genre);
        m.setStatus(status);
        m.setReleaseDate(LocalDate.now().plusDays(releaseOffsetDays));
        m.setDescription("Nội dung phim " + title + ".");
        m.setPosterUrl("https://placehold.co/300x450?text=" + title.replace(' ', '+'));
        return movieRepository.save(m);
    }

    private Showtime showtime(Movie movie, Room room, LocalDateTime start, long price, ShowtimeStatus status) {
        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setRoom(room);
        s.setStartTime(start);
        s.setEndTime(start.plusMinutes(movie.getDuration() + 15));
        s.setTicketPrice(BigDecimal.valueOf(price));
        s.setStatus(status);
        return showtimeRepository.save(s);
    }

    private void booking(Customer customer, Showtime showtime, LocalDate date, BookingStatus status, String... seats) {
        Booking b = new Booking();
        b.setCustomer(customer);
        b.setShowtime(showtime);
        b.setBookingDate(date.atTime(LocalTime.of(10, 30)));
        b.setStatus(status);
        b.setTotalAmount(showtime.getTicketPrice().multiply(BigDecimal.valueOf(seats.length)));
        List.of(seats).forEach(seat -> b.addTicket(new Ticket(showtime, seat, showtime.getTicketPrice())));
        bookingRepository.save(b);
    }
}
```

### Giải thích & lưu ý

**Dữ liệu sau khi seed (dùng cho mọi kịch bản test bên dưới):**

| Loại | Dữ liệu |
|---|---|
| Tài khoản | `admin@fucinema.vn` / `Admin@123` (ADMIN) · `an.nv@gmail.com` / `Customer@123` · `binh.tt@gmail.com` / `Customer@123` (**15 tuổi**) |
| Phim | *Biệt Đội Siêu Tốc* (T13, SHOWING) · *Chú Mèo Phiêu Lưu* (P, SHOWING) · *Ngôi Nhà Bí Ẩn* (T18, SHOWING) · *Mùa Hè Năm Ấy* (T16, COMING_SOON) · *Vũ Trụ Song Song* (T13, COMING_SOON) · *Huyền Thoại Cũ* (P, STOPPED) |
| Phòng | Room 01 (2D, 8×12) · Room 02 (3D, 10×14) · Room 03 (IMAX, 12×16, MAINTENANCE) |
| Suất chiếu (N = hôm nay) | N−3 19:00 *Huyền Thoại Cũ*/R01 · N−2 20:00 *Biệt Đội*/R02 · **N+1 09:00 *Biệt Đội*/R01** · N+1 14:00 *Chú Mèo*/R01 · N+1 21:00 *Ngôi Nhà*/R02 · N+2 18:30 *Biệt Đội*/R02 · N+3 10:00 *Chú Mèo*/R02 · N+3 20:00 *Ngôi Nhà*/R01 (CANCELLED) |
| Đơn | An: N−5 CONFIRMED (Huyền Thoại, C5 C6) · An: N−4 CONFIRMED (Biệt Đội N−2, D7) · Bình: N−1 CONFIRMED (Chú Mèo, A1 A2 A3) · An: N PENDING (Biệt Đội N+1, E5 E6) · Bình: N CANCELLED (Biệt Đội N+1, F1) · An: N PENDING (Ngôi Nhà, B4) |

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| Chạy app lần 1 | Log `>>> Seed completed` |
| Chạy app lần 2 | Không seed lại (không lỗi trùng unique) |
| `SELECT password FROM accounts` | Chuỗi bắt đầu `$2a$` (BCrypt) |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#4** đã được assign cho **A (Leader)**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/4-seed-data` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(seed): add password encoder bean` + `Refs #4`
  - [ ] `feat(seed): seed accounts, customers, genres and movies` + `Refs #4`
  - [ ] `feat(seed): seed rooms, showtimes and sample bookings` + `Refs #4`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(seed): seed sample data for development and testing`** → base `develop`, mô tả theo PR template, ghi `Closes #4`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 8; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Seed chỉ chạy khi DB rỗng
  - [ ] Có đủ trường hợp test: suất đã qua, suất huỷ, đơn đủ 3 trạng thái, khách < 16 tuổi
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #4 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part B — Repository

## TODO 9 — Repository & derived query

**Issue:** #3 · **Người làm:** A (Leader) · **Nhánh:** `feature/3-entities-repositories`

**Mục tiêu:** Mỗi entity có một repository `extends JpaRepository`; khai báo các derived query ở mục 9.1 của đề.

### Các bước

1. Tạo interface `XxxRepository extends JpaRepository<Entity, KiểuId>` cho **mọi** entity (`ReviewRepository` dùng `ReviewId`).
2. Khai báo derived query theo bảng dưới. Tên method dùng **tên field Java** (vd. `findByAccount_AccountId` đi qua quan hệ `account` → field `accountId`).
3. Chạy app: nếu sai tên field, app **lỗi ngay khi khởi động** (`PropertyReferenceException`).

| Repository | Derived query | Dùng ở |
|---|---|---|
| Account | `findByUsername`, `existsByUsername`, `existsByUsernameAndAccountIdNot` | TODO 13, 14, 17 |
| Customer | `findByAccount_AccountId`, `existsByEmail/Mobile/IdentityCard`, `exists...AndCustomerIdNot` | TODO 13, 14, 17, 30 |
| Genre | `existsByGenreNameIgnoreCase`, `existsByGenreNameIgnoreCaseAndGenreIdNot`, `findAllByOrderByGenreNameAsc` | TODO 18 |
| Movie | `existsByGenre_GenreId`, `countByStatus`, `findByStatusNotOrderByTitleAsc` | TODO 18, 23, 26 |
| Room | `findByStatusOrderByRoomNameAsc`, `existsByRoomNameIgnoreCase(AndRoomIdNot)` | TODO 22, 23 |
| Showtime | `existsByMovie_MovieId`, `existsByRoom_RoomId`, `findByMovie_MovieIdAndStatusAndStartTimeAfterOrderByStartTimeAsc` (+ `@EntityGraph`) | TODO 21, 22, 27 |
| Booking | `existsByCustomer_CustomerId`, `existsByShowtime_ShowtimeId`, `countByStatus`, `findByCustomer_CustomerId(AndStatus)` (+ `@EntityGraph`) | TODO 17, 23, 26, 29 |
| Review | `findByMovie_MovieIdOrderByReviewDateDesc` (+ `@EntityGraph`) | TODO 31 |

Các repository chỉ có derived query (code đầy đủ):

### Code

📄 **`src/main/java/com/fu/cinema/repository/AccountRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndAccountIdNot(String username, Long accountId);
}
```

📄 **`src/main/java/com/fu/cinema/repository/GenreRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    boolean existsByGenreNameIgnoreCase(String genreName);

    boolean existsByGenreNameIgnoreCaseAndGenreIdNot(String genreName, Long genreId);

    List<Genre> findAllByOrderByGenreNameAsc();
}
```

📄 **`src/main/java/com/fu/cinema/repository/RoomRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.entity.Room;
import com.fu.cinema.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByStatusOrderByRoomNameAsc(RoomStatus status);

    boolean existsByRoomNameIgnoreCase(String roomName);

    boolean existsByRoomNameIgnoreCaseAndRoomIdNot(String roomName, Long roomId);
}
```

📄 **`src/main/java/com/fu/cinema/repository/CustomerRepository.java`** — *phần derived query (custom query bổ sung ở TODO 10)*

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ===== Derived query =====
    Optional<Customer> findByAccount_AccountId(Long accountId);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByIdentityCard(String identityCard);

    boolean existsByEmailAndCustomerIdNot(String email, Long customerId);

    boolean existsByMobileAndCustomerIdNot(String mobile, Long customerId);

    boolean existsByIdentityCardAndCustomerIdNot(String identityCard, Long customerId);

    // (custom query: xem TODO 10)
}
```

📄 **`src/main/java/com/fu/cinema/repository/MovieRepository.java`** — *phần derived query (custom query bổ sung ở TODO 10)*

```java
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // ===== Derived query =====
    boolean existsByGenre_GenreId(Long genreId);

    long countByStatus(MovieStatus status);

    List<Movie> findByStatusNotOrderByTitleAsc(MovieStatus status);      // dropdown tạo suất chiếu

    // (custom query: xem TODO 10)
}
```

📄 **`src/main/java/com/fu/cinema/repository/ShowtimeRepository.java`** — *phần derived query (custom query bổ sung ở TODO 10)*

```java
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // ===== Derived query =====
    boolean existsByMovie_MovieId(Long movieId);

    boolean existsByRoom_RoomId(Long roomId);

    @EntityGraph(attributePaths = "room")
    List<Showtime> findByMovie_MovieIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            Long movieId, ShowtimeStatus status, LocalDateTime after);

    // (custom query: xem TODO 10)
}
```

📄 **`src/main/java/com/fu/cinema/repository/BookingRepository.java`** — *phần derived query (custom query bổ sung ở TODO 10)*

```java
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ===== Derived query =====
    boolean existsByCustomer_CustomerId(Long customerId);

    boolean existsByShowtime_ShowtimeId(Long showtimeId);

    long countByStatus(BookingStatus status);

    @EntityGraph(attributePaths = {"showtime", "showtime.movie", "showtime.room"})
    Page<Booking> findByCustomer_CustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"showtime", "showtime.movie", "showtime.room"})
    Page<Booking> findByCustomer_CustomerIdAndStatus(Long customerId, BookingStatus status, Pageable pageable);

    // (custom query: xem TODO 10)
}
```

📄 **`src/main/java/com/fu/cinema/repository/ReviewRepository.java`** — *phần derived query (custom query bổ sung ở TODO 10)*

```java
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    // ===== Derived query =====
    @EntityGraph(attributePaths = "customer")
    List<Review> findByMovie_MovieIdOrderByReviewDateDesc(Long movieId);

    // (custom query: xem TODO 10)
}
```

### Giải thích & lưu ý

- `exists...AndXxxIdNot(value, id)`: kiểm tra trùng khi **sửa** — loại trừ chính bản ghi đang sửa.
- `@EntityGraph(attributePaths = {...})` trên derived query: lấy luôn quan hệ LAZY trong cùng câu SQL → view dùng được `b.showtime.movie.title` khi `open-in-view=false`.
- Derived query trả về `Page<T>` khi nhận tham số `Pageable` (Spring tự sinh câu `COUNT`).

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| Khởi động app | Không có `PropertyReferenceException` / `QueryCreationException` |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/3-entities-repositories` (issue #3); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(repository): add jpa repositories for all entities` + `Refs #3`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #3.

## TODO 10 — Custom query (Q1–Q12)

**Issue:** #3 · **Người làm:** A (Leader) — mỗi module review query của mình · **Nhánh:** `feature/3-entities-repositories`

**Mục tiêu:** Viết các query JPQL, native, projection, `@Modifying`, phân trang theo mục 9.2 của đề.

### Các bước

1. Tạo các DTO/projection dùng cho query: `MovieRevenueDTO` (constructor expression), `RatingSummary`, `RoomShowtimeCount` (interface projection).
2. Bổ sung custom query vào repository (code đầy đủ bên dưới).

| # | Method | Loại | Kỹ thuật chính |
|---|---|---|---|
| Q1 | `CustomerRepository.search(kw, pageable)` | JPQL + Page | `:kw IS NULL OR ... LIKE`, `JOIN FETCH` + `countQuery` |
| Q2 | `MovieRepository.search(kw, genreId, statuses, pageable)` | JPQL + Page | tham số tuỳ chọn, `IN :statuses` |
| Q3 | `ShowtimeRepository.findOverlapping(roomId, start, end, excludeId)` | JPQL | điều kiện giao nhau `start < :end AND end > :start` |
| Q4 | `TicketRepository.findSoldSeatCodes(showtimeId)` | JPQL | chọn 1 cột, loại đơn `CANCELLED` |
| Q5 | `BookingRepository.findDetailById(id)` | JPQL | `JOIN FETCH` nhiều cấp + collection |
| Q6 | `BookingRepository.search(status, from, to, kw, pageable)` | JPQL + Page | nhiều tham số tuỳ chọn |
| Q7 | `BookingRepository.revenueByMovie(from, to)` | JPQL → DTO | `SUM`, `COUNT`, `GROUP BY`, `ORDER BY SUM DESC` |
| Q8 | `BookingRepository.findConfirmedInPeriod(from, to)` | JPQL | `ORDER BY bookingDate DESC` |
| Q9 | `BookingRepository.cancelAllByShowtime(id)` | `@Modifying` UPDATE | `clearAutomatically`, `flushAutomatically` |
| Q10 | `ReviewRepository.summarize(movieId)` | JPQL → interface projection | `AVG`, `COUNT` |
| Q11 | `ShowtimeRepository.countTodayShowtimesByRoom()` | **Native** SQL Server | `CAST(GETDATE() AS DATE)`, alias khớp getter |
| Q12 | `BookingRepository.countWatched(customerId, movieId, now)` | JPQL | điều kiện đi qua 2 cấp quan hệ |
| + | `ShowtimeRepository.findByIdForUpdate(id)` | JPQL + `@Lock(PESSIMISTIC_WRITE)` | khoá dòng khi đặt vé (TODO 28) |

### Code

📄 **`src/main/java/com/fu/cinema/dto/MovieRevenueDTO.java`**

```java
package com.fu.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

// Dùng trong constructor expression của JPQL: kiểu tham số khớp kết quả (COUNT → Long, SUM(BigDecimal) → BigDecimal)
@Getter
@AllArgsConstructor
public class MovieRevenueDTO {

    private final String title;
    private final Long ticketCount;
    private final BigDecimal revenue;
}
```

📄 **`src/main/java/com/fu/cinema/dto/RatingSummary.java`**

```java
package com.fu.cinema.dto;

public interface RatingSummary {
    Double getAverage();

    Long getTotal();
}
```

📄 **`src/main/java/com/fu/cinema/dto/RoomShowtimeCount.java`**

```java
package com.fu.cinema.dto;

// Interface projection cho native query: tên getter khớp alias trong SQL
public interface RoomShowtimeCount {
    String getRoomName();

    Long getShowtimeCount();
}
```

📄 **`src/main/java/com/fu/cinema/repository/CustomerRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ===== Derived query =====
    Optional<Customer> findByAccount_AccountId(Long accountId);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByIdentityCard(String identityCard);

    boolean existsByEmailAndCustomerIdNot(String email, Long customerId);

    boolean existsByMobileAndCustomerIdNot(String mobile, Long customerId);

    boolean existsByIdentityCardAndCustomerIdNot(String identityCard, Long customerId);

    // ===== Q1: tìm theo tên / email / SĐT + phân trang (JOIN FETCH account để hiện trạng thái) =====
    @Query(value = """
            SELECT c FROM Customer c JOIN FETCH c.account a
            WHERE :kw IS NULL
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(c.email)    LIKE LOWER(CONCAT('%', :kw, '%'))
               OR c.mobile          LIKE CONCAT('%', :kw, '%')
            """,
            countQuery = """
            SELECT COUNT(c) FROM Customer c
            WHERE :kw IS NULL
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(c.email)    LIKE LOWER(CONCAT('%', :kw, '%'))
               OR c.mobile          LIKE CONCAT('%', :kw, '%')
            """)
    Page<Customer> search(@Param("kw") String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c JOIN FETCH c.account WHERE c.customerId = :id")
    Optional<Customer> findWithAccountById(@Param("id") Long id);
}
```

📄 **`src/main/java/com/fu/cinema/repository/MovieRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.entity.Movie;
import com.fu.cinema.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // ===== Derived query =====
    boolean existsByGenre_GenreId(Long genreId);

    long countByStatus(MovieStatus status);

    List<Movie> findByStatusNotOrderByTitleAsc(MovieStatus status);      // dropdown tạo suất chiếu

    // ===== Q2: tìm theo tên + thể loại + danh sách trạng thái (tham số tuỳ chọn), phân trang =====
    @Query(value = """
            SELECT m FROM Movie m JOIN FETCH m.genre g
            WHERE (:kw IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :kw, '%')))
              AND (:genreId IS NULL OR g.genreId = :genreId)
              AND m.status IN :statuses
            """,
            countQuery = """
            SELECT COUNT(m) FROM Movie m
            WHERE (:kw IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :kw, '%')))
              AND (:genreId IS NULL OR m.genre.genreId = :genreId)
              AND m.status IN :statuses
            """)
    Page<Movie> search(@Param("kw") String keyword,
                       @Param("genreId") Long genreId,
                       @Param("statuses") Collection<MovieStatus> statuses,
                       Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN FETCH m.genre WHERE m.movieId = :id")
    Optional<Movie> findWithGenreById(@Param("id") Long id);
}
```

📄 **`src/main/java/com/fu/cinema/repository/ShowtimeRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.dto.RoomShowtimeCount;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.enums.ShowtimeStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // ===== Derived query =====
    boolean existsByMovie_MovieId(Long movieId);

    boolean existsByRoom_RoomId(Long roomId);

    @EntityGraph(attributePaths = "room")
    List<Showtime> findByMovie_MovieIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
            Long movieId, ShowtimeStatus status, LocalDateTime after);

    // ===== Danh sách suất chiếu cho Admin (lọc tuỳ chọn) =====
    @Query(value = """
            SELECT s FROM Showtime s JOIN FETCH s.movie m JOIN FETCH s.room r
            WHERE (:movieId IS NULL OR m.movieId = :movieId)
              AND (:roomId IS NULL OR r.roomId = :roomId)
              AND (:from IS NULL OR s.startTime >= :from)
              AND (:to IS NULL OR s.startTime < :to)
            """,
            countQuery = """
            SELECT COUNT(s) FROM Showtime s
            WHERE (:movieId IS NULL OR s.movie.movieId = :movieId)
              AND (:roomId IS NULL OR s.room.roomId = :roomId)
              AND (:from IS NULL OR s.startTime >= :from)
              AND (:to IS NULL OR s.startTime < :to)
            """)
    Page<Showtime> search(@Param("movieId") Long movieId,
                          @Param("roomId") Long roomId,
                          @Param("from") LocalDateTime from,
                          @Param("to") LocalDateTime to,
                          Pageable pageable);

    @Query("SELECT s FROM Showtime s JOIN FETCH s.movie JOIN FETCH s.room WHERE s.showtimeId = :id")
    Optional<Showtime> findDetailById(@Param("id") Long id);

    // ===== Q3: suất chiếu chồng giờ trong cùng phòng (BR-08) =====
    @Query("""
            SELECT s FROM Showtime s JOIN FETCH s.movie
            WHERE s.room.roomId = :roomId
              AND s.status <> com.fu.cinema.enums.ShowtimeStatus.CANCELLED
              AND s.startTime < :end
              AND s.endTime > :start
              AND (:excludeId IS NULL OR s.showtimeId <> :excludeId)
            """)
    List<Showtime> findOverlapping(@Param("roomId") Long roomId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("excludeId") Long excludeId);

    // Khoá dòng suất chiếu khi đặt vé → 2 người đặt cùng lúc sẽ được xử lý tuần tự
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Showtime s WHERE s.showtimeId = :id")
    Optional<Showtime> findByIdForUpdate(@Param("id") Long id);

    // ===== Q11: native SQL Server — số suất chiếu hôm nay theo phòng (dashboard) =====
    @Query(value = """
            SELECT r.room_name AS roomName, COUNT(s.showtime_id) AS showtimeCount
            FROM rooms r
            LEFT JOIN showtimes s
                   ON s.room_id = r.room_id
                  AND s.status = 'OPEN'
                  AND CAST(s.start_time AS DATE) = CAST(GETDATE() AS DATE)
            GROUP BY r.room_name
            ORDER BY r.room_name
            """, nativeQuery = true)
    List<RoomShowtimeCount> countTodayShowtimesByRoom();
}
```

📄 **`src/main/java/com/fu/cinema/repository/BookingRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.dto.MovieRevenueDTO;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ===== Derived query =====
    boolean existsByCustomer_CustomerId(Long customerId);

    boolean existsByShowtime_ShowtimeId(Long showtimeId);

    long countByStatus(BookingStatus status);

    @EntityGraph(attributePaths = {"showtime", "showtime.movie", "showtime.room"})
    Page<Booking> findByCustomer_CustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"showtime", "showtime.movie", "showtime.room"})
    Page<Booking> findByCustomer_CustomerIdAndStatus(Long customerId, BookingStatus status, Pageable pageable);

    // ===== Q5: chi tiết đơn kèm khách, suất chiếu, phim, phòng, vé =====
    @Query("""
            SELECT DISTINCT b FROM Booking b
            JOIN FETCH b.customer c
            JOIN FETCH b.showtime s
            JOIN FETCH s.movie
            JOIN FETCH s.room
            LEFT JOIN FETCH b.tickets
            WHERE b.bookingId = :id
            """)
    Optional<Booking> findDetailById(@Param("id") Long id);

    // ===== Q6: lọc đơn cho Admin (trạng thái, khoảng ngày, từ khoá khách hàng) =====
    @Query(value = """
            SELECT b FROM Booking b
            JOIN FETCH b.customer c
            JOIN FETCH b.showtime s
            JOIN FETCH s.movie m
            WHERE (:status IS NULL OR b.status = :status)
              AND (:from IS NULL OR b.bookingDate >= :from)
              AND (:to IS NULL OR b.bookingDate < :to)
              AND (:kw IS NULL
                   OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :kw, '%'))
                   OR LOWER(c.email)    LIKE LOWER(CONCAT('%', :kw, '%'))
                   OR c.mobile          LIKE CONCAT('%', :kw, '%'))
            """,
            countQuery = """
            SELECT COUNT(b) FROM Booking b JOIN b.customer c
            WHERE (:status IS NULL OR b.status = :status)
              AND (:from IS NULL OR b.bookingDate >= :from)
              AND (:to IS NULL OR b.bookingDate < :to)
              AND (:kw IS NULL
                   OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :kw, '%'))
                   OR LOWER(c.email)    LIKE LOWER(CONCAT('%', :kw, '%'))
                   OR c.mobile          LIKE CONCAT('%', :kw, '%'))
            """)
    Page<Booking> search(@Param("status") BookingStatus status,
                         @Param("from") LocalDateTime from,
                         @Param("to") LocalDateTime to,
                         @Param("kw") String keyword,
                         Pageable pageable);

    // ===== Q7: doanh thu theo phim trong kỳ, giảm dần (constructor expression → DTO) =====
    @Query("""
            SELECT new com.fu.cinema.dto.MovieRevenueDTO(m.title, COUNT(t), SUM(t.price))
            FROM Ticket t
            JOIN t.booking b
            JOIN t.showtime s
            JOIN s.movie m
            WHERE b.status = com.fu.cinema.enums.BookingStatus.CONFIRMED
              AND b.bookingDate >= :from AND b.bookingDate < :to
            GROUP BY m.movieId, m.title
            ORDER BY SUM(t.price) DESC
            """)
    List<MovieRevenueDTO> revenueByMovie(@Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to);

    // ===== Q8: đơn CONFIRMED trong kỳ, mới nhất trước =====
    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.customer
            JOIN FETCH b.showtime s
            JOIN FETCH s.movie
            WHERE b.status = com.fu.cinema.enums.BookingStatus.CONFIRMED
              AND b.bookingDate >= :from AND b.bookingDate < :to
            ORDER BY b.bookingDate DESC
            """)
    List<Booking> findConfirmedInPeriod(@Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);

    // ===== Q9: huỷ hàng loạt đơn của một suất chiếu (BR-09) =====
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Booking b SET b.status = com.fu.cinema.enums.BookingStatus.CANCELLED
            WHERE b.showtime.showtimeId = :showtimeId
              AND b.status <> com.fu.cinema.enums.BookingStatus.CANCELLED
            """)
    int cancelAllByShowtime(@Param("showtimeId") Long showtimeId);

    // ===== Q12: khách đã xem phim chưa (BR-16) =====
    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.customer.customerId = :customerId
              AND b.showtime.movie.movieId = :movieId
              AND b.status = com.fu.cinema.enums.BookingStatus.CONFIRMED
              AND b.showtime.endTime < :now
            """)
    long countWatched(@Param("customerId") Long customerId,
                       @Param("movieId") Long movieId,
                       @Param("now") LocalDateTime now);
}
```

📄 **`src/main/java/com/fu/cinema/repository/TicketRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // ===== Q4: ghế đã bán của một suất chiếu (không tính đơn đã huỷ) =====
    @Query("""
            SELECT t.seatCode FROM Ticket t
            WHERE t.showtime.showtimeId = :showtimeId
              AND t.booking.status <> com.fu.cinema.enums.BookingStatus.CANCELLED
            """)
    List<String> findSoldSeatCodes(@Param("showtimeId") Long showtimeId);
}
```

📄 **`src/main/java/com/fu/cinema/repository/ReviewRepository.java`**

```java
package com.fu.cinema.repository;

import com.fu.cinema.dto.RatingSummary;
import com.fu.cinema.entity.Review;
import com.fu.cinema.entity.ReviewId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    @EntityGraph(attributePaths = "customer")
    List<Review> findByMovie_MovieIdOrderByReviewDateDesc(Long movieId);

    // ===== Q10: điểm trung bình & số lượt đánh giá (interface projection) =====
    @Query("""
            SELECT AVG(r.rating) AS average, COUNT(r) AS total
            FROM Review r WHERE r.movie.movieId = :movieId
            """)
    RatingSummary summarize(@Param("movieId") Long movieId);
}
```

### Giải thích & lưu ý

- **JPQL** viết theo tên entity/field (`Booking b`, `b.bookingDate`); **native** viết theo tên bảng/cột (`rooms`, `start_time`).
- Query có `JOIN FETCH` + `Pageable` **phải** khai báo `countQuery` (Spring không tự sinh COUNT đúng khi có FETCH).
- `:kw IS NULL OR ...`: service đổi chuỗi rỗng thành `null` (`ValidationUtils.blankToNull`) để bỏ qua điều kiện.
- Q7 trả DTO qua **constructor expression** `SELECT new com.fu.cinema.dto.MovieRevenueDTO(...)` — ghi đầy đủ package, kiểu tham số khớp (`COUNT` → `Long`, `SUM(BigDecimal)` → `BigDecimal`).
- Q9 là bulk update, bỏ qua persistence context → `clearAutomatically = true` để đọc lại không bị dữ liệu cũ; phải chạy trong `@Transactional`.
- Enum trong JPQL viết đầy đủ: `com.fu.cinema.enums.BookingStatus.CONFIRMED`.
- Q11 dùng hàm riêng SQL Server (`GETDATE()`) → chỉ chạy trên SQL Server; không gọi Q11 trong test H2.

### Kiểm thử

| Query | Cách kiểm tra (dữ liệu seed) | Kết quả mong đợi |
|---|---|---|
| Q3 | Tạo suất *Chú Mèo* ở Room 01 lúc N+1 10:00 (TODO 23) | Báo trùng với *Biệt Đội Siêu Tốc* 09:00–11:15 |
| Q4 | Mở sơ đồ ghế suất N+1 09:00 (TODO 28) | E5, E6 xám; **F1 trống** (đơn đã huỷ) |
| Q7 | Báo cáo N−7 → N (TODO 26) | Chú Mèo 3 vé 210.000đ > Huyền Thoại Cũ 2 vé 140.000đ > Biệt Đội 1 vé 90.000đ |
| Q10 | Chi tiết phim chưa có đánh giá | "Chưa có đánh giá" |
| Q11 | Dashboard Admin (TODO 26) | Bảng số suất chiếu hôm nay theo phòng |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/3-entities-repositories` (issue #3); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(repository): add search queries with pagination` + `Refs #3`
  - [ ] `feat(repository): add showtime overlap and sold seats queries` + `Refs #3`
  - [ ] `feat(repository): add revenue report query with dto projection` + `Refs #3`
  - [ ] `feat(repository): add bulk cancel query and native dashboard query` + `Refs #3`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(entity): add domain entities and repositories`** → base `develop`, mô tả theo PR template, ghi `Closes #3`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 4, 5, 6, 7, 9, 10; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] `@ManyToOne`/`@OneToOne` dùng `FetchType.LAZY`
  - [ ] Enum `STRING`
  - [ ] Không `@Data` trên entity
  - [ ] `optional = false` + `nullable = false` cho FK bắt buộc
  - [ ] Kiểu `LocalDateTime`, `BigDecimal`
  - [ ] `ReviewId` có `Serializable` + `equals/hashCode`
  - [ ] Chỉ `Booking.tickets` có cascade
  - [ ] Kiểu ID đúng (`ReviewId` cho Review)
  - [ ] Tên derived query khớp tên field
  - [ ] Có `countQuery` cho query `JOIN FETCH` + `Pageable`
  - [ ] `@Modifying` có `clearAutomatically`
  - [ ] Không nối chuỗi tham số
  - [ ] Native query dùng tên bảng/cột
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #3 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part C — Service & xử lý lỗi

## TODO 11 — Service interface, exception, tiện ích

**Issue:** #5 · **Người làm:** A (Leader) · **Nhánh:** `feature/5-service-contracts-error-handling`

**Mục tiêu:** Chốt **hợp đồng** (interface) của toàn bộ service để các thành viên làm song song; tạo exception và tiện ích dùng chung.

### Các bước

1. Tạo `BusinessException` (vi phạm quy tắc nghiệp vụ → hiện thông báo cho người dùng) và `ResourceNotFoundException` (→ trang 404).
2. Tạo tiện ích `ValidationUtils` (`blankToNull`, `age`), `DateUtils`, `PageUtils` (URL phân trang).
3. Tạo **toàn bộ interface service** dưới đây và merge sớm. Mỗi thành viên cài đặt `XxxServiceImpl` trong nhánh feature của mình (TODO 13 → 31).
4. Quy ước cho mọi `ServiceImpl`:
   - `@Service`, `@RequiredArgsConstructor` (constructor injection), `@Transactional(readOnly = true)` ở **class**.
   - Method ghi dữ liệu gắn thêm `@Transactional`.
   - Kiểm tra quy tắc nghiệp vụ (BR-xx) trong service, ném `BusinessException` với thông báo tiếng Việt rõ ràng.

### Code

📄 **`src/main/java/com/fu/cinema/exception/BusinessException.java`**

```java
package com.fu.cinema.exception;

// Vi phạm quy tắc nghiệp vụ (BR-xx) — thông báo hiển thị trực tiếp cho người dùng
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
```

📄 **`src/main/java/com/fu/cinema/exception/ResourceNotFoundException.java`**

```java
package com.fu.cinema.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " không tồn tại (id = " + id + ")");
    }
}
```

📄 **`src/main/java/com/fu/cinema/util/ValidationUtils.java`**

```java
package com.fu.cinema.util;

import java.time.LocalDate;
import java.time.Period;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    public static int age(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}
```

📄 **`src/main/java/com/fu/cinema/util/DateUtils.java`**

```java
package com.fu.cinema.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtils() {
    }

    public static String format(LocalDateTime t) {
        return t == null ? "" : t.format(FMT);
    }
}
```

📄 **`src/main/java/com/fu/cinema/util/PageUtils.java`**

```java
package com.fu.cinema.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class PageUtils {

    public static final int DEFAULT_SIZE = 10;

    private PageUtils() {
    }

    /** URL hiện tại (giữ nguyên các tham số lọc) nhưng bỏ tham số page → dùng cho thanh phân trang. */
    public static String currentUrlWithoutPage() {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page")
                .build()
                .toUriString();
    }
}
```

📄 **`src/main/java/com/fu/cinema/service/AuthService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.RegisterForm;
import com.fu.cinema.dto.SessionUser;

public interface AuthService {

    SessionUser login(String username, String password);

    SessionUser register(RegisterForm form);
}
```

📄 **`src/main/java/com/fu/cinema/service/CustomerService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.ChangePasswordForm;
import com.fu.cinema.dto.CustomerForm;
import com.fu.cinema.dto.ProfileForm;
import com.fu.cinema.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {

    // ===== Admin =====
    Page<Customer> search(String keyword, int page, int size);

    CustomerForm getForm(Long customerId);

    void create(CustomerForm form);

    void update(Long customerId, CustomerForm form);

    /** @return true nếu đã xoá hẳn, false nếu chỉ khoá tài khoản (BR-04) */
    boolean delete(Long customerId);

    void toggleLock(Long customerId);

    long count();

    // ===== Customer =====
    ProfileForm getProfile(Long customerId);

    void updateProfile(Long customerId, ProfileForm form);

    void changePassword(Long accountId, ChangePasswordForm form);
}
```

📄 **`src/main/java/com/fu/cinema/service/GenreService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.GenreForm;
import com.fu.cinema.entity.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> findAll();

    GenreForm getForm(Long genreId);

    void create(GenreForm form);

    void update(Long genreId, GenreForm form);

    void delete(Long genreId);
}
```

📄 **`src/main/java/com/fu/cinema/service/MovieService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.MovieForm;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.enums.MovieStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {

    Page<Movie> searchForAdmin(String keyword, Long genreId, MovieStatus status, int page, int size, String sort);

    Page<Movie> searchForCustomer(String keyword, Long genreId, int page, int size);

    Movie getById(Long movieId);

    MovieForm getForm(Long movieId);

    void create(MovieForm form);

    void update(Long movieId, MovieForm form);

    /** @return true nếu đã xoá hẳn, false nếu chỉ chuyển STOPPED (BR-06) */
    boolean delete(Long movieId);

    List<Movie> findSchedulable();

    long countShowing();
}
```

📄 **`src/main/java/com/fu/cinema/service/RoomService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.RoomForm;
import com.fu.cinema.entity.Room;

import java.util.List;

public interface RoomService {

    List<Room> findAll();

    List<Room> findActive();

    RoomForm getForm(Long roomId);

    void create(RoomForm form);

    void update(Long roomId, RoomForm form);

    /** @return true nếu đã xoá hẳn, false nếu chỉ chuyển MAINTENANCE */
    boolean delete(Long roomId);
}
```

📄 **`src/main/java/com/fu/cinema/service/ShowtimeService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.RoomShowtimeCount;
import com.fu.cinema.dto.SeatMap;
import com.fu.cinema.dto.ShowtimeForm;
import com.fu.cinema.entity.Showtime;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {

    Page<Showtime> search(Long movieId, Long roomId, LocalDate date, int page, int size);

    ShowtimeForm getForm(Long showtimeId);

    void create(ShowtimeForm form);

    void update(Long showtimeId, ShowtimeForm form);

    /** @return true nếu đã xoá hẳn, false nếu chỉ huỷ suất chiếu (BR-09) */
    boolean delete(Long showtimeId);

    List<Showtime> findUpcomingByMovie(Long movieId);

    SeatMap getSeatMap(Long showtimeId);

    List<RoomShowtimeCount> countTodayByRoom();
}
```

📄 **`src/main/java/com/fu/cinema/service/BookingService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.BookingRequest;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.enums.BookingStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface BookingService {

    // ===== Customer =====
    Booking create(Long customerId, BookingRequest request);

    Page<Booking> findByCustomer(Long customerId, BookingStatus status, int page, int size);

    Booking getDetailForCustomer(Long bookingId, Long customerId);

    void cancelByCustomer(Long bookingId, Long customerId);

    // ===== Admin =====
    Page<Booking> search(BookingStatus status, LocalDate fromDate, LocalDate toDate, String keyword, int page, int size);

    Booking getDetail(Long bookingId);

    void confirm(Long bookingId);

    void cancelByAdmin(Long bookingId);

    long countPending();
}
```

📄 **`src/main/java/com/fu/cinema/service/ReportService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.ReportResult;

import java.time.LocalDate;

public interface ReportService {

    ReportResult generate(LocalDate startDate, LocalDate endDate);
}
```

📄 **`src/main/java/com/fu/cinema/service/ReviewService.java`**

```java
package com.fu.cinema.service;

import com.fu.cinema.dto.RatingSummary;
import com.fu.cinema.dto.ReviewForm;
import com.fu.cinema.entity.Review;

import java.util.List;

public interface ReviewService {

    List<Review> findByMovie(Long movieId);

    RatingSummary summarize(Long movieId);

    boolean canReview(Long customerId, Long movieId);

    ReviewForm getForm(Long customerId, Long movieId);

    void save(Long customerId, Long movieId, ReviewForm form);
}
```

### Giải thích & lưu ý

- Interface được merge **trước** giúp mọi người code controller/view dựa trên chữ ký method đã thống nhất. Muốn đổi chữ ký → mở issue, báo cả nhóm.
- Method `delete` trả `boolean` để controller biết "đã xoá hẳn" hay "chỉ đổi trạng thái" (BR-04, BR-06, BR-09) và hiện thông báo phù hợp.
- Controller **không** được inject repository (TECH-02).

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| `mvn compile` | Thành công (chưa có impl vẫn compile được) |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#5** đã được assign cho **A (Leader)**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/5-service-contracts-error-handling` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(service): add service interfaces for all modules` + `Refs #5`
  - [ ] `feat(error): add business and not-found exceptions` + `Refs #5`
  - [ ] `feat(util): add validation, date and paging helpers` + `Refs #5`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #5.

## TODO 12 — Xử lý lỗi tập trung & trang lỗi

**Issue:** #5 · **Người làm:** A (Leader) · **Nhánh:** `feature/5-service-contracts-error-handling`

**Mục tiêu:** Lỗi được xử lý thống nhất, không lộ stack trace; có trang 403/404/500.

### Các bước

1. Tạo `GlobalExceptionHandler` (`@ControllerAdvice`):
   - `ResourceNotFoundException`, `NoResourceFoundException`, `MethodArgumentTypeMismatchException` → `error/404` (HTTP 404).
   - `BusinessException` không được controller bắt → đặt flash `error`, redirect về trang trước (`Referer`).
   - `Exception` còn lại → log chi tiết, trả `error/500`.
2. Tạo `templates/error/403.html`, `404.html`, `500.html` và `error.html` (Spring Boot tự dùng khi `response.sendError(...)`).
3. Flash message hiển thị qua fragment `flash` (TODO 3) với 2 biến `success` / `error`.

### Code

📄 **`src/main/java/com/fu/cinema/exception/GlobalExceptionHandler.java`**

```java
package com.fu.cinema.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.support.RequestContextUtils;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex) {
        log.debug("Not found: {}", ex.getMessage());
        return "error/404";
    }

    /** Lỗi nghiệp vụ không được controller bắt → quay lại trang trước kèm thông báo. */
    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOther(Exception ex) {
        log.error("Unexpected error", ex);                                   // log chi tiết, KHÔNG hiển thị stack trace
        return "error/500";
    }
}
```

📄 **`src/main/resources/templates/error/404.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Lỗi 404')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-5 text-center">
    <h1 class="display-3">404</h1>
    <p class="lead">Trang hoặc dữ liệu bạn tìm không tồn tại.</p>
    <a class="btn btn-primary" th:href="@{/}">Về trang chủ</a>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- `403.html`, `500.html`, `error.html` giống `404.html`, chỉ khác mã lỗi và câu thông báo.
- Quy ước trong controller:
  - Lỗi **form** (validate, trùng dữ liệu) → `catch (BusinessException e)` → `model.addAttribute("error", ...)` và trả lại view form (giữ dữ liệu đã nhập).
  - Lỗi **thao tác** (xoá, xác nhận...) → để `GlobalExceptionHandler` xử lý (flash + redirect).
- `RequestContextUtils.getOutputFlashMap(request)` dùng thay `RedirectAttributes` trong `@ExceptionHandler`.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| `/movies/9999` | Trang 404 |
| `/movies/abc` | Trang 404 (không phải 400/500) |
| Xoá thể loại *Hành động* (TODO 18) | Quay lại danh sách, thông báo đỏ "Không thể xoá..." |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/5-service-contracts-error-handling` (issue #5); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(error): add global exception handler and error pages` + `Refs #5`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(service): service contracts, exceptions and shared utilities`** → base `develop`, mô tả theo PR template, ghi `Closes #5`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 11, 12; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Chữ ký method đủ cho các TODO của module mình (mỗi thành viên review phần mình)
  - [ ] Đặt tên method rõ nghĩa
  - [ ] Không trả stack trace ra view
  - [ ] Log lỗi không mong đợi bằng `log.error`
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #5 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part D — Xác thực & phân quyền

## TODO 13 — Đăng nhập / đăng xuất

**Issue:** #6 · **Người làm:** A (Leader) · **Nhánh:** `feature/6-auth`

**Mục tiêu:** Đăng nhập bằng email + mật khẩu (BCrypt), lưu người dùng vào `HttpSession`, điều hướng theo vai trò.

### Các bước

1. Tạo `SessionUser` (chỉ chứa `accountId`, `customerId`, `displayName`, `role` — **không** có mật khẩu) và `SessionUtils`.
2. Tạo `LoginForm` (validate `@NotBlank`).
3. Cài đặt `AuthServiceImpl.login()`: tìm theo username → `passwordEncoder.matches()` → chặn tài khoản `LOCKED` (BR-03) → tạo `SessionUser`.
4. `AuthController`: `GET/POST /login`, `POST /logout`. Sau khi đăng nhập, **huỷ session cũ, tạo session mới** (chống session fixation).
5. `GlobalModelAttributes` (`@ControllerAdvice` + `@ModelAttribute("currentUser")`) đưa người dùng hiện tại vào **mọi** view → menu theo vai trò (TODO 3).
6. Tạo `auth/login.html`.

### Code

📄 **`src/main/java/com/fu/cinema/dto/SessionUser.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.enums.Role;
import lombok.Value;

import java.io.Serializable;

// Thông tin người dùng lưu trong HttpSession (KHÔNG lưu mật khẩu)
@Value
public class SessionUser implements Serializable {

    Long accountId;
    Long customerId;          // null nếu là ADMIN
    String displayName;
    Role role;

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }
}
```

📄 **`src/main/java/com/fu/cinema/util/SessionUtils.java`**

```java
package com.fu.cinema.util;

import com.fu.cinema.dto.SessionUser;
import jakarta.servlet.http.HttpSession;

public final class SessionUtils {

    public static final String CURRENT_USER = "currentUser";

    private SessionUtils() {
    }

    public static SessionUser getCurrentUser(HttpSession session) {
        return session == null ? null : (SessionUser) session.getAttribute(CURRENT_USER);
    }

    /** Luôn lấy customerId từ session, KHÔNG lấy từ form/URL. */
    public static Long getCurrentCustomerId(HttpSession session) {
        SessionUser user = getCurrentUser(session);
        return user == null ? null : user.getCustomerId();
    }
}
```

📄 **`src/main/java/com/fu/cinema/dto/LoginForm.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {

    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
}
```

📄 **`src/main/java/com/fu/cinema/service/impl/AuthServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.RegisterForm;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.entity.Account;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.enums.AccountStatus;
import com.fu.cinema.enums.Role;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.repository.AccountRepository;
import com.fu.cinema.repository.CustomerRepository;
import com.fu.cinema.service.AuthService;
import com.fu.cinema.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final int MIN_REGISTER_AGE = 13;

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SessionUser login(String username, String password) {
        Account account = accountRepository.findByUsername(username.trim().toLowerCase())
                .filter(a -> passwordEncoder.matches(password, a.getPassword()))
                .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (account.getStatus() == AccountStatus.LOCKED) {                       // BR-03
            throw new BusinessException("Tài khoản đã bị khoá. Vui lòng liên hệ quản trị viên");
        }

        if (account.getRole() == Role.ADMIN) {
            return new SessionUser(account.getAccountId(), null, "Administrator", Role.ADMIN);
        }
        Customer customer = customerRepository.findByAccount_AccountId(account.getAccountId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ khách hàng"));
        return new SessionUser(account.getAccountId(), customer.getCustomerId(), customer.getFullName(), Role.CUSTOMER);
    }

    @Override
    @Transactional                                                                // Account + Customer cùng 1 transaction
    public SessionUser register(RegisterForm form) {
        String email = form.getEmail().trim().toLowerCase();

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu nhập lại không khớp");
        }
        if (ValidationUtils.age(form.getBirthday()) < MIN_REGISTER_AGE) {            // BR-02
            throw new BusinessException("Bạn phải từ đủ " + MIN_REGISTER_AGE + " tuổi để đăng ký");
        }
        if (accountRepository.existsByUsername(email) || customerRepository.existsByEmail(email)) {   // BR-01
            throw new BusinessException("Email đã được sử dụng");
        }
        if (customerRepository.existsByMobile(form.getMobile())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }
        if (customerRepository.existsByIdentityCard(form.getIdentityCard())) {
            throw new BusinessException("Số CCCD đã được sử dụng");
        }

        Account account = accountRepository.save(new Account(
                email, passwordEncoder.encode(form.getPassword()), Role.CUSTOMER, AccountStatus.ACTIVE));

        Customer customer = new Customer();
        customer.setFullName(form.getFullName().trim());
        customer.setEmail(email);
        customer.setMobile(form.getMobile());
        customer.setBirthday(form.getBirthday());
        customer.setIdentityCard(form.getIdentityCard());
        customer.setAccount(account);
        customerRepository.save(customer);

        return new SessionUser(account.getAccountId(), customer.getCustomerId(), customer.getFullName(), Role.CUSTOMER);
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/AuthController.java`**

```java
package com.fu.cinema.controller;

import com.fu.cinema.dto.LoginForm;
import com.fu.cinema.dto.RegisterForm;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.AuthService;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("form", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("form") LoginForm form, BindingResult br,
                        HttpServletRequest request, Model model) {
        if (br.hasErrors()) {
            return "auth/login";
        }
        try {
            SessionUser user = authService.login(form.getUsername(), form.getPassword());
            startSession(request, user);
            return user.isAdmin() ? "redirect:/admin" : "redirect:/";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult br,
                           HttpServletRequest request, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "auth/register";                                   // giữ lại dữ liệu đã nhập
        }
        try {
            startSession(request, authService.register(form));
            ra.addFlashAttribute("success", "Đăng ký thành công. Chào mừng bạn đến FU Cinema!");
            return "redirect:/";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "Đã đăng xuất");
        return "redirect:/login";
    }

    private void startSession(HttpServletRequest request, SessionUser user) {
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();                                         // chống session fixation
        }
        request.getSession(true).setAttribute(SessionUtils.CURRENT_USER, user);
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/GlobalModelAttributes.java`**

```java
package com.fu.cinema.controller;

import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Đưa người dùng hiện tại vào Model của mọi view (menu theo vai trò)
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        return SessionUtils.getCurrentUser(request.getSession(false));   // false: không tạo session mới
    }
}
```

📄 **`src/main/resources/templates/auth/login.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Đăng nhập')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-5" style="max-width: 420px">
    <h3 class="mb-4">Đăng nhập</h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form th:action="@{/login}" th:object="${form}" method="post" novalidate>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input class="form-control" th:field="*{username}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{username}"></div>
        </div>
        <div class="mb-3">
            <label class="form-label">Mật khẩu</label>
            <input type="password" class="form-control" th:field="*{password}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{password}"></div>
        </div>
        <button class="btn btn-primary w-100">Đăng nhập</button>
    </form>
    <p class="mt-3 text-center">Chưa có tài khoản? <a th:href="@{/register}">Đăng ký</a></p>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Thông báo lỗi đăng nhập **chung chung** ("Tên đăng nhập hoặc mật khẩu không đúng") → không để lộ email nào đã tồn tại.
- `SessionUser` phải `Serializable` (session có thể được ghi ra đĩa / chia sẻ giữa các server).
- Đăng xuất dùng **POST** (form trong navbar), không dùng link GET.
- `GlobalModelAttributes` dùng `request.getSession(false)` → không tạo session thừa cho khách vãng lai.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| `admin@fucinema.vn` / `Admin@123` | Vào `/admin`, menu Admin |
| `an.nv@gmail.com` / `Customer@123` | Về trang chủ, menu "Vé của tôi", "Hồ sơ" |
| Sai mật khẩu | "Tên đăng nhập hoặc mật khẩu không đúng" |
| Tài khoản bị khoá (TODO 17) | "Tài khoản đã bị khoá..." |
| Đăng xuất | Về `/login`, menu khách |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#6** đã được assign cho **A (Leader)**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/6-auth` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(auth): add login and logout with session` + `Refs #6`
  - [ ] `feat(auth): block locked accounts on login` + `Refs #6`
  - [ ] `feat(auth): expose current user to all views` + `Refs #6`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #6.

## TODO 14 — Đăng ký tài khoản khách hàng

**Issue:** #6 · **Người làm:** A (Leader) · **Nhánh:** `feature/6-auth`

**Mục tiêu:** Khách tự đăng ký; tạo `Account` + `Customer` trong một transaction.

### Các bước

1. Tạo `RegisterForm` với Bean Validation: email, SĐT `^0\d{9}$`, CCCD `^\d{12}$`, ngày sinh `@Past`, mật khẩu mạnh `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$`.
2. `AuthServiceImpl.register()` (đã có ở TODO 13) kiểm tra: mật khẩu nhập lại khớp, **đủ 13 tuổi** (BR-02), email/SĐT/CCCD chưa dùng (BR-01) → lưu `Account` (CUSTOMER, ACTIVE, mật khẩu BCrypt) rồi `Customer`.
3. `AuthController`: `GET/POST /register` (đã có ở TODO 13) — lỗi thì trả lại form, **giữ dữ liệu đã nhập**; thành công thì đăng nhập luôn.
4. Tạo `auth/register.html` — mỗi trường có `th:errorclass="is-invalid"` + `th:errors`.

### Code

📄 **`src/main/java/com/fu/cinema/dto/RegisterForm.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RegisterForm {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại gồm 10 chữ số, bắt đầu bằng 0")
    private String mobile;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải ở quá khứ")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD gồm đúng 12 chữ số")
    private String identityCard;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
             message = "Mật khẩu ≥ 8 ký tự, có chữ hoa, chữ thường và chữ số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirmPassword;
}
```

📄 **`src/main/resources/templates/auth/register.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Đăng ký')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-5" style="max-width: 640px">
    <h3 class="mb-4">Đăng ký tài khoản</h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form th:action="@{/register}" th:object="${form}" method="post" novalidate class="row g-3">
        <div class="col-12">
            <label class="form-label">Họ tên</label>
            <input class="form-control" th:field="*{fullName}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{fullName}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Email (dùng để đăng nhập)</label>
            <input type="email" class="form-control" th:field="*{email}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{email}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input class="form-control" th:field="*{mobile}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{mobile}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input type="date" class="form-control" th:field="*{birthday}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{birthday}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">CCCD</label>
            <input class="form-control" th:field="*{identityCard}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{identityCard}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Mật khẩu</label>
            <input type="password" class="form-control" th:field="*{password}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{password}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Nhập lại mật khẩu</label>
            <input type="password" class="form-control" th:field="*{confirmPassword}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{confirmPassword}"></div>
        </div>
        <div class="col-12"><button class="btn btn-warning w-100">Đăng ký</button></div>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- `@Transactional` trên `register()`: nếu lưu `Customer` lỗi (vd. trùng unique), `Account` vừa tạo cũng bị rollback.
- Email được `trim().toLowerCase()` trước khi lưu và khi đăng nhập → không phân biệt hoa thường.
- Validate 2 lớp: Bean Validation (định dạng) ở form + kiểm tra nghiệp vụ (trùng, tuổi) ở service.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Đăng ký email `an.nv@gmail.com` | "Email đã được sử dụng", form giữ dữ liệu |
| SĐT `123` | Lỗi định dạng dưới ô SĐT |
| Ngày sinh cách đây 10 năm | "Bạn phải từ đủ 13 tuổi..." |
| Mật khẩu `abc` | Lỗi mật khẩu mạnh |
| Dữ liệu hợp lệ | Đăng nhập luôn, về trang chủ, có thông báo xanh |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/6-auth` (issue #6); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(auth): add customer registration with validation` + `Refs #6`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #6.

## TODO 15 — Phân quyền bằng `HandlerInterceptor`

**Issue:** #6 · **Người làm:** A (Leader) · **Nhánh:** `feature/6-auth`

**Mục tiêu:** Chặn truy cập `/admin/**` (chỉ ADMIN) và `/customer/**` (chỉ CUSTOMER).

### Các bước

1. Tạo `AuthInterceptor implements HandlerInterceptor`:
   - Chưa đăng nhập → redirect `/login`.
   - Sai vai trò → `response.sendError(403)` → Spring Boot hiển thị `templates/error/403.html`.
2. Đăng ký interceptor trong `WebConfig implements WebMvcConfigurer` cho `/admin/**`, `/customer/**`.

### Code

📄 **`src/main/java/com/fu/cinema/config/AuthInterceptor.java`**

```java
package com.fu.cinema.config;

import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.enums.Role;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SessionUser user = SessionUtils.getCurrentUser(request.getSession(false));
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (user == null) {                                                  // chưa đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        boolean forbidden = (path.startsWith("/admin") && user.getRole() != Role.ADMIN)
                || (path.startsWith("/customer") && user.getRole() != Role.CUSTOMER);
        if (forbidden) {                                                     // sai vai trò → 403
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
```

📄 **`src/main/java/com/fu/cinema/config/WebConfig.java`**

```java
package com.fu.cinema.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/admin/**", "/customer/**");
    }
}
```

### Giải thích & lưu ý

- Chỉ áp dụng cho `/admin/**`, `/customer/**` → trang chủ, chi tiết phim, `/login`, `/register`, `/css/**` không bị chặn.
- Quy ước URL: mọi chức năng Admin đặt dưới `/admin`, mọi chức năng cần đăng nhập của khách đặt dưới `/customer` → không phải sửa interceptor khi thêm chức năng.
- *(Bonus)* Thay interceptor bằng Spring Security (`SecurityFilterChain`, `hasRole`).

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Chưa đăng nhập, vào `/admin/movies` | Chuyển về `/login` |
| Đăng nhập Customer, vào `/admin` | Trang 403 |
| Đăng nhập Admin, vào `/customer/bookings` | Trang 403 |
| Chưa đăng nhập, bấm giờ chiếu ở chi tiết phim | Chuyển về `/login` |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/6-auth` (issue #6); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(auth): add role-based interceptor for admin and customer paths` + `Refs #6`
  - [ ] `feat(error): add 403 page for forbidden access` + `Refs #6`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(auth): authentication, registration and role-based authorization`** → base `develop`, mô tả theo PR template, ghi `Closes #6`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 13, 14, 15; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] So khớp mật khẩu bằng `passwordEncoder.matches`
  - [ ] Không lưu mật khẩu vào session
  - [ ] Tạo session mới sau khi đăng nhập
  - [ ] Tạo Account + Customer trong 1 transaction
  - [ ] Kiểm tra trùng email cả bảng `accounts` (username) và `customers`
  - [ ] Không chặn nhầm trang công khai / static
  - [ ] So sánh vai trò bằng enum, không bằng chuỗi
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #6 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part E — Admin: Quản lý khách hàng

## TODO 16 — Danh sách & tìm kiếm khách hàng

**Issue:** #7 · **Người làm:** B · **Nhánh:** `feature/7-customer-admin`

**Mục tiêu:** Danh sách khách hàng có tìm kiếm theo tên/email/SĐT, phân trang, sắp xếp theo tên.

### Các bước

1. Cài đặt `CustomerServiceImpl` (file đầy đủ bên dưới — phần **Admin** dùng cho TODO 16–17, phần **Customer** dùng cho TODO 30).
2. `search()`: đổi từ khoá rỗng → `null`, gọi Q1 với `PageRequest.of(page, size, Sort.by("fullName"))`.
3. `AdminCustomerController.list()`: đưa `customers` (Page), `kw`, `pageUrl` vào model.
4. Tạo `admin/customers/list.html`: form tìm kiếm GET, bảng, nút Sửa / Khoá / Xoá (form POST), fragment phân trang.

### Code

📄 **`src/main/java/com/fu/cinema/service/impl/CustomerServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.ChangePasswordForm;
import com.fu.cinema.dto.CustomerForm;
import com.fu.cinema.dto.ProfileForm;
import com.fu.cinema.entity.Account;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.enums.AccountStatus;
import com.fu.cinema.enums.Role;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.AccountRepository;
import com.fu.cinema.repository.BookingRepository;
import com.fu.cinema.repository.CustomerRepository;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= Admin =================

    @Override
    public Page<Customer> search(String keyword, int page, int size) {
        return customerRepository.search(ValidationUtils.blankToNull(keyword),
                PageRequest.of(Math.max(page, 0), size, Sort.by("fullName")));
    }

    @Override
    public CustomerForm getForm(Long customerId) {
        Customer c = findWithAccount(customerId);
        CustomerForm form = new CustomerForm();
        form.setCustomerId(c.getCustomerId());
        form.setFullName(c.getFullName());
        form.setEmail(c.getEmail());
        form.setMobile(c.getMobile());
        form.setBirthday(c.getBirthday());
        form.setIdentityCard(c.getIdentityCard());
        form.setStatus(c.getAccount().getStatus());
        return form;
    }

    @Override
    @Transactional
    public void create(CustomerForm form) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new BusinessException("Mật khẩu không được để trống khi tạo khách hàng");
        }
        String email = form.getEmail().trim().toLowerCase();
        checkDuplicates(email, form.getMobile(), form.getIdentityCard(), null, null);
        checkAge(form);

        Account account = accountRepository.save(new Account(
                email, passwordEncoder.encode(form.getPassword()), Role.CUSTOMER, form.getStatus()));
        Customer customer = new Customer();
        customer.setAccount(account);
        copy(form, customer, email);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void update(Long customerId, CustomerForm form) {
        Customer customer = findWithAccount(customerId);
        Account account = customer.getAccount();
        String email = form.getEmail().trim().toLowerCase();
        checkDuplicates(email, form.getMobile(), form.getIdentityCard(), customerId, account.getAccountId());
        checkAge(form);

        copy(form, customer, email);
        account.setUsername(email);                          // username = email
        account.setStatus(form.getStatus());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            account.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        // dirty checking tự sinh UPDATE khi commit
    }

    @Override
    @Transactional
    public boolean delete(Long customerId) {
        Customer customer = findWithAccount(customerId);
        if (bookingRepository.existsByCustomer_CustomerId(customerId)) {          // BR-04
            customer.getAccount().setStatus(AccountStatus.LOCKED);
            return false;
        }
        Account account = customer.getAccount();
        customerRepository.delete(customer);                 // xoá bảng con (giữ FK) trước
        accountRepository.delete(account);
        return true;
    }

    @Override
    @Transactional
    public void toggleLock(Long customerId) {
        Account account = findWithAccount(customerId).getAccount();
        account.setStatus(account.getStatus() == AccountStatus.ACTIVE ? AccountStatus.LOCKED : AccountStatus.ACTIVE);
    }

    @Override
    public long count() {
        return customerRepository.count();
    }

    // ================= Customer =================

    @Override
    public ProfileForm getProfile(Long customerId) {
        Customer c = findWithAccount(customerId);
        ProfileForm form = new ProfileForm();
        form.setUsername(c.getAccount().getUsername());
        form.setFullName(c.getFullName());
        form.setEmail(c.getEmail());
        form.setMobile(c.getMobile());
        form.setBirthday(c.getBirthday());
        form.setIdentityCard(c.getIdentityCard());
        return form;
    }

    @Override
    @Transactional
    public void updateProfile(Long customerId, ProfileForm form) {
        Customer customer = findWithAccount(customerId);
        String email = form.getEmail().trim().toLowerCase();
        checkDuplicates(email, form.getMobile(), form.getIdentityCard(), customerId, customer.getAccount().getAccountId());
        if (ValidationUtils.age(form.getBirthday()) < 13) {
            throw new BusinessException("Khách hàng phải từ đủ 13 tuổi");
        }
        customer.setFullName(form.getFullName().trim());
        customer.setEmail(email);                            // username đăng nhập KHÔNG đổi
        customer.setMobile(form.getMobile());
        customer.setBirthday(form.getBirthday());
        customer.setIdentityCard(form.getIdentityCard());
    }

    @Override
    @Transactional
    public void changePassword(Long accountId, ChangePasswordForm form) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản", accountId));
        if (!passwordEncoder.matches(form.getCurrentPassword(), account.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không đúng");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu nhập lại không khớp");
        }
        account.setPassword(passwordEncoder.encode(form.getNewPassword()));
    }

    // ================= helpers =================

    private Customer findWithAccount(Long customerId) {
        return customerRepository.findWithAccountById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng", customerId));
    }

    private void checkDuplicates(String email, String mobile, String identityCard, Long customerId, Long accountId) {
        boolean isNew = customerId == null;
        boolean emailUsed = isNew
                ? customerRepository.existsByEmail(email) || accountRepository.existsByUsername(email)
                : customerRepository.existsByEmailAndCustomerIdNot(email, customerId)
                  || accountRepository.existsByUsernameAndAccountIdNot(email, accountId);
        if (emailUsed) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (isNew ? customerRepository.existsByMobile(mobile)
                  : customerRepository.existsByMobileAndCustomerIdNot(mobile, customerId)) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }
        if (isNew ? customerRepository.existsByIdentityCard(identityCard)
                  : customerRepository.existsByIdentityCardAndCustomerIdNot(identityCard, customerId)) {
            throw new BusinessException("Số CCCD đã được sử dụng");
        }
    }

    private void checkAge(CustomerForm form) {
        if (ValidationUtils.age(form.getBirthday()) < 13) {
            throw new BusinessException("Khách hàng phải từ đủ 13 tuổi");
        }
    }

    private void copy(CustomerForm form, Customer customer, String email) {
        customer.setFullName(form.getFullName().trim());
        customer.setEmail(email);
        customer.setMobile(form.getMobile());
        customer.setBirthday(form.getBirthday());
        customer.setIdentityCard(form.getIdentityCard());
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminCustomerController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.CustomerForm;
import com.fu.cinema.enums.AccountStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private static final String FORM_VIEW = "admin/customers/form";

    private final CustomerService customerService;

    @GetMapping
    public String list(@RequestParam(required = false) String kw,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("customers", customerService.search(kw, page, PageUtils.DEFAULT_SIZE));
        model.addAttribute("kw", kw);
        model.addAttribute("pageUrl", PageUtils.currentUrlWithoutPage());
        return "admin/customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CustomerForm());
        model.addAttribute("statuses", AccountStatus.values());
        return FORM_VIEW;
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") CustomerForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        model.addAttribute("statuses", AccountStatus.values());
        if (br.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            customerService.create(form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return FORM_VIEW;
        }
        ra.addFlashAttribute("success", "Đã thêm khách hàng " + form.getFullName());
        return "redirect:/admin/customers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", customerService.getForm(id));
        model.addAttribute("statuses", AccountStatus.values());
        return FORM_VIEW;
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") CustomerForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        form.setCustomerId(id);
        model.addAttribute("statuses", AccountStatus.values());
        if (br.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            customerService.update(id, form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return FORM_VIEW;
        }
        ra.addFlashAttribute("success", "Đã cập nhật khách hàng " + form.getFullName());
        return "redirect:/admin/customers";
    }

    @PostMapping("/{id}/lock")
    public String toggleLock(@PathVariable Long id, RedirectAttributes ra) {
        customerService.toggleLock(id);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản");
        return "redirect:/admin/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        boolean deleted = customerService.delete(id);
        ra.addFlashAttribute("success", deleted
                ? "Đã xoá khách hàng"
                : "Khách hàng đã có đơn đặt vé nên chỉ khoá tài khoản");
        return "redirect:/admin/customers";
    }
}
```

📄 **`src/main/resources/templates/admin/customers/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Khách hàng')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <div class="d-flex justify-content-between mb-3">
        <h3>Khách hàng</h3>
        <a class="btn btn-primary" th:href="@{/admin/customers/new}">+ Thêm khách hàng</a>
    </div>
    <form class="row g-2 mb-3" method="get" th:action="@{/admin/customers}">
        <div class="col-md-10"><input class="form-control" name="kw" th:value="${kw}" placeholder="Tên, email hoặc số điện thoại"></div>
        <div class="col-md-2 d-grid"><button class="btn btn-outline-primary">Tìm</button></div>
    </form>
    <table class="table table-hover align-middle">
        <thead class="table-light"><tr><th>Họ tên</th><th>Email</th><th>SĐT</th><th>Ngày sinh</th><th>Trạng thái</th><th></th></tr></thead>
        <tbody>
        <tr th:if="${customers.empty}"><td colspan="6" class="text-center text-muted">Không có dữ liệu</td></tr>
        <tr th:each="c : ${customers.content}">
            <td th:text="${c.fullName}"></td>
            <td th:text="${c.email}"></td>
            <td th:text="${c.mobile}"></td>
            <td th:text="${#temporals.format(c.birthday, 'dd/MM/yyyy')}"></td>
            <td><span class="badge" th:classappend="${c.account.status.name() == 'ACTIVE'} ? 'bg-success' : 'bg-secondary'"
                      th:text="${c.account.status}"></span></td>
            <td class="text-end text-nowrap">
                <a class="btn btn-sm btn-outline-secondary" th:href="@{/admin/customers/{id}/edit(id=${c.customerId})}">Sửa</a>
                <form class="d-inline" method="post" th:action="@{/admin/customers/{id}/lock(id=${c.customerId})}">
                    <button class="btn btn-sm btn-outline-warning"
                            th:text="${c.account.status.name() == 'ACTIVE'} ? 'Khoá' : 'Mở khoá'"></button>
                </form>
                <form class="d-inline" method="post" th:action="@{/admin/customers/{id}/delete(id=${c.customerId})}"
                      onsubmit="return confirm('Xoá khách hàng này? Nếu đã có đơn đặt vé, tài khoản sẽ bị khoá.');">
                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
    <div th:replace="~{fragments/layout :: pagination(${customers}, ${pageUrl})}"></div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Tìm kiếm dùng **GET** (`?kw=...&page=1`) → URL chia sẻ/bookmark được, nút Back hoạt động đúng.
- `pageUrl` giữ nguyên `kw` khi chuyển trang.
- Query Q1 `JOIN FETCH c.account` → hiển thị trạng thái tài khoản mà không bị lazy exception / N+1.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Tìm `nguyen` | Nguyen Van An |
| Tìm `0912` | Tran Thi Binh |
| Tìm `GMAIL` | Cả 2 khách (không phân biệt hoa thường) |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#7** đã được assign cho **B**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/7-customer-admin` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(customer): add customer search query with pagination` + `Refs #7`
  - [ ] `feat(customer): add admin customer list page` + `Refs #7`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #7.

## TODO 17 — Thêm / sửa / khoá / xoá khách hàng

**Issue:** #7 · **Người làm:** B · **Nhánh:** `feature/7-customer-admin`

**Mục tiêu:** CRUD khách hàng kèm tài khoản; xoá theo BR-04.

### Các bước

1. Tạo `CustomerForm` (mật khẩu: bắt buộc khi thêm, để trống khi sửa = giữ nguyên).
2. Service (đã có ở TODO 16):
   - `create()`: kiểm tra trùng + tuổi → tạo `Account` (username = email) + `Customer`.
   - `update()`: kiểm tra trùng **loại trừ chính bản ghi** (`...AndCustomerIdNot`), cập nhật cả username và trạng thái tài khoản; dirty checking tự UPDATE.
   - `delete()`: có đơn → `LOCKED`, trả `false`; chưa có → xoá `Customer` rồi `Account`, trả `true` (BR-04).
   - `toggleLock()`: ACTIVE ⇄ LOCKED.
3. Controller: `GET/POST /new`, `GET/POST /{id}/edit`, `POST /{id}/lock`, `POST /{id}/delete` (đã có ở TODO 16).
4. Tạo `admin/customers/form.html` dùng chung cho thêm và sửa.

### Code

📄 **`src/main/java/com/fu/cinema/dto/CustomerForm.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.enums.AccountStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Form Admin thêm/sửa khách hàng. Mật khẩu bắt buộc khi thêm, để trống khi sửa = giữ nguyên
@Data
public class CustomerForm {

    private Long customerId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại gồm 10 chữ số, bắt đầu bằng 0")
    private String mobile;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải ở quá khứ")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD gồm đúng 12 chữ số")
    private String identityCard;

    @Pattern(regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
             message = "Mật khẩu ≥ 8 ký tự, có chữ hoa, chữ thường và chữ số")
    private String password;

    @NotNull
    private AccountStatus status = AccountStatus.ACTIVE;
}
```

📄 **`src/main/resources/templates/admin/customers/form.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Khách hàng')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 720px">
    <h3 th:text="${form.customerId == null} ? 'Thêm khách hàng' : 'Sửa khách hàng'"></h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:object="${form}" novalidate class="row g-3"
          th:action="${form.customerId == null} ? @{/admin/customers/new} : @{/admin/customers/{id}/edit(id=${form.customerId})}">
        <div class="col-12">
            <label class="form-label">Họ tên</label>
            <input class="form-control" th:field="*{fullName}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{fullName}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Email (tên đăng nhập)</label>
            <input type="email" class="form-control" th:field="*{email}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{email}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input class="form-control" th:field="*{mobile}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{mobile}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input type="date" class="form-control" th:field="*{birthday}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{birthday}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">CCCD</label>
            <input class="form-control" th:field="*{identityCard}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{identityCard}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label" th:text="${form.customerId == null} ? 'Mật khẩu' : 'Mật khẩu mới (để trống nếu không đổi)'"></label>
            <input type="password" class="form-control" th:field="*{password}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{password}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Trạng thái tài khoản</label>
            <select class="form-select" th:field="*{status}">
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}"></option>
            </select>
        </div>
        <div class="col-12">
            <button class="btn btn-primary">Lưu</button>
            <a class="btn btn-secondary" th:href="@{/admin/customers}">Huỷ</a>
        </div>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Xoá theo thứ tự **bảng con trước** (`customers` giữ FK) rồi mới xoá `accounts`.
- Form dùng chung: `th:action` chọn URL theo `form.customerId == null`.
- Xoá có `confirm()` phía trình duyệt + POST (không xoá bằng link GET).

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Thêm khách với SĐT `0901234567` | "Số điện thoại đã được sử dụng" |
| Thêm khách mới hợp lệ rồi xoá | "Đã xoá khách hàng", mất khỏi DB |
| Xoá *Nguyen Van An* (đã có đơn) | "...chỉ khoá tài khoản"; An không đăng nhập được |
| Sửa An, để trống mật khẩu | Mật khẩu cũ vẫn dùng được |
| Mở khoá An | Đăng nhập lại được |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/7-customer-admin` (issue #7); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(customer): add create and edit customer form` + `Refs #7`
  - [ ] `feat(customer): lock and unlock customer account` + `Refs #7`
  - [ ] `feat(customer): delete customer or lock when bookings exist` + `Refs #7`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(customer): admin customer management`** → base `develop`, mô tả theo PR template, ghi `Closes #7`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 16, 17; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Không N+1 khi hiển thị trạng thái tài khoản
  - [ ] Giữ từ khoá khi phân trang
  - [ ] Rule BR-04 nằm ở service
  - [ ] Kiểm tra trùng khi sửa loại trừ chính bản ghi
  - [ ] Mật khẩu mới được mã hoá
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #7 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part F — Admin: Thể loại & phim

## TODO 18 — CRUD thể loại

**Issue:** #8 · **Người làm:** C · **Nhánh:** `feature/8-genre-crud`

**Mục tiêu:** Quản lý thể loại; chặn xoá khi còn phim (BR-05).

### Các bước

1. `GenreForm`, `GenreServiceImpl` (kiểm tra trùng tên không phân biệt hoa thường, chặn xoá bằng `existsByGenre_GenreId`).
2. `AdminGenreController` (list, new, edit, delete).
3. `admin/genres/list.html`, `admin/genres/form.html`.

### Code

📄 **`src/main/java/com/fu/cinema/dto/GenreForm.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreForm {

    private Long genreId;

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 50, message = "Tên thể loại tối đa 50 ký tự")
    private String genreName;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 500)
    private String description;
}
```

📄 **`src/main/java/com/fu/cinema/service/impl/GenreServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.GenreForm;
import com.fu.cinema.entity.Genre;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.GenreRepository;
import com.fu.cinema.repository.MovieRepository;
import com.fu.cinema.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAllByOrderByGenreNameAsc();
    }

    @Override
    public GenreForm getForm(Long genreId) {
        Genre g = find(genreId);
        GenreForm form = new GenreForm();
        form.setGenreId(g.getGenreId());
        form.setGenreName(g.getGenreName());
        form.setDescription(g.getDescription());
        return form;
    }

    @Override
    @Transactional
    public void create(GenreForm form) {
        if (genreRepository.existsByGenreNameIgnoreCase(form.getGenreName().trim())) {
            throw new BusinessException("Tên thể loại đã tồn tại");
        }
        genreRepository.save(new Genre(form.getGenreName().trim(), form.getDescription().trim()));
    }

    @Override
    @Transactional
    public void update(Long genreId, GenreForm form) {
        Genre genre = find(genreId);
        if (genreRepository.existsByGenreNameIgnoreCaseAndGenreIdNot(form.getGenreName().trim(), genreId)) {
            throw new BusinessException("Tên thể loại đã tồn tại");
        }
        genre.setGenreName(form.getGenreName().trim());
        genre.setDescription(form.getDescription().trim());
    }

    @Override
    @Transactional
    public void delete(Long genreId) {
        Genre genre = find(genreId);
        if (movieRepository.existsByGenre_GenreId(genreId)) {                     // BR-05
            throw new BusinessException("Không thể xoá: vẫn còn phim thuộc thể loại \"" + genre.getGenreName() + "\"");
        }
        genreRepository.delete(genre);
    }

    private Genre find(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Thể loại", id));
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminGenreController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.GenreForm;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/genres")
@RequiredArgsConstructor
public class AdminGenreController {

    private static final String FORM_VIEW = "admin/genres/form";

    private final GenreService genreService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "admin/genres/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new GenreForm());
        return FORM_VIEW;
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") GenreForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            genreService.create(form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return FORM_VIEW;
        }
        ra.addFlashAttribute("success", "Đã thêm thể loại " + form.getGenreName());
        return "redirect:/admin/genres";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", genreService.getForm(id));
        return FORM_VIEW;
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") GenreForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        form.setGenreId(id);
        if (br.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            genreService.update(id, form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return FORM_VIEW;
        }
        ra.addFlashAttribute("success", "Đã cập nhật thể loại");
        return "redirect:/admin/genres";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        genreService.delete(id);                         // BusinessException → GlobalExceptionHandler (flash error)
        ra.addFlashAttribute("success", "Đã xoá thể loại");
        return "redirect:/admin/genres";
    }
}
```

📄 **`src/main/resources/templates/admin/genres/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Thể loại')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <div class="d-flex justify-content-between mb-3">
        <h3>Thể loại phim</h3>
        <a class="btn btn-primary" th:href="@{/admin/genres/new}">+ Thêm thể loại</a>
    </div>
    <table class="table table-hover">
        <thead class="table-light"><tr><th>Tên thể loại</th><th>Mô tả</th><th></th></tr></thead>
        <tbody>
        <tr th:each="g : ${genres}">
            <td th:text="${g.genreName}"></td>
            <td th:text="${g.description}"></td>
            <td class="text-end text-nowrap">
                <a class="btn btn-sm btn-outline-secondary" th:href="@{/admin/genres/{id}/edit(id=${g.genreId})}">Sửa</a>
                <form class="d-inline" method="post" th:action="@{/admin/genres/{id}/delete(id=${g.genreId})}"
                      onsubmit="return confirm('Xoá thể loại này?');">
                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/admin/genres/form.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Thể loại')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 640px">
    <h3 th:text="${form.genreId == null} ? 'Thêm thể loại' : 'Sửa thể loại'"></h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:object="${form}" novalidate
          th:action="${form.genreId == null} ? @{/admin/genres/new} : @{/admin/genres/{id}/edit(id=${form.genreId})}">
        <div class="mb-3">
            <label class="form-label">Tên thể loại</label>
            <input class="form-control" th:field="*{genreName}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{genreName}"></div>
        </div>
        <div class="mb-3">
            <label class="form-label">Mô tả</label>
            <textarea class="form-control" rows="3" th:field="*{description}" th:errorclass="is-invalid"></textarea>
            <div class="invalid-feedback" th:errors="*{description}"></div>
        </div>
        <button class="btn btn-primary">Lưu</button>
        <a class="btn btn-secondary" th:href="@{/admin/genres}">Huỷ</a>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- `delete()` ném `BusinessException` → `GlobalExceptionHandler` đặt flash lỗi và quay lại danh sách (controller không cần try/catch).

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Thêm `hành động` | "Tên thể loại đã tồn tại" |
| Xoá *Khoa học viễn tưởng* | Xoá thành công |
| Xoá *Hành động* | "Không thể xoá: vẫn còn phim..." |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#8** đã được assign cho **C**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/8-genre-crud` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(genre): add genre service with duplicate and delete checks` + `Refs #8`
  - [ ] `feat(genre): add admin genre pages` + `Refs #8`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(genre): genre management`** → base `develop`, mô tả theo PR template, ghi `Closes #8`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 18; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] BR-05
  - [ ] Trùng tên không phân biệt hoa thường
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #8 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 19 — Danh sách phim: tìm, lọc, phân trang, sắp xếp

**Issue:** #9 · **Người làm:** C · **Nhánh:** `feature/9-movie-crud`

**Mục tiêu:** Admin tìm phim theo tên, lọc thể loại + trạng thái, sắp xếp, phân trang.

### Các bước

1. Cài đặt `MovieServiceImpl` (file đầy đủ — dùng cho TODO 19–21, 23, 27).
2. `searchForAdmin()`: chỉ cho sắp xếp theo whitelist `title`, `releaseDate`, `duration` (chống truyền tên field tuỳ ý); `status == null` → tất cả trạng thái.
3. `AdminMovieController.list()` + `admin/movies/list.html` (các bộ lọc trong 1 form GET).

### Code

📄 **`src/main/java/com/fu/cinema/service/impl/MovieServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.MovieForm;
import com.fu.cinema.entity.Genre;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.enums.MovieStatus;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.GenreRepository;
import com.fu.cinema.repository.MovieRepository;
import com.fu.cinema.repository.ShowtimeRepository;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private static final Set<String> SORTABLE = Set.of("title", "releaseDate", "duration");

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public Page<Movie> searchForAdmin(String keyword, Long genreId, MovieStatus status, int page, int size, String sort) {
        String field = SORTABLE.contains(sort) ? sort : "releaseDate";
        Sort order = "title".equals(field) ? Sort.by(field).ascending() : Sort.by(field).descending();
        Set<MovieStatus> statuses = status == null ? EnumSet.allOf(MovieStatus.class) : EnumSet.of(status);
        return movieRepository.search(ValidationUtils.blankToNull(keyword), genreId, statuses,
                PageRequest.of(Math.max(page, 0), size, order));
    }

    @Override
    public Page<Movie> searchForCustomer(String keyword, Long genreId, int page, int size) {
        // Khách hàng không thấy phim STOPPED (BR-06)
        return movieRepository.search(ValidationUtils.blankToNull(keyword), genreId,
                EnumSet.of(MovieStatus.SHOWING, MovieStatus.COMING_SOON),
                PageRequest.of(Math.max(page, 0), size, Sort.by("releaseDate").descending()));
    }

    @Override
    public Movie getById(Long movieId) {
        return movieRepository.findWithGenreById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Phim", movieId));
    }

    @Override
    public MovieForm getForm(Long movieId) {
        Movie m = getById(movieId);
        MovieForm f = new MovieForm();
        f.setMovieId(m.getMovieId());
        f.setTitle(m.getTitle());
        f.setDirector(m.getDirector());
        f.setDuration(m.getDuration());
        f.setReleaseDate(m.getReleaseDate());
        f.setAgeRating(m.getAgeRating());
        f.setDescription(m.getDescription());
        f.setPosterUrl(m.getPosterUrl());
        f.setGenreId(m.getGenre().getGenreId());
        f.setStatus(m.getStatus());
        return f;
    }

    @Override
    @Transactional
    public void create(MovieForm form) {
        Movie movie = new Movie();
        copy(form, movie);
        movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void update(Long movieId, MovieForm form) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Phim", movieId));
        copy(form, movie);
    }

    @Override
    @Transactional
    public boolean delete(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Phim", movieId));
        if (showtimeRepository.existsByMovie_MovieId(movieId)) {                   // BR-06: soft delete
            movie.setStatus(MovieStatus.STOPPED);
            return false;
        }
        movieRepository.delete(movie);
        return true;
    }

    @Override
    public List<Movie> findSchedulable() {
        return movieRepository.findByStatusNotOrderByTitleAsc(MovieStatus.STOPPED);
    }

    @Override
    public long countShowing() {
        return movieRepository.countByStatus(MovieStatus.SHOWING);
    }

    private void copy(MovieForm form, Movie movie) {
        Genre genre = genreRepository.findById(form.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Thể loại", form.getGenreId()));
        movie.setTitle(form.getTitle().trim());
        movie.setDirector(form.getDirector().trim());
        movie.setDuration(form.getDuration());
        movie.setReleaseDate(form.getReleaseDate());
        movie.setAgeRating(form.getAgeRating());
        movie.setDescription(form.getDescription().trim());
        movie.setPosterUrl(form.getPosterUrl().trim());
        movie.setGenre(genre);
        movie.setStatus(form.getStatus());
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminMovieController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.MovieForm;
import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.MovieStatus;
import com.fu.cinema.service.GenreService;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private static final String FORM_VIEW = "admin/movies/form";

    private final MovieService movieService;
    private final GenreService genreService;

    @GetMapping
    public String list(@RequestParam(required = false) String kw,
                       @RequestParam(required = false) Long genreId,
                       @RequestParam(required = false) MovieStatus status,
                       @RequestParam(defaultValue = "releaseDate") String sort,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("movies", movieService.searchForAdmin(kw, genreId, status, page, PageUtils.DEFAULT_SIZE, sort));
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("statuses", MovieStatus.values());
        model.addAttribute("kw", kw);
        model.addAttribute("genreId", genreId);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("pageUrl", PageUtils.currentUrlWithoutPage());
        return "admin/movies/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        MovieForm form = new MovieForm();
        form.setStatus(MovieStatus.COMING_SOON);
        return showForm(form, model);
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") MovieForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return showForm(form, model);
        }
        movieService.create(form);
        ra.addFlashAttribute("success", "Đã thêm phim " + form.getTitle());
        return "redirect:/admin/movies";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return showForm(movieService.getForm(id), model);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") MovieForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        form.setMovieId(id);
        if (br.hasErrors()) {
            return showForm(form, model);
        }
        movieService.update(id, form);
        ra.addFlashAttribute("success", "Đã cập nhật phim " + form.getTitle());
        return "redirect:/admin/movies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        boolean deleted = movieService.delete(id);
        ra.addFlashAttribute("success", deleted
                ? "Đã xoá phim"
                : "Phim đã có suất chiếu nên được chuyển sang trạng thái STOPPED");
        return "redirect:/admin/movies";
    }

    private String showForm(MovieForm form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("ageRatings", AgeRating.values());
        model.addAttribute("statuses", MovieStatus.values());
        return FORM_VIEW;
    }
}
```

📄 **`src/main/resources/templates/admin/movies/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Quản lý phim')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <div class="d-flex justify-content-between mb-3">
        <h3>Quản lý phim</h3>
        <a class="btn btn-primary" th:href="@{/admin/movies/new}">+ Thêm phim</a>
    </div>
    <form class="row g-2 mb-3" method="get" th:action="@{/admin/movies}">
        <div class="col-md-4"><input class="form-control" name="kw" th:value="${kw}" placeholder="Tên phim"></div>
        <div class="col-md-2">
            <select class="form-select" name="genreId">
                <option value="">Thể loại</option>
                <option th:each="g : ${genres}" th:value="${g.genreId}" th:text="${g.genreName}" th:selected="${g.genreId == genreId}"></option>
            </select>
        </div>
        <div class="col-md-2">
            <select class="form-select" name="status">
                <option value="">Trạng thái</option>
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}" th:selected="${s == status}"></option>
            </select>
        </div>
        <div class="col-md-2">
            <select class="form-select" name="sort">
                <option value="releaseDate" th:selected="${sort == 'releaseDate'}">Mới khởi chiếu</option>
                <option value="title" th:selected="${sort == 'title'}">Tên A→Z</option>
                <option value="duration" th:selected="${sort == 'duration'}">Thời lượng</option>
            </select>
        </div>
        <div class="col-md-2 d-grid"><button class="btn btn-outline-primary">Lọc</button></div>
    </form>
    <table class="table table-hover align-middle">
        <thead class="table-light"><tr><th>Tên phim</th><th>Thể loại</th><th>Thời lượng</th><th>Khởi chiếu</th><th>Độ tuổi</th><th>Trạng thái</th><th></th></tr></thead>
        <tbody>
        <tr th:if="${movies.empty}"><td colspan="7" class="text-center text-muted">Không có dữ liệu</td></tr>
        <tr th:each="m : ${movies.content}">
            <td th:text="${m.title}"></td>
            <td th:text="${m.genre.genreName}"></td>
            <td th:text="|${m.duration} phút|"></td>
            <td th:text="${#temporals.format(m.releaseDate, 'dd/MM/yyyy')}"></td>
            <td th:text="${m.ageRating}"></td>
            <td th:text="${m.status}"></td>
            <td class="text-end text-nowrap">
                <a class="btn btn-sm btn-outline-secondary" th:href="@{/admin/movies/{id}/edit(id=${m.movieId})}">Sửa</a>
                <form class="d-inline" method="post" th:action="@{/admin/movies/{id}/delete(id=${m.movieId})}"
                      onsubmit="return confirm('Xoá phim? Nếu đã có suất chiếu, phim sẽ chuyển sang STOPPED.');">
                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
    <div th:replace="~{fragments/layout :: pagination(${movies}, ${pageUrl})}"></div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Dùng chung Q2 cho Admin (mọi trạng thái) và Customer (chỉ `SHOWING`, `COMING_SOON`) bằng tham số `statuses`.
- Whitelist cột sắp xếp: tham số `sort` từ URL đi thẳng vào `ORDER BY` → nếu không kiểm soát sẽ gây lỗi `PropertyReferenceException`.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Lọc thể loại *Hoạt hình* | Chú Mèo Phiêu Lưu, Huyền Thoại Cũ |
| Lọc trạng thái `STOPPED` | Huyền Thoại Cũ |
| Tìm `bi` + lọc *Hành động* | Biệt Đội Siêu Tốc |
| Sắp xếp Tên A→Z | Thứ tự bảng chữ cái |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#9** đã được assign cho **C**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/9-movie-crud` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(movie): add movie search with genre and status filters` + `Refs #9`
  - [ ] `feat(movie): add admin movie list with pagination and sorting` + `Refs #9`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #9.

## TODO 20 — Thêm / sửa phim

**Issue:** #9 · **Người làm:** C · **Nhánh:** `feature/9-movie-crud`

**Mục tiêu:** Form phim có validate; chọn thể loại từ dropdown.

### Các bước

1. `MovieForm` (`@Min(30) @Max(300)` thời lượng, `@NotNull` độ tuổi, thể loại, trạng thái).
2. `create()`/`update()` trong `MovieServiceImpl` (TODO 19) — `copy()` tìm `Genre` theo `genreId`.
3. `admin/movies/form.html`: dropdown thể loại, độ tuổi, trạng thái; lỗi hiển thị cạnh từng trường.

### Code

📄 **`src/main/java/com/fu/cinema/dto/MovieForm.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MovieForm {

    private Long movieId;

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Đạo diễn không được để trống")
    @Size(max = 100)
    private String director;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 30, message = "Thời lượng tối thiểu 30 phút")
    @Max(value = 300, message = "Thời lượng tối đa 300 phút")
    private Integer duration;

    @NotNull(message = "Ngày khởi chiếu không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @NotNull(message = "Vui lòng chọn độ tuổi")
    private AgeRating ageRating;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 2000)
    private String description;

    @NotBlank(message = "Poster URL không được để trống")
    @Size(max = 500)
    private String posterUrl;

    @NotNull(message = "Vui lòng chọn thể loại")
    private Long genreId;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private MovieStatus status;
}
```

📄 **`src/main/resources/templates/admin/movies/form.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Phim')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 820px">
    <h3 th:text="${form.movieId == null} ? 'Thêm phim' : 'Sửa phim'"></h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:object="${form}" novalidate class="row g-3"
          th:action="${form.movieId == null} ? @{/admin/movies/new} : @{/admin/movies/{id}/edit(id=${form.movieId})}">
        <div class="col-md-8">
            <label class="form-label">Tên phim</label>
            <input class="form-control" th:field="*{title}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{title}"></div>
        </div>
        <div class="col-md-4">
            <label class="form-label">Thể loại</label>
            <select class="form-select" th:field="*{genreId}" th:errorclass="is-invalid">
                <option value="">-- Chọn --</option>
                <option th:each="g : ${genres}" th:value="${g.genreId}" th:text="${g.genreName}"></option>
            </select>
            <div class="invalid-feedback" th:errors="*{genreId}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Đạo diễn</label>
            <input class="form-control" th:field="*{director}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{director}"></div>
        </div>
        <div class="col-md-3">
            <label class="form-label">Thời lượng (phút)</label>
            <input type="number" class="form-control" th:field="*{duration}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{duration}"></div>
        </div>
        <div class="col-md-3">
            <label class="form-label">Khởi chiếu</label>
            <input type="date" class="form-control" th:field="*{releaseDate}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{releaseDate}"></div>
        </div>
        <div class="col-md-3">
            <label class="form-label">Độ tuổi</label>
            <select class="form-select" th:field="*{ageRating}" th:errorclass="is-invalid">
                <option value="">-- Chọn --</option>
                <option th:each="a : ${ageRatings}" th:value="${a}" th:text="${a}"></option>
            </select>
            <div class="invalid-feedback" th:errors="*{ageRating}"></div>
        </div>
        <div class="col-md-3">
            <label class="form-label">Trạng thái</label>
            <select class="form-select" th:field="*{status}">
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}"></option>
            </select>
        </div>
        <div class="col-md-6">
            <label class="form-label">Poster URL</label>
            <input class="form-control" th:field="*{posterUrl}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{posterUrl}"></div>
        </div>
        <div class="col-12">
            <label class="form-label">Mô tả</label>
            <textarea class="form-control" rows="4" th:field="*{description}" th:errorclass="is-invalid"></textarea>
            <div class="invalid-feedback" th:errors="*{description}"></div>
        </div>
        <div class="col-12">
            <button class="btn btn-primary">Lưu</button>
            <a class="btn btn-secondary" th:href="@{/admin/movies}">Huỷ</a>
        </div>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Khi `br.hasErrors()` phải **nạp lại** danh sách thể loại/độ tuổi/trạng thái cho dropdown → gom vào hàm `showForm()` trong controller.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Thời lượng `10` | "Thời lượng tối thiểu 30 phút" |
| Không chọn thể loại | "Vui lòng chọn thể loại" |
| Dữ liệu hợp lệ | Phim mới xuất hiện đầu danh sách (theo ngày khởi chiếu) |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/9-movie-crud` (issue #9); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(movie): add movie form with validation` + `Refs #9`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #9.

## TODO 21 — Xoá phim (soft delete)

**Issue:** #9 · **Người làm:** C · **Nhánh:** `feature/9-movie-crud`

**Mục tiêu:** Xoá phim theo BR-06.

### Các bước

1. `MovieServiceImpl.delete()` (TODO 19): có suất chiếu (`existsByMovie_MovieId`) → `status = STOPPED`, trả `false`; ngược lại xoá, trả `true`.
2. Controller hiện thông báo theo kết quả.
3. Phim `STOPPED`: không hiện ở trang chủ (Q2 với `statuses`), không có trong dropdown tạo suất chiếu (`findByStatusNotOrderByTitleAsc`).

### Code

Code dùng cho TODO này đã có ở các file trước:

- `MovieServiceImpl.delete()` — TODO 19
- `AdminMovieController.delete()` — TODO 19

### Giải thích & lưu ý

- Soft delete giữ lại lịch sử đơn đặt vé & báo cáo doanh thu của phim.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Xoá *Mùa Hè Năm Ấy* | "Đã xoá phim" |
| Xoá *Biệt Đội Siêu Tốc* | "...chuyển sang trạng thái STOPPED"; trang chủ không còn phim này |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/9-movie-crud` (issue #9); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(movie): soft delete movie that has showtimes` + `Refs #9`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(movie): movie management with filters and soft delete`** → base `develop`, mô tả theo PR template, ghi `Closes #9`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 19, 20, 21; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Whitelist cột sắp xếp
  - [ ] Các bộ lọc kết hợp được
  - [ ] Dropdown được nạp lại khi form lỗi
  - [ ] BR-06 trong service
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #9 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part G — Admin: Phòng & suất chiếu

## TODO 22 — CRUD phòng chiếu

**Issue:** #10 · **Người làm:** D · **Nhánh:** `feature/10-room-crud`

**Mục tiêu:** Quản lý phòng; không xoá phòng đã có suất chiếu.

### Các bước

1. `RoomForm` (`totalRows` 1–26 tương ứng hàng A–Z, `seatsPerRow` 1–30).
2. `RoomServiceImpl`: trùng tên → lỗi; **không cho đổi sơ đồ ghế** của phòng đã có suất chiếu (vé cũ có thể trỏ tới ghế không còn tồn tại); xoá phòng có suất chiếu → `MAINTENANCE`.
3. `AdminRoomController`, `admin/rooms/list.html`, `admin/rooms/form.html`.

### Code

📄 **`src/main/java/com/fu/cinema/dto/RoomForm.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RoomForm {

    private Long roomId;

    @NotBlank(message = "Tên phòng không được để trống")
    @Size(max = 50)
    private String roomName;

    @NotNull(message = "Vui lòng chọn loại phòng")
    private RoomType roomType;

    @NotNull(message = "Số hàng không được để trống")
    @Min(value = 1, message = "Số hàng từ 1 đến 26")
    @Max(value = 26, message = "Số hàng từ 1 đến 26")
    private Integer totalRows;

    @NotNull(message = "Số ghế mỗi hàng không được để trống")
    @Min(value = 1, message = "Số ghế mỗi hàng từ 1 đến 30")
    @Max(value = 30, message = "Số ghế mỗi hàng từ 1 đến 30")
    private Integer seatsPerRow;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private RoomStatus status;
}
```

📄 **`src/main/java/com/fu/cinema/service/impl/RoomServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.RoomForm;
import com.fu.cinema.entity.Room;
import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.RoomRepository;
import com.fu.cinema.repository.ShowtimeRepository;
import com.fu.cinema.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll(Sort.by("roomName"));
    }

    @Override
    public List<Room> findActive() {
        return roomRepository.findByStatusOrderByRoomNameAsc(RoomStatus.ACTIVE);
    }

    @Override
    public RoomForm getForm(Long roomId) {
        Room r = find(roomId);
        RoomForm f = new RoomForm();
        f.setRoomId(r.getRoomId());
        f.setRoomName(r.getRoomName());
        f.setRoomType(r.getRoomType());
        f.setTotalRows(r.getTotalRows());
        f.setSeatsPerRow(r.getSeatsPerRow());
        f.setStatus(r.getStatus());
        return f;
    }

    @Override
    @Transactional
    public void create(RoomForm form) {
        if (roomRepository.existsByRoomNameIgnoreCase(form.getRoomName().trim())) {
            throw new BusinessException("Tên phòng đã tồn tại");
        }
        roomRepository.save(new Room(form.getRoomName().trim(), form.getRoomType(),
                form.getTotalRows(), form.getSeatsPerRow(), form.getStatus()));
    }

    @Override
    @Transactional
    public void update(Long roomId, RoomForm form) {
        Room room = find(roomId);
        if (roomRepository.existsByRoomNameIgnoreCaseAndRoomIdNot(form.getRoomName().trim(), roomId)) {
            throw new BusinessException("Tên phòng đã tồn tại");
        }
        boolean layoutChanged = !room.getTotalRows().equals(form.getTotalRows())
                || !room.getSeatsPerRow().equals(form.getSeatsPerRow());
        if (layoutChanged && showtimeRepository.existsByRoom_RoomId(roomId)) {
            throw new BusinessException("Không thể đổi sơ đồ ghế của phòng đã có suất chiếu");
        }
        room.setRoomName(form.getRoomName().trim());
        room.setRoomType(form.getRoomType());
        room.setTotalRows(form.getTotalRows());
        room.setSeatsPerRow(form.getSeatsPerRow());
        room.setStatus(form.getStatus());
    }

    @Override
    @Transactional
    public boolean delete(Long roomId) {
        Room room = find(roomId);
        if (showtimeRepository.existsByRoom_RoomId(roomId)) {
            room.setStatus(RoomStatus.MAINTENANCE);
            return false;
        }
        roomRepository.delete(room);
        return true;
    }

    private Room find(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Phòng chiếu", id));
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminRoomController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.RoomForm;
import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.RoomType;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private static final String FORM_VIEW = "admin/rooms/form";

    private final RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        return "admin/rooms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        RoomForm form = new RoomForm();
        form.setStatus(RoomStatus.ACTIVE);
        return showForm(form, model);
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") RoomForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return showForm(form, model);
        }
        try {
            roomService.create(form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(form, model);
        }
        ra.addFlashAttribute("success", "Đã thêm phòng " + form.getRoomName());
        return "redirect:/admin/rooms";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return showForm(roomService.getForm(id), model);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") RoomForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        form.setRoomId(id);
        if (br.hasErrors()) {
            return showForm(form, model);
        }
        try {
            roomService.update(id, form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(form, model);
        }
        ra.addFlashAttribute("success", "Đã cập nhật phòng " + form.getRoomName());
        return "redirect:/admin/rooms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        boolean deleted = roomService.delete(id);
        ra.addFlashAttribute("success", deleted
                ? "Đã xoá phòng"
                : "Phòng đã có suất chiếu nên được chuyển sang trạng thái MAINTENANCE");
        return "redirect:/admin/rooms";
    }

    private String showForm(RoomForm form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("statuses", RoomStatus.values());
        return FORM_VIEW;
    }
}
```

📄 **`src/main/resources/templates/admin/rooms/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Phòng chiếu')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <div class="d-flex justify-content-between mb-3">
        <h3>Phòng chiếu</h3>
        <a class="btn btn-primary" th:href="@{/admin/rooms/new}">+ Thêm phòng</a>
    </div>
    <table class="table table-hover align-middle">
        <thead class="table-light"><tr><th>Tên phòng</th><th>Loại</th><th>Sơ đồ</th><th>Sức chứa</th><th>Trạng thái</th><th></th></tr></thead>
        <tbody>
        <tr th:each="r : ${rooms}">
            <td th:text="${r.roomName}"></td>
            <td th:text="${r.roomType.label}"></td>
            <td th:text="|${r.totalRows} hàng × ${r.seatsPerRow} ghế|"></td>
            <td th:text="${r.capacity}"></td>
            <td th:text="${r.status}"></td>
            <td class="text-end text-nowrap">
                <a class="btn btn-sm btn-outline-secondary" th:href="@{/admin/rooms/{id}/edit(id=${r.roomId})}">Sửa</a>
                <form class="d-inline" method="post" th:action="@{/admin/rooms/{id}/delete(id=${r.roomId})}"
                      onsubmit="return confirm('Xoá phòng? Nếu đã có suất chiếu, phòng sẽ chuyển sang MAINTENANCE.');">
                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/admin/rooms/form.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Phòng chiếu')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 640px">
    <h3 th:text="${form.roomId == null} ? 'Thêm phòng' : 'Sửa phòng'"></h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:object="${form}" novalidate class="row g-3"
          th:action="${form.roomId == null} ? @{/admin/rooms/new} : @{/admin/rooms/{id}/edit(id=${form.roomId})}">
        <div class="col-md-6">
            <label class="form-label">Tên phòng</label>
            <input class="form-control" th:field="*{roomName}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{roomName}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Loại phòng</label>
            <select class="form-select" th:field="*{roomType}" th:errorclass="is-invalid">
                <option value="">-- Chọn --</option>
                <option th:each="t : ${roomTypes}" th:value="${t}" th:text="${t.label}"></option>
            </select>
            <div class="invalid-feedback" th:errors="*{roomType}"></div>
        </div>
        <div class="col-md-4">
            <label class="form-label">Số hàng (A–Z)</label>
            <input type="number" class="form-control" th:field="*{totalRows}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{totalRows}"></div>
        </div>
        <div class="col-md-4">
            <label class="form-label">Ghế mỗi hàng</label>
            <input type="number" class="form-control" th:field="*{seatsPerRow}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{seatsPerRow}"></div>
        </div>
        <div class="col-md-4">
            <label class="form-label">Trạng thái</label>
            <select class="form-select" th:field="*{status}">
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}"></option>
            </select>
        </div>
        <div class="col-12">
            <button class="btn btn-primary">Lưu</button>
            <a class="btn btn-secondary" th:href="@{/admin/rooms}">Huỷ</a>
        </div>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Sức chứa hiển thị bằng `${r.capacity}` (method `getCapacity()` của entity).

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Xoá *Room 03* | Xoá thành công (chưa có suất chiếu) |
| Xoá *Room 01* | Chuyển `MAINTENANCE` |
| Sửa Room 01 thành 5 hàng | "Không thể đổi sơ đồ ghế..." |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#10** đã được assign cho **D**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/10-room-crud` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(room): add room service and admin pages` + `Refs #10`
  - [ ] `feat(room): prevent deleting or resizing rooms with showtimes` + `Refs #10`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(room): room management`** → base `develop`, mô tả theo PR template, ghi `Closes #10`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 22; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Không xoá/đổi sơ đồ phòng đã dùng
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #10 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 23 — CRUD suất chiếu & kiểm tra trùng lịch

**Issue:** #11 · **Người làm:** D · **Nhánh:** `feature/11-showtime-crud`

**Mục tiêu:** Tạo/sửa suất chiếu theo BR-07, BR-08; xoá theo BR-09.

### Các bước

1. `ShowtimeForm`: `startTime`, `endTime` dùng `@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")` — khớp `<input type="datetime-local">`.
2. `ShowtimeServiceImpl.apply()` kiểm tra lần lượt:
   1. Phim không `STOPPED`, phòng `ACTIVE`.
   2. Để trống `endTime` → tự tính `startTime + duration + 15 phút`.
   3. `startTime < endTime` và `endTime ≥ startTime + duration` (BR-07).
   4. Không trùng lịch cùng phòng (Q3, loại trừ chính suất đang sửa) — báo rõ suất bị trùng (BR-08).
3. `update()`: suất đã có đơn → không cho đổi phim/phòng.
4. `delete()`: có đơn → `CANCELLED` + huỷ hàng loạt đơn (Q9) **trong cùng transaction**; chưa có → xoá.
5. `getSeatMap()`, `findUpcomingByMovie()` dùng cho Customer (TODO 27, 28) — cùng file.
6. `AdminShowtimeController`, `admin/showtimes/list.html` (lọc ngày/phim/phòng), `admin/showtimes/form.html`.

### Code

📄 **`src/main/java/com/fu/cinema/dto/ShowtimeForm.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.enums.ShowtimeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShowtimeForm {

    private Long showtimeId;

    @NotNull(message = "Vui lòng chọn phim")
    private Long movieId;

    @NotNull(message = "Vui lòng chọn phòng")
    private Long roomId;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")          // khớp định dạng của <input type="datetime-local">
    private LocalDateTime startTime;

    // Để trống → hệ thống tự tính = StartTime + Duration + 15 phút dọn phòng
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(value = "1000", message = "Giá vé tối thiểu 1.000đ")
    private BigDecimal ticketPrice;

    @NotNull
    private ShowtimeStatus status = ShowtimeStatus.OPEN;
}
```

📄 **`src/main/java/com/fu/cinema/service/impl/ShowtimeServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.RoomShowtimeCount;
import com.fu.cinema.dto.SeatMap;
import com.fu.cinema.dto.ShowtimeForm;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Room;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.enums.MovieStatus;
import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.ShowtimeStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.*;
import com.fu.cinema.service.ShowtimeService;
import com.fu.cinema.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeServiceImpl implements ShowtimeService {

    static final int CLEANING_MINUTES = 15;

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    @Override
    public Page<Showtime> search(Long movieId, Long roomId, LocalDate date, int page, int size) {
        LocalDateTime from = date == null ? null : date.atStartOfDay();
        LocalDateTime to = date == null ? null : date.plusDays(1).atStartOfDay();
        return showtimeRepository.search(movieId, roomId, from, to,
                PageRequest.of(Math.max(page, 0), size, Sort.by("startTime").descending()));
    }

    @Override
    public ShowtimeForm getForm(Long showtimeId) {
        Showtime s = findDetail(showtimeId);
        ShowtimeForm f = new ShowtimeForm();
        f.setShowtimeId(s.getShowtimeId());
        f.setMovieId(s.getMovie().getMovieId());
        f.setRoomId(s.getRoom().getRoomId());
        f.setStartTime(s.getStartTime());
        f.setEndTime(s.getEndTime());
        f.setTicketPrice(s.getTicketPrice());
        f.setStatus(s.getStatus());
        return f;
    }

    @Override
    @Transactional
    public void create(ShowtimeForm form) {
        Showtime showtime = new Showtime();
        apply(form, showtime, null);
        showtimeRepository.save(showtime);
    }

    @Override
    @Transactional
    public void update(Long showtimeId, ShowtimeForm form) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Suất chiếu", showtimeId));
        boolean hasBookings = bookingRepository.existsByShowtime_ShowtimeId(showtimeId);
        if (hasBookings && (!showtime.getMovie().getMovieId().equals(form.getMovieId())
                || !showtime.getRoom().getRoomId().equals(form.getRoomId()))) {
            throw new BusinessException("Suất chiếu đã có người đặt vé: không được đổi phim hoặc phòng");
        }
        apply(form, showtime, showtimeId);
    }

    @Override
    @Transactional
    public boolean delete(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Suất chiếu", showtimeId));
        if (bookingRepository.existsByShowtime_ShowtimeId(showtimeId)) {           // BR-09
            showtime.setStatus(ShowtimeStatus.CANCELLED);
            bookingRepository.cancelAllByShowtime(showtimeId);                      // Q9 — @Modifying
            return false;
        }
        showtimeRepository.delete(showtime);
        return true;
    }

    @Override
    public List<Showtime> findUpcomingByMovie(Long movieId) {
        return showtimeRepository.findByMovie_MovieIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                movieId, ShowtimeStatus.OPEN, LocalDateTime.now());
    }

    @Override
    public SeatMap getSeatMap(Long showtimeId) {
        Showtime s = findDetail(showtimeId);
        List<String> rows = IntStream.range(0, s.getRoom().getTotalRows())
                .mapToObj(i -> String.valueOf((char) ('A' + i)))
                .toList();
        return new SeatMap(s, rows, s.getRoom().getSeatsPerRow(),
                new HashSet<>(ticketRepository.findSoldSeatCodes(showtimeId)));
    }

    @Override
    public List<RoomShowtimeCount> countTodayByRoom() {
        return showtimeRepository.countTodayShowtimesByRoom();
    }

    // ================= helpers =================

    private Showtime findDetail(Long id) {
        return showtimeRepository.findDetailById(id).orElseThrow(() -> new ResourceNotFoundException("Suất chiếu", id));
    }

    /** Kiểm tra BR-07, BR-08 rồi gán dữ liệu từ form vào entity. */
    private void apply(ShowtimeForm form, Showtime showtime, Long excludeId) {
        Movie movie = movieRepository.findById(form.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Phim", form.getMovieId()));
        Room room = roomRepository.findById(form.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Phòng chiếu", form.getRoomId()));

        if (movie.getStatus() == MovieStatus.STOPPED) {
            throw new BusinessException("Phim \"" + movie.getTitle() + "\" đã ngừng chiếu");
        }
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new BusinessException("Phòng \"" + room.getRoomName() + "\" đang bảo trì");
        }

        LocalDateTime start = form.getStartTime();
        LocalDateTime end = form.getEndTime() != null
                ? form.getEndTime()
                : start.plusMinutes(movie.getDuration() + CLEANING_MINUTES);   // gợi ý EndTime
        if (!start.isBefore(end)) {
            throw new BusinessException("Giờ bắt đầu phải trước giờ kết thúc");
        }
        if (end.isBefore(start.plusMinutes(movie.getDuration()))) {
            throw new BusinessException("Giờ kết thúc phải sau giờ bắt đầu ít nhất " + movie.getDuration() + " phút (thời lượng phim)");
        }

        if (form.getStatus() != ShowtimeStatus.CANCELLED) {
            List<Showtime> overlaps = showtimeRepository.findOverlapping(room.getRoomId(), start, end, excludeId);
            if (!overlaps.isEmpty()) {                                                 // BR-08
                Showtime o = overlaps.get(0);
                throw new BusinessException("Trùng lịch với suất \"" + o.getMovie().getTitle() + "\" ("
                        + DateUtils.format(o.getStartTime()) + " – " + DateUtils.format(o.getEndTime())
                        + ") tại " + room.getRoomName());
            }
        }

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(start);
        showtime.setEndTime(end);
        showtime.setTicketPrice(form.getTicketPrice());
        showtime.setStatus(form.getStatus());
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminShowtimeController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.ShowtimeForm;
import com.fu.cinema.enums.ShowtimeStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.RoomService;
import com.fu.cinema.service.ShowtimeService;
import com.fu.cinema.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class AdminShowtimeController {

    private static final String FORM_VIEW = "admin/showtimes/form";

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final RoomService roomService;

    @GetMapping
    public String list(@RequestParam(required = false) Long movieId,
                       @RequestParam(required = false) Long roomId,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("showtimes", showtimeService.search(movieId, roomId, date, page, PageUtils.DEFAULT_SIZE));
        model.addAttribute("movies", movieService.findSchedulable());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("movieId", movieId);
        model.addAttribute("roomId", roomId);
        model.addAttribute("date", date);
        model.addAttribute("pageUrl", PageUtils.currentUrlWithoutPage());
        return "admin/showtimes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        return showForm(new ShowtimeForm(), model);
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") ShowtimeForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return showForm(form, model);
        }
        try {
            showtimeService.create(form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(form, model);
        }
        ra.addFlashAttribute("success", "Đã thêm suất chiếu");
        return "redirect:/admin/showtimes";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return showForm(showtimeService.getForm(id), model);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("form") ShowtimeForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        form.setShowtimeId(id);
        if (br.hasErrors()) {
            return showForm(form, model);
        }
        try {
            showtimeService.update(id, form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(form, model);
        }
        ra.addFlashAttribute("success", "Đã cập nhật suất chiếu");
        return "redirect:/admin/showtimes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        boolean deleted = showtimeService.delete(id);
        ra.addFlashAttribute("success", deleted
                ? "Đã xoá suất chiếu"
                : "Suất chiếu đã có đơn đặt vé: đã huỷ suất chiếu và toàn bộ đơn liên quan");
        return "redirect:/admin/showtimes";
    }

    private String showForm(ShowtimeForm form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("movies", movieService.findSchedulable());
        model.addAttribute("rooms", roomService.findActive());
        model.addAttribute("statuses", ShowtimeStatus.values());
        return FORM_VIEW;
    }
}
```

📄 **`src/main/resources/templates/admin/showtimes/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Suất chiếu')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <div class="d-flex justify-content-between mb-3">
        <h3>Suất chiếu</h3>
        <a class="btn btn-primary" th:href="@{/admin/showtimes/new}">+ Thêm suất chiếu</a>
    </div>
    <form class="row g-2 mb-3" method="get" th:action="@{/admin/showtimes}">
        <div class="col-md-3"><input type="date" class="form-control" name="date" th:value="${date}"></div>
        <div class="col-md-4">
            <select class="form-select" name="movieId">
                <option value="">Tất cả phim</option>
                <option th:each="m : ${movies}" th:value="${m.movieId}" th:text="${m.title}" th:selected="${m.movieId == movieId}"></option>
            </select>
        </div>
        <div class="col-md-3">
            <select class="form-select" name="roomId">
                <option value="">Tất cả phòng</option>
                <option th:each="r : ${rooms}" th:value="${r.roomId}" th:text="${r.roomName}" th:selected="${r.roomId == roomId}"></option>
            </select>
        </div>
        <div class="col-md-2 d-grid"><button class="btn btn-outline-primary">Lọc</button></div>
    </form>
    <table class="table table-hover align-middle">
        <thead class="table-light"><tr><th>Phim</th><th>Phòng</th><th>Bắt đầu</th><th>Kết thúc</th><th>Giá vé</th><th>Trạng thái</th><th></th></tr></thead>
        <tbody>
        <tr th:if="${showtimes.empty}"><td colspan="7" class="text-center text-muted">Không có dữ liệu</td></tr>
        <tr th:each="s : ${showtimes.content}">
            <td th:text="${s.movie.title}"></td>
            <td th:text="${s.room.roomName}"></td>
            <td th:text="${#temporals.format(s.startTime, 'dd/MM/yyyy HH:mm')}"></td>
            <td th:text="${#temporals.format(s.endTime, 'HH:mm')}"></td>
            <td th:text="${#numbers.formatDecimal(s.ticketPrice, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td>
            <td th:text="${s.status}"></td>
            <td class="text-end text-nowrap">
                <a class="btn btn-sm btn-outline-secondary" th:href="@{/admin/showtimes/{id}/edit(id=${s.showtimeId})}">Sửa</a>
                <form class="d-inline" method="post" th:action="@{/admin/showtimes/{id}/delete(id=${s.showtimeId})}"
                      onsubmit="return confirm('Xoá suất chiếu? Nếu đã có đơn, suất chiếu và các đơn sẽ bị huỷ.');">
                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
    <div th:replace="~{fragments/layout :: pagination(${showtimes}, ${pageUrl})}"></div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/admin/showtimes/form.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Suất chiếu')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 720px">
    <h3 th:text="${form.showtimeId == null} ? 'Thêm suất chiếu' : 'Sửa suất chiếu'"></h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:object="${form}" novalidate class="row g-3"
          th:action="${form.showtimeId == null} ? @{/admin/showtimes/new} : @{/admin/showtimes/{id}/edit(id=${form.showtimeId})}">
        <div class="col-md-6">
            <label class="form-label">Phim</label>
            <select class="form-select" th:field="*{movieId}" th:errorclass="is-invalid">
                <option value="">-- Chọn --</option>
                <option th:each="m : ${movies}" th:value="${m.movieId}" th:text="|${m.title} (${m.duration}')|"></option>
            </select>
            <div class="invalid-feedback" th:errors="*{movieId}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Phòng (đang hoạt động)</label>
            <select class="form-select" th:field="*{roomId}" th:errorclass="is-invalid">
                <option value="">-- Chọn --</option>
                <option th:each="r : ${rooms}" th:value="${r.roomId}" th:text="|${r.roomName} (${r.roomType.label})|"></option>
            </select>
            <div class="invalid-feedback" th:errors="*{roomId}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Bắt đầu</label>
            <input type="datetime-local" class="form-control" th:field="*{startTime}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{startTime}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Kết thúc <small class="text-muted">(để trống = thời lượng + 15 phút)</small></label>
            <input type="datetime-local" class="form-control" th:field="*{endTime}">
        </div>
        <div class="col-md-6">
            <label class="form-label">Giá vé (đ)</label>
            <input type="number" class="form-control" th:field="*{ticketPrice}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{ticketPrice}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Trạng thái</label>
            <select class="form-select" th:field="*{status}">
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}"></option>
            </select>
        </div>
        <div class="col-12">
            <button class="btn btn-primary">Lưu</button>
            <a class="btn btn-secondary" th:href="@{/admin/showtimes}">Huỷ</a>
        </div>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Hai khoảng `[s1, e1)` và `[s2, e2)` giao nhau ⇔ `s1 < e2 AND e1 > s2` — đây là điều kiện trong Q3.
- Lọc theo ngày dùng khoảng `[date 00:00, date+1 00:00)` thay vì hàm `CAST` → dùng được index trên `start_time`.
- `DateUtils.format()` để hiển thị thời gian trong thông báo lỗi.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Tạo *Chú Mèo*, Room 01, N+1 10:00 | "Trùng lịch với suất \"Biệt Đội Siêu Tốc\" (… 09:00 – … 11:15) tại Room 01" |
| Tạo suất ở Room 03 | Không có trong dropdown (phòng bảo trì) |
| Kết thúc trước bắt đầu | "Giờ bắt đầu phải trước giờ kết thúc" |
| Xoá suất *Chú Mèo* N+3 10:00 | Xoá thành công (chưa có đơn) |
| Xoá suất *Biệt Đội* N+1 09:00 | Suất `CANCELLED`, 2 đơn của suất chuyển `CANCELLED` |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#11** đã được assign cho **D**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/11-showtime-crud` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(showtime): add overlap check query for room schedule` + `Refs #11`
  - [ ] `feat(showtime): add showtime form with end time suggestion` + `Refs #11`
  - [ ] `feat(showtime): validate time range, room status and movie status` + `Refs #11`
  - [ ] `feat(showtime): cancel showtime and its bookings when deleting` + `Refs #11`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(showtime): showtime scheduling with overlap validation`** → base `develop`, mô tả theo PR template, ghi `Closes #11`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 23; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Loại trừ chính suất đang sửa khi kiểm tra trùng
  - [ ] Huỷ suất + huỷ đơn trong 1 transaction
  - [ ] Báo trước cho E khi thêm method vào `BookingRepository`
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #11 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part H — Admin: Đặt vé & báo cáo

## TODO 24 — Danh sách & chi tiết đơn đặt vé (Admin)

**Issue:** #12 · **Người làm:** E · **Nhánh:** `feature/12-booking-admin`

**Mục tiêu:** Admin lọc đơn theo trạng thái, khoảng ngày, từ khoá khách hàng; xem chi tiết đơn.

### Các bước

1. Cài đặt `BookingServiceImpl` (file đầy đủ — phần **Customer** dùng cho TODO 28–29, phần **Admin** cho TODO 24–25).
2. `search()`: validate `from ≤ to`; đổi ngày thành khoảng `[from 00:00, to+1 00:00)`; gọi Q6.
3. `getDetail()`: Q5 (`JOIN FETCH` khách, suất chiếu, phim, phòng, vé).
4. `AdminBookingController` + `admin/bookings/list.html`, `admin/bookings/detail.html`.

### Code

📄 **`src/main/java/com/fu/cinema/service/impl/BookingServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.BookingRequest;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.entity.Ticket;
import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.enums.ShowtimeStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.BookingRepository;
import com.fu.cinema.repository.CustomerRepository;
import com.fu.cinema.repository.ShowtimeRepository;
import com.fu.cinema.repository.TicketRepository;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    static final int MAX_SEATS_PER_BOOKING = 8;
    static final int CANCEL_BEFORE_HOURS = 2;
    private static final Pattern SEAT_PATTERN = Pattern.compile("^([A-Z])([1-9]\\d?)$");

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;

    // ================= Customer =================

    @Override
    @Transactional                                               // BR-12: Booking + Ticket trong 1 transaction
    public Booking create(Long customerId, BookingRequest request) {
        // 1. Chuẩn hoá & kiểm tra danh sách ghế
        List<String> seats = request.getSeatCodes().stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toUpperCase())
                .distinct()
                .sorted()
                .toList();
        if (seats.isEmpty()) {
            throw new BusinessException("Vui lòng chọn ít nhất 1 ghế");
        }
        if (seats.size() > MAX_SEATS_PER_BOOKING) {
            throw new BusinessException("Mỗi đơn tối đa " + MAX_SEATS_PER_BOOKING + " ghế");
        }

        // 2. Khoá suất chiếu (PESSIMISTIC_WRITE) → các yêu cầu đặt vé cùng suất được xử lý tuần tự
        Showtime showtime = showtimeRepository.findByIdForUpdate(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Suất chiếu", request.getShowtimeId()));
        if (showtime.getStatus() != ShowtimeStatus.OPEN || !showtime.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Suất chiếu không còn mở bán");                       // BR-10
        }

        // 3. Ghế phải tồn tại trong phòng
        int rows = showtime.getRoom().getTotalRows();
        int perRow = showtime.getRoom().getSeatsPerRow();
        for (String seat : seats) {
            Matcher m = SEAT_PATTERN.matcher(seat);
            if (!m.matches() || m.group(1).charAt(0) - 'A' >= rows || Integer.parseInt(m.group(2)) > perRow) {
                throw new BusinessException("Ghế " + seat + " không tồn tại trong phòng " + showtime.getRoom().getRoomName());
            }
        }

        // 4. Ghế chưa được bán (kiểm tra lại TRONG transaction, không tin dữ liệu từ form)
        Set<String> sold = new HashSet<>(ticketRepository.findSoldSeatCodes(showtime.getShowtimeId()));
        List<String> taken = seats.stream().filter(sold::contains).toList();
        if (!taken.isEmpty()) {
            throw new BusinessException("Ghế đã có người đặt: " + String.join(", ", taken));
        }

        // 5. Độ tuổi (BR-11)
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng", customerId));
        int minAge = showtime.getMovie().getAgeRating().getMinAge();
        int ageAtShow = Period.between(customer.getBirthday(), showtime.getStartTime().toLocalDate()).getYears();
        if (ageAtShow < minAge) {
            throw new BusinessException("Phim \"" + showtime.getMovie().getTitle() + "\" chỉ dành cho khán giả từ "
                    + minAge + " tuổi trở lên");
        }

        // 6. Tạo đơn + vé (cascade ALL → lưu Booking là lưu luôn Ticket)
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalAmount(showtime.getTicketPrice().multiply(BigDecimal.valueOf(seats.size())));
        seats.forEach(seat -> booking.addTicket(new Ticket(showtime, seat, showtime.getTicketPrice())));
        return bookingRepository.save(booking);
    }

    @Override
    public Page<Booking> findByCustomer(Long customerId, BookingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by("bookingDate").descending());
        return status == null
                ? bookingRepository.findByCustomer_CustomerId(customerId, pageable)
                : bookingRepository.findByCustomer_CustomerIdAndStatus(customerId, status, pageable);
    }

    @Override
    public Booking getDetailForCustomer(Long bookingId, Long customerId) {
        Booking booking = getDetail(bookingId);
        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            // Không tiết lộ đơn của người khác có tồn tại hay không → trả 404
            throw new ResourceNotFoundException("Đơn đặt vé", bookingId);
        }
        return booking;
    }

    @Override
    @Transactional
    public void cancelByCustomer(Long bookingId, Long customerId) {
        Booking booking = getDetailForCustomer(bookingId, customerId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Đơn đã được huỷ trước đó");
        }
        if (LocalDateTime.now().isAfter(booking.getShowtime().getStartTime().minusHours(CANCEL_BEFORE_HOURS))) {
            throw new BusinessException("Chỉ được huỷ đơn trước giờ chiếu ít nhất " + CANCEL_BEFORE_HOURS + " giờ"); // BR-14
        }
        booking.setStatus(BookingStatus.CANCELLED);
    }

    // ================= Admin =================

    @Override
    public Page<Booking> search(BookingStatus status, LocalDate fromDate, LocalDate toDate, String keyword, int page, int size) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException("Từ ngày phải trước hoặc bằng đến ngày");
        }
        return bookingRepository.search(status,
                fromDate == null ? null : fromDate.atStartOfDay(),
                toDate == null ? null : toDate.plusDays(1).atStartOfDay(),
                ValidationUtils.blankToNull(keyword),
                PageRequest.of(Math.max(page, 0), size, Sort.by("bookingDate").descending()));
    }

    @Override
    public Booking getDetail(Long bookingId) {
        return bookingRepository.findDetailById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn đặt vé", bookingId));
    }

    @Override
    @Transactional
    public void confirm(Long bookingId) {
        Booking booking = getDetail(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {                               // BR-13
            throw new BusinessException("Chỉ xác nhận được đơn đang ở trạng thái PENDING");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public void cancelByAdmin(Long bookingId) {
        Booking booking = getDetail(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Đơn đã được huỷ trước đó");
        }
        booking.setStatus(BookingStatus.CANCELLED);
    }

    @Override
    public long countPending() {
        return bookingRepository.countByStatus(BookingStatus.PENDING);
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminBookingController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public String list(@RequestParam(required = false) BookingStatus status,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                       @RequestParam(required = false) String kw,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("bookings", bookingService.search(status, from, to, kw, page, PageUtils.DEFAULT_SIZE));
        model.addAttribute("statuses", BookingStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("kw", kw);
        model.addAttribute("pageUrl", PageUtils.currentUrlWithoutPage());
        return "admin/bookings/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getDetail(id));
        return "admin/bookings/detail";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.confirm(id);
        ra.addFlashAttribute("success", "Đã xác nhận đơn #" + id);
        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        bookingService.cancelByAdmin(id);
        ra.addFlashAttribute("success", "Đã huỷ đơn #" + id);
        return "redirect:/admin/bookings/" + id;
    }
}
```

📄 **`src/main/resources/templates/admin/bookings/list.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Đơn đặt vé')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <h3 class="mb-3">Đơn đặt vé</h3>
    <form class="row g-2 mb-3" method="get" th:action="@{/admin/bookings}">
        <div class="col-md-2">
            <select class="form-select" name="status">
                <option value="">Trạng thái</option>
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}" th:selected="${s == status}"></option>
            </select>
        </div>
        <div class="col-md-2"><input type="date" class="form-control" name="from" th:value="${from}" title="Từ ngày"></div>
        <div class="col-md-2"><input type="date" class="form-control" name="to" th:value="${to}" title="Đến ngày"></div>
        <div class="col-md-4"><input class="form-control" name="kw" th:value="${kw}" placeholder="Tên, email, SĐT khách hàng"></div>
        <div class="col-md-2 d-grid"><button class="btn btn-outline-primary">Lọc</button></div>
    </form>
    <table class="table table-hover align-middle">
        <thead class="table-light"><tr><th>#</th><th>Ngày đặt</th><th>Khách hàng</th><th>Phim</th><th>Suất chiếu</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead>
        <tbody>
        <tr th:if="${bookings.empty}"><td colspan="8" class="text-center text-muted">Không có dữ liệu</td></tr>
        <tr th:each="b : ${bookings.content}">
            <td th:text="${b.bookingId}"></td>
            <td th:text="${#temporals.format(b.bookingDate, 'dd/MM/yyyy HH:mm')}"></td>
            <td th:text="${b.customer.fullName}"></td>
            <td th:text="${b.showtime.movie.title}"></td>
            <td th:text="${#temporals.format(b.showtime.startTime, 'dd/MM HH:mm')}"></td>
            <td th:text="${#numbers.formatDecimal(b.totalAmount, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td>
            <td><span class="badge" th:text="${b.status}"
                      th:classappend="${b.status.name() == 'CONFIRMED'} ? 'bg-success' : (${b.status.name() == 'PENDING'} ? 'bg-warning text-dark' : 'bg-secondary')"></span></td>
            <td><a class="btn btn-sm btn-outline-primary" th:href="@{/admin/bookings/{id}(id=${b.bookingId})}">Chi tiết</a></td>
        </tr>
        </tbody>
    </table>
    <div th:replace="~{fragments/layout :: pagination(${bookings}, ${pageUrl})}"></div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/admin/bookings/detail.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Chi tiết đơn')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 820px">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <h3 th:text="|Đơn đặt vé #${booking.bookingId}|"></h3>
    <table class="table">
        <tr><th>Trạng thái</th><td th:text="${booking.status}"></td></tr>
        <tr><th>Ngày đặt</th><td th:text="${#temporals.format(booking.bookingDate, 'dd/MM/yyyy HH:mm')}"></td></tr>
        <tr><th>Khách hàng</th><td th:text="|${booking.customer.fullName} · ${booking.customer.email} · ${booking.customer.mobile}|"></td></tr>
        <tr><th>Phim</th><td th:text="${booking.showtime.movie.title}"></td></tr>
        <tr><th>Suất chiếu</th><td th:text="|${#temporals.format(booking.showtime.startTime, 'dd/MM/yyyy HH:mm')} · ${booking.showtime.room.roomName}|"></td></tr>
        <tr><th>Ghế</th><td><span class="badge bg-dark me-1" th:each="t : ${booking.tickets}" th:text="${t.seatCode}"></span></td></tr>
        <tr><th>Tổng tiền</th><td class="fw-bold" th:text="${#numbers.formatDecimal(booking.totalAmount, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td></tr>
    </table>
    <div class="d-flex gap-2">
        <form th:if="${booking.status.name() == 'PENDING'}" method="post" th:action="@{/admin/bookings/{id}/confirm(id=${booking.bookingId})}">
            <button class="btn btn-success">Xác nhận</button>
        </form>
        <form th:if="${booking.status.name() != 'CANCELLED'}" method="post" th:action="@{/admin/bookings/{id}/cancel(id=${booking.bookingId})}"
              onsubmit="return confirm('Huỷ đơn này?');">
            <button class="btn btn-outline-danger">Huỷ đơn</button>
        </form>
        <a class="btn btn-secondary" th:href="@{/admin/bookings}">Quay lại</a>
    </div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Có thể làm **trước** khi D xong chức năng đặt vé — dữ liệu đơn lấy từ seed.
- Lọc ngày bằng `@DateTimeFormat(iso = DATE)` trên `@RequestParam LocalDate`.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Lọc `PENDING` | 2 đơn của An |
| Lọc từ khoá `binh` | 2 đơn của Bình |
| Từ ngày > đến ngày | "Từ ngày phải trước hoặc bằng đến ngày" |
| Mở chi tiết đơn | Đủ khách, phim, phòng, ghế, tổng tiền — không lỗi lazy |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#12** đã được assign cho **E**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/12-booking-admin` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(booking): add admin booking filter query` + `Refs #12`
  - [ ] `feat(booking): add booking detail with join fetch` + `Refs #12`
  - [ ] `feat(booking): add admin booking list and detail pages` + `Refs #12`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push nhánh (có thể mở **Draft PR** để nhóm theo dõi); PR chính thức mở ở TODO cuối của issue #12.

## TODO 25 — Xác nhận / huỷ đơn

**Issue:** #12 · **Người làm:** E · **Nhánh:** `feature/12-booking-admin`

**Mục tiêu:** Chuyển trạng thái đơn theo BR-13.

### Các bước

1. `BookingServiceImpl.confirm()`: chỉ `PENDING → CONFIRMED`.
2. `cancelByAdmin()`: mọi trạng thái trừ `CANCELLED` → `CANCELLED`.
3. Nút trên trang chi tiết hiển thị theo trạng thái (`th:if`), gửi bằng POST.

### Code

Code dùng cho TODO này đã có ở các file trước:

- `BookingServiceImpl.confirm()`, `cancelByAdmin()` — TODO 24
- `AdminBookingController.confirm()`, `cancel()` — TODO 24
- `admin/bookings/detail.html` — TODO 24

### Giải thích & lưu ý

- Ẩn nút trên giao diện **không đủ**: service vẫn phải kiểm tra trạng thái (người dùng có thể gửi POST trực tiếp).

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Xác nhận đơn PENDING của An | Trạng thái `CONFIRMED` |
| Gửi lại POST xác nhận (F5 / công cụ) | "Chỉ xác nhận được đơn đang ở trạng thái PENDING" |
| Huỷ đơn đã huỷ | "Đơn đã được huỷ trước đó" |

### ✅ Checklist làm việc nhóm

- [ ] Tiếp tục trên nhánh `feature/12-booking-admin` (issue #12); đã `git rebase origin/develop` nếu develop có thay đổi.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(booking): confirm and cancel booking with status rules` + `Refs #12`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(booking): admin booking management`** → base `develop`, mô tả theo PR template, ghi `Closes #12`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 24, 25; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Chi tiết đơn dùng JOIN FETCH
  - [ ] Lọc kết hợp nhiều tiêu chí
  - [ ] Luồng trạng thái nằm ở service
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #12 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 26 — Báo cáo doanh thu & Dashboard

**Issue:** #17 · **Người làm:** E · **Nhánh:** `feature/17-revenue-report`

**Mục tiêu:** Báo cáo StartDate → EndDate (BR-15): doanh thu theo phim (giảm dần), danh sách đơn (giảm dần), dòng tổng. Kèm dashboard Admin.

### Các bước

1. `ReportResult` (kết quả báo cáo), `ReportServiceImpl.generate()`:
   - Validate ngày; đổi thành `[startDate 00:00, endDate+1 00:00)` để **bao gồm trọn ngày cuối**.
   - Q7 → doanh thu theo phim; Q8 → danh sách đơn `CONFIRMED`; tính tổng bằng Stream.
2. `AdminReportController`: mặc định từ đầu tháng đến hôm nay; lỗi ngày → hiện thông báo trên trang.
3. `admin/report.html`: form chọn ngày (GET), 3 thẻ tổng, 2 bảng.
4. Dashboard: `DashboardStats`, `AdminDashboardController`, `admin/dashboard.html` (dùng Q11 native).

### Code

📄 **`src/main/java/com/fu/cinema/dto/ReportResult.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.entity.Booking;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Value
public class ReportResult {

    LocalDate startDate;
    LocalDate endDate;
    List<MovieRevenueDTO> revenueByMovie;
    List<Booking> bookings;
    long totalBookings;
    long totalTickets;
    BigDecimal totalRevenue;

    public boolean isEmpty() {
        return bookings.isEmpty();
    }
}
```

📄 **`src/main/java/com/fu/cinema/service/impl/ReportServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.MovieRevenueDTO;
import com.fu.cinema.dto.ReportResult;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.repository.BookingRepository;
import com.fu.cinema.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;

    @Override
    public ReportResult generate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Vui lòng chọn đầy đủ từ ngày và đến ngày");
        }
        if (startDate.isAfter(endDate)) {                                                 // BR-15
            throw new BusinessException("Từ ngày phải trước hoặc bằng đến ngày");
        }
        // [startDate 00:00, endDate + 1 ngày 00:00) → bao gồm trọn ngày cuối
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.plusDays(1).atStartOfDay();

        List<MovieRevenueDTO> byMovie = bookingRepository.revenueByMovie(from, to);        // giảm dần doanh thu
        List<Booking> bookings = bookingRepository.findConfirmedInPeriod(from, to);       // giảm dần ngày đặt

        long totalTickets = byMovie.stream().mapToLong(MovieRevenueDTO::getTicketCount).sum();
        BigDecimal totalRevenue = byMovie.stream().map(MovieRevenueDTO::getRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResult(startDate, endDate, byMovie, bookings, bookings.size(), totalTickets, totalRevenue);
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminReportController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping
    public String report(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         Model model) {
        // Mặc định: từ đầu tháng đến hôm nay
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        try {
            model.addAttribute("report", reportService.generate(start, end));
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "admin/report";
    }
}
```

📄 **`src/main/resources/templates/admin/report.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Báo cáo')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <h3 class="mb-3">Báo cáo doanh thu bán vé</h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form class="row g-2 mb-4" method="get" th:action="@{/admin/reports}">
        <div class="col-md-4"><label class="form-label">Từ ngày</label>
            <input type="date" class="form-control" name="startDate" th:value="${startDate}"></div>
        <div class="col-md-4"><label class="form-label">Đến ngày</label>
            <input type="date" class="form-control" name="endDate" th:value="${endDate}"></div>
        <div class="col-md-4 d-grid align-self-end"><button class="btn btn-primary">Xem báo cáo</button></div>
    </form>

    <th:block th:if="${report != null}">
        <p th:if="${report.empty}" class="text-muted">Không có dữ liệu trong khoảng thời gian đã chọn.</p>
        <th:block th:unless="${report.empty}">
            <div class="row g-3 mb-4">
                <div class="col-md-4"><div class="card card-body"><div class="text-muted">Tổng số đơn</div>
                    <div class="fs-3 fw-bold" th:text="${report.totalBookings}"></div></div></div>
                <div class="col-md-4"><div class="card card-body"><div class="text-muted">Tổng số vé</div>
                    <div class="fs-3 fw-bold" th:text="${report.totalTickets}"></div></div></div>
                <div class="col-md-4"><div class="card card-body"><div class="text-muted">Tổng doanh thu</div>
                    <div class="fs-3 fw-bold" th:text="${#numbers.formatDecimal(report.totalRevenue, 0, 'COMMA', 0, 'POINT')} + 'đ'"></div></div></div>
            </div>

            <h5>(a) Doanh thu theo phim (giảm dần)</h5>
            <table class="table table-sm">
                <thead class="table-light"><tr><th>#</th><th>Phim</th><th>Số vé</th><th>Doanh thu</th></tr></thead>
                <tbody><tr th:each="r, st : ${report.revenueByMovie}">
                    <td th:text="${st.count}"></td>
                    <td th:text="${r.title}"></td>
                    <td th:text="${r.ticketCount}"></td>
                    <td th:text="${#numbers.formatDecimal(r.revenue, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td>
                </tr></tbody>
            </table>

            <h5 class="mt-4">(b) Đơn đã xác nhận trong kỳ (mới nhất trước)</h5>
            <table class="table table-sm">
                <thead class="table-light"><tr><th>#</th><th>Ngày đặt</th><th>Khách hàng</th><th>Phim</th><th>Tổng tiền</th></tr></thead>
                <tbody><tr th:each="b : ${report.bookings}">
                    <td><a th:href="@{/admin/bookings/{id}(id=${b.bookingId})}" th:text="${b.bookingId}"></a></td>
                    <td th:text="${#temporals.format(b.bookingDate, 'dd/MM/yyyy HH:mm')}"></td>
                    <td th:text="${b.customer.fullName}"></td>
                    <td th:text="${b.showtime.movie.title}"></td>
                    <td th:text="${#numbers.formatDecimal(b.totalAmount, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td>
                </tr></tbody>
            </table>
        </th:block>
    </th:block>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/java/com/fu/cinema/dto/DashboardStats.java`**

```java
package com.fu.cinema.dto;

import lombok.Value;

import java.util.List;

@Value
public class DashboardStats {

    long showingMovies;
    long customers;
    long pendingBookings;
    List<RoomShowtimeCount> todayShowtimesByRoom;
}
```

📄 **`src/main/java/com/fu/cinema/controller/admin/AdminDashboardController.java`**

```java
package com.fu.cinema.controller.admin;

import com.fu.cinema.dto.DashboardStats;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final MovieService movieService;
    private final CustomerService customerService;
    private final BookingService bookingService;
    private final ShowtimeService showtimeService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", new DashboardStats(
                movieService.countShowing(),
                customerService.count(),
                bookingService.countPending(),
                showtimeService.countTodayByRoom()));
        return "admin/dashboard";
    }
}
```

📄 **`src/main/resources/templates/admin/dashboard.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Dashboard')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <h3 class="mb-4">Dashboard</h3>
    <div class="row g-3 mb-4">
        <div class="col-md-4"><div class="card card-body"><div class="text-muted">Phim đang chiếu</div>
            <div class="fs-2 fw-bold" th:text="${stats.showingMovies}"></div></div></div>
        <div class="col-md-4"><div class="card card-body"><div class="text-muted">Khách hàng</div>
            <div class="fs-2 fw-bold" th:text="${stats.customers}"></div></div></div>
        <div class="col-md-4"><div class="card card-body"><div class="text-muted">Đơn chờ xác nhận</div>
            <div class="fs-2 fw-bold" th:text="${stats.pendingBookings}"></div>
            <a th:href="@{/admin/bookings(status='PENDING')}">Xem đơn →</a></div></div>
    </div>
    <h5>Suất chiếu hôm nay theo phòng</h5>
    <table class="table table-sm w-auto">
        <thead><tr><th>Phòng</th><th>Số suất</th></tr></thead>
        <tbody><tr th:each="r : ${stats.todayShowtimesByRoom}">
            <td th:text="${r.roomName}"></td><td th:text="${r.showtimeCount}"></td></tr></tbody>
    </table>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Lỗi hay gặp: dùng `bookingDate <= endDate` với `endDate` là ngày (00:00) → **mất dữ liệu cả ngày cuối**. Luôn dùng `< endDate + 1 ngày`.
- Báo cáo dùng GET → URL `/admin/reports?startDate=...&endDate=...` có thể lưu lại/gửi cho người khác.

### Kiểm thử

| Thao tác (N = hôm nay) | Kết quả mong đợi |
|---|---|
| Từ N−7 đến N | Chú Mèo Phiêu Lưu 3 vé 210.000đ → Huyền Thoại Cũ 2 vé 140.000đ → Biệt Đội Siêu Tốc 1 vé 90.000đ; tổng 3 đơn, 6 vé, 440.000đ; danh sách đơn: Bình (N−1), An (N−4), An (N−5) |
| Từ N−1 đến N−1 | Chỉ đơn của Bình (bao gồm trọn ngày N−1) |
| Từ N+1 đến N | "Từ ngày phải trước hoặc bằng đến ngày" |
| Khoảng không có đơn | "Không có dữ liệu..." |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#17** đã được assign cho **E**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/17-revenue-report` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(report): add revenue by movie query with dto projection` + `Refs #17`
  - [ ] `feat(report): add confirmed bookings in period query` + `Refs #17`
  - [ ] `feat(report): add report page with date range filter and totals` + `Refs #17`
  - [ ] `feat(report): add admin dashboard statistics` + `Refs #17`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(report): revenue report by date range`** → base `develop`, mô tả theo PR template, ghi `Closes #17`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 26; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Chỉ tính `CONFIRMED`
  - [ ] Bao gồm trọn ngày cuối
  - [ ] Sắp xếp giảm dần
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #17 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part I — Customer

## TODO 27 — Trang chủ & chi tiết phim

**Issue:** #13 · **Người làm:** C · **Nhánh:** `feature/13-home-movie-detail`

**Mục tiêu:** Khách xem phim đang/sắp chiếu, tìm & lọc; chi tiết phim có lịch chiếu nhóm theo ngày.

### Các bước

1. `HomeController`:
   - `GET /` và `/movies`: `movieService.searchForCustomer()` (ẩn `STOPPED`), 12 phim/trang.
   - `GET /movies/{id}`: phim + suất chiếu `OPEN` sắp tới (nhóm theo ngày bằng `Collectors.groupingBy` với `LinkedHashMap` giữ thứ tự) + điểm đánh giá + danh sách đánh giá (TODO 31).
2. `index.html` (lưới thẻ phim), `movie-detail.html` (lịch chiếu, nút giờ chiếu → `/customer/showtimes/{id}/seats`).

### Code

📄 **`src/main/java/com/fu/cinema/controller/HomeController.java`**

```java
package com.fu.cinema.controller;

import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Showtime;
import com.fu.cinema.service.GenreService;
import com.fu.cinema.service.MovieService;
import com.fu.cinema.service.ReviewService;
import com.fu.cinema.service.ShowtimeService;
import com.fu.cinema.util.PageUtils;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MovieService movieService;
    private final GenreService genreService;
    private final ShowtimeService showtimeService;
    private final ReviewService reviewService;

    @GetMapping({"/", "/movies"})
    public String home(@RequestParam(required = false) String kw,
                       @RequestParam(required = false) Long genreId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<Movie> movies = movieService.searchForCustomer(kw, genreId, page, 12);
        model.addAttribute("movies", movies);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("kw", kw);
        model.addAttribute("genreId", genreId);
        model.addAttribute("pageUrl", PageUtils.currentUrlWithoutPage());
        return "index";
    }

    @GetMapping("/movies/{id}")
    public String movieDetail(@PathVariable Long id,
                              HttpServletRequest request,
                              Model model) {
        model.addAttribute("movie", movieService.getById(id));

        // Suất chiếu sắp tới, nhóm theo ngày (giữ thứ tự thời gian)
        Map<LocalDate, List<Showtime>> byDate = showtimeService.findUpcomingByMovie(id).stream()
                .collect(Collectors.groupingBy(s -> s.getStartTime().toLocalDate(), LinkedHashMap::new, Collectors.toList()));
        model.addAttribute("showtimesByDate", byDate);

        model.addAttribute("rating", reviewService.summarize(id));
        model.addAttribute("reviews", reviewService.findByMovie(id));
        SessionUser user = SessionUtils.getCurrentUser(request.getSession(false));
        Long customerId = user == null ? null : user.getCustomerId();
        boolean canReview = reviewService.canReview(customerId, id);
        model.addAttribute("canReview", canReview);
        if (canReview) {
            model.addAttribute("reviewForm", reviewService.getForm(customerId, id));
        }
        return "movie-detail";
    }
}
```

📄 **`src/main/resources/templates/index.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Phim')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>

    <form class="row g-2 mb-4" th:action="@{/movies}" method="get">
        <div class="col-md-6">
            <input class="form-control" name="kw" th:value="${kw}" placeholder="Tìm tên phim...">
        </div>
        <div class="col-md-4">
            <select class="form-select" name="genreId">
                <option value="">-- Tất cả thể loại --</option>
                <option th:each="g : ${genres}" th:value="${g.genreId}" th:text="${g.genreName}"
                        th:selected="${g.genreId == genreId}"></option>
            </select>
        </div>
        <div class="col-md-2 d-grid"><button class="btn btn-primary">Tìm</button></div>
    </form>

    <p th:if="${movies.empty}" class="text-muted">Không tìm thấy phim phù hợp.</p>

    <div class="row row-cols-2 row-cols-md-4 g-4">
        <div class="col" th:each="m : ${movies.content}">
            <div class="card h-100">
                <img class="card-img-top poster" th:src="${m.posterUrl}" th:alt="${m.title}">
                <div class="card-body">
                    <span class="badge"
                          th:classappend="${m.status.name() == 'SHOWING'} ? 'bg-success' : 'bg-secondary'"
                          th:text="${m.status.name() == 'SHOWING'} ? 'Đang chiếu' : 'Sắp chiếu'"></span>
                    <span class="badge bg-danger" th:text="${m.ageRating}"></span>
                    <h6 class="card-title mt-2" th:text="${m.title}"></h6>
                    <p class="small text-muted mb-0" th:text="|${m.genre.genreName} · ${m.duration} phút|"></p>
                </div>
                <div class="card-footer bg-white border-0">
                    <a class="btn btn-outline-primary btn-sm w-100" th:href="@{/movies/{id}(id=${m.movieId})}">Chi tiết & đặt vé</a>
                </div>
            </div>
        </div>
    </div>

    <div class="mt-4" th:replace="~{fragments/layout :: pagination(${movies}, ${pageUrl})}"></div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/movie-detail.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head(${movie.title})}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>

    <div class="row g-4">
        <div class="col-md-3"><img class="poster rounded" th:src="${movie.posterUrl}" th:alt="${movie.title}"></div>
        <div class="col-md-9">
            <h2 th:text="${movie.title}"></h2>
            <p>
                <span class="badge bg-danger" th:text="${movie.ageRating}"></span>
                <span class="badge bg-info text-dark" th:text="${movie.genre.genreName}"></span>
                <span class="text-muted" th:text="|${movie.duration} phút · Khởi chiếu ${#temporals.format(movie.releaseDate, 'dd/MM/yyyy')}|"></span>
            </p>
            <p><strong>Đạo diễn:</strong> <span th:text="${movie.director}"></span></p>
            <p th:text="${movie.description}"></p>
            <p>
                <strong>Đánh giá:</strong>
                <span th:if="${rating.total > 0}"
                      th:text="|${#numbers.formatDecimal(rating.average, 1, 1)}/5 ⭐ (${rating.total} lượt)|"></span>
                <span th:if="${rating.total == 0}" class="text-muted">Chưa có đánh giá</span>
            </p>
        </div>
    </div>

    <h4 class="mt-4">Lịch chiếu</h4>
    <p th:if="${showtimesByDate.isEmpty()}" class="text-muted">Chưa có suất chiếu.</p>
    <div th:each="entry : ${showtimesByDate}" class="mb-3">
        <h6 th:text="${#temporals.format(entry.key, 'EEEE, dd/MM/yyyy')}"></h6>
        <a th:each="s : ${entry.value}" class="btn btn-outline-success me-2 mb-2"
           th:href="@{/customer/showtimes/{id}/seats(id=${s.showtimeId})}"
           th:text="|${#temporals.format(s.startTime, 'HH:mm')} · ${s.room.roomName}|"></a>
    </div>

    <h4 class="mt-4">Đánh giá của khán giả</h4>
    <form th:if="${canReview}" th:action="@{/customer/movies/{id}/review(id=${movie.movieId})}"
          th:object="${reviewForm}" method="post" class="card card-body mb-3">
        <div class="row g-2">
            <div class="col-md-2">
                <select class="form-select" th:field="*{rating}">
                    <option th:each="i : ${#numbers.sequence(5, 1, -1)}" th:value="${i}" th:text="|${i} ⭐|"></option>
                </select>
            </div>
            <div class="col-md-8"><input class="form-control" th:field="*{comment}" placeholder="Nhận xét của bạn..."></div>
            <div class="col-md-2 d-grid"><button class="btn btn-primary">Gửi đánh giá</button></div>
        </div>
    </form>
    <div th:each="r : ${reviews}" class="border-bottom py-2">
        <strong th:text="${r.customer.fullName}"></strong>
        <span th:text="|${r.rating} ⭐|"></span>
        <small class="text-muted" th:text="${#temporals.format(r.reviewDate, 'dd/MM/yyyy')}"></small>
        <div th:text="${r.comment}"></div>
    </div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Nút giờ chiếu trỏ tới `/customer/...` → chưa đăng nhập sẽ bị interceptor chuyển về `/login` (đúng tiêu chí chấp nhận).
- Truy cập trực tiếp `/movies/{id}` của phim `STOPPED` vẫn xem được thông tin (không có suất chiếu mới) — phù hợp với đường link cũ đã chia sẻ.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| Trang chủ | 5 phim, **không** có *Huyền Thoại Cũ* |
| Tìm `mèo` | Chú Mèo Phiêu Lưu |
| Chi tiết *Biệt Đội Siêu Tốc* | Lịch chiếu N+1 09:00 (Room 01), N+2 18:30 (Room 02) |
| Chi tiết *Ngôi Nhà Bí Ẩn* | Chỉ N+1 21:00 (suất N+3 đã huỷ không hiện) |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#13** đã được assign cho **C**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/13-home-movie-detail` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(movie): add home page with showing and coming soon movies` + `Refs #13`
  - [ ] `feat(movie): add movie detail with upcoming showtimes` + `Refs #13`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(movie): home page and movie detail`** → base `develop`, mô tả theo PR template, ghi `Closes #13`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 27; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Ẩn phim STOPPED và suất huỷ/đã qua
  - [ ] Khách chưa đăng nhập xem được
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #13 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 28 — Đặt vé chọn ghế

**Issue:** #14 · **Người làm:** D · **Nhánh:** `feature/14-seat-booking`

**Mục tiêu:** Khách chọn một hoặc nhiều ghế và đặt vé theo BR-10, BR-11, BR-12.

### Các bước

1. `SeatMap` (suất chiếu, nhãn hàng A..., số ghế mỗi hàng, tập ghế đã bán) — tạo bởi `ShowtimeServiceImpl.getSeatMap()` (TODO 23).
2. `BookingRequest` (`showtimeId`, `List<String> seatCodes` — `@NotEmpty`, `@Size(max = 8)`).
3. `BookingServiceImpl.create()` (TODO 24) — đọc kỹ 6 bước trong code:
   1. Chuẩn hoá, bỏ trùng danh sách ghế; 1–8 ghế.
   2. **Khoá suất chiếu** bằng `findByIdForUpdate` (`PESSIMISTIC_WRITE`) → 2 người đặt cùng lúc được xử lý tuần tự.
   3. Suất `OPEN` và chưa bắt đầu.
   4. Ghế tồn tại trong phòng (regex `^[A-Z][1-9]\d?$` + giới hạn hàng/cột).
   5. Ghế chưa bán — **kiểm tra lại trong transaction** (Q4), không tin dữ liệu từ form.
   6. Đủ tuổi theo `AgeRating` tại ngày chiếu (BR-11).
   7. Tạo `Booking` (PENDING) + `Ticket` (cascade), **tổng tiền tính ở server**.
4. `CustomerBookingController`: `GET /customer/showtimes/{id}/seats`, `POST /customer/bookings` (file đầy đủ, cũng dùng cho TODO 29).
5. `customer/seats.html`: mỗi ghế là checkbox `name="seatCodes"`; ghế đã bán `disabled`; JS chỉ để hiện tạm tính.

### Code

📄 **`src/main/java/com/fu/cinema/dto/SeatMap.java`**

```java
package com.fu.cinema.dto;

import com.fu.cinema.entity.Showtime;
import lombok.Value;

import java.util.List;
import java.util.Set;

// Dữ liệu cho màn hình chọn ghế
@Value
public class SeatMap {

    Showtime showtime;
    List<String> rowLabels;   // A, B, C...
    int seatsPerRow;
    Set<String> soldSeats;

    public boolean isSold(String seatCode) {
        return soldSeats.contains(seatCode);
    }
}
```

📄 **`src/main/java/com/fu/cinema/dto/BookingRequest.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookingRequest {

    @NotNull
    private Long showtimeId;

    @NotEmpty(message = "Vui lòng chọn ít nhất 1 ghế")
    @Size(max = 8, message = "Mỗi đơn tối đa 8 ghế")
    private List<String> seatCodes = new ArrayList<>();
}
```

📄 **`src/main/java/com/fu/cinema/controller/customer/CustomerBookingController.java`**

```java
package com.fu.cinema.controller.customer;

import com.fu.cinema.dto.BookingRequest;
import com.fu.cinema.entity.Booking;
import com.fu.cinema.enums.BookingStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.BookingService;
import com.fu.cinema.service.ShowtimeService;
import com.fu.cinema.util.PageUtils;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerBookingController {

    private final ShowtimeService showtimeService;
    private final BookingService bookingService;

    @GetMapping("/showtimes/{id}/seats")
    public String seats(@PathVariable Long id, Model model) {
        model.addAttribute("seatMap", showtimeService.getSeatMap(id));
        return "customer/seats";
    }

    @PostMapping("/bookings")
    public String book(@Valid @ModelAttribute BookingRequest request, BindingResult br,
                       HttpSession session, RedirectAttributes ra) {
        String back = "redirect:/customer/showtimes/" + request.getShowtimeId() + "/seats";
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return back;
        }
        try {
            Booking booking = bookingService.create(SessionUtils.getCurrentCustomerId(session), request);
            ra.addFlashAttribute("success", "Đặt vé thành công! Mã đơn #" + booking.getBookingId());
            return "redirect:/customer/bookings/" + booking.getBookingId();
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return back;
        }
    }

    @GetMapping("/bookings")
    public String history(@RequestParam(required = false) BookingStatus status,
                          @RequestParam(defaultValue = "0") int page,
                          HttpSession session, Model model) {
        model.addAttribute("bookings", bookingService.findByCustomer(
                SessionUtils.getCurrentCustomerId(session), status, page, PageUtils.DEFAULT_SIZE));
        model.addAttribute("statuses", BookingStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("pageUrl", PageUtils.currentUrlWithoutPage());
        return "customer/bookings";
    }

    @GetMapping("/bookings/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("booking", bookingService.getDetailForCustomer(id, SessionUtils.getCurrentCustomerId(session)));
        return "customer/booking-detail";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        try {
            bookingService.cancelByCustomer(id, SessionUtils.getCurrentCustomerId(session));
            ra.addFlashAttribute("success", "Đã huỷ đơn #" + id);
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/bookings/" + id;
    }
}
```

📄 **`src/main/resources/templates/customer/seats.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Chọn ghế')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" th:with="s=${seatMap.showtime}">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <h3 th:text="${s.movie.title}"></h3>
    <p class="text-muted"
       th:text="|${#temporals.format(s.startTime, 'dd/MM/yyyy HH:mm')} · ${s.room.roomName} (${s.room.roomType.label}) · ${#numbers.formatDecimal(s.ticketPrice, 0, 'COMMA', 0, 'POINT')}đ/vé|"></p>

    <form method="post" th:action="@{/customer/bookings}">
        <input type="hidden" name="showtimeId" th:value="${s.showtimeId}">
        <div class="screen py-1">MÀN HÌNH</div>
        <div class="seat-grid">
            <div class="seat-row" th:each="row : ${seatMap.rowLabels}">
                <span class="row-label" th:text="${row}"></span>
                <span th:each="n : ${#numbers.sequence(1, seatMap.seatsPerRow)}"
                      th:with="code=${row + n}"
                      class="seat" th:classappend="${seatMap.isSold(code)} ? 'sold'">
                    <input type="checkbox" name="seatCodes" th:id="${code}" th:value="${code}"
                           th:disabled="${seatMap.isSold(code)}">
                    <label th:for="${code}" th:text="${code}"></label>
                </span>
            </div>
        </div>
        <p class="mt-3 small text-muted">Ô xám: ghế đã bán · Tối đa 8 ghế mỗi đơn</p>
        <p>Đã chọn: <strong id="count">0</strong> ghế · Tạm tính: <strong id="total">0</strong>đ</p>
        <button class="btn btn-success">Đặt vé</button>
        <a class="btn btn-secondary" th:href="@{/movies/{id}(id=${s.movie.movieId})}">Quay lại</a>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
<!-- Chỉ để hiển thị tạm tính; tổng tiền thật được TÍNH LẠI ở server -->
<script th:inline="javascript">
    const price = /*[[${seatMap.showtime.ticketPrice}]]*/ 0;
    document.querySelectorAll('input[name=seatCodes]').forEach(cb => cb.addEventListener('change', () => {
        const n = document.querySelectorAll('input[name=seatCodes]:checked').length;
        document.getElementById('count').textContent = n;
        document.getElementById('total').textContent = (n * price).toLocaleString('vi-VN');
    }));
</script>
</body>
</html>
```

### Giải thích & lưu ý

- **Không bao giờ** dùng giá/tổng tiền từ form — người dùng sửa được HTML.
- `customerId` lấy từ **session** (`SessionUtils.getCurrentCustomerId`), không nhận từ form.
- Nếu muốn chặn trùng ghế ở mức DB: có thể thêm cột trạng thái vào `Ticket` và unique index có điều kiện (`WHERE status = 'ACTIVE'`) trên SQL Server — nâng cao, không bắt buộc.

### Kiểm thử

| Thao tác (đăng nhập An trừ khi ghi khác) | Kết quả mong đợi |
|---|---|
| Mở ghế suất *Biệt Đội* N+1 09:00 | E5, E6 xám; F1 **trống** (đơn huỷ) |
| Chọn F1, F2 → Đặt vé | Đơn mới PENDING, 160.000đ, chuyển tới chi tiết đơn |
| Mở 2 tab, cùng chọn G1, đặt lần lượt | Tab 2: "Ghế đã có người đặt: G1" |
| Chọn 9 ghế | "Mỗi đơn tối đa 8 ghế" |
| Không chọn ghế | "Vui lòng chọn ít nhất 1 ghế" |
| Đăng nhập **Bình** (15 tuổi), đặt *Ngôi Nhà Bí Ẩn* (T18) | "...chỉ dành cho khán giả từ 18 tuổi trở lên" |
| Sửa HTML gửi ghế `Z99` | "Ghế Z99 không tồn tại..." |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#14** đã được assign cho **D**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/14-seat-booking` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(booking): add sold seats query for showtime` + `Refs #14`
  - [ ] `feat(booking): render seat map for showtime` + `Refs #14`
  - [ ] `feat(booking): create booking with multiple tickets in one transaction` + `Refs #14`
  - [ ] `feat(booking): validate seat availability, showtime status and age rating` + `Refs #14`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(booking): online seat selection and booking`** → base `develop`, mô tả theo PR template, ghi `Closes #14`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 28; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Toàn bộ BR-10 → BR-12
  - [ ] Kiểm tra ghế trống trong transaction + khoá suất chiếu
  - [ ] Tổng tiền tính ở server
  - [ ] customerId lấy từ session
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #14 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 29 — Lịch sử đặt vé & huỷ đơn

**Issue:** #15 · **Người làm:** E · **Nhánh:** `feature/15-booking-history`

**Mục tiêu:** Khách xem đơn của mình, lọc theo trạng thái, xem chi tiết, huỷ theo BR-14.

### Các bước

1. `BookingServiceImpl` (TODO 24): `findByCustomer()` (derived + `@EntityGraph`), `getDetailForCustomer()` (kiểm tra chủ sở hữu → không phải thì **404**), `cancelByCustomer()` (trước giờ chiếu ≥ 2 giờ).
2. `CustomerBookingController` (TODO 28): `GET /customer/bookings`, `GET /customer/bookings/{id}`, `POST /customer/bookings/{id}/cancel`.
3. `customer/bookings.html`, `customer/booking-detail.html`.

### Code

📄 **`src/main/resources/templates/customer/bookings.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Vé của tôi')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <h3 class="mb-3">Lịch sử đặt vé</h3>
    <form class="row g-2 mb-3" method="get" th:action="@{/customer/bookings}">
        <div class="col-md-3">
            <select class="form-select" name="status" onchange="this.form.submit()">
                <option value="">Tất cả trạng thái</option>
                <option th:each="st : ${statuses}" th:value="${st}" th:text="${st}" th:selected="${st == status}"></option>
            </select>
        </div>
    </form>
    <table class="table table-hover align-middle">
        <thead class="table-light"><tr><th>#</th><th>Ngày đặt</th><th>Phim</th><th>Suất chiếu</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead>
        <tbody>
        <tr th:if="${bookings.empty}"><td colspan="7" class="text-center text-muted">Bạn chưa có đơn đặt vé nào</td></tr>
        <tr th:each="b : ${bookings.content}">
            <td th:text="${b.bookingId}"></td>
            <td th:text="${#temporals.format(b.bookingDate, 'dd/MM/yyyy HH:mm')}"></td>
            <td th:text="${b.showtime.movie.title}"></td>
            <td th:text="|${#temporals.format(b.showtime.startTime, 'dd/MM/yyyy HH:mm')} · ${b.showtime.room.roomName}|"></td>
            <td th:text="${#numbers.formatDecimal(b.totalAmount, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td>
            <td th:text="${b.status}"></td>
            <td><a class="btn btn-sm btn-outline-primary" th:href="@{/customer/bookings/{id}(id=${b.bookingId})}">Chi tiết</a></td>
        </tr>
        </tbody>
    </table>
    <div th:replace="~{fragments/layout :: pagination(${bookings}, ${pageUrl})}"></div>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/customer/booking-detail.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Chi tiết vé')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 720px">
    <div th:replace="~{fragments/layout :: flash}"></div>
    <h3 th:text="|Đơn #${booking.bookingId}|"></h3>
    <table class="table">
        <tr><th>Trạng thái</th><td th:text="${booking.status}"></td></tr>
        <tr><th>Phim</th><td th:text="${booking.showtime.movie.title}"></td></tr>
        <tr><th>Suất chiếu</th><td th:text="|${#temporals.format(booking.showtime.startTime, 'dd/MM/yyyy HH:mm')} · ${booking.showtime.room.roomName}|"></td></tr>
        <tr><th>Ghế</th><td><span class="badge bg-dark me-1" th:each="t : ${booking.tickets}" th:text="${t.seatCode}"></span></td></tr>
        <tr><th>Tổng tiền</th><td class="fw-bold" th:text="${#numbers.formatDecimal(booking.totalAmount, 0, 'COMMA', 0, 'POINT')} + 'đ'"></td></tr>
        <tr><th>Ngày đặt</th><td th:text="${#temporals.format(booking.bookingDate, 'dd/MM/yyyy HH:mm')}"></td></tr>
    </table>
    <form th:if="${booking.status.name() != 'CANCELLED'}" method="post"
          th:action="@{/customer/bookings/{id}/cancel(id=${booking.bookingId})}"
          onsubmit="return confirm('Bạn chắc chắn muốn huỷ đơn này?');" class="d-inline">
        <button class="btn btn-outline-danger">Huỷ đơn</button>
    </form>
    <a class="btn btn-secondary" th:href="@{/customer/bookings}">Quay lại</a>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

Code dùng cho TODO này đã có ở các file trước:

- `BookingServiceImpl.findByCustomer()`, `getDetailForCustomer()`, `cancelByCustomer()` — TODO 24
- `CustomerBookingController.history()`, `detail()`, `cancel()` — TODO 28

### Giải thích & lưu ý

- Trả **404** (không phải 403) khi xem đơn của người khác → không tiết lộ đơn đó có tồn tại hay không (chống dò ID — lỗi IDOR).

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| An mở "Vé của tôi" | 4 đơn của An, mới nhất trước |
| Lọc `CONFIRMED` | 2 đơn |
| An huỷ đơn suất N+1 09:00 | "Đã huỷ đơn" |
| An huỷ đơn suất đã chiếu | "Chỉ được huỷ đơn trước giờ chiếu ít nhất 2 giờ" |
| An đổi URL sang ID đơn của Bình | Trang 404 |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#15** đã được assign cho **E**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/15-booking-history` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(booking): add customer booking history with pagination` + `Refs #15`
  - [ ] `feat(booking): allow customer to cancel booking before showtime` + `Refs #15`
  - [ ] `fix(booking): prevent access to other customers bookings` + `Refs #15`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(booking): customer booking history and cancellation`** → base `develop`, mô tả theo PR template, ghi `Closes #15`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 29; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] customerId lấy từ session
  - [ ] BR-14
  - [ ] Chống IDOR
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #15 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 30 — Hồ sơ cá nhân & đổi mật khẩu

**Issue:** #16 · **Người làm:** B · **Nhánh:** `feature/16-profile`

**Mục tiêu:** Khách cập nhật thông tin (không đổi username) và đổi mật khẩu.

### Các bước

1. `ProfileForm`, `ChangePasswordForm`.
2. `CustomerServiceImpl` (TODO 16): `getProfile()`, `updateProfile()` (kiểm tra trùng với **người khác**), `changePassword()` (kiểm tra mật khẩu cũ bằng `matches`, mã hoá mật khẩu mới).
3. `CustomerProfileController`: cập nhật xong thì **cập nhật lại `SessionUser`** để menu hiện tên mới.
4. `customer/profile.html` (ô username `disabled`), `customer/password.html`.

### Code

📄 **`src/main/java/com/fu/cinema/dto/ProfileForm.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ProfileForm {

    private String username;                 // chỉ hiển thị, không cho sửa

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại gồm 10 chữ số, bắt đầu bằng 0")
    private String mobile;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải ở quá khứ")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD gồm đúng 12 chữ số")
    private String identityCard;
}
```

📄 **`src/main/java/com/fu/cinema/dto/ChangePasswordForm.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordForm {

    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
             message = "Mật khẩu ≥ 8 ký tự, có chữ hoa, chữ thường và chữ số")
    private String newPassword;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String confirmPassword;
}
```

📄 **`src/main/java/com/fu/cinema/controller/customer/CustomerProfileController.java`**

```java
package com.fu.cinema.controller.customer;

import com.fu.cinema.dto.ChangePasswordForm;
import com.fu.cinema.dto.ProfileForm;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.CustomerService;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        model.addAttribute("form", customerService.getProfile(SessionUtils.getCurrentCustomerId(session)));
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("form") ProfileForm form, BindingResult br,
                                HttpSession session, Model model, RedirectAttributes ra) {
        SessionUser user = SessionUtils.getCurrentUser(session);
        form.setUsername(customerService.getProfile(user.getCustomerId()).getUsername());
        if (br.hasErrors()) {
            return "customer/profile";
        }
        try {
            customerService.updateProfile(user.getCustomerId(), form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "customer/profile";
        }
        // cập nhật tên hiển thị trên menu
        session.setAttribute(SessionUtils.CURRENT_USER,
                new SessionUser(user.getAccountId(), user.getCustomerId(), form.getFullName().trim(), user.getRole()));
        ra.addFlashAttribute("success", "Đã cập nhật hồ sơ");
        return "redirect:/customer/profile";
    }

    @GetMapping("/password")
    public String passwordForm(Model model) {
        model.addAttribute("form", new ChangePasswordForm());
        return "customer/password";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("form") ChangePasswordForm form, BindingResult br,
                                 HttpSession session, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "customer/password";
        }
        try {
            customerService.changePassword(SessionUtils.getCurrentUser(session).getAccountId(), form);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "customer/password";
        }
        ra.addFlashAttribute("success", "Đã đổi mật khẩu");
        return "redirect:/customer/profile";
    }
}
```

📄 **`src/main/resources/templates/customer/profile.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Hồ sơ')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 720px">
    <div class="d-flex justify-content-between">
        <h3>Hồ sơ cá nhân</h3>
        <a class="btn btn-outline-secondary" th:href="@{/customer/password}">Đổi mật khẩu</a>
    </div>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:action="@{/customer/profile}" th:object="${form}" novalidate class="row g-3 mt-1">
        <div class="col-12">
            <label class="form-label">Tên đăng nhập</label>
            <input class="form-control" th:value="*{username}" disabled>
        </div>
        <div class="col-12">
            <label class="form-label">Họ tên</label>
            <input class="form-control" th:field="*{fullName}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{fullName}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Email liên hệ</label>
            <input type="email" class="form-control" th:field="*{email}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{email}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input class="form-control" th:field="*{mobile}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{mobile}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input type="date" class="form-control" th:field="*{birthday}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{birthday}"></div>
        </div>
        <div class="col-md-6">
            <label class="form-label">CCCD</label>
            <input class="form-control" th:field="*{identityCard}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{identityCard}"></div>
        </div>
        <div class="col-12"><button class="btn btn-primary">Lưu thay đổi</button></div>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

📄 **`src/main/resources/templates/customer/password.html`**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="vi">
<head th:replace="~{fragments/layout :: head('Đổi mật khẩu')}"></head>
<body>
<nav th:replace="~{fragments/layout :: navbar}"></nav>
<main class="container py-4" style="max-width: 480px">
    <h3>Đổi mật khẩu</h3>
    <div th:replace="~{fragments/layout :: flash}"></div>
    <form method="post" th:action="@{/customer/password}" th:object="${form}" novalidate>
        <div class="mb-3">
            <label class="form-label">Mật khẩu hiện tại</label>
            <input type="password" class="form-control" th:field="*{currentPassword}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{currentPassword}"></div>
        </div>
        <div class="mb-3">
            <label class="form-label">Mật khẩu mới</label>
            <input type="password" class="form-control" th:field="*{newPassword}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{newPassword}"></div>
        </div>
        <div class="mb-3">
            <label class="form-label">Nhập lại mật khẩu mới</label>
            <input type="password" class="form-control" th:field="*{confirmPassword}" th:errorclass="is-invalid">
            <div class="invalid-feedback" th:errors="*{confirmPassword}"></div>
        </div>
        <button class="btn btn-primary">Đổi mật khẩu</button>
        <a class="btn btn-secondary" th:href="@{/customer/profile}">Huỷ</a>
    </form>
</main>
<footer th:replace="~{fragments/layout :: footer}"></footer>
</body>
</html>
```

### Giải thích & lưu ý

- Ô `disabled` không được gửi lên server → không dựa vào form để biết username; controller lấy lại từ DB.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| An đổi SĐT thành `0912345678` | "Số điện thoại đã được sử dụng" |
| An đổi họ tên | Menu hiện tên mới |
| Đổi mật khẩu, sai mật khẩu hiện tại | "Mật khẩu hiện tại không đúng" |
| Đổi mật khẩu đúng | Đăng nhập lại bằng mật khẩu mới được |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#16** đã được assign cho **B**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/16-profile` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(profile): view and update customer profile` + `Refs #16`
  - [ ] `feat(profile): change password with current password check` + `Refs #16`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(profile): customer profile and password change`** → base `develop`, mô tả theo PR template, ghi `Closes #16`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 30; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] Không cho đổi username
  - [ ] Trùng dữ liệu loại trừ chính mình
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #16 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

## TODO 31 — (Bonus) Đánh giá phim

**Issue:** #18 · **Người làm:** C · **Nhánh:** `feature/18-movie-review`

**Mục tiêu:** Khách đã xem phim được đánh giá 1–5 sao; mỗi khách mỗi phim một đánh giá (khoá kép), sửa được.

### Các bước

1. `ReviewForm`.
2. `ReviewServiceImpl`: `canReview()` dùng Q12 (đơn `CONFIRMED` + suất đã kết thúc); `save()` tìm theo `ReviewId` → có thì sửa, chưa có thì tạo (dùng `getReferenceById` để không phải SELECT customer/movie).
3. `CustomerReviewController`: `POST /customer/movies/{id}/review`.
4. Form đánh giá & danh sách đánh giá nằm trong `movie-detail.html` (TODO 27).

### Code

📄 **`src/main/java/com/fu/cinema/dto/ReviewForm.java`**

```java
package com.fu.cinema.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewForm {

    @NotNull(message = "Vui lòng chọn số sao")
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank(message = "Vui lòng nhập nhận xét")
    @Size(max = 1000)
    private String comment;
}
```

📄 **`src/main/java/com/fu/cinema/service/impl/ReviewServiceImpl.java`**

```java
package com.fu.cinema.service.impl;

import com.fu.cinema.dto.RatingSummary;
import com.fu.cinema.dto.ReviewForm;
import com.fu.cinema.entity.Review;
import com.fu.cinema.entity.ReviewId;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.repository.BookingRepository;
import com.fu.cinema.repository.CustomerRepository;
import com.fu.cinema.repository.MovieRepository;
import com.fu.cinema.repository.ReviewRepository;
import com.fu.cinema.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;

    @Override
    public List<Review> findByMovie(Long movieId) {
        return reviewRepository.findByMovie_MovieIdOrderByReviewDateDesc(movieId);
    }

    @Override
    public RatingSummary summarize(Long movieId) {
        return reviewRepository.summarize(movieId);
    }

    @Override
    public boolean canReview(Long customerId, Long movieId) {
        return customerId != null && bookingRepository.countWatched(customerId, movieId, LocalDateTime.now()) > 0;
    }

    @Override
    public ReviewForm getForm(Long customerId, Long movieId) {
        ReviewForm form = new ReviewForm();
        reviewRepository.findById(new ReviewId(customerId, movieId)).ifPresent(r -> {
            form.setRating(r.getRating());
            form.setComment(r.getComment());
        });
        return form;
    }

    @Override
    @Transactional
    public void save(Long customerId, Long movieId, ReviewForm form) {
        if (!canReview(customerId, movieId)) {                                            // BR-16
            throw new BusinessException("Bạn chỉ có thể đánh giá phim đã xem");
        }
        ReviewId id = new ReviewId(customerId, movieId);
        Review review = reviewRepository.findById(id).orElseGet(() -> {                  // khoá kép → thêm mới hoặc sửa
            Review r = new Review();
            r.setId(id);
            r.setCustomer(customerRepository.getReferenceById(customerId));
            r.setMovie(movieRepository.getReferenceById(movieId));
            return r;
        });
        review.setRating(form.getRating());
        review.setComment(form.getComment().trim());
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);
    }
}
```

📄 **`src/main/java/com/fu/cinema/controller/customer/CustomerReviewController.java`**

```java
package com.fu.cinema.controller.customer;

import com.fu.cinema.dto.ReviewForm;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.ReviewService;
import com.fu.cinema.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/movies")
@RequiredArgsConstructor
public class CustomerReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{movieId}/review")
    public String review(@PathVariable Long movieId, @Valid @ModelAttribute("reviewForm") ReviewForm form,
                         BindingResult br, HttpSession session, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
        } else {
            try {
                reviewService.save(SessionUtils.getCurrentCustomerId(session), movieId, form);
                ra.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá!");
            } catch (BusinessException e) {
                ra.addFlashAttribute("error", e.getMessage());
            }
        }
        return "redirect:/movies/" + movieId;
    }
}
```

### Giải thích & lưu ý

- Khoá kép làm "mỗi khách một đánh giá mỗi phim" được đảm bảo **ở mức DB**, không chỉ ở code.

### Kiểm thử

| Thao tác | Kết quả mong đợi |
|---|---|
| An mở *Biệt Đội Siêu Tốc* | Có form đánh giá (đã xem suất N−2) |
| An gửi 5 sao | Điểm 5.0/5 (1 lượt) |
| An gửi lại 4 sao | Vẫn 1 lượt, điểm 4.0 (sửa, không thêm) |
| Bình mở *Biệt Đội Siêu Tốc* | Không có form |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#18** đã được assign cho **C**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `feature/18-movie-review` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `feat(review): add rating summary query` + `Refs #18`
  - [ ] `feat(review): allow customers who watched to review movie` + `Refs #18`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`feat(review): movie rating and review`** → base `develop`, mô tả theo PR template, ghi `Closes #18`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 31; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] BR-16
  - [ ] Upsert theo khoá kép
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #18 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

# Part J — Hoàn thiện

## TODO 32 — README, test, release v1.0.0

**Issue:** #19 · **Người làm:** A (Leader) · **Nhánh:** `docs/19-readme → release/1.0.0`

**Mục tiêu:** Người khác clone về chạy được chỉ bằng README; phát hành `v1.0.0`.

### Các bước

1. Test khởi động ứng dụng với H2 (`mvn verify` chạy được trên mọi máy, không cần SQL Server).
2. Viết `README.md` theo mẫu dưới, chụp ảnh các màn hình chính lưu vào `docs/images/`.
3. Chạy toàn bộ checklist ở mục 12 của `Assignment1_ReadMe.md` trên nhánh `release/1.0.0`; lỗi → issue `bug` → nhánh `fix/*` từ `release/1.0.0`.
4. PR `release/1.0.0 → main` (2 approvals, merge commit) → tag `v1.0.0` → GitHub Release → back-merge `main → develop`.

**Mẫu `README.md`:**
````markdown
# FU Cinema Ticket System

Website bán vé xem phim — Spring Boot 3, Spring Data JPA, Thymeleaf, SQL Server.

## Công nghệ
Java 17 · Spring Boot 3.3 · Spring Data JPA (Hibernate 6) · Thymeleaf · Bootstrap 5 · SQL Server · Maven

## Cài đặt & chạy
1. Tạo database: `CREATE DATABASE FUCinemaTicketDB;`
2. Copy `src/main/resources/application-local.properties.example` → `application-local.properties`, điền tài khoản SQL Server.
3. Chạy: `mvn spring-boot:run` → mở http://localhost:8080
4. Lần chạy đầu tiên, dữ liệu mẫu được tạo tự động.

## Tài khoản mẫu
| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | admin@fucinema.vn | Admin@123 |
| Customer | an.nv@gmail.com | Customer@123 |
| Customer (15 tuổi) | binh.tt@gmail.com | Customer@123 |

## Thành viên & phân công
| Thành viên | MSSV | Chức năng |
|---|---|---|
| A (Leader) | ... | Nền tảng, entity, đăng nhập/đăng ký/phân quyền |
| B | ... | Quản lý khách hàng, hồ sơ |
| C | ... | Thể loại, phim, trang chủ, đánh giá |
| D | ... | Phòng, suất chiếu, đặt vé |
| E | ... | Quản lý đơn, lịch sử đơn, báo cáo |

## Ảnh màn hình
![Trang chủ](docs/images/home.png)
````

### Code

📄 **`src/test/java/com/fu/cinema/FuCinemaTicketSystemApplicationTests.java`**

```java
package com.fu.cinema;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")          // dùng H2 (src/test/resources/application-test.properties) → chạy test không cần SQL Server
class FuCinemaTicketSystemApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

### Giải thích & lưu ý

- `mvn verify` (tác giả chạy trước khi mở PR, reviewer chạy lại khi review — thay cho CI) chạy test này với H2 (profile `test`) → phát hiện sớm lỗi mapping entity, sai tên derived query, sai cú pháp JPQL.

### Kiểm thử

| Kiểm tra | Kết quả mong đợi |
|---|---|
| `mvn verify` | `BUILD SUCCESS`, test `contextLoads` pass |
| Clone mới + làm theo README | Chạy được, đăng nhập được bằng tài khoản mẫu |
| GitHub → Releases | Có `v1.0.0` kèm release notes |

### ✅ Checklist làm việc nhóm

- [ ] Issue **#19** đã được assign cho **A (Leader)**, gắn label & milestone, kéo sang cột *In progress*.
- [ ] Tạo nhánh `docs/19-readme → release/1.0.0` từ `develop` mới nhất.
- [ ] Commit theo thứ tự (mỗi commit build được):
  - [ ] `test: add application context smoke test` + `Refs #19`
  - [ ] `docs(readme): add setup guide, accounts and screenshots` + `Refs #19`
  - [ ] `docs(readme): add team members and task assignment` + `Refs #19`
- [ ] `mvn -q compile` thành công; chạy app không có exception trong log.
- [ ] Đã đi hết bảng **Kiểm thử** ở trên (cả trường hợp lỗi).
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`; không còn code tạm / `System.out.println`.
- [ ] Push và mở PR **`docs(readme): project documentation · release: v1.0.0`** → base `develop`, mô tả theo PR template, ghi `Closes #19`, kèm ảnh chụp màn hình (nếu có giao diện).
- [ ] PR bao trọn TODO 32; kích thước hợp lý (tách PR nếu quá ~400 dòng logic).
- [ ] Reviewer (khác module) kiểm tra:
  - [ ] README đủ để người mới chạy được
  - [ ] Không lộ mật khẩu thật
- [ ] `mvn verify` pass trên máy tác giả và reviewer · ≥ 1 approval · mọi conversation đã resolve.
- [ ] **Squash and merge** với tiêu đề Conventional Commits; nhánh tự xoá; issue #19 tự đóng; board → *Done*.
- [ ] Thông báo cho nhóm nếu PR thay đổi phần dùng chung (entity, interface service, layout, `pom.xml`).

---

## Phụ lục A — Bảng tra file → TODO

| File | TODO |
|---|---|
| `pom.xml` | 1 |
| `…/cinema/FuCinemaTicketSystemApplication.java` | 1 |
| `.gitignore` | 1 |
| `resources/application.properties` | 2 |
| `resources/application-local.properties.example` | 2 |
| `src/test/resources/application-test.properties` | 2 |
| `resources/templates/fragments/layout.html` | 3 |
| `resources/static/css/app.css` | 3 |
| `…/cinema/enums/Role.java` | 4 |
| `…/cinema/enums/AccountStatus.java` | 4 |
| `…/cinema/enums/AgeRating.java` | 4 |
| `…/cinema/enums/MovieStatus.java` | 4 |
| `…/cinema/enums/RoomType.java` | 4 |
| `…/cinema/enums/RoomStatus.java` | 4 |
| `…/cinema/enums/ShowtimeStatus.java` | 4 |
| `…/cinema/enums/BookingStatus.java` | 4 |
| `…/cinema/entity/Account.java` | 4 |
| `…/cinema/entity/Customer.java` | 4 |
| `…/cinema/entity/Genre.java` | 5 |
| `…/cinema/entity/Movie.java` | 5 |
| `…/cinema/entity/Room.java` | 6 |
| `…/cinema/entity/Showtime.java` | 6 |
| `…/cinema/entity/Booking.java` | 7 |
| `…/cinema/entity/Ticket.java` | 7 |
| `…/cinema/entity/ReviewId.java` | 7 |
| `…/cinema/entity/Review.java` | 7 |
| `…/cinema/config/PasswordConfig.java` | 8 |
| `…/cinema/config/DataSeeder.java` | 8 |
| `…/cinema/repository/AccountRepository.java` | 9 |
| `…/cinema/repository/GenreRepository.java` | 9 |
| `…/cinema/repository/RoomRepository.java` | 9 |
| `…/cinema/repository/CustomerRepository.java` | 9, 10 |
| `…/cinema/repository/MovieRepository.java` | 9, 10 |
| `…/cinema/repository/ShowtimeRepository.java` | 9, 10 |
| `…/cinema/repository/BookingRepository.java` | 9, 10 |
| `…/cinema/repository/ReviewRepository.java` | 9, 10 |
| `…/cinema/dto/MovieRevenueDTO.java` | 10 |
| `…/cinema/dto/RatingSummary.java` | 10 |
| `…/cinema/dto/RoomShowtimeCount.java` | 10 |
| `…/cinema/repository/TicketRepository.java` | 10 |
| `…/cinema/exception/BusinessException.java` | 11 |
| `…/cinema/exception/ResourceNotFoundException.java` | 11 |
| `…/cinema/util/ValidationUtils.java` | 11 |
| `…/cinema/util/DateUtils.java` | 11 |
| `…/cinema/util/PageUtils.java` | 11 |
| `…/cinema/service/AuthService.java` | 11 |
| `…/cinema/service/CustomerService.java` | 11 |
| `…/cinema/service/GenreService.java` | 11 |
| `…/cinema/service/MovieService.java` | 11 |
| `…/cinema/service/RoomService.java` | 11 |
| `…/cinema/service/ShowtimeService.java` | 11 |
| `…/cinema/service/BookingService.java` | 11 |
| `…/cinema/service/ReportService.java` | 11 |
| `…/cinema/service/ReviewService.java` | 11 |
| `…/cinema/exception/GlobalExceptionHandler.java` | 12 |
| `resources/templates/error/404.html` | 12 |
| `…/cinema/dto/SessionUser.java` | 13 |
| `…/cinema/util/SessionUtils.java` | 13 |
| `…/cinema/dto/LoginForm.java` | 13 |
| `…/cinema/service/impl/AuthServiceImpl.java` | 13 |
| `…/cinema/controller/AuthController.java` | 13 |
| `…/cinema/controller/GlobalModelAttributes.java` | 13 |
| `resources/templates/auth/login.html` | 13 |
| `…/cinema/dto/RegisterForm.java` | 14 |
| `resources/templates/auth/register.html` | 14 |
| `…/cinema/config/AuthInterceptor.java` | 15 |
| `…/cinema/config/WebConfig.java` | 15 |
| `…/cinema/service/impl/CustomerServiceImpl.java` | 16 |
| `…/cinema/controller/admin/AdminCustomerController.java` | 16 |
| `resources/templates/admin/customers/list.html` | 16 |
| `…/cinema/dto/CustomerForm.java` | 17 |
| `resources/templates/admin/customers/form.html` | 17 |
| `…/cinema/dto/GenreForm.java` | 18 |
| `…/cinema/service/impl/GenreServiceImpl.java` | 18 |
| `…/cinema/controller/admin/AdminGenreController.java` | 18 |
| `resources/templates/admin/genres/list.html` | 18 |
| `resources/templates/admin/genres/form.html` | 18 |
| `…/cinema/service/impl/MovieServiceImpl.java` | 19 |
| `…/cinema/controller/admin/AdminMovieController.java` | 19 |
| `resources/templates/admin/movies/list.html` | 19 |
| `…/cinema/dto/MovieForm.java` | 20 |
| `resources/templates/admin/movies/form.html` | 20 |
| `…/cinema/dto/RoomForm.java` | 22 |
| `…/cinema/service/impl/RoomServiceImpl.java` | 22 |
| `…/cinema/controller/admin/AdminRoomController.java` | 22 |
| `resources/templates/admin/rooms/list.html` | 22 |
| `resources/templates/admin/rooms/form.html` | 22 |
| `…/cinema/dto/ShowtimeForm.java` | 23 |
| `…/cinema/service/impl/ShowtimeServiceImpl.java` | 23 |
| `…/cinema/controller/admin/AdminShowtimeController.java` | 23 |
| `resources/templates/admin/showtimes/list.html` | 23 |
| `resources/templates/admin/showtimes/form.html` | 23 |
| `…/cinema/service/impl/BookingServiceImpl.java` | 24 |
| `…/cinema/controller/admin/AdminBookingController.java` | 24 |
| `resources/templates/admin/bookings/list.html` | 24 |
| `resources/templates/admin/bookings/detail.html` | 24 |
| `…/cinema/dto/ReportResult.java` | 26 |
| `…/cinema/service/impl/ReportServiceImpl.java` | 26 |
| `…/cinema/controller/admin/AdminReportController.java` | 26 |
| `resources/templates/admin/report.html` | 26 |
| `…/cinema/dto/DashboardStats.java` | 26 |
| `…/cinema/controller/admin/AdminDashboardController.java` | 26 |
| `resources/templates/admin/dashboard.html` | 26 |
| `…/cinema/controller/HomeController.java` | 27 |
| `resources/templates/index.html` | 27 |
| `resources/templates/movie-detail.html` | 27 |
| `…/cinema/dto/SeatMap.java` | 28 |
| `…/cinema/dto/BookingRequest.java` | 28 |
| `…/cinema/controller/customer/CustomerBookingController.java` | 28 |
| `resources/templates/customer/seats.html` | 28 |
| `resources/templates/customer/bookings.html` | 29 |
| `resources/templates/customer/booking-detail.html` | 29 |
| `…/cinema/dto/ProfileForm.java` | 30 |
| `…/cinema/dto/ChangePasswordForm.java` | 30 |
| `…/cinema/controller/customer/CustomerProfileController.java` | 30 |
| `resources/templates/customer/profile.html` | 30 |
| `resources/templates/customer/password.html` | 30 |
| `…/cinema/dto/ReviewForm.java` | 31 |
| `…/cinema/service/impl/ReviewServiceImpl.java` | 31 |
| `…/cinema/controller/customer/CustomerReviewController.java` | 31 |
| `src/test/java/com/fu/cinema/FuCinemaTicketSystemApplicationTests.java` | 32 |

> Các file `templates/error/403.html`, `500.html`, `error.html` giống `404.html` (TODO 12), chỉ khác mã lỗi và câu thông báo.

## Phụ lục B — Lỗi thường gặp

| Lỗi / hiện tượng | Nguyên nhân | Cách sửa |
|---|---|---|
| `Login failed for user 'sa'` / không kết nối được 1433 | Sai mật khẩu, chưa bật SQL Auth hoặc TCP/IP | Kiểm tra `application-local.properties`, SQL Server Configuration Manager |
| `The driver could not establish a secure connection ... (SSL)` | Driver mssql-jdbc mặc định mã hoá | Thêm `encrypt=true;trustServerCertificate=true` vào URL |
| `PropertyReferenceException: No property 'xxx'` khi khởi động | Derived query / `Sort.by(...)` sai tên field | Dùng đúng tên field Java |
| `LazyInitializationException` khi render view | Truy cập quan hệ LAZY ngoài transaction (`open-in-view=false`) | `JOIN FETCH` / `@EntityGraph` trong query |
| `Could not resolve root entity` | JPQL dùng tên bảng thay vì tên entity | `FROM Booking b`, không phải `FROM bookings` |
| `Cannot create TypedQuery ... constructor` (Q7) | Sai package hoặc kiểu tham số DTO | Ghi đầy đủ `com.fu.cinema.dto.MovieRevenueDTO`, `Long`/`BigDecimal` |
| Thay đổi không được lưu, không báo lỗi | Method ghi thiếu `@Transactional` (đang `readOnly`) | Thêm `@Transactional` trên method ghi |
| `TransactionRequiredException` khi chạy `@Modifying` | Gọi ngoài transaction | Gọi từ method service có `@Transactional` |
| Dữ liệu cũ sau bulk update | Cache persistence context | `@Modifying(clearAutomatically = true)` |
| `Failed to convert ... LocalDateTime` từ form | Sai pattern với `datetime-local` | `@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")` |
| Báo cáo thiếu đơn ngày cuối | Dùng `<= endDate 00:00` | Dùng `< endDate.plusDays(1).atStartOfDay()` |
| `StackOverflowError` khi log/in entity | `@Data`/`toString` duyệt quan hệ 2 chiều | Dùng `@Getter @Setter`, không `@Data` cho entity |
| Trang 403 không đúng giao diện | Thiếu `templates/error/403.html` | Tạo đủ trang lỗi (TODO 12) |
| Conflict ở `pom.xml`, `layout.html`, entity | Nhiều người cùng sửa file dùng chung | Theo bảng "người sở hữu file" trong `Assignment1_teamwork.md` mục 9 |

## Phụ lục C — Checklist tổng trước khi release

### Chức năng (chạy trên `release/1.0.0` với dữ liệu seed mới)
| ✔ | Kịch bản | Kết quả mong đợi |
|---|---|---|
| [ ] | Đăng nhập Admin / Customer / tài khoản bị khoá | Điều hướng đúng / báo khoá |
| [ ] | Đăng ký trùng email, dưới 13 tuổi, mật khẩu yếu | Báo lỗi tương ứng, giữ dữ liệu form |
| [ ] | Customer vào `/admin`, khách vào `/customer/...` | 403 / chuyển `/login` |
| [ ] | Tìm khách hàng + phân trang giữ từ khoá | Đúng kết quả |
| [ ] | Xoá khách chưa có đơn / đã có đơn | Xoá hẳn / khoá tài khoản |
| [ ] | Xoá thể loại còn phim | Bị chặn |
| [ ] | Lọc phim kết hợp, xoá phim có/không suất chiếu | Đúng; soft delete `STOPPED` |
| [ ] | Tạo suất chiếu trùng giờ cùng phòng | Báo trùng, chỉ rõ suất |
| [ ] | Xoá suất có đơn | Suất + đơn chuyển `CANCELLED` |
| [ ] | Xác nhận / huỷ đơn sai luồng | Báo lỗi |
| [ ] | Báo cáo N−7 → N | 3 phim giảm dần doanh thu, tổng 440.000đ |
| [ ] | Trang chủ ẩn phim STOPPED; chi tiết phim có lịch chiếu | Đúng |
| [ ] | Đặt nhiều ghế; ghế đã bán; > 8 ghế; chưa đủ tuổi | Thành công / bị chặn đúng thông báo |
| [ ] | Lịch sử đơn; huỷ trước/sau 2 giờ; xem đơn người khác | Đúng; 404 với đơn người khác |
| [ ] | Cập nhật hồ sơ, đổi mật khẩu | Đúng |
| [ ] | *(Bonus)* Đánh giá phim đã xem / chưa xem | Có form / không có form |

### Kỹ thuật
- [ ] `mvn verify` → `BUILD SUCCESS` trên nhánh `release/1.0.0` (chạy trên máy Leader và Tester của sprint).
- [ ] Không controller nào inject repository; mọi rule nghiệp vụ nằm ở service.
- [ ] Mọi thao tác ghi dùng POST + Post/Redirect/Get; có flash message.
- [ ] Không có stack trace hiển thị trên giao diện; trang 403/404/500 hoạt động.
- [ ] Không có mật khẩu thật trong repository; `application-local.properties` không bị track.

### Quy trình nhóm
- [ ] Mọi issue Sprint 0–3 đã đóng qua PR (`Closes #…`).
- [ ] Mỗi thành viên có commit + PR được merge cho phần của mình; mỗi PR có reviewer.
- [ ] `release/1.0.0` → `main`, tag `v1.0.0`, GitHub Release, back-merge `main → develop`.
