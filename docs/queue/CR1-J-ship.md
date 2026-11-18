<!-- gate-run: /tmp/cbs-gate-CR1-J-Oo6lVTc9 -->
## changelog
### {next} — CR1-J, source archives beside the exe and the OpenJDK runtime — 2026-11-18, ready, see docs/queue/CR1-J-notes.md
The sold exe now runs on Eclipse Temurin 21.0.13+9, an OpenJDK build under
the GPL with the Classpath Exception, not Oracle's runtime, whose No-Fee
Terms bar a sold product. Build EXE.bat names that JDK and checks it, and
ships beside the exe, in source\, the complete source of OpenJFX 21+31 and
of the runtime with the scripts that built it, pinned by SHA-256 and fetched
once into a cache outside git. The exe and Credits carry the game's own
copyright line; Credits adds the runtime's notices, read from the image the
game runs on, Microsoft's terms for its C and C++ runtime DLLs (the Visual
C++ and Windows SDK licences), and where the source is. A missing notice,
terms file or archive stops the build with nothing published (PackagingCheck).
## pending
| CR1-J-A | a player EULA carrying Microsoft's Distributable Code conditions, and the name on the copyright line ({next}) | Microsoft's terms (VS 2022 4.b.ii, the Windows SDK's ii) ask that end users agree to terms protecting its DLLs at least as much as its own and that the publisher indemnify Microsoft; the terms ship in the image and on Credits, but neither store shows them as an agreement. Recommended: Steam's custom EULA field and the itch.io page, incorporating the files in legal\ by reference, Jerus treated as a licensee (he holds the VS and SDK licences) rather than redistributing Adoptium's and Gluon's copies under 4.a.iv, the conditions being the same. The line reads "Copyright (c) 2026 Jerus" (Credits.COPYRIGHT): his legal name or the publisher instead is his call | docs/queue/CR1-J-notes.md |
| CR1-J-S | Steam and itch.io onto a CR1-J build ({next}) | steam-build's app_build_5571850.vdf and Upload to Steam.bat point at content-0.9.1 (Oracle's NFTC runtime, no source\), and the live itch.io 0.9.1 download is the same build. Steps: copy a CR1-J build to steam-build\content-<version>\CityBuilderSim; run Packaging verify on that copy with Temurin's java before upload; point the vdf's ContentRoot and Desc at it, mend the bat's header and upload; replace the itch.io download with the same zip. Never upload content-0.9.1 again | docs/queue/CR1-J-notes.md |
