package com.example.app.models;

import java.util.HashMap;

/*
A function interface that helps fetching items 
from jsonObjects and converting them to the correct type.
*/

@FunctionalInterface
public interface FromJson<T>{
    T createFromJson(HashMap<String, Object> map);
}