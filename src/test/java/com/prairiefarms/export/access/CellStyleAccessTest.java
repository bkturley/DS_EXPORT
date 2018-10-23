package com.prairiefarms.export.access;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellStyleAccessTest {

    private CellStyleAccess testSubject;
    private WorkbookAccess workbookAccess = new WorkbookAccess();

    @Before
    public void setUp(){
        testSubject = new CellStyleAccess();
    }

    @Test
    public void testNewCellStyleReturnsExpectedStyleForInteger() {
        assertEquals(getIntegerCellStyle(), testSubject.newCellStyle("integer"));
    }

    @Test
    public void testNewCellStyleReturnsExpectedStyleFor2Decimal() {
        assertEquals(getTwoDecimalCellStyle(), testSubject.newCellStyle("2decimal"));
    }

    @Test
    public void testNewCellStyleReturnsExpectedStyleFor4Decimal() {
        assertEquals(getFourDecimalCellStyle(), testSubject.newCellStyle("4decimal"));
    }

    @Test
    public void testNewCellStyleReturnsExpectedStyleForString() {
        assertEquals(getStringCellStyle(), testSubject.newCellStyle("string"));
    }

    @Test
    public void testNewCellStyleReturnsExpectedStyleForUnknown() {
        assertEquals(getStringCellStyle(), testSubject.newCellStyle("fubar"));
    }


    private CellStyle getIntegerCellStyle() {
        CellStyle returnMe = workbookAccess.getInstance().createCellStyle();
        returnMe.setDataFormat(workbookAccess.getInstance().createDataFormat().getFormat("_(* #,##0_);[RED]_(* \\(#,##0\\);_(* -??_);_(@_)"));
        returnMe.setAlignment(HorizontalAlignment.CENTER);
        return returnMe;
    }

    private CellStyle getTwoDecimalCellStyle() {
        CellStyle returnMe = workbookAccess.getInstance().createCellStyle();
        returnMe.setDataFormat(workbookAccess.getInstance().createDataFormat().getFormat("_(* #,##0.00_);[RED]_(* \\(#,##0.00\\);_(* -??_);_(@_)"));
        returnMe.setAlignment(HorizontalAlignment.CENTER);
        return returnMe;
    }

    private CellStyle getFourDecimalCellStyle() {
        CellStyle returnMe = workbookAccess.getInstance().createCellStyle();
        returnMe.setDataFormat(workbookAccess.getInstance().createDataFormat().getFormat("_(* #,##0.0000_);[RED]_(* \\(#,##0.0000\\);_(* -??_);_(@_)"));
        returnMe.setAlignment(HorizontalAlignment.CENTER);
        return returnMe;
    }

    private CellStyle getStringCellStyle() {
        CellStyle returnMe = workbookAccess.getInstance().createCellStyle();
        returnMe.setAlignment(HorizontalAlignment.CENTER);
        return returnMe;
    }
}