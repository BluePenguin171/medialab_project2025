package com.example.app.models;

public class Admin extends Writer{
    public Admin(String first, String last, String username, String password, int id){
        super(first, last, username, password, id, new String[1]);
        this.role = ROLES.ADMIN;
    }

    public Admin(String name,String password, int id, String [] Categories){
        super(name,password,id,Categories);
        this.role = ROLES.ADMIN;
    }

    public User createUser(String first, String last, String username, String password, int id, String [] categories){
        return new User(first,last,username,password, id, categories);
    }
}