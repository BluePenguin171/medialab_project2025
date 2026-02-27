package com.example.app.models;

import java.util.ArrayList;

public class Admin extends Writer{
    private ArrayList<User> newUsers = new ArrayList<User>();
    private boolean hasChangedCategories = false;

    //constructors
    public Admin(String first, String last, String username, String password, int id,  ArrayList<String> categories,ArrayList<TextFilePair> watchlist){
        super(first, last, username, password, id, categories, watchlist);     
        this.role = ROLES.ADMIN;
    }

    public Admin(String name,String username, String password, int id, ArrayList<String> categories,ArrayList<TextFilePair> watchlist){
        super(name,username,password,id,categories, watchlist);
        this.role = ROLES.ADMIN;
    }

    public Admin(String first, String last, String username, String password, int id, ArrayList<String> categories){
        this(first, last, username, password, id, categories, new ArrayList<TextFilePair>());
    }

    public Admin(String name, String username, String password, int id, ArrayList<String> categories){
        this(name, username, password, id, categories, new ArrayList<TextFilePair>());
    }

    public User addNewUser(User newUser){
        this.newUsers.add(newUser);
        return newUser;
    }

    public ArrayList<User> getNewUsers(){
        return this.newUsers;
    }

    public boolean getChangedCategoriesFlag(){
        return hasChangedCategories;
    }

    public void setChangedCategoriesFlag(){
        hasChangedCategories = true;
    }
}