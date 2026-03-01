package com.example.app.controllers;

import java.util.Optional;

import com.example.app.Utils;
import com.example.app.models.TextFile;
import com.example.app.models.Writer;
import com.example.app.screens.MainScreen;

import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;


/*
This class change a bit the architecture but it was the best solution i could find to be able to control the Text file row buttons
*/

public class XButtonControllers implements Callback<TableColumn<TextFile, Void>, TableCell<TextFile, Void>> {

    private final MainScreen main;

    public XButtonControllers(MainScreen main) {
        this.main = main;
    }

    @Override
    public TableCell<TextFile, Void> call(TableColumn<TextFile, Void> param) {
        return new TableCell<>() {
            private final ImageView icon = new ImageView(
                new Image(getClass().getResourceAsStream("/assets/delete_icon.png"), 20, 10, true, false)
            );
            private final StackPane container = new StackPane(icon); 

            {
                container.setPrefSize(32, 32); 
                container.setStyle("-fx-cursor: hand;");
                container.setOnMouseClicked(event -> { // ✅ listener on container, not icon
                    TextFile file = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Delete Action");
                    alert.setContentText("You are about to delete the following file (all versions): " + file.getTitle() + "\nAre you 100% sure?");

                    ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                    ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

                    alert.getButtonTypes().setAll(yesButton, noButton);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == noButton) {
                        return;
                    }

                    Writer writer = (Writer) main.getUser();
                    writer.setHasNewFile();
                    writer.removeFromWatchlist(file.getId()); 
                    TextFile.deleteFile(file);
                    Utils.allTextFiles.remove(file);
                    main.setViewCategories(writer.getCategories());
                    main.returnToMainArea();    //update Table
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if(!main.getUser().getRole().equals("Admin") && !(main.getUser().getRole().equals("Writer") && main.getUser().getName().equals(getTableView().getItems().get(getIndex()).getAuthor()))){
                        setGraphic(null);
                        return;
                    }
                    setGraphic(container); // ✅ set container, not icon
                }
            }
        };
    }
}