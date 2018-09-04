package com.prairiefarms.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLineList {
    Configuration configuration = new Configuration();
    FileAccess fileAccess = new FileAccess();
    public List<String> getLines(String textFileName) throws IOException {
        List<String> textLines = new ArrayList<>();
        String nextLine;
        File textFile = fileAccess.getFile(configuration.getProperty("workingDirectory") + textFileName.trim() + ".txt");
        FileReader fileReader = new FileReader(textFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((nextLine = bufferedReader.readLine()) != null) {
            textLines.add(nextLine);
        }
        fileReader.close();
        return textLines;
    }
}
