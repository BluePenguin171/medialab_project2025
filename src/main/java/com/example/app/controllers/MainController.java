package com.example.app.controllers;

import java.util.ArrayList;
import java.util.Optional;

import com.example.app.App;
import com.example.app.Utils;
import com.example.app.models.Admin;
import com.example.app.models.User;
import com.example.app.models.Writer;
import com.example.app.screens.MainScreen;
import com.example.app.models.TextFile;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


public class MainController {
    private App app;
    private MainScreen main; 

    public MainController(App app, MainScreen main){
        this.app = app;
        this.main = main;

        initController();
    }

    private void initController(){
        main.getAddUsers().setOnMouseClicked(
            (e) -> {
                if(main.getUser().getCategories().size() == 0){ //only "All" category exists
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message");
                    alert.setHeaderText("Error");
                    alert.setContentText("Please create a category before adding a user!");
                    alert.showAndWait();
                    return;
                }
                if(!main.getActionActiveFlag()){
                    main.setActionActiveFlage();
                    main.createUserForm();
                    formLogic();
                }
            }
        );

        main.getCategories().setOnAction(
            (e) -> CategoryLogic()
        );

        main.getNewFileIcon().setOnMouseClicked(
            (e)->{
                if(main.getUser().getCategories().size() == 0){ //only "All" category exists
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message");
                    alert.setHeaderText("Error");
                    alert.setContentText("Please create a category before creating a file!");
                    alert.showAndWait();
                    return;
                }

                if(!main.getActionActiveFlag()){
                    main.setActionActiveFlage();
                    TextFile newFile = createFileDialog();
                    if(newFile == null) {
                        main.setActionActiveFlage();
                        return;
                    }
                    main.textFileArea(newFile);
                } 
            }
        );

        //Go Back Button Controller
        main.getGoBack().setOnAction(
            (e) -> {
                main.setActionActiveFlage();
                
                Utils.decrementTextFileID(); //revert text file ID
                
                main.setActiveFiletoNull();
                main.returnToMainArea();
            }
        );

        //Save Button Controller
        main.getSave().setOnAction(
            (e) -> {
                main.setActionActiveFlage();
                
                TextFile activeFile = main.getActiveFile();
                TextArea textArea = main.getTextArea();
                String content = textArea.getText();
                activeFile.saveContent(content);
                Utils.allTextFiles.add(activeFile); //add to global list (also updates version)
                
                Writer writer = (Writer) main.getUser();
                writer.setHasNewFile(); //set new file flag for writer
                
                main.setActiveFiletoNull();
                main.returnToMainArea();

            }
        );

        
    }

    private void formLogic(){
        main.getFirstName().setOnAction((e) -> main.getLastName().requestFocus());
        main.getLastName().setOnAction((e) -> main.getUsername().requestFocus());
        main.getUsername().setOnAction((e) -> main.getPassword().requestFocus());
        main.getPassword().setOnAction((e) -> RunFormLogic());
        main.getSubmit().setOnAction((e) -> RunFormLogic());
        //Custom multiple selection logic for categories list
        main.getCategoriesList().setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                lv.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (lv.getSelectionModel().getSelectedIndices().contains(index)) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                    event.consume();
                }
            });

            return cell;
        });
    }

    private void RunFormLogic(){

        String first,last,username,password,role; 
        first = main.getFirstName().getText();
        last = main.getLastName().getText();
        username = main.getUsername().getText();
        password = main.getPassword().getText();
        ArrayList<String> selectedCategories = new ArrayList<>(main.getCategoriesList().getSelectionModel().getSelectedItems());
        Toggle selectedRole = main.getRoleGroup().getSelectedToggle();
        ToggleButton selectedButton = (ToggleButton) selectedRole;
        role = selectedButton.getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("Error");
        if(first.equals("")) alert.setContentText("First name is missing!");
        else if(last.equals("")) alert.setContentText("Last name is missing!");
        else if(username.equals("")) alert.setContentText("Username is missing!");
        else if(password.equals("")) alert.setContentText("Password is missing!");
        else if(selectedCategories.isEmpty() && !role.equals("Admin")) alert.setContentText("Please select at least one category!");
        else {
           System.out.println("The form is valid");
           main.returnToMainArea();
           main.setActionActiveFlage();
           Admin admin = (Admin) main.getUser();
           if(role.equals("Admin")) Utils.addNewUser(new Admin(first, last, username, password, Utils.generateUserID(), selectedCategories));
           else if(role.equals("Writer")) Utils.addNewUser(new Writer(first,last, username, password, Utils.generateUserID(), selectedCategories));
           else Utils.addNewUser(new User(first, last, username, password, Utils.generateUserID(), selectedCategories)); 
           
           admin.setNewUsersFlag();
           //clear form fields
           main.getFirstName().setText("");
           main.getLastName().setText("");
           main.getUsername().setText("");
           main.getPassword().setText("");
           main.getCategoriesList().getSelectionModel().clearSelection();
           
           return;
        }
        alert.showAndWait();
    }

    private void CategoryLogic(){
        String selected = main.getCategories().getValue();
        if(selected.equals("Create Category...")){
            System.out.println("Create category clicked");
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Category");
            dialog.setHeaderText("Create a new category");
            dialog.setContentText("Category name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                String category = name.trim();
                boolean exists = Utils.allCategories.contains(category);
                if (!name.trim().isEmpty() && !exists) {
                    ObservableList<String> items = main.getCategories().getItems();
                    items.add(items.size() -1 , name); // add to combo box
                    main.getCategories().setValue(name);       // auto-select it
                    Admin admin = (Admin) main.getUser();
                    admin.setChangedCategoriesFlag();           // inform admin newCategory flag                  
                    admin.getCategories().add(category);          // add to admin categories list so it can be visible
                    Utils.allCategories.add(name);                     // add to global categories list
                } else if (exists){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message");
                    alert.setHeaderText("Error");
                    alert.setContentText("Category already exists!");
                    alert.showAndWait();
                    main.getCategories().setValue("All");    
                }
            });
            
        } else {
            System.out.println("Selected category: " + selected);
            //TODO : filter content by category
        }
            
    }

    private TextFile createFileDialog(){
        Dialog<TextFile> dialog = new Dialog<>();
        dialog.setTitle("Create File");
        dialog.setHeaderText("Create a new file");

        ButtonType createButtonType = new ButtonType("New", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");


        ComboBox<String> categoryBox = new ComboBox<>();
        for(String cat : main.getUser().getCategories()){
            categoryBox.getItems().add(cat);
        }
        categoryBox.setPromptText("Category");
        categoryBox.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryBox, 1, 1);

        dialog.getDialogPane().setContent(grid);
        // Disable Create until valid
        Node createButton = dialog.getDialogPane()
                .lookupButton(createButtonType);
        createButton.setDisable(true);

        // Re-check whenever name OR category changes
        ChangeListener<String> validator = (obs, oldVal, newVal) ->
                createButton.setDisable(!isFileFormValid(nameField, categoryBox));

        nameField.textProperty().addListener(validator);
        categoryBox.valueProperty().addListener((obs, oldVal, newVal) ->
            createButton.setDisable(!isFileFormValid(nameField, categoryBox))
        );

        // Focus name field
        Platform.runLater(nameField::requestFocus);
        
        dialog.setResultConverter(button -> {
            if (button == createButtonType) {
                return new TextFile(
                        Utils.generateTextFileID(),
                        nameField.getText().trim(),
                        main.getUser().getName(),
                        categoryBox.getValue(),
                        0
                    );
                }
                return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private boolean isFileFormValid(TextField nameField, ComboBox<String> categoryBox) {
        return !nameField.getText().trim().isEmpty()
            && (categoryBox.getValue() != null);
    }
}
