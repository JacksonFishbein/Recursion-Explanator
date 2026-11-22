
@echo off
title Recursion Explanator

javac RecursionGUI.java RecursionExplanator.java RunInputProgram.java

if %errorlevel% neq 0 (
    pause
    exit /b 1
)

echo Starting Recursion Explanator...

java RecursionGUI

pause
