package org.qzhu.mutationObserver;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Qianqian Zhu
 */
public class MethodCollector {

    public LinkedList<String> methodNameCollector;
    public LinkedList<ArrayList<String>> methodSequenceCollector;

    public MethodCollector(){
        methodNameCollector = new LinkedList<>();
        methodSequenceCollector = new LinkedList<ArrayList<String>>();
    }

}
