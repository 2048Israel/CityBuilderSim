# PowerPlanCheck.java - 369 lines · 20 methods · 3 constants · harnesses

`ham/citybuildersim/PowerPlanCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities.
> 
> WHY. PW1's planner counted a wind farm at a fixed firm third (PowerDispatch.WIND_FIRM) wherever it stood, a plant still going
> up as if it ran, the works' own power as nothing, and ranked plants by the plan's present value with no slice of the load in it
> (PW1-P's five gaps). PW3 reads the wind's, the sun's and the river's months at the city's latitude, the south on its own
> season, credits a variable row with its peak hours, falling with its share, and plans as the study's prototype does
> (spec-electricity 2.8): the reserve test first, its order the cheapest firm row for the slice of the load the gap lies in,
> then the spread.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE PLACE AND THE SEASON. A city south of the equator has its months six on; a fixed rack yields less than a tracker,
>      and less far from the equator than near it; each place's months average its year; the tracker is research E25's.
>   2. THE CREDIT FALLS WITH THE SHARE: a variable row counts all of its peak hours with no variable row standing, a straight
>      line down to CREDIT_FLOOR at CREDIT_HALF of the energy, and the floor past it; a fuelled row counts all of it.
>   3. THE FIRM SUPPLY counts the works' own power, rises with a wind farm added (its posts never take more than it adds),
>      and counts a plant still going up only from its opening.
>   4. THE RESERVE TEST ORDERS THE SLICE'S CHEAPEST ROW: short at the base, coal; short at the peak alone, by more than a coal
>      plant, a peaker.
>   5. WIND ALONE NEVER PASSES THE TEST: a city knowing only wind and sun has no reserve order, however short; knowing a firm
>      row too, the same city has.
>   6. THE SPREAD orders wind where the fuelled rows standing run dear (diesel), and none where they run cheap (coal).
>   7. THE GROUND: a coal plant's is planned past the one listing, and the automatic path assembles it over several offers;
>      past ASSEMBLE_MOST offers none is planned.
>   8. HOW OFTEN EACH FIRES: each suggestion counts once, on its own counter (BuildAdvice.RESERVE_ORDERS, SPREAD_ORDERS);
>      the default run's counts are LongPlaytest's line, "the power planner (PW3)".

**Uses:** [BuildAdvice](BuildAdvice.md) (72), [PowerDispatch](PowerDispatch.md) (63), [Game](Game.md) (16), [AutoBuilder](AutoBuilder.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (8), [UtilitiesHandler](UtilitiesHandler.md) (6), [LandManager](LandManager.md) (2), [EssentialWorksCheck](EssentialWorksCheck.md) (2), [WorksValue](WorksValue.md) (2), [WorksFinanceCheck](WorksFinanceCheck.md) (1), [BuildingType](BuildingType.md) (1), [Substations](Substations.md) (1), [SubstationCheck](SubstationCheck.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 106 | · 1. the place and the season |
| 153 | · 2. the credit falls with the share |
| 173 | · 3. the firm supply |
| 205 | · 4. the reserve test orders the slice's cheapest row |
| 238 | · 5. wind alone never passes the test |
| 260 | · 6. the spread |
| 310 | · 7. the ground |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `PowerPlanCheck.out` | `System.out` |  |
| 49 | `PowerPlanCheck.POWER` | `BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)` |  |
| 51 | `PowerPlanCheck.COAL` | `"Coal Power Plant", TURBINE = "Oil-Fired Turbine", DIESEL = "Diesel Station",...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 334 | **type** `public final class PowerPlanCheck` | The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities. |
| 40 | 4 | `static void check(String label, boolean ok)` |  |
| 45 | 3 | `static boolean close(double a, double b, double tol)` |  |
| 59 | 7 | `static Game town(Path root, String name, String...menu)` | WorksFinanceCheck.town()'s founded 1900 town (its one coal plant, people at three-quarters of its homes, a flat history, the builder's debt limit 0 and cash anyway) knowing only `menu` of the power plants, with the ca... |
| 67 | 5 | `static void know(Game g, String...menu)` |  |
| 73 | 1 | `static BuildingsTemplate plant(Game g, String name)` |  |
| 76 | 1 | `static BuildAdvice.Ahead now(Game g)` | The automatic path's opening for an order placed now: its slack, the town's flat history. |
| 79 | 10 | `static void drawAt(Game g, double share)` | The city's draw set so the planner's peak-hour demand at now(g) is `share` of its firm supply (demand is linear in the draw). |
| 91 | 4 | `static int slice(Game g)` | The slice of the load the gap at now(g) lies in (BuildAdvice.slice()). |
| 97 | 4 | `static BuildAdvice.Suggestion suggest(Game g)` | The automatic path's power suggestion, as AutoBuilder.step() asks it: the city's cash and ground, its slack, every plant. |
| 102 | 1 | `static String named(BuildAdvice.Suggestion s)` |  |
| 104 | 1 | `static void quietly(Runnable r)` |  |

### 1. the place and the season (lines 106-152)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 45 | `static void placeAndSeason(Path root)` |  |

### 2. the credit falls with the share (lines 153-172)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 18 | `static void credits()` |  |

### 3. the firm supply (lines 173-204)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 30 | `static void firmSupply(Path root)` |  |

### 4. the reserve test orders the slice's cheapest row (lines 205-237)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 31 | `static void reserveTest(Path root)` |  |

### 5. wind alone never passes the test (lines 238-259)

| line | len | member | says |
|---:|---:|---|---|
| 239 | 20 | `static void windAlone(Path root)` |  |

### 6. the spread (lines 260-309)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 14 | `static Game covered(Path root, String name, String fuelled)` | A town whose fuelled row `fuelled` stands to cover the reserve test at a wind farm's opening, knowing it and the wind farm, and wired to carry it: the dispatch runs no more than the wires carry (Substations.costs()), ... |
| 281 | 28 | `static void spread(Path root)` |  |

### 7. the ground (lines 310-369)

| line | len | member | says |
|---:|---:|---|---|
| 311 | 40 | `static void ground(Path root)` |  |
| 352 | 17 | `public static void main(String[] args) throws Exception` |  |

