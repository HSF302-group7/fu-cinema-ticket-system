# Assignment 01 — Hướng dẫn làm việc nhóm với Git & GitHub (chuẩn production)

> Áp dụng cho đề `Assignment1.md` — **FUCinemaTicketSystem**.
> Mục tiêu: cả nhóm làm việc **song song** trên một repository mà **không giẫm chân nhau**, mọi thay đổi đều **truy vết được** (issue → nhánh → commit → pull request → review → merge → release), giống quy trình của một đội phát triển thực tế.
>
> Nhóm mặc định **5 thành viên** (nhóm 4 người: gộp vai trò D và E — xem mục 3.2).
>
> **Phạm vi GitHub Free:** quy trình này **không dùng CI (GitHub Actions)**. Thay vào đó, mọi PR phải **build & test cục bộ** (`mvn verify`) trên máy tác giả, và reviewer **chạy lại trên máy mình** khi review (xem mục 12.5). Với repository **Private** trên GitHub Free, một số tính năng (branch protection/ruleset, CODEOWNERS, Draft PR) không được hỗ trợ — xem ghi chú ở mục 4 để có cách thay thế.

---

## Mục lục

1. [Nguyên tắc chung](#1-nguyên-tắc-chung)
2. [Mô hình nhánh & quy ước đặt tên](#2-mô-hình-nhánh--quy-ước-đặt-tên)
3. [Vai trò & phân công chức năng](#3-vai-trò--phân-công-chức-năng)
4. [Sprint 0 — Leader thiết lập repository (step-by-step)](#4-sprint-0--leader-thiết-lập-repository)
5. [Sprint 0 — Thành viên chuẩn bị môi trường](#5-sprint-0--thành-viên-chuẩn-bị-môi-trường)
6. [Quy trình làm một chức năng (vòng đời một issue)](#6-quy-trình-làm-một-chức-năng)
7. [Hướng dẫn theo từng chức năng](#7-hướng-dẫn-theo-từng-chức-năng)
8. [Pull Request & Code Review](#8-pull-request--code-review)
9. [Xử lý xung đột (merge conflict)](#9-xử-lý-xung-đột)
10. [Release & Hotfix](#10-release--hotfix)
11. [Kế hoạch sprint gợi ý](#11-kế-hoạch-sprint-gợi-ý)
12. [Phụ lục: template & cấu hình mẫu](#12-phụ-lục-template--cấu-hình-mẫu)
13. [Cheat sheet lệnh Git](#13-cheat-sheet-lệnh-git)
14. [Checklist làm việc nhóm](#14-checklist-làm-việc-nhóm)

---

## 1. Nguyên tắc chung

| # | Nguyên tắc |
|---|---|
| 1 | **Không ai commit/push trực tiếp** lên `main` và `develop` — mọi thay đổi đi qua **Pull Request (PR)**. |
| 2 | **Không có issue thì không code.** Mỗi chức năng/lỗi là một issue; mỗi issue là một nhánh; mỗi nhánh là một PR. |
| 3 | **Nhánh nhỏ, sống ngắn** (1–3 ngày). PR lý tưởng < 400 dòng thay đổi để review được kỹ. |
| 4 | **Commit nhỏ, đúng chuẩn** Conventional Commits; mỗi commit build được. |
| 5 | **Đồng bộ thường xuyên** với `develop` (ít nhất mỗi ngày một lần) để giảm xung đột. |
| 6 | **`mvn verify` phải pass trên máy** (tác giả chạy trước khi mở PR, reviewer chạy lại khi review) và có **ít nhất 1 approval** trước khi merge. Người viết code **không tự approve**. |
| 7 | **Không commit bí mật**: mật khẩu DB, token… nằm trong `application-local.properties` (đã ignore) hoặc biến môi trường. |
| 8 | **Giao tiếp trên GitHub**: thảo luận kỹ thuật trong issue/PR để có lịch sử; nhóm chat chỉ để nhắc việc. |

---

## 2. Mô hình nhánh & quy ước đặt tên

### 2.1 Mô hình nhánh (Git Flow rút gọn)

```
main      ●───────────────────────────────●──────────●  (v1.0.0)   ← chỉ chứa bản phát hành
           \                             / \        /
release     \                    ●──●──●   \      /    release/1.0.0 (kiểm thử, sửa lỗi nhỏ)
             \                  /           \    /
hotfix        \                /             ●──●       hotfix/1.0.1-... (sửa lỗi khẩn cấp trên main)
               \              /                  \
develop    ●────●────●────●──●────────────────────●     ← nhánh tích hợp, luôn chạy được
                 \  /  \  /
feature           ●─●    ●─●                             feature/<issue>-<mô-tả>
```

| Nhánh | Tạo từ | Merge vào | Kiểu merge | Bảo vệ |
|---|---|---|---|---|
| `main` | — | — | Merge commit (từ `release/*`, `hotfix/*`) | PR + 2 approvals |
| `develop` | `main` (1 lần đầu) | — | Squash merge (từ `feature/*`, `fix/*`) | PR + 1 approval |
| `feature/*` | `develop` | `develop` | Squash | — |
| `fix/*` | `develop` | `develop` | Squash | — |
| `release/*` | `develop` | `main`, rồi back-merge về `develop` | Merge commit | — |
| `hotfix/*` | `main` | `main`, rồi back-merge về `develop` | Merge commit | — |
| `docs/*`, `chore/*`, `test/*`, `refactor/*` | `develop` | `develop` | Squash | — |

### 2.2 Quy ước đặt tên nhánh

```
<type>/<issue-number>-<mo-ta-ngan-kebab-case>
```

| Ví dụ | Ý nghĩa |
|---|---|
| `feature/12-movie-crud` | Chức năng CRUD phim, issue #12 |
| `feature/20-seat-booking` | Đặt vé chọn ghế, issue #20 |
| `fix/35-showtime-overlap-check` | Sửa lỗi kiểm tra trùng suất chiếu, issue #35 |
| `chore/3-github-templates` | Thêm issue/PR template |
| `docs/40-readme-setup` | Viết README |
| `release/1.0.0` · `hotfix/1.0.1-login-null` | Phát hành / sửa khẩn cấp |

Quy tắc: chữ thường, không dấu, không khoảng trắng, ≤ 50 ký tự, **luôn có số issue**.

### 2.3 Quy ước commit (Conventional Commits)

```
<type>(<scope>): <subject>

[body: giải thích vì sao / thay đổi gì — có thể viết tiếng Việt]

Refs #<issue>        ← hoặc "Closes #<issue>" ở commit/PR cuối cùng
```

| `type` | Khi nào |
|---|---|
| `feat` | Thêm chức năng |
| `fix` | Sửa lỗi |
| `refactor` | Sửa cấu trúc, không đổi hành vi |
| `test` | Thêm/sửa test |
| `docs` | Tài liệu |
| `style` | Format code, không đổi logic |
| `chore` | Cấu hình, build, dependency |
| `perf` | Tối ưu hiệu năng |

| `scope` (theo module) | |
|---|---|
| `setup`, `config`, `layout`, `entity`, `auth`, `customer`, `genre`, `movie`, `room`, `showtime`, `booking`, `report`, `profile`, `review`, `error`, `seed` | |

Quy tắc `subject`: tiếng Anh, **thể mệnh lệnh** (`add`, `fix`, `update`), viết thường, không dấu chấm cuối, ≤ 72 ký tự.

✅ `feat(movie): add soft delete when movie has showtimes`
✅ `fix(booking): prevent double booking of the same seat`
❌ `update code` · `Fixed bug.` · `feat: làm xong TODO 20`

---

## 3. Vai trò & phân công chức năng

### 3.1 Vai trò trong nhóm

| Vai trò | Trách nhiệm |
|---|---|
| **Team Leader / Maintainer** (thành viên A) | Tạo & cấu hình repository, quản lý board, milestone, duyệt PR vào `main`, tạo release, giải quyết tranh chấp kỹ thuật. |
| **Developer** (B, C, D, E) | Nhận issue, code, viết test cơ bản, mở PR, review PR của người khác. |
| **Reviewer luân phiên** | Mỗi PR cần 1 reviewer **khác module**; luân phiên để ai cũng hiểu toàn bộ hệ thống. |
| **Tester của sprint** (luân phiên) | Cuối sprint chạy lại checklist trong `Assignment1.md` trên `develop`, mở issue `bug` nếu phát hiện lỗi. |

### 3.2 Phân công theo module (5 người)

| Thành viên | Module | TODO (`Assignment1.md`) | Nhánh chính |
|---|---|---|---|
| **A — Leader** | Nền tảng: project, cấu hình, layout, **toàn bộ entity + repository khung**, seed, xử lý lỗi; **Đăng nhập / Đăng ký / Phân quyền** | 1–4, 7 (phần Review), 8, 9 (khung), 12, 13–15, 32 | `feature/<n>-project-setup`, `feature/<n>-entities`, `feature/<n>-auth` |
| **B** | **Quản lý khách hàng (Admin)** + **Hồ sơ cá nhân (Customer)** | 16, 17, 30 | `feature/<n>-customer-admin`, `feature/<n>-profile` |
| **C** | **Thể loại & phim (Admin)** + **Trang chủ, chi tiết phim (Customer)** + *(bonus)* Đánh giá | 5*, 18–21, 27, 31 | `feature/<n>-genre-crud`, `feature/<n>-movie-crud`, `feature/<n>-home-movie-detail` |
| **D** | **Phòng & suất chiếu (Admin)** + **Đặt vé chọn ghế (Customer)** | 6*, 22, 23, 28 | `feature/<n>-room-crud`, `feature/<n>-showtime-crud`, `feature/<n>-seat-booking` |
| **E** | **Quản lý đơn đặt vé (Admin)** + **Lịch sử & huỷ đơn (Customer)** + **Báo cáo** | 24, 25, 26, 29 | `feature/<n>-booking-admin`, `feature/<n>-booking-history`, `feature/<n>-revenue-report` |

\* Entity được Leader tạo trong Sprint 0 (để tránh xung đột); thành viên phụ trách module **review** PR entity của module mình và bổ sung query/validation trong nhánh feature.

**Nhóm 4 người:** D đảm nhận thêm *Quản lý đơn đặt vé (Admin)* (TODO 24, 25); E bị bỏ, *Lịch sử & huỷ đơn* (29) chuyển cho B, *Báo cáo* (26) chuyển cho A.

### 3.3 Ma trận phụ thuộc giữa các chức năng

```
Sprint 0 (A): setup ─► entities + repositories ─► seed ─► layout + error handling
                                     │
         ┌──────────────┬────────────┼──────────────┬──────────────┐
         ▼              ▼            ▼              ▼              ▼
     A: auth       B: customer   C: genre/movie  D: room/showtime  (E chờ D có Booking flow
     (13–15)        admin (16–17)   (18–21)        (22–23)          hoặc dùng seed data)
         │              │            │              │              │
         └──────┬───────┴────────────┴──────┬───────┘              │
                ▼                           ▼                      ▼
        B: profile (30)          C: home/detail (27)      E: booking admin (24–25)
                                            │                      │
                                            ▼                      ▼
                                  D: seat booking (28) ─────► E: history/cancel (29), report (26)
```

> Chức năng của Customer cần **đăng nhập** (A, TODO 13–15). Trong lúc chờ, thành viên có thể tạm lấy `customerId` cố định từ seed data, **nhưng không được merge** phần tạm đó.

---

## 4. Sprint 0 — Leader thiết lập repository

> Thực hiện **một lần** bởi Leader (A), trong 1–2 ngày đầu. Các thành viên khác chuẩn bị môi trường (mục 5) song song.

### Bước 1 — Tạo Organization / Repository

1. GitHub → **New organization** (Free) tên ví dụ `hsf302-se18xx-group3` *(tuỳ chọn — dùng tài khoản cá nhân cũng được)*.
2. **New repository**:
   - Name: `fu-cinema-ticket-system`
   - Description: `FU Cinema Ticket System — Spring Boot, Spring Data JPA, Thymeleaf, SQL Server`
   - Visibility: **Private**
     > GitHub Free + Private: không bật được branch protection/ruleset, CODEOWNERS, Draft PR. Nếu cần các tính năng này, Leader tạo repo dưới **tài khoản cá nhân đã kích hoạt GitHub Pro** (miễn phí qua *GitHub Student Developer Pack* với email `@fpt.edu.vn`), hoặc dùng repo **Public** nếu giảng viên cho phép. Nếu không, nhóm áp dụng các quy tắc ở Bước 3 như **quy ước bắt buộc** (Leader kiểm tra lịch sử commit của `main`/`develop`).
   - **Không** tick "Add README / .gitignore" (sẽ đẩy project từ máy lên).
3. **Settings → Collaborators and teams** (hoặc *People/Teams* nếu dùng Organization):
   - Thêm các thành viên quyền **Write**.
   - Thêm giảng viên quyền **Read**.

### Bước 2 — Tạo project và đẩy commit đầu tiên

```bash
# Tạo project bằng Spring Initializr (TODO 1) rồi mở terminal tại thư mục gốc project
git init -b main
git add .
git commit -m "chore(setup): init spring boot project"
git remote add origin https://github.com/<org>/fu-cinema-ticket-system.git
git push -u origin main

# Tạo nhánh develop
git switch -c develop
git push -u origin develop
```

`.gitignore` tối thiểu (bổ sung vào file Spring Initializr sinh ra):

```gitignore
target/
.idea/
*.iml
.vscode/
.DS_Store
*.log
application-local.properties
.env
```

### Bước 3 — Cấu hình repository

**Settings → General**
- **Default branch:** `develop` (PR mặc định nhắm vào `develop`).
- **Pull Requests:** ✔ *Allow squash merging* (mặc định: *Pull request title*) · ✔ *Allow merge commits* (dùng cho release/hotfix) · ✘ *Allow rebase merging* · ✔ *Automatically delete head branches*.

**Settings → Rules → Rulesets** (hoặc *Branches → Branch protection rules*) — tạo 2 rule:

| Thiết lập | `develop` | `main` |
|---|---|---|
| Require a pull request before merging | ✔ | ✔ |
| Required approvals | **1** | **2** |
| Dismiss stale approvals when new commits are pushed | ✔ | ✔ |
| Require review from Code Owners | ✘ | ✔ |
| Require conversation resolution before merging | ✔ | ✔ |
| Block force pushes | ✔ | ✔ |
| Restrict deletions | ✔ | ✔ |

> Không dùng CI nên **không cấu hình status check**. Thay vào đó, trước khi merge tác giả phải rebase lên `develop` mới nhất và chạy lại `mvn verify` (mục 6, Bước 8).
>
> Nếu repo Private trên GitHub Free (mục Rules không khả dụng), cả nhóm tuân thủ bảng trên như quy ước: không push trực tiếp `main`/`develop`, không tự merge PR khi chưa đủ approval, không force-push nhánh chung. Leader rà `git log --first-parent develop` mỗi sprint — commit không đi qua PR sẽ bị revert.

### Bước 4 — Labels, Milestones, Project board

**Issues → Labels** (xoá bớt label mặc định không dùng, tạo mới):

| Nhóm | Label |
|---|---|
| Loại | `type: feature`, `type: bug`, `type: chore`, `type: docs`, `type: refactor` |
| Module | `module: auth`, `module: customer`, `module: movie`, `module: showtime`, `module: booking`, `module: report`, `module: infra` |
| Ưu tiên | `priority: high`, `priority: medium`, `priority: low` |
| Trạng thái | `status: blocked`, `status: needs-info` |
| Khác | `good first issue`, `bonus` |

**Issues → Milestones:** `Sprint 0 — Foundation`, `Sprint 1 — Admin core`, `Sprint 2 — Customer flow`, `Sprint 3 — Report & Release v1.0.0` (đặt hạn chót cho từng sprint).

**Projects → New project → Board** tên `FUCinema Board`, các cột: `Backlog` → `To do (Sprint)` → `In progress` → `In review` → `Done`.
Bật *Workflows* mặc định: *Item added → Backlog*, *Pull request merged → Done*, *Item closed → Done*.

### Bước 5 — Thêm template, CODEOWNERS (PR đầu tiên)

```bash
git switch develop
git switch -c chore/1-repo-templates
# tạo các file theo mục 12:
#   .github/ISSUE_TEMPLATE/feature_request.md
#   .github/ISSUE_TEMPLATE/bug_report.md
#   .github/pull_request_template.md
#   .github/CODEOWNERS
#   src/main/resources/application-local.properties.example
git add .github src/main/resources/application-local.properties.example
git commit -m "chore(setup): add issue/pr templates and codeowners" -m "Refs #1"
git push -u origin chore/1-repo-templates
```

Mở PR `chore/1-repo-templates → develop`. **Lần đầu** (chưa có reviewer) Leader có thể dùng quyền admin để merge; từ đây trở đi tuân thủ rule đầy đủ.

### Bước 6 — Tạo issue cho toàn bộ TODO

Mỗi TODO trong `Assignment1.md` → **1 issue** (TODO lớn có thể tách nhỏ). Ví dụ:

| Issue | Tiêu đề | Labels | Milestone | Assignee |
|---|---|---|---|---|
| #1 | `[CHORE] Repo templates & CODEOWNERS` | `type: chore`, `module: infra` | Sprint 0 | A |
| #2 | `[FEATURE] Project setup & configuration (TODO 1–3)` | `type: feature`, `module: infra` | Sprint 0 | A |
| #3 | `[FEATURE] Entities & repositories skeleton (TODO 4–7, 9)` | `type: feature`, `module: infra` | Sprint 0 | A |
| #4 | `[FEATURE] Seed data (TODO 8)` | `type: feature`, `module: infra` | Sprint 0 | A |
| #5 | `[FEATURE] Global exception handling & layout (TODO 12)` | `type: feature`, `module: infra` | Sprint 0 | A |
| #6 | `[FEATURE] Login / logout / register / interceptor (TODO 13–15)` | `module: auth` | Sprint 1 | A |
| #7 | `[FEATURE] Admin customer management (TODO 16–17)` | `module: customer` | Sprint 1 | B |
| #8 | `[FEATURE] Genre CRUD (TODO 18)` | `module: movie` | Sprint 1 | C |
| #9 | `[FEATURE] Movie CRUD + soft delete (TODO 19–21)` | `module: movie` | Sprint 1 | C |
| #10 | `[FEATURE] Room CRUD (TODO 22)` | `module: showtime` | Sprint 1 | D |
| #11 | `[FEATURE] Showtime CRUD + overlap check (TODO 23)` | `module: showtime` | Sprint 1 | D |
| #12 | `[FEATURE] Admin booking management (TODO 24–25)` | `module: booking` | Sprint 1 | E |
| #13 | `[FEATURE] Home & movie detail (TODO 27)` | `module: movie` | Sprint 2 | C |
| #14 | `[FEATURE] Seat selection & booking (TODO 28)` | `module: booking` | Sprint 2 | D |
| #15 | `[FEATURE] Booking history & cancel (TODO 29)` | `module: booking` | Sprint 2 | E |
| #16 | `[FEATURE] Customer profile & change password (TODO 30)` | `module: customer` | Sprint 2 | B |
| #17 | `[FEATURE] Revenue report (TODO 26)` | `module: report` | Sprint 3 | E |
| #18 | `[FEATURE] Movie review (TODO 31)` | `module: movie`, `bonus` | Sprint 3 | C |
| #19 | `[DOCS] README & release v1.0.0 (TODO 32)` | `type: docs` | Sprint 3 | A |

Thêm tất cả issue vào board `FUCinema Board`, kéo issue Sprint 0 sang cột `To do (Sprint)`.

> Tạo nhanh bằng GitHub CLI (tuỳ chọn):
> ```bash
> gh issue create --title "[FEATURE] Movie CRUD + soft delete (TODO 19–21)" \
>   --label "type: feature,module: movie" --milestone "Sprint 1 — Admin core" --assignee <username-C> \
>   --body-file .github/ISSUE_TEMPLATE/feature_request.md
> ```

### Bước 7 — Làm nền tảng (Sprint 0) và merge sớm

Leader lần lượt làm issue #2 → #5, **mỗi issue một PR**, các thành viên review. Thứ tự merge: setup → entities/repositories → seed → layout/error. Khi #3 (entity) được merge, các thành viên mới bắt đầu feature của mình.

> Lý do Leader làm toàn bộ entity: entity là phần **nhiều người cùng sửa nhất** (quan hệ chéo giữa các bảng) → làm tập trung một lần giúp tránh xung đột và thống nhất thiết kế. Thành viên phụ trách module **bắt buộc review** PR entity phần của mình.

---

## 5. Sprint 0 — Thành viên chuẩn bị môi trường

```bash
# 1. Cấu hình Git (một lần trên mỗi máy)
git config --global user.name "Nguyen Van B"
git config --global user.email "bnvse180000@fpt.edu.vn"     # email trùng email tài khoản GitHub
git config --global pull.rebase true                       # pull dạng rebase, lịch sử gọn
git config --global core.autocrlf true                     # Windows (macOS/Linux: input)

# 2. Chấp nhận lời mời vào repository (email / GitHub notifications), rồi clone
git clone https://github.com/<org>/fu-cinema-ticket-system.git
cd fu-cinema-ticket-system
git switch develop

# 3. Tạo file cấu hình DB cá nhân (KHÔNG commit)
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
#    sửa username/password SQL Server của máy mình

# 4. Tạo database và chạy thử
#    SSMS: CREATE DATABASE FUCinemaTicketDB;
mvn spring-boot:run
```

Kiểm tra: `git status` **không** hiển thị `application-local.properties` (đã bị ignore).

> Xác thực khi push: dùng **Git Credential Manager** (Windows tự cài cùng Git for Windows) hoặc **Personal Access Token (fine-grained)** thay cho mật khẩu; hoặc SSH key.

---

## 6. Quy trình làm một chức năng

Vòng đời chuẩn cho **mọi** issue:

```
Issue (To do) ─► nhận việc ─► tạo nhánh ─► code + commit ─► push ─► Draft PR (In progress)
   ─► đồng bộ develop ─► Ready for review (In review) ─► review/sửa ─► mvn verify pass + approve
   ─► Squash merge ─► xoá nhánh ─► issue tự đóng (Done)
```

### Bước 1 — Nhận issue
- Trên board, kéo issue sang `In progress`, **Assign** cho mình.
- Đọc kỹ tiêu chí chấp nhận; có gì chưa rõ → hỏi ngay **trong comment của issue**.

### Bước 2 — Tạo nhánh từ `develop` mới nhất
```bash
git switch develop
git pull origin develop
git switch -c feature/9-movie-crud
```
*(Hoặc bấm **Create a branch** ngay trong trang issue → chọn base `develop`.)*

### Bước 3 — Code và commit nhỏ, thường xuyên
```bash
git status
git add src/main/java/com/fu/cinema/service/MovieService.java src/main/java/com/fu/cinema/service/impl/MovieServiceImpl.java
git commit -m "feat(movie): add movie service with search and pagination" -m "Refs #9"
```
- Mỗi commit một ý; build được (`mvn -q compile`).
- **Không** `git add .` khi chưa xem `git status` (tránh đưa file rác, file cấu hình cá nhân).

### Bước 4 — Push và mở **Draft PR** sớm
```bash
git push -u origin feature/9-movie-crud
```
GitHub → **Compare & pull request** → base `develop` → chọn **Create draft pull request**.
Draft PR giúp nhóm thấy tiến độ và góp ý sớm.
> Repo Private trên GitHub Free không có Draft PR → mở PR thường với tiêu đề bắt đầu bằng `[WIP]` (reviewer chưa review), bỏ `[WIP]` khi sẵn sàng.

### Bước 5 — Đồng bộ với `develop` (ít nhất mỗi ngày)
```bash
git fetch origin
git rebase origin/develop          # đưa commit của mình lên trên develop mới nhất
# nếu có xung đột → xem mục 9
git push --force-with-lease        # chỉ force-push trên NHÁNH CỦA MÌNH
```
> `--force-with-lease` an toàn hơn `--force`: từ chối nếu trên remote có commit mới mà bạn chưa có.

### Bước 6 — Tự kiểm tra rồi chuyển **Ready for review**
- Chạy app, đi hết tiêu chí chấp nhận của issue + phần checklist liên quan trong `Assignment1.md`.
- `mvn verify` pass trên máy mình (**bắt buộc** — thay cho CI); ghi kết quả vào mục *Cách kiểm thử* của PR.
- Điền đầy đủ PR template (mô tả, ảnh chụp màn hình, cách test, `Closes #9`).
- Bấm **Ready for review**, chọn **Reviewers** (1 người khác module + người phụ trách module liên quan nếu có).
- Kéo issue sang `In review`.

### Bước 7 — Review & sửa
- Reviewer góp ý trực tiếp trên dòng code; tác giả sửa bằng **commit mới** (không force-push trong lúc đang review để reviewer thấy thay đổi).
  ```bash
  git commit -m "fix(movie): validate duration range in movie form" -m "Refs #9"
  git push
  ```
- Trả lời từng comment, bấm **Resolve conversation** khi đã xử lý.

### Bước 8 — Merge
- Trước khi merge: rebase lên `develop` mới nhất (Bước 5) và chạy lại `mvn verify` — thay cho rule *Require branches to be up to date*.
- Khi build pass + đủ approval + hết conversation: **Squash and merge**.
- Tiêu đề squash commit = tiêu đề PR theo Conventional Commits, ví dụ:
  `feat(movie): movie management with search, filter and soft delete (#21)`
- Nội dung squash commit giữ dòng `Closes #9` → issue tự đóng.
- Nhánh tự xoá (đã bật *Automatically delete head branches*). Xoá nhánh local:
  ```bash
  git switch develop
  git pull
  git branch -d feature/9-movie-crud
  git fetch --prune
  ```

---

## 7. Hướng dẫn theo từng chức năng

> Mỗi chức năng: **issue → nhánh → các commit gợi ý (theo thứ tự) → tiêu đề PR → điểm reviewer cần soi → kiểm thử trước khi merge**.

### 7.1 [A] Nền tảng dự án — TODO 1–3, 12 (Sprint 0)

| | |
|---|---|
| Issue | #2 Project setup · #5 Layout & error handling |
| Nhánh | `feature/2-project-setup`, `feature/5-layout-error-handling` |
| Commits | `chore(setup): add dependencies for web, thymeleaf, jpa, validation and mssql` → `chore(config): add application properties with local profile` → `chore(setup): create layered package structure` → `feat(layout): add base thymeleaf layout with role-based menu` → `feat(error): add global exception handler and error pages` |
| PR | `chore(setup): bootstrap spring boot project structure and configuration` |
| Reviewer soi | Không có mật khẩu trong `application.properties`; package đúng cấu trúc; layout dùng `th:replace`/fragment; flash message hiển thị chung. |
| Test | Clone mới → làm theo README → app chạy được. |

### 7.2 [A] Entity & Repository khung — TODO 4–7, 9 (Sprint 0)

| | |
|---|---|
| Issue | #3 · Nhánh `feature/3-entities-repositories` |
| Commits | `feat(entity): add enums for roles and statuses` → `feat(entity): add account and customer with one-to-one mapping` → `feat(entity): add genre and movie entities` → `feat(entity): add room and showtime entities` → `feat(entity): add booking and ticket with cascade` → `feat(entity): add review with composite key` → `feat(repository): add jpa repositories for all entities` |
| PR | `feat(entity): add domain entities and repositories` |
| Reviewer soi | **Mỗi thành viên review phần entity của module mình.** FK, unique, `nullable`, `FetchType.LAZY`, `mappedBy` đúng phía, không `@Data`, enum `STRING`, `BigDecimal` cho tiền, `@EmbeddedId` + `@MapsId` cho Review. |
| Test | App khởi động, SSMS thấy đủ 9 bảng với FK/PK đúng. |

> Sau PR này, **mọi thay đổi entity** phải qua issue riêng có label `module: infra` và thông báo cho cả nhóm (vì ảnh hưởng nhiều module).

### 7.3 [A] Seed data — TODO 8 (Sprint 0)

| | |
|---|---|
| Nhánh | `feature/4-seed-data` |
| Commits | `feat(seed): add password encoder bean` → `feat(seed): seed accounts, customers, genres and movies` → `feat(seed): seed rooms, showtimes and sample bookings` |
| Reviewer soi | Seed chỉ chạy khi bảng rỗng; dữ liệu đủ để test mọi chức năng (suất đã qua, suất huỷ, đơn đủ trạng thái, khách dưới 16 tuổi). |

### 7.4 [A] Đăng nhập, đăng ký, phân quyền — TODO 13–15 (Sprint 1)

| | |
|---|---|
| Issue | #6 · Nhánh `feature/6-auth` |
| Commits | `feat(auth): add login and logout with session` → `feat(auth): block locked accounts on login` → `feat(auth): add customer registration with validation` → `feat(auth): add role-based interceptor for admin and customer paths` → `feat(error): add 403 page for forbidden access` |
| PR | `feat(auth): authentication, registration and role-based authorization` |
| Reviewer soi | So khớp mật khẩu bằng `passwordEncoder.matches`; không lưu password vào session; đăng ký tạo Account + Customer trong **1 transaction**; interceptor loại trừ `/login`, `/register`, `/css/**`, `/`. |
| Test | Đủ 3 trường hợp: Admin, Customer, tài khoản LOCKED; truy cập chéo vai trò → 403. |
| Lưu ý nhóm | Merge **sớm nhất có thể** trong Sprint 1 — các chức năng Customer phụ thuộc vào đây. Leader cung cấp helper `SessionUtils.getCurrentCustomerId(session)` cho mọi người dùng chung. |

### 7.5 [B] Quản lý khách hàng (Admin) — TODO 16–17 (Sprint 1)

| | |
|---|---|
| Issue | #7 · Nhánh `feature/7-customer-admin` |
| Commits | `feat(customer): add customer search query with pagination` → `feat(customer): add admin customer list page` → `feat(customer): add create and edit customer form` → `feat(customer): lock and unlock customer account` → `feat(customer): delete customer or lock when bookings exist` |
| PR | `feat(customer): admin customer management` |
| Reviewer soi | Query tìm kiếm tham số hoá; kiểm tra trùng email/SĐT/CCCD khi **sửa** (loại trừ chính bản ghi đang sửa); xoá dùng POST + xác nhận; rule BR-04. |
| Test | Xoá khách chưa có đơn → mất khỏi DB; khách đã có đơn → `LOCKED` và không đăng nhập được. |

### 7.6 [C] Thể loại & phim (Admin) — TODO 18–21 (Sprint 1)

| | |
|---|---|
| Issue | #8 Genre · #9 Movie |
| Nhánh | `feature/8-genre-crud`, `feature/9-movie-crud` (**2 PR riêng**, Genre merge trước) |
| Commits (Movie) | `feat(movie): add movie search with genre and status filters` → `feat(movie): add admin movie list with pagination and sorting` → `feat(movie): add movie form with validation` → `feat(movie): soft delete movie that has showtimes` |
| PR | `feat(genre): genre management` · `feat(movie): movie management with filters and soft delete` |
| Reviewer soi | BR-05, BR-06; dropdown thể loại; giữ tham số lọc khi phân trang; không hiện phim `STOPPED` ở các dropdown tạo suất chiếu. |

### 7.7 [D] Phòng & suất chiếu (Admin) — TODO 22–23 (Sprint 1)

| | |
|---|---|
| Issue | #10 Room · #11 Showtime |
| Nhánh | `feature/10-room-crud`, `feature/11-showtime-crud` |
| Commits (Showtime) | `feat(showtime): add overlap check query for room schedule` → `feat(showtime): add showtime form with end time suggestion` → `feat(showtime): validate time range, room status and movie status` → `feat(showtime): cancel showtime and its bookings when deleting` |
| PR | `feat(showtime): showtime scheduling with overlap validation` |
| Reviewer soi | Điều kiện chồng giờ `start < otherEnd AND end > otherStart`; loại trừ chính suất đang sửa; huỷ suất + huỷ đơn trong **1 transaction** (`@Modifying` với `clearAutomatically`). |
| Phối hợp | Q9 (huỷ hàng loạt đơn) nằm trong `BookingRepository` — **báo trước cho E** để tránh cùng sửa một file; ưu tiên D thêm method, E review. |

### 7.8 [E] Quản lý đơn đặt vé (Admin) — TODO 24–25 (Sprint 1)

| | |
|---|---|
| Issue | #12 · Nhánh `feature/12-booking-admin` |
| Commits | `feat(booking): add admin booking filter query` → `feat(booking): add booking detail with join fetch` → `feat(booking): add admin booking list and detail pages` → `feat(booking): confirm and cancel booking with status rules` |
| PR | `feat(booking): admin booking management` |
| Reviewer soi | Chi tiết đơn không bị lazy exception; luồng trạng thái BR-13 nằm trong service (không nằm trong controller). |
| Phụ thuộc | Dùng đơn trong seed data để làm trước, không cần chờ D xong đặt vé. |

### 7.9 [C] Trang chủ & chi tiết phim (Customer) — TODO 27 (Sprint 2)

| | |
|---|---|
| Nhánh | `feature/13-home-movie-detail` |
| Commits | `feat(movie): add home page with showing and coming soon movies` → `feat(movie): add movie detail with upcoming showtimes` |
| Reviewer soi | Ẩn phim `STOPPED` và suất `CANCELLED`/đã qua; khách chưa đăng nhập vẫn xem được. |

### 7.10 [D] Đặt vé chọn ghế (Customer) — TODO 28 (Sprint 2) — *chức năng phức tạp nhất*

| | |
|---|---|
| Issue | #14 · Nhánh `feature/14-seat-booking` |
| Commits | `feat(booking): add sold seats query for showtime` → `feat(booking): render seat map for showtime` → `feat(booking): create booking with multiple tickets in one transaction` → `feat(booking): validate seat availability, showtime status and age rating` → `test(booking): add unit tests for booking validation` |
| PR | `feat(booking): online seat selection and booking` |
| Reviewer soi | Toàn bộ BR-10 → BR-12; kiểm tra lại ghế trống **ngay trong transaction** trước khi lưu (không tin dữ liệu từ form); tổng tiền tính ở server; xử lý khi 2 người đặt trùng ghế. |
| Khuyến nghị | PR lớn → chia 2 PR: (1) hiển thị sơ đồ ghế, (2) tạo đơn. Nhờ **2 reviewer**. |

### 7.11 [E] Lịch sử & huỷ đơn (Customer) — TODO 29 (Sprint 2)

| | |
|---|---|
| Nhánh | `feature/15-booking-history` |
| Commits | `feat(booking): add customer booking history with pagination` → `feat(booking): allow customer to cancel booking before showtime` → `fix(booking): prevent access to other customers bookings` |
| Reviewer soi | Luôn lấy `customerId` từ **session**, không lấy từ form/URL; BR-14 (trước giờ chiếu ≥ 2 giờ). |

### 7.12 [B] Hồ sơ & đổi mật khẩu — TODO 30 (Sprint 2)

| | |
|---|---|
| Nhánh | `feature/16-profile` |
| Commits | `feat(profile): view and update customer profile` → `feat(profile): change password with current password check` |
| Reviewer soi | Không cho đổi username; kiểm tra trùng email/SĐT với **người khác**; mật khẩu mới mã hoá BCrypt. |

### 7.13 [E] Báo cáo doanh thu — TODO 26 (Sprint 3)

| | |
|---|---|
| Issue | #17 · Nhánh `feature/17-revenue-report` |
| Commits | `feat(report): add revenue by movie query with dto projection` → `feat(report): add confirmed bookings in period query` → `feat(report): add report page with date range filter and totals` |
| PR | `feat(report): revenue report by date range` |
| Reviewer soi | Chỉ tính `CONFIRMED`; khoảng ngày bao gồm cả ngày cuối (`endDate` → `endDate.plusDays(1).atStartOfDay()` với điều kiện `<`); sắp xếp giảm dần; validate `startDate ≤ endDate`. |

### 7.14 [C] *(Bonus)* Đánh giá phim — TODO 31 (Sprint 3)

| | |
|---|---|
| Nhánh | `feature/18-movie-review` · label `bonus` |
| Commits | `feat(review): add rating summary query` → `feat(review): allow customers who watched to review movie` |
| Reviewer soi | Khoá kép (customerId, movieId) → sửa nếu đã tồn tại; BR-16. |

### 7.15 [A] README & Release — TODO 32 (Sprint 3)

| | |
|---|---|
| Nhánh | `docs/19-readme` → sau đó `release/1.0.0` (xem mục 10) |
| Commits | `docs(readme): add setup guide, accounts and screenshots` → `docs(readme): add team members and task assignment` |

---

## 8. Pull Request & Code Review

### 8.1 Tiêu chuẩn một PR tốt
- **Một PR = một issue**, base là `develop`.
- Tiêu đề theo Conventional Commits (sẽ thành commit message sau khi squash).
- Mô tả theo template: *What / Why / How to test / Screenshots / Checklist / `Closes #`*.
- Kích thước hợp lý (< 400 dòng thay đổi, không tính file HTML lớn). PR quá lớn → tách.
- Không chứa thay đổi ngoài phạm vi issue (format lại file khác, đổi tên lung tung…).

### 8.2 Checklist dành cho Reviewer

**Chức năng**
- [ ] Đáp ứng đủ tiêu chí chấp nhận của issue; đã checkout nhánh chạy thử (`gh pr checkout <số>` hoặc `git fetch origin && git switch <nhánh>`).
- [ ] Quy tắc nghiệp vụ (BR) liên quan nằm ở **service**, có thông báo lỗi rõ ràng.

**Kiến trúc & code**
- [ ] Controller → Service (interface) → Repository; controller không gọi repository.
- [ ] `@Transactional` đúng chỗ; method ghi không bị `readOnly`.
- [ ] Query tham số hoá; không N+1 rõ ràng; không `LazyInitializationException`.
- [ ] Validate form bằng Bean Validation + kiểm tra nghiệp vụ ở service.
- [ ] Đặt tên rõ nghĩa; không code chết, không `System.out.println` debug; không comment-out code.

**Bảo mật & cấu hình**
- [ ] Không có mật khẩu/token trong code; không log mật khẩu.
- [ ] Thao tác thay đổi dữ liệu dùng POST; kiểm tra quyền sở hữu dữ liệu (Customer chỉ thao tác dữ liệu của mình).

**Quy trình**
- [ ] Đã chạy `mvn verify` trên máy reviewer → `BUILD SUCCESS`; commit đúng chuẩn; PR liên kết issue.

### 8.3 Cách góp ý

| Tiền tố | Ý nghĩa |
|---|---|
| `blocker:` | Phải sửa trước khi merge (sai nghiệp vụ, lỗi bảo mật, lỗi chạy). |
| `suggestion:` | Nên sửa, có thể làm ở PR sau (tạo issue). |
| `nit:` | Chi tiết nhỏ (tên biến, format) — không chặn merge. |
| `question:` | Cần tác giả giải thích. |

Góp ý về **code**, không về **người**; luôn đưa lý do hoặc gợi ý cách sửa. Dùng **Request changes** khi có `blocker`, **Approve** khi đạt, **Comment** khi chỉ hỏi.

### 8.4 Thời hạn
- Reviewer phản hồi PR trong **24 giờ**.
- Tác giả xử lý góp ý trong **24 giờ**; nếu bất đồng, thảo luận trong PR, Leader quyết định cuối cùng.

---

## 9. Xử lý xung đột

### 9.1 Phòng tránh
- Pull/rebase `develop` **mỗi ngày**; PR nhỏ, merge nhanh.
- **File dễ xung đột** — quy định người sở hữu, ai sửa phải báo trước trong nhóm:

| File | Người sở hữu | Quy tắc |
|---|---|---|
| `pom.xml` | A | Thêm dependency qua issue `chore`, không tự ý thêm trong nhánh feature. |
| `application.properties` | A | Chỉ thêm key mới, không sửa key người khác. |
| `entity/*` | A | Thay đổi qua issue `module: infra`. |
| `templates/fragments/header.html` (menu) | A | Mỗi người chỉ thêm **một dòng** menu cho module của mình. |
| `BookingRepository` | D & E | Thêm method ở **cuối file**, mỗi method một commit riêng. |
| `DataSeeder` | A | Thêm dữ liệu ở phương thức riêng theo module. |

### 9.2 Giải quyết khi rebase
```bash
git fetch origin
git rebase origin/develop
# Git dừng lại, báo file xung đột
git status                         # xem file "both modified"
# Mở file, tìm các đoạn:
# <<<<<<< HEAD (develop)  ...  ======= ...  >>>>>>> (commit của bạn)
# Sửa thành nội dung đúng (thường là GIỮ CẢ HAI phần thay đổi), xoá các dấu <<< === >>>
git add <file-đã-sửa>
git rebase --continue              # lặp lại cho đến khi xong
mvn -q compile                     # chắc chắn vẫn build được
git push --force-with-lease
```
- Muốn huỷ giữa chừng: `git rebase --abort`.
- Xung đột trong phần của người khác → **gọi người đó cùng xử lý**, không tự quyết.
- IntelliJ: *Git → Resolve Conflicts…* hiển thị 3 cột (yours / result / theirs) giúp xử lý trực quan.

### 9.3 Lỡ tay
| Tình huống | Cách xử lý |
|---|---|
| Commit nhầm lên `develop` local (chưa push) | `git switch -c feature/<n>-...` (mang commit sang nhánh mới) → `git switch develop` → `git reset --hard origin/develop` |
| Commit nhầm file `application-local.properties` (chưa push) | `git rm --cached <file>` → `git commit --amend` |
| **Đã push** mật khẩu lên GitHub | Đổi mật khẩu **ngay lập tức**; xoá khỏi code bằng commit mới; báo Leader (coi mật khẩu đã lộ — xoá lịch sử không đủ an toàn). |
| Merge nhầm PR vào `develop` | Trên PR đã merge bấm **Revert** → tạo PR revert → review → merge. Không force-push `develop`. |
| Sửa message commit cuối (chưa push) | `git commit --amend -m "..."` |

---

## 10. Release & Hotfix

### 10.1 Phát hành v1.0.0 (cuối Sprint 3)

```bash
# 1. Leader tạo nhánh release từ develop khi đã đủ chức năng (code freeze)
git switch develop && git pull
git switch -c release/1.0.0
git push -u origin release/1.0.0
```
2. **Tester của sprint** chạy toàn bộ checklist `Assignment1.md` trên `release/1.0.0`. Lỗi tìm thấy → issue `type: bug` → nhánh `fix/<n>-...` **tạo từ `release/1.0.0`**, PR vào `release/1.0.0`.
3. Khi ổn định: mở PR `release/1.0.0 → main`, tiêu đề `release: v1.0.0`, cần **2 approvals**, chọn **Create a merge commit**.
4. Gắn tag và tạo GitHub Release:
   ```bash
   git switch main && git pull
   git tag -a v1.0.0 -m "FUCinemaTicketSystem v1.0.0"
   git push origin v1.0.0
   ```
   GitHub → **Releases → Draft a new release** → chọn tag `v1.0.0` → **Generate release notes** (tự liệt kê các PR) → Publish.
5. **Back-merge** về `develop` để `develop` có các bản sửa lỗi trong release: PR `main → develop` (merge commit).
6. Đóng milestone Sprint 3.

### 10.2 Hotfix (lỗi nghiêm trọng phát hiện sau khi phát hành)
```bash
git switch main && git pull
git switch -c hotfix/1.0.1-booking-total-amount
# sửa lỗi
git commit -m "fix(booking): correct total amount when booking multiple seats" -m "Closes #42"
git push -u origin hotfix/1.0.1-booking-total-amount
```
PR vào `main` (2 approvals) → tag `v1.0.1` → release → back-merge `main → develop`.

### 10.3 Semantic Versioning
`MAJOR.MINOR.PATCH` — `1.0.0` bản đầu; thêm chức năng tương thích → `1.1.0`; sửa lỗi → `1.0.1`.

---

## 11. Kế hoạch sprint gợi ý

Giả sử thời gian làm bài **4 tuần**:

| Sprint | Thời gian | Mục tiêu | Kết quả cuối sprint |
|---|---|---|---|
| **Sprint 0** | Ngày 1–3 | Repo, template, project, entity, repository, seed, layout | `develop` chạy được, có đủ bảng & dữ liệu mẫu |
| **Sprint 1** | Tuần 1–2 | Auth + toàn bộ chức năng Admin (khách hàng, thể loại, phim, phòng, suất chiếu, quản lý đơn) | Admin thao tác được toàn bộ CRUD |
| **Sprint 2** | Tuần 3 | Luồng Customer: trang chủ, chi tiết phim, đặt vé, lịch sử/huỷ, hồ sơ | Khách hàng đặt vé end-to-end |
| **Sprint 3** | Tuần 4 | Báo cáo, bonus, sửa lỗi, README, release `v1.0.0` | Tag `v1.0.0` trên `main` |

**Nghi thức (ceremony) tối thiểu:**
- **Sprint planning** (đầu sprint, 30 phút): chọn issue vào sprint, assign, ước lượng.
- **Daily stand-up** (10 phút, có thể nhắn trong nhóm): *Hôm qua làm gì? Hôm nay làm gì? Đang bị chặn bởi gì?*
- **Sprint review + retro** (cuối sprint, 30 phút): demo trên `develop`; điều gì tốt / cần cải thiện → ghi vào một issue `docs` hoặc Discussion.

### Definition of Done (DoD) — một issue chỉ được coi là xong khi:
- [ ] Code đáp ứng đủ tiêu chí chấp nhận.
- [ ] Đã tự test thủ công các trường hợp chính và trường hợp lỗi.
- [ ] `mvn verify` pass (tác giả + reviewer), có ≥ 1 approval, mọi conversation đã resolve.
- [ ] Đã squash merge vào `develop`, nhánh đã xoá, issue đã đóng, board ở cột `Done`.
- [ ] Nếu thay đổi cách cài đặt/chạy → đã cập nhật README.

---

## 12. Phụ lục: template & cấu hình mẫu

### 12.1 `.github/ISSUE_TEMPLATE/feature_request.md`

```markdown
---
name: Feature request
about: Chức năng mới theo TODO của Assignment
title: "[FEATURE] "
labels: "type: feature"
---

## Mô tả
<!-- Chức năng gì, cho vai trò nào (Admin/Customer) -->

## TODO liên quan
TODO ...

## Tiêu chí chấp nhận
- [ ] ...
- [ ] ...

## Quy tắc nghiệp vụ áp dụng
BR-...

## Ghi chú kỹ thuật
<!-- Repository/service/controller/template dự kiến, phụ thuộc issue khác (#...) -->
```

### 12.2 `.github/ISSUE_TEMPLATE/bug_report.md`

```markdown
---
name: Bug report
about: Báo lỗi
title: "[BUG] "
labels: "type: bug"
---

## Mô tả lỗi

## Các bước tái hiện
1. Đăng nhập bằng ...
2. Vào trang ...
3. ...

## Kết quả mong đợi

## Kết quả thực tế
<!-- Ảnh chụp màn hình, log lỗi (không dán mật khẩu) -->

## Môi trường
Nhánh/commit: ...   ·   Trình duyệt: ...
```

### 12.3 `.github/pull_request_template.md`

```markdown
## Mô tả
<!-- Thay đổi gì và vì sao -->

Closes #

## Loại thay đổi
- [ ] feat  - [ ] fix  - [ ] refactor  - [ ] docs  - [ ] chore  - [ ] test

## Cách kiểm thử
1. Đăng nhập bằng ...
2. ...
Kết quả mong đợi: ...

## Ảnh chụp màn hình
<!-- Bắt buộc với thay đổi giao diện -->

## Checklist
- [ ] Tiêu đề PR theo Conventional Commits
- [ ] Đã rebase/merge develop mới nhất
- [ ] `mvn verify` → `BUILD SUCCESS` trên máy mình
- [ ] Đã tự test các trường hợp chính và trường hợp lỗi
- [ ] Controller không gọi repository; nghiệp vụ nằm ở service
- [ ] Không chứa mật khẩu/thông tin nhạy cảm
- [ ] Đã cập nhật README (nếu cần)
```

### 12.4 `.github/CODEOWNERS`

```text
# Người sở hữu mặc định
*                                                   @leader-A

# Theo module (thay bằng username GitHub thật)
/src/main/java/com/fu/cinema/entity/                @leader-A
/pom.xml                                            @leader-A
/src/main/resources/application.properties          @leader-A
**/*Customer*                                       @member-B
**/*Profile*                                        @member-B
**/*Genre*                                          @member-C
**/*Movie*                                          @member-C
**/*Review*                                         @member-C
**/*Room*                                           @member-D
**/*Showtime*                                       @member-D
**/*Booking*                                        @member-E
**/*Ticket*                                         @member-E
**/*Report*                                         @member-E
```
> Mỗi dòng chỉ gồm **một pattern** và danh sách owner. Nếu nhiều dòng cùng khớp một file, **dòng cuối cùng** được áp dụng — vì vậy file entity `Movie.java` sẽ thuộc `@member-C` (dòng sau ghi đè dòng `entity/`).
> Với rule "Require review from Code Owners" (bật cho `main`), PR chạm vào file của module nào sẽ tự động yêu cầu chủ module review.
> Repo Private trên GitHub Free **không hỗ trợ CODEOWNERS** (file vẫn có thể commit nhưng không có tác dụng) → tác giả PR tự chọn chủ module trong *Reviewers* theo bảng trên.

### 12.5 Kiểm tra build cục bộ (thay cho CI)

Nhóm dùng GitHub Free nên **không cấu hình GitHub Actions**. Bước kiểm tra tự động được thay bằng lệnh chạy trên máy:

```bash
# Tác giả — trước khi mở PR / chuyển Ready for review / merge
git fetch origin && git rebase origin/develop
mvn -B -ntp clean verify           # biên dịch + chạy test với profile test (H2)

# Reviewer — khi review
gh pr checkout <số-PR>             # hoặc: git fetch origin && git switch <nhánh>
mvn -B -ntp clean verify
mvn spring-boot:run                # chạy thử theo "Cách kiểm thử" trong PR
```

> Test không cần SQL Server: thêm dependency `com.h2database:h2` (scope `test`) và file `src/test/resources/application-test.properties` dùng H2 (`spring.datasource.url=jdbc:h2:mem:testdb;MODE=MSSQLServer`, `spring.jpa.hibernate.ddl-auto=create-drop`); class test dùng `@ActiveProfiles("test")`. Test chạm query **native SQL Server** (Q11) nên đánh dấu `@Disabled` hoặc tách profile riêng.

*(Tuỳ chọn)* Git hook tự chạy `mvn verify` trước mỗi lần push — tạo file `.githooks/pre-push` (commit vào repo):

```bash
#!/bin/sh
echo "pre-push: mvn verify..."
mvn -q -B -ntp verify || { echo "Build/test FAILED - push bị huỷ"; exit 1; }
```

Mỗi thành viên bật một lần: `git config core.hooksPath .githooks` (macOS/Linux thêm `chmod +x .githooks/pre-push`). Bỏ qua khi thật cần: `git push --no-verify`.

### 12.6 `src/main/resources/application-local.properties.example`

```properties
# Copy thành application-local.properties (đã .gitignore) và điền thông tin máy bạn
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FUCinemaTicketDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=CHANGE_ME
```

`application.properties` (được commit):
```properties
spring.application.name=fu-cinema-ticket-system
spring.profiles.active=local
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.thymeleaf.cache=false
```

---

## 13. Cheat sheet lệnh Git

| Mục đích | Lệnh |
|---|---|
| Cập nhật develop | `git switch develop && git pull` |
| Tạo nhánh feature | `git switch -c feature/<n>-<mo-ta>` |
| Xem thay đổi | `git status` · `git diff` · `git diff --staged` |
| Stage từng phần | `git add -p` |
| Commit | `git commit -m "feat(scope): subject" -m "Refs #n"` |
| Push lần đầu | `git push -u origin <nhánh>` |
| Đồng bộ với develop | `git fetch origin && git rebase origin/develop` |
| Push sau rebase | `git push --force-with-lease` |
| Cất tạm thay đổi | `git stash` · `git stash pop` |
| Xem lịch sử gọn | `git log --oneline --graph --decorate -20` |
| Huỷ thay đổi 1 file (chưa commit) | `git restore <file>` |
| Bỏ stage 1 file | `git restore --staged <file>` |
| Sửa commit cuối (chưa push) | `git commit --amend` |
| Hoàn tác commit đã push | `git revert <hash>` |
| Dọn nhánh đã xoá trên remote | `git fetch --prune` · `git branch -d <nhánh>` |
| Checkout PR để review (GitHub CLI) | `gh pr checkout <số-PR>` |
| Tạo PR (GitHub CLI) | `gh pr create --base develop --fill --draft` |

---

## 14. Checklist làm việc nhóm

### Thiết lập (Leader)
- [ ] Repository private, đủ thành viên (Write) và giảng viên (Read).
- [ ] Nhánh `main`, `develop`; default branch = `develop`.
- [ ] Ruleset/branch protection cho `main` (2 approvals) và `develop` (1 approval), chặn force-push *(repo Private trên GitHub Free: thống nhất thành quy ước nhóm — xem mục 4, Bước 3)*.
- [ ] Chỉ bật squash + merge commit; tự xoá nhánh sau merge.
- [ ] Labels, 4 milestone, Project board có automation.
- [ ] Issue template, PR template, CODEOWNERS, `application-test.properties` (H2), `application-local.properties.example`.
- [ ] Cả nhóm chạy được `mvn verify` trên máy (thay cho CI).
- [ ] Mỗi TODO có issue, gắn label, milestone, assignee.

### Mỗi thành viên
- [ ] Cấu hình `user.name`/`user.email` trùng tài khoản GitHub.
- [ ] Không có `application-local.properties`, `target/`, `.idea/` trong repository.
- [ ] Mỗi chức năng được giao: issue → nhánh đúng tên → commit đúng chuẩn → PR → review → squash merge.
- [ ] Review ít nhất **1 PR của người khác** mỗi sprint.
- [ ] Không push trực tiếp `main`/`develop`; không force-push nhánh dùng chung.

### Cuối dự án
- [ ] Mọi issue Sprint 0–3 đã đóng (trừ bonus không làm — đóng với lý do *not planned*).
- [ ] `release/1.0.0` → `main`, tag `v1.0.0`, GitHub Release có release notes.
- [ ] `main` được back-merge về `develop`.
- [ ] README có hướng dẫn chạy, tài khoản mẫu, bảng phân công, ảnh màn hình.
- [ ] Insights → **Contributors** cho thấy mọi thành viên đều có commit; mọi PR có reviewer.
