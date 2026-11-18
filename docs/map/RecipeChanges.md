# RecipeChanges.java - 590 lines · 47 methods · 1 constants · model

`ham/citybuildersim/RecipeChanges.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> Composes independent discovery changes on the selected process recipe.
> 
> WHY (R2d): a second whole recipe erased the first piece's effect. Each undo
> is summed from an immutable anchor, in code order, and rounded only once.
> Recipe decisions are saved in the existing recipe ids after "~"; fitting
> counts are read on application, including after a construction site opens.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (45), [Recipes](Recipes.md) (40), [Game](Game.md) (32), [Recipe](Recipe.md) (27), [RecipeChange](RecipeChange.md) (23), [Good](Good.md) (15), [JobType](JobType.md) (15), [Equipment](Equipment.md) (12), [BuildingsStacks](BuildingsStacks.md) (10), [LicenceFees](LicenceFees.md) (7), [Freight](Freight.md) (3), [Sector](Sector.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [Agriculture](Agriculture.md) (2), [SpreadPlanner](SpreadPlanner.md) (2), [Research](Research.md) (1), [EquipmentFit](EquipmentFit.md) (1), [BuildingType](BuildingType.md) (1), [Refining](Refining.md) (1), [RefineryFlow](RefineryFlow.md) (1)

**Used by (10):** [BuildingsTemplate](BuildingsTemplate.md), [EquipmentFit](EquipmentFit.md), [FittingCheck](FittingCheck.md), [Freight](Freight.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [LicenceFees](LicenceFees.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE` | `36.0 / 24000 * 28 / 7.5` | Coal tonnes replacing a modern litre of rail diesel: 36 MJ/L, 24 GJ/t, and the study's 28% / 7.5% efficiencies (research-1900 PE21, estimate). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 576 | **type** `public final class RecipeChanges` | Composes independent discovery changes on the selected process recipe. |
| 16 | 1 | `private RecipeChanges()` |  |
| 23 | 15 | `static String choose(BuildingsTemplate t, String base, int month, Research found, Recipes.Prices p)` | Saved recipe decisions only; equipment remains on the stack's own counts. |
| 40 | 18 | `static Recipe decode(BuildingsTemplate t, String id)` | Resolves a saved composite recipe id without reading a new month's prices. |
| 60 | 3 | `public static Recipe compose(BuildingsTemplate t, Recipe base, Map<String, Double> absent, String id)` | A fresh recipe with the stated fractions of each undo. |
| 65 | 44 | `public static Recipe compose(BuildingsTemplate t, Recipe a, long na, Recipe b, long nb, Map<String, Double> absent, String id)` | Mixes the unrounded process contributions and independent undos, then rounds once. |
| 110 | 3 | `private static boolean has(String id, String code)` |  |
| 115 | 4 | `private static double share(RecipeChange change, Map<String, Double> absent)` | A later process improvement affects only the share running its prerequisite. |
| 120 | 4 | `private static Recipe base(BuildingsTemplate t, String id)` |  |
| 126 | 36 | `static boolean apply(Game game, BuildingsTemplate t, BuildingsStacks stack, String[] picked)` | Applies the complete unrounded mix; false leaves an unchanged legacy template to its driver. |
| 164 | 4 | `public static double gainOf(Game game, BuildingsTemplate t, Equipment piece)` | Gross monthly contribution of one independent piece, before R2c's rent. |
| 170 | 5 | `static double gainOf(Game game, BuildingsTemplate t, RecipeChange entry)` | The actual process with this entry present minus the process without it. |
| 177 | 3 | `static double netGainForRefit(Game game, BuildingsTemplate t, Equipment piece)` | The next actual one-building refit, over the fleet's same rounded figures as payroll. |
| 182 | 3 | `static double grossGainForRefit(Game game, BuildingsTemplate t, Equipment piece)` | The same actual one-building counterfactual before patent rent (R1d). |
| 186 | 10 | `private static double gainForRefit(Game game, BuildingsTemplate t, Equipment piece, boolean net)` |  |
| 198 | 7 | `static double gainForStanding(Game game, BuildingsTemplate t, Equipment piece)` | The whole actual production fleet's benefit from the pieces it really has. |
| 207 | 3 | `static double gainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted)` | One proposed building, with exactly the chosen pieces, compared with the same set lacking this piece. |
| 211 | 3 | `static double netGainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted)` |  |
| 215 | 9 | `private static double gainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted, boolean net)` |  |
| 225 | 4 | `private static double netDifferenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without, long q...` |  |
| 230 | 5 | `private static double netDifferenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without, long q...` |  |
| 236 | 4 | `private static double differenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without, long quan...` |  |
| 241 | 4 | `private static double differenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without, long quan...` |  |
| 246 | 6 | `static Recipe recipeForNew(Game game, BuildingsTemplate t, java.util.Set<String> fitted)` |  |
| 253 | 10 | `private static String[] newChoices(Game game, BuildingsTemplate t, Recipes.Prices prices)` |  |
| 264 | 5 | `static Map<String, Long> counts(BuildingsTemplate t, java.util.Set<String> fitted)` |  |
| 274 | 8 | `private static double productionAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids)` | The nameplate production of a building with these unfitted counts and these running recipes, by apply()'s own sum (R2c-P). |
| 284 | 5 | `static double productionWithout(Game game, BuildingsTemplate t, BuildingsStacks stack, String pieceId)` | One building's nameplate production with this piece off every building of the stack, the rest as they stand (R2c-P). |
| 291 | 3 | `static double productionFor(Game game, BuildingsTemplate t, java.util.Set<String> fitted)` | A new building's nameplate production with exactly these pieces fitted, on the recipes it would run (R2c-P). |
| 295 | 10 | `private static double valueAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids, Recipes.Pr...` |  |
| 307 | 26 | `private static double horseCostAt(Game game, BuildingsTemplate target, Map<String, Long> counts, long quantity, Recipe recipe, ...` | Agriculture's actual form uses one land-weighted share on the whole sector's crops and rounded posts (R1d review). |
| 334 | 6 | `private static long missingHorses(BuildingsTemplate t, Map<String, Long> counts, long quantity)` |  |
| 341 | 13 | `private static Map<String, Double> absencesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[]...` |  |
| 356 | 3 | `static double feesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids)` | Every royalty on the same rounded physical state used to judge a fitting. |
| 360 | 36 | `private static double feesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids, boolean pro...` |  |
| 398 | 23 | `private static Map<String, Double> absences(Game game, BuildingsTemplate t, boolean newBuilding)` | The missing fractions in the actual fleet or a new building's known process. |
| 423 | 7 | `private static Recipe running(Game game, BuildingsTemplate t, Map<String, Double> absent)` | Recompose the unrounded process mix at the counterfactual missing fractions. |
| 432 | 3 | `public static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p)` | The benefit of removing this undo at the stated prices, without mutating a template. |
| 436 | 4 | `private static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, Map<String, Double> absent, Recipe base)` |  |
| 442 | 12 | `private static double gain(BuildingsTemplate t, RecipeChange entry, Map<String, Double> absent, java.util.function.Function<Map...` | One counterfactual for choices, fitting, appraisal and captured rent, including every scoped dependent. |
| 455 | 7 | `private static double extraValue(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, double crackerFeed)` |  |
| 464 | 4 | `static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, boolean standing, double rate, java.util.S...` | Imported rent on independent recipe changes actually used by this part of a stack. |
| 469 | 35 | `static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, boolean standing, double rate, java.util.S...` |  |
| 506 | 9 | `private static double standingValue(Recipe r, Recipes.Prices p, ham.citybuildersim.sectors.SpreadPlanner.Values values, double ...` | R2c's standing price roles: dispatched goods and consumables, fixed posts and utilities. |
| 521 | 29 | `public static void railService(Game game, ham.citybuildersim.sectors.Freight rail, double fuelEquivalent, double steamShare, bo...` | Capture PE21 where the private railway invoices its real service, before the common monthly strike. |
| 552 | 8 | `static Map<Integer, Double> serviceFeeMemo(Game game)` | The earlier rail invoice's saved cost survives the later goods-production capture. |
| 562 | 11 | `public static double railSteamShare(Game game)` | Capacity-weighted steam engines on the railway; default cities read no stacks. |
| 575 | 15 | `static void declareGoods(Game game, BuildingsTemplate t)` | Each possible old recipe's goods, for the owner's declaration and reload parity. |

