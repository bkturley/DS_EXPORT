package com.prairiefarms.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class Writer {

    Configuration configuration = new Configuration();
    Workbook workBook = new XSSFWorkbook();

    public File write(String fileName, Map<Map<String, CellType>, String> linesWithType) throws IOException {

        Sheet sheet = workBook.createSheet("Report");
        int rowIndex = 0;
        boolean doneWritingHeaders = false;
        for (Map.Entry<Map<String, CellType>, String> recordWithType : linesWithType.entrySet()) {
            Map<String, CellType> cellWithType = recordWithType.getKey();
            String lineType = recordWithType.getValue();

            switch (lineType) {
                case "header":
                    if (!doneWritingHeaders) {
                        writeHeaderLine(cellWithType, rowIndex++, sheet);
                    }
                    break;
                case "label":
                    writeLabelLine(cellWithType, rowIndex++, sheet);
                    break;
                case "detail":
                    doneWritingHeaders = true;
                    writeDetailLine(cellWithType, rowIndex++, sheet);
                    break;
                case "footer":
                    writeFooterLine(cellWithType, rowIndex++, sheet);
                    break;
                default:
                    errorOnUnknownRecordType(cellWithType);
            }
        }

        String xlsxFile = fileName + ".xlsx";

        FileOutputStream newXLSXfile = new FileOutputStream(configuration.getProperty("workingDirectory") + xlsxFile.trim());

        workBook.write(newXLSXfile);

        newXLSXfile.close();
        autoSizeColumns(workBook); // not working
        workBook.close();

        return new File(configuration.getProperty("workingDirectory") + xlsxFile.trim());

    }

    private void writeHeaderLine(Map<String, CellType> cellWithType, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for (Map.Entry<String, CellType> cellValue : cellWithType.entrySet()) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellStyle(getHeaderTextStyle());
            cell.setCellType(cellValue.getValue());
            cell.setCellValue(cellValue.getKey());
        }
    }

    private void writeLabelLine(Map<String, CellType> cellWithType, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for (Map.Entry<String, CellType> cellValue : cellWithType.entrySet()) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellType(cellValue.getValue());
            cell.setCellStyle(getLabelTextStyle());
            cell.setCellValue(cellValue.getKey());
        }
    }

    private void writeDetailLine(Map<String, CellType> cellWithType, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Map.Entry<String, CellType> cellValue : cellWithType.entrySet()) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellType(cellValue.getValue());
            //            cell.setCellStyle(getDetailTextStyle());
            cell.setCellValue(cellValue.getKey());
        }
    }

    private void writeFooterLine(Map<String, CellType> cellWithType, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Map.Entry<String, CellType> cellValue : cellWithType.entrySet()) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellStyle(getTotalTextStyle());
            cell.setCellType(cellValue.getValue());
            cell.setCellValue(cellValue.getKey());
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

    private void errorOnUnknownRecordType(Map<String, CellType> cellWithType) throws IOException {
        throw new IOException(".xlsx conversion failed, This record has unknown type:" + System.getProperty("line.separator")
                + cellWithType.get(0));
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
