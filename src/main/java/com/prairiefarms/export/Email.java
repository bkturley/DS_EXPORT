package com.prairiefarms.export;

import com.prairiefarms.export.access.ConfigurationAccess;
import com.prairiefarms.export.access.FileAccess;
import com.prairiefarms.export.access.LoggerAccess;
import com.prairiefarms.export.factory.ExcelWorkbookFileFactory;
import com.prairiefarms.export.access.MimeMessageAccess;
import org.apache.commons.lang3.StringUtils;

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
    private ConfigurationAccess configurationAccess;
    private List<String> attachmentPaths;
    private MimeMessageAccess mimeMessageAccess;
    private FileAccess fileAccess;

    private Multipart multipart = new MimeMultipart();

    public Email(String documentName){
        this(documentName,
                new ExcelWorkbookFileFactory(),
                new ConfigurationAccess(),
                new ArrayList<String>(),
                new MimeMessageAccess(),
                new FileAccess());
    }

    public Email(String documentName,
                 ExcelWorkbookFileFactory excelWorkbookFactory,
                 ConfigurationAccess configurationAccess,
                 List<String> attachmentPaths,
                 MimeMessageAccess mimeMessageAccess,
                 FileAccess fileAccess){
        this.excelWorkbookFactory = excelWorkbookFactory;
        this.configurationAccess = configurationAccess;
        this.attachmentPaths = attachmentPaths;
        this.mimeMessageAccess = mimeMessageAccess;
        this.fileAccess = fileAccess;
        this.attachmentPaths.addAll(getAttachment(documentName));
    }

    public void send(List<String> toAddresses, String subjectLine, String messageBodyText){
        try{
            if(StringUtils.isNotBlank(xlsxError)){
                List<String> errorNotificationToAddresses = new ArrayList<>();
                errorNotificationToAddresses.addAll(toAddresses);
                errorNotificationToAddresses.add(configurationAccess.getProperty("errorNotificationEmailAddress"));
                mimeMessageAccess.newMimeMessage(errorNotificationToAddresses, subjectLine, getMessageContent(messageBodyText));
            }else {
                mimeMessageAccess.newMimeMessage(toAddresses, subjectLine, getMessageContent(messageBodyText));
            }
        }catch (MessagingException messagingException){
            new LoggerAccess().log(messagingException);
        }finally {
            for(String filePath : attachmentPaths){
                fileAccess.deleteFile(filePath);
            }
        }
    }

    Multipart getMessageContent(String messageBodyText) throws MessagingException {

        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(configurationAccess.getProperty("defaultMessageBody")
                + "<br><br>"
                + messageBodyText
                + "<br><br>"
                + xlsxError
                + "<br><br>"
                + configurationAccess.getProperty("disclaimer"), "text/html");


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
        return fileAccess.getFile(configurationAccess.getProperty("workingDirectory")
                + fileName
                + fileExtension);
    }

    private String getXlsxFilePath(String fileName) throws IOException {
        return excelWorkbookFactory.newExcelWorkbookFile(fileName).getAbsolutePath();
    }
}
