# bil481_HW1

+ First, be sure that Antlr4.6 and GraphViz is well installed on your device

+ Afterwards run "java -jar ANTLR_JAR_PATH Java8.g4" in your comment prompt to generate Java8.g4, Java8Lexer.java, Java8Parser.java, Java8BaseListener.java, Java8Listener.java, Java8Lexer.tokens and Java8.tokens

+ Then compile all java files using "javac *.java"

+ Then, run "java org.antlr.v4.gui.TestRig Java8 compilationUnit -gui" and paste some basic java source code to test the grammar

+ Then, use "cat input/test.java | java CallGraphListener" to test your results

+ Then, to create the output file that will contain a png file use "mkdir output"

+ Then, use "cat input/test.java | java CallGraphListener | dot -T png -o output/test.png" command to pipe the output to its file

+ Generated graph can be seen in png file in output directory. By changing the methods in test.java file you can observe the chances in the graph.  
