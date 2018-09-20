package com.prairiefarms.export;

import com.prairiefarms.export.access.LoggerAccess;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;

import static org.mockito.Mockito.*;

public class ExportTest {

    private Export testSubject;
    private static String[] testInput = {"1", "2", "3"};
    @Mock
    ExportReportApplication mockExportReportApplication;
    @Mock
    LoggerAccess mockLoggerAccess;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testSubject = new Export(mockExportReportApplication, mockLoggerAccess);
    }

}