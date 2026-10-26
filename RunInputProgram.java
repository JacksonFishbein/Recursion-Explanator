
import java.io.*;
import java.nio.file.*;
import javax.tools.*;

public class RunInputProgram {

   public static void run(String sourceCode) {
      Path tempDir = null;

      try {
         //Create a temporary folder for the input program
         tempDir = Files.createTempDirectory("inputProgram");
         //Find the public class name
         java.util.regex.Matcher matcher =
         java.util.regex.Pattern.compile(
            "\\bpublic\\s+class\\s+(\\w+)"
         ).matcher(sourceCode);
         
         if (!matcher.find()) {
            System.out.println("Could not find a public class in the input.");
            return;
         }

         String className = matcher.group(1);
         //Save the source code using the required filename
         Path javaFile = tempDir.resolve(className + ".java");
         Files.writeString(javaFile, sourceCode);
         //Get the system Java compiler
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
         
         if (compiler == null) {
            System.out.println("Java compiler not found. Install a JDK.");
            return;
         }
         
         //Compile the input program
         int result = compiler.run(null, null, null,"-d", tempDir.toString(),javaFile.toString());

         if (result != 0) {
            System.out.println("Compilation failed.");
            return;
         }

         //Run the compiled program as a separate process
         ProcessBuilder builder = new ProcessBuilder("java", "-cp", tempDir.toString(),className);
         builder.inheritIO();
         Process process = builder.start();
         process.waitFor();

      } catch (Exception e) {
      System.out.println("Error running input program: " + e.getMessage());
   } finally {
      //Clean temp files
   }
}
}
