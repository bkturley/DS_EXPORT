package com.prairiefarms.export.access;

import org.apache.poi.ss.usermodel.*;


// developer note: This class is more complex than expected because calls to Workbook.createCellStyle() must be kept to a minimum.
// calling Workbook.createCellStyle() for every cell in a sheet will break Apache poi.
public class CellStyleAccess {

    private WorkbookAccess workbookAccess = new WorkbookAccess();

    private CellStyle stringCellStyle;
    private CellStyle integerCellStyle;
    private CellStyle twoDecimalCellStyle;
    private CellStyle fourDecimalCellStyle;

    public CellStyleAccess(){
        stringCellStyle = getStringCellStyle();
        integerCellStyle = getIntegerCellStyle();
        twoDecimalCellStyle = getTwoDecimalCellStyle();
        fourDecimalCellStyle = getFourDecimalCellStyle();
    }

    private CellStyle getFourDecimalCellStyle() {
        CellStyle returnMe;
        if(fourDecimalCellStyle != null){
            returnMe = fourDecimalCellStyle;
        }else {
            fourDecimalCellStyle = workbookAccess.getInstance().createCellStyle();
            fourDecimalCellStyle.setDataFormat(workbookAccess.getInstance().createDataFormat().getFormat("_(* #,##0.0000_);[RED]_(* \\(#,##0.0000\\);_(* -??_);_(@_)"));
            fourDecimalCellStyle.setAlignment(HorizontalAlignment.CENTER);
            returnMe = fourDecimalCellStyle;
        }
        return returnMe;
    }

    private CellStyle getTwoDecimalCellStyle() {
        CellStyle returnMe;
        if(twoDecimalCellStyle != null){
            returnMe = twoDecimalCellStyle;
        }else {
            twoDecimalCellStyle = workbookAccess.getInstance().createCellStyle();
            twoDecimalCellStyle.setDataFormat(workbookAccess.getInstance().createDataFormat().getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
            twoDecimalCellStyle.setAlignment(HorizontalAlignment.CENTER);
            returnMe = twoDecimalCellStyle;
        }
        return returnMe;
    }

    private CellStyle getIntegerCellStyle() {
        CellStyle returnMe;
        if(integerCellStyle != null){
            returnMe = integerCellStyle;
        }else {
            integerCellStyle = workbookAccess.getInstance().createCellStyle();
            integerCellStyle.setDataFormat(workbookAccess.getInstance().createDataFormat().getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
            integerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            returnMe = integerCellStyle;
        }
        return returnMe;
    }

    private CellStyle getStringCellStyle() {
        CellStyle returnMe;
        if(stringCellStyle != null){
            returnMe = stringCellStyle;
        }else{
            stringCellStyle = workbookAccess.getInstance().createCellStyle();
            stringCellStyle.setAlignment(HorizontalAlignment.CENTER);
            returnMe = stringCellStyle;
        }
        return returnMe;
    }

    public CellStyle newCellStyle(String cellType) {
        CellStyle returnMe;
        switch (cellType){
            case "integer":
                returnMe = getIntegerCellStyle();
                break;
            case "2decimal":
                returnMe = getTwoDecimalCellStyle();
                break;
            case "4decimal":
                returnMe = getFourDecimalCellStyle();
                break;
            case "string":
            case "blank":
            default:
                returnMe = getStringCellStyle();
        }
        return returnMe;
    }

}
