@echo off
echo 🚀 Installing JDK 17 using PowerShell (More Reliable)
echo.

:: Check if PowerShell is available
powershell -Command "Write-Host 'PowerShell available'" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ PowerShell not available. Using batch method...
    call install-jdk17-here.bat
    exit /b %ERRORLEVEL%
)

:: Run PowerShell installation script
echo 📥 Running PowerShell installation...
powershell -ExecutionPolicy Bypass -File "install-jdk17-here.ps1"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ JDK 17 installation completed!
    echo 🎯 Ready to build with: build-with-hilt-room.bat
) else (
    echo.
    echo ❌ PowerShell installation failed. Trying batch method...
    call install-jdk17-here.bat
)

pause