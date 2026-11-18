<!-- readiness: provisional -->
## changelog
### {next} — CR1-J, source archives beside the exe and the OpenJDK runtime — 2026-11-18, ready, see docs/queue/CR1-J-notes.md
The sold exe now runs on Eclipse Temurin 21.0.13+9, an OpenJDK build under
the GPL with the Classpath Exception, not Oracle's runtime, whose No-Fee
Terms bar a sold product. Build EXE.bat names that JDK and checks it, and
ships the complete source of OpenJFX 21+31 and of the runtime beside the exe
in source\, pinned by SHA-256 and fetched once into a cache outside git. The
Credits page adds the runtime's notices, read from the image the game runs
on, Microsoft's terms for its C and C++ runtime DLLs, and where the source
is. A missing notice, terms file or archive stops the build with nothing
published (PackagingCheck).
## pending
| CR1-J-A | a player EULA carrying Microsoft's Distributable Code conditions ({next}) | Microsoft's terms ask that end users agree to terms protecting its DLLs at least as much as its own; the terms ship in the image and on Credits, but neither store shows them as an agreement. Recommended: Steam's custom EULA field and the itch.io page, incorporating the files in legal\ by reference | docs/queue/CR1-J-notes.md |
