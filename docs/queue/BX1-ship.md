## changelog
### BX1 — Build EXE.bat's zip is a zip from any shell — 2026-11-25, ready, see docs/queue/BX1-notes.md
`Build EXE.bat` zips the game with Windows' own `tar.exe`, named by its full path. Called from Git Bash, a bare
`tar` was Git's GNU tar, which ignores `-a`: 0.9.204's `CityBuilderSim-0.9.204.zip` came out a plain tar
(603,791,360 bytes, a whole number of 10,240-byte records, zeros at the end) with exit 0, so the
Compress-Archive fallback never ran, and butler refused it ("zip: not a valid zip file"). The release went up
re-zipped by hand (itch.io, channel `windows`, build #2497681). Untagged: the game is unchanged.
