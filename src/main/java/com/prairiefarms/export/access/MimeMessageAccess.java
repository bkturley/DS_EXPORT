package com.prairiefarms.export.access;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

public class MimeMessageAccess {
    private SessionAccess sessionAccess = new SessionAccess();
    private ConfigurationAccess configurationAccess = new ConfigurationAccess();
    public void newMimeMessage(List<String> addresses, String subjectLine, Multipart messageContent) throws MessagingException {
        MimeMessage message = new MimeMessage(sessionAccess.getSession());

        message.setFrom(new InternetAddress(configurationAccess.getProperty("defaultSenderEmailAddress").trim().trim()));

        for(String address : addresses){
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
        }

        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(""));
        message.setSubject(subjectLine);
        message.setContent(messageContent);
        message.setSentDate(new Date());
        Transport.send(message);
    }
}
