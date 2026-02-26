package com.example.app.controllers;

import com.example.app.App;
import com.example.app.models.User;
import com.example.app.screens.LoginForm;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    private App app;
    private LoginForm screen;
    

    public LoginController(App app,LoginForm screen){
        this.app = app;
        this.screen = screen;

        initController();
    }

    void initController(){
        //onButtonPressed Action
        screen.getButton().setOnAction((e) -> checkInput(screen.getUsername(),screen.getPassword(),screen.getErrorMessage()));

         //onEnterPressed Actions
        screen.getUsername().setOnAction((e) -> screen.getPassword().requestFocus());
        screen.getPassword().setOnAction((e) -> checkInput(screen.getUsername(),screen.getPassword(),screen.getErrorMessage()));
    }

    private void checkInput(TextField userText,TextField passText, Label error_message){
        String username = userText.getText();
        String password = passText.getText();
        if(username.equals("")) error_message.setText("Username is missing!");
        else if(password.equals("")) error_message.setText("Password is missing!");
        else {
            try{
                User user = app.getJsonController().JsonSearchCredentials(username, password);
                if (user == null) error_message.setText("Username or Password is wrong!");
                else {
                    app.setUser(user);
                    app.revealMainScreen();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        userText.clear();
        passText.clear();
        userText.requestFocus();
    }

}


