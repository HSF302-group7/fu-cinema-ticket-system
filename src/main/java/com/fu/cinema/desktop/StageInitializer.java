package com.fu.cinema.desktop;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setTitle("FU Cinema — Quầy vé");
        StackPane root = new StackPane(new Label("FU Cinema Desktop — Sẵn sàng"));
        Scene scene = new Scene(root, 1000, 640);
        stage.setScene(scene);
        stage.show();
    }
}
