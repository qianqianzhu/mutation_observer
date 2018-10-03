package org.qzhu.mutationObserver;

import org.junit.Test;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.qzhu.mutationObserver.Utils.*;
import static org.qzhu.mutationObserver.Utils.setAllMethodDirectTestFromJar;

/**
 * @author Qianqian Zhu
 */
public class UtilsTest {

    @Test
    public void testGetAllFilesFromDir() throws IOException {

        String testDir ="./src/main/resources/";
        List<String> fileNames = new ArrayList<>();
        fileNames = getAllFilesFromDir(fileNames,".java",testDir);
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
    public void testGenerateMethodInfoMapByClassName(){
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByClassName(allMethodInfo,true);
        assertEquals(allMethodInfoMap.size(),1);
        assertTrue(allMethodInfoMap.keySet().contains("org.apache.commons.lang3.concurrent.Memoizer"));
    }


    @Test
    public void testParsePitestFile() throws IOException {
        String pitestFileName = "/Users/qianqianzhu/phd/testability/mutation_testing_observability/pitest_result/commons-lang-LANG_3_7_mutations.csv";
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);

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
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        String resultFileName = "./src/main/results/test_match_count.csv";
        generateFeatureMatrix(allMethodInfo,resultFileName,false);

    }


    @Test
    public void testSetAllMethodBytecodeNameFromJar(){
        String jarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodBytecodeNameFromJar(jarFileName,allMethodInfo);

        assertEquals(allMethodInfo.get(0).method_name,"org.apache.commons.lang3.concurrent.Memoizer:<init>");
        assertEquals(allMethodInfo.get(0).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)");

        assertEquals(allMethodInfo.get(2).method_name,"org.apache.commons.lang3.concurrent.Memoizer$Callable:call");
        assertEquals(allMethodInfo.get(2).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer$1:call()");

        assertEquals(allMethodInfo.get(3).method_name,"org.apache.commons.lang3.concurrent.Memoizer:compute");
        assertEquals(allMethodInfo.get(3).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)");

    }

    @Test
    public void testSetAllMethodBytecodeNameFromDir(){
        String classDir = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/classes";
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodBytecodeNameFromDir(classDir,allMethodInfo);

        assertEquals(allMethodInfo.get(0).method_name,"org.apache.commons.lang3.concurrent.Memoizer:<init>");
        assertEquals(allMethodInfo.get(0).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)");

        assertEquals(allMethodInfo.get(2).method_name,"org.apache.commons.lang3.concurrent.Memoizer$Callable:call");
        assertEquals(allMethodInfo.get(2).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer$1:call()");

        assertEquals(allMethodInfo.get(3).method_name,"org.apache.commons.lang3.concurrent.Memoizer:compute");
        assertEquals(allMethodInfo.get(3).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)");
    }


    @Test
    public void testGenerateMethodInfoMapByMethodByteName(){
        String sourceJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";

        String fileName = "./src/test/resources/Memoizer.java";

        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        HashMap<String,MethodInfo> allMethodInfoMap = generateMethodInfoMapByMethodByteName(sourceJarFileName,allMethodInfo);
        assertEquals(allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer$1:call()").method_name,
                "org.apache.commons.lang3.concurrent.Memoizer$Callable:call");
        assertEquals(allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)").method_name,
                "org.apache.commons.lang3.concurrent.Memoizer:<init>");

    }

    @Test
    public void testSetAllMethodDirectTestFromJar(){
        String testJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7-tests.jar";
        String sourceJarFileName = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";

        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodDirectTestFromJar(sourceJarFileName,testJarFileName,allMethodInfo);

        assertEquals(allMethodInfo.get(0).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)");
        assertEquals(allMethodInfo.get(0).directTestCases.size(),4);

        assertEquals(allMethodInfo.get(3).bytecodeName,"org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)");
        assertEquals(allMethodInfo.get(3).directTestCases.size(),10);

        //        for (MethodInfo method:allMethodInfo){
//            System.out.println(method.bytecodeName+" ; "+method.directTestCases.toString());
//        }
    }

    @Test
    public void testSetAllMethodDirectTestFromFromDir(){
        String sourceDir = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/classes";
        String testDir = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/test-classes";

        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodDirectTestFromDir(sourceDir,testDir,allMethodInfo);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)",
                allMethodInfo.get(0).bytecodeName);
        assertEquals(4,allMethodInfo.get(0).directTestCases.size());

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)",
                allMethodInfo.get(3).bytecodeName);
        assertEquals(10,allMethodInfo.get(3).directTestCases.size());

//        for (MethodInfo method:allMethodInfo){
//            System.out.println(method.bytecodeName+" "+method.directTestCases.size()+" "+method.testReachDistance);
//        }
    }

    @Test
    public void testSetTestReachDistance(){
        String sourceDir = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/classes";
        String testDir = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/test-classes";

        String fileName = "./src/test/resources/TypeUtils.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,true);
        setAllMethodDirectTestFromDir(sourceDir,testDir,allMethodInfo);

        for (MethodInfo method:allMethodInfo){
            if(method.directTestCases.size()==0)
                assertTrue(method.testReachDistance>0);
            if(method.directTestCases.size()>0) {
                assertEquals(0, method.testReachDistance);
            }
        }
    }

    @Test
    public void testGenerateMethodInfoMapByMethodName(){
        String fileName = "./src/test/resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByMethodName(allMethodInfo);
        ArrayList<MethodInfo> testMethodInfos = allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer:<init>");
        assertEquals(testMethodInfos.size(),2);
        int methodLength0 = testMethodInfos.get(0).stop_line-testMethodInfos.get(0).start_line+1;
        int methodLength1 = testMethodInfos.get(1).stop_line-testMethodInfos.get(1).start_line+1;
        assertTrue(methodLength0<=methodLength1);


    }

}