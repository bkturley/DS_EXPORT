package com.prairiefarms.export.access;

import java.io.File;

public class FileAccess {
    public File getFile(String filePath){
        return new File(filePath);
    }

    public void deleteFile(String filePath){
        getFile(filePath).delete();
    }
}
