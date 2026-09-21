# CityBuilderSim - read this first

This file is for an AI session (Claude Code, Cowork, a chat with the repository
attached) picking up the game. It says where things are, what the standing
rules are, and how a batch of work is done here. The human introduction is
`README.md`; read that too, it is short. Everything below assumes it.

The game is a macroeconomic city simulator in Java 21 and JavaFX, one Maven
project, one flat package `ham.citybuildersim` plus `sectors/`, `ui/` and `tools/`.
The version is `GameVersion.VERSION`, the save format `GameVersion.SAVE_FORMAT`,
and nothing else in the tree states either. The author is Jerus (Samuel); a
comment that starts `Jerus:` is his brief in his own words, and it is the
requirement.

## Open these before reading source

The tree is 129,000 lines; `Game.java` alone is 8,000, and the interface is
nineteen files, the largest just over 4,000. Do not read them. Read the generated indexes and jump.

| document | what it answers |
|---|---|
| `docs/map/README.md` | one row per file: size, method count, what it is, how many files use it |
| `docs/map/NAME.md` | one file's banner sections and every method with its line - the table of contents the file does not have |
| `docs/dials.md` | every `static final` constant, its value and the sentence above it: "is there a dial for this, and where" |
| `docs/month-order.md` | the month as a numbered list of statements with line numbers: where a change that must land "after wages, before the shops buy" goes |
| `docs/harnesses.md` | what every harness asserts, in its own labels, and which harnesses mention which class |

They are generated from the sources by `ham.citybuildersim.tools.Maps`
(`Regenerate maps.bat` on the PC; `java -cp target/classes
ham.citybuildersim.tools.Maps` anywhere). Their line numbers are as of the date
at the top of each; regenerate after a batch and commit them with it.

Three command-line tools for the same purpose:

    java -cp target/classes ham.citybuildersim.tools.Where nextMonth            # every member called that, file and line
    java -cp target/classes ham.citybuildersim.tools.Where Game.nextMonth -print  # prints just that method, numbered
    java -cp target/classes ham.citybuildersim.tools.Where "THE BANK"           # finds a banner section by its title
    java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.SaveDump 3 sectors   # looks inside a save without loading it
    java -cp target/classes ham.citybuildersim.tools.Stale                      # which comments and documents have stopped being true; StaleCheck asserts its firm half

The design record lives outside the repository, in the claude.ai project
"Leverage & Growth: Java Game": `claude/todo.md` is the list of what is open,
`claude/changelog.md` what shipped and when (newest first), `claude/index.md`
a map of the two hundred design notes by subsystem, and the published manual
(artifact "CityBuilderSim", https://claude.ai/artifact/BkBAN1RDiQTpCj79WPbpCp)
is the model written out. A session that has been away reads the changelog's
top and the todo's section 0 before anything else.

## The standing rules

These are Jerus's, and they do not move.

- **Save slots 1-9 are his cities. Never write them.** Slot 10 is the
  assistant's playtest slot. A harness city uses `GameFiles.scratch(label)` or
  an explicit temp path, never `new Game()`, which points at the real save
  folder.
- **Write fixes to the PC without asking** ("yes, always"), then verify every
  file byte-for-byte. Ask as many questions as you like; do not wait on
  answers to ship what is already decided.
- **Per-batch write-up** goes to the project as `claude/<title>.md`, in prose,
  with what was found and what was decided; the todo and the changelog get
  their lines. The README's counts are not the record - the changelog is.
- **A harness is the finding.** The four rules in `README.md` under *The
  checks*: a fixture must cause the condition; assert against the model's own
  constants; count how often a mechanic fires in a real run; never move a
  harness's premise to let a change through.
- **`Sectors.KEYS` order is load-bearing** (equity index, household share
  arrays, audit pools). New sector at the end. Same for `BuildingType` and for
  ids in `buildings.json`, which saves are keyed by: take the next id, never
  renumber.
- **A flow cannot be reconstructed from the state a month ended in.** If a
  screen or a check needs it next month, save it (`DataSave`, `HistorySave`,
  and the load path - `ReadPathCheck` and `SaveFileCheck` are what catch a
  field that made it into one and not the other).
- **The month is a sequence, not a set.** `Game.nextMonth()` and
  `SimulationEngine.simulateMonth()` hold it; `docs/month-order.md` lists it.
  A line that lands in one phase and not the other is the classic bug here.
- **Bump `SAVE_FORMAT` only when an old save would load wrongly**, not merely
  incompletely; Gson leaves a missing key alone.
- **Every `println` is the log.** There is no separate logging call to adopt.
- **Keep each file's own line endings** until the repository is normalised
  (`.gitattributes` says LF; the tree is still mixed - see the audit note).

## The shape of the tree

    src/main/java/ham/citybuildersim/
        CityBuilderSim.java        launcher (deliberately not an Application subclass; stays here for the jar's main class)
        Game.java                  the month, the seam every system meets at; 8,000 lines, 25 banner sections
        Motoring.java, LuxuryCounter.java, Offending.java, CityBasket.java
                                   mechanics moved out of Game on 2026-09-18, behaviour unchanged: each is
                                   called from the month and read through Game's delegating getters (the
                                   project's splitting-game.md)
        SimulationEngine.java      the order the month runs in (190 lines - read it whole)
        Sector.java / Sectors.java the template every business extends, and the registry
        sectors/                   fifteen sector classes; Mining.java is the shape to copy
        ui/                        the interface: UserInterface.java is the window (4,000 lines: clock, rail,
                                   strips, panels, dialogs), one <Name>Screen.java per tab (split 2026-09-18 -
                                   the project's splitting-the-interface.md), Money/Statement/Pieces/Levers
                                   (what the screens share), Palette.java, Icons.java. The model never imports it.
        *Check.java                fifty-seven harnesses, each a main() with static helpers
        AllChecks.java             the runner; its HARNESSES list is the registry - a harness not in it does not run
        LongPlaytest.java          4,002 months, audited every one; also the fixture builder harnesses borrow
        tools/                     the index generators, the two look-up tools and Stale (the prose check); nothing in the game uses them
    src/main/resources/buildings.json    the balance file (ids permanent); consumption.json the basket

A `.java` file at the root that holds only a comment saying MOVED is a stub
left where a class used to be, because a cloud session cannot delete on the
PC; Jerus `git rm`s them. The maps skip them.

Conventions the code is written to, which the indexes rely on:

- A class opens with a javadoc whose first sentence says what it is, then a
  `WHY` paragraph: what this replaced and what went wrong with it. The code
  map prints the first sentence; write one.
- A screen, a mechanic or a batch is introduced by a banner comment,
  `/* ===== TITLE ...`, often with the date in it. `Where "TITLE"` and the
  section tables find them. Sub-parts use `/* ----- title -----`.
- A dial is a `static final` with one sentence above it; `docs/dials.md`
  prints that sentence next to the value, so a constant without one shows up
  blank there.
- A harness prints its sections as `--- title ---` and labels every assertion
  through a `static void helper(String label, ...)`; `docs/harnesses.md` reads
  both. `fails` is the exit code; `-q` on `AllChecks` hides the prose.

## How a batch is done

**On the PC (Claude Code, NetBeans beside it):**

1. Read `docs/map/README.md`, then the map of each file the change touches.
   `docs/harnesses.md` says what already checks the area.
2. Change the model first, the save second, the screen last; a harness for
   the mechanic before the screen, because the screen is checked by eye.
3. Build: NetBeans *Clean and Build*, or `mvn -q compile`. Run the suite:
   `java -cp "target/classes;%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar" ham.citybuildersim.AllChecks -q`
   (about two minutes). `AllChecks money bank` runs two.
4. `Regenerate maps.bat`. Commit the sources, `buildings.json` and `docs/`
   together; bump `GameVersion.VERSION` when the batch is player-visible.
5. Write the batch up in the project - the why, while you still know it.
6. **The docs pass, by a fresh agent** - `docs/docs-pass.md` is its brief.
   It makes every comment, header, index and count agree with the code
   again, and it is done by a context that did not make the change, because
   the one that did has stopped seeing the prose. `Docs pass.bat` runs it
   headless against the last commit (`docs/docs-pass-agent.md` is the same
   thing as a subagent, for an interactive session). Act on its report. A
   batch is not done until this has run. Its mechanical half is
   `tools.Stale`, which `StaleCheck` runs in the suite; the pass is for what
   a tool cannot judge.
   Which model, Jerus's rule (2026-09-21): both agents - the implementer and
   the docs pass - run on Opus, not Fable, whatever the job's size; he does
   not have the tokens to run Fable for every task. (The 2026-09-18 rule let
   Fable take the substantial jobs; it is withdrawn.) The orchestrating
   session keeps the gate either way.

**From a cloud session linked to the PC (Cowork):**

The working copy is staged from the PC, built there, and the changed files
are written back. Stage the tree (in batches of fifty paths), keep a
byte-for-byte verify copy, compile the model without JavaFX (`build.sh`
excludes `ui/`, `CityBuilderSim` and `BuildMenuCheck`; `build-ui.sh`
compiles everything against the cached JavaFX jars), run `AllChecks -q` for
the baseline, edit, rebuild, rerun, then commit each changed file to its
original path with the modification time recorded at staging, re-stage it and
compare. A fresh output folder per deploy, because the bridge caches by staged
path. Slot 10 for any playtest that touches a real save folder. Then the docs
pass (step 6 above): spawn a subagent with a fresh context, give it
`docs/docs-pass.md`, the deploy set's file list and the design note, and act
on its report before the deploy is verified.

**Either way, before saying a change works:** the suite is green except for
what was already red (say which), the map is regenerated, the write-up
exists, and the docs pass has run. `docs/notes/` carries copies of the
project's `todo.md`, `changelog.md` and `index.md` as of the last deploy from
the cloud loop, so a session on the PC can read the list and the record; the
project is the original.

## Adding things - where the shape already is

- **A sector:** one class extending `Sector` in `sectors/`, one key constant
  and one line at the end of `Sectors.KEYS`; `sectors/Mining.java` is the
  shortest. Add its buildings to `buildings.json` (`BuildingDataCheck`), a
  `<Name>Check` modelled on `RestaurantsCheck` (sections numbered from the
  header's "what this has to prove" list), and its line in `AllChecks`.
- **A saved field:** the field, `DataSave` out and in, `HistorySave` if it is
  a series, and the reader in `Game`'s load path; then `SaveFileCheck`.
  No format bump unless an old save would now read wrongly.
- **A screen section:** a banner in the tab's own class in `ui/` (the window
  itself is `UserInterface`), the panel rebuilt on the clock so it must
  restore its own scroll position; every model figure it shows through a
  public getter, never recomputed in the screen - the interface is a separate
  package now and sees only the model's public API.
- **A dial:** `static final`, in the class that owns the mechanic, one
  sentence above it, and the harness asserts against the constant rather than
  the number.
- **A harness section:** `out.println("--- what it proves ---")`, labels that
  read as sentences ("...and the same fact the other way up"), fixtures that
  cause the condition.
