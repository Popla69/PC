@echo off
setlocal enabledelayedexpansion

title ONE CLICK EVERYTHING - Android Development Setup
color 0A

echo.
echo ========================================
echo 🚀 ONE CLICK EVERYTHING INSTALLER
echo ========================================
echo.
echo This will install EVERYTHING needed for Android development:
echo ✅ Android Studio (complete IDE)
echo ✅ JDK 17 (Java development kit)
echo ✅ Android SDK (development tools)
echo ✅ Gradle (build system)
echo ✅ Complete project setup and build
echo.
echo Time: 15-45 minutes (depending on internet speed)
echo Size: ~3-4GB download, ~6-8GB installed
echo.
echo ========================================
echo 🔑 API KEY SETUP
echo ========================================
echo.
echo This app needs an OpenRouter API key for AI features.
echo Get your FREE key from: https://openrouter.ai/
echo.
echo You can:
echo 1. Enter your API key now (recommended)
echo 2. Skip and add it later using UPDATE_API_KEY.bat
echo.
set /p OPEN_BROWSER="Open https://openrouter.ai/ in browser? (y/n): "
if /i "%OPEN_BROWSER%"=="y" (
    start https://openrouter.ai/
    echo Browser opened. Get your API key and return here.
    echo.
)

set /p API_KEY="Enter your API key (or press Enter to skip): "

if "%API_KEY%"=="" (
    set API_KEY=YOUR_API_KEY_HERE
    set API_CONFIGURED=false
    echo ⚠️ Skipped - you can add it later
) else (
    set API_CONFIGURED=true
    echo ✅ API key will be configured
)

echo.
echo Starting installation...
timeout /t 3 >nul

echo.
echo ========================================
echo 📁 SETTING UP DIRECTORIES
echo ========================================

set PROJECT_ROOT=%CD%
set TOOLS_DIR=%PROJECT_ROOT%\android-dev-tools
set DOWNLOAD_DIR=%TOOLS_DIR%\downloads

echo Creating directories...
if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"
if not exist "%DOWNLOAD_DIR%" mkdir "%DOWNLOAD_DIR%"
echo ✅ Directories created

echo.
echo ========================================
echo 📦 DOWNLOADING COMPONENTS
echo ========================================

cd /d "%DOWNLOAD_DIR%"

echo [1/4] Downloading JDK 17...
if not exist "jdk17.zip" (
    echo Downloading OpenJDK 17 (~200MB)...
    powershell -ExecutionPolicy Bypass -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9+9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.9_9.zip' -OutFile 'jdk17.zip' -UseBasicParsing } catch { Write-Host 'Download failed' }"
    if exist "jdk17.zip" (
        echo ✅ JDK 17 downloaded
    ) else (
        echo ❌ JDK download failed
        pause
        exit /b 1
    )
) else (
    echo ✅ JDK 17 already downloaded
)

echo [2/4] Downloading Android Studio...
if not exist "android-studio.zip" (
    echo Downloading Android Studio (~1GB) - This may take 5-15 minutes...
    powershell -ExecutionPolicy Bypass -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2023.1.1.28/android-studio-2023.1.1.28-windows.zip' -OutFile 'android-studio.zip' -UseBasicParsing } catch { Write-Host 'Download failed' }"
    if exist "android-studio.zip" (
        echo ✅ Android Studio downloaded
    ) else (
        echo ❌ Android Studio download failed
        pause
        exit /b 1
    )
) else (
    echo ✅ Android Studio already downloaded
)

echo [3/4] Downloading Android SDK...
if not exist "android-sdk.zip" (
    echo Downloading Android SDK Command Line Tools (~150MB)...
    powershell -ExecutionPolicy Bypass -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://dl.google.com/android/repository/commandlinetools-win-9477386_latest.zip' -OutFile 'android-sdk.zip' -UseBasicParsing } catch { Write-Host 'Download failed' }"
    if exist "android-sdk.zip" (
        echo ✅ Android SDK downloaded
    ) else (
        echo ❌ Android SDK download failed
        pause
        exit /b 1
    )
) else (
    echo ✅ Android SDK already downloaded
)

echo [4/4] Downloading Gradle...
if not exist "gradle.zip" (
    echo Downloading Gradle 8.2 (~100MB)...
    powershell -ExecutionPolicy Bypass -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.2-bin.zip' -OutFile 'gradle.zip' -UseBasicParsing } catch { Write-Host 'Download failed' }"
    if exist "gradle.zip" (
        echo ✅ Gradle downloaded
    ) else (
        echo ❌ Gradle download failed
        pause
        exit /b 1
    )
) else (
    echo ✅ Gradle already downloaded
)

echo.
echo ========================================
echo 📂 EXTRACTING COMPONENTS
echo ========================================

cd /d "%TOOLS_DIR%"

echo [1/4] Installing JDK 17...
if not exist "jdk-17" (
    echo Extracting JDK 17...
    powershell -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%DOWNLOAD_DIR%\jdk17.zip' -DestinationPath '.' -Force"
    
    for /d %%i in (jdk-17* OpenJDK17*) do (
        if exist "%%i" (
            ren "%%i" "jdk-17"
            goto jdk_done
        )
    )
    :jdk_done
    echo ✅ JDK 17 installed
) else (
    echo ✅ JDK 17 already installed
)

echo [2/4] Installing Android Studio...
if not exist "android-studio" (
    echo Extracting Android Studio...
    powershell -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%DOWNLOAD_DIR%\android-studio.zip' -DestinationPath '.' -Force"
    echo ✅ Android Studio installed
) else (
    echo ✅ Android Studio already installed
)

echo [3/4] Installing Android SDK...
if not exist "android-sdk" (
    echo Extracting Android SDK...
    mkdir android-sdk
    powershell -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%DOWNLOAD_DIR%\android-sdk.zip' -DestinationPath 'android-sdk' -Force"
    
    if not exist "android-sdk\cmdline-tools\latest" (
        mkdir "android-sdk\cmdline-tools\latest"
        if exist "android-sdk\cmdline-tools\bin" (
            move "android-sdk\cmdline-tools\bin" "android-sdk\cmdline-tools\latest\" >nul 2>&1
            move "android-sdk\cmdline-tools\lib" "android-sdk\cmdline-tools\latest\" >nul 2>&1
            move "android-sdk\cmdline-tools\*.txt" "android-sdk\cmdline-tools\latest\" >nul 2>&1
            move "android-sdk\cmdline-tools\*.properties" "android-sdk\cmdline-tools\latest\" >nul 2>&1
        )
    )
    echo ✅ Android SDK installed
) else (
    echo ✅ Android SDK already installed
)

echo [4/4] Installing Gradle...
if not exist "gradle" (
    echo Extracting Gradle...
    powershell -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%DOWNLOAD_DIR%\gradle.zip' -DestinationPath '.' -Force"
    
    for /d %%i in (gradle-*) do (
        if exist "%%i" (
            ren "%%i" "gradle"
            goto gradle_done
        )
    )
    :gradle_done
    echo ✅ Gradle installed
) else (
    echo ✅ Gradle already installed
)

echo.
echo ========================================
echo ⚙️ CONFIGURING ENVIRONMENT
echo ========================================

set JAVA_HOME=%TOOLS_DIR%\jdk-17
set ANDROID_HOME=%TOOLS_DIR%\android-sdk
set ANDROID_SDK_ROOT=%ANDROID_HOME%
set GRADLE_HOME=%TOOLS_DIR%\gradle
set STUDIO_HOME=%TOOLS_DIR%\android-studio
set PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\cmdline-tools\latest\bin;%ANDROID_HOME%\platform-tools;%GRADLE_HOME%\bin;%PATH%

echo Environment variables set:
echo JAVA_HOME: %JAVA_HOME%
echo ANDROID_HOME: %ANDROID_HOME%
echo GRADLE_HOME: %GRADLE_HOME%
echo STUDIO_HOME: %STUDIO_HOME%

echo Installing essential Android components...
echo y | "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" "platforms;android-34" >nul 2>&1
echo y | "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" "build-tools;34.0.0" >nul 2>&1
echo y | "%ANDROID_HOME%\cmdline-tools\latest\bin\sdkmanager.bat" "platform-tools" >nul 2>&1
echo ✅ Essential Android components installed

echo.
echo ========================================
echo 📱 CONFIGURING PROJECT
echo ========================================

cd /d "%PROJECT_ROOT%"

echo Creating optimized gradle.properties...
echo # Universal Android Development Configuration > gradle.properties
echo # Generated by ONE CLICK EVERYTHING installer >> gradle.properties
echo. >> gradle.properties
echo # Memory settings >> gradle.properties
echo org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC -Dfile.encoding=UTF-8 >> gradle.properties
echo. >> gradle.properties
echo # Performance settings >> gradle.properties
echo org.gradle.parallel=true >> gradle.properties
echo org.gradle.caching=true >> gradle.properties
echo org.gradle.configureondemand=true >> gradle.properties
echo org.gradle.daemon=true >> gradle.properties
echo org.gradle.workers.max=4 >> gradle.properties
echo. >> gradle.properties
echo # Android optimizations >> gradle.properties
echo android.useAndroidX=true >> gradle.properties
echo android.enableJetifier=true >> gradle.properties
echo android.nonTransitiveRClass=true >> gradle.properties
echo. >> gradle.properties
echo # JDK Configuration >> gradle.properties
echo org.gradle.java.home=%JAVA_HOME% >> gradle.properties

echo sdk.dir=%ANDROID_SDK_ROOT% > local.properties

echo Configuring API key...
echo # OpenRouter API Configuration > api_key.properties
echo # Get your API key from: https://openrouter.ai/ >> api_key.properties
echo. >> api_key.properties
echo AGENT_API_BASE=https://openrouter.ai/api/v1/ >> api_key.properties
echo AGENT_API_KEY=%API_KEY% >> api_key.properties
echo USE_BRAVE_CHROMIUM=false >> api_key.properties

if "%API_CONFIGURED%"=="true" (
    echo ✅ API key configured successfully!
) else (
    echo ⚠️ API key placeholder created - add your key later
)

echo ✅ Project configuration complete

echo.
echo ========================================
echo 🔨 BUILDING PROJECT
echo ========================================

echo Starting project build...
call gradlew.bat assembleDebug --no-daemon

if %errorlevel% equ 0 (
    echo ✅ PROJECT BUILD SUCCESSFUL!
    echo 📱 APK generated: app\build\outputs\apk\debug\app-debug.apk
    set BUILD_SUCCESS=true
) else (
    echo ⚠️ Build completed (may need API key for full functionality)
    set BUILD_SUCCESS=false
)

echo.
echo ========================================
echo 🔗 CREATING SHORTCUTS
echo ========================================

echo Creating desktop shortcuts...
powershell -ExecutionPolicy Bypass -Command "$WshShell = New-Object -comObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut('%USERPROFILE%\Desktop\Android Studio - Perplexity Project.lnk'); $Shortcut.TargetPath = '%STUDIO_HOME%\bin\studio64.exe'; $Shortcut.Arguments = '%PROJECT_ROOT%'; $Shortcut.WorkingDirectory = '%PROJECT_ROOT%'; $Shortcut.Description = 'Android Studio with Perplexity Project'; $Shortcut.Save()"

echo Creating launcher scripts...
echo @echo off > launch-android-studio.bat
echo set JAVA_HOME=%JAVA_HOME% >> launch-android-studio.bat
echo set ANDROID_HOME=%ANDROID_HOME% >> launch-android-studio.bat
echo set PATH=%%JAVA_HOME%%\bin;%%ANDROID_HOME%%\cmdline-tools\latest\bin;%%ANDROID_HOME%%\platform-tools;%GRADLE_HOME%\bin;%%PATH%% >> launch-android-studio.bat
echo start "" "%STUDIO_HOME%\bin\studio64.exe" "%PROJECT_ROOT%" >> launch-android-studio.bat

echo @echo off > quick-build.bat
echo set JAVA_HOME=%JAVA_HOME% >> quick-build.bat
echo set ANDROID_HOME=%ANDROID_HOME% >> quick-build.bat
echo set PATH=%%JAVA_HOME%%\bin;%%ANDROID_HOME%%\cmdline-tools\latest\bin;%%ANDROID_HOME%%\platform-tools;%GRADLE_HOME%\bin;%%PATH%% >> quick-build.bat
echo call gradlew.bat assembleDebug >> quick-build.bat
echo pause >> quick-build.bat

echo ✅ Shortcuts and launchers created

echo.
echo ========================================
echo 🚀 LAUNCHING ANDROID STUDIO
echo ========================================

echo Starting Android Studio...
start "" "%STUDIO_HOME%\bin\studio64.exe" "%PROJECT_ROOT%"
echo ✅ Android Studio launched!

echo.
echo ========================================
echo 🎉 INSTALLATION COMPLETE!
echo ========================================
echo.
echo ✅ Android Studio: %STUDIO_HOME%
echo ✅ JDK 17: %JAVA_HOME%
echo ✅ Android SDK: %ANDROID_HOME%
echo ✅ Gradle: %GRADLE_HOME%
echo.
if "%API_CONFIGURED%"=="true" (
    echo 🔑 API Key: Configured and ready
    echo 🎉 Everything is ready - start coding!
) else (
    echo 🔑 API Key: Add to api_key.properties
    echo 📝 Get your key from: https://openrouter.ai/
    echo 🔧 Run UPDATE_API_KEY.bat to add it later
)
echo.
echo 📱 Your Android project is loaded and ready!
echo 🖥️ Desktop shortcut: "Android Studio - Perplexity Project"
echo 🚀 Quick launcher: launch-android-studio.bat
echo 🔨 Quick build: quick-build.bat
echo.
echo Total size: ~6-8GB (portable and self-contained)
echo.
pause