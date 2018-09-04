package com.prairiefarms.export;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


class ExcelWorkbook {

    // attributes
    private String fileName;

    //dependencies
    private ReportAccess reportAccess;
    private Configuration configuration;
    private Workbook workBook;
    private FileLineList fileLineList;


    public ExcelWorkbook(String fileName) throws IOException {
        this(fileName,
                new ReportAccess(),
                new XSSFWorkbook(),
                new Configuration(),
                new FileLineList());
    }

    ExcelWorkbook(String fileName,
                  ReportAccess reportAccess,
                  Workbook workBook,
                  Configuration configuration,
                  FileLineList fileLineList) throws IOException {

        this.fileName = fileName;
        this.reportAccess = reportAccess;
        this.workBook = workBook;
        this.configuration = configuration;
        this.fileLineList = fileLineList;
        writeReportLines(getReportData(fileName));
    }

    public String getFileName() {
        return fileName;
    }

    private Map<List<String>, String> getReportData(String fileName) throws IOException {
        return getWriteableData(fileLineList.getLines(fileName), reportAccess.getReport(fileName));
    }

    private void writeReportLines(Map<List<String>, String> linesWithType) throws IOException {
        if (!linesWithType.isEmpty()) {


            Sheet sheet = workBook.createSheet("Report");

            int rowIndex = 0;
            boolean doneWritingHeaders = false;
            for (Map.Entry<List<String>, String> lineWithType : linesWithType.entrySet()) {
                List<String> cellValues = lineWithType.getKey();
                String lineType = lineWithType.getValue();

                switch (lineType) {
                    case "header":
                        if (!doneWritingHeaders) {
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
                    case "footer":
                        writeFooterLine(cellValues, rowIndex++, sheet);
                        break;
                    default:
                        errorOnUnknownRecordType(cellValues);
                }
            }

            String xlsxFile = fileName + ".xlsx";

            FileOutputStream newXLSXfile = new FileOutputStream(configuration.getProperty("workingDirectory") + xlsxFile.trim());

            workBook.write(newXLSXfile);

            newXLSXfile.close();
            autoSizeColumns(workBook);
            workBook.close();

            new File(configuration.getProperty("workingDirectory") + xlsxFile.trim());

            this.fileName = xlsxFile;

        }
    }

    private void writeHeaderLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for (String cellValue : cellValues) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
            cell.setCellStyle(getHeaderTextStyle());
        }
    }

    private CellStyle getHeaderTextStyle() {
        CellStyle headerTextStyle;
        headerTextStyle = workBook.createCellStyle();
        Font headerTextFont = workBook.createFont();
        headerTextFont.setBold(true);
        headerTextStyle.setAlignment(HorizontalAlignment.LEFT);
        headerTextStyle.setFont(headerTextFont);
        return headerTextStyle;
    }

    private void writeLabelLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for (String cellValue : cellValues) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
            cell.setCellStyle(getLabelTextStyle());
        }
    }

    private CellStyle getLabelTextStyle() {
        CellStyle labelTextStyle = workBook.createCellStyle();
        Font labelTextFont = workBook.createFont();
        labelTextFont.setBold(true);
        labelTextFont.setColor(IndexedColors.BLACK.index);

        labelTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        labelTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        labelTextStyle.setFont(labelTextFont);
        labelTextStyle.setAlignment(HorizontalAlignment.CENTER);
        return labelTextStyle;
    }

    private void writeDetailLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
//            cell.setCellStyle();
        }
    }

    private void writeFooterLine(List<String> cellValues, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(cellValue);
            cell.setCellStyle(getTotalTextStyle());
        }
    }

    private CellStyle getTotalTextStyle() {
        CellStyle totalTextStyle = workBook.createCellStyle();
        Font totalTextFont = workBook.createFont();
        totalTextFont.setBold(true);
        totalTextFont.setColor(IndexedColors.BLACK.index);
        totalTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        totalTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalTextStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalTextStyle.setFont(totalTextFont);
        return totalTextStyle;
    }

    private void errorOnUnknownRecordType(List<String> cellValues) throws IOException {
        throw new IOException(".xlsx conversion failed, This record has unknown type:" + System.getProperty("line.separator")
                + cellValues.get(0));
    }

    private Map<List<String>, String> getWriteableData(List<String> textLines, Report report) {
        Map<List<String>, String> returnMe = new LinkedHashMap<>();
        for (String textLine : textLines) {
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

                    boolean recordMatchesKnownType = positionalMatch && dataTypesMatch;
                    if (recordMatchesKnownType) {
                        returnMe.put(fieldValues, reportRow.getName());
                    }
                }
            }
        }

        return returnMe;
    }

    private void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(0);
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }
}
