package com.prairiefarms.export.access;

import com.prairiefarms.export.factory.AuthenticatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.mail.Session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SessionAccessTest {

    private SessionAccess testSubject;
    @Mock
    ConfigurationAccess mockConfigurationAccess;
    @Mock
    AuthenticatorFactory mockAuthenticatorFactory;
    private String expectedEmailHost = "expectedEmailHostValue";

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        testSubject = new SessionAccess(); //for 100% coverage
        testSubject = new SessionAccess(mockConfigurationAccess, mockAuthenticatorFactory);
        when(mockConfigurationAccess.getProperty("emailHost")).thenReturn(expectedEmailHost);
    }

    @Test
    public void testGetSessionReturnsExpectedSession() {
        Session testResult = testSubject.getSession();
        assertEquals(testResult.getProperty("mail.smtp.auth"), "true");
        assertEquals(testResult.getProperty("mail.smtp.starttls.enable"), "true");
        assertEquals(testResult.getProperty("mail.smtp.host"), expectedEmailHost);
        assertEquals(testResult.getProperty("mail.smtp.port"), "25");
        verify(mockAuthenticatorFactory).newAuthentication();
    }

}