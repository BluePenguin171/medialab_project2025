package com.example.app.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.example.app.Utils;

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


    protected final Name name ;
    protected final String username; 
    protected final String password; 
    protected final ArrayList<String>  Categories;  
    protected ROLES role; 
    protected final int id;
    protected ArrayList<TextFilePair> watchlist = new ArrayList<>();
    
    //constructors
    public User(String first, String last, String username, String password, int id, ArrayList<String> Categories, ArrayList<TextFilePair> watchlist){
        this.name = new Name(first,last); 
        this.username = username;
        this.password = password;
        this.role = ROLES.USER;
        this.id = id;
        this.watchlist = watchlist;
        this.Categories = new ArrayList<String>(Categories);  
    }

    public User(String name, String username, String password, int id, ArrayList<String> Categories, ArrayList<TextFilePair> watchlist){
        this.name = new Name(name); 
        this.username = username;
        this.password = password;
        this.role = ROLES.USER;
        this.id = id;
        this.watchlist = watchlist;
        this.Categories = new ArrayList<String>(Categories); //Copy Categories list  
    }

    public User(String first, String last, String username, String password, int id, ArrayList<String> Categories){
        this(first, last, username, password, id, Categories, new ArrayList<TextFilePair>());
    }

    public User(String name, String username, String password, int id, ArrayList<String> Categories){
        this(name, username, password, id, Categories, new ArrayList<TextFilePair>());
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

    public ArrayList<TextFilePair> getWatchlist() {
        return watchlist;
    }



    //Methods
    public void addToWatchlist(int fileId, int version){
        watchlist.add(new TextFilePair(fileId, version));
    }

    public void removeFromWatchlist(int fileId){
        watchlist.removeIf(pair -> pair.fileId == fileId);
    }

    public HashMap<String, Object> toJson(){
        LinkedHashMap<String, Object> json = new LinkedHashMap<>();
        json.put("id", this.id);
        json.put("name", this.name.toString());
        json.put("username", this.username);
        json.put("password", this.password);
        json.put("role", this.role.toString());
        json.put("categories", this.Categories);
        ArrayList<HashMap<String, Object>> watchlistJson = new ArrayList<>();
        for(TextFilePair pair : watchlist){
            HashMap<String, Object> pairJson = new HashMap<>();
            pairJson.put("fileId", pair.fileId);
            pairJson.put("version", pair.version);
            watchlistJson.add(pairJson);
        }
        json.put("watchlist", watchlistJson);
        return json;
    }

    static public User createFromJson(HashMap<String, Object> json) throws IllegalArgumentException{
        int id = (int) json.get("id");
        String name = (String) json.get("name");
        String username = (String) json.get("username");
        String password = (String) json.get("password");
        String roleStr = (String) json.get("role");
        ROLES role = ROLES.valueOf(roleStr.toUpperCase());
        @SuppressWarnings("unchecked")
        ArrayList<String> categories = (ArrayList<String>) json.get("categories");
        @SuppressWarnings("unchecked")
        ArrayList<TextFilePair> watchlist = (ArrayList<TextFilePair>) json.get("watchlist");
        if(role == ROLES.ADMIN) return new Admin(name, username, password, id, Utils.allCategories, watchlist);
        else if(role == ROLES.WRITER) return new Writer(name, username, password, id, categories, watchlist);
        else if(role == ROLES.USER) return new User(name, username, password, id, categories, watchlist);

        throw new IllegalArgumentException("Invalid role in JSON: " + roleStr);
    }

}
