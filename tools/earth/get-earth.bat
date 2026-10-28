@echo off
rem get-earth.bat - makes sure data\earth-2.bin, the real Earth's data, is in
rem place and whole. Written 2026-10-11; earth-2 instead of earth-1 since ET1.
rem
rem The game reads the Earth from data\earth-2.bin in the folder it runs in
rem (EarthData.DATA_FILE). The file is about 282 MB, too large for the
rem repository, so git leaves data\ out and the auto pull never brings it.
rem It is the asset of the GitHub release "earth-2", and this fetches it from
rem there when it is missing or does not match tools\earth\earth-2.sha256.
rem
rem "earth-1" or "world-1" as the second argument fetches that file instead:
rem data\earth-1.bin (the bake's input, bake-earth-2.bat) or data\world-1.bin
rem (the nations' world, WorldData.DATA_FILE, batch WN0; against
rem tools\world\world-1.sha256). Nothing in the game reads world-1.bin yet, so
rem Play CityBuilderSim.bat and Build EXE.bat do not ask for it.
rem
rem Play CityBuilderSim.bat and Build EXE.bat call it before they start; it
rem can be double-clicked too. With nothing to fetch it only checks the
rem file's SHA-256, which takes under a second. Exit code 0 when the file is
rem in place and whole, 1 when it is not: the game still runs then, on the
rem generated world, but Build EXE.bat stops, because a release must carry it.
rem
rem     call "tools\earth\get-earth.bat" nopause      from another script
rem
rem A new Earth is a new file, a new hash file and a new release: change the
rem three names below together with EarthData.DATA_FILE.
setlocal
pushd "%~dp0..\.."
if not exist "tools\earth\get-earth.bat" (
  echo Earth: cannot find the project folder from %~dp0
  popd
  exit /b 1
)
set "FILE=data\earth-2.bin"
set "HASHFILE=tools\earth\earth-2.sha256"
set "URL=https://github.com/2048Israel/CityBuilderSim/releases/download/earth-2/earth-2.bin"
set "TAG=earth-2"
set "SIZE=about 282 MB"
rem "earth-1" as the second argument fetches earth-1.bin instead, the bake's input (bake-earth-2.bat).
if /i "%~2"=="earth-1" (
    set "FILE=data\earth-1.bin"
    set "HASHFILE=tools\earth\earth-1.sha256"
    set "URL=https://github.com/2048Israel/CityBuilderSim/releases/download/earth-1/earth-1.bin"
    set "TAG=earth-1"
    set "SIZE=about 53 MB"
)
rem "world-1" as the second argument fetches world-1.bin, the nations' world (WN0), against tools\world\world-1.sha256.
if /i "%~2"=="world-1" (
    set "FILE=data\world-1.bin"
    set "HASHFILE=tools\world\world-1.sha256"
    set "URL=https://github.com/2048Israel/CityBuilderSim/releases/download/world-1/world-1.bin"
    set "TAG=world-1"
    set "SIZE=the nations' world"
)
set "PART=%FILE%.download"
set "RC=1"

if not exist "%HASHFILE%" (
  echo Earth: %HASHFILE% is missing - this copy is older than the Earth.
  goto done
)
for /f "usebackq tokens=1" %%h in ("%HASHFILE%") do set "WANT=%%h"

if not exist "%FILE%" goto fetch
call :hash "%FILE%"
if /i "%GOT%"=="%WANT%" (
  echo Earth: %FILE% is in place.
  set "RC=0"
  goto done
)
echo Earth: %FILE% does not match %HASHFILE%, so it is fetched again.

:fetch
echo Earth: downloading %FILE% from GitHub (%SIZE%)...
if not exist data md data
if exist "%PART%" del /q "%PART%"
set "GOTIT="
where curl >nul 2>&1
if errorlevel 1 goto powershell
curl -L --fail --retry 3 --retry-delay 5 --progress-bar -o "%PART%" "%URL%"
if not errorlevel 1 set "GOTIT=1"
if defined GOTIT goto downloaded
:powershell
if exist "%PART%" del /q "%PART%"
powershell -NoProfile -Command "$ProgressPreference = 'SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol = 'Tls12'; try { Invoke-WebRequest -UseBasicParsing -Uri $env:URL -OutFile $env:PART } catch { exit 1 }"
if not errorlevel 1 set "GOTIT=1"
:downloaded
if not defined GOTIT goto nodownload
if not exist "%PART%" goto nodownload

call :hash "%PART%"
if /i not "%GOT%"=="%WANT%" (
  echo Earth: the download does not match %HASHFILE%, so it was thrown away.
  del /q "%PART%"
  goto done
)
move /y "%PART%" "%FILE%" >nul
if errorlevel 1 (
  echo Earth: could not move the download to %FILE% - is the game open?
  goto done
)
echo Earth: %FILE% is in place.
set "RC=0"
goto done

:nodownload
if exist "%PART%" del /q "%PART%"
echo Earth: could not download it. Check the connection, or put %TAG%.bin
echo in the data folder by hand from
echo     https://github.com/2048Israel/CityBuilderSim/releases/tag/%TAG%

:done
popd
if /i not "%~1"=="nopause" (
  echo.
  pause
)
exit /b %RC%

rem Sets GOT to the SHA-256 of the file named, in lower-case hex, no spaces.
:hash
set "GOT="
for /f "skip=1 delims=" %%h in ('certutil -hashfile "%~1" SHA256') do if not defined GOT set "GOT=%%h"
if defined GOT set "GOT=%GOT: =%"
exit /b 0
