package org.qzhu.mutationObserver.source;

/**
 * @author Qianqian Zhu
 */
public class ClassInfo {

     public int voidMethodNo;
     public int totalMethodNo;
     public int getterMethodNo;
     private String className;

    public ClassInfo(String className) {
        this.className = className;
    }

    public double getVoidNonVoidPercent(){
         return ((double)voidMethodNo)/(totalMethodNo-voidMethodNo);
     }

    public double getVoidGetterPercent(){
        return ((double)voidMethodNo)/getterMethodNo;
    }

}

