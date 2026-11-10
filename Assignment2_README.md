# Assignment 02 — FU Cinema Desktop: Ứng dụng quầy vé JavaFX tích hợp Spring Boot
# created by traltb@fe.edu.vn

> **Hệ thống:** FU Cinema Ticket System — phần mở rộng **Desktop** (`fu-cinema-ticket-system`, package `com.fu.cinema.desktop`)
> **Môn:** HSF302 · **Hình thức:** nhóm **4 thành viên** (A – Leader, B, C, D) · làm tiếp trên **repository của Assignment 01**
> **Công nghệ bắt buộc:** Java 21 · JavaFX 21.0.x (FXML + CSS) · Spring Boot 4.1.1 · Spring Data JPA · SQL Server
> **Điều kiện tiên quyết:** đã hoàn thành Assignment 01 (TODO 1–32, release `v1.0.0`) — backend + web Thymeleaf chạy được.
> **Gợi ý lời giải:** `Assignment_Guide.md` · 

---

## Mục lục

1. [Giới thiệu](#1-giới-thiệu)
2. [Phạm vi](#2-phạm-vi)
3. [Kiến trúc bắt buộc](#3-kiến-trúc-bắt-buộc)
4. [Yêu cầu kỹ thuật (TECH-11 → TECH-18)](#4-yêu-cầu-kỹ-thuật)
5. [Quy tắc nghiệp vụ áp dụng](#5-quy-tắc-nghiệp-vụ-áp-dụng)
6. [Danh sách màn hình](#6-danh-sách-màn-hình)
7. [Danh sách TODO (33–46)](#7-danh-sách-todo-3346)
8. [Tổ chức nhóm 4 người](#8-tổ-chức-nhóm-4-người)
9. [Quy trình Git](#9-quy-trình-git)
10. [Kịch bản kiểm thử (TC-D01 → TC-D20)](#10-kịch-bản-kiểm-thử)
11. [Checklist kiểm tra theo TODO](#11-checklist-kiểm-tra-theo-todo)
12. [Checklist nộp bài](#12-checklist-nộp-bài)
13. [Thang điểm](#13-thang-điểm)
14. [Nộp bài & demo](#14-nộp-bài--demo)

---

## 1. Giới thiệu

Rạp **FU Cinema** trang bị máy tính tại quầy. Nhân viên quầy (tài khoản **ADMIN**) cần một **ứng dụng desktop** để:

- **Bán vé trực tiếp** cho khách đến rạp trên **sơ đồ ghế**.
- Quản lý **thể loại, phim, phòng chiếu, suất chiếu**.
- **Xác nhận / huỷ** các đơn khách đặt online trên website.
- Xem **dashboard** và **báo cáo doanh thu** có biểu đồ, xuất file CSV.

Ứng dụng desktop **dùng lại toàn bộ** tầng `service` → `repository` → `entity` của Assignment 01 và **dùng chung database** `FUCinemaTicketDB` với website. Website và ứng dụng desktop phải chạy **đồng thời** được.

**Mục tiêu học tập:** sau bài này sinh viên có thể

- Xây dựng ứng dụng JavaFX theo mô hình **FXML + Controller + CSS**, dùng **binding**, **ObservableList**, **TableView**, **Dialog**, **Chart**.
- Tích hợp **Spring Boot** vào vòng đời JavaFX; controller FXML là **Spring bean**.
- Tái sử dụng tầng nghiệp vụ cho nhiều giao diện (web + desktop) — đúng tinh thần **3-Layer**.
- Xử lý **đa luồng** đúng cách (`Task`, FX Application Thread), tránh `LazyInitializationException`.
- Làm việc nhóm 4 người theo quy trình **issue → nhánh → PR → review → merge**.

---

## 2. Phạm vi

| Có trong ứng dụng desktop | Không làm (đã có trên web) |
|---|---|
| Đăng nhập / đăng xuất — **chỉ ADMIN** | Đăng ký, hồ sơ, đổi mật khẩu khách hàng |
| Dashboard KPI + biểu đồ | Lịch sử đơn của khách, huỷ đơn phía khách |
| CRUD Thể loại, Phim | Đánh giá phim |
| CRUD Phòng chiếu, Suất chiếu | Quản lý khách hàng đầy đủ (desktop chỉ **tìm & chọn** khách) |
| **Bán vé tại quầy** trên sơ đồ ghế | REST API |
| Quản lý đơn: lọc, chi tiết, xác nhận, huỷ | |
| Báo cáo doanh thu: biểu đồ, bảng, xuất CSV | |

> Các mục "Không làm" có thể làm thêm để lấy điểm **bonus** (mục 7, Part R).

---

## 3. Kiến trúc bắt buộc

### 3.1. Tổ chức mã nguồn

- **Cùng repository, cùng project Maven** với Assignment 01. **Không** tạo project mới, **không** sửa entity.
- Thêm package `com.fu.cinema.desktop` và **entry point thứ hai** `DesktopLauncher`.
- Web chạy bằng main cũ `FuCinemaTicketSystemApplication`; desktop chạy bằng `DesktopLauncher`.

```
                      ┌──────────────── fu-cinema-ticket-system (1 project, 1 DB) ───────────────┐
 Trình duyệt ──HTTP──►│ controller/* (Thymeleaf)  ─┐                                              │
                      │                            ├─► service/* ─► repository/* ─► entity/* ─► SQL Server
 JavaFX (quầy vé) ───►│ desktop/controller/* ─► desktop/facade/*                                   │
                      │  (FXML, Spring bean)         (map entity → record, chỉ gọi service)        │
                      └────────────────────────────────────────────────────────────────────────────┘
```

### 3.2. Cấu trúc package bắt buộc

```
src/main/java/com/fu/cinema/desktop/
├── DesktopLauncher.java          main() — KHÔNG extends Application
├── CinemaFxApp.java              extends Application (init / start / stop)
├── StageReadyEvent.java
├── StageInitializer.java
├── navigation/                   View (enum), SceneManager
├── session/                      DesktopSession
├── facade/                       *Facade — @Service, chỉ gọi service có sẵn
├── model/                        record hiển thị trên UI (*Row, *Item, *Data)
├── controller/                   controller FXML (Spring bean, prototype)
├── component/                    SeatMapView
└── util/                         FxAsync, AlertUtil, FxValidator, MoneyUtil, CsvExporter
src/main/resources/
├── application-desktop.properties
└── desktop/
    ├── fxml/                     *.fxml
    └── css/desktop.css
```

### 3.3. Luồng gọi

```
FXML (View) → Controller (desktop) → Facade (desktop) → Service (Assignment 01) → Repository → Entity
```

- Controller **chỉ** gọi Facade. Facade **chỉ** gọi Service. **Không ai** trong package `desktop` được inject Repository.
- Facade trả về **record** (package `desktop.model`), không trả entity lên UI.

---

## 4. Yêu cầu kỹ thuật

| Mã | Yêu cầu |
|---|---|
| **TECH-11** | JavaFX **21.0.x**; giao diện bằng **FXML** (được dùng Scene Builder); CSS trong file riêng `desktop/css/desktop.css`; **không** dùng `module-info.java`. |
| **TECH-12** | Spring khởi động trong `Application.init()` bằng `SpringApplicationBuilder` với `WebApplicationType.NONE`, profile `desktop`; đóng context trong `stop()`. Class chứa `main()` **không** kế thừa `Application`. Tắt DevTools restart khi chạy desktop. |
| **TECH-13** | Controller FXML là Spring bean `@Component @Scope("prototype")`, **constructor injection**; FXML được nạp qua `SceneManager` với `setControllerFactory(context::getBean)`. |
| **TECH-14** | UI chỉ làm việc với **record**. Mapping entity → record thực hiện **bên trong transaction** ở Facade (`@Transactional(readOnly = true)` cho method đọc, `@Transactional` cho method ghi). Không được có `LazyInitializationException`. |
| **TECH-15** | **Không** truy vấn DB trên FX Application Thread: mọi lời gọi Facade chạy trong `javafx.concurrent.Task` (qua `FxAsync`); **không** cập nhật UI từ background thread. Khi đang tải: hiện chỉ báo và khoá nút liên quan. |
| **TECH-16** | Validation **2 lớp**: (1) UI — ô bắt buộc, định dạng số/ngày (`TextFormatter`, `Spinner`, `DatePicker`), nút bị disable bằng binding; (2) Bean Validation trên Form DTO có sẵn (`MovieForm`, `ShowtimeForm`…) qua `jakarta.validation.Validator`; (3) lỗi nghiệp vụ `BusinessException` từ service. Lỗi hiển thị bằng `Alert`/label, **không** lộ stack trace. |
| **TECH-17** | Dùng **binding** cho trạng thái enable/disable, tổng tiền, label đếm; `ObservableList` cập nhật bằng `setAll(...)`. |
| **TECH-18** | Web và desktop chạy **đồng thời** trên cùng DB không lỗi; thông tin DB vẫn nằm trong `application-local.properties` (không commit). Thay đổi (nếu có) ở service cũ **không được** làm hỏng web. |

> Các TECH-01 → TECH-10 của Assignment 01 tiếp tục áp dụng.

---

## 5. Quy tắc nghiệp vụ áp dụng

Toàn bộ quy tắc **đã được kiểm tra ở service** (Assignment 01). Desktop phải **thể hiện đúng** kết quả và thông báo:

| Mã | Quy tắc | Thể hiện ở |
|---|---|---|
| BR-03 | Tài khoản `LOCKED` không đăng nhập được | TODO 36 |
| **BR-D1** *(mới)* | Ứng dụng desktop **chỉ** cho tài khoản `ADMIN` đăng nhập; thao tác ghi kiểm tra quyền ở Facade | TODO 36 |
| BR-05 | Không xoá thể loại còn phim | TODO 38 |
| BR-06 | Xoá phim: chưa có suất → xoá; có suất → `STOPPED` | TODO 40 |
| BR-07, BR-08 | Suất chiếu: Start < End, End ≥ Start + Duration, phòng ACTIVE, phim không STOPPED; không chồng giờ cùng phòng | TODO 41 |
| BR-09 | Xoá suất có đơn → `CANCELLED` + huỷ toàn bộ đơn | TODO 41 |
| BR-10 | Suất `OPEN`, chưa bắt đầu; 1–8 ghế; ghế tồn tại và chưa bán | TODO 42, 43 |
| BR-11 | Phim T13/T16/T18: khách đủ tuổi | TODO 43 |
| BR-12 | Tổng tiền = giá × số ghế; Booking + Ticket trong 1 transaction | TODO 43 |
| **BR-D2** *(mới)* | Đơn bán tại quầy: nếu chọn **"Đã thanh toán"** thì đơn chuyển ngay `CONFIRMED`, ngược lại để `PENDING` | TODO 43 |
| BR-13 | `PENDING → CONFIRMED`; `PENDING/CONFIRMED → CANCELLED`; `CANCELLED` không đổi được | TODO 44 |
| BR-15 | Báo cáo: Start ≤ End; chỉ đơn `CONFIRMED`; giảm dần | TODO 45 |

---

## 6. Danh sách màn hình

| Màn hình | FXML | Controller | TODO |
|---|---|---|---|
| Đăng nhập | `login.fxml` | `LoginController` | 36 |
| Khung chính (menu, vùng nội dung, status bar) | `main.fxml` | `MainController` | 34 |
| Dashboard | `dashboard.fxml` | `DashboardController` | 37 |
| Thể loại | `genre.fxml` | `GenreController` | 38 |
| Phim (danh sách) | `movie.fxml` | `MovieController` | 39 |
| Phim (thêm/sửa) | `movie-dialog.fxml` | `MovieDialogController` | 40 |
| Phòng & Suất chiếu (2 tab) | `schedule.fxml` | `ScheduleController` | 41 |
| Bán vé tại quầy | `counter-sale.fxml` | `CounterSaleController` | 42, 43 |
| Quản lý đơn | `booking.fxml` | `BookingAdminController` | 44 |
| Báo cáo doanh thu | `report.fxml` | `ReportController` | 45 |

---

## 7. Danh sách TODO (33–46)

> Đánh số **tiếp nối** Assignment 01. Mỗi TODO = **ít nhất 1 commit**, đi qua **issue → nhánh → PR → review → merge**. Cột **Người làm** theo phân công mục 8.

### Part K — Nền tảng desktop (1.5 điểm)

| TODO | Người làm | Yêu cầu | Tiêu chí chấp nhận (AC) |
|---|---|---|---|
| **TODO 33** | A | **Tích hợp JavaFX**: thêm `javafx-controls`, `javafx-fxml` 21.0.x vào `pom.xml`; tạo `DesktopLauncher`, `CinemaFxApp`, `StageReadyEvent`, `StageInitializer`; tạo `application-desktop.properties`; tắt DevTools restart. | Chạy `DesktopLauncher` → cửa sổ mở; log **không** có "Tomcat started"; web vẫn chạy bằng main cũ; đóng cửa sổ → process Java kết thúc; `mvn clean verify` pass. |
| **TODO 34** | A | **Điều hướng**: enum `View` (đường dẫn FXML + tiêu đề), `SceneManager` (Spring bean) nạp FXML với `setControllerFactory`; `main.fxml` là `BorderPane`: menu bên trái/trên, vùng `center` để nạp màn hình con, status bar (tên người đăng nhập, đồng hồ). | Chuyển giữa các màn hình không mở cửa sổ mới; controller `@Scope("prototype")`; CSS nạp từ file. |
| **TODO 35** | B | **Tiện ích dùng chung**: `FxAsync` (chạy `Task` trên virtual thread, callback thành công/thất bại trên FX thread), `AlertUtil` (thông báo theo loại exception dùng *switch pattern matching*), `FxValidator` (Bean Validation → tô đỏ ô lỗi), `MoneyUtil` (định dạng VNĐ), `PageResult<T>`. | Không có lời gọi Facade nào nằm ngoài `FxAsync`; lỗi không hiện stack trace; `BusinessException` hiện đúng message của service. |

### Part L — Đăng nhập & phân quyền (1.0 điểm)

| TODO | Người làm | Yêu cầu | AC |
|---|---|---|---|
| **TODO 36** | A | **Đăng nhập desktop**: `login.fxml` (username, `PasswordField`, nút Đăng nhập là `defaultButton`, disable khi ô trống); gọi `AuthService.login` qua `AuthFacade` trong `Task`; lưu `SessionUser` vào `DesktopSession` (Spring singleton thay `HttpSession`); chỉ `ADMIN` (BR-D1); đăng xuất xoá session, về màn hình đăng nhập; mọi method ghi của Facade gọi `desktopSession.requireAdmin()`. | Sai mật khẩu / LOCKED → báo lỗi (BR-03); tài khoản CUSTOMER bị từ chối; status bar hiện tên người dùng; sau đăng xuất không quay lại màn hình cũ được. |

### Part M — Dashboard (0.75 điểm)

| TODO | Người làm | Yêu cầu | AC |
|---|---|---|---|
| **TODO 37** | D | **Dashboard**: 3 thẻ KPI (phim đang chiếu, số khách hàng, đơn PENDING); `BarChart` "Suất chiếu hôm nay theo phòng" (Q11); nút **Làm mới** + tự làm mới mỗi 60 giây bằng `Timeline`. | Số liệu khớp trang `/admin` của web; tạo suất mới trên web → làm mới thấy thay đổi; `Timeline` dừng khi rời màn hình. |

### Part N — Quản lý danh mục (2.0 điểm)

| TODO | Người làm | Yêu cầu | AC |
|---|---|---|---|
| **TODO 38** | C | **Thể loại**: `TableView` (tên, mô tả, số phim nếu có) + form; thêm/sửa/xoá qua `GenreForm`; xoá có `Alert` xác nhận. | Xoá thể loại còn phim → thông báo BR-05, không xoá; tên trùng → báo lỗi; validate rỗng/độ dài trên UI. |
| **TODO 39** | C | **Danh sách phim**: tìm theo tên (debounce 300 ms), `ComboBox` thể loại & trạng thái (có "Tất cả"), sắp xếp (ngày khởi chiếu / tên), `Pagination` 10 dòng/trang; dòng `STOPPED` tô xám bằng `PseudoClass`; cột ngày/thời lượng định dạng bằng `setCellFactory`; context menu Sửa/Xoá; double-click mở Sửa. | Các tiêu chí lọc kết hợp được; đổi bộ lọc → về trang 1; dùng record + lambda (không `PropertyValueFactory` với record). |
| **TODO 40** | C | **Thêm/sửa/xoá phim**: `Dialog<MovieForm>` (FXML riêng) gồm tên, đạo diễn, `Spinner` thời lượng, `DatePicker` khởi chiếu, `ComboBox` độ tuổi/thể loại/trạng thái, poster URL, mô tả; nút Lưu bị chặn bằng *event filter* khi có lỗi validate (ô lỗi viền đỏ + tooltip); xoá theo BR-06. | Thời lượng ngoài [30, 300] → báo lỗi ngay trên dialog, dialog không đóng; xoá phim có suất → "đã chuyển STOPPED"; phim chưa có suất → "đã xoá". |
| **TODO 41** | D | **Phòng & Suất chiếu** (`TabPane`): tab Phòng — CRUD, cột sức chứa; tab Suất chiếu — lọc theo ngày/phim/phòng + `Pagination`, form tạo/sửa (phim chỉ lấy `findSchedulable`, phòng `ACTIVE`, `DatePicker` + `Spinner` giờ/phút, giá vé), **gợi ý EndTime** = Start + Duration + 15' tự cập nhật; xoá theo BR-09. | Trùng giờ cùng phòng → thông báo chỉ rõ suất bị trùng (BR-08); phòng MAINTENANCE không có trong lựa chọn; xoá suất có đơn → "đã huỷ suất và các đơn". |

### Part O — Bán vé tại quầy (2.0 điểm)

| TODO | Người làm | Yêu cầu | AC |
|---|---|---|---|
| **TODO 42** | D | **Component `SeatMapView`** (tái sử dụng được): dựng từ `ShowtimeService.getSeatMap`; nhãn hàng A, B…; mỗi ghế là `ToggleButton` (style class `seat`, pseudo-class `:sold`); ghế đã bán bị disable; hình "MÀN HÌNH" và chú thích màu; công khai `ObservableList<String> getSelectedSeats()`; tối đa **8 ghế** (ghế thứ 9 tự bỏ chọn + cảnh báo). | Phòng 8×12 hiện đủ 96 ghế; ghế đã bán không chọn được; danh sách ghế chọn cập nhật tức thì. |
| **TODO 43** | B | **Màn hình bán vé**: (1) tìm & chọn khách (tên – SĐT – tuổi); (2) chuỗi **phim → suất chiếu → sơ đồ ghế** (đổi phim thì xoá suất & ghế); (3) tổng tiền **bind** theo ghế chọn × giá; (4) nút "Bán vé" chỉ bật khi đủ khách + suất + ≥ 1 ghế; (5) checkbox **"Đã thanh toán"** (BR-D2); (6) gọi `BookingService.create` trong `Task`; thành công → hộp thoại tóm tắt (mã đơn, ghế, tổng tiền, trạng thái) + tải lại sơ đồ; lỗi → thông báo + tải lại sơ đồ. | BR-10, BR-11, BR-12, BR-D2 đúng; **tranh chấp ghế** web ↔ desktop: chỉ 1 bên thành công, DB không có vé trùng; UI không đơ khi đặt. |

### Part P — Đơn đặt vé & báo cáo (1.75 điểm)

| TODO | Người làm | Yêu cầu | AC |
|---|---|---|---|
| **TODO 44** | B | **Quản lý đơn**: lọc trạng thái, khoảng ngày đặt (2 `DatePicker`), từ khoá khách; `TableView` + `Pagination`, mới nhất trước; cột tổng tiền định dạng VNĐ, trạng thái tô màu; double-click → chi tiết (khách, phim, phòng, giờ chiếu, danh sách ghế, tổng tiền); nút **Xác nhận**/**Huỷ** bật theo trạng thái dòng (binding) và có `Alert` xác nhận. | Sai luồng trạng thái → lỗi BR-13; chi tiết đơn không lỗi Lazy; đơn tạo trên web xuất hiện sau khi làm mới. |
| **TODO 45** | B | **Báo cáo doanh thu**: chọn Start/End (`DatePicker`, chặn chọn End < Start); `ReportService.generate`; (a) `BarChart` doanh thu theo phim + tooltip "Phim: x vé – y đ"; (b) `PieChart` tỷ trọng số vé; (c) bảng đơn CONFIRMED giảm dần ngày; (d) dòng tổng; **Xuất CSV** (`FileChooser`, UTF-8 có BOM, escape dấu phẩy/ngoặc kép). | Kỳ không có dữ liệu → "Không có dữ liệu", nút xuất CSV disable; số liệu khớp `/admin/reports`; Cancel `FileChooser` không báo lỗi; mở CSV bằng Excel đúng tiếng Việt. |

### Part Q — Hoàn thiện (1.0 điểm)

| TODO | Người làm | Yêu cầu | AC |
|---|---|---|---|
| **TODO 46** | A (+ cả nhóm) | Cập nhật `README.md` (mục **Desktop**: yêu cầu, cách chạy 2 main, tài khoản, ảnh chụp ≥ 6 màn hình desktop, bảng phân công); **≥ 3 test** cho Facade (`@SpringBootTest` + `@ActiveProfiles("test")`, H2); release **`v2.0.0`**. | Người khác clone → chạy được web và desktop chỉ theo README; `mvn clean verify` pass; release có trên GitHub. |

### Part R — Bonus (tối đa +1.0)

| Mã | Nội dung | Điểm |
|---|---|---|
| B1 | **In vé** bằng `PrinterJob` sau khi bán | +0.25 |
| B2 | **Kiosk mode cho khách**: CUSTOMER đăng nhập → xem phim, đặt vé (tái dùng `SeatMapView`), lịch sử, huỷ đơn (BR-14) | +0.5 |
| B3 | Sơ đồ ghế **tự làm mới** mỗi 10 giây khi đang mở màn hình bán vé | +0.25 |
| B4 | Tách **multi-module** (`cinema-core`, `cinema-web`, `cinema-desktop`), web & desktop vẫn chạy | +0.5 |
| B5 | Theme sáng/tối đổi lúc chạy, hoặc đa ngôn ngữ (`ResourceBundle`) | +0.25 |
| B6 | Đóng gói bản cài đặt bằng `jpackage` | +0.25 |

---

## 8. Tổ chức nhóm 4 người

### 8.1. Vai trò

| Thành viên | Vai trò | Phụ trách code | Review chính |
|---|---|---|---|
| **A — Leader** | Kiến trúc desktop, cấu hình, release | TODO 33, 34, 36, 46 | PR của D (TODO 37, 42), B (TODO 43) |
| **B** | Luồng bán vé, đơn, báo cáo | TODO 35, 43, 44, 45 | PR của A (TODO 34), C (TODO 38, 40) |
| **C** | Danh mục phim | TODO 38, 39, 40 | PR của A (TODO 33, 46), D (TODO 41), B (TODO 44) |
| **D** | Lịch chiếu, sơ đồ ghế, dashboard | TODO 37, 41, 42 | PR của B (TODO 35, 45), A (TODO 36, 46), C (TODO 39) |

> Mỗi thành viên **bắt buộc** có ít nhất **3 PR desktop** được merge và **review ít nhất 3 PR** của người khác. Người viết **không** tự approve.

### 8.2. Danh sách issue (Leader tạo đầu Sprint 4)

> Số issue thực tế **tiếp nối** số cuối cùng trên GitHub (issue và PR dùng chung bộ đếm) → đặt tên nhánh theo **số thật**.
> Milestone: `Sprint 4 — Desktop foundation` · `Sprint 5 — Desktop features & Release v2.0.0` · Label mới: `module: desktop`.

| Issue | Title | Người làm | Reviewer | Milestone | Phụ thuộc |
|---|---|---|---|---|---|
| D1 | `[FEATURE] JavaFX bootstrap & desktop profile (TODO 33)` | A | C | Sprint 4 | — |
| D2 | `[FEATURE] Desktop navigation & main layout (TODO 34)` | A | B | Sprint 4 | D1 |
| D3 | `[FEATURE] Desktop shared utilities (TODO 35)` | B | D | Sprint 4 | D1 |
| D4 | `[FEATURE] Desktop login & DesktopSession (TODO 36)` | A | D | Sprint 4 | D2, D3 |
| D5 | `[FEATURE] Desktop dashboard with charts (TODO 37)` | D | A | Sprint 4 | D4 |
| D6 | `[FEATURE] Desktop genre management (TODO 38)` | C | B | Sprint 4 | D4 |
| D7 | `[FEATURE] Desktop movie list, filter & pagination (TODO 39)` | C | D | Sprint 5 | D6 |
| D8 | `[FEATURE] Desktop movie dialog & delete (TODO 40)` | C | B | Sprint 5 | D7 |
| D9 | `[FEATURE] Desktop rooms & showtimes (TODO 41)` | D | C | Sprint 5 | D4 |
| D10 | `[FEATURE] SeatMapView component (TODO 42)` | D | A | Sprint 5 | D9 |
| D11 | `[FEATURE] Counter sale screen (TODO 43)` | B | A | Sprint 5 | D10 |
| D12 | `[FEATURE] Desktop booking management (TODO 44)` | B | C | Sprint 5 | D4 |
| D13 | `[FEATURE] Desktop revenue report & CSV export (TODO 45)` | B | D | Sprint 5 | D4 |
| D14 | `[DOCS] Desktop README, tests & release v2.0.0 (TODO 46)` | A | C + D | Sprint 5 | tất cả |

### 8.3. Lịch làm việc đề nghị

| Tuần | Mốc | A | B | C | D |
|---|---|---|---|---|---|
| 1 | Sprint 4 bắt đầu | D1 → D2 | D3 | Học Bài 4 (Javafx_exercises), dựng FXML phim | Học Bài 5, dựng prototype sơ đồ ghế |
| 2 | Sprint 4 kết thúc | D4 | D12 (bắt đầu) | D6 | D5 |
| 3 | Sprint 5a | Review, hỗ trợ | D12 → D13 | D7 → D8 | D9 → D10 |
| 4 | Sprint 5b | D14, release | D11 | Kiểm thử chéo TC-D06–D12 | Kiểm thử chéo TC-D13–D16 |

---

## 9. Quy trình Git

Giữ nguyên quy trình Assignment 01 (`Assignment1_teamwork.md`), bổ sung:

- Nhánh: `feature/<số-issue>-desktop-<tên>` (ví dụ `feature/61-desktop-counter-sale`).
- Commit theo Conventional Commits, scope **`desktop`**:
  ```
  feat(desktop): add javafx dependencies and desktop launcher        Refs #<n>
  feat(desktop): add seat map component                              Refs #<n>
  fix(desktop): reload seat map after booking failure                Refs #<n>
  docs(readme): add desktop setup guide and screenshots              Refs #<n>
  ```
- PR mô tả: ảnh chụp màn hình desktop + các TC-D đã chạy.
- `CODEOWNERS`: thêm `/src/main/java/com/fu/cinema/desktop/` và `/src/main/resources/desktop/` → A; thay đổi `pom.xml` cần A duyệt.
- Không sửa file service/entity của người khác khi chưa có issue `module: infra` và người sở hữu cùng review.

---

## 10. Kịch bản kiểm thử

> Chuẩn bị: dữ liệu seed của Assignment 01; chạy **web** và **desktop** cùng lúc.

| TC | Bước | Kết quả mong đợi | TODO |
|---|---|---|---|
| TC-D01 | Chạy `DesktopLauncher` | Màn hình đăng nhập mở; log không có Tomcat; web vẫn truy cập được | 33 |
| TC-D02 | Đăng nhập `admin@fucinema.vn` sai mật khẩu | Thông báo sai thông tin | 36 |
| TC-D03 | Đăng nhập `an.nv@gmail.com` / `Customer@123` | Bị từ chối: chỉ dành cho nhân viên | 36 |
| TC-D04 | Đăng nhập admin đúng | Vào màn hình chính, status bar có tên; dashboard khớp `/admin` | 34, 36, 37 |
| TC-D05 | Web tạo suất chiếu hôm nay → Desktop bấm Làm mới dashboard | Biểu đồ cập nhật | 37 |
| TC-D06 | Thêm "Tài liệu" → sửa → xoá thể loại | Thành công, bảng cập nhật | 38 |
| TC-D07 | Xoá thể loại "Hành động" | Báo lỗi BR-05, không xoá | 38 |
| TC-D08 | Lọc phim theo thể loại + trạng thái + từ khoá, chuyển trang | Kết hợp đúng; đổi lọc về trang 1 | 39 |
| TC-D09 | Thêm phim thời lượng 20 phút | Dialog không đóng, ô thời lượng viền đỏ + thông báo | 40 |
| TC-D10 | Xoá phim đã có suất chiếu | "Đã chuyển STOPPED", dòng tô xám | 40 |
| TC-D11 | Tạo suất trùng giờ cùng phòng | Báo lỗi chỉ rõ suất bị trùng | 41 |
| TC-D12 | Chọn phim 120 phút, bắt đầu 19:00 | EndTime gợi ý 21:15 | 41 |
| TC-D13 | Bán vé: khách An → phim → suất → 3 ghế, tick "Đã thanh toán" | Tổng = 3 × giá; bán thành công, đơn CONFIRMED; ghế chuyển đã bán | 42, 43 |
| TC-D14 | Chọn ghế thứ 9 | Không chọn được, cảnh báo tối đa 8 ghế | 42 |
| TC-D15 | Khách Bình (< 16 tuổi) mua phim T18 | Lỗi BR-11 | 43 |
| TC-D16 | Web (An) và Desktop cùng chọn 1 ghế của cùng suất, bấm gần đồng thời | 1 bên thành công, bên kia báo ghế đã có người đặt; DB không có vé trùng | 43 |
| TC-D17 | Lọc đơn PENDING → Xác nhận; chọn đơn CONFIRMED → Huỷ | Trạng thái đổi đúng, nút bật/tắt theo trạng thái | 44 |
| TC-D18 | Chọn đơn CANCELLED | Nút Xác nhận/Huỷ bị disable | 44 |
| TC-D19 | Báo cáo kỳ có dữ liệu → Xuất CSV → mở bằng Excel | Chart, bảng, tổng khớp web; CSV đúng tiếng Việt | 45 |
| TC-D20 | Đăng xuất → đóng cửa sổ | Về đăng nhập; đóng app → process kết thúc, không treo | 33, 36 |

---

## 11. Checklist kiểm tra theo TODO

### Part K — Nền tảng
- [ ] **TODO 33** — `pom.xml` có `javafx-controls`, `javafx-fxml` 21.0.x; không có `module-info.java`
- [ ] **TODO 33** — `DesktopLauncher` không extends `Application`; tắt `spring.devtools.restart.enabled`
- [ ] **TODO 33** — `CinemaFxApp.init()` dùng `SpringApplicationBuilder(...).web(WebApplicationType.NONE).headless(false).profiles("desktop")`; `stop()` gọi `context.close()`
- [ ] **TODO 33** — `StageInitializer` bắt `StageReadyEvent`, hiển thị màn hình đăng nhập
- [ ] **TODO 33** — Web vẫn chạy bằng main cũ; `mvn clean verify` pass
- [ ] **TODO 34** — enum `View` + `SceneManager` (Spring bean) dùng `setControllerFactory(context::getBean)`
- [ ] **TODO 34** — `main.fxml` là `BorderPane`; chuyển màn hình bằng `setCenter`; status bar có tên + đồng hồ
- [ ] **TODO 34** — Mọi controller `@Component @Scope("prototype")`, constructor injection
- [ ] **TODO 35** — `FxAsync` chạy `Task` trên virtual thread; callback trên FX thread; có hiển thị/khoá UI khi đang chạy
- [ ] **TODO 35** — `AlertUtil` phân loại exception bằng switch pattern matching; không lộ stack trace
- [ ] **TODO 35** — `FxValidator` dùng `jakarta.validation.Validator`, tô đỏ ô lỗi + tooltip
- [ ] **TODO 35** — `MoneyUtil` định dạng `1.234.000 đ`

### Part L — Đăng nhập
- [ ] **TODO 36** — Nút Đăng nhập disable khi ô trống (binding); Enter để đăng nhập
- [ ] **TODO 36** — Gọi `AuthService.login` trong `Task`; chỉ ADMIN được vào (BR-D1); LOCKED bị chặn (BR-03)
- [ ] **TODO 36** — `DesktopSession` lưu `SessionUser`; Facade ghi gọi `requireAdmin()`
- [ ] **TODO 36** — Đăng xuất xoá session, về màn hình đăng nhập

### Part M — Dashboard
- [ ] **TODO 37** — 3 KPI khớp `/admin`; `BarChart` suất chiếu hôm nay theo phòng
- [ ] **TODO 37** — Nút Làm mới + `Timeline` 60 s; `Timeline` dừng khi rời màn hình

### Part N — Danh mục
- [ ] **TODO 38** — CRUD thể loại; xác nhận trước khi xoá; BR-05
- [ ] **TODO 39** — Tìm (debounce) + lọc thể loại/trạng thái + sắp xếp + `Pagination`, đổi lọc về trang 1
- [ ] **TODO 39** — Record + lambda cell value; `setCellFactory` định dạng; `setRowFactory` + `PseudoClass` cho `STOPPED`
- [ ] **TODO 40** — `Dialog<MovieForm>` có `setResultConverter`; event filter chặn Lưu khi lỗi
- [ ] **TODO 40** — Bean Validation của `MovieForm` hiển thị cạnh ô; BR-06 thông báo đúng
- [ ] **TODO 41** — CRUD phòng, cột sức chứa; CRUD suất chiếu; lọc ngày/phim/phòng
- [ ] **TODO 41** — EndTime gợi ý tự cập nhật; BR-07, BR-08 (chỉ rõ suất trùng), BR-09

### Part O — Bán vé
- [ ] **TODO 42** — `SeatMapView` là component riêng (package `component`), dựng từ `getSeatMap`
- [ ] **TODO 42** — Ghế đã bán disable, pseudo-class `:sold`; chú thích màu; tối đa 8 ghế
- [ ] **TODO 43** — Chuỗi phụ thuộc khách → phim → suất → ghế; đổi phim xoá suất & ghế
- [ ] **TODO 43** — Tổng tiền và trạng thái nút "Bán vé" dùng binding
- [ ] **TODO 43** — `BookingService.create` trong `Task`; "Đã thanh toán" → `confirm` (BR-D2); tải lại sơ đồ sau bán/lỗi
- [ ] **TODO 43** — BR-10, BR-11, BR-12 và TC-D16 tranh chấp ghế pass

### Part P — Đơn & báo cáo
- [ ] **TODO 44** — Lọc kết hợp + `Pagination`, mới nhất trước; chi tiết đơn đủ thông tin, không Lazy
- [ ] **TODO 44** — Nút Xác nhận/Huỷ bind theo trạng thái; `Alert` xác nhận; BR-13
- [ ] **TODO 45** — DatePicker chặn End < Start; BarChart + tooltip; PieChart; bảng đơn; dòng tổng
- [ ] **TODO 45** — Kỳ trống → "Không có dữ liệu", nút CSV disable; CSV UTF-8 BOM, escape đúng

### Part Q — Hoàn thiện
- [ ] **TODO 46** — README mục Desktop đầy đủ + ≥ 6 ảnh + bảng phân công
- [ ] **TODO 46** — ≥ 3 test Facade chạy H2; `mvn clean verify` pass
- [ ] **TODO 46** — Release `v2.0.0` trên `main`

---

## 12. Checklist nộp bài

**Build & chạy**
- [ ] `mvn clean verify` → BUILD SUCCESS (test cũ + test desktop)
- [ ] Web (`FuCinemaTicketSystemApplication`) và desktop (`DesktopLauncher`) chạy **đồng thời** được
- [ ] Không commit `application-local.properties`, `target/`, `.idea/`

**Kiến trúc** (kiểm tra bằng lệnh)
- [ ] `grep -rn "Repository" src/main/java/com/fu/cinema/desktop` → **rỗng**
- [ ] `grep -rln "import com.fu.cinema.entity" src/main/java/com/fu/cinema/desktop/controller src/main/java/com/fu/cinema/desktop/component` → **rỗng** (UI không dùng entity)
- [ ] `grep -rn "PropertyValueFactory" src/main/java/com/fu/cinema/desktop` → rỗng hoặc chỉ dùng với class có getter
- [ ] Không có `module-info.java`; không sửa entity

**JavaFX**
- [ ] Mọi lời gọi Facade nằm trong `FxAsync`; UI không đơ khi tải
- [ ] FXML cho mọi màn hình; CSS trong `desktop/css/desktop.css`
- [ ] Binding cho enable/disable, tổng tiền; `ObservableList.setAll`
- [ ] Mọi `showAndWait()` xử lý cả OK và Cancel

**Nghiệp vụ**
- [ ] TC-D01 → TC-D20 đều ✅ (ghi kết quả vào README hoặc PR cuối)

**Làm việc nhóm**
- [ ] 14 issue desktop, label `module: desktop`, đúng milestone
- [ ] Mỗi thành viên ≥ 3 PR desktop merge, ≥ 3 lượt review
- [ ] Commit theo Conventional Commits, có `Refs #<n>`; PR có `Closes #<n>`

---

## 13. Thang điểm

| Phần | Nội dung | Điểm |
|---|---|---|
| Part K | Nền tảng: TODO 33–35 | 1.5 |
| Part L | Đăng nhập & phân quyền: TODO 36 | 1.0 |
| Part M | Dashboard: TODO 37 | 0.75 |
| Part N | Thể loại, phim, phòng, suất chiếu: TODO 38–41 | 2.0 |
| Part O | Sơ đồ ghế & bán vé tại quầy: TODO 42–43 | 2.0 |
| Part P | Quản lý đơn & báo cáo: TODO 44–45 | 1.75 |
| Part Q | README, test, release: TODO 46 | 1.0 |
| **Tổng** | | **10.0** |
| Bonus | Part R (B1–B6) | tối đa +1.0 |

**Trừ điểm (−0.5 mỗi lỗi):**

| Lỗi | Cách phát hiện |
|---|---|
| Inject Repository trong package `desktop` | `grep` mục 12 |
| Truy vấn DB trên FX thread (UI đơ) hoặc cập nhật UI từ background thread | Demo + đọc code |
| Entity hiển thị trực tiếp trên UI / `LazyInitializationException` khi demo | Đọc code, demo chi tiết đơn |
| Thay đổi làm **website hỏng** | Demo web sau khi demo desktop |
| Commit mật khẩu thật, `target/`, `.idea/` | `git ls-files` |
| Thành viên không có PR desktop được merge | GitHub Insights → Contributors |

**Điểm cá nhân:** điểm nhóm × hệ số đóng góp (0.6 – 1.0) dựa trên PR, review, commit của từng người.

---

## 14. Nộp bài & demo

**Nộp:**
1. Link GitHub repository (giảng viên quyền *Read*), nhánh `main` có release **`v2.0.0`**.
2. `README.md` có mục Desktop, ảnh chụp, bảng phân công, kết quả TC-D01 → TC-D20.
3. File Word theo template (mục lục tự động, đánh số tiêu đề, ảnh từng bước) — bổ sung chương **Desktop**.

**Demo (15 phút/nhóm):**

| Phút | Nội dung | Người trình bày |
|---|---|---|
| 0–2 | Kiến trúc: 1 project, 2 entry point, facade | A |
| 2–5 | Đăng nhập, dashboard, danh mục phim | A, C |
| 5–9 | Lịch chiếu, bán vé tại quầy, **TC-D16 tranh chấp ghế web ↔ desktop** | D, B |
| 9–12 | Quản lý đơn, báo cáo, xuất CSV | B |
| 12–15 | Giảng viên hỏi ngẫu nhiên từng thành viên về code của mình | Cả nhóm |
