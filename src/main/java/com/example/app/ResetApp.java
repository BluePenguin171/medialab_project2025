package com.example.app;

import java.io.File;
import java.io.FileWriter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonWriter;

public class ResetApp {
    private ResetApp(){} 

    public static void checkReset(){    
        File [] mandatory_files = {
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
            
            System.out.println("Creating passkey.json...");
            createInitialPasskeyFile();
            
            System.out.println("Creating users.json...");
            createInitialUsersFile();
            
            System.out.println("++++++Initiallization has been complete!++++++");
        } catch (Exception e){
            System.out.println("Something went VERY WRONG while Initiallizing the app!");
            e.printStackTrace();
            System.exit(-1);     
        }
    }

    static private void createInitialPasskeyFile() throws Exception{
         JsonObject admin_passkey = Json.createObjectBuilder()
        .add("username",Utils.DEFAULT_ADMIN_USERNAME)
        .add("password",Utils.DEFAULT_ADMIN_PASSWORD)
        .add("user_index",1)
        .build();

        JsonArray array_passkey = Json.createArrayBuilder().add(admin_passkey).build();

        //writing the Json Object to the File
        try(
            FileWriter passkeyfile = new FileWriter("src/main/medialab/JsonFiles/passkey.json");
            JsonWriter passkeywriter = Json.createWriter(passkeyfile);
        )
        {
            passkeywriter.writeArray(array_passkey);
        } catch (Exception e){
            throw e;
        }
    }

    static private void createInitialUsersFile() throws Exception{
        JsonArray array_users = Json.createArrayBuilder().build();

        //writing the Json Object to the File
        try(
            FileWriter passkeyfile = new FileWriter("src/main/medialab/JsonFiles/users.json");
            JsonWriter passkeywriter = Json.createWriter(passkeyfile);
        )
        {
            passkeywriter.writeArray(array_users);
        } catch (Exception e){
            throw e;
        }
    }
}
