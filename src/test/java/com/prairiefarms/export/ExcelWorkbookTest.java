package com.prairiefarms.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExcelWorkbookTest {

    private static final String MOCK_FILES_LOCATION = "mock/Files/location";
    private ExcelWorkbook testSubject;

    private static String fileNameTestInput = "fileNameTestInputValue";
    @Mock
    private ReportAccess mockReportAccess;
    @Mock
    private XSSFWorkbook mockXSSFWorkbook;
    @Mock
    private Configuration mockConfiguration;
    @Mock
    private FileLineList mockFileLineList;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        testSubject = new ExcelWorkbook(fileNameTestInput,
                mockReportAccess,
                mockXSSFWorkbook,
                mockConfiguration,
                mockFileLineList);
        when(mockConfiguration.getProperty("workingDirectory")).thenReturn(MOCK_FILES_LOCATION);
    }

    @Test
    public void getFileName() {
        assertEquals(fileNameTestInput, testSubject.getFileName());
    }

}