# CR1-J — source archives, an OpenJDK runtime and the remaining notices

Queued from CR1-L by the orchestrator's review decision of 2026-11-17.
The inventory and release provenance are in [CR1-L's notes](CR1-L-notes.md).

The Credits page carries OpenJFX's licence and graphics notices. Distribution
also needs the corresponding-source provision settled: the Classpath
Exception does not waive [GPL v2 section 3](https://github.com/openjdk/jfx/blob/21%2B31/LICENSE).
The review raised this and the Microsoft native runtime terms as P1 findings;
the orchestrator explicitly deferred both to this batch. CR1-L adds licence
texts; the distribution obligations below remain for CR1-J.

★ **Decided by Jerus, 2026-11-17 at 20:23:** ship corresponding source
archives beside the exe. Have the build produce and stage the complete
corresponding sources of OpenJFX 21 and the chosen OpenJDK runtime, with a
check that they match the shipped binaries and cannot be omitted. Include
the native code, interface definitions, build and installation material
required by GPL v2 section 3. Upstream source links alone are not this decision.

★ **The sold exe moves from Oracle NFTC to an OpenJDK build.** The inspected
image's runtime/legal/java.base/LICENSE states Oracle No-Fee Terms and
Conditions (NFTC). Its redistribution clause at lines 62–66 bars fees for
products bundled with that runtime; the same clause is in
[Oracle's published NFTC terms](https://www.oracle.com/downloads/licenses/no-fee-license.html).
Eclipse Temurin 21 is the orchestrator's recommended distribution. Installing
it on the PC awaits Jerus's word (asked by the orchestrator at 20:26).
Select and verify that JDK explicitly for jpackage, rather than accepting an
unidentified executable from PATH or the current Oracle fallback. CR1-L
does not install a JDK or alter the packaging runtime.

Build the runtime inventory from the image that Build EXE.bat actually ships,
including runtime/legal and its references to other files within that folder.
The inspected Oracle 21.0.4 image has 49 entries across 12 modules. Its
java.base/LICENSE is the NFTC file described above. Identify the replacement
runtime's vendor, version, GPL and third-party terms, and ship its matching
corresponding-source archive and notices with the game.
Do not copy the current packager's legal folder into the jar as a fixed list.

Audit the Microsoft DLLs carried inside javafx-graphics-21-win.jar separately
from the JRE: msvcp140*, vcruntime140*, ucrtbase and api-ms-*. Find the terms
applicable to the exact shipped binaries, verify redistribution conditions
for the sold game, and carry their required terms/notices in the image and
Credits page. Preserve all upstream notices and the jpackage legal folder.

Prove this path in the packaged image, including a missing-source fixture
that makes packaging fail. Notice rendering must preserve the
actual files in full, cope with internal references, and avoid repeated archive
or disk reads on later Credits visits. Keep a repeatable build-time check that
stops publication if any required notice, terms or source archive is absent.
