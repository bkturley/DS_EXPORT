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
    private static String[] testInput = {"0", "1", "2", "3"};
    @Mock
    ExportReportApplication mockExportReportApplication;
    @Mock
    LoggerAccess mockLoggerAccess;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testSubject = new Export(mockExportReportApplication, mockLoggerAccess);
    }

    @Test
    public void testCallsAreLogged(){
        verifyZeroInteractions(mockLoggerAccess);
        testSubject.main(testInput);
        verify(mockLoggerAccess).log("called with params: "  + testInput[0] + " " + testInput[1] + " " + testInput[2] + " " + testInput[3]);

    }

    @Test
    public void testApplicationIsRunWhenCalled(){
        verifyZeroInteractions(mockExportReportApplication);
        testSubject.main(testInput);
        verify(mockExportReportApplication).run(testInput);
    }

    @Test
    public void testExceptionsAreLogged(){
        verifyZeroInteractions(mockLoggerAccess);
        testSubject.main(null);
        verify(mockLoggerAccess).log(any(Exception.class));
    }

}