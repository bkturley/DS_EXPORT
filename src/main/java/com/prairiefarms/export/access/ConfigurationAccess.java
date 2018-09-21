package com.prairiefarms.export.access;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationAccess {

    //TODO: singleton?

    private static String productionConfigFilePath = "/java/ds_export/DS_EXPORT.properties";
    private static String developmentConfigFilePath = "D:/ds_export/DS_EXPORT.properties";
    private static Properties properties;

    public ConfigurationAccess(){
        if(properties == null){
            this.properties = new Properties();
            try {
                ConfigurationAccess.properties.load(new FileInputStream(productionConfigFilePath));
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    ConfigurationAccess.properties.load(new FileInputStream(developmentConfigFilePath));
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
