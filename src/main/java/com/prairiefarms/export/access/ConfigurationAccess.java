package com.prairiefarms.export.access;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationAccess {

    private static String productionConfigFilePath = "/java/ds_export/DS_EXPORT.properties";
    private static String developmentConfigFilePath = "D:/ds_export/DS_EXPORT.properties";
    private Properties properties;

    public ConfigurationAccess(){
        this(new Properties());
    }

    ConfigurationAccess(Properties properties){
        this.properties = properties;
        if(properties.isEmpty()){
            try {
                this.properties.load(new FileInputStream(productionConfigFilePath));
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    this.properties.load(new FileInputStream(developmentConfigFilePath));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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
