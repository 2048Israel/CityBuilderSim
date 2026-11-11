# RecipeCheck.java - 1,168 lines · 41 methods · 3 constants · harnesses

`ham/citybuildersim/RecipeCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> Recipes (R1c): the ways a building runs, as data on its template, chosen at the city's prices in a city founded the 1900 way, set on the template's
> figures weighted by the buildings of each part of a stack, and saved.
> 
> WHAT THIS HAS TO PROVE (R1c, 2026-11-05; docs/queue/R1c-notes.md; research-design 2.1 and 9). R1 and R1b made a piece of equipment the count of a
> stack's buildings still without it. This part lets a building know several ways to run, and a city founded the 1900 way run the best each part
> of a stack can, at its own prices. What is checkable:
> 
>   1. THE CATALOG READS RECIPES: a figure a recipe does not state is the template's top level, "uses": {} states uses nothing, the one entry that
>      states no figure is the default, a template with no list has one default recipe whose figures are its top level, a recipe's consumables are
>      priced at the founding's level as the top level's are, and each bad entry (two defaults or none, an unknown good or job type, a piece the
>      building does not carry, a piece that ends no form and that no recipe needs, two recipe pieces) is reported and skipped or its list dropped
>   2. A CITY FOUNDED THE DEFAULT WAY CHOOSES NOTHING: after months pressed, and with the driver asked outright, every template's figures are its
>      default recipe's, nothing is chosen and nothing is saved - including a template given recipes that would earn more
>   3. A CITY FOUNDED THE 1900 WAY RUNS THE BEST RECIPE AT ITS PRICES: the value of a month is the model's own sum, equal to the planners' running
>      cost with the power a works makes and its consumables; two recipes differing in one input flip with the price of it either side of the
>      break-even; a tie keeps the default and then the file's order; and in the city, the template's figures after the choice are the chosen
>      recipe's, from the driver called outright and from the month
>   4. KNOWN AND RUNNABLE: a recipe whose since is after the city's month, or a code not in the catalogue, is never chosen; on a template with a recipe piece the unfitted
>      part never runs a recipe that needs it, the fitted part never runs one that needs nothing (even when that earns more), and a part with
>      none it can run runs the default
>   5. THE MIX: of five buildings, two unfitted, the template's makes and uses are the weighted mean of the two recipes, its posts and its draw
>      rounded; with no unfitted building the fitted recipe's figures alone and with all of them the unfitted part's; with no building at all the
>      fitted part's; both parts on the default is exactly the top level, to the bit, and sets back what a mix had changed
>   6. THE FIT'S GAIN FOR A RECIPE PIECE: for a piece that ends no form it is the value of the best recipe a fitted building could run less that of
>      an unfitted one, and its verdict goes from NOT_YET to PAYS as a price crosses the break-even; a piece that ends a form keeps its form's
>      gain, a piece nothing needs adds nothing
>   7. SAVE AND LOAD: the choices are written only where a part runs other than the default and come back on a load, which ends on them and does
>      not strike them again from the loaded prices; the next month strikes them afresh, and an unbroken town and its reloaded twin run the same
>   8. A GAME THAT PLAYED A 1900 TOWN, then founded the default way, keeps no choice and no template off its top level
>   9. THE SHIPPED ROWS (R1c's data, buildings.json): in a town founded the 1900 way every template the file gives recipes has the choice read, its
>      unfitted part runs a recipe that needs nothing and is known in 1900 and its fitted part one that needs the piece (the farms' tractor is
>      PE14's, June 1917, so theirs runs the default in 1900), each the best its part can run at the city's prices by the model's own value(), and its figures are
>      the mix; the Oxygen Steel Shop's unfitted part runs the Bessemer converter and its fitted part the open hearth; the Pulp Mill, with no
>      piece, cannot run kraft before kraft's year and can from it, and runs the best it can; the farms buy no fuel on horses and the template's
>      fuel times the farms standing in a default town; each recipe piece is priced (PAYS or NOT_YET, its gain Recipes.gainOf()), never
>      NOT_KNOWN; and a reload of the town ends on the same choices and the same figures
> 
> Every fixture CAUSES its condition: the prices are moved across a break-even the harness solves from the model's own value function (affine in a
> price, so two evaluations give it), the recipes of 1 to 8 are given to the town's own templates (a city's templates are its own, and the Bakery they
> use has no shipped row), the counts are set on the stacks, and each figure is asserted against the recipes' own and the model's functions. The towns
> of 1 to 8 are 1900 villages that meet the shipped rows too, so each of their fixtures asks about its own template (choiceOf()), and 9 reads the rows.

**Uses:** [Recipes](Recipes.md) (115), [Good](Good.md) (106), [Recipe](Recipe.md) (76), [BuildingsTemplate](BuildingsTemplate.md) (55), [EquipmentFit](EquipmentFit.md) (33), [Game](Game.md) (32), [JobType](JobType.md) (25), [EquipmentCheck](EquipmentCheck.md) (20), [Equipment](Equipment.md) (12), [BuildingsStacks](BuildingsStacks.md) (9), [FittingCheck](FittingCheck.md) (7), [Sector](Sector.md) (7), [GameFiles](GameFiles.md) (7), [PolicyDates](PolicyDates.md) (5), [Founding](Founding.md) (4), [CityCalendar](CityCalendar.md) (4), [PriceLevel](PriceLevel.md) (3), [BuildingType](BuildingType.md) (3), [Forms1900Check](Forms1900Check.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [ProcessingSector](ProcessingSector.md) (1), [ExtractionSector](ExtractionSector.md) (1)

## Sections

| line | section |
|---:|---|
| 143 | · helpers |
| 270 | 1. the catalog reads recipes |
| 419 | 2. a city founded the default way chooses nothing |
| 460 | 3. a city founded the 1900 way runs the best recipe at its prices |
| 570 | 4. known and runnable |
| 661 | 5. the mix |
| 754 | 6. the fit's gain for a recipe piece |
| 825 | 7. save and load |
| 906 | 8. a Game that played a 1900 town, then founded the default way |
| 938 | 9. the shipped rows |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `RecipeCheck.DRIVE` | `EquipmentCheck.DRIVE` |  |
| 272 | `RecipeCheck.WORKS` | `""" {"buildings": [ {"id": 1, "name": "Test Works", "category": "INDUSTRIAL",...` |  |
| 286 | `RecipeCheck.BAD` | `""" {"buildings": [ {"id": 1, "name": "Bad Works", "category": "INDUSTRIAL", ...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `static int fails` |  |
| 67 | `static PrintStream out` |  |
| 68 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 1105 | **type** `public class RecipeCheck` | Recipes (R1c): the ways a building runs, as data on its template, chosen at the city's prices in a city founded the 1900 way, set on the template's figures weighted by the buildings of each part of a stack, and saved. |
| 72 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 77 | 1 | `static void check(String label, boolean ok)` |  |
| 80 | 4 | `static void near(String label, double actual, double expected, double rel)` | A figure against the harness's own, to a relative tolerance (and an absolute floor for a figure that is nothing). |
| 85 | 5 | `static void quietly(Runnable r)` |  |
| 92 | 1 | `static void step(Game g, int months)` | One press of the clock, as the player's button makes it, quietly. |
| 94 | 1 | `static BuildingsStacks stack(Game g, String template)` |  |
| 96 | 1 | `static BuildingsTemplate template(Game g, String name)` |  |
| 103 | 1 | `static List<String> choiceOf(Game g, BuildingsTemplate t)` | What the city chose for one template: [the fitted part's recipe id, the unfitted part's], or null when it chose nothing for it (both parts run the default). |
| 106 | 9 | `static List<String> savedChoice(String json, BuildingsTemplate t)` | What a save file holds for one template's recipe choice, or null when it holds none. |
| 116 | 1 | `static double cityPrice(Game g, Good good)` |  |
| 118 | 24 | `public static void main(String[] args) throws Exception` |  |

### helpers (lines 143-269)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 12 | `static String differs(BuildingsTemplate t, Recipe r)` | Why a template's live figures are not a recipe's (every figure, to the bit), or null when they are. |
| 159 | 1 | `static boolean runs(BuildingsTemplate t, Recipe r)` |  |
| 162 | 3 | `static String differs(BuildingsTemplate a, BuildingsTemplate b)` | Two templates' live figures alike, to the bit: the first difference, or null. |
| 170 | 10 | `static Recipe like(Recipe base, String id, String since, String needs, double makesTimes, double usesTimes, int noDiploma, int ...` | A recipe like `base`, not the default, with the changes asked for: makes and uses times a factor, the posts of NO_DIPLOMA, the draw and what it makes itself (a negative figure leaves it as the base's). |
| 182 | 11 | `static Recipe plus(Recipe base, Good made, double madeUnits, Good used, double usedUnits)` | `base` with one good more made and one more used: goods a bakery's sector does not declare in its constructor (same id, since and piece as the base's). |
| 195 | 5 | `static Map<String, String> declared(Game g)` | What every sector declares, by key: the goods it makes and the goods it uses, in the order declared. |
| 202 | 5 | `static Recipes.Prices frozen(Recipes.Prices p)` | The prices of a moment held still: a Prices whose goods are what they were when this was called (the city's read live, a market moved later would move them). |
| 209 | 5 | `static double outputValue(Recipe r, Recipes.Prices p)` | What the recipe's output is worth at these prices: its makes alone (the harness sizes a fixture with it). |
| 216 | 4 | `static double zeroOf(DoubleUnaryOperator affine)` | The x at which an affine function of it is nothing, from its values at 0 and at 1. |
| 222 | 4 | `static double lead(Recipe a, Recipe b, Recipes.Prices p, Good good, double price)` | What a recipe is worth over another at these prices with one good's price put at `price`: the model's own value() twice. |
| 228 | 6 | `static Game town(String label, boolean unfitted)` | A town on the goods' literals with its sectors held, founded the 1900 way (every piece unfitted) or the default way. |
| 235 | 5 | `static GameFiles.Result save10(Game g, String name)` |  |
| 241 | 8 | `static Game load10(Game from)` |  |
| 250 | 1 | `static int year(Game g)` |  |
| 253 | 1 | `static int jan(int year)` | The game month of a calendar year's January: the month Recipes and EquipmentFit ask by since R2 (a year since is known from it). |
| 256 | 13 | `static List<BuildingsTemplate> parse(String json, String[] said)` | A catalogue reading, with what it reported. |

### 1. the catalog reads recipes (lines 270-418)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 91 | `static void sectionOne()` |  |
| 410 | 8 | `static boolean throwsOnSet(BuildingsTemplate t, List<Recipe> list)` |  |

### 2. a city founded the default way chooses nothing (lines 419-459)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 38 | `static void sectionTwo() throws Exception` |  |

### 3. a city founded the 1900 way runs the best recipe at its prices (lines 460-569)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 107 | `static void sectionThree()` |  |

### 4. known and runnable (lines 570-660)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 88 | `static void sectionFour()` |  |

### 5. the mix (lines 661-753)

| line | len | member | says |
|---:|---:|---|---|
| 664 | 18 | `static String notMix(BuildingsTemplate t, Recipe a, int na, Recipe b, int nb)` | Whether a template's live figures are the mean of two recipes weighted na and nb, the posts and the draw rounded; the first thing that is not, or null. |
| 683 | 70 | `static void sectionFive()` |  |

### 6. the fit's gain for a recipe piece (lines 754-824)

| line | len | member | says |
|---:|---:|---|---|
| 756 | 68 | `static void sectionSix()` |  |

### 7. save and load (lines 825-905)

| line | len | member | says |
|---:|---:|---|---|
| 827 | 78 | `static void sectionSeven() throws Exception` |  |

### 8. a Game that played a 1900 town, then founded the default way (lines 906-937)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 29 | `static void sectionEight()` |  |

### 9. the shipped rows (lines 938-1168)

| line | len | member | says |
|---:|---:|---|---|
| 941 | 10 | `static void stand(Game g, BuildingsTemplate t, int n, int[] direct)` | Stands at least n of the template in the town: bought without construction where the ground has room for it, else put on the books; each order that took the second way adds one to direct[0]. |
| 953 | 4 | `static double score(Recipe r, Recipes.Prices p)` | A recipe's value for a ranking: not a number is the worst there is, as Recipes.best() reads it. |
| 962 | 10 | `static String notBest(BuildingsTemplate t, Recipe run, boolean fitted, int month, Recipes.Prices p)` | Why a part does not run the best recipe it can, by the model's own value(), or null when it does: it cannot run it (the default is run where the part can run none), or another it can run is worth more at these prices. |
| 973 | 195 | `static void sectionNine() throws Exception` |  |

