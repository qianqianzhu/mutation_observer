package org.qzhu.mutationObserver.source;

import org.qzhu.grammar.Java8Parser;

public class SimpleMethodInfoVisitor extends MethodInfoVisitor {

    @Override
    public void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        //System.out.print("if{");
        currentMethodSequence.add("cond");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("cond"));

    }
    @Override
    public void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        currentMethodSequence.add("cond");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("cond"));
    }
    @Override
    public void exitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterWhileStatement(Java8Parser.WhileStatementContext ctx) {
        //System.out.print("while{");
        currentMethodSequence.add("loop");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("loop"));
    }

    @Override
    public void exitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterForStatement(Java8Parser.ForStatementContext ctx) {
        //System.out.print("for{");
        currentMethodSequence.add("loop");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("loop"));
    }

    @Override
    public void exitForStatement(Java8Parser.ForStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

    @Override
    public void enterDoStatement(Java8Parser.DoStatementContext ctx) {
        //System.out.print("do{");
        currentMethodSequence.add("loop");
        currentMethodSequence.add("{");
        currentMethodTreeNode = currentMethodTreeNode.addChild(new Node<>("loop"));
    }
    @Override
    public void exitDoStatement(Java8Parser.DoStatementContext ctx) {
        //System.out.print("}");
        currentMethodSequence.add("}");
        currentMethodTreeNode = currentMethodTreeNode.getParent();
    }

}
