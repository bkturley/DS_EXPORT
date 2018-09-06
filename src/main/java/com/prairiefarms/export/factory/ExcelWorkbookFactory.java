package com.prairiefarms.export.factory;

import com.prairiefarms.export.ExcelWorkbook;

import java.io.IOException;

public class ExcelWorkbookFactory {
    public ExcelWorkbook newExcelWorkbook(String fileName) throws IOException {
        return new ExcelWorkbook(fileName);
    }
}
