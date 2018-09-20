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

//       String testDir ="./src/main/resources/";
       String baseDir = "/Users/qianqianzhu/phd/testability/ast/project/";
       String project = "curator-curator-x-zkclient-bridge-3.0.0";
       String testDir = baseDir+project+"/src/main/java/";
       List<String> fileNames = new ArrayList<>();
       fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);

//       String testDir2 = "/Users/qianqianzhu/phd/testability/commons-math-MATH_3_6_1_pitest/src/main/java/";
//       List<String> fileNames2 = new ArrayList<>();
//       fileNames2 = Utils.getAllJavaFilesFromDir(fileNames2,testDir2);
//       fileNames.addAll(fileNames2);

       LinkedList<MethodInfo> allMethodInfo = new LinkedList<>();
       for(String fileName: fileNames){
           System.out.println("Processing "+fileName);
           LinkedList<MethodInfo> methodInfo = Utils.getAllMethodInfoFromSource(fileName);
           //System.out.println(methodCollector.methodNameCollector);
           //System.out.println(methodCollector.methodSequenceCollector);
           allMethodInfo.addAll(methodInfo);
       }

       int totalMethod = allMethodInfo.size();
       System.out.println("Total method no.: "+totalMethod);

       System.out.println("Parsing Pitest results...");
       String pitestFileName = "/Users/qianqianzhu/phd/testability/mutation_testing_observability/pitest_result/"+project+"_mutations.csv";
       Utils.parsePitestFile(pitestFileName,allMethodInfo);
//       String pitestFileName2 = "/Users/qianqianzhu/phd/testability/mutation_testing_observability/pitest_result/commons-math-MATH_3_6_1_mutations.csv";
//       Utils.parsePitestFile(pitestFileName2,allMethodInfo);
       System.out.println("generating feature matrix & write results to file...");
       String resultFileName = "./src/main/results/"+project+"_match_count.csv";
       Utils.generateFeatureMatrix(allMethodInfo,resultFileName);
       System.out.println("finished!");

   }


}