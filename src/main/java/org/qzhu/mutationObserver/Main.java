package org.qzhu.mutationObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.qzhu.mutationObserver.source.MethodInfo;

/**
 * @author Qianqian Zhu
 */
class Main {
    static int returnValue = 0;

   public static void main(String[] args) {

       if(args==null){
           System.err.println("Missing configuration argument");
           returnValue =  -1;
           return;
       }

       if(args.length < 3){
           System.err.println("Missing configuration argument");
           returnValue =  -1;
           return;
       }

       String project = args[0];
       String baseDir = args[1];
       String pitestFileName = args[2];

       String resultFileName;
       if(args.length == 3){
           resultFileName = project+"mutation_observer_all_results.csv";
       }else{
           resultFileName = args[3];
       }


       try {
           analyse(project,baseDir,pitestFileName,resultFileName);
           returnValue =  0;
       } catch (IOException e) {
           System.err.println("No Such File Exception");
           returnValue = -2;
       } catch(IndexOutOfBoundsException e){
           System.err.println("Index Out Of Bounds Exception");
           returnValue = -2;
       }


   }


   public static void analyse(String project,String baseDir,String pitestFileName, String resultFileName) throws IOException {
       System.out.println("Analysing "+project);

       //String baseDir = "/Users/qianqianzhu/phd/testability/ast/project/";
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
       //String pitestFileName = "/Users/qianqianzhu/phd/testability/mutation_testing_observability/pitest_result/"+project+"_mutations.csv";
       Utils.parsePitestFile(pitestFileName,allMethodInfo);

       System.out.println("Parsing test classes results...");
       Utils.setAllMethodDirectTestFromDir(sourceClassDir,testClassDir,allMethodInfo);

       System.out.println("generating feature matrix & write results to file...");
       //String resultFileName = "./src/main/results/more2/"+project+"_all_feature_simple.csv";
       Utils.generateFeatureMatrix(allMethodInfo,resultFileName,true);
       System.out.println("finished!");
   }


}