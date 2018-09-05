package com.prairiefarms.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class Email {

    private Configuration configuration = new Configuration();

    private ArrayList<String> attachments = new ArrayList<>();
    private String xlsxError = "";

    Email(String documentName){
        attachments.add(getTxtFileAttachment(documentName));
        attachments.add(getPdfFileAttachment(documentName));
        try {
            attachments.add(getXlsxFileAttachment(documentName));
        }catch (java.lang.Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            xlsxError = "Problem converting to Xlsx format. Technical details: <br>" + sw.toString();
            e.printStackTrace();
        }
    }



    void send(String addresses, String subjectLine, String messageBodyText) throws MessagingException {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", configuration.getProperty("emailHost"));
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configuration.getProperty("user"),
                        configuration.getProperty("password"));
            }
        });


        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(configuration.getProperty("defaultSenderEmailAddress").trim().trim()));

        String[] toRecipients = addresses.trim().split(" ");

        for(String toRecipient : toRecipients){
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toRecipient));
        }

        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(""));
        message.setSubject(subjectLine);

        Multipart multipart = new MimeMultipart();

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

        message.setContent(multipart);

        message.setSentDate(new Date());

        Transport.send(message);

    }

    private String getTxtFileAttachment(String fileName){
        return configuration.getProperty("workingDirectory")
                + fileName
                + ".txt";
    }

    private String getPdfFileAttachment(String fileName){
        return configuration.getProperty("workingDirectory")
                + fileName
                + ".pdf";
    }

    private String getXlsxFileAttachment(String fileName) throws IOException {
        return configuration.getProperty("workingDirectory") + new ExcelWorkbook(fileName).getFileName();
    }
}
