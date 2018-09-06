package com.prairiefarms.export;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Configuration {

    private static Properties properties;

    public Configuration(){
        this(new Properties());
    }

    Configuration(Properties properties){
        try {
            Configuration.properties = properties;
//            Configuration.properties.load(new FileInputStream("/java/DS_EXPORT.properties")); //for IBM i
            Configuration.properties.load(new FileInputStream("D:\\DS_EXPORT.properties"));   // for local developer machine
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

    List getList(String lookupKey){
        List returnMe = new ArrayList();

        String[] lookupResult = getProperty(lookupKey).split(" ");
        if(!Arrays.asList(lookupResult).isEmpty()){
            returnMe = Arrays.asList(lookupResult);
        }
        return returnMe;
    }

}
