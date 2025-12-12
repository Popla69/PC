@echo off
echo Setting Android AVD to D: drive...
setx ANDROID_AVD_HOME "D:\AndroidData\avd"
setx ANDROID_SDK_HOME "D:\AndroidData"
echo.
echo Environment variables set!
echo Please restart Android Studio for changes to take effect.
echo.
pause
