package com.example.app;

import com.example.app.models.User;
import com.example.app.screens.LoginForm;
import com.example.app.screens.MainScreen;
import com.example.app.services.JsonService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {
    private JsonService jsoncontroller = new JsonService();
    private LoginForm login_screen;
    private MainScreen main_screen;
    private User active_user;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("MediaLab Documents");


        ResetApp.checkReset(jsoncontroller); 
        Utils.initUtils(jsoncontroller);

        revealLoginForm();
    }


    public void revealLoginForm(){
        login_screen = new LoginForm(this);
        Scene scene = new Scene(login_screen.getScreen());
        stage.setScene(scene);
        
        stage.show();
    }

    public void revealMainScreen(){
        main_screen = new MainScreen(this,active_user);
        Scene scene = new Scene(main_screen.getScreen(),850,600);
        stage.setScene(scene);
        
        stage.show();
    }

    public JsonService getJsonController(){
        return jsoncontroller;
    }

    public User getActiveUser(){
        return active_user;
    }

    public void setUser(User user){
        this.active_user = user;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


