@echo off
setlocal

rem ---------------------------------------------------------------------------
rem  Regenerates docs\ from the sources: the code map (docs\map), the dials,
rem  the month order and the harness map. See CLAUDE.md for what they are for.
rem
rem  Reads the classes NetBeans writes to target\classes, so Clean and Build
rem  first. Takes about a second. Commit docs\ with the batch it describes.
rem ---------------------------------------------------------------------------

cd /d "%~dp0"

if not exist "target\classes\ham\citybuildersim\tools\Maps.class" (
    echo.
    echo   Cannot find target\classes\ham\citybuildersim\tools\Maps.class
    echo.
    echo   Build first: in NetBeans, right-click the project and choose
    echo   "Clean and Build". The generators compile with the rest of the game.
    echo.
    pause
    exit /b 1
)

rem Prefer whatever java is on PATH; fall back to the installed JDK.
set "JAVA=java"
where java >nul 2>nul || set "JAVA=C:\Program Files\Java\jdk-21\bin\java.exe"

"%JAVA%" -cp "target\classes" ham.citybuildersim.tools.Maps

set "CODE=%ERRORLEVEL%"
echo.
if not "%CODE%"=="0" (
    echo   The generator exited with code %CODE% - the error is above.
) else (
    echo   docs\ is current. Commit it with the sources it describes.
)
echo.
pause
