package com.prairiefarms.export.factory;

import com.prairiefarms.export.access.ConfigurationAccess;
import com.prairiefarms.export.access.WorkbookAccess;
import com.prairiefarms.export.access.CellStyleAccess;
import com.prairiefarms.export.factory.products.WriteableLine;
import com.prairiefarms.export.factory.products.WriteableReportData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelWorkbookFileFactory {


    private ConfigurationAccess configurationAccess;
    private WorkbookAccess workbookAccess;
    private CellStyleAccess cellStyleAccess;
    private WriteableReportDataFactory writeableReportDataFactory;

    public ExcelWorkbookFileFactory(){
        this(new ConfigurationAccess(), new WorkbookAccess(), new CellStyleAccess(), new WriteableReportDataFactory());
    }

    ExcelWorkbookFileFactory(ConfigurationAccess configurationAccess,
                             WorkbookAccess workbook,
                             CellStyleAccess cellStyleAccess,
                             WriteableReportDataFactory writeableReportDataFactory){
        this.configurationAccess = configurationAccess;
        this.workbookAccess = workbook;
        this.cellStyleAccess = cellStyleAccess;
        this.writeableReportDataFactory = writeableReportDataFactory;
    }


    public File newExcelWorkbookFile(String fileName) throws IOException {

        Sheet sheet = workbookAccess.getInstance().createSheet("Report");
        WriteableReportData writeableLines = writeableReportDataFactory.newWriteableReportData(fileName);
        int maxCellsPerRow = getMaxColumnIndex(writeableLines);
        boolean pastHeadline = false;
        for (WriteableLine writeableLine : writeableLines.getData()) {
            List<Pair<String, String>> cells = writeableLine.getCells();
            switch (writeableLine.getRecordType()) {
                case "headline":
                    if(!pastHeadline){
                        writeMergedLine(maxCellsPerRow, cells, sheet);
                    }
                    break;
                case "label":
                    if(!pastHeadline){
                        writeRecordLine(cells, sheet);
                    }
                    break;
                case "detail":
                    pastHeadline = true;
                    writeRecordLine(cells, sheet);
                    break;
                case "header":
                case "footer":
                    writeMergedLine(maxCellsPerRow, cells,  sheet);
                    break;
                default:
                    errorOnUnknownRecordType(cells);
            }
        }

        //if this gets out of hand, migrate to database lookups instead.
        boolean reportTypeIsP818TPRT = "P818TPRT".equals(fileName.split("\\.")[3]);
        if(reportTypeIsP818TPRT) {
            writeGroupTabs(writeableLines);
        }

        return new File(getNewExcelWorkbookFilePath(fileName));

    }

    private void writeGroupTabs(WriteableReportData writeableLines) {

        Map<Integer, Sheet> groupTabs = new TreeMap<>();
        for (WriteableLine writeableLine : writeableLines.getData()) {

            List<Pair<String, String>> cells = writeableLine.getCells();

            if ("detail".equals(writeableLine.getRecordType()) && StringUtils.isNotBlank(cells.get(1).getLeft())) {

                Integer groupNumber = Integer.parseInt(cells.get(1).getLeft().trim());

                if(!groupTabs.containsKey(groupNumber)){
                    groupTabs.put(groupNumber, workbookAccess.getInstance().createSheet("Group " + groupNumber.toString()));
                    // todo: write the label line?

                }
                writeRecordLine(cells, groupTabs.get(groupNumber));
            }
        }

        // sort group tabs ascending order.
        int tabIndex = 1;
        for (Integer groupNumber : groupTabs.keySet()) {
            workbookAccess.getInstance().setSheetOrder("Group " + groupNumber.toString(), tabIndex++);
        }
    }

    private String getNewExcelWorkbookFilePath(String fileName) throws IOException {
        String newXlsxFilePath = configurationAccess.getProperty("workingDirectory") + fileName + ".xlsx";
        FileOutputStream newXLSXfile = new FileOutputStream(newXlsxFilePath);
        workbookAccess.getInstance().write(newXLSXfile);
        newXLSXfile.close();
        workbookAccess.getInstance().close();
        return newXlsxFilePath;
    }

    private int getMaxColumnIndex(WriteableReportData writeableReportData) {
        int returnMe = 0;
        for(WriteableLine writeableLine : writeableReportData.getData()){
            if(writeableLine.getCells().size() > returnMe && "detail".equals(writeableLine.getRecordType())){
                returnMe = writeableLine.getCells().size();
            }
        }
        return returnMe - 1;
    }

    private void writeRecordLine(List<Pair<String, String>> cellList, Sheet sheet) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        int cellIndex = 0;
        for (Pair<String, String> cellValue : cellList) {
            Cell cell = row.createCell(cellIndex++);
            setCellValue(cell, cellValue);
            cell.setCellStyle(cellStyleAccess.newCellStyle(cellValue.getRight()));
        }
    }

    private void writeMergedLine(int mergeSize, List<Pair<String, String>> cellList, Sheet sheet) {
        int nextRowIndex = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(nextRowIndex);
        int cellIndex = 0;
        String mergedText = "";
        Cell cell = row.createCell(cellIndex++);
        for (Pair<String, String> cellValue : cellList) {
            mergedText += cellValue.getLeft() + " ";
        }

        cell.setCellValue(mergedText);
        cell.setCellStyle(cellStyleAccess.newCellStyle("string"));
        sheet.addMergedRegion(new CellRangeAddress(nextRowIndex, nextRowIndex, 0, mergeSize));
    }

    private void setCellValue(Cell cell, Pair<String, String> cellValue) {
        switch (cellValue.getRight()){
            case "integer":
            case "double":
                if(StringUtils.isNotBlank(cellValue.getLeft())){
                    cell.setCellValue(Double.parseDouble(cellValue.getLeft().replaceAll(",", "")));
                }else{
                    cell.setCellValue(0);
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
