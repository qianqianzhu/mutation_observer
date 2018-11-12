package org.qzhu.mutationObserver.callgraph;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Qianqian Zhu
 * Class copied with modifications from java-callgraph: https://github.com/gousiosg/java-callgraph
 */
public class ClassVisitor extends EmptyVisitor {

    private JavaClass clazz;
    private ConstantPoolGen constants;
    private HashMap<String,MethodInfo> allMethodInfoMapByMethodByteName;
    boolean directTestFlag;
    Digraph<String> callGraph;
    HashMap<String,TestCaseInfo> testSuite;
//    private String classReferenceFormat;

    public ClassVisitor(JavaClass jc, HashMap<String,MethodInfo> allMethodInfoMapByMethodByteName,
                        boolean directTestFlag, Digraph<String> callGraph,HashMap<String,TestCaseInfo> testSuite) {
        clazz = jc;
        this.directTestFlag = directTestFlag;
        this.callGraph = callGraph;
        this.testSuite = testSuite;
        constants = new ConstantPoolGen(clazz.getConstantPool());
        this.allMethodInfoMapByMethodByteName = allMethodInfoMapByMethodByteName;
//        classReferenceFormat = "C:" + clazz.getClassName() + " %s";
    }

    public Digraph<String> getCallGraph(){
        return callGraph;
    }

    public void visitJavaClass(JavaClass jc) {
//        jc.getConstantPool().accept(this);
        Method[] methods = jc.getMethods();
        for (int i = 0; i < methods.length; i++)
            methods[i].accept(this);
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

    public void visitMethod(Method method) {
        MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
        MethodCallVisitor visitor = new MethodCallVisitor(mg,clazz,allMethodInfoMapByMethodByteName,
                directTestFlag,callGraph,testSuite);
        visitor.start();

        // count test's LNOC
        int lastLine = -1;
        int firstLine = Integer.MAX_VALUE;
        if (method.getLineNumberTable() != null){
            LineNumberTable table = method.getLineNumberTable();
            if (table!=null){
                for (LineNumber line : table.getLineNumberTable()) {
                    lastLine = Math.max(lastLine, line.getLineNumber());
                    firstLine = Math.min(firstLine, line.getLineNumber());
                }
            }
        }
        String methodName = clazz.getClassName() + ":" + mg.getName() +"(" + argumentList(mg.getArgumentTypes()) + ")";
        if (testSuite.get(methodName)!=null) {
            testSuite.get(methodName).NLOC = lastLine - firstLine + 1;
        }

    }

    public void start() {
        visitJavaClass(clazz);
    }
}
