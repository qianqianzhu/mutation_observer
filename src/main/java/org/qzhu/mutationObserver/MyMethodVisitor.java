package org.qzhu.mutationObserver;

import org.qzhu.grammar.Java8BaseListener;
import org.qzhu.grammar.Java8Parser;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Qianqian Zhu
 */
public class MyMethodVisitor extends Java8BaseListener {
    String packageName;
    String className;
    String currentMethodName;
    int classNestCount;
    ArrayList<String> currentMethodSequence = new ArrayList<>();
    MethodCollector methodCollector = new MethodCollector();
    LinkedList<String> methodNameCollector = methodCollector.methodNameCollector;
    LinkedList<ArrayList<String>> methodSequenceCollector = methodCollector.methodSequenceCollector;

    public MethodCollector getMethodCollector(){
        return methodCollector;
    }

    @Override
    public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        packageName = ctx.packageName().getText();
        if(packageName!=null){
            packageName += ".";
        }
        classNestCount=0;
    }

    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        classNestCount++;
        //System.out.println(classNestCount);
        if(classNestCount>1) {
            className = className+"$"+ctx.Identifier().getText();
        }else{
            className = ctx.Identifier().getText();
        }
    }
    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        classNestCount--;
        if(classNestCount>0) {
            int lastDollarIndex = className.lastIndexOf("$");
            className = className.substring(0, lastDollarIndex);
        }
        //System.out.println(classNestCount);
    }

    @Override
    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        String methodName = ctx.methodHeader().methodDeclarator().Identifier().getText();
        currentMethodName = packageName+className+":"+methodName;
        //currentMethodSequence.clear();
        methodNameCollector.add(currentMethodName);
        //System.out.println(currentMethodName);
    }
    @Override
    public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        //System.out.println();
        //System.out.println(currentMethodSequence.toString());
        methodSequenceCollector.add(new ArrayList<String>(currentMethodSequence));
        currentMethodSequence.clear();

    }

    @Override
    public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        String methodName = ctx.constructorDeclarator().simpleTypeName().getText();
        currentMethodName = packageName+className+":"+methodName;
        methodNameCollector.add(currentMethodName);
        //System.out.println(currentMethodName);
    }

    @Override
    public void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        //System.out.println(currentMethodSequence.toString());
        methodSequenceCollector.add(new ArrayList<String>(currentMethodSequence));
        currentMethodSequence.clear();
    }

    @Override
    public void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        //System.out.print("if{");
        currentMethodSequence.add("if");
        currentMethodSequence.add("{");
    }
    @Override
    public void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
    }

    @Override
    public void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        //System.out.print("if{}else{");
        currentMethodSequence.add("if");
        currentMethodSequence.add("{");
        currentMethodSequence.add("}");
        currentMethodSequence.add("else");
        currentMethodSequence.add("{");
    }
    @Override
    public void exitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
    }

    @Override
    public void enterWhileStatement(Java8Parser.WhileStatementContext ctx) {
        //System.out.print("while{");
        currentMethodSequence.add("while");
        currentMethodSequence.add("{");
    }

    @Override
    public void exitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
    }

    @Override
    public void enterForStatement(Java8Parser.ForStatementContext ctx) {
        //System.out.print("for{");
        currentMethodSequence.add("for");
        currentMethodSequence.add("{");
    }

    @Override
    public void exitForStatement(Java8Parser.ForStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
    }

    @Override
    public void enterDoStatement(Java8Parser.DoStatementContext ctx) {
        //System.out.print("do{");
        currentMethodSequence.add("do");
        currentMethodSequence.add("{");
    }
    @Override
    public void exitDoStatement(Java8Parser.DoStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
    }

}
