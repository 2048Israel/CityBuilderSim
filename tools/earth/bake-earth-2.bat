@echo off
rem bake-earth-2.bat - bakes data\earth-2.bin, the Earth 2 study's terrain and sea depth
rem (docs\studies\spec-earth2-terrain.md), on this PC. Written 2026-10-12.
rem
rem Double-click it. It needs Anaconda (or any Python 3.10+ with numpy and scipy) and
rem about 12 GB free on this drive: GEBCO 2026 (7.5 GB, kept in data\earth2-work\dl
rem for the bake) and the per-tile results (about 3 GB). The 1" heights - NASADEM,
rem about 130 GB (est.) over 14,520 tiles - are streamed and deleted tile by tile, so
rem they never take more than a few hundred MB at once.
rem
rem How long (est.): the download is most of it - 140 GB in all; at 100 Mbit/s about
rem 3 hours, at 500 Mbit/s under an hour - and the arithmetic about 1.2 hours of one
rem core, shared over CBS_PROCS processes below. Each stage skips work already done,
rem so if it stops (a dropped connection, the PC asleep) double-click it again.
rem
rem It runs, in order (each is a Python script beside this file):
rem   get-earth.bat     (with earth-1) data\earth-1.bin, whose layers earth-2 copies
rem   slope_tiles.py    every NASADEM tile: heights to 90 m, the slope, its classes per 15" cell
rem   landmass.py       GEBCO 2026 (downloaded here once, 7.5 GB); every 15" land cell's landmass
rem                     (ground cut off by more than a 1,020 m crossing of fresh water or shallow sea)
rem   bake2.py          the shallow sea and depth; writes earth-2.bin
rem   check2.py         earth-1's layers byte for byte, the new layers' rules, Halifax, Medellin, Everest
rem The result: data\earth-2.bin and data\earth-2.sha256, and data\earth2-work\out\check2-report.txt.
rem For the release "earth-2": upload earth-2.bin, with tools\earth\ATTRIBUTION-2.txt beside it.
setlocal
pushd "%~dp0..\.."
if not exist "tools\earth\bake2.py" (
  echo Earth 2: cannot find the project folder from %~dp0
  goto fail
)

rem ---- Python: Anaconda's, wherever it was installed
set "PY="
for %%d in ("%USERPROFILE%\anaconda3" "%USERPROFILE%\Anaconda3" "%LOCALAPPDATA%\anaconda3" "C:\ProgramData\anaconda3" "C:\ProgramData\Anaconda3" "%USERPROFILE%\miniconda3") do (
  if not defined PY if exist "%%~d\python.exe" set "PY=%%~d\python.exe"
)
if not defined PY (
  where python >nul 2>&1 && set "PY=python"
)
if not defined PY (
  echo Earth 2: no Python found. Install Anaconda, or put python on the PATH.
  goto fail
)
echo Earth 2: using %PY%
"%PY%" -m pip install --quiet --user tifffile imagecodecs
if errorlevel 1 echo Earth 2: pip could not install tifffile/imagecodecs - continuing, they may be there already.

rem ---- where it works: data\ is git-ignored
set "CBS_E2=%CD%\data\earth2-work"
set "CBS_EARTH1=%CD%\data\earth-1.bin"
set "CBS_PROCS=4"
if not exist "%CBS_E2%" md "%CBS_E2%"

call "tools\earth\get-earth.bat" nopause earth-1
if errorlevel 1 goto fail

echo Earth 2: [1/4] the slope, tile by tile (the long part; it says how far it is every 100 tiles)
"%PY%" -I "tools\earth\slope_tiles.py" 12
if errorlevel 1 goto fail
echo Earth 2: [2/4] GEBCO and the landmasses
"%PY%" -I "tools\earth\landmass.py"
if errorlevel 1 goto fail
echo Earth 2: [3/4] the sea's depth, and earth-2.bin
"%PY%" -I "tools\earth\bake2.py"
if errorlevel 1 goto fail
echo Earth 2: [4/4] the checks
"%PY%" -I "tools\earth\check2.py"
if errorlevel 1 goto fail
copy /y "%CBS_E2%\out\earth-2.bin" "data\earth-2.bin" >nul
copy /y "%CBS_E2%\out\earth-2.sha256" "data\earth-2.sha256" >nul
echo.
echo Earth 2: done. data\earth-2.bin and data\earth-2.sha256 are in place; the checks are in
echo %CBS_E2%\out\check2-report.txt
popd
pause
exit /b 0

:fail
echo.
echo Earth 2: stopped. Double-click it again to carry on where it stopped; the logs are in
echo data\earth2-work\log.
popd
pause
exit /b 1
