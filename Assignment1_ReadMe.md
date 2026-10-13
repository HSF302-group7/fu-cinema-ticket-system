# Assignment 01 — Building a Cinema Ticketing Website Using Spring Boot & Spring Data JPA

> **Hệ thống:** FU Cinema Ticket System — **`FUCinemaTicketSystem`**
> **Môn:** HSF302 · **Hình thức:** Làm theo nhóm (xem `Assignment1_teamwork.md`)
> **Công nghệ bắt buộc:** Spring Boot · Spring Data JPA · Thymeleaf (hoặc JSTL) · SQL Server · kiến trúc **3-Layer + Repository Pattern**

---

## Mục lục

1. [Giới thiệu](#1-giới-thiệu)
2. [Thiết kế cơ sở dữ liệu](#2-thiết-kế-cơ-sở-dữ-liệu)
3. [Mục tiêu](#3-mục-tiêu)
4. [Chức năng chính](#4-chức-năng-chính)
5. [Quy tắc nghiệp vụ](#5-quy-tắc-nghiệp-vụ)
6. [Yêu cầu kỹ thuật](#6-yêu-cầu-kỹ-thuật)
7. [Dữ liệu mẫu](#7-dữ-liệu-mẫu)
8. [Danh sách TODO](#8-danh-sách-todo)
9. [Danh sách query bắt buộc](#9-danh-sách-query-bắt-buộc)
10. [Danh sách màn hình & URL](#10-danh-sách-màn-hình--url)
11. [Checklist hoàn thành](#11-checklist-hoàn-thành)
12. [Thang điểm & nộp bài](#12-thang-điểm--nộp-bài)

---

## 1. Giới thiệu

**Hệ thống quản lý bán vé xem phim** (Cinema Ticketing Management System) giúp rạp chiếu phim quản lý và tự động hoá hoạt động kinh doanh: từ quản lý phim, phòng chiếu, lịch chiếu đến bán vé trực tuyến, quản lý khách hàng và báo cáo doanh thu. Các thành phần chính:

- **Đặt vé trực tuyến:** khách hàng xem phim đang chiếu, chọn suất chiếu, chọn **một hoặc nhiều ghế** và đặt vé qua giao diện web; hệ thống hiển thị ghế còn trống theo thời gian thực và xác nhận đơn đặt vé.
- **Quản lý phim & lịch chiếu:** theo dõi danh sách phim (thể loại, đạo diễn, thời lượng, độ tuổi, trạng thái), phòng chiếu và suất chiếu; đảm bảo các suất chiếu trong cùng một phòng không trùng giờ.
- **Quản lý khách hàng:** lưu thông tin liên hệ, giấy tờ tuỳ thân và lịch sử đặt vé; hỗ trợ tra cứu nhanh và chăm sóc khách hàng.

Bạn là lập trình viên của **FUCinemaTicketSystem**. Quản lý dự án yêu cầu xây dựng một **website** bằng **Spring Boot** và **Spring Data JPA** để:

- Quản lý thông tin **khách hàng**.
- Quản lý thông tin **phim** (kèm thể loại, phòng chiếu, suất chiếu).
- Quản lý **giao dịch đặt vé**.

Ứng dụng phải hỗ trợ đầy đủ các thao tác **CRUD** (Create, Read, Update, Delete). Dữ liệu được lưu trong **SQL Server** và truy xuất thông qua **Spring Data JPA**; giao diện dùng **Thymeleaf** (hoặc JSTL).

---

## 2. Thiết kế cơ sở dữ liệu

Database: **`FUCinemaTicketDB`** (SQL Server). Bảng được tạo bằng **Spring Data JPA** (`ddl-auto`) từ entity.

### 2.1 Lược đồ quan hệ

**Account** (<u>AccountID</u>, Username, Password, Role, Status)
*Tất cả trường bắt buộc. Username duy nhất. Password lưu dạng mã hoá BCrypt. Role ∈ {ADMIN, CUSTOMER}. Status ∈ {ACTIVE, LOCKED}.*

**Customer** (<u>CustomerID</u>, FullName, Mobile, Birthday, IdentityCard, Email, AccountID)
*Tất cả trường bắt buộc. Mobile, IdentityCard, Email, AccountID duy nhất. Quan hệ 1–1 với Account.*

**Genre** (<u>GenreID</u>, GenreName, Description)
*Tất cả trường bắt buộc. GenreName duy nhất.*

**Movie** (<u>MovieID</u>, Title, Director, Duration, ReleaseDate, AgeRating, Description, PosterUrl, GenreID, Status)
*Tất cả trường bắt buộc. Duration (phút) ∈ [30, 300]. AgeRating ∈ {P, T13, T16, T18}. Status ∈ {COMING_SOON, SHOWING, STOPPED}.*

**Room** (<u>RoomID</u>, RoomName, RoomType, TotalRows, SeatsPerRow, Status)
*Tất cả trường bắt buộc. RoomName duy nhất. RoomType ∈ {TWO_D, THREE_D, IMAX}. TotalRows ∈ [1, 26], SeatsPerRow ∈ [1, 30]. Status ∈ {ACTIVE, MAINTENANCE}.*

**Showtime** (<u>ShowtimeID</u>, MovieID, RoomID, StartTime, EndTime, TicketPrice, Status)
*Tất cả trường bắt buộc. **StartTime < EndTime**. TicketPrice > 0. Status ∈ {OPEN, CANCELLED}.*

**Booking** (<u>BookingID</u>, CustomerID, ShowtimeID, BookingDate, TotalAmount, Status)
*Tất cả trường bắt buộc. Status ∈ {PENDING, CONFIRMED, CANCELLED}.*

**Ticket** (<u>TicketID</u>, BookingID, ShowtimeID, SeatCode, Price)
*Tất cả trường bắt buộc. SeatCode dạng `A1`, `B12`… Một ghế chỉ được bán một lần trong một suất chiếu (trừ đơn đã huỷ).*

**Review** (<u>CustomerID, MovieID</u>, Rating, Comment, ReviewDate)
*Tất cả trường bắt buộc. **Khoá chính kép**. Rating ∈ [1, 5].*

### 2.2 Sơ đồ quan hệ

```
Account 1 ──── 1 Customer 1 ──── N Booking N ──── 1 Showtime N ──── 1 Movie N ──── 1 Genre
                    │                 │                │              │
                    │                 1                N              │
                    │                 │                │              │
                    │                 N                1              │
                    │               Ticket            Room            │
                    │                                                 │
                    └────────── N  Review  N ─────────────────────────┘
                              (PK = CustomerID + MovieID)
```

| Quan hệ | Loại | Owning side (có FK) |
|---|---|---|
| Account – Customer | 1–1 | `Customer.account` (`AccountID`) |
| Genre – Movie | 1–N | `Movie.genre` |
| Movie – Showtime | 1–N | `Showtime.movie` |
| Room – Showtime | 1–N | `Showtime.room` |
| Customer – Booking | 1–N | `Booking.customer` |
| Showtime – Booking | 1–N | `Booking.showtime` |
| Booking – Ticket | 1–N | `Ticket.booking` (cascade ALL, orphanRemoval) |
| Customer, Movie – Review | N–N có thuộc tính | `Review` dùng `@EmbeddedId` + `@MapsId` |

---

## 3. Mục tiêu

Sau khi hoàn thành, sinh viên có thể:

- Phát triển ứng dụng web bằng **Spring Boot, Thymeleaf/JSTL, Spring Data JPA**.
- Dùng **Spring Data JPA** tạo database SQL Server **`FUCinemaTicketDB`** gồm các bảng: **Account, Customer, Genre, Movie, Room, Showtime, Booking, Ticket, Review**.
- Ánh xạ đủ các loại quan hệ: **1–1, 1–N, N–1, khoá chính kép**.
- Phát triển ứng dụng theo kiến trúc **3-Layer (Controller – Service – Repository) + Repository Pattern** để thực hiện CRUD.
- Viết truy vấn bằng **derived query, JPQL, native SQL, projection, `@Modifying`, phân trang/sắp xếp**.
- Xử lý **transaction**, **validation**, **phân quyền theo vai trò** và **làm việc nhóm với Git/GitHub**.

---

## 4. Chức năng chính

### 4.1 Khách (chưa đăng nhập)
- Xem danh sách phim **đang chiếu** / **sắp chiếu**, tìm phim theo tên, lọc theo thể loại.
- Xem chi tiết phim và các suất chiếu sắp tới.
- **Đăng ký** tài khoản khách hàng, **đăng nhập**.

### 4.2 Vai trò **Admin**
- **Quản lý khách hàng:** xem, tìm kiếm, thêm, sửa; **xoá** — nếu khách hàng chưa có đơn đặt vé thì xoá hẳn, nếu đã có đơn thì chỉ **khoá tài khoản** (`LOCKED`).
- **Quản lý phim** (kèm thể loại): xem, tìm kiếm, lọc, thêm, sửa; **xoá** — nếu phim **chưa có suất chiếu** thì xoá hẳn, nếu **đã có suất chiếu** thì chỉ đổi trạng thái sang `STOPPED`.
- **Quản lý phòng chiếu & suất chiếu:** CRUD, đảm bảo suất chiếu không trùng giờ trong cùng phòng.
- **Quản lý đặt vé:** xem danh sách đơn, lọc theo trạng thái/thời gian/khách hàng, xem chi tiết vé, **xác nhận** hoặc **huỷ** đơn.
- **Báo cáo thống kê** giao dịch bán vé theo khoảng thời gian **StartDate → EndDate**, sắp xếp **giảm dần**.

### 4.3 Vai trò **Customer**
- **Đăng ký** tài khoản.
- **Đặt vé trực tuyến** cho một suất chiếu với **một hoặc nhiều** ghế.
- **Quản lý hồ sơ** cá nhân, đổi mật khẩu.
- **Xem lịch sử giao dịch** (đơn đặt vé), xem chi tiết, **huỷ** đơn hợp lệ.
- *(Bonus)* **Đánh giá** phim đã xem.

---

## 5. Quy tắc nghiệp vụ

| Mã | Quy tắc |
|---|---|
| **BR-01** | Username/Email, Mobile (10 chữ số, bắt đầu bằng 0), IdentityCard (12 chữ số) **không trùng**. |
| **BR-02** | Khách hàng đăng ký phải **từ đủ 13 tuổi** trở lên. Mật khẩu ≥ 8 ký tự, có chữ hoa, chữ thường và chữ số. |
| **BR-03** | Tài khoản `LOCKED` **không đăng nhập** được. |
| **BR-04** | Xoá khách hàng: chưa có Booking → xoá Customer và Account; đã có Booking → chuyển Account sang `LOCKED`. |
| **BR-05** | Xoá thể loại chỉ khi **không còn phim** thuộc thể loại đó. |
| **BR-06** | Xoá phim: chưa có Showtime → xoá; đã có Showtime → đổi `Status = STOPPED` (soft delete). Phim `STOPPED` không hiển thị cho khách và không tạo được suất chiếu mới. |
| **BR-07** | Suất chiếu: **StartTime < EndTime**; `EndTime ≥ StartTime + Duration` của phim; phòng phải `ACTIVE`; phim không được `STOPPED`. |
| **BR-08** | Hai suất chiếu **cùng phòng** không được **chồng giờ** (giao nhau về khoảng thời gian), không xét suất `CANCELLED`. |
| **BR-09** | Xoá suất chiếu: chưa có Booking → xoá; đã có Booking → đổi `Status = CANCELLED` và **huỷ toàn bộ đơn** của suất đó. |
| **BR-10** | Đặt vé: suất chiếu phải `OPEN` và **chưa bắt đầu**; mỗi đơn **1–8 ghế**; ghế phải **tồn tại** trong phòng (theo TotalRows × SeatsPerRow) và **chưa được bán** (không tính đơn `CANCELLED`). |
| **BR-11** | Phim `T13`/`T16`/`T18`: khách hàng phải đủ tuổi tương ứng tại ngày chiếu. |
| **BR-12** | `TotalAmount = TicketPrice × số ghế`. Đơn mới có trạng thái `PENDING`. Việc tạo Booking + các Ticket phải nằm trong **một transaction**. |
| **BR-13** | Chuyển trạng thái đơn: `PENDING → CONFIRMED` (Admin), `PENDING/CONFIRMED → CANCELLED`. Đơn `CANCELLED` không chuyển trạng thái được nữa. |
| **BR-14** | Khách hàng chỉ được huỷ đơn **của chính mình** và trước giờ chiếu **ít nhất 2 giờ**. |
| **BR-15** | Báo cáo: `StartDate ≤ EndDate`; chỉ tính đơn `CONFIRMED` có `BookingDate` trong khoảng [StartDate, EndDate]; sắp xếp **giảm dần**. |
| **BR-16** | *(Bonus)* Chỉ được đánh giá phim đã xem (có đơn `CONFIRMED` và suất chiếu đã kết thúc); mỗi khách hàng đánh giá mỗi phim **một lần** (khoá kép), có thể sửa đánh giá. |

---

## 6. Yêu cầu kỹ thuật

| Mã | Yêu cầu |
|---|---|
| **TECH-01** | Spring Boot 4.x, Java 21+, Maven. Dependencies: `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `mssql-jdbc`, `lombok`, `spring-security-crypto` (chỉ để mã hoá BCrypt), `spring-boot-devtools` (tuỳ chọn). |
| **TECH-02** | Kiến trúc 3-Layer: `controller` → `service` (interface + impl) → `repository` → `entity`. Controller **không** gọi repository trực tiếp. |
| **TECH-03** | Dùng **DTO/Form object** cho form nhập liệu; validate bằng Bean Validation (`@NotBlank`, `@Pattern`, `@Past`, `@Min`…) + kiểm tra nghiệp vụ ở Service. |
| **TECH-04** | Service gắn `@Transactional(readOnly = true)` ở class; method ghi gắn `@Transactional`. |
| **TECH-05** | Lỗi nghiệp vụ ném `BusinessException`; xử lý tập trung bằng `@ControllerAdvice`; hiển thị thông báo bằng flash message. |
| **TECH-06** | Đăng nhập bằng **HttpSession**; phân quyền bằng `HandlerInterceptor`: `/admin/**` chỉ `ADMIN`, `/customer/**` chỉ `CUSTOMER`. *(Bonus: dùng Spring Security.)* |
| **TECH-07** | Thông tin kết nối DB **không commit** lên Git: đặt trong `application-local.properties` (đã `.gitignore`) hoặc biến môi trường. |
| **TECH-08** | Danh sách có **phân trang** (mặc định 10 dòng/trang) và **sắp xếp**. |
| **TECH-09** | Entity: `jakarta.persistence`, không dùng `@Data` cho entity có quan hệ 2 chiều, quan hệ `@ManyToOne` dùng `FetchType.LAZY`. |
| **TECH-10** | Làm việc nhóm trên GitHub theo quy trình trong `Assignment1_teamwork.md` (issue, nhánh, pull request, review, Conventional Commits). |

---

## 7. Dữ liệu mẫu

Seed tự động khi khởi động (nếu bảng rỗng) bằng `CommandLineRunner` hoặc `data.sql`.

| Bảng | Dữ liệu tối thiểu |
|---|---|
| Account | `admin@fucinema.vn` / `Admin@123` (ADMIN) · `an.nv@gmail.com` / `Customer@123` (CUSTOMER) · `binh.tt@gmail.com` / `Customer@123` (CUSTOMER) |
| Customer | 2 khách hàng gắn với 2 tài khoản CUSTOMER (1 người dưới 16 tuổi để test BR-11) |
| Genre | Hành động, Hoạt hình, Kinh dị, Tình cảm, Khoa học viễn tưởng |
| Movie | 6 phim: 3 `SHOWING`, 2 `COMING_SOON`, 1 `STOPPED`; có ít nhất 1 phim `T18` |
| Room | `Room 01` (2D, 8×12) · `Room 02` (3D, 10×14) · `Room 03` (IMAX, 12×16, `MAINTENANCE`) |
| Showtime | ≥ 6 suất trong 7 ngày tới, ít nhất 1 suất **đã qua** và 1 suất `CANCELLED` |
| Booking/Ticket | ≥ 5 đơn với đủ 3 trạng thái, trải trên nhiều ngày để test báo cáo |

---

## 8. Danh sách TODO

> Mỗi TODO gồm **yêu cầu** và **tiêu chí chấp nhận (AC)**. Mỗi TODO hoàn thành = **ít nhất một commit** và được merge qua **pull request** (xem `Assignment1_teamwork.md`).

### Part 0 — Khởi tạo dự án (0.5 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 1** | Tạo project Spring Boot `fu-cinema-ticket-system` (group `com.fu.cinema`) với dependencies ở TECH-01. Tạo `.gitignore`, `README.md`. | `mvn spring-boot:run` khởi động thành công. |
| **TODO 2** | Cấu hình `application.properties` (chung) và `application-local.properties` (thông tin DB, không commit); database `FUCinemaTicketDB`; `ddl-auto=update`, `show-sql=true`; có file mẫu `application-local.properties.example`. | Kết nối được SQL Server; không có mật khẩu thật trong repository. |
| **TODO 3** | Tạo cấu trúc package: `entity`, `enums`, `repository`, `service`, `service.impl`, `dto`, `controller`, `config`, `exception`, `util`. Tạo layout Thymeleaf dùng chung (`fragments/header.html`, `footer.html`, `layout.html`) có menu thay đổi theo vai trò và vùng hiển thị flash message. | Trang chủ hiển thị layout; menu khác nhau khi chưa đăng nhập / Admin / Customer. |

### Part A — Entity & cơ sở dữ liệu (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 4** | Tạo các enum: `Role`, `AccountStatus`, `AgeRating`, `MovieStatus`, `RoomType`, `RoomStatus`, `ShowtimeStatus`, `BookingStatus`. Tạo entity `Account`, `Customer` (quan hệ **1–1**, `Customer` giữ FK `account_id`, unique). | Bảng `accounts`, `customers` được tạo, FK + unique đúng. Enum lưu dạng chuỗi. |
| **TODO 5** | Tạo entity `Genre`, `Movie` (quan hệ **1–N**). | FK `genre_id` NOT NULL; `title`, `genre_name` có ràng buộc phù hợp. |
| **TODO 6** | Tạo entity `Room`, `Showtime` (`Showtime` N–1 `Movie`, N–1 `Room`). | FK `movie_id`, `room_id`; kiểu `LocalDateTime` cho thời gian; giá kiểu `BigDecimal`. |
| **TODO 7** | Tạo entity `Booking`, `Ticket` (`Booking` 1–N `Ticket`, cascade ALL + orphanRemoval) và `Review` với **khoá chính kép** (`@Embeddable ReviewId` + `@EmbeddedId` + `@MapsId`). | Bảng `reviews` có PK kép (`customer_id`, `movie_id`). |
| **TODO 8** | Seed dữ liệu mẫu (mục 7), mật khẩu mã hoá bằng `BCryptPasswordEncoder`. | Đăng nhập được bằng tài khoản mẫu ở TODO 13; dữ liệu đủ để test mọi chức năng. |

### Part B — Repository (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 9** | Tạo repository cho mọi entity (`extends JpaRepository`). Khai báo **derived query** ở mục 9.1. | App khởi động không lỗi `PropertyReferenceException`. |
| **TODO 10** | Khai báo **custom query** ở mục 9.2: JPQL, native, projection, `@Modifying`, phân trang. | Mỗi query trả đúng kết quả trên dữ liệu mẫu (kiểm tra qua màn hình hoặc test). |

### Part C — Service & xử lý lỗi (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 11** | Tạo service interface + impl: `AuthService`, `CustomerService`, `GenreService`, `MovieService`, `RoomService`, `ShowtimeService`, `BookingService`, `ReportService`, *(bonus)* `ReviewService`. Gắn `@Transactional` theo TECH-04. Tạo `BusinessException`, `ResourceNotFoundException`. | Controller chỉ gọi service; mọi rule ở mục 5 được kiểm tra trong service. |
| **TODO 12** | Tạo `GlobalExceptionHandler` (`@ControllerAdvice`), trang lỗi `error/403.html`, `error/404.html`, `error/500.html`; flash message cho thao tác thành công/thất bại. | Nhập ID không tồn tại → trang 404; vi phạm nghiệp vụ → thông báo rõ ràng, không lộ stack trace. |

### Part D — Xác thực & phân quyền (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 13** | **Đăng nhập / đăng xuất** bằng username + password (kiểm tra BCrypt), lưu thông tin người dùng (`id`, `name`, `role`) vào `HttpSession`; điều hướng theo vai trò (Admin → `/admin`, Customer → `/`). | Sai mật khẩu / tài khoản `LOCKED` → báo lỗi (BR-03). Đăng xuất xoá session. |
| **TODO 14** | **Đăng ký** tài khoản khách hàng (`RegisterForm`): tạo `Account` (CUSTOMER, ACTIVE) + `Customer` trong một transaction. | Validate BR-01, BR-02; dữ liệu nhập được giữ lại khi lỗi. |
| **TODO 15** | **Phân quyền** bằng `HandlerInterceptor` (`WebMvcConfigurer`): chưa đăng nhập → chuyển về `/login`; sai vai trò → trang 403. | Customer vào `/admin/**` → 403; khách vào `/customer/**` → `/login`. |

### Part E — Admin: Quản lý khách hàng (0.75 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 16** | Danh sách khách hàng: **tìm kiếm** theo tên / email / số điện thoại, **phân trang**, sắp xếp theo tên; hiển thị trạng thái tài khoản. | Từ khoá không phân biệt hoa thường; giữ từ khoá khi chuyển trang. |
| **TODO 17** | **Thêm / sửa** khách hàng (kèm tài khoản); **khoá / mở khoá** tài khoản; **xoá** theo BR-04. | Có hộp xác nhận trước khi xoá; thông báo rõ "đã xoá" hay "đã khoá". |

### Part F — Admin: Quản lý thể loại & phim (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 18** | CRUD **thể loại**; xoá theo BR-05. | Xoá thể loại còn phim → báo lỗi, không xoá. |
| **TODO 19** | Danh sách **phim**: tìm theo tên, lọc theo thể loại và trạng thái, **phân trang**, sắp xếp theo ngày khởi chiếu / tên. | Các tiêu chí lọc kết hợp được với nhau. |
| **TODO 20** | **Thêm / sửa** phim (`MovieForm`), chọn thể loại từ dropdown, validate thời lượng, ngày khởi chiếu, độ tuổi. | Lỗi validate hiển thị cạnh từng trường. |
| **TODO 21** | **Xoá phim** theo BR-06 (soft delete). | Phim đã có suất chiếu → chuyển `STOPPED`, thông báo tương ứng. |

### Part G — Admin: Phòng chiếu & suất chiếu (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 22** | CRUD **phòng chiếu**; không cho xoá phòng đã có suất chiếu (chuyển `MAINTENANCE`). | Hiển thị sức chứa = TotalRows × SeatsPerRow. |
| **TODO 23** | CRUD **suất chiếu**: chọn phim, phòng, giờ bắt đầu, giá vé; tự gợi ý `EndTime = StartTime + Duration + 15 phút`; kiểm tra BR-07, BR-08; xoá theo BR-09. Danh sách suất chiếu lọc theo ngày, phim, phòng. | Tạo suất trùng giờ cùng phòng → báo lỗi chỉ rõ suất bị trùng. |

### Part H — Admin: Quản lý đặt vé & báo cáo (1.0 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 24** | Danh sách **đơn đặt vé**: lọc theo trạng thái, khoảng ngày đặt, từ khoá khách hàng; phân trang, mới nhất trước. Trang **chi tiết** đơn: thông tin khách, phim, suất chiếu, danh sách ghế, tổng tiền. | Lọc kết hợp nhiều tiêu chí; chi tiết đơn không bị `LazyInitializationException`. |
| **TODO 25** | **Xác nhận / huỷ** đơn theo BR-13. | Thao tác sai luồng trạng thái → báo lỗi. |
| **TODO 26** | **Báo cáo thống kê** theo **StartDate → EndDate** (BR-15): (a) doanh thu **theo phim**: tên phim, số vé bán, doanh thu — **giảm dần theo doanh thu**; (b) danh sách đơn `CONFIRMED` trong kỳ — **giảm dần theo ngày đặt**; (c) dòng tổng: tổng số đơn, tổng vé, tổng doanh thu. | `StartDate > EndDate` → báo lỗi; kỳ không có dữ liệu → hiển thị "Không có dữ liệu". |

### Part I — Customer (1.25 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 27** | **Trang chủ**: phim `SHOWING` và `COMING_SOON` (không hiện `STOPPED`), tìm theo tên, lọc thể loại. **Chi tiết phim**: thông tin phim, điểm đánh giá trung bình, danh sách suất chiếu `OPEN` sắp tới nhóm theo ngày. | Khách chưa đăng nhập xem được; bấm "Đặt vé" khi chưa đăng nhập → `/login`. |
| **TODO 28** | **Đặt vé**: trang sơ đồ ghế của suất chiếu (ghế trống / đã bán), chọn **một hoặc nhiều** ghế, xem tổng tiền, xác nhận đặt. Kiểm tra BR-10, BR-11, BR-12. | Hai người cùng chọn một ghế: người thứ hai nhận thông báo ghế đã bán; đơn và vé được lưu đồng thời hoặc không lưu gì. |
| **TODO 29** | **Lịch sử giao dịch**: danh sách đơn của khách đang đăng nhập (lọc theo trạng thái, phân trang, mới nhất trước), xem chi tiết; **huỷ đơn** theo BR-14. | Không xem/huỷ được đơn của người khác (đổi ID trên URL → 403/404). |
| **TODO 30** | **Hồ sơ cá nhân**: xem, cập nhật thông tin (không đổi username), **đổi mật khẩu** (nhập mật khẩu cũ). | Validate BR-01, BR-02; đổi mật khẩu sai mật khẩu cũ → báo lỗi. |
| **TODO 31** | *(Bonus)* **Đánh giá phim** theo BR-16: thêm / sửa đánh giá; hiển thị danh sách đánh giá và điểm trung bình ở trang chi tiết phim. | Chưa xem phim → không hiện nút đánh giá. |

### Part J — Hoàn thiện (0.5 điểm)

| TODO | Yêu cầu | Tiêu chí chấp nhận |
|---|---|---|
| **TODO 32** | Hoàn thiện `README.md`: mô tả dự án, công nghệ, hướng dẫn cài đặt & chạy, tài khoản mẫu, thành viên & phân công, ảnh chụp màn hình chính. Tạo release `v1.0.0` trên GitHub. | Người khác clone về chạy được chỉ bằng cách làm theo README. |

---

## 9. Danh sách query bắt buộc

### 9.1 Derived query

| Repository | Method | Dùng cho |
|---|---|---|
| `AccountRepository` | `Optional<Account> findByUsername(String username)` · `boolean existsByUsername(String username)` | TODO 13, 14 |
| `CustomerRepository` | `Optional<Customer> findByAccount_AccountId(Long id)` · `boolean existsByEmail(String)` · `boolean existsByMobile(String)` · `boolean existsByIdentityCard(String)` · `boolean existsByEmailAndCustomerIdNot(String, Long)` | TODO 13, 14, 17, 30 |
| `GenreRepository` | `boolean existsByGenreNameIgnoreCase(String)` · `List<Genre> findAllByOrderByGenreNameAsc()` | TODO 18 |
| `MovieRepository` | `List<Movie> findByStatusInOrderByReleaseDateDesc(Collection<MovieStatus>)` · `boolean existsByGenre_GenreId(Long)` · `long countByStatus(MovieStatus)` | TODO 18, 27 |
| `RoomRepository` | `List<Room> findByStatus(RoomStatus)` · `boolean existsByRoomNameIgnoreCase(String)` | TODO 22, 23 |
| `ShowtimeRepository` | `boolean existsByMovie_MovieId(Long)` · `boolean existsByRoom_RoomId(Long)` · `List<Showtime> findByMovie_MovieIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(Long, ShowtimeStatus, LocalDateTime)` | TODO 21, 22, 27 |
| `BookingRepository` | `boolean existsByCustomer_CustomerId(Long)` · `boolean existsByShowtime_ShowtimeId(Long)` · `Page<Booking> findByCustomer_CustomerId(Long, Pageable)` · `Page<Booking> findByCustomer_CustomerIdAndStatus(Long, BookingStatus, Pageable)` | TODO 17, 23, 29 |
| `ReviewRepository` | `List<Review> findByMovie_MovieIdOrderByReviewDateDesc(Long)` | TODO 31 |

### 9.2 Custom query

| # | Repository | Mục đích | Loại | Gợi ý |
|---|---|---|---|---|
| Q1 | `CustomerRepository` | Tìm khách theo từ khoá (tên/email/SĐT), phân trang | JPQL + `Pageable` | `LOWER(...) LIKE LOWER(CONCAT('%', :kw, '%'))` với `OR` |
| Q2 | `MovieRepository` | Tìm phim theo tên + thể loại + trạng thái (tham số tuỳ chọn), phân trang | JPQL + `Pageable` (hoặc Specification) | `(:genreId IS NULL OR m.genre.genreId = :genreId)` |
| Q3 | `ShowtimeRepository` | Đếm suất chiếu **chồng giờ** trong cùng phòng (BR-08) | JPQL | `s.room.roomId = :roomId AND s.status <> CANCELLED AND s.startTime < :end AND s.endTime > :start AND (:excludeId IS NULL OR s.showtimeId <> :excludeId)` |
| Q4 | `TicketRepository` | Lấy danh sách ghế **đã bán** của một suất chiếu | JPQL | `SELECT t.seatCode FROM Ticket t WHERE t.showtime.showtimeId = :id AND t.booking.status <> CANCELLED` |
| Q5 | `BookingRepository` | Chi tiết đơn kèm khách, suất chiếu, phim, vé | JPQL `JOIN FETCH` | Tránh `LazyInitializationException` / N+1 |
| Q6 | `BookingRepository` | Lọc đơn (Admin) theo trạng thái, khoảng ngày, từ khoá khách hàng | JPQL + `Pageable` (hoặc Specification) | Tham số tuỳ chọn |
| Q7 | `BookingRepository` | **Báo cáo doanh thu theo phim** trong kỳ, giảm dần | JPQL aggregate → DTO `MovieRevenueDTO(title, ticketCount, revenue)` | `SUM`, `COUNT`, `GROUP BY`, `ORDER BY SUM(...) DESC` |
| Q8 | `BookingRepository` | Danh sách đơn `CONFIRMED` trong kỳ, giảm dần theo ngày | Derived hoặc JPQL | `findByStatusAndBookingDateBetweenOrderByBookingDateDesc` |
| Q9 | `BookingRepository` | **Huỷ hàng loạt** đơn của một suất chiếu bị huỷ (BR-09) | `@Modifying` JPQL UPDATE | `clearAutomatically = true` |
| Q10 | `ReviewRepository` | Điểm trung bình & số lượt đánh giá của phim | JPQL aggregate | `AVG(r.rating)`, `COUNT(r)` |
| Q11 | `ShowtimeRepository` | Thống kê nhanh cho dashboard Admin: số suất chiếu hôm nay theo phòng | **Native SQL** (SQL Server) + interface projection | `CAST(start_time AS DATE) = CAST(GETDATE() AS DATE)` |
| Q12 | `BookingRepository` | Kiểm tra khách đã xem phim (BR-16) | JPQL `EXISTS`/`COUNT` | `b.status = CONFIRMED AND b.showtime.endTime < :now` |

---

## 10. Danh sách màn hình & URL

| Vai trò | Màn hình | URL (gợi ý) |
|---|---|---|
| Khách | Trang chủ, tìm phim | `GET /`, `GET /movies?kw=&genreId=` |
| Khách | Chi tiết phim + suất chiếu | `GET /movies/{id}` |
| Khách | Đăng nhập / đăng ký / đăng xuất | `GET/POST /login`, `GET/POST /register`, `POST /logout` |
| Customer | Chọn ghế & đặt vé | `GET /customer/showtimes/{id}/seats`, `POST /customer/bookings` |
| Customer | Lịch sử đặt vé, chi tiết, huỷ | `GET /customer/bookings`, `GET /customer/bookings/{id}`, `POST /customer/bookings/{id}/cancel` |
| Customer | Hồ sơ, đổi mật khẩu | `GET/POST /customer/profile`, `GET/POST /customer/password` |
| Customer | *(Bonus)* Đánh giá | `POST /customer/movies/{id}/review` |
| Admin | Dashboard | `GET /admin` |
| Admin | Khách hàng | `/admin/customers` (list, `new`, `{id}/edit`, `{id}/delete`, `{id}/lock`) |
| Admin | Thể loại | `/admin/genres` |
| Admin | Phim | `/admin/movies` |
| Admin | Phòng chiếu | `/admin/rooms` |
| Admin | Suất chiếu | `/admin/showtimes` |
| Admin | Đơn đặt vé | `/admin/bookings`, `/admin/bookings/{id}`, `POST /admin/bookings/{id}/confirm`, `POST /admin/bookings/{id}/cancel` |
| Admin | Báo cáo | `GET /admin/reports?startDate=&endDate=` |

> Thao tác thay đổi dữ liệu (thêm/sửa/xoá/huỷ) dùng **POST** (không dùng link GET để xoá). Áp dụng mẫu **Post/Redirect/Get** sau khi xử lý form.

---

## 11. Checklist hoàn thành

### Part 0 — Khởi tạo
- [ ] TODO 1 — Project chạy được; có `.gitignore`, `README.md`.
- [ ] TODO 2 — Kết nối `FUCinemaTicketDB`; thông tin DB nằm ngoài Git; có file `.example`.
- [ ] TODO 3 — Đủ package; layout Thymeleaf dùng chung; menu theo vai trò; vùng flash message.

### Part A — Entity
- [ ] TODO 4 — 8 enum lưu dạng chuỗi; `Account` 1–1 `Customer`, FK unique.
- [ ] TODO 5 — `Genre` 1–N `Movie`.
- [ ] TODO 6 — `Showtime` N–1 `Movie`, N–1 `Room`; `LocalDateTime`, `BigDecimal`.
- [ ] TODO 7 — `Booking` 1–N `Ticket` (cascade, orphanRemoval); `Review` khoá kép `@EmbeddedId` + `@MapsId`.
- [ ] TODO 8 — Seed đủ dữ liệu; mật khẩu BCrypt.
- [ ] Không dùng `@Data` cho entity có quan hệ 2 chiều; `@ManyToOne` LAZY.

### Part B — Repository
- [ ] TODO 9 — Đủ derived query mục 9.1, app khởi động không lỗi.
- [ ] TODO 10 — Đủ Q1–Q12 (Q11 native, Q7 DTO, Q5 JOIN FETCH, Q9 `@Modifying`).
- [ ] Không nối chuỗi tham số vào query.

### Part C — Service & lỗi
- [ ] TODO 11 — Mỗi service có interface + impl; `@Transactional(readOnly = true)` ở class, `@Transactional` ở method ghi.
- [ ] TODO 11 — Toàn bộ BR-01 → BR-15 được kiểm tra ở service.
- [ ] TODO 12 — `@ControllerAdvice`; trang 403/404/500; flash message thành công/thất bại.
- [ ] Controller không inject repository.

### Part D — Xác thực & phân quyền
- [ ] TODO 13 — Đăng nhập BCrypt, chặn tài khoản `LOCKED`, đăng xuất xoá session, điều hướng theo vai trò.
- [ ] TODO 14 — Đăng ký tạo `Account` + `Customer` trong 1 transaction; validate trùng, tuổi, mật khẩu mạnh.
- [ ] TODO 15 — Interceptor chặn `/admin/**`, `/customer/**` đúng vai trò.

### Part E — Admin: Khách hàng
- [ ] TODO 16 — Tìm kiếm + phân trang + sắp xếp, giữ từ khoá khi chuyển trang.
- [ ] TODO 17 — Thêm/sửa; khoá/mở khoá; xoá theo BR-04 có xác nhận.

### Part F — Admin: Thể loại & phim
- [ ] TODO 18 — CRUD thể loại; chặn xoá khi còn phim.
- [ ] TODO 19 — Danh sách phim: tìm + lọc kết hợp + phân trang + sắp xếp.
- [ ] TODO 20 — Form phim có validate, dropdown thể loại.
- [ ] TODO 21 — Xoá phim: xoá hẳn hoặc `STOPPED` theo BR-06.

### Part G — Admin: Phòng & suất chiếu
- [ ] TODO 22 — CRUD phòng; không xoá phòng đã có suất chiếu.
- [ ] TODO 23 — CRUD suất chiếu; kiểm tra BR-07, BR-08 (Q3); xoá/huỷ theo BR-09 (Q9).

### Part H — Admin: Đặt vé & báo cáo
- [ ] TODO 24 — Lọc đơn nhiều tiêu chí + phân trang; chi tiết đơn dùng JOIN FETCH.
- [ ] TODO 25 — Xác nhận/huỷ đúng luồng trạng thái BR-13.
- [ ] TODO 26 — Báo cáo StartDate → EndDate: theo phim (giảm dần doanh thu), danh sách đơn (giảm dần ngày), dòng tổng; validate ngày.

### Part I — Customer
- [ ] TODO 27 — Trang chủ ẩn phim `STOPPED`; chi tiết phim có suất chiếu sắp tới.
- [ ] TODO 28 — Sơ đồ ghế; đặt nhiều ghế; kiểm tra BR-10/11/12; 1 transaction.
- [ ] TODO 29 — Lịch sử đơn của chính mình; huỷ theo BR-14; chặn truy cập đơn người khác.
- [ ] TODO 30 — Cập nhật hồ sơ; đổi mật khẩu có kiểm tra mật khẩu cũ.
- [ ] TODO 31 — *(Bonus)* Đánh giá theo BR-16, điểm trung bình (Q10, Q12).

### Part J — Hoàn thiện
- [ ] TODO 32 — README đầy đủ; release `v1.0.0`.

### Chất lượng & quy trình
- [ ] Mọi thao tác ghi dùng POST + Post/Redirect/Get.
- [ ] Không có lỗi `LazyInitializationException`, không có stack trace hiện ra giao diện.
- [ ] Mỗi TODO được thực hiện qua **issue → nhánh → commit → pull request → review → merge**.
- [ ] Commit theo Conventional Commits; không commit mật khẩu, thư mục `target/`, `.idea/`.
- [ ] Mỗi thành viên có commit và pull request được merge cho phần việc của mình.

---

## 12. Thang điểm & nộp bài

| Phần | Nội dung | Điểm |
|---|---|---|
| Part 0 | Khởi tạo dự án (TODO 1–3) | 0.5 |
| Part A | Entity & database (TODO 4–8) | 1.0 |
| Part B | Repository & query (TODO 9–10) | 1.0 |
| Part C | Service & xử lý lỗi (TODO 11–12) | 1.0 |
| Part D | Xác thực & phân quyền (TODO 13–15) | 1.0 |
| Part E | Admin – Khách hàng (TODO 16–17) | 0.75 |
| Part F | Admin – Thể loại & phim (TODO 18–21) | 1.0 |
| Part G | Admin – Phòng & suất chiếu (TODO 22–23) | 1.0 |
| Part H | Admin – Đặt vé & báo cáo (TODO 24–26) | 1.0 |
| Part I | Customer (TODO 27–30) | 1.25 |
| Part J | README, release (TODO 32) | 0.5 |
| **Tổng** | | **10.0** |
| Bonus | TODO 31 (đánh giá phim) · Spring Security thay interceptor · Unit test service | tối đa +1.0 |
| Trừ điểm | Commit mật khẩu thật · Controller gọi repository · Không dùng PR/review · Thành viên không có commit | −0.5 mỗi lỗi |

**Nộp bài:**
1. Link GitHub repository (đã mời giảng viên với quyền *Read*), nhánh `main` chứa bản release `v1.0.0`.
2. File `README.md` có hướng dẫn chạy và bảng phân công.
3. Script tạo database hoặc mô tả cấu hình `ddl-auto`.
4. File word theo template, trình bày đúng định dạng, có mục lục tự động, đánh số tự động cho các tiêu đề, hình chụp ở mỗi bước
