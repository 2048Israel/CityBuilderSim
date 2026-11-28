# TownGridCheck.java - 643 lines · 24 methods · 6 constants · harnesses

`ham/citybuildersim/TownGridCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> Power for a small town (batch ASH1).
> 
> WHAT THIS HAS TO PROVE. A town that grows past its founding 10 MW grid used to read "204% served" in green on NEEDS YOU while the
> wires shed 4.6% of its month, and was sold a 100 MW Substation for a 5 MW need; the grid units' crews were paid whole at a thousandth
> of the load. Each of these fails on the model before ASH1:
> 
>   1. THE POWER ROW READS THE LESSER OF THE PLANTS AND THE WIRES. Plants well over the peak and wires under it: the row is red, is
>      NEEDS YOU's, says "by the wires" and is advised on the grid's units; the plants' share read alone is over 100%. Distribution
>      Substations on site turn it amber, a plant on site does not; and the wires built past the plants' share give the row back to
>      the plants, green.
> 
>   2. A TOWN THAT GROWS PAST 10 MW IN SMALL STEPS IS GIVEN DISTRIBUTION SUBSTATIONS. The automatic builder's every grid order is the
>      5 MW unit, never the 100 MW one; each order covers the demand the advice sized it for and one unit fewer would not; and once
>      the orders have opened the carry covers the peak.
> 
>   3. THE GRID UNITS' POSTS FOLLOW THE LOAD THE WIRES CARRY. On a fixed grid, as the draw rises the share used rises and the posts
>      offered with it, each type at postsOffered(raw posts, share); past the carry every post is offered; the labour market is
>      offered every post less exactly the grid's withheld; and the payroll is struck on the posts offered.
> 
>   4. THE MONTHS ON THE WAY ARE THE SITE'S PACE. A 100 MW Substation among many small sites that the month's water-fill caps: the
>      rule's wait reads a month or more longer than the pace, the POWER row reads the pace (owed points over the points the plan
>      gives the site), and the month then advances the site by exactly those points.
> 
>   5. THE MONEY AUDIT STAYS EXACT to the cent over section 2's months.
> 
> Every fixture CAUSES its condition rather than waiting for it, and asserts against the model's own constants and getters.

**Uses:** [Game](Game.md) (48), [CityNeeds](CityNeeds.md) (34), [Substations](Substations.md) (25), [BuildingManager](BuildingManager.md) (24), [BuildAdvice](BuildAdvice.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (14), [JobType](JobType.md) (11), [ConstructionControl](ConstructionControl.md) (8), [BuildCard](BuildCard.md) (5), [AutoBuilder](AutoBuilder.md) (3), [MoneyAudit](MoneyAudit.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [SubstationCheck](SubstationCheck.md) (2), [GameFiles](GameFiles.md) (1), [PriceLevel](PriceLevel.md) (1), [PowerDispatch](PowerDispatch.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [DeathCareFirstCheck](DeathCareFirstCheck.md) (1), [BuildingType](BuildingType.md) (1), [CareType](CareType.md) (1), [Construction](Construction.md) (1)

## Sections

| line | section |
|---:|---|
| 125 | 1 |
| 187 | 2 |
| 280 | 3 |
| 335 | 4 |
| 625 | 5 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `TownGridCheck.OUT` | `System.out, QUIET = new PrintStream(OutputStream.nullOutputStream())` |  |
| 45 | `TownGridCheck.GRID` | `BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), POWER = BuildAdvice.Measure.of...` |  |
| 192 | `TownGridCheck.GROWTH` | `new ArrayList<>()` |  |
| 197 | `TownGridCheck.FIRST_WORKS` | `4, WORKS_A_MONTH = 3, GROWING_MONTHS = 8, SETTLING_MONTHS = 24, MOST_MONTHS =...` | Stock added a month: the town's water works. |
| 338 | `TownGridCheck.NEARLY_DONE` | `.99` | The share of its points a small site has built when the big one is ordered: it owes the least of its month, however large its weight on the crews (BuildingManager.siteShares()). |
| 341 | `TownGridCheck.PACE_WARM_MONTHS` | `36, PACE_CREWS = 30_000, SMALL_SITES = 36` | The months the town lives before section 4's site is ordered, and the output its crews are raised to at founding: a town whose labour, builders and population hold the figures a month keeps. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 193 | `static long growthSubstations` |  |
| 194 | `static double growthSlack` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 607 | **type** `public final class TownGridCheck` | Power for a small town (batch ASH1). |
| 41 | 1 | `static void check(String label, boolean ok)` |  |
| 42 | 1 | `static boolean near(double a, double b, double relative)` |  |
| 43 | 1 | `static void quietly(Runnable work)` |  |
| 48 | 10 | `static Game city(String label)` | A founded city with cash and ground to spare, and nothing built of the grid: the founding 10 MW is its wires. |
| 59 | 5 | `static BuildingsTemplate named(Game g, String name)` |  |
| 66 | 1 | `static double[] wires(Game g)` | The grid's pair at today's demand: the wires' carry and the peak asked of them. |
| 69 | 1 | `static double[] plants(Game g)` | The plants' pair at today's demand. |
| 71 | 4 | `static CityNeeds.Need powerRow(Game g)` |  |
| 77 | 14 | `static void drawPast(Game g, double kw)` | The town's water works, a municipal building that stands whatever the businesses do and draws power by its template: stock added until the grid's peak is over `kw`. |
| 93 | 10 | `static void plantsOver(Game g, double times)` | Plants over the peak: Coal Power Plants (the players' first plant) stood until the plants' served share is `times` the peak, however thinly the town staffs them. |
| 105 | 7 | `static Game shortTown(String label)` | A town drawing 1.3 times the founding grid at its peak, with plants twice that: its plants serve it and its wires do not. |
| 114 | 10 | `static Game settledTown(String label, int months, int crews)` | A short town (as shortTown()) that has lived `months` months with crews raised, so that its labour, builders and population have settled to the figures a month then keeps. |

### 1 (lines 125-186)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 59 | `static void theRow()` |  |

### 2 (lines 187-279)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 1 | **type** `record Month(int month, double peak, double carry, long standing, long onSite, long ordered, double need, d...` | One month of the growing town: what the pass saw and ordered, and what the month's money audit left. |
| 199 | 80 | `static void growth()` |  |

### 3 (lines 280-334)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 52 | `static void posts()` |  |

### 4 (lines 335-624)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 21 | `static void pace()` |  |
| 370 | 13 | `static void trap(Game g)` | Standing Paved Roads as ConstructionTrapCheck lays them, as the player lays them between the presses, until their repairs take the builders' whole month: the roads raise the road ratio and with it the builders' month ... |
| 385 | 6 | `static boolean sameOnEveryScreen(Game g, BuildingsTemplate t, Game.SiteLeft left, CityNeeds.Need row)` | The site's months left on every other screen equal the right panel's line's, to the bit: the overview's row (null where it shows none), the build card's and the construction page's. |
| 398 | 30 | `static void queued(Game g)` | REV-ASH1 4, the queue's wait and no work at all, on the town the contractor is off in (section 4's first). |
| 437 | 58 | `static void handedAbroad()` | REV-ASH1 4, the foreign crews' work on a site handed to them (FC2b, ForeignChoiceCheck's section 5): a town of its own, warmed as section 4's with the contractor on (a handover needs the foreign trade, BuildingManager... |
| 503 | 49 | `static void noHomeOutput()` | REV-ASH1 3: a town whose own crews give the sites nothing, standing roads' repairs taking every point of the builders' month (the trap, ConstructionTrapCheck), while the foreign contractor's crews build: the right pan... |
| 553 | 71 | `static void pace(Game g, boolean contractorOn)` |  |

### 5 (lines 625-643)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 6 | `static void money()` |  |
| 634 | 9 | `public static void main(String[] args)` |  |

