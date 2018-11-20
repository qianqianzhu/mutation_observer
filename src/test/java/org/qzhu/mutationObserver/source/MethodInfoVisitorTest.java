package org.qzhu.mutationObserver.source;

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
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * @author Qianqian Zhu
 */
public class MethodInfoVisitorTest {

    private LinkedList<MethodInfo> methodWalker(String filename){
        try {
            InputStream inputStream = MethodInfoVisitorTest.class.getResourceAsStream(filename);
            Lexer lexer = new Java8Lexer(CharStreams.fromStream(inputStream));
            TokenStream tokenStream = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokenStream);
            ParseTree tree = parser.compilationUnit(); // parse
            ParseTreeWalker walker = new ParseTreeWalker();
            MethodInfoVisitor methodVisitor = new MethodInfoVisitor();
            walker.walk(methodVisitor,tree);
            LinkedList<MethodInfo> allMethodInfo= methodVisitor.getAllMethodInfoCollector();
            return allMethodInfo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testNestClassCase() {
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/TypeUtils.java");

        MethodInfo testMethod0 = allMethodInfo.get(0);
        assertEquals(57,testMethod0.start_line);
        assertEquals(58,testMethod0.stop_line);
        assertTrue(testMethod0.isVoid);
        assertTrue(testMethod0.methodModifier.contains("private"));
        assertTrue(testMethod0.method_name.equals("org.apache.commons.lang3.reflect.TypeUtils$WildcardTypeBuilder:<init>"));

        MethodInfo testMethod4 = allMethodInfo.get(4);
        assertTrue(testMethod4.method_name.equals("org.apache.commons.lang3.reflect.TypeUtils$GenericArrayTypeImpl:<init>"));
    }


    @Test
    public void testLongMethodCase(){
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/LocaleUtils.java");
        // test constructor
        MethodInfo testMethod0 = allMethodInfo.get(0);
        assertTrue(testMethod0.method_name.equals("org.apache.commons.lang3.LocaleUtils:<init>"));

        MethodInfo testMethod1 = allMethodInfo.get(1);
        assertTrue(testMethod1.method_sequence.toString()
                .equals("[if, {, }, if, {, }, if, {, }, if, {, }, if, {, if, {, }, if, {, }, if, {, }, if, {, }, if, {, }, }]"));
    }

    @Test
    public void testIfElseStatement() {
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/helloworld.java");

        MethodInfo testMethod1 = allMethodInfo.get(1);
        String treeString = "";
        treeString = testMethod1.methodTreeRoot.toString(treeString);
        assertEquals("(root(for(if(if-else(if)(if-else(if)(if))))))",treeString);
    }


    @Test
    public void testNestMethod(){
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/Memoizer.java");
        MethodInfo testMethod1 = allMethodInfo.get(2);
        String treeString = "";
        treeString = testMethod1.methodTreeRoot.toString(treeString);
        assertEquals("(root)",treeString);

        MethodInfo testMethod2 = allMethodInfo.get(3);
        String treeString2 = "";
        treeString = testMethod2.methodTreeRoot.toString(treeString2);
        assertEquals("(root(while(if(if))(if)))",treeString);

    }

    @Test
    public void testEnumClassGetter(){
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/ChatColor.java");
        MethodInfo testMethod1 = allMethodInfo.get(0);
        assertEquals("org.bukkit.ChatColor:<init>",testMethod1.method_name);

        MethodInfo testMethod2 = allMethodInfo.get(2);
        assertEquals("org.bukkit.ChatColor:getChar",testMethod2.method_name);
        assertTrue(testMethod2.isGetter);

        MethodInfo testMethod3 = allMethodInfo.get(6);
        assertEquals("org.bukkit.ChatColor:getByChar",testMethod3.method_name);
        assertFalse(testMethod3.isGetter);
    }


    @Test
    public void testInterface(){
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/XYItemRenderer.java");
        assertEquals(allMethodInfo.size(),0);
    }

    @Test
    public void testTypeParameters(){
        LinkedList<MethodInfo> allMethodInfo = methodWalker("/DiffBuilder.java");
        MethodInfo testMethod1 = allMethodInfo.get(2);
        assertEquals("org.apache.commons.lang3.builder.DiffBuilder$Diff<Boolean>:getLeft",
                testMethod1.method_name);

        MethodInfo testMethod2 = allMethodInfo.get(5);
        assertEquals("org.apache.commons.lang3.builder.DiffBuilder$Diff<Boolean[]>:getLeft",
                testMethod2.method_name);
    }
}