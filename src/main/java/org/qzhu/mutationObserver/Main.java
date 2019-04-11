package org.qzhu.mutationObserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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
           System.err.println(e.getMessage());
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

       // Use either Maven or Gradle build directory.
       String mavenBuildDir = baseDir+project+"/target/";
       String gradleBuildDir = baseDir+project+"/build/classes/java/";
       String sourceClassDir, testClassDir;
       // Check whether the Maven build directory (target) exists.
       // If this is the case, use Mavens source and test class directory
       if (Files.exists(FileSystems.getDefault().getPath(mavenBuildDir))) {
           sourceClassDir = mavenBuildDir+"/classes/";
           testClassDir = mavenBuildDir+"/test-classes/";
       }
       // If this is not the case, use Gradles source and test class directory
       else if (Files.exists(FileSystems.getDefault().getPath(gradleBuildDir))) {
           sourceClassDir = gradleBuildDir+"/main/";
           testClassDir = gradleBuildDir+"/test/";
       }
       // If neither exist, throw exception for not finding either standard build directory
       else {
           throw new FileNotFoundException("The standard Maven or Gradle build directory is not found.");
       }

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
