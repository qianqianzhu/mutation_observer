package org.qzhu.mutationObserver;

import org.qzhu.mutationObserver.source.MethodInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 */
class Main {

   public static void main(String[] args) throws IOException {
       String []  projects = {
               "Bukkit-1.7.9-R0.2",
               "commons-lang-LANG_3_7",
               "commons-math-MATH_3_6_1",
               "java-apns-apns-0.2.3",
               "jfreechart-1.5.0",
               "pysonar2-2.1"};

       for (String project: projects){
           analyse(project,true);
       }

//       analyse("java-apns-apns-0.2.3",true);

   }


   public static void analyse(String project,boolean is_simple) throws IOException {
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
           LinkedList<MethodInfo> methodInfo = Utils.getAllMethodInfoFromSource(fileName,is_simple);
           allMethodInfo.addAll(methodInfo);
       }

       int totalMethod = allMethodInfo.size();
       System.out.println("Total method no.: "+totalMethod);

       System.out.println("Parsing Pitest results...");
       String pitestFileName = "/Users/qianqianzhu/phd/testability/mutation_testing_observability/pitest_result/"+project+"_mutations.csv";
       Utils.parsePitestFile(pitestFileName,allMethodInfo);

       System.out.println("Parsing test classes results...");
       Utils.setAllMethodDirectTestFromDir(sourceClassDir,testClassDir,allMethodInfo);

       System.out.println("generating feature matrix & write results to file...");
       String resultFileName = "./src/main/results/more2/"+project+"_all_feature_simple.csv";
       Utils.generateFeatureMatrix(allMethodInfo,resultFileName,is_simple);
       System.out.println("finished!");

   }


}