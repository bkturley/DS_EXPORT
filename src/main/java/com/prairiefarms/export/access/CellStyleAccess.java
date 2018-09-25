package com.prairiefarms.export.access;

import org.apache.poi.ss.usermodel.*;

public class CellStyleAccess {

    private WorkbookAccess workbookAccess = new WorkbookAccess();

    private Workbook workbook;

    private CellStyle stringCellStyle;
    private CellStyle integerCellStyle;
    private CellStyle twoDecimalCellStyle;
    private CellStyle fourDecimalCellStyle;

    public CellStyleAccess(){
        workbook = workbookAccess.getInstance();

        integerCellStyle = workbook.createCellStyle();
        integerCellStyle.setDataFormat(workbook.createDataFormat().getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
        integerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        twoDecimalCellStyle = workbook.createCellStyle();
        twoDecimalCellStyle.setDataFormat(workbook.createDataFormat().getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
        twoDecimalCellStyle.setAlignment(HorizontalAlignment.CENTER);

        stringCellStyle = workbook.createCellStyle();
        Font detailTextFont = workbook.createFont();
        stringCellStyle.setAlignment(HorizontalAlignment.CENTER);
        stringCellStyle.setFont(detailTextFont);

        fourDecimalCellStyle = workbook.createCellStyle();
        fourDecimalCellStyle.setDataFormat(workbook.createDataFormat().getFormat("_(* #,##0.0000_);[RED]_(* \\(#,##0.0000\\);_(* -??_);_(@_)"));
        fourDecimalCellStyle.setAlignment(HorizontalAlignment.CENTER);
    }


    public CellStyle newCellStyle(String cellType) {
        CellStyle returnMe;
        switch (cellType){
            case "integer":
                returnMe = integerCellStyle;
                break;
            case "2decimal":
                returnMe = twoDecimalCellStyle;
                break;
            case "4decimal":
                returnMe = fourDecimalCellStyle;
                break;
            case "string":
            case "blank":
            default:
                returnMe = stringCellStyle;
        }
        return returnMe;
    }

}
