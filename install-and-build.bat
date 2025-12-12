@echo off
echo 🚀 Complete Setup: Install JDK 17 + Build Mobile Perplexity Comet
echo ⚡ Full Hilt + Room + KAPT Support
echo.

:: Step 1: Install JDK 17 in project directory
echo 📥 Step 1: Installing JDK 17...
call install-jdk17-here.bat

if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 17 installation failed!
    pause
    exit /b 1
)

echo.
echo 🔨 Step 2: Building with Full Dependencies...

:: Step 2: Build with Hilt + Room
call build-with-hilt-room.bat

if %ERRORLEVEL% EQU 0 (
    echo.
    echo 🎉 SUCCESS! Mobile Perplexity Comet is ready!
    echo.
    echo 📱 APK Location: app\build\outputs\apk\debug\app-debug.apk
    echo 🔧 Install command: adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo ✅ Features Enabled:
    echo    • Hilt Dependency Injection
    echo    • Room Database Persistence  
    echo    • AI Processing Pipeline
    echo    • Multi-mode Search
    echo    • File Analysis
    echo    • Citation Parsing
    echo    • LaTeX Rendering
    echo    • Offline Support
) else (
    echo.
    echo ❌ Build failed! Check error messages above.
)

echo.
pause