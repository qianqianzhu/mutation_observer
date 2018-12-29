package org.testproject;

public class A{

    public void methodA(){
        methodB();
    }

    public void methodB(){
        methodE();
    }

    public void methodC(){
        methodB();
        methodD();
    }

    public void methodD(){
        methodC();
    }

    public void methodE(){
        methodC();
    }
}
