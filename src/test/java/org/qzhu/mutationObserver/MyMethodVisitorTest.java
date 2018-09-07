package org.qzhu.mutationObserver;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import org.qzhu.grammar.Java8Lexer;
import org.qzhu.grammar.Java8Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class MyMethodVisitorTest {

    private MethodCollector methodWalker(String filename){
        try {
            InputStream inputStream = MyMethodVisitorTest.class.getResourceAsStream(filename);
            Lexer lexer = new Java8Lexer(CharStreams.fromStream(inputStream));
            TokenStream tokenStream = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokenStream);
            ParseTree tree = parser.compilationUnit(); // parse
            ParseTreeWalker walker = new ParseTreeWalker();
            MyMethodVisitor methodVisitor = new MyMethodVisitor();
            walker.walk(methodVisitor,tree);
            MethodCollector methodCollector = methodVisitor.getMethodCollector();
            return methodCollector;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testNestClassCase() {
        MethodCollector methodCollector = methodWalker("/TypeUtils.java");
        LinkedList<String> methodNameCollector = new LinkedList<>(methodCollector.methodNameCollector);
        assertTrue(methodNameCollector.contains("org.apache.commons.lang3.reflect.TypeUtils$WildcardTypeBuilder:WildcardTypeBuilder"));
        assertTrue(methodNameCollector.contains("org.apache.commons.lang3.reflect.TypeUtils$GenericArrayTypeImpl:getGenericComponentType"));
    }

    @Test
    public void testLongMethodCase(){
        MethodCollector methodCollector = methodWalker("/LocaleUtils.java");
        LinkedList<ArrayList<String>> methodSequenceCollector = new LinkedList<ArrayList<String>>(methodCollector.methodSequenceCollector);
        assertTrue(methodSequenceCollector.get(1).toString()
                .equals("[if, {, }, if, {, }, if, {, }, if, {, }, if, {, if, {, }, if, {, }, if, {, }, if, {, }, if, {, }, }]"));
    }

}