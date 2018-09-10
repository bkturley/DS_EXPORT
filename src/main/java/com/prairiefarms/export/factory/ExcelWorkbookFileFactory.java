package com.prairiefarms.export;

import com.prairiefarms.export.access.WorkbookAccess;
import com.prairiefarms.export.factory.CellStyleFactory;
import com.prairiefarms.export.factory.WriteableReportDataFactory;
import com.prairiefarms.export.factory.products.WriteableLine;
import com.prairiefarms.export.factory.products.WriteableReportData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

class ExcelWorkbookFileFactory {


    private Configuration configuration;
    private WorkbookAccess workbookAccess;
    private CellStyleFactory cellStyleFactory;
    private WriteableReportDataFactory writeableReportDataFactory;

    public ExcelWorkbookFileFactory(){
        this(new Configuration(), new WorkbookAccess(), new CellStyleFactory(), new WriteableReportDataFactory());
    }

    ExcelWorkbookFileFactory(Configuration configuration,
                             WorkbookAccess workbook,
                             CellStyleFactory cellStyleFactory,
                             WriteableReportDataFactory writeableReportDataFactory){
        this.configuration = configuration;
        this.workbookAccess = workbook;
        this.cellStyleFactory = cellStyleFactory;
        this.writeableReportDataFactory = writeableReportDataFactory;
    }


    public File newExcelWorkbookFile(String fileName) throws IOException {
        Sheet sheet = workbookAccess.getInstance().createSheet("Report");
        int rowIndex = 0;
        boolean doneWritingHeaders = false;
        WriteableReportData writeableLines = writeableReportDataFactory.newWriteableReportData(fileName);
        for (WriteableLine WriteableLine : writeableLines.getData()) {
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
        workbookAccess.getInstance().write(newXLSXfile);
        newXLSXfile.close();
        workbookAccess.getInstance().close();

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
