package com.prairiefarms.export;

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

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testSubject = new Export(mockExportReportApplication);
    }

    @Test
    public void testMainDelegatesToApplication() throws MessagingException {

        testSubject.main(testInput);
        verify(mockExportReportApplication).run(testInput);
    }

    @Test
    public void testMainSwallowsExceptions() throws MessagingException {
        java.lang.RuntimeException expectedException = mock(java.lang.RuntimeException.class);
        doThrow(expectedException).when(mockExportReportApplication).run(testInput);
        testSubject.main(testInput);
        verify(expectedException).printStackTrace();
    }
}