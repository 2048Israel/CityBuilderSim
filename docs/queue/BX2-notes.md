# BX2 — PackagingCheck reads BX1's zip line: notes

Builder: the orchestrator (Claude Opus, conversation 89ee5cf7), 2026-11-25, on `batch/BX2` from
origin/main 4183928ca (BX1 over 0.9.204).

## What was found

R1g's ALL dry run (cbs-gate-R1g-PcIeOrFf, box, 16:18 UTC) stopped on an unnamed PackagingCheck red:
"FAIL the image is staged and checked after jpackage and before the zip". PackagingCheck found the
zip step with `bat.indexOf("tar -a -c")`. BX1 changed that line to
`"%SystemRoot%\System32\tar.exe" -a -c -f "%ZIP%" -C dist "%APPNAME%" 2>nul`, so the index was -1
and the order assertion failed. BX1's gate passed because `tools/pick.sh` maps no harness to
`Build EXE.bat` (a file outside src reached only the "N" line), so its pick ran no PackagingCheck.
Every ALL dry run on main since 4183928ca carried this red.

## What was changed

- `PackagingCheck`: the zip is found by its full line, `"%SystemRoot%\System32\tar.exe" -a -c -f "%ZIP%"`.
  A new assertion, "the zip is made by Windows' own tar, never a tar found on PATH (BX1)", holds
  that line present and no line starting with a bare `tar`. That is BX1's effect, now asserted.
- `tools/pick.sh`: a change to `Build EXE.bat` picks PackagingCheck.

## The checks

See the run below.
