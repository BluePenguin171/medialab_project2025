package com.example.app;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.app.models.Admin;
import com.example.app.models.TextFile;
import com.example.app.models.User;
import com.example.app.models.Writer;
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
        closingEvent();
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

    public void closingEvent(){
        stage.setOnCloseRequest(event -> {
            if(active_user != null){
                try{
                    if(active_user instanceof Writer){
                        Writer writer = (Writer) active_user;
                        if(writer.hasNewFile()){
                            ArrayList<HashMap<String, Object>> jsonFiles = TextFile.createToJson(Utils.allTextFiles);
                            FileWriter fileWriter = new FileWriter(Utils.TEXT_JSON_FILE);
                            jsoncontroller.writeJsonFile(fileWriter, jsonFiles,"id","title","author","category","version","lastModified");
                            fileWriter.close();
                        }
                    }

                    if(active_user instanceof Admin){ 
                        Admin admin = (Admin) active_user;
                        if(admin.getNewUsersFlag()){
                            ArrayList<HashMap<String, Object>> jsonUsers = User.createToJson(Utils.allUsers);
                            FileWriter userFileWriter = new FileWriter(Utils.USERS_JSON_FILE);
                            FileWriter passkeyFileWriter = new FileWriter(Utils.PASSKEY_JSON_FILE);
                            jsoncontroller.writeJsonFile(userFileWriter, jsonUsers,"id","name","username","password","role","categories","watchlist");
                            jsoncontroller.writeJsonFile(passkeyFileWriter, jsonUsers, "username", "password", "id");
                            userFileWriter.close();
                            passkeyFileWriter.close();
                        }

                        if(admin.getChangedCategoriesFlag()){
                            FileWriter utilsFileWriter = new FileWriter(Utils.UTILS_JSON_FILE);
                            HashMap<String, Object> utilsJson = new HashMap<>();
                            utilsJson.put("categories", Utils.allCategories);
                            jsoncontroller.writeJsonFile(utilsFileWriter, utilsJson, "categories");
                            utilsFileWriter.close();
                        }
                    System.out.println("App closed, all changes saved.");
                    } else {
                        System.out.println("App closed, no changes to save.");
                    }
                } catch (Exception e){
                    System.out.println("Error saving users on exit: " + e.getMessage());
                }
            }
        });
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


