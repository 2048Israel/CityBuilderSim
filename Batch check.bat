@echo off
setlocal enabledelayedexpansion

rem ---------------------------------------------------------------------------
rem  A batch's checks in one call, on the PC: compile, the suite, the maps,
rem  Stale and the tree, each said in a line or two; everything long goes to
rem  target\checks\. The twin of batch-check.sh, for Claude Code on this
rem  machine (it does not pause, so a session can run it).
rem
rem      Batch check.bat                 compile, the whole suite, maps, Stale, git status
rem      Batch check.bat money bank      the same, the suite only the harnesses named so
rem      Batch check.bat --fast          compile, maps, Stale, git status: no suite
rem
rem  Compiles with Maven when mvn is on the PATH; otherwise it uses what
rem  NetBeans last built (Clean and Build first). Exit code 0 only when it
rem  compiled, every harness run passed and Stale has no firm finding.
rem ---------------------------------------------------------------------------

cd /d "%~dp0"
set "OUT=target\checks"
if not exist "%OUT%" mkdir "%OUT%"
set "FAILED=0"
set "FAST=0"
if "%~1"=="--fast" ( set "FAST=1" & shift )
set "NAMES="
:names
if not "%~1"=="" ( set "NAMES=!NAMES! %~1" & shift & goto names )

set "JAVA=java"
where java >nul 2>nul || set "JAVA=C:\Program Files\Java\jdk-21\bin\java.exe"

rem --- compile ----------------------------------------------------------------
where mvn >nul 2>nul
if errorlevel 1 (
    if not exist "target\classes\ham\citybuildersim\AllChecks.class" (
        echo compile   no mvn on the PATH and nothing built: NetBeans, Clean and Build
        exit /b 1
    )
    echo compile   skipped - no mvn on the PATH, using NetBeans' last build
) else (
    call mvn -q -B compile > "%OUT%\compile.txt" 2>&1
    if errorlevel 1 (
        echo compile   FAILED - whole output: %OUT%\compile.txt
        findstr /C:".java:[" "%OUT%\compile.txt"
        exit /b 1
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
    "%JAVA%" -cp "%CP%" ham.citybuildersim.AllChecks -f%NAMES% > "%OUT%\suite.txt" 2>&1
    if errorlevel 1 (
        set "FAILED=1"
        echo suite     FAILED
        type "%OUT%\suite.txt"
    ) else (
        for /f "delims=:" %%L in ('findstr /C:" run in " "%OUT%\suite.txt"') do echo suite     OK - %%L
    )
)

rem --- the maps ----------------------------------------------------------------
"%JAVA%" -cp target\classes ham.citybuildersim.tools.Maps > "%OUT%\maps.txt" 2>&1
if errorlevel 1 (
    set "FAILED=1"
    echo maps      FAILED - %OUT%\maps.txt
) else (
    echo maps      regenerated
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

exit /b %FAILED%
