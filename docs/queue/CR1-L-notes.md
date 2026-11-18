# CR1-L — the libraries' notices on the Credits page

Built from origin/main 45de48dc2 (0.9.140), on batch/CR1-L.

## What was built and why

- ★ The resolved binary jars are Gson 2.10.1 and OpenJFX 21 (base, graphics,
  controls and fxml, including their Windows classifiers). None contains a
  LICENSE, NOTICE, COPYING or legal/ file, under META-INF or elsewhere. The
  matching sources jars inspected (Gson, JavaFX base and graphics) omit them
  too. Gson's embedded POM and manifest name Apache-2.0; its manifest names
  the release tag gson-parent-2.10.1. No Gson NOTICE exists in that release.
- ★ The orchestrator approved the fallback by bus on 2026-11-17: exact files
  from each project's own release, one copy, with a version check against the
  POM. tools/credits is that single copy; Maven puts it in credits/ in both
  jars at process-resources, including ordinary mvn compile. No duplicate
  lives in src/main/resources. sources.properties records release URLs,
  versions and SHA-256 hashes of the untouched upstream texts.
- Gson's LICENSE: [gson-parent-2.10.1](https://github.com/google/gson/blob/gson-parent-2.10.1/LICENSE).
  OpenJFX's [21+31 release](https://github.com/openjdk/jfx/tree/21%2B31):
  LICENSE (GPL v2 including the Classpath Exception), ADDITIONAL_LICENSE_INFO,
  ASSEMBLY_EXCEPTION, and the bundled graphics module's legal/jpeg_fx.md and
  legal/mesa3d.md. No media/web modules are dependencies. The source release's
  build.properties identifies major 21, minor/security/patch 0.
- The page keeps Earth, nations and fonts first, then one section for Gson
  and one for OpenJFX. Each upstream text is shown in full; OpenJFX's files
  are named and ordered within its section. The JPEG acknowledgement is also
  explicit in the lead. Losing any OpenJFX text marks its whole section
  missing, with a library-specific note, rather than presenting a partial
  licence as complete. Only the Credits introduction changes in the screen.
- CreditsCheck holds section order, full text equality, provenance/hashes,
  missing-file behavior, paragraph preservation, and dependency versions in
  pom.xml. It scans the resolved dependency jars for legal files: any newly
  supplied NOTICE/licence fails the fallback audit so a future upgrade must
  take the text from the jar instead. All existing Earth/font checks remain.

## The other software in the exe

- ★ Build EXE.bat asks jpackage to link java.base, java.desktop,
  java.scripting, java.sql, jdk.jfr, jdk.unsupported and jdk.localedata;
  jpackage adds their dependent modules. The existing image at
  C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/dist/CityBuilderSim/runtime/legal
  has 12 module directories and 49 legal entries (170,958 bytes), including the runtime's
  own COPYRIGHT/LICENSE and third-party notices for AES, ASM, ICU, Unicode,
  CLDR, the public suffix list, SipHash, wepoll, zlib, colour imaging,
  FreeType, GIF, HarfBuzz, JPEG, LittleCMS, PNG, Mesa, BCEL, DOM, CUP,
  Xalan, Xerces and Thai dictionaries. Some entries are relative references
  to java.base's licence/copyright. This installed JDK is Oracle 21.0.4.
- The page should also expose those runtime notices, read from the actual
  packaged runtime's legal folder, with links/references resolved inside
  that folder. It is a build-dependent inventory, larger than the two fixed
  library sections; copying this PC's JDK legal folder into the jar would
  drift from another packager's runtime. Queue CR1-J for that work, keeping
  the runtime/legal folder shipped by jpackage intact. OpenJFX's Windows
  graphics jar also carries its native renderer/font/image DLLs and Microsoft
  runtime DLLs; its upstream graphics notices are included here. CR1-J should
  audit accompanying Windows runtime notices while inventorying the image.

## Checks and review

- Initial ./batch-check.sh --fast: exit 0; compile OK, maps regenerated,
  Stale 0 firm, tree 0 changed. No entries in known-reds.txt name CR1-L.
- Used Where UserInterface.showCreditsMenu -print; never read UserInterface
  or Game whole. WC1 makes Codex the whole-batch builder; no delegation.
- No model read by the month changed: Credits is read only by the screen
  and its harness. The default playtest trace is unchanged; no ensemble.
- This changes no process scripts, runner machinery or session rules, so
  no one-in-one-out process count applies.
- Final ./batch-check.sh --fast: exit 0; compile OK, maps regenerated,
  Stale 0 firm. ./batch-check.sh CreditsCheck TimingCheck: exit 0; both
  harnesses passed. CreditsCheck made 91 successful assertions, no FAIL.
  A CPU admission hold cleared; it was not a test failure. Logs are in
  target/checks/ (CreditsCheck.log and TimingCheck.log); driver summaries
  /tmp/CR1-L-final-fast.log and /tmp/CR1-L-named.log in Git Bash.
- Re-fetched all six legal files from the pinned upstream URLs after their
  commit: byte-for-byte equality. git diff --check passed and every touched
  tracked file has LF line endings. The diff was reviewed independently of
  the implementation: only resource loading and menu prose, no money, saves,
  threading or simulation path changes. The UI keeps its existing paragraph
  labels and scrollers; no headless visual check was attempted.
- mvn -q -B -DskipTests package: exit 0. Opened both the thin jar and the
  executable shaded jar and compared all seven credits legal/provenance
  resources with tools/credits: every byte matched in each jar.
- sh tools/pick.sh origin/main HEAD: ALL, 218 harnesses, because pom.xml
  changed. Review line: `needed - threading: ui/UserInterface.java (UI-wide)`;
  the menu's prose is the only screen behavior change. No --pick suite was
  run locally: ALL requires the orchestrator's dry-run slot. origin/main
  remained 45de48dc2 at the final pre-handoff fetch.
