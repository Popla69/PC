@echo off
echo 🔧 Switching to Project JDK 17
echo ==============================
echo.

echo 📝 Enabling custom JDK path in gradle.properties...

:: Create backup
copy gradle.properties gradle.properties.backup

:: Uncomment the JDK path line
powershell -Command "(Get-Content gradle.properties) -replace '^# org.gradle.java.home=F', 'org.gradle.java.home=F' | Set-Content gradle.properties"

echo ✅ Updated gradle.properties to use project JDK 17
echo.
echo 📋 Current configuration:
findstr "java.home" gradle.properties
echo.
echo 🎯 Next steps in Android Studio:
echo 1. File → Settings → Build Tools → Gradle
echo 2. Set Gradle JDK to: F:\AgenticAI\jdk-env\jdk-17.0.17+10
echo 3. Sync Project with Gradle Files
echo.
pause