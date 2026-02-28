package com.example.app.models;

import java.util.HashMap;

public class TextFilePair{
    public final int fileId;
    public final int version;

    public TextFilePair(int fileId, int version){
        this.fileId = fileId;
        this.version = version;
    }

    static public TextFilePair createFromJson(HashMap<String,Integer> json){
        return new TextFilePair(json.get("fileId"),json.get("version"));
    }
}