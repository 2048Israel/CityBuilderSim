# CityBuilderSim

A macroeconomic city simulator in Java 21 and JavaFX. You lay out a city; the
economy underneath it is the game. An age pyramid decides the workforce, a
labour market prices it, seven private sectors keep their own books and expand
on their own judgement, a commercial bank funds them, a treasury borrows at a
rate the market quotes it, and a currency floats against a world that has its
own prices and its own inflation.

Nothing in it is a headline number with a formula behind it. Every dollar that
leaves a pool arrives in another or crosses the border in a way the audit can
name, and a harness asserts that to the cent every month of a 333-year run.

**Status:** in development, headed for Steam. Build `0.4.4`, save format `21`.

---

## Requirements

| | |
|---|---|
| JDK | 21 (jpackage, used by the Windows build, ships inside it) |
| JavaFX | 21 — pulled by Maven, no separate SDK install needed |
| Maven | 3.8+ (NetBeans bundles one) |
| Gson | 2.10.1 — pulled by Maven |

Developed in NetBeans on Windows. The game itself is plain Java and JavaFX and
has no Windows-only code; only `Build EXE.bat` is Windows-specific.

## Build and run

**In NetBeans:** right-click the project → **Clean and Build**, then run it.
That writes two jars into `target\`:

- `CityBuilderSim-1.0-SNAPSHOT.jar` — thin, needs the dependencies on the classpath
- `CityBuilderSim-1.0-SNAPSHOT-executable.jar` — shaded, everything bundled

`Play CityBuilderSim.bat` launches the shaded one. It calls `java` directly
rather than relying on a `.jar` file association, and it deliberately leaves a
console window open — the game prints its month-by-month reports there, and if
it fails to start the reason is in that window instead of vanishing with it.

**From the command line:**

```
mvn clean package
java -jar target/CityBuilderSim-1.0-SNAPSHOT-executable.jar
```

The entry point is `ham.citybuildersim.CityBuilderSim`, which is a plain class
that calls `launch()` rather than an `Application` subclass. That is not a style
choice: the JVM launcher checks whether the main class extends `Application` and
refuses to start it without `javafx.graphics` as a *module*. A launcher class
sidesteps the check, which is what lets the shaded jar run with JavaFX merely on
the classpath.

## The checks

`AllChecks` runs the lot, one JVM each — **forty-four harnesses plus the
4,002-month playtest**, which it reports as forty-five, in about eighty seconds.
In NetBeans, right-click `AllChecks.java` → **Run File**. From a command line,
with the project's classpath assembled:

```
java -cp <classes>:<javafx jars>:<gson.jar> ham.citybuildersim.AllChecks
```

- `-q` suppresses each harness's own output and prints only the pass/fail line.
- Any other argument filters by substring: `AllChecks money bank` runs
  `MoneyCheck` and `BankCheck` and nothing else.
- The exit status is the number of harnesses that failed, so a build script can
  read it.
- `BuildMenuCheck` needs JavaFX on the classpath and is skipped without it.

**Run `AllChecks` after a copy-back, not before.** A batch has more than once
landed in the Java and not in `buildings.json`, and `BuildingDataCheck` exists
to catch exactly that.

Three rules the harnesses are written to, and worth keeping:

1. **A fixture must cause the condition under test, not stand next to it.** The
   single most common way a check has quietly stopped testing anything.
2. **Assert against the model's own constants, never a literal.** A check pinned
   to `0.06` tests that nobody edited the harness.
3. **Count how often a new mechanic actually fires in a real run.** Out-migration
   once shipped fully tested and fired zero times in 4,002 months.

## `buildings.json` — the balance file

Every building in the game is a row in `src/main/resources/buildings.json`:
cash cost, construction points, materials, maintenance, capacity, footprint in
square feet, power and water draw, road load, its job mix across the eleven job
types, which sector owns it, and what it makes and uses.

`BuildingCatalog` looks in two places, in this order:

1. `buildings.json` **in the working directory** — the copy you edit while tuning
2. `buildings.json` **inside the jar** — the shipped defaults

So a copy sitting next to the exe can be edited and the change is live on the
next launch: a balance pass with no rebuild, for you now and for modders later.
A broken edit costs the file, not the game.

**Ids are permanent.** Saves are keyed by them. Add a building by taking the
next id and bumping `nextId`; never renumber.

Money throughout the file is in thousands — a `cashCost` of `30` is $30,000.

## Where the game keeps its files

Not beside the exe, and not in the home folder: under the platform's application
data directory, because Steam Cloud syncs from a fixed set of known roots and a
bare home folder is not one of them.

| Platform | Location |
|---|---|
| Windows | `%APPDATA%\CityBuilderSim\` |
| macOS | `~/Library/Application Support/CityBuilderSim/` |
| Linux | `$XDG_DATA_HOME/CityBuilderSim/`, else `~/.local/share/CityBuilderSim/` |

Inside it: `saves/slot-01.json` … `slot-10.json` and `saves/autosave.json`, each
with a `-history.json` beside it carrying that slot's graphs; and `log.txt` with
`log-previous.txt` behind it. Every `println` in the game already goes to the
log, because a logging system that needs a thousand edits does not get adopted —
and in a packaged build there is no console, so all of it used to go nowhere.

Saves are written to a temp file, the existing save is backed up, then the temp
file is atomically moved. The worst case is a stray temp file and a save one
autosave old.

**Older saves always load; only the future direction is refused.** Adding a
field does not need a format bump — Gson leaves a missing key alone, and a city
saved before the field loads with it empty, which is correct for that city. Bump
`GameVersion.SAVE_FORMAT` only when an old save would be read *wrongly* rather
than incompletely.

## Making the Windows build

`Build EXE.bat`, after a Clean and Build. It runs `jpackage` over the shaded jar
plus a trimmed Java runtime and writes `dist\CityBuilderSim\`, containing
`CityBuilderSim.exe`. That whole folder is the game — about 110 MB, carrying its
own Java runtime, so it runs on a machine with no Java installed at all.

The type is `app-image`, not an installer, deliberately: Steam does not want an
`.msi`, it wants the game's files and it does the installing. It also avoids
needing the WiX toolset.

The module list is not guesswork — `jdeps` was run against this exact jar.
`jdk.unsupported` matters more than it looks: Gson reaches for
`sun.misc.Unsafe`, and that module is not in the default `java.se` set.

To hand the folder to someone, zip it. Expect **SmartScreen** to block an
unsigned exe: *More info → Run anyway*.

## The source tree

```
src/main/java/ham/citybuildersim/     143 files
    CityBuilderSim.java               the launcher
    Game.java                         the month, and the seam every system meets at
    SimulationEngine.java             the order the month runs in
    UserInterface.java                every screen
    Sector.java                       the template every business extends
    Sectors.java                      the registry — the only list of them
    sectors/                          the seven sector classes
    *Check.java                       the harnesses
    AllChecks.java                    the runner
    LongPlaytest.java                 4,002 months, audited every one
src/main/resources/buildings.json     the balance file
```

**Adding a sector** is one class extending `Sector`, one key constant, and one
line in `Sectors.KEYS`. A sector class is a declaration of about twenty lines —
what it makes, what it uses, what it stocks, and any hook it overrides.
`sectors/Mining.java` is the shortest one and the shape to copy. Before the
template the sectors were five handlers in five shapes named by hand in about a
hundred places; the whole point of it is that the eighth costs an afternoon.

**The month is a sequence, not a set.** `SimulationEngine.simulateMonth()` and
`Game.nextMonth()` hold it, and most of the hard bugs in this project's history
have been a line that made it into one phase and not the other. The standing
rule that follows from it: **a flow cannot be reconstructed from the state a
month ended in**, which is why the save carries income statements, the VAT
ledger, inventory in units, wage history, loss streaks, plots consumed, last
month's household shapes and six months of prisoners.

## Versioning

The version lives in exactly two places and they have to move together:

- `GameVersion.VERSION` — the window title, and every save
- `APPVER` in `Build EXE.bat` — jpackage stamps it into the exe and cannot read
  it from the Java side

Bump both on every release.

## Documentation

The full model — every system, constant and measured figure, and the open
questions with what has actually been measured about each — is kept as a
separate living manual rather than in this file, so that a headline number
moving does not mean editing the README:

- **The manual:** https://claude.ai/code/artifact/5702a183-08f7-4004-a87b-f8518dffad19
  *(currently private to the author)*
- Per-batch design notes and the running to-do list live alongside it.

Beyond that, the code is the documentation. Class headers carry a `WHY` section
explaining what the thing replaced and what went wrong with the previous
version, because the reason a formula has its shape is the part that is
expensive to rediscover.
