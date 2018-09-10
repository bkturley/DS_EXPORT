package com.prairiefarms.export;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        List<String> expectedArrayList = Arrays.asList("txt", "pdf", "xlsx");
        when(mockEmail.send(toAddressesList, testInput[2], testInput[3])).thenReturn(expectedArrayList);
        testSubject.run(testInput);
        verify(mockIfsCleanup).remove(expectedArrayList);
    }
}