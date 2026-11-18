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
rem
rem  "Build EXE.bat nopause" is for a script: no pause, and no test launch at
rem  the end (CR1-J: a session proving the build must not start the game on
rem  your saves). A double-click pauses and launches as it always did.
rem ===========================================================================

cd /d "%~dp0"

set "PAUSE=pause"
set "LAUNCH=1"
if /i "%~1"=="nopause" (
    set "PAUSE=rem"
    set "LAUNCH="
)

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
    %PAUSE%
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

rem --------------------------- the packaging JDK ---------------------------
rem  CR1-J (Jerus, 2026-11-17): the sold game's runtime is Eclipse Temurin 21,
rem  an OpenJDK build under the GPL with the Classpath Exception. Until then
rem  this took jpackage from PATH or else Oracle's JDK in Program Files, and
rem  Oracle's runtime comes under its No-Fee Terms, which bar fees for a
rem  product bundled with it. So the JDK is named, never searched for: its
rem  release file must say Eclipse Adoptium and Java 21, and its own jlink and
rem  jpackage are the only ones run. CBS_PACKAGING_JDK names another folder;
rem  the same checks apply, and Packaging jdk below holds it to the version
rem  whose source ships (tools\credits\source-archives.properties).
set "JDK=%USERPROFILE%\Tools\jdk-21-temurin"
if defined CBS_PACKAGING_JDK set "JDK=%CBS_PACKAGING_JDK%"
if not exist "%JDK%\release" (
    echo   No JDK at %JDK%
    echo.
    echo   The game is packaged with Eclipse Temurin 21 and nothing else. Unzip
    echo   it there from https://adoptium.net/temurin/releases/?version=21 , or
    echo   set CBS_PACKAGING_JDK to the folder it is in.
    echo.
    %PAUSE%
    exit /b 1
)
findstr /b /c:"IMPLEMENTOR=\"Eclipse Adoptium\"" "%JDK%\release" >nul
if errorlevel 1 (
    echo   %JDK% is not Eclipse Temurin: its release file does not say
    echo   IMPLEMENTOR="Eclipse Adoptium". A sold build carries Temurin's runtime
    echo   and no other, so no build.
    echo.
    %PAUSE%
    exit /b 1
)
findstr /b /r /c:"JAVA_VERSION=.21\." "%JDK%\release" >nul
if errorlevel 1 (
    echo   %JDK% is not Java 21: its release file's JAVA_VERSION is another,
    echo   so no build.
    echo.
    %PAUSE%
    exit /b 1
)
for %%t in (java jlink jpackage) do if not exist "%JDK%\bin\%%t.exe" (
    echo   %JDK%\bin has no %%t.exe - is it a whole JDK? No build.
    echo.
    %PAUSE%
    exit /b 1
)

echo  JDK:       %JDK%

rem ----------------------------- find the jar -----------------------------
if not exist "%JARPATH%" (
    echo.
    echo   Cannot find %JARPATH%
    echo.
    echo   Build the project first - in NetBeans, right-click the project
    echo   and choose Clean and Build. That writes both jars into target\.
    echo.
    %PAUSE%
    exit /b 1
)

echo  jar:       %JARPATH%
echo  modules:   %MODULES%
echo.

rem  The JDK against the pins: the exact Temurin whose source archive ships.
"%JDK%\bin\java.exe" -cp "%JARPATH%" ham.citybuildersim.tools.Packaging jdk "%JDK%"
if errorlevel 1 (
    echo.
    echo   This JDK is not the one whose source ships with the game - the
    echo   reason is above - so no build.
    echo.
    %PAUSE%
    exit /b 1
)

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
    %PAUSE%
    exit /b 1
)
echo.

rem ----------------------------- the nations -----------------------------
rem  The nations' world, data\world-1.bin (about 9 MB), is not in git either.
rem  Since LC3 (2026-11-02) it holds no figure a sold game may not ship, so a
rem  build carries it: without it the game has no nations, no world market
rem  and no partners (IR1: 0.9.0 went out so). Fetched as the Earth is.
call "tools\earth\get-earth.bat" nopause world-1
if errorlevel 1 (
    echo.
    echo   No good data\world-1.bin, so no build: without it the game would
    echo   have no nations. The reason is above.
    echo.
    %PAUSE%
    exit /b 1
)
echo.

rem --------------------------- the source archives ---------------------------
rem  CR1-J (Jerus, 2026-11-17): the complete corresponding source of OpenJFX
rem  and of the Java runtime ships beside the exe, as section 3 of their GPL
rem  asks - the publishers' own archives, pinned by version and SHA-256 in
rem  tools\credits\source-archives.properties. They are fetched once into a
rem  cache outside git (about 175 MB; CBS_SOURCE_CACHE names another folder)
rem  and checked every build. One missing or changed and there is no build.
set "SOURCES=%USERPROFILE%\Tools\CityBuilderSim-sources"
if defined CBS_SOURCE_CACHE set "SOURCES=%CBS_SOURCE_CACHE%"
"%JDK%\bin\java.exe" -cp "%JARPATH%" ham.citybuildersim.tools.Packaging fetch "%SOURCES%"
if errorlevel 1 (
    echo.
    echo   No good copy of every source archive, so no build: a sold build
    echo   carries the complete source of OpenJFX and of its Java runtime.
    echo   The reason is above.
    echo.
    %PAUSE%
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
    %PAUSE%
    exit /b 1
)

rem ------------------------------ the runtime ------------------------------
rem  Temurin's own jlink makes the runtime, with the options jpackage would
rem  have used itself, and jpackage takes it as it is. Temurin's NOTICE, its
rem  distribution's notice, goes beside the runtime's release file and legal
rem  folder, where the Credits page reads them.
if exist "build\runtime" rmdir /s /q "build\runtime"
"%JDK%\bin\jlink.exe" --add-modules %MODULES% --strip-native-commands --strip-debug --no-man-pages --no-header-files --output "build\runtime"
if errorlevel 1 (
    echo.
    echo   jlink failed - the reason is above.
    echo.
    %PAUSE%
    exit /b 1
)
copy /y "%JDK%\NOTICE" "build\runtime\NOTICE" >nul

rem ------------------------------- build -------------------------------
echo  Running jpackage. This takes a minute.
echo.

rem  --icon is the game's icon (BR1, 2026-10-27), which tools\branding\make_icons.py
rem  draws. --description is what Windows calls the exe in Task Manager and on
rem  a pinned taskbar button, so it is the game's name, Civic Ledger. The exe
rem  itself keeps APPNAME, CityBuilderSim.exe: a player's shortcut, the itch.io
rem  app's launch and a Steam launch option all point at that file, and a new
rem  name would strand each of them at the first update.

"%JDK%\bin\jpackage.exe" ^
  --type app-image ^
  --name "%APPNAME%" ^
  --app-version %APPVER% ^
  --input "build\jpackage-input" ^
  --main-jar "%JARNAME%" ^
  --main-class ham.citybuildersim.CityBuilderSim ^
  --runtime-image "build\runtime" ^
  --dest "dist" ^
  --vendor "Jerus" ^
  --icon "src\main\resources\icons\icon.ico" ^
  --description "Civic Ledger"

if not "%ERRORLEVEL%"=="0" (
    echo.
    echo   jpackage failed with code %ERRORLEVEL% - the reason is above.
    echo.
    %PAUSE%
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
rem  The nations' world goes beside it (LC3); its credits cover the Rankings' figures (world-2025.json) and the
rem  world fixture in the jar too (LC1).
copy /y "data\world-1.bin" "dist\%APPNAME%\data\" >nul
copy /y "tools\world\ATTRIBUTION.txt" "dist\%APPNAME%\data\world-ATTRIBUTION.txt" >nul
if not exist "dist\%APPNAME%\data\earth-2.bin" (
    echo.
    echo   Could not copy data\earth-2.bin into dist\%APPNAME%\data - stopping
    echo   rather than leave a build without the Earth.
    echo.
    %PAUSE%
    exit /b 1
)
if not exist "dist\%APPNAME%\data\world-1.bin" (
    echo.
    echo   Could not copy data\world-1.bin into dist\%APPNAME%\data - stopping
    echo   rather than leave a build without the nations.
    echo.
    %PAUSE%
    exit /b 1
)
echo  Copied data\earth-2.bin and data\world-1.bin next to the exe, with their credits.

rem ----------------------- the source and the notices beside the exe -----------------------
rem  CR1-J: the source archives and their README go in source\, the
rem  libraries' and Microsoft's texts (the jar's credits) in legal\. Then the
rem  whole image is checked before anything is zipped - Temurin's runtime and
rem  its legal folder, every notice and terms file, every archive against its
rem  pin - and an image that fails is removed, so nothing unfit is shipped.
echo  Staging the source archives and the notices...
"%JDK%\bin\java.exe" -cp "%JARPATH%" ham.citybuildersim.tools.Packaging stage "%SOURCES%" "dist\%APPNAME%"
if errorlevel 1 goto unfit
"%JDK%\bin\java.exe" -cp "%JARPATH%" ham.citybuildersim.tools.Packaging verify "dist\%APPNAME%"
if errorlevel 1 goto unfit

rem ------------------------------- the zip -------------------------------
rem  What goes up as the release: the whole folder under its own name, as
rem  dist\CityBuilderSim-<version>.zip, the Earth and the source inside it.
rem  Made before the test launch below, so nothing the game writes as it runs
rem  gets into it.
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
echo  runtime, the real Earth and the source code its licences ask for, so it
echo  will run on a machine with no Java on it. The zip is that folder; point
echo  Steam at the exe inside it.
echo.
if not defined LAUNCH exit /b 0
echo  Starting it now so you can see whether it works...
echo.

rem  /D sets the working directory to the game's own folder, which is what
rem  Windows does when the exe is double-clicked. It matters: the working
rem  directory is where save.txt lands and where buildings.json is looked for,
rem  so launching without it would test a setup no player will ever have.
start "" /D "dist\%APPNAME%" "%APPNAME%.exe"

pause
exit /b 0

:unfit
echo.
echo   The built image lacks what a sold build must carry, listed above, so
echo   dist\%APPNAME% and this version's zip are removed: nothing is published.
echo.
if exist "dist\%APPNAME%" rmdir /s /q "dist\%APPNAME%"
if exist "dist\%APPNAME%-%APPVER%.zip" del /q "dist\%APPNAME%-%APPVER%.zip"
%PAUSE%
exit /b 1
