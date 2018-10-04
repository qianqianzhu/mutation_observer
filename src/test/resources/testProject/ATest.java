package org.testproject;

        import org.junit.Test;
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
    }

    @Test
    public void testC(){
        classA.methodA();
    }

    @Test
    public void testD(){
        classA.methodD();
    }
}

