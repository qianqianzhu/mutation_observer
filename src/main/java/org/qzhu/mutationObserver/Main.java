package org.qzhu.mutationObserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
class Main {

   public static void main(String[] args) throws IOException {

//       String testDir ="./src/main/resources/";
       String testDir = "/Users/qianqianzhu/phd/testability/commons-lang-LANG_3_7/src/main/java/";
       List<String> fileNames = new ArrayList<>();
       fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);

       String testDir2 = "/Users/qianqianzhu/phd/testability/commons-math-MATH_3_6_1_pitest/src/main/java/";
       List<String> fileNames2 = new ArrayList<>();
       fileNames2 = Utils.getAllJavaFilesFromDir(fileNames2,testDir2);

       fileNames.addAll(fileNames2);

       LinkedList<MethodInfo> allMethodInfo = new LinkedList<>();
       for(String fileName: fileNames){
           System.out.println("Processing "+fileName);
           LinkedList<MethodInfo> methodInfo = Utils.getAllMethodInfoFromFile(fileName);
           //System.out.println(methodCollector.methodNameCollector);
           //System.out.println(methodCollector.methodSequenceCollector);
           allMethodInfo.addAll(methodInfo);
       }

       int totalMethod = allMethodInfo.size();
       System.out.println("Total method no.: "+totalMethod);

       System.out.println("Parsing Pitest results...");
       String pitestFileName = "/Users/qianqianzhu/phd/testability/Observer/pitest_result/commons-lang-LANG_3_7_mutations.csv";
       Utils.parsePitestFile(pitestFileName,allMethodInfo);
       String pitestFileName2 = "/Users/qianqianzhu/phd/testability/Observer/pitest_result/apache_commons_math3_mutations.csv";
       Utils.parsePitestFile(pitestFileName2,allMethodInfo);
       System.out.println("generating LCS matrix...");
       System.out.println("Write results to file...");
       String resultFileName = "./src/main/results/two_projects_lcs.csv";
       Utils.generateLCSMatrix(allMethodInfo,resultFileName);
       System.out.println("finished!");


//       int[][] LCSMatrix = Utils.generateLCSMatrix(allMethodNameCollector,allMethodSequenceCollector);
//       for(int row = 0; row<totalMethod;row++) {
//           System.out.print(allMethodNameCollector.get(row)+" ");
//
//           for(int col = 0; col<totalMethod; col++) {
//               System.out.print(LCSMatrix[row][col]+" ");
//           }
//           System.out.println();
//       }

   }


}