package com.example.app.controllers;

import com.example.app.models.TextFile;
import com.example.app.screens.MainScreen;

import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;


/*
This class change a bit the architecture but it was the best solution i could find to be able to control the Text file row buttons
*/

public class WatchingButtonControllers implements Callback<TableColumn<TextFile, Void>, TableCell<TextFile, Void>> {

    private final MainScreen main;

    public WatchingButtonControllers(MainScreen main) {
        this.main = main;
    }

    @Override
    public TableCell<TextFile, Void> call(TableColumn<TextFile, Void> param) {
        return new TableCell<>() {
            private final StackPane icon = new StackPane();
            private final ImageView eyeClose = new ImageView(
                new Image(getClass().getResourceAsStream("/assets/eye_close.png"), 20, 15, true, false)
            );
            private final ImageView eyeOpen = new ImageView(
                new Image(getClass().getResourceAsStream("/assets/eye_open.png"), 20, 15, true, false)
            );

            private boolean isWatching = false;

            {
                icon.setPrefSize(32, 32);
                icon.setStyle("-fx-cursor: hand;");
                icon.setOnMouseClicked(event -> {
                    // ✅ safe — only fires after cell is attached
                    TextFile file = getTableView().getItems().get(getIndex());
                    isWatching = !isWatching;
                    icon.getChildren().setAll(isWatching ? eyeOpen : eyeClose);
                    if (isWatching) main.getUser().addToWatchlist(file.getId(), file.getVersion());
                    else main.getUser().removeFromWatchlist(file.getId());
                });
                icon.getChildren().add(eyeClose);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // ✅ safe — updateItem is always called by the table after attachment
                    TextFile file = getTableView().getItems().get(getIndex());
                    isWatching = main.getUser().hasTextID(file.getId());
                    icon.getChildren().setAll(isWatching ? eyeOpen : eyeClose);
                    setGraphic(icon);
                }
            }
        };
    }
}