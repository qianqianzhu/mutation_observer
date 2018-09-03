package org.qzhu.mutationObserver;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.qzhu.grammar.Java8Lexer;
import org.qzhu.grammar.Java8Parser;


/**
 * @author Qianqian Zhu
 */
class Main {
   public static void main(String[] args) {
      System.out.println("Antlr4 Example");
      try {
         /*
          * get the input file as an InputStream
          */
         InputStream inputStream = Main.class.getResourceAsStream("/helloworld.java");
         /*
          * make Lexer
          */
         Lexer lexer = new Java8Lexer(CharStreams.fromStream(inputStream));
         /*
          * get a TokenStream on the Lexer
          */
         TokenStream tokenStream = new CommonTokenStream(lexer);
         /*
          * make a Parser on the token stream
          */
         Java8Parser parser = new Java8Parser(tokenStream);
         /*
          * get the top node of the AST. This corresponds to the topmost rule of equation.q4, "equation"
          */
         ParseTree tree = parser.compilationUnit(); // parse; start at prog
         //EquationContext equationContext = parser.equation();

         System.out.println(tree.toStringTree(parser)); // print tree as text

      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}