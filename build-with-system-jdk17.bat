@echo off
echo 🚀 Building with System JDK 17 + Full Hilt + Room
echo.

:: Auto-detect and setup system JDK 17
call setup-system-jdk17.bat

if %ERRORLEVEL% NEQ 0 (
    echo ❌ JDK 17 setup failed!
    pause
    exit /b 1
)

echo.
echo 🔨 Building Mobile Perplexity Comet...
echo ⚡ Full Hilt + Room + KAPT enabled
echo.

:: Clean and build
call gradlew.bat clean assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ BUILD SUCCESSFUL! 🎉
    echo.
    echo 📦 APK: app\build\outputs\apk\debug\app-debug.apk
    echo 📱 Install: adb install app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ❌ BUILD FAILED!
    echo Run: gradlew.bat --info assembleDebug for details
)

echo.
pause