@echo off
setlocal

rem ---------------------------------------------------------------------------
rem  Finds the comments and documents that have stopped being true: a javadoc
rem  with no member under it, prose naming a file that is not in the tree, a
rem  header that miscounts its own banners, and the soft ones under those.
rem
rem  Reads the classes NetBeans writes to target\classes, so Clean and Build
rem  first. Takes a second. The firm findings are the exit code, and StaleCheck
rem  asserts them in AllChecks - this is for reading the list.
rem ---------------------------------------------------------------------------

cd /d "%~dp0"

if not exist "target\classes\ham\citybuildersim\tools\Stale.class" (
    echo.
    echo   Cannot find target\classes\ham\citybuildersim\tools\Stale.class
    echo.
    echo   Build first: in NetBeans, right-click the project and choose
    echo   "Clean and Build". The tools compile with the rest of the game.
    echo.
    pause
    exit /b 1
)

rem Prefer whatever java is on PATH; fall back to the installed JDK.
set "JAVA=java"
where java >nul 2>nul || set "JAVA=C:\Program Files\Java\jdk-21\bin\java.exe"

"%JAVA%" -cp "target\classes" ham.citybuildersim.tools.Stale

set "CODE=%ERRORLEVEL%"
echo.
if "%CODE%"=="0" (
    echo   Nothing stale that this can see. The prose and the tree agree.
) else (
    echo   %CODE% firm finding^(s^) above - each one is a sentence to fix or a
    echo   file to put back. The soft categories are not counted here.
)
echo.
pause
