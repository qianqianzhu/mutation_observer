package org.qzhu.mutationObserver;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.qzhu.grammar.Java8Lexer;
import org.qzhu.grammar.Java8Parser;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
public class Utils {

    public static List<String> getAllJavaFilesFromDir(List<String> fileNames, String dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getAllJavaFilesFromDir(fileNames, String.valueOf(path));
                } else {
                    if(path.toAbsolutePath().toString().endsWith(".java")) {
                        fileNames.add(path.toString());
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public static MethodCollector getAllMethodSequenceFromFile(String fileName){
        try {
            // get the input file as an InputStream
            InputStream inputStream = new FileInputStream(fileName);
            // make Lexer
            Lexer lexer = new Java8Lexer(CharStreams.fromStream(inputStream));
            // get a TokenStream on the Lexer
            TokenStream tokenStream = new CommonTokenStream(lexer);
            // make a Parser on the token stream
            Java8Parser parser = new Java8Parser(tokenStream);
            // get the top node of the AST. This corresponds to the topmost rule of grammar
            ParseTree tree = parser.compilationUnit(); // parse
            // create standard walker
            ParseTreeWalker walker = new ParseTreeWalker();
            // add self-implemented listener
            MyMethodVisitor methodVisitor = new MyMethodVisitor();
            // walk the ast with self-implemented listener
            walker.walk(methodVisitor,tree);
            //System.out.println(tree.toStringTree(parser)); // print tree as text
            //System.out.println(LCSMatrix);
            return methodVisitor.getMethodCollector();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int lcs(ArrayList<String> a, ArrayList<String> b){
        int aLen = a.size();
        int bLen = b.size();
        int L[][] = new int[aLen+1][bLen+1];

        for(int i=0;i<=aLen;i++){
            for(int j=0;j<=bLen;j++){
                if(i==0 || j==0 ) {
                    L[i][j] = 0;
                }else if(a.get(i-1).equals(b.get(j-1))){
                    L[i][j] = L[i-1][j-1] +1;
                }else{
                    L[i][j] = Math.max(L[i-1][j],L[i][j-1]);
                }
            }
        }
        return L[aLen][bLen];
    }


    public static int[][] generateLCSMatrix(LinkedList<String> allMethodNameCollector,
                                            LinkedList<ArrayList<String>> allMethodSequences,
                                            String fileName) throws IOException {
        int totalMethodNo = allMethodSequences.size();
        int LCSMatrix[][] = new int[totalMethodNo][totalMethodNo];
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        for(int row =0;row<totalMethodNo;row++){
            writer.write(allMethodNameCollector.get(row)+",");

            for(int col=0;col<totalMethodNo;col++){
                LCSMatrix[row][col]=lcs(allMethodSequences.get(row),allMethodSequences.get(col));

                writer.write(Integer.toString(LCSMatrix[row][col]));
                if(col!=totalMethodNo-1){
                    writer.write(",");
                }
            }
            writer.write("\n");
            writer.flush();
        }
        writer.close();

        return LCSMatrix;
    }


}
