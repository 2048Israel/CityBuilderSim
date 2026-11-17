# R6b — Research: the near-future rows — notes

Batch R6b, 2026-11-16/17, built headless from batch/R6b (cut from main aeac6e5a, 0.9.130, after CU2). The
brief: `docs/studies/research-near-future.md` section 10 (with 3, 9 and 11) and QUEUE's R6b row, on R6's
recommended calls ★1, ★2, ★6 and ★7, ★3 overturned (Jerus, 2026-11-09: "Keep to 2060"; the 2030-2060 tier
is R6-T's). Coding went to four Sonnet slices (the mark; FE1; FE2; FE4), each diff reviewed and committed here.

## The mark and its dials

- `research.json` learns two optional keys, `"projected": true` and `"window": "2027-2030"` (its readme says
  so); `ResearchCatalog.Entry` reads them (`projected()`, `window()`, `mark()`, `dateWords()`), and a row
  without them is history. Gson leaves a missing key alone and a save holds codes: the keys need no
  `SAVE_FORMAT` change (the driverless kit does: format 55, below).
- `ResearchCatalog.HISTORY_ENDS` = November 2026, the month the catalogue was last read against the world.
  `ResearchCheck` 11 holds every row to it: dated after it, projected with a window holding its year; on or
  before it, history with no window. FE4 (June 2028) is projected, 2027-2030; FE1 (May 2025) and FE2 (Nov 2025)
  are history.
- `ResearchCatalog.NEAR_FUTURE` = January 2025 (the study's rule 6): a row dated from it on is the near future,
  `isNearFuture()`, `nearFuture(code)`; `ResearchCheck` 12 holds those to the F codes and back (★7).
- **Where it shows** (the study's 10.2): the inbox's "From abroad" line (`entry.dateWords()`: "The world has it
  now (2028, Boden, Sweden; projected, 2027-2030)") and its "Found here" line; the research screen's timeline
  dots and found-here rows (`whenWords`), its City History flags (`ResearchView.flagEntries`), and the build
  card's origin and New strip (`BuildScreen.originPlace`), all through `ResearchView.marked()`.
  `ResearchPreview` and `YearBook` print no row's date or world text, so neither changed.
- **The default city's words**: it never knows a near-future row (★1 below), so `knownAtFounding()` there is
  the catalogue less the near future's and the research pages say "founded knowing all 91 of the catalogue's
  discoveries dated before January 2025; the near future's 3 come only to a city founded the 1900 way"
  (`ResearchView.foundedKnowingWords`); `stillToCome()` is 0 there.

## The rows, what each gives and where

- **FE1, the driverless truck** (`research.json` FE1, May 2025, Dallas to Houston, fee). The Truck Depot (139)
  carries a second piece, the driverless kit, ending a new form `Equipment.Form.CAB_DRIVERS`. `Haulage` keeps
  a driven share of the depots' truck room (not saved; `EquipmentForms` sets it each month and after a load
  from the kit's counts, 1 in a city founded today's way): `tonnesATruck()` blends `TONNES_A_TRUCK_MONTH`'s one
  shift (`SHIFT_HOURS` 10) with `DRIVERLESS_HOURS` 20, `driverPostsATruck()` blends `DRIVERS_A_TRUCK` with
  `OVERSIGHT_A_TRUCK` 0.1, and the capacity, the fleet's need, the drivers struck (`strikeDrivers()`) and the
  quote's estimates read them; at a share of 1 each is main's own expression, to the bit. The blend is over
  the trucks the depots keep (`driverlessShare()`, min(trucks, room) over trucks, as `strikeDrivers()` counts
  them): a truck past the room stands on the street, under no kit, and is driven (REV-R6b-1). The kit's gain is
  `EquipmentFit.driverGain()`: the drivers' wages less the oversight's, and the capital month
  (`Haulage.truckCapitalMonth()`) of the trucks no longer needed; `LicenceFees` bills FE1's fee on it unchanged.
  `FreightView`'s plant plan reads the live figures; the trucks' info text names both tonnages.
- **FE2, sulphide heap leaching** (Nov 2025, Johnson Camp, Arizona; needs E12). A new template, the Sulphide
  Leach & SX-EW Plant (153, Copper Mining): the oxide plant's 1,000 t of cathode and 3,500 t of acid on one
  SULPHIDE site, its dig `CopperMining.SULPHIDE_LEACH_DIG` = 1 / `SULPHIDE_LEACH_RECOVERY` (.85), its water 1,876
  units (71 L a kg, sourced). It runs on `CopperMining`'s own digs by (type, dig) with no change; the page
  shows its cathode apart from the oxide plants'.
- **FE4, hydrogen-reduced iron** (projected June 2028, Boden, Sweden; needs PE1 and E4). A new template, the
  Hydrogen Steel Works (154, Heavy Industry, `HeavyIndustry.HYDROGEN_WORKS`): pellets (1/0.65 t a tonne) and
  power (`HYDROGEN_MWH_A_TONNE` 3.48 over `HydroSites.KWH_PER_UNIT`, 483,333 units) into 100,000 t of steel, no
  coke, coal, limestone or scrap. Heavy Industry now uses PELLETS, so its pelletising plants' pellets go to the
  works first (captive); with no works the captive need is 0 and the arithmetic is a made-only good's
  (`HydrogenSteelCheck` 5, to the bit). The sector page gains a "pellets, bought" block at 0, as every other
  good it uses has.
- **Named for their owners' batches** (the ship file's pending): FE3 and FE6 after PW2 and PW4, FE5 after a
  storage batch, FM1 and FM2 after R2f and the gap's mRNA row. Nothing of PW's or FM2's was built.

## Saves: format 55, and the floor with it (REV-R6b-1)

`GameVersion.SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` both go from 54 to 55, by the rule (an old save would
load wrongly), with no conversion code. A format-54 save holds no unfitted count for the depots' driverless
kit, a piece this batch adds, and the load path reads a missing count as fitted (`Game`'s
`restoreUnfitted`), so `EquipmentForms.setDriven` would set a 1900 city's driven share to 0 from its first
month, in 1930 as in 2030: twice the tonnes a truck, the drivers laid off, no kit bought, no FE1 fee. A
format-54 save is now refused, naming the build that opens it. No field is added; `GameVersion`'s 55 block
says the same.

## The calls (balance), each taken on the recommended way

- **★1 A city founded today's way never knows a near-future row** (from `NEAR_FUTURE`): `Game`'s template rule
  and `EquipmentForms`' driven share. Others: (b) on its date in every city; (c) known from month 1, as every
  other row is there. Why: research, recipes and the research screens run only in a city founded the 1900 way;
  `buildings.json`'s figures are today's, and a near-future row is not; the default playtest, the gate's
  audited city, stays today's to the bit.
- **★2 FE1's hours: `DRIVERLESS_HOURS` 20 against the 10-hour shift** (est.; unpublished, the study's 3.2).
  Others: (b) 24; (c) hold the row. Why: the hours-of-service rule (49 CFR 395.3) binds the cab, not the truck;
  loading, fuelling and upkeep remain.
- **★3 FE1's oversight: `OVERSIGHT_A_TRUCK` 0.1 a truck** (est., unpublished). Others: (b) none; (c) a third.
  Why: remote assistance and terminal staff are real posts; the builders publish no ratio.
- **★4 FE1's kit: half a truck's price for each truck the depot keeps** (MACHINERY 802.24, est.). Others: a
  truck's whole price; a tenth. Why: nothing on price is published (the study's 3.2); half a truck is a large outlay that pays over its 120 months only where drivers cost much.
- **★5 FE1's wear: a driverless truck wears by the month as a driven one does** (the game's calendar life).
  Other: by the tonne, so it wears twice as fast. Why: the game prices a truck's life in months
  (`Sector.VAN_LIFE_MONTHS`) everywhere; the gain then counts the capital of the trucks no longer needed.
- **★6 FE2's recovery: 85%, the builder's ceiling** (est.). Other: the oxide plant's 72% (`LEACH_DIG` 1.4).
  Why: the row is the sulphide process the builder sells; its stated ceiling is the only figure.
- **★7 FE2 is kind "building", size "recipe"**: a process on an existing plant's frame (the convention for a
  row that opens a building but is a recipe-sized find). Other: size "building".
- **★8 FE4 is one integrated works, pellets and power into steel.** Others: (b) a new DRI good and an arc
  furnace recipe; (c) pig iron as a stand-in. Why: the study's 3.48 MWh is a tonne of liquid steel, the game
  has no DRI good, and a new good moves `Good`'s order and every market's.
- **★9 FE4's figures: 0.4 of a Blast Furnace plus 0.8 of a Large Mini-Mill** (cost, posts, ground, water, est.).
- **★10 FE4's date: the study's projection, June 2028**, window 2027-2030 (Argus, N8, refused the read; the
  figure the study had stands).

## What the checks showed

Each new harness was run once on main's own classes (aeac6e5a, the branch's harness classes beside them):
- `DriverlessTruckCheck` red on main: `NoSuchMethodError: Haulage.setDriverShare(double)` before its first
  section. On the branch 66 ok: the share's dials to the bit at 1; the kit NOT_KNOWN the month before FE1 and
  known at it (or from an earlier find); its gain `driverGain()` at the city's figures, verdict PAYS; every
  depot fitted, the tonnes a truck at 6,000, the need's trucks halved and the drivers struck by the formula;
  the fleet then raised past the depots' room (200 room, 100 on the street; REV-R6b-1): the fleet's tonnes
  1,500,000, the street's trucks at 3,000, the posts 120, the drivers struck on the room alone. That line run
  once on the review's Haulage (eb55dd31's, compiled apart, ahead on the path): red by value, 1,800,000 t
  against 1,500,000 and 30 posts against 120 (the street's trucks driverless too). One depot of two fitted, the share .5; a city founded today's way at exactly 1. **Played count**: a 1900
  village 24 months from FE1's month, the share below 1 in 24 of 24 (the fixture fits every other piece by
  hand: the village has no builders, so a 6-point electric-drive job held the fitting queue's head).
- `SulphideLeachCheck` red on main: "fixture: Sulphide Leach & SX-EW Plant (153) and the Leach & SX-EW Plant
  (123) are in the catalogue, in Copper Mining FAIL", then a NullPointerException (no FE2). On the branch:
  the row; known from FE2's month in a 1900 city, never in one founded today's way; a played month's cathode
  digging `SULPHIDE_LEACH_DIG` a tonne exactly, acid bought, no concentrate, no smelter. **The plan** on a
  sulphide-founded town: 153 earns 1.358% of its whole cost a month, the Mine & Mill 1.029%, the Large Mine
  1.055%. **Played count**: 24 months from FE2's month, 0 built at the sector's own money gate, 1 with it set
  aside (a 1900 city at 2025 clears no copper plant at its own gate, as `RefineryCheck` finds).
- `HydrogenSteelCheck` red on main: "fixture: the Hydrogen Steel Works (154) is in the catalogue, in Heavy
  Industry FAIL", then a NullPointerException (no FE4). On the branch: the row against
  `HYDROGEN_MWH_A_TONNE` and `KWH_PER_UNIT`; known from FE4's month; **where it pays**: at the model's own
  tariff (0.000259 a unit) in a city whose fabricators import steel it earns 532.57k a month on 68,751k
  (0.775%) against a hurdle of 389.33k and `plan()` orders it; at 0.001049 a unit (4.1 times; break-even
  0.000654) 246.09k (0.358%), refused at the money gate. With its steel exported it clears on no tariff, free
  power included: it pays where it replaces imported steel, as the integrated route does (MaterialsCheck 11).
  Captive pellets from the city's plant first; with no works the made pellets' arithmetic to the bit.
  **Played count**: 0 built in 24 months from FE4's month, four runs (two cities, the money gate kept and set
  aside): the tariff is the utility's again each month, and on it the works does not clear. The planner bills
  362,500 of the works' 483,333 units a unit of tariff: 0.75, 1 - `LicenceFees.SHARE`, FE4 being patented and
  the world taking a quarter of the gain; section 3 asserts it against the constant (REV-R6b-1).
- **A real run**: the default playtest builds, fits and drives none of the three (★1; the trace below).
- Inventories the rows move, each moved to the new truth with its reason in the label: `ResearchCheck` (94
  rows, ENGINEERING 50, needs 29 to 25, months known 32, templates dated by code 33, the F codes' years and
  fields), `BuildingDataCheck` (18 sited rows, copper's four, 44 buying consumables, the dated list),
  `EquipmentCheck` (the depot's two pieces), `MaterialsCheck` (Iron & Steel's captive set with PELLETS),
  `ResearchViewCheck` and `KeptCheck` (the default city's founding words), `BuildCardCheck` (a card for every
  building the city knows, none for the near future's two; the buyers among known buildings). The mark's own:
  `ResearchCheck` 11-13, red on main (7 FAIL on the file's 94 rows, then `NoSuchFieldError: NEAR_FUTURE`).
- **The pick** (`./batch-check.sh --pick`, after merging main f38eafbc, FB1): ALL 215 harnesses
  (`AllChecks` changed); one NEW red, `EducationCheck` "a poor city at x3 enrols fewer than at x1: the trap is
  back", **main's own**: main's aeac6e5a classes give the same line with the same figures (university 1,652 at
  x1, 1,632 at x3, willing .72 and .35). It is a NIGHTLY harness the gate runs only when a pick names it (HA1),
  so no gate saw it. Named in the ship file's known reds, its look queued as EDU1. Review: "not needed - no
  money, saves or threading in the diff". Stale: 0 firm.
- **REV-R6b-1** (the orchestrator's stranger's review of eb55dd31): NOT SHIP, one must-fix and two mends,
  all made: the format to 55 (Saves, above); the truck blend over the depots' trucks only; the hydrogen
  works' tariff slope pinned to the draw x (1 - `LicenceFees.SHARE`). Run alone after it, each green:
  `DriverlessTruckCheck` 66, `HydrogenSteelCheck`, `HaulageCheck`, `FreightViewCheck`, `EquipmentCheck`,
  `FittingCheck`, `Forms1900Check`, `SaveFileCheck`, `ReadPathCheck`; `SaveSlotCheck` all but its one timing
  line (the autosave month's median 32.8 ms against `SaveWriter.CLOCK_MS` 25, hard when run alone; the gate
  runs no timing, and nothing here touches the autosave). Maps regenerated; Stale 0 firm.
- Left as found: `EquipmentFit.cartGain()` still strikes the motor truck against teams at the one-shift
  tonnes (the forms never meet: a 1900 city's horses are gone long before 2025); `BuildingVisual` draws 153
  as a copper pit.

## The trace

The branch's default playtest (2,400 months, `-Dplaytest.trace`, at c46259c4, all the model's changes in)
against main's reference at `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-aeac6e5a/` (made by this batch
from aeac6e5a, for other builders too): **all eight CSVs byte-identical** (bank, bonds, build, house, labour,
month, pop 2,405 lines each, borrow 54). The trace does not move; no ensemble (`docs/batch.md` 6). The
REV-R6b-1 mends traced again at 09f7decd (Haulage changed; the orchestrator's bar), the same run into
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-R6b-mends/`: **all eight byte-identical** to main's again (2,405
lines each, borrow 54). In a city founded today's way the share stays 1, where both methods return before the
blend; the format number is in no CSV. FB1,
merged after, changed harnesses only.

## The dry run

On the orchestrator's go after TQ1 shipped: `sh tools/gate.sh R6b --dry-run` on bc6e0dc6 (main ec481b4a, 0.9.132,
merged in: `AllChecks`' registry took both sides' harnesses, the generated indexes taken from main and regenerated),
run `/tmp/cbs-gate-R6b-WnmIOf8F`. The suite ran in 1,769 s. **Gate exit 1**, on one unnamed line:
`KeptCheck` 55, the Bank's Profit page, "each month hands back nine in ten of the nodes the first draw made, or
more (256 of 299)". The first draw made 299 nodes, and one month made 93, so a block of the page dropped and was
drawn again. Run alone, main ec481b4a's classes (built apart) and this branch's both draw 297 nodes and keep 289,
at most 14 made in a month. Both give the same three reds as each other: the Build Overview's `KEPT_BUILD_MS`
timing (hard when alone) and FB6's two "after each choice" lines. So it is not R6b's code; it is a fixture whose
page does not hold still under the suite's load, FB6's family (KBF1, KF63). It is named in the ship file's known
reds under FB6, with its cause. **Judged again** (`CBS_REJUDGE=1`, the registry with the ship file's rows, on
the run's own `checks/suite.txt`): **exit 3**, known red only, nine harnesses. Main's own: DeathRecordCheck,
InvestCheck, LongPlaytest, MapCheck, PlanCheck, PrizeCheck, YoungBankCheck (TQ1's list). Named in this ship
file: EducationCheck (EDU1) and KeptCheck (FB6's line and this one). Records: `{next}` = 0.9.133, R6b's block on
top, 1 Pending row, 1 Queued row added (EDU1).
<!-- gate-run: /tmp/cbs-gate-R6b-WnmIOf8F -->
