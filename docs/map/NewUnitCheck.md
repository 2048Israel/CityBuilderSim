# NewUnitCheck.java - 861 lines · 30 methods · 3 constants · harnesses

`ham/citybuildersim/NewUnitCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

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
>      fitting input; the Build screen's own card, drawn kept, hands back the order it was drawn with a month on, whose
>      reprice then puts the actual new plants' posts in the order bar and whose Build button orders the catalogue
>      template; and the order on site is not touched by reading it.
>   8. What is on site counts the actual new plants' posts, and the landlords' head shortage counts them too.
>   9. The power ahead of a queued changed draw: two Cement Plants without their rotary kiln add the shaft kilns' draw,
>      not the fleet's, and only from their completion.
>  10. What a queued changed crew asks of the city: two Shared Services Centres without their computer add their own
>      college posts to the colleges' places (CityNeeds.hires()) and their own posts to the landlords' head shortage.
>  11. Power ahead: a queued Coal Power Plant whose new unit rejects the turbine and the pulverised coal generates less,
>      and the power measure reads that generation.
>  12. The card's goods: a new Electric Arc Furnace without its continuous caster burns fuel oil, and the card's words say
>      it; a new Logging Camp without its chainsaw cuts less, and the card's outlets list that cut.
> 
> Sections 7 and 12 draw on the JavaFX toolkit (ui.KeptBench, KeptCheck's way); where it cannot start, their card parts
> are said to be skipped and the rest runs.

**Uses:** [JobType](JobType.md) (44), [BuildCard](BuildCard.md) (38), [Good](Good.md) (36), [Game](Game.md) (32), [BuildingsTemplate](BuildingsTemplate.md) (24), [NewUnit](NewUnit.md) (21), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (19), [KeptCheck](KeptCheck.md) (12), [BuildAdvice](BuildAdvice.md) (11), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (11), [BuildingsStacks](BuildingsStacks.md) (10), [ResearchCatalog](ResearchCatalog.md) (10), [WageBand](WageBand.md) (9), [KeptBench](KeptBench.md) (8), [EquipmentFit](EquipmentFit.md) (7), [BuildingManager](BuildingManager.md) (7), [Recipes](Recipes.md) (4), [Founding](Founding.md) (4), [Sector](Sector.md) (3), [Bank](Bank.md) (3), [WorksValue](WorksValue.md) (3), [BuildingType](BuildingType.md) (3), [PopulationManager](PopulationManager.md) (2), [Recipe](Recipe.md) (2), [CityNeeds](CityNeeds.md) (2), [EducationType](EducationType.md) (2), [RecipeCheck](RecipeCheck.md) (2), [FittingCheck](FittingCheck.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2)... and 7 more

## Sections

| line | section |
|---:|---|
| 95 | · the actual state a new building runs |
| 209 | · 1 |
| 250 | · 2 |
| 291 | · 3 |
| 348 | · 4 |
| 374 | · 5 |
| 424 | · 6 |
| 446 | · 7 and 8 |
| 621 | · 9 |
| 671 | · 10 |
| 725 | · 11 |
| 784 | · 12 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `NewUnitCheck.out` | `System.out` |  |
| 79 | `NewUnitCheck.ROBOT` | `"welding-robot"` |  |
| 189 | `NewUnitCheck.NO` | `JobType.NO_DIPLOMA.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 75 | `static int fails, checks` |  |
| 76 | `static boolean quietRun` |  |
| 99 | `final int[] jobs` |  |
| 100 | `int total, electricity` |  |
| 101 | `double gen, production1, estimate, running` |  |
| 161 | `Game game` |  |
| 162 | `BuildingsTemplate works` |  |
| 163 | `BuildingsStacks stack` |  |
| 164 | `Ran actual` |  |
| 165 | `double rejectedAt` |  |
| 192 | `static boolean toolkitTried` | Whether the toolkit has been tried, and why it is down (null: up); it starts once in a JVM, so sections 7 and 12 share it. |
| 193 | `static String toolkitDown` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 788 | **type** `public final class NewUnitCheck` | A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every future-building figure. |
| 81 | 6 | `static void check(String label, boolean ok, String detail)` |  |
| 88 | 1 | `static void section(String title)` |  |
| 90 | 1 | `static boolean near(double a, double b)` |  |
| 93 | 1 | `static boolean apart(double a, double b)` | Whether two readings differ by more than rounding: the premise that a reader of the fleet would be told apart. |

### the actual state a new building runs (lines 95-208)

| line | len | member | says |
|---:|---:|---|---|
| 98 | 10 | **type** `static final class Ran` | The live figures of a template, and the investors' numbers, read in one state. |
| 103 | 4 | `public String toString()` _(in NewUnitCheck.Ran)_ |  |
| 109 | 15 | `static Ran ranOf(Game g, BuildingsTemplate t, boolean priced)` |  |
| 126 | 6 | `static Set<String> rejected(Game g, BuildingsTemplate t)` | The pieces a new building of t would not get now. |
| 137 | 12 | `static<T> T whereItRuns(Game g, BuildingsTemplate t, Supplier<T> read)` | Reads where a new building of t actually runs: the standing fleet's stack set without every piece a new one would not get, the recipes applied, `read` run there, then the fitting put back and applied again. |
| 150 | 8 | `static boolean sameFigures(NewUnit u, BuildingsTemplate t)` |  |
| 160 | 7 | **type** `static final class Cars` | A 1900 city with one fitted Vehicle Works and every discovery known at the robot's date. |
| 168 | 10 | `static Cars robotTown(String label) throws Exception` |  |
| 180 | 8 | `static boolean rejectRobot(Cars c)` | Steps machinery from a cent up by half again until the verdict for a new plant drops the robot; false if it never does. |
| 196 | 7 | `static String toolkit()` | The JavaFX toolkit, started at the first ask (KeptCheck.startJavaFx()): null when it is up, else why not. |
| 205 | 3 | `static double fleetQuote(Game g, BuildingsTemplate t, Sector owner)` | The fitted fleet's quote: what the sector says of the fleet's figures, as a reader of the template would have quoted. |

### 1 (lines 209-249)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 38 | `static void standingEverywhere() throws Exception` |  |

### 2 (lines 250-290)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 38 | `static void rejectedRobot(Cars c) throws Exception` |  |

### 3 (lines 291-347)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 54 | `static void staffingBoundary(Cars c) throws Exception` |  |

### 4 (lines 348-373)

| line | len | member | says |
|---:|---:|---|---|
| 350 | 23 | `static void pulpChoice() throws Exception` |  |

### 5 (lines 374-423)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 47 | `static void bankClerks() throws Exception` |  |

### 6 (lines 424-445)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 19 | `static void matching(Cars c) throws Exception` |  |

### 7 and 8 (lines 446-620)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 15 | `static List<Object> siteSnapshot(Game g, BuildingsTemplate w)` | An order on site as the Build card's reads must leave it: the stack, its sites and their contracts. |
| 465 | 125 | `static void identitiesAndSite() throws Exception` |  |
| 592 | 1 | `static String unskilled(long posts)` | The order bar's words for this many unskilled posts (BuildScreen.refreshOrderBar(), jobPlural()). |
| 599 | 6 | `static double latentWith(Game g, double coming)` | The landlords' head shortage with `coming` posts on site, from its parts (RealEstate.latentHeadShortfall()'s arithmetic, no home on site): every post standing, the builders' laid-off crews included, and the coming one... |
| 607 | 13 | `static double[] loadsAhead(Game g, BuildingsTemplate t, StringBuilder said, int[] done)` | The power ahead of a queue on a stack: the readings at half the first completion, just before it, at it and past the last. |

### 9 (lines 621-670)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 47 | `static void changedDraw() throws Exception` |  |

### 10 (lines 671-724)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 44 | `static void crewDemand() throws Exception` |  |
| 719 | 5 | `static double college(Ran r)` | A building's posts in the college band (COLLEGE_HEALTH, _BUSINESS, _ENGINEERING), the band a college teaches. |

### 11 (lines 725-783)

| line | len | member | says |
|---:|---:|---|---|
| 727 | 56 | `static void powerAhead() throws Exception` |  |

### 12 (lines 784-861)

| line | len | member | says |
|---:|---:|---|---|
| 786 | 52 | `static void cardGoods() throws Exception` |  |
| 839 | 22 | `public static void main(String[] args) throws Exception` |  |

