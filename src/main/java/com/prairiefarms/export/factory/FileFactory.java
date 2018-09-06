package com.prairiefarms.export.factory;

import java.io.File;

public class FileFactory {
    public File getFile(String filePath){
        return new File(filePath);
    }
}
