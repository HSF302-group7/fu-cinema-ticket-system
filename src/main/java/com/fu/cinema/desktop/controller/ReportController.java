package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.facade.ReportFacade;
import com.fu.cinema.desktop.model.ReportBookingRow;
import com.fu.cinema.desktop.model.ReportData;
import com.fu.cinema.desktop.model.RevenueRow;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.CsvExporter;
import com.fu.cinema.desktop.util.FxAsync;
import com.fu.cinema.desktop.util.MoneyUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller màn hình Báo cáo doanh thu & Xuất dữ liệu CSV.
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ReportController {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReportFacade facade;
    private final FxAsync fxAsync;

    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private Button btnGenerate;
    @FXML private Button btnExport;
    @FXML private Label lblEmpty;
    @FXML private Label lblTotals;

    @FXML private BarChart<String, Number> chartRevenue;
    @FXML private PieChart pieTickets;

    @FXML private TableView<ReportBookingRow> tblBookings;
    @FXML private TableColumn<ReportBookingRow, Long> colId;
    @FXML private TableColumn<ReportBookingRow, String> colBookingDate;
    @FXML private TableColumn<ReportBookingRow, String> colCustomer;
    @FXML private TableColumn<ReportBookingRow, String> colMovie;
    @FXML private TableColumn<ReportBookingRow, String> colTotal;

    private final ObjectProperty<ReportData> data = new SimpleObjectProperty<>();
    private final ObservableList<ReportBookingRow> bookingRows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Mặc định: từ ngày 1 đầu tháng hiện tại đến ngày hôm nay
        dpStart.setValue(LocalDate.now().withDayOfMonth(1));
        dpEnd.setValue(LocalDate.now());

        // Quy tắc BR-15: Chặn chọn End < Start trên giao diện DatePicker
        dpEnd.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(empty || (dpStart.getValue() != null && d.isBefore(dpStart.getValue())));
            }
        });

        dpStart.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpEnd.getValue() != null && dpEnd.getValue().isBefore(newVal)) {
                dpEnd.setValue(newVal);
            }
        });

        chartRevenue.setAnimated(false);
        pieTickets.setAnimated(false);

        // Nút Xuất CSV bị disable khi chưa có dữ liệu hoặc danh sách rỗng
        btnExport.disableProperty().bind(Bindings.createBooleanBinding(
                () -> data.get() == null || data.get().isEmpty(), data));

        // Thiết lập bảng danh sách đơn đặt vé xác nhận trong kỳ
        tblBookings.setItems(bookingRows);
        colId.setCellValueFactory(c -> Bindings.createObjectBinding(() -> c.getValue().id()));
        colBookingDate.setCellValueFactory(c -> Bindings.createStringBinding(() ->
                c.getValue().bookingDate() != null ? c.getValue().bookingDate().format(DT_FORMAT) : "N/A"));
        colCustomer.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().customerName()));
        colMovie.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().movieTitle()));
        colTotal.setCellValueFactory(c -> Bindings.createStringBinding(() -> MoneyUtil.format(c.getValue().total())));

        // Tự động tải báo cáo cho kỳ mặc định
        onGenerate();
    }

    @FXML
    private void onGenerate() {
        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();
        if (start == null || end == null) {
            AlertUtil.warn("Vui lòng chọn đầy đủ từ ngày và đến ngày.");
            return;
        }
        if (start.isAfter(end)) {
            AlertUtil.warn("Từ ngày phải trước hoặc bằng đến ngày (BR-15).");
            return;
        }

        fxAsync.run(() -> facade.generate(start, end), this::render, btnGenerate);
    }

    private void render(ReportData d) {
        data.set(d);
        boolean empty = d.isEmpty();
        lblEmpty.setVisible(empty);
        lblEmpty.setManaged(empty);

        bookingRows.setAll(d.bookings());
        lblTotals.setText("Tổng: %d đơn · %d vé · %s".formatted(
                d.totalBookings(), d.totalTickets(), MoneyUtil.format(d.totalRevenue())));

        if (empty) {
            chartRevenue.getData().clear();
            pieTickets.getData().clear();
            return;
        }

        // Biểu đồ cột BarChart doanh thu theo phim
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        d.revenue().forEach(r -> series.getData().add(new XYChart.Data<>(r.title(), r.revenue())));
        chartRevenue.getData().setAll(List.of(series));

        // Tooltip cho từng cột trong BarChart: "Tên phim: x vé – y đ"
        for (int i = 0; i < series.getData().size(); i++) {
            RevenueRow r = d.revenue().get(i);
            XYChart.Data<String, Number> item = series.getData().get(i);
            String tooltipText = "%s: %d vé – %s".formatted(r.title(), r.tickets(), MoneyUtil.format(r.revenue()));
            if (item.getNode() != null) {
                Tooltip.install(item.getNode(), new Tooltip(tooltipText));
            } else {
                item.nodeProperty().addListener((obs, oldVal, newNode) -> {
                    if (newNode != null) {
                        Tooltip.install(newNode, new Tooltip(tooltipText));
                    }
                });
            }
        }

        // Biểu đồ tròn PieChart tỷ trọng số vé bán ra giữa các phim
        pieTickets.getData().setAll(d.revenue().stream()
                .map(r -> new PieChart.Data(r.title() + " (" + r.tickets() + ")", r.tickets()))
                .toList());

        for (int i = 0; i < d.revenue().size(); i++) {
            RevenueRow r = d.revenue().get(i);
            PieChart.Data slice = pieTickets.getData().get(i);
            String sliceTip = "%s: %d vé (%s)".formatted(r.title(), r.tickets(), MoneyUtil.format(r.revenue()));
            if (slice.getNode() != null) {
                Tooltip.install(slice.getNode(), new Tooltip(sliceTip));
            } else {
                slice.nodeProperty().addListener((obs, oldVal, newNode) -> {
                    if (newNode != null) {
                        Tooltip.install(newNode, new Tooltip(sliceTip));
                    }
                });
            }
        }
    }

    @FXML
    private void onExport() {
        ReportData snapshot = data.get();
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Xuất báo cáo doanh thu CSV");
        fc.setInitialFileName("doanh-thu_%s_%s.csv".formatted(snapshot.start(), snapshot.end()));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        File file = fc.showSaveDialog(btnExport.getScene().getWindow());
        if (file == null) {
            return; // Người dùng ấn Huỷ / Cancel -> không làm gì
        }
        fxAsync.runVoid(
                () -> CsvExporter.write(file.toPath(), snapshot),
                () -> AlertUtil.info("Đã xuất " + file.getName() + " thành công!"),
                null
        );
    }
}

