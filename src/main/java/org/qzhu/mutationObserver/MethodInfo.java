package org.qzhu.mutationObserver;

import java.util.ArrayList;

public class MethodInfo {
    public int start_line;
    public int stop_line;
    public String method_name;
    public ArrayList<String> method_sequence;
    public int total_mut;
    public int kill_mut;
    public ArrayList<String> methodModifier;
    public boolean isVoid;
    public Node<String> methodTreeRoot;

    public MethodInfo(int start_line, int stop_line, String method_name) {
        this.start_line = start_line;
        this.stop_line = stop_line;
        this.method_name = method_name;
        this.total_mut = 0;
        this.kill_mut = 0;
    }
    

    public void setMethod_sequence(ArrayList<String> method_sequence) {
        this.method_sequence = method_sequence;
    }

    public int getMutationScore(){
        int mutScore =0;
        if (total_mut!=0){
            mutScore = kill_mut/total_mut;
        }
        return mutScore;
    }

}
