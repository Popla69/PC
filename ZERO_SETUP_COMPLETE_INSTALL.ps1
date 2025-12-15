# PowerShell version of the zero setup installer
# This should bypass Windows security restrictions

Write-Host "🚀 ZERO-SETUP ANDROID DEVELOPMENT INSTALLER" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""

# Set execution policy for this session
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force

# Check if running as administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")

if (-not $isAdmin) {
    Write-Host "⚠️  This installer needs administrator privileges" -ForegroundColor Yellow
    Write-Host "   Restarting as administrator..." -ForegroundColor Yellow
    
    # Restart as administrator
    Start-Process PowerShell -Verb RunAs -ArgumentList "-File `"$PSCommandPath`""
    exit
}

Write-Host "✅ Running with administrator privileges" -ForegroundColor Green
Write-Host ""

# Call the batch file
& ".\ZERO_SETUP_COMPLETE_INSTALL.bat"