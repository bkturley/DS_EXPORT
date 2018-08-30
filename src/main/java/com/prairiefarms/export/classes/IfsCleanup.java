package com.prairiefarms.export.classes;

import com.prairiefarms.export.classes.Configuration;

import java.io.File;
import java.util.List;

public class IfsCleanup {

    private Configuration configuration;

    public IfsCleanup(){
        this.configuration = new Configuration();
    }

    IfsCleanup(Configuration configuration){
        this.configuration = configuration;
    }

    public void remove(String reportId){
        //todo:
        List<String> exportFormats = configuration.getList("exportFormats");
        for (String exportFormat : exportFormats){
            File deleteMe = new File(configuration.getProperty("workingDirectory") + reportId + exportFormat);
            deleteMe.delete();
        }
    }
}
