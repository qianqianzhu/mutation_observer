package org.qzhu.mutationObserver.callgraph;
import org.apache.bcel.classfile.ClassParser;
import org.junit.*;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Qianqian Zhu
 */
public class MethodCallVisitorTest {

    private HashMap<String,TestCaseInfo> testWalker(String filename){
        // parse test files (.class)
        HashMap<String,TestCaseInfo> testSuite = new HashMap<>();
        Digraph<String> callGraph = new Digraph<>();
        HashMap<String, MethodInfo> allMethodInfoMap = new HashMap<>();
        ClassParser cp = new ClassParser(filename);
        ClassVisitor classVisitor = null;
        try {
            classVisitor = new ClassVisitor(cp.parse(),allMethodInfoMap,true,callGraph,testSuite);
        } catch (IOException e) {
            e.printStackTrace();
        }
        classVisitor.start();
        return testSuite;
    }

    @Test
    public void testTestCaseInfo(){
        HashMap<String,TestCaseInfo> testSuite = testWalker("./src/test/test_resources/testProject/target/test-classes/org/testproject/ATest.class");

        HashMap<String,Integer> expectedAssert = new HashMap<>();
        expectedAssert.put("org.testproject.ATest:testA()",0);
        expectedAssert.put("org.testproject.ATest:testB()",1);
        expectedAssert.put("org.testproject.ATest:testC()",2);
        expectedAssert.put("org.testproject.ATest:testD()",3);

        HashMap<String,Integer> expectedNLOC = new HashMap<>();
        expectedNLOC.put("org.testproject.ATest:testA()",3);
        expectedNLOC.put("org.testproject.ATest:testB()",3);
        expectedNLOC.put("org.testproject.ATest:testC()",4);
        expectedNLOC.put("org.testproject.ATest:testD()",5);

        for (String name: expectedAssert.keySet()){
           //System.out.println(name +" " + testSuite.get(name).assertNo + " "+ testSuite.get(name).NLOC);
            int assertNo = testSuite.get(name).assertNo;
            int nloc = testSuite.get(name).NLOC;
            int expectedAssertNo = expectedAssert.get(name);
            int expectedNloc = expectedNLOC.get(name);
            assertEquals(expectedAssertNo,assertNo);
            assertEquals(expectedNloc,nloc);
        }

    }


    @Test
    public void testTestCaseInfo2(){
        HashMap<String,TestCaseInfo> testSuite = testWalker("./src/test/test_resources/ConversionTest.class");
        assertTrue(testSuite.get("org.apache.commons.lang3.ConversionTest:testLongToBinary()").methodCalls
                .contains("org.apache.commons.lang3.ConversionTest:assertBinaryEquals(boolean[],boolean[])"));

        assertTrue(testSuite.get("org.apache.commons.lang3.ConversionTest:testShortToBinary()").methodCalls
                .contains("org.apache.commons.lang3.ConversionTest:assertBinaryEquals(boolean[],boolean[])"));

        assertFalse(testSuite.get("org.apache.commons.lang3.ConversionTest:testHexToShort()").methodCalls
                .contains("org.apache.commons.lang3.ConversionTest:assertBinaryEquals(boolean[],boolean[])"));

//        for (String name: testSuite.keySet()){
//           System.out.println(name +" " + testSuite.get(name).assertNo + " "+ testSuite.get(name).methodCalls.contains("org.apache.commons.lang3.ConversionTest:assertBinaryEquals(boolean[],boolean[])"));
//            int assertNo = testSuite.get(name).assertNo;
//            int nloc = testSuite.get(name).NLOC;
//            int expectedAssertNo = expectedAssert.get(name);
//            int expectedNloc = expectedNLOC.get(name);
//            assertEquals(expectedAssertNo,assertNo);
//            assertEquals(expectedNloc,nloc);
//        }
    }

}
