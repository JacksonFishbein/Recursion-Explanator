import java.util.*;
import java.util.regex.*;
import java.nio.file.*;

public class RecursionExplanator {
   static Scanner scanner = new Scanner(System.in);
   public static void main(String[] args) {
      System.out.println("=== Recursion Explanator ===");
      System.out.println();
      System.out.println("What Input Would You Like To Use?");
      System.out.println("1. Copy/Paste Java Code");
      System.out.println("2. TXT File");
      System.out.println("3. JAVA File");
      System.out.println();
      System.out.print("Enter Your Choice: ");
      String choice = scanner.nextLine();
   
      String sourceCode = "";
      
      try {
         if (choice.equals("1")) {
            sourceCode = cmdInput();
         }
         else if (choice.equals("2")) {
            sourceCode = fileInput(".txt");
         }
         else if (choice.equals("3")) {
            sourceCode = fileInput(".java");
         }
         else {
            System.out.println("Invalid Choice.");
            scanner.close();
            return;
         }
         
         analyzeCode(sourceCode);
         
      } catch (Exception e) {
         System.out.println("Error reading input: " + e.getMessage());
      }
      
     scanner.close();
   }
      
      // Read CMD Copy/Paste Code
      public static String cmdInput() {
         System.out.println();
         System.out.println("Paste the java code below.");
         System.out.println("Type '113' on a new line when done.");
         System.out.println();
         
         StringBuilder sourceCode = new StringBuilder();
         
         while (true) {
            String line = scanner.nextLine();
            
            if (line.equals("113")) {
               break;
            }
            
            sourceCode.append(line).append("\n");
         }
         
         return sourceCode.toString();
      }  
      
      // Read File Path File
      public static String fileInput(String extension) {
         System.out.println();
         System.out.print("Enter the path to the " + extension + " file: ");
         
         String filePath = scanner.nextLine();
         
         if (!filePath.toLowerCase().endsWith(extension)) {
            System.out.println();
            System.out.println("Please Enter a " + extension + " file path.");
            return "";
         }
         
         try {
            return Files.readString(Paths.get(filePath));
         }
         catch (Exception e) {
            System.out.println("Could not read file.");
            return "";
         }
      }

   public static void analyzeCode(String code) {
   
      String[] lines = code.split("\\R");
   
      for (int i = 0; i < lines.length; i++) {
      
         String line = lines[i];
      
         // Look for method declaration
         Pattern methodPattern = Pattern.compile(
            "(public|private|protected)?\\s*" +
            "(static\\s+)?" +
            "[\\w<>\\[\\]]+\\s+" +
            "(\\w+)\\s*\\([^)]*\\)\\s*\\{?"
         );
      
         Matcher methodMatcher = methodPattern.matcher(line);
      
         if (!methodMatcher.find()) {
            continue;
         }
      
         String methodName = methodMatcher.group(3);
      
         // Find end of the method
         int braceCount = 0;
         boolean started = false;
         int end = i;
      
         for (int j = i; j < lines.length; j++) {
            for (char c : lines[j].toCharArray()) {
               if (c == '{') {
                  braceCount++;
                  started = true;
               }
            
               else if (c == '}') {
                  braceCount--;
               }
            }
         
            if (started && braceCount == 0) {
               end = j;
               break;
            }
         }
      
         // Search this method to see if calls itself
         List<String> recursiveCalls = new ArrayList<>();
         for (int j = i + 1; j <= end; j++) {
            Pattern callPattern = Pattern.compile(
               "\\b" + Pattern.quote(methodName) + "\\s*\\([^;]*?\\)"
            );
         
            Matcher callMatcher = callPattern.matcher(lines[j]);
         
            while (callMatcher.find()) {
               recursiveCalls.add(callMatcher.group());
            }
         }
      
         // If no self calls = method not recursive
         if (recursiveCalls.isEmpty()) {
            continue;
         }
      
         System.out.println("----------------------------------------");
         System.out.println("Recursive Method: " + methodName);
         System.out.println();
      
         // Find if statements for base case search
         System.out.println("Base Case:");
      
         boolean found = false;
         for (int j = i +1; j <= end; j++) {
            String trimmed = lines[j].trim();
            if (trimmed.startsWith("if")) {
               found = true;
               System.out.println(lines[j]);
            
               // If open brace on this line, print all following until closing brace
               if (trimmed.contains("{")) {
                  int nestedBraces = 0;
               
                  for (int k = j; k <= end; k++) {
                     for (char c : lines[k].toCharArray()) {
                        if (c == '{') {
                           nestedBraces++;
                        }
                        else if (c == '}') {
                           nestedBraces--;
                        }
                     }
               
                     if (k != j) {
                        System.out.println(lines[k]);
                     }
               
                     if (nestedBraces == 0) {
                        break;
                     }
                  }
               
                  break;
               }
            }
         }
      
         if (!found) {
            System.out.println("No if statement found.");
         }
      
         System.out.println();
         System.out.println("Recursive Calls:");
         for (String call : recursiveCalls) {
            System.out.println(call);
         }
      
         System.out.println();
      }
   }
}