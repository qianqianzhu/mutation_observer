package org.qzhu.mutationObserver.jhawk;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.qzhu.mutationObserver.Utils.getAllMethodInfoFromSource;
import static org.qzhu.mutationObserver.Utils.parsePitestFile;
import static org.qzhu.mutationObserver.jhawk.JhawkMatcher.matchJhawkMethod;
import static org.qzhu.mutationObserver.jhawk.JhawkMatcher.parseJhawkResults;

/**
 * @author Qianqian Zhu
 */
public class JhawkMatcherTest {

    @Test
    public void testParseJhawkResults(){
        String jhawkFilename = "/Users/qianqianzhu/phd/testability/ast/jhawk/jfreechart-1.5.0-ChartFactory_method.csv";
        HashMap<String, ArrayList<String>> jhawkMethodMap = parseJhawkResults(jhawkFilename);
        int[] expected = {3,13,16,71,74};
        assertEquals(jhawkMethodMap.size(),30);
        ArrayList<String> methodLines = jhawkMethodMap.get("org.jfree.chart.ChartFactory:createPieChart");
        assertEquals(methodLines.size(),5);
        for (int i=0;i<methodLines.size();i++){
            String[] columns = methodLines.get(i).split(";");
            assertEquals(Integer.parseInt(columns[14]),expected[i]);
        }
    }

    @Test
    public void testMatchJhawkMethod() throws IOException {
        String fileName = "./src/test/resources/ChartFactory.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        String pitestFileName = "/Users/qianqianzhu/phd/testability/Observer/pitest_result/jfreechart-1.5.0_mutations.csv";
        parsePitestFile(pitestFileName,allMethodInfo);
        String jhawkFilename = "/Users/qianqianzhu/phd/testability/ast/jhawk/jfreechart-1.5.0-ChartFactory_method.csv";
        String resultFilename = "/Users/qianqianzhu/phd/testability/ast/jhawk/jfreechart-1.5.0-ChartFactory_test_result.csv";
        matchJhawkMethod(jhawkFilename,resultFilename,allMethodInfo);

    }

}
