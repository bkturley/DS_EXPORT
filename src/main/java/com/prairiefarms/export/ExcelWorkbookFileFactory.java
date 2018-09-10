package com.prairiefarms.export;

import java.io.*;

public class ExcelWorkbookFileFactory {

    Writer writer;

    public ExcelWorkbookFileFactory(){
        this(new Writer());
    }

    public ExcelWorkbookFileFactory(Writer writer) {
        this.writer = writer;
    }

    public File newExcelWorkbookFile(String fileName) throws IOException {
        return writer.write(fileName);
    }
}
