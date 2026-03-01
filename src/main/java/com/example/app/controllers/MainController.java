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
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
                    
                    textAreaController();
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
                if(!Utils.allTextFiles.contains(activeFile)) Utils.allTextFiles.add(activeFile);
                

                Writer writer = (Writer) main.getUser();
                writer.setHasNewFile(); //set new file flag for writer
                writer.updateWatchlistVersion(activeFile.getId(), activeFile.getVersion()); //update watchlist version for the file just edited
                

                main.setActiveFiletoNull();
                main.getCategories().setValue("All"); //auto select all categories to see the new file added    
                main.setViewCategories(writer.getCategories());
                main.returnToMainArea();

            }
        );


        //search Bar controller (search based on title or author, if @ is in front of the search term, search based on author, otherwise search based on title)
        main.getSearchBar().textProperty().addListener(
            (observable, oldValue, newValue) -> {
                if(main.getActionActiveFlag()) return;

                //if delete a letter or change a letter, go back based on the category you are looking
                //else keep narrowing it down
                if(!newValue.startsWith(oldValue) || newValue.length() <= oldValue.length()){ 
                    String cat = main.getCategories().getValue();
                    if(cat == "All") main.setViewCategories(main.getUser().getCategories());
                    else main.setViewCategories(cat);
                }

                

                ArrayList<TextFile> searchPool = main.getViewedTextFiles();
                String term = newValue.trim();


                if(term.startsWith("@")){
                    ArrayList<TextFile> results = new ArrayList<>();
                    if(term.substring(1).equals("")){   //i am tired, i know its messy but it works
                        main.setViewTextFiles(searchPool);
                        main.returnToMainArea();
                        return;
                    }
                    if(TextFile.filterBasedOnAuthor(term.substring(1), searchPool, results)){
                        main.setViewTextFiles(results);
                        main.returnToMainArea();
                    }
                    return;
                }

                if(term == ""){ //if term empty, meaning a character has deleted, refresh to search pool (should have changed above)
                    main.setViewTextFiles(searchPool);
                    main.returnToMainArea();
                    return; 
                } 

                ArrayList<TextFile> results = new ArrayList<>();
                if(TextFile.filterBasedOnTitle(term, searchPool, results)){
                    main.setViewTextFiles(results);
                    main.returnToMainArea();
                }
            }
        );

        //Delete Category Controller
        main.getDeleteCategory().setOnMouseClicked(
            (e) -> {
                deleteCategoryDialgo();
            }
        );

        //Delete User Controller
        main.getDeleteUser().setOnMouseClicked(
            (e) -> {
                deleteUserDialog();
            }
        );

        //Logout Controller
        main.getLogoutLabel().setOnMouseClicked(
            (e) -> {
                if(!main.getActionActiveFlag()){
                    try{
                        app.logout();
                    } catch (Exception ex){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Message");
                        alert.setHeaderText("Error");
                        alert.setContentText("An error occurred while logging out: " + ex.getMessage());
                        alert.showAndWait();
                        System.exit(-1);
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message");
                    alert.setHeaderText("Error");
                    alert.setContentText("Please save or go back from the current file before logging out!");
                    alert.showAndWait();
                }
            }
        );
    }

    public void tableController(){
        //Table Controller (if click on title column, open the file)
        if(main.getFileTable() != null){
           

            main.getFileTable().setRowFactory(tv -> {
                TableRow<TextFile> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (!row.isEmpty()) {
                        double clickX = e.getSceneX();
                        if (clickX <= main.getTitleCol().getWidth()) {
                            TextFile file = row.getItem();
                            if(!main.getActionActiveFlag()){
                                main.setActionActiveFlage();
                                main.textFileArea(file);
                            } 
                        } else  if ((clickX > main.getVerionStartPos()) && (clickX < (main.getVerionStartPos() + main.getVersionColumn().getWidth())) && (main.getUser().getRole().equals("Admin") || main.getUser().getRole().equals("Writer"))) {    
                            TextFile file = row.getItem();
                            int requestedVersion = dialogToSelectVersion(file);
                            if(requestedVersion == -1) return; //if user cancels the dialog
                            if(!main.getActionActiveFlag()){
                                main.setActionActiveFlage();
                                main.textFileArea(file, requestedVersion);
                            }
                        }
                    }
                });
                return row;
            });
        }

    }

    public void textAreaController(){
        //TextArea Consume Enter Key To paragraph
        main.getTextArea().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume(); //eat the default Enter

                int caretPos = main.getTextArea().getCaretPosition();
                if (event.isShiftDown()) {
                    // Enter → new paragraph (newline + tab)
                    main.getTextArea().insertText(caretPos, "\n\t");
                } else {
                    // Shift + Enter → simple new line
                    main.getTextArea().insertText(caretPos, "\n");
                }
            }
        });
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
        if(selected.equals("All")){
            main.setViewCategories(main.getUser().getCategories());
            main.returnToMainArea();
        }
        else if(selected.equals("Create Category...")){
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
                    main.updateRighSideLabels();
                } else if (exists){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message");
                    alert.setHeaderText("Error");
                    alert.setContentText("Category already exists!");
                    alert.showAndWait();
                    main.getCategories().setValue("All");    
                }
            });
            
        } 
        else{
            ArrayList<String> selected_cat = new ArrayList<String>();
            selected_cat.add(selected);
            main.setViewCategories(selected_cat);
            main.returnToMainArea();
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

    private void deleteCategoryDialgo(){
        Dialog<TextFile> dialog = new Dialog<>();
        dialog.setTitle("Delete Category");
        dialog.setHeaderText("Delete a category");

        ButtonType createButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

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


        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        // Disable Create until valid
        Node createButton = dialog.getDialogPane()
                .lookupButton(createButtonType);
        createButton.setDisable(true);


        categoryBox.valueProperty().addListener((obs, oldVal, newVal) ->
            createButton.setDisable(!isFileFormValid(categoryBox)) //dummy string because the method checks for empty string but we dont have a name field in this dialog
        );

        
        dialog.setResultConverter(button -> {
            if (button == createButtonType) {
                String categoryToDelete = categoryBox.getValue();
                TextFile.cascadeDeleteCategory(categoryToDelete, main.getUser()); //delete category from all text files that have it
                Admin admin = (Admin) main.getUser();
                admin.getCategories().remove(categoryToDelete); //remove from admin categories list
                admin.setChangedCategoriesFlag();
                admin.setHasNewFile();

                Utils.allCategories.remove(categoryToDelete); //remove from global categories list
                main.getCategories().getItems().remove(categoryToDelete); //remove from combo box
                main.getCategories().setValue("All"); //auto select all to refresh the view
                main.setViewCategories(admin.getCategories()); //refresh view based on remaining categories
                main.returnToMainArea();
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteUserDialog(){
        Dialog<TextFile> dialog = new Dialog<>();
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Delete a user");

        ButtonType createButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        ComboBox<String> UserBox = new ComboBox<>();
        for(User user : Utils.allUsers){
            if(user.getId() != main.getUser().getId()) UserBox.getItems().add(user.getName());
        }
        UserBox.setPromptText("User");
        UserBox.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        grid.add(new Label("User:"), 0, 0);
        grid.add(UserBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        // Disable Create until valid
        Node createButton = dialog.getDialogPane()
                .lookupButton(createButtonType);
        createButton.setDisable(true);


        UserBox.valueProperty().addListener((obs, oldVal, newVal) ->
            createButton.setDisable(!isFileFormValid(UserBox)) //dummy string because the method checks for empty string but we dont have a name field in this dialog
        );

        
        dialog.setResultConverter(button -> {
            if (button == createButtonType) {
                String userToDelete = UserBox.getValue();
                Utils.allUsers.removeIf(user -> user.getName().equals(userToDelete));
                Admin admin = (Admin) main.getUser();
                admin.setNewUsersFlag();
                main.returnToMainArea();
                return null;
            }
            return null;
        });

        dialog.showAndWait();

    }

    private int dialogToSelectVersion(TextFile file){
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Select Version");
        dialog.setHeaderText("Select a version of the file");

        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        ComboBox<String> versionBox = new ComboBox<>();
        int oldestVersion = main.getUser().getRole().equals("Admin") ? 0 : Math.max(file.getVersion()-3,0);
        for(int i = file.getVersion(); i > oldestVersion; i--){
            versionBox.getItems().add("ver " + String.valueOf(i));
        }
        versionBox.setPromptText("Version");
        versionBox.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        grid.add(new Label("Version:"), 0, 0);
        grid.add(versionBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        // Disable Create until valid
        Node createButton = dialog.getDialogPane()
                .lookupButton(selectButtonType);
        createButton.setDisable(true);


        versionBox.valueProperty().addListener((obs, oldVal, newVal) ->
            createButton.setDisable(!isFileFormValid(versionBox)) //dummy string because the method checks for empty string but we dont have a name field in this dialog
        );

        
        dialog.setResultConverter(button -> {
            if (button == selectButtonType) {
                String versionStr = versionBox.getValue();
                int versionNum = Integer.parseInt(versionStr.split(" ")[1]);
                return versionNum;
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();  // Capture the result
        return result.orElse(-1); 
    }

    private boolean isFileFormValid(TextField nameField, ComboBox<String> categoryBox) {
        return !nameField.getText().trim().isEmpty()
            && (categoryBox.getValue() != null);
    }

    private boolean isFileFormValid(ComboBox<String> categoryBox) {
        return (categoryBox.getValue() != null);
    }
}
