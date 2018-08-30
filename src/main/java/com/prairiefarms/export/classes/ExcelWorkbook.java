package com.prairiefarms.export.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.prairiefarms.export.types.*;
import com.prairiefarms.export.types.Exception;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ExcelWorkbook {

    Configuration configuration = new Configuration();

    private static List<Line> lines;
    private static int lineCount;

    private static Report reportSection;
    private static List<ReportColumn> columns;

    private static Sheet sheet;
    private static Row row;
    private static Cell cell;

    private static int autoSizeColumns;
    private static int position;
    private static int length;

    private static CellStyle headerTextStyle;

    private static CellStyle labelTextStyle;

    private static CellStyle subHeaderTextStyle;

    private static CellStyle identificationStyle;

    private static CellStyle integerStyle;

    private static CellStyle doubleStyle;

    private static CellStyle totalTextStyle;

    private static CellStyle totalIntegerStyle;

    private static CellStyle totalDoubleStyle;

    private File file;

    ExcelWorkbook(String jsonName, String fileName) throws IOException {
        File textFile = new File(configuration.getProperty("workingDirectory") + fileName.trim() + ".txt");


        FileReader fileReader;

        BufferedReader bufferedReader;

        String thisLine;

        Line line;
        fileReader = new FileReader(textFile);

        bufferedReader = new BufferedReader(fileReader);

        lines = new LinkedList<>();

        lineCount = 0;

        reportSection = new Report();

        reportSection = getReportSection(jsonName, fileName);

        List<Exception> exceptions = new LinkedList<>();

        while ((thisLine = bufferedReader.readLine()) != null) {
            line = new Line();

            line.setType(" ");
            line.setPrecision(0);
            line.setElement(0);
            line.setLine(thisLine);

            Exception exception;
            if (lineCount < reportSection.getSection().size()
                    && (!reportSection.getSection().get(lineCount).getName().equals("customer")
                    && !reportSection.getSection().get(lineCount).getName().equals("product")
                    && !reportSection.getSection().get(lineCount).getName().equals("detail")
                    && !reportSection.getSection().get(lineCount).getName().equals("footer"))) {
                line.setType(reportSection.getSection().get(lineCount).getName());
                line.setElement(lineCount);

                switch (reportSection.getSection().get(lineCount).getName().trim()) {
                    case "header":
                        exception = new Exception();

                        exception.setType("header");
                        exception.setRepeatable(reportSection.getSection().get(lineCount).isRepeatable());
                        exception.setElement(lineCount);

                        if (thisLine.trim().contains("PAGE")) {
                            exception.setLine(line.getLine().substring(0, line.getLine().trim().indexOf("PAGE")));

                        } else {
                            exception.setLine(line.getLine());
                        }

                        exceptions.add(exception);

                        break;

                    case "label":
                        exception = new Exception();

                        exception.setType("label");
                        exception.setRepeatable(reportSection.getSection().get(lineCount).isRepeatable());
                        exception.setElement(lineCount);
                        exception.setLine(thisLine);

                        exceptions.add(exception);

                        break;
                }

            } else {
                for (int y = 0; y < reportSection.getSection().size(); y++) {
                    ReportRow section;

                    section = reportSection.getSection().get(y);

                    if (line.getLine().trim().contains("TOTALS:") && section.getName().trim().equals("footer")) {
                        line.setType("footer");

                        if (section.getIdentifier() != null) {
                            if (line.getLine().trim().contains(section.getIdentifier())) {
                                line.setElement(y);

                                break;
                            }

                        } else {
                            line.setElement(y);

                            break;
                        }
                    }

                    if (line.getLine().trim().contains("CUSTOMER:") && section.getName().trim().equals("customer")) {
                        line.setType("customer");

                        line.setElement(y);

                        break;
                    }

                    if (line.getLine().trim().contains("PRODUCT:") && section.getName().trim().equals("product")) {
                        line.setType("product");

                        line.setElement(y);

                        break;
                    }
                }

                if (line.getType().trim().equals("")) {
                    for (Exception exception1 : exceptions) {

                        exception = exception1;

                        if (line.getLine().contains(exception.getLine())) {
                            if (exception.isRepeatable()) {
                                line.setType(exception.getType());
                                line.setElement(exception.getElement());

                            } else {
                                line.setType("SKIP");
                            }

                            break;
                        }
                    }
                }

                if (line.getType().trim().equals("")) {
                    line.setType("detail");

                    for (int y = 0; y < reportSection.getSection().size(); y++) {
                        if (reportSection.getSection().get(y).getName().trim().equals(line.getType().trim())) {
                            line.setElement(y);

                            break;
                        }
                    }
                }
            }

            if (!line.getType().trim().equals("")) {
                lines.add(line);

                lineCount++;
            }
        }

        fileReader.close();



//        if (!textFile.delete()){
//            throw new FileNotFoundException(textFile.getName() + "not found while attempting delete");
//        }



        if (!lines.isEmpty()) {
//            textFile.delete();

            autoSizeColumns = 0;

            Workbook workBook = new XSSFWorkbook();

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

            sheet = workBook.createSheet("Report");

            lineCount = 0;

            for (Line line1 : lines) {

                line = line1;

                for (int y = 0; y < reportSection.getSection().size(); y++) {
                    if (reportSection.getSection().get(y).getName().trim().equals(line.getType().trim())
                            && y == line.getElement()) {
                        columns = new LinkedList<>();

                        columns = reportSection.getSection().get(y).getColumns();

                        break;
                    }
                }

                switch (line.getType().trim()) {
                    case "header":
                        setHeader(line.getLine());

                        break;

                    case "customer":
                        setCustomer(line.getLine());

                        break;

                    case "product":
                        setProduct(line.getLine());

                        break;

                    case "label":
                        setLabel(line.getLine());

                        break;

                    case "detail":
                        setDetail(line.getLine());

                        break;

                    case "footer":
                        setFooter(line.getLine());

                        break;
                }
            }

            for(short resizeColumn = 0; resizeColumn < autoSizeColumns; resizeColumn++) sheet.autoSizeColumn(resizeColumn);

            String xlsxFile = fileName + ".xlsx";

            FileOutputStream newXLSXfile = new FileOutputStream(configuration.getProperty("workingDirectory") + xlsxFile.trim());

            workBook.write(newXLSXfile);

            newXLSXfile.close();

            workBook.close();

            file = new File(configuration.getProperty("workingDirectory") + xlsxFile.trim());

        }
    }

    public String getFileName(){
        return file.getName();
    }

    private Report getReportSection(String jsonName, String matchToName) throws IOException {
        File jsonFile = new File(configuration.getProperty("jsonMaps") + jsonName.trim() + ".json");

        Report thisReport = null;

        ObjectMapper objectMapper = new ObjectMapper();

        List<Report> reports = objectMapper.readValue(jsonFile, new TypeReference<List<Report>>() {
        });

        for (Report report : reports) {
            if (matchToName.trim().toLowerCase().contains(report.getTitle().trim().toLowerCase())) {
                thisReport = report;
                break;
            }
        }

        if(thisReport == null){
            throw new IOException("Report Conversion layout with title: " + matchToName + " not found in conversion file: " +jsonName);
        }
        return thisReport;
    }

    private static void setHeader(String parseLine) {
        StringBuilder headerString = new StringBuilder();

        row = sheet.createRow(lineCount);

        for (ReportColumn column : columns) {
            position = column.getPosition()[0];
            length = column.getPosition()[1];

            headerString.append(parseLine.substring(position - 1, Math.min(position + length, parseLine.length())).trim()).append("  ");
        }

        cell = row.createCell(0);

        cell.setCellValue(headerString.toString());

        cell.setCellStyle(headerTextStyle);

        sheet.addMergedRegion(new CellRangeAddress(lineCount, lineCount, 0, 9));

        lineCount++;
    }

    private static void setCustomer(String parseLine) {
        lineCount++;

        row = sheet.createRow(lineCount);

        for (int columnInfo = 0; columnInfo < columns.size(); columnInfo++) {
            position = columns.get(columnInfo).getPosition()[0];
            length = columns.get(columnInfo).getPosition()[1];

            switch (columns.get(columnInfo).getType()) {
                case "string":
                    cell = row.createCell(columnInfo);

                    cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length())));

                    cell.setCellStyle(subHeaderTextStyle);

                    break;

                case "integer":
                    cell = row.createCell(columnInfo);

                    try {
                        cell.setCellValue(Integer
                                .valueOf(parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                        .trim().replace("-", "")));

                    } catch (NumberFormatException e) {
                        cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                .trim());
                    }

                    break;

                case "double":

                    break;
            }
        }

        sheet.addMergedRegion(new CellRangeAddress(lineCount, lineCount, 0, 9));

        lineCount++;
    }

    private static void setProduct(String parseLine) {
        lineCount++;

        row = sheet.createRow(lineCount);

        headerLoop(parseLine, subHeaderTextStyle);

        sheet.addMergedRegion(new CellRangeAddress(lineCount, lineCount, 0, 9));

        lineCount++;
    }

    private static void setLabel(String parseLine) {
        row = sheet.createRow(lineCount);

        autoSizeColumns = columns.size();

        headerLoop(parseLine, labelTextStyle);

        lineCount++;
    }

    private static void headerLoop(String parseLine, CellStyle subHeaderTextStyle) {
        for (int columnInfo = 0; columnInfo < columns.size(); columnInfo++) {
            position = columns.get(columnInfo).getPosition()[0];
            length = columns.get(columnInfo).getPosition()[1];

            switch (columns.get(columnInfo).getType()) {
                case "string":
                    cell = row.createCell(columnInfo);

                    cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length())));

                    cell.setCellStyle(subHeaderTextStyle);

                    break;

                case "integer":

                    break;

                case "double":

                    break;
            }
        }
    }

    private static void setDetail(String parseLine) {
        row = sheet.createRow(lineCount);

        for (int columnInfo = 0; columnInfo < columns.size(); columnInfo++) {
            position = columns.get(columnInfo).getPosition()[0];
            length = columns.get(columnInfo).getPosition()[1];

            switch (columns.get(columnInfo).getType()) {
                case "string":
                    cell = row.createCell(columnInfo);

                    cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length())));

                    break;

                case "integer":
                    cell = row.createCell(columnInfo);

                    if (columns.get(columnInfo).getFormat() != null && columns.get(columnInfo).getFormat().trim().equals("identification")) {
                        cell.setCellValue(Integer.valueOf(
                                parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                        .trim()));

                        cell.setCellStyle(identificationStyle);

                    } else {
                        parse(parseLine);

                        cell.setCellStyle(integerStyle);
                    }

                    break;

                case "double":
                    cell = row.createCell(columnInfo);

                    dupeCode(parseLine);

                    cell.setCellStyle(doubleStyle);

                    break;
            }
        }

        lineCount++;
    }

    private static void dupeCode(String parseLine) {
        try {
            if (parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                    .contains("-")) {
                cell.setCellValue(-1 * Double.valueOf(
                        parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                .trim().replace("-", "")));

            } else {
                cell.setCellValue(Double.valueOf(
                        parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                .trim().replace("-", "")));
            }

        } catch (NumberFormatException e) {
            cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                    .trim());
        }
    }

    private static void parse(String parseLine) {
        try {
            if (parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                    .contains("-")) {
                cell.setCellValue(-1 * Integer.valueOf(
                        parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                .trim().replace("-", "")));

            } else {
                cell.setCellValue(Integer.valueOf(
                        parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                                .trim().replace("-", "")));
            }

        } catch (NumberFormatException e) {
            cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length()))
                    .trim());

        }
    }

    private static void setFooter(String parseLine) {
        row = sheet.createRow(lineCount);

        for (int columnInfo = 0; columnInfo < columns.size(); columnInfo++) {
            position = columns.get(columnInfo).getPosition()[0];
            length = columns.get(columnInfo).getPosition()[1];

            switch (columns.get(columnInfo).getType()) {
                case "string":
                    cell = row.createCell(columnInfo);

                    cell.setCellValue(parseLine.substring(position - 1, Math.min(position + length, parseLine.length())));

                    cell.setCellStyle(totalTextStyle);

                    break;

                case "integer":
                    cell = row.createCell(columnInfo);

                    parse(parseLine);

                    cell.setCellStyle(totalIntegerStyle);

                    break;

                case "double":
                    cell = row.createCell(columnInfo);

                    dupeCode(parseLine);

                    cell.setCellStyle(totalDoubleStyle);

                    break;
            }
        }

        lineCount++;
    }

}
