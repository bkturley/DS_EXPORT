package com.prairiefarms.export;

import com.prairiefarms.export.factory.products.writeableLine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Writer {

    Configuration configuration = new Configuration();
    Workbook workBook = new XSSFWorkbook();

    public File write(String fileName, List<writeableLine> writeableLines) throws IOException {
        Sheet sheet = workBook.createSheet("Report");
        int rowIndex = 0;
        boolean doneWritingHeaders = false;
        for (writeableLine writeableLine : writeableLines) {
            List<Pair<String, CellType>> cells = writeableLine.getCells();
            String recordType = writeableLine.getRecordType();
            switch (recordType) {
                case "detail":
                    doneWritingHeaders = true;
                    writeDetailLine(cells, rowIndex++, sheet);
                    break;
                case "header":
                    if (!doneWritingHeaders) {
                        writeHeaderLine(cells, rowIndex++, sheet);
                    }
                    break;
                case "label":
                    if (!doneWritingHeaders) {
                        writeLabelLine(cells, rowIndex++, sheet);
                    }
                    break;
                case "footer":
                    writeFooterLine(cells, rowIndex++, sheet);
                    break;
                default:
                    errorOnUnknownRecordType(cells);
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

    private void writeHeaderLine(List<Pair<String, CellType>> cellList, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for (Pair<String, CellType> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellStyle(getHeaderTextStyle());
            cell.setCellType(cellValue.getValue());
            cell.setCellValue(cellValue.getKey());
        }
    }

    private void writeLabelLine(List<Pair<String, CellType>> cellList, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;
        for (Pair<String, CellType> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellType(cellValue.getValue());
            cell.setCellStyle(getLabelTextStyle());
            cell.setCellValue(cellValue.getKey());
        }
    }

    private void writeDetailLine(List<Pair<String, CellType>> cellList, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Pair<String, CellType> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellType(cellValue.getValue());
            //            cell.setCellStyle(getDetailTextStyle());
            cell.setCellValue(cellValue.getKey());
        }
    }

    private void writeFooterLine(List<Pair<String, CellType>> cellList, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Pair<String, CellType> cellValue : cellList) {
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

    private void errorOnUnknownRecordType(List<Pair<String, CellType>> cellList) throws IOException {
        throw new IOException(".xlsx conversion failed, This record has unknown type:" + System.getProperty("line.separator")
                + cellList.toString());
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
