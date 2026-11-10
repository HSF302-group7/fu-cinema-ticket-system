package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.facade.BookingFacade;
import com.fu.cinema.desktop.model.BookingDetail;
import com.fu.cinema.desktop.model.BookingRow;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.FxAsync;
import com.fu.cinema.desktop.util.MoneyUtil;
import com.fu.cinema.enums.BookingStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Controller cho màn hình Quản lý đơn đặt vé trên Desktop (Spring Prototype Bean).
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class BookingAdminController {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BookingFacade facade;
    private final FxAsync fxAsync;
    private final DesktopSession session;

    @FXML private VBox root;
    @FXML private Label lblTitle;
    @FXML private ComboBox<BookingStatus> cbStatus;
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private TextField tfKeyword;
    @FXML private Button btnConfirm, btnCancel;

    @FXML private TableView<BookingRow> table;
    @FXML private TableColumn<BookingRow, Long> colId;
    @FXML private TableColumn<BookingRow, String> colCustomer, colMovie, colRoom, colTotal;
    @FXML private TableColumn<BookingRow, Integer> colSeatCount;
    @FXML private TableColumn<BookingRow, BookingStatus> colStatus;
    @FXML private TableColumn<BookingRow, String> colBookingDate, colStartTime;
    @FXML private Pagination pagination;

    @FXML
    private void initialize() {
        SessionUser user = session.getUser();
        boolean isCustomer = user != null && user.isCustomer();
        if (isCustomer) {
            if (lblTitle != null) {
                lblTitle.setText("Vé của tôi");
            }
            btnConfirm.setVisible(false);
            btnConfirm.setManaged(false);
        }

        setupFilterBar();
        setupTableColumns();
        setupActionBindings();

        // Lắng nghe sự kiện chuyển trang của Pagination
        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> loadPage(newIdx.intValue()));

        // Tải trang đầu tiên
        loadPage(0);
    }

    private void setupFilterBar() {
        cbStatus.getItems().add(null);
        cbStatus.getItems().addAll(BookingStatus.values());
        cbStatus.setConverter(new StringConverter<>() {
            @Override
            public String toString(BookingStatus status) {
                return status == null ? "-- Tất cả trạng thái --" : status.name();
            }

            @Override
            public BookingStatus fromString(String string) {
                return null;
            }
        });
        cbStatus.setValue(null);
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(c -> Bindings.createObjectBinding(() -> c.getValue().id()));
        colCustomer.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().customerName()));
        colMovie.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().movieTitle()));
        colRoom.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().roomName()));
        colSeatCount.setCellValueFactory(c -> Bindings.createObjectBinding(() -> c.getValue().seatCount()));
        colTotal.setCellValueFactory(c -> Bindings.createStringBinding(() -> MoneyUtil.format(c.getValue().total())));

        colStartTime.setCellValueFactory(c -> Bindings.createStringBinding(() ->
                c.getValue().startTime() != null ? c.getValue().startTime().format(DT_FORMAT) : "N/A"));

        colBookingDate.setCellValueFactory(c -> Bindings.createStringBinding(() ->
                c.getValue().bookingDate() != null ? c.getValue().bookingDate().format(DT_FORMAT) : "N/A"));

        // Định dạng và tô màu theo trạng thái đơn
        colStatus.setCellValueFactory(c -> Bindings.createObjectBinding(() -> c.getValue().status()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BookingStatus status, boolean empty) {
                super.updateItem(status, empty);
                getStyleClass().removeAll("st-pending", "st-confirmed", "st-cancelled");
                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status.name());
                    getStyleClass().add("st-" + status.name().toLowerCase());
                }
            }
        });

        // Double click vào dòng để xem chi tiết
        table.setRowFactory(tv -> {
            TableRow<BookingRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showDetail(row.getItem().id());
                }
            });
            return row;
        });
    }

    private void setupActionBindings() {
        ReadOnlyObjectProperty<BookingRow> sel = table.getSelectionModel().selectedItemProperty();

        // Nút Xác nhận: chỉ bật khi chọn đơn PENDING
        btnConfirm.disableProperty().bind(Bindings.createBooleanBinding(
                () -> sel.get() == null || sel.get().status() != BookingStatus.PENDING, sel));

        // Nút Huỷ: chỉ bật khi chọn đơn khác CANCELLED (tức là PENDING hoặc CONFIRMED)
        btnCancel.disableProperty().bind(Bindings.createBooleanBinding(
                () -> sel.get() == null || sel.get().status() == BookingStatus.CANCELLED, sel));
    }

    @FXML
    public void onSearch() {
        // Tìm kiếm luôn đưa về trang 0
        if (pagination.getCurrentPageIndex() == 0) {
            loadPage(0);
        } else {
            pagination.setCurrentPageIndex(0);
        }
    }

    @FXML
    public void onReset() {
        cbStatus.setValue(null);
        dpFrom.setValue(null);
        dpTo.setValue(null);
        tfKeyword.clear();
        onSearch();
    }

    private void loadPage(int page) {
        fxAsync.run(
                () -> facade.search(cbStatus.getValue(), dpFrom.getValue(), dpTo.getValue(), tfKeyword.getText(), page),
                result -> {
                    table.getItems().setAll(result.items());
                    pagination.setPageCount(Math.max(1, result.totalPages()));
                },
                table
        );
    }

    @FXML
    private void onConfirm() {
        BookingRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (AlertUtil.confirm("Xác nhận đơn đặt vé #" + selected.id() + " của " + selected.customerName() + "?")) {
            fxAsync.runVoid(
                    () -> facade.confirm(selected.id()),
                    () -> {
                        AlertUtil.info("Đã xác nhận thành công đơn #" + selected.id());
                        loadPage(pagination.getCurrentPageIndex());
                    },
                    null
            );
        }
    }

    @FXML
    private void onCancel() {
        BookingRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (AlertUtil.confirm("Bạn có chắc chắn muốn HUỶ đơn đặt vé #" + selected.id() + "?")) {
            fxAsync.runVoid(
                    () -> facade.cancel(selected.id()),
                    () -> {
                        AlertUtil.info("Đã huỷ thành công đơn #" + selected.id());
                        loadPage(pagination.getCurrentPageIndex());
                    },
                    null
            );
        }
    }

    private void showDetail(Long bookingId) {
        fxAsync.run(
                () -> facade.detail(bookingId),
                detail -> {
                    Dialog<Void> dialog = new Dialog<>();
                    dialog.setTitle("Chi tiết đơn đặt vé #" + detail.id());
                    dialog.setHeaderText("Đơn đặt vé #" + detail.id() + " — " + detail.customerName());
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                    GridPane grid = new GridPane();
                    grid.setHgap(16);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(16, 24, 16, 24));

                    int row = 0;
                    addDetailRow(grid, row++, "Khách hàng:", detail.customerName() + " (" + detail.mobile() + ")");
                    addDetailRow(grid, row++, "Phim:", detail.movieTitle());
                    addDetailRow(grid, row++, "Phòng chiếu:", detail.roomName());
                    addDetailRow(grid, row++, "Suất chiếu:", detail.startTime() != null ? detail.startTime().format(DT_FORMAT) : "N/A");
                    addDetailRow(grid, row++, "Danh sách ghế:", String.join(", ", detail.seats()));
                    addDetailRow(grid, row++, "Tổng tiền:", MoneyUtil.format(detail.total()));
                    addDetailRow(grid, row++, "Trạng thái:", detail.status() != null ? detail.status().name() : "N/A");
                    addDetailRow(grid, row++, "Thời gian đặt:", detail.bookingDate() != null ? detail.bookingDate().format(DT_FORMAT) : "N/A");

                    dialog.getDialogPane().setContent(grid);
                    dialog.showAndWait();
                },
                table
        );
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lblTitle = new Label(label);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        Label lblVal = new Label(value);
        lblVal.setStyle("-fx-text-fill: #111827;");
        grid.add(lblTitle, 0, row);
        grid.add(lblVal, 1, row);
    }
}

