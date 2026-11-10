package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.component.SeatMapView;
import com.fu.cinema.desktop.facade.CounterSaleFacade;
import com.fu.cinema.desktop.model.CustomerItem;
import com.fu.cinema.desktop.model.MovieItem;
import com.fu.cinema.desktop.model.ShowtimeItem;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.FxAsync;
import com.fu.cinema.desktop.util.MoneyUtil;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CounterSaleController {

    private final CounterSaleFacade facade;
    private final FxAsync fxAsync;
    private final DesktopSession session;

    @FXML private Label lblTitle;
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
        SessionUser user = session.getUser();
        boolean isCustomer = user != null && user.isCustomer();

        // (2) Chuỗi phụ thuộc phim -> suất -> ghế
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

        if (isCustomer) {
            if (lblTitle != null) {
                lblTitle.setText("ĐẶT VÉ XEM PHIM");
            }
            btnSell.setText("Xác nhận đặt vé");
            chkPaid.setSelected(false);
            chkPaid.setVisible(false);
            chkPaid.setManaged(false);

            tfCustomer.setText(user.getDisplayName() != null ? user.getDisplayName() : "Khách hàng");
            tfCustomer.setEditable(false);
            tfCustomer.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-font-weight: bold;");

            lvCustomers.setVisible(false);
            lvCustomers.setManaged(false);

            // Nút đặt vé đối với Khách hàng: KHÔNG phụ thuộc lvCustomers!
            // Chỉ cần: đã chọn suất chiếu + đã chọn ít nhất 1 ghế + không bận xử lý
            btnSell.disableProperty().bind(cbShowtime.valueProperty().isNull()
                    .or(Bindings.isEmpty(seatMap.getSelectedSeats()))
                    .or(busy));
        } else {
            // ADMIN: Luồng bán vé tại quầy chuẩn
            if (lblTitle != null) {
                lblTitle.setText("BÁN VÉ TẠI QUẦY");
            }
            btnSell.setText("BÁN VÉ");
            chkPaid.setVisible(true);
            chkPaid.setManaged(true);
            chkPaid.setSelected(true);

            // Nút bán chỉ bật khi đủ điều kiện: chọn khách + chọn suất + chọn ghế + không bận
            btnSell.disableProperty().bind(lvCustomers.getSelectionModel().selectedItemProperty().isNull()
                    .or(cbShowtime.valueProperty().isNull())
                    .or(Bindings.isEmpty(seatMap.getSelectedSeats()))
                    .or(busy));

            // (1) Tìm khách (debounce 300ms)
            debounce.setOnFinished(e -> fxAsync.run(() -> facade.searchCustomers(tfCustomer.getText().trim()), lvCustomers.getItems()::setAll));
            tfCustomer.textProperty().addListener((o, a, b) -> debounce.playFromStart());
            fxAsync.run(() -> facade.searchCustomers(""), lvCustomers.getItems()::setAll);
        }

        fxAsync.run(facade::movies, cbMovie.getItems()::setAll);
    }

    @FXML
    private void onSell() {
        SessionUser user = session.getUser();
        boolean isCustomer = user != null && user.isCustomer();

        Long customerId;
        String customerName;

        if (isCustomer) {
            customerId = user.getCustomerId() != null ? user.getCustomerId() : 1L;
            customerName = user.getDisplayName() != null ? user.getDisplayName() : "Khách hàng";
        } else {
            CustomerItem customer = lvCustomers.getSelectionModel().getSelectedItem();
            if (customer == null) {
                AlertUtil.showError(new IllegalStateException("Vui lòng chọn khách hàng trong danh sách."));
                return;
            }
            customerId = customer.id();
            customerName = customer.fullName();
        }

        ShowtimeItem showtime = cbShowtime.getValue();
        if (showtime == null) {
            AlertUtil.showError(new IllegalStateException("Vui lòng chọn suất chiếu."));
            return;
        }

        List<String> seats = List.copyOf(seatMap.getSelectedSeats());
        if (seats.isEmpty()) {
            AlertUtil.showError(new IllegalStateException("Vui lòng chọn ít nhất một ghế."));
            return;
        }

        // QUY TẮC CỐT LÕI:
        // - Khách hàng (CUSTOMER) tự đặt vé: LUÔN LUÔN paid = false -> trạng thái PENDING để Admin duyệt!
        // - Admin/Nhân viên bán vé tại quầy: paid theo Checkbox "Đã thanh toán" (BR-D2)
        boolean paid = !isCustomer && chkPaid.isSelected();
        String movieTitle = cbMovie.getValue() != null ? cbMovie.getValue().title() : "Phim";

        String confirmPrompt = isCustomer
                ? "Quý khách có chắc chắn muốn đặt %d vé (%s) cho phim \"%s\"?\n\n(Lưu ý: Đơn vé sẽ ở trạng thái PENDING chờ Admin duyệt)"
                        .formatted(seats.size(), String.join(", ", seats), movieTitle)
                : "Bán %d vé (%s) cho %s?".formatted(seats.size(), String.join(", ", seats), customerName);

        if (!AlertUtil.confirm(confirmPrompt)) {
            return;
        }

        busy.set(true);
        fxAsync.run(() -> facade.sell(customerId, showtime.id(), seats, paid),
                result -> {
                    busy.set(false);
                    if (isCustomer) {
                        AlertUtil.info("🎉 Đặt vé thành công!\n\n" +
                                "• Mã đơn vé: #" + result.bookingId() + "\n" +
                                "• Phim: " + movieTitle + "\n" +
                                "• Ghế đã đặt: " + String.join(", ", result.seats()) + "\n" +
                                "• Tổng tiền: " + MoneyUtil.format(result.total()) + "\n" +
                                "• Trạng thái: " + result.status() + " (Chờ Admin duyệt) ⏳\n\n" +
                                "Đơn vé của bạn đã được lưu trên hệ thống và đang chờ Admin duyệt.");
                    } else {
                        AlertUtil.info("Đơn #%d · Ghế %s · %s · %s".formatted(result.bookingId(),
                                String.join(", ", result.seats()), MoneyUtil.format(result.total()), result.status()));
                    }
                    reloadSeatMap();
                },
                ex -> {
                    busy.set(false);
                    AlertUtil.showError(ex); // "Ghế đã có người đặt: C5" / BR-11 ...
                    reloadSeatMap(); // ghế vừa bị người khác mua hiện thành "đã bán"
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

    /**
     * Hỗ trợ điều hướng thông minh từ AI Chatbot: Tự động chọn phim và suất chiếu để mở sơ đồ ghế.
     */
    public void selectShowtime(Long showtimeId, String movieTitle) {
        Platform.runLater(() -> {
            cbMovie.getItems().stream()
                    .filter(m -> m.title().equalsIgnoreCase(movieTitle) || movieTitle.contains(m.title()))
                    .findFirst()
                    .ifPresent(m -> {
                        cbMovie.setValue(m);
                        fxAsync.run(() -> facade.showtimes(m.id()), showtimes -> {
                            cbShowtime.getItems().setAll(showtimes);
                            showtimes.stream()
                                    .filter(st -> st.id().equals(showtimeId))
                                    .findFirst()
                                    .ifPresent(cbShowtime::setValue);
                        });
                    });
        });
    }
}


