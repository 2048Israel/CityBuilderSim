# PowerWiresCheck.java - 1,599 lines · 83 methods · 27 constants · harnesses

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
>      serves of the load asked at least what the plants it cut would have (less a part in a thousand). (a) is cut to the count the harness's own frontier
>      allows (every month from the order's opening to its date); (b)'s wire-bound months are the town's own, law on or off: its standing plants offer more than
>      the carry before any order, the same months are wire-bound in both runs and the law leaves no more unserved in them, and the grid is ordered in the
>      first month it is lacked now, every due the law keeps recorded for a plant it placed.
> 
>   4. WHAT THE LAW LEAVES ALONE. (a) Wires that carry the load at the plant's date: the order is not cut and no wires are ordered. (b) An
>      order the plants on site already serve the load for (the spread's wind, for the fuel it saves) behind wires short at its date: placed
>      whole, not cut.
> 
>   5. THE GRID'S PEAK AT ITS DATE AND LATITUDE. Now it is the native dispatch's own month at the city's latitude; ahead it is the highest
>      month of that season over the months the order serves, from its opening to its horizon, at most a year of them.
> 
>   6. THE ALLOWANCE IS A SEARCH. The rule that sizes an order (its months, each with the wires that open by then) allows a count only if it allows
>      every smaller one, since the builder finds the count by halving.
> 
>   7. A FUNDED ASK IS PLACED THROUGH THE WIRES TOO (REV-PW9-G 1). The ask for a plant whose bond has raised its cost, reopened by the pass: the wires
>      short of it are ordered first (the grid's step runs before the plant is placed), the plant is placed cut to the count the wires allow, or held
>      with the ask's own line, the ask still open and what it raised unchanged.
> 
>   8. THE FRONTIER (REV-PW9-G 2). A plant that opens months before its date, and a grid unit on site that opens between the two: the wires carry
>      the load at the date and not in the months before, so the order is cut to the count the harness's own frontier allows, and no month from
>      the plant's opening to the wires' is wire-bound.
> 
>   9. THE PROMISE (REV-PW9-G 3). A plant placed relying on grid units on site records the month they are due by; while it stands an automatic order
>      whose points would put a unit past it is held by the builders, the units are open by it, it goes the month after it passes, and a save keeps it.
> 
>  10. A RUSHED GRID SITE (REV-PW9-G 4). Units on a rushed site, and a plant's points on the same crews: the units the wires are credited by a date
>      are no more than open by it.
> 
>  11. AN ABROAD GRID SITE (REV-PW9-G 5). A site with its work handed to the foreign crews at a slow pace credits no unit before the foreign crews
>      can build it, and no more units by a date than open by it.
> 
>  12. A CANCELLED GRID SITE (REV-PW9-G 7). The law reads no carry from a site the player is cancelling: the order is cut as it is in the same
>      town with no grid site.
> 
>  13. THE MONEY AUDIT STAYS EXACT to the cent over every month the sections above ran.
> 
> ... (1 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (122), [AutoBuilder](AutoBuilder.md) (78), [Game](Game.md) (72), [BuildingsTemplate](BuildingsTemplate.md) (43), [Substations](Substations.md) (28), [BuildingManager](BuildingManager.md) (23), [PowerDispatch](PowerDispatch.md) (14), [UtilitiesHandler](UtilitiesHandler.md) (12), [WorksValue](WorksValue.md) (11), [ConstructionControl](ConstructionControl.md) (6), [CityCalendar](CityCalendar.md) (4), [PriceLevel](PriceLevel.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [BuildingType](BuildingType.md) (2), [WorksFinanceCheck](WorksFinanceCheck.md) (2), [MoneyAudit](MoneyAudit.md) (2), [GameFiles](GameFiles.md) (1), [TempDirs](TempDirs.md) (1), [EssentialWorksCheck](EssentialWorksCheck.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [CareType](CareType.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `PowerWiresCheck.OUT` | `System.out, QUIET = new PrintStream(OutputStream.nullOutputStream())` |  |
| 83 | `PowerWiresCheck.GRID` | `BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), POWER = BuildAdvice.Measure.of...` |  |
| 86 | `PowerWiresCheck.DIESEL` | `"Diesel Station"` | The one plant the fixtures' city knows: 19 MW firm in one unit, small enough to be ordered a unit at a time. |
| 89 | `PowerWiresCheck.OVER_CARRY` | `1.4, PLANTS_OVER = 2` | Section 1: the town's peak is this many times the founding wires'; its plants' firm supply this many times the peak. |
| 92 | `PowerWiresCheck.UNDER_CARRY` | `.85` | Section 2: the town's peak today is this share of the founding wires' carry, so the wires carry it now and not the peak a plant's opening brings. |
| 95 | `PowerWiresCheck.HELD_LOAD` | `.95` | Section 3: the town's peak today as a share of the carry of the founding wires and a distribution unit. |
| 98 | `PowerWiresCheck.HELD_MONTHS` | `16` | Section 3: the native months run after the pass: the school's promised thirteen and three more. |
| 101 | `PowerWiresCheck.LEAST_MONTHS` | `15, MOST_MONTHS = 70` | Section 2: the native months run at least this many (a year and a quarter, so that the highest season comes round), at most MOST_MONTHS. |
| 104 | `PowerWiresCheck.AUDIT` | `new ArrayList<>()` | Every month's money audit residual, sections 2 and 3's, for section 6. |
| 406 | `PowerWiresCheck.YEAR_AFTER` | `13` | The months a run goes on after every order has opened (a year and a month, so that the highest season comes round with them all standing). |
| 618 | `PowerWiresCheck.CARE` | `BuildAdvice.Measure.care(CareType.GENERAL)` | The service whose first completion the fixtures protect: General care's Community Health Centres. |
| 621 | `PowerWiresCheck.STEAM` | `"Small Steam Station"` | The one plant section 3 knows: a unit a twentieth of a Diesel Station's, so that an order of several is cut to fewer. |
| 862 | `PowerWiresCheck.WIND` | `"Wind Farm"` | The one plant section 4 (b)'s spread orders: a farm that displaces the diesels' fuel. |
| 865 | `PowerWiresCheck.SPREAD_UNITS` | `2` | Section 4 (b): the distribution units that stand, so that the wires carry more than the founding grid's plant and the diesels run past it. |
| 914 | `PowerWiresCheck.SEASON_DRAW` | `.5` | Section 5: the town's draw as a share of the founding wires', the ahead windows' months, and their growth and slack. |
| 915 | `PowerWiresCheck.WINDOWS` | `{ 3, 8, 14, 20, 30, 45 }` |  |
| 916 | `PowerWiresCheck.WINDOW_K` | `1.07, WINDOW_SLACK =.1` |  |
| 1094 | `PowerWiresCheck.FRONTIER_UNITS` | `2, PROMISE_UNITS = 3` | Sections 8, 9: the distribution units ordered before the pass (8: so many that the first of them opens after the plant; 9: few, so that it opens before). |
| 1125 | `PowerWiresCheck.FRONTIER_MONTHS` | `36` | Section 8: the months run, the law on and off. |
| 1226 | `PowerWiresCheck.PROMISE_WORDS` | `"waits to preserve the opening of the wires Power's plants were ordered with"` |  |
| 1320 | `PowerWiresCheck.HOME` | `"House"` | Section 9's second town: the homes going up by hand on the same crews before the pass, as many as leave the wires read open by their due (each a few points: the finest load the crews take). |
| 1408 | `PowerWiresCheck.RUSHED_UNITS` | `3, RUSHED_PLANTS = 100` | Section 10: the units on the rushed site, the plants whose points share its crews (the chosen count, and those the search tried), and the months run counting the grid's units. |
| 1409 | `PowerWiresCheck.RUSHED_SEARCH` | `{ RUSHED_PLANTS }` |  |
| 1412 | `PowerWiresCheck.GRID_MONTHS` | `70` | Sections 10, 11: the months run counting the grid's units standing. |
| 1484 | `PowerWiresCheck.ABROAD_UNITS` | `3` | Section 11: the units on the site handed abroad, and the months the foreign crews take on the first of them. |
| 1485 | `PowerWiresCheck.ABROAD_FIRST_MONTHS` | `30` |  |
| 1538 | `PowerWiresCheck.CANCELLED_UNITS` | `3` | Section 12: the units on the site that is cancelled. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `static int fails` |  |
| 296 | `final Game g` |  |
| 297 | `final BuildAdvice.Suggestion card` |  |
| 298 | `final BuildAdvice.Ahead p` |  |
| 299 | `final boolean window` |  |
| 300 | `final Map<BuildingsTemplate, Long> plants, grid` |  |
| 410 | `int months, wireBound, gridWhileHeld, firstGrid` |  |
| 412 | `final List<String> dues` | The dues the law recorded, month by month as "month: due". |
| 417 | `final String law` |  |
| 418 | `long plantUnits` |  |
| 419 | `double plantSpend, plantPoints, asked, served` |  |
| 420 | `int months, wireBound, plantBound` |  |
| 421 | `final StringBuilder gridOpened` |  |
| 423 | `final List<String> trace` | Month by month: plants standing, plants on site, offered kW, carry kW, peak kW, unserved kWh, and what bound the month. |
| 425 | `final List<Long> standing` | Month by month: the plants standing, the grid's units standing and the kWh served, for comparing one run with another. |
| 426 | `final List<Double> servedBy` |  |
| 428 | `final List<Double> careDone` | Month by month: the care centres' construction points done (the protected site the plants share the crews with), whether the plants bound the month, and the plants' offered kW. |
| 429 | `final List<Boolean> plantBoundBy` |  |
| 431 | `final List<Double> unservedBy` | Month by month: the kWh unserved, and the grid's peak as the month was entered (the draw the month's season asks of the wires). |
| 433 | `double peakNow` | The peak of the month about to run, set by run() before it. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1525 | **type** `public final class PowerWiresCheck` | The wires are ordered with the power, and the grid is planned at its date and latitude (batch PW9-G). |
| 79 | 1 | `static void check(String label, boolean ok)` |  |
| 80 | 1 | `static boolean near(double a, double b, double relative)` |  |
| 81 | 1 | `static void quietly(Runnable work)` |  |
| 108 | 5 | `static BuildingsTemplate named(Game g, String name)` | ------------------------------------------------------------------------------------------------------------------ fixtures |
| 118 | 13 | `static Game city(String label, long crews)` | A founded city with cash and ground to spare, the founding 10 MW its wires and nothing built of the grid or the power; the Diesel Station the one plant known, and builders of `crews` output (Construction Depots stood ... |
| 136 | 16 | `static Game town(String label, String...menu)` | A founded town (WorksFinanceCheck.reserveTown(): its people, its works, every essential service served, a flat history) with its Coal Power Plant retired, so that its plants are the founding grid's 10 MW alone, and th... |
| 154 | 8 | `static String describe(Game g)` | What the fixture's town is, for the log: people, draw, wires, plants, the plants' staffing and the builders. |
| 164 | 1 | `static double[] wires(Game g)` | The grid's pair at today's demand: the wires' carry and the peak asked of them. |
| 167 | 1 | `static double[] plants(Game g)` | The plants' pair at today's demand. |
| 170 | 14 | `static void drawPast(Game g, double kw)` | The town's water works, a municipal building that stands whatever the businesses do and draws power by its template: stock added until the grid's peak is over `kw`. |
| 186 | 14 | `static void drawTo(Game g, double kw)` | The town's water works stood as far as the grid's peak stays at or under `kw` (drawPast()'s whole works, one fewer than it): at most that. |
| 202 | 10 | `static void plantsTo(Game g, double kw, String plant)` | Units of a plant stood until the plants' firm supply is `kw`. |
| 214 | 5 | `static int lowestMonth()` | The calendar month (1-12) whose season is the year's lowest on the north's calendar: a plant ordered in it opens into a higher one. |
| 221 | 3 | `static void advanceTo(Game g, int monthOfYear)` | The months run, quietly, until the calendar stands at `monthOfYear`. |
| 226 | 3 | `static BuildAdvice.Suggestion powerCard(Game g)` | The power suggestion as the automatic builder's pass asks it: the city's cash and ground, its slack, every plant it knows. |
| 231 | 1 | `static BuildAdvice.Ahead loadAt(BuildAdvice.Ahead p)` | What the wires are asked at a power order's own date and demand: its months, growth and slack, the grid reading no power factor. |
| 234 | 1 | `static double wiresShare(Game g, BuildAdvice.Ahead p)` | The wires' served share at a date, the grid's own units on site counted. |
| 237 | 3 | `static double plantsShare(Game g, BuildAdvice.Ahead p)` | The plants' served share at the order's own date, the plants on site counted from their own openings. |
| 242 | 8 | `static double windowSeason(Game g, double months, double lat)` | The highest season of the native dispatch over the months an order serves: from its opening to its horizon, at most a year of them; the harness's own oracle. |
| 252 | 6 | `static double offered(Game g)` | What the plants offered the month last struck, kW: every dispatch row's offer. |
| 260 | 4 | `static boolean wireBound(Game g)` | Whether the month last struck left load unserved while the plants offered more than the wires carry. |
| 266 | 6 | `static void months(Game g, int n)` | `n` native months run, quietly, each month's money audit kept for section 13. |
| 274 | 5 | `static Map<BuildingsTemplate, Long> building(Game g, BuildAdvice.Measure m, boolean counted)` | A measure's sites on order less those the player is cancelling (the harness's own reading: the law reads no carry from a cancelled site, REV-PW9-G 7); `counted`, all of them. |
| 281 | 5 | `static double orderPoints(Game g, BuildingsTemplate t, long count)` | What `count` of `t` puts on the crews: its buildings at the points their equipment takes and its works (AutoBuilder.orderPoints() is private: its three lines). |
| 295 | 65 | **type** `static final class Frontier` | The law's rule read again by the harness (REV-PW9-G 2) from the model's own parts, for a power suggestion: a count's months run from the whole month its quote opens it by (AutoBuilder.wait()), never more than a year b... |
| 302 | 8 | `Frontier(Game g, BuildAdvice.Suggestion card, boolean window, boolean cancelled)` _(in PowerWiresCheck.Frontier)_ |  |
| 312 | 8 | `List<Double> dates(long n)` _(in PowerWiresCheck.Frontier)_ | The months n of the plant stand to its date. |
| 321 | 1 | `BuildAdvice.Ahead power(double d)` _(in PowerWiresCheck.Frontier)_ |  |
| 323 | 1 | `BuildAdvice.Ahead load(double d)` _(in PowerWiresCheck.Frontier)_ |  |
| 325 | 1 | `Map<BuildingsTemplate, Long> plantsAt(double d)` _(in PowerWiresCheck.Frontier)_ |  |
| 327 | 1 | `boolean exempt(double d)` _(in PowerWiresCheck.Frontier)_ |  |
| 330 | 5 | `double carried(Map<BuildingsTemplate, Long> open, double d)` _(in PowerWiresCheck.Frontier)_ | The wires' served share at month d with `open` of the grid's units on site open. |
| 337 | 3 | `double wires(long m, double d)` _(in PowerWiresCheck.Frontier)_ | ...with m of the plant on the crews (none: the queue as it stands). |
| 341 | 8 | `boolean allows(long m)` _(in PowerWiresCheck.Frontier)_ |  |
| 351 | 1 | `long least()` _(in PowerWiresCheck.Frontier)_ | The most units allowed, each smaller count allowed too. |
| 354 | 5 | `double due(long n)` _(in PowerWiresCheck.Frontier)_ | The first month n of the plant relies on wires on site (the month the law records): bound there, the standing wires short of its load; NaN for none. |
| 362 | 1 | `static long frontierLeast(Game g, BuildAdvice.Suggestion card)` | The harness's own frontier count of a power suggestion: the most units whose every month, from its opening to its date, the wires allow (the model's count, Frontier.least()). |
| 365 | 5 | `static int openMonth(Game g, Map<BuildingsTemplate, Long> site, long unit, BuildingsTemplate plant, long count, int most)` | The month the n-th of a measure's units on order opens by, counted from now (0 for none within `most` months): by WorksValue.openByAfter() with the plant's points. |
| 373 | 29 | `static void physics()` | ------------------------------------------------------------------------------------------------------------------ 1 |
| 409 | 5 | **type** `static final class Run` | What a run of native months found. |
| 416 | 76 | **type** `static final class Delivery` | What a run delivered, the law on or off: the plants the builder ordered and what they cost, the months the grid failed and how, the load asked and served. |
| 435 | 1 | `Delivery(String law)` _(in PowerWiresCheck.Delivery)_ |  |
| 438 | 3 | `void passed(AutoBuilder auto)` _(in PowerWiresCheck.Delivery)_ | The plant units the builder's last pass ordered, and their cost. |
| 443 | 5 | `void passed(Game g)` _(in PowerWiresCheck.Delivery)_ | ...and the construction points those units put on the crews (the units' own: the preparation and works a site may owe are not counted). |
| 450 | 25 | `void struck(Game g)` _(in PowerWiresCheck.Delivery)_ | What the month last struck asked and served, and whether the wires or the plants bound it (unserved while the plants offered no more than the carry). |
| 477 | 6 | `int firstDifference(Delivery o)` _(in PowerWiresCheck.Delivery)_ | The first month (counted from 1) in which this run's plants standing, grid standing or kWh served differ from `o`'s, or 0 where they are the same in every month of both. |
| 485 | 1 | `double share()` _(in PowerWiresCheck.Delivery)_ | Served over asked across the run: 1 where nothing was asked. |
| 487 | 4 | `void print()` _(in PowerWiresCheck.Delivery)_ |  |
| 494 | 4 | `static long gridUnits(Game g)` | The grid units standing: Substations and distribution units. |
| 505 | 37 | `static Run run(Game g, BuildAdvice.Measure care, int most, Delivery d, boolean exact)` | Native months, one at a time and the automatic builder on, until every order of the grid, the power and `care` (null for none) has opened and YEAR_AFTER months more have run, at most `most`; or, `exact`, `most` months... |
| 544 | 7 | `static Game orderTown(String label)` | Section 2's town, built afresh: the builders ample, nothing protected, the automatic builder on at slack 0, the draw UNDER_CARRY of the founding wires in the year's lowest month. |
| 552 | 62 | `static void ordered()` |  |
| 627 | 17 | `static Game schoolTown(String label)` | 3's town: section 2's, the Small Steam Station the plant known, with General care lacking now, its Community Health Centres going up on the builders the town has and their first completion saved (PowerPlanCheck's care... |
| 646 | 8 | `static boolean delays(Game g, BuildingsTemplate t, long count)` | Whether `count` of the order would put the protected centres' first completion past its quote (AutoBuilder.pendingDelay(), read from outside). |
| 660 | 16 | `static Game heldTown(boolean cut)` | 3's town, built afresh: the school's town in the year's lowest month with a distribution unit standing and the draw HELD_LOAD of the carry (the wires carry today's peak, and not the highest season's), the automatic bu... |
| 678 | 10 | `static String sites(Game g)` | The two sites a plant order crowds, for the log: the care centres and the grid's units, each as standing, on site and points done of points on site. |
| 693 | 130 | `static void held(boolean cut)` | 3 (a) and (b): the plants short of the wires' share (a) or already at it and short of the reserve (b), and the wires' units held by the school's deadline; then the same town with the law off, and what each delivered. |
| 826 | 34 | `static void leftAlone()` | ------------------------------------------------------------------------------------------------------------------ 4 |
| 872 | 38 | `static void spreadLeftAlone()` | 4 (b): the town of section 1 (the founding wires short of its peak, Diesel Stations standing well past it) knowing the Wind Farm too, so that the plants on site serve the load at the farm's opening and the one order t... |
| 918 | 44 | `static void season()` |  |
| 966 | 5 | `static long plantsStanding(Game g)` | The power plants standing, for the runs' months. |
| 973 | 6 | `static String said(AutoBuilder auto)` | The steps of the last pass that are the grid's or the power's, for the log: measure, ordered of wanted, and what cut them. |
| 980 | 17 | `static void searched()` |  |
| 1001 | 13 | `static AutoBuilder.OpenAsk fundedAsk(Game g, BuildAdvice.Suggestion card)` | The ask for a plant, open and funded: its bond has raised the order's cost and a tenth more (AskProceedsCheck's ask()), opened six months ago. |
| 1016 | 9 | `static void open(Game g, AutoBuilder.OpenAsk ask)` | The ask open on the city's books, the pass its next one. |
| 1027 | 8 | `static int stepOf(AutoBuilder auto, BuildAdvice.Measure m, boolean ordering)` | The index of the first step of the last pass that is `m`'s (and ordered some, if `ordering`); -1 for none. |
| 1036 | 54 | `static void reoffered()` |  |
| 1100 | 11 | `static Game wiresTown(String label, long units)` | The town of sections 8 and 9: the Small Steam Station the one plant known, the founding wires with a peak that passes them at the year's high season, `units` distribution units ordered through the game's own build pat... |
| 1113 | 10 | `static double idleBefore(Delivery d, long plants0, long grid0, double carry, int[] bounds)` | The months from the plants' opening (the first run month with more plants standing than at the start) to the wires' (the first with more grid units), and the kW-months the plants offered past the carry in them. |
| 1127 | 52 | `static void frontier()` |  |
| 1186 | 13 | `static double[] firstUnit(Game g, BuildAdvice.Suggestion s, long n)` | The grid's soonest first unit on site as the crew guard reads it (AutoBuilder.wiresDelayed(): each site's quote or its foreign crews' floor, the later), without and with `n` of the suggestion on the crews: {before, af... |
| 1201 | 3 | `static boolean guardHolds(Game g, double[] reading, double firstDue)` | Whether the guard holds that reading: the first unit opens later with the order, and past the earliest due. |
| 1206 | 10 | `static double unitMonths(Game g)` | The months to the grid's soonest first unit on site as the queue reads it now (firstUnit()'s before, no order). |
| 1218 | 7 | `static boolean unitLate(Game g, double firstDue)` | Whether the queue already reads the grid's first unit past the due, with no order of the plant's. |
| 1233 | 42 | `static void heavyHeld(Game g, String what, boolean counter)` | While a due stands, the town's draw grows until the pass would place an order the wires allow whose first unit the guard reads later and past the earliest due; then the pass holds it. |
| 1276 | 41 | `static void promise()` |  |
| 1323 | 10 | `static double unitMonthsWith(Game g, BuildingsTemplate t, long n)` | The grid's soonest first unit with `n` of t ordered on the crews (the crews' quote, as firstUnit()'s after). |
| 1342 | 62 | `static void overtaken()` | 9's second town: section 9's, its plant placed and its due recorded; then its power served now (plants stood), its General care stood at the line now in its least unit (the one offered for it), the reserve raised to i... |
| 1415 | 9 | `static long[] gridOpened(Game g, int most)` | The grid units open by each month 0..most of `most` months from now: the game's own count, every month's. |
| 1426 | 4 | `static int monthOf(long[] byMonth, int k)` | The first month by which `k` units are counted open in a by-month count, 0 for never within it. |
| 1431 | 4 | `static void rushed()` |  |
| 1436 | 44 | `static void rushed(long plants, boolean asserted)` |  |
| 1487 | 47 | `static void abroad()` |  |
| 1540 | 28 | `static void cancelled()` |  |
| 1571 | 6 | `static void money()` | ------------------------------------------------------------------------------------------------------------------ 13 |
| 1580 | 19 | `public static void main(String[] args)` |  |

