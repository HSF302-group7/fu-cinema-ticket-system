package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.navigation.SceneManager;
import com.fu.cinema.desktop.navigation.View;
import com.fu.cinema.desktop.session.DesktopSession;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    private final SceneManager sceneManager;
    private final DesktopSession session;

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
}
