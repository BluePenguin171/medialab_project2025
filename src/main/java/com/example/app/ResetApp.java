package com.example.app;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.app.models.Admin;
import com.example.app.services.JsonService;

public class ResetApp {
    static private JsonService jsonController; 
    
    private ResetApp(){} 

    public static void checkReset(JsonService jsoncontroller){    
        jsonController = jsoncontroller;
        final File [] mandatory_files = {
            new File("src/main/medialab"),
            new File("src/main/medialab/JsonFiles"),
            new File("src/main/medialab/TextFiles"),
            new File("src/main/medialab/JsonFiles/passkey.json"),
            new File("src/main/medialab/JsonFiles/users.json"),
        };

        boolean init_flag = false;
        for(File f : mandatory_files){
            if(!f.exists()){
                init_flag = true;
            } 
        }

        /*
        TODO : create a prompt window that informs user for initiallization
        */

        if(init_flag){
            createAppFiles(mandatory_files);
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
        Admin admin = new Admin(Utils.DEFAULT_ADMIN_FIRST_NAME,Utils.DEFAULT_ADMIN_LAST_NAME,Utils.DEFAULT_ADMIN_USERNAME,Utils.DEFAULT_ADMIN_PASSWORD,1);

        List<HashMap<String, Object>> admins = new ArrayList<>();
        admins.add(admin.toHashMap());

        //writing the Json Object to the File
        try(
            FileWriter passkeyfile = new FileWriter(Utils.PASSKEY_JSON_FILE);
            FileWriter usersfile= new FileWriter(Utils.USERS_JSON_FILE);
        )
        {
            jsonController.JsonInitFile(admins , passkeyfile, "username","password","user_id");
            jsonController.JsonInitFile(admins, usersfile, "user_id","name","username","role");
        } catch (Exception e){
            throw e;
        }
    }
}
