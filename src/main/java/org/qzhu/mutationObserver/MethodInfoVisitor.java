package org.qzhu.mutationObserver;

import org.qzhu.grammar.Java8BaseListener;
import org.qzhu.grammar.Java8Parser;
import org.qzhu.grammar.Java8Parser.MethodModifierContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
public class MethodInfoVisitor extends Java8BaseListener {
    String packageName;
    String className;
    String currentMethodName;
    int classNestCount;
    ArrayList<String> currentMethodSequence = new ArrayList<>();


    LinkedList<MethodInfo> methodInfoCollector = new LinkedList<>();
    LinkedList<MethodInfo> allMethodInfoCollector = new LinkedList<>();

    public LinkedList<MethodInfo> getAllMethodInfoCollector() { return allMethodInfoCollector; }

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
        boolean isVoid = false;
        String methodResultType = ctx.methodHeader().result().getText();
        if(methodResultType.equals("void")){
            isVoid = true;
        }
        ArrayList<String> methodModifier = new ArrayList<>();
        List<MethodModifierContext> modifierContext = ctx.methodModifier();
        for(MethodModifierContext mmc: modifierContext){
            methodModifier.add(mmc.getText());
        }
        String methodName = ctx.methodHeader().methodDeclarator().Identifier().getText();
        currentMethodName = packageName+className+":"+methodName;
        //currentMethodSequence.clear();
        //System.out.println(currentMethodName);
        //System.out.println(methodName+" line no.:"+ctx.start.getLine()+"~"+ctx.stop.getLine());
        MethodInfo currenctMethod = new MethodInfo(ctx.start.getLine(), ctx.stop.getLine(),currentMethodName);
        currenctMethod.isVoid = isVoid;
        currenctMethod.methodModifier = methodModifier;
        methodInfoCollector.push(currenctMethod);
    }
    @Override
    public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        //System.out.println();
        //System.out.println(currentMethodSequence.toString());
        MethodInfo currentMethod = methodInfoCollector.pop();
        currentMethod.setMethod_sequence(new ArrayList<>(currentMethodSequence));
        allMethodInfoCollector.add(currentMethod);
        currentMethodSequence.clear();

    }

    @Override
    public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        //String methodName = ctx.constructorDeclarator().simpleTypeName().getText();
        currentMethodName = packageName+className+":<init>";
        //System.out.println(currentMethodName);
        //System.out.println(methodName+" line no.:"+ctx.start.getLine()+"~"+ctx.stop.getLine());
        MethodInfo currenctMethod = new MethodInfo(ctx.start.getLine(), ctx.stop.getLine(),currentMethodName);
        currenctMethod.isVoid = true;
        ArrayList<String> methodModifier = new ArrayList<>();
        List<Java8Parser.ConstructorModifierContext> modifierContext = ctx.constructorModifier();
        for(Java8Parser.ConstructorModifierContext mmc: modifierContext){
            methodModifier.add(mmc.getText());
        }
        currenctMethod.methodModifier = methodModifier;
        methodInfoCollector.push(currenctMethod);
    }

    @Override
    public void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        //System.out.println(currentMethodSequence.toString());
        MethodInfo currentMethod = methodInfoCollector.pop();
        currentMethod.setMethod_sequence(new ArrayList<>(currentMethodSequence));
        allMethodInfoCollector.add(currentMethod);
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
