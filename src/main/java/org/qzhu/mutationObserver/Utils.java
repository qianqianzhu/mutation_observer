package org.qzhu.mutationObserver;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.bcel.classfile.ClassParser;
import org.qzhu.grammar.Java8Lexer;
import org.qzhu.grammar.Java8Parser;
import org.qzhu.mutationObserver.callgraph.ClassVisitor;
import org.qzhu.mutationObserver.callgraph.SourceMethodsIndexer;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Qianqian Zhu
 */
public class Utils {

    public static List<String> getAllFilesFromDir(List<String> fileNames,String suffix ,String dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getAllFilesFromDir(fileNames, suffix ,String.valueOf(path));
                } else {
                    if(path.toAbsolutePath().toString().endsWith(suffix)) {
                        fileNames.add(path.toString());
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }


    public static LinkedList<MethodInfo> getAllMethodInfoFromSource(String fileName){
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
            MethodInfoVisitor methodVisitor = new MethodInfoVisitor();
            // walk the ast with self-implemented listener
            walker.walk(methodVisitor,tree);
            //System.out.println(tree.toStringTree(parser)); // print tree as text
            //System.out.println(LCSMatrix);
            return methodVisitor.getAllMethodInfoCollector();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setAllMethodBytecodeNameFromJar(String jarFileName,LinkedList<MethodInfo> allMethodInfo){
        ClassParser cp;
        try {
            File f = new File(jarFileName);
            if (!f.exists()) {
                System.err.println("Jar file " + jarFileName + " does not exist");
            }
            JarFile jar = new JarFile(f);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory())
                    continue;

                if (!entry.getName().endsWith(".class"))
                    continue;

                cp = new ClassParser(jarFileName,entry.getName());
                HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByClassName(allMethodInfo);
                SourceMethodsIndexer methodsIndexer = new SourceMethodsIndexer(cp.parse(),allMethodInfoMap);
                methodsIndexer.start();
            }

        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setAllMethodBytecodeNameFromDir(String dir, LinkedList<MethodInfo> allMethodInfo){
        try {
            List<String> fileNames = new ArrayList<>();
            fileNames = Utils.getAllFilesFromDir(fileNames,".class",dir);
            for(String filename: fileNames) {
                ClassParser cp = new ClassParser(filename);
                HashMap<String, ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByClassName(allMethodInfo);
                SourceMethodsIndexer methodsIndexer = new SourceMethodsIndexer(cp.parse(), allMethodInfoMap);
                methodsIndexer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setAllMethodDirectTestFromJar(String sourceJarFileName,String testJarFileName, LinkedList<MethodInfo> allMethodInfo){
        ClassParser cp;
        try {
            File f = new File(testJarFileName);
            if (!f.exists()) {
                System.err.println("Jar file " + testJarFileName + " does not exist");
            }
            JarFile jar = new JarFile(f);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory())
                    continue;

                if (!entry.getName().endsWith(".class"))
                    continue;

                cp = new ClassParser(testJarFileName,entry.getName());
                HashMap<String,MethodInfo> allMethodInfoMap = generateMethodInfoMapByMethodByteName(sourceJarFileName,allMethodInfo);
//                System.out.println(allMethodInfo.size());
                ClassVisitor classVisitor = new ClassVisitor(cp.parse(),allMethodInfoMap);
                classVisitor.start();
            }

        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void setAllMethodDirectTestFromDir(String sourceDir, String testDir, LinkedList<MethodInfo> allMethodInfo){
        try {
            List<String> fileNames = new ArrayList<>();
            fileNames = Utils.getAllFilesFromDir(fileNames,".class",testDir);
            for(String filename: fileNames) {
                ClassParser cp =new ClassParser(filename);
                HashMap<String,MethodInfo> allMethodInfoMap = generateMethodInfoMapByMethodByteName(sourceDir,allMethodInfo);
                ClassVisitor classVisitor = new ClassVisitor(cp.parse(),allMethodInfoMap);
                classVisitor.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
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


    public static int[][] generateLCSMatrix(LinkedList<MethodInfo> allMethodInfo,
                                            String fileName) throws IOException {
        int totalMethodNo = allMethodInfo.size();
        int LCSMatrix[][] = new int[totalMethodNo][totalMethodNo];
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        for(int row =0;row<totalMethodNo;row++){
            MethodInfo thisMethod = allMethodInfo.get(row);
            writer.write(thisMethod.method_name+";"
                    +thisMethod.methodModifier.contains("public")+";"
                    +thisMethod.isVoid+";"
                    +(thisMethod.stop_line-thisMethod.start_line+1)+";"
                    +thisMethod.kill_mut+";"
                    +thisMethod.total_mut+";"
                    +thisMethod.method_sequence.toString()+";");

            for(int col=0;col<totalMethodNo;col++){
                LCSMatrix[row][col]=lcs(thisMethod.method_sequence,allMethodInfo.get(col).method_sequence);

                writer.write(Integer.toString(LCSMatrix[row][col]));
                if(col!=totalMethodNo-1){
                    writer.write(";");
                }
            }
            writer.write("\n");
            writer.flush();
        }
        writer.close();

        return LCSMatrix;
    }

    private static List<Node<String>> generateSearchPatterns(){
        List<Node<String>> searchPatterns = new LinkedList<>();
        String[] statements = {"if","if-else","while","for","do"};
        for(int i=0;i<statements.length;i++) {
            // 1-node pattern
            searchPatterns.add(new Node<>(statements[i]));
            for(int j=0;j<statements.length;j++){
                // 2=node pattern
                Node<String> searchPattern = new Node<>(statements[i]);
                searchPattern.addChild(new Node<>(statements[j]));
                searchPatterns.add(new Node<>(searchPattern));
            }
        }
        return searchPatterns;
    }

    public static void generateFeatureMatrix(LinkedList<MethodInfo> allMethodInfo,
                                            String fileName) throws IOException {
        int totalMethodNo = allMethodInfo.size();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        List<Node<String>> searchPatterns = generateSearchPatterns();
        // file header
        writer.write("method_name;is_public;is_void;method_lengh;kill_mut;total_mut;nested_depth;direct_test_no;method_sequence");
        for(int pid=0;pid<searchPatterns.size();pid++){
            String treeString = "";
            treeString = searchPatterns.get(pid).toString(treeString);
            writer.write(";"+treeString);
        }
        writer.write("\n");

        // match count
        for(int mid =0;mid<totalMethodNo;mid++){
            MethodInfo thisMethod = allMethodInfo.get(mid);
            String treeString2 = "";
            treeString2 = thisMethod.methodTreeRoot.toString(treeString2);

            writer.write(thisMethod.method_name+";"
                    +thisMethod.methodModifier.contains("public")+";"
                    +thisMethod.isVoid+";"
                    +(thisMethod.stop_line-thisMethod.start_line+1)+";"
                    +thisMethod.kill_mut+";"
                    +thisMethod.total_mut+";"
                    +(thisMethod.methodTreeRoot.maxDepth()-1)+";"
                    +thisMethod.directTestCases.size()+";"
                    +treeString2+";");

            for(int pid=0;pid<searchPatterns.size();pid++){
                int matchCount = thisMethod.methodTreeRoot.matchCount(searchPatterns.get(pid));
                writer.write(Integer.toString(matchCount));
                if(pid!=searchPatterns.size()-1){
                    writer.write(";");
                }
            }
            writer.write("\n");
            writer.flush();
        }
        writer.close();

    }

    public static HashMap<String,ArrayList<MethodInfo>> generateMethodInfoMapByClassName(LinkedList<MethodInfo> allMethodInfo){
        // create methods map for mutation score: key-className(without nested class), easy for iterating methodInfo
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMapByClassName = new HashMap<>();
        for(MethodInfo method: allMethodInfo){
            String methodName = method.method_name;
            String[] nameInfos = methodName.split(":");
            String className =nameInfos[0];
            String key = className;
            if(className.indexOf("$")!=-1){
                key = className.substring(0,className.indexOf("$"));
            }
            ArrayList<MethodInfo> methodInfos;
            if(!allMethodInfoMapByClassName.containsKey(key)){
                methodInfos = new ArrayList<>();
            }else{
                methodInfos = allMethodInfoMapByClassName.get(key);
            }
            methodInfos.add(method);
            allMethodInfoMapByClassName.put(key,methodInfos);
        }
        return allMethodInfoMapByClassName;
    }


    public static HashMap<String,MethodInfo> generateMethodInfoMapByMethodByteName(String file, LinkedList<MethodInfo> allMethodInfo){
        // create methods map for mutation score: key-method bytecode name, easy for iterating methodInfo
        HashMap<String,MethodInfo> allMethodInfoMapByMethodByteName = new HashMap<>();
        if (allMethodInfo.get(0).bytecodeName==null){
            if(file.endsWith(".jar")) {
                setAllMethodBytecodeNameFromJar(file, allMethodInfo);
            }else{
                setAllMethodBytecodeNameFromDir(file, allMethodInfo);
            }
        }
        for(MethodInfo method: allMethodInfo){
            allMethodInfoMapByMethodByteName.put(method.bytecodeName,method);
        }
        return allMethodInfoMapByMethodByteName;
    }


    public static void parsePitestFile(String pitest_filename,LinkedList<MethodInfo> allMethodInfo) throws IOException {
        // create methods map for mutation score: key-className(without nested class)
        HashMap<String,ArrayList<MethodInfo>> allMethodMap = generateMethodInfoMapByClassName(allMethodInfo);

        // read pit results from file
        BufferedReader pitest_reader = new BufferedReader(new FileReader(pitest_filename));
        String line;
        while ((line = pitest_reader.readLine()) != null) {
            String columns[] = line.split(",");
            if (columns.length < 6)
                continue;
            String className = columns[1];
            String classNameWithoutNest=className;
            if(className.indexOf("$")!=-1) {
                classNameWithoutNest = className.substring(0, className.indexOf("$"));
            }
            //System.out.println(classNameWithoutNest);
            int lineNo = Integer.parseInt(columns[4].trim());
            // iterate method map
            if(allMethodMap.containsKey(classNameWithoutNest)){
                // match method name
                ArrayList<MethodInfo> methodInfos = allMethodMap.get(classNameWithoutNest);
                for(MethodInfo method: methodInfos){
                    // match method location
                    if(lineNo>=method.start_line && lineNo <= method.stop_line){
                        method.total_mut = method.total_mut+1;
                        if(!(columns[5].equals("SURVIVED") || columns[5].equals("NO_COVERAGE"))){
                            method.kill_mut = method.kill_mut+1;
                        }
                    }
                }
            }
        }
    }


}
