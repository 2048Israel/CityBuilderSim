# GB5 notes - the gate box runs the harnesses that need a display

FS4 found it (docs/queue/FS4-notes.md): the gate box has no screen, so
KeptCheck skipped there, and no gate since GB1 (2026-11-20) had judged it or
its timing. Built 2026-11-20/21 by GB5 (Claude Opus, headless). Jerus ran
the one line the box needed (`sudo apt install libpangoft2-1.0-0`, "Done,
installed"); nothing else is his.

## What was found

- **The display harnesses** are the nine that start the JavaFX toolkit, now
  `AllChecks.DISPLAY`. Without a display each prints a skip and passes:

  | harness | what it proves | its line without a display |
  |---|---|---|
  | ChartFitCheck | the full-screen chart's foot is on the screen (FW10) | `skipped: the JavaFX toolkit cannot start here (...); the chart needs it to be laid out` |
  | KeptCheck | the kept pages end where a page built new would, and cost what they should | `skipped: ... the page needs it to be laid out` |
  | KeptTimingCheck | KeptCheck's timed sections within their budgets (FS4) | the same |
  | MapCheck | the map's data and painter | `drawing the icons onto a Canvas: skipped - ...` |
  | MoneyWordsCheck | every page's words with the money after the figure (FM1) | `skipped: ... the pages need it to be drawn` |
  | NewUnitCheck | a new building read as it would run | sections 7 and 12: `(no held order: ...)`, `(the furnace's card words are not read: ...)` |
  | ReplayCheck | the replay's window, moves and Follow (TR1, TR2) | section 5d: `skipped: this JVM is headless (no display)` |
  | StepsCheck | the magnitude controls and bond lots | section 4: `UI presses skipped: ...` |
  | ToastCheck | a toast keeps out of the page's way (TS1) | `skipped: ... the page needs it to be laid out` |

  BuildMenuCheck needs JavaFX's classes, not the toolkit, and ChipRender
  (tools/, run by hand) starts its own; neither is one.
- **Six starts, each asking AWT.** KeptCheck, MoneyWordsCheck, StepsCheck (through
  KeptCheck's), UiProbe, ReplayMoveProbe and FollowBench each had a start that asked
  `GraphicsEnvironment.isHeadless()` first. On Linux with no `DISPLAY`, AWT is
  headless whatever JavaFX could draw on, so even Monocle would have skipped.
- **Monocle draws, Pango lays the text out.** TestFX's Monocle for JavaFX 21
  (`org.testfx:openjfx-monocle:21.0.2`) brings the toolkit up headless, in
  software (`prism.order=sw`). On Linux, JavaFX lays text out only through
  Pango (its font factory's glyph layout is native Pango, with no other).
  The box had no `libpangoft2`, so a page's first label threw on the FX thread,
  and the harness hung rather than failed.
- **The proof before the install** used Pango's five packages (`libpango-1.0-0`,
  `libpangoft2-1.0-0` 1.57.0-1, `libthai0`, `libthai-data`, `libdatrie1`), fetched
  with `apt download` (no sudo), unpacked into a scratch folder, and given to the JVM
  by `LD_LIBRARY_PATH`. On it:
  - KeptCheck ran in 243 s (exit 7, 8 fails, 0 skips);
  - MoneyWordsCheck 5 s, StepsCheck 1 s, NewUnitCheck 2 s and ReplayCheck 16 s
    passed;
  - MapCheck's one red was SIDECAR_MS, timed hard by hand.
- **Monocle draws what KeptCheck measures.** On the same commit, the PC
  (Windows glass, Direct3D) and the box (Monocle, software) print the same
  4,034 assertion lines, every count, bound and share included. Only the heap
  line differs (below).
- **KeptCheck's reds, main's on both machines.** Six fail on the PC and on the box:
  - section 55, "most months make no node at all (9 of 25)", the Bank's Profit page;
  - section 78, "...the held slider is the same node, still held, at the thumb
    (0.005)";
  - the four fund rows, known-reds 47-50 (FS5).

  They are main's. Main's own KeptCheck (a9bfcd070's, compiled apart and run on
  the PC) fails the same six. It prints the same 4,034 lines as the branch's,
  figures included, and the heap is the same too: 1,379.0 MiB against 1,378.9.

  They have gone unjudged since the gate moved to the box. FS4's last KeptCheck
  on the PC (303830e24) had 55, 78 and the fund rows green. Main then changed 36
  source files, OE1's order estimate among them, and the played city moved under
  them. They are FS5's: its range is KeptCheck from section 55, KRT1's 77 and 78.
  Section 55's line names a count, "(9 of 25)", and known-reds row 63's
  "(11 of 25)" is section 72's, so the ship file has a row for each of the two.
- **KeptTimingCheck on the box** (hard, alone): 29 of 29 budgets met. The
  worst is 0.42 of its budget (the Build Overview, 5.08 of KEPT_BUILD_MS 12 ms).
  The nightly's judge (`timing-pair.awk mode=night`) counts all 29 OK, so no
  budget misses on the box's hardware.
- **The heap, the box's alone.** Section 102's "the full kept check leaves a
  quarter of AllChecks.HEAP_MB free" (KEPT_HEAP_SHARE .75, under 1,536 MiB of
  2,048) reads:
  - 1,378.9 MiB on the PC;
  - on the box: 1,718.5 MiB in a suite run (scratch Pango), then 1,555.0,
    1,553.9 with `prism.maxvram` at 64 MB (so the texture pool's cap is not it),
    and 1,569.9 on the installed Pango.

  So it misses by 18 to 183 MiB.

  A class histogram at that line (a scratch copy, G1) totals 1,556 MiB live: it is
  held, not garbage. `int[]` comes first at 226 MB.

  The same histogram on the PC in software (`prism.order=sw`) reads 1,780.3
  MiB, with `int[]` at 429 MB; a second such run reads 1,594.3.

  Every other class matches on both: 43,202 Labels and 75,621 `double[]` on
  each, and the 1.3 million RectBounds within 14. Only `int[]` differs, and by
  subtraction it is about 40 MB on the PC's Direct3D.

  What holds it is the software renderer:
  - At the line, no window is open, and Prism's software texture pool holds 0 MB.
  - A scratch test settles it. Forty 800x450 canvases, each drawn once, hold 241
    MiB on the heap in software and nothing on Direct3D. Released, they are
    gone at the next GC.
  - A snapshot's pixels go with its image on both renderers.

  So a canvas the kept pages hold (their charts) keeps its render target on the
  heap in software, where Direct3D keeps it in video memory. The excess is at
  the render scale: the PC's display scaling gives about twice the box's.

  The kept pages hold nothing more on the box. The red is the box's renderer,
  not a retention, and nothing in GB5 makes it.

## What changed and why

- **`pom.xml`**: Monocle, test scope, so neither the shaded jar nor the exe holds
  it (GPL 2 with the Classpath Exception, as OpenJFX). JavaFX stays at 21.
- **`ui/FollowBench.startJavaFx`, the one start (★2).** The other five call it:
  KeptCheck's (and through it MoneyWordsCheck's and StepsCheck's), UiProbe's
  and ReplayMoveProbe's. It works the same way in each case:
  - With no display named (`-Dcbs.display`, `FollowBench.DISPLAY_PROPERTY`), it
    asks AWT, as every start did.
  - It starts the toolkit, then lays one line of text out on the FX thread, so a
    toolkit that is up but cannot draw text (no Pango) is a reason given in a
    second, not a hung harness.
  - With a display named, a start that fails prints
    `FAIL: the JavaFX toolkit cannot start here (...), where -Dcbs.display=...
    names a display (GB5)` and exits 1, never a skip (★3). A Pango reason adds
    `the box lacks libpangoft2: sudo apt install libpangoft2-1.0-0`.
- **`tools/gate-box.sh`**: on the box, a gate's or a night's JVMs get
  `-Dcbs.display=monocle -Dglass.platform=Monocle -Dmonocle.platform=Headless
  -Dprism.order=sw` through `JDK_JAVA_OPTIONS`, so every child the suite starts
  has them. An ensemble's runs draw nothing and are left alone. When `ldconfig`
  has no `libpangoft2`, the run's stream says so in one line: `display: the box
  lacks libpangoft2: sudo apt install libpangoft2-1.0-0 (GB5: every display
  harness fails without it)`.
- **`AllChecks.DISPLAY` and the display line.** With a display named, the
  summary prints `display: monocle: N display harnesses started, none can skip
  here (red if the toolkit cannot start): ...`. `batch-check.sh` shows it as
  its `display` line.
- **The guard, TimingCheck 10 (★4).** These are its parts:
  - a scan: no file but FollowBench (and ChipRender, by hand) starts the
    toolkit, none asks AWT, and `AllChecks.DISPLAY` is every harness that
    starts it;
  - three child JVMs on Monocle, run with ChartFitCheck: a toolkit that cannot
    start (`prism.order=nonesuch`) skips with no display named, and fails with
    exit 1 with one named; the box's software pipeline passes;
  - the display line's wording.

  `TimingCheck.run` now drops `JDK_JAVA_OPTIONS` from its children, so the box's
  own options never reach a fixture that sets its own.
- **`tools/ship-selftest.sh`**:
  - a box with Pango says nothing of it;
  - the night's JVMs get the four options;
  - a box without Pango (a stub `ldconfig`) prints the one line.
- **`README.md`** (the display harnesses' paragraph) and **`docs/gatebox.md`**
  (The machine: the display).
- **The PC is unchanged.**
  - The PC names no display, so every start asks AWT as before. A Windows
    session has a display, the cloud's has none and skips with the same
    lines.
  - The one new act on the PC is the start's line of text.
  - `batch-check.sh`'s new line prints only what a named display writes, so
    on the PC it prints nothing. `Batch check.bat` is untouched.

## The calls (★)

- ★1 **Monocle, with Pango (the orchestrator's A, 2026-11-20).** Monocle is a
  dependency, starts no process to manage, and draws what KeptCheck measures.
  The proof is the 4,034 identical lines; its timing is in budget. Xvfb would
  need the same Pango and GTK 3 besides (glass's Linux windows), so more
  installs, and an X server around every run. Either needs Jerus's one line.
- ★2 **One start, not six.** A start's rules (when to skip, when to fail) live
  in one place, and the guard can scan for any other.
- ★3 **A named display never skips.** The brief's "a check that fails if a
  display harness skips on the box": on the box, where a display is named, a
  start that fails is a FAIL and exit 1 in the harness itself, so a gate is red
  at once. The summary's display line lists what ran.
- ★4 **The guard in TimingCheck**, which already starts child JVMs and scans
  the sources. The suite runs it on the PC and on the box alike. Monocle runs
  on Windows too, so the PC proves the box's path.
- ★5 **The heap passes by name, on FS5.** It cannot be mended without moving
  KEPT_HEAP_SHARE or HEAP_MB, or without counting the heap net of the
  renderer's pixels, which changes the check's premise. The brief allows
  neither ("the budgets do not move here").
  - The ship file's row names the cause and the miss.
  - FS5 is the fix because its range, KeptCheck from section 55, holds section
    102.
  - Recommended for FS5, or for a batch of its own if the orchestrator would
    rather: KP2c's share read the pages' heap where the renderer's targets live
    off it, the pipeline asked through Prism's settings; or a box share beside
    the PC's.

  On the PC nothing changes (1,379 MiB, green).

## The checks

Every JVM on the PC was `./batch-check.sh --fast` or one named harness at a
time, with the earth and world paths set. The box's runs were niced, in my
own scratch folders (`~/cbs/gb5-*`, removed at the end), and gave way to any
gate, night or ensemble. One did: the probe stopped itself for FB15b's
ensemble.

- **PC:**
  - `./batch-check.sh --fast` at 25b871699 (main merged): compile OK, Stale
    0 firm, 194 soft. The maps were regenerated (a1c59c11f).
  - `sh tools/ship-selftest.sh`: 202 of 202, the three GB5 lines among them.
  - KeptCheck at 100a48a0d: 6 FAILED, the six above; the heap is 1,378.9 MiB.
    Main's own KeptCheck: the same six, the same 4,034 lines, 1,379.0 MiB.
  - KeptCheck in software (`prism.order=sw`), with a class histogram: 1,780.3
    MiB.
  - The same in software with a probe at the heap line: 1,594.3 MiB, no
    window open, the software texture pool at 0 MB. The scratch canvas test
    above ran in seconds.
  - `./batch-check.sh` with one name a call at a1c59c11f:
    - TimingCheck (67 s), ChartFitCheck, ToastCheck, MoneyWordsCheck,
      StepsCheck, NewUnitCheck and ReplayCheck all exit 0.
    - TimingCheck 10's seven lines are OK.
    - KeptCheck timed out at 1,740 s at section 48: 17 JVMs of other
      sessions' suites ran beside it. Its direct runs above took 1,350 s and
      print the six.
- **Box, Monocle, the installed Pango, the gate's four options through
  `JDK_JAVA_OPTIONS`:**
  - `./batch-check.sh` with one name a call at 25b871699: ChartFitCheck,
    MapCheck, MoneyWordsCheck, NewUnitCheck, ReplayCheck, StepsCheck and
    ToastCheck all exit 0. Each prints its `display monocle: 1 display
    harness started, none can skip here` line, and none has a skip line.
    MapCheck's two SKIPPED lines are its own, the same as without a display:
    a fixture not reached, and soft timing.
  - On the scratch Pango, before the install, at 100a48a0d:
    `./batch-check.sh KeptCheck` gave the six main reds and the heap, and
    `./batch-check.sh --timing KeptTimingCheck` gave 29 OK.
  - KeptCheck on the installed Pango with a histogram: 1,569.9 MiB, 1,556 MiB
    live.
- **The pick:** `sh tools/pick.sh origin/main HEAD` is ALL (234 harnesses),
  since pom.xml and AllChecks.java changed, and the review is needed:
  "threading: +Platform.runLater in ui/FollowBench.java". That is the start's
  line of text, laid out on the FX thread and awaited for 60 s, as the
  toolkit's own start is.

## One in, one out

`git diff --numstat origin/main`, without the generated maps, the notes and
the ship file: +140 -84.

- The process files remove more than they add, +76 -83: the scripts, the
  suite's machinery, the rules a session reads, and the six starts made one.
- The rest is what the brief asked for: TimingCheck 10, the guard, and
  pom.xml's dependency, +64 -1.

## What is left for Jerus

Nothing. His one line is done (`libpangoft2-1.0-0` 1.57.0-1, dpkg "install
ok installed").

## The dry run

**PASSED**, 2026-11-21 05:38 Halifax (09:38 UTC), gate exit 0,
published: no. The authorized command was
`sh tools/gate.sh GB5 --dry-run --no-tag --branch-gate`; the stranger's
threading review had already said SHIP.

The kept launcher run is `/home/jerus/cbs/runs/cbs-gate-GB5-AJMa9yLa` on the
box, branch `d7a444724b01fffc0dbd80ca8c5074c75282763a` against main
`fb64f5c7db286224d7193361282a856ae4a78135` (0.9.177). Its selection is
`full nightly BridgeMapCheck` followed by `KeptCheck version=0.9.177` on the
next line, exactly as its `selection` file records. The pick is ALL, 235
registered harnesses; 222 ran in 867 s, 211 passed and 11 had known reds.
The suite's 17 failing assertions are all known, with no new or unnamed
reds; its exit was 3 and the whole check stage took 885 s. Compile passed,
the maps regenerated, and Stale had 0 firm and 194 soft findings.

All nine display harnesses appear in the Monocle display line. KeptCheck
completed with only section 102's named software-renderer heap red.
KeptTimingCheck was soft in this gate and skipped its clock verdict as
designed; its separate hard box proof above met all 29 budgets. The default
trace is unchanged; this batch moves no playtest and needs no ensemble.

Recovery session GB5c2 read the original launch input and old out-2/out-3,
then verified all 853 sealed files both on the box and in the copied run at
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GB5-AJMa9yLa`.
The manifest's SHA-256 is
`27ba041b73313f46e2ba73d06a782ffbab1200cca480d4822af289a8c3437ae7`
on both. The MemWatch mirror is
`C:/Users/Jerus/tools/MemWatch/gatebox/runs/cbs-gate-GB5-AJMa9yLa`.
No suite or self-test was rerun, and no kept evidence or source was changed.
At recovery, main `8bf087822c251b44b6434934f5c0dfdc439f0c76` adds only
OB1's queued Markdown docs beyond the tested main, so the kept run's main
compatibility rule still holds. Only these notes and the ship file advance
the tested branch; the orchestrator publishes with `--no-tag --reuse` and
the box path while that compatibility holds.
