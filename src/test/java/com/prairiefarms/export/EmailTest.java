package com.prairiefarms.export;

import com.prairiefarms.export.access.ConfigurationAccess;
import com.prairiefarms.export.access.FileAccess;
import com.prairiefarms.export.factory.ExcelWorkbookFileFactory;
import com.prairiefarms.export.access.MimeMessageAccess;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.MessagingException;
import java.io.File;
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
    private ExcelWorkbookFileFactory mockExcelWorkbookFactory;
    @Mock
    private ConfigurationAccess mockConfigurationAccess;
    private ArrayList<String> arrayListState;
    @Mock
    private MimeMessageAccess mockMimeMessageAccess;
    @Mock
    private FileAccess mockFileAccess;

    //test Input
    private static List<String> addressesTestInput = Arrays.asList("addresses Test Input Value".split(" "));
    private static String subjectLineTestInput = "subjectLineTestInputValue";
    private static String messageBodyTestInput = "messageBodyTestInputValue";


    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        arrayListState = new ArrayList<>();
        when(mockConfigurationAccess.getProperty("workingDirectory")).thenReturn("workingDirectory/");
        File xlsxFile = mock(File.class);
        when(xlsxFile.getAbsolutePath()).thenReturn("workingDirectory/" + documentNameTestInput + ".xlsx");
        File txtFile = mock(File.class);
        when(txtFile.getAbsolutePath()).thenReturn("workingDirectory/" + documentNameTestInput + ".txt");
        File pdfFile = mock(File.class);
        when(pdfFile.getAbsolutePath()).thenReturn("workingDirectory/" + documentNameTestInput + ".pdf");
        when(mockFileAccess.getFile("workingDirectory/" + documentNameTestInput + ".txt")).thenReturn(txtFile);
        when(mockFileAccess.getFile("workingDirectory/" + documentNameTestInput + ".pdf")).thenReturn(pdfFile);
        when(mockExcelWorkbookFactory.newExcelWorkbookFile(documentNameTestInput)).thenReturn(xlsxFile);
        testSubject = new Email(documentNameTestInput,
                mockExcelWorkbookFactory,
                mockConfigurationAccess,
                arrayListState,
                mockMimeMessageAccess,
                mockFileAccess);
    }

    @Test
    public void testSendSendsExpectedEmail() throws MessagingException {
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        verify(mockMimeMessageAccess).newMimeMessage(addressesTestInput,
                subjectLineTestInput,
                testSubject.getMessageContent(messageBodyTestInput));
    }

    @Test
    public void testSendSendsWithExpectedAttachments() throws MessagingException {
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        assertEquals(3, arrayListState.size());
        assertTrue(arrayListState.contains(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".pdf"));
        assertTrue(arrayListState.contains(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".txt"));
        assertTrue(arrayListState.contains(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".xlsx"));
    }

    @Test
    public void testSendOmitsXlsxAttachmentOnError() throws MessagingException, IOException {
        when(mockExcelWorkbookFactory.newExcelWorkbookFile(documentNameTestInput)).thenThrow(new RuntimeException());
        arrayListState = new ArrayList<>();
        testSubject = new Email(documentNameTestInput,
                mockExcelWorkbookFactory,
                mockConfigurationAccess,
                arrayListState,
                mockMimeMessageAccess,
                mockFileAccess);
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        assertEquals(2, arrayListState.size());
        assertTrue(arrayListState.contains(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".pdf"));
        assertTrue(arrayListState.contains(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".txt"));
    }

    @Test
    public void testSendCleansUpIfsFiles() {
        verify(mockFileAccess, times(0)).deleteFile(anyString());
        testSubject.send(addressesTestInput, subjectLineTestInput, messageBodyTestInput);
        verify(mockFileAccess).deleteFile(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".pdf");
        verify(mockFileAccess).deleteFile(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".txt");
        verify(mockFileAccess).deleteFile(mockConfigurationAccess.getProperty("workingDirectory") + documentNameTestInput + ".xlsx");
    }
}