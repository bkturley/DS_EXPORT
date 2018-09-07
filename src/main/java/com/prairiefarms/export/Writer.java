package com.prairiefarms.export;

import com.prairiefarms.export.factory.products.WriteableLine;
import org.apache.commons.lang3.StringUtils;
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
    private CellStyle columnLabelCellStyle;

    private CellStyle detailStringCellStyle;
    private CellStyle detailIntegerCellStyle;
    private CellStyle detailDoubleCellStyle;


    private CellStyle totalStringCellStyle;



    File write(String fileName, List<WriteableLine> WriteableLines) throws IOException {
        Sheet sheet = workBook.createSheet("Report");
        int rowIndex = 0;
        boolean doneWritingHeaders = false;
        for (WriteableLine WriteableLine : WriteableLines) {
            List<Pair<String, String>> cells = WriteableLine.getCells();
            String recordType = WriteableLine.getRecordType();
            switch (recordType) {
                case "detail":
                    doneWritingHeaders = true;
                    writeReportLine(cells, rowIndex++, sheet);
                    break;
                case "header":
                    if (!doneWritingHeaders) {
                        writeReportLine(cells, rowIndex++, sheet);
                    }
                    break;
                case "label":
                    if (!doneWritingHeaders) {
                        writeReportLine(cells, rowIndex++, sheet);
                    }
                    break;
                case "footer":
                    writeReportLine(cells, rowIndex++, sheet);
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

    private void writeReportLine(List<Pair<String, String>> cellList, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Pair<String, String> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            setCellValue(cell, cellValue);
            cell.setCellStyle(getCellStyle(cellValue.getRight()));
        }
    }

    private void setCellValue(Cell cell, Pair<String, String> cellValue) {
        switch (cellValue.getRight()){
            case "integer":
            case "double":
                if(StringUtils.isNotBlank(cellValue.getLeft())){
                    cell.setCellValue(Double.parseDouble(cellValue.getLeft()));
                }else{
                    cell.setCellValue("");
                }
                break;
            case "blank":
                cell.setCellValue("");
                break;
            default:
                cell.setCellValue(cellValue.getLeft());
        }
    }


    private CellType getCellType(String string) {
        CellType returnMe = null;
        switch (string){
            case "string":
                returnMe = CellType.STRING;
                break;
            case "integer":
            case "double":
                returnMe = CellType.NUMERIC;
                break;
            case "blank":
                returnMe = CellType.BLANK;
                break;
            default:
                returnMe = CellType.STRING;
        }
        return returnMe;
    }

    private CellStyle getCellStyle(String cellType) {
        CellStyle returnMe = null;
        switch (cellType){
            case "string":
                returnMe = getDetailStringCellStyle();
                break;
            case "integer":
                returnMe = getDetailIntegerCellStyle();
                break;
            case "double":
                returnMe = getDetailDoubleCellStyle();
                break;
            case "blank":
                returnMe = getDetailBlankCellStyle();
                break;
            default:
                returnMe = getDetailStringCellStyle();
        }
        return returnMe;
    }



    private CellStyle getDetailBlankCellStyle() {
        return getDetailStringCellStyle();
    }

    private CellStyle getDetailStringCellStyle() {
        if(detailStringCellStyle == null){
            detailStringCellStyle = workBook.createCellStyle();
            Font detailTextFont = workBook.createFont();
            detailStringCellStyle.setAlignment(HorizontalAlignment.CENTER);
            detailStringCellStyle.setFont(detailTextFont);
        }
        return detailStringCellStyle;
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

    private CellStyle getColumnLabelCellStyle() {
        if(columnLabelCellStyle == null){
            columnLabelCellStyle = workBook.createCellStyle();
            Font labelTextFont = workBook.createFont();
            labelTextFont.setBold(true);
            labelTextFont.setColor(IndexedColors.BLACK.index);

            columnLabelCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            columnLabelCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            columnLabelCellStyle.setFont(labelTextFont);
            columnLabelCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return columnLabelCellStyle;
    }

    private CellStyle getTotalStringCellStyle() {
        if(totalStringCellStyle == null){
            totalStringCellStyle = workBook.createCellStyle();
            Font totalTextFont = workBook.createFont();
            totalTextFont.setBold(true);
            totalTextFont.setColor(IndexedColors.BLACK.index);
            totalStringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            totalStringCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStringCellStyle.setAlignment(HorizontalAlignment.CENTER);
            totalStringCellStyle.setFont(totalTextFont);
        }
        return totalStringCellStyle;
    }

    private void errorOnUnknownRecordType(List<Pair<String, String>> cellList) throws IOException {
        throw new IOException(".xlsx conversion failed, This record has unknown type:" + System.getProperty("line.separator")
                + cellList.toString());
    }

    public CellStyle getDetailIntegerCellStyle() {
        if(detailIntegerCellStyle == null){
            DataFormat integerFormat = workBook.createDataFormat();
            detailIntegerCellStyle = workBook.createCellStyle();
            detailIntegerCellStyle.setDataFormat(integerFormat.getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
            detailIntegerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return detailIntegerCellStyle;
    }

    private CellStyle getDetailDoubleCellStyle() {
        if(detailDoubleCellStyle == null){
            DataFormat doubleFormat = workBook.createDataFormat();
            detailDoubleCellStyle = workBook.createCellStyle();
            detailDoubleCellStyle.setDataFormat(doubleFormat.getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
            detailDoubleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return detailDoubleCellStyle;
    }
}
