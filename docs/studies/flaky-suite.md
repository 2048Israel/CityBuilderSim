# The tests that break when another batch moves the played city (FS1, a study)

Every harness in the suite sorted by how each of its sections gets its
condition, so that the sections that read a played city can be made to cause
it: 224 harnesses and 2,619 sections on main 1f6a9da1a (0.9.153), the 543
that read a played city each judged (kept played, or caused and how), sixteen
sweep batches that take them (FS2-FS17, section 6), and one call for Jerus on
the 77 sections on the wall clock (FS1-C, section 7).

**WHY.** Since 2026-11-16, twelve of the 26 queue rows that read as defects
were the suite's own fragility, not the game's (`docs/queue/FS1.md`): a
harness read a city played for hundreds of months at a month or a tolerance
measured on main's path, another batch moved the path (OX1's households,
LX1's balance, HN1's homes), and main's right code went red at a stranger's
gate. Each was mended one row at a time, a tolerance measured again on the
new path or the premise made an Observation (PO1), and the batch that mended
it found the next: the FB ships removed 11 rows and added 11. The standing
rule is already "a fixture must cause the condition" (`docs/contract.md` 1);
most of these harnesses were written before it held. This study finds every
place the suite still waits for a played city to happen on its condition, and
sizes the work to cause it instead, harness by harness, in batches one gate
can carry.

The tables: [flaky-suite/by-harness.md](flaky-suite/by-harness.md) (one row
per harness: its counts, its city, its sweep, the batches at whose gate it
went red), [flaky-suite/by-section.md](flaky-suite/by-section.md) (one row per
played or clocked section, with its kind and verdict) and
[flaky-suite/red-lines.md](flaky-suite/red-lines.md) (main's red lines and the
kept gates' reds, each classed).

## 1. How it was read

- **The source.** Ten read-only slices (Sonnet, xhigh, run one at a time) read
  every harness in `AllChecks.HARNESSES` from the worktree at 1f6a9da1a: its
  header's "what this has to prove", each section's code and the helpers it
  calls, and every borrowed fixture's definition, to see whether it plays
  months. A throwaway script outside the tree first marked each printed
  section with crude markers (LongPlaytest calls, month calls, loops, phrases
  like "some month", clock calls, Observation sites); the markers were a
  reading list, not a verdict, and the slices classed the code. Where a
  harness prints its titles from a variable the slices counted its real
  sections (MapCheck 49, not the 40 the markers found).
- **The kept gates.** 316 kept gate runs (`/tmp/cbs-gate-*`, read only) from
  2026-11-11 22:33 to 11-18 22:29; 187 of them have a suite, each `NEW red` and
  `known red` line tallied by batch. R1d's whole-suite failure and the SH runs
  are left out as noise.
- **Main's record.** `docs/notes/known-reds.txt` and `docs/notes/gate.txt`:
  every red line on main today, each classed by the section it comes from
  ([red-lines.md](flaky-suite/red-lines.md)).

The brief counted 221 harness files on 79e336def; 1f6a9da1a has 224.

## 2. The classes and the kinds

One class per section; a section with any played assertion is (b), and a
section both played and clocked counts in both.

- **(a) caused.** The fixture puts the condition there (a town built for it,
  the state set directly, a lever pulled, a dial set), and the assertion holds
  by the mechanic whatever path the city took. Months may run; the fixture's
  cause decides the reading. Pure data, formula, geometry and drawn-state
  sections are (a).
- **(b) played.** Months run (`LongPlaytest.run`, the advisor or autopilot,
  the harness's own loop, or a borrowed fixture that plays) and the section
  reads what the played city happened to become. Four kinds:
  - `identity`: asserted on every month or state of a run and true of any
    state: an audit to nought, a register whole, books that balance, saved
    equal to loaded, a twin equal by construction, the map equal to the model.
    The played city gives it breadth.
  - `found`: the premise is an event the run must happen to reach ("in some
    month", "at least one", "a city that has a Y").
  - `picked`: read at a picked month or after a fixed count of months, or
    against a bound measured on main's path (`TRACK_SAVINGS` .016 "on main's
    grown city", `LIFT_AT` "measured on main's town", "nine in ten nodes").
  - `order`: one figure computed by two paths or in two orders, compared to
    the bit or to 1e-9, which a moved path can part with no law broken.
  - `(obs)` after a kind: the premise is already wrapped in
    `Observation.reached` (PO1, 2026-11-13): it prints OBSERVED and skips its
    laws, so it never fails a gate, and its laws are not proved either.
- **(c) clocked.** A wall-clock bound or wait (`nanoTime` budgets, `_MS`
  dials, an `await`, `sleep` or `get` with a timeout, frames counted against
  time), or a reading that moves under the machine's load (a worker's result
  by a deadline, nodes handed back after a timed redraw). Through `Timing` it
  is soft in every run but `--timing`, judged nightly; a plain assert is hard
  at every gate.

**The verdicts.** A (b) section is `KEEP` (it truly needs a played city: an
identity over many states, nothing short of playing reaches them) or `CAUSE`
(a fixture that would cause it, named: its own town, a builder it can borrow,
a setter, the model's constant to assert against, the threshold from both
sides). A (c) section is `COUNT`, `TIMING`, `NIGHTLY` or `KEEP` (section 7).

## 3. What the suite is

| | sections | |
|---|---|---|
| all | 2,619 | in 224 harnesses |
| (a) caused | 2,027 | |
| (b) played | 543 | identity 167, found 258, picked 104, order 14; 66 of them already Observations |
| of (b), to cause | 425 | 67 of them have a KEEP half beside the CAUSE |
| of (b), kept played | 118 | identities, the run's own audits |
| (c) clocked | 77 | 28 of them also (b) |

By harness:

- **Caused throughout (77):** CopperBuffer, BuildingData, Branding, Credits,
  Packaging, MoneySign, Books, Water, Hydro, WorldData, Nations,
  WorldMeasures, WorldMoneys, Partners, VaultMoneys, PegAnchor, GoldPrice,
  EarthFounding, EarthDeposits, Policy, PolicyDates, Land, LandClass,
  RunWorks, Zoning, ZoningView, Mining, DebtExchange, DebtMoneys, Market,
  Expectations, Formats, OrderBook, Robustness, MainLine, RailDraw, Junction,
  PlaytestFix, BuildMenu, ChartFit, Toast, Consumption, Medicine,
  BusinessServices, Manufacturing, Agriculture, FoodIndustry, TradeCost,
  Restaurants, Refinery, RefineryView, OilView, QuayRail, FreightView,
  Quarrying, Forestry, WoodProducts, IndustryView, Chart, Timing, Observation,
  Stale, Ensemble, Crossing, BridgeMap, WantedPosts, Electrolyser, Recipe,
  HistoricalRecipe, HistoricalLicence, LicenceFees, PublicLicence, Research,
  Discovery, GovernmentPlayer, ER1, PostwarRecipe (each `...Check`). They are
  the shape the rest are swept to.
- **Played, and rightly (6):** RealDollar, Materials, HydrogenSteel,
  SulphideLeach, Equipment and Fitting read a played city only for
  identities; with Determinism and Replay (identities, and a clock) they stay
  as they are, the clocks aside.
- **Clocked only (11):** World, Grid, Earth, WorldMarket, EarthTerrain, Globe,
  QuayCrane, IslandNet, PartnerCalls (every bound through `Timing` already),
  KnownReds and ResearchPreview (a plain bound each: FS3).
- **With played sections to cause (128):** the sweeps' 127 and LongPlaytest,
  whose layout ring (CL7's line) stays with CL7, its audits with the run
  (★1).

**What has gone red.** Of main's 70 red lines and the kept gates' reds
([red-lines.md](flaky-suite/red-lines.md)), 47 are (b): 17 `order`
(DenominationCheck's eight "the same ..." twins, PriceLevelCheck's seven, two
of RoadCheck's), about 20 `picked` and 10 `found`; 7 are (c); 4 are KRT1's
"kept equal to fresh" broken on another path, not yet traced; 7 are real
faults the harness found (ShortSellingCheck's register twice, PopulationCheck,
PlanCheck 6 twice, YearBookCheck, ResearchViewCheck's exit); the rest are
lines caught by their text (LandNationsCheck's expected exception lines) or
unclear. At the kept gates the harnesses most often NEW red on another
batch's code were KeptCheck (10 batches), PriceLevel, Map and LandNations (4
each), ResearchView (4), Prize, LongPlaytest, Landmass and CopperRefining (3
each).

**Since the reading.** HL1 (0.9.154) merged on 2026-11-19 with no new
section in any harness, and filed four rows: BuildAdviceCheck 7's streets'
card (a played `order` section the slice had classed caused: the harness
recomputes the least with a filter the card does not use, and HL1's short
city parts them at x1000; a 544th played section, FS12's), DistressBleedCheck
5's two lines (HP2 item 4's class, FS9's), both filed to FS1 and re-filed to
their sweeps by FS1's ship (section 8), and MapCheck 1's track accounting
(HL1-M's, an identity that found a real fault). The counts above are the
reading's at 1f6a9da1a.

Three of the brief's twelve rows class otherwise on reading the code:

- **PX2 is a real fault**, not a moved path. PlanCheck 6's + floor is an
  identity (every district's plan keeps it at every 30th month), and LX1's
  city found a plan that breaks it: district (0, 0) with two + junctions 7
  plots apart against `LATTICE` 8. Its row stays as it is; FS6 must not run
  beside it.
- **MQ1 is played, not loaded.** MonetaryCheck's steady town settles on the
  price index's rate at a month found on main's path (97), within an
  allowance measured there: (b) `picked(obs)`. It goes to FS10.
- **KRT1 is unclear.** A `Text`'s bounds in the kept Wood Products page
  (48.2x15.0) against a page built new (48.2x16.0) is either a layout that
  differs run to run (c) or a real kept fault on that path; FS4 traces it
  first with the model and the clock held still.

## 4. Which played sections stay played

A section stays played when it is an identity: a law true of every state,
whose played city is its breadth, not its premise (★4). The audits to nought
in LongPlaytest and the money harnesses, the registers kept whole, reload
equal to live (ReplayCheck, ReloadCheck, SaveFileCheck's long cities), the
twins equal by construction where the twin is built and not found
(DeterminismCheck), the map equal to the model. Nothing short of playing
reaches a city's late states, and a red there is a fault on that path, so it
is queued as a bug, not mended in the harness: PX2 is the case.

Everything else is caused: a premise found in a played city is a premise a
fixture can put there. The `(obs)` sections are among them (★3): an
Observation keeps a gate green but proves nothing on the path where the
premise is not reached, and the 66 of them are 66 laws not run at most gates.

## 5. The rules every sweep keeps

- **★1. A sweep edits only its own harnesses.** No sweep edits LongPlaytest,
  and none changes a fixture builder another harness borrows unless the
  borrowers are in the same sweep (grouped so: `MapCheck.playedCity` with
  PlanCheck, `CopperMiningCheck.copperTown` with CopperRefining,
  HaulageCheck's base with HomeRail, `SaveSlotCheck.grownCity` with
  ReplayCheck). A builder borrowed across sweeps (`ChildcareCheck.town`, by
  AutoBuild in FS12 and by Auction and WorksFinance in FS15;
  `AutoBuildCheck.noGround`, by IslandLink) is left as it is, and a borrower
  that needs a premise builds it on its own scratch city. A sweep's ship touches no game class, so its gate is
  the picked harnesses alone (`tools/pick.sh`: the harness, its borrowers and
  TimingCheck), never the whole suite.
- **★2. The order of causes.** In order of preference: (1) the state set
  directly on a scratch city, by the setter the model has (`setCashForTest`,
  `setPolicyRate`, `setSectorCash`, `pinRate`, `restore...`); (2) a town built
  for it (`LongPlaytest.build`, `villageBuild`, `buildStack`, the harness's
  own `town()`); (3) a threshold from both sides, just under and just over,
  asserted against the model's own constant, never a number measured on a
  path; (4) the reading taken at the month the fixture caused it. Only when
  none will do, a bounded "play until the condition holds, up to N months",
  which fails as a fixture when the bound passes (PK1's Done: it may not pick
  a month), or stays an Observation. A setter the model lacks is added in the
  model class as a test hook only if the batch's brief names it (the table
  marks three: "none yet: to add"); otherwise it is the fixture's own build.
- **★3. A caused premise is no Observation.** Where a sweep causes what an
  Observation waited for, the `Observation.reached` call goes, its laws run
  hard, and the harness leaves `Observation.HARNESSES` when it has none left.
- **★4. KEEP stays played.** A sweep does not touch a KEEP section (the
  table's 118). If one goes red at the sweep's own gate it is a real fault:
  filed as a bug row, not re-measured.
- **The labels stay.** A section whose condition is now caused keeps its
  label where the law is the same, so `docs/notes/known-reds.txt`'s fragments
  still find the line until the sweep's ship retires them; a label that must
  change is re-filed in the same ship.
- **Done** for every sweep: each section the table gives the sweep with
  `CAUSE` causes its condition by ★2 and asserts its law against the model's
  constants; the harnesses pass alone and under `--pick`; the known-reds rows
  the sweep's id now carries are retired by its gate (they are vetoed at a
  batch's own gate, so a sweep ships only green on them); each harness's
  header names its fixtures; no section reads a month, a count or a tolerance
  measured on a path. And a check that would catch the old fault still
  catches it: a sweep that turns a played premise into a caused one shows, in
  its notes, the law failing on a fixture that breaks it.

## 6. The sweeps

All after HL1 (it moves the played city and edits six of the swept harnesses);
each sized for one gate; ordered by today's reds (★5): FS2-FS6 take the
harnesses most often red at others' gates and 33 of the 44 red rows the
sweeps carry, FS7-FS12 and FS14 one or two each, and FS13 and FS15-FS17 are
preventive. "Not beside"
names a batch that edits the same harness: the two do not run at once. Each
sweep's sections are its rows in [by-section.md](flaky-suite/by-section.md)
(the first column), each with the cause a slice named for it.

| id | sweep | harnesses | to cause | kept | after | not beside |
|---|---|---|---|---|---|---|
| [FS2](#61-fs2) | the twins and the scale | 3 | 13 | 8 | SZ1 | DK1 |
| [FS3](#62-fs3) | the clocked lines (FS1-C's way) | 5 | 2, and 18 clocked | 2 | FS1-C's answer | HA1-CLOCK |
| [FS4](#63-fs4) | KeptCheck I: the city, the chrome, People, History, KP2-KP4 | 1 | 19 | 5 | SZ1 | |
| [FS5](#64-fs5) | KeptCheck II: the money, Government, Trade, Policy, the households | 1 | 28 | 0 | FS4 | BI1 |
| [FS6](#65-fs6) | the map and the district plans | 5 | 28 | 1 | SZ1 | PX2, PW1, SQ1b, HL1-M |
| [FS7](#66-fs7) | freight, ports and the island ground | 15 | 34 | 10 | | |
| [FS8](#67-fs8) | industry, research and the views' models | 20 | 37 | 13 | RN1-H | CL7, R3b-R |
| [FS9](#68-fs9) | the bank and its borrowers | 10 | 34 | 4 | | QR1, OE1 |
| [FS10](#69-fs10) | the currency, the gold and the central bank | 10 | 31 | 6 | | |
| [FS11](#610-fs11) | health, homes and the households' shops | 13 | 28 | 13 | RN1-H | MR1, OX2, HS1, LQ2 |
| [FS12](#611-fs12) | building: the advice, the card, the auto-builder, the roads | 9 | 31 | 8 | SZ1 | |
| [FS13](#612-fs13) | the fund, the treasury and the trading city | 7 | 28 | 6 | SZ1 | |
| [FS14](#613-fs14) | the Exchange and the bond desk | 8 | 27 | 6 | | HS1, PW1 |
| [FS15](#614-fs15) | the works and the services' town | 4 | 19 | 2 | SZ1 | |
| [FS16](#615-fs16) | schools, work and the households' books | 9 | 37 | 4 | | EDU1, CU2-W |
| [FS17](#616-fs17) | the saves, the history and the previews | 11 | 28 | 15 | SZ1 | PW1-R, FB14 |

"After SZ1" where SZ1 edits one of the sweep's harnesses (AuctionCheck,
AutoBuildCheck, KeptCheck, MapCheck, PriceLevelCheck, SaveFileCheck,
TreasuryCheck, WorksFinanceCheck); "after RN1-H" where RN1-H edits a fixture
the sweep reads (DiscoveryCheck, HousingCheck). Each brief below is the
batch's brief; the common rules are section 5, and the folded rows' reds are
already the sweep's own in `docs/notes/known-reds.txt` (section 8).

### 6.1 FS2

**The twins and the scale:** ScaleCheck, PriceLevelCheck, DenominationCheck
(after SZ1; not beside DK1). Takes HP1 item 3, HP2 items 1 and 3, PK1's
ScaleCheck half and HL1-R item 2.

Every red line here is a twin compared over a path one twin no longer shares:
PriceLevelCheck's town at the factor against today's, whose nudge horizon is
20 of 120 months on OX1's path (main 120), so the `h.*` and `reload.*` series
part after month 20 and the summary lines fail; ScaleCheck's K-times copy
whose savings track within `TRACK_SAVINGS` .016 "on main's grown city" (2.83e-2
on LX1's); ScaleCheck's copy month 165, which holds in 7 of 36 months on HL1's
path (HL1-R item 2); "some partner is given a lane" found in 2 city-months on
main and none on LX1's. Cause each: compare the twins over the months both
are inside their horizon by the model's own horizon (not a count of months);
strike the copy at a month the fixture causes (PK1: "at any month, not at a
picked one"); give a partner a lane by the trade it is given, not found.
DenominationCheck's eight "the same ..." lines are DK1's (a real reform
fault, not beside it); its five played sections are caused on its own two
towns. The sections: Denomination 243, 463, 471, 475, 879; PriceLevel 6-11;
Scale 6, 9; the clocks PriceLevel 6-9 and 11 (10-minute child guards) kept,
Scale 6's bound in `Timing` already.

### 6.2 FS3

**The clocked lines, the way FS1-C recommends:** LandNationsCheck,
KnownRedsCheck, ResearchPreviewCheck, ReplayCheck, SaveSlotCheck (after
Jerus's answer to FS1-C; not beside HA1-CLOCK, which edits SaveSlotCheck).
Takes QW1.

Section 7's table is the work: each plain wall-clock bound becomes a count or
an order with a hang guard far above need, its speed half a `Timing` line
judged nightly. LandNationsCheck 14 (QW1): the hover's 10 s `await` ANDed with
`Timing.within(128 ms)` becomes a wait on the quoter's own completion with a
60 s guard and the eight frames a `Timing` line; the uncaught 10 s
`awaitTermination` in its `finally` (L1734), which kills main, is caught and
reported as one line. QW1's eleven rows go with it: the hover line, the
uncaught exception, and nine lines `AllChecks.FAILING` took from the dead
process's tail (eight of the section's own `OK` lines and its expected
exception). The other
four: KnownRedsCheck 6 (a child JVM within 2 s, plain), ResearchPreviewCheck 9
(a median under 1 s, plain), ReplayCheck 485 (`sleep` of `FLUSH_MS` + 1,200),
832 (frames against time: the correctness half a count, the rate a `Timing`
line) and 1068, and SaveSlotCheck 7c.7 (`FLOOR_SECONDS` 30 s). Two played
sections are caused: LandNations 10 (the founding shelf already holds two
offers across the river, and section 8 buys them by hand: assert on that
shelf with no months) and SaveSlot 7c.1 (fill the lists by hand as 7c.1b
does, and drop `grownCity`'s 180 played months; ReplayCheck borrows that
city, so both change here, ★1). The hang guards in SaveSlotCheck 7e and 7f
(5 s and 10 s on sub-second work) stay. If the pick of KnownRedsCheck is ALL,
the batch asks for a dry run.

### 6.3 FS4

**KeptCheck I: the city, the chrome, People, History and the KP2-KP4 pages**
(sections below 55, with the `pageKept`/`pageReshaped` helpers; after SZ1).
Takes KS1-rest (KeptCheck 49, KT1, KA1, KP2-N, KP3-N, KP4-N) and KRT1's Wood
Products (50) and Construction (49) lines.

KeptCheck plays one city, Danzik, 600 months by the advisor and then about 25
more per section, so by the Government tab it is near month 3,500, and every
page reads whatever that city has. A page's kept rule (a redraw hands back
nine in ten of the first draw's nodes, the kept page equals one built new) is
an identity of the page; what the city has is the premise: a materials
industry, a statement with a debt schedule, a works on site. Cause each
premise on the scratch city the section draws (the town the slices name:
`FittingCheck.village`, `LongPlaytest.villageBuild`, `RefineryViewCheck.town`,
`LandmassCheck`'s city), and take the share against `HANDED_BACK`, not a
figure seen. First trace KRT1 with the model and the clock held still: a
`Text` 15.0 high kept and 16.0 built new is a kept fault if it repeats, a font
read under load if it does not (then the line compares content, not bounds).
Split KT1's run time and the timed sections (5, 10, 13, 20, `pageCost`) into
their own harness in `Timing.HARNESSES` (a `KeptTimingCheck`, say), so KeptCheck is
correctness alone; that changes `AllChecks` and `Timing`, so the pick is ALL
and the batch has a dry run. The sections: Kept 1-2, 6, 8, 11, 14, 21-23,
25-27, 29-31, 33, 35-44, 46-50, 52, 54, and the two helpers.

### 6.4 FS5

**KeptCheck II: the money, Government, Trade, Policy and the households' pages**
(sections 55 and up; after FS4, which splits the harness; not beside BI1,
which tests the Bank page's integrity cards in KeptCheck's city). Takes KFP1, KBP1,
KRW1, CD9-K, KP5-N, KP6-N, KP7-N and KRT1's slider (78) and property rate
(77) lines.

The same city and the same shape as FS4. KFP1: the wage floor is pinned at
the going rate for two lines measured on that path (68's Government Overview,
46 of 395 nodes; the Partners' chart's pixels), and section 63's fund lets its
rule go a month for buys that settle a holding it did not hold, found: cause
the settlement with a holding put there. KBP1: the Bank's Profit page (55)
and "after each choice" (57) handed back fewer than nine in ten after one
month in R6b's dry run and LB5-B's gate, both on main's code (299 nodes, then
93: a block dropped and drawn anew), and keep them when run alone: trace it
held still as KRT1 (a kept fault or a load reading), then cause the page's
content. KRW1: 87's read of the saved cities "within a minute" stalls in a
native file read under load: wait on the worker's own completion (FS1-C), the
minute a `Timing` line if kept at all; 92-93 likewise. The sections: Kept
55-64, 66, 67, 69-78, 80, 82-87, 89, 92, 94-96, 99, 100, 102.

### 6.5 FS6

**The map and the district plans:** MapCheck, PlanCheck, OrderSearchCheck,
BuildingStackCheck, GatewayCheck (after SZ1; not beside PX2 or PW1, which
edit PlanCheck, SQ1b, BuildingStackCheck, or HL1-M, MapCheck 1's track
accounting, a real fault section 1's identity found on HL1's town). Takes BQ1, LM1-R and KS1-rest's
three GT1-T map timing lines.

MapCheck's section 1 plays a 120-month city on the `IRON_SEED` world with a
mine bought at `IRON_AT`, and some twenty sections read it as `city`; PlanCheck
6 and 17 play it again; OrderSearchCheck plays 500 then 1,200 months. Cause:
the sections that want a mine, a term market, a coast, a dense block, each on
a city built for it (`MiningCheck.makeRoom` for the iron, `MapCheck.Squares`
copies for the blocks). BQ1: the card-bond lines look for a 20-year auction
that clears instead of causing one (LM1-D's city sold nothing): cause the
auction's cover. LM1-R: S4's share check checks nothing on main or LM1 (no
arrival reaches a closed cell): cause arrivals in the closed cells. PlanCheck
6's and 17's `allHold` identity stays played (PX2 is its real fault), and
their premises are caused by fixtures the harness has: the avenues by 13's
and 5's, a walled district by 18's field belt and `riverCity`; 13 and 14
assert on `x1` and their own fixtures, the played half dropped. The
three GT1-T lines go through `Timing` and have been soft at a gate since
GR1, as has FS4's chrome line: the four rows are vestigial, and each sweep's
own gate retires them. The sections: Map 1,
2c, 2h, 2h-c, 4, 6-9, 11, 12, 14, 14b, 14d-f, 18; Gateway's coast months and
its save; Plan 6 (its premises), 13, 14, 17; OrderSearch 1, 2, 5;
BuildingStack L195, L241.

### 6.6 FS7

**Freight, ports and the island ground:** Rail, RailDepot, Haulage,
LandlordPort, TruckRoute, Car, Van, Port, TradeBand, Landmass, IslandLink,
IslandRail, Bridge, Slope, HomeRail (each `...Check`). Takes FF1 item 1.

FF1 item 1: LandmassCheck 12's fixture orders Houses at home until the linked
free ground is full, and main fails it word for word in the full form (1,439
buildings drawn on the island): order the Houses and read
`getAvailableSqFt()` in the same instant, no month between (the auto-builder
buys ground in it), its every-month audit kept; 12(AL1) completes the two
terminals at once, as home's is, and derives its ceiling from the terminal's
points over the builders' monthly capacity, not 400 measured. The long runs (Slope 9, Medellin 2,000 months;
Landmass 13, 1,200; HomeRail 9, 1,250; IslandLink 9, 240) are each a real
run's premise ("a real run rides the railway") that a built town causes; the
freight shares are taken against the model's constants (`Freight.RAIL_FLOOR`,
`Good.TRUCKS`, `Ports.tally`). Its 10 kept sections are the audits of those
runs. The sections are the sweep's rows in the table.

### 6.7 FS8

**Industry, research and the views' models:** CopperRefining, CoalMining,
CopperMining, CementConcrete, Oil, Well, GoldMining, Forms1900,
DriverlessTruck, FieldOperators, Prize, ResearchView, Clock, GovernmentView,
Inbox, MoneyWords, Rankings, SectorFlow, SectorStatement, YearBook (each
`...Check`; after RN1-H, which edits DiscoveryCheck's fixture; not beside CL7
or R3b-R, which hold CopperRefining's and Prize's real reds). Takes FF1 item 2.

FF1 item 2: CopperRefiningCheck's "the rule cut an order in at least one
month of the run" (9f, Rotterdam 1,000 months) finds none on main; 9(a)-(e)
already cause the cut with `netExportAt` (rod's net below cathode's), so the
played count goes, or rod is set under cathode on a built town for one
month. CopperRefining 11's premise is caused the same way (a rod mill beside
a cathode-priced refinery idles); its 28-minute child of LongPlaytest's seed
1 stays nightly as the playtest's audit. Research: `Research.restore` sets a city's research state, so
ResearchView 9, 19, 20 and Prize 12, 20 need no played research. The views
(Rankings, SectorFlow, SectorStatement, YearBook, Inbox, MoneyWords, Clock 5)
play a probe city for a figure to show; set the figure. The sections are the
sweep's rows in the table.

### 6.8 FS9

**The bank and its borrowers:** Bank, YoungBank, Mortgage, SupplierCredit,
Credit, CapitalFlow, CarryTrade, Invest, DistressBleed, BankFailure (each
`...Check`; not beside QR1 or OE1, which hold real reds in this area). Takes
HP2 item 4.

HP2 item 4: DistressBleedCheck's floor raised again "with the net count at
the dial" waits for `LIFT_AT`, a timing measured on main's town (recovered at
a net count of 26): set the streak with
`BusinessInvestment.restoreLossMonths` and a desk loan, and call
`planDistressRetirement` at `DISTRESS` - 1 and at `DISTRESS` (both sides), no
`LIFT_AT`. YoungBankCheck plays BankCheck 19's town 120 months for a sector of
one plant to weigh a works beyond the bank's equity and default (OE1's real
red is its "never in resolution"): set a lone-plant sector's loan above
`bank.equity()` directly (`BusinessDebtManager.setAssets`, `issueLoan`), write
it off, and read the ratio against `Bank.CAPITAL_RATIO` from both sides. BankCheck's
nine played sections the same. The sections are the sweep's rows in the
table.

### 6.9 FS10

**The currency, the gold and the central bank:** CurrencyRegime,
GoldStandard, Intervention, PassThrough, ExchangeRate, Foreign, Currency,
InterestAbroad, CentralBank, Monetary (each `...Check`). Takes HP1 item 1 and
MQ1.

HP1 item 1: GoldStandardCheck 9 brackets the month's struck rate by
`goldRate(currencyGap(), getGoldCover())` read after the month, on OX1's town
a cover read inside the month (0.5993): assert against the cover Game
struck with, through a getter, or hold the cover still (no sale, M0 fixed) so
the two ends meet; the bracket is the harness's error, not the model's. MQ1: MonetaryCheck's steady town settles on the
price index's rate at a found month (97 on main, none within 90 months on
CU2): a redesign: hold the currency (`pinRate`, or a rule that holds it) so the
month's delay cannot move the rows, or read inside one window between the
shelf's yearly repricings (months 49, 61, 73, 85); section 7's premises by
`PriceIndex.restore` and the world level set. Nine of the
sweep's sections are Observations (PO1): each caused, each Observation goes
(★3). `CurrencyCheck.defended` and `setStruckRate` cause the peg's states.

### 6.10 FS11

**Health, homes and the households' shops:** Health, Infrastructure,
HousingDoor, Population, DeathRecord, CausesOfDeath, Crime, Sickness,
HouseholdMemory, Housing, LuxuryRetail, Grocery, FoodProcessing (each
`...Check`; after RN1-H, which edits HousingCheck; not beside MR1, OX2, HS1
or LQ2). Takes HP2 item 2.

HP2 item 2: HealthCheck's dear city is "no hungrier than the free one by more
than the price of care", and on LX1's path it is 14.3 points hungrier: the
two cities differ in more than care's price after years of play. Set the
households' savings and the prices on a `HouseholdBalance` and `Healthcare`
at the three scales and read affordability and hunger for one month, the
threshold from both sides. HousingDoorCheck replays the ensemble's AUTO2 seed
12 to month 500 for a landlord at the lender's door: set the segment short
and a door whose bank capital is below the order on a founding city, call
`Game.considerOnMortgage` once, the till just under and just over the door's
price; its every-month watch (L190) stays. FoodProcessingCheck plays up to 600
months until the planner builds: build the shelf and the grocery demand by
hand (L101 and L141 do) and call `plan` once. The 13 kept are the households' audits. The
sections are the sweep's rows in the table.

### 6.11 FS12

**Building: the advice, the card, the auto-builder and the roads:** AutoBuild,
BuildAdvice, BuildCard, BuildersFirst, DeathCareFirst, ConstructionTrap,
ConstructionControl, FoundingYard, Road (each `...Check`; after SZ1, which
edits AutoBuildCheck). Takes HP1 item 2.

HP1 item 2: RoadCheck's "on a busy street the paved road is the least over
its life" is calibrated to today's traffic on its 36-month town; on OX1's
town the advice chose gravel (-66,414 against -65,763), the advice right by
its own figures: set the streets' load either side of the line
(`InfrastructureManager.setLoad`, as sections 10-11 do), and for the bit
compare `BuildAdviceCheck.lifeFigure` summed in BuildAdvice's order.
BuildAdviceCheck 7 (filed to FS1 by HL1): the streets' card at three prices
of ground against the harness's own recomputation, which skips a road whose
opening count is not 1, a filter the card does not use; recompute by the
card's rule and cause the winner at each price by the load. AutoBuildCheck's
decades (600-month runs) cause each hold-back on a town built for it; the
`ChildcareCheck.town` it borrows is left as it is (★1). The sections are the sweep's rows in the
table.

### 6.12 FS13

**The fund, the treasury and the trading city:** Fund, FundLedger, Treasury,
Buyback, Restructure, ForeignDebt, Curve (each `...Check`; after SZ1, which
edits TreasuryCheck). Takes PK1's FundCheck half.

PK1: FundCheck's base is a pinned month (73): build the bank and its owners
by the sections' own fixtures (`ExchangeCheck.bankWith`, the exchange and
equity objects, or a village with a Commercial Bank), and set the exchange's
split state where a section counts units. Treasury's roll-a to roll-d read a
played city's rollovers: set the year's surplus and the notes' due months
(the inputs of `Rollover.netting`) and assert the netting against the
formula; Buyback 3-10 the same with `setPolicyRate` and
`ForeignAccounts.pinRate`. The sections are the
sweep's rows in the table.

### 6.13 FS14

**The Exchange and the bond desk:** FloatLink, Desk, Call, Holders, Bond,
Equity, Exchange, ShortSelling (each `...Check`; not beside HS1, which holds
ShortSellingCheck's real register reds, or PW1). Takes SX2.

SX2: ShortSellingCheck 7's 1,000-month city never buys shares from the world
on LX1's path (63 months on 0.9.137): cause the purchase in one month as
section 9 already does, and keep the 2,000 months' audit, register and
shorts-off identities played. Fourteen of the
sweep's sections are Observations, the most of any: each caused, each goes
(★3). Holders 1-11, Desk 2-7, Call 4-7 read a played city's book: set it
(`setSectorCash`, `setEquity`, `moveFirstCallForTest`). The sections are the
sweep's rows in the table.

### 6.14 FS15

**The works and the services' town:** Childcare, EssentialWorks,
WorksFinance, Auction (each `...Check`; after SZ1, which edits AuctionCheck
and WorksFinanceCheck). `ChildcareCheck.town` stays as it is: AutoBuildCheck
(FS12) borrows it too (★1).

ChildcareCheck's `town()` plays 36 months with sectors held, at x1, x4 and
x16; its L290 and L425 read the lack it found: build the founding by
`buildStack` with no months and place the lack by hand, then call
`orderChildcare` once. WorksFinanceCheck's twelve played sections borrow
that town and wait for a works to want finance: build the premise on a fresh
scratch city as section 4's carried-ground town does (`setCashForTest`,
`setOwnedSqFt`, the need by its restore setters), and a reopened benchmark by
hand on a `Debt` piece, asserted against its own schedule. AuctionCheck 11's 1,200
months on AUTOBUILD stays kept (the auction's audit). The sections are the
sweep's rows in the table.

### 6.15 FS16

**Schools, work and the households' books:** Education, Labour, StaffingTie,
StaffingDraw, ArrivalMix, Owner, Outside, Household, WageLadder (each
`...Check`; not beside EDU1, which holds EducationCheck's real red, or
CU2-W).

EducationCheck's fourteen played sections read enrolment and licensed
heads off a 360-month city ("a poor city at x3 enrols fewer than at x1" is
EDU1's real red): set the licensed heads and the posts directly
(`PopulationManager`), and take the seats against the posts from both sides
on the model's staffing count. Labour's nine and the staffing pair read a
city's posts after play: set `workforceByBand` and the supply, and assert
the cascade law on that. The
sections are the sweep's rows in the table.

### 6.16 FS17

**The saves, the history and the previews:** SaveFile, ReadPath, Reload,
History, Gdp, NewGame, PolicyPreview, MoneyDates, Calendar, BasketYear,
SkipReport (each `...Check`; after SZ1, which edits SaveFileCheck; not beside
PW1-R or FB14).

The 15 kept are the save's identities (saved equal to loaded, a reloaded
city plays on as the one that kept running), and they stay played (★4).
PolicyPreviewCheck's seven played sections wait on a 60-month city to have
claimants and a bank's profit (nine of the sweep's sections are
Observations, most of them its nineteen sites): lay workers off and open a
bank by lever, and read at that month (★3: the sites go). The sections are the sweep's rows in the
table.

## 7. The clocked call (FS1-C)

Jerus's call; recommended as below (★6). Of the 77 clocked sections, most
already go through `Timing` and are soft at every gate (Globe, Earth,
EarthTerrain, World, Grid, Port, QuayCrane, PartnerCalls, WorldMarket,
Bridge, IslandNet, Plan, Scale 6, LandNations 12-13, KeptCheck 5, 10, 13, 20
and `pageCost`, and MapCheck's lines): no change. The plain ones:

| harness, section | the wait | recommended | why |
|---|---|---|---|
| LandNationsCheck 14 (QW1) | a 10 s `await` and eight frames, ANDed with `Timing.within(128 ms)`; a 10 s `awaitTermination` in `finally` (L1734) | **clock-free wait**: on the quoter's completion, a 60 s hang guard; the frames a `Timing` line; the `finally` caught | 10,001.66 ms beside LX1's ensemble; the uncaught one kills main, and one red became eleven rows |
| KeptCheck 87, 92-93 (KRW1) | the saved cities read "within a minute" | **clock-free wait** on the worker's own completion; the minute a `Timing` line if at all | a native file read stalls under load |
| KnownRedsCheck 6 | a child JVM within 2 s | **clock-free wait**, a long guard | a JVM's start is the load's |
| ReplayCheck 485, 832, 1068 | `sleep(FLUSH_MS + 1,200)`; frames against time | **clock-free wait** on the flush; 832's frame rate a `Timing` line | the correctness half is an order |
| SaveSlotCheck 7c.7 | `FLOOR_SECONDS` 30 s of real time | **clock-free wait**, the floor read from the model's clock | |
| ResearchPreviewCheck 9 | a median under 1 s, plain | **`Timing`**, judged nightly | a speed bound, not a law |
| CopperRefiningCheck 11 | a 28-minute child | **nightly**, as it is | |
| KeptCheck's run time (KT1) | the whole harness's minutes | **nightly**, its timed sections split into their own harness (FS4) | |
| KeptCheck 55 (KBP1), KRT1 | nodes handed back after a month; a `Text`'s bounds | **traced** with the model and the clock held still first (FS4, FS5) | may be real kept faults |
| SaveSlotCheck 7e, 7f | 5 s and 10 s on sub-second work | **kept** (hang guards) | far above need |
| DeterminismCheck, PriceLevelCheck | 15 and 10 minutes on a child | **kept** (hang guards) | |

Running a harness alone, outside the suite, is not recommended: a gate would
then run it beside nothing, and the load it fails under is the nightly's and
the ensemble's. The other ways: (b) every plain clock moved to `Timing` as it
is (soft at the gate, but a count that should be hard, such as "the hover
yields within eight frames", goes soft with it); (c) the clocked harnesses
nightly only (QW1's correctness is then judged once a night). FS3 builds the
recommended way unless Jerus says otherwise.

## 8. The folded rows

Each row of the brief against the sweep that takes it; the orchestrator takes
the row off the queue when FS1 ships. Its reds in
`docs/notes/known-reds.txt` are re-filed to the sweep by FS1's ship file
(`## known-reds`: the same harness and fragment, the sweep's id, so the
sweep's own gate vetoes them and it ships only green on them): 44 rows, the
three HL1 filed to FS1 among them, which FS1's own gate would otherwise have
retired with nothing mended.

| row | taken by | red rows re-filed |
|---|---|---|
| HP1 | FS10 (GoldStandard 9), FS12 (Road), FS2 (PriceLevel) | 1, 1, 5 |
| HP2 | FS2 (PriceLevel's lane, Scale's savings), FS11 (Health), FS9 (DistressBleed) | 2, 1, 2 |
| HL1's FS1 rows | FS12 (BuildAdviceCheck 7), FS9 (DistressBleedCheck 5's two lines) | 1, 2 |
| SX2 | FS14 | 1 |
| KFP1 | FS5 | 4 |
| FF1 | FS7 (Landmass 12), FS8 (CopperRefining 9f) | 1, 1 |
| PK1 | FS2 (ScaleCheck's copy month), FS13 (FundCheck's 73 months) | |
| KRT1 | FS4 (Wood Products 50, Construction 49), FS5 (the slider 78, the property rate 77) | 2, 2 |
| KBP1 | FS5 (55, and 57's "after each choice") | 2 |
| KRW1 | FS5 (87) | 1 |
| QW1 | FS3 | 11 |
| MQ1 | FS10 | |
| BQ1 | FS6 | |
| LM1-R | FS6 | |
| KS1-rest | FS4 (KeptCheck 49, KT1, KA1, KP2-N to KP4-N), FS5 (CD9-K, KP5-N to KP7-N), FS6 (the three GT1-T map lines) | 1, 0, 3 |
| HL1-R | item 2 to FS2; the row stays for item 1 (Business Services' kept share) | |
| PX2 | not folded: a real fault (section 3); its two rows stay | |

The other harnesses' real reds keep their rows and their batches (CL7, DK1,
EDU1, HS1, MR1, OE1, OX2, R3b-R): the sweep that shares the harness runs
after or apart from them, never beside.
