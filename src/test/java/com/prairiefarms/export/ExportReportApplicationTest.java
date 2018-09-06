package com.prairiefarms.export;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

public class ExportReportApplicationTest {

    private ExportReportApplication testSubject;
    @Mock
    private Email mockEmail;
    @Mock
    private IfsCleanup mockIfsCleanup;

    private static String[] testInput = {"reportNameTestInputValue",
            "recipients Test Input Value",
            "subjectLineTestInputValue",
            "messageBodyTestInputValue"};
    private static List<String> toAddressesList = Arrays.asList(testInput[1].split(" "));

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        testSubject = new ExportReportApplication(mockEmail, mockIfsCleanup);
    }

    @Test
    public void testRunEmailsSpecifiedFile() throws MessagingException {
        testSubject.run(testInput);
        verify(mockEmail).send(toAddressesList, testInput[2], testInput[3]);
    }

    @Test
    public void testRunRemovesLeftoverIfsFiles() throws MessagingException {
        testSubject.run(testInput);
        verify(mockIfsCleanup).remove(testInput[0]);
    }
}