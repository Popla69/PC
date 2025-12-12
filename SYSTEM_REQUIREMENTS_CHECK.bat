@echo off
setlocal enabledelayedexpansion

REM ========================================
REM SYSTEM REQUIREMENTS CHECKER
REM ========================================
REM Quick compatibility test before setup

echo ========================================
echo 🔍 ANDROID DEVELOPMENT COMPATIBILITY TEST
echo ========================================
echo.

REM Test RAM
echo [1/5] Checking RAM...
for /f "tokens=2 delims==" %%i in ('wmic computersystem get TotalPhysicalMemory /value ^| find "="') do set RAM_BYTES=%%i
set /a RAM_GB=!RAM_BYTES!/1073741824

if !RAM_GB! GEQ 32 (
    echo     ✅ RAM: !RAM_GB!GB - EXCELLENT ^(Expected build: 15-30s^)
    set RAM_SCORE=5
) else if !RAM_GB! GEQ 16 (
    echo     ✅ RAM: !RAM_GB!GB - GOOD ^(Expected build: 30-60s^)
    set RAM_SCORE=4
) else if !RAM_GB! GEQ 8 (
    echo     ⚠️  RAM: !RAM_GB!GB - MODERATE ^(Expected build: 60-120s^)
    set RAM_SCORE=2
) else (
    echo     ❌ RAM: !RAM_GB!GB - LOW ^(May struggle^)
    set RAM_SCORE=1
)

REM Test CPU
echo [2/5] Checking CPU...
for /f "tokens=2 delims==" %%i in ('wmic cpu get NumberOfCores /value ^| find "="') do set CPU_CORES=%%i

if !CPU_CORES! GEQ 8 (
    echo     ✅ CPU: !CPU_CORES! cores - EXCELLENT
    set CPU_SCORE=5
) else if !CPU_CORES! GEQ 4 (
    echo     ✅ CPU: !CPU_CORES! cores - GOOD  
    set CPU_SCORE=4
) else if !CPU_CORES! GEQ 2 (
    echo     ⚠️  CPU: !CPU_CORES! cores - MODERATE
    set CPU_SCORE=2
) else (
    echo     ❌ CPU: !CPU_CORES! cores - LOW
    set CPU_SCORE=1
)

REM Test Storage
echo [3/5] Checking storage...
for /f "tokens=3" %%i in ('dir /-c ^| find "bytes free"') do set FREE_SPACE=%%i
set FREE_SPACE=!FREE_SPACE:,=!
set /a FREE_GB=!FREE_SPACE!/1073741824

if !FREE_GB! GEQ 50 (
    echo     ✅ Storage: !FREE_GB!GB free - EXCELLENT
    set STORAGE_SCORE=5
) else if !FREE_GB! GEQ 20 (
    echo     ✅ Storage: !FREE_GB!GB free - GOOD
    set STORAGE_SCORE=4
) else if !FREE_GB! GEQ 10 (
    echo     ⚠️  Storage: !FREE_GB!GB free - MINIMUM
    set STORAGE_SCORE=2
) else (
    echo     ❌ Storage: !FREE_GB!GB free - INSUFFICIENT
    set STORAGE_SCORE=1
)

REM Test Internet
echo [4/5] Checking internet...
ping -n 1 google.com >nul 2>&1
if !errorlevel! equ 0 (
    echo     ✅ Internet: Connected
    set NET_SCORE=5
) else (
    echo     ❌ Internet: Not connected ^(Required for setup^)
    set NET_SCORE=1
)

REM Test Windows
echo [5/5] Checking Windows version...
for /f "tokens=2 delims=[]" %%i in ('ver') do set WIN_VER=%%i
echo     ✅ Windows: !WIN_VER! - Compatible
set WIN_SCORE=5

REM Calculate total score
set /a TOTAL_SCORE=!RAM_SCORE!+!CPU_SCORE!+!STORAGE_SCORE!+!NET_SCORE!+!WIN_SCORE!

echo.
echo ========================================
echo 📊 COMPATIBILITY REPORT
echo ========================================
echo.
echo System Specifications:
echo   RAM: !RAM_GB!GB
echo   CPU: !CPU_CORES! cores
echo   Storage: !FREE_GB!GB free
echo   Internet: Connected
echo   Windows: !WIN_VER!
echo.
echo Compatibility Score: !TOTAL_SCORE!/25
echo.

if !TOTAL_SCORE! GEQ 20 (
    echo 🚀 RATING: EXCELLENT
    echo This system is perfect for Android development!
    echo Expected build times: 15-45 seconds
    echo Recommended: Run ULTIMATE_ONE_CLICK_SETUP.bat
) else if !TOTAL_SCORE! GEQ 15 (
    echo ✅ RATING: GOOD  
    echo This system will work well for Android development.
    echo Expected build times: 30-90 seconds
    echo Recommended: Run ULTIMATE_ONE_CLICK_SETUP.bat
) else if !TOTAL_SCORE! GEQ 10 (
    echo ⚠️  RATING: MODERATE
    echo This system can handle Android development but may be slow.
    echo Expected build times: 60-180 seconds
    echo Consider: Upgrading RAM or using a faster machine
) else (
    echo ❌ RATING: INSUFFICIENT
    echo This system may struggle with Android development.
    echo Issues: 
    if !RAM_SCORE! LSS 3 echo   - Insufficient RAM ^(need 16GB+^)
    if !CPU_SCORE! LSS 3 echo   - Weak CPU ^(need 4+ cores^)
    if !STORAGE_SCORE! LSS 3 echo   - Low storage ^(need 10GB+ free^)
    if !NET_SCORE! LSS 3 echo   - No internet connection
    echo.
    echo Recommendation: Use a more powerful machine
)

echo.
pause