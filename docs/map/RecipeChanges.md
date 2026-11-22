# RecipeChanges.java - 614 lines · 46 methods · 1 constants · model

`ham/citybuildersim/RecipeChanges.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Composes independent discovery changes on the selected process recipe.
> 
> WHY (R2d): a second whole recipe erased the first piece's effect. Each undo
> is summed from an immutable anchor, in code order, and rounded only once.
> Recipe decisions are saved in the existing recipe ids after "~"; fitting
> counts are read on application, including after a construction site opens.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (45), [Recipes](Recipes.md) (40), [Game](Game.md) (31), [Recipe](Recipe.md) (29), [RecipeChange](RecipeChange.md) (24), [Good](Good.md) (15), [JobType](JobType.md) (15), [LicenceFees](LicenceFees.md) (12), [Equipment](Equipment.md) (12), [BuildingsStacks](BuildingsStacks.md) (9), [Sector](Sector.md) (4), [NewUnit](NewUnit.md) (3), [Freight](Freight.md) (3), [ResearchCatalog](ResearchCatalog.md) (2), [Agriculture](Agriculture.md) (2), [SpreadPlanner](SpreadPlanner.md) (2), [Research](Research.md) (1), [BuildAdvice](BuildAdvice.md) (1), [EquipmentFit](EquipmentFit.md) (1), [BuildingType](BuildingType.md) (1), [Refining](Refining.md) (1), [RefineryFlow](RefineryFlow.md) (1)

**Used by (13):** [BuildingsTemplate](BuildingsTemplate.md), [EquipmentFit](EquipmentFit.md), [FittingCheck](FittingCheck.md), [Freight](Freight.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [LicenceFees](LicenceFees.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [NewUnit](NewUnit.md), [NewUnitCheck](NewUnitCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE` | `36.0 / 24000 * 28 / 7.5` | Coal tonnes replacing a modern litre of rail diesel: 36 MJ/L, 24 GJ/t, and the study's 28% / 7.5% efficiencies (research-1900 PE21, estimate). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 600 | **type** `public final class RecipeChanges` | Composes independent discovery changes on the selected process recipe. |
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
| 254 | 10 | `static String[] newChoices(Game game, BuildingsTemplate t, Recipes.Prices prices)` | The recipes a new building of this template would run, the fitted part's and the unfitted part's (R2c-P, REV-R2c-P-1: the planners' licence prices the state they run in). |
| 271 | 22 | `static NewUnit newUnit(Game game, BuildingsTemplate t, java.util.Set<String> fitted)` | One new building's figures (QN1, NewUnit.of()): apply()'s own sum for a stack of one with exactly the pieces `fitted`, on the process newChoices() gives, the template untouched. |
| 294 | 5 | `static Map<String, Long> counts(BuildingsTemplate t, java.util.Set<String> fitted)` |  |
| 305 | 8 | `static double productionAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids)` | The nameplate production of a building with these unfitted counts and these running recipes, by apply()'s own sum (R2c-P; package-private for LicenceFees.billOf(), REV-R2c-P-1). |
| 314 | 10 | `private static double valueAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids, Recipes.Pr...` |  |
| 326 | 26 | `private static double horseCostAt(Game game, BuildingsTemplate target, Map<String, Long> counts, long quantity, Recipe recipe, ...` | Agriculture's actual form uses one land-weighted share on the whole sector's crops and rounded posts (R1d review). |
| 353 | 6 | `private static long missingHorses(BuildingsTemplate t, Map<String, Long> counts, long quantity)` |  |
| 360 | 13 | `private static Map<String, Double> absencesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[]...` |  |
| 375 | 3 | `static double feesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids)` | Every royalty on the same rounded physical state used to judge a fitting. |
| 379 | 41 | `private static double feesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids, boolean pro...` |  |
| 422 | 23 | `private static Map<String, Double> absences(Game game, BuildingsTemplate t, boolean newBuilding)` | The missing fractions in the actual fleet or a new building's known process. |
| 447 | 7 | `private static Recipe running(Game game, BuildingsTemplate t, Map<String, Double> absent)` | Recompose the unrounded process mix at the counterfactual missing fractions. |
| 456 | 3 | `public static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p)` | The benefit of removing this undo at the stated prices, without mutating a template. |
| 460 | 4 | `private static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, Map<String, Double> absent, Recipe base)` |  |
| 466 | 12 | `private static double gain(BuildingsTemplate t, RecipeChange entry, Map<String, Double> absent, java.util.function.Function<Map...` | One counterfactual for choices, fitting, appraisal and captured rent, including every scoped dependent. |
| 479 | 7 | `private static double extraValue(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, double crackerFeed)` |  |
| 488 | 4 | `static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, boolean standing, double rate, java.util.S...` | Imported rent on independent recipe changes actually used by this part of a stack. |
| 493 | 35 | `static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, boolean standing, double rate, java.util.S...` |  |
| 530 | 9 | `private static double standingValue(BuildingsTemplate t, Sector owner, Recipe r, Recipes.Prices p, ham.citybuildersim.sectors.S...` | R2c's standing price roles: dispatched goods and consumables, fixed posts and utilities. |
| 545 | 29 | `public static void railService(Game game, ham.citybuildersim.sectors.Freight rail, double fuelEquivalent, double steamShare, bo...` | Capture PE21 where the private railway invoices its real service, before the common monthly strike. |
| 576 | 8 | `static Map<Integer, Double> serviceFeeMemo(Game game)` | The earlier rail invoice's saved cost survives the later goods-production capture. |
| 586 | 11 | `public static double railSteamShare(Game game)` | Capacity-weighted steam engines on the railway; default cities read no stacks. |
| 599 | 15 | `static void declareGoods(Game game, BuildingsTemplate t)` | Each possible old recipe's goods, for the owner's declaration and reload parity. |

