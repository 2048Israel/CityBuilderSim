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

The tree is over 250,000 lines; `Game.java` alone is over 15,000, and the
interface is twenty-seven files, the largest about 6,000. Do not read them. Read the generated indexes and jump.

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
    java -cp target/classes ham.citybuildersim.tools.ManualToMarkdown --wrap page.html docs/manual.html   # the pulled manual as a standalone page, then...
    java -cp target/classes ham.citybuildersim.tools.ManualToMarkdown docs/manual.html docs/manual.md     # ...as the Markdown GitHub renders

The design record lives outside the repository, in the claude.ai project
"Civic Ledger: Java Game": `claude/todo.md` is the list of what is open,
`claude/changelog.md` what shipped and when (newest first), `claude/index.md`
a map of the two hundred design notes by subsystem, and the published manual
(artifact "CityBuilderSim", https://claude.ai/artifact/BkBAN1RDiQTpCj79WPbpCp)
is the model written out. The manual is also in the tree, so that GitHub shows
it without a link: `docs/manual.md` (GitHub renders it) and `docs/manual.html`
(the page itself, opens from a clone) are GENERATED from the published page by
`tools.ManualToMarkdown` at every publish - never edit them; change the page,
publish, pull it, and run the tool's two lines (`--wrap` first, then the
Markdown from what it wrote). A session that has been away reads the
changelog's top and the todo's section 0 before anything else.

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
        Game.java                  the month, the seam every system meets at; over 15,000 lines, 41 banner sections
        Motoring.java, LuxuryCounter.java, Offending.java, CityBasket.java
                                   mechanics moved out of Game on 2026-09-18, behaviour unchanged: each is
                                   called from the month and read through Game's delegating getters (the
                                   project's splitting-game.md)
        SimulationEngine.java      the order the month runs in (200 lines - read it whole)
        Sector.java / Sectors.java the template every business extends, and the registry
        sectors/                   seventeen sector classes, RefineryFlow (0.7.80: the refinery's units, the
                                   flow through them) and SpreadPlanner (0.7.82: what a processing sector
                                   orders - the best earnings on cost at the city's own prices, past the
                                   feed, ground, staff and money gates - and the idle-then-shed rule; the
                                   refinery its first client, the materials chains' works next); Mining.java
                                   is the shape to copy
        BondMarket.java, CorporateBond.java, OrderBook.java, InterimLoan.java
                                   the businesses' bonds (0.7.12): the market and each participant's rule, the
                                   bond, the limit-order book the bonds and (since round 2) the shares trade on
                                   - Exchange is the shares' side - and the loan lent after a default (the
                                   project's the-firms-sell-bonds.md)
        Rollover.java              the treasury's rollover of what falls due next month (0.7.13): the setting,
                                   the ledger of the surplus it has netted and the record; Game's ROLLING WHAT
                                   FALLS DUE reads the city and books the issues (the project's
                                   rolling-what-falls-due.md)
        TreasuryFund.java          the city's fund (0.7.14): its cash, its two dials (what goes in, and since
                                   0.7.48 what it withdraws), its rule's statics, the
                                   rescues and the bank's preferred offer as a record; the books themselves
                                   are the register's and the bonds' city holdings, and Game's THE CITY'S
                                   FUND AND THE BANK'S RESCUE runs it
        FundLedger.java, FundView.java
                                   the fund's cost basis (0.7.39): each holding's adjusted cost base by the
                                   average-cost method, what it realized and paid, and the record of what the
                                   fund did, booked where its holdings move - bookkeeping, saved inside the
                                   fund's state - and the pure door the fund's pages read it all through
                                   (positions, the return, search, a ticket's quote, the record); FundLedgerCheck
                                   holds both (the project's spec-fund-0739.md)
        ConstructionControl.java   the player's hand on the construction queue (0.7.22): the city's order of
                                   its own sites, rushes on overtime, cancels and the shells they leave,
                                   demolitions and buy-outs, and since 0.7.70 its gravel roads paved - the
                                   state, saved under one key, and each rule's
                                   arithmetic with its source; BuildingManager applies the crews and Game's
                                   THE PLAYER'S HAND ON THE QUEUE moves the money
        StrategicReserve.java      the city's strategic reserve of crude (0.7.85): what its tanks hold and cost,
                                   the fill ordered and the release standing, the city's side of crude's
                                   clearing (Markets.CityTrader) and the month the next strike settles;
                                   Game's THE STRATEGIC RESERVE pulls the levers and pays - OilCheck 16
                                   holds it (runs/spec-oil.md 2.8)
        Ports.java, BoatSchedule.java
                                   the city's sea terminals (0.7.86): each kind of cargo's berths, the share
                                   of its goods they take before or after the railway, the band they narrow
                                   (Ports.factor(), at the railway's step 5) and the month's tonnes by sea,
                                   saved under one key; and the month's ships as a pure function of time -
                                   the calls those tonnes make, each boat's place on its route, a frame's
                                   query - never saved, the map's to draw (since 0.7.97). PortCheck holds
                                   both, MapCheck 9 the boats on the map (runs/spec-oil.md 2.9-2.10)
        DecisionLog.java           what the player decided, and when (0.7.23): every change of a policy and
                                   every spend at scale, recorded where it is applied, held while a city is
                                   founded or loaded, saved under one key; the History chart's flags
        ChartModel.java            a time chart's arithmetic without the toolkit (0.7.23): the window and how
                                   pan, zoom and ranges move it, the year ticks, nice value scales, the
                                   episode lane's rows and the flags, and since 0.7.50 the copy a chart
                                   draws from and the stack's runs; ui/TimeChart.java draws it, ChartCheck
                                   holds it
        CityNeeds.java             NEEDS YOU, measured (0.7.24): everything with a lever against its own line,
                                   in the panel's order - moved out of ui/SummaryScreen whole, so the left
                                   panel, the header's "Needs you" chip and the Build overview read one list;
                                   and SERVED (0.7.41), the one verdict every service gauge is read by -
                                   supply over demand, its lines turned over
        BuildAdvice.java           the Build tab's categories and measures, each measure's figure before and
                                   after an order by the model's own arithmetic, and the rule behind the
                                   overview's WHAT WOULD HELP MOST (0.7.24; since 0.7.51 priced with its
                                   ground and sized to the businesses' projection, since 0.7.70 a road
                                   over its life, since 0.7.71 a care building the size that fits the
                                   need); advice, not a model change - BuildAdviceCheck, RoadCheck and
                                   ChildcareCheck hold it
        AutoBuilder.java           automatic building (0.7.73): the player's switch and two dials (a
                                   spare margin, a debt limit - since 0.7.81 the city's debt over a year
                                   of GDP, and "Build from cash anyway" for when it is over), and the
                                   month's pass that orders the
                                   build advice's own cards for the city's works within the builders,
                                   the budget and the limit - since 0.7.77 buying the bare ground they
                                   lack as Build's land shortcut would - borrowing on the funding page's
                                   bond; its log, its inbox notices - AutoBuildCheck holds it
        BuildCard.java             one build card's figures for all 101 buildings (0.7.25): what it gives the
                                   city and in what unit, its money and scarce-resource bars, the group it is
                                   compared within and its tags, the investors' word and the first gate it
                                   fails for them (since 0.7.75 every gate, with what it would earn, cost and
                                   take to pay back: appraise()), the verdict on an order; pure -
                                   BuildCardCheck holds it
        SectorFlow.java            one business's month as a flow (0.7.30): each good in and out, its units off
                                   the production rows and its money off the statement, the plant's six
                                   throttles and the rate they multiply to; what the Sectors screen's
                                   Operations page draws; pure - SectorFlowCheck holds it
        RefineryView.java          the refinery's month as a picture (0.7.95): the crude and where it was bought,
                                   the column's cuts, each unit's run, spread and gate, the products and who took
                                   them, the flow traced cut to unit to product, and the pictogram's layout to
                                   scale with its words and colours; what Refining's Operations page paints;
                                   pure - RefineryViewCheck holds it (runs/spec-oil.md 2.12)
        OilView.java               the oil industry on one page (0.7.96): the wells by kind and what they would
                                   lift over ten years if nothing new were built, each pool within its oil;
                                   every kind of refinery unit with its spread and the gate that stops one
                                   more; every product's price and month off the refinery picture's; the
                                   strategic reserve and its levers' reach; the chart and every word; what
                                   Oil's Operations page paints - pure, OilViewCheck holds it (runs/spec-oil.md 2.13)
        SectorStatements.java      one business's month as formal statements (0.7.74): profit or loss through
                                   gross and operating profit, the classified sheet, the cash flow in three
                                   sections, the changes in equity (since 0.7.75 in columns, share capital and
                                   what it kept) and the debt schedule by kind - and the bank's - each a list of
                                   rows, its format by Sector.statementFormat(); every bottom line the model's
                                   own; pure - ui/StatementView draws it, SectorStatementCheck holds it
        PolicyPreview.java         what a staged set of the Policy tab's dials would do (0.7.36): the tax take
                                   under another policy line by line, and THE BUDGET before and after, each
                                   line its owner's read of a detached TaxPolicy.copy() - since 0.7.48
                                   the fund's withdrawal, and since 0.7.52 the rule at each step of its
                                   strictness (CentralBankCheck 21); advice, not a model change -
                                   PolicyPreviewCheck holds it
        Expectations.java          the anchor (0.7.42): expected inflation, the central bank's credibility and
                                   the expected price level every money constant that prices something is
                                   struck at, at the top of every month (Game.restrikeMoneyConstants());
                                   ExpectationsCheck holds it (the project's spec-inflation.md)
        SupplierCredit.java        the grocers' trade credit (0.7.44): what the shops owe their suppliers for
                                   stock bought on credit when the till could not pay, struck at the clearing
                                   and repaid at the next strike out of the sale it stocked, on both sides'
                                   books, the world's share in the money audit; SupplierCreditCheck holds it
        World.java, Resource.java, Deposit.java, CityLand.java, LandConversion.java
                                   the world a city stands on (0.7.56: one seed's coast, lakes, river and
                                   fields of seven resources) and the city's land on it (0.7.57; since
                                   0.7.67 whole blocks of a grid, six offers a side; an older save
                                   converted once) - LandManager and LandMarket sell it, WorldCheck and
                                   LandCheck hold it (the project's spec-land.md and spec-grid.md)
        LandGrid.java, GridOffers.java, LegacyLand.java, GridConversion.java
                                   the block grid (0.7.65 to 0.7.67): the ground owned as a quadtree of
                                   blocks lined up with the world, the six places a side and their
                                   rectangles, spec-land's lanes kept read-only, and a saved city's ground
                                   put on the grid once at load - GridCheck and ConversionCheck hold them
        CityMap.java, TilePainter.java, TileRaster.java, BuildingVisual.java
                                   the city map (0.7.60): the buildings by type in 7.68 km districts,
                                   each drawn once on its own land; since 0.7.88 a tile painted from
                                   its district's street plan, what a plan cannot hold carried to the
                                   next district and the rest packed at the city's edge (since 0.7.89
                                   within bands of CHAIN_BAND districts), and rastered when a screen
                                   asks; the sidecar beside the save; nothing in the model reads it -
                                   MapCheck holds it
        CityRuns.java              the city's highways and railway (0.7.89): laid city-wide, month by
                                   month, on corridors from the founding site's lines - straight by
                                   preference, never moved, the newest end taken first - with the
                                   rail yards near the mines; kept in the map's sidecar, each
                                   district's plan drawn round them (spec-roads-and-ports.md 2.7,
                                   2.8) - MapCheck 8 holds it
        DistrictPlan.java          a district's street plan (0.7.87): the cells it opens and their
                                   layouts, every street with its kind and width - the model's road
                                   drawn as the streets' surface - and every building's box; pure, a
                                   port of the project's roads prototype (spec-roads-and-ports.md 2);
                                   the painter draws from it since 0.7.88; since 0.7.90 an estate
                                   cell's streets sized to what opens it, since 0.7.92 the outer
                                   kinds on industry's leftover ground when no cell is left, since
                                   0.7.97 the refinery's units first of industry in touching cells (one
                                   campus) - PlanCheck holds it
        CityShore.java, SeaRoutes.java, ShipShapes.java
                                   the map at the water (0.7.97): the sea terminals, tank farms and the
                                   reserve's tanks laid once on the shore by water that opens to the sea,
                                   a terminal's quay to its berth, kept in the map's sidecar (FORMAT 6);
                                   each terminal's route out to the world, found once on a 480 m sea grid
                                   and pulled straight to the offing and the abyss; a boat's hull, deck
                                   and colours by its class - the map's, nothing in the model reads them
                                   (spec-roads-and-ports.md 2.8, 4) - MapCheck 9 holds them
        ui/                        the interface: UserInterface.java is the window (about 6,000 lines: the header
                                   and its clock, the rail, the main menu, the panels, dialogs), one
                                   <Name>Screen.java per tab (split 2026-09-18 - the project's
                                   splitting-the-interface.md), Money/Statement/Pieces/Levers (what the screens
                                   share), Ladder.java (every dial, since 0.7.6), Palette.java (the colours, and
                                   its nested Fonts, which loads IBM Plex since 0.7.21), Icons.java, and
                                   FoundingScreen.java (New city's page, 0.7.10, one panel since 0.7.21; the
                                   record it fills is Founding.java), and ConstructionScreen.java (the Build
                                   tab's construction page, 0.7.22), and TimeChart.java (City History's
                                   charts, 0.7.23: pan, zoom, ranges, an overview, named crises, the
                                   decision flags, full screen), and FundScreen.java (the city's fund as a
                                   brokerage, 0.7.39: Finances dispatches its area to it), and MapView.java
                                   (the city map on a canvas, 0.7.61: small in the land office, over the
                                   window on Expand; its arithmetic is the model's MapFrame, LandMap and
                                   MapTiles, which MapCheck holds). The model never imports it.
        *Check.java                ninety-one harnesses, each a main() with static helpers
        AllChecks.java             the runner; its HARNESSES list is the registry - a harness not in it does not run
        LongPlaytest.java          4,000-odd months, audited every one; also the fixture builder harnesses borrow
        tools/                     the index generators, the two look-up tools, Stale (the prose check) and ManualToMarkdown (the manual into docs/); nothing in the game uses them
    src/main/resources/buildings.json    the balance file (ids permanent); consumption.json the basket;
                                         fonts/ the eight IBM Plex files and their licence, OFL.txt (0.7.21);
                                         conversion-saves.json the five saves' land ConversionCheck converts (0.7.66)

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
   (about nine minutes). `AllChecks money bank` runs two.
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
