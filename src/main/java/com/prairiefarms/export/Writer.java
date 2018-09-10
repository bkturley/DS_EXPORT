package com.prairiefarms.export;

import com.prairiefarms.export.factory.CellStyleFactory;
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
    private CellStyleFactory cellStyleFactory = new CellStyleFactory(workBook);

    public File write(String fileName, List<WriteableLine> writeableLines) throws IOException {
        Sheet sheet = workBook.createSheet("Report");
        int rowIndex = 0;
        boolean doneWritingHeaders = false;
        for (WriteableLine WriteableLine : writeableLines) {
            List<Pair<String, String>> cells = WriteableLine.getCells();
            switch (WriteableLine.getRecordType()) {
                case "detail":
                case "footer":
                    doneWritingHeaders = true;
                    writeReportLine(cells, rowIndex++, sheet);
                    break;
                case "header":
                case "label":
                    if (!doneWritingHeaders) {
                        writeReportLine(cells, rowIndex++, sheet);
                    }
                    break;
                default:
                    errorOnUnknownRecordType(cells);
            }
        }

        String newXlsxFilePath = configuration.getProperty("workingDirectory") + fileName + ".xlsx";

        FileOutputStream newXLSXfile = new FileOutputStream(newXlsxFilePath);
        workBook.write(newXLSXfile);
        newXLSXfile.close();
        workBook.close();

        return new File(newXlsxFilePath);

    }

    private void writeReportLine(List<Pair<String, String>> cellList, int rowIndex, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (Pair<String, String> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            setCellValue(cell, cellValue);
            cell.setCellStyle(cellStyleFactory.newCellStyle(cellValue.getRight()));
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
                cell.setCellType(CellType.NUMERIC);
                break;
            case "blank":
                cell.setCellValue("");
                cell.setCellType(CellType.BLANK);
                break;
            default:
                cell.setCellValue(cellValue.getLeft());
                cell.setCellType(CellType.STRING);
        }
    }


    private void errorOnUnknownRecordType(List<Pair<String, String>> cellList) throws IOException {
        throw new IOException(".xlsx conversion failed, This record has unknown type:" + System.getProperty("line.separator")
                + cellList.toString());
    }


}
