package org.qzhu.mutationObserver.source;

import org.qzhu.grammar.Java8BaseListener;
import org.qzhu.grammar.Java8Parser;
import org.qzhu.grammar.Java8Parser.MethodModifierContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
public  class MethodInfoVisitor extends Java8BaseListener {
    String packageName;
    String className;
    String currentMethodName;
    int classNestCount;
    ArrayList<String> currentMethodSequence = new ArrayList<>();
    Node<String> currentMethodTreeNode = new Node<>("root");


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
    }

    @Override public void enterEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        classNestCount++;
        if(classNestCount>1) {
            className = className+"$"+ctx.Identifier().getText();
        }else{
            className = ctx.Identifier().getText();

        }
    }

    @Override public void exitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        classNestCount--;
        if(classNestCount>0) {
            int lastDollarIndex = className.lastIndexOf("$");
            className = className.substring(0, lastDollarIndex);
        }
    }

    @Override
    public void enterClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx){
        if(ctx.classBody()!=null) {
            classNestCount++;
            int start = ctx.getText().indexOf(ctx.Identifier(0).getText());
            int end = ctx.getText().indexOf("(");
            if (classNestCount > 1) {
                className = className + "$" + ctx.getText().substring(start,end);
            } else {

                className = ctx.getText().substring(start,end);
            }
        }
    }
    @Override
    public void exitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx){
        if(ctx.classBody()!=null) {
            classNestCount--;
            if (classNestCount > 0) {
                int lastDollarIndex = className.lastIndexOf("$");
                className = className.substring(0, lastDollarIndex);
            }
        }

    }

    @Override
    public void enterClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx){
        if(ctx.classBody()!=null) {
            classNestCount++;
            int start = ctx.getText().indexOf(ctx.Identifier(0).getText());
            int end = ctx.getText().indexOf("(");
            if (classNestCount > 1) {
                // collect identifier
                className = className + "$" + ctx.getText().substring(start,end);
            } else {
                className = ctx.getText().substring(start,end);
            }
        }

    }
    @Override
    public void exitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx){
        if(ctx.classBody()!=null) {
            classNestCount--;
            if (classNestCount > 0) {
                int lastDollarIndex = className.lastIndexOf("$");
                className = className.substring(0, lastDollarIndex);
            }
        }
    }

    @Override
    public void enterClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx){
        if(ctx.classBody()!=null) {
            classNestCount++;
//        System.out.println(classNestCount);
            int start = ctx.getText().indexOf(ctx.Identifier().getText());
            int end = ctx.getText().indexOf("(");
            if(classNestCount>1) {
                // collect identifier
                className = className + "$" + ctx.getText().substring(start,end);
            }else{
                className = ctx.getText().substring(start,end);
            }
        }
    }
    @Override
    public void exitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx){
        if(ctx.classBody()!=null) {
            classNestCount--;
            if (classNestCount > 0) {
                int lastDollarIndex = className.lastIndexOf("$");
                className = className.substring(0, lastDollarIndex);
            }
        }
    }

    @Override
    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        if(methodInfoCollector.size()!=0){
            // save non-completed method
            MethodInfo previousMethod = methodInfoCollector.pop();
            previousMethod.method_sequence = new ArrayList<>(currentMethodSequence);
            previousMethod.methodTreeRoot = new Node<>(currentMethodTreeNode);
            methodInfoCollector.push(previousMethod);
            // to re-new previous info
            currentMethodSequence.clear();
            currentMethodTreeNode = new Node<>("root");
        }

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
        Java8Parser.FormalParameterListContext formalParameterList = ctx.methodHeader().methodDeclarator().formalParameterList();
        currentMethodName = packageName+className+":"+methodName;

        MethodInfo currentMethod = new MethodInfo(ctx.start.getLine(), ctx.stop.getLine(),packageName+className,currentMethodName);
        currentMethod.isVoid = isVoid;
        currentMethod.isNested = className.contains("$");
        currentMethod.methodModifier = methodModifier;
        // getter conditions: public & no arguments & non-void & start with get
        currentMethod.isGetter = (methodModifier.contains("public") & (formalParameterList==null) & (!isVoid) & methodName.startsWith("get"));
        methodInfoCollector.push(currentMethod);


    }
    @Override
    public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        MethodInfo currentMethod = methodInfoCollector.pop();
        currentMethod.setMethod_sequence(new ArrayList<>(currentMethodSequence));
        currentMethod.methodTreeRoot = new Node<>(currentMethodTreeNode.getRoot());
        allMethodInfoCollector.add(currentMethod);
        if(methodInfoCollector.size()==0) {
            currentMethodSequence.clear();
            currentMethodTreeNode = new Node<>("root");
        }else{
            // retrieve previous method info
            MethodInfo previousMethod = methodInfoCollector.pop();
            currentMethodName = previousMethod.method_name;
            currentMethodSequence = previousMethod.method_sequence;
            currentMethodTreeNode = previousMethod.methodTreeRoot;
            // push back for further update
            methodInfoCollector.push(previousMethod);
        }

    }

    @Override
    public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        if(methodInfoCollector.size()!=0){
            // save non-completed method
            MethodInfo previousMethod = methodInfoCollector.pop();
            previousMethod.method_sequence = new ArrayList<>(currentMethodSequence);
            previousMethod.methodTreeRoot = new Node<>(currentMethodTreeNode);
            methodInfoCollector.push(previousMethod);
            // clear previous method info
            currentMethodSequence.clear();
            currentMethodTreeNode = new Node<>("root");
        }

        // collect new method info
        currentMethodName = packageName+className+":<init>";
        MethodInfo currentMethod = new MethodInfo(ctx.start.getLine(), ctx.stop.getLine(),packageName+className,currentMethodName);
        currentMethod.isVoid = true;
        currentMethod.isNested = className.contains("$");
        currentMethod.isGetter = false;
        ArrayList<String> methodModifier = new ArrayList<>();
        List<Java8Parser.ConstructorModifierContext> modifierContext = ctx.constructorModifier();
        for(Java8Parser.ConstructorModifierContext mmc: modifierContext){
            methodModifier.add(mmc.getText());
        }
        currentMethod.methodModifier = methodModifier;
        methodInfoCollector.push(currentMethod);
    }

    @Override
    public void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        MethodInfo currentMethod = methodInfoCollector.pop();
        currentMethod.setMethod_sequence(new ArrayList<>(currentMethodSequence));
        currentMethod.methodTreeRoot = new Node<>(currentMethodTreeNode.getRoot());
        allMethodInfoCollector.add(currentMethod);

        if(methodInfoCollector.size()==0) {
            currentMethodSequence.clear();
            currentMethodTreeNode = new Node<>("root");
        }else{
            MethodInfo previousMethod = methodInfoCollector.pop();
            currentMethodName = previousMethod.method_name;
            currentMethodSequence = previousMethod.method_sequence;
            currentMethodTreeNode = previousMethod.methodTreeRoot;
            methodInfoCollector.push(previousMethod);  // to keep method info
        }
    }

    @Override
    public void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        currentMethodSequence.add("if");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("if"));

    }
    @Override
    public void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        currentMethodSequence.add("if-else");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("if-else"));
    }
    @Override
    public void exitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterWhileStatement(Java8Parser.WhileStatementContext ctx) {
        currentMethodSequence.add("while");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("while"));
    }

    @Override
    public void exitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterForStatement(Java8Parser.ForStatementContext ctx) {
        currentMethodSequence.add("for");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("for"));
    }

    @Override
    public void exitForStatement(Java8Parser.ForStatementContext ctx) {
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterDoStatement(Java8Parser.DoStatementContext ctx) {
        currentMethodSequence.add("do");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("do"));
    }
    @Override
    public void exitDoStatement(Java8Parser.DoStatementContext ctx) {
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

}
