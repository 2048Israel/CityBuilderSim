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
rem  THE VERSION IS READ OUT OF GameVersion.java, not written here.
rem
rem  It used to be typed in both places, with a comment in GameVersion asking
rem  whoever changed one to remember the other. That is not a mechanism, it is
rem  a hope, and it went out of step the first time it mattered: 0.5.1 in the
rem  source, 0.5.0 stamped on the exe. One place now - the .java file - and
rem  this pulls it from there.
set "APPVER="
for /f "tokens=2 delims==" %%v in ('findstr /c:"final String VERSION =" "src\main\java\ham\citybuildersim\GameVersion.java"') do (
    for /f "tokens=1 delims=;" %%w in ("%%v") do set "APPVER=%%w"
)
set "APPVER=%APPVER: =%"
set "APPVER=%APPVER:"=%"
if not defined APPVER (
    echo.
    echo   Could not read VERSION out of GameVersion.java - stopping rather
    echo   than stamping this build with the wrong number.
    echo.
    pause
    exit /b 1
)
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

rem ------------------------------ the Earth ------------------------------
rem  The real Earth, data\earth-2.bin (about 282 MB), is not in git. A build
rem  must carry it, so this fetches it when this PC lacks it or holds a bad
rem  copy, and stops before jpackage when it cannot be had.
call "tools\earth\get-earth.bat" nopause
if errorlevel 1 (
    echo.
    echo   No good data\earth-2.bin, so no build: without it the game would
    echo   offer only the generated world. The reason is above.
    echo.
    pause
    exit /b 1
)
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

rem  --icon is the game's icon (BR1, 2026-10-27), which tools\branding\make_icons.py
rem  draws. --description is what Windows calls the exe in Task Manager and on
rem  a pinned taskbar button, so it is the game's name, Civic Ledger. The exe
rem  itself keeps APPNAME, CityBuilderSim.exe: a player's shortcut, the itch.io
rem  app's launch and a Steam launch option all point at that file, and a new
rem  name would strand each of them at the first update.

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
  --icon "src\main\resources\icons\icon.ico" ^
  --description "Civic Ledger"

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

rem --------------------------- the Earth beside the exe ---------------------------
rem  The game looks for data\earth-2.bin in the folder it runs in, which for
rem  the exe is its own folder (the start line at the bottom says why). Its
rem  sources' credits go with it, as their licences ask.
mkdir "dist\%APPNAME%\data"
copy /y "data\earth-2.bin" "dist\%APPNAME%\data\" >nul
copy /y "tools\earth\ATTRIBUTION-2.txt" "dist\%APPNAME%\data\earth-2-ATTRIBUTION.txt" >nul
rem  The Rankings' figures (world-2025.json) and the world fixture are in the jar: their credits go too (LC1).
copy /y "tools\world\ATTRIBUTION.txt" "dist\%APPNAME%\data\world-ATTRIBUTION.txt" >nul
if not exist "dist\%APPNAME%\data\earth-2.bin" (
    echo.
    echo   Could not copy data\earth-2.bin into dist\%APPNAME%\data - stopping
    echo   rather than leave a build without the Earth.
    echo.
    pause
    exit /b 1
)
echo  Copied data\earth-2.bin next to the exe, with its credits and the world's.

rem ------------------------------- the zip -------------------------------
rem  What goes up as the release: the whole folder under its own name, as
rem  dist\CityBuilderSim-<version>.zip, the Earth inside it. Made before the
rem  test launch below, so nothing the game writes as it runs gets into it.
set "ZIP=dist\%APPNAME%-%APPVER%.zip"
if exist "%ZIP%" del /q "%ZIP%"
echo  Zipping it as %ZIP% ...
tar -a -c -f "%ZIP%" -C dist "%APPNAME%" 2>nul
if errorlevel 1 if exist "%ZIP%" del /q "%ZIP%"
if not exist "%ZIP%" powershell -NoProfile -Command "Compress-Archive -Path 'dist\%APPNAME%' -DestinationPath '%ZIP%'"

echo.
echo  ===  Done  ===
echo.
echo  dist\%APPNAME%\%APPNAME%.exe
if exist "%ZIP%" (
    echo  %ZIP%   - upload this one as the release
) else (
    echo  No zip: zipping failed. Zip dist\%APPNAME% by hand.
)
echo.
echo  That whole dist\%APPNAME% folder is the game. It carries its own Java
echo  runtime and the real Earth, so it will run on a machine with no Java on
echo  it. The zip is that folder; point Steam at the exe inside it.
echo.
echo  Starting it now so you can see whether it works...
echo.

rem  /D sets the working directory to the game's own folder, which is what
rem  Windows does when the exe is double-clicked. It matters: the working
rem  directory is where save.txt lands and where buildings.json is looked for,
rem  so launching without it would test a setup no player will ever have.
start "" /D "dist\%APPNAME%" "%APPNAME%.exe"

pause
