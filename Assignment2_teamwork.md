# Assignment 02 — Hướng dẫn Setup & Quy trình Làm việc nhóm (FU Cinema Desktop)

> **Dự án:** FU Cinema Ticket System — Mở rộng ứng dụng Desktop (JavaFX + Spring Boot)  
> **Áp dụng cho:** Nhóm 7 (5 thành viên) · Môn HSF302  
> **Nguyên tắc cốt lõi:** Làm tiếp tục trực tiếp trên repository và project hiện tại của Assignment 01, dùng chung Database `FUCinemaTicketDB`. Tuyệt đối **KHÔNG** tạo project mới, **KHÔNG** sửa cấu trúc Entity cũ.

---

## Mục lục

1. [Tổng quan & Kiến trúc hệ thống](#1-tổng-quan--kiến-trúc-hệ-thống)
2. [Hướng dẫn Setup môi trường cho thành viên](#2-hướng-dẫn-setup-môi-trường-cho-thành-viên)
3. [Quy trình Quản lý công việc trên GitHub (Issue, Board, Sprint)](#3-quy-trình-quản-lý-công-việc-trên-github)
4. [Quy chuẩn Git Flow (Nhánh, Commit, Pull Request, Review)](#4-quy-chuẩn-git-flow)
5. [Bảng phân chia công việc cho 5 thành viên (TODO 33–46)](#5-bảng-phân-chia-công-việc-cho-5-thành-viên)
6. [Ma trận Code Review (Bắt buộc)](#6-ma-trận-code-review)
7. [Checklist kỹ thuật không được vi phạm (Tránh bị 0 điểm)](#7-checklist-kỹ-thuật-không-được-vi-phạm)

---

## 1. Tổng quan & Kiến trúc hệ thống

Ứng dụng Desktop JavaFX được xây dựng phục vụ cho **nhân viên rạp chiếu phim (tài khoản ADMIN)** tại quầy vé:
- Đăng nhập chỉ dành cho `ADMIN` (tài khoản `CUSTOMER` bị từ chối).
- Bán vé trực tiếp tại quầy trên **sơ đồ ghế ma trận thời gian thực** (tối đa 8 ghế/đơn, hỗ trợ checkbox *"Đã thanh toán"*).
- Quản lý danh mục: Thể loại, Phim, Phòng chiếu, Suất chiếu.
- Quản lý & duyệt/huỷ đơn đặt vé online từ website.
- Dashboard KPI và Báo cáo doanh thu có biểu đồ, hỗ trợ xuất file CSV (UTF-8 BOM).

### Mô hình kiến trúc bắt buộc (3-Layer + Facade):

```
                      ┌──────────────── fu-cinema-ticket-system (1 project, 1 DB) ───────────────┐
 Trình duyệt (Web) ──►│ controller/* (Thymeleaf)  ─┐                                              │
                      │                            ├─► service/* ─► repository/* ─► entity/* ─► SQL Server
 JavaFX (Desktop) ───►│ desktop/controller/* ─► desktop/facade/*                                   │
                      │  (FXML, Spring prototype)    (map entity → record, chỉ gọi service)        │
                      └────────────────────────────────────────────────────────────────────────────┘
```

**Luồng gọi bắt buộc:**
$$\text{FXML} \longrightarrow \text{Controller (desktop)} \longrightarrow \text{Facade (desktop)} \longrightarrow \text{Service (Assignment 01)} \longrightarrow \text{Repository} \longrightarrow \text{Entity}$$

- **Controller chỉ gọi Facade.**
- **Facade chỉ gọi Service có sẵn.**
- **Tuyệt đối KHÔNG ai trong package `com.fu.cinema.desktop` được inject `Repository`.**
- Facade chuyển đổi Entity sang **Java Record** (trong package `desktop.model`) bên trong transaction; UI không bao giờ làm việc trực tiếp với Entity để tránh lỗi `LazyInitializationException`.

---

## 2. Hướng dẫn Setup môi trường cho thành viên

Mỗi thành viên khi bắt đầu làm Assignment 02 cần thực hiện lần lượt các bước sau trên máy mình:

### Bước 1: Chuẩn bị công cụ & JDK
- **JDK:** Bắt buộc **Java 21** (kiểm tra bằng lệnh `java -version`).
- **Scene Builder (Khuyên dùng):** Cài đặt [Scene Builder](https://gluonhq.com/products/scene-builder/) để kéo thả chỉnh sửa các file FXML trong `src/main/resources/desktop/fxml/`.

### Bước 2: Đồng bộ mã nguồn từ nhánh `develop`
Mọi công việc đều bắt đầu từ nhánh `develop`. Mở Terminal tại thư mục project:
```bash
# Chuyển sang nhánh develop
git checkout develop

# Kéo code mới nhất về
git pull origin develop
```

### Bước 3: Cấu hình Database & File kết nối
- Đảm bảo SQL Server đang chạy database `FUCinemaTicketDB` với đầy đủ dữ liệu seed của Assignment 01.
- Kiểm tra file `src/main/resources/application-local.properties` (file này đã được `.gitignore`, mỗi máy tự giữ cấu hình của mình):
  ```properties
  spring.datasource.url=jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=FUCinemaTicketDB;encrypt=true;trustServerCertificate=true
  spring.datasource.username=sa
  spring.datasource.password=123456
  ```

### Bước 4: Kiểm tra build & Cấu hình Run Configuration trong IDE (IntelliJ IDEA)
Chạy kiểm tra build:
```bash
# Windows
.\mvnw.cmd clean test-compile

# macOS / Linux
./mvnw clean test-compile
```
Khi thấy `BUILD SUCCESS` là hoàn tất setup môi trường.

#### Cách chạy đồng thời 2 ứng dụng trong IntelliJ:
1. **Chạy Web:** Run class `FuCinemaTicketSystemApplication.java` $\rightarrow$ Truy cập trình duyệt `http://localhost:8080`.
2. **Chạy Desktop (sau khi TODO 33 merge):** Tạo Run Configuration mới:
   - Main class: `com.fu.cinema.desktop.DesktopLauncher`
   - JRE: Java 21
   - Active profiles: để trống (code tự nạp profile `desktop`).
   - Bấm **Run** $\rightarrow$ Cửa sổ JavaFX sẽ mở lên, không chiếm cổng 8080 và chạy song song với Web.

---

## 3. Quy trình Quản lý công việc trên GitHub

Nhóm sử dụng **GitHub Projects Board**, **Milestones**, **Labels** và **Issues** để theo dõi tiến độ theo chuẩn Agile/Scrum.

### 3.1. Phân chia Milestones (Sprints)
- **`Sprint 4 — Desktop foundation`:** Các công việc nền tảng Desktop (TODO 33 đến 38).
- **`Sprint 5 — Desktop features & Release v2.0.0`:** Các tính năng chi tiết, bán vé, báo cáo và hoàn thiện (TODO 39 đến 46).

### 3.2. Cấu trúc Label bắt buộc
- **`module: desktop`:** Gắn vào **tất cả** các Issue và Pull Request của Assignment 2.
- `enhancement` / `feature`: Chức năng mới.
- `bug`: Sửa lỗi nếu phát sinh.

### 3.3. Quy trình sử dụng Board (`FUCinema Board`)
Mọi Issue khi tạo ra phải được add vào dự án **`FUCinema Board`** (`github.com/orgs/HSF302-group7/projects/1`):
1. **`To do (Sprint)`:** Các task thuộc Sprint hiện tại đang chờ làm.
2. **`In progress`:** Khi thành viên bắt đầu checkout nhánh để code, kéo thẻ sang đây.
3. **`In review`:** Khi mở Pull Request và gắn người review, kéo thẻ sang đây.
4. **`Done`:** Khi PR được Approve và Merge vào `develop`, issue tự động đóng hoặc kéo sang đây.

---

## 4. Quy chuẩn Git Flow

> **QUY TẮC BẤT DI BẤT DỊCH:** Tuyệt đối không commit/push trực tiếp vào `main` và `develop`. Mọi thay đổi đều phải đi qua **Issue $\rightarrow$ Branch $\rightarrow$ Pull Request $\rightarrow$ Code Review $\rightarrow$ Merge**.

### Bước 1: Thành viên tự tạo Issue trên GitHub
Trước khi code bất kỳ việc gì, thành viên vào mục **Issues** bấm **`New issue`**:
- **Title:** `[FEATURE] <Tên chức năng> (TODO <số>)`  
  *(Xem bảng mục 5 bên dưới để copy đúng tiêu đề).*
- **Assignees:** Chọn chính mình.
- **Labels:** `module: desktop`, `enhancement`.
- **Milestone:** Chọn `Sprint 4` hoặc `Sprint 5`.
- **Projects:** `FUCinema Board`.
- Ghi lại **số Issue** (ví dụ: `#62`).

### Bước 2: Tạo nhánh tính năng từ `develop`
Tên nhánh tuân theo quy ước: `feature/<số-issue>-desktop-<tên-chức-năng>`:
```bash
git checkout develop
git pull origin develop
git checkout -b feature/62-desktop-javafx-setup
```

### Bước 3: Viết code & Commit theo chuẩn Conventional Commits
Commit nhỏ, rõ ràng, luôn có `Refs #<số-issue>`:
```bash
git add .
git commit -m "feat(desktop): add javafx dependencies and desktop launcher" -m "Refs #62"
```

### Bước 4: Đẩy nhánh lên GitHub & Mở Pull Request (PR)
```bash
git push -u origin feature/62-desktop-javafx-setup
```
Trên GitHub, bấm **`Compare & pull request`**:
- **Base branch:** `develop` $\leftarrow$ **Compare branch:** `feature/...`
- **Tiêu đề PR:** `feat(desktop): <mô tả ngắn gọn>`
- **Nội dung PR:**
  ```markdown
  ### Mô tả thay đổi
  - Hoàn thành TODO xx: <mô tả>

  ### Kiểm thử
  - [x] Đã chạy `mvn test-compile` thành công trên máy
  - [x] Đã chạy thử giao diện không lỗi

  Closes #62
  ```
- **Reviewers:** Gắn thành viên được chỉ định review theo ma trận mục 6.
- **Labels:** `module: desktop`.

### Bước 5: Review & Merge
1. Reviewer kéo nhánh của bạn về máy chạy thử hoặc xem tab **Files changed**.
2. Nếu đạt yêu cầu: Bấm **`Review changes`** $\rightarrow$ Chọn **`Approve`**.
3. Người được phân công merge chọn **`Squash and merge`** vào `develop`, sau đó xoá nhánh tính năng (Delete branch).

---

## 5. Bảng phân chia công việc cho 5 thành viên (TODO 33–46)

| STT | Thành viên | Vai trò | TODO phụ trách | Tiêu đề Issue mẫu | Milestone |
|:---:|---|:---:|:---:|---|:---:|
| 1 | **Nguyễn Ngọc Thạch** (`ngocthach324`) | **A — Leader** | **TODO 33** | `[FEATURE] JavaFX bootstrap & desktop profile (TODO 33)` | Sprint 4 |
| 2 | **Nguyễn Ngọc Thạch** (`ngocthach324`) | **A — Leader** | **TODO 34** | `[FEATURE] Desktop navigation & main layout (TODO 34)` | Sprint 4 |
| 3 | **Nguyễn Văn Nhật** (`vietnhatsun`) | **B** | **TODO 35** | `[FEATURE] Desktop shared utilities FxAsync & AlertUtil (TODO 35)` | Sprint 4 |
| 4 | **Nguyễn Ngọc Thạch** (`ngocthach324`) | **A — Leader** | **TODO 36** | `[FEATURE] Desktop login & DesktopSession (TODO 36)` | Sprint 4 |
| 5 | **Nguyễn Trí Anh Khoa** (`NguyenTriAnhKhoaDE200261`) | **E** | **TODO 37** | `[FEATURE] Desktop dashboard with charts & KPI (TODO 37)` | Sprint 4 |
| 6 | **Hoàng Ngọc Nam** (`nemz6406`) | **C** | **TODO 38** | `[FEATURE] Desktop genre management (TODO 38)` | Sprint 4 |
| 7 | **Hoàng Ngọc Nam** (`nemz6406`) | **C** | **TODO 39** | `[FEATURE] Desktop movie list, filter & pagination (TODO 39)` | Sprint 5 |
| 8 | **Hoàng Ngọc Nam** (`nemz6406`) | **C** | **TODO 40** | `[FEATURE] Desktop movie dialog & delete BR-06 (TODO 40)` | Sprint 5 |
| 9 | **Lại Bằng Long** (`long1616`) | **D** | **TODO 41** | `[FEATURE] Desktop rooms & showtimes management (TODO 41)` | Sprint 5 |
| 10 | **Lại Bằng Long** (`long1616`) | **D** | **TODO 42** | `[FEATURE] SeatMapView matrix component (TODO 42)` | Sprint 5 |
| 11 | **Nguyễn Văn Nhật** (`vietnhatsun`) | **B** | **TODO 43** | `[FEATURE] Counter sale screen & booking flow (TODO 43)` | Sprint 5 |
| 12 | **Nguyễn Trí Anh Khoa** (`NguyenTriAnhKhoaDE200261`) | **E** | **TODO 44** | `[FEATURE] Desktop booking management & status update (TODO 44)` | Sprint 5 |
| 13 | **Nguyễn Trí Anh Khoa** (`NguyenTriAnhKhoaDE200261`) | **E** | **TODO 45** | `[FEATURE] Desktop revenue report & CSV export (TODO 45)` | Sprint 5 |
| 14 | **Nguyễn Ngọc Thạch** (`ngocthach324`) | **A — Leader** | **TODO 46** | `[DOCS] Desktop README, tests & release v2.0.0 (TODO 46)` | Sprint 5 |

---

## 6. Ma trận Code Review (Bắt buộc)

Checklist chấm điểm yêu cầu: **Mỗi thành viên phải có ít nhất 3 lượt review PR của người khác và người viết không tự approve**. Bảng phân công reviewer:

| Người viết PR | PR / TODO phụ trách | Reviewer chính 1 | Reviewer chính 2 |
|---|---|:---:|:---:|
| **Thạch (A)** | TODO 33, 34, 36, 46 | Nam (C) | Nhật (B) |
| **Nhật (B)** | TODO 35, 43 | Long (D) | Thạch (A) |
| **Nam (C)** | TODO 38, 39, 40 | Nhật (B) | Khoa (E) |
| **Long (D)** | TODO 41, 42 | Thạch (A) | Nam (C) |
| **Khoa (E)** | TODO 37, 44, 45 | Long (D) | Nam (C) |

---

## 7. Checklist kỹ thuật không được vi phạm (Tránh bị trừ điểm)

Giảng viên kiểm tra mã nguồn tự động bằng các câu lệnh `grep`. Các lỗi dưới đây sẽ bị **trừ 0.5 điểm mỗi lỗi**:

1. **Tuyệt đối KHÔNG inject `Repository` trong package `com.fu.cinema.desktop`:**
   Controller desktop chỉ gọi Facade; Facade chỉ gọi Service.
2. **Tuyệt đối KHÔNG để Entity xuất hiện trên Controller / Component Desktop:**
   Tất cả dữ liệu hiển thị phải là Java **Record** (trong `desktop.model`). Entity chỉ được map sang Record bên trong transaction của Facade.
3. **Tuyệt đối KHÔNG truy vấn Database trực tiếp trên FX Application Thread:**
   Mọi lời gọi Facade phải đặt trong `fxAsync.run(...)` (chạy trên virtual thread Task), cập nhật UI qua callback chạy trên FX thread để tránh làm đơ ứng dụng.
4. **Tuyệt đối KHÔNG làm hỏng Web Thymeleaf của Assignment 01:**
   Khi sửa đổi hoặc bổ sung hàm trong Service cũ, phải đảm bảo các trang web cũ vẫn chạy bình thường.
5. **Tuyệt đối KHÔNG commit mật khẩu hoặc file nhạy cảm:**
   Không commit `application-local.properties`, thư mục `target/`, `.idea/`.

---
*Chúc Nhóm 7 hoàn thành xuất sắc Assignment 02 và đạt điểm tối đa!*

