# PowerPlanCheck.java - 678 lines · 32 methods · 6 constants · harnesses

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
>      And the reserve test credits a farm: where the town runs dear and one Wind Farm's credited firm kW closes the gap, the
>      farm is the order, its total net cost below the screening curve's firm pick's (6a).
>   7. THE GROUND: a coal plant's is planned past the one listing, and the automatic path assembles it over several offers;
>      past ASSEMBLE_MOST offers none is planned. (7a) The quote is what the ground costs, offers past the listing at their own prices; (7b) an
>      assembly that cannot cover the order buys nothing and borrows nothing; (7c) an order the builders would refuse moves no money (REV-PW3 #1, #2).
>   8. HOW OFTEN EACH FIRES: each suggestion counts once, on its own counter (BuildAdvice.RESERVE_ORDERS, SPREAD_ORDERS);
>      the default run's counts are LongPlaytest's line, "the power planner (PW3)".

**Uses:** [BuildAdvice](BuildAdvice.md) (119), [PowerDispatch](PowerDispatch.md) (65), [Game](Game.md) (30), [BuildingsTemplate](BuildingsTemplate.md) (25), [AutoBuilder](AutoBuilder.md) (23), [UtilitiesHandler](UtilitiesHandler.md) (9), [WorksValue](WorksValue.md) (5), [LandManager](LandManager.md) (4), [LandParcel](LandParcel.md) (4), [LandMarket](LandMarket.md) (3), [EssentialWorksCheck](EssentialWorksCheck.md) (2), [SubstationCheck](SubstationCheck.md) (2), [World](World.md) (2), [Landmasses](Landmasses.md) (2), [WorksFinanceCheck](WorksFinanceCheck.md) (1), [BuildingType](BuildingType.md) (1), [Substations](Substations.md) (1), [BuildersFirstCheck](BuildersFirstCheck.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 151 | · 1. the place and the season |
| 198 | · 2. the credit falls with the share |
| 218 | · 3. the firm supply |
| 250 | · 4. the reserve test orders the slice's cheapest row |
| 285 | · 5. wind alone never passes the test |
| 316 | · 6. the spread |
| 482 | · 7. the ground |
| 549 | · 7a. the quote is the cost |
| 605 | · 7b. an assembly that cannot cover |
| 631 | · 7c. an order the builders would refuse |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `PowerPlanCheck.out` | `System.out` |  |
| 54 | `PowerPlanCheck.POWER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)` |  |
| 56 | `PowerPlanCheck.COAL` | `"Coal Power Plant", TURBINE = "Oil-Fired Turbine", DIESEL = "Diesel Station",...` |  |
| 369 | `PowerPlanCheck.GAP_SHARE` | `0.5` | What fraction of one credited Wind Farm's firm kW the dear town of 6a is short by at the reserve test. |
| 422 | `PowerPlanCheck.DEPOTS` | `100` | Construction Depots standing in the pending-plant fixture's town: their 400 points each lift the builders' output over the diesels' upkeep. |
| 425 | `PowerPlanCheck.PREP_MONTHS` | `60` | The months of the builders' whole output the coal's ground owes in preparation (a foothill pad, the works): its first plant opens after the farm's 36. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 638 | **type** `public final class PowerPlanCheck` | The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities. |
| 45 | 4 | `static void check(String label, boolean ok)` |  |
| 50 | 3 | `static boolean close(double a, double b, double tol)` |  |
| 64 | 7 | `static Game town(Path root, String name, String...menu)` | WorksFinanceCheck.town()'s founded 1900 town (its one coal plant, people at three-quarters of its homes, a flat history, the builder's debt limit 0 and cash anyway) knowing only `menu` of the power plants, with the ca... |
| 72 | 5 | `static void know(Game g, String...menu)` |  |
| 78 | 1 | `static BuildingsTemplate plant(Game g, String name)` |  |
| 81 | 1 | `static BuildAdvice.Ahead now(Game g)` | The automatic path's opening for an order placed now: its slack, the town's flat history. |
| 84 | 10 | `static void drawAt(Game g, double share)` | The city's draw set so the planner's peak-hour demand at now(g) is `share` of its firm supply (demand is linear in the draw). |
| 96 | 10 | `static void drawGap(Game g, BuildAdvice.Ahead p, double gap)` | The city's draw set so the planner's peak-hour demand at `p` exceeds its firm supply by `gap` kW (demand is linear in the draw). |
| 108 | 4 | `static int slice(Game g)` | The slice of the load the gap at now(g) lies in (BuildAdvice.slice()). |
| 114 | 4 | `static BuildAdvice.Suggestion suggest(Game g)` | The automatic path's power suggestion, as AutoBuilder.step() asks it: the city's cash and ground, its slack, every plant. |
| 125 | 21 | `static String order(Game g)` | The reserve order as the planner ranks it, best first: each candidate's suggestion with the ones before it left out (suggestFor()'s `skip`), its count, and its figures: a firm row's screening curve (BuildAdvice.screen... |
| 147 | 1 | `static String named(BuildAdvice.Suggestion s)` |  |
| 149 | 1 | `static void quietly(Runnable r)` |  |

### 1. the place and the season (lines 151-197)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 45 | `static void placeAndSeason(Path root)` |  |

### 2. the credit falls with the share (lines 198-217)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 18 | `static void credits()` |  |

### 3. the firm supply (lines 218-249)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 30 | `static void firmSupply(Path root)` |  |

### 4. the reserve test orders the slice's cheapest row (lines 250-284)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 33 | `static void reserveTest(Path root)` |  |

### 5. wind alone never passes the test (lines 285-315)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 29 | `static void windAlone(Path root)` |  |

### 6. the spread (lines 316-481)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 14 | `static Game covered(Path root, String name, String fuelled)` | A town whose fuelled row `fuelled` stands to cover the reserve test at a wind farm's opening, knowing it and the wind farm, and wired to carry it: the dispatch runs no more than the wires carry (Substations.costs()), ... |
| 337 | 30 | `static void spread(Path root)` |  |
| 372 | 3 | `static double firmAdded(Game g, BuildingsTemplate t, Map<BuildingsTemplate, Long> at, BuildAdvice.Ahead p)` | The firm kW one `t` adds at `p`'s opening to the site's, the rank's denominator (BuildAdvice.reserveRank()). |
| 382 | 38 | `static void reserveCredit(Path root)` | PW3 (REV-PW3 #3): the reserve test credits a farm. |
| 428 | 6 | `static BuildAdvice.Spread windRow(Game g)` | The Wind Farm's row of the spread at `g`, or null when it is not weighed. |
| 441 | 40 | `static void spreadPending(Path root)` | PW3 (REV-PW3 #4): a plant still going up counts in the spread from its own opening, as the reserve test reads it. |

### 7. the ground (lines 482-548)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 40 | `static void ground(Path root)` |  |
| 525 | 1 | **type** `record GroundTown(Game g, BuildingsTemplate coal, long past, BuildAdvice.Suggestion asked)` | Section 7's town (PW3, REV-PW3 #1, #2): owns no ground spare, `past` the fewest coal plants whose ground runs past its listing, and a reserve test asking for at least that many. |
| 527 | 15 | `static GroundTown groundTown(Path root, String name)` |  |
| 544 | 4 | `static double listedCash(Game g, java.util.List<LandParcel> offers)` | What these offers cost in cash as paying for them moves it (the land office's toggle): the listed ones' prices, as groundFor() prices the listing. |

### 7a. the quote is the cost (lines 549-604)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 39 | `static void quoteIsCost(Path root)` |  |
| 594 | 10 | `static GroundTown shortTown(Path root, String name)` | A town whose world has no ground left to list (one purchase of all of it, on the books as any purchase is) and three offers standing: the ground the order needs past them is none that can be had. |

### 7b. an assembly that cannot cover (lines 605-630)

| line | len | member | says |
|---:|---:|---|---|
| 606 | 24 | `static void cannotCover(Path root)` |  |

### 7c. an order the builders would refuse (lines 631-678)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 25 | `static void refused(Path root)` |  |
| 658 | 20 | `public static void main(String[] args) throws Exception` |  |

