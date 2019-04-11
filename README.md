# mutation_observer: tool for mutation testing observer
This tool is designed to measure the *observability* features of mutation testing at the method level. In another word, you can learn how hard to achieve 100% mutation score of each method in your project.
## Compile
The mutation_observer package is build with maven. Install maven and do:
```java
mvn package
```
This will produce a `target` directory with the following two jars:

- mutationObserver-1.0-SNAPSHOT.jar: This is the standard maven packaged jar
- mutationObserver-1.0-SNAPSHOT-jar-with-dependencies.jar: This is an executable jar which includes the mutation mutation observer

## Run
The current version of `mutation_observer` works on the assumption that the observed project is built using either Maven or Gradle (i.e. it expects the build directory of either build tool). Before running `mutation_observer`, you need to compile and test the target project (`mvn test`) and obtain the [PiTest](http://pitest.org/quickstart/maven/) mutation result for your project. The PiTest mutation result must in `csv` format. You can achieve that with `outputFormats` in Pitest plugin:
```xml
<plugin>
     <groupId>org.pitest</groupId>
     <artifactId>pitest-maven</artifactId>
     <version>1.4.0</version>
     <configuration>
         <skip>false</skip>̨
         <outputFormats>
            <outputFormat>CSV</outputFormat>
         </outputFormats>
     </configuration>
 </plugin>
```

To run `mutation_observer`, you need to specify four parameters:
- `project_name`: project name
- `project_dir`: project directory
- `PiTest_file_path`: the file path for PiTest mutation results (in csv format)
- `output_file_path`: file path for output results. This parameter is optional. If this parameter not specified, the output file will be in the current dir with name of `[project_name]_mutation_observer_all_results.csv`).
```java
java -jar mutationObserver-1.0-SNAPSHOT-jar-with-dependencies.jar [project_name] [project_dir] [PiTest_file_path] <output_file_path>
```

## Output
The output csv file contains 21 columns:

- method_name: method name
- is_public: whether the method is public or not
- is_static: whether the method is static or not
- is_void: whether the return value of the method is void or not
- is_nested: whether the method is located in a nested class or not
- method_length: the number of lines of code in the method
- kill_mut: the total number of mutations killed for this method
- total_mut: the total number of mutations generated from this method
- nested_depth: the maximum number of nested depth
- direct_test_no: the number of methods directly invoked by the test methods
- test_distance: the shortest method call sequence required to invoke the method in test methods
- void_no: the number of void methods in the class where the method located
- getter_no: the number of getter methods in the class where the method located
- total_method_no: the total number of methods in the class where the method located
- method_sequence: the text representation of control-flow sequence for this method, e.g., `(root(cond))`
- (cond): the number of conditions (`if`, `if-else` and `switch`) in the method
- (cond(cond)): the number of nested conditions (e.g. `if{if{}}`) in the method
- (cond(loop)): the number of nested condition-loops (e.g. `if{for{}}`) in the method
- (loop): the number of loops (`for`,  `while` and `do-while`) in the method
- (loop(cond)): the number of nested loop-conditions (e.g. `for{if{}}}``) in the method
- (loop(loop)): the number of nested loop-conditions (e.g. `for{for{}}`) in the method

## Example
You can learn how to use `mutation_observer` by following the example here step by step:

1. first download `mutation_observer` via GitHub repository:
```
git clone https://github.com/qianqianzhu/mutation_observer.git
```

2. compile `mutation_observer` with maven:
```java
cd mutation_observer
mvn package
```

3. execute `mutation_observer` with example project `testProjec`:

```java
java -jar mutationObserver-1.0-SNAPSHOT-jar-with-dependencies.jar testProject ../src/test/test_resources/ ../src/test/test_resources/testProject/target/pit-reports/201901170312/mutations.csv
```

4. Now you can find the output file in your current directory `testProject_mutation_observer_all_results.csv`:

```
method_name;is_public;is_static;is_void;is_nested;method_length;kill_mut;total_mut;nested_depth;direct_test_no;test_distance;void_no;getter_no;total_method_no;method_sequence;(cond);(cond(cond));(cond(loop));(loop);(loop(cond));(loop(loop))
org.testproject.A:methodA;true;false;true;false;3;0;1;0;2;1;5;0;5;(root);0;0;0;0;0;0
org.testproject.A:methodB;true;false;true;false;3;0;1;0;0;2;5;0;5;(root);0;0;0;0;0;0
org.testproject.A:methodC;true;false;true;false;4;0;2;0;0;2;5;0;5;(root);0;0;0;0;0;0
org.testproject.A:methodD;true;false;true;false;3;0;1;0;1;1;5;0;5;(root);0;0;0;0;0;0
org.testproject.A:methodE;true;false;true;false;3;0;1;0;0;3;5;0;5;(root);0;0;0;0;0;0
```
