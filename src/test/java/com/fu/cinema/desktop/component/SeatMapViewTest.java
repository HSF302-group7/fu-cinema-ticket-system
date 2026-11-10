package com.fu.cinema.desktop.component;

import com.fu.cinema.desktop.model.SeatMapData;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeatMapViewTest {

    @BeforeAll
    static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // Toolkit đã được khởi tạo
        }
    }

    @Test
    @DisplayName("SeatMapView: Khởi tạo rỗng không lỗi và danh sách ghế chọn ban đầu rỗng")
    void testInitialState() {
        SeatMapView view = new SeatMapView();
        assertNotNull(view.getSelectedSeats());
        assertTrue(view.getSelectedSeats().isEmpty());
    }

    @Test
    @DisplayName("SeatMapView (BR-10): Ghế đã bán (sold) bị disable, ghế trống có thể chọn và huỷ chọn")
    void testSeatSelectionAndSoldStatus() {
        SeatMapView view = new SeatMapView();
        SeatMapData data = new SeatMapData(
                1L,
                "Mai",
                "Cinema 1",
                LocalDateTime.of(2026, 11, 10, 19, 0),
                BigDecimal.valueOf(80000),
                List.of("A", "B"),
                4,
                Set.of("A1", "B2") // A1 và B2 đã bán
        );

        view.setData(data);

        // Lấy GridPane chứa ma trận ghế (nằm ở vị trí con thứ 1 của VBox)
        GridPane grid = (GridPane) view.getChildren().stream()
                .filter(node -> node instanceof GridPane)
                .findFirst()
                .orElseThrow();

        ToggleButton seatA1 = null;
        ToggleButton seatA2 = null;

        for (Node node : grid.getChildren()) {
            if (node instanceof ToggleButton btn) {
                if ("1".equals(btn.getText()) && GridPane.getRowIndex(node) == 0) {
                    seatA1 = btn;
                } else if ("2".equals(btn.getText()) && GridPane.getRowIndex(node) == 0) {
                    seatA2 = btn;
                }
            }
        }

        // A1 đã bán -> phải bị disable
        assertNotNull(seatA1);
        assertTrue(seatA1.isDisabled(), "Ghế A1 đã bán phải bị disable");

        // A2 chưa bán -> enable
        assertNotNull(seatA2);
        assertFalse(seatA2.isDisabled(), "Ghế A2 trống không được disable");

        // Click chọn A2 -> danh sách selected phải có A2
        seatA2.setSelected(true);
        assertEquals(1, view.getSelectedSeats().size());
        assertTrue(view.getSelectedSeats().contains("A2"));

        // Bỏ chọn A2 -> danh sách selected rỗng
        seatA2.setSelected(false);
        assertTrue(view.getSelectedSeats().isEmpty());
    }

    @Test
    @DisplayName("SeatMapView (BR-10): Giới hạn tối đa 8 ghế, ghế thứ 9 tự động bỏ chọn")
    void testMaxSeatsLimit() {
        SeatMapView view = new SeatMapView();
        // Phòng 2 hàng, 5 ghế mỗi hàng = 10 ghế
        SeatMapData data = new SeatMapData(
                1L,
                "Mai",
                "Cinema 1",
                LocalDateTime.of(2026, 11, 10, 19, 0),
                BigDecimal.valueOf(80000),
                List.of("A", "B"),
                5,
                Set.of() // Tất cả đều trống
        );

        view.setData(data);

        GridPane grid = (GridPane) view.getChildren().stream()
                .filter(node -> node instanceof GridPane)
                .findFirst()
                .orElseThrow();

        List<ToggleButton> seats = grid.getChildren().stream()
                .filter(node -> node instanceof ToggleButton)
                .map(node -> (ToggleButton) node)
                .toList();

        // Chọn lần lượt 8 ghế đầu tiên
        for (int i = 0; i < 8; i++) {
            seats.get(i).setSelected(true);
        }
        assertEquals(8, view.getSelectedSeats().size());

        // Thử chọn ghế thứ 9 -> Ghế thứ 9 phải bị huỷ chọn và số lượng vẫn là 8
        seats.get(8).setSelected(true);
        assertFalse(seats.get(8).isSelected(), "Ghế thứ 9 phải tự động chuyển thành false");
        assertEquals(8, view.getSelectedSeats().size());
    }
}
