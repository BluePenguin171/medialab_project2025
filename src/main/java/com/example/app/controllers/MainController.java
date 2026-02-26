package com.example.app.controllers;

import java.util.Optional;

import com.example.app.App;
import com.example.app.Utils;
import com.example.app.screens.MainScreen;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;

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
                if(!main.getAddUserActive()){
                    main.setAddUserActive();
                    System.out.println("Button pressed");
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
    }

    private void RunFormLogic(){
        String first,last,username,password; 
        first = main.getFirstName().getText();
        last = main.getLastName().getText();
        username = main.getUsername().getText();
        password = main.getPassword().getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Messege");
        alert.setHeaderText("Error");
        if(first.equals("")) alert.setContentText("First name is missing!");
        else if(last.equals("")) alert.setContentText("Last name is missing!");
        else if(username.equals("")) alert.setContentText("Username is missing!");
        else if(password.equals("")) alert.setContentText("Password is missing!");
        else {
           System.out.println("The form is valid");
           main.returnToMainArea();
           main.setAddUserActive();
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
                    main.getUser().addNewCategory(name);                 // add to user's new categories
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
