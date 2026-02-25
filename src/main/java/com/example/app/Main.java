package com.example.app;

import java.util.ArrayList;

import com.example.app.models.Admin;
import com.example.app.models.User;
import com.example.app.services.JsonService;

public class Main {
    public static void main(String [] args){
        JsonService jsoncontroller = new JsonService();
        
        ResetApp.checkReset(jsoncontroller);  //check if app needs a reset 
        try{
            User admin = jsoncontroller.JsonSearchUseronId(3);
            System.out.println(admin.getName());
            ArrayList<String> cat = admin.getCategories();
            for(String s : cat) System.out.println(s);
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
