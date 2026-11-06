# Recipes.java - 249 lines · 17 methods · 0 constants · model

`ham/citybuildersim/Recipes.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [Recipe](Recipe.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (10), [Game](Game.md) (5), [Good](Good.md) (4), [Research](Research.md) (4), [JobType](JobType.md) (2), [Markets](Markets.md) (1), [EconomyManager](EconomyManager.md) (1), [SpreadPlanner](SpreadPlanner.md) (1), [BuildingManager](BuildingManager.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Sector](Sector.md) (1)

**Used by (5):** [DiscoveryCheck](DiscoveryCheck.md), [EquipmentFit](EquipmentFit.md), [Game](Game.md), [RecipeCheck](RecipeCheck.md), [ResearchCheck](ResearchCheck.md)

## Sections

| line | section |
|---:|---|
| 60 | THE PURE CORE: figures in, figures out |
| 140 | THE GAME LAYER: the city's prices, read on demand |

## Fields (state)

| line | field | says |
|---:|---|---|
| 156 | `private final Map<Integer, String[]> chosen` | The recipes each template's two parts run, by template id: [the fitted part's, the unfitted part's]. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 204 | **type** `public final class Recipes` | Chooses the recipe each part of a stack runs, at the city's own prices, and sets the template's figures to what its buildings run: the driver of R1c's recipes, as EquipmentForms is of R1's forms. |
| 52 | 7 | **type** `public record Prices(ToDoubleFunction<Good> good, double[] wages, double pricePerWatt, double pricePerWater...` | The prices a recipe is valued at, as the planners know them: a good's price a unit (the city's, SpreadPlanner.cityValue(); or a harness's own), the wage of each band by JobType ordinal, the three units' prices and the... |
| 55 | 3 | `public Prices withGoods(ToDoubleFunction<Good> other)` _(in Recipes.Prices)_ | The same prices with another price for the goods. |

### THE PURE CORE: figures in, figures out (lines 60-139)

| line | len | member | says |
|---:|---:|---|---|
| 67 | 13 | `public static double value(Recipe r, Prices p)` | What a month at nameplate is worth to one building running the recipe, at these prices: what it makes at their prices, less what it uses, less its posts at the wage of each band (as BusinessInvestment.runningCostOf() ... |
| 86 | 3 | `public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int month)` | Whether a part of a stack can run a recipe at a game month: it is known at the month (a year from its January, a code from its entry's month in research.json), and, on a template with a recipe piece, the fitted part r... |
| 91 | 5 | `public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int month, Research found)` | The same in a city whose research may have found the recipe's code before history's month (R3: Recipe.knownAt(int, Research)); no research (null) reads history's. |
| 101 | 3 | `public static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p)` | The best recipe a part can run at these prices: the highest value() of those it canRun(); a tie keeps the default, then the file's order; the default where none can run. |
| 106 | 18 | `public static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found)` | The same in a city whose research may have found a recipe's code before history's month (R3); no research (null) reads history's. |
| 130 | 3 | `public static double recipeGain(BuildingsTemplate t, int month, Prices p)` | What the template's recipe piece adds a month to one building at these prices: the value of the best recipe a fitted building could run less that of the best an unfitted one could (EquipmentFit's gain for a piece that... |
| 135 | 4 | `public static double recipeGain(BuildingsTemplate t, int month, Prices p, Research found)` | The same in a city whose research may have found a recipe's code before history's month (R3); no research (null) reads history's. |

### THE GAME LAYER: the city's prices, read on demand (lines 140-249)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 6 | `public static Prices pricesOf(Game game)` | The city's prices: what the planners weigh each good at (SpreadPlanner.cityValue() of its market), the wages, the power and water units' prices and the exchange factor (Markets.getExchangeRate(), what ProcessingSector... |
| 151 | 3 | `public static double gainOf(Game game, BuildingsTemplate t)` | The gain of the template's recipe piece at the city's prices and in the city's month (EquipmentFit.gainOf() for a piece that ends no form). |
| 163 | 13 | `public void choose(Game game)` | Chooses, for every template with a choice of recipes, the best each part can run at the city's prices in the city's month, and keeps it (a template whose parts both run the default keeps nothing). |
| 182 | 16 | `public void apply(Game game)` | Sets every template with a choice of recipes to what its buildings run: the recipes chosen (the default where none was, or where the template no longer has the one saved), weighted by the buildings of each part (the s... |
| 207 | 6 | `private static void declareGoods(Game game, BuildingsTemplate t)` | The sector of a template with a choice of recipes declares the goods of EVERY recipe of it that its constructor did not (Sector.declareRecipeGoods()), as Agriculture.setHorseShare() declares CROPS once a share is set:... |
| 215 | 3 | `public void clear()` | Forgets the choices: Game.buildWorld() calls it, so that a Game that played a 1900 city starts the next city as a fresh process does. |
| 223 | 4 | `public String[] chosen(int templateId)` | The recipes a template's two parts run now (R5: what ResearchView.recipesInUse() counts): a copy of [the fitted part's recipe id, the unfitted part's], or null when both run the default (the template has no choice kept). |
| 229 | 5 | `public Map<Integer, List<String>> forSave()` | The choices to save, by template id: [the fitted part's recipe id, the unfitted part's]; a fresh map, empty when every part runs the default (the save then has no key). |
| 240 | 9 | `public void restore(Map<Integer, List<String>> byId)` | Puts the saved choices back, replacing all (Game's load path, then apply()): a load ends on the SAVED choices and does not strike them again from the loaded prices. |

