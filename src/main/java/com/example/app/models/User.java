package com.example.app.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class User{
    /* User SubClass for Name  */
    private static class Name{
        private String first;
        private String last; 

        public Name(String first, String last){
            this.first = first;
            this.last = last;
        }

        public Name(String name) {  //create a name from a single string
            String[] parts = name.split(" ", 2); 
            this.first = parts[0];
            if (parts.length > 1) {
                this.last = parts[1];
            } else {
                this.last = ""; 
            }
        }

        @Override
        public String toString(){
            return first + " " + last;
        }
    }

    protected Name name ;
    protected String username; 
    protected String password; 
    protected ArrayList<String>  Categories;  
    protected ROLES role; 
    protected int id;
    protected ArrayList<String> newCategories = new ArrayList<String>();
    //TODO :  Watchlist ?     

    public User(String first, String last, String username, String password, int id, ArrayList<String> Categories){
        this.name = new Name(first,last); 
        this.username = username;
        this.password = password;
        this.role = ROLES.USER;
        this.id = id;
        this.Categories = new ArrayList<String>(Categories); //Copy Categories list  
    }

    public User(String name, String username, int id, ArrayList<String> Categories){
        this.name = new Name(name); 
        this.username = username;
        this.password = null;
        this.role = ROLES.USER;
        this.id = id;
        this.Categories = new ArrayList<String>(Categories); //Copy Categories list  
    }

    //getters 
    public String getName(){
        return name.toString();
    }

    public String getUsername(){
        return username;
    }

    public ArrayList<String> getCategories(){
        return Categories;
    }

    public int getRoleDescriptor(){
        return role.getDescriptor(); 
    }

    public String getRole(){
        String str = role.toString();
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase(); //convert for UI
    }

    public int getId(){
        return id;
    }


    public ArrayList<String> getNewCategories(){
        return newCategories;
    }

    public void addNewCategory(String cat){
        newCategories.add(cat);
    }



    //Methods
    public void removeCategory(String cat){
        this.Categories.remove(cat); 
    }

    public HashMap<String, Object> toHashMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("user_id", id);
        map.put("name", this.name.toString());
        map.put("username", this.username);
        map.put("password", this.password);
        map.put("role", this.role.toString());
        //TODO : map.put("Categories", this.Categories);
        return map;
    }


}