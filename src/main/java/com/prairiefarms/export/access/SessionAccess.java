package com.prairiefarms.export.access;

import com.prairiefarms.export.factory.AuthenticatorFactory;

import javax.mail.Session;
import java.util.Properties;

public class SessionAccess {

    private ConfigurationAccess configurationAccess;
    private AuthenticatorFactory authenticatorFactory;

    public SessionAccess(){
        this(new ConfigurationAccess(), new AuthenticatorFactory());
    }

    SessionAccess(ConfigurationAccess configurationAccess, AuthenticatorFactory authenticatorFactory){
        this.configurationAccess = configurationAccess;
        this.authenticatorFactory = authenticatorFactory;
    }

    public Session getSession(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", configurationAccess.getProperty("emailHost"));
        props.put("mail.smtp.port", "25");
        return  Session.getInstance(props, authenticatorFactory.newAuthentication());
    }
}
