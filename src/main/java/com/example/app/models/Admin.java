package com.example.app.models;

import java.util.ArrayList;

public class Admin extends Writer{
    public Admin(String first, String last, String username, String password, int id){
        super(first, last, username, password, id, new ArrayList<String>());
        this.role = ROLES.ADMIN;
    }

    public Admin(String name,String password, int id, ArrayList<String> Categories){
        super(name,password,id,Categories);
        this.role = ROLES.ADMIN;
    }

    public User createUser(String first, String last, String username, String password, int id, ArrayList<String> categories){
        return new User(first,last,username,password, id, categories);
    }
}