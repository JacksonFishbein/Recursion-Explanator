# \# Recursion Explanator

# 

# \*\*A Java recursion analysis and visualization tool that helps programmers understand how recursive methods execute, step by step.\*\*

# 

# Recursion Explanator is a Java-based desktop application designed to make recursion easier to understand. It analyzes Java source code, identifies recursive methods, and visualizes recursive calls, returns, and the call stack as a program executes.

# 

# Instead of simply displaying the final output, Recursion Explanator allows users to follow the execution process and see how recursive methods work internally.

# 

# \---

# 

# \## Features

# 

# \### Recursive Code Analysis

# \- Automatically analyze Java source code.

# \- Identify recursive methods.

# \- Detect recursive calls and base cases.

# \- Display information about identified recursive methods.

# 

# \### Recursion Visualization

# \- Display recursive `CALL` and `RETURN` events.

# \- Show return values as methods complete.

# \- Visualize the recursive call stack.

# \- Track the order in which recursive methods execute.

# \- Configure the maximum recursion visualization depth.

# 

# \### Program Execution

# \- Compile and execute user-provided Java programs.

# \- Capture compiler messages and program output.

# \- Execute programs directly through the application.

# \- Use background execution to prevent the GUI from freezing.

# 

# \### Execution Controls

# \- Step through recursive execution one event at a time.

# \- Automatically continue execution.

# \- Follow recursive calls and returns as they happen.

# 

# \### Desktop GUI

# \- Java Swing graphical interface.

# \- Integrated Java code editor.

# \- Open and save Java source files.

# \- Clear the code editor.

# \- Integrated output console.

# \- Dark Mode and Light Mode.

# \- Cross-platform launcher support for Windows and macOS.

# 

# \---

# 

# \## Screenshots

# 

# Screenshots of the application will be added here.

# 

# \---

# 

# \## Requirements

# 

# Before running Recursion Explanator, make sure you have:

# 

# \- Java Development Kit (JDK) installed.

# \- Java compiler (`javac`) available through your system PATH.

# \- Java runtime (`java`) available through your system PATH.

# 

# The application uses Java Swing for its graphical interface.

# 

# \---

# 

# \## Installation

# 

# \### 1. Clone the Repository

# 

# ```bash

# git clone https://github.com/JacksonFishbein/Recursion-Explanator.git

# ```

# 

# Navigate into the project directory:

# 

# ```bash

# cd Recursion-Explanator

# ```

# 

# \### 2. Launch the Application

# 

# \*\*Windows\*\*

# 

# Double-click:

# 

# `WINDOWS - Recursion Explanator.bat`

# 

# \*\*macOS\*\*

# 

# Double-click:

# 

# `MAC\_OS - Recursion Explanator.command`

# 

# If macOS does not allow the launcher to run, open Terminal in the project directory and execute:

# 

# ```bash

# chmod +x "MAC\_OS - Recursion Explanator.command"

# ```

# 

# Then double-click the launcher again.

# 

# The launchers compile the Java source files and start the graphical application.

# 

# \---

# 

# \## Manual Compilation

# 

# You can also compile and launch the application manually.

# 

# Compile:

# 

# ```bash

# javac RecursionGUI.java RecursionExplanator.java RunInputProgram.java

# ```

# 

# Launch:

# 

# ```bash

# java RecursionGUI

# ```

# 

# \---

# 

# \## Technologies Used

# 

# \- \*\*Java\*\* – Core application and program execution.

# \- \*\*Java Swing\*\* – Graphical user interface.

# \- \*\*Java Compiler API\*\* – Compilation of submitted Java programs.

# \- \*\*SwingWorker\*\* – Background execution and GUI responsiveness.

# \- \*\*Java Runtime Instrumentation\*\* – Tracking recursive calls and returns.

# \- \*\*Git \& GitHub\*\* – Version control and project development.

# 

# \---

# 

# \## Project Goals

# 

# The goal of Recursion Explanator is to make recursion more approachable by providing a visual representation of what happens behind the scenes during recursive execution.

# 

# By combining source code analysis, runtime instrumentation, and interactive execution controls, the application helps programmers develop a stronger understanding of recursion and call stack behavior.

