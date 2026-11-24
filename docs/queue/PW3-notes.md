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
- ★F **groundFor() assembles, on a rehearsal** (REV-PW3 #1, #2). When the standing listing cannot cover, the offers
  past it are bought on a rehearsal, a copy of the land office on a copy of the land (`LandManager.rehearse()`,
  `AutoBuilder.assembled()`): each purchase lists the place's next, as the office does, until the held ground covers
  or `ASSEMBLE_MOST` offers are held (4 x `LandMarket.OFFERS`, 96: past the 18 a 325 MW coal plant's 200 acres took
  in LandCheck); null past that. The quote is the cash that paying exactly those whole offers moves
  (`Game.landCashFor()`). It was the missing square feet at the office's price a square foot, which the replenished
  offers at their own prices outran (the reviewer's #1; PowerPlanCheck 7a: 741 paid against 670 quoted, the cash 21
  against 92 protected). `place()` now asks the builders' refusals first (`Game.blocker()`, which `buildStack()` asks
  itself, one copy of the rules), rehearses the ground, sizes the bond to its exact cost, and only then sells the bond
  and buys the offers held, each checked by id and price. An order that cannot be placed buys nothing and sells no
  bond (the reviewer's #2, which bought the ground and sold the bond first; 7b, 7c). Listing draws on no random
  stream, so the rehearsal's offers are the office's. An offer held unbought was the first design: with its ground
  unclaimed the office lists the same rectangle again under a new id, so it would be paid twice. An offer on a
  landmass the city has not linked counts its linked dry ground alone (`linkedSqFt()`, ET5's rule held). The
  refusals are read on the city as it stands: an order whose refusal only the ground it buys would clear (a river or
  a coast arriving with an offer) is held. Two limits, the re-review's (REV-PW3b, P3): the rehearsal's hold
  (`LandManager.Rehearsal.hold()`) skips the purchase's `landChanged()` recount, so a held offer's `linkedSqFt()`
  could differ from the bought one's only where a purchase moves the linked set, and that would fall to `place()`'s
  residual path (an offer gone or priced otherwise: the order held with the ground bought and the bond sold); and
  PowerPlanCheck 7b's assembly fails in `groundFor()`, so `place()`'s own hold where the rehearsal cannot cover is
  reached only if the market changes between the plan and the purchase, which no fixture exercises.
- ★G **E25 fits, E31 does not.** The tracker raises the farm's yield a kWp (PVGIS tracked over fixed, by band):
  before E25 is known the farm runs at the fixed rack's yield, after it at the tracked (the undone pattern, today's
  figure the tracked). The tandem module raises a module's watts a square metre, not its yield a kWp: its gain is
  the farm's ground, which is FE9's question, so E31 stays Pending with PW7/FE9.

- ★H **Wind and solar in the reserve test, at their falling credit** (REV-PW3 #3; the first design, firm rows
  alone, was the study's `plan()`). A planner credits a farm toward the reserve margin at its ELCC, which falls with
  the variable rows' share (NREL, docs.nlr.gov/docs/fy14osti/61182.pdf; `creditFall()`). So a farm is a candidate at
  the credit the firm supply gives it, its count held to the farms that keep the variable rows' share of the energy
  at or under `CREDIT_HALF` (`BuildAdvice.reserveMost()`), where the credit is at its floor and a fleet adds a
  tenth: wind alone never passes the test (PowerPlanCheck 5: 12 Solar Farms at a share of 0.242, the firm supply
  305,208 kW against the 898,111 the test asks; knowing a diesel, the order closes it). Left out, a farm whose
  credit closes a gap lost the thermal capital it avoids, since the spread values running alone (the reviewer's #3;
  PowerPlanCheck 6a ordered 1 Diesel Station for a gap of half a farm's credit). ★L compares a farm with a firm row.
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
  before the plants pending (EssentialWorksCheck after FC2's merge: 4 Oil-Fired Plants past the deadline, joining an
  Oil-Fired Plant on site ahead of FC2's Container Terminal, open in 58 months against the first pending diesel's
  988; before the merge 17 Diesel Stations, 2 of the 37 pending opening after the candidate). Seed 1 now ends with no copper works but 6 Wire & Cable Works (CU2's run: 1), 0 Rod Mills.
- ★L **The screening curve ranks the firm rows, and a closing farm is weighed against its pick** (rule 1). A firm
  candidate's twelve months of capital and ground over `lifeFactor()` and of its fixed running, plus its running a
  kWh for the slice's hours, over the firm kW of the gap it serves; the slice from where the firm supply sits against
  the blocks' loads over the losses. A wind or solar farm whose credit closes the gap is compared with the screening
  curve's firm pick on the year's capital and fixed running less the year's running each saves (`reserveRank()`, the
  spread's dispatch at each one's opening: PJM's Net CONE, the cost of new entry less its energy margins), the
  cheaper the order (`placeFarms()`; PowerPlanCheck 6a: 1 Wind Farm at -37.5 against 1 Diesel Station's 164.5). A
  farm that cannot close ranks after the firm rows that do, by that net cost a firm kW. HW1's tier (an order that
  closes the gap before one that cannot) still comes first. One rank by total net cost for every row was tried and
  dropped. At the base it tied coal and diesel within 0.12% (47 diesel at -167,700 against 4 coal at -167,500): the
  dispatch at today's draw does not see the hours of a gap measured at the reserve test's margin. And orders sized to
  the margin chased their own queue (EssentialWorksCheck's overdue pending: 26 diesel in month 37 and 3 more in month
  39, before the deadline).
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
  - the reserve plant was a firm row where it was a Wind Farm (★H's first design). With the farms back in the
    reserve test (REV-PW3 #3), main's Wind Farm fixtures are restored: WorksFinanceCheck's highway promise (2 Wind
    Farms) and independent pending load (8), EssentialWorksCheck's interim power (1) and scarce cash (the cash
    step's 1 Wind Farm). The overdue pending keeps the Diesel Station (1910, dated to the town's year, PW2's lever)
    for reality's reason, which a fixture line now asserts: no fleet of farms within `CREDIT_HALF`'s share
    (`reserveMost()`: 19 farms) covers its gap (the most such a fleet adds 27,913 firm kW against 297,476; at the
    credit's floor some 600 farms would, a fleet no planner builds), and a planner short by that much builds firm
    plant. After FC2's merge (one Container Terminal ahead of the pending diesels, FC2's reason) every candidate
    opened after the last diesel and nothing was short at its opening, so the fixture stands one Oil-Fired Plant on
    site ahead of the terminal: a candidate of its kind opens first and is short (a "fixture:" line asserts it);
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
- After REV-PW3's four mends, one at a time: **PowerPlanCheck** green with its new lines, each shown red first on
  the unmended code (6a, the farm that closes a gap; 6b, a plant on site counted from its opening in the spread; 7a,
  the quote is what the ground costs; 7b, an assembly that cannot cover buys nothing and leaves the market as it was;
  7c, an order the builders would refuse moves no money); **BuildAdviceCheck, AutoBuildCheck, WorksFinanceCheck,
  EssentialWorksCheck, BuildCardCheck, NewUnitCheck, LandCheck** green; the twenty that buy land (CreditCrunchCheck,
  EarthFoundingCheck, HousingDoorCheck, LandClassCheck, LandNationsCheck, LandmassCheck, LicenceFeesCheck, MapCheck,
  MaterialsCheck, MiningCheck, NewGameCheck, OilCheck, OrderDoorCheck, PortCheck, ReplayCheck, SaveFileCheck,
  TreasuryCheck, WaterCheck, WellCheck and EssentialWorksCheck) green; **KeptCheck** main's two red lines alone
  (`docs/notes/gate.txt`), the same on a clean copy of the commit before.
- **LongPlaytest** (the default run, 2,400 months) on the final code: green, every month audited and every reload matched (§5).

## 5. What the default run builds, decade by decade (spec §6, "Measure first" 3)

LongPlaytest's default city (founded 1900, today's buildings but every power plant from its year, PW2), the
automatic path's power orders by the decade they were placed in:

| decade | after REV-PW3's mends | before them (★Q in) |
|---|---|---|
| 1900s | 1 Oil-Fired Plant | 1 Oil-Fired Plant |
| 1910s-1950s | nothing (the founding grid and the works' own power carry the town) | nothing |
| 1960s | 1 diesel, 1 turbine, 1 coal | 1 diesel, 1 turbine, 1 coal |
| 1970s | 4 diesel, 2 turbines | 4 diesel, 2 turbines |
| 1980s | 8 diesel, 8 turbines, **3 Wind Farms**, 1 coal | 7 diesel, 7 turbines, 1 coal |
| 1990s | 4 diesel, 2 turbines, 1 coal | 7 diesel, 1 turbine |
| 2000s | 13 diesel, 7 turbines, 1 Oil-Fired Plant | 3 diesel, 2 turbines, 1 Oil-Fired Plant |
| 2010s | 2 diesel, 2 turbines | 15 diesel, 14 turbines, 1 coal |
| 2020s | 4 diesel, 2 turbines | nothing |
| 2030s | 15 diesel, 15 turbines, 2 coal, **1 Solar Farm** | 5 diesel, 4 turbines, 2 Oil-Fired Plants |
| 2040s | 12 diesel, 6 turbines, 1 coal | 17 turbines, 5 diesel, 3 coal |
| 2050s | 1 coal | 7 diesel, 2 coal, 1 turbine, 1 Oil-Fired Plant |
| 2060s | 23 diesel, 12 turbines, 5 Oil-Fired Plants, **2 Wind Farms, 1 Solar Farm, 2 Biomass**, 1 coal | 19 diesel, 2 turbines, 2 Oil-Fired Plants, 1 coal |
| 2070s | 1 coal | nothing |

After the mends: 86 Diesel Stations, 57 Oil-Fired Turbines, 9 coal plants, 7 Oil-Fired Plants, 5 Wind Farms, 2
Solar Farms and 2 Biomass Plants (before: 73, 51, 9, 7 and no farms); 447 reserve suggestions and 8 spread
suggestions (292 and 11); one offer assembled past the listing; no copper works but 3 Wire & Cable Works, as
before. **Against main:** FC2's gate run on 0.9.195 (2026-11-23, the same default run) ordered 170 Oil-Fired
Turbines, 8 Diesel Stations and 1 Oil-Fired Plant, no coal and no farms.

**Why so few farms** (the orchestrator asked: did research find them, and what did the planner price them at;
measured by a helper on this run's city kept at January 2012 and January 2042, before the mends, in the model's
$k). The farms are known: E16 is dated December 1980, so the Wind Farm stands from then (the 1980s' three), solar
from 1982, biomass from 1970; the city has no hydro slot. What rules a farm out is its ground. The city buys a
farm's ground at its land office, 0.119 a square foot at the market in 2012 and 0.259 in 2042 (the office's own
0.260 and 0.642): ground is 87% of a Wind Farm's capital (1.74M sq ft) and 96% of a Solar Farm's (14.5M). On the
screening curve's peak slice, a firm kW-year cost 0.41 in a turbine, 0.54 in a diesel or an Oil-Fired Plant, 0.57
in coal, 1.43 in biomass, 3.23 in a Wind Farm and 3.75 in a Solar Farm (about 2.5 times each in 2042). The spread
saw the same: a year's saving against its cost of 0.09 for coal and wind, 0.04 for solar in 2012, 0.43, 0.35 and
0.12 in 2042. With free ground a Wind Farm would pass the spread in 2042 (2.16) and a Solar Farm too (2.45); ground
would have to cost 57.7 (wind) and 18.8 (solar) a thousand square feet against the market's 258.7. And in 2012 the
works' own must-run power (2.44M kW) was above the city's mean draw (2.31M), so the grid's own plants served only
the peak (the diesels and turbines ran at a capacity factor of 0), where no farm's energy is worth its capital.
**Against reality:** grids grew on coal and hydro base load, and wind and solar are the largest additions since the
2010s, oil a few percent. Here the base is the works' own power, and a farm's ground is bought at a city's land
price: real farms stand on rural ground, leased or bought at a few percent of their capital. Nothing in the model
prices rural ground apart from the city's (a helper's reading of the tree: the city's price varies only by relief;
`LandFactors` is a nation's multiplier; agriculture buys at the city's price, with a property-tax relief at use
value; no building rents its ground; a farm's ground use is a coal plant's). Rural ground for the power farms is
RG1's (Pending on main, 943d0da82; Jerus asked), not PW3's, so PW3 ships with the city's price and the mix follows
it. The planner chooses a farm where its numbers say so (PowerPlanCheck 6 and 6a; this run's 1980s,
2030s and 2060s).

**Against §2.9's table:** 1900's city burned coal in plants of half a megawatt and took a third of its power from
rivers; this one has no hydro site ordered, and today's sizes (a 280 MW coal unit, an 86 MW Oil-Fired Plant, a 19
MW diesel) make every step of a growing town's gap small beside a coal plant, so spec §2.8 rule 1's "a unit bigger
than the gap pays for its idle part" gives most of it to the peakers, as the study's "town of 10 MW" built a diesel
station (a reading of the rule, not measured month by month). So the mix is a peakers' fleet with coal base plants
and a few farms among it, where main's was turbines alone; the ensemble will say what it costs the city (§6).
