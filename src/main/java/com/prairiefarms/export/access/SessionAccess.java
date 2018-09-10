package com.prairiefarms.export.access;

import com.prairiefarms.export.Configuration;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class SessionAccess {
    Configuration configuration = new Configuration();
    public Session getSession(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", configuration.getProperty("emailHost"));
        props.put("mail.smtp.port", "25");

        return  Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configuration.getProperty("user"),
                        configuration.getProperty("password"));
            }
        });
    }
}
