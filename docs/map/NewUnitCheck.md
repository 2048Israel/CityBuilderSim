# NewUnitCheck.java - 941 lines · 30 methods · 3 constants · harnesses

`ham/citybuildersim/NewUnitCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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
> 
> Sections 7 and 12 draw on the JavaFX toolkit (ui.KeptBench, KeptCheck's way); where it cannot start, their card parts
> are said to be skipped, section 7 orders through buildStack() and says so, and the rest runs: the harness exits 0
> without them, as KeptCheck does.

**Uses:** [BuildCard](BuildCard.md) (46), [JobType](JobType.md) (44), [Good](Good.md) (41), [Game](Game.md) (32), [BuildingsTemplate](BuildingsTemplate.md) (26), [NewUnit](NewUnit.md) (21), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (19), [KeptCheck](KeptCheck.md) (14), [BuildAdvice](BuildAdvice.md) (12), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (11), [BuildingsStacks](BuildingsStacks.md) (10), [ResearchCatalog](ResearchCatalog.md) (10), [KeptBench](KeptBench.md) (10), [WageBand](WageBand.md) (9), [EquipmentFit](EquipmentFit.md) (7), [BuildingManager](BuildingManager.md) (7), [Recipe](Recipe.md) (5), [Recipes](Recipes.md) (4), [Founding](Founding.md) (4), [Sector](Sector.md) (3), [Bank](Bank.md) (3), [WorksValue](WorksValue.md) (3), [BuildingType](BuildingType.md) (3), [RecipeChange](RecipeChange.md) (2), [PopulationManager](PopulationManager.md) (2), [CityNeeds](CityNeeds.md) (2), [EducationType](EducationType.md) (2), [Money](Money.md) (2), [RecipeCheck](RecipeCheck.md) (2), [FittingCheck](FittingCheck.md) (2)... and 8 more

## Sections

| line | section |
|---:|---|
| 98 | · the actual state a new building runs |
| 212 | · 1 |
| 253 | · 2 |
| 294 | · 3 |
| 351 | · 4 |
| 377 | · 5 |
| 427 | · 6 |
| 449 | · 7 and 8 |
| 638 | · 9 |
| 688 | · 10 |
| 742 | · 11 |
| 801 | · 12 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 80 | `NewUnitCheck.out` | `System.out` |  |
| 82 | `NewUnitCheck.ROBOT` | `"welding-robot"` |  |
| 192 | `NewUnitCheck.NO` | `JobType.NO_DIPLOMA.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `static int fails, checks` |  |
| 79 | `static boolean quietRun` |  |
| 102 | `final int[] jobs` |  |
| 103 | `int total, electricity` |  |
| 104 | `double gen, production1, estimate, running` |  |
| 164 | `Game game` |  |
| 165 | `BuildingsTemplate works` |  |
| 166 | `BuildingsStacks stack` |  |
| 167 | `Ran actual` |  |
| 168 | `double rejectedAt` |  |
| 195 | `static boolean toolkitTried` | Whether the toolkit has been tried, and why it is down (null: up); it starts once in a JVM, so sections 7 and 12 share it. |
| 196 | `static String toolkitDown` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 865 | **type** `public final class NewUnitCheck` | A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every future-building figure. |
| 84 | 6 | `static void check(String label, boolean ok, String detail)` |  |
| 91 | 1 | `static void section(String title)` |  |
| 93 | 1 | `static boolean near(double a, double b)` |  |
| 96 | 1 | `static boolean apart(double a, double b)` | Whether two readings differ by more than rounding: the premise that a reader of the fleet would be told apart. |

### the actual state a new building runs (lines 98-211)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 10 | **type** `static final class Ran` | The live figures of a template, and the investors' numbers, read in one state. |
| 106 | 4 | `public String toString()` _(in NewUnitCheck.Ran)_ |  |
| 112 | 15 | `static Ran ranOf(Game g, BuildingsTemplate t, boolean priced)` |  |
| 129 | 6 | `static Set<String> rejected(Game g, BuildingsTemplate t)` | The pieces a new building of t would not get now. |
| 140 | 12 | `static<T> T whereItRuns(Game g, BuildingsTemplate t, Supplier<T> read)` | Reads where a new building of t actually runs: the standing fleet's stack set without every piece a new one would not get, the recipes applied, `read` run there, then the fitting put back and applied again. |
| 153 | 8 | `static boolean sameFigures(NewUnit u, BuildingsTemplate t)` |  |
| 163 | 7 | **type** `static final class Cars` | A 1900 city with one fitted Vehicle Works and every discovery known at the robot's date. |
| 171 | 10 | `static Cars robotTown(String label) throws Exception` |  |
| 183 | 8 | `static boolean rejectRobot(Cars c)` | Steps machinery from a cent up by half again until the verdict for a new plant drops the robot; false if it never does. |
| 199 | 7 | `static String toolkit()` | The JavaFX toolkit, started at the first ask (KeptCheck.startJavaFx()): null when it is up, else why not. |
| 208 | 3 | `static double fleetQuote(Game g, BuildingsTemplate t, Sector owner)` | The fitted fleet's quote: what the sector says of the fleet's figures, as a reader of the template would have quoted. |

### 1 (lines 212-252)

| line | len | member | says |
|---:|---:|---|---|
| 214 | 38 | `static void standingEverywhere() throws Exception` |  |

### 2 (lines 253-293)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 38 | `static void rejectedRobot(Cars c) throws Exception` |  |

### 3 (lines 294-350)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 54 | `static void staffingBoundary(Cars c) throws Exception` |  |

### 4 (lines 351-376)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 23 | `static void pulpChoice() throws Exception` |  |

### 5 (lines 377-426)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 47 | `static void bankClerks() throws Exception` |  |

### 6 (lines 427-448)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 19 | `static void matching(Cars c) throws Exception` |  |

### 7 and 8 (lines 449-637)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 15 | `static List<Object> siteSnapshot(Game g, BuildingsTemplate w)` | An order on site as the Build card's reads must leave it: the stack, its sites and their contracts. |
| 468 | 139 | `static void identitiesAndSite() throws Exception` |  |
| 609 | 1 | `static String unskilled(long posts)` | The order bar's words for this many unskilled posts (BuildScreen.refreshOrderBar(), jobPlural()). |
| 616 | 6 | `static double latentWith(Game g, double coming)` | The landlords' head shortage with `coming` posts on site, from its parts (RealEstate.latentHeadShortfall()'s arithmetic, no home on site): every post standing, the builders' laid-off crews included, and the coming one... |
| 624 | 13 | `static double[] loadsAhead(Game g, BuildingsTemplate t, StringBuilder said, int[] done)` | The power ahead of a queue on a stack: the readings at half the first completion, just before it, at it and past the last. |

### 9 (lines 638-687)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 47 | `static void changedDraw() throws Exception` |  |

### 10 (lines 688-741)

| line | len | member | says |
|---:|---:|---|---|
| 690 | 44 | `static void crewDemand() throws Exception` |  |
| 736 | 5 | `static double college(Ran r)` | A building's posts in the college band (COLLEGE_HEALTH, _BUSINESS, _ENGINEERING), the band a college teaches. |

### 11 (lines 742-800)

| line | len | member | says |
|---:|---:|---|---|
| 744 | 56 | `static void powerAhead() throws Exception` |  |

### 12 (lines 801-941)

| line | len | member | says |
|---:|---:|---|---|
| 803 | 108 | `static void cardGoods() throws Exception` |  |
| 912 | 29 | `public static void main(String[] args) throws Exception` |  |

