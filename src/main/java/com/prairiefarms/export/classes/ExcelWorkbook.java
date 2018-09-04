package com.prairiefarms.export.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.prairiefarms.export.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


class ExcelWorkbook {

    private Configuration configuration = new Configuration();

    private CellStyle headerTextStyle;

    private CellStyle labelTextStyle;

    private CellStyle subHeaderTextStyle;

    private CellStyle identificationStyle;

    private CellStyle integerStyle;

    private CellStyle doubleStyle;

    private CellStyle totalTextStyle;

    private CellStyle totalIntegerStyle;

    private CellStyle totalDoubleStyle;

    private String fileName;


    ExcelWorkbook(String fileName) throws IOException {
        this.fileName = fileName;
        writeReportLines(getReportData(fileName));
    }

    private Map<List<String>, String> getReportData(String fileName) throws IOException {
        return getWriteableData(getTextFileLines(fileName), getReport(fileName));
    }

    private void writeReportLines(Map<List<String>, String> linesWithType) throws IOException {
        if (!linesWithType.isEmpty()) {
            Workbook workBook = new XSSFWorkbook();
            setWorkbookStyle(workBook);

            Sheet sheet = workBook.createSheet("Report");

            int rowIndex = 0;
            boolean doneWritingHeaders = false;
            for (Map.Entry<List<String>, String> lineWithType : linesWithType.entrySet()) {
                List<String> cellValues = lineWithType.getKey();
                String lintType = lineWithType.getValue();

                switch (lintType) {
                    case "header":
                        if(!doneWritingHeaders){
                            writeHeaderLine(cellValues, rowIndex++, sheet);
                        }
                        break;
                    case "label":
                        writeLabelLine(cellValues, rowIndex++, sheet);
                        break;
                    case "detail":
                        doneWritingHeaders = true;
                        writeDetailLine(cellValues, rowIndex++, sheet);
                        break;
//                    case "footer":
//                        writeFooterLine(cellValues, rowIndex++, sheet);
//                        break;
                }
            }

            String xlsxFile = fileName + ".xlsx";

            FileOutputStream newXLSXfile = new FileOutputStream(configuration.getProperty("workingDirectory") + xlsxFile.trim());

            workBook.write(newXLSXfile);

            newXLSXfile.close();
            workBook.close();

            new File(configuration.getProperty("workingDirectory") + xlsxFile.trim());

            this.fileName = xlsxFile;

        }
    }

    private void setWorkbookStyle(Workbook workBook) {
        headerTextStyle = workBook.createCellStyle();
        Font headerTextFont = workBook.createFont();
        headerTextFont.setBold(true);
        headerTextStyle.setAlignment(HorizontalAlignment.LEFT);
        headerTextStyle.setFont(headerTextFont);

        labelTextStyle = workBook.createCellStyle();
        Font labelTextFont = workBook.createFont();
        labelTextFont.setBold(true);
        labelTextFont.setColor(IndexedColors.BLACK.index);

        labelTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        labelTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        labelTextStyle.setFont(labelTextFont);
        labelTextStyle.setAlignment(HorizontalAlignment.CENTER);

        subHeaderTextStyle = workBook.createCellStyle();
        Font subHeaderTextFont = workBook.createFont();
        subHeaderTextFont.setBold(true);
        subHeaderTextStyle.setFont(subHeaderTextFont);
        subHeaderTextStyle.setAlignment(HorizontalAlignment.LEFT);
        subHeaderTextStyle.setBorderBottom(BorderStyle.THIN);

        DataFormat identificationFormat = workBook.createDataFormat();
        identificationStyle = workBook.createCellStyle();
        identificationStyle.setDataFormat(identificationFormat.getFormat("#0"));
        identificationStyle.setAlignment(HorizontalAlignment.RIGHT);

        DataFormat integerFormat = workBook.createDataFormat();
        integerStyle = workBook.createCellStyle();
        integerStyle.setDataFormat(integerFormat.getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
        integerStyle.setAlignment(HorizontalAlignment.RIGHT);

        DataFormat doubleFormat = workBook.createDataFormat();
        doubleStyle = workBook.createCellStyle();
        doubleStyle.setDataFormat(doubleFormat.getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
        doubleStyle.setAlignment(HorizontalAlignment.RIGHT);

        totalTextStyle = workBook.createCellStyle();
        Font totalTextFont = workBook.createFont();
        totalTextFont.setBold(true);
        totalTextFont.setColor(IndexedColors.BLACK.index);
        totalTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        totalTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalTextStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalTextStyle.setFont(totalTextFont);

        DataFormat totalIntegerFormat = workBook.createDataFormat();
        totalIntegerStyle = workBook.createCellStyle();
        Font totalIntegerFont = workBook.createFont();
        totalIntegerFont.setBold(true);
        totalIntegerFont.setColor(IndexedColors.BLACK.index);
        totalIntegerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        totalIntegerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalIntegerStyle
                .setDataFormat(totalIntegerFormat.getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
        totalIntegerStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalIntegerStyle.setFont(totalIntegerFont);
        totalIntegerStyle.setBorderTop(BorderStyle.THIN);
        totalIntegerStyle.setBorderBottom(BorderStyle.DOUBLE);

        DataFormat totalDoubleFormat = workBook.createDataFormat();
        totalDoubleStyle = workBook.createCellStyle();
        Font totalDoubleFont = workBook.createFont();
        totalDoubleFont.setBold(true);
        totalDoubleFont.setColor(IndexedColors.BLACK.index);
        totalDoubleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        totalDoubleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalDoubleStyle.setDataFormat(
                totalDoubleFormat.getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
        totalDoubleStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalDoubleStyle.setFont(totalDoubleFont);
        totalDoubleStyle.setBorderTop(BorderStyle.THIN);
        totalDoubleStyle.setBorderBottom(BorderStyle.DOUBLE);
    }

    private Report getReport(String textFileName) throws IOException {
        Report returnME = null;

        File jsonFile = new File(configuration.getProperty("jsonMaps") + textFileName.trim() + ".json");
        List<Report> reports = new ObjectMapper().readValue(jsonFile, new TypeReference<List<Report>>() {
        });

        for (Report report : reports) {
            if (textFileName.trim().toLowerCase().contains(report.getTitle().trim().toLowerCase())) {
                returnME = report;
                break;
            }
        }

        if (returnME == null) {
            throw new IOException("Report Conversion layout with title: " + textFileName + " not found in conversion file: " + fileName + ".json");
        }
        return returnME;
    }

    private String determineTextFileLineRowType(String textLine, Report report) {
        String returnMe = "unknown";
        if (!textLine.isEmpty()) {
            for (ReportRow reportRow : report.getReportRows()) {
                //build list of invalid areas
                Map<Integer, Integer> invalidPositions = new LinkedHashMap<>();
                Map<Integer, Integer> validPositions = new LinkedHashMap<>();
                int startingIndex = 0;
                for (ReportColumn reportColumn : reportRow.getColumns()) {
                    int beginCharacterIndex = reportColumn.getPosition()[0] - 1; //rpg starts indexes at 1
                    int endCharacterIndex = reportColumn.getPosition()[1] - 1; //rpg starts indexes at 1
                    invalidPositions.put(startingIndex, beginCharacterIndex);
                    validPositions.put(beginCharacterIndex, endCharacterIndex);
                    startingIndex = endCharacterIndex + 1;
                }
                invalidPositions.put(startingIndex, startingIndex + textLine.substring(startingIndex).length());
                //test that characters are in position.
                Boolean positionalMatch = true;
                for (Map.Entry<Integer, Integer> entry : invalidPositions.entrySet()) {
                    if (StringUtils.isNotBlank(textLine.substring(entry.getKey(), entry.getValue()))) {
                        positionalMatch = false;
                    }
                }

                // and of correct Type
                Boolean dataTypesMatch = true;
                if (positionalMatch) {
                    for (ReportColumn reportColumn : reportRow.getColumns()) {
                        String validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
                        if (StringUtils.isNotBlank(validateMe)) {
                            switch (reportColumn.getType()) {
                                case "integer":
                                    try {
                                        Integer.parseInt(validateMe.trim());
                                    } catch (NumberFormatException numberFormatException) {
                                        dataTypesMatch = false;
                                    }
                                case "double":
                                    try {
                                        Float.parseFloat(validateMe.trim());
                                    } catch (NumberFormatException numberFormatException) {
                                        dataTypesMatch = false;
                                    }
                            }
                        }
                    }
                }

                //match!
                if (positionalMatch && dataTypesMatch) {
                    returnMe = reportRow.getName();
                }

            }
        } else {
            returnMe = "blank";
        }

        return returnMe;
    }

    private List<String> getTextFileLines(String textFileName) throws IOException {
        List<String> textLines = new ArrayList<>();
        String nextLine;
        File textFile = new File(configuration.getProperty("workingDirectory") + textFileName.trim() + ".txt");
        FileReader fileReader = new FileReader(textFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((nextLine = bufferedReader.readLine()) != null) {
            textLines.add(nextLine);
        }
        fileReader.close();
        return textLines;
    }

    String getFileName() {
        return fileName;
    }


    private void writeHeaderLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for(String cellValue : cellValues){
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
            cell.setCellStyle(headerTextStyle);
        }
    }

    private void writeLabelLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for(String cellValue : cellValues){
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
            cell.setCellStyle(labelTextStyle);
        }
    }

    private void writeDetailLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for(String cellValue : cellValues){
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
//            cell.setCellStyle();
        }
    }


    private void writeFooterLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for(String cellValue : cellValues){
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
            cell.setCellStyle(totalTextStyle);
        }
    }

    private Map<List<String>, String> getWriteableData(List<String> textLines, Report report) {
        Map<List<String>, String> returnMe = new LinkedHashMap<>();
        for(String textLine : textLines){
            if (!textLine.isEmpty()) {
                for (ReportRow reportRow : report.getReportRows()) {
                    //build list of invalid areas
                    Map<Integer, Integer> invalidPositions = new LinkedHashMap<>();
                    Map<Integer, Integer> validPositions = new LinkedHashMap<>();
                    int startingIndex = 0;
                    for (ReportColumn reportColumn : reportRow.getColumns()) {
                        int beginCharacterIndex = reportColumn.getPosition()[0] - 1; //rpg starts indexes at 1
                        int endCharacterIndex = reportColumn.getPosition()[1] - 1; //rpg starts indexes at 1
                        invalidPositions.put(startingIndex, beginCharacterIndex);
                        validPositions.put(beginCharacterIndex, endCharacterIndex);
                        startingIndex = endCharacterIndex + 1;
                    }
                    invalidPositions.put(startingIndex, startingIndex + textLine.substring(startingIndex).length());
                    //test that characters are in position.
                    Boolean positionalMatch = true;
                    for (Map.Entry<Integer, Integer> entry : invalidPositions.entrySet()) {
                        if (StringUtils.isNotBlank(textLine.substring(entry.getKey(), entry.getValue()))) {
                            positionalMatch = false;
                        }
                    }

                    // and of correct Type
                    Boolean dataTypesMatch = true;
                    List<String> fieldValues = new ArrayList<>();
                    if (positionalMatch) {
                        for (ReportColumn reportColumn : reportRow.getColumns()) {
                            String validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
                            fieldValues.add(validateMe);
                            if (StringUtils.isNotBlank(validateMe)) {
                                switch (reportColumn.getType()) {
                                    case "integer":
                                        try {
                                            Integer.parseInt(validateMe.trim());
                                        } catch (NumberFormatException numberFormatException) {
                                            dataTypesMatch = false;
                                        }
                                    case "double":
                                        try {
                                            Float.parseFloat(validateMe.trim());
                                        } catch (NumberFormatException numberFormatException) {
                                            dataTypesMatch = false;
                                        }
                                }
                            }
                        }
                    }

                    //match!
                    if (positionalMatch && dataTypesMatch) {
                        returnMe.put(fieldValues, reportRow.getName());
                    }

                }
            }
        }

        return returnMe;
    }

}
