package com.prairiefarms.export;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;

import static org.mockito.Mockito.verify;

public class ExportReportApplicationTest {

    private ExportReportApplication testSubject;
    @Mock
    private Email mockEmail;
    @Mock
    private IfsCleanup mockIfsCleanup;

    private static String[] testInput = {"reportNameTestInputValue",
                                  "recipientsTestInputValue",
                                  "subjectLineTestInputValue",
                                  "messageBodyTestInputValue"};

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testSubject = new ExportReportApplication(mockEmail, mockIfsCleanup);
    }

    @Test
    public void testRunEmailsSpecifiedFile() throws MessagingException {
        testSubject.run(testInput);
        verify(mockEmail).send(testInput[1], testInput[2], testInput[3]);
    }

    @Test
    public void testRunRemovesLeftoverIfsFiles() throws MessagingException {
        testSubject.run(testInput);
        verify(mockIfsCleanup).remove(testInput[0]);
    }
}