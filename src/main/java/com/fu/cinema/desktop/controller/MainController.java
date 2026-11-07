package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.ai.AiChatService;
import com.fu.cinema.desktop.ai.ChatMessage;
import com.fu.cinema.desktop.ai.ShowtimeAction;
import com.fu.cinema.desktop.navigation.SceneManager;
import com.fu.cinema.desktop.navigation.View;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.FxAsync;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final FxAsync fxAsync;

    @FXML private BorderPane root;
    @FXML private Label lblUser;
    @FXML private Label lblClock;

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
        lblUser.textProperty().bind(Bindings.concat("Nhân viên: ", session.displayNameProperty()));
        clock = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblClock.setText(LocalDateTime.now().format(CLOCK))));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        onDashboard();
    }

    private void navigate(View view) {
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
                btnBook.setOnAction(e -> onBookShowtime(action));
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

    private void onBookShowtime(ShowtimeAction action) {
        // Thu nhỏ cửa sổ chat
        onCloseAiChat();

        // Chuyển sang màn hình Bán vé quầy
        CounterSaleController counterSale = sceneManager.showIn(root, View.COUNTER_SALE);
        if (counterSale != null) {
            counterSale.selectShowtime(action.showtimeId(), action.movieTitle());
        }

        AlertUtil.info("AI đã mở màn hình Bán vé cho phim \"" + action.movieTitle() + "\" (Suất " + action.time() + ").\nQuý khách vui lòng chọn khách hàng và ghế trên sơ đồ.");
    }

    private void scrollToBottom() {
        Platform.runLater(() -> spChatScroll.setVvalue(1.0));
    }
}
