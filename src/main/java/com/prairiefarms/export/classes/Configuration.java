package com.prairiefarms.export.classes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Configuration {

    static Properties configuration;

    Configuration(){
        try {
            if(configuration == null) {
                configuration = new Properties();
//                configuration.load(new FileInputStream("/java/DS_EXPORT.properties")); //for IBM i
                configuration.load(new FileInputStream("D:\\DS_EXPORT.properties"));       // for local developer machine
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String lookupKey){
        String returnMe = "";
        String lookupResult = configuration.getProperty(lookupKey);
        if(lookupResult != null){
            returnMe = lookupResult;
        }
        return returnMe;
    }

    public List getList(String lookupKey){
        List returnMe = new ArrayList();

        String[] lookupResult = getProperty(lookupKey).split(" ");
        if(!Arrays.asList(lookupResult).isEmpty()){
            returnMe = Arrays.asList(lookupResult);
        }
        return returnMe;
    }

}
