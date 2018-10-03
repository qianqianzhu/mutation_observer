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
        String project = "pysonar2-2.1";
        gatherJhawkData(project);
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
            //System.out.println(methodCollector.methodNameCollector);
            //System.out.println(methodCollector.methodSequenceCollector);
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
        String jhawkFilename = "/Users/qianqianzhu/phd/testability/ast/jhawk/"+project+"_method.csv";
        String resultFilename = "/Users/qianqianzhu/phd/testability/ast/jhawk/all/"+project+"_all_result.csv";
        matchJhawkMethod(jhawkFilename,resultFilename,allMethodInfo);

    }

    public static HashMap<String,ArrayList<String>> parseJhawkResults(String jhawkFile) {
        try {
            HashMap<String, ArrayList<String>> jhawMethodMap = new HashMap<>();
            BufferedReader jhawkReader = new BufferedReader(new FileReader(jhawkFile));
            String line;
            while ((line = jhawkReader.readLine()) != null) {
                if(line.contains("System;Package;Class;Name; COMP;"))
                    continue; // skip header line
                StringBuffer keysb = new StringBuffer();
                line = line.replace("\"", "");  // remove '"' surrounded by texts
                line = line.replace(",", ".");  // replace ',' to '.' as decimal point in Jhawk file is ","

                String columns[] = line.split(";");  // total column no.: 31
                keysb.append(columns[1] + "." + columns[2]);
                if (columns[2].equals(columns[3]) ||
                        // nested class cases
                        (columns[2].contains("$") && columns[2].substring(columns[2].lastIndexOf("$") + 1).equals(columns[3]))) {
                    keysb.append(":<init>");

                } else {
                    keysb.append(":" + columns[3]);
                }
                String key = keysb.toString();

                ArrayList<String> jhawkmethods;
                if (jhawMethodMap.get(key) == null) {
                    jhawkmethods = new ArrayList<>();
                } else {
                    jhawkmethods = jhawMethodMap.get(key);
                }
                jhawkmethods.add(line);
                jhawMethodMap.put(key, jhawkmethods);

            }

            // sort methodInfo by NLOC: ascending
            for (String methodName : jhawMethodMap.keySet()) {
                ArrayList<String> methodLines = jhawMethodMap.get(methodName);
                Collections.sort(methodLines, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        String[] lhsColumns = lhs.split(";");
                        String[] rhsColumns = rhs.split(";");
                        int lhsNLOC = Integer.parseInt(lhsColumns[14]);
                        int rhsNLOC = Integer.parseInt(rhsColumns[14]);

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


    public static void matchJhawkMethod(String jhawkFile, String resultFilename, LinkedList<MethodInfo> allMethodInfo) throws IOException {
        HashMap<String,ArrayList<String>> jhawMethodMap = parseJhawkResults(jhawkFile);
        HashMap<String,ArrayList<MethodInfo>> allMethodInfoMap = generateMethodInfoMapByMethodName(allMethodInfo);

        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilename));
        // header
        // new feature results
        List<Node<String>> searchPatterns;
        searchPatterns= generateSimpleSearchPatterns();
        writer.write("full_name;is_public;is_static;is_void;is_nested;method_length;" +
                "kill_mut;total_mut;nested_depth;direct_test_no;void_no;getter_no;" +
                "total_method_no;method_sequence");
        for(int pid=0;pid<searchPatterns.size();pid++){
            String treeString = "";
            treeString = searchPatterns.get(pid).toString(treeString);
            writer.write(";"+treeString);
        }
        // jhawk results
        writer.write(";COMP;NOCL;NOS;HLTH;HVOC;HEFF;HBUG;" +
                "CREF;XMET;LMET;NLOC;VDEC;TDN;NAND;LOOP;MOD;NOPR;EXCT;" +
                "MDN;EXCR;HVOL;VREF;NOC;NOA;CAST;HDIF;NEXP");
        writer.write("\n");

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
                    writer.write(methodInfos.get(mid).method_name);
                    // new feature data
                    Map<String, ClassInfo> classInfoMap = sumMethodInfoByClassName(allMethodInfo);  // class-level
                    MethodInfo thisMethod = methodInfos.get(mid);
                    String treeString2 = "";
                    treeString2 = thisMethod.methodTreeRoot.toString(treeString2);
                    String className = thisMethod.className;
                    writer.write(";" +thisMethod.methodModifier.contains("public")+";"
                            +thisMethod.methodModifier.contains("static")+";"
                            +thisMethod.isVoid+";"
                            +thisMethod.isNested+";"
                            +(thisMethod.stop_line-thisMethod.start_line+1)+";"
                            +thisMethod.kill_mut+";"
                            +thisMethod.total_mut+";"
                            +(thisMethod.methodTreeRoot.maxDepth()-1)+";"
                            +thisMethod.directTestCases.size()+";"
                            +classInfoMap.get(className).voidMethodNo+";"
                            +classInfoMap.get(className).getterMethodNo+";"
                            +classInfoMap.get(className).totalMethodNo+";"
                            +treeString2);

                    for(int pid=0;pid<searchPatterns.size();pid++){
                        int matchCount = thisMethod.methodTreeRoot.matchCount(searchPatterns.get(pid));
                        writer.write(";"+Integer.toString(matchCount));
                    }

                    // jhawk data
                    for (int i = 4; i < 31; i++) {
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
