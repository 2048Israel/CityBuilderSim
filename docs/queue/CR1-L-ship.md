<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CR1-L-20261118T0339 -->
## changelog
### {next} — CR1-L, the libraries' notices on the Credits page — 2026-11-18, ready, see docs/queue/CR1-L-notes.md
The Credits page now shows Gson's Apache 2.0 licence and OpenJFX's GPL v2
with the Classpath Exception, its additional notices and the graphics
module's JPEG and Mesa notices, after the fonts. These releases omit legal
files from their jars, so the build packages one exact upstream release
copy of each; CreditsCheck pins their versions, hashes and full page text.
Runtime notices, source delivery and the Microsoft DLL terms await CR1-J.

## queued
| CR1-J | Build and check corresponding-source archives beside the exe for OpenJFX 21 and the OpenJDK runtime (Jerus chose this); move packaging from Oracle NFTC to OpenJDK (Temurin 21 recommended, PC installation awaiting Jerus); expose runtime notices and include Microsoft's DLL terms | docs/queue/CR1-J.md |
