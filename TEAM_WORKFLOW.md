# FU Cinema Ticket System — Team & AI Agent Workflow Guide

> **Dành cho:** Thành viên nhóm HSF302 và các **AI Assistant / AI Agent** (Antigravity, Cursor, GitHub Copilot, Claude, ChatGPT...) hỗ trợ lập trình trên máy của từng thành viên.
> **Tài liệu tham chiếu cốt lõi:** `Assignment1_ReadMe.md` (Đặc tả nghiệp vụ), `Assignment1_solution.md` (Hướng dẫn chi tiết từng TODO), `Assignment1_teamwork.md` (Quy chuẩn Git Flow).

---

## 🤖 PHẦN 1: CHỈ THỊ BẮT BUỘC DÀNH CHO AI AGENT (AI AGENT INSTRUCTIONS)

Nếu bạn là một **AI Agent** đang đọc file này để hỗ trợ lập trình viên, bạn **BẮT BUỘC** phải tuân thủ nghiêm ngặt các quy tắc sau đây:

### 1. Quy tắc số 1: TUYỆT ĐỐI KHÔNG VIẾT COMMENT (STRICT 0 COMMENTS)
- **CẤM:** Không được chèn bất kỳ dòng comment nào trong mã nguồn:
  - Java: Không dùng `//`, `/* ... */`, JavaDoc `/** ... */`.
  - HTML / Thymeleaf: Không dùng `<!-- ... -->`.
  - Properties: Không dùng `# ...`.
  - CSS / JS: Không dùng `/* ... */`, `// ...`.
- **Lý do:** Đây là tiêu chí chấm điểm tự động / quy định chấm bài nghiêm ngặt của môn học. Vi phạm sẽ bị trừ điểm trực tiếp.

### 2. Quy tắc số 2: BẢO MẬT CẤU HÌNH CÁ NHÂN (NO CREDENTIAL LEAKS)
- Tuyệt đối **KHÔNG** sửa hoặc ghi thông tin tài khoản/mật khẩu DB vào `src/main/resources/application.properties`.
- Mọi thông tin kết nối máy cá nhân nằm độc quyền trong `src/main/resources/application-local.properties` (file này đã được `.gitignore`).

### 3. Quy tắc số 3: TUÂN THỦ CONVENTIONAL COMMITS & ISSUE TRACKING
- Mỗi chức năng luôn gắn liền với một Issue cụ thể.
- Mọi commit phải có cấu trúc:
  ```text
  <type>(<scope>): <mô tả ngắn>

  Refs #<số-issue>
  ```
  *(Ví dụ: `feat(movie): add movie search by title and genre` kèm `Refs #9`)*.
- Khi mở Pull Request, nội dung phải có từ khóa đóng issue: `Closes #<số-issue>`.

### 4. Quy tắc số 4: KIẾN TRÚC & CÔNG NGHỆ CHUẨN
- **Tech Stack:** Java 21, Spring Boot 4.x, Spring Data JPA, Thymeleaf, Microsoft SQL Server, Lombok, `spring-security-crypto` (chỉ dùng `BCryptPasswordEncoder`).
- **Bảo mật & Phân quyền:** KHÔNG dùng `spring-boot-starter-security`. Sử dụng `HttpSession` kết hợp `HandlerInterceptor` để kiểm tra đăng nhập và phân quyền (`ROLE_ADMIN`, `ROLE_CUSTOMER`).
- **Giao diện:** Thymeleaf kế thừa layout chung tại `src/main/resources/templates/fragments/layout.html` bằng cú pháp `th:replace="~{fragments/layout :: fragmentName}"`.

---

## 👥 PHẦN 2: THIẾT LẬP MÔI TRƯỜNG MÁY MỚI (DÀNH CHO THÀNH VIÊN)

Mỗi thành viên khi vừa tham gia vào repo cần thực hiện lần lượt các bước sau:

### Bước 1: Cấu hình Git trên máy cá nhân
Mở Terminal / PowerShell và chạy:
```powershell
git config --global user.name "Họ và Tên Của Bạn"
git config --global user.email "email_cua_ban@fpt.edu.vn"
git config --global pull.rebase true
git config --global core.autocrlf true
```

### Bước 2: Clone repository & chuyển sang nhánh `develop`
```powershell
git clone https://github.com/HSF302-group7/fu-cinema-ticket-system.git
cd fu-cinema-ticket-system
git switch develop
```

### Bước 3: Tạo Database trên SQL Server
Mở SSMS hoặc Azure Data Studio, kết nối SQL Server và chạy:
```sql
CREATE DATABASE FUCinemaTicketDB;
```

### Bước 4: Tạo cấu hình kết nối DB cá nhân
1. Sao chép file `src/main/resources/application-local.properties.example` thành file mới tên là `src/main/resources/application-local.properties`.
2. Mở file `application-local.properties` vừa tạo, cập nhật đúng thông tin SQL Server máy bạn:
```properties
spring.datasource.url=jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=FUCinemaTicketDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=mat_khau_cua_ban
```
*(Lưu ý: Nếu dùng port mặc định 1433 thì URL là `jdbc:sqlserver://localhost:1433;databaseName=FUCinemaTicketDB;encrypt=true;trustServerCertificate=true`)*.

### Bước 5: Kiểm tra kết nối & khởi chạy
Chạy lệnh kiểm tra:
```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```
Khi log hiển thị `HikariPool-1 - Start completed` và `Started FuCinemaTicketSystemApplication` trên cổng `8080`, việc chuẩn bị môi trường đã hoàn tất!

---

## 🔄 PHẦN 3: QUY TRÌNH LÀM 1 CHỨC NĂNG (VÒNG ĐỜI 1 TASK TỪ A - Z)

Mọi thành viên **KHÔNG ĐƯỢC CODE TRỰC TIẾP** trên nhánh `develop` hoặc `main`. Quy trình thực hiện chuẩn gồm 5 giai đoạn:

```
[Nhận Issue trên Board]
         │
         ▼
[Tạo nhánh feature từ develop]
         │
         ▼
[Code & Test cục bộ (0 comment)]
         │
         ▼
[Đồng bộ rebase develop & Push]
         │
         ▼
[Mở Pull Request & Chờ Review]
         │
         ▼
[Squash and merge & Đóng Issue]
```

### Giai đoạn 1: Nhận việc & Tạo nhánh
1. Mở GitHub repo, vào tab **Projects** $\rightarrow$ chọn **FUCinema Board**.
2. Tìm thẻ Issue được phân công cho mình, kéo từ cột `To do (Sprint)` sang cột `In progress`.
3. Mở Terminal, đồng bộ code mới nhất của `develop` và tạo nhánh làm việc theo quy ước:
   ```powershell
   git switch develop
   git pull
   git switch -c <loại>/<số-issue>-<tên-chức-năng>
   ```
   *Ví dụ Member C làm CRUD phim (Issue #9):*
   ```powershell
   git switch -c feature/9-movie-crud
   ```

### Giai đoạn 2: Lập trình & Commit cục bộ
- Tham khảo kỹ mục TODO tương ứng trong file `Assignment1_solution.md` và các quy tắc nghiệp vụ trong `Assignment1_ReadMe.md`.
- Nhớ nhắc AI Agent của bạn: *"Tuân thủ nghiêm ngặt quy tắc 0 comment trong code!"*.
- Chia nhỏ các commit có ý nghĩa, mỗi commit build được:
  ```powershell
  git add <file>
  git commit -m "feat(movie): implement movie form validation and save logic" -m "Refs #9"
  ```
- Kiểm tra tính đúng đắn trước khi hoàn thiện:
  ```powershell
  .\mvnw.cmd clean compile
  ```

### Giai đoạn 3: Đồng bộ develop và Đẩy code lên GitHub
Trước khi đẩy lên GitHub, luôn rebase với `develop` để đảm bảo không bị xung đột với code người khác vừa merge:
```powershell
git fetch origin
git rebase origin/develop
git push -u origin feature/<số-issue>-<tên-chức-năng>
```

### Giai đoạn 4: Mở Pull Request (PR)
1. Truy cập GitHub, bấm **Compare & pull request** ở thông báo nhánh của bạn.
2. Kiểm tra nhánh: **`base: develop`** $\leftarrow$ **`compare: <nhánh-của-bạn>`**.
3. Đặt tiêu đề theo Conventional Commits, ví dụ:
   `feat(movie): implement movie crud and soft delete`
4. Nội dung PR điền theo mẫu (PR Template):
   - Nêu tóm tắt những gì đã làm.
   - Ghi rõ: `Closes #<số-issue>` (để đóng issue tự động khi merge).
   - Tích chọn đầy đủ checklist.
5. Ở thanh sidebar bên phải:
   - **Reviewers:** Gán Leader hoặc thành viên khác trong nhóm review.
   - **Assignees:** Gán chính bạn.
   - **Projects:** Chọn `FUCinema Board`.
   - **Milestone:** Chọn Sprint tương ứng.
6. Bấm **Create pull request**.
7. Kéo Issue trên Board từ `In progress` sang `In review`.

### Giai đoạn 5: Review & Merge
1. Reviewer mở PR, kiểm tra các tiêu chí:
   - Code có chứa comment không? (Nếu có $\rightarrow$ yêu cầu xóa ngay).
   - Có lộ mật khẩu DB hay file thừa không?
   - Chạy thử `mvn verify` hoặc `mvn clean compile` trên máy có pass không?
2. Nếu mọi thứ đạt chuẩn: Reviewer bấm **Approve**.
3. Người tạo PR hoặc Leader tiến hành:
   - Chọn **Squash and merge** $\rightarrow$ **Confirm squash and merge**.
   - Bấm nút **Delete branch** để xóa nhánh tính năng trên remote.
4. Trở về máy cục bộ, dọn dẹp nhánh tính năng:
   ```powershell
   git switch develop
   git pull
   git branch -d feature/<số-issue>-<tên-chức-năng>
   ```

---

## 📋 PHẦN 4: BẢNG PHÂN CÔNG THEO VAI TRÒ & SPRINT

| Issue | Chức năng (TODO) | Phân công | Module | Nhánh chuẩn | Milestone |
|:---:|---|:---:|:---:|---|:---:|
| **#1** | Repo templates & CODEOWNERS | **A (Leader)** | `infra` | `chore/1-repo-templates` | Sprint 0 (Done) |
| **#2** | Setup dự án, DB, Layout (TODO 1–3) | **A (Leader)** | `infra` | `feature/2-project-setup` | Sprint 0 (Done) |
| **#3** | Entities & Repositories (TODO 4–7, 9, 10) | **A (Leader)** | `infra` | `feature/3-entities-repositories` | Sprint 0 |
| **#4** | Seed data mẫu (TODO 8) | **A (Leader)** | `infra` | `feature/4-seed-data` | Sprint 0 |
| **#5** | Service contracts & Exception (TODO 11–12) | **A (Leader)** | `infra` | `feature/5-service-contracts-error-handling` | Sprint 0 |
| **#6** | Đăng nhập, đăng ký, Interceptor (TODO 13–15) | **A (Leader)** | `auth` | `feature/6-auth` | Sprint 1 |
| **#7** | Quản lý khách hàng admin (TODO 16–17) | **B** | `customer` | `feature/7-customer-admin` | Sprint 1 |
| **#8** | CRUD Thể loại phim (TODO 18) | **C** | `movie` | `feature/8-genre-crud` | Sprint 1 |
| **#9** | CRUD Phim & Soft delete (TODO 19–21) | **C** | `movie` | `feature/9-movie-crud` | Sprint 1 |
| **#10** | Quản lý Phòng chiếu (TODO 22) | **D** | `showtime` | `feature/10-room-crud` | Sprint 1 |
| **#11** | Quản lý Suất chiếu & Trùng lịch (TODO 23) | **D** | `showtime` | `feature/11-showtime-crud` | Sprint 1 |
| **#12** | Quản lý Đơn đặt vé admin (TODO 24–25) | **E** | `booking` | `feature/12-booking-admin` | Sprint 1 |
| **#13** | Trang chủ & Chi tiết phim (TODO 27) | **C** | `movie` | `feature/13-home-movie-detail` | Sprint 2 |
| **#14** | Sơ đồ ghế & Đặt vé (TODO 28) | **D** | `booking` | `feature/14-seat-booking` | Sprint 2 |
| **#15** | Lịch sử vé & Huỷ vé (TODO 29) | **E** | `booking` | `feature/15-booking-history` | Sprint 2 |
| **#16** | Hồ sơ cá nhân & Đổi mật khẩu (TODO 30) | **B** | `customer` | `feature/16-profile` | Sprint 2 |
| **#17** | Báo cáo doanh thu & Thống kê (TODO 26) | **E** | `report` | `feature/17-revenue-report` | Sprint 3 |
| **#18** | Đánh giá phim - Bonus (TODO 31) | **C** | `movie` | `feature/18-movie-review` | Sprint 3 |
| **#19** | Tài liệu README & Release v1.0.0 (TODO 32) | **A (Leader)** | `docs` | `docs/19-readme` | Sprint 3 |

---

## 🎯 PHẦN 5: CHECKLIST TRƯỚC KHI BẤM "CREATE PULL REQUEST"

- [ ] Dự án biên dịch thành công không lỗi: `.\mvnw.cmd clean compile`.
- [ ] Đã chạy thử ứng dụng hoạt động đúng yêu cầu nghiệp vụ.
- [ ] Toàn bộ code sửa đổi **KHÔNG CÓ DÒNG COMMENT NÀO**.
- [ ] Không có file cấu hình nhạy cảm (`application-local.properties`) lọt vào danh sách commit.
- [ ] Nhánh làm việc đã rebase với bản mới nhất của `develop`.
- [ ] Đã gán `Closes #<issue>` trong nội dung PR.
- [ ] Đã gán Reviewer và Milestone trên GitHub PR.
