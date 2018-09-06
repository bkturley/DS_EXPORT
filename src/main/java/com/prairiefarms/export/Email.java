package com.prairiefarms.export;

import com.prairiefarms.export.factory.ExcelWorkbookFactory;
import com.prairiefarms.export.factory.MimeMessageFactory;

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



public class Email {

    private String xlsxError = "";

    private ExcelWorkbookFactory excelWorkbookFactory;
    private Configuration configuration;
    private ArrayList<String> attachments;
    private MimeMessageFactory mimeMessageFactory;

    private Multipart multipart = new MimeMultipart();


    public Email(String documentName){
        this(documentName,
                new ExcelWorkbookFactory(),
                new Configuration(),
                new ArrayList<String>(),
                new MimeMessageFactory());
    }

    public Email(String documentName,
                 ExcelWorkbookFactory excelWorkbookFactory,
                 Configuration configuration,
                 ArrayList<String> attachments,
                 MimeMessageFactory mimeMessageFactory){
        this.excelWorkbookFactory = excelWorkbookFactory;
        this.configuration = configuration;
        this.attachments = attachments;
        this.mimeMessageFactory = mimeMessageFactory;

        this.attachments.add(getTxtFileAttachment(documentName));
        this.attachments.add(getPdfFileAttachment(documentName));
        try {
            this.attachments.add(getXlsxFileAttachment(documentName));
        }catch (java.lang.Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            xlsxError = "Problem converting to Xlsx format. Technical details: <br>" + sw.toString();
        }
    }

    void send(List<String> toAddresses, String subjectLine, String messageBodyText) throws MessagingException {
        mimeMessageFactory.newMimeMessage(toAddresses, subjectLine, getMessageContent(messageBodyText));
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

        for (String anAttachment : attachments) {
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

    private String getTxtFileAttachment(String fileName){
        return getFileAsAttachment(fileName, ".txt");
    }

    private String getPdfFileAttachment(String fileName){
        return getFileAsAttachment(fileName, ".pdf");
    }

    private String getFileAsAttachment(String fileName, String fileExtension){
        return configuration.getProperty("workingDirectory")
                + fileName
                + fileExtension;
    }

    private String getXlsxFileAttachment(String fileName) throws IOException {
        return configuration.getProperty("workingDirectory") + excelWorkbookFactory.newExcelWorkbook(fileName).getFileName();
    }
}
