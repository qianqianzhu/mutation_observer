package org.qzhu.mutationObserver.source;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.Type;
import org.qzhu.mutationObserver.source.MethodInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Qianqian Zhu
 * Class copied with modifications from java-callgraph: https://github.com/gousiosg/java-callgraph
 */
public class SourceMethodsIndexer extends EmptyVisitor {
    private JavaClass clazz;
    private HashMap<String,ArrayList<MethodInfo>> allMethodInfoMapByClassName;

    public SourceMethodsIndexer(JavaClass jc, HashMap<String,ArrayList<MethodInfo>> allMethodInfoMapByClassName) {
        clazz = jc;
        this.allMethodInfoMapByClassName = allMethodInfoMapByClassName;
    }

    private void setMethodBytecodeName(String classNameWithoutNest,String methodName, int lineNo, String methodBytecodeName){
        if(allMethodInfoMapByClassName.containsKey(classNameWithoutNest)){
            ArrayList<MethodInfo> methodInfoInThisClass = allMethodInfoMapByClassName.get(classNameWithoutNest);
            for(MethodInfo method: methodInfoInThisClass){
                if (method.method_name.contains(methodName) && lineNo>=method.start_line && lineNo<=method.stop_line){
                    method.bytecodeName = methodBytecodeName;
                }
            }
        }
    }

    public void visitJavaClass(JavaClass jc) {
        Method[] methods = jc.getMethods();
        for (int i = 0; i < methods.length; i++)
            methods[i].accept(this);
    }


    public void visitMethod(Method method) {
        if(method.getLineNumberTable()!=null) {
            int lineNo = method.getLineNumberTable().getSourceLine(0);
            String className = clazz.getClassName();
            String classNameWithoutNest=className;
            if(className.indexOf("$")!=-1) {
                classNameWithoutNest = className.substring(0, className.indexOf("$"));
            }
            String methodName = method.getName();
            String methodBytecodeName = clazz.getClassName() + ":" + methodName + "(" + argumentList(method.getArgumentTypes()) + ")" ;
            setMethodBytecodeName(classNameWithoutNest,methodName,lineNo,methodBytecodeName);
        }
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
        visitJavaClass(clazz);
    }


}
