package com.prairiefarms.export.factory;

import org.apache.poi.ss.usermodel.*;

public class CellStyleFactory {

    Workbook workbook;

    private CellStyle headerCellStyle;
    private CellStyle columnLabelCellStyle;

    private CellStyle detailStringCellStyle;
    private CellStyle detailIntegerCellStyle;
    private CellStyle detailDoubleCellStyle;


    private CellStyle totalStringCellStyle;

    public CellStyleFactory(Workbook workbook){
        this.workbook = workbook;
    }

    public CellStyle newCellStyle(String cellType) {
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
            detailStringCellStyle = workbook.createCellStyle();
            Font detailTextFont = workbook.createFont();
            detailStringCellStyle.setAlignment(HorizontalAlignment.CENTER);
            detailStringCellStyle.setFont(detailTextFont);
        }
        return detailStringCellStyle;
    }

    private CellStyle getHeaderTextStyle() {
        if (headerCellStyle == null) {
            headerCellStyle = workbook.createCellStyle();
            Font headerTextFont = workbook.createFont();
            headerTextFont.setBold(true);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setFont(headerTextFont);
        }
        return headerCellStyle;
    }

    private CellStyle getColumnLabelCellStyle() {
        if(columnLabelCellStyle == null){
            columnLabelCellStyle = workbook.createCellStyle();
            Font labelTextFont = workbook.createFont();
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
            totalStringCellStyle = workbook.createCellStyle();
            Font totalTextFont = workbook.createFont();
            totalTextFont.setBold(true);
            totalTextFont.setColor(IndexedColors.BLACK.index);
            totalStringCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            totalStringCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStringCellStyle.setAlignment(HorizontalAlignment.CENTER);
            totalStringCellStyle.setFont(totalTextFont);
        }
        return totalStringCellStyle;
    }

    private CellStyle getDetailIntegerCellStyle() {
        if(detailIntegerCellStyle == null){
            DataFormat integerFormat = workbook.createDataFormat();
            detailIntegerCellStyle = workbook.createCellStyle();
            detailIntegerCellStyle.setDataFormat(integerFormat.getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
            detailIntegerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return detailIntegerCellStyle;
    }

    private CellStyle getDetailDoubleCellStyle() {
        if(detailDoubleCellStyle == null){
            DataFormat doubleFormat = workbook.createDataFormat();
            detailDoubleCellStyle = workbook.createCellStyle();
            detailDoubleCellStyle.setDataFormat(doubleFormat.getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
            detailDoubleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        }
        return detailDoubleCellStyle;
    }

}
