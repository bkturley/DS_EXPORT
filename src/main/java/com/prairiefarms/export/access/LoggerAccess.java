package com.prairiefarms.export.access;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class LoggerAccess {

    private Path path;
    private static String slashLine = "///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////";
    private static String newLine = System.getProperty("line.separator");
    public LoggerAccess(){
        this(new ConfigurationAccess());
    }

    LoggerAccess(ConfigurationAccess configurationAccess){
        String logFilePathString = configurationAccess.getProperty("loggingPath");
        try {
            new File(logFilePathString).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        path = Paths.get(logFilePathString);
    }

    public void log(String logMessage){

        String dateString = new Date().toString();

        String logMe = newLine +
                       slashLine + newLine +
                       "//  Begin log message: Timestamp = " + dateString + newLine +
                       slashLine + newLine +
                       logMessage;

        try {
            Files.write(path, logMe.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void log(Exception logMe){
        log(ExceptionUtils.getStackTrace(logMe));
    }

}
