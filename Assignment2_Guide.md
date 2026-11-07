# Assignment_Guide.md — Gợi ý lời giải Assignment 02 (FU Cinema Desktop)

> Đi kèm `Assignment_README.md`. Tài liệu này dùng để tham khảo, sinh viên có thể làm khác miễn đáp ứng được các yêu cầu TODO, commit đúng yêu cầu.
> Code dưới đây là **khung gợi ý** bám đúng API của backend Assignment 01 (`HuongDan.md`): `AuthService.login`, `MovieService.searchForAdmin`, `ShowtimeService.getSeatMap`, `BookingService.create`… Tên package gốc: `com.fu.cinema`.
> Lombok đã có trong project → dùng `@RequiredArgsConstructor`, `@Slf4j`. Các `import` hiển nhiên được lược bớt.

---

## Mục lục

- [0. Năm điều phải nắm trước khi code](#0-năm-điều-phải-nắm-trước-khi-code)
- [TODO 33 — Tích hợp JavaFX](#todo-33--tích-hợp-javafx)
- [TODO 34 — Điều hướng & khung chính](#todo-34--điều-hướng--khung-chính)
- [TODO 35 — Tiện ích dùng chung](#todo-35--tiện-ích-dùng-chung)
- [TODO 36 — Đăng nhập desktop](#todo-36--đăng-nhập-desktop)
- [TODO 37 — Dashboard](#todo-37--dashboard)
- [TODO 38 — Thể loại](#todo-38--thể-loại)
- [TODO 39 — Danh sách phim](#todo-39--danh-sách-phim)
- [TODO 40 — Dialog thêm/sửa phim](#todo-40--dialog-thêmsửa-phim)
- [TODO 41 — Phòng & suất chiếu](#todo-41--phòng--suất-chiếu)
- [TODO 42 — SeatMapView](#todo-42--seatmapview)
- [TODO 43 — Bán vé tại quầy](#todo-43--bán-vé-tại-quầy)
- [TODO 44 — Quản lý đơn](#todo-44--quản-lý-đơn)
- [TODO 45 — Báo cáo doanh thu & CSV](#todo-45--báo-cáo-doanh-thu--csv)
- [TODO 46 — README, test, release](#todo-46--readme-test-release)
- [Bonus — gợi ý nhanh](#bonus--gợi-ý-nhanh)
- [Bảng lỗi thường gặp](#bảng-lỗi-thường-gặp)

---

## 0. Năm điều phải nắm trước khi code

| # | Điều | Vì sao (riêng project này) |
|---|---|---|
| 1 | **Spring khởi động trong `init()`**, `WebApplicationType.NONE` | Không có Tomcat → chạy song song với web (cổng 8080) không đụng nhau. |
| 2 | **Tắt DevTools restart** | Pom Assignment 01 có `spring-boot-devtools`; restart classloader làm `FXMLLoader` nạp controller ở classloader khác → `ClassCastException`. |
| 3 | **Facade map entity → record trong transaction** | `spring.jpa.open-in-view=false` + mọi `@ManyToOne(LAZY)` → gọi `booking.getCustomer().getFullName()` trên FX thread sẽ ném `LazyInitializationException`. |
| 4 | **Method ghi của Facade phải `@Transactional` (không readOnly)** | Nếu class Facade gắn `@Transactional(readOnly = true)` mà method ghi không ghi đè, `BookingService.create` (`@Transactional`, propagation REQUIRED) sẽ **tham gia** transaction read-only bên ngoài → Hibernate để flush mode MANUAL → **dữ liệu không được lưu mà không báo lỗi**. |
| 5 | **Gọi Facade trong `Task`** | Truy vấn SQL Server trên FX thread làm UI đơ; cập nhật UI chỉ trong `setOnSucceeded/setOnFailed`. |

Luồng chuẩn của mọi màn hình:

```java
fxAsync.run(() -> movieFacade.search(...),          // background thread (virtual thread)
            page -> { movies.setAll(page.items()); }); // FX thread
```

---

## TODO 33 — Tích hợp JavaFX

**File:** `pom.xml`, `desktop/DesktopLauncher.java`, `desktop/CinemaFxApp.java`, `desktop/StageReadyEvent.java`, `desktop/StageInitializer.java`, `resources/application-desktop.properties`.

### `pom.xml` — thêm

```xml
<properties>
    <java.version>21</java.version>
    <javafx.version>21.0.5</javafx.version>
</properties>

<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>${javafx.version}</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>${javafx.version}</version>
</dependency>
```

> Giữ nguyên `spring-boot-maven-plugin` (main của web). Không thêm `module-info.java`.

### `DesktopLauncher.java`

```java
package com.fu.cinema.desktop;

import javafx.application.Application;

/** Entry point desktop. KHÔNG extends Application → tránh "JavaFX runtime components are missing". */
public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");   // phải đặt TRƯỚC khi Spring chạy
        Application.launch(CinemaFxApp.class, args);
    }
}
```

### `CinemaFxApp.java`

```java
package com.fu.cinema.desktop;

import com.fu.cinema.FuCinemaTicketSystemApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class CinemaFxApp extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {                                   // JavaFX-Launcher thread, chưa có UI
        context = new SpringApplicationBuilder(FuCinemaTicketSystemApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .profiles("desktop")                       // cộng thêm profile "local" khai báo trong application.properties
                .run(getParameters().getRaw().toArray(String[]::new));
    }

    @Override
    public void start(Stage stage) {
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        if (context != null) {
            context.close();
        }
        Platform.exit();
    }
}
```

### `StageReadyEvent.java` + `StageInitializer.java`

```java
public class StageReadyEvent extends ApplicationEvent {
    public StageReadyEvent(Stage stage) { super(stage); }
    public Stage getStage() { return (Stage) getSource(); }
}
```

```java
@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final SceneManager sceneManager;                 // TODO 34

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        sceneManager.init(event.getStage());
        sceneManager.switchTo(View.LOGIN);
    }
}
```

> Ở commit TODO 33 (chưa có `SceneManager`) có thể tạm hiển thị `new Scene(new Label("FU Cinema Desktop"), 800, 600)` để build được; TODO 34 thay bằng `SceneManager`.

### `application-desktop.properties`

```properties
spring.main.web-application-type=none
spring.devtools.restart.enabled=false
spring.jpa.show-sql=false
spring.threads.virtual.enabled=true
```

### Chạy

- IntelliJ: Run `DesktopLauncher.main()` (tạo Run Configuration riêng, *Active profiles* để trống — profile đặt trong code).
- Maven: `mvn spring-boot:run -Dspring-boot.run.main-class=com.fu.cinema.desktop.DesktopLauncher`

**Tự kiểm tra:** log có `The following 2 profiles are active: "local", "desktop"` và **không** có `Tomcat started`; mở web song song vẫn chạy.

---

## TODO 34 — Điều hướng & khung chính

### `navigation/View.java`

```java
public enum View {
    LOGIN("login.fxml", "Đăng nhập"),
    MAIN("main.fxml", "FU Cinema — Quầy vé"),
    DASHBOARD("dashboard.fxml", "Dashboard"),
    GENRE("genre.fxml", "Thể loại"),
    MOVIE("movie.fxml", "Phim"),
    MOVIE_DIALOG("movie-dialog.fxml", "Phim"),
    SCHEDULE("schedule.fxml", "Phòng & Suất chiếu"),
    COUNTER_SALE("counter-sale.fxml", "Bán vé tại quầy"),
    BOOKING("booking.fxml", "Đơn đặt vé"),
    REPORT("report.fxml", "Báo cáo doanh thu");

    private final String file;
    private final String title;

    View(String file, String title) { this.file = file; this.title = title; }

    public String path()  { return "/desktop/fxml/" + file; }
    public String title() { return title; }
}
```

### `navigation/SceneManager.java`

```java
@Component
@RequiredArgsConstructor
public class SceneManager {

    private static final String CSS = "/desktop/css/desktop.css";

    private final ApplicationContext context;
    private Stage stage;
    private Scene scene;

    public void init(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(new StackPane(), 1200, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(CSS)).toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(640);
        stage.show();
    }

    /** Đổi toàn bộ nội dung cửa sổ (Login ↔ Main). */
    public void switchTo(View view) {
        scene.setRoot(load(view).root());
        stage.setTitle(view.title());
    }

    /** Nạp màn hình con vào vùng center của khung chính. */
    public void showIn(BorderPane host, View view) {
        host.setCenter(load(view).root());
    }

    public <C> Loaded<C> load(View view) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view.path()));
        loader.setControllerFactory(context::getBean);          // controller = Spring bean (prototype)
        try {
            Parent root = loader.load();
            return new Loaded<>(root, loader.getController());
        } catch (IOException e) {
            throw new UncheckedIOException("Không nạp được " + view.path(), e);
        }
    }

    public Stage getStage() { return stage; }

    public record Loaded<C>(Parent root, C controller) {}
}
```

### `main.fxml` (rút gọn)

```xml
<BorderPane fx:id="root" xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.fu.cinema.desktop.controller.MainController" styleClass="main">
    <left>
        <VBox styleClass="sidebar" spacing="4">
            <Label text="FU CINEMA" styleClass="brand"/>
            <Button text="Dashboard"        onAction="#onDashboard"   maxWidth="Infinity"/>
            <Button text="Bán vé tại quầy"  onAction="#onCounterSale" maxWidth="Infinity" styleClass="primary"/>
            <Button text="Đơn đặt vé"       onAction="#onBooking"     maxWidth="Infinity"/>
            <Separator/>
            <Button text="Thể loại"          onAction="#onGenre"       maxWidth="Infinity"/>
            <Button text="Phim"              onAction="#onMovie"       maxWidth="Infinity"/>
            <Button text="Phòng & Suất chiếu" onAction="#onSchedule"   maxWidth="Infinity"/>
            <Button text="Báo cáo"           onAction="#onReport"      maxWidth="Infinity"/>
            <Region VBox.vgrow="ALWAYS"/>
            <Button text="Đăng xuất"         onAction="#onLogout"      maxWidth="Infinity"/>
        </VBox>
    </left>
    <bottom>
        <HBox styleClass="status-bar" spacing="16">
            <Label fx:id="lblUser"/>
            <Region HBox.hgrow="ALWAYS"/>
            <Label fx:id="lblClock"/>
        </HBox>
    </bottom>
</BorderPane>
```

### `controller/MainController.java`

```java
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MainController {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    private final SceneManager sceneManager;
    private final DesktopSession session;                      // TODO 36

    @FXML private BorderPane root;
    @FXML private Label lblUser;
    @FXML private Label lblClock;

    private Timeline clock;

    @FXML
    private void initialize() {
        lblUser.textProperty().bind(Bindings.concat("Nhân viên: ", session.displayNameProperty()));
        clock = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblClock.setText(LocalDateTime.now().format(CLOCK))));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        onDashboard();
    }

    @FXML private void onDashboard()   { sceneManager.showIn(root, View.DASHBOARD); }
    @FXML private void onCounterSale() { sceneManager.showIn(root, View.COUNTER_SALE); }
    @FXML private void onBooking()     { sceneManager.showIn(root, View.BOOKING); }
    @FXML private void onGenre()       { sceneManager.showIn(root, View.GENRE); }
    @FXML private void onMovie()       { sceneManager.showIn(root, View.MOVIE); }
    @FXML private void onSchedule()    { sceneManager.showIn(root, View.SCHEDULE); }
    @FXML private void onReport()      { sceneManager.showIn(root, View.REPORT); }

    @FXML
    private void onLogout() {
        clock.stop();
        session.logout();
        sceneManager.switchTo(View.LOGIN);
    }
}
```

### `desktop/css/desktop.css` (khởi điểm)

```css
.root               { -fx-font-family: "Segoe UI"; -fx-font-size: 13px; }
.sidebar            { -fx-background-color: #1f2937; -fx-padding: 12; -fx-pref-width: 200; }
.sidebar .button    { -fx-background-color: transparent; -fx-text-fill: #e5e7eb; -fx-alignment: center-left; }
.sidebar .button:hover { -fx-background-color: #374151; }
.brand              { -fx-text-fill: #e50914; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 12 0; }
.button.primary     { -fx-background-color: #e50914; -fx-text-fill: white; -fx-font-weight: bold; }
.status-bar         { -fx-padding: 4 12; -fx-background-color: #f3f4f6; }
.page               { -fx-padding: 16; -fx-spacing: 12; }
.page-title         { -fx-font-size: 20px; -fx-font-weight: bold; }
.text-field:error, .combo-box:error, .date-picker:error, .spinner:error, .text-area:error {
    -fx-border-color: #d32f2f; -fx-border-width: 1.5;
}
```

**Điểm hay sai:** quên `@Scope("prototype")` → mở lại màn hình dùng chung controller cũ, field `@FXML` trỏ vào node cũ; đường dẫn FXML thiếu `/` đầu.

---

## TODO 35 — Tiện ích dùng chung

### `util/FxAsync.java`

```java
@Component
public class FxAsync implements DisposableBean {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public <T> Task<T> run(Callable<T> work, Consumer<T> onSuccess) {
        return run(work, onSuccess, AlertUtil::showError, null);
    }

    public <T> Task<T> run(Callable<T> work, Consumer<T> onSuccess, Node busy) {
        return run(work, onSuccess, AlertUtil::showError, busy);
    }

    /**
     * @param busy node bị khoá trong lúc chạy (nút, form…), có thể null
     */
    public <T> Task<T> run(Callable<T> work, Consumer<T> onSuccess, Consumer<Throwable> onError, Node busy) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();                                 // background — KHÔNG đụng UI ở đây
            }
        };
        if (busy != null) {
            busy.setDisable(true);
        }
        task.setOnSucceeded(e -> {                                  // FX thread
            if (busy != null) busy.setDisable(false);
            onSuccess.accept(task.getValue());
        });
        task.setOnFailed(e -> {                                     // FX thread
            if (busy != null) busy.setDisable(false);
            onError.accept(task.getException());
        });
        executor.submit(task);
        return task;
    }

    /** Cho thao tác không trả kết quả (xoá, xác nhận…). */
    public Task<Void> runVoid(Runnable work, Runnable onSuccess, Node busy) {
        return run(() -> { work.run(); return null; }, v -> onSuccess.run(), AlertUtil::showError, busy);
    }

    @Override
    public void destroy() {
        executor.shutdownNow();
    }
}
```

> Lưu ý: không dùng `busy.disableProperty().bind(...)` vì node có thể đã bị bind ở chỗ khác (ví dụ nút Bán vé) → `A bound value cannot be set`. Với nút đã bind, truyền `busy = null` và đưa `task.runningProperty()` vào biểu thức binding (xem TODO 43).

### `util/AlertUtil.java`

```java
@Slf4j
public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showError(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException || ex instanceof ExecutionException) && ex.getCause() != null
                ? ex.getCause() : ex;
        Alert.AlertType type = Alert.AlertType.ERROR;
        String message = switch (cause) {                                  // Java 21 pattern matching
            case BusinessException be -> {
                type = Alert.AlertType.WARNING;
                yield be.getMessage();
            }
            case ResourceNotFoundException nf -> nf.getMessage();
            case CannotCreateTransactionException cc -> "Không kết nối được cơ sở dữ liệu. Kiểm tra SQL Server.";
            default -> {
                log.error("Lỗi không mong muốn", cause);                   // stack trace chỉ vào log
                yield "Lỗi hệ thống, vui lòng thử lại.";
            }
        };
        show(type, type == Alert.AlertType.WARNING ? "Không thực hiện được" : "Lỗi", message);
    }

    public static void info(String message)  { show(Alert.AlertType.INFORMATION, "Thông báo", message); }
    public static void warn(String message)  { show(Alert.AlertType.WARNING, "Cảnh báo", message); }

    public static boolean confirm(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type, message, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
```

> Gán biến cục bộ `type` bên trong khối `case` của *switch expression* là hợp lệ (không phải lambda). `case` có `default` bắt buộc vì `Throwable` không phải sealed.

### `util/FxValidator.java`

```java
@Component
@RequiredArgsConstructor
public class FxValidator {

    private static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

    private final Validator validator;                 // jakarta.validation.Validator do Spring Boot cấu hình

    /** @return map tên field → message (rỗng nếu hợp lệ) */
    public Map<String, String> validate(Object form) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<Object> v : validator.validate(form)) {
            errors.putIfAbsent(v.getPropertyPath().toString(), v.getMessage());
        }
        return errors;
    }

    /** Tô đỏ + tooltip cho ô lỗi, xoá lỗi cũ ở ô hợp lệ. @return true nếu không có lỗi */
    public boolean mark(Map<String, String> errors, Map<String, Control> fields) {
        fields.forEach((name, control) -> {
            String msg = errors.get(name);
            control.pseudoClassStateChanged(ERROR, msg != null);
            control.setTooltip(msg == null ? null : new Tooltip(msg));
        });
        return errors.isEmpty();
    }
}
```

### `util/MoneyUtil.java`, `model/PageResult.java`

```java
public final class MoneyUtil {
    private static final NumberFormat VND = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    private MoneyUtil() {}
    public static String format(BigDecimal v) { return v == null ? "" : VND.format(v) + " đ"; }
}
```

```java
public record PageResult<T>(List<T> items, int page, int totalPages, long totalElements) {

    public static <E, T> PageResult<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResult<>(page.getContent().stream().map(mapper).toList(),
                page.getNumber(), Math.max(1, page.getTotalPages()), page.getTotalElements());
    }
}
```

**Tự kiểm tra:** tắt SQL Server rồi bấm một chức năng → hiện "Không kết nối được cơ sở dữ liệu", app không treo, không có stack trace trên Alert.

---

## TODO 36 — Đăng nhập desktop

### `session/DesktopSession.java`

```java
/** Thay cho HttpSession của web. Singleton: 1 người dùng / 1 cửa sổ. */
@Component
public class DesktopSession {

    private volatile SessionUser user;                                   // đọc được từ background thread
    private final ReadOnlyStringWrapper displayName = new ReadOnlyStringWrapper("");

    public void login(SessionUser user) {                                // gọi trên FX thread
        this.user = user;
        displayName.set(user.getDisplayName());
    }

    public void logout() {
        this.user = null;
        displayName.set("");
    }

    public SessionUser getUser() { return user; }

    public ReadOnlyStringProperty displayNameProperty() { return displayName.getReadOnlyProperty(); }

    /** Defense in depth — gọi ở mọi method ghi của Facade (BR-D1). */
    public void requireAdmin() {
        SessionUser u = user;
        if (u == null || !u.isAdmin()) {
            throw new BusinessException("Bạn không có quyền thực hiện thao tác này");
        }
    }
}
```

### `facade/AuthFacade.java`

```java
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;                 // đã kiểm tra BCrypt + BR-03

    public SessionUser loginAdmin(String username, String password) {
        SessionUser user = authService.login(username, password);
        if (!user.isAdmin()) {                                                     // BR-D1
            throw new BusinessException("Ứng dụng quầy vé chỉ dành cho nhân viên (tài khoản ADMIN)");
        }
        return user;
    }
}
```

### `login.fxml` (rút gọn) + `LoginController`

```xml
<VBox fx:id="form" alignment="CENTER" spacing="10" styleClass="login"
      xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.fu.cinema.desktop.controller.LoginController">
    <Label text="FU CINEMA — QUẦY VÉ" styleClass="page-title"/>
    <TextField fx:id="tfUsername" promptText="Email đăng nhập" maxWidth="320"/>
    <PasswordField fx:id="pfPassword" promptText="Mật khẩu" maxWidth="320"/>
    <Button fx:id="btnLogin" text="Đăng nhập" defaultButton="true" onAction="#onLogin"
            styleClass="primary" maxWidth="320"/>
    <ProgressIndicator fx:id="progress" visible="false" prefWidth="28" prefHeight="28"/>
</VBox>
```

```java
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class LoginController {

    private final AuthFacade authFacade;
    private final DesktopSession session;
    private final SceneManager sceneManager;
    private final FxAsync fxAsync;

    @FXML private VBox form;
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Button btnLogin;
    @FXML private ProgressIndicator progress;

    @FXML
    private void initialize() {
        btnLogin.disableProperty().bind(
                tfUsername.textProperty().isEmpty().or(pfPassword.textProperty().isEmpty()));
        Platform.runLater(tfUsername::requestFocus);
    }

    @FXML
    private void onLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();
        progress.setVisible(true);
        fxAsync.run(() -> authFacade.loginAdmin(username, password),
                user -> {
                    progress.setVisible(false);
                    session.login(user);
                    sceneManager.switchTo(View.MAIN);
                },
                ex -> {
                    progress.setVisible(false);
                    pfPassword.clear();
                    AlertUtil.showError(ex);
                },
                form);                                    // khoá cả form khi đang đăng nhập
    }
}
```

**Điểm hay sai:** để `busy = btnLogin` trong khi nút đã `bind` → lỗi *A bound value cannot be set* (vì vậy khoá `form`).
**Tự kiểm tra:** TC-D02, TC-D03, TC-D04, TC-D20.

---

## TODO 37 — Dashboard

### Facade + record

```java
public record RoomCount(String roomName, long count) {}
public record DashboardData(long showingMovies, long customers, long pendingBookings, List<RoomCount> todayByRoom) {}
```

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardFacade {

    private final MovieService movieService;
    private final CustomerService customerService;
    private final BookingService bookingService;
    private final ShowtimeService showtimeService;

    public DashboardData load() {
        List<RoomCount> rooms = showtimeService.countTodayByRoom().stream()          // Q11 native
                .map(r -> new RoomCount(r.getRoomName(), r.getShowtimeCount()))
                .toList();
        return new DashboardData(movieService.countShowing(), customerService.count(),
                bookingService.countPending(), rooms);
    }
}
```

### `DashboardController`

```java
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardFacade facade;
    private final FxAsync fxAsync;

    @FXML private VBox root;
    @FXML private Label lblShowing, lblCustomers, lblPending, lblUpdated;
    @FXML private BarChart<String, Number> chartRooms;
    @FXML private Button btnRefresh;

    private Timeline autoRefresh;

    @FXML
    private void initialize() {
        chartRooms.setAnimated(false);                         // tránh lỗi vẽ khi cập nhật liên tục
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(60), e -> refresh()));
        autoRefresh.setCycleCount(Animation.INDEFINITE);
        autoRefresh.play();
        // Rời màn hình (center bị thay) → node không còn thuộc Scene → dừng Timeline
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) autoRefresh.stop();
        });
        refresh();
    }

    @FXML
    private void refresh() {
        fxAsync.run(facade::load, this::render, btnRefresh);
    }

    private void render(DashboardData d) {
        lblShowing.setText(String.valueOf(d.showingMovies()));
        lblCustomers.setText(String.valueOf(d.customers()));
        lblPending.setText(String.valueOf(d.pendingBookings()));
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Suất chiếu hôm nay");
        d.todayByRoom().forEach(r -> series.getData().add(new XYChart.Data<>(r.roomName(), r.count())));
        chartRooms.getData().setAll(List.of(series));
        lblUpdated.setText("Cập nhật lúc " + LocalTime.now().withNano(0));
    }
}
```

**FXML:** 3 thẻ KPI là `VBox styleClass="kpi"` trong `HBox`; `<BarChart fx:id="chartRooms"><xAxis><CategoryAxis/></xAxis><yAxis><NumberAxis minorTickVisible="false"/></yAxis></BarChart>`.

---

## TODO 38 — Thể loại

### Facade + record

```java
public record GenreRow(Long id, String name, String description) {}
```

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenreFacade {

    private final GenreService genreService;
    private final DesktopSession session;

    public List<GenreRow> findAll() {
        return genreService.findAll().stream()
                .map(g -> new GenreRow(g.getGenreId(), g.getGenreName(), g.getDescription()))
                .toList();
    }

    @Transactional                                         // ⚠ ghi đè readOnly (mục 0.4)
    public void save(GenreForm form) {
        session.requireAdmin();
        if (form.getGenreId() == null) {
            genreService.create(form);
        } else {
            genreService.update(form.getGenreId(), form);
        }
    }

    @Transactional
    public void delete(Long genreId) {
        session.requireAdmin();
        genreService.delete(genreId);                      // BR-05 → BusinessException
    }
}
```

### `GenreController` (ý chính)

```java
@FXML private TableView<GenreRow> table;
@FXML private TableColumn<GenreRow, String> colName, colDesc;
@FXML private TextField tfName;
@FXML private TextArea taDesc;
@FXML private Button btnSave, btnDelete;
private final ObservableList<GenreRow> rows = FXCollections.observableArrayList();
private Long editingId;                                          // null = thêm mới

@FXML
private void initialize() {
    colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
    colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().description()));
    table.setItems(rows);
    table.getSelectionModel().selectedItemProperty().addListener((o, a, row) -> fill(row));
    btnDelete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
    btnSave.disableProperty().bind(tfName.textProperty().isEmpty());
    reload();
}

private void reload() { fxAsync.run(facade::findAll, rows::setAll); }

@FXML
private void onSave() {
    GenreForm form = new GenreForm();
    form.setGenreId(editingId);
    form.setGenreName(tfName.getText().trim());
    form.setDescription(taDesc.getText().trim());
    if (!validator.mark(validator.validate(form), Map.of("genreName", tfName, "description", taDesc))) return;
    fxAsync.runVoid(() -> facade.save(form), () -> { AlertUtil.info("Đã lưu thể loại"); onNew(); reload(); }, formBox);
}

@FXML
private void onDelete() {
    GenreRow row = table.getSelectionModel().getSelectedItem();
    if (AlertUtil.confirm("Xoá thể loại \"" + row.name() + "\"?")) {
        fxAsync.runVoid(() -> facade.delete(row.id()), () -> { AlertUtil.info("Đã xoá"); reload(); }, null);
    }
}
```

> `formBox` là `VBox` chứa form (`@FXML private VBox formBox;`). **Không** truyền `btnSave` vào `runVoid` vì nút đó đã `bind` → lỗi *A bound value cannot be set*.

---

## TODO 39 — Danh sách phim

### Record + Facade

```java
public record MovieRow(Long id, String title, String genreName, int duration,
                       LocalDate releaseDate, AgeRating ageRating, MovieStatus status) {
    public static MovieRow from(Movie m) {
        return new MovieRow(m.getMovieId(), m.getTitle(), m.getGenre().getGenreName(),   // LAZY → cần transaction
                m.getDuration(), m.getReleaseDate(), m.getAgeRating(), m.getStatus());
    }
}
public record GenreItem(Long id, String name) {
    @Override public String toString() { return name; }           // ComboBox hiển thị bằng toString()
}
```

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieFacade {

    public static final int PAGE_SIZE = 10;

    private final MovieService movieService;
    private final GenreService genreService;
    private final DesktopSession session;

    public PageResult<MovieRow> search(String keyword, Long genreId, MovieStatus status, String sort, int page) {
        return PageResult.of(movieService.searchForAdmin(keyword, genreId, status, page, PAGE_SIZE, sort),
                MovieRow::from);
    }

    public List<GenreItem> genres() {
        return genreService.findAll().stream().map(g -> new GenreItem(g.getGenreId(), g.getGenreName())).toList();
    }

    public MovieForm getForm(Long movieId) { return movieService.getForm(movieId); }

    @Transactional
    public void save(MovieForm form) {
        session.requireAdmin();
        if (form.getMovieId() == null) movieService.create(form);
        else movieService.update(form.getMovieId(), form);
    }

    /** @return true = đã xoá hẳn, false = chuyển STOPPED (BR-06) */
    @Transactional
    public boolean delete(Long movieId) {
        session.requireAdmin();
        return movieService.delete(movieId);
    }
}
```

### `MovieController` (ý chính)

```java
private static final PseudoClass STOPPED = PseudoClass.getPseudoClass("stopped");
private final ObservableList<MovieRow> rows = FXCollections.observableArrayList();
private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

@FXML
private void initialize() {
    colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
    colGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().genreName()));
    colDuration.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().duration()));
    colDuration.setCellFactory(col -> new TableCell<>() {
        @Override protected void updateItem(Integer v, boolean empty) {
            super.updateItem(v, empty);
            setText(empty || v == null ? null : v + " phút");
        }
    });
    colRelease.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().releaseDate()));
    colRelease.setCellFactory(col -> new TableCell<>() {
        @Override protected void updateItem(LocalDate v, boolean empty) {
            super.updateItem(v, empty);
            setText(empty || v == null ? null : v.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    });
    table.setRowFactory(tv -> {
        TableRow<MovieRow> row = new TableRow<>() {
            @Override protected void updateItem(MovieRow m, boolean empty) {
                super.updateItem(m, empty);
                pseudoClassStateChanged(STOPPED, !empty && m != null && m.status() == MovieStatus.STOPPED);
            }
        };
        row.setOnMouseClicked(e -> { if (e.getClickCount() == 2 && !row.isEmpty()) openDialog(row.getItem().id()); });
        return row;
    });
    table.setItems(rows);

    cbStatus.getItems().setAll(Arrays.asList((MovieStatus) null));   // null = "Tất cả"
    cbStatus.getItems().addAll(MovieStatus.values());
    cbSort.getItems().setAll("releaseDate", "title");
    cbSort.setValue("releaseDate");

    debounce.setOnFinished(e -> search());
    tfSearch.textProperty().addListener((o, a, b) -> debounce.playFromStart());
    cbGenre.valueProperty().addListener((o, a, b) -> search());
    cbStatus.valueProperty().addListener((o, a, b) -> search());
    cbSort.valueProperty().addListener((o, a, b) -> search());
    pagination.currentPageIndexProperty().addListener((o, a, b) -> load(b.intValue()));

    fxAsync.run(facade::genres, g -> { cbGenre.getItems().setAll(g); load(0); });
}

/** Đổi bộ lọc → về trang 1. */
private void search() {
    if (pagination.getCurrentPageIndex() != 0) pagination.setCurrentPageIndex(0);   // listener sẽ load(0)
    else load(0);
}

private void load(int page) {
    String kw = tfSearch.getText();
    Long genreId = cbGenre.getValue() == null ? null : cbGenre.getValue().id();
    fxAsync.run(() -> facade.search(kw, genreId, cbStatus.getValue(), cbSort.getValue(), page), result -> {
        rows.setAll(result.items());
        pagination.setPageCount(result.totalPages());
        lblCount.setText(result.totalElements() + " phim");
    });
}
```

```css
.table-row-cell:stopped            { -fx-background-color: #eeeeee; }
.table-row-cell:stopped .table-cell { -fx-text-fill: #9e9e9e; }
```

> Hiển thị "Tất cả" cho giá trị `null` của ComboBox: dùng `setButtonCell`/`setCellFactory` trả `"Tất cả"` khi item null. `Pagination` ở đây chỉ dùng làm thanh chuyển trang (`setPageFactory(i -> new Region())` hoặc đặt dưới TableView với `maxPageIndicatorCount="7"`).

---

## TODO 40 — Dialog thêm/sửa phim

### `movie-dialog.fxml` (root là `GridPane fx:id="root"`)

Các ô: `tfTitle`, `tfDirector`, `spDuration` (`Spinner` 30–300, editable), `dpRelease`, `cbAgeRating`, `cbGenre`, `cbStatus`, `tfPoster`, `taDescription`.

### `MovieDialogController`

```java
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MovieDialogController {

    private final FxValidator validator;

    @FXML private GridPane root;
    @FXML private TextField tfTitle, tfDirector, tfPoster;
    @FXML private Spinner<Integer> spDuration;
    @FXML private DatePicker dpRelease;
    @FXML private ComboBox<AgeRating> cbAgeRating;
    @FXML private ComboBox<GenreItem> cbGenre;
    @FXML private ComboBox<MovieStatus> cbStatus;
    @FXML private TextArea taDescription;

    private Long movieId;

    @FXML
    private void initialize() {
        spDuration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 120));
        spDuration.setEditable(true);
        // Spinner editable không tự commit giá trị gõ tay khi mất focus
        spDuration.focusedProperty().addListener((o, was, is) -> { if (!is) commitSpinner(); });
        cbAgeRating.getItems().setAll(AgeRating.values());
        cbStatus.getItems().setAll(MovieStatus.values());
    }

    public Optional<MovieForm> open(Window owner, MovieForm form, List<GenreItem> genres) {
        cbGenre.getItems().setAll(genres);
        fill(form);

        Dialog<MovieForm> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(form.getMovieId() == null ? "Thêm phim" : "Sửa phim");
        dialog.getDialogPane().setContent(root);
        ButtonType save = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        // Chặn đóng dialog khi dữ liệu không hợp lệ (event filter — Javafx_exercises 1.5)
        dialog.getDialogPane().lookupButton(save).addEventFilter(ActionEvent.ACTION, e -> {
            if (!validate()) e.consume();
        });
        dialog.setResultConverter(bt -> bt == save ? toForm() : null);
        return dialog.showAndWait();
    }

    private boolean validate() {
        commitSpinner();
        return validator.mark(validator.validate(toForm()), Map.of(
                "title", tfTitle, "director", tfDirector, "duration", spDuration,
                "releaseDate", dpRelease, "ageRating", cbAgeRating, "genreId", cbGenre,
                "status", cbStatus, "posterUrl", tfPoster, "description", taDescription));
    }

    private MovieForm toForm() {
        MovieForm f = new MovieForm();
        f.setMovieId(movieId);
        f.setTitle(tfTitle.getText().trim());
        f.setDirector(tfDirector.getText().trim());
        f.setDuration(spDuration.getValue());
        f.setReleaseDate(dpRelease.getValue());
        f.setAgeRating(cbAgeRating.getValue());
        f.setGenreId(cbGenre.getValue() == null ? null : cbGenre.getValue().id());
        f.setStatus(cbStatus.getValue());
        f.setPosterUrl(tfPoster.getText().trim());
        f.setDescription(taDescription.getText().trim());
        return f;
    }

    private void fill(MovieForm f) { /* set từng control từ form; cbGenre chọn item có id == f.getGenreId() */ }

    private void commitSpinner() {
        try {
            spDuration.getValueFactory().setValue(Integer.parseInt(spDuration.getEditor().getText().trim()));
        } catch (NumberFormatException ignored) {
            spDuration.getEditor().setText(String.valueOf(spDuration.getValue()));
        }
    }
}
```

> Spinner đặt min = 1 (không phải 30) để **Bean Validation** của `MovieForm` (`@Min(30)`, `@Max(300)`) là nơi báo lỗi → đạt TC-D09.

### Trong `MovieController`

```java
@FXML
private void onAdd() { openDialog(null); }

private void openDialog(Long movieId) {
    Window owner = table.getScene().getWindow();
    fxAsync.run(() -> new Object[]{ facade.genres(), movieId == null ? new MovieForm() : facade.getForm(movieId) },
        data -> {
            @SuppressWarnings("unchecked") List<GenreItem> genres = (List<GenreItem>) data[0];
            SceneManager.Loaded<MovieDialogController> dlg = sceneManager.load(View.MOVIE_DIALOG);
            dlg.controller().open(owner, (MovieForm) data[1], genres).ifPresent(form ->
                fxAsync.runVoid(() -> facade.save(form), () -> { AlertUtil.info("Đã lưu phim"); load(pagination.getCurrentPageIndex()); }, null));
        });
}

@FXML
private void onDelete() {
    MovieRow m = table.getSelectionModel().getSelectedItem();
    if (m == null || !AlertUtil.confirm("Xoá phim \"" + m.title() + "\"?")) return;
    fxAsync.run(() -> facade.delete(m.id()), deleted -> {
        AlertUtil.info(deleted ? "Đã xoá phim" : "Phim đã có suất chiếu → đã chuyển sang STOPPED");   // BR-06
        load(pagination.getCurrentPageIndex());
    });
}
```

> Gọn hơn `Object[]`: tạo record `MovieDialogData(List<GenreItem> genres, MovieForm form)` trong Facade.

---

## TODO 41 — Phòng & suất chiếu

### Record

```java
public record RoomRow(Long id, String name, RoomType type, int rows, int seatsPerRow, RoomStatus status) {
    public int capacity() { return rows * seatsPerRow; }
}
public record RoomItem(Long id, String name) { @Override public String toString() { return name; } }
public record MovieItem(Long id, String title, int duration, AgeRating ageRating) {
    @Override public String toString() { return title + " (" + duration + "')"; }
}
public record ShowtimeRow(Long id, String movieTitle, String roomName, LocalDateTime start,
                          LocalDateTime end, BigDecimal price, ShowtimeStatus status) {}
```

### `ShowtimeFacade`

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShowtimeFacade {

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final DesktopSession session;

    public PageResult<ShowtimeRow> search(Long movieId, Long roomId, LocalDate date, int page) {
        return PageResult.of(showtimeService.search(movieId, roomId, date, page, 10), s -> new ShowtimeRow(
                s.getShowtimeId(), s.getMovie().getTitle(), s.getRoom().getRoomName(),
                s.getStartTime(), s.getEndTime(), s.getTicketPrice(), s.getStatus()));
    }

    public List<MovieItem> schedulableMovies() {                       // không có phim STOPPED (BR-07)
        return movieService.findSchedulable().stream()
                .map(m -> new MovieItem(m.getMovieId(), m.getTitle(), m.getDuration(), m.getAgeRating())).toList();
    }

    public List<RoomItem> activeRooms() {                              // không có phòng MAINTENANCE
        return roomService.findActive().stream().map(r -> new RoomItem(r.getRoomId(), r.getRoomName())).toList();
    }

    public ShowtimeForm getForm(Long id) { return showtimeService.getForm(id); }

    @Transactional
    public void save(ShowtimeForm form) {
        session.requireAdmin();
        if (form.getShowtimeId() == null) showtimeService.create(form);       // BR-07, BR-08
        else showtimeService.update(form.getShowtimeId(), form);
    }

    /** @return true = xoá hẳn, false = huỷ suất + huỷ toàn bộ đơn (BR-09) */
    @Transactional
    public boolean delete(Long id) {
        session.requireAdmin();
        return showtimeService.delete(id);
    }
}
```

`RoomFacade` làm tương tự với `RoomService` (`findAll`, `getForm`, `create/update` qua `RoomForm`, `delete` trả boolean → "đã xoá" / "đã chuyển MAINTENANCE").

### Form suất chiếu — gợi ý EndTime bằng binding

```java
@FXML private ComboBox<MovieItem> cbMovie;
@FXML private ComboBox<RoomItem> cbRoom;
@FXML private DatePicker dpDate;
@FXML private Spinner<Integer> spHour, spMinute;
@FXML private TextField tfPrice;
@FXML private Label lblEnd;

@FXML
private void initialize() {
    spHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 19));
    spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 55, 0, 5));
    tfPrice.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,9}") ? c : null));
    dpDate.setDayCellFactory(p -> new DateCell() {                     // không cho chọn ngày quá khứ
        @Override public void updateItem(LocalDate d, boolean empty) {
            super.updateItem(d, empty);
            setDisable(empty || d.isBefore(LocalDate.now()));
        }
    });
    lblEnd.textProperty().bind(Bindings.createStringBinding(() -> {
        LocalDateTime end = suggestedEnd();
        return end == null ? "—" : "Kết thúc (gợi ý): " + end.format(DateTimeFormatter.ofPattern("HH:mm dd/MM"));
    }, cbMovie.valueProperty(), dpDate.valueProperty(), spHour.valueProperty(), spMinute.valueProperty()));
}

private LocalDateTime startTime() {
    return dpDate.getValue() == null ? null : LocalDateTime.of(dpDate.getValue(), LocalTime.of(spHour.getValue(), spMinute.getValue()));
}

private LocalDateTime suggestedEnd() {                                  // Start + Duration + 15' dọn phòng
    LocalDateTime start = startTime();
    return start == null || cbMovie.getValue() == null ? null : start.plusMinutes(cbMovie.getValue().duration() + 15L);
}

@FXML
private void onSaveShowtime() {
    ShowtimeForm f = new ShowtimeForm();
    f.setShowtimeId(editingId);
    f.setMovieId(cbMovie.getValue() == null ? null : cbMovie.getValue().id());
    f.setRoomId(cbRoom.getValue() == null ? null : cbRoom.getValue().id());
    f.setStartTime(startTime());
    f.setEndTime(suggestedEnd());               // hoặc null → service tự tính
    f.setTicketPrice(tfPrice.getText().isBlank() ? null : new BigDecimal(tfPrice.getText()));
    if (!validator.mark(validator.validate(f), Map.of("movieId", cbMovie, "roomId", cbRoom,
            "startTime", dpDate, "ticketPrice", tfPrice))) return;
    fxAsync.runVoid(() -> facade.save(f), () -> { AlertUtil.info("Đã lưu suất chiếu"); reload(); }, formBox);
}
```

> Thông báo "trùng với suất …" (BR-08) do `ShowtimeServiceImpl` ném `BusinessException` → `AlertUtil` hiện nguyên văn. Nếu message của service chưa ghi rõ suất trùng, sửa ở service (issue `module: showtime`, người sở hữu review) — đừng tự kiểm tra lại trong desktop.

---

## TODO 42 — SeatMapView

### Record dữ liệu

```java
public record SeatMapData(Long showtimeId, String movieTitle, String roomName, LocalDateTime startTime,
                          BigDecimal price, List<String> rowLabels, int seatsPerRow, Set<String> soldSeats) {}
```

Facade (đặt trong `CounterSaleFacade`, TODO 43):

```java
public SeatMapData seatMap(Long showtimeId) {
    SeatMap m = showtimeService.getSeatMap(showtimeId);          // BR-10: suất không OPEN/đã bắt đầu → BusinessException
    Showtime s = m.getShowtime();
    return new SeatMapData(showtimeId, s.getMovie().getTitle(), s.getRoom().getRoomName(), s.getStartTime(),
            s.getTicketPrice(), List.copyOf(m.getRowLabels()), m.getSeatsPerRow(), Set.copyOf(m.getSoldSeats()));
}
```

### `component/SeatMapView.java`

```java
public class SeatMapView extends VBox {

    public static final int MAX_SEATS = 8;                                       // BR-10
    private static final PseudoClass SOLD = PseudoClass.getPseudoClass("sold");

    private final GridPane grid = new GridPane();
    private final ObservableList<String> selected = FXCollections.observableArrayList();
    private final ObservableList<String> selectedView = FXCollections.unmodifiableObservableList(selected);

    public SeatMapView() {                                   // constructor rỗng → dùng được trong FXML
        getStyleClass().add("seat-map");
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        Label screen = new Label("MÀN HÌNH");
        screen.getStyleClass().add("screen");
        screen.setMaxWidth(Double.MAX_VALUE);
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER);
        getChildren().addAll(screen, grid, legend());
    }

    /** Danh sách ghế đang chọn — màn hình khác chỉ được ĐỌC/bind. */
    public ObservableList<String> getSelectedSeats() { return selectedView; }

    public void setData(SeatMapData data) {
        selected.clear();
        grid.getChildren().clear();
        if (data == null) return;
        for (int r = 0; r < data.rowLabels().size(); r++) {
            String row = data.rowLabels().get(r);
            Label rowLabel = new Label(row);
            rowLabel.getStyleClass().add("row-label");
            grid.add(rowLabel, 0, r);
            for (int c = 1; c <= data.seatsPerRow(); c++) {
                String code = row + c;                                          // A1, B12…
                ToggleButton seat = new ToggleButton(String.valueOf(c));
                seat.getStyleClass().add("seat");
                seat.setTooltip(new Tooltip(code));
                boolean sold = data.soldSeats().contains(code);
                seat.pseudoClassStateChanged(SOLD, sold);
                seat.setDisable(sold);
                seat.selectedProperty().addListener((obs, was, is) -> onToggle(seat, code, is));
                grid.add(seat, c, r);
            }
        }
    }

    private void onToggle(ToggleButton seat, String code, boolean isSelected) {
        if (!isSelected) {
            selected.remove(code);
            return;
        }
        if (selected.size() >= MAX_SEATS) {
            seat.setSelected(false);                                            // listener chạy lại với false → remove (không có) → OK
            Platform.runLater(() -> AlertUtil.warn("Mỗi đơn tối đa " + MAX_SEATS + " ghế"));
            return;
        }
        selected.add(code);
    }

    private HBox legend() {
        HBox box = new HBox(16, legendItem("Trống", "legend-free"),
                legendItem("Đang chọn", "legend-selected"), legendItem("Đã bán", "legend-sold"));
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Label legendItem(String text, String styleClass) {
        Region swatch = new Region();
        swatch.getStyleClass().addAll("legend-swatch", styleClass);
        Label l = new Label(text, swatch);
        return l;
    }
}
```

```css
.screen          { -fx-background-color: #9ca3af; -fx-text-fill: white; -fx-alignment: center; -fx-padding: 4; }
.seat            { -fx-min-width: 34; -fx-min-height: 30; -fx-background-color: #e5e7eb; -fx-background-radius: 6 6 2 2; }
.seat:hover      { -fx-background-color: #d1d5db; }
.seat:selected   { -fx-background-color: #16a34a; -fx-text-fill: white; }
.seat:sold       { -fx-background-color: #6b7280; -fx-text-fill: white; -fx-opacity: 1; }   /* ghi đè mờ 0.4 mặc định của :disabled */
.legend-swatch   { -fx-min-width: 16; -fx-min-height: 16; -fx-background-radius: 3; }
.legend-free     { -fx-background-color: #e5e7eb; }
.legend-selected { -fx-background-color: #16a34a; }
.legend-sold     { -fx-background-color: #6b7280; }
```

Dùng trong FXML: `<?import com.fu.cinema.desktop.component.SeatMapView?>` … `<ScrollPane fitToWidth="true"><SeatMapView fx:id="seatMap"/></ScrollPane>`.

> `SeatMapView` **không** phải Spring bean (FXMLLoader tạo bằng constructor rỗng) → không inject gì vào nó; dữ liệu đưa vào bằng `setData`.

---

## TODO 43 — Bán vé tại quầy

### Record

```java
public record CustomerItem(Long id, String fullName, String mobile, int age) {
    public static CustomerItem from(Customer c) {
        return new CustomerItem(c.getCustomerId(), c.getFullName(), c.getMobile(),
                Period.between(c.getBirthday(), LocalDate.now()).getYears());
    }
    @Override public String toString() { return fullName + " – " + mobile + " – " + age + " tuổi"; }
}
public record ShowtimeItem(Long id, LocalDateTime start, String roomName, BigDecimal price) {
    @Override public String toString() {
        return start.format(DateTimeFormatter.ofPattern("HH:mm EEE dd/MM", Locale.of("vi", "VN"))) + " · " + roomName;
    }
}
public record SaleResult(Long bookingId, List<String> seats, BigDecimal total, BookingStatus status) {}
```

### `CounterSaleFacade`

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CounterSaleFacade {

    private final CustomerService customerService;
    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;
    private final DesktopSession session;

    public List<CustomerItem> searchCustomers(String keyword) {
        return customerService.search(keyword, 0, 20).map(CustomerItem::from).getContent();
    }

    public List<MovieItem> movies() {
        return movieService.findSchedulable().stream()
                .map(m -> new MovieItem(m.getMovieId(), m.getTitle(), m.getDuration(), m.getAgeRating())).toList();
    }

    public List<ShowtimeItem> showtimes(Long movieId) {                   // chỉ suất OPEN sắp tới
        return showtimeService.findUpcomingByMovie(movieId).stream()
                .map(s -> new ShowtimeItem(s.getShowtimeId(), s.getStartTime(), s.getRoom().getRoomName(), s.getTicketPrice()))
                .toList();
    }

    // public SeatMapData seatMap(Long showtimeId) { ... }   → code ở TODO 42

    /** BR-10, BR-11, BR-12 kiểm tra trong BookingService.create (khoá PESSIMISTIC_WRITE suất chiếu). */
    @Transactional                                                          // ⚠ KHÔNG readOnly — mục 0.4
    public SaleResult sell(Long customerId, Long showtimeId, List<String> seats, boolean paid) {
        session.requireAdmin();
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(showtimeId);
        request.setSeatCodes(new ArrayList<>(seats));
        Booking booking = bookingService.create(customerId, request);       // PENDING
        if (paid) {
            bookingService.confirm(booking.getBookingId());                 // BR-D2 → CONFIRMED (cùng transaction)
        }
        return new SaleResult(booking.getBookingId(), seats.stream().sorted(SEAT_ORDER).toList(),
                booking.getTotalAmount(), booking.getStatus());
    }

    /** A2 < A10 < B1 */
    public static final Comparator<String> SEAT_ORDER = Comparator
            .comparing((String s) -> s.charAt(0))
            .thenComparingInt(s -> Integer.parseInt(s.substring(1)));
}
```

> `confirm` gọi `getDetail` → cùng persistence context nên `booking.getStatus()` trả về `CONFIRMED` sau khi confirm.
> Nếu `create` ném `BusinessException`, transaction ngoài rollback toàn bộ → không có đơn "nửa vời".

### `counter-sale.fxml` — bố cục gợi ý

```
BorderPane
├─ left (320px): VBox
│    TextField tfCustomer (prompt "Tìm khách: tên / email / SĐT") + ListView<CustomerItem> lvCustomers
│    ComboBox<MovieItem> cbMovie · ComboBox<ShowtimeItem> cbShowtime
│    Label lblSeats · Label lblTotal (styleClass "total") · CheckBox chkPaid "Đã thanh toán"
│    Button btnSell "Bán vé" (primary)
└─ center: ScrollPane → SeatMapView seatMap
```

### `CounterSaleController`

```java
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CounterSaleController {

    private final CounterSaleFacade facade;
    private final FxAsync fxAsync;

    @FXML private TextField tfCustomer;
    @FXML private ListView<CustomerItem> lvCustomers;
    @FXML private ComboBox<MovieItem> cbMovie;
    @FXML private ComboBox<ShowtimeItem> cbShowtime;
    @FXML private SeatMapView seatMap;
    @FXML private Label lblSeats, lblTotal;
    @FXML private CheckBox chkPaid;
    @FXML private Button btnSell;

    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    @FXML
    private void initialize() {
        // (1) Tìm khách
        debounce.setOnFinished(e -> fxAsync.run(() -> facade.searchCustomers(tfCustomer.getText()), lvCustomers.getItems()::setAll));
        tfCustomer.textProperty().addListener((o, a, b) -> debounce.playFromStart());

        // (2) Chuỗi phụ thuộc phim → suất → ghế
        cbMovie.valueProperty().addListener((o, a, movie) -> {
            cbShowtime.getItems().clear();
            clearSeatMap();
            if (movie != null) fxAsync.run(() -> facade.showtimes(movie.id()), cbShowtime.getItems()::setAll);
        });
        cbShowtime.valueProperty().addListener((o, a, st) -> {
            clearSeatMap();
            if (st != null) reloadSeatMap();
        });

        // (3) Tổng tiền = giá × số ghế (binding)
        ObjectBinding<BigDecimal> total = Bindings.createObjectBinding(
                () -> price.get() == null ? BigDecimal.ZERO
                        : price.get().multiply(BigDecimal.valueOf(seatMap.getSelectedSeats().size())),
                price, seatMap.getSelectedSeats());
        lblTotal.textProperty().bind(Bindings.createStringBinding(() -> "Tổng: " + MoneyUtil.format(total.get()), total));
        lblSeats.textProperty().bind(Bindings.createStringBinding(
                () -> "Ghế: " + String.join(", ", seatMap.getSelectedSeats().stream().sorted(CounterSaleFacade.SEAT_ORDER).toList()),
                seatMap.getSelectedSeats()));

        // (4) Nút bán chỉ bật khi đủ điều kiện và không đang xử lý
        btnSell.disableProperty().bind(lvCustomers.getSelectionModel().selectedItemProperty().isNull()
                .or(cbShowtime.valueProperty().isNull())
                .or(Bindings.isEmpty(seatMap.getSelectedSeats()))
                .or(busy));

        fxAsync.run(facade::movies, cbMovie.getItems()::setAll);
        fxAsync.run(() -> facade.searchCustomers(""), lvCustomers.getItems()::setAll);
    }

    @FXML
    private void onSell() {
        CustomerItem customer = lvCustomers.getSelectionModel().getSelectedItem();
        ShowtimeItem showtime = cbShowtime.getValue();
        List<String> seats = List.copyOf(seatMap.getSelectedSeats());
        boolean paid = chkPaid.isSelected();
        if (!AlertUtil.confirm("Bán %d vé (%s) cho %s?".formatted(seats.size(), String.join(", ", seats), customer.fullName()))) {
            return;
        }
        busy.set(true);
        fxAsync.run(() -> facade.sell(customer.id(), showtime.id(), seats, paid),
                result -> {
                    busy.set(false);
                    AlertUtil.info("Đơn #%d · Ghế %s · %s · %s".formatted(result.bookingId(),
                            String.join(", ", result.seats()), MoneyUtil.format(result.total()), result.status()));
                    reloadSeatMap();
                },
                ex -> {
                    busy.set(false);
                    AlertUtil.showError(ex);          // "Ghế đã có người đặt: C5" / BR-11 ...
                    reloadSeatMap();                  // ghế vừa bị người khác mua hiện thành "đã bán"
                },
                null);
    }

    private void reloadSeatMap() {
        ShowtimeItem st = cbShowtime.getValue();
        if (st == null) return;
        fxAsync.run(() -> facade.seatMap(st.id()), data -> {
            price.set(data.price());
            seatMap.setData(data);
        });
    }

    private void clearSeatMap() {
        price.set(null);
        seatMap.setData(null);
    }
}
```

**Tự kiểm tra TC-D16 (tranh chấp):** mở web (đăng nhập `an.nv`) và desktop cùng suất; chọn cùng ghế; bấm đặt ở web trước rồi bán ở desktop → desktop hiện "Ghế đã có người đặt: …" và sơ đồ cập nhật ghế đó thành đã bán. Kiểm tra DB:
```sql
SELECT showtime_id, seat_code, COUNT(*) FROM tickets t JOIN bookings b ON b.booking_id = t.booking_id
WHERE b.status <> 'CANCELLED' GROUP BY showtime_id, seat_code HAVING COUNT(*) > 1;   -- phải rỗng
```

---

## TODO 44 — Quản lý đơn

### Record + Facade

```java
public record BookingRow(Long id, String customerName, String movieTitle, String roomName,
                         LocalDateTime startTime, int seatCount, BigDecimal total,
                         BookingStatus status, LocalDateTime bookingDate) {
    public static BookingRow from(Booking b) {
        Showtime s = b.getShowtime();
        return new BookingRow(b.getBookingId(), b.getCustomer().getFullName(), s.getMovie().getTitle(),
                s.getRoom().getRoomName(), s.getStartTime(), b.getTickets().size(), b.getTotalAmount(),
                b.getStatus(), b.getBookingDate());
    }
}
public record BookingDetail(Long id, String customerName, String mobile, String movieTitle, String roomName,
                            LocalDateTime startTime, List<String> seats, BigDecimal total,
                            BookingStatus status, LocalDateTime bookingDate) {}
```

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingFacade {

    private final BookingService bookingService;
    private final DesktopSession session;

    public PageResult<BookingRow> search(BookingStatus status, LocalDate from, LocalDate to, String keyword, int page) {
        return PageResult.of(bookingService.search(status, from, to, keyword, page, 15), BookingRow::from);
    }

    public BookingDetail detail(Long id) {
        Booking b = bookingService.getDetail(id);                     // Q5 JOIN FETCH
        Showtime s = b.getShowtime();
        return new BookingDetail(b.getBookingId(), b.getCustomer().getFullName(), b.getCustomer().getMobile(),
                s.getMovie().getTitle(), s.getRoom().getRoomName(), s.getStartTime(),
                b.getTickets().stream().map(Ticket::getSeatCode).toList(),
                b.getTotalAmount(), b.getStatus(), b.getBookingDate());
    }

    @Transactional
    public void confirm(Long id) { session.requireAdmin(); bookingService.confirm(id); }        // BR-13

    @Transactional
    public void cancel(Long id)  { session.requireAdmin(); bookingService.cancelByAdmin(id); }  // BR-13
}
```

> `b.getTickets().size()` trong danh sách gây thêm 1 query/dòng (N+1) — chấp nhận được với 15 dòng/trang; muốn tối ưu thì dùng `totalAmount / ticketPrice`.

### `BookingAdminController` — nút bật/tắt theo trạng thái

```java
ReadOnlyObjectProperty<BookingRow> sel = table.getSelectionModel().selectedItemProperty();
btnConfirm.disableProperty().bind(Bindings.createBooleanBinding(
        () -> sel.get() == null || sel.get().status() != BookingStatus.PENDING, sel));
btnCancel.disableProperty().bind(Bindings.createBooleanBinding(
        () -> sel.get() == null || sel.get().status() == BookingStatus.CANCELLED, sel));

colStatus.setCellFactory(col -> new TableCell<>() {
    @Override protected void updateItem(BookingStatus st, boolean empty) {
        super.updateItem(st, empty);
        getStyleClass().removeAll("st-pending", "st-confirmed", "st-cancelled");
        setText(empty || st == null ? null : st.name());
        if (!empty && st != null) getStyleClass().add("st-" + st.name().toLowerCase());
    }
});

@FXML
private void onConfirm() {
    BookingRow r = sel.get();
    if (AlertUtil.confirm("Xác nhận đơn #" + r.id() + "?")) {
        fxAsync.runVoid(() -> facade.confirm(r.id()), () -> load(pagination.getCurrentPageIndex()), null);
    }
}
```

- Lọc: `cbStatus` (có "Tất cả" = null), `dpFrom`, `dpTo`, `tfKeyword` → giống TODO 39 (`search()` về trang 1).
- Chi tiết: double-click dòng → `fxAsync.run(() -> facade.detail(id), this::showDetail)`; `showDetail` dựng `Dialog<Void>` với `GridPane` nhãn/giá trị và `ListView<String>` ghế.

```css
.st-pending   { -fx-text-fill: #b45309; -fx-font-weight: bold; }
.st-confirmed { -fx-text-fill: #15803d; -fx-font-weight: bold; }
.st-cancelled { -fx-text-fill: #b91c1c; }
```

---

## TODO 45 — Báo cáo doanh thu & CSV

### Record + Facade

```java
public record RevenueRow(String title, long tickets, BigDecimal revenue) {}
public record ReportBookingRow(Long id, LocalDateTime bookingDate, String customerName, String movieTitle, BigDecimal total) {}
public record ReportData(LocalDate start, LocalDate end, List<RevenueRow> revenue, List<ReportBookingRow> bookings,
                         long totalBookings, long totalTickets, BigDecimal totalRevenue) {
    public boolean isEmpty() { return bookings.isEmpty(); }
}
```

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportFacade {

    private final ReportService reportService;

    public ReportData generate(LocalDate start, LocalDate end) {
        ReportResult r = reportService.generate(start, end);                      // BR-15 (Start > End → BusinessException)
        return new ReportData(start, end,
                r.getRevenueByMovie().stream()
                        .map(m -> new RevenueRow(m.getTitle(), m.getTicketCount(), m.getRevenue())).toList(),
                r.getBookings().stream()
                        .map(b -> new ReportBookingRow(b.getBookingId(), b.getBookingDate(),
                                b.getCustomer().getFullName(), b.getShowtime().getMovie().getTitle(), b.getTotalAmount()))
                        .toList(),
                r.getTotalBookings(), r.getTotalTickets(), r.getTotalRevenue());
    }
}
```

### `ReportController` (ý chính)

```java
private final ObjectProperty<ReportData> data = new SimpleObjectProperty<>();

@FXML
private void initialize() {
    dpStart.setValue(LocalDate.now().withDayOfMonth(1));
    dpEnd.setValue(LocalDate.now());
    dpEnd.setDayCellFactory(p -> new DateCell() {                          // chặn End < Start
        @Override public void updateItem(LocalDate d, boolean empty) {
            super.updateItem(d, empty);
            setDisable(empty || (dpStart.getValue() != null && d.isBefore(dpStart.getValue())));
        }
    });
    chartRevenue.setAnimated(false);
    pieTickets.setAnimated(false);
    btnExport.disableProperty().bind(Bindings.createBooleanBinding(
            () -> data.get() == null || data.get().isEmpty(), data));
    // cột bảng: lambda như TODO 39
}

@FXML
private void onGenerate() {
    fxAsync.run(() -> facade.generate(dpStart.getValue(), dpEnd.getValue()), this::render, btnGenerate);
}

private void render(ReportData d) {
    data.set(d);
    lblEmpty.setVisible(d.isEmpty());                                       // "Không có dữ liệu"
    bookingRows.setAll(d.bookings());
    lblTotals.setText("Tổng: %d đơn · %d vé · %s".formatted(d.totalBookings(), d.totalTickets(), MoneyUtil.format(d.totalRevenue())));

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Doanh thu");
    d.revenue().forEach(r -> series.getData().add(new XYChart.Data<>(r.title(), r.revenue())));
    chartRevenue.getData().setAll(List.of(series));
    // Node của từng cột chỉ có SAU khi series đã nằm trong chart
    for (int i = 0; i < series.getData().size(); i++) {
        RevenueRow r = d.revenue().get(i);
        Tooltip.install(series.getData().get(i).getNode(),
                new Tooltip("%s: %d vé – %s".formatted(r.title(), r.tickets(), MoneyUtil.format(r.revenue()))));
    }

    pieTickets.getData().setAll(d.revenue().stream()
            .map(r -> new PieChart.Data(r.title() + " (" + r.tickets() + ")", r.tickets())).toList());
}

@FXML
private void onExport() {
    FileChooser fc = new FileChooser();
    fc.setInitialFileName("doanh-thu_%s_%s.csv".formatted(data.get().start(), data.get().end()));
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
    File file = fc.showSaveDialog(btnExport.getScene().getWindow());
    if (file == null) return;                                               // Cancel → không làm gì
    ReportData snapshot = data.get();
    fxAsync.runVoid(() -> CsvExporter.write(file.toPath(), snapshot),
            () -> AlertUtil.info("Đã xuất " + file.getName()), btnGenerate);
}
```

### `util/CsvExporter.java`

```java
public final class CsvExporter {

    private CsvExporter() {
    }

    public static void write(Path path, ReportData d) {
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write('﻿');                                                 // BOM → Excel đọc đúng tiếng Việt
            w.write("Báo cáo doanh thu," + d.start() + "," + d.end());
            w.newLine();
            w.write("Phim,Số vé,Doanh thu");
            w.newLine();
            for (RevenueRow r : d.revenue()) {
                w.write(String.join(",", csv(r.title()), String.valueOf(r.tickets()), r.revenue().toPlainString()));
                w.newLine();
            }
            w.newLine();
            w.write("Mã đơn,Ngày đặt,Khách hàng,Phim,Tổng tiền");
            w.newLine();
            for (ReportBookingRow b : d.bookings()) {
                w.write(String.join(",", b.id().toString(), b.bookingDate().toString(), csv(b.customerName()),
                        csv(b.movieTitle()), b.total().toPlainString()));
                w.newLine();
            }
            w.write("TỔNG,%d đơn,%d vé,,%s".formatted(d.totalBookings(), d.totalTickets(), d.totalRevenue().toPlainString()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String csv(String v) {
        if (v == null) return "";
        return v.contains(",") || v.contains("\"") || v.contains("\n") ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }
}
```

---

## TODO 46 — README, test, release

### Test Facade (`src/test/java/com/fu/cinema/desktop/DesktopFacadeTest.java`)

Dùng đúng cấu hình test có sẵn của Assignment 01 (`@SpringBootTest` + profile `test` → H2, `DataSeeder` tự seed).

```java
@SpringBootTest
@ActiveProfiles("test")
class DesktopFacadeTest {

    @Autowired private AuthFacade authFacade;
    @Autowired private MovieFacade movieFacade;
    @Autowired private BookingFacade bookingFacade;
    @Autowired private GenreFacade genreFacade;
    @Autowired private DesktopSession session;

    @Test
    void customerAccount_cannotLoginDesktop() {                                   // BR-D1
        assertThrows(BusinessException.class, () -> authFacade.loginAdmin("an.nv@gmail.com", "Customer@123"));
    }

    @Test
    void adminAccount_canLogin() {
        assertTrue(authFacade.loginAdmin("admin@fucinema.vn", "Admin@123").isAdmin());
    }

    @Test
    void movieRows_areMappedInsideTransaction() {                                // không LazyInitializationException
        PageResult<MovieRow> page = movieFacade.search(null, null, null, "title", 0);
        assertFalse(page.items().isEmpty());
        assertTrue(page.items().stream().allMatch(m -> m.genreName() != null));
    }

    @Test
    void bookingRows_haveCustomerAndMovie() {
        PageResult<BookingRow> page = bookingFacade.search(null, null, null, null, 0);
        assertTrue(page.items().stream().allMatch(b -> b.customerName() != null && b.movieTitle() != null));
    }

    @Test
    void writeOperation_requiresAdminSession() {
        session.logout();
        assertThrows(BusinessException.class, () -> genreFacade.delete(1L));
    }
}
```

> Context test nạp cả bean desktop (`SceneManager`, `FxAsync`, facade…) — các bean này **không** khởi động JavaFX toolkit trong constructor nên chạy được trên CI/H2. Controller là prototype nên không được tạo. Nếu một bean desktop đụng tới `Stage`/`Scene` trong constructor → test lỗi `Toolkit not initialized` → chuyển phần đó vào method.

### README — mục Desktop (khung)

```markdown
## Ứng dụng Desktop (Quầy vé)
### Yêu cầu: JDK 21, SQL Server (đã cấu hình như phần Web), Windows/macOS/Linux có giao diện
### Chạy
- IntelliJ: Run `com.fu.cinema.desktop.DesktopLauncher`
- Maven: `mvn spring-boot:run -Dspring-boot.run.main-class=com.fu.cinema.desktop.DesktopLauncher`
- Chạy song song web: `mvn spring-boot:run` ở terminal khác
### Tài khoản: admin@fucinema.vn / Admin@123 (chỉ ADMIN dùng được desktop)
### Ảnh chụp: login · dashboard · phim · suất chiếu · bán vé · báo cáo
### Kết quả kiểm thử TC-D01 → TC-D20
### Phân công: bảng A/B/C/D + link PR
```

### Release

```bash
git switch develop && git pull
git switch -c release/v2.0.0
# cập nhật <version>2.0.0</version> trong pom.xml, README
git commit -am "chore(release): prepare v2.0.0"
# PR release/v2.0.0 → main (2 approvals), merge
git switch main && git pull
git tag -a v2.0.0 -m "FU Cinema Desktop"
git push origin v2.0.0
# GitHub → Releases → Draft a new release → tag v2.0.0 → mô tả + ảnh chụp
# back-merge main → develop
```

---

## Bonus — gợi ý nhanh

| Mã | Gợi ý |
|---|---|
| B1 — In vé | Dựng `VBox` vé (logo, phim, phòng, ghế, giờ, mã đơn) → `PrinterJob job = PrinterJob.createPrinterJob(); if (job != null && job.showPrintDialog(owner)) { if (job.printPage(node)) job.endJob(); }` — chạy trên FX thread. |
| B2 — Kiosk | `AuthFacade.loginCustomer` cho phép CUSTOMER; `MainController` ẩn menu Admin bằng `visible/managed` bind theo role; dùng lại `SeatMapView`; gọi `BookingService.create(session.getUser().getCustomerId(), …)`, `findByCustomer`, `cancelByCustomer` (BR-14). Mọi Facade ghi Admin vẫn `requireAdmin()`. |
| B3 — Tự làm mới ghế | `Timeline` 10 s trong `CounterSaleController` gọi `reloadSeatMap()` **nhưng giữ ghế đang chọn** nếu chưa bị bán (so khớp lại sau `setData`); dừng Timeline khi rời màn hình như TODO 37. |
| B4 — Multi-module | `cinema-core` (entity, repository, service, dto, exception, config), `cinema-web` (controller, templates, `FuCinemaTicketSystemApplication`), `cinema-desktop` (package `desktop`, main `DesktopLauncher` + `@SpringBootApplication(scanBasePackages = "com.fu.cinema")` riêng). Tham khảo cấu trúc Chapter 09 (data-module/ui-module). |
| B5 — Theme | 2 file `light.css`, `dark.css`; `scene.getStylesheets().setAll(...)` khi bấm nút; lưu lựa chọn bằng `java.util.prefs.Preferences`. |
| B6 — jpackage | `mvn package` → `jpackage --input target --main-jar fu-cinema-ticket-system-2.0.0.jar --main-class org.springframework.boot.loader.launch.PropertiesLauncher --java-options "-Dloader.main=com.fu.cinema.desktop.DesktopLauncher" --name FUCinemaDesktop --type msi` (Windows cần WiX Toolset). |

---

## Bảng lỗi thường gặp

| Triệu chứng | Nguyên nhân | Cách sửa |
|---|---|---|
| `JavaFX runtime components are missing` | Chạy `CinemaFxApp` trực tiếp | Chạy `DesktopLauncher` |
| `Port 8080 already in use` / log có Tomcat | Thiếu `.web(WebApplicationType.NONE)` | Thêm vào builder |
| `ClassCastException ... cannot be cast to ...Controller` | DevTools restart classloader | `spring.devtools.restart.enabled=false` (đặt bằng `System.setProperty` trước khi launch) |
| `LazyInitializationException` khi đổ bảng | Map entity ngoài transaction / trả entity lên UI | Map trong Facade `@Transactional(readOnly = true)` |
| Bấm Lưu báo thành công nhưng DB không đổi | Method ghi của Facade kế thừa `readOnly = true` của class | Gắn `@Transactional` lên method ghi (mục 0.4) |
| `Not on FX application thread` | Đụng UI trong `Task.call()` | Chỉ cập nhật UI trong callback của `FxAsync` |
| `A bound value cannot be set` | `setDisable` trên nút đã `bind` | Đưa trạng thái bận vào biểu thức binding (`busy` property) |
| `LoadException: Location is not set` | Sai đường dẫn | `/desktop/fxml/xxx.fxml` |
| `NullPointerException` ở field `@FXML` | Sai `fx:id` hoặc dùng field trong constructor | Kiểm tra `fx:id`; code khởi tạo trong `initialize()` |
| Form hợp lệ trên web nhưng desktop lưu dữ liệu sai | Desktop không gọi Bean Validation | `FxValidator.validate(form)` trước khi gọi Facade |
| Spinner không nhận số gõ tay | Spinner editable chưa commit | `commitSpinner()` (TODO 40) |
| Tooltip BarChart không hiện | Gắn tooltip trước khi series vào chart | Cài tooltip **sau** `chart.getData().setAll(...)` |
| Dashboard vẫn gọi DB sau khi chuyển màn hình | `Timeline` không dừng | Dừng khi `root.getScene() == null` |
| Test `Toolkit not initialized` | Bean desktop tạo `Stage`/`Scene` trong constructor | Dời vào method được gọi sau khi FX đã chạy |
