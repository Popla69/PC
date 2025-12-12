@echo off
echo 🔧 Switching to Android Studio Embedded JDK
echo ==========================================
echo.

echo 📝 Commenting out custom JDK path in gradle.properties...

:: Create backup
copy gradle.properties gradle.properties.backup

:: Comment out the JDK path line
powershell -Command "(Get-Content gradle.properties) -replace '^org.gradle.java.home=', '# org.gradle.java.home=' | Set-Content gradle.properties"

echo ✅ Updated gradle.properties to use Android Studio's embedded JDK
echo.
echo 📋 Current configuration:
findstr "java.home" gradle.properties
echo.
echo 🎯 Next steps in Android Studio:
echo 1. File → Settings → Build Tools → Gradle
echo 2. Set Gradle JDK to: "Embedded JDK version 17.x.x"
echo 3. Sync Project with Gradle Files
echo.
pause