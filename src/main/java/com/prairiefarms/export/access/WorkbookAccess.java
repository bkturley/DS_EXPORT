package com.prairiefarms.export.access;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkbookAccess {
    private static Workbook instance;

    public WorkbookAccess(){};

    public static synchronized Workbook getInstance(){
        if(instance == null){
            instance = new XSSFWorkbook();
        }
        return instance;
    }
}
