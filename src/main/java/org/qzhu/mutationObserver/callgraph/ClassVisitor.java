package org.qzhu.mutationObserver.callgraph;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
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
    HashSet<String> testSuite;
//    private String classReferenceFormat;

    public ClassVisitor(JavaClass jc, HashMap<String,MethodInfo> allMethodInfoMapByMethodByteName,
                        boolean directTestFlag, Digraph<String> callGraph,HashSet<String> testSuite) {
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

//    public void visitConstantPool(ConstantPool constantPool) {
//        for (int i = 0; i < constantPool.getLength(); i++) {
//            Constant constant = constantPool.getConstant(i);
//            if (constant == null)
//                continue;
//            if (constant.getTag() == 7) {
//                String referencedClass =
//                    constantPool.constantToString(constant);
//                System.out.println(String.format(classReferenceFormat,
//                        referencedClass));
//            }
//        }
//    }

    public void visitMethod(Method method) {
        MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
        MethodCallVisitor visitor = new MethodCallVisitor(mg,clazz,allMethodInfoMapByMethodByteName,
                directTestFlag,callGraph,testSuite);
        visitor.start(); 
    }

    public void start() {
        visitJavaClass(clazz);
    }
}
