# CR1-J — source archives beside the exe and the OpenJDK runtime

Built from origin/main 873de9427 (0.9.143), on batch/CR1-J. Jerus,
2026-11-17: 20:23 "Go for b" (the complete corresponding sources ship beside
the exe), 20:27 "Go ahead install what you need" (Eclipse Temurin 21.0.13+9 at
C:/Users/Jerus/Tools/jdk-21-temurin, zip SHA-256 a4dbaa05…04f9; Oracle's JDK
and the PATH untouched).

## What was read

The brief (docs/queue/CR1-J.md) and CR1-L's notes; Build EXE.bat; Credits and
CreditsCheck; the 0.9.1 image in steam-build (Oracle 21.0.4, NFTC); Temurin's
release file and legal/; javafx-graphics-21-win.jar's entries; Microsoft's
[Visual C++ 2015-2022 runtime licence](https://visualstudio.microsoft.com/wp-content/uploads/2021/09/Visual-C-Runtime-2015-2022-License-1.docx),
the [VS 2022 Community licence](https://visualstudio.microsoft.com/license-terms/vs2022-ga-community/)
("Distributable Code"), [its redistribution list](https://learn.microsoft.com/en-us/visualstudio/releases/2022/redistribution)
and [Redistributing Visual C++ files](https://learn.microsoft.com/en-us/cpp/windows/redistributing-visual-cpp-files)
(the UCRT under the Windows SDK's terms).

## What was built and why

- ★ **The runtime is Temurin's, named, never searched for.** Build EXE.bat
  takes `%USERPROFILE%\Tools\jdk-21-temurin` (CBS_PACKAGING_JDK overrides),
  stops unless its release file says IMPLEMENTOR="Eclipse Adoptium" and Java
  21 and its java, jlink and jpackage exist, then `tools.Packaging jdk` holds
  IMPLEMENTOR, IMPLEMENTOR_VERSION and JAVA_VERSION to the source pins
  exactly: a Temurin update stops the build until its source archive is
  pinned. PATH and the Oracle fallback are gone.
- ★ **jlink is the bat's own step**, with jpackage's default options (strip
  native commands and debug, no man pages, no headers), and jpackage takes
  it by `--runtime-image`: the runtime is a folder the build can check, and
  the JDK's NOTICE (Temurin's own, which jlink leaves out) is copied in as
  runtime/NOTICE. runtime/legal is Temurin's for the linked modules, whole.
- ★ **The source archives** are pinned in tools/credits/source-archives.properties
  (file, version, URL, SHA-256), which the jar carries, so the jar knows what
  ships beside it: OpenJFX's openjdk/jfx tag 21+31 (60,377,702 bytes:
  modules/ with the native code, buildSrc, build.gradle, gradle) and
  Adoptium's OpenJDK21U-jdk-sources_21.0.13_9.tar.gz (115,231,532 bytes:
  src/, make/, configure, Makefile). `Packaging fetch` downloads each once
  into `%USERPROFILE%\Tools\CityBuilderSim-sources` (CBS_SOURCE_CACHE
  overrides), verifies it, and never commits it; `Packaging stage` copies them
  to dist\CityBuilderSim\source with README.txt (Credits.sourceReadme, from
  the pins). OpenJFX's tag is the build Maven ships: VersionInfo in the
  classpath jar says 21+31, which PackagingCheck and every build check.
- **Nothing ships short.** `Packaging fetch` stops the build before jpackage
  when an archive cannot be had or its hash differs; after the copies,
  `Packaging verify` checks the image (runtime vendor and version, NOTICE,
  legal/ whole with its references, GPL v2 not NFTC, every Microsoft DLL
  named by the notice, the notices beside the exe equal to the jar's, both
  archives present and pinned, the README, one jar, OpenJFX's build); any
  problem deletes dist\CityBuilderSim and makes no zip.
- ★ **The runtime's notices on Credits** come from the image the game runs
  on: Credits.runtimeLegal reads runtime/NOTICE, then legal/java.base first
  and the rest by path, resolving jlink's "Please see ..\java.base\LICENSE"
  stubs inside legal/. Read once in CR1-L's Loaded holder, so later visits
  read nothing. A dangling or outside reference or no legal/ marks the
  section missing. Run from a full JDK (NetBeans, mvn) it shows that JDK's
  legal, which is only ever a developer's.
- ★ **Microsoft's libraries** carry their terms whole: the licence as
  tools/credits/microsoft/LICENSE-vc-runtime-2015-2022.txt (Word's text of
  Microsoft's docx, provenance in sources.properties) and our NOTICE.txt naming
  the files, where they come from and Microsoft's copyright; in the jar,
  beside the exe in legal\microsoft, and on Credits.
- ★ Written by Opus, not slices: the code and the licence research were one
  piece, each check settling what the next file had to say.

## Microsoft's terms for the exact binaries

The binaries, each validly Authenticode-signed by Microsoft Corporation
(checked on all 96): javafx-graphics-21-win.jar carries 47 (msvcp140,
msvcp140_1, msvcp140_2, vcruntime140, vcruntime140_1 at 14.34.31938.0,
ucrtbase 10.0.17763.132, 41 api-ms-win-*); Temurin's bin 49 (msvcp140,
vcruntime140, vcruntime140_1 at 14.40.33810.0, ucrtbase 10.0.26100.1742, 45
api-ms-win-*). No concrt, vccorlib, vcomp or MFC file anywhere.

- The VC++ files are the Visual C++ 2015-2022 redistributable (14.3x is VS
  2022), "Distributable Code" under the VS 2022 licence's REDIST list; the
  UCRT files are the Windows SDK's redistributable UCRT. Both licences let a
  developer redistribute them, in a sold program too, on conditions:
  1. the program adds significant primary functionality: a game does;
  2. distributors and end users agree to terms that protect the code at
     least as much as Microsoft's: open, the EULA call below (CR1-J-A);
  3. a valid copyright notice: legal\microsoft\NOTICE.txt and Credits;
  4. indemnify Microsoft against claims about the program: the publisher's
     obligation by distributing, nothing a build can do;
  5. no Microsoft trademark in the program's name, no notice altered, the
     files not made subject to an Excluded Licence: the name is Civic Ledger,
     the files ship unmodified (the signatures hold), and binaries beside
     GPL code are not relicensed under it (Adoptium and Oracle's OpenJFX
     builds ship them so).
- So the terms allow the sold game; nothing stops for the orchestrator. The
  end-user licence for these exact files is Microsoft's runtime licence
  (EULA ID Cpp_2015-2022_ENU.1033), shipped whole. PackagingCheck and every
  build fail on a Microsoft DLL the notice does not name (concrt140 in a
  fixture), so a new one waits on an audit.


## The packaged image

`Build EXE.bat nopause` in this worktree (at a00f98e87, before the zip
removal on an unfit stop), 2026-11-18 01:18, exit 0: dist\CityBuilderSim and
dist\CityBuilderSim-0.9.143.zip (600,852,480 bytes). Packaging fetch
downloaded jfx-21+31.tar.gz from GitHub and verified it; Temurin's archive
was already in the cache.

runtime\release (jlink keeps these two keys only, so verify reads the
vendor from java.base's VersionProps: Eclipse Adoptium, Temurin-21.0.13+9):

    JAVA_VERSION="21.0.13"
    MODULES="java.base java.datatransfer java.xml java.prefs java.desktop java.logging java.scripting java.transaction.xa java.sql jdk.jfr jdk.localedata jdk.unsupported"

runtime\NOTICE is Temurin's "Notices for Eclipse Temurin"; runtime\legal
holds 61 files in 12 module folders.

source\: OpenJDK21U-jdk-sources_21.0.13_9.tar.gz (115,231,532 bytes),
jfx-21+31.tar.gz (60,377,702) and README.txt (1,173). legal\: gson\LICENSE;
microsoft\NOTICE.txt and LICENSE-vc-runtime-2015-2022.txt; openjfx\LICENSE,
ADDITIONAL_LICENSE_INFO, ASSEMBLY_EXCEPTION, jpeg_fx.md, mesa3d.md;
source-archives.properties and sources.properties.

The Credits page over this image (Credits.sections on its runtime, run from
a scratch class outside the repository):

| section | text | beside the exe |
|---|---|---|
| The Earth | 6,817 chars | data/earth-2-ATTRIBUTION.txt |
| The nations | 19,677 | data/world-ATTRIBUTION.txt |
| The fonts | 4,362 | |
| Gson | 11,358 | |
| OpenJFX | 30,885 | |
| The Java runtime | 209,113 (4,400 lines, 62 files) | runtime/legal |
| Microsoft's runtime libraries | 7,861 | legal/microsoft |
| The source code | 1,173 | source/README.txt |

**Smoke run.** The exe ran from PowerShell's Start-Process, never through
computer use, with APPDATA and user.home on target\cr1j-smoke. Its log says
"Civic Ledger 0.9.143 started", "Java 21.0.13 on Windows 11" and "Saves:
…\target\cr1j-smoke\appdata\CityBuilderSim", and the fonts and icon
loaded. OpenJFX unpacked 50 files, 47 of them Microsoft's, into the scratch
home's .openjfx\cache\21+31. After 40 s both pids were stopped. The real
%APPDATA%\CityBuilderSim had 731 files before and after, with 0 differences.

**Missing source, the real bat.** CBS_SOURCE_CACHE pointed at a folder
holding a wrong jfx-21+31.tar.gz and no Temurin archive, and Java's HTTPS
went to 127.0.0.1:9. The bat printed:
- "jfx-21+31.tar.gz differs from its pin, so it is fetched again";
- "could not be fetched (java.net.ConnectException)" for both archives;
- "Packaging fetch: 2 problems." and "No good copy of every source
  archive, so no build".

It ended with BUILD EXIT 1, no dist folder and no zip.

Packaging verify on a copy of the real image without
source\jfx-21+31.tar.gz and legal\microsoft\NOTICE.txt reported "2
problems" and exit 1. From there, the bat's unfit stop removes the image
and this version's zip, and PackagingCheck holds that wiring.


## Commercial use, piece by piece

| piece | terms | sold game |
|---|---|---|
| Temurin 21.0.13+9 runtime | GPL v2 with the Classpath Exception; its third-party notices in runtime/legal | yes, the source beside it |
| OpenJFX 21+31 | GPL v2 with the Classpath Exception | yes, the source beside it |
| Gson 2.10.1 | Apache 2.0 | yes |
| the notices in runtime/legal (zlib, libpng, FreeType, harfbuzz, …) | permissive (each shown whole) | yes |
| Microsoft VC++ and UCRT DLLs | Distributable Code, VS 2022 and Windows SDK licences | yes, on their conditions (above) |
| the two source archives | GPL v2 (+CE), their own notices inside | yes, we are obliged to |
| our NOTICE.txt and the README | ours | yes |

## Steam (C:\Users\Jerus\steam-build, nothing changed there)

scripts\app_build_5571850.vdf points ContentRoot at content-0.9.1\CityBuilderSim,
a copy of the itch.io 0.9.1 build on Oracle's 21.0.4 runtime (NFTC). For the
next upload, Jerus or the orchestrator: copy this build's
dist\CityBuilderSim (or unzip its release zip) to
`C:\Users\Jerus\steam-build\content-<version>\CityBuilderSim`, set ContentRoot to
that folder and Desc to the version, and run Upload to Steam.bat (whose
header comment names 0.9.1 too). FileMapping "*" recursive then ships
source\, legal\ and the Temurin runtime unchanged; the depot grows by about
175 MB. Do not upload content-0.9.1 again: it is the NFTC runtime.

## Checks

- `./batch-check.sh CreditsCheck PackagingCheck`: exit 0; CreditsCheck 142
  ok, PackagingCheck 84 ok. `./batch-check.sh --fast`: exit 0, 0 firm Stale
  findings.
- `sh tools/pick.sh origin/main HEAD`: ALL 219 harnesses (pom.xml changed),
  with "review needed - threading: ui/UserInterface.java (UI-wide)". The only
  change in UserInterface is the Credits page's intro string.
- No model change, so the default trace does not move and there is no
  ensemble.
- Not a process batch. Build EXE.bat is the product's packaging, not a
  session's machinery, and no rule a session reads changed.
- A slip: the two named harnesses, with compile and maps, ran twice around
  00:50 while DM1-P's dry run (00:28 to 01:17) was going. The brief allowed
  only `--fast` then. The build, the smoke run and the rerun waited for it
  to end.
- ★ OpenJFX publishes its source only as the tag. GitHub's tag tarball is
  generated, and GitHub changed such bytes once (2023, reverted). If it
  changes again, the pin stops the build rather than ship an unchecked
  archive. The cache keeps the verified copy, and the cure is to re-pin
  after comparing the tree, or to attach the verified copy to a release as
  earth-2.bin is.
- Adoptium designates OpenJDK21U-jdk-sources_21.0.13_9.tar.gz as the source
  of this binary (src/, make/, configure: the code, the interfaces and the
  scripts that build it). Its build wrapper is public at
  adoptium/temurin-build.


## Calls left for Jerus

- CR1-J-A, a player EULA: Microsoft's Distributable Code terms ask that
  end users and distributors agree to terms that protect the DLLs at least as
  much as Microsoft's, and that the publisher indemnify Microsoft. The terms
  ship in the image and on Credits now; neither store shows them as an
  agreement. Recommended: Steam's custom EULA field (and the itch.io page)
  carrying a short EULA that incorporates the files in legal\ by reference.
