package com.prairiefarms.export.factory;

import com.prairiefarms.export.access.ConfigurationAccess;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class AuthenticatorFactory {

    private ConfigurationAccess configurationAccess;

    public AuthenticatorFactory() {
        this(new ConfigurationAccess());
    }

    AuthenticatorFactory(ConfigurationAccess configurationAccess) {
        this.configurationAccess = configurationAccess;
    }

    public Authenticator newAuthentication() {
        return new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configurationAccess.getProperty("user"),
                        configurationAccess.getProperty("password"));

            }
        };
    }
}
