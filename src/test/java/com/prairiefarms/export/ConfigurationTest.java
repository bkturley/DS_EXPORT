package com.prairiefarms.export;

import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigurationTest {

    private Configuration testSubject;
    private Properties properties;

    @Before
    public void setup(){
        properties = new  Properties();
        testSubject = new Configuration(properties);
    }

    @Test
    public void testGetPropertyReturnsExpectedProperty() {
        String testKey = "testKeyValue";
        String testValue = "testValueValue";
        properties.setProperty(testKey, testValue);
        assertEquals(testValue, testSubject.getProperty(testKey));
    }

    @Test
    public void testGetListReturnsExpectedList() {
        String testKey = "testKeyValue";
        String testValue = "one two three";
        properties.setProperty(testKey, testValue);
        List<String> expectedResult = Arrays.asList(testValue.split(" "));
        List<String> testResult = testSubject.getList(testKey);
        assertEquals(expectedResult.size(), testResult.size());
        assertTrue(testResult.containsAll(expectedResult));
    }
}