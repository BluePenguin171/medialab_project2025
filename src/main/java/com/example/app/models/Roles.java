package com.example.app.models;

enum ROLES {
    USER(1),
    WRITER(2),
    ADMIN(3);

    private int descriptor; 
    private ROLES(int descriptor){
        this.descriptor = descriptor;
    }

    public int getDescriptor(){
        return descriptor;
    }

    @Override
    public String toString() {
        return name();
    }
}