#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

echo "========================================================================="
echo "                  Starting ModelMatch Server (Java 21 LTS)               "
echo "========================================================================="

mkdir -p target/classes
javac -cp "lib/gson-2.11.0.jar" -d "target/classes" $(find src/main/java -name "*.java")
java -cp "target/classes:lib/gson-2.11.0.jar" com.modelmatch.Main 8080
