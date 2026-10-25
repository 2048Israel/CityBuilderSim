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
rem      Batch check.bat --timing        compile and the timing harnesses alone, hard:
rem                                      the ship gate's (TM1)
rem
rem  Not here: the 80-run ensemble, tools/ensemble/ensemble.sh in Git Bash, run
rem  only for a batch whose playtest trace moves (EN1, 2026-10-20).
rem
rem  Timing (TM1, 2026-10-18; TM2, 2026-10-25): batches check side by side.
rem  When another batch's JVM or Jerus's game is running as the suite starts
rem  (tools\jvms.ps1), the suite times soft (-Dcbs.timing=soft): a timing
rem  figure over its budget says SOFT on its line and is not counted, and a
rem  line says so. --timing is always hard, and it first waits for a quiet
rem  minute (tools\jvms.ps1 -Quiet): four empty looks in a row, 15 s apart,
rem  where a look that finds another batch's JVM starts the count over - a
rem  runner that starts its JVMs one after another leaves a gap that one
rem  look can fall in. At most 30 minutes; then it runs hard all the same and
rem  says so. The game is not waited for (CBS_QUIET_GAME=1 waits for it too):
rem  a line says it runs. The wait's lines show as they come.
rem
rem  An option it does not know is refused with exit 2 (--pick is
rem  batch-check.sh's): taken for a harness name it ran none and said All clear
rem  (BP1, 2026-10-25).
rem  A self-test hook (tools/quiet-selftest.sh): with CBS_TIMING_WAIT_ONLY set,
rem  --timing does the wait right after the options are read - no compile, no
rem  harness - and exits with the wait's exit code (0 quiet, 3 the cap).
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
set "TIMING=0"
set "PAUSE=1"
if defined CLAUDECODE set "PAUSE=0"
set "NAMES="
:args
if "%~1"=="--fast" ( set "FAST=1" & shift & goto args )
if "%~1"=="--maps" ( set "MAPS=1" & shift & goto args )
if "%~1"=="--timing" ( set "TIMING=1" & shift & goto args )
if "%~1"=="--no-pause" ( set "PAUSE=0" & shift & goto args )
if "%~1"=="" goto argsdone
set "ARG=%~1"
if "!ARG:~0,2!"=="--" (
    echo Batch check.bat: unknown option !ARG! - it takes --fast, --maps, --timing, --no-pause and harness names
    set "FAILED=2"
    goto done
)
set "NAMES=!NAMES! %~1" & shift & goto args
:argsdone

rem --- the self-test hook (TM2): --timing with CBS_TIMING_WAIT_ONLY waits and stops, before the compile ---
if not "%TIMING%"=="1" goto nohook
if not defined CBS_TIMING_WAIT_ONLY goto nohook
powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1 -Quiet
set "WAITRC=%errorlevel%"
echo timing    hard - the timing harnesses alone (CBS_TIMING_WAIT_ONLY: not run)
exit /b %WAITRC%
:nohook

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

rem --- timing: hard after a quiet minute, or soft while another batch's JVM or the game runs (TM1, TM2) ---
set "TIMED="
if "%FAST%"=="1" goto timed
if "%TIMING%"=="1" goto wait
set "OTHERS=0"
set "GAME=0"
for /f "tokens=1,2" %%A in ('powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1 2^>nul') do (
    set "OTHERS=%%A"
    set "GAME=%%B"
)
goto soft
:wait
rem --timing: the wait prints its own lines as they come, so it is not read through for /f.
rem It ends 0 (a quiet minute) or 3 (the 30-minute cap, said): hard either way, and the game line is its.
powershell -NoProfile -ExecutionPolicy Bypass -File tools\jvms.ps1 -Quiet
:hard
set "TIMED=-Dcbs.timing=hard"
set "NAMES=!NAMES! --timing"
echo timing    hard - the timing harnesses alone
goto timed
:soft
if "%OTHERS%%GAME%"=="00" goto timed
set "TIMED=-Dcbs.timing=soft"
set "WHO="
if not "%OTHERS%"=="0" set "WHO=%OTHERS% other batch JVM(s)"
if not "%GAME%"=="0" if defined WHO set "WHO=!WHO! and"
if not "%GAME%"=="0" set "WHO=!WHO! the game"
echo timing    soft - !WHO! running: a timing miss says SOFT and is not counted
:timed

rem --- the suite -------------------------------------------------------------
if "%FAST%"=="0" (
    if "%TIMING%"=="1" ( echo suite     running the timing harnesses... ) else ( echo suite     running, about ten minutes... )
    "%JAVA%" %TIMED% -cp "%CP%" ham.citybuildersim.AllChecks -f%NAMES% > "%OUT%\suite.txt" 2>&1
    if errorlevel 1 (
        set "FAILED=1"
        echo suite     FAILED
        type "%OUT%\suite.txt"
    ) else (
        for /f "delims=:" %%L in ('findstr /C:" run in " "%OUT%\suite.txt"') do echo suite     OK - %%L
        for /f "delims=" %%L in ('findstr /C:" SOFT, not counted " "%OUT%\suite.txt"') do echo timing    %%L
    )
)

rem --timing: the timing harnesses and nothing more
if "%TIMING%"=="1" goto done

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
