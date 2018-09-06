package com.prairiefarms.export;

import com.prairiefarms.export.factory.ReportFactory;
import org.apache.poi.ss.usermodel.CellType;

import java.io.*;
import java.util.*;

public class ExcelWorkbook {

    private String fileName;

    public ExcelWorkbook(String fileName) throws IOException {
        this(fileName,
                new FileLineList(),
                new ReportFactory(),
                new WritableData(),
                new Writer());
    }

    ExcelWorkbook(String fileName,
                  FileLineList fileLineList,
                  ReportFactory reportFactory,
                  WritableData writableData,
                  Writer writer) throws IOException {
        List<String> linesOfSpoolFile = fileLineList.getLines(fileName);
        Report reportLayout = reportFactory.getReport(fileName);
        Map<Map<String, CellType>, String> writableRows = writableData.getWriteableData(linesOfSpoolFile, reportLayout);
        writer.write(fileName, writableRows);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName + ".xlsx";
    }
}
