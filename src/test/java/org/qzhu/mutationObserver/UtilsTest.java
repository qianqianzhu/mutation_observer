package org.qzhu.mutationObserver;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Qianqian Zhu
 */
public class UtilsTest {

    @Test
    public void testGetAllFilesFromDir() throws IOException {

        String testDir ="./src/main/resources/";
        List<String> fileNames = new ArrayList<>();
        fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);
        assertEquals(fileNames.size(),2);
        assertTrue(fileNames.contains("./src/main/resources/helloworld.java"));
        assertTrue(fileNames.contains("./src/main/resources/ClassPathUtils.java"));
    }


    @Test
    public void testLcsCase1() {
        String[] sq1 = "AGGTAB".split("");
        String[] sq2 = "GXTXAYB".split("");

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(Utils.lcs(a,b),4);

    }

    @Test
    public void testLcsCase2() {
        String[] sq1 = {"if","{","}"};
        String[] sq2 = {"if","{","}","else","{","}"};

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(Utils.lcs(a,b),3);

    }


    @Test
    public void testParsePitestFile() throws IOException {
        String testDir ="./src/main/resources/";
        String pitestFileName = "/Users/qianqianzhu/phd/testability/Observer/pitest_result/commons-lang-LANG_3_7_mutations.csv";

        List<String> fileNames = new ArrayList<>();
        fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);
        LinkedList<MethodInfo> allMethodInfo = new LinkedList<>();
        for(String fileName: fileNames){
            System.out.println("Processing "+fileName);
            LinkedList<MethodInfo> methodInfo = Utils.getAllMethodInfoFromFile(fileName);
            //System.out.println(methodCollector.methodNameCollector);
            //System.out.println(methodCollector.methodSequenceCollector);
            allMethodInfo.addAll(methodInfo);
        }

        System.out.println("generating LCS matrix...");
        int totalMethod = allMethodInfo.size();
        System.out.println("Total method no.: "+totalMethod);
        String fileName = "./src/main/results/commons-lang-LANG_3_7_lcs.csv";
        System.out.println("Parsing Pitest results...");

        Utils.parsePitestFile(pitestFileName,allMethodInfo);
        for (MethodInfo method:allMethodInfo){
            System.out.println(method.method_name+":"+method.kill_mut+" "+method.total_mut);
        }

        assertEquals(allMethodInfo.get(0).total_mut,0);
        assertEquals(allMethodInfo.get(0).kill_mut,0);
        assertEquals(allMethodInfo.get(1).total_mut,1);
        assertEquals(allMethodInfo.get(1).kill_mut,1);

    }
}