@echo off
setlocal
cd /d "%~dp0"
if exist "%~dp0apache-maven-3.9.9\bin\mvn.cmd" (
    "%~dp0apache-maven-3.9.9\bin\mvn.cmd" %*
) else (
    mvn %*
)
