# Recursion Explanator

A Java-based desktop application for analyzing and visualizing recursive methods. Follow recursive calls, returns, and call stack behavior step by step through an interactive graphical interface.

## Features

### Code Analysis
- Automatically analyzes Java source code.
- Identifies recursive methods.
- Detects recursive calls and base cases.
- Displays information about identified recursive methods.

### Recursion Visualization
- Displays recursive CALL and RETURN events.
- Shows return values as methods complete.
- Visualizes the recursive call stack.
- Tracks the order of recursive execution.
- Supports configurable maximum visualization depth.

### Program Execution
- Compiles and executes user-provided Java programs.
- Captures compiler messages and program output.
- Runs programs directly through the application.
- Uses background execution to keep the GUI responsive.

### Execution Controls
- Step through recursive execution one event at a time.
- Continue execution automatically.
- Follow recursive calls and returns as they happen.

### Graphical Interface
- Java Swing graphical interface.
- Integrated Java code editor.
- Open and save Java source files.
- Clear the code editor.
- Integrated output console.
- Dark Mode and Light Mode.

## Screenshots

Screenshots of the application will be added here.

## Requirements

- Java Development Kit (JDK) installed.
- Java compiler (`javac`) available through the system PATH.
- Java runtime (`java`) available through the system PATH.

## Installation

### Clone the Repository

```bash
git clone https://github.com/JacksonFishbein/Recursion-Explanator.git
```

Navigate into the project directory:

```bash
cd Recursion-Explanator
```

### Launch on Windows

Double-click the following file:

`WINDOWS - Recursion Explanator.bat`

### Launch on macOS

Double-click the following file:

`MAC_OS - Recursion Explanator.command`

If macOS prevents the launcher from running, open Terminal in the project directory and execute:

```bash
chmod +x "MAC_OS - Recursion Explanator.command"
```

Then launch the file again.

The launchers compile the Java source files and start the graphical application.

## Manual Compilation

Compile the application:

```bash
javac RecursionGUI.java RecursionExplanator.java RunInputProgram.java
```

Launch the application:

```bash
java RecursionGUI
```

## Technologies Used

- Java
- Java Swing
- Java Compiler API
- SwingWorker
- Runtime source-code instrumentation
- Git
- GitHub

## Project Goals

Recursion Explanator aims to make recursion easier to understand by showing what happens behind the scenes during recursive execution.

By combining source code analysis, runtime instrumentation, and interactive execution controls, the application helps programmers develop a stronger understanding of recursion and call stack behavior.