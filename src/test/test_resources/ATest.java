package org.testproject;
import org.junit.*;

import static junit.framework.Assert.assertNotNull;

public class ATest{
    A classA = new A();

    @Test @Ignore
    public void testA(){
        classA.methodA();
        testC();
    }

    @Test @Ignore
    public void testB(){
        testA();
        assertNotNull(classA);
    }

    @Test @Ignore
    public void testC(){
        classA.methodA();
        assertNotNull(classA);
        assertNotNull(classA);
    }

    @Test @Ignore
    public void testD(){
        classA.methodD();
        assertNotNull(classA);
        assertNotNull(classA);
        assertNotNull(classA);
    }
}


