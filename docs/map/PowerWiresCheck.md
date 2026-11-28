# PowerWiresCheck.java - 807 lines · 43 methods · 17 constants · harnesses

`ham/citybuildersim/PowerWiresCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The wires are ordered with the power, and the grid is planned at its date and latitude (batch PW9-G).
> 
> WHY. REV-PW9 P1-b: 0.9.218's modern-equipment city ordered a 21 MW Diesel and 9 Small Steam Stations at pre134-135 behind 15 MW of
> wires, its Substation held 13 passes by the High school's protected completion; PW9's round 2 had 31 stations behind the founding 10 MW,
> and 12.61 GWh of its months unserved. The dispatch caps every half-block at what the wires carry, so generation past them is capital
> that never flows. Each of these fails on the model before PW9-G.
> 
> WHAT THIS HAS TO PROVE.
> 
>   1. THE CONDITION AND ITS REAL EFFECT. A town on the founding wires, its load's peak past them, plants standing with firm supply well
>      past it: a native month serves no half-block more than the wires carry, leaves load unserved, generates no more than the wires let
>      through, and serves what it asked less what it left. No law here: it is the physics the law answers to.
> 
>   2. THE WIRES ORDERED WITH THE POWER. Ample builders, nothing protected, the automatic builder on: the plants short at the next
>      plant's opening and the wires short of the load there though they carry today's. In one pass the wires are ordered before the plant,
>      and carry the load at its date; the months that follow, until every order has opened, are never wire-bound. The same town built
>      afresh and run the same months with the law off (AutoBuilder.wiresOffForTest, 0.9.218's builder) is wire-bound in some month, and
>      the law serves of the load asked no less than it does.
> 
>   3. THE WIRES HELD BY A PROTECTED SCHOOL'S DEADLINE. Where a service lacking now has its first completion quoted and the builders are
>      tight, the wires are held by it (the protected law stands) and the plants are cut to the share the wires carry, or held whole where
>      they already reach it; and while the service goes up the grid is ordered only in a month it is lacked now (the crew guard's exemption).
>      Against the same town with the law off, the cut orders fewer plant units, leaves no more months unserved with the plants short, and
>      serves of the load asked at least what the plants it cut would have (less a part in a thousand).
> 
>   4. WHAT THE LAW LEAVES ALONE. (a) Wires that carry the load at the plant's date: the order is not cut and no wires are ordered. (b) An
>      order the plants on site already serve the load for (the spread's wind, for the fuel it saves) behind wires short at its date: placed
>      whole, not cut.
> 
>   5. THE GRID'S PEAK AT ITS DATE AND LATITUDE. Now it is the native dispatch's own month at the city's latitude; ahead it is the highest
>      month of that season over the months the order serves, from its opening to its horizon, at most a year of them.
> 
>   6. THE MONEY AUDIT STAYS EXACT to the cent over every month the sections above ran.
> 
> Every fixture CAUSES its condition rather than waiting for it, and asserts against the model's own constants and getters.

**Uses:** [BuildAdvice](BuildAdvice.md) (66), [Game](Game.md) (39), [AutoBuilder](AutoBuilder.md) (32), [Substations](Substations.md) (22), [BuildingsTemplate](BuildingsTemplate.md) (18), [BuildingManager](BuildingManager.md) (14), [UtilitiesHandler](UtilitiesHandler.md) (12), [PowerDispatch](PowerDispatch.md) (12), [WorksValue](WorksValue.md) (5), [CityCalendar](CityCalendar.md) (4), [PriceLevel](PriceLevel.md) (3), [BuildingType](BuildingType.md) (2), [WorksFinanceCheck](WorksFinanceCheck.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [MoneyAudit](MoneyAudit.md) (2), [GameFiles](GameFiles.md) (1), [TempDirs](TempDirs.md) (1), [EssentialWorksCheck](EssentialWorksCheck.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [CareType](CareType.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `PowerWiresCheck.OUT` | `System.out, QUIET = new PrintStream(OutputStream.nullOutputStream())` |  |
| 55 | `PowerWiresCheck.GRID` | `BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), POWER = BuildAdvice.Measure.of...` |  |
| 58 | `PowerWiresCheck.DIESEL` | `"Diesel Station"` | The one plant the fixtures' city knows: 19 MW firm in one unit, small enough to be ordered a unit at a time. |
| 61 | `PowerWiresCheck.OVER_CARRY` | `1.4, PLANTS_OVER = 2` | Section 1: the town's peak is this many times the founding wires'; its plants' firm supply this many times the peak. |
| 64 | `PowerWiresCheck.UNDER_CARRY` | `.85` | Section 2: the town's peak today is this share of the founding wires' carry, so the wires carry it now and not the peak a plant's opening brings. |
| 67 | `PowerWiresCheck.HELD_LOAD` | `.95` | Section 3: the town's peak today as a share of the carry of the founding wires and a distribution unit. |
| 70 | `PowerWiresCheck.HELD_MONTHS` | `16` | Section 3: the native months run after the pass: the school's promised thirteen and three more. |
| 73 | `PowerWiresCheck.LEAST_MONTHS` | `15, MOST_MONTHS = 70` | Section 2: the native months run at least this many (a year and a quarter, so that the highest season comes round), at most MOST_MONTHS. |
| 76 | `PowerWiresCheck.AUDIT` | `new ArrayList<>()` | Every month's money audit residual, sections 2 and 3's, for section 6. |
| 272 | `PowerWiresCheck.YEAR_AFTER` | `13` | The months a run goes on after every order has opened (a year and a month, so that the highest season comes round with them all standing). |
| 466 | `PowerWiresCheck.CARE` | `BuildAdvice.Measure.care(CareType.GENERAL)` | The service whose first completion the fixtures protect: General care's Community Health Centres. |
| 469 | `PowerWiresCheck.STEAM` | `"Small Steam Station"` | The one plant section 3 knows: a unit a twentieth of a Diesel Station's, so that an order of several is cut to fewer. |
| 685 | `PowerWiresCheck.WIND` | `"Wind Farm"` | The one plant section 4 (b)'s spread orders: a farm that displaces the diesels' fuel. |
| 688 | `PowerWiresCheck.SPREAD_UNITS` | `2` | Section 4 (b): the distribution units that stand, so that the wires carry more than the founding grid's plant and the diesels run past it. |
| 737 | `PowerWiresCheck.SEASON_DRAW` | `.5` | Section 5: the town's draw as a share of the founding wires', the ahead windows' months, and their growth and slack. |
| 738 | `PowerWiresCheck.WINDOWS` | `{ 3, 8, 14, 20, 30, 45 }` |  |
| 739 | `PowerWiresCheck.WINDOW_K` | `1.07, WINDOW_SLACK =.1` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static int fails` |  |
| 276 | `int months, wireBound, gridWhileHeld, firstGrid` |  |
| 281 | `final String law` |  |
| 282 | `long plantUnits` |  |
| 283 | `double plantSpend, plantPoints, asked, served` |  |
| 284 | `int months, wireBound, plantBound` |  |
| 285 | `final StringBuilder gridOpened` |  |
| 287 | `final List<String> trace` | Month by month: plants standing, plants on site, offered kW, carry kW, peak kW, unserved kWh, and what bound the month. |
| 289 | `final List<Long> standing` | Month by month: the plants standing, the grid's units standing and the kWh served, for comparing one run with another. |
| 290 | `final List<Double> servedBy` |  |
| 292 | `final List<Double> careDone` | Month by month: the care centres' construction points done (the protected site the plants share the crews with), whether the plants bound the month, and the plants' offered kW. |
| 293 | `final List<Boolean> plantBoundBy` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 47 | 761 | **type** `public final class PowerWiresCheck` | The wires are ordered with the power, and the grid is planned at its date and latitude (batch PW9-G). |
| 51 | 1 | `static void check(String label, boolean ok)` |  |
| 52 | 1 | `static boolean near(double a, double b, double relative)` |  |
| 53 | 1 | `static void quietly(Runnable work)` |  |
| 80 | 5 | `static BuildingsTemplate named(Game g, String name)` | ------------------------------------------------------------------------------------------------------------------ fixtures |
| 90 | 13 | `static Game city(String label, long crews)` | A founded city with cash and ground to spare, the founding 10 MW its wires and nothing built of the grid or the power; the Diesel Station the one plant known, and builders of `crews` output (Construction Depots stood ... |
| 108 | 16 | `static Game town(String label, String...menu)` | A founded town (WorksFinanceCheck.reserveTown(): its people, its works, every essential service served, a flat history) with its Coal Power Plant retired, so that its plants are the founding grid's 10 MW alone, and th... |
| 126 | 8 | `static String describe(Game g)` | What the fixture's town is, for the log: people, draw, wires, plants, the plants' staffing and the builders. |
| 136 | 1 | `static double[] wires(Game g)` | The grid's pair at today's demand: the wires' carry and the peak asked of them. |
| 139 | 1 | `static double[] plants(Game g)` | The plants' pair at today's demand. |
| 142 | 14 | `static void drawPast(Game g, double kw)` | The town's water works, a municipal building that stands whatever the businesses do and draws power by its template: stock added until the grid's peak is over `kw`. |
| 158 | 14 | `static void drawTo(Game g, double kw)` | The town's water works stood as far as the grid's peak stays at or under `kw` (drawPast()'s whole works, one fewer than it): at most that. |
| 174 | 10 | `static void plantsTo(Game g, double kw, String plant)` | Units of a plant stood until the plants' firm supply is `kw`. |
| 186 | 5 | `static int lowestMonth()` | The calendar month (1-12) whose season is the year's lowest on the north's calendar: a plant ordered in it opens into a higher one. |
| 193 | 3 | `static void advanceTo(Game g, int monthOfYear)` | The months run, quietly, until the calendar stands at `monthOfYear`. |
| 198 | 3 | `static BuildAdvice.Suggestion powerCard(Game g)` | The power suggestion as the automatic builder's pass asks it: the city's cash and ground, its slack, every plant it knows. |
| 203 | 1 | `static BuildAdvice.Ahead loadAt(BuildAdvice.Ahead p)` | What the wires are asked at a power order's own date and demand: its months, growth and slack, the grid reading no power factor. |
| 206 | 1 | `static double wiresShare(Game g, BuildAdvice.Ahead p)` | The wires' served share at a date, the grid's own units on site counted. |
| 209 | 3 | `static double plantsShare(Game g, BuildAdvice.Ahead p)` | The plants' served share at the order's own date, the plants on site counted from their own openings. |
| 214 | 8 | `static double windowSeason(Game g, double months, double lat)` | The highest season of the native dispatch over the months an order serves: from its opening to its horizon, at most a year of them; the harness's own oracle. |
| 224 | 6 | `static double offered(Game g)` | What the plants offered the month last struck, kW: every dispatch row's offer. |
| 232 | 4 | `static boolean wireBound(Game g)` | Whether the month last struck left load unserved while the plants offered more than the wires carry. |
| 239 | 29 | `static void physics()` | ------------------------------------------------------------------------------------------------------------------ 1 |
| 275 | 3 | **type** `static final class Run` | What a run of native months found. |
| 280 | 69 | **type** `static final class Delivery` | What a run delivered, the law on or off: the plants the builder ordered and what they cost, the months the grid failed and how, the load asked and served. |
| 295 | 1 | `Delivery(String law)` _(in PowerWiresCheck.Delivery)_ |  |
| 298 | 3 | `void passed(AutoBuilder auto)` _(in PowerWiresCheck.Delivery)_ | The plant units the builder's last pass ordered, and their cost. |
| 303 | 5 | `void passed(Game g)` _(in PowerWiresCheck.Delivery)_ | ...and the construction points those units put on the crews (the units' own: the preparation and works a site may owe are not counted). |
| 310 | 22 | `void struck(Game g)` _(in PowerWiresCheck.Delivery)_ | What the month last struck asked and served, and whether the wires or the plants bound it (unserved while the plants offered no more than the carry). |
| 334 | 6 | `int firstDifference(Delivery o)` _(in PowerWiresCheck.Delivery)_ | The first month (counted from 1) in which this run's plants standing, grid standing or kWh served differ from `o`'s, or 0 where they are the same in every month of both. |
| 342 | 1 | `double share()` _(in PowerWiresCheck.Delivery)_ | Served over asked across the run: 1 where nothing was asked. |
| 344 | 4 | `void print()` _(in PowerWiresCheck.Delivery)_ |  |
| 351 | 4 | `static long gridUnits(Game g)` | The grid units standing: Substations and distribution units. |
| 362 | 28 | `static Run run(Game g, BuildAdvice.Measure care, int most, Delivery d, boolean exact)` | Native months, one at a time and the automatic builder on, until every order of the grid, the power and `care` (null for none) has opened and YEAR_AFTER months more have run, at most `most`; or, `exact`, `most` months... |
| 392 | 7 | `static Game orderTown(String label)` | Section 2's town, built afresh: the builders ample, nothing protected, the automatic builder on at slack 0, the draw UNDER_CARRY of the founding wires in the year's lowest month. |
| 400 | 62 | `static void ordered()` |  |
| 475 | 17 | `static Game schoolTown(String label)` | 3's town: section 2's, the Small Steam Station the plant known, with General care lacking now, its Community Health Centres going up on the builders the town has and their first completion saved (PowerPlanCheck's care... |
| 494 | 8 | `static boolean delays(Game g, BuildingsTemplate t, long count)` | Whether `count` of the order would put the protected centres' first completion past its quote (AutoBuilder.pendingDelay(), read from outside). |
| 508 | 16 | `static Game heldTown(boolean cut)` | 3's town, built afresh: the school's town in the year's lowest month with a distribution unit standing and the draw HELD_LOAD of the carry (the wires carry today's peak, and not the highest season's), the automatic bu... |
| 526 | 10 | `static String sites(Game g)` | The two sites a plant order crowds, for the log: the care centres and the grid's units, each as standing, on site and points done of points on site. |
| 541 | 105 | `static void held(boolean cut)` | 3 (a) and (b): the plants short of the wires' share (a) or already at it and short of the reserve (b), and the wires' units held by the school's deadline; then the same town with the law off, and what each delivered. |
| 649 | 34 | `static void leftAlone()` | ------------------------------------------------------------------------------------------------------------------ 4 |
| 695 | 38 | `static void spreadLeftAlone()` | 4 (b): the town of section 1 (the founding wires short of its peak, Diesel Stations standing well past it) knowing the Wind Farm too, so that the plants on site serve the load at the farm's opening and the one order t... |
| 741 | 44 | `static void season()` |  |
| 788 | 6 | `static void money()` | ------------------------------------------------------------------------------------------------------------------ 6 |
| 795 | 12 | `public static void main(String[] args)` |  |

