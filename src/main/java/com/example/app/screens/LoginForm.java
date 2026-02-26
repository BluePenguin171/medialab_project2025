package com.example.app.screens;

import com.example.app.App;
import com.example.app.controllers.LoginController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LoginForm {
    private TextField username = new TextField();
    private TextField password = new PasswordField();
    private GridPane grid = new GridPane();
    private Label error_message = new Label();
    private Button login = new Button("Login");
    private VBox screen_layout = new VBox();

    //LoginForm View Setup
    public LoginForm(App app){
        //TextFields
        username.setPromptText("Enter your username...");
        password.setPromptText("Enter your password...");
        username.setAlignment(Pos.CENTER_LEFT);
        password.setAlignment(Pos.CENTER_LEFT);

         //GridSupport for TextFields
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5.5);
        grid.setVgap(6.5);
        grid.setPadding(new Insets(0,10,10,10));
        grid.add(new Label("username:"),0,0);
        grid.add(username,1,0);
        grid.add(new Label("password:"),0,1);
        grid.add(password,1,1);


         // Error message
        error_message.setStyle("-fx-text-fill: red;");
        error_message.setMaxWidth(Double.MAX_VALUE);       //align left corresponding 
        error_message.setAlignment(Pos.CENTER_LEFT);       //to the grid


        screen_layout.setAlignment(Pos.CENTER);
        screen_layout.setPadding(new Insets(10,20,20,10));
        

        screen_layout.getChildren().addAll(
            error_message,
            grid,
            new Region() {{ setPrefHeight(15); }}, //set a gap between grid and login
            login
        );

        VBox.setMargin(grid, new Insets(5, 0, 0, 0));

        new LoginController(app,this); 
    }

    public VBox getScreen(){
        return screen_layout;
    }

    public TextField getUsername(){
        return username;
    }

    public TextField getPassword(){
        return password;
    }

    public Label getErrorMessage(){
        return error_message;
    }

    public Button getButton(){
        return login;
    }
}


