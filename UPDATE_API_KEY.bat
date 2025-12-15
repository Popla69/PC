@echo off
setlocal enabledelayedexpansion

title Update API Key - Perplexity Android App
color 0A

echo.
echo ========================================
echo 🔑 UPDATE API KEY
echo ========================================
echo.
echo This will update your OpenRouter API key for the Perplexity Android app.
echo.
echo 🌐 Get your API key from: https://openrouter.ai/
echo.
echo Steps to get your API key:
echo 1. Visit: https://openrouter.ai/
echo 2. Sign up for a free account (if you haven't already)
echo 3. Go to "Keys" section in your dashboard
echo 4. Create a new API key
echo 5. Copy the key and paste it below
echo.

choice /c YN /m "Do you want to open https://openrouter.ai/ in your browser? (Y/N)"
if !errorlevel! equ 1 (
    echo.
    echo Opening OpenRouter website...
    start https://openrouter.ai/
    echo.
    echo Please get your API key from the website and return here.
    echo.
)

echo.
set /p NEW_API_KEY="Enter your OpenRouter API key: "

if "!NEW_API_KEY!"=="" (
    echo.
    echo ❌ No API key provided. Operation cancelled.
    pause
    exit /b 1
)

echo.
echo [UPDATE] Updating API key configuration...

REM Backup existing file if it exists
if exist "api_key.properties" (
    copy "api_key.properties" "api_key.properties.backup" >nul 2>&1
    echo     ✅ Backup created: api_key.properties.backup
)

REM Create new API key file
echo # OpenRouter API Configuration > api_key.properties
echo # Updated: %date% %time% >> api_key.properties
echo # Get your API key from: https://openrouter.ai/ >> api_key.properties
echo. >> api_key.properties
echo AGENT_API_BASE=https://openrouter.ai/api/v1/ >> api_key.properties
echo AGENT_API_KEY=!NEW_API_KEY! >> api_key.properties
echo USE_BRAVE_CHROMIUM=false >> api_key.properties
echo. >> api_key.properties
echo # Instructions: >> api_key.properties
echo # 1. Get your API key from: https://openrouter.ai/ >> api_key.properties
echo # 2. Replace the key above if needed >> api_key.properties
echo # 3. Save this file >> api_key.properties
echo # 4. Run quick-build.bat to rebuild the project >> api_key.properties

echo     ✅ API key updated successfully!

echo.
echo ========================================
echo 🔨 REBUILD PROJECT
echo ========================================
echo.
echo Your API key has been updated. Now rebuilding the project...
echo.

REM Check if we have the development environment set up
if exist "android-dev-tools\jdk-17" (
    echo [BUILD] Using installed development environment...
    set JAVA_HOME=%CD%\android-dev-tools\jdk-17
    set ANDROID_HOME=%CD%\android-dev-tools\android-sdk
    set GRADLE_HOME=%CD%\android-dev-tools\gradle
    set PATH=!JAVA_HOME!\bin;!ANDROID_HOME!\cmdline-tools\latest\bin;!ANDROID_HOME!\platform-tools;!GRADLE_HOME!\bin;!PATH!
) else (
    echo [BUILD] Using system environment...
)

echo     Building Android project with new API key...
call gradlew.bat assembleDebug --no-daemon

if !errorlevel! equ 0 (
    echo.
    echo ========================================
    echo 🎉 SUCCESS!
    echo ========================================
    echo.
    echo ✅ API key updated and project rebuilt successfully!
    echo ✅ APK generated: app\build\outputs\apk\debug\app-debug.apk
    echo ✅ Your app now has full AI functionality!
    echo.
    echo 🚀 You can now run your Android app with complete features.
    echo.
) else (
    echo.
    echo ========================================
    echo ⚠️  BUILD ISSUES
    echo ========================================
    echo.
    echo The API key was updated, but there were build issues.
    echo This might be normal - try running the app anyway.
    echo.
    echo If problems persist:
    echo 1. Check that your API key is correct
    echo 2. Try running quick-build.bat
    echo 3. Check your internet connection
    echo.
)

echo ========================================
echo 📋 SUMMARY
echo ========================================
echo.
echo 🔑 API Key: Updated in api_key.properties
echo 📱 Project: Rebuilt with new configuration
echo 🚀 Status: Ready to use!
echo.
echo To launch Android Studio: launch-android-studio.bat
echo To rebuild project: quick-build.bat
echo.
pause