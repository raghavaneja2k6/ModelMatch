@echo off
setlocal
cd /d "%~dp0"
echo =========================================================================
echo                   Starting ModelMatch Server (Java 21 LTS)
echo           ChatGPT Replica UI + TF-IDF Classifier + Gemini 3.5 Flash
echo =========================================================================

if not exist "target\classes" mkdir target\classes

echo [1/2] Compiling Java source files...
javac -cp "lib/gson-2.11.0.jar" -d "target/classes" src/main/java/com/modelmatch/model/*.java src/main/java/com/modelmatch/classifier/*.java src/main/java/com/modelmatch/repository/*.java src/main/java/com/modelmatch/engine/*.java src/main/java/com/modelmatch/service/*.java src/main/java/com/modelmatch/controller/*.java src/main/java/com/modelmatch/server/*.java src/main/java/com/modelmatch/Main.java

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed. Please ensure JDK 21+ is in your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching ModelMatch on http://localhost:8080 ...
java -cp "target/classes;lib/gson-2.11.0.jar" com.modelmatch.Main 8080

pause
