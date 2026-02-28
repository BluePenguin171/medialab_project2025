package com.example.app.models;

import java.util.ArrayList;

public class Writer extends User{

    private boolean hasNewFile = false;

    //constructors
    public Writer(String first, String last, String username, String password, int id, ArrayList<String> Categories, ArrayList<TextFilePair> watchlist){
        super(first, last, username, password, id, Categories, watchlist);
        this.role = ROLES.WRITER;
    }

    public Writer(String name,String username, String password, int id, ArrayList<String> Categories, ArrayList<TextFilePair> watchlist){
        super(name,username, password, id, Categories, watchlist);
        this.role = ROLES.WRITER;
    }

    public Writer(String first, String last, String username, String password, int id, ArrayList<String> Categories){
        this(first, last, username, password, id, Categories, new ArrayList<TextFilePair>());
    }

    public Writer(String name, String username, String password, int id, ArrayList<String> Categories){
        this(name, username, password, id, Categories, new ArrayList<TextFilePair>());
    }

    public boolean hasNewFile() {
        return hasNewFile;
    }

    public void setHasNewFile() {
        this.hasNewFile = true;
    }
}