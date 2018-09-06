package com.prairiefarms.export;

import com.prairiefarms.export.factory.ExcelWorkbookFactory;
import com.prairiefarms.export.factory.MimeMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EmailTest {

    private Email testSubject;

    //testSubject internal State
    private static String documentNameTestInput = "documentNameTestInput";
    @Mock
    private ExcelWorkbookFactory mockExcelWorkbookFactory;
    @Mock
    private Configuration mockConfiguration;
    private ArrayList<String> arrayListState;
    @Mock
    private MimeMessageFactory mockMimeMessageFactory;

    //test Input
    private static List<String> addressesTestInput = Arrays.asList("addresses Test Input Value".split(" "));
    private static String subjectLineTestInput = "subjectLineTestInputValue";
    private static String messageBodyTestInput = "messageBodyTestInputValue";


    @Before
    public void setup() throws MessagingException, IOException {
        MockitoAnnotations.initMocks(this);
        arrayListState = new ArrayList<>();
        when(mockConfiguration.getProperty("workingDirectory")).thenReturn("workingDirectory/");
        ExcelWorkbook excelWorkbook = mock(ExcelWorkbook.class);
        when(excelWorkbook.getFileName()).thenReturn(documentNameTestInput + ".xlsx");
        when(mockExcelWorkbookFactory.newExcelWorkbook(documentNameTestInput)).thenReturn(excelWorkbook);
        testSubject = new Email(documentNameTestInput,
                mockExcelWorkbookFactory,
                mockConfiguration,
                arrayListState,
                mockMimeMessageFactory);
    }

    @Test
    public void testSendSendsExpectedEmail() throws MessagingException {
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        verify(mockMimeMessageFactory).newMimeMessage(addressesTestInput,
                subjectLineTestInput,
                testSubject.getMessageContent(messageBodyTestInput));
    }

    @Test
    public void testSendSendsWithExpectedAttachments() throws MessagingException {
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        assertEquals(3, arrayListState.size());
        assertTrue(arrayListState.contains(mockConfiguration.getProperty("workingDirectory") + documentNameTestInput + ".pdf"));
        assertTrue(arrayListState.contains(mockConfiguration.getProperty("workingDirectory") + documentNameTestInput + ".txt"));
        assertTrue(arrayListState.contains(mockConfiguration.getProperty("workingDirectory") + documentNameTestInput + ".xlsx"));
    }

    @Test
    public void testSendOmitsXlsxAttachmentOnError() throws MessagingException, IOException {
        when(mockExcelWorkbookFactory.newExcelWorkbook(documentNameTestInput)).thenThrow(new RuntimeException());
        arrayListState = new ArrayList<>();
        testSubject = new Email(documentNameTestInput,
                mockExcelWorkbookFactory,
                mockConfiguration,
                arrayListState,
                mockMimeMessageFactory);
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        assertEquals(2, arrayListState.size());
        assertTrue(arrayListState.contains(mockConfiguration.getProperty("workingDirectory") + documentNameTestInput + ".pdf"));
        assertTrue(arrayListState.contains(mockConfiguration.getProperty("workingDirectory") + documentNameTestInput + ".txt"));
    }
}