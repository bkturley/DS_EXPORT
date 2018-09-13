package com.prairiefarms.export.access;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class SessionAccess {
    private ConfigurationAccess configurationAccess = new ConfigurationAccess();
    public Session getSession(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", configurationAccess.getProperty("emailHost"));
        props.put("mail.smtp.port", "25");

        return  Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configurationAccess.getProperty("user"),
                        configurationAccess.getProperty("password"));
            }
        });
    }
}
