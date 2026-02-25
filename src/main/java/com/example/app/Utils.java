package com.example.app;

import java.io.File;

public class Utils {
    private Utils() {}; 

    static public final File [] MANDATORY_FILES = {
            new File("src/main/medialab"),
            new File("src/main/medialab/JsonFiles"),
            new File("src/main/medialab/TextFiles"),
            new File("src/main/medialab/JsonFiles/passkey.json"),
            new File("src/main/medialab/JsonFiles/users.json"),
            new File("src/main/medialab/JsonFiles/categories.json")
    };

    static public final String DEFAULT_ADMIN_FIRST_NAME = "Bob";
    static public final String DEFAULT_ADMIN_LAST_NAME = "Adminakis";
    static public final String DEFAULT_ADMIN_USERNAME = "medialab";
    static public final String DEFAULT_ADMIN_PASSWORD = "medialab2025";

    static public final String USERS_JSON_FILE = "src/main/medialab/JsonFiles/users.json";
    static public final String PASSKEY_JSON_FILE = "src/main/medialab/JsonFiles/passkey.json";
    static public final String CATEGORIES_JSON_FILE = "src/main/medialab/JsonFiles/categories.json";

}
