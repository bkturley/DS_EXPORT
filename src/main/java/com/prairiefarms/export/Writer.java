package com.prairiefarms.export;

import com.prairiefarms.export.factory.products.WriteableLine;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

class Writer {

    private Configuration configuration = new Configuration();
    private Workbook workBook = new XSSFWorkbook();

    private CellStyle headerCellStyle;
    private CellStyle labelCellStyle;
    private CellStyle detailCellStyle;
    private CellStyle totalCellStyle;

    File write(String fileName, List<WriteableLine> WriteableLines) throws IOException {
        Sheet sheet = workBook.createSheet("Report");
        int rowIndex = 0;
        boolean doneWritingHeaders = false;
        for (WriteableLine WriteableLine : WriteableLines) {
            List<Pair<String, CellType>> cells = WriteableLine.getCells();
            String recordType = WriteableLine.getRecordType();
            switch (recordType) {
                case "detail":
                    doneWritingHeaders = true;
                    writeReportLine(cells, rowIndex++, sheet, getDetailCellStyle());
                    break;
                case "header":
                    if (!doneWritingHeaders) {
                        writeReportLine(cells, rowIndex++, sheet, getHeaderTextStyle());
                    }
                    break;
                case "label":
                    if (!doneWritingHeaders) {
                        writeReportLine(cells, rowIndex++, sheet, getLabelCellStyle());
                    }
                    break;
                case "footer":
                    writeReportLine(cells, rowIndex++, sheet, getTotalCellStyle());
                    break;
                default:
                    errorOnUnknownRecordType(cells);
            }
        }

        String xlsxFile = fileName + ".xlsx";

        FileOutputStream newXLSXfile = new FileOutputStream(configuration.getProperty("workingDirectory") + xlsxFile.trim());

        workBook.write(newXLSXfile);

        newXLSXfile.close();
        workBook.close();

        return new File(configuration.getProperty("workingDirectory") + xlsxFile.trim());

    }

    private void writeReportLine(List<Pair<String, CellType>> cellList, int rowIndex, Sheet sheet, CellStyle cellStyle) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Pair<String, CellType> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellStyle(getTotalCellStyle());
            cell.setCellType(cellValue.getValue());
            cell.setCellStyle(cellStyle);
            cell.setCellValue(cellValue.getKey());
        }
    }

    private CellStyle getDetailCellStyle() {
        if(detailCellStyle == null){
            detailCellStyle = workBook.createCellStyle();
            Font headerTextFont = workBook.createFont();
            detailCellStyle.setAlignment(HorizontalAlignment.CENTER);
            detailCellStyle.setFont(headerTextFont);
        }
        return detailCellStyle;
    }

    private CellStyle getHeaderTextStyle() {
        if (headerCellStyle == null) {
            headerCellStyle = workBook.createCellStyle();
            Font headerTextFont = workBook.createFont();
            headerTextFont.setBold(true);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setFont(headerTextFont);
        }
        return headerCellStyle;
    }

    private CellStyle getLabelCellStyle() {
        if(labelCellStyle == null){
            labelCellStyle = workBook.createCellStyle();
            Font labelTextFont = workBook.createFont();
            labelTextFont.setBold(true);
            labelTextFont.setColor(IndexedColors.BLACK.index);

            labelCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            labelCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            labelCellStyle.setFont(labelTextFont);
            labelCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return labelCellStyle;
    }

    private CellStyle getTotalCellStyle() {
        if(totalCellStyle == null){
            totalCellStyle = workBook.createCellStyle();
            Font totalTextFont = workBook.createFont();
            totalTextFont.setBold(true);
            totalTextFont.setColor(IndexedColors.BLACK.index);
            totalCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            totalCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalCellStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalCellStyle.setFont(totalTextFont);
        }
        return totalCellStyle;
    }

    private void errorOnUnknownRecordType(List<Pair<String, CellType>> cellList) throws IOException {
        throw new IOException(".xlsx conversion failed, This record has unknown type:" + System.getProperty("line.separator")
                + cellList.toString());
    }
}
