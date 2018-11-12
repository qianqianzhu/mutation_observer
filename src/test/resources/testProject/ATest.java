package org.testproject;
import org.junit.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ATest{
    A classA = new A();

    @Test
    public void testA(){
        classA.methodA();
        testC();
    }

    @Test
    public void testB(){
        testA();
        assertNotNull(classA);
    }

    @Test
    public void testC(){
        classA.methodA();
        assertNotNull(classA);
        assertNotNull(classA);
    }

    @Test
    public void testD(){
        classA.methodD();
        assertNotNull(classA);
        assertNotNull(classA);
        assertNotNull(classA);
    }
}

