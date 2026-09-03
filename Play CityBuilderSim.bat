@echo off
setlocal

rem ---------------------------------------------------------------------------
rem  Launches the packaged game.
rem
rem  A .jar is not an executable on Windows: double-clicking one only works if a
rem  JRE is installed AND .jar is associated with javaw.exe. This calls java
rem  directly, so neither has to be true.
rem
rem  The console window it opens is on purpose. The game prints its month-by-
rem  month reports there, and if it fails to start, the reason is in that window
rem  instead of vanishing with it.
rem ---------------------------------------------------------------------------

cd /d "%~dp0"

set "JAR=target\CityBuilderSim-1.0-SNAPSHOT-executable.jar"

if not exist "%JAR%" (
    echo.
    echo   Cannot find %JAR%
    echo.
    echo   Build it first: in NetBeans, right-click the project and choose
    echo   "Clean and Build". That writes both jars into target\.
    echo.
    pause
    exit /b 1
)

rem Prefer whatever java is on PATH; fall back to the installed JDK.
set "JAVA=java"
where java >nul 2>nul || set "JAVA=C:\Program Files\Java\jdk-21\bin\java.exe"

if not "%JAVA%"=="java" if not exist "%JAVA%" (
    echo.
    echo   No Java found on PATH, and not at:
    echo   %JAVA%
    echo.
    echo   Install a JDK 21 runtime, or edit this file to point at yours.
    echo.
    pause
    exit /b 1
)

echo Starting CityBuilderSim...
echo.

"%JAVA%" -jar "%JAR%"

set "CODE=%ERRORLEVEL%"
echo.
if not "%CODE%"=="0" (
    echo   The game exited with code %CODE% - the error is above.
) else (
    echo   Closed normally.
)
echo.
pause
