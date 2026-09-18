@echo off
setlocal

rem ---------------------------------------------------------------------------
rem  The docs pass, headless: a FRESH Claude Code process reads the diff of the
rem  last commit and makes every comment, header, index and count agree with
rem  the code again. See docs\docs-pass.md for what it does and does not do.
rem
rem      Docs pass.bat                    - the last commit, no design note
rem      Docs pass.bat notes\the-cars.md  - the last commit, with its design note
rem
rem  Needs Claude Code on this machine (npm install -g @anthropic-ai/claude-code,
rem  then "claude" once to log in with the same account as the app). Commit the
rem  batch first: the pass reads what git says changed. It cannot ask questions,
rem  so anything it is unsure of becomes a TODO(docs) in the comment and a line
rem  in its report. Read the report; then commit what it did as its own commit.
rem ---------------------------------------------------------------------------

cd /d "%~dp0"

where claude >nul 2>nul
if errorlevel 1 (
    echo.
    echo   Claude Code is not on the PATH. Install it with
    echo       npm install -g @anthropic-ai/claude-code
    echo   run "claude" once to log in, and try again.
    echo.
    pause
    exit /b 1
)

if not exist "target\classes\ham\citybuildersim\tools\Maps.class" (
    echo.
    echo   Build first ^(NetBeans: Clean and Build^) so the pass can regenerate docs\.
    echo.
    pause
    exit /b 1
)

set "NOTE=%~1"
if "%NOTE%"=="" (
    set "NOTEPART=There is no design note for this batch; say so in the report and do the mechanical parts."
) else (
    set "NOTEPART=The design note for this batch is %NOTE%; read it first."
)

git diff HEAD~1 --stat
echo.
echo   Running the docs pass on the diff above. This takes a few minutes.
echo.

git diff HEAD~1 | claude -p "You are the docs pass for this repository. Read docs\docs-pass.md in full and follow it in order. The diff of the last commit is on stdin; git diff --name-only HEAD~1 lists the files. %NOTEPART% End with the report the brief asks for." --permission-mode acceptEdits --allowedTools "Read,Edit,Write,Glob,Grep,Bash(git diff *),Bash(git log *),Bash(git status *),Bash(java *),Bash(Regenerate maps.bat)"

set "CODE=%ERRORLEVEL%"
echo.
if not "%CODE%"=="0" (
    echo   The pass exited with code %CODE% - the output is above.
) else (
    echo   Done. Read the report above, look at "git diff", and commit the docs as their own commit.
)
echo.
pause
