package com.prairiefarms.export;

import com.prairiefarms.export.factory.ReportFactory;
import com.prairiefarms.export.factory.products.WriteableLine;
import com.prairiefarms.export.types.Report;

import java.io.*;
import java.util.*;

public class ExcelWorkbook {

    File file;

    public ExcelWorkbook(String fileName) throws IOException {
        this(fileName,
                new textLineList(),
                new ReportFactory(),
                new mappedReportData(),
                new Writer());
    }

    ExcelWorkbook(String fileName,
                  textLineList textLineList,
                  ReportFactory reportFactory,
                  mappedReportData mappedReportData,
                  Writer writer) throws IOException {

        List<String> linesOfSpoolFile = textLineList.getLines(fileName);
        Report reportLayout = reportFactory.getReport(fileName);
        List<WriteableLine> writableRows = mappedReportData.getWriteableData(linesOfSpoolFile, reportLayout);
        file = writer.write(fileName, writableRows);
    }

    public File getFile() {
        return file;
    }
}
