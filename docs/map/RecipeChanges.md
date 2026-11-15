# RecipeChanges.java - 443 lines · 32 methods · 1 constants · model

`ham/citybuildersim/RecipeChanges.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> Composes independent discovery changes on the selected process recipe.
> 
> WHY (R2d): a second whole recipe erased the first piece's effect. Each undo
> is summed from an immutable anchor, in code order, and rounded only once.
> Recipe decisions are saved in the existing recipe ids after "~"; fitting
> counts are read on application, including after a construction site opens.

**Uses:** [Recipes](Recipes.md) (32), [BuildingsTemplate](BuildingsTemplate.md) (29), [Recipe](Recipe.md) (25), [RecipeChange](RecipeChange.md) (21), [Game](Game.md) (18), [Good](Good.md) (12), [JobType](JobType.md) (8), [BuildingsStacks](BuildingsStacks.md) (8), [Equipment](Equipment.md) (5), [LicenceFees](LicenceFees.md) (4), [Freight](Freight.md) (3), [Sector](Sector.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [SpreadPlanner](SpreadPlanner.md) (2), [Research](Research.md) (1), [BuildingType](BuildingType.md) (1), [Refining](Refining.md) (1), [RefineryFlow](RefineryFlow.md) (1)

**Used by (8):** [BuildingsTemplate](BuildingsTemplate.md), [EquipmentFit](EquipmentFit.md), [Freight](Freight.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [LicenceFees](LicenceFees.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE` | `36.0 / 24000 * 28 / 7.5` | Coal tonnes replacing a modern litre of rail diesel: 36 MJ/L, 24 GJ/t, and the study's 28% / 7.5% efficiencies (research-1900 PE21, estimate). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 429 | **type** `public final class RecipeChanges` | Composes independent discovery changes on the selected process recipe. |
| 16 | 1 | `private RecipeChanges()` |  |
| 23 | 15 | `static String choose(BuildingsTemplate t, String base, int month, Research found, Recipes.Prices p)` | Saved recipe decisions only; equipment remains on the stack's own counts. |
| 40 | 18 | `static Recipe decode(BuildingsTemplate t, String id)` | Resolves a saved composite recipe id without reading a new month's prices. |
| 60 | 3 | `public static Recipe compose(BuildingsTemplate t, Recipe base, Map<String, Double> absent, String id)` | A fresh recipe with the stated fractions of each undo. |
| 65 | 44 | `public static Recipe compose(BuildingsTemplate t, Recipe a, int na, Recipe b, int nb, Map<String, Double> absent, String id)` | Mixes the unrounded process contributions and independent undos, then rounds once. |
| 110 | 3 | `private static boolean has(String id, String code)` |  |
| 115 | 4 | `private static double share(RecipeChange change, Map<String, Double> absent)` | A later process improvement affects only the share running its prerequisite. |
| 120 | 4 | `private static Recipe base(BuildingsTemplate t, String id)` |  |
| 126 | 36 | `static boolean apply(Game game, BuildingsTemplate t, BuildingsStacks stack, String[] picked)` | Applies the complete unrounded mix; false leaves an unchanged legacy template to its driver. |
| 164 | 4 | `public static double gainOf(Game game, BuildingsTemplate t, Equipment piece)` | Gross monthly contribution of one independent piece, before R2c's rent. |
| 170 | 5 | `static double gainOf(Game game, BuildingsTemplate t, RecipeChange entry)` | The actual process with this entry present minus the process without it. |
| 177 | 9 | `static double gainForRefit(Game game, BuildingsTemplate t, Equipment piece)` | The next actual one-building refit, over the fleet's same rounded figures as payroll. |
| 188 | 7 | `static double gainForStanding(Game game, BuildingsTemplate t, Equipment piece)` | The whole actual production fleet's benefit from the pieces it really has. |
| 197 | 8 | `static double gainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted)` | One proposed building, with exactly the chosen pieces, compared with the same set lacking this piece. |
| 206 | 4 | `private static double differenceAt(Game game, BuildingsTemplate t, Map<String, Integer> with, Map<String, Integer> without, int...` |  |
| 211 | 6 | `static Recipe recipeForNew(Game game, BuildingsTemplate t, java.util.Set<String> fitted)` |  |
| 218 | 6 | `private static String[] newChoices(Game game, BuildingsTemplate t, Recipes.Prices prices)` |  |
| 225 | 5 | `private static Map<String, Integer> counts(BuildingsTemplate t, java.util.Set<String> fitted)` |  |
| 231 | 18 | `private static double valueAt(Game game, BuildingsTemplate t, Map<String, Integer> counts, int quantity, String[] ids, Recipes....` |  |
| 251 | 23 | `private static Map<String, Double> absences(Game game, BuildingsTemplate t, boolean newBuilding)` | The missing fractions in the actual fleet or a new building's known process. |
| 276 | 7 | `private static Recipe running(Game game, BuildingsTemplate t, Map<String, Double> absent)` | Recompose the unrounded process mix at the counterfactual missing fractions. |
| 285 | 3 | `public static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p)` | The benefit of removing this undo at the stated prices, without mutating a template. |
| 289 | 4 | `private static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, Map<String, Double> absent, Recipe base)` |  |
| 295 | 12 | `private static double gain(BuildingsTemplate t, RecipeChange entry, Map<String, Double> absent, java.util.function.Function<Map...` | One counterfactual for choices, fitting, appraisal and captured rent, including every scoped dependent. |
| 308 | 7 | `private static double extraValue(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, double crackerFeed)` |  |
| 317 | 4 | `static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, boolean standing, double rate, java.util.S...` | Imported rent on independent recipe changes actually used by this part of a stack. |
| 322 | 35 | `static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, boolean standing, double rate, java.util.S...` |  |
| 359 | 9 | `private static double standingValue(Recipe r, Recipes.Prices p, ham.citybuildersim.sectors.SpreadPlanner.Values values, double ...` | R2c's standing price roles: dispatched goods and consumables, fixed posts and utilities. |
| 374 | 29 | `public static void railService(Game game, ham.citybuildersim.sectors.Freight rail, double fuelEquivalent, double steamShare, bo...` | Capture PE21 where the private railway invoices its real service, before the common monthly strike. |
| 405 | 8 | `static Map<Integer, Double> serviceFeeMemo(Game game)` | The earlier rail invoice's saved cost survives the later goods-production capture. |
| 415 | 11 | `public static double railSteamShare(Game game)` | Capacity-weighted steam engines on the railway; default cities read no stacks. |
| 428 | 15 | `static void declareGoods(Game game, BuildingsTemplate t)` | Each possible old recipe's goods, for the owner's declaration and reload parity. |

