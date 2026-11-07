package com.fu.cinema.desktop.controller;

import com.fu.cinema.desktop.facade.MovieFacade;
import com.fu.cinema.desktop.model.GenreItem;
import com.fu.cinema.desktop.model.MovieRow;
import com.fu.cinema.desktop.navigation.SceneManager;
import com.fu.cinema.desktop.navigation.View;
import com.fu.cinema.desktop.util.AlertUtil;
import com.fu.cinema.desktop.util.FxAsync;
import com.fu.cinema.dto.MovieForm;
import com.fu.cinema.enums.MovieStatus;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MovieController {

    private static final PseudoClass STOPPED = PseudoClass.getPseudoClass("stopped");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MovieFacade facade;
    private final FxAsync fxAsync;
    private final SceneManager sceneManager;

    @FXML private TextField tfSearch;
    @FXML private ComboBox<GenreItem> cbGenre;
    @FXML private ComboBox<MovieStatus> cbStatus;
    @FXML private ComboBox<String> cbSort;
    @FXML private Label lblCount;
    @FXML private TableView<MovieRow> table;
    @FXML private TableColumn<MovieRow, String> colTitle;
    @FXML private TableColumn<MovieRow, String> colGenre;
    @FXML private TableColumn<MovieRow, Integer> colDuration;
    @FXML private TableColumn<MovieRow, LocalDate> colRelease;
    @FXML private TableColumn<MovieRow, String> colAge;
    @FXML private TableColumn<MovieRow, String> colStatus;
    @FXML private Pagination pagination;

    private final ObservableList<MovieRow> rows = FXCollections.observableArrayList();
    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    @FXML
    private void initialize() {
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        colGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().genreName()));
        colAge.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ageRating() != null ? c.getValue().ageRating().name() : ""));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status() != null ? c.getValue().status().name() : ""));

        colDuration.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().duration()));
        colDuration.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v + " phút");
            }
        });

        colRelease.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().releaseDate()));
        colRelease.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.format(DATE_FORMATTER));
            }
        });

        table.setRowFactory(tv -> {
            TableRow<MovieRow> row = new TableRow<>() {
                @Override
                protected void updateItem(MovieRow m, boolean empty) {
                    super.updateItem(m, empty);
                    pseudoClassStateChanged(STOPPED, !empty && m != null && m.status() == MovieStatus.STOPPED);
                }
            };
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    openDialog(row.getItem().id());
                }
            });
            return row;
        });
        table.setItems(rows);

        cbStatus.getItems().setAll(Arrays.asList((MovieStatus) null));
        cbStatus.getItems().addAll(MovieStatus.values());
        cbStatus.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MovieStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tất cả trạng thái" : item.name());
            }
        });
        cbStatus.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MovieStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tất cả trạng thái" : item.name());
            }
        });

        cbSort.getItems().setAll("releaseDate", "title");
        cbSort.setValue("releaseDate");
        cbSort.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if ("releaseDate".equals(item)) {
                    setText("Ngày phát hành");
                } else if ("title".equals(item)) {
                    setText("Tên phim");
                } else {
                    setText(item);
                }
            }
        });
        cbSort.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if ("releaseDate".equals(item)) {
                    setText("Ngày phát hành");
                } else if ("title".equals(item)) {
                    setText("Tên phim");
                } else {
                    setText(item);
                }
            }
        });

        cbGenre.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(GenreItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tất cả thể loại" : item.name());
            }
        });
        cbGenre.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(GenreItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tất cả thể loại" : item.name());
            }
        });

        pagination.setPageFactory(i -> new Region());
        pagination.currentPageIndexProperty().addListener((o, a, b) -> load(b.intValue()));

        debounce.setOnFinished(e -> search());
        tfSearch.textProperty().addListener((o, a, b) -> debounce.playFromStart());
        cbGenre.valueProperty().addListener((o, a, b) -> search());
        cbStatus.valueProperty().addListener((o, a, b) -> search());
        cbSort.valueProperty().addListener((o, a, b) -> search());

        fxAsync.run(facade::genres, g -> {
            cbGenre.getItems().setAll(Arrays.asList((GenreItem) null));
            cbGenre.getItems().addAll(g);
            load(0);
        });
    }

    private void search() {
        if (pagination.getCurrentPageIndex() != 0) {
            pagination.setCurrentPageIndex(0);
        } else {
            load(0);
        }
    }

    private void load(int page) {
        String kw = tfSearch.getText();
        Long genreId = cbGenre.getValue() == null ? null : cbGenre.getValue().id();
        fxAsync.run(
                () -> facade.search(kw, genreId, cbStatus.getValue(), cbSort.getValue(), page),
                result -> {
                    rows.setAll(result.items());
                    pagination.setPageCount(Math.max(1, result.totalPages()));
                    lblCount.setText(result.totalElements() + " phim");
                }
        );
    }

    @FXML
    private void onAdd() {
        openDialog(null);
    }

    @FXML
    private void onDelete() {
        MovieRow m = table.getSelectionModel().getSelectedItem();
        if (m == null) {
            AlertUtil.warn("Vui lòng chọn phim cần xoá");
            return;
        }
        if (!AlertUtil.confirm("Bạn có chắc chắn muốn xoá phim \"" + m.title() + "\"?")) {
            return;
        }
        fxAsync.run(
                () -> facade.delete(m.id()),
                deleted -> {
                    AlertUtil.info(deleted ? "Đã xoá phim thành công" : "Phim đã có suất chiếu -> đã chuyển sang STOPPED");
                    load(pagination.getCurrentPageIndex());
                }
        );
    }

    private void openDialog(Long movieId) {
        Window owner = table.getScene().getWindow();
        fxAsync.run(
                () -> new Object[]{ facade.genres(), movieId == null ? new MovieForm() : facade.getForm(movieId) },
                data -> {
                    @SuppressWarnings("unchecked") List<GenreItem> genres = (List<GenreItem>) data[0];
                    SceneManager.Loaded<MovieDialogController> dlg = sceneManager.load(View.MOVIE_DIALOG);
                    dlg.controller().open(owner, (MovieForm) data[1], genres).ifPresent(form ->
                            fxAsync.runVoid(
                                    () -> facade.save(form),
                                    () -> {
                                        AlertUtil.info("Đã lưu phim thành công");
                                        load(pagination.getCurrentPageIndex());
                                    },
                                    null
                            )
                    );
                }
        );
    }
}
