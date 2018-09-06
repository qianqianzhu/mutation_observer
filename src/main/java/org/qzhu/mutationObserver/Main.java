package org.qzhu.mutationObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
class Main {
   public static void main(String[] args) {
       String testDir ="./src/main/resources/";
       //String testDir = "/Users/qianqianzhu/phd/testability/commons-lang-LANG_3_7/src/main/java/org/apache/commons/lang3/math";
       List<String> fileNames = new ArrayList<>();
       fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);
       LinkedList<String> allMethodNameCollector = new LinkedList<>();
       LinkedList<ArrayList<String>> allMethodSequenceCollector = new LinkedList<ArrayList<String>>();
       for(String fileName: fileNames){
           MethodCollector methodCollector = Utils.getAllMethodSequenceFromFile(fileName);
           System.out.println(methodCollector.methodNameCollector);
           System.out.println(methodCollector.methodSequenceCollector);
           allMethodNameCollector.addAll(methodCollector.methodNameCollector);
           allMethodSequenceCollector.addAll(methodCollector.methodSequenceCollector);
       }

       int totalMethod = allMethodNameCollector.size();
       System.out.println("Total method no.: "+totalMethod);
       int[][] LCSMatrix = Utils.generateLCSMatrix(allMethodSequenceCollector);
       for(int row = 0; row<totalMethod;row++) {
           System.out.print(allMethodNameCollector.get(row)+" ");

           for(int col = 0; col<totalMethod; col++) {
               System.out.print(LCSMatrix[row][col]+" ");
           }

           System.out.println();
       }

   }


}