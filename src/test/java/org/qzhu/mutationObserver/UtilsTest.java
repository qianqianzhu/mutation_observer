package org.qzhu.mutationObserver;

import org.junit.Test;

import javax.xml.ws.RequestWrapper;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.qzhu.mutationObserver.Utils.*;

/**
 * @author Qianqian Zhu
 */
public class UtilsTest {

    @Test
    public void testGetAllFilesFromDir() throws IOException {

        String testDir ="./src/main/resources/";
        List<String> fileNames = new ArrayList<>();
        fileNames = getAllJavaFilesFromDir(fileNames,testDir);
        assertEquals(fileNames.size(),3);
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

        assertEquals(lcs(a,b),4);

    }

    @Test
    public void testLcsCase2() {
        String[] sq1 = {"if","{","}"};
        String[] sq2 = {"if","{","}","else","{","}"};

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(lcs(a,b),3);

    }


    @Test
    public void testParsePitestFile() throws IOException {
        String pitestFileName = "/Users/qianqianzhu/phd/testability/Observer/pitest_result/commons-lang-LANG_3_7_mutations.csv";
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName);

        int totalMethod = allMethodInfo.size();
        System.out.println("Total method no.: "+totalMethod);
        System.out.println("Parsing Pitest results...");

        parsePitestFile(pitestFileName,allMethodInfo);
//        for (MethodInfo method:allMethodInfo){
//            System.out.println(method.method_name+":"+method.kill_mut+" "+method.total_mut);
//        }

        assertEquals(allMethodInfo.get(0).method_name,"org.apache.commons.lang3.concurrent.Memoizer:<init>");
        assertEquals(allMethodInfo.get(0).total_mut,0);
        assertEquals(allMethodInfo.get(0).kill_mut,0);

        assertEquals(allMethodInfo.get(3).method_name,"org.apache.commons.lang3.concurrent.Memoizer:compute");
        assertEquals(allMethodInfo.get(3).total_mut,6);
        assertEquals(allMethodInfo.get(3).kill_mut,6);

    }


    @Test
    public void testGenerateFeatureMatrix() throws IOException {
        String fileName = "./src/test/resources/helloworld.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName);
        String resultFileName = "./src/main/results/test_match_count.csv";
        generateFeatureMatrix(allMethodInfo,resultFileName);

    }


    @Test
    public void testSetAllMethodBytecodeNameFromJar(){
        String jarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName);
        setAllMethodBytecodeNameFromJar(jarFileName,allMethodInfo);

        assertEquals(allMethodInfo.get(0).method_name,"org.apache.commons.lang3.concurrent.Memoizer:<init>");
        assertEquals(allMethodInfo.get(0).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)");

        assertEquals(allMethodInfo.get(2).method_name,"org.apache.commons.lang3.concurrent.Memoizer$Callable:call");
        assertEquals(allMethodInfo.get(2).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer$1:call()");

        assertEquals(allMethodInfo.get(3).method_name,"org.apache.commons.lang3.concurrent.Memoizer:compute");
        assertEquals(allMethodInfo.get(3).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)");

//                for (MethodInfo method:allMethodInfo){
//            System.out.println(method.method_name+" ; "+method.bytecodeName);
//        }
    }


    @Test
    public void testGenerateMethodInfoMapByMethodByteName(){
//        String testJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7-tests.jar";
        String sourceJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";

        String fileName = "./src/test/resources/Memoizer.java";

        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName);
        HashMap<String,MethodInfo> allMethodInfoMap = generateMethodInfoMapByMethodByteName(sourceJarFileName,allMethodInfo);
        assertEquals(allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer$1:call()").method_name,
                "org.apache.commons.lang3.concurrent.Memoizer$Callable:call");
        assertEquals(allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)").method_name,
                "org.apache.commons.lang3.concurrent.Memoizer:<init>");

//        for(String methodByteName:allMethodInfoMap.keySet()){
//            System.out.println(methodByteName+" = "+allMethodInfoMap.get(methodByteName).method_name);
//
//        }

    }

    @Test
    public void testSetAllMethodDirectTestFromJar(){
        String testJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7-tests.jar";
        String sourceJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";

        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName);
        setAllMethodDirectTestFromJar(sourceJarFileName,testJarFileName,allMethodInfo);

        assertEquals(allMethodInfo.get(0).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)");
        assertEquals(allMethodInfo.get(0).directTestCases.size(),4);

        assertEquals(allMethodInfo.get(3).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)");
        assertEquals(allMethodInfo.get(3).directTestCases.size(),10);

        //        for (MethodInfo method:allMethodInfo){
//            System.out.println(method.bytecodeName+" ; "+method.directTestCases.toString());
//        }
    }

}