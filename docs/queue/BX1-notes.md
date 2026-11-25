# BX1 — Build EXE.bat's zip is a zip from any shell: notes

Builder: Claude (Opus), a desktop session, 2026-11-25, on `batch/BX1` from
origin/main ee0384007 (0.9.204). Jerus asked for 0.9.204 on itch.io, then
"Yes, fix it" for the fault that push found.

## What was found

Pushing 0.9.204 to itch.io (IR1/IR2's steps, a temp clone at the tag),
`Build EXE.bat nopause` was called through cmd from Git Bash. Its zip step,
`tar -a -c -f "%ZIP%" -C dist "%APPNAME%"`, found Git's `/usr/bin/tar` first
on the PATH cmd inherited. GNU tar takes `-a` (auto-compress) from the
suffix, knows no `.zip`, and writes a plain tar with exit 0, so the
`Compress-Archive` fallback never ran and the script printed "upload this one
as the release". butler: `zip: not a valid zip file`. A double-click (Jerus's
way) finds `C:\Windows\System32\tar.exe` (bsdtar) and was never affected.

## What changed

One line and its WHY: the zip step calls `"%SystemRoot%\System32\tar.exe"`.
The fallback is unchanged. The file stays CRLF (`git ls-files --eol`).

## Checks

- The whole script from Git Bash (`type tar` said `/usr/bin/tar`), in this
  worktree at 184355f7d with main's data files copied in: exit 0, and
  `CityBuilderSim-0.9.204.zip` is 524,738,892 bytes, 217 entries,
  `zipfile.testzip()` clean.
- `./batch-check.sh --fast`: exit 0. `tools/pick.sh`: NONE of 254 harnesses
  read the file; no review owed (no money, saves or threading); no dry run
  owed (the pick is not ALL and the publisher is untouched).

## For Jerus

Nothing. 0.9.204 is live on itch.io (`windows`, build #2497681), zipped by
hand with Windows' tar after the failure above.
