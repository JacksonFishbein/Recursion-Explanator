import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import javax.tools.*;
import java.util.function.Consumer;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RunInputProgram {
   private static volatile Process currentProcess;
   private static final int MAX_DEPTH = 5;

   public static void run(String sourceCode) {
      run(sourceCode, System.out::println);
   }

   public static void run(String sourceCode, Consumer<String> output) {
      Path tempDir = null;

      try {
         // Create a temporary directory for the submitted program.
         tempDir = Files.createTempDirectory("inputProgram");

         // Find the public class name.
         Matcher matcher = Pattern.compile(
                 "\\bpublic\\s+(?:final\\s+)?class\\s+(\\w+)"
         ).matcher(sourceCode);

         if (!matcher.find()) {
            System.out.println("Could not find a public class.");
            return;
         }

         String className = matcher.group(1);

         // Instrument recursive methods.
         String modifiedCode = instrumentRecursiveMethods(sourceCode);

         // Save the submitted program and generated tracing class.
         Path javaFile = tempDir.resolve(className + ".java");
         Files.writeString(javaFile, modifiedCode);

         Path traceFile = tempDir.resolve("RecursionTrace.java");
         Files.writeString(traceFile, getTraceSource());

         // Compile both Java files.
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

         if (compiler == null) {
            output.accept("Java compiler not found. Install a JDK.");
            return;
         }

         ByteArrayOutputStream compilerOutput = new ByteArrayOutputStream();
         int result = compiler.run(
                 null,
                 compilerOutput,
                 compilerOutput,
                 "-d",
                 tempDir.toString(),
                 javaFile.toString(),
                 traceFile.toString()
         );
         String compileMessages = compilerOutput.toString();
         if (!compileMessages.isEmpty()) {
            for (String message : compileMessages.split("\\R")) {
               output.accept(message);
            }
         }
         if (result != 0) {
            output.accept("Compilation failed.");
            return;
         }

         // Run the submitted program.
         output.accept("");
         output.accept("       RECURSION STEP VISUALIZER");
         output.accept("");
         output.accept("Maximum displayed recursion depth: " + MAX_DEPTH);
         output.accept("Press ENTER to advance each step.");
         output.accept("Type Q to continue automatically.");
         output.accept("");

         ProcessBuilder builder = new ProcessBuilder(
                 "java",
                 "-cp",
                 tempDir.toString(),
                 className
         );

         builder.redirectErrorStream(true);

         Process process = builder.start();
         currentProcess = process;
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

         String line;
         while ((line = reader.readLine()) != null) {
            output.accept(line);
         }
         process.waitFor();

      } catch (Exception e) {
         System.out.println("Error running input program: " + e.getMessage());

      } finally {
         // Delete temporary files.
         if (tempDir != null) {
            try {
               Files.walk(tempDir)
                       .sorted(Comparator.reverseOrder())
                       .forEach(path -> {
                          try {
                             Files.deleteIfExists(path);
                          } catch (IOException e) {
                             // Ignore cleanup errors.
                          }
                       });
            } catch (IOException e) {
               // Ignore cleanup errors.
            }
         }
      }
   }

   // Find and instrument methods that call themselves.
   private static String instrumentRecursiveMethods(String source) {
      Pattern methodPattern = Pattern.compile(
              "(?m)(?:(?:public|protected|private)\\s+)?" +
                      "(?:(?:static|final|synchronized|strictfp)\\s+)*" +
                      "[\\w$<>\\[\\].,?]+\\s+" +
                      "([\\w$]+)\\s*\\(([^)]*)\\)\\s*\\{"
      );

      Matcher matcher = methodPattern.matcher(source);
      List<MethodEdit> edits = new ArrayList<>();

      while (matcher.find()) {
         String methodName = matcher.group(1);
         int openBrace = matcher.end() - 1;
         int closeBrace = findMatchingBrace(source, openBrace);

         if (closeBrace == -1) {
            continue;
         }

         String methodBody = source.substring(openBrace + 1, closeBrace);

         Pattern recursiveCall = Pattern.compile(
                 "\\b" + Pattern.quote(methodName) + "\\s*\\("
         );

         if (!recursiveCall.matcher(methodBody).find()) {
            continue;
         }

         System.out.println("Instrumenting recursive method: " + methodName);

         String modifiedBody = instrumentMethodBody(methodBody, methodName);

         String replacement =
                 "{\n" +
                         "RecursionTrace.enter(\"" + methodName + "\");\n" +
                         modifiedBody +
                         "\n}";

         edits.add(new MethodEdit(openBrace, closeBrace + 1, replacement));
      }

      // Apply edits from the bottom of the source upward so indexes stay valid.
      edits.sort((a, b) -> Integer.compare(b.start, a.start));

      StringBuilder result = new StringBuilder(source);

      for (MethodEdit edit : edits) {
         result.replace(edit.start, edit.end, edit.replacement);
      }

      return result.toString();
   }

   // Find the closing brace that matches the opening method brace.
   private static int findMatchingBrace(String source, int start) {
      int braceCount = 0;

      for (int i = start; i < source.length(); i++) {
         char current = source.charAt(i);

         if (current == '{') {
            braceCount++;
         } else if (current == '}') {
            braceCount--;

            if (braceCount == 0) {
               return i;
            }
         }
      }

      return -1;
   }

   // Add tracing calls to return statements.
   private static String instrumentMethodBody(String body, String methodName) {
      Pattern valueReturn = Pattern.compile("\\breturn\\s+([^;]+);");
      Matcher matcher = valueReturn.matcher(body);

      boolean hasValueReturn = matcher.find();
      matcher.reset();

      StringBuffer modified = new StringBuffer();

      while (matcher.find()) {
         String expression = matcher.group(1);

         String replacement =
                 "return RecursionTrace.returning(\"" +
                         methodName + "\", " + expression + ");";

         matcher.appendReplacement(
                 modified,
                 Matcher.quoteReplacement(replacement)
         );
      }

      matcher.appendTail(modified);

      // Handle return statements without a value.
      Pattern voidReturn = Pattern.compile("\\breturn\\s*;");
      Matcher voidMatcher = voidReturn.matcher(modified.toString());

      StringBuffer finalBody = new StringBuffer();

      while (voidMatcher.find()) {
         String replacement =
                 "RecursionTrace.returningVoid(\"" +
                         methodName + "\"); return;";

         voidMatcher.appendReplacement(
                 finalBody,
                 Matcher.quoteReplacement(replacement)
         );
      }

      voidMatcher.appendTail(finalBody);

      // Handle void methods that reach the end normally.
      if (!hasValueReturn) {
         finalBody.append(
                 "\nRecursionTrace.returningVoid(\"" + methodName + "\");\n"
         );
      }

      return finalBody.toString();
   }

   // Stores the source positions and replacement text for each method edit.
   private static class MethodEdit {
      int start;
      int end;
      String replacement;

      MethodEdit(int start, int end, String replacement) {
         this.start = start;
         this.end = end;
         this.replacement = replacement;
      }
   }

   // Generates the RecursionTrace.java source file.
   private static String getTraceSource() {
      return """
        import java.util.*;

        public class RecursionTrace {
            private static final int MAX_DEPTH = 5;
            private static Deque<String> callStack = new ArrayDeque<>();
            private static Scanner input = new Scanner(System.in);
            private static boolean automatic = false;

            public static void enter(String method) {
                callStack.push(method);

                if (callStack.size() > MAX_DEPTH) {
                    if (callStack.size() == MAX_DEPTH + 1) {
                        System.out.println("[Max depth reached]");
                    }
                    return;
                }

                System.out.println("\\nCALL: " + method);
                displayStack();
                pause();
            }

            public static <T> T returning(String method, T value) {
                if (callStack.size() <= MAX_DEPTH) {
                    System.out.println("\\nRETURN: " + method);
                    System.out.println("VALUE: " + value);
                    displayStack();
                    pause();
                }

                if (!callStack.isEmpty()) {
                    callStack.pop();
                }

                return value;
            }

            public static void returningVoid(String method) {
                if (callStack.size() <= MAX_DEPTH) {
                    System.out.println("\\nRETURN: " + method);
                    displayStack();
                    pause();
                }

                if (!callStack.isEmpty()) {
                    callStack.pop();
                }
            }

            private static void displayStack() {
                System.out.println("CALL STACK:");

                int level = 1;

                for (String call : callStack) {
                    System.out.println("  " + level++ + ". " + call + "()");
                }
            }

            private static void pause() {
                if (automatic) return;

                System.out.print("[ENTER] Next Step | [Q] Run All: ");

                if (input.nextLine().equalsIgnoreCase("q")) {
                    automatic = true;
                    System.out.println("\\n[Running automatically]");
                }
            }
        }
        """;
   }
   // Advance the program by one recursion step.
   public static void sendNextStep() {
      try {
         if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.getOutputStream().write("\n".getBytes());
            currentProcess.getOutputStream().flush();
         }
      } catch (Exception e) {
         System.out.println("Error sending next step: " + e.getMessage());
      }
   }

   // Tell the program to continue automatically.
   public static void continueAutomatically() {
      try {
         if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.getOutputStream().write("q\n".getBytes());
            currentProcess.getOutputStream().flush();
         }
      } catch (Exception e) {
         System.out.println("Error continuing program: " + e.getMessage());
      }
   }
}
