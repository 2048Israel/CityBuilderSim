# PowerPlanCheck.java - 1,296 lines · 47 methods · 11 constants · harnesses

`ham/citybuildersim/PowerPlanCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities.
> 
> WHY. PW1's planner counted a wind farm at a fixed firm third (PowerDispatch.WIND_FIRM) wherever it stood, a plant still going
> up as if it ran, the works' own power as nothing, and ranked plants by the plan's present value with no slice of the load in it
> (PW1-P's five gaps). PW3 reads the wind's, the sun's and the river's months at the city's latitude, the south on its own
> season, credits a variable row with its peak hours, falling with its share, and plans as the study's prototype does
> (spec-electricity 2.8): the reserve test first, its order the firm row the screening curve picks for the slice of the load the gap lies
> in - unless a wind or solar farm, credited at its falling credit as planners credit them at their ELCC toward the reserve margin,
> closes the gap for less in total net cost than that pick (the thermal capital it avoids; PJM's Net CONE) - then the spread.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE PLACE AND THE SEASON. A city south of the equator has its months six on; a fixed rack yields less than a tracker,
>      and less far from the equator than near it; each place's months average its year; the tracker is research E25's.
>   2. THE CREDIT FALLS WITH THE SHARE: a variable row counts all of its peak hours with no variable row standing, a straight
>      line down to CREDIT_FLOOR at CREDIT_HALF of the energy, and the floor past it; a fuelled row counts all of it.
>   3. THE FIRM SUPPLY counts the works' own power, rises with a wind farm added (its posts never take more than it adds),
>      and counts a plant still going up only from its opening.
>   4. THE RESERVE TEST ORDERS THE SLICE'S CHEAPEST FIRM ROW: short at the base, coal; short at the peak alone, by more than a
>      coal plant, a peaker.
>   5. WIND ALONE NEVER PASSES THE TEST: a city knowing only wind and sun may order farms, never more than keep their share of
>      the energy at CREDIT_HALF, and with them added is still short; knowing a firm row too, the order closes it.
>   6. THE SPREAD orders wind where the fuelled rows standing run dear (diesel), and none where they run cheap (coal).
>      A plant still going up counts in it from its own opening: coal that opens after the farm does takes none of its saving.
>      And the reserve test credits a farm: where the town runs dear and one Wind Farm's credited firm kW closes a gap under
>      FIRST_WORK_SHARE of a Diesel Station's, no firm row is a candidate (the share, 2026-11-24) and the farm is the order (6a); where the gap
>      is at or over the share, the farms that close it are weighed against the diesel and, their total net cost below the firm pick's,
>      are the order (6a(ii)).
>   7. THE GROUND: a coal plant's is planned past the one listing, and the automatic path assembles it over several offers;
>      past ASSEMBLE_MOST offers none is planned. (7a) The quote is what the ground costs, offers past the listing at their own prices; (7b) an
>      assembly that cannot cover the order buys nothing and borrows nothing; (7c) an order the builders would refuse moves no money (REV-PW3 #1, #2).
>   8. A GAP IN THE RESERVE ALONE IS NOT A PLANT (2026-11-24): with the seasonal peak (PLAN_PEAK_SEASON: the peak block of the year's highest
>      month) covered at a plant's opening, a gap to the peak hour (PLAN_PEAK_HOUR) orders it only when it fills FIRST_WORK_SHARE of the
>      plant; so does a gap in the seasonal peak that only the opening's slack and horizon project (REV-PW3d); a gap in the seasonal peak
>      at the plant's own completion, its lead and the town's growth over it, orders as ever, the month short or not (REV-PW3e) (8a). And
>      the month lacks power when its peak month's peak block goes unserved, which
>      the city's own dispatch shows at three firm levels, not below the plan's peak hour (BuildAdvice.lacksNow(), 8b); and the auto-builder's holds read the same "short now", through a real pass, in both directions (AutoBuilder.crewLimit() and pendingDelay(), 8b (b)).
>   9. HOW OFTEN EACH FIRES: each suggestion counts once, on its own counter (BuildAdvice.RESERVE_ORDERS, SPREAD_ORDERS);
>      the default run's counts are LongPlaytest's line, "the power planner (PW3)".

**Uses:** [BuildAdvice](BuildAdvice.md) (300), [PowerDispatch](PowerDispatch.md) (107), [Game](Game.md) (46), [BuildingsTemplate](BuildingsTemplate.md) (36), [AutoBuilder](AutoBuilder.md) (33), [UtilitiesHandler](UtilitiesHandler.md) (14), [WorksValue](WorksValue.md) (11), [WorksFinanceCheck](WorksFinanceCheck.md) (6), [EssentialWorksCheck](EssentialWorksCheck.md) (6), [SubstationCheck](SubstationCheck.md) (5), [BuildingManager](BuildingManager.md) (5), [LandManager](LandManager.md) (4), [LandParcel](LandParcel.md) (4), [BuildingType](BuildingType.md) (3), [Substations](Substations.md) (3), [LandMarket](LandMarket.md) (3), [Good](Good.md) (3), [BuildingsStacks](BuildingsStacks.md) (2), [World](World.md) (2), [Landmasses](Landmasses.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [Traffic](Traffic.md) (2), [BuildersFirstCheck](BuildersFirstCheck.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [CareType](CareType.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 159 | · 1. the place and the season |
| 206 | · 2. the credit falls with the share |
| 226 | · 3. the firm supply |
| 258 | · 4. the reserve test orders the slice's cheapest row |
| 293 | · 5. wind alone never passes the test |
| 324 | · 6. the spread |
| 532 | · 7. the ground |
| 599 | · 7a. the quote is the cost |
| 655 | · 7b. an assembly that cannot cover |
| 681 | · 7c. an order the builders would refuse |
| 708 | · 8. a gap in the reserve alone is not a plant; the month lacks power when its peak month's peak block goes unserved |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `PowerPlanCheck.out` | `System.out` |  |
| 62 | `PowerPlanCheck.POWER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)` |  |
| 64 | `PowerPlanCheck.COAL` | `"Coal Power Plant", TURBINE = "Oil-Fired Turbine", DIESEL = "Diesel Station",...` |  |
| 378 | `PowerPlanCheck.GAP_SHARE` | `0.5` | What fraction of one credited Wind Farm's firm kW the dear town of 6a is short by at the reserve test. |
| 386 | `PowerPlanCheck.OVER_SHARE` | `1.1` | (6a(ii)) The second dear town's gap at a farm's opening, as a multiple of FIRST_WORK_SHARE of one Diesel Station's firm kW: over the share, so the diesel is a candidate, and under what the farms that close it credit. |
| 472 | `PowerPlanCheck.DEPOTS` | `100` | Construction Depots standing in the pending-plant fixture's town: their 400 points each lift the builders' output over the diesels' upkeep. |
| 475 | `PowerPlanCheck.PREP_MONTHS` | `60` | The months of the builders' whole output the coal's ground owes in preparation (a foothill pad, the works): its first plant opens after the farm's 36. |
| 711 | `PowerPlanCheck.SEASONAL` | `PowerDispatch.PLAN_PEAK_SEASON / PowerDispatch.PLAN_PEAK_HOUR` | The year's highest month's peak block over the peak hour's draw: the firm supply the month's dispatch needs at its worst over the supply the plan does (PowerDispatch.PLAN_PEAK_SEASON over PLAN_PEAK_HOUR). |
| 719 | `PowerPlanCheck.NOW_OVER` | `1.02, NOW_UNDER =.99` | The month's firm supply over the seasonal peak's draw, just over it (the month does not lack) and just under it (it does), in 8a's last two fixtures. |
| 722 | `PowerPlanCheck.GROWTH_AT_LEAD` | `.30` | The town's growth over the coal plant's lead in 8a's growing town (BusinessInvestment.growthFactor() at the lead, less one), to a hundredth (the history is whole people). |
| 904 | `PowerPlanCheck.ARRIVES` | `12` | (viii)'s months until the calendar brings the Diesel Station: as 1910's to a town of 1908, well inside coal's lead with the diesel's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 1248 | **type** `public final class PowerPlanCheck` | The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities. |
| 53 | 4 | `static void check(String label, boolean ok)` |  |
| 58 | 3 | `static boolean close(double a, double b, double tol)` |  |
| 72 | 7 | `static Game town(Path root, String name, String...menu)` | WorksFinanceCheck.town()'s founded 1900 town (its one coal plant, people at three-quarters of its homes, a flat history, the builder's debt limit 0 and cash anyway) knowing only `menu` of the power plants, with the ca... |
| 80 | 5 | `static void know(Game g, String...menu)` |  |
| 86 | 1 | `static BuildingsTemplate plant(Game g, String name)` |  |
| 89 | 1 | `static BuildAdvice.Ahead now(Game g)` | The automatic path's opening for an order placed now: its slack, the town's flat history. |
| 92 | 10 | `static void drawAt(Game g, double share)` | The city's draw set so the planner's peak-hour demand at now(g) is `share` of its firm supply (demand is linear in the draw). |
| 104 | 10 | `static void drawGap(Game g, BuildAdvice.Ahead p, double gap)` | The city's draw set so the planner's peak-hour demand at `p` exceeds its firm supply by `gap` kW (demand is linear in the draw). |
| 116 | 4 | `static int slice(Game g)` | The slice of the load the gap at now(g) lies in (BuildAdvice.slice()). |
| 122 | 4 | `static BuildAdvice.Suggestion suggest(Game g)` | The automatic path's power suggestion, as AutoBuilder.step() asks it: the city's cash and ground, its slack, every plant. |
| 133 | 21 | `static String order(Game g)` | The reserve order as the planner ranks it, best first: each candidate's suggestion with the ones before it left out (suggestFor()'s `skip`), its count, and its figures: a firm row's screening curve (BuildAdvice.screen... |
| 155 | 1 | `static String named(BuildAdvice.Suggestion s)` |  |
| 157 | 1 | `static void quietly(Runnable r)` |  |

### 1. the place and the season (lines 159-205)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 45 | `static void placeAndSeason(Path root)` |  |

### 2. the credit falls with the share (lines 206-225)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 18 | `static void credits()` |  |

### 3. the firm supply (lines 226-257)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 30 | `static void firmSupply(Path root)` |  |

### 4. the reserve test orders the slice's cheapest row (lines 258-292)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 33 | `static void reserveTest(Path root)` |  |

### 5. wind alone never passes the test (lines 293-323)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 29 | `static void windAlone(Path root)` |  |

### 6. the spread (lines 324-531)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 14 | `static Game covered(Path root, String name, String fuelled)` | A town whose fuelled row `fuelled` stands to cover the reserve test at a wind farm's opening, knowing it and the wind farm, and wired to carry it: the dispatch runs no more than the wires carry (Substations.costs()), ... |
| 345 | 31 | `static void spread(Path root)` |  |
| 381 | 3 | `static double firmAdded(Game g, BuildingsTemplate t, Map<BuildingsTemplate, Long> at, BuildAdvice.Ahead p)` | The firm kW one `t` adds at `p`'s opening to the site's, the rank's denominator (BuildAdvice.reserveRank()). |
| 392 | 10 | `static void settleGap(Game g, BuildAdvice.Ahead p, java.util.function.DoubleSupplier gap)` | The dear town of the spread (diesels standing, which a farm displaces) made short at a farm's opening by `gap` kW, wired to carry the draw that sets: a farm's credit and a diesel's fall with the draw, so `gap` is read... |
| 408 | 25 | `static void reserveCredit(Path root)` | PW3 (REV-PW3 #3): the reserve test credits a farm. |
| 439 | 31 | `static void reserveFarms(Path root)` | 6a's original point, on a second town: the gap at a farm's opening is OVER_SHARE of FIRST_WORK_SHARE of a Diesel Station's firm kW, so a diesel (the screening curve's firm pick) fills the share and closes it, and more... |
| 478 | 6 | `static BuildAdvice.Spread windRow(Game g)` | The Wind Farm's row of the spread at `g`, or null when it is not weighed. |
| 491 | 40 | `static void spreadPending(Path root)` | PW3 (REV-PW3 #4): a plant still going up counts in the spread from its own opening, as the reserve test reads it. |

### 7. the ground (lines 532-598)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 40 | `static void ground(Path root)` |  |
| 575 | 1 | **type** `record GroundTown(Game g, BuildingsTemplate coal, long past, BuildAdvice.Suggestion asked)` | Section 7's town (PW3, REV-PW3 #1, #2): owns no ground spare, `past` the fewest coal plants whose ground runs past its listing, and a reserve test asking for at least that many. |
| 577 | 15 | `static GroundTown groundTown(Path root, String name)` |  |
| 594 | 4 | `static double listedCash(Game g, java.util.List<LandParcel> offers)` | What these offers cost in cash as paying for them moves it (the land office's toggle): the listed ones' prices, as groundFor() prices the listing. |

### 7a. the quote is the cost (lines 599-654)

| line | len | member | says |
|---:|---:|---|---|
| 600 | 39 | `static void quoteIsCost(Path root)` |  |
| 644 | 10 | `static GroundTown shortTown(Path root, String name)` | A town whose world has no ground left to list (one purchase of all of it, on the books as any purchase is) and three offers standing: the ground the order needs past them is none that can be had. |

### 7b. an assembly that cannot cover (lines 655-680)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 24 | `static void cannotCover(Path root)` |  |

### 7c. an order the builders would refuse (lines 681-707)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 25 | `static void refused(Path root)` |  |

### 8. a gap in the reserve alone is not a plant; the month lacks power when its peak month's peak block goes unserved (lines 708-1296)

| line | len | member | says |
|---:|---:|---|---|
| 714 | 3 | `static void drawRatio(Game g, BuildAdvice.Ahead p, double ratio)` | The city's draw set so the peak hour's demand at `p` is `ratio` of its firm supply (the gap that makes it so is drawGap()'s). |
| 725 | 3 | `static BuildAdvice.Ahead completion(Game g, double lead)` | A plant's own completion: its lead and the town's growth over it, no horizon past it and no slack (BuildAdvice.suggestFor()'s share rule). |
| 730 | 4 | `static double supplyOverDraw(Game g, BuildAdvice.Ahead p)` | The firm supply over the city's draw at `p`: the plan's peak hour is PLAN_PEAK_HOUR of the draw, the month's peak block PLAN_PEAK, in the year's highest month PLAN_PEAK_SEASON. |
| 736 | 8 | `static void grow(Game g, double lead)` | The town's history set so it grows GROWTH_AT_LEAD over `lead` months (BusinessInvestment.growthFactor(), the businesses' trend), from half its reachable people. |
| 769 | 133 | `static void calmPeak(Path root)` | 8a's table (REV-PW3f, 2026-11-24): every case the last four reviews found, the fixture that causes it, and the form of the rule it is red under (each shown red, docs/queue/PW3-notes.md section 2, the need date). |
| 915 | 156 | `static void needDate(Path root)` | 8a (v) to (ix), REV-PW3f's P1 (2026-11-24), and (v') and (v''), REV-PW3g's P1: the need date. |
| 1073 | 5 | `static int peakCalendarMonth(UtilitiesHandler u)` | The calendar month of the year's highest load at this city's latitude, as its dispatch reads it (PowerDispatch.season(month, latitude)). |
| 1083 | 14 | `static double[] peakBlock(Game g, double have)` | The town's month dispatched by the city's own update (UtilitiesHandler.updateEnergyRatio()) in its peak month, the draw set so its firm supply is `have` of it and the wires carry whatever the dispatch asks: the peak b... |
| 1098 | 26 | `static void lacksNow(Path root)` |  |
| 1130 | 31 | `static Game holdTown(Path root, String name, double level)` | 8b (b): WorksFinanceCheck.reserveTown()'s founded town, every other essential service given its operating capacity, with a second Coal Power Plant going up, nearly built (Power's saved completion, AutoBuilder's pendin... |
| 1168 | 38 | `static void holds(Path root)` | 8b (b): the holds read the same "short now" as the month does, through a real pass (AutoBuilder.crewLimit() and pendingDelay()). |
| 1211 | 19 | `static Game careTown(Path root, String name, double level, BuildAdvice.Measure care)` | 8b (b), the other way round: reserveTown()'s founded town with General care lacking now, its Community Health Centres going up and their first completion saved (pendingUntil, as rememberPending() quotes it), and no pl... |
| 1236 | 35 | `static void holdsPower(Path root)` | 8b (b), the guard: the order of a measure the month lacks is never held (AutoBuilder.crewLimit()). |
| 1272 | 24 | `public static void main(String[] args) throws Exception` |  |

