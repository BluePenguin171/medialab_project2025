package com.example.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.app.models.User;
import com.example.app.services.JsonService;

public class Utils {
    private Utils() {}; 


    static public ArrayList<String> allCategories; 
    static public ArrayList<User> allUsers;
    static private int idCounter;

    static public final File [] MANDATORY_FILES = {
            new File("src/main/medialab"),
            new File("src/main/medialab/JsonFiles"),
            new File("src/main/medialab/TextFiles"),
            new File("src/main/medialab/JsonFiles/passkey.json"),
            new File("src/main/medialab/JsonFiles/users.json"),
            new File("src/main/medialab/JsonFiles/utils.json")
    };

    static public final String DEFAULT_ADMIN_FIRST_NAME = "Bob";
    static public final String DEFAULT_ADMIN_LAST_NAME = "Adminakis";
    static public final String DEFAULT_ADMIN_USERNAME = "medialab";
    static public final String DEFAULT_ADMIN_PASSWORD = "medialab2025";

    static public final String USERS_JSON_FILE = "src/main/medialab/JsonFiles/users.json";
    static public final String PASSKEY_JSON_FILE = "src/main/medialab/JsonFiles/passkey.json";
    static public final String UTILS_JSON_FILE = "src/main/medialab/JsonFiles/utils.json";


    static public void initUtils(JsonService jsonService){
        getAllCategories(jsonService);
    }

    static public User getUserByID(JsonService jsonService, int id){
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

    static private ArrayList<String> createFromJson(HashMap<String, Object> json){
        ArrayList<String> categories = (ArrayList<String>) json.get("categories");
        return new ArrayList<>(categories); 
    }

    static public int generateID(){
        if(idCounter == 0){
            for(User user : allUsers){
                if(user.getId() > idCounter) idCounter = user.getId();
            }
        }
        return ++idCounter;
    }

}

/*
  static public User createFromJson(HashMap<String, Object> json){
        int id = (int) json.get("id");
        String name = (String) json.get("name");
        String username = (String) json.get("username");
        String password = (String) json.get("password");
        String roleStr = (String) json.get("role");
        ROLES role = ROLES.valueOf(roleStr.toUpperCase());
        ArrayList<String> categories = (ArrayList<String>) json.get("categories");
        ArrayList<TextFilePair> watchlist = (ArrayList<TextFilePair>) json.get("watchlist");
        User user = new User(name, username, password, id, categories, watchlist);
        user.role = role; 
        return user;
    }
*/