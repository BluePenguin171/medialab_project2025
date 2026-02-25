package com.example.app.models;

public class Writer extends User{
    public Writer(String first, String last, String username, String password, int id, String [] Categories){
        super(first, last, username, password, id, Categories);
        this.role = ROLES.WRITER;
    }

    public Writer(String name,String password, int id, String [] Categories){
        super(name,password,id,Categories);
        this.role = ROLES.WRITER;
    }
}