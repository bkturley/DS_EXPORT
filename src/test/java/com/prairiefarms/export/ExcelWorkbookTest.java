package com.prairiefarms.export;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;

public class ExcelWorkbookTest {

    private static final String MOCK_FILES_LOCATION = "mock/Files/location";
    private ExcelWorkbook testSubject;

    private static String fileNameTestInput = "fileNameTestInputValue";
    @Mock FileLineList mockFileLineList;
    @Mock
    private ReportAccess mockReportAccess;
    @Mock
    private WritableData mockWritableData;
    @Mock
    private Writer mockWriter;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        testSubject = new ExcelWorkbook(fileNameTestInput,
                mockFileLineList,
                mockReportAccess,
                mockWritableData,
                mockWriter);
    }

    @Test
    public void getFileName() {
        assertEquals(fileNameTestInput + ".xlsx", testSubject.getFileName());
    }

}