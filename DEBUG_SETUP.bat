@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 🔍 DEBUG VERSION - ZERO SETUP INSTALLER
echo ========================================
echo.
echo This debug version will show exactly where any errors occur.
echo.
pause

echo [DEBUG] Starting system compatibility test...

REM Test RAM with error handling
echo [DEBUG] Testing RAM...
for /f "tokens=2 delims==" %%i in ('wmic computersystem get TotalPhysicalMemory /value 2^>nul ^| find "="') do set RAM_BYTES=%%i

if "!RAM_BYTES!"=="" (
    echo [ERROR] Could not detect RAM - trying alternative method...
    for /f "skip=1" %%p in ('wmic computersystem get TotalPhysicalMemory') do (
        if not "%%p"=="" set RAM_BYTES=%%p
        goto :ram_done
    )
    :ram_done
)

if "!RAM_BYTES!"=="" (
    echo [ERROR] RAM detection failed completely
    set RAM_GB=8
    echo [DEBUG] Assuming 8GB RAM for compatibility
) else (
    set /a RAM_GB=!RAM_BYTES!/1073741824
    echo [DEBUG] RAM detected: !RAM_GB! GB
)

REM Test CPU cores with error handling
echo [DEBUG] Testing CPU cores...
for /f "tokens=2 delims==" %%i in ('wmic cpu get NumberOfCores /value 2^>nul ^| find "="') do set CPU_CORES=%%i

if "!CPU_CORES!"=="" (
    echo [ERROR] Could not detect CPU cores - assuming 4 cores
    set CPU_CORES=4
) else (
    echo [DEBUG] CPU cores detected: !CPU_CORES!
)

REM Test disk space with error handling
echo [DEBUG] Testing disk space...
for /f "tokens=3" %%i in ('dir /-c 2^>nul ^| find "bytes free"') do set FREE_SPACE=%%i

if "!FREE_SPACE!"=="" (
    echo [ERROR] Could not detect free space - assuming sufficient
    set FREE_GB=50
) else (
    set FREE_SPACE=!FREE_SPACE:,=!
    set /a FREE_GB=!FREE_SPACE!/1073741824
    echo [DEBUG] Free space detected: !FREE_GB! GB
)

REM Test internet with error handling
echo [DEBUG] Testing internet connection...
ping -n 1 google.com >nul 2>&1
if !errorlevel! equ 0 (
    echo [DEBUG] Internet: Connected
) else (
    echo [WARNING] Internet test failed - may still work
)

echo.
echo ========================================
echo 📊 SYSTEM SUMMARY
echo ========================================
echo RAM: !RAM_GB! GB
echo CPU Cores: !CPU_CORES!
echo Free Space: !FREE_GB! GB
echo.

REM Basic compatibility check
if !RAM_GB! LSS 4 (
    echo [ERROR] System has less than 4GB RAM - Android Studio will not work
    pause
    exit /b 1
)

if !FREE_GB! LSS 10 (
    echo [ERROR] Less than 10GB free space - insufficient for installation
    pause
    exit /b 1
)

echo [SUCCESS] System appears compatible!
echo.
echo Would you like to continue with the full installation?
echo This will download and install:
echo - Android Studio (~1GB)
echo - JDK 17 (~200MB)
echo - Android SDK (~2GB)
echo - Gradle (~100MB)
echo.
choice /c YN /m "Continue with full installation? (Y/N)"
if !errorlevel! equ 2 (
    echo Installation cancelled by user.
    pause
    exit /b 0
)

echo.
echo ========================================
echo 🚀 STARTING FULL INSTALLATION
echo ========================================
echo.
echo Calling main installer...
call "ZERO_SETUP_COMPLETE_INSTALL.bat"

echo.
echo ========================================
echo 🏁 DEBUG SESSION COMPLETE
echo ========================================
pause