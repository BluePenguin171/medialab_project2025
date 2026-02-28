package com.example.app;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.example.app.models.Admin;
import com.example.app.services.JsonService;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ResetApp {
    static private JsonService jsonController; 
    
    private ResetApp(){} 

    public static void checkReset(JsonService jsoncontroller){    
        jsonController = jsoncontroller;
        boolean init_flag = false;
        for(File f : Utils.MANDATORY_FILES){
            if(!f.exists()){
                init_flag = true;
            } 
        }

        if(init_flag){
            InitDialog();
            createAppFiles(Utils.MANDATORY_FILES);
        }
    }

    static private void InitDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Initiallization");
        alert.setHeaderText("Initiallization Required");
        alert.setContentText("This is the first time you are running the app or some of the necessary files are missing! Do you want to Initiallize the necessary files and folders (This will overwrite any existing data!) ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return;
        } else {
            // User chose CANCEL or closed the dialog, exit the application
            System.out.println("Application cannot run without initiallization. Exiting...");
            System.exit(0);
        }
    }

    static private void createAppFiles(File [] files){
        try{
            System.out.println("++++++Creating Initial Files++++++");
            System.out.println("Creating Initial Folders...");

            for(int i =0; i<3; i++){             //Creating necessary folders
                if(!files[i].exists() && !files[i].mkdir()) throw new Exception ("Mandatory folders couldn't be created");
            }
            
            System.out.println("Creating json files...");
            createInitialJsonFiles();
        
            
            System.out.println("++++++Initiallization has been complete!++++++");
        } catch (Exception e){
            System.out.println("Something went VERY WRONG while Initiallizing the app!");
            e.printStackTrace();
            System.exit(-1);     
        }
    }

    static private void createInitialJsonFiles() throws Exception{
        Admin DEFAULT_ADMIN = new Admin(Utils.DEFAULT_ADMIN_FIRST_NAME,Utils.DEFAULT_ADMIN_LAST_NAME,Utils.DEFAULT_ADMIN_USERNAME,Utils.DEFAULT_ADMIN_PASSWORD,1,new ArrayList<>());
        ArrayList<HashMap<String,Object>> DEFAULT_ADMIN_WRAPPER = new ArrayList<HashMap<String,Object>>();
        DEFAULT_ADMIN_WRAPPER.add(DEFAULT_ADMIN.toJson());  //The users.json file should be an array of users 
        //writing the Json Object to the File
        HashMap<String, Object> DEFAULT_UTILS = new HashMap<>();
        DEFAULT_UTILS.put("categories", new ArrayList<String>());
        
        try(
            FileWriter passkeyfile = new FileWriter(Utils.PASSKEY_JSON_FILE);
            FileWriter usersfile= new FileWriter(Utils.USERS_JSON_FILE);
            FileWriter categoriesfile = new FileWriter(Utils.UTILS_JSON_FILE);
        )
        {
            jsonController.writeJsonFile(passkeyfile, DEFAULT_ADMIN_WRAPPER, "username","password","id");
            jsonController.writeJsonFile(usersfile,DEFAULT_ADMIN_WRAPPER, "id","name","username","password","role","categories","watchlist");
            jsonController.writeJsonFile(categoriesfile, DEFAULT_UTILS, "categories"); 
        } catch (Exception e){
            throw e;
        }
    }
}
