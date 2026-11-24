# PW3 - wind and solar by place and season, and the power planner

Builder: Claude (Opus), headless, started by the orchestrator 2026-11-23, on `batch/PW3` cut from origin/main
0c7f55602 (0.9.195, LP-G1, SAVE_FORMAT 63). The brief: QUEUE.md's PW3 row and
[spec-electricity §5](../studies/spec-electricity.md#5-the-batches), with PW1-P's five gaps (PW1 notes §0, §2, §6)
and PW2's ground finding (REV-PW2).

## 0. Scope (as decided, before the code)

- **Wind, solar and hydro by calendar month**, from EIA's 2025 fleet (EPM table 6.07.B), each month's index its
  capacity factor over the mean of the twelve, so the twelve average exactly 1 [D].
- **Solar by place**: the farm's annual capacity factor at a city's latitude is EIA's US fleet 24.0% times PVGIS's
  yield in the city's 10-degree band over PVGIS's yield at the US fleet's latitudes; its monthly shape is the band's
  own (PVGIS, both hemispheres, the south's months turned six). A generated world (latitude NaN) is the US fleet.
- **The south's season**: a city south of the equator reads SEASON, wind, solar and hydro six months on.
- **Credits fall with their share**: wind's and solar's firm credit falls in a straight line from its own figure to
  a tenth of it as the variable rows' share of the energy rises from 0 to a half (the study's credit(p, share)).
- **The planner** (BuildAdvice's POWER branch, so the advice, the auto-builder and the playtest player all follow
  it): the reserve test first, the plant ranked by its price a firm kW-year in the slice it serves (the screening
  curve, with its fuel); then, when the test passes, the spread, re-dispatched over a year.
- **PW1-P's gaps**: (1) the works' own power counted as supply, (2) a plant on site counted only from when it opens,
  (3) the screening curve, (4) the south's season, (5) a plant's firm supply at its own posts' fill.
- **groundFor()**: the planner prices a plant's ground at what it can buy; a young city assembles a large plant's
  ground over several offers (Jerus, 2026-11-23: "you just buy more land").
- **Research**: E25 (the single-axis tracker) enters `research.json`: it fits the farm PW3 builds (★G). E31 does not
  (★G): Pending with PW7/FE9. E29 is the storage batch's, queued by the ship file.

## 1. Sources

- **EIA, Electric Power Monthly, table 6.07.B** (capacity factors for utility-scale generators not primarily using
  fossil fuels), 2025, fetched 2026-11-23; US public domain. Wind 37.4 37.2 43.1 40.7 32.1 32.2 27.5 23.8 23.1 33.8
  35.4 39.2 (annual 33.8%); solar PV 16.2 18.7 23.6 27.5 29.3 31.9 31.9 29.7 26.7 22.4 16.9 13.1 (24.0%); hydro
  34.7 35.1 38.2 40.8 42.8 39.8 34.2 32.3 26.1 24.9 28.5 36.1 (34.4%). The spec's figures (§2.3-2.4: wind Jan 1.11,
  Mar 1.28, Jul 0.81, Sep 0.68; solar Dec 0.55, Jun-Jul 1.33; hydro May 1.24, Oct 0.72) are these.
- **PVGIS 5.3** (European Commission, JRC), PVcalc with the ERA5 database, a 1 kWp c-Si system at 14% losses, a
  fixed rack at optimal angles and a single horizontal N-S axis tracker; `docs/studies/electricity/pvgis.py` and its
  output `out/pvgis.txt`. The licence: the PVGIS pages link the Commission's legal notice, "content owned by the EU
  on this website is licensed under the Creative Commons Attribution 4.0 International (CC BY 4.0) licence ...
  reuse is allowed, provided appropriate credit is given and changes are indicated". Credit "PVGIS (c) European
  Communities"; the change is the bands' averaging and normalising. Recorded in `docs/studies/licences.md`.

## 2. The calls (★)

- ★A **Hydro by month too.** Spec §2.4 is PW3's section; the river's month is EIA's hydro fleet, normalised.
- ★B **The south turns six months**, SEASON with it (the study's); a city at the equator is the north's.
- ★C **Credits as dials, from the study**: wind's peak credit 0.730 of its average, solar's 2.621 of its average
  [M, the study's dispatch of the S4 load]; the fall to a tenth (CREDIT_FLOOR) at a half of the energy (the study's
  credit(p, share)).
- ★D **The reserve test plans against the year's peak hour, with NERC's margin over it.** `PLAN_PEAK_HOUR` =
  `PEAK_HOUR / (1 - GRID_LOSSES)`, 1.631 of the mean draw (the study's 1.549 at the plants [D, S4: 760,649 /
  491,034 MW], the losses in), and `RESERVE` 0.15 over it: NEEDS YOU's power yellow is `POWER_YELLOW` =
  1 / (1 + RESERVE), so "ahead" is the reserve test passed. `PLAN_PEAK` (the peak block's 1.20 over the losses)
  stays for the wires (`Substations.peak()`: a substation carries the block, not the hour), and water keeps
  `NETWORK_YELLOW` 0.75.
- ★E **One life for every row**: the game's 20-year bond life and its real rate (BuildAdvice.lifeFactor), as every
  other order is valued; buildings.json has no life field and the study's per-row lives would be a new one.
- ★F **groundFor() assembles**: when the standing listing cannot cover, the plan prices the rest at the land
  office's price a square foot (as `WorksValue.GroundPlan` prices ground, so the plan and the value agree) and
  `place()` buys offer by offer (each purchase lists the place's next), until it covers or `ASSEMBLE_MOST`
  offers are bought (4 x `LandMarket.OFFERS`, 96: past the 18 a 325 MW coal plant's 200 acres took in
  LandCheck); null past that. An offer on a landmass the city has not linked counts its linked dry ground alone
  (`linkedSqFt()`, ET5's rule held). Ground bought for an order that is not then placed stays the city's.
- ★G **E25 fits, E31 does not.** The tracker raises the farm's yield a kWp (PVGIS tracked over fixed, by band):
  before E25 is known the farm runs at the fixed rack's yield, after it at the tracked (the undone pattern, today's
  figure the tracked). The tandem module raises a module's watts a square metre, not its yield a kWp: its gain is
  the farm's ground, which is FE9's question, so E31 stays Pending with PW7/FE9.

- ★H **Firm rows alone in the reserve test** (the study's `plan()`): a wind or solar farm is no candidate. Its
  credit falls with the variable rows' share, so each farm added lowers every farm's credit and the firm supply is
  not monotone in the count of farms (`count()` stopped at 8 farms for a shortage no count closes); and a fleet of
  farms as the peak's reserve is not what a planner builds (NERC credits wind at its ELCC, falling with its share,
  which is `creditFall()`). The spread orders a farm where it saves its cost. So "wind alone never passes the test"
  is the planner's rule, not arithmetic: at `MOST` farms the floor's tenth would pass.
- ★I **The firm supply walks the standing stacks**, not the catalogue's templates: a plant off the menu still stands
  and runs (WorksFinanceCheck's highway fixture took the menu's templates away and the city's own plants with them).
- ★J **Each plant's firm kW at its own posts' fill** (PW1-P's fifth gap): a standing stack and an added plant count
  `production1 x credit x` the fill of their own job categories (`BuildAdvice.staffing()`), a hydro station all or
  nothing (its water, `firmFill()`), the founding grid at the pooled utility fill with the added posts in it. Until
  PW3 every plant read the pooled fill, which every wind farm's posts pulled down by more than its firm third added.
- ★K **A plant on site counts from its opening, and HW1's promise is kept.** Each candidate is tested at its own
  opening with what the site has open by then (`WorksValue.openBy()`): a coal plant pending 60 months does not
  hide the loss a diesel can meet in 30 (EssentialWorksCheck's interim power). But on the automatic path a
  candidate opening after the pending first completion is due (`AutoBuilder.promisedMonths()`, HW1's saved
  deadline) counts the site whole while that promise stands: each order shares the builders and puts back the
  plants already pending, so read from their openings the next month's candidate found fewer open and ordered again
  (EssentialWorksCheck's overdue pending, 22 Diesel Stations pending: 2 Oil-Fired, 29 and 12 Diesel and 6 Coal in
  four months). The promise expires as HW1's does; the Build card (no promise) reads every plant by its opening.
  `pendingCapacity()`'s slow-site rule is skipped for power: the per-candidate test does its work, and dropping a
  pending coal plant whole ordered a second coal plant against the shortfall the first already met at its opening.
- ★Q **A late power site is kept, each plant at its revised opening** (found by CopperRefiningCheck 11, seed 1).
  HW1 drops a site past its deadline while the city is short (`pendingCapacity()`): every plant on it counts
  nothing. In seed 1's 1960s boom the builders' queue stretched (coal quoted 129 months by m720), each deadline
  passed, the site was dropped and the whole shortage ordered again (m725: 2 coal, 6 turbines, 27 diesel and 7
  oil-fired in one month, nothing pending the next), each order crowding the builders and slipping the next
  deadline. 19 coal plants and 28 diesel later the planner's firm supply stood at 7.1 GW against a 1.13 GW peak
  hour; the cheap power drew a copper chain that ended insolvent (67 smelters, 70 refineries, 155 Rod Mills at
  m2400, $1.875T of debt on $1.853T of assets) and the section failed. The real law: a planner counts a unit under
  construction at its expected in-service date (NERC's Long-Term Reliability Assessment counts units under
  construction among the anticipated resources) and orders more only for what opens sooner, which is ★K's
  per-candidate `openBy()` reading of the builders' queue. So power's site is kept past the deadline, and
  `promisedMonths()` reads NaN once the promise has expired. HW1's late order still comes where a candidate opens
  before the plants pending (EssentialWorksCheck: 17 Diesel Stations past the deadline, 2 of the 37 pending opening
  after the candidate). Seed 1 now ends with no copper works but 6 Wire & Cable Works (CU2's run: 1), 0 Rod Mills.
- ★L **The screening curve ranks the reserve order** (rule 1): a candidate's twelve months of capital and ground over
  `lifeFactor()` and of its fixed running, plus its running a kWh for the slice's hours, over the firm kW of the gap
  it serves; the slice from where the firm supply sits against the blocks' loads over the losses. HW1's tier (an
  order that closes the gap before one that cannot) still comes first.
- ★M **The spread at today's draw** (rule 2): the twelve months dispatched again with one more unit
  (`UtilitiesHandler.yearRunning()`), the fuel and the shortage it displaces against twelve months of its capital
  over its life and its fixed running; one unit a suggestion, the best net a dollar of capital first.
- ★N **Solar's anchor is the tracked US fleet**: EIA's 24.0% is a fleet mostly on single-axis trackers, so
  `production1` is the tracked figure at the US fleet's places and a fixed rack is `SOLAR_US_FIXED` (0.794) of it
  there, PVGIS's ratio. A solar farm's credit is scaled by its output in the year's peak month here over the US
  fleet's then. Wind has no place figure: its months turn with the hemisphere, its year is the US fleet's.
- ★O **E25 is equipment, no fee**, dated December 2007 (Nellis, 14 MW), its effect PVGIS's tracked over fixed by
  band; a 1900 city's farms stand on fixed racks until its research finds it (`UtilitiesHandler.trackers()`,
  PowerPlanCheck 1).
- ★P **Fixtures restated** (Jerus's rule, 2026-11-23: check reality first). Each red was new behaviour that is the
  real law, so the check keeps what holds and states the real effect:
  - the reserve plant is a firm row where it was a Wind Farm (★H): WorksFinanceCheck's highway promise and
    independent pending load, EssentialWorksCheck's interim power, overdue pending and scarce cash, with the Diesel
    Station (1910) dated to the town's year (PW2's lever); each premise now asserts the firm row is the proposal;
  - EssentialWorksCheck's overdue pending: twice the advice's count pending (a quarter more ran short of the
    forecast two months on, and the planner's five more were its own reading); its loaded twin is given the
    fixture's lever as it is given the sector holds (a template's date is the catalogue's, which no save carries:
    without it the twin knew no Diesel Station and ordered nothing where the unbroken city ordered 17), and the
    label prints both cities' orders;
  - the peak hour and the reserve (★D): BuildAdviceCheck 4b and 14, BuildCardCheck 10 (power at `POWER_YELLOW`,
    water at `NETWORK_YELLOW`), NewUnitCheck 11 and EssentialWorksCheck's mill read `PLAN_PEAK_HOUR`;
  - WorksFinanceCheck's carried ground: the unavailable plan is past `ASSEMBLE_MOST` offers, and a plan within it
    is priced past the listing at the office price (★F).
  - HydroCheck's fiat sites: a station's water is its average times the river's month (March 1.1086 on the
    generated world's north calendar: EIA's fleet runs high in spring), where it was its average exactly; the
    levelling under the water holds, and a new line asserts the twelve months average the year.
  - WaterPowerCheck 5: the licence's moved kW run at the coal plant's own posts' fill, as the merit order runs every
    plant (★J; `UtilitiesHandler.plantFill()`, new, the figure it reads), where it expected the pooled fill.
  - PublicLicenceCheck (a): the steam turbine's kW (PE6) run at the coal plant's own fill, the same restatement (the
    fee 235.66 where the pooled fill made it 329.41: the plant's posts fill at 0.715 of the pool's).
  - MaterialsCheck 4: what the grid generates is the founding grid at the pooled fill and each plant at its own,
    the works' surplus on top (129,053.32 kW: 128,813.32 generated and 240 sold), where it expected the pooled fill
    times the whole nameplate; the net draw and the surplus held exactly as before.

## 3. Old saves

**No format bump.** PW3 saves nothing new: the months, the credits and the planner read the city's latitude (its
founding), its research (E25) and what stands; `AutoBuilder.promisedMonths()` reads HW1's saved `pendingUntil`;
the counters are not saved. An old save loads as it is: E25 is known or not by the research it holds, a
1900 city finds it as any research. Nothing an old save holds is read differently.

## 4. The checks

Run here, one at a time (`./batch-check.sh <name>`), the earth and world data set:

- **PowerPlanCheck** (new, spec §4), green, its eight sections: the south's months six on and the sun by place and
  rack (a fixed rack under a tracker in every band, each place's months averaging its year, E25 turning the rack);
  the credit's straight fall to the floor; the firm supply (the works' own power kW for kW, a wind farm raising it by
  no more than its credit, a pending coal plant counted from its opening); the reserve test ordering coal short at
  the base and a peaker short at the peak alone by more than a coal plant; a wind-and-sun city with no reserve order
  and the same city with a reserve order once it knows a diesel; the spread ordering wind beside diesel (a farm saves
  186 a year against its 149) and nothing beside coal (26 against 149); a coal order's ground assembled past the
  listing, none past `ASSEMBLE_MOST`; each suggestion counted once (here: 5 reserve, 1 spread).
- **AutoBuildCheck, BuildAdviceCheck, WorksFinanceCheck, EssentialWorksCheck**: green on the final code, with ★P's
  restatements.
- **BuildCardCheck, NewUnitCheck** green after ★P; **PowerDispatchCheck, ResearchCheck** (241), **PowerPlantsCheck**
  (138), **PowerViewCheck, ReadPathCheck, SubstationCheck, LandCheck, ResearchViewCheck** (219),
  **ModernEngineeringCheck** (207), **DiscoveryCheck** (270) green on the first round's code.
- **CreditsCheck, SaveFileCheck, PowerPlantsCheck, PowerViewCheck, PowerDispatchCheck, SubstationCheck, LandCheck,
  PowerPriceCheck, ElectrolyserCheck, GridCheck** green on the final code; **HydroCheck, WaterPowerCheck** green
  after ★P.
- The readers of the dispatch, one at a time on the code before ★Q (which touches only the automatic path's late
  power site): **HistoricalLicenceCheck, WaterCheck,
  PriceLevelCheck, OrderDoorCheck, HydrogenSteelCheck, CoalMiningCheck, CopperMiningCheck, QuarryingCheck,
  CementConcreteCheck, ForestryCheck, WoodProductsCheck** green; **Forms1900Check, ScaleCheck** known reds only;
  **PublicLicenceCheck, MaterialsCheck** green after ★P; **CopperRefiningCheck** red in section 11 (seed 1, 155 Rod
  Mills), which found ★Q.
- After ★Q, one at a time: **AutoBuildCheck, BuildAdviceCheck, WorksFinanceCheck, PowerPlanCheck, BuildCardCheck,
  NewUnitCheck** green; **EssentialWorksCheck** green with its twin's lever (★P); **CopperRefiningCheck** green
  (seed 1 at m2400: 0 Rod Mills, 6 Wire & Cable Works, 26.3 minutes).
- **LongPlaytest** (the default run, 2,400 months) on the final code: green, every month audited and every reload
  matched (§5).

## 5. What the default run builds, decade by decade (spec §6, "Measure first" 3)

LongPlaytest's default city (founded 1900, today's buildings but every power plant from its year, PW2), the
automatic path's power orders by the decade they were placed in:

| decade | ordered |
|---|---|
| 1900s | 1 Oil-Fired Plant |
| 1910s-1950s | nothing (the founding grid and the works' own power carry the town) |
| 1960s | 1 Diesel Station, 1 Oil-Fired Turbine, 1 Coal Power Plant |
| 1970s | 4 diesel, 2 turbines |
| 1980s | 7 diesel, 7 turbines, 1 coal |
| 1990s | 7 diesel, 1 turbine |
| 2000s | 3 diesel, 2 turbines, 1 Oil-Fired Plant |
| 2010s | 15 diesel, 14 turbines, 1 coal |
| 2020s | nothing |
| 2030s | 5 diesel, 4 turbines, 2 Oil-Fired Plants |
| 2040s | 17 turbines, 5 diesel, 3 coal |
| 2050s | 7 diesel, 2 coal, 1 turbine, 1 Oil-Fired Plant |
| 2060s | 19 diesel, 2 turbines, 2 Oil-Fired Plants, 1 coal |

In all: 73 Diesel Stations, 51 Oil-Fired Turbines, 9 coal plants and 7 Oil-Fired Plants; no wind, no solar and
no biomass (whether the run's research found the farms, or the spread weighed them and found them dear, is not
measured). The planner suggested 292 reserve orders and 11 spread orders; one offer was assembled past the
listing. Measured on the final code, ★Q in: before it the same run ordered 125 diesel, 39 turbines, 22
Oil-Fired Plants, 7 coal and 2 biomass, and stood a copper chain (8 smelters, 6 refineries, 19 scrap refineries,
1 Rod Mill) that it no longer stands (3 Wire & Cable Works alone). **Against main:** FC2's gate run on 0.9.195
(2026-11-23, the same default run) ordered 170 Oil-Fired Turbines, 8 Diesel Stations and 1 Oil-Fired Plant, no
coal. **Against §2.9's table:** 1900's city burned coal in plants of half a megawatt and took a third of its power
from rivers; this one has no hydro site ordered, and today's sizes (a 280 MW coal unit, an 86 MW Oil-Fired Plant,
a 19 MW diesel) make every step of a growing town's gap small beside a coal plant, so spec §2.8 rule 1's "a unit
bigger than the gap pays for its idle part" gives most of it to the peakers, as the study's "town of 10 MW" built
a diesel station (a reading of the rule, not measured month by month). So the mix is a peakers' fleet with coal
base plants among it, where main's was turbines alone; the ensemble will say what it costs the city (§6).
