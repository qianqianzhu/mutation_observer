package org.qzhu.mutationObserver.jhawk;

import org.qzhu.mutationObserver.Utils;
import org.qzhu.mutationObserver.source.ClassInfo;
import org.qzhu.mutationObserver.source.MethodInfo;
import org.qzhu.mutationObserver.source.Node;

import java.io.*;
import java.util.*;

import static org.qzhu.mutationObserver.Utils.generateMethodInfoMapByMethodName;
import static org.qzhu.mutationObserver.Utils.generateSimpleSearchPatterns;
import static org.qzhu.mutationObserver.Utils.sumMethodInfoByClassName;

/**
 * @author Qianqian Zhu
 */
public class JhawkMatcher {

    public static void main(String args[]) throws IOException {

        String []  projects = {
                "Bukkit-1.7.9-R0.2",
                "commons-lang-LANG_3_7",
                "commons-math-MATH_3_6_1",
                "java-apns-apns-0.2.3",
                "jfreechart-1.5.0",
                "pysonar2-2.1"};

        for (String project: projects){
            gatherJhawkData(project);
        }

    }

    public static void gatherJhawkData(String project) throws IOException {
        System.out.println("Analysing "+project);

        String baseDir = "/Users/qianqianzhu/phd/testability/ast/project/";
        String testDir = baseDir+project+"/src/main/java/";
        String sourceClassDir = baseDir+project+"/target/classes/";
        String testClassDir = baseDir+project+"/target/test-classes/";

        List<String> fileNames = new ArrayList<>();
        fileNames = Utils.getAllFilesFromDir(fileNames,".java",testDir);

        LinkedList<MethodInfo> allMethodInfo = new LinkedList<>();
        for(String fileName: fileNames){
            System.out.println("Processing "+fileName);
            LinkedList<MethodInfo> methodInfo = Utils.getAllMethodInfoFromSource(fileName,true);
            allMethodInfo.addAll(methodInfo);
        }

        int totalMethod = allMethodInfo.size();
        System.out.println("Total method no.: "+totalMethod);

        System.out.println("Parsing Pitest results...");
        String pitestFileName = "/Users/qianqianzhu/phd/testability/mutation_testing_observability/pitest_result/"+project+"_mutations.csv";
        Utils.parsePitestFile(pitestFileName,allMethodInfo);

        System.out.println("Parsing test classes results...");
        Utils.setAllMethodDirectTestFromDir(sourceClassDir,testClassDir,allMethodInfo);

        System.out.println("Parsing Jhawk results...");
        String jhawkFileMethod = "/Users/qianqianzhu/phd/testability/ast/jhawk/"+project+"/"+project+"_method.csv";
        String jhawkFileClass = "/Users/qianqianzhu/phd/testability/ast/jhawk/"+project+"/"+project+"_class.csv";
        String jhawkAll = "/Users/qianqianzhu/phd/testability/ast/jhawk/"+project+"/"+project+"_all.csv";
        combineJhawkResults(jhawkFileMethod,jhawkFileClass,jhawkAll);
        String resultFilename = "/Users/qianqianzhu/phd/testability/ast/jhawk/all/"+project+"_all_result.csv";
        matchJhawkMethod(jhawkAll,resultFilename,allMethodInfo);
    }

    public static HashMap<String,ArrayList<String>> parseJhawkResults(String jhawkFile) {
        try {
            HashMap<String, ArrayList<String>> jhawMethodMap = new HashMap<>();
            BufferedReader jhawkReader = new BufferedReader(new FileReader(jhawkFile));
            String line;
            while ((line = jhawkReader.readLine()) != null) {
                if(line.contains("full_name;COMP;NOCL;NOS;HLTH;HVOC;HEFF;HBUG;"))
                    continue; // skip header line

                String columns[] = line.split(";");  // total column no.: 367
                String key = columns[0];

                ArrayList<String> jhawkmethods;
                if (jhawMethodMap.get(key) == null) {
                    jhawkmethods = new ArrayList<>();
                } else {
                    jhawkmethods = jhawMethodMap.get(key);
                }
                jhawkmethods.add(line);
                jhawMethodMap.put(key, jhawkmethods);

            }
            jhawkReader.close();

            // sort methodInfo by NLOC: ascending
            for (String methodName : jhawMethodMap.keySet()) {
                ArrayList<String> methodLines = jhawMethodMap.get(methodName);
                Collections.sort(methodLines, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        String[] lhsColumns = lhs.split(";");
                        String[] rhsColumns = rhs.split(";");
                        int lhsNLOC = Integer.parseInt(lhsColumns[11]);
                        int rhsNLOC = Integer.parseInt(rhsColumns[11]);

                        if (lhsNLOC == rhsNLOC)
                            return 0;
                        else if (lhsNLOC > rhsNLOC)
                            return 1;
                        else
                            return -1;
                    }
                });
            }
            return jhawMethodMap;
        }
        catch (IOException ioException){
            System.err.println(ioException.getMessage());
            return null;
        }
    }


    public static HashMap<String,String> parseJhawkClassResults(String jhawkFileClass){
        try {
            // parse class metrics
            HashMap<String, String> jhawClassMap = new HashMap<>();  // store class metrics info: key-className, value-all class-level metrics line
            BufferedReader jhawkReaderClass = new BufferedReader(new FileReader(jhawkFileClass));
            String lineClass;
            while ((lineClass = jhawkReaderClass.readLine()) != null) {
                if (lineClass.contains("System;Package;Name; No. Methods;"))
                    continue; // skip header line
                StringBuffer keysb = new StringBuffer();
                StringBuffer valuesb = new StringBuffer();
                lineClass = lineClass.replace("\"", "");  // remove '"' surrounded by texts

                String columns[] = lineClass.split(";");  // total column no.: 43
                keysb.append(columns[1] + "." + columns[2]);  // className
                for(int i=3;i<43;i++) {
                    columns[i] = columns[i].replace(",", "."); // replace ',' to '.' as decimal point in Jhawk file is ","
                    if((i!=29)) // drop columns[29]: Superclass
                        valuesb.append(";"+columns[i]);
                }
                jhawClassMap.put(keysb.toString(),valuesb.toString());
            }
            jhawkReaderClass.close();
            return jhawClassMap;
        }
        catch (IOException ioException){
            System.err.println(ioException.getMessage());
            return null;
        }

    }

    public static void combineJhawkResults(String jhawkFileMethod,String jhawkFileClass,String resultFile) {
        try {
            HashMap<String, String> jhawClassMap = parseJhawkClassResults(jhawkFileClass);
            BufferedReader jhawkReader = new BufferedReader(new FileReader(jhawkFileMethod));
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
            writer.write("full_name;COMP;NOCL;NOS;HLTH;HVOC;HEFF;HBUG;CREF;" +
                    "XMET;LMET;NLOC;VDEC;TDN;NAND;LOOP;MOD;NOPR;EXCT;MDN;EXCR;HVOL;" +
                    "VREF;NOC;NOA;CAST;HDIF;NEXP;No.Methods(class);LCOM(class);" +
                    "AVCC(class);NOS(class);HBUG(class);HEFF(class);UWCS(class);" +
                    "INST(class);PACK(class);RFC(class);CBO(class);MI(class);" +
                    "CCML(class);NLOC(class);RVF(class);LCOM2(class);MAXCC(class);" +
                    "R-R(class);NSUB(class);NSUP(class);NCO(class);FOUT(class);" +
                    "DIT(class);CCOM(class);COH(class);S-R(class);MINC(class);" +
                    "EXT(class);INTR(class);MPC(class);HVOL(class);HIER(class);" +
                    "HLTH(class);SIX(class);TCC(class);NQU(class);F-IN(class);" +
                    "MOD(class);LMC(class)\n");
            String line;
            while ((line = jhawkReader.readLine()) != null) {
                if(line.contains("System;Package;Class;Name; COMP;"))
                    continue; // skip header line
                StringBuffer lineSB = new StringBuffer();
                line = line.replace("\"", "");  // remove '"' surrounded by texts

                String columns[] = line.split(";");  // total column no.: 31
                // remove class name suffix by Jhawk
                String[] splits = columns[2].split("\\$");
                StringBuffer newClassName = new StringBuffer();
                for (String split: splits){
                    if(matchesEndWithSuffix(split)){
                        int end = split.lastIndexOf("_");
                        split = split.substring(0,end);
                    }
                    newClassName.append(split+"$");
                }

                lineSB.append(columns[1]+".");
                if (newClassName.length()!=0) {
                    lineSB.append(newClassName.deleteCharAt(newClassName.length() - 1).toString());
                }else{
                    lineSB.append("$");
                }
                String className = columns[1] + "." + columns[2];  // original Jhawk class for getting class-level metrics

                // remove method name suffix by Jhawk
                String methodName = columns[3];
                if (matchesEndWithSuffix(methodName)){
                    int end = methodName.lastIndexOf("_");
                    methodName = methodName.substring(0,end);
                }

                if (columns[2].equals(columns[3]) ||
                        // nested class case
                        (columns[2].contains("$") && columns[2].substring(columns[2].lastIndexOf("$") + 1).equals(columns[3]))) {
                    lineSB.append(":<init>");

                } else {
                    lineSB.append(":" + methodName);
                }
                for(int i=4;i<31;i++){
                    columns[i] = columns[i].replace(",", "."); // replace ',' to '.' as decimal point in Jhawk file is ","
                    lineSB.append(";"+columns[i]);
                }
                // write combined results to file
                writer.write(lineSB.toString()+jhawClassMap.get(className)+"\n");
                writer.flush();
            }
            jhawkReader.close();
            writer.close();
        }
        catch (IOException ioException){
            System.err.println(ioException.getMessage());
        }
    }

    public static boolean matchesEndWithSuffix(String s) {
        return s.matches(".+_[0-9]+$");
    }


    public static void matchJhawkMethod(String jhawkFile, String resultFilename, LinkedList<MethodInfo> allMethodInfo) throws IOException {
        HashMap<String,ArrayList<String>> jhawMethodMap = parseJhawkResults(jhawkFile);
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByMethodName(allMethodInfo);

        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilename));
        // header
        // new feature results
        List<Node<String>> searchPatterns;
        searchPatterns= generateSimpleSearchPatterns();
        writer.write("full_name;is_public;is_static;is_void;is_nested;method_length;" +
                "kill_mut;total_mut;nested_depth;direct_test_no;test_distance;assertionNo;testNLOC;void_no;" +
                "getter_no;total_method_no;method_sequence");
        for(int pid=0;pid<searchPatterns.size();pid++){
            String treeString = "";
            treeString = searchPatterns.get(pid).toString(treeString);
            writer.write(";"+treeString);
        }
        // jhawk results
        writer.write(";COMP;NOCL;NOS;HLTH;HVOC;HEFF;HBUG;CREF;" +
                "XMET;LMET;NLOC;VDEC;TDN;NAND;LOOP;MOD;NOPR;EXCT;MDN;EXCR;HVOL;" +
                "VREF;NOC;NOA;CAST;HDIF;NEXP;No.Methods(class);LCOM(class);" +
                "AVCC(class);NOS(class);HBUG(class);HEFF(class);UWCS(class);" +
                "INST(class);PACK(class);RFC(class);CBO(class);MI(class);" +
                "CCML(class);NLOC(class);RVF(class);LCOM2(class);MAXCC(class);" +
                "R-R(class);NSUB(class);NSUP(class);NCO(class);FOUT(class);" +
                "DIT(class);CCOM(class);COH(class);S-R(class);MINC(class);" +
                "EXT(class);INTR(class);MPC(class);HVOL(class);HIER(class);" +
                "HLTH(class);SIX(class);TCC(class);NQU(class);F-IN(class);" +
                "MOD(class);LMC(class)\n");
        // data
        for(String methodName: jhawMethodMap.keySet()){
            ArrayList<String> methodLines = jhawMethodMap.get(methodName);
            ArrayList<MethodInfo> methodInfos = allMethodInfoMap.get(methodName);
            if (methodInfos==null) {
                System.out.println("null exception:" + methodName);
            }
            else if(methodInfos.size()!= methodLines.size()){
                System.out.println("size exception:" + methodName);
            }
            else{
                for(int mid=0;mid<methodLines.size();mid++) {
                    String[] columns = methodLines.get(mid).split(";");
                    // new feature data
                    Map<String, ClassInfo> classInfoMap = sumMethodInfoByClassName(allMethodInfo);  // class-level
                    MethodInfo thisMethod = methodInfos.get(mid);
                    String treeString2 = "";
                    treeString2 = thisMethod.methodTreeRoot.toString(treeString2);
                    String className = thisMethod.className;
                    writer.write(methodInfos.get(mid).method_name);
                    writer.write(";" + thisMethod.methodModifier.contains("public") + ";"
                            + thisMethod.methodModifier.contains("static") + ";"
                            + thisMethod.isVoid + ";"
                            + thisMethod.isNested + ";"
                            + (thisMethod.stop_line - thisMethod.start_line + 1) + ";"
                            + thisMethod.kill_mut + ";"
                            + thisMethod.total_mut + ";"
                            + (thisMethod.methodTreeRoot.maxDepth() - 1) + ";"
                            + thisMethod.directTestCases.size() + ";"
                            + thisMethod.testReachDistance + ";"
                            + thisMethod.assertionNo + ";"
                            + thisMethod.testNLOC+";"
                            + classInfoMap.get(className).voidMethodNo + ";"
                            + classInfoMap.get(className).getterMethodNo + ";"
                            + classInfoMap.get(className).totalMethodNo + ";"
                            + treeString2);

                    for (int pid = 0; pid < searchPatterns.size(); pid++) {
                        int matchCount = thisMethod.methodTreeRoot.matchCount(searchPatterns.get(pid));
                        writer.write(";" + Integer.toString(matchCount));
                    }

                    for (int i = 1; i < 67; i++) {
                        writer.write(";" + columns[i]);
                    }

                    writer.write("\n");
                    writer.flush();
                }
            }
        }
        writer.close();
    }

}
