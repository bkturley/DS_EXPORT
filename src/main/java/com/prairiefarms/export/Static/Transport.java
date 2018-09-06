package com.prairiefarms.export.Static;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class Transport {
    public void send(MimeMessage sendMe) throws MessagingException {
        javax.mail.Transport.send(sendMe);
    }
}
