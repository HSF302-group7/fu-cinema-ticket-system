package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.ai.AiChatService;
import com.fu.cinema.desktop.ai.ChatMessage;
import com.fu.cinema.desktop.ai.ShowtimeAction;
import com.fu.cinema.desktop.facade.CounterSaleFacade;
import com.fu.cinema.desktop.model.SeatMapData;
import com.fu.cinema.desktop.navigation.SceneManager;
import com.fu.cinema.desktop.navigation.View;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.FxAsync;
import com.fu.cinema.desktop.util.MoneyUtil;
import com.fu.cinema.dto.SessionUser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    private final SceneManager sceneManager;
    private final DesktopSession session;
    private final AiChatService aiChatService;
    private final CounterSaleFacade counterSaleFacade;
    private final FxAsync fxAsync;

    @FXML private BorderPane root;
    @FXML private Label lblUser;
    @FXML private Label lblRoleBadge;
    @FXML private Label lblClock;

    // --- Sidebar Menu Buttons ---
    @FXML private Button btnDashboard;
    @FXML private Button btnCounterSale;
    @FXML private Button btnBooking;
    @FXML private Separator sepAdmin;
    @FXML private Button btnGenre;
    @FXML private Button btnMovie;
    @FXML private Button btnSchedule;
    @FXML private Button btnReport;

    // --- AI Chat Overlay Controls ---
    @FXML private VBox aiChatWindow;
    @FXML private Button btnAiFab;
    @FXML private VBox chatMessagesBox;
    @FXML private ScrollPane spChatScroll;
    @FXML private HBox aiLoadingIndicator;
    @FXML private TextField tfAiInput;

    private Timeline clock;
    private final List<ChatMessage> conversationHistory = new ArrayList<>();
    private boolean welcomeSent = false;

    @FXML
    private void initialize() {
        clock = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblClock.setText(LocalDateTime.now().format(CLOCK))));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        SessionUser u = session.getUser();
        boolean isCustomer = u != null && u.isCustomer();

        if (isCustomer) {
            // CUSTOMER: Hiển thị Chatbot AI và tối ưu giao diện đặt vé xem phim
            btnAiFab.setVisible(true);
            btnAiFab.setManaged(true);

            String displayName = u.getDisplayName() != null ? u.getDisplayName() : "Khách hàng";
            lblUser.setText("Khách hàng: " + displayName);
            if (lblRoleBadge != null) {
                lblRoleBadge.setText("CUSTOMER");
                lblRoleBadge.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
            }

            if (btnCounterSale != null) btnCounterSale.setText("🎟️ Đặt vé xem phim");
            if (btnBooking != null) btnBooking.setText("📋 Vé của tôi");

            // Ẩn các tính năng quản lý nội bộ của rạp
            if (btnGenre != null) { btnGenre.setVisible(false); btnGenre.setManaged(false); }
            if (btnSchedule != null) { btnSchedule.setVisible(false); btnSchedule.setManaged(false); }
            if (btnReport != null) { btnReport.setVisible(false); btnReport.setManaged(false); }
            if (sepAdmin != null) { sepAdmin.setVisible(false); sepAdmin.setManaged(false); }

            onMovie();
        } else {
            // ADMIN: ẨN HOÀN TOÀN AI Chatbot theo yêu cầu
            btnAiFab.setVisible(false);
            btnAiFab.setManaged(false);
            aiChatWindow.setVisible(false);
            aiChatWindow.setManaged(false);

            String displayName = u != null && u.getDisplayName() != null ? u.getDisplayName() : "Quản trị viên";
            lblUser.setText("Nhân viên: " + displayName);
            if (lblRoleBadge != null) {
                lblRoleBadge.setText("ADMIN");
                lblRoleBadge.setStyle("-fx-background-color: #e11d48; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");
            }

            if (btnCounterSale != null) btnCounterSale.setText("🎟️ Bán vé tại quầy");
            if (btnBooking != null) btnBooking.setText("📑 Quản lý đơn vé");

            if (btnGenre != null) { btnGenre.setVisible(true); btnGenre.setManaged(true); }
            if (btnSchedule != null) { btnSchedule.setVisible(true); btnSchedule.setManaged(true); }
            if (btnReport != null) { btnReport.setVisible(true); btnReport.setManaged(true); }
            if (sepAdmin != null) { sepAdmin.setVisible(true); sepAdmin.setManaged(true); }

            onDashboard();
        }
    }

    private void navigate(View view) {
        Button activeBtn = switch (view) {
            case DASHBOARD -> btnDashboard;
            case COUNTER_SALE -> btnCounterSale;
            case BOOKING -> btnBooking;
            case MOVIE -> btnMovie;
            case GENRE -> btnGenre;
            case SCHEDULE -> btnSchedule;
            case REPORT -> btnReport;
            default -> null;
        };
        updateActiveButton(activeBtn);

        try {
            sceneManager.showIn(root, view);
        } catch (Exception e) {
            log.info("Màn hình {} chưa có FXML, hiển thị placeholder", view.title());
            StackPane placeholder = new StackPane(
                    new Label("Màn hình " + view.title() + " đang được phát triển...")
            );
            placeholder.getStyleClass().add("page");
            root.setCenter(placeholder);
        }
    }

    private void updateActiveButton(Button activeButton) {
        List<Button> buttons = List.of(btnDashboard, btnCounterSale, btnBooking, btnMovie, btnGenre, btnSchedule, btnReport);
        for (Button btn : buttons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-nav-btn-highlight");
            }
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("sidebar-nav-btn-highlight")) {
            activeButton.getStyleClass().add("sidebar-nav-btn-highlight");
        }
    }

    @FXML private void onDashboard()   { navigate(View.DASHBOARD); }
    @FXML private void onCounterSale() { navigate(View.COUNTER_SALE); }
    @FXML private void onBooking()     { navigate(View.BOOKING); }
    @FXML private void onGenre()       { navigate(View.GENRE); }
    @FXML private void onMovie()       { navigate(View.MOVIE); }
    @FXML private void onSchedule()    { navigate(View.SCHEDULE); }
    @FXML private void onReport()      { navigate(View.REPORT); }

    @FXML
    private void onLogout() {
        if (clock != null) {
            clock.stop();
        }
        session.logout();
        try {
            sceneManager.switchTo(View.LOGIN);
        } catch (Exception e) {
            StackPane logoutPane = new StackPane(new Label("Đã đăng xuất. Vui lòng đóng ứng dụng."));
            logoutPane.getStyleClass().add("page");
            root.setCenter(logoutPane);
        }
    }

    // =========================================================================
    //  AI CHAT CONCIERGE & FLOATING ACTION BUTTON (PART R BONUS)
    // =========================================================================

    @FXML
    private void onToggleAiChat() {
        boolean isVisible = aiChatWindow.isVisible();
        aiChatWindow.setVisible(!isVisible);
        aiChatWindow.setManaged(!isVisible);

        if (!isVisible) {
            if (!welcomeSent) {
                welcomeSent = true;
                appendBotBubble(ChatMessage.bot(
                        "Xin chào quý khách! 👋 Tôi là Trợ lý ảo AI của rạp FU Cinema.\n\n" +
                        "Tôi có thể hỗ trợ bạn:\n" +
                        "• Tra cứu phim đang chiếu và lịch chiếu\n" +
                        "• Gợi ý suất chiếu phù hợp với thời gian rảnh\n" +
                        "• Giải đáp quy định độ tuổi (P, K, T13, T16, T18), giá vé và nội quy rạp\n\n" +
                        "Bạn cần tìm phim hoặc suất chiếu nào hôm nay ạ?",
                        List.of()
                ));
            }
            Platform.runLater(tfAiInput::requestFocus);
        }
    }

    @FXML
    private void onCloseAiChat() {
        aiChatWindow.setVisible(false);
        aiChatWindow.setManaged(false);
    }

    @FXML
    private void onClearAiChat() {
        chatMessagesBox.getChildren().clear();
        conversationHistory.clear();
        welcomeSent = false;
        onToggleAiChat(); // Đóng lại
    }

    @FXML
    private void onSendAiMessage() {
        String text = tfAiInput.getText() != null ? tfAiInput.getText().trim() : "";
        if (text.isBlank()) {
            return;
        }

        tfAiInput.clear();
        appendUserBubble(text);

        aiLoadingIndicator.setVisible(true);
        aiLoadingIndicator.setManaged(true);
        scrollToBottom();

        // Chạy bất đồng bộ qua FxAsync (không đơ giao diện)
        fxAsync.run(() -> aiChatService.sendMessage(text, List.copyOf(conversationHistory)),
                botMessage -> {
                    aiLoadingIndicator.setVisible(false);
                    aiLoadingIndicator.setManaged(false);
                    conversationHistory.add(ChatMessage.user(text));
                    conversationHistory.add(botMessage);
                    appendBotBubble(botMessage);
                },
                error -> {
                    aiLoadingIndicator.setVisible(false);
                    aiLoadingIndicator.setManaged(false);
                    String errText = error.getMessage() != null && error.getMessage().toLowerCase().contains("timed out")
                            ? "Mất quá nhiều thời gian kết nối tới máy chủ AI (mạng chập chờn). Quý khách vui lòng thử bấm gửi lại nhé!"
                            : error.getMessage();
                    appendBotBubble(ChatMessage.bot("⚠️ Đã xảy ra sự cố: " + errText, List.of()));
                },
                null);
    }

    private void appendUserBubble(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.getStyleClass().add("ai-chat-bubble-user-text");

        VBox bubble = new VBox(lbl);
        bubble.getStyleClass().add("ai-chat-bubble-user");

        HBox container = new HBox(bubble);
        container.setAlignment(Pos.CENTER_RIGHT);

        chatMessagesBox.getChildren().add(container);
        scrollToBottom();
    }

    private void appendBotBubble(ChatMessage message) {
        VBox contentBox = new VBox(8);

        Label lbl = new Label(message.text());
        lbl.setWrapText(true);
        lbl.getStyleClass().add("ai-chat-bubble-bot-text");
        contentBox.getChildren().add(lbl);

        // Nếu AI gợi ý các suất chiếu, tạo action buttons trực quan
        if (message.actions() != null && !message.actions().isEmpty()) {
            VBox actionsBox = new VBox(6);
            for (ShowtimeAction action : message.actions()) {
                Button btnBook = new Button("🎬 Đặt vé: " + action.movieTitle() + " (" + action.time() + " · " + action.roomName() + ")");
                btnBook.getStyleClass().add("ai-action-btn");
                btnBook.setMaxWidth(Double.MAX_VALUE);
                btnBook.setOnAction(e -> onSelectShowtimeInChat(action));
                actionsBox.getChildren().add(btnBook);
            }
            contentBox.getChildren().add(actionsBox);
        }

        VBox bubble = new VBox(contentBox);
        bubble.getStyleClass().add("ai-chat-bubble-bot");

        HBox container = new HBox(bubble);
        container.setAlignment(Pos.CENTER_LEFT);

        chatMessagesBox.getChildren().add(container);
        scrollToBottom();
    }

    /**
     * BƯỚC 1: Khi khách bấm vào 1 suất chiếu trong chat -> AI hỏi số lượng vé và hiển thị các thẻ chip 1 vé, 2 vé, 3 vé...
     */
    private void onSelectShowtimeInChat(ShowtimeAction action) {
        appendUserBubble("Tôi muốn đặt vé: " + action.movieTitle() + " (" + action.time() + " · " + action.roomName() + ")");

        VBox contentBox = new VBox(10);
        Label lbl = new Label("Dạ, quý khách đã chọn suất chiếu phim \"" + action.movieTitle() + "\" lúc " + action.time() + " (" + action.roomName() + ").\n\nQuý khách muốn đặt bao nhiêu vé ạ?");
        lbl.setWrapText(true);
        lbl.getStyleClass().add("ai-chat-bubble-bot-text");
        contentBox.getChildren().add(lbl);

        // Hiển thị các thẻ chọn số lượng vé (dạng chip / pills 1 vé, 2 vé, 3 vé, 4 vé...)
        FlowPane chipsBox = new FlowPane();
        chipsBox.setHgap(8);
        chipsBox.setVgap(8);
        chipsBox.getStyleClass().add("ai-chip-box");

        for (int count = 1; count <= 6; count++) {
            final int ticketCount = count;
            Button btnChip = new Button("🎫 " + count + " vé");
            btnChip.getStyleClass().add("ai-chip-btn");
            btnChip.setOnAction(e -> onSelectTicketCountInChat(action, ticketCount));
            chipsBox.getChildren().add(btnChip);
        }
        contentBox.getChildren().add(chipsBox);

        VBox bubble = new VBox(contentBox);
        bubble.getStyleClass().add("ai-chat-bubble-bot");

        HBox container = new HBox(bubble);
        container.setAlignment(Pos.CENTER_LEFT);
        chatMessagesBox.getChildren().add(container);
        scrollToBottom();
    }

    /**
     * BƯỚC 2: Khi khách chọn số vé -> AI tra cứu ghế trống và hỏi khách chọn chỗ ngồi mong muốn.
     */
    private void onSelectTicketCountInChat(ShowtimeAction action, int ticketCount) {
        appendUserBubble("Tôi muốn đặt " + ticketCount + " vé");

        fxAsync.run(() -> counterSaleFacade.seatMap(action.showtimeId()),
                seatMapData -> {
                    List<SeatOption> options = findSeatOptions(seatMapData, ticketCount);
                    if (options.isEmpty()) {
                        appendBotTextOnly("⚠️ Rất tiếc, suất chiếu này hiện không còn đủ " + ticketCount + " ghế trống. Quý khách vui lòng chọn lại số lượng vé hoặc suất chiếu khác nhé!");
                        return;
                    }

                    VBox contentBox = new VBox(10);
                    Label lbl = new Label("Dạ, quý khách muốn đặt " + ticketCount + " vé. Dưới đây là các vị trí ghế trống đẹp nhất trong phòng " + action.roomName() + ", quý khách muốn chọn chỗ ngồi nào ạ?");
                    lbl.setWrapText(true);
                    lbl.getStyleClass().add("ai-chat-bubble-bot-text");
                    contentBox.getChildren().add(lbl);

                    VBox seatOptionsBox = new VBox(6);
                    for (SeatOption opt : options) {
                        Button btnSeat = new Button("💺 " + opt.label());
                        btnSeat.getStyleClass().add("ai-seat-option-btn");
                        btnSeat.setMaxWidth(Double.MAX_VALUE);
                        btnSeat.setOnAction(e -> onSelectSeatsInChat(action, ticketCount, opt.seats(), opt.label(), seatMapData));
                        seatOptionsBox.getChildren().add(btnSeat);
                    }

                    Button btnOpenMap = new Button("🗺️ Hoặc tự chọn ghế trên sơ đồ phòng");
                    btnOpenMap.getStyleClass().add("ai-secondary-btn");
                    btnOpenMap.setMaxWidth(Double.MAX_VALUE);
                    btnOpenMap.setOnAction(e -> onOpenSeatMap(action));
                    seatOptionsBox.getChildren().add(btnOpenMap);

                    contentBox.getChildren().add(seatOptionsBox);

                    VBox bubble = new VBox(contentBox);
                    bubble.getStyleClass().add("ai-chat-bubble-bot");

                    HBox container = new HBox(bubble);
                    container.setAlignment(Pos.CENTER_LEFT);
                    chatMessagesBox.getChildren().add(container);
                    scrollToBottom();
                },
                ex -> appendBotTextOnly("⚠️ Không thể tải sơ đồ ghế: " + ex.getMessage()),
                null);
    }

    /**
     * BƯỚC 3: Khi khách chọn chỗ ngồi -> AI hiển thị Card xác nhận thông tin đơn vé (trạng thái PENDING để Admin duyệt).
     */
    private void onSelectSeatsInChat(ShowtimeAction action, int ticketCount, List<String> selectedSeats, String seatDesc, SeatMapData seatMapData) {
        appendUserBubble("Tôi chọn: " + seatDesc);

        BigDecimal unitPrice = seatMapData.price() != null ? seatMapData.price() : BigDecimal.valueOf(80000);
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(ticketCount));
        String seatsText = String.join(", ", selectedSeats);

        VBox card = new VBox(8);
        card.getStyleClass().add("ai-confirm-card");

        Label lblCardTitle = new Label("📋 XÁC NHẬN THÔNG TIN ĐẶT VÉ");
        lblCardTitle.setStyle("-fx-font-weight: 800; -fx-text-fill: #1e1b4b; -fx-font-size: 13px;");

        Label lblDetails = new Label(
                "• Phim: " + action.movieTitle() + "\n" +
                "• Suất chiếu: " + action.time() + " · " + action.roomName() + "\n" +
                "• Số lượng vé: " + ticketCount + " vé\n" +
                "• Vị trí ghế đã chọn: " + seatsText + "\n" +
                "• Đơn giá: " + MoneyUtil.format(unitPrice) + "/vé\n" +
                "• TỔNG TIỀN: " + MoneyUtil.format(totalAmount) + "\n" +
                "• Trạng thái sau khi đặt: PENDING (Chờ Admin duyệt) ⏳"
        );
        lblDetails.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #334155; -fx-line-spacing: 2;");

        Label lblAsk = new Label("Quý khách có muốn đặt vé với thông tin như trên không?");
        lblAsk.setStyle("-fx-font-weight: 600; -fx-text-fill: #0f172a; -fx-font-size: 12px;");

        Button btnConfirm = new Button("✅ Xác nhận đặt vé (Chờ Admin duyệt)");
        btnConfirm.getStyleClass().add("ai-confirm-btn");
        btnConfirm.setMaxWidth(Double.MAX_VALUE);
        btnConfirm.setOnAction(e -> {
            btnConfirm.setDisable(true);
            executeBookingInChat(action, selectedSeats, totalAmount);
        });

        Button btnChangeSeats = new Button("🔄 Đổi chọn chỗ ngồi khác");
        btnChangeSeats.getStyleClass().add("ai-secondary-btn");
        btnChangeSeats.setMaxWidth(Double.MAX_VALUE);
        btnChangeSeats.setOnAction(e -> onSelectTicketCountInChat(action, ticketCount));

        card.getChildren().addAll(lblCardTitle, new Separator(), lblDetails, lblAsk, btnConfirm, btnChangeSeats);

        VBox bubble = new VBox(card);
        bubble.getStyleClass().add("ai-chat-bubble-bot");

        HBox container = new HBox(bubble);
        container.setAlignment(Pos.CENTER_LEFT);
        chatMessagesBox.getChildren().add(container);
        scrollToBottom();
    }

    /**
     * BƯỚC 4: Người dùng bấm xác nhận -> Gọi Facade tạo vé (paid=false để trạng thái PENDING cho Admin duyệt)!
     */
    private void executeBookingInChat(ShowtimeAction action, List<String> seats, BigDecimal total) {
        SessionUser user = session.getUser();
        Long customerId = (user != null && user.getCustomerId() != null) ? user.getCustomerId() : 1L;

        // Lưu ý: paid = false để đơn vé ở trạng thái PENDING chờ Admin duyệt (BR-D2)
        fxAsync.run(() -> counterSaleFacade.sell(customerId, action.showtimeId(), seats, false),
                result -> {
                    VBox successBox = new VBox(8);
                    successBox.getStyleClass().add("ai-success-card");

                    Label lblTitle = new Label("🎉 ĐẶT VÉ THÀNH CÔNG! (CHỜ DUYỆT)");
                    lblTitle.setStyle("-fx-font-weight: 900; -fx-text-fill: #15803d; -fx-font-size: 13.5px;");

                    Label lblSuccess = new Label(
                            "• Mã đơn vé: #" + result.bookingId() + "\n" +
                            "• Phim: " + action.movieTitle() + "\n" +
                            "• Suất chiếu: " + action.time() + " · " + action.roomName() + "\n" +
                            "• Ghế đã đặt: " + String.join(", ", result.seats()) + "\n" +
                            "• Trạng thái: PENDING (Chờ Admin duyệt) ⏳\n" +
                            "• Tổng tiền: " + MoneyUtil.format(result.total()) + "\n\n" +
                            "Đơn vé của quý khách đã được lưu trên hệ thống và đang ở trạng thái PENDING. Quản trị viên/Nhân viên rạp sẽ sớm duyệt đơn vé cho bạn nhé! 🍿🎬"
                    );
                    lblSuccess.setStyle("-fx-font-size: 12px; -fx-text-fill: #14532d; -fx-line-spacing: 2;");

                    Button btnViewMyBookings = new Button("📋 Xem trong Vé của tôi");
                    btnViewMyBookings.getStyleClass().add("btn-secondary");
                    btnViewMyBookings.setMaxWidth(Double.MAX_VALUE);
                    btnViewMyBookings.setOnAction(e -> {
                        onCloseAiChat();
                        onBooking();
                    });

                    successBox.getChildren().addAll(lblTitle, new Separator(), lblSuccess, btnViewMyBookings);

                    VBox bubble = new VBox(successBox);
                    bubble.getStyleClass().add("ai-chat-bubble-bot");

                    HBox container = new HBox(bubble);
                    container.setAlignment(Pos.CENTER_LEFT);
                    chatMessagesBox.getChildren().add(container);
                    scrollToBottom();
                },
                ex -> appendBotTextOnly("⚠️ Đặt vé không thành công: " + ex.getMessage()),
                null);
    }

    private void onOpenSeatMap(ShowtimeAction action) {
        onCloseAiChat();
        onCounterSale();
        CounterSaleController counterSale = sceneManager.showIn(root, View.COUNTER_SALE);
        if (counterSale != null) {
            counterSale.selectShowtime(action.showtimeId(), action.movieTitle());
        }
        AlertUtil.info("AI đã mở sơ đồ phòng chiếu \"" + action.roomName() + "\".\nQuý khách vui lòng chọn các ghế ưng ý và bấm 'Xác nhận đặt vé' nhé!");
    }

    private void appendBotTextOnly(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.getStyleClass().add("ai-chat-bubble-bot-text");
        VBox bubble = new VBox(lbl);
        bubble.getStyleClass().add("ai-chat-bubble-bot");
        HBox container = new HBox(bubble);
        container.setAlignment(Pos.CENTER_LEFT);
        chatMessagesBox.getChildren().add(container);
        scrollToBottom();
    }

    public record SeatOption(String label, List<String> seats) {}

    private List<SeatOption> findSeatOptions(SeatMapData data, int ticketCount) {
        if (data == null || data.rowLabels() == null) {
            return List.of();
        }
        List<SeatOption> options = new ArrayList<>();
        List<String> rows = new ArrayList<>(data.rowLabels());

        // Duyệt qua từng hàng ghế để tìm phương án dãy ghế liền kề
        for (String row : rows) {
            for (int col = 1; col <= data.seatsPerRow() - ticketCount + 1; col++) {
                boolean allFree = true;
                List<String> candidate = new ArrayList<>();
                for (int i = 0; i < ticketCount; i++) {
                    String seatCode = row + (col + i);
                    if (data.soldSeats().contains(seatCode)) {
                        allFree = false;
                        break;
                    }
                    candidate.add(seatCode);
                }
                if (allFree) {
                    String desc = "";
                    if (row.equalsIgnoreCase("D") || row.equalsIgnoreCase("E")) {
                        desc = " (Trung tâm ⭐)";
                    } else if (row.equalsIgnoreCase("C")) {
                        desc = " (Hàng giữa view đẹp)";
                    } else if (row.equalsIgnoreCase("A") || row.equalsIgnoreCase("B")) {
                        desc = " (Gần màn hình)";
                    } else {
                        desc = " (Hàng sau)";
                    }
                    String label = "Hàng " + row + ": " + String.join(", ", candidate) + desc;
                    options.add(new SeatOption(label, candidate));
                    break; // Mỗi hàng lấy 1 phương án đẹp nhất để đa dạng các hàng
                }
            }
            if (options.size() >= 4) {
                break; // Tối đa 4 phương án hàng ghế
            }
        }

        // Nếu không có dãy ghế liền kề nào, gom các ghế trống bất kỳ
        if (options.isEmpty()) {
            List<String> fallback = new ArrayList<>();
            for (String row : rows) {
                for (int col = 1; col <= data.seatsPerRow(); col++) {
                    String seatCode = row + col;
                    if (!data.soldSeats().contains(seatCode)) {
                        fallback.add(seatCode);
                        if (fallback.size() == ticketCount) {
                            options.add(new SeatOption("Ghế trống: " + String.join(", ", fallback), fallback));
                            return options;
                        }
                    }
                }
            }
        }
        return options;
    }

    private void scrollToBottom() {
        Platform.runLater(() -> spChatScroll.setVvalue(1.0));
    }
}
