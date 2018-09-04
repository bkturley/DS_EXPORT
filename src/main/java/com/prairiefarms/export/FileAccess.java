package com.prairiefarms.export;

import java.io.File;

public class FileAccess {
    public File getFile(String filePath){
        return new File(filePath);
    }
}
