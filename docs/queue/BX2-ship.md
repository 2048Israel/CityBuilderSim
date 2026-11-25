<!-- readiness: provisional -->
## changelog
### BX2 — PackagingCheck reads BX1's zip line — 2026-11-25, ready, see docs/queue/BX2-notes.md
BX1 named Windows' own `tar.exe` by its full path in `Build EXE.bat`, and PackagingCheck still found the zip
by `tar -a -c`, so main was red on "the image is staged and checked after jpackage and before the zip" from
4183928ca on. BX1's gate never ran PackagingCheck: `tools/pick.sh` tied no harness to the bat, and R1g's dry
run found the red. The check now finds the zip by its full line and asserts that the zip is made by Windows'
own tar, never a bare `tar` from PATH. The pick runs PackagingCheck when the bat changes. Untagged: the game
is unchanged.
