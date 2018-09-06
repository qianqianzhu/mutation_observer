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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class MyMethodVisitorTest {

    @Test
    public void testNestClassCase(){
        try {
            InputStream inputStream = MyMethodVisitorTest.class.getResourceAsStream("/TypeUtils.java");
            Lexer lexer = new Java8Lexer(CharStreams.fromStream(inputStream));
            TokenStream tokenStream = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokenStream);
            ParseTree tree = parser.compilationUnit(); // parse
            ParseTreeWalker walker = new ParseTreeWalker();
            MyMethodVisitor methodVisitor = new MyMethodVisitor();
            walker.walk(methodVisitor,tree);
            MethodCollector methodCollector = methodVisitor.getMethodCollector();
            LinkedList<String> methodNameCollector = new LinkedList<String>(methodCollector.methodNameCollector);
            //System.out.println(methodNameCollector.toString());
            assertTrue(methodNameCollector.contains("org.apache.commons.lang3.reflect.TypeUtils$WildcardTypeBuilder:WildcardTypeBuilder"));
            assertTrue(methodNameCollector.contains("org.apache.commons.lang3.reflect.TypeUtils$GenericArrayTypeImpl:getGenericComponentType"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}