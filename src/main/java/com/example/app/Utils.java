package com.example.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.app.models.TextFile;
import com.example.app.models.User;
import com.example.app.services.JsonService;

public class Utils {
    private Utils() {}; 


    static public ArrayList<String> allCategories; 
    static public ArrayList<User> allUsers;
    static public ArrayList<TextFile> allTextFiles;
    static private int UseridCounter;
    static private int TextFileIdCounter;

    static public final String DEFAULT_ADMIN_FIRST_NAME = "Bob";
    static public final String DEFAULT_ADMIN_LAST_NAME = "Adminakis";
    static public final String DEFAULT_ADMIN_USERNAME = "medialab";
    static public final String DEFAULT_ADMIN_PASSWORD = "medialab2025";

    static public final String USERS_JSON_FILE = "src/main/medialab/JsonFiles/users.json";
    static public final String PASSKEY_JSON_FILE = "src/main/medialab/JsonFiles/passkey.json";
    static public final String UTILS_JSON_FILE = "src/main/medialab/JsonFiles/utils.json";
    static public final String TEXT_JSON_FILE = "src/main/medialab/JsonFiles/textfiles.json";


    static public final File [] MANDATORY_FILES = {
            new File("src/main/medialab"),
            new File("src/main/medialab/JsonFiles"),
            new File("src/main/medialab/TextFiles"),
            new File(PASSKEY_JSON_FILE),
            new File(USERS_JSON_FILE),
            new File(TEXT_JSON_FILE),
            new File(UTILS_JSON_FILE)
    };

    static public void initUtils(JsonService jsonService){
        getAllCategories(jsonService);
    }

    static public User getUserByID(JsonService jsonService, int id){
        getAllTextFiles(jsonService);
        getAllUsers(jsonService);   //initiallize only if the user provide correct credentials
        for(User user : allUsers){
            if(user.getId() == id) return user;
        }
        return null; 
    }

    static private void getAllCategories(JsonService jsonService){  
        try{
            allCategories = jsonService.readJsonFile(UTILS_JSON_FILE, Utils::createFromJson, "categories");
        } catch (Exception e){
            System.out.println("Error fetching categories: " + e.getMessage());
            allCategories = new ArrayList<>(); // Set to empty array on error
        }
    }

    static private void getAllUsers(JsonService jsonService){  
        try{
            allUsers = jsonService.readJsonFileList(USERS_JSON_FILE, User::createFromJson);
        } catch (Exception e){
            System.out.println("Error fetching users: " + e.getMessage());
            System.exit(1);
        }
    }

    static private void getAllTextFiles(JsonService jsonService){
        try{
            allTextFiles = jsonService.readJsonFileList(TEXT_JSON_FILE, TextFile::createFromJson);
        } catch (Exception e){
            System.out.println("Error fetching text files: " + e.getMessage());
            System.exit(1);
        }
    }

    static private ArrayList<String> createFromJson(HashMap<String, Object> json){
        @SuppressWarnings("unchecked")
        ArrayList<String> categories = (ArrayList<String>) json.get("categories");
        return new ArrayList<>(categories); 
    }

    static public int generateUserID(){
        if(UseridCounter == 0){
            for(User user : allUsers){
                if(user.getId() > UseridCounter) UseridCounter = user.getId();
            }
        }
        return ++UseridCounter;
    }

    static public int generateTextFileID(){
        if(TextFileIdCounter == 0){
            for(TextFile file : allTextFiles){
                if(file.getId() > TextFileIdCounter) TextFileIdCounter = file.getId();
            }
        }
        return ++TextFileIdCounter;
    }

    //In case the text file eventually wont created 
    static public void decrementTextFileID(){
        TextFileIdCounter--;
    }

    static public void addNewUser(User users){
        allUsers.add(users);
    }

}
