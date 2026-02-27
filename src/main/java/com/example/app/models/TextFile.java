package com.example.app.models;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.example.app.Utils;

public final class TextFile{
    private int id;
    private String title;
    private String author;
    private int version;
    private String category;
    private String content = null;

    public TextFile(int id, String title, String author, String category,int version) throws IllegalArgumentException{
        this.id = id;
        this.title = title;
        this.author = author;
        this.version = version;

        //check if category is valid
        for(String cat : Utils.allCategories){
            if(cat.equals(category)){
                this.category = category;
                return;
            }
        }

        throw new IllegalArgumentException("Invalid category: " + category);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getVersion() {
        return version;
    }

    public String getFilePath(int requestedVersion) throws IllegalArgumentException{
        if(requestedVersion >= 0 && requestedVersion <= version){
            return "src/main/medialab/TextFiles/"+title + "_v" + requestedVersion + ".txt";
        }
        throw new IllegalArgumentException("Requested version " + requestedVersion + " is out of bounds for file " + title);
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public void incrementVersion() {
        this.version++;
    }

    public String getContent(){
        return getContent(this.version);
    }

    public String getContent(int requestedVersion) {
        try(
            FileReader fileReader = new FileReader(this.getFilePath(requestedVersion));
        )
        {
            StringBuilder sb = new StringBuilder();
            int c;
            while((c = fileReader.read()) != -1){
                sb.append((char)c);
            }
            content = sb.toString();
            return content;

        } catch(Exception e){
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }

        
    }

    public void saveContent(String newContent) {
        incrementVersion();
        try(
            FileWriter fileWriter = new FileWriter(this.getFilePath(this.version));
        ) 
        {
            fileWriter.write(newContent);
            content = newContent; // Update the content in memory
        } catch (Exception e) {
            throw new RuntimeException("Error writing file: " + e.getMessage());
        }
    }

    public HashMap<String, Object> toJson(){
        LinkedHashMap<String, Object> json = new LinkedHashMap<>();
        json.put("id", this.id);
        json.put("title", this.title);
        json.put("author", this.author);
        json.put("category", this.category);
        json.put("version", this.version);
        return json;
    }
    
    static public TextFile createFromJson(HashMap<String, Object> json){
        int id = (int) json.get("id");
        String title = (String) json.get("title");
        String author = (String) json.get("author");
        String category = (String) json.get("category");
        int version = (int) json.get("version");
        return new TextFile(id,title, author, category, version);
    }

    
}