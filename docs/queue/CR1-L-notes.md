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
  After review, its dedicated brief is [CR1-J.md](CR1-J.md), expanded to
  source delivery and the Microsoft terms as well as runtime notices.

## Review mends and deferred calls

- REV-CR1-L-1 raised four findings. ★ The orchestrator explicitly assigned
  findings 3 and 4 here and deferred findings 1 and 2 to CR1-J, as a call
  for Jerus, in bus message 20261117-201821-406-orchestrator-4df2.
- Finding 3: Maven Enforcer 3.6.2's requireFilesExist runs at validate,
  before compilation or resource copying. It requires all six upstream
  legal texts and sources.properties. An absent tools/credits directory or
  file now fails the build instead of silently producing an incomplete jar.
  Rule behavior follows [Maven's built-in requireFilesExist documentation](https://maven.apache.org/enforcer/enforcer-rules/requireFilesExist.html).
  Negative build checks: with the whole tools/credits directory temporarily
  renamed, mvn -q -B -DskipTests clean package failed at RequireFilesExist
  (exit 1). Then removed each of the seven files individually and ran
  mvn -q -B validate: all seven failed at the same rule, exit 1. Each
  rename was restored in finally; with every file restored, validate passed.
  Full log: C:/Users/Jerus/AppData/Local/Temp/CR1-L-omissions.log.
- Finding 4: Credits.sections() uses a lazy class holder and an immutable
  list. JVM initialization publishes one complete snapshot; later menu
  visits reuse the list without jar reads. The reader-injected overload is
  still uncached, preserving the missing-file fixtures. CreditsCheck holds
  repeat-visit identity and immutability as well as those fixtures.
- ★ Findings 1 and 2 remain for CR1-J: OpenJFX's GPL v2 corresponding
  source delivery, the JRE's distribution/source terms, and the Microsoft
  runtime DLL terms. Adding licence texts here does not complete those
  obligations. Jerus chose source archives beside the exe at 20:23,
  conveyed in bus message 20261117-202537-635-orchestrator-77f2: the build
  must make and check the archives for OpenJFX 21 and the OpenJDK runtime.
- Further inspection of the existing image found that
  runtime/legal/java.base/LICENSE starts with Oracle No-Fee Terms and
  Conditions (NFTC), rather than GPL. This was reported to the orchestrator;
  the licence's redistribution clause at lines 62–66 includes a prohibition
  on fees for products bundled with it. CR1-J moves packaging to OpenJDK,
  Eclipse Temurin 21 recommended by the orchestrator. PC installation awaits
  Jerus's word, asked at 20:26. No runtime or packaging change is made here.
  The dedicated CR1-J brief records the decision and its image/source checks.

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
- After review mends: ./batch-check.sh --fast exit 0; compile OK (64s),
  maps regenerated, Stale 0 firm. ./batch-check.sh CreditsCheck exit 0;
  93 successful assertions, no FAIL, including cache identity and immutable
  sections. Logs: /tmp/CR1-L-review-fast.log and
  /tmp/CR1-L-review-credits.log in Git Bash, and target/checks/CreditsCheck.log.
  git diff --check passed. The pick remains ALL (pom.xml), 218 harnesses;
  the review line remains UI-wide threading. The orchestrator reserves the
  dry run after HW1-V; at that handoff it was still awaiting the go.
- Dry-run go received in bus message 20261117-233351-919-orchestrator-429d.
  Merged origin/main 3f09922826b1562f42c6b3cf510de58935d0d884 (0.9.142,
  including HW1-V and FB8). Only generated dials/harness/index docs conflicted;
  took this branch's copies and regenerated all maps on the merged source.
  ./batch-check.sh --fast passed (exit 0), Stale 0 firm; no source conflict
  or change to CR1-L's implementation. Merge summary: /tmp/CR1-L-merge-fast.log.

## Final launcher dry run

- Retained run: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CR1-L-20261118T0339`;
  `ship.log` is the complete publisher log, `checks/` holds each harness log,
  `selection` says full, and `checks.complete` records version 0.9.143.
- The two commits tested were branch
  `f1b6d6810dbbe25f7f22436db2d357ab1fe0a266` and main
  `3f09922826b1562f42c6b3cf510de58935d0d884`, as the launcher's
  `branch.commit` and `main.commit` record. The provisional branch was pushed
  before launch. Command: `timeout 10800 sh tools/gate.sh CR1-L --dry-run`.
  No other build, harness or ensemble was started alongside it by this session.
- Finished 2026-11-18 00:19:08 Atlantic with **gate exit 0**. The suite's
  check stage took 2,402 seconds and returned exit 3: 203 gate-tier harnesses,
  193 passed and 10 failed only on recorded known reds. Those harnesses were
  DeathRecordCheck, DistressBleedCheck, HealthCheck, MapCheck, PlanCheck,
  PopulationCheck, PriceLevelCheck, RoadCheck, ScaleCheck and YoungBankCheck.
  No NEW red or unnamed line (`unnamed.txt` is empty); no exemption added.
  CreditsCheck passed all 93 assertions in the fresh merge clone.
- Maps regenerated and Stale found 0 firm findings. Ten timing assertions
  were soft (EarthTerrainCheck 2, SaveSlotCheck 1, MapCheck 6, PlanCheck 1);
  nine path assertions were informational observations. Two starts waited
  for CPU admission. No ensemble: CR1-L still changes no month input.
- The launcher restored its clone's tree and made no publication. Its logs
  were retained; the successful scratch clone was removed by the launcher.
  The orchestrator was told at 00:19 that the machine slot was released,
  before the 01:15 nightly reservation.
- After this run, only these notes and the ship file changed. The gate-run
  comment is the ship file's first line; its known-reds parser was checked.
  The provisional marker is removed last in its own commit and checked
  absent on origin before reporting ready.
