@echo off
echo Compiling RecursiveExplainer.java...
javac RecursionExplanator.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed.
    pause
    exit /b
)

echo.
echo Compilation successful.
echo Running RecursiveExplainer...
echo.

java RecursionExplanator

pause