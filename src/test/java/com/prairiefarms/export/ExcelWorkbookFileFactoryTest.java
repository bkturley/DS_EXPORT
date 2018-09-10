package com.prairiefarms.export;

import com.prairiefarms.export.factory.ReportFactory;
import com.prairiefarms.export.factory.WriteableReportDataFactory;
import com.prairiefarms.export.factory.products.TextLineList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class ExcelWorkbookFileFactoryTest {

    private static final String MOCK_FILES_LOCATION = "mock/Files/location";
    private ExcelWorkbookFileFactory testSubject;

    private static String fileNameTestInput = "fileNameTestInputValue";
    @Mock
    TextLineList mockTextLineList;
    @Mock
    private ReportFactory mockReportFactory;
    @Mock
    private WriteableReportDataFactory mockWriteableReportDataFactory;
    @Mock
    private Writer mockWriter;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        testSubject = new ExcelWorkbookFileFactory(mockWriter);
    }

    @Test
    public void getFileName() {
//        assertEquals(fileNameTestInput + ".xlsx", testSubject.newExcelWorkbookFile());
    }

}