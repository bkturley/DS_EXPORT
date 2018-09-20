package com.prairiefarms.export.access;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationAccess {


    private static Properties properties;

    public ConfigurationAccess(){
        this(new Properties());
    }

    public ConfigurationAccess(Properties properties){
        try {
            ConfigurationAccess.properties = properties;
            ConfigurationAccess.properties.load(new FileInputStream("/java/ds_export/DS_EXPORT.properties")); //for IBM i
//            ConfigurationAccess.properties.load(new FileInputStream("D:\\DS_EXPORT.properties"));   // for local developer machine
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String lookupKey){
        String returnMe = "";
        String lookupResult = properties.getProperty(lookupKey);
        if(lookupResult != null){
            returnMe = lookupResult;
        }
        return returnMe;
    }

}
