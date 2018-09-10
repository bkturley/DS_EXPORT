package com.prairiefarms.export;
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

    public void remove(List<String> filePaths){

        for(String filePath : filePaths){
            File deleteMe = new File(filePath);
            deleteMe.delete();
        }
    }
}
