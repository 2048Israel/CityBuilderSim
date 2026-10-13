@echo off
setlocal enabledelayedexpansion

rem ---------------------------------------------------------------------------
rem  A batch's checks in one call, on the PC: compile, the suite, Stale and the
rem  tree, each said in a line or two; everything long goes to target\checks\.
rem  The twin of batch-check.sh.
rem
rem      Batch check.bat                 compile, the whole suite, Stale, git status
rem      Batch check.bat money bank      the same, the suite only the harnesses named so
rem      Batch check.bat --fast          compile, Stale, git status: no suite
rem      Batch check.bat --maps          the maps regenerated as well
rem
rem  It writes nothing git tracks unless --maps is given: this folder pulls
rem  main every ten minutes, and a regenerated docs\ here would stop the pull
rem  (2026-10-13). A batch done on this machine regenerates with --maps.
rem
rem  Compiles with Maven when mvn is on the PATH; otherwise it uses what
rem  NetBeans last built (Clean and Build first). Exit code 0 only when it
rem  compiled, every harness run passed and Stale has no firm finding. It
rem  waits for a key at the end unless Claude Code ran it (CLAUDECODE is set)
rem  or --no-pause is given.
rem ---------------------------------------------------------------------------

cd /d "%~dp0"
set "OUT=target\checks"
if not exist "%OUT%" mkdir "%OUT%"
set "FAILED=0"
set "FAST=0"
set "MAPS=0"
set "PAUSE=1"
if defined CLAUDECODE set "PAUSE=0"
set "NAMES="
:args
if "%~1"=="--fast" ( set "FAST=1" & shift & goto args )
if "%~1"=="--maps" ( set "MAPS=1" & shift & goto args )
if "%~1"=="--no-pause" ( set "PAUSE=0" & shift & goto args )
if not "%~1"=="" ( set "NAMES=!NAMES! %~1" & shift & goto args )

set "JAVA=java"
where java >nul 2>nul || set "JAVA=C:\Program Files\Java\jdk-21\bin\java.exe"

rem --- compile ----------------------------------------------------------------
where mvn >nul 2>nul
if errorlevel 1 (
    if not exist "target\classes\ham\citybuildersim\AllChecks.class" (
        echo compile   no mvn on the PATH and nothing built: NetBeans, Clean and Build
        set "FAILED=1"
        goto done
    )
    echo compile   skipped - no mvn on the PATH, using NetBeans' last build
) else (
    call mvn -q -B compile > "%OUT%\compile.txt" 2>&1
    if errorlevel 1 (
        echo compile   FAILED - whole output: %OUT%\compile.txt
        findstr /C:".java:[" "%OUT%\compile.txt"
        set "FAILED=1"
        goto done
    )
    echo compile   OK
)

rem the classpath: gson, and JavaFX for BuildMenuCheck, from the local Maven repository
set "M2=%USERPROFILE%\.m2\repository"
set "CP=target\classes;%M2%\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"
for %%M in (base graphics controls fxml) do (
    for %%J in ("%M2%\org\openjfx\javafx-%%M\21\*.jar") do set "CP=!CP!;%%~fJ"
)

rem --- the suite -------------------------------------------------------------
if "%FAST%"=="0" (
    echo suite     running, about ten minutes...
    "%JAVA%" -cp "%CP%" ham.citybuildersim.AllChecks -f%NAMES% > "%OUT%\suite.txt" 2>&1
    if errorlevel 1 (
        set "FAILED=1"
        echo suite     FAILED
        type "%OUT%\suite.txt"
    ) else (
        for /f "delims=:" %%L in ('findstr /C:" run in " "%OUT%\suite.txt"') do echo suite     OK - %%L
    )
)

rem --- the maps, only when asked ---------------------------------------------------
if "%MAPS%"=="1" (
    "%JAVA%" -cp target\classes ham.citybuildersim.tools.Maps > "%OUT%\maps.txt" 2>&1
    if errorlevel 1 (
        set "FAILED=1"
        echo maps      FAILED - %OUT%\maps.txt
    ) else (
        echo maps      regenerated
    )
)

rem --- Stale -------------------------------------------------------------------
"%JAVA%" -cp target\classes ham.citybuildersim.tools.Stale > "%OUT%\stale.txt" 2>&1
if errorlevel 1 set "FAILED=1"
for /f "delims=." %%L in ('findstr /R /C:"^[0-9]* firm finding" "%OUT%\stale.txt"') do echo stale     %%L - report: %OUT%\stale.txt

rem --- the tree ----------------------------------------------------------------
where git >nul 2>nul
if not errorlevel 1 (
    echo tree:
    git status --short
)

:done
echo.
if "%FAILED%"=="0" ( echo   All clear. ) else ( echo   Something failed - see above. )
if "%PAUSE%"=="1" pause
exit /b %FAILED%
