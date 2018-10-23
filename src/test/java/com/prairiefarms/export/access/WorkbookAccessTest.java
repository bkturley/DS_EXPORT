package com.prairiefarms.export.access;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkbookAccessTest {


    private WorkbookAccess testsubject;

    @Before
    public void setup(){
        testsubject = new WorkbookAccess();
    }

    @Test
    public void testGetInstanceReturnsSameInstance() {
        assertEquals(testsubject.getInstance(), testsubject.getInstance());
    }
}