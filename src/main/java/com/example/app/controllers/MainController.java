package com.example.app.controllers;

import java.util.ArrayList;
import java.util.Optional;

import com.example.app.App;
import com.example.app.Utils;
import com.example.app.models.Admin;
import com.example.app.models.User;
import com.example.app.models.Writer;
import com.example.app.screens.MainScreen;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;


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
                if(!main.getAddUserActive()){
                    main.setAddUserActive();
                    main.createUserForm();
                    formLogic();
                }
            }
        );

        main.getCategories().setOnAction(
            (e) -> CategoryLogic()
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
           main.setAddUserActive();
           Admin admin = (Admin) main.getUser();
           if(role.equals("Admin")) Utils.addNewUser(new Admin(first, last, username, password, Utils.generateID(), selectedCategories));
           else if(role.equals("Writer")) Utils.addNewUser(new Writer(first,last, username, password, Utils.generateID(), selectedCategories));
           else Utils.addNewUser(new User(first, last, username, password, Utils.generateID(), selectedCategories)); 
           
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
}
