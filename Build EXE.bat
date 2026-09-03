@echo off
setlocal

rem ===========================================================================
rem  Turns the packaged jar into a real Windows program.
rem
rem  jpackage ships inside the JDK. It takes the fat jar plus a trimmed-down
rem  Java runtime and writes a folder containing CityBuilderSim.exe. A player
rem  who gets that folder needs no Java installed at all, which is the whole
rem  point - "install Java first" is not something a Steam buyer will do.
rem
rem  Type is app-image, NOT an installer. That is deliberate: Steam does not
rem  want an .msi, it wants the game's files, and it does the installing.
rem  app-image also avoids needing the WiX toolset, which the installer types
rem  require and which is not installed here.
rem
rem  Run this after a Clean and Build. It reads target\ and writes dist\.
rem ===========================================================================

cd /d "%~dp0"

set "APPNAME=CityBuilderSim"
set "APPVER=1.0.0"
set "JARNAME=CityBuilderSim-1.0-SNAPSHOT-executable.jar"
set "JARPATH=target\%JARNAME%"

rem  The module list is not guesswork - jdeps was run against this exact jar
rem  and reported these. jdk.unsupported matters more than it looks: gson
rem  reaches for sun.misc.Unsafe, and that module is not part of the default
rem  java.se set. jdk.localedata is added on top so the money and number
rem  formatting behaves on machines that are not set to English.
set "MODULES=java.base,java.desktop,java.scripting,java.sql,jdk.jfr,jdk.unsupported,jdk.localedata"

echo.
echo  ===  Building %APPNAME% %APPVER%  ===
echo.

rem --------------------------- find jpackage ---------------------------
set "JPACKAGE=jpackage"
where jpackage >nul 2>nul || set "JPACKAGE=C:\Program Files\Java\jdk-21\bin\jpackage.exe"

if not "%JPACKAGE%"=="jpackage" if not exist "%JPACKAGE%" (
    echo   Could not find jpackage.
    echo   Looked on PATH and at: %JPACKAGE%
    echo.
    echo   jpackage comes with JDK 14 and later. If your JDK lives elsewhere,
    echo   edit the JPACKAGE line in this file.
    echo.
    pause
    exit /b 1
)

echo  jpackage:  %JPACKAGE%

rem ----------------------------- find the jar -----------------------------
if not exist "%JARPATH%" (
    echo.
    echo   Cannot find %JARPATH%
    echo.
    echo   Build the project first - in NetBeans, right-click the project
    echo   and choose Clean and Build. That writes both jars into target\.
    echo.
    pause
    exit /b 1
)

echo  jar:       %JARPATH%
echo  modules:   %MODULES%
echo.

rem ------------------------- stage a clean input dir -------------------------
rem  jpackage copies EVERYTHING in --input into the app folder, so it gets a
rem  directory of its own containing nothing but the one jar. Pointing it at
rem  target\ would drag the thin jar and every build artefact along with it.
if exist "build\jpackage-input" rmdir /s /q "build\jpackage-input"
mkdir "build\jpackage-input"
copy /y "%JARPATH%" "build\jpackage-input\" >nul

rem --------------------- clear the previous build, carefully ---------------------
rem  Only removes a folder that is recognisably a previous output of this
rem  script. Anything else there is left alone and the build stops instead.
if exist "dist\%APPNAME%\%APPNAME%.exe" rmdir /s /q "dist\%APPNAME%"

if exist "dist\%APPNAME%" (
    echo   dist\%APPNAME% exists but does not look like a previous build.
    echo   Move or rename it, then run this again.
    echo.
    pause
    exit /b 1
)

rem ------------------------------- build -------------------------------
echo  Running jpackage. This takes a minute - it is assembling a Java
echo  runtime, not just copying files.
echo.

"%JPACKAGE%" ^
  --type app-image ^
  --name "%APPNAME%" ^
  --app-version %APPVER% ^
  --input "build\jpackage-input" ^
  --main-jar "%JARNAME%" ^
  --main-class ham.citybuildersim.CityBuilderSim ^
  --add-modules %MODULES% ^
  --dest "dist" ^
  --vendor "Jerus" ^
  --description "Macroeconomic city simulator"

if not "%ERRORLEVEL%"=="0" (
    echo.
    echo   jpackage failed with code %ERRORLEVEL% - the reason is above.
    echo.
    pause
    exit /b 1
)

rem ------------------- put the balance file where it is editable -------------------
rem  buildings.json is already inside the jar and the game falls back to that
rem  copy, so this is not required for it to run. It is here because
rem  BuildingCatalog checks the working directory FIRST, which means a copy
rem  sitting next to the exe can be edited and the change is live on the next
rem  launch - a balance pass without a rebuild, for you now and for modders
rem  later. A broken edit costs the file, not the game.
if exist "src\main\resources\buildings.json" (
    copy /y "src\main\resources\buildings.json" "dist\%APPNAME%\" >nul
    echo  Copied buildings.json next to the exe - editable without rebuilding.
)

echo.
echo  ===  Done  ===
echo.
echo  dist\%APPNAME%\%APPNAME%.exe
echo.
echo  That whole dist\%APPNAME% folder is the game. It carries its own Java
echo  runtime, so it will run on a machine with no Java on it. Zip the folder
echo  to hand it to someone, or point Steam at the exe inside it.
echo.
echo  Starting it now so you can see whether it works...
echo.

rem  /D sets the working directory to the game's own folder, which is what
rem  Windows does when the exe is double-clicked. It matters: the working
rem  directory is where save.txt lands and where buildings.json is looked for,
rem  so launching without it would test a setup no player will ever have.
start "" /D "dist\%APPNAME%" "%APPNAME%.exe"

pause
