package org.qzhu.mutationObserver.callgraph;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.generic.*;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Qianqian Zhu
 * Class copied with modifications from java-callgraph: https://github.com/gousiosg/java-callgraph
 */
public class MethodCallVisitor extends EmptyVisitor {

    JavaClass visitedClass;
    private MethodGen mg;
    private ConstantPoolGen cp;
//    private String format;
    private String testCaseName;
    private HashMap<String,MethodInfo> allMethodInfoMapByMethodByteName;
//    HashSet<String> testSuite;
    HashMap<String,TestCaseInfo> testSuite;
    Digraph<String> callGraph;
    boolean directTestFlag;

    public MethodCallVisitor(MethodGen m, JavaClass jc, HashMap<String,MethodInfo> allMethodInfoMapByMethodByteName,
                             boolean directTestFlag, Digraph<String> callGraph,HashMap<String,TestCaseInfo> testSuite) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        this.directTestFlag = directTestFlag;
        this.callGraph = callGraph;
        this.testSuite = testSuite;
        this.allMethodInfoMapByMethodByteName = allMethodInfoMapByMethodByteName;
        testCaseName = visitedClass.getClassName() + ":" + mg.getName() +"(" + argumentList(mg.getArgumentTypes()) + ")";
//        format = "M:" + visitedClass.getClassName() + ":" + mg.getName() +"(" + argumentList(mg.getArgumentTypes()) + ")"
//            + " " + "(%s)%s:%s(%s)";
    }

    private String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }

    public void start() {
        if (mg.isAbstract() || mg.isNative())
            return;
        for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            
            if (!visitInstruction(i))
                i.accept(this);
        }
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction) 
                && !(i instanceof ReturnInstruction));
    }

    private void setDirectTestCases(InvokeInstruction i){
        String invokeMethodName = i.getReferenceType(cp)+":"+i.getMethodName(cp)+"("+argumentList(i.getArgumentTypes(cp))+")";
        if(directTestFlag) {
            if (allMethodInfoMapByMethodByteName.containsKey(invokeMethodName)) {
                MethodInfo methodInfo = allMethodInfoMapByMethodByteName.get(invokeMethodName);
                methodInfo.directTestCases.add(testCaseName);
            }
            // add test case
            if(testSuite.get(testCaseName)==null){
                TestCaseInfo testCaseInfo = new TestCaseInfo(testCaseName);
                testSuite.put(testCaseName,testCaseInfo);
            }
            // count assertion no.
            if(invokeMethodName.startsWith("org.junit.Assert")){
                testSuite.get(testCaseName).assertNo++;
                testSuite.put(testCaseName,testSuite.get(testCaseName));
            }
        }
        callGraph.add(testCaseName,invokeMethodName);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        setDirectTestCases(i);
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
        setDirectTestCases(i);
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        setDirectTestCases(i);
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        setDirectTestCases(i);
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        setDirectTestCases(i);
    }
}
