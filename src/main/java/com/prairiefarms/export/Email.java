package com.prairiefarms.export;

import com.prairiefarms.export.access.FileAccess;
import com.prairiefarms.export.factory.ExcelWorkbookFileFactory;
import com.prairiefarms.export.factory.MimeMessageFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

class Email {

    private String xlsxError = "";

    private ExcelWorkbookFileFactory excelWorkbookFactory;
    private Configuration configuration;
    private List<String> attachmentPaths;
    private MimeMessageFactory mimeMessageFactory;
    private FileAccess fileAccess;

    private Multipart multipart = new MimeMultipart();

    public Email(String documentName){
        this(documentName,
                new ExcelWorkbookFileFactory(),
                new Configuration(),
                new ArrayList<String>(),
                new MimeMessageFactory(),
                new FileAccess());
    }

    public Email(String documentName,
                 ExcelWorkbookFileFactory excelWorkbookFactory,
                 Configuration configuration,
                 List<String> attachmentPaths,
                 MimeMessageFactory mimeMessageFactory,
                 FileAccess fileAccess){
        this.excelWorkbookFactory = excelWorkbookFactory;
        this.configuration = configuration;
        this.attachmentPaths = attachmentPaths;
        this.mimeMessageFactory = mimeMessageFactory;
        this.fileAccess = fileAccess;
        this.attachmentPaths.addAll(getAttachment(documentName));
    }

    public void send(List<String> toAddresses, String subjectLine, String messageBodyText) throws MessagingException {
        mimeMessageFactory.newMimeMessage(toAddresses, subjectLine, getMessageContent(messageBodyText));
        for(String filePath : attachmentPaths){
            fileAccess.deleteFile(filePath);
        }
    }

    Multipart getMessageContent(String messageBodyText) throws MessagingException {

        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(configuration.getProperty("defaultMessageBody")
                + "<br><br>"
                + messageBodyText
                + "<br><br>"
                + xlsxError
                + "<br><br>"
                + configuration.getProperty("disclaimer"), "text/html");


        multipart.addBodyPart(messageBodyPart);

        for (String anAttachment : attachmentPaths) {
            if (anAttachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(anAttachment.trim());
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(source.getName().trim());
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        return multipart;
    }

    private List<String> getAttachment(String documentName) {
        List<String> returnMe = new ArrayList<>();
        returnMe.add(getTxtFilePath(documentName));
        returnMe.add(getPdfFilePath(documentName));
        try {
            returnMe.add(getXlsxFilePath(documentName));
        }catch (java.lang.Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            xlsxError = "Problem converting to Xlsx format. Technical details: <br>" + sw.toString();
        }
        return returnMe;
    }

    private String getTxtFilePath(String fileName){
        return getAttachmentFile(fileName, ".txt").getAbsolutePath();
    }

    private String getPdfFilePath(String fileName){
        return getAttachmentFile(fileName, ".pdf").getAbsolutePath();
    }

    private File getAttachmentFile(String fileName, String fileExtension){
        return fileAccess.getFile(configuration.getProperty("workingDirectory")
                + fileName
                + fileExtension);
    }

    private String getXlsxFilePath(String fileName) throws IOException {
        return excelWorkbookFactory.newExcelWorkbookFile(fileName).getAbsolutePath();
    }
}
