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
import javax.json.JsonString;
import javax.json.JsonWriter;


import com.example.app.Utils;
import com.example.app.models.Admin;
import com.example.app.models.User;
import com.example.app.models.Writer;


public class JsonService {

    public void JsonInitFile(List<HashMap<String,Object>>  KVlist, FileWriter file, String... fields) throws Exception{
        //Convert HashMap List to Json Object List
        JsonArrayBuilder jsonDataBuilder = Json.createArrayBuilder();
        
        for(int i =0 ; i<KVlist.size(); i++){
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for(String key : KVlist.get(i).keySet()){
                if(!Arrays.asList(fields).contains(key)) continue; //if the key is not in the requested fields, ignore

                Object value = KVlist.get(i).get(key);
                
                if(value instanceof String) builder.add(key,(String) value);
                else if(value instanceof Integer) builder.add(key,(Integer) value);
                else if (value instanceof ArrayList) {
                    ArrayList<?> list = (ArrayList<?>) value;
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    for (Object item : list) {
                        if (item instanceof String) {
                            arrayBuilder.add((String) item);
                        }
                    }
                    builder.add(key, arrayBuilder);
                }
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

    public void JsonInitFile(FileWriter file) throws Exception{
        JsonObject emptyData = Json.createObjectBuilder().add("all_categories",Json.createArrayBuilder().add("All").build()).build();
        try(
            JsonWriter jsonfile = createJsonFile(file);
        ){
            jsonfile.writeObject(emptyData);
        } catch (Exception e) {
            throw e;
        }
    }

    private JsonWriter createJsonFile(FileWriter file){
        return Json.createWriter(file);
    }

    private User JsonSearchUseronId(int id) throws Exception{
        File jsonFile = new File(Utils.USERS_JSON_FILE);
        try (FileReader fileReader = new FileReader(jsonFile);
             JsonReader jsonReader = Json.createReader(fileReader)) {

            // Read the JSON array from the file
            JsonArray jsonArray = jsonReader.readArray();
            
            //iterate through jsonArray to find requested user
            ArrayList<String> categories;
            int temp_id;
            for(int i =0; i< jsonArray.size(); i++){
                JsonObject j = jsonArray.getJsonObject(i);
                temp_id = j.getInt("user_id"); 
                if(!(temp_id == id)) continue; //ignore if not the requested user
                String role = j.getString("role");
                //fetch categories
                if(role.equals("ADMIN")){
                    categories = new ArrayList<>(Utils.allCategories);
                }
                else{
                    categories = convertJsonArraytoString(j.getJsonArray("categories"));
                }
                
                switch (role) {
                    case "ADMIN":
                        return new Admin(j.getString("name"),j.getString("username"),temp_id,categories);
                    case "WRITER":
                        return new Writer(j.getString("name"),j.getString("username"),temp_id,categories);
                    default:
                        return new User(j.getString("name"),j.getString("username"),temp_id,categories);
                        
                }


            }
            
            throw new Exception("FATAL ERROR: User with the provided id not found!");
        } catch (Exception e){
            throw e;
        }
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
            temp_id = j.getInt("user_id"); 
            if(j.getString("username").equals(username) && j.getString("password").equals(password)) return JsonSearchUseronId(temp_id);
        }
        
        return null;
    } catch (Exception e){
        throw e;
    }
    }

    public ArrayList<String> fetchAllCategories() throws Exception{
        File jsonFile = new File(Utils.CATEGORIES_JSON_FILE);

        try (FileReader fileReader = new FileReader(jsonFile);
            JsonReader jsonReader = Json.createReader(fileReader)) {

            JsonObject jsonObj = jsonReader.readObject();

            ArrayList<String> categories = convertJsonArraytoString(jsonObj.getJsonArray("all_categories"));
            return categories;

        } catch(Exception e){
            throw e;
        }

    }

    private ArrayList<String> convertJsonArraytoString(JsonArray json){
        ArrayList<String> result = new ArrayList<>();
        json.getValuesAs(JsonString.class) // get list of JsonString
            .stream()
            .map(JsonString::getString)    // extract the string value
            .forEach(result::add);          // add to ArrayList
        return result;
    }
}

