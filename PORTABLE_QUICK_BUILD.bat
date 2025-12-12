@echo off
REM ========================================
REM PORTABLE QUICK BUILD
REM ========================================
REM Use this after running ULTIMATE_ONE_CLICK_SETUP.bat
REM for fast subsequent builds

echo ========================================
echo 🚀 PORTABLE QUICK BUILD
echo ========================================
echo.

REM Check if environment exists
if not exist "portable-android-env" (
    echo ❌ Environment not found!
    echo Please run ULTIMATE_ONE_CLICK_SETUP.bat first
    pause
    exit /b 1
)

REM Activate environment
echo [1/3] Activating portable environment...
call activate-env.bat

REM Quick system check
echo [2/3] Quick system check...
for /f "tokens=2 delims==" %%i in ('wmic computersystem get TotalPhysicalMemory /value ^| find "="') do set RAM_BYTES=%%i
set /a RAM_GB=!RAM_BYTES!/1073741824
echo     System RAM: !RAM_GB!GB

REM Build
echo [3/3] Building project...
echo     Using optimized settings for this system
echo.

call gradlew assembleDebug --daemon --parallel --build-cache

if !errorlevel! equ 0 (
    echo.
    echo ✅ BUILD SUCCESSFUL!
    echo 📱 APK: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ❌ BUILD FAILED - Check errors above
)

echo.
pause