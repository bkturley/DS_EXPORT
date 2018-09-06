package com.prairiefarms.export.factory;

import com.prairiefarms.export.Configuration;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

public class MimeMessageFactory {
    SessionFactory sessionFactory = new SessionFactory();
    Configuration configuration = new Configuration();
    public void newMimeMessage(List<String> addresses, String subjectLine, Multipart messageContent) throws MessagingException {
        MimeMessage message = new MimeMessage(sessionFactory.getSession());

        message.setFrom(new InternetAddress(configuration.getProperty("defaultSenderEmailAddress").trim().trim()));

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
