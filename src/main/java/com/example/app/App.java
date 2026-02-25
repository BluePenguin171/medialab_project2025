package com.example.app;

import com.example.app.models.User;
import com.example.app.services.JsonService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class App extends Application {
    JsonService jsoncontroller = new JsonService();


    @Override
    public void start(Stage primaryStage) {
//--------------LOGIN_SCENE-------------------------
        //TextFields
        TextField username = new TextField();
        TextField password = new PasswordField();
        username.setPromptText("Enter your username...");
        password.setPromptText("Enter your password...");
        username.setAlignment(Pos.CENTER_LEFT);
        password.setAlignment(Pos.CENTER_LEFT);
        
        //GridSupport for TextFields
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5.5);
        grid.setVgap(6.5);
        grid.setPadding(new Insets(0,10,10,10));
        grid.add(new Label("username:"),0,0);
        grid.add(username,1,0);
        grid.add(new Label("password:"),0,1);
        grid.add(password,1,1);

        
        // Error message
        Label error_message = new Label();
        error_message.setStyle("-fx-text-fill: red;");
        error_message.setMaxWidth(Double.MAX_VALUE);       //align left corresponding 
        error_message.setAlignment(Pos.CENTER_LEFT);       //to the grid

        // Login button
        Button login = new Button("Login");
        

        //Final Layout
        VBox layout1 = new VBox();
        layout1.setAlignment(Pos.CENTER);
        layout1.setPadding(new Insets(10,20,20,10));
        

        layout1.getChildren().addAll(
            error_message,
            grid,
            new Region() {{ setPrefHeight(15); }}, //set a gap between grid and login
            login
        );

        VBox.setMargin(grid, new Insets(5, 0, 0, 0));



        //onButtonPressed Action
        login.setOnAction((e) -> checkInput(username,password,error_message));

        //onEnterPressed Actions
        username.setOnAction((e) -> password.requestFocus());
        password.setOnAction((e) -> checkInput(username,password,error_message));

        Scene scene1 = new Scene(layout1);
        primaryStage.setTitle("MediaLab File Managment System");
        primaryStage.setScene(scene1);
        
        primaryStage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }

    private void checkInput(TextField username,TextField password, Label error_message){
        String user = username.getText();
        String pass = password.getText();
        if(user.equals("")) error_message.setText("Username is missing!");
        else if(pass.equals("")) error_message.setText("Password is missing!");
        else {
            try{
                User U = jsoncontroller.JsonSearchCredentials(user, pass);
                if (U == null) error_message.setText("Username or Password is wrong!");
                else error_message.setText("You got it");
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        username.clear();
        password.clear();
        username.requestFocus();
    }


}


