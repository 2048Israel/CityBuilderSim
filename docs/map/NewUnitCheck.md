# NewUnitCheck.java - 1,206 lines · 35 methods · 3 constants · harnesses

`ham/citybuildersim/NewUnitCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every
> future-building figure.
> 
> WHY (QN1, R2e's fifth review). In a city founded the 1900 way a template's live figures are the standing fleet's mix, while
> a building ordered now is fitted with whatever EquipmentFit.fittedForNew() keeps: a Vehicle Works fleet carrying the
> welding robot runs 100 unskilled posts, and the plant ordered while machinery is dear opens without the robot and needs
> 300. The investors, the Build card, the order bar, the staffing gate, the posts and draw on site, the bank's new-branch
> fee gate and the power ahead each quoted the fleet's 100. NewUnit.of() resolves one new building's figures where they are
> read, and the template stays what is ordered, stacked and keyed.
> 
> No oracle here resolves a proposal. Each expected value is taken from the ACTUAL state a new building runs: the fleet's
> stack set unfitted for the pieces a new one would not get, the recipes applied, and the template's live figures read
> there, or a door read through the standing routes (NewUnit.standing(), or the investors' door under
> NewUnit.standingWhile(): the template as it stands, as main read it), then the fitting restored; or it is composed from
> the recipe data. A wrong proposal therefore cannot reach both sides (REV-QN1, B). The fleet's own figures appear only as
> each section's PREMISE: that the expected value differs from what a reader of the fleet would give, so a reader that
> kept reading the fleet fails.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. A city founded the default way reads every template as it stands (nothing proposed, the same posts, draw, makes and
>      uses), and no recipeChanges "without" block in buildings.json moves land, which NewUnit does not carry.
>   2. The rejected welding robot: the fleet keeps 100 unskilled posts, the actual new plant has 300, and the new unit, the
>      investors' estimate, the appraisal and the investors' summary all read the new plant's.
>   3. The staffing boundary with arrivals closed: the Build card carries the 300-post unit and its running bill, the
>      investors' own gate is STAFFING where the fleet's 100 posts would pass, and the order bar counts 600 for two.
>   4. The pulp mill's saved choice is kept, not re-chosen afresh by the moved prices (a guard).
>   5. The bank's new-branch estimate and fee gate price the new branch's own clerks once, on the one branch that has them,
>      and the gate's boundary is told from the removed (N + 1) x (C + extra) one.
>   6. With the robot accepted again the new unit's figures equal the template's, the template being the same object.
>   7. Identities: the card, the appraisal and the investors keep the catalogue template through a month and a changed
>      fitting input; the Build screen's own card, drawn kept, keeps the Build button and steps it was drawn with a month
>      on, whose steps pressed then reprice the order bar at the actual new plants' posts and whose Build button orders the
>      catalogue template; and the order on site is not touched by reading it.
>   8. What is on site counts the actual new plants' posts, and the landlords' head shortage counts them too.
>   9. The power ahead of a queued changed draw: two Cement Plants without their rotary kiln add the shaft kilns' draw,
>      not the fleet's, and only from their completion.
>  10. What a queued changed crew asks of the city: two Shared Services Centres without their computer add their own
>      college posts to the colleges' places (CityNeeds.hires()) and their own posts to the landlords' head shortage.
>  11. Power ahead: a queued Coal Power Plant whose new unit rejects the turbine and the pulverised coal generates less,
>      and the power measure reads that generation.
>  12. The card's goods: a new Electric Arc Furnace without its continuous caster burns fuel oil, and the card's words and
>      its group's heading say it; a new Logging Camp without its chainsaw cuts less, and the card's outlets and its
>      sentences say that cut; and a tripwire that no piece changes which goods are made, nor what a building on typed
>      ground makes, where the heading's made words and stands() would read a new unit and the fleet alike.
>  13. Held for one pass (AP1, WorksValue.hold()), on section 9's queued plants: inside a scope the power ahead at every
>      horizon, and a power plant's avoided loss, are the bare figures to the bit; an order, a rush and a priority placed
>      inside it, and the progress and the builders-first switch moved by hand, are read at the next call; one schedule is
>      built for the scope with nothing changed on site (a count, WorksValue.SCHEDULES); a second city (saved and loaded),
>      a second thread, and the second city's hold nested in g's read their own bare figures and leave g's scope as it
>      was; closed, nothing is held.
> 
> Sections 7 and 12 draw on the JavaFX toolkit (ui.KeptBench, KeptCheck's way); where it cannot start, their card parts
> are said to be skipped, section 7 orders through buildStack() and says so, and the rest runs: the harness exits 0
> without them, as KeptCheck does.

**Uses:** [WorksValue](WorksValue.md) (58), [Game](Game.md) (46), [BuildCard](BuildCard.md) (46), [JobType](JobType.md) (44), [Good](Good.md) (41), [BuildingsTemplate](BuildingsTemplate.md) (32), [NewUnit](NewUnit.md) (21), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (20), [BuildAdvice](BuildAdvice.md) (18), [KeptCheck](KeptCheck.md) (14), [BuildingsStacks](BuildingsStacks.md) (11), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (11), [ResearchCatalog](ResearchCatalog.md) (10), [KeptBench](KeptBench.md) (10), [WageBand](WageBand.md) (9), [BuildingManager](BuildingManager.md) (9), [EquipmentFit](EquipmentFit.md) (7), [Founding](Founding.md) (6), [Recipe](Recipe.md) (5), [Sector](Sector.md) (4), [Recipes](Recipes.md) (4), [RecipeCheck](RecipeCheck.md) (4), [Bank](Bank.md) (3), [BuildingType](BuildingType.md) (3), [RecipeChange](RecipeChange.md) (2), [PopulationManager](PopulationManager.md) (2), [ConstructionControl](ConstructionControl.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (2), [CityNeeds](CityNeeds.md) (2), [EducationType](EducationType.md) (2)... and 12 more

## Sections

| line | section |
|---:|---|
| 104 | · the actual state a new building runs |
| 218 | · 1 |
| 259 | · 2 |
| 300 | · 3 |
| 357 | · 4 |
| 383 | · 5 |
| 433 | · 6 |
| 455 | · 7 and 8 |
| 644 | · 9 |
| 697 | · 13 |
| 945 | · 10 |
| 999 | · 11 |
| 1065 | · 12 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `NewUnitCheck.out` | `System.out` |  |
| 88 | `NewUnitCheck.ROBOT` | `"welding-robot"` |  |
| 198 | `NewUnitCheck.NO` | `JobType.NO_DIPLOMA.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `static int fails, checks` |  |
| 85 | `static boolean quietRun` |  |
| 108 | `final int[] jobs` |  |
| 109 | `int total, electricity` |  |
| 110 | `double gen, production1, estimate, running` |  |
| 170 | `Game game` |  |
| 171 | `BuildingsTemplate works` |  |
| 172 | `BuildingsStacks stack` |  |
| 173 | `Ran actual` |  |
| 174 | `double rejectedAt` |  |
| 201 | `static boolean toolkitTried` | Whether the toolkit has been tried, and why it is down (null: up); it starts once in a JVM, so sections 7 and 12 share it. |
| 202 | `static String toolkitDown` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 1124 | **type** `public final class NewUnitCheck` | A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every future-building figure. |
| 90 | 6 | `static void check(String label, boolean ok, String detail)` |  |
| 97 | 1 | `static void section(String title)` |  |
| 99 | 1 | `static boolean near(double a, double b)` |  |
| 102 | 1 | `static boolean apart(double a, double b)` | Whether two readings differ by more than rounding: the premise that a reader of the fleet would be told apart. |

### the actual state a new building runs (lines 104-217)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 10 | **type** `static final class Ran` | The live figures of a template, and the investors' numbers, read in one state. |
| 112 | 4 | `public String toString()` _(in NewUnitCheck.Ran)_ |  |
| 118 | 15 | `static Ran ranOf(Game g, BuildingsTemplate t, boolean priced)` |  |
| 135 | 6 | `static Set<String> rejected(Game g, BuildingsTemplate t)` | The pieces a new building of t would not get now. |
| 146 | 12 | `static<T> T whereItRuns(Game g, BuildingsTemplate t, Supplier<T> read)` | Reads where a new building of t actually runs: the standing fleet's stack set without every piece a new one would not get, the recipes applied, `read` run there, then the fitting put back and applied again. |
| 159 | 8 | `static boolean sameFigures(NewUnit u, BuildingsTemplate t)` |  |
| 169 | 7 | **type** `static final class Cars` | A 1900 city with one fitted Vehicle Works and every discovery known at the robot's date. |
| 177 | 10 | `static Cars robotTown(String label) throws Exception` |  |
| 189 | 8 | `static boolean rejectRobot(Cars c)` | Steps machinery from a cent up by half again until the verdict for a new plant drops the robot; false if it never does. |
| 205 | 7 | `static String toolkit()` | The JavaFX toolkit, started at the first ask (KeptCheck.startJavaFx()): null when it is up, else why not. |
| 214 | 3 | `static double fleetQuote(Game g, BuildingsTemplate t, Sector owner)` | The fitted fleet's quote: what the sector says of the fleet's figures, as a reader of the template would have quoted. |

### 1 (lines 218-258)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 38 | `static void standingEverywhere() throws Exception` |  |

### 2 (lines 259-299)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 38 | `static void rejectedRobot(Cars c) throws Exception` |  |

### 3 (lines 300-356)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 54 | `static void staffingBoundary(Cars c) throws Exception` |  |

### 4 (lines 357-382)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 23 | `static void pulpChoice() throws Exception` |  |

### 5 (lines 383-432)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 47 | `static void bankClerks() throws Exception` |  |

### 6 (lines 433-454)

| line | len | member | says |
|---:|---:|---|---|
| 435 | 19 | `static void matching(Cars c) throws Exception` |  |

### 7 and 8 (lines 455-643)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 15 | `static List<Object> siteSnapshot(Game g, BuildingsTemplate w)` | An order on site as the Build card's reads must leave it: the stack, its sites and their contracts. |
| 474 | 139 | `static void identitiesAndSite() throws Exception` |  |
| 615 | 1 | `static String unskilled(long posts)` | The order bar's words for this many unskilled posts (BuildScreen.refreshOrderBar(), jobPlural()). |
| 622 | 6 | `static double latentWith(Game g, double coming)` | The landlords' head shortage with `coming` posts on site, from its parts (RealEstate.latentHeadShortfall()'s arithmetic, no home on site): every post standing, the builders' laid-off crews included, and the coming one... |
| 630 | 13 | `static double[] loadsAhead(Game g, BuildingsTemplate t, StringBuilder said, int[] done)` | The power ahead of a queue on a stack: the readings at half the first completion, just before it, at it and past the last. |

### 9 (lines 644-696)

| line | len | member | says |
|---:|---:|---|---|
| 646 | 50 | `static Queued changedDraw() throws Exception` |  |

### 13 (lines 697-944)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 1 | **type** `record Queued(Game g, BuildingsTemplate kiln)` | Section 9's city after it: two Cement Plants queued on the plant's own stack, their new units' draw not the fleet's. |
| 703 | 5 | `static boolean sameBits(double[] a, double[] b)` | Every reading equal to the other to the bit (Double.doubleToLongBits). |
| 709 | 1 | `static boolean sameBits(double a, double b)` |  |
| 712 | 5 | `static double[] loads(Game g, double[] horizons)` | The power ahead at each horizon, read as the caller's scope (or none) has it. |
| 719 | 9 | `static double[] horizons(Game g, BuildingsTemplate kiln, int steps)` | Horizons from now to a month past the last unit on the kilns' site, by its own quotes: every completion inside them. |
| 729 | 215 | `static void heldForAPass(Queued q) throws Exception` |  |

### 10 (lines 945-998)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 44 | `static void crewDemand() throws Exception` |  |
| 993 | 5 | `static double college(Ran r)` | A building's posts in the college band (COLLEGE_HEALTH, _BUSINESS, _ENGINEERING), the band a college teaches. |

### 11 (lines 999-1064)

| line | len | member | says |
|---:|---:|---|---|
| 1001 | 63 | `static void powerAhead() throws Exception` |  |

### 12 (lines 1065-1206)

| line | len | member | says |
|---:|---:|---|---|
| 1067 | 108 | `static void cardGoods() throws Exception` |  |
| 1176 | 30 | `public static void main(String[] args) throws Exception` |  |

