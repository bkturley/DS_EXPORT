package com.prairiefarms.export.access;

import org.junit.Before;
import org.junit.Test;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigurationAccessTest {

    private ConfigurationAccess testSubject;
    private Properties properties;

    @Before
    public void setup(){
        properties = new  Properties();
        testSubject = new ConfigurationAccess(properties);
    }

    @Test
    public void testGetPropertyReturnsExpectedProperty() {
        String testKey = "testKeyValue";
        String testValue = "testValueValue";
        properties.setProperty(testKey, testValue);
        assertEquals(testValue, testSubject.getProperty(testKey));
    }

}