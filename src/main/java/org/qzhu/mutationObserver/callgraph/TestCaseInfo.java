package org.qzhu.mutationObserver.callgraph;

import java.util.ArrayList;

/**
 * @author Qianqian Zhu
 */
public class TestCaseInfo {
    public int NLOC=0;
    public String name;
    public int assertNo=0;
    public ArrayList<String> methodCalls = new ArrayList<>();

    public TestCaseInfo(String name){
        this.name = name;
    }

}
