package com.prairiefarms.export;

import org.apache.poi.ss.usermodel.CellType;

import java.io.*;
import java.util.*;

class ExcelWorkbook {

    private String fileName;

    public ExcelWorkbook(String fileName) throws IOException {
        this(fileName,
                new FileLineList(),
                new ReportAccess(),
                new WritableData(),
                new Writer());
    }

    ExcelWorkbook(String fileName,
                  FileLineList fileLineList,
                  ReportAccess reportAccess,
                  WritableData writableData,
                  Writer writer) throws IOException {
        List<String> linesOfSpoolFile = fileLineList.getLines(fileName);
        Report reportLayout = reportAccess.getReport(fileName);
        Map<Map<String, CellType>, String> writableRows = writableData.getWriteableData(linesOfSpoolFile, reportLayout);
        writer.write(fileName, writableRows);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName + ".xlsx";
    }
}
