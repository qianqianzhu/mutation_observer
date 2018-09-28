package org.qzhu.mutationObserver.source;

import java.util.ArrayList;

/**
 * @author Qianqian Zhu
 */
public class MethodInfo {
    public int start_line;
    public int stop_line;
    public String className;
    public String method_name;
    public String bytecodeName;
    public ArrayList<String> method_sequence;
    public int total_mut;
    public int kill_mut;
    public ArrayList<String> methodModifier;
    public boolean isVoid;
    public boolean isGetter;   // whether this method is a getter method
    public boolean isNested;   // whether this method is in a nested class
    public Node<String> methodTreeRoot;
    public ArrayList<String> directTestCases = new ArrayList<>();

    public MethodInfo(int start_line, int stop_line, String className,String method_name) {
        this.start_line = start_line;
        this.stop_line = stop_line;
        this.className = className;
        this.method_name = method_name;
        this.total_mut = 0;
        this.kill_mut = 0;
    }

    public void setMethod_sequence(ArrayList<String> method_sequence) {
        this.method_sequence = method_sequence;
    }



}
