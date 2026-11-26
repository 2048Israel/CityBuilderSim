# Recipes.java - 494 lines · 33 methods · 1 constants · model

`ham/citybuildersim/Recipes.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> Chooses the recipe each part of a stack runs, at the city's own prices, and sets the template's figures to what its buildings run: the driver of
> R1c's recipes, as EquipmentForms is of R1's forms.
> 
> WHY (R1c, 2026-11-05; docs/studies/research-design.md 2.1; Jerus: "Recipes are free to upgrade immediately."). A building could run
> one way only, and the second way of making the same good (a farm on a tractor, coke in by-product ovens) was a second building with its own id. A
> template now carries recipes (Recipe), and "each month it runs the best one it knows at the city's own prices (SpreadPlanner.cityValue()) ...
> Switching costs nothing and takes effect that month." What a building may run depends on its equipment: the template's RECIPE PIECE, the one piece
> its recipes need, is on the fitted buildings of a stack (the quantity less BuildingsStacks.getUnfitted()) and not on the rest, so a stack is two
> parts, and each part runs the best recipe it can: the FITTED part only the recipes that need the piece (the piece replaces the old way once it
> is in: by-product ovens replace the beehive ovens, a farm with its tractor has sold its horses), the UNFITTED part only the recipes that need
> nothing; a template with no recipe piece is all one part and runs the best of all it knows. A part with no recipe it can run (none known at
> the city's month: a year from its January, a code from its entry's month in research.json, R2) runs the default.
> 
> WHAT RUNS IS THE TEMPLATE'S FIGURES (R1c-notes call 3). Every reader of a building's figures (well over a hundred) reads the template, and a city's templates
> are its own (Game.buildWorld() makes a BuildingManager for every city), so apply() sets each template's live makes, uses, jobs, electricity, gen,
> water and consumables to the mix of its two parts' recipes weighted by their buildings (BuildingsTemplate.runRecipes()), and no reader changes.
> A recipe may make or use a good its sector's constructor does not declare: in a city founded the 1900 way apply() has the sector declare the
> goods of every recipe of its templates (Sector.declareRecipeGoods(), as R0c's Agriculture.setHorseShare() declares CROPS once a share is set;
> all of them, so a reloaded twin declares what the city played straight did), and the sectors of every other city are as they always were.
> 
> ONLY A CITY FOUNDED THE 1900 WAY CHOOSES (Game.foundedUnfitted(); research-design 9, "a building with everything known runs exactly today's
> figures"). In a city founded the default way nothing here reads a price, sets a figure or is saved: every template runs its top level, and every
> city a player has played keeps its trace.
> 
> TWO LAYERS, as EquipmentFit is: the pure core (value(), canRun(), best(), recipeGain()) takes its prices as arguments, so a harness can move one
> across the break-even, and the game layer (pricesOf(), choose(), apply()) reads the city's. Called where the Policy hears the month
> (Game.policyHearsTheMonth(): the founding, the top of every month, a load) choose() then apply(), and apply() once more right after the sites
> advance (SimulationEngine.simulateMonth()) and in the load path, each BEFORE EquipmentForms.apply(), because the steam form weighs each
> building by its electricity.
> 
> SAVED, and a load ends on the SAVED choices (R1c-notes call 5): the choices are the part of the state a month's prices struck and no later month can
> reconstruct (a flow is not recoverable from the state it ended in), so DataSave carries them (forSave(), restore()), only for a template where a
> part runs a recipe other than the default. A loaded city is not choosing again from the loaded prices until the next month's top does.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (18), [Recipe](Recipe.md) (17), [Game](Game.md) (10), [JobType](JobType.md) (6), [Research](Research.md) (6), [WageBand](WageBand.md) (6), [Good](Good.md) (5), [RecipeChanges](RecipeChanges.md) (4), [ArrivalMix](ArrivalMix.md) (4), [LicenceFees](LicenceFees.md) (3), [WageLadder](WageLadder.md) (3), [PayTier](PayTier.md) (3), [Markets](Markets.md) (1), [EconomyManager](EconomyManager.md) (1), [SpreadPlanner](SpreadPlanner.md) (1), [BuildingManager](BuildingManager.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Sector](Sector.md) (1), [LabourMarket](LabourMarket.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1)

**Used by (19):** [DiscoveryCheck](DiscoveryCheck.md), [EquipmentFit](EquipmentFit.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [FittingCheck](FittingCheck.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [MaterialsCheck](MaterialsCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [NewUnitCheck](NewUnitCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [ResearchCheck](ResearchCheck.md), [Village1900Check](Village1900Check.md), [WaterPowerCheck](WaterPowerCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | THE PURE CORE: figures in, figures out |
| 169 | THE GAME LAYER: the city's prices, read on demand |
| 269 | THE 1900 HOME (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 308 | `Recipes.PAY_MONTHS` | `12` | How many months of the households' pay the walk's mean takes: a year, so a month's jump in the pay moves the home by a twelfth of it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 205 | `private final Map<Integer, String[]> chosen` | The recipes each template's two parts run, by template id: [the fitted part's, the unfitted part's]. |
| 299 | `public static boolean HOMES_1900` | R1g's switch for the 1900 home, on unless -Dr1g.home=off: the controls' lever while the fix and its pace are Jerus's call (the notes, 2), and the one Village1900Check reads to show the home's effect, failing with it off. |
| 305 | `public static boolean HOME_WALK` | R1g's switch for the walk (THE HOME'S COST MOVES WITH WAGES, above), on unless -Dr1g.walk=off: each month's apply() sets the homes to the mean of the last PAY_MONTHS months' homeScaleOfPay() rather than holding them a... |
| 311 | `private final java.util.ArrayDeque<Double> payIndices` | The walk's pay index of each of the last PAY_MONTHS months, oldest first, and the game month of the newest (saved; DataSave). |
| 312 | `private int payIndexMonth` |  |
| 321 | `private final java.util.Map<BuildingsTemplate, Dated> datedHomes` | The homes this has dated, by identity, a city's templates being its own (weak, so a finished city's templates go with it). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 449 | **type** `public final class Recipes` | Chooses the recipe each part of a stack runs, at the city's own prices, and sets the template's figures to what its buildings run: the driver of R1c's recipes, as EquipmentForms is of R1's forms. |
| 52 | 13 | **type** `public record Prices(ToDoubleFunction<Good> good, double[] wages, double pricePerWatt, double pricePerWater...` | The prices a recipe is valued at, as the planners know them: a good's price a unit (the city's, SpreadPlanner.cityValue(); or a harness's own), the wage of each band by JobType ordinal, the three units' prices and the... |
| 56 | 3 | `public Prices(ToDoubleFunction<Good> good, double[] wages, double pricePerWatt, double pricePerWaterUnit, double exchange)` _(in Recipes.Prices)_ | Prices whose works' surplus is credited at the draw's own price: a harness's flat prices, as every recipe was weighed before PW1-G. |
| 61 | 3 | `public Prices withGoods(ToDoubleFunction<Good> other)` _(in Recipes.Prices)_ | The same prices with another price for the goods. |

### THE PURE CORE: figures in, figures out (lines 66-168)

| line | len | member | says |
|---:|---:|---|---|
| 74 | 4 | `public static double powerCost(Recipe r, Prices p)` | What one building running the recipe pays for its power a month at these prices (PW1-G): its draw past what it makes at its class's price, the power it does not buy netted at the price it would pay, and what it makes ... |
| 84 | 13 | `public static double value(Recipe r, Prices p)` | What a month at nameplate is worth to one building running the recipe, at these prices: what it makes at their prices, less what it uses, less its posts at the wage of each band (as BusinessInvestment.runningCostOf() ... |
| 103 | 3 | `public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int month)` | Whether a part of a stack can run a recipe at a game month: it is known at the month (a year from its January, a code from its entry's month in research.json), and, on a template with a recipe piece, the fitted part r... |
| 108 | 7 | `public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int month, Research found)` | The same in a city whose research may have found the recipe's code before history's month (R3: Recipe.knownAt(int, Research)); no research (null) reads history's. |
| 120 | 3 | `public static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p)` | The best recipe a part can run at these prices: the highest value() less patent rent of those it canRun(); a tie keeps the default, then the file's order; the default where none can run. |
| 125 | 3 | `public static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found)` | The same in a city whose research may have found a recipe's code before history's month (R3); no research (null) reads history's. |
| 130 | 3 | `static Recipe bestGross(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found)` | The same choice before patent rent, for a default-founded city's fitting estimates (R2c). |
| 134 | 18 | `private static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found, boolean netFees)` |  |
| 158 | 3 | `public static double recipeGain(BuildingsTemplate t, int month, Prices p)` | What the template's recipe piece adds a month to one building at these prices: the value of the best recipe a fitted building could run less that of the best an unfitted one could (EquipmentFit's gain for a piece that... |
| 163 | 5 | `public static double recipeGain(BuildingsTemplate t, int month, Prices p, Research found)` | The same in a city whose research may have found a recipe's code before history's month (R3); no research (null) reads history's. |

### THE GAME LAYER: the city's prices, read on demand (lines 169-268)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 6 | `public static Prices pricesOf(Game game)` | The city's prices: what the planners weigh each good at (SpreadPlanner.cityValue() of its market), the wages, the power and water units' prices and the exchange factor (Markets.getExchangeRate(), what ProcessingSector... |
| 183 | 3 | `public static Prices pricesOf(Game game, BuildingsTemplate t)` | ...as the template's owner pays for its power (PW1, REV-PW1c): Heavy Industry and Mining the energy and transmission, every other class both wires (EconomyManager.getPowerPriceFor(), the price the month's bills strike). |
| 188 | 4 | `static Prices forTemplate(Prices p, Game game, BuildingsTemplate t)` | The same prices with the power at the template's class (pricesOf(Game, BuildingsTemplate)); the same object where the two agree. |
| 194 | 9 | `public static double gainOf(Game game, BuildingsTemplate t)` | The gain of the template's recipe piece at the city's prices and in the city's month (EquipmentFit.gainOf() for a piece that ends no form). |
| 212 | 16 | `public void choose(Game game)` | Chooses, for every template with a choice of recipes, the best each part can run at the city's prices in the city's month, and keeps it (a template whose parts both run the default keeps nothing). |
| 234 | 18 | `public void apply(Game game)` | Sets every template with a choice of recipes to what its buildings run: the recipes chosen (the default where none was, or where the template no longer has the one saved), weighted by the buildings of each part (the s... |
| 261 | 7 | `private static void declareGoods(Game game, BuildingsTemplate t)` | The sector of a template with a choice of recipes declares the goods of EVERY recipe of it that its constructor did not (Sector.declareRecipeGoods()), as Agriculture.setHorseShare() declares CROPS once a share is set:... |

### THE 1900 HOME (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix) (lines 269-494)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 1 | **type** `private record Dated(double scale, int materials, int points, double cash, double upkeep)` | A dated home's scale as it stands, and its materials, points, cash cost and upkeep as the catalogue stated them, the money in founding money (the walk sets them again from these, never from the last month's, so a load... |
| 328 | 19 | `public static double homeScale()` | What a 1900 home costs of today's: the 1901 household's monthly wage over the same household's on today's ladder (WageLadder.TODAY), the household being 1901's working arrivals by band (ArrivalMix's first knot) and ea... |
| 354 | 9 | `public static double homeScaleOfPay(Game game)` | What a 1900 home costs of today's at the households' pay (the walk's index, Jerus's (3)): what a filled job paid its household last month on average (HouseholdAccounts.getAverageWage()), in founding money (over the co... |
| 365 | 16 | `static double todaysHousehold()` | homeScale()'s household on today's ladder: 1901's working arrivals by band, each band paid the mean of its job types' rungs today. |
| 386 | 16 | `private void dateHomes(Game game)` | Builds each of the city's homes to 1900's standard (THE 1900 HOME, above): at homeScale() once with the walk off, or with it at payMean(), set again whenever that differs from the scale the home stands at. |
| 407 | 10 | `private double payMean(Game game)` | The walk's scale: the month's homeScaleOfPay() taken once a game month (the founding's second apply() and a load's, which reads the month the save was taken in, take nothing more), the mean of the last PAY_MONTHS taken. |
| 419 | 7 | `public double[] payIndicesForSave()` | The walk's months of the pay index, oldest first, for DataSave: null with none (a city founded the default way, or the walk off). |
| 428 | 1 | `public int payIndexMonth()` | The game month of the newest of payIndicesForSave(), for DataSave. |
| 434 | 8 | `public void restorePayIndices(double[] months, int month)` | Puts back the walk's saved months, before the load path's apply(): none (a save from before R1g's walk, or a city founded the default way) leaves the walk to start afresh at the loaded month. |
| 444 | 3 | `public boolean datedHome(BuildingsTemplate t)` | Whether this city's homes are built to 1900's standard: for the harnesses. |
| 449 | 4 | `public double datedScale(BuildingsTemplate t)` | The scale a dated home stands at (homeScale(), or the walk's mean); 1 for a home this has not dated: for the harnesses. |
| 459 | 4 | `public void clear()` | Forgets the choices and the walk's months: Game.buildWorld() calls it, so that a Game that played a 1900 city starts the next city as a fresh process does. |
| 468 | 4 | `public String[] chosen(int templateId)` | The recipes a template's two parts run now (R5: what ResearchView.recipesInUse() counts): a copy of [the fitted part's recipe id, the unfitted part's], or null when both run the default (the template has no choice kept). |
| 474 | 5 | `public Map<Integer, List<String>> forSave()` | The choices to save, by template id: [the fitted part's recipe id, the unfitted part's]; a fresh map, empty when every part runs the default (the save then has no key). |
| 485 | 9 | `public void restore(Map<Integer, List<String>> byId)` | Puts the saved choices back, replacing all (Game's load path, then apply()): a load ends on the SAVED choices and does not strike them again from the loaded prices. |

