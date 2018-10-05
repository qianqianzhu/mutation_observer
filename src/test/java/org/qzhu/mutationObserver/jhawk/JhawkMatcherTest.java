package org.qzhu.mutationObserver.jhawk;


import org.qzhu.mutationObserver.source.MethodInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.qzhu.mutationObserver.Utils.getAllMethodInfoFromSource;
import static org.qzhu.mutationObserver.Utils.parsePitestFile;
import static org.qzhu.mutationObserver.jhawk.JhawkMatcher.*;

/**
 * @author Qianqian Zhu
 */
public class JhawkMatcherTest {

    @Test
    public void testParseJhawkResults(){
        String jhawkFilename = "./src/test/resources/testJhawk/jfreechart-1.5.0-ChartFactory_all.csv";
        HashMap<String, ArrayList<String>> jhawkMethodMap = parseJhawkResults(jhawkFilename);
        int[] expected = {3,13,16,71,74};
        assertEquals(jhawkMethodMap.size(),30);
        ArrayList<String> methodLines = jhawkMethodMap.get("org.jfree.chart.ChartFactory:createPieChart");
        assertEquals(methodLines.size(),5);
        for (int i=0;i<methodLines.size();i++){
            String[] columns = methodLines.get(i).split(";");
            assertEquals(Integer.parseInt(columns[11]),expected[i]);
        }
    }

    @Test
    public void testCombineJhawkResults() throws IOException {
        String jhawkMethodFilename = "./src/test/resources/testJhawk/jfreechart-1.5.0-ChartFactory_method.csv";
        String jhawkClassFilename = "./src/test/resources/testJhawk/jfreechart-1.5.0-ChartFactory_class.csv";
        String jhawkResultFilename = "./src/test/resources/testJhawk/jfreechart-1.5.0-ChartFactory_all.csv";
        combineJhawkResults(jhawkMethodFilename,jhawkClassFilename,jhawkResultFilename);

        // read file to check correctness
        BufferedReader jhawkReader = new BufferedReader(new FileReader(jhawkResultFilename));
        String line;
        int lineNo = 0;
        while ((line = jhawkReader.readLine()) != null){
            String[] columns = line.split(";");
            assertEquals(67,columns.length);
            lineNo++;
        }
        assertEquals(lineNo,55);
    }

    @Test
    public void testMatchJhawkMethod() throws IOException {
        String fileName = "./src/test/resources/ChartFactory.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,true);
        String pitestFileName = "./src/test/resources/testPitest/jfreechart-1.5.0_mutations.csv";
        parsePitestFile(pitestFileName,allMethodInfo);
        String jhawkFilename = "./src/test/resources/testJhawk/jfreechart-1.5.0-ChartFactory_all.csv";
        String matchResultFilename = "./src/test/resources/testJhawk/jfreechart-1.5.0-ChartFactory_test_results.csv";
        matchJhawkMethod(jhawkFilename,matchResultFilename,allMethodInfo);
        // read file to check correctness
        BufferedReader jhawkReader = new BufferedReader(new FileReader(matchResultFilename));
        String line;
        int lineNo = 0;
        while ((line = jhawkReader.readLine()) != null){
            String[] columns = line.split(";");
            assertEquals(87,columns.length);
            lineNo++;
        }
        assertEquals(lineNo,55);
    }

}
