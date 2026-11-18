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
(the UCRT under the Windows SDK's terms); the Windows SDK's licence as the
SDK 10.0.26100 on this PC installs it (Windows Kits\10\Licenses\10.0.26100.0\
sdk_license.rtf, EULA ID WIN10SDK.RTM.AUG_2018_en-US); Temurin's release
file's BUILD_SOURCE and adoptium/temurin-build at that commit.

## Review mends (REV-CR1-J-1 on 34ffba077)

1. Condition 3 asks for **our** copyright notice, not Microsoft's: the exe now
   says "Copyright (c) 2026 Jerus" (jpackage --copyright; LegalCopyright in
   its version info), and Credits opens with a section "Civic Ledger" giving
   it. "Jerus" is the name the tree and the itch.io exe (CompanyName) show;
   the legal name is CR1-J-A's call.
2. CreditsCheck's DLL check skips when no javafx-graphics-*-win.jar is on the
   classpath (Linux), as PackagingCheck's does.
3. NOTICE.txt now says per DLL: the five VC++ files are VS 2022 Distributable
   Code (end use: the VC++ runtime licence); ucrtbase and api-ms-win-* are the
   Windows SDK's Distributable Code, whose licence is carried whole
   (legal\microsoft\LICENSE-windows-sdk.txt, on Credits, pinned).
4. `Packaging.pinProblems` ties every archive's file and URL, the vendor
   version and the Java version to runtime.version; verify and the jdk step
   fail on a version-only bump (PackagingCheck's 21.0.14+7 fixture).
5. temurin-build at Temurin's BUILD_SOURCE 1cf43d7a is pinned and ships in
   source\; the jdk step holds BUILD_SOURCE and BUILD_SOURCE_REPO to the pin;
   source\README.txt gives our jlink command (runtime.jlink, which
   PackagingCheck holds to the bat's line).
6. Build EXE.bat: every check is `if not "%ERRORLEVEL%"=="0"`, every failure
   from jlink on goes to :unfit, which removes dist\CityBuilderSim and this
   version's zip; PackagingCheck asserts no `if errorlevel` and no
   `exit /b 1` between the runtime step and :unfit.
7. Pending row CR1-J-S (Steam and itch.io still on 0.9.1, NFTC).
8. Credits' BOM test is `"﻿"`; the legal walk catches
   UncheckedIOException too.

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
  src/, make/, configure, Makefile), and since the review adoptium/
  temurin-build at the release's BUILD_SOURCE commit 1cf43d7a (625,385 bytes,
  Apache 2.0: the scripts that configured and built that JDK). `Packaging
  pinProblems` holds every file name, URL and the vendor version to
  runtime.version, so a version-only bump stops. `Packaging fetch` downloads each once
  into `%USERPROFILE%\Tools\CityBuilderSim-sources` (CBS_SOURCE_CACHE
  overrides), verifies it, and never commits it; `Packaging stage` copies them
  to dist\CityBuilderSim\source with README.txt (Credits.sourceReadme, from
  the pins). OpenJFX's tag is the build Maven ships: VersionInfo in the
  classpath jar says 21+31, which PackagingCheck and every build check.
- **Nothing ships short.** `Packaging fetch` stops the build before jpackage
  when an archive cannot be had or its hash differs; after the copies,
  `Packaging verify` checks the image (runtime vendor and version, NOTICE,
  legal/ whole with its references, GPL v2 not NFTC, every Microsoft DLL
  named by the notice, the notices beside the exe equal to the jar's, every
  archives present and pinned, the README, one jar, OpenJFX's build); any
  problem deletes dist\CityBuilderSim and makes no zip.
- ★ **The runtime's notices on Credits** come from the image the game runs
  on: Credits.runtimeLegal reads runtime/NOTICE, then legal/java.base first
  and the rest by path, resolving jlink's "Please see ..\java.base\LICENSE"
  stubs inside legal/. Read once in CR1-L's Loaded holder, so later visits
  read nothing. A dangling or outside reference or no legal/ marks the
  section missing. Run from a full JDK (NetBeans, mvn) it shows that JDK's
  legal, which is only ever a developer's.
- ★ **Microsoft's libraries** carry their terms whole: the VC++ runtime
  licence as tools/credits/microsoft/LICENSE-vc-runtime-2015-2022.txt (Word's
  text of Microsoft's docx), the Windows SDK's as LICENSE-windows-sdk.txt
  (Word's text of the installed SDK's sdk_license.rtf; both with provenance
  and hashes in sources.properties), and our NOTICE.txt naming each file,
  which licence covers it, where it comes from and Microsoft's copyright; in
  the jar, beside the exe in legal\microsoft, and on Credits.
- ★ **The game's own notice**: "Copyright (c) 2026 Jerus" (Credits.COPYRIGHT)
  in the exe's version info by jpackage --copyright, and Credits' first
  section, "Civic Ledger", which says the code, rules, art and words are his
  and the rest is used under the licences that follow.
- ★ Written by Opus, not slices: the code and the licence research were one
  piece, each check settling what the next file had to say.

## Microsoft's terms for the exact binaries

The binaries, each validly Authenticode-signed by Microsoft Corporation
(checked on all 96): javafx-graphics-21-win.jar carries 47 (msvcp140,
msvcp140_1, msvcp140_2, vcruntime140, vcruntime140_1 at 14.34.31938.0,
ucrtbase 10.0.17763.132, 41 api-ms-win-*); Temurin's bin 49 (msvcp140,
vcruntime140, vcruntime140_1 at 14.40.33810.0, ucrtbase 10.0.26100.1742, 45
api-ms-win-*). No concrt, vccorlib, vcomp or MFC file anywhere.

- Per DLL: msvcp140, msvcp140_1, msvcp140_2, vcruntime140 and
  vcruntime140_1 are the Visual C++ 2015-2022 redistributable (14.3x is VS
  2022), "Distributable Code" under the VS 2022 licence's REDIST list, whose
  end-user licence is the VC++ runtime licence (EULA ID
  Cpp_2015-2022_ENU.1033). ucrtbase and the api-ms-win-* forwarders are the
  Universal CRT, the Windows SDK's Distributable Code (its Redist\ucrt
  folder) under the SDK licence (EULA ID WIN10SDK.RTM.AUG_2018_en-US, the
  text the installed SDK 10.0.26100 carries; OpenJFX's ucrtbase
  10.0.17763.132 is the October 2018 SDK's build, whose own licence copy was
  not to be had, and the EULA ID names the 2018 terms). On Windows 10 and
  later Windows loads its own UCRT, so these copies are inert there.
  NOTICE.txt now says this per file, and both licences ship whole.
- Both licences let a developer redistribute them, in a sold program too,
  on conditions:
  1. the program adds significant primary functionality: a game does;
  2. distributors and end users agree to terms that protect the code at
     least as much as Microsoft's (the SDK: "terms that protect it at least
     as much as this agreement"): open, the EULA call below (CR1-J-A);
  3. **our** valid copyright notice on the program (the first round read
     this as Microsoft's notice, wrongly): "Copyright (c) 2026 Jerus" in the
     exe and first on Credits; Microsoft's own notice is kept unaltered in
     NOTICE.txt as condition 5 asks;
  4. indemnify Microsoft against claims about the program: the publisher's
     obligation by distributing, nothing a build can do (CR1-J-A says so);
  5. no Microsoft trademark in the program's name, no notice altered, the
     files not made subject to an Excluded Licence: the name is Civic Ledger,
     the files ship unmodified (the signatures hold), and binaries beside
     GPL code are not relicensed under it (Adoptium and Oracle's OpenJFX
     builds ship them so).
- Whose licence: Jerus never took these DLLs from an SDK of his; they come
  inside Adoptium's JDK and Gluon's OpenJFX jar. Either he redistributes as
  a licensee (the SDK 10.0.26100 and VS Build Tools are installed on this PC,
  so he holds both licences) or as a distributor the original developer
  permits (VS 2022 4.a.iv, the SDK's third-party distribution clause). The
  conditions are the same either way, so the build does not depend on the
  answer; CR1-J-A records it.
- So the terms allow the sold game; nothing stops for the orchestrator.
  PackagingCheck and every build fail on a Microsoft DLL the notice does not
  name (concrt140 in a fixture), so a new one waits on an audit.


## The packaged image

The first build: `Build EXE.bat nopause` in this worktree (at a00f98e87, before the zip
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

**The build after the review mends** (ddde3d576, 2026-11-18 01:55, exit 0):
Packaging fetch downloaded temurin-build-1cf43d7a….tar.gz and verified it,
then "Packaging stage: all in place." and "Packaging verify: all in place.".
The exe's version info: CompanyName Jerus, LegalCopyright "Copyright (c)
2026 Jerus", FileVersion 0.9.143. source\: the two archives above,
temurin-build-1cf43d7a3e77886bfea7d550a902e151a87d724d.tar.gz (625,385) and
README.txt (2,136, with the temurin-build entry and the jlink command).
legal\microsoft: NOTICE.txt (1,405), LICENSE-vc-runtime-2015-2022.txt
(6,660), LICENSE-windows-sdk.txt (27,998). The zip: 601,528,320 bytes.
Credits now has nine sections, "Civic Ledger" first (CreditsCheck asserts
the order and the text; the page was not listed again to keep a JVM off
the machine during the nightly).

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
| Microsoft VC++ DLLs (msvcp140*, vcruntime140*) | Distributable Code, VS 2022 licence; end use under the VC++ runtime licence | yes, on its conditions (above) |
| Microsoft UCRT DLLs (ucrtbase, api-ms-win-*) | Distributable Code, Windows SDK licence | yes, on its conditions (above) |
| the JDK and OpenJFX source archives | GPL v2 (+CE), their own notices inside | yes, we are obliged to |
| the temurin-build archive | Apache 2.0 | yes |
| our NOTICE.txt, the README and the game's copyright line | ours | yes |

## Steam and itch.io (nothing changed there; Pending row CR1-J-S)

scripts\app_build_5571850.vdf points ContentRoot at content-0.9.1\CityBuilderSim,
a copy of the itch.io 0.9.1 build on Oracle's 21.0.4 runtime (NFTC, no
source\, LegalCopyright "Copyright (C) 2026"), and Upload to Steam.bat's
header names 0.9.1 too; the live itch.io download is that same 0.9.1 build.
For the next upload, Jerus or the orchestrator:
1. copy a CR1-J build's dist\CityBuilderSim (or unzip its release zip) to
   `C:\Users\Jerus\steam-build\content-<version>\CityBuilderSim`;
2. check that copy before upload, with Temurin's java (the image's runtime
   has no java.exe): `%USERPROFILE%\Tools\jdk-21-temurin\bin\java -cp
   <copy>\app\CityBuilderSim-1.0-SNAPSHOT-executable.jar
   ham.citybuildersim.tools.Packaging verify <copy>` must end "all in place";
3. set the vdf's ContentRoot to that folder and Desc to the version, mend the
   bat's header comment, and run Upload to Steam.bat. FileMapping "*"
   recursive ships source\, legal\ and the Temurin runtime unchanged; the
   depot grows by about 176 MB of source;
4. replace the itch.io download with the same build's zip.

Never upload content-0.9.1 again: its runtime is NFTC.

## Checks

- `./batch-check.sh CreditsCheck PackagingCheck`: exit 0; after the review
  CreditsCheck 154 ok, PackagingCheck 109 ok (first round 142 and 84).
  `./batch-check.sh --fast` after the mends: exit 0, 0 firm Stale findings,
  the maps unchanged.
- `sh tools/pick.sh origin/main HEAD`: ALL 219 harnesses (pom.xml changed),
  with "review needed - threading: ui/UserInterface.java (UI-wide)". The only
  change in UserInterface is the Credits page's intro string (the first
  round; the mends do not touch it).
- REV-CR1-J-1's recheck at 1224741e8: SHIP, each of the eight findings
  mended. origin/main 442ad3935 (DM1-P) merged in at 7ba92dd7d, scripts and
  records only.
- `sh tools/gate.sh CR1-J --dry-run` at 7ba92dd7d, 2026-11-18 02:24 to
  03:03: gate exit 0. The suite ran 2,294 s, exit 3, known red only:
  DeathRecordCheck, DistressBleedCheck, HealthCheck, MapCheck, PlanCheck,
  PopulationCheck, PriceLevelCheck, RoadCheck, ScaleCheck, YoungBankCheck
  (main's, none in this batch's code). No unnamed lines. The run is kept as
  /tmp/cbs-gate-CR1-J-Oo6lVTc9, named on the ship file's first line.
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
  scripts that build it). Its build wrapper, adoptium/temurin-build at the
  release's BUILD_SOURCE commit, ships too since the review; that archive is
  GitHub-generated like OpenJFX's, with the same pin and the same cure.


## Calls left for Jerus

- CR1-J-A, a player EULA and the name on the game:
  - Microsoft's Distributable Code terms (VS 2022 4.b.ii, the SDK's ii) ask
    that end users and distributors agree to terms that protect the DLLs at
    least as much as Microsoft's, and that the publisher indemnify Microsoft.
    The terms ship in the image and on Credits now; neither store shows them
    as an agreement. Recommended: Steam's custom EULA field (and the itch.io
    page) carrying a short EULA that incorporates the files in legal\ by
    reference. The call is real whichever way the next point falls.
  - Whose licence the DLLs come under: Jerus as a licensee (VS Build Tools
    and the Windows SDK 10.0.26100 are installed on this PC), or Jerus
    redistributing Adoptium's and Gluon's copies as a distributor they may
    permit (VS 2022 4.a.iv, the SDK's third-party distribution clause). The
    conditions match, so nothing waits on it. Recommended: treat him as a
    licensee meeting the conditions himself, the stricter reading.
  - The copyright line says "Copyright (c) 2026 Jerus", the name the tree
    and the itch.io exe use. If the notice should carry his legal name or the
    publisher (the Steam page's developer/publisher, later Beyond
    Compounding), it is one constant, Credits.COPYRIGHT, with the bat's
    --copyright held to it by CreditsCheck.
- CR1-J-S, Steam and itch.io still serve 0.9.1 on Oracle's NFTC runtime with
  no source\: the four steps under "Steam and itch.io" above, at the next
  upload.
