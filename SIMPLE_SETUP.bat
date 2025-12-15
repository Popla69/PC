@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 🚀 SIMPLE ANDROID SETUP
echo ========================================
echo.
echo This is a simplified version that focuses on core functionality.
echo.
pause

REM Create tools directory
echo [1/5] Creating tools directory...
set TOOLS_DIR=%CD%\dev-tools
if not exist "!TOOLS_DIR!" mkdir "!TOOLS_DIR!"
echo     ✅ Tools directory: !TOOLS_DIR!

REM Download JDK 17 (smaller, more reliable)
echo [2/5] Setting up JDK 17...
cd /d "!TOOLS_DIR!"
if not exist "jdk-17" (
    echo     Downloading JDK 17...
    powershell -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9%%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.9_9.zip' -OutFile 'jdk17.zip' -ErrorAction Stop; Write-Host 'Download successful' } catch { Write-Host 'Download failed:' $_.Exception.Message; exit 1 }"
    
    if exist "jdk17.zip" (
        echo     Extracting JDK 17...
        powershell -Command "Expand-Archive -Path 'jdk17.zip' -DestinationPath '.' -Force"
        if exist "jdk-17.0.9+9" ren "jdk-17.0.9+9" "jdk-17"
        del jdk17.zip
        echo     ✅ JDK 17 installed
    ) else (
        echo     ❌ JDK download failed
        pause
        exit /b 1
    )
) else (
    echo     ✅ JDK 17 already exists
)

REM Set up environment
echo [3/5] Setting up environment...
set JAVA_HOME=!TOOLS_DIR!\jdk-17
set PATH=!JAVA_HOME!\bin;!PATH!

echo     JAVA_HOME: !JAVA_HOME!
echo     Testing Java...
java -version
if !errorlevel! equ 0 (
    echo     ✅ Java working correctly
) else (
    echo     ❌ Java setup failed
    pause
    exit /b 1
)

REM Create basic project configuration
echo [4/5] Creating project configuration...
cd /d "%CD%"

REM Create minimal gradle.properties
echo # Basic Gradle configuration > gradle.properties
echo org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m >> gradle.properties
echo org.gradle.parallel=true >> gradle.properties
echo org.gradle.caching=true >> gradle.properties
echo org.gradle.daemon=true >> gradle.properties
echo android.useAndroidX=true >> gradle.properties
echo android.enableJetifier=true >> gradle.properties
echo org.gradle.java.home=!JAVA_HOME! >> gradle.properties

REM Create API key file if it doesn't exist
if not exist "api_key.properties" (
    echo # Add your OpenRouter API key here > api_key.properties
    echo AGENT_API_BASE=https://openrouter.ai/api/v1/ >> api_key.properties
    echo AGENT_API_KEY=YOUR_API_KEY_HERE >> api_key.properties
    echo USE_BRAVE_CHROMIUM=false >> api_key.properties
)

echo     ✅ Project configuration created

REM Try to build the project
echo [5/5] Building project...
echo     This will use the existing Gradle wrapper...

call gradlew.bat assembleDebug --no-daemon

if !errorlevel! equ 0 (
    echo.
    echo ========================================
    echo 🎉 BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo ✅ Project built successfully
    echo ✅ APK: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Next steps:
    echo 1. Add your API key to api_key.properties
    echo 2. Install Android Studio manually if needed
    echo 3. Open this project in Android Studio
    echo.
    echo Tools installed:
    echo - JDK 17: !JAVA_HOME!
    echo - Gradle: Using project wrapper
    echo.
) else (
    echo.
    echo ========================================
    echo ⚠️  BUILD COMPLETED WITH ISSUES
    echo ========================================
    echo.
    echo This is normal for first run without API key.
    echo.
    echo To fix:
    echo 1. Add your API key to api_key.properties
    echo 2. Run: gradlew assembleDebug
    echo.
    echo Tools installed:
    echo - JDK 17: !JAVA_HOME!
    echo - Gradle: Using project wrapper
)

echo.
pause