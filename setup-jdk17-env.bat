@echo off
echo 🚀 Setting up JDK 17 Virtual Environment for Android Development
echo.

:: Create virtual environment directory
if not exist "jdk-env" mkdir jdk-env
cd jdk-env

:: Check if JDK 17 is already downloaded
if exist "jdk-17" (
    echo ✅ JDK 17 already exists in virtual environment
    goto :setup_env
)

echo 📥 Downloading JDK 17 for virtual environment...
echo.
echo Please download JDK 17 from one of these sources:
echo.
echo 1. Eclipse Temurin JDK 17: https://adoptium.net/temurin/releases/?version=17
echo 2. Oracle JDK 17: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
echo 3. Microsoft OpenJDK 17: https://docs.microsoft.com/en-us/java/openjdk/download#openjdk-17
echo.
echo Extract the downloaded JDK to: %CD%\jdk-17
echo Then press any key to continue...
pause

:setup_env
:: Set virtual environment variables
set JAVA_HOME=%CD%\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo ✅ Virtual JDK 17 Environment Setup Complete!
echo.
echo JAVA_HOME: %JAVA_HOME%
echo.

:: Test Java version
echo Testing Java version...
java -version

echo.
echo 🎯 Virtual environment ready for Android development with Hilt + Room!
echo.
echo To use this environment:
echo 1. Run this script: setup-jdk17-env.bat
echo 2. Then run: build-with-hilt-room.bat
echo.
pause