package org.qzhu.mutationObserver;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.qzhu.mutationObserver.Utils.*;

/**
 * @author Qianqian Zhu
 */
public class UtilsTest {

	@BeforeClass
	public static void setup() {
		// Create a results folder if not already exists.
		new File("./src/main/results/").mkdirs();
	}
	
    @Test
    public void testGetAllFilesFromDir() throws IOException {
        String testDir ="./src/main/resources/";
        List<String> fileNames = new ArrayList<>();
        fileNames = getAllFilesFromDir(fileNames,".java",testDir);
        assertEquals(3,fileNames.size());
        assertEquals("./src/main/resources/ClassPathUtils.java", fileNames.get(0));
        assertEquals("./src/main/resources/helloworld.java", fileNames.get(1));
        assertEquals("./src/main/resources/TypeUtils.java", fileNames.get(2));
    }


    @Test
    public void testLcsCase1() {
        String[] sq1 = "AGGTAB".split("");
        String[] sq2 = "GXTXAYB".split("");

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(4,lcs(a,b));

    }

    @Test
    public void testLcsCase2() {
        String[] sq1 = {"if","{","}"};
        String[] sq2 = {"if","{","}","else","{","}"};

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(3,lcs(a,b));

    }

    @Test
    public void testGenerateMethodInfoMapByClassName(){
        String fileName = "./src/test/test_resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByClassName(allMethodInfo,true);
        assertEquals(1,allMethodInfoMap.size());
        assertTrue(allMethodInfoMap.keySet().contains("org.apache.commons.lang3.concurrent.Memoizer"));
    }


    @Test
    public void testParsePitestFile() throws IOException {
        String pitestFileName = "./src/test/test_resources/testPitest/commons-lang-LANG_3_7_mutations.csv";
        String fileName = "./src/test/test_resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);

        int totalMethod = allMethodInfo.size();
//        System.out.println("Total method no.: "+totalMethod);
//        System.out.println("Parsing Pitest results...");

        parsePitestFile(pitestFileName,allMethodInfo);
//        for (MethodInfo method:allMethodInfo){
//            System.out.println(method.method_name+":"+method.kill_mut+" "+method.total_mut);
//        }

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>",allMethodInfo.get(0).method_name);
        assertEquals(0,allMethodInfo.get(0).total_mut);
        assertEquals(0,allMethodInfo.get(0).kill_mut);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute",allMethodInfo.get(3).method_name);
        assertEquals(6,allMethodInfo.get(3).total_mut);
        assertEquals(6,allMethodInfo.get(3).kill_mut);

    }


    @Test
    public void testGenerateFeatureMatrix() throws IOException {
        String fileName = "./src/test/test_resources/helloworld.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        String resultFileName = "./src/main/results/test_match_count.csv";
        generateFeatureMatrix(allMethodInfo,resultFileName,false);

    }


    @Test
    public void testSetAllMethodBytecodeNameFromJar(){
        String jarFileName = "./src/test/test_resources/commons-lang-LANG_3_7/commons-lang3-3.7.jar";
        String fileName = "./src/test/test_resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodBytecodeNameFromJar(jarFileName,allMethodInfo);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>",allMethodInfo.get(0).method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)",allMethodInfo.get(0).bytecodeName);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer$Callable<O>:call",allMethodInfo.get(2).method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer$1:call()",allMethodInfo.get(2).bytecodeName);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute",allMethodInfo.get(3).method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)",allMethodInfo.get(3).bytecodeName);

    }

    @Test
    public void testSetAllMethodBytecodeNameFromDir(){
        String classDir = "./src/test/test_resources/commons-lang-LANG_3_7/classes";
        String fileName = "./src/test/test_resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodBytecodeNameFromDir(classDir,allMethodInfo);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>",allMethodInfo.get(0).method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)",
                allMethodInfo.get(0).bytecodeName);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer$Callable<O>:call",allMethodInfo.get(2).method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer$1:call()",allMethodInfo.get(2).bytecodeName);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute",allMethodInfo.get(3).method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)",allMethodInfo.get(3).bytecodeName);
    }


    @Test
    public void testGenerateMethodInfoMapByMethodByteName(){
        String sourceJarFileName = "./src/test/test_resources/commons-lang-LANG_3_7/commons-lang3-3.7.jar";

        String fileName = "./src/test/test_resources/Memoizer.java";

        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        HashMap<String,MethodInfo> allMethodInfoMap = generateMethodInfoMapByMethodByteName(sourceJarFileName,allMethodInfo);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer$Callable<O>:call",
                allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer$1:call()").method_name);
        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>",
                allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)").method_name
                );

    }

    @Test
    public void testSetAllMethodDirectTestFromJar(){
        String testJarFileName = "./src/test/test_resources/commons-lang-LANG_3_7/commons-lang3-3.7-tests.jar";
        String sourceJarFileName = "./src/test/test_resources/commons-lang-LANG_3_7/commons-lang3-3.7.jar";

        String fileName = "./src/test/test_resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        setAllMethodDirectTestFromJar(sourceJarFileName,testJarFileName,allMethodInfo);

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:<init>(org.apache.commons.lang3.concurrent.Computable)",allMethodInfo.get(0).bytecodeName);
        assertEquals(4,allMethodInfo.get(0).directTestCases.size());

        assertEquals("org.apache.commons.lang3.concurrent.Memoizer:compute(java.lang.Object)",allMethodInfo.get(3).bytecodeName);
        assertEquals(10,allMethodInfo.get(3).directTestCases.size());

        //        for (MethodInfo method:allMethodInfo){
//            System.out.println(method.bytecodeName+" ; "+method.directTestCases.toString());
//        }
    }

    @Test
    public void testSetAllMethodDirectTestFromFromDir(){
        String sourceDir = "./src/test/test_resources/commons-lang-LANG_3_7/classes";
        String testDir = "./src/test/test_resources/commons-lang-LANG_3_7/test-classes";

        String fileName = "./src/test/test_resources/Memoizer.java";
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
    public void testSetTestReachDistance() {
        String sourceDir = "./src/test/test_resources/testProject/target/classes";
        String testDir = "./src/test/test_resources/testProject/target/test-classes";

        String fileName = "./src/test/test_resources/testProject/src/main/java/org/testproject/A.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,true);

        HashMap<String,Integer> allMethodTestReachDistance = setAllMethodDirectTestFromDir(sourceDir,testDir,allMethodInfo);
        HashMap<String,Integer> expected = new HashMap<>();

        expected.put("java.lang.Object:<init>()",1);

        expected.put("org.testproject.A:<init>()",1);
        expected.put("org.testproject.A:methodA()",1);
        expected.put("org.testproject.A:methodB()",2);
        expected.put("org.testproject.A:methodC()",2);
        expected.put("org.testproject.A:methodD()",1);
        expected.put("org.testproject.A:methodE()",3);

        expected.put("org.testproject.ATest:<init>()",0);
        expected.put("org.testproject.ATest:testA()",0);
        expected.put("org.testproject.ATest:testB()",0);
        expected.put("org.testproject.ATest:testD()",0);
        expected.put("org.testproject.ATest:testC()",0);

        expected.put("START",-1);

        for (String methodName: expected.keySet()){
//            System.out.println(methodName+" "+allMethodTestReachDistance.get(methodName));
            assertEquals(expected.get(methodName),allMethodTestReachDistance.get(methodName));
        }
    }

    @Test
    public void testSetTestReachDistanceLargeClass(){
        String sourceDir = "./src/test/test_resources/testProject/target/classes";
        String testDir = "./src/test/test_resources/testProject/target/test-classes";

        String fileName = "./src/test/test_resources/TypeUtils.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,true);
        setAllMethodDirectTestFromDir(sourceDir,testDir,allMethodInfo);

        for (MethodInfo method:allMethodInfo){
            if(method.directTestCases.size()==0)
                assertTrue(method.testReachDistance>0);
            if(method.directTestCases.size()>0) {
                assertEquals(1, method.testReachDistance);
            }
        }
    }

    @Test
    public void testGenerateMethodInfoMapByMethodName(){
        String fileName = "./src/test/test_resources/Memoizer.java";
        LinkedList<MethodInfo> allMethodInfo = getAllMethodInfoFromSource(fileName,false);
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByMethodName(allMethodInfo);
        ArrayList<MethodInfo> testMethodInfos = allMethodInfoMap.get("org.apache.commons.lang3.concurrent.Memoizer:<init>");
        assertEquals(testMethodInfos.size(),2);
        int methodLength0 = testMethodInfos.get(0).stop_line-testMethodInfos.get(0).start_line+1;
        int methodLength1 = testMethodInfos.get(1).stop_line-testMethodInfos.get(1).start_line+1;
        assertTrue(methodLength0<=methodLength1);


    }

}