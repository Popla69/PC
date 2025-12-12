@echo off
echo ========================================
echo DIAGNOSTIC BUILD SCRIPT
echo ========================================
echo.
echo This script provides detailed build information for troubleshooting
echo.

REM Stop any existing Gradle processes
echo [1/4] Stopping Gradle daemon...
call gradlew --stop
timeout /t 2 /nobreak >nul

REM Show system information
echo [2/4] System Information:
echo Memory: 8GB allocated to Gradle
echo Parallel: Enabled
echo Cache: Enabled
echo KAPT: Worker API enabled
echo.

REM Build with detailed logging
echo [3/4] Building with detailed diagnostics...
call gradlew assembleDebug --daemon --parallel --build-cache --info --stacktrace

REM Show final status
echo.
echo [4/4] Diagnostic build completed
echo.
if %ERRORLEVEL% EQU 0 (
    echo ✅ Build successful with optimizations!
) else (
    echo ❌ Build failed - check output above for details
)

pause