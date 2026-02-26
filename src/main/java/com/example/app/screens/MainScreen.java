package com.example.app.screens;

import com.example.app.App;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MainScreen {
    App app;
    private Label helloWorld = new Label("Hello World");
    private HBox screen = new HBox();
    
    public MainScreen(App app){
        this.app = app;
        screen.getChildren().add(helloWorld);

        System.out.println("Hello " + app.getActiveUser().getName());
    }

    public HBox getScreen(){
        return screen;
    }
}
