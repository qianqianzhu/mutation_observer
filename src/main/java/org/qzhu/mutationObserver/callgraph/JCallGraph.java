package org.qzhu.mutationObserver.callgraph;

import org.apache.bcel.classfile.ClassParser;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Qianqian Zhu
 * Class copied with modifications from java-callgraph: https://github.com/gousiosg/java-callgraph
 */
public class JCallGraph {

    public static void main(String[] args) {
        String jarFile = "/Users/qianqianzhu/phd/testability/ast/project/commons-lang-LANG_3_7/target/commons-lang3-3.7.jar";
        ClassParser cp;
        try {
//            for (String arg : args) {

            File f = new File(jarFile);

            if (!f.exists()) {
                System.err.println("Jar file " + jarFile + " does not exist");
            }

            JarFile jar = new JarFile(f);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory())
                    continue;

                if (!entry.getName().endsWith(".class"))
                    continue;

                cp = new ClassParser(jarFile,entry.getName());
//                SourceMethodsIndexer methodsIndexer = new SourceMethodsIndexer(cp.parse());
                    ClassVisitor visitor = new ClassVisitor(cp.parse());
//                methodsIndexer.start();
//                Map<String,Integer> method2sourceLine = methodsIndexer.getMethod2sourceLine();
//                for (String methodName: method2sourceLine.keySet()){
//                    System.out.println(methodName+":"+method2sourceLine.get(methodName));
//                }
                    visitor.start();
            }
//            }
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
