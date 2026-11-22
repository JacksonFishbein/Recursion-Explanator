
#!/bin/bash

cd "$(dirname "$0")"

javac RecursionGUI.java RecursionExplanator.java RunInputProgram.java

if [ $? -ne 0 ]; then
    echo "Compilation failed. Check the errors above."
    read -p "Press Enter to close..."
    exit 1
fi

echo "Starting Recursion Explanator..."

java RecursionGUI
