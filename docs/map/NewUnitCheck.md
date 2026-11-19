# NewUnitCheck.java - 610 lines · 22 methods · 3 constants · harnesses

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
> No oracle here calls NewUnit or anything QN1 added. Each expected value is taken from the ACTUAL state a new building runs:
> the fleet's stack set unfitted for the pieces a new one would not get, the recipes applied, and the template's live figures
> (or the investors' estimate) read there, then the fitting restored; or it is composed from the recipe data. The fleet's
> own figures appear only as each section's PREMISE: that the expected value differs from what a reader of the fleet
> would give, so a reader that kept reading the fleet fails.
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
>      fitting input, and an order on site is not touched by reading them.
>   8. What is on site counts the actual new plant's posts, and the power ahead adds its draw only from its completion (a
>      guard on the real stack: the recipe data moves no draw).
>   9. Power ahead: a queued Coal Power Plant whose new unit rejects the turbine and the pulverised coal generates less,
>      and the power measure reads that generation.

**Uses:** [JobType](JobType.md) (41), [BuildCard](BuildCard.md) (31), [Game](Game.md) (24), [Good](Good.md) (20), [BuildingsTemplate](BuildingsTemplate.md) (19), [NewUnit](NewUnit.md) (18), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (10), [BuildAdvice](BuildAdvice.md) (10), [EquipmentFit](EquipmentFit.md) (7), [BuildingsStacks](BuildingsStacks.md) (7), [HistoricalRecipeCheck](HistoricalRecipeCheck.md) (7), [ResearchCatalog](ResearchCatalog.md) (5), [WageBand](WageBand.md) (5), [Recipes](Recipes.md) (4), [Sector](Sector.md) (3), [Bank](Bank.md) (3), [Recipe](Recipe.md) (2), [BuildingManager](BuildingManager.md) (2), [WorksValue](WorksValue.md) (2), [Founding](Founding.md) (2), [BuildingType](BuildingType.md) (2), [RecipeCheck](RecipeCheck.md) (2), [FittingCheck](FittingCheck.md) (2), [EquipmentCheck](EquipmentCheck.md) (2), [Forms1900Check](Forms1900Check.md) (2), [Equipment](Equipment.md) (1), [RecipeChange](RecipeChange.md) (1), [PopulationManager](PopulationManager.md) (1), [PolicyDates](PolicyDates.md) (1), [RecipeChanges](RecipeChanges.md) (1)... and 1 more

## Sections

| line | section |
|---:|---|
| 82 | · the actual state a new building runs |
| 181 | · 1 |
| 222 | · 2 |
| 263 | · 3 |
| 320 | · 4 |
| 346 | · 5 |
| 396 | · 6 |
| 418 | · 7 and 8 |
| 532 | · 9 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `NewUnitCheck.out` | `System.out` |  |
| 66 | `NewUnitCheck.ROBOT` | `"welding-robot"` |  |
| 174 | `NewUnitCheck.NO` | `JobType.NO_DIPLOMA.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails, checks` |  |
| 63 | `static boolean quietRun` |  |
| 86 | `final int[] jobs` |  |
| 87 | `int total, electricity` |  |
| 88 | `double gen, production1, estimate, running` |  |
| 146 | `Game game` |  |
| 147 | `BuildingsTemplate works` |  |
| 148 | `BuildingsStacks stack` |  |
| 149 | `Ran actual` |  |
| 150 | `double rejectedAt` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 61 | 550 | **type** `public final class NewUnitCheck` | A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every future-building figure. |
| 68 | 6 | `static void check(String label, boolean ok, String detail)` |  |
| 75 | 1 | `static void section(String title)` |  |
| 77 | 1 | `static boolean near(double a, double b)` |  |
| 80 | 1 | `static boolean apart(double a, double b)` | Whether two readings differ by more than rounding: the premise that a reader of the fleet would be told apart. |

### the actual state a new building runs (lines 82-180)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 10 | **type** `static final class Ran` | The live figures of a template, and the investors' numbers, read in one state. |
| 90 | 4 | `public String toString()` _(in NewUnitCheck.Ran)_ |  |
| 96 | 13 | `static Ran ranOf(Game g, BuildingsTemplate t, boolean priced)` |  |
| 111 | 6 | `static Set<String> rejected(Game g, BuildingsTemplate t)` | The pieces a new building of t would not get now. |
| 122 | 12 | `static<T> T whereItRuns(Game g, BuildingsTemplate t, Supplier<T> read)` | Reads where a new building of t actually runs: the standing fleet's stack set without every piece a new one would not get, the recipes applied, `read` run there, then the fitting put back and applied again. |
| 135 | 8 | `static boolean sameFigures(NewUnit u, BuildingsTemplate t)` |  |
| 145 | 7 | **type** `static final class Cars` | A 1900 city with one fitted Vehicle Works and every discovery known at the robot's date. |
| 153 | 10 | `static Cars robotTown(String label) throws Exception` |  |
| 165 | 8 | `static boolean rejectRobot(Cars c)` | Steps machinery from a cent up by half again until the verdict for a new plant drops the robot; false if it never does. |
| 177 | 3 | `static double fleetQuote(Game g, BuildingsTemplate t, Sector owner)` | The fitted fleet's quote: what the sector says of the fleet's figures, as a reader of the template would have quoted. |

### 1 (lines 181-221)

| line | len | member | says |
|---:|---:|---|---|
| 183 | 38 | `static void standingEverywhere() throws Exception` |  |

### 2 (lines 222-262)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 38 | `static void rejectedRobot(Cars c) throws Exception` |  |

### 3 (lines 263-319)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 54 | `static void staffingBoundary(Cars c) throws Exception` |  |

### 4 (lines 320-345)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 23 | `static void pulpChoice() throws Exception` |  |

### 5 (lines 346-395)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 47 | `static void bankClerks() throws Exception` |  |

### 6 (lines 396-417)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 19 | `static void matching(Cars c) throws Exception` |  |

### 7 and 8 (lines 418-531)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 15 | `static List<Object> siteSnapshot(Game g, BuildingsTemplate w)` | An order on site as the Build card's reads must leave it: the stack, its sites and their contracts. |
| 437 | 94 | `static void identitiesAndSite() throws Exception` |  |

### 9 (lines 532-610)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 56 | `static void powerAhead() throws Exception` |  |
| 591 | 19 | `public static void main(String[] args) throws Exception` |  |

