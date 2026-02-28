package com.example.app.services;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;
import javax.json.JsonWriter;


import com.example.app.Utils;
import com.example.app.models.FromJson;
import com.example.app.models.TextFilePair;
import com.example.app.models.User;



public class JsonService{

    public void writeJsonFile(FileWriter filepath, ArrayList<HashMap<String,Object>> map,String ... fields) throws Exception{
        JsonArray jsonData = objectlistToJson(map, fields);
        try(
            JsonWriter jsonfile = Json.createWriter(filepath);
        ){
            jsonfile.writeArray(jsonData);
        } catch (Exception e) {
            throw new Exception("FATAL ERROR: Failed to write to file " + filepath);
        }
     }
    
    public void writeJsonFile(FileWriter filepath, HashMap<String,Object> map,String ... fields) throws Exception{
        JsonObject jsonData = objectToJson(map, fields);
        try(
            JsonWriter jsonfile = Json.createWriter(filepath);
        ){
            jsonfile.writeObject(jsonData);
        } catch (Exception e) {
            throw new Exception("FATAL ERROR: Failed to write to file " + filepath);
        }
     }

    public <T> T readJsonFile(String filepath, FromJson<T> object, String... fields) throws Exception{
        File jsonFile = new File(filepath);
        try (FileReader fileReader = new FileReader(jsonFile);
            JsonReader jsonReader = Json.createReader(fileReader)) {
            JsonObject jsonObject = jsonReader.readObject();
            return object.createFromJson(jsonToObject(jsonObject, fields));

        } catch (Exception e){
            throw new Exception("FATAL ERROR: Failed to read from file " + filepath);
        }
    }
    
    public <T> ArrayList<T> readJsonFileList(String filepath, FromJson<T> object, String... fields) throws Exception{
                
        File jsonFile = new File(filepath);
        try (FileReader fileReader = new FileReader(jsonFile);
            JsonReader jsonReader = Json.createReader(fileReader)) {
            JsonArray jsonArray = jsonReader.readArray();
            ArrayList<T> list = new ArrayList<>();
            List<HashMap<String,Object>> mapList = jsonToObjectList(jsonArray, fields);
            for(HashMap<String,Object> map : mapList){
                list.add(object.createFromJson(map));
            }
            return list;

        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("FATAL ERROR: Failed to read from file " + filepath);
        }
    }

    private JsonArray objectlistToJson(List<HashMap<String,Object>> KVlist, String... fields){
        JsonArrayBuilder jsonDataBuilder = Json.createArrayBuilder();
        
        for(int i =0 ; i<KVlist.size(); i++){
            jsonDataBuilder.add(objectToJson(KVlist.get(i), fields));
        }

        return jsonDataBuilder.build();
     }

    private JsonObject objectToJson(HashMap<String,Object> map, String... fields){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for(String key : map.keySet()){
            if(!Arrays.asList(fields).contains(key)) continue; //if the key is not in the requested fields, ignore
            Object value = map.get(key);
            if(value instanceof String) builder.add(key,(String) value);
            else if(value instanceof Integer) builder.add(key,(Integer) value);
            else if(value instanceof ArrayList) {
                ArrayList<?> list = (ArrayList<?>) value;
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Object item : list) {
                    if (item instanceof String) {
                        arrayBuilder.add((String) item);
                    }
                    else if (item instanceof Integer) {
                        arrayBuilder.add((Integer) item);
                    }
                    else if(item instanceof HashMap){
                        TextFilePair pair = TextFilePair.createFromJson((HashMap<String, Integer>) item);
                        arrayBuilder.add(Json.createArrayBuilder()
                            .add(pair.fileId)
                            .add(pair.version)
                            .build());
                    }
                    else throw new IllegalArgumentException("Use only String or Int in JSON Files");
                }
                builder.add(key, arrayBuilder);
            }
            else throw new IllegalArgumentException("Use only String or Int in JSON Files");
        }

        return builder.build();
    }

    private HashMap<String,Object> jsonToObject(JsonObject json, String... fields){
        HashMap<String,Object> map = new HashMap<>();
        boolean ignoreFields = fields.length == 0; //if no fields are provided, ignore the field filtering and fetch all fields
        for(String key : json.keySet()){
            if(!ignoreFields && !Arrays.asList(fields).contains(key)) continue; //if the key is not in the requested fields, ignore
            if(json.get(key).getValueType() == ValueType.STRING) map.put(key, json.getString(key));
            else if(json.get(key).getValueType() == ValueType.NUMBER) map.put(key, json.getInt(key));
            else if(json.get(key).getValueType() == ValueType.ARRAY){
                ArrayList<Object> list = new ArrayList<>();
                JsonArray jsonArray = json.getJsonArray(key);
                for(int i =0; i< jsonArray.size(); i++){
                    if(jsonArray.get(i).getValueType() == ValueType.STRING) list.add(jsonArray.getString(i));
                    else if(jsonArray.get(i).getValueType() == ValueType.NUMBER) list.add(jsonArray.getInt(i));
                    else if(jsonArray.get(i).getValueType() == ValueType.ARRAY){
                        JsonArray pairArray = jsonArray.getJsonArray(i);
                        if(pairArray.size() != 2 || pairArray.get(0).getValueType() != ValueType.NUMBER || pairArray.get(1).getValueType() != ValueType.NUMBER) throw new IllegalArgumentException("Invalid TextFilePair format in JSON file");
                        list.add(new TextFilePair(pairArray.getInt(0), pairArray.getInt(1)));
                    }
                    else throw new IllegalArgumentException("Use only String or Int in JSON Files");
                }
                map.put(key, list);
            }
            else throw new IllegalArgumentException("Use only String or Int in JSON Files");
        }
        return map;
    }

    private List<HashMap<String,Object>> jsonToObjectList(JsonArray jsonArray, String... fields){
        List<HashMap<String,Object>> list = new ArrayList<>();
        for(int i =0; i< jsonArray.size(); i++){
            if(jsonArray.get(i).getValueType() != ValueType.OBJECT) throw new IllegalArgumentException("Something wen very wrong with the JSON file...suggest checking the file content");
            list.add(jsonToObject(jsonArray.getJsonObject(i), fields));
        }
        return list;
    }

    public User JsonSearchCredentials(String username,String password) throws Exception{
         File jsonFile = new File(Utils.PASSKEY_JSON_FILE);
        try (FileReader fileReader = new FileReader(jsonFile);
            JsonReader jsonReader = Json.createReader(fileReader)) {

        // Read the JSON array from the file
        JsonArray jsonArray = jsonReader.readArray();
        
        //iterate through jsonArray to find requested user
        int temp_id;
        for(int i =0; i< jsonArray.size(); i++){
            JsonObject j = jsonArray.getJsonObject(i);
            temp_id = j.getInt("id"); 
            if(j.getString("username").equals(username) && j.getString("password").equals(password)) 
                return Utils.getUserByID(this,temp_id);
        }
        
        return null;
        } catch (Exception e){
            throw e;
        }
    }
}
