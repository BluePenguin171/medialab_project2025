package com.example.app.models;

public class TextFilePair{
    public final int fileId;
    public final int version;

    public TextFilePair(int fileId, int version){
        this.fileId = fileId;
        this.version = version;
    }
}