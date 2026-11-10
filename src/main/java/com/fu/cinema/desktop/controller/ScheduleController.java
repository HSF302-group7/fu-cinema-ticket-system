package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.facade.RoomFacade;
import com.fu.cinema.desktop.facade.ShowtimeFacade;
import com.fu.cinema.desktop.model.MovieItem;
import com.fu.cinema.desktop.model.PageResult;
import com.fu.cinema.desktop.model.RoomItem;
import com.fu.cinema.desktop.model.RoomRow;
import com.fu.cinema.desktop.model.ShowtimeRow;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.FxAsync;
import com.fu.cinema.desktop.util.FxValidator;
import com.fu.cinema.desktop.util.MoneyUtil;
import com.fu.cinema.dto.RoomForm;
import com.fu.cinema.dto.ShowtimeForm;
import com.fu.cinema.enums.RoomStatus;
import com.fu.cinema.enums.RoomType;
import com.fu.cinema.enums.ShowtimeStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ScheduleController {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final DateTimeFormatter END_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM");

    private final ShowtimeFacade showtimeFacade;
    private final RoomFacade roomFacade;
    private final FxAsync fxAsync;
    private final FxValidator validator;

    // --- TabPane ---
    @FXML private TabPane tabPane;

    // --- Tab Suất chiếu: Filter Controls ---
    @FXML private ComboBox<MovieItem> cbFilterMovie;
    @FXML private ComboBox<RoomItem> cbFilterRoom;
    @FXML private DatePicker dpFilterDate;

    // --- Tab Suất chiếu: Table & Pagination ---
    @FXML private TableView<ShowtimeRow> tblShowtimes;
    @FXML private TableColumn<ShowtimeRow, Number> colStId;
    @FXML private TableColumn<ShowtimeRow, String> colStMovie;
    @FXML private TableColumn<ShowtimeRow, String> colStRoom;
    @FXML private TableColumn<ShowtimeRow, String> colStStart;
    @FXML private TableColumn<ShowtimeRow, String> colStEnd;
    @FXML private TableColumn<ShowtimeRow, String> colStPrice;
    @FXML private TableColumn<ShowtimeRow, String> colStStatus;
    @FXML private TableColumn<ShowtimeRow, Void> colStAction;
    @FXML private Pagination paginationShowtimes;

    // --- Tab Suất chiếu: Form Controls ---
    @FXML private VBox formShowtimeBox;
    @FXML private Label lblShowtimeFormTitle;
    @FXML private ComboBox<MovieItem> cbMovie;
    @FXML private ComboBox<RoomItem> cbRoom;
    @FXML private DatePicker dpDate;
    @FXML private Spinner<Integer> spHour;
    @FXML private Spinner<Integer> spMinute;
    @FXML private TextField tfPrice;
    @FXML private Label lblEnd;

    private Long editingShowtimeId = null;
    private int currentShowtimePage = 0;

    // --- Tab Phòng chiếu: Table Controls ---
    @FXML private TableView<RoomRow> tblRooms;
    @FXML private TableColumn<RoomRow, Number> colRoomId;
    @FXML private TableColumn<RoomRow, String> colRoomName;
    @FXML private TableColumn<RoomRow, String> colRoomType;
    @FXML private TableColumn<RoomRow, Number> colRoomRows;
    @FXML private TableColumn<RoomRow, Number> colRoomSeatsPerRow;
    @FXML private TableColumn<RoomRow, Number> colRoomCapacity;
    @FXML private TableColumn<RoomRow, String> colRoomStatus;
    @FXML private TableColumn<RoomRow, Void> colRoomAction;

    // --- Tab Phòng chiếu: Form Controls ---
    @FXML private VBox formRoomBox;
    @FXML private Label lblRoomFormTitle;
    @FXML private TextField tfRoomName;
    @FXML private ComboBox<RoomType> cbRoomType;
    @FXML private Spinner<Integer> spTotalRows;
    @FXML private Spinner<Integer> spSeatsPerRow;
    @FXML private ComboBox<RoomStatus> cbRoomStatus;
    @FXML private Label lblRoomCapacityCalc;

    private Long editingRoomId = null;

    @FXML
    private void initialize() {
        initShowtimeTab();
        initRoomTab();

        // Tải dữ liệu ban đầu
        refreshDropdowns();
        loadShowtimes(0);
        loadRooms();
    }

    // =========================================================================
    //  INIT TAB SUẤT CHIẾU
    // =========================================================================
    private void initShowtimeTab() {
        // Cấu hình Spinner & Input
        spHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 19));
        spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 55, 0, 5));
        tfPrice.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,9}") ? c : null));

        // Không cho chọn ngày trong quá khứ khi lập lịch
        dpDate.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(empty || d.isBefore(LocalDate.now()));
            }
        });

        // Binding tự động tính giờ kết thúc gợi ý (Start + Duration + 15' dọn phòng)
        lblEnd.textProperty().bind(Bindings.createStringBinding(() -> {
            LocalDateTime end = suggestedEnd();
            return end == null ? "Kết thúc (gợi ý): —" : "Kết thúc (gợi ý): " + end.format(END_FORMATTER);
        }, cbMovie.valueProperty(), dpDate.valueProperty(), spHour.valueProperty(), spMinute.valueProperty()));

        // Cấu hình TableColumns
        colStId.setCellValueFactory(d -> new javafx.beans.property.SimpleLongProperty(d.getValue().id()));
        colStMovie.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().movieTitle()));
        colStRoom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().roomName()));
        colStStart.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().start() != null ? d.getValue().start().format(DT_FORMATTER) : ""));
        colStEnd.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().end() != null ? d.getValue().end().format(DT_FORMATTER) : ""));
        colStPrice.setCellValueFactory(d -> new SimpleStringProperty(MoneyUtil.format(d.getValue().price())));
        colStStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().status() != null ? d.getValue().status().name() : ""));

        setupShowtimeActionsColumn();

        // Cấu hình Pagination
        paginationShowtimes.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            currentShowtimePage = newIdx.intValue();
            loadShowtimes(currentShowtimePage);
        });
    }

    private void setupShowtimeActionsColumn() {
        colStAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xoá");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.setStyle("-fx-font-size: 11px;");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");

                btnEdit.setOnAction(e -> {
                    ShowtimeRow row = getTableView().getItems().get(getIndex());
                    onEditShowtime(row);
                });

                btnDelete.setOnAction(e -> {
                    ShowtimeRow row = getTableView().getItems().get(getIndex());
                    onDeleteShowtime(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private LocalDateTime startTime() {
        LocalDate date = dpDate.getValue();
        if (date == null) return null;
        Integer hour = spHour.getValue();
        Integer minute = spMinute.getValue();
        if (hour == null || minute == null) return null;
        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }

    private LocalDateTime suggestedEnd() {
        LocalDateTime start = startTime();
        MovieItem movie = cbMovie.getValue();
        if (start == null || movie == null) return null;
        return start.plusMinutes(movie.duration() + 15L); // Start + Duration + 15 phút dọn phòng
    }

    public void loadShowtimes(int page) {
        Long movieId = cbFilterMovie.getValue() != null ? cbFilterMovie.getValue().id() : null;
        Long roomId = cbFilterRoom.getValue() != null ? cbFilterRoom.getValue().id() : null;
        LocalDate date = dpFilterDate.getValue();

        fxAsync.run(() -> showtimeFacade.search(movieId, roomId, date, page),
                (PageResult<ShowtimeRow> result) -> {
                    tblShowtimes.setItems(FXCollections.observableArrayList(result.items()));
                    int totalPages = Math.max(1, result.totalPages());
                    paginationShowtimes.setPageCount(totalPages);
                    if (paginationShowtimes.getCurrentPageIndex() != result.page()) {
                        paginationShowtimes.setCurrentPageIndex(result.page());
                    }
                }, tblShowtimes);
    }

    @FXML
    private void onFilterShowtimes() {
        currentShowtimePage = 0;
        loadShowtimes(0);
    }

    @FXML
    private void onResetFilterShowtimes() {
        cbFilterMovie.setValue(null);
        cbFilterRoom.setValue(null);
        dpFilterDate.setValue(null);
        currentShowtimePage = 0;
        loadShowtimes(0);
    }

    @FXML
    private void onNewShowtime() {
        resetShowtimeForm();
        lblShowtimeFormTitle.setText("Thêm suất chiếu mới");
    }

    private void onEditShowtime(ShowtimeRow row) {
        editingShowtimeId = row.id();
        lblShowtimeFormTitle.setText("Sửa suất chiếu #" + row.id());

        fxAsync.run(() -> showtimeFacade.getForm(row.id()), form -> {
            // Chọn phim
            cbMovie.getItems().stream()
                    .filter(m -> m.id().equals(form.getMovieId()))
                    .findFirst().ifPresent(cbMovie::setValue);

            // Chọn phòng
            cbRoom.getItems().stream()
                    .filter(r -> r.id().equals(form.getRoomId()))
                    .findFirst().ifPresent(cbRoom::setValue);

            if (form.getStartTime() != null) {
                dpDate.setValue(form.getStartTime().toLocalDate());
                spHour.getValueFactory().setValue(form.getStartTime().getHour());
                spMinute.getValueFactory().setValue(form.getStartTime().getMinute());
            }

            tfPrice.setText(form.getTicketPrice() != null ? form.getTicketPrice().toPlainString() : "");
        }, formShowtimeBox);
    }

    @FXML
    private void onSaveShowtime() {
        ShowtimeForm f = new ShowtimeForm();
        f.setShowtimeId(editingShowtimeId);
        f.setMovieId(cbMovie.getValue() == null ? null : cbMovie.getValue().id());
        f.setRoomId(cbRoom.getValue() == null ? null : cbRoom.getValue().id());
        f.setStartTime(startTime());
        f.setEndTime(suggestedEnd()); // Nếu null thì service tự tính
        f.setTicketPrice(tfPrice.getText().isBlank() ? null : new BigDecimal(tfPrice.getText().trim()));
        f.setStatus(ShowtimeStatus.OPEN);

        Map<String, Control> fieldControls = Map.of(
                "movieId", cbMovie,
                "roomId", cbRoom,
                "startTime", dpDate,
                "ticketPrice", tfPrice
        );

        if (!validator.mark(validator.validate(f), fieldControls)) {
            return;
        }

        fxAsync.runVoid(() -> showtimeFacade.save(f), () -> {
            AlertUtil.info("Đã lưu suất chiếu thành công!");
            resetShowtimeForm();
            loadShowtimes(currentShowtimePage);
        }, formShowtimeBox);
    }

    private void onDeleteShowtime(ShowtimeRow row) {
        boolean confirmed = AlertUtil.confirm("Bạn có chắc chắn muốn xoá suất chiếu #" + row.id() + " (" + row.movieTitle() + ")?");
        if (!confirmed) return;

        fxAsync.run(() -> showtimeFacade.delete(row.id()), isDeleted -> {
            if (Boolean.TRUE.equals(isDeleted)) {
                AlertUtil.info("Đã xoá hoàn toàn suất chiếu.");
            } else {
                AlertUtil.info("Suất chiếu đã có đơn vé. Hệ thống đã huỷ suất chiếu và toàn bộ các đơn liên quan (tuân thủ BR-09).");
            }
            loadShowtimes(currentShowtimePage);
        }, tblShowtimes);
    }

    @FXML
    private void onCancelShowtimeForm() {
        resetShowtimeForm();
    }

    private void resetShowtimeForm() {
        editingShowtimeId = null;
        lblShowtimeFormTitle.setText("Thêm suất chiếu mới");
        cbMovie.setValue(null);
        cbRoom.setValue(null);
        dpDate.setValue(LocalDate.now());
        spHour.getValueFactory().setValue(19);
        spMinute.getValueFactory().setValue(0);
        tfPrice.clear();
        validator.mark(Map.of(), Map.of(
                "movieId", cbMovie,
                "roomId", cbRoom,
                "startTime", dpDate,
                "ticketPrice", tfPrice
        ));
    }

    // =========================================================================
    //  INIT TAB PHÒNG CHIẾU
    // =========================================================================
    private void initRoomTab() {
        cbRoomType.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbRoomStatus.setItems(FXCollections.observableArrayList(RoomStatus.values()));
        cbRoomStatus.setValue(RoomStatus.ACTIVE);

        spTotalRows.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 26, 8));
        spSeatsPerRow.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 12));

        // Binding tính sức chứa phòng: totalRows * seatsPerRow
        lblRoomCapacityCalc.textProperty().bind(Bindings.createStringBinding(() -> {
            int rows = spTotalRows.getValue() != null ? spTotalRows.getValue() : 0;
            int seats = spSeatsPerRow.getValue() != null ? spSeatsPerRow.getValue() : 0;
            return "Sức chứa tính toán: " + (rows * seats) + " ghế (" + rows + " hàng × " + seats + " ghế/hàng)";
        }, spTotalRows.valueProperty(), spSeatsPerRow.valueProperty()));

        colRoomId.setCellValueFactory(d -> new javafx.beans.property.SimpleLongProperty(d.getValue().id()));
        colRoomName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        colRoomType.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().type() != null ? d.getValue().type().getLabel() : ""));
        colRoomRows.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().rows()));
        colRoomSeatsPerRow.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().seatsPerRow()));
        colRoomCapacity.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().capacity()));
        colRoomStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().status() != null ? d.getValue().status().name() : ""));

        setupRoomActionsColumn();
    }

    private void setupRoomActionsColumn() {
        colRoomAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xoá");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.setStyle("-fx-font-size: 11px;");
                btnDelete.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");

                btnEdit.setOnAction(e -> {
                    RoomRow row = getTableView().getItems().get(getIndex());
                    onEditRoom(row);
                });

                btnDelete.setOnAction(e -> {
                    RoomRow row = getTableView().getItems().get(getIndex());
                    onDeleteRoom(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    public void loadRooms() {
        fxAsync.run(roomFacade::findAll, rooms -> {
            tblRooms.setItems(FXCollections.observableArrayList(rooms));
        }, tblRooms);
    }

    @FXML
    private void onNewRoom() {
        resetRoomForm();
        lblRoomFormTitle.setText("Thêm phòng chiếu mới");
    }

    private void onEditRoom(RoomRow row) {
        editingRoomId = row.id();
        lblRoomFormTitle.setText("Sửa phòng chiếu #" + row.id());

        fxAsync.run(() -> roomFacade.getForm(row.id()), form -> {
            tfRoomName.setText(form.getRoomName());
            cbRoomType.setValue(form.getRoomType());
            if (form.getTotalRows() != null) spTotalRows.getValueFactory().setValue(form.getTotalRows());
            if (form.getSeatsPerRow() != null) spSeatsPerRow.getValueFactory().setValue(form.getSeatsPerRow());
            cbRoomStatus.setValue(form.getStatus());
        }, formRoomBox);
    }

    @FXML
    private void onSaveRoom() {
        RoomForm f = new RoomForm();
        f.setRoomId(editingRoomId);
        f.setRoomName(tfRoomName.getText().trim());
        f.setRoomType(cbRoomType.getValue());
        f.setTotalRows(spTotalRows.getValue());
        f.setSeatsPerRow(spSeatsPerRow.getValue());
        f.setStatus(cbRoomStatus.getValue());

        Map<String, Control> fieldControls = Map.of(
                "roomName", tfRoomName,
                "roomType", cbRoomType,
                "totalRows", spTotalRows,
                "seatsPerRow", spSeatsPerRow,
                "status", cbRoomStatus
        );

        if (!validator.mark(validator.validate(f), fieldControls)) {
            return;
        }

        fxAsync.runVoid(() -> roomFacade.save(f), () -> {
            AlertUtil.info("Đã lưu phòng chiếu thành công!");
            resetRoomForm();
            loadRooms();
            refreshDropdowns();
        }, formRoomBox);
    }

    private void onDeleteRoom(RoomRow row) {
        boolean confirmed = AlertUtil.confirm("Bạn có chắc chắn muốn xoá phòng chiếu #" + row.id() + " (" + row.name() + ")?");
        if (!confirmed) return;

        fxAsync.run(() -> roomFacade.delete(row.id()), isDeleted -> {
            if (Boolean.TRUE.equals(isDeleted)) {
                AlertUtil.info("Đã xoá phòng chiếu thành công.");
            } else {
                AlertUtil.info("Phòng chiếu đã phát sinh suất chiếu, hệ thống đã chuyển sang trạng thái BẢO TRÌ (MAINTENANCE).");
            }
            loadRooms();
            refreshDropdowns();
        }, tblRooms);
    }

    @FXML
    private void onCancelRoomForm() {
        resetRoomForm();
    }

    private void resetRoomForm() {
        editingRoomId = null;
        lblRoomFormTitle.setText("Thêm phòng chiếu mới");
        tfRoomName.clear();
        cbRoomType.setValue(null);
        spTotalRows.getValueFactory().setValue(8);
        spSeatsPerRow.getValueFactory().setValue(12);
        cbRoomStatus.setValue(RoomStatus.ACTIVE);
        validator.mark(Map.of(), Map.of(
                "roomName", tfRoomName,
                "roomType", cbRoomType,
                "totalRows", spTotalRows,
                "seatsPerRow", spSeatsPerRow,
                "status", cbRoomStatus
        ));
    }

    // =========================================================================
    //  REFRESH DROPDOWNS DÙNG CHUNG
    // =========================================================================
    private void refreshDropdowns() {
        // Phim có thể lập lịch (BR-07: loại bỏ phim STOPPED)
        fxAsync.run(showtimeFacade::schedulableMovies, movies -> {
            cbFilterMovie.setItems(FXCollections.observableArrayList(movies));
            cbMovie.setItems(FXCollections.observableArrayList(movies));
        });

        // Phòng hoạt động (loại bỏ phòng MAINTENANCE)
        fxAsync.run(showtimeFacade::activeRooms, rooms -> {
            cbFilterRoom.setItems(FXCollections.observableArrayList(rooms));
            cbRoom.setItems(FXCollections.observableArrayList(rooms));
        });
    }
}

