package com.example.app.services;


import java.io.FileWriter;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public class JsonService {

    public void JsonInitFile(HashMap<String,Object> [] KVlist, FileWriter file) throws Exception{
        //Convert HashMap List to Json Object List
        JsonArrayBuilder jsonDataBuilder = Json.createArrayBuilder();
        
        for(int i =0 ; i<KVlist.length; i++){
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for(String key : KVlist[i].keySet()){
                Object value = KVlist[i].get(key);
                if(value instanceof String) builder.add(key,(String) value);
                else if(value instanceof Integer) builder.add(key,(Integer) value);
                else throw new IllegalArgumentException("Use only String or Int in JSON Files");
            }
            jsonDataBuilder.add(builder.build());
        }

        JsonArray jsonData = jsonDataBuilder.build();
        
        try(
            JsonWriter jsonfile = createJsonFile(file);
        ){
            jsonfile.writeArray(jsonData);
        } catch (Exception e) {
            throw e;
        }
    }

    private JsonWriter createJsonFile(FileWriter file){
        return Json.createWriter(file);
    }

}

