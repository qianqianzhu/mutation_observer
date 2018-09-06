package org.qzhu.mutationObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
class Main {
   public static void main(String[] args) throws IOException {
       //String testDir ="./src/main/resources/";
       String testDir = "/Users/qianqianzhu/phd/testability/commons-lang-LANG_3_7/src/main/java/";
       List<String> fileNames = new ArrayList<>();
       fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);
       LinkedList<String> allMethodNameCollector = new LinkedList<>();
       LinkedList<ArrayList<String>> allMethodSequenceCollector = new LinkedList<ArrayList<String>>();
       for(String fileName: fileNames){
           System.out.println("Processing "+fileName);
           MethodCollector methodCollector = Utils.getAllMethodSequenceFromFile(fileName);
           //System.out.println(methodCollector.methodNameCollector);
           //System.out.println(methodCollector.methodSequenceCollector);
           allMethodNameCollector.addAll(methodCollector.methodNameCollector);
           allMethodSequenceCollector.addAll(methodCollector.methodSequenceCollector);
       }
       System.out.println("generating LCS matrix...");
       int totalMethod = allMethodNameCollector.size();
       System.out.println("Total method no.: "+totalMethod);
       String fileName = "./src/main/results/commons-lang-LANG_3_7_lcs.csv";
       Utils.generateLCSMatrix(allMethodNameCollector,allMethodSequenceCollector,fileName);
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