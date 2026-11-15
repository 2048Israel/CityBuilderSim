# RecipeChanges.java - 188 lines · 12 methods · 1 constants · model

`ham/citybuildersim/RecipeChanges.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> Composes independent discovery changes on the selected process recipe.
> 
> WHY (R2d): a second whole recipe erased the first piece's effect. Each undo
> is summed from an immutable anchor, in code order, and rounded only once.
> Recipe decisions are saved in the existing recipe ids after "~"; fitting
> counts are read on application, including after a construction site opens.

**Uses:** [Recipe](Recipe.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (10), [Good](Good.md) (10), [JobType](JobType.md) (6), [Recipes](Recipes.md) (5), [RecipeChange](RecipeChange.md) (5), [Game](Game.md) (4), [BuildingsStacks](BuildingsStacks.md) (2), [Freight](Freight.md) (2), [Research](Research.md) (1), [Equipment](Equipment.md) (1), [BuildingType](BuildingType.md) (1), [Sector](Sector.md) (1)

**Used by (5):** [BuildingsTemplate](BuildingsTemplate.md), [EquipmentFit](EquipmentFit.md), [Freight](Freight.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE` | `36.0 / 24000 * 28 / 7.5` | Coal tonnes replacing a modern litre of rail diesel: 36 MJ/L, 24 GJ/t, and the study's 28% / 7.5% efficiencies (research-1900 PE21, estimate). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 174 | **type** `public final class RecipeChanges` | Composes independent discovery changes on the selected process recipe. |
| 16 | 1 | `private RecipeChanges()` |  |
| 23 | 8 | `static String choose(BuildingsTemplate t, String base, int month, Research found, Recipes.Prices p)` | Saved recipe decisions only; equipment remains on the stack's own counts. |
| 33 | 8 | `static Recipe decode(BuildingsTemplate t, String id)` | Resolves a saved composite recipe id without reading a new month's prices. |
| 43 | 3 | `public static Recipe compose(BuildingsTemplate t, Recipe base, Map<String, Double> absent, String id)` | A fresh recipe with the stated fractions of each undo. |
| 48 | 44 | `public static Recipe compose(BuildingsTemplate t, Recipe a, int na, Recipe b, int nb, Map<String, Double> absent, String id)` | Mixes the unrounded process contributions and independent undos, then rounds once. |
| 93 | 3 | `private static boolean has(String id, String code)` |  |
| 97 | 4 | `private static Recipe base(BuildingsTemplate t, String id)` |  |
| 103 | 33 | `static boolean apply(Game game, BuildingsTemplate t, BuildingsStacks stack, String[] picked)` | Applies the complete unrounded mix; false leaves an unchanged legacy template to its driver. |
| 138 | 4 | `public static double gainOf(Game game, BuildingsTemplate t, Equipment piece)` | Gross monthly contribution of one independent piece, before R2c's rent. |
| 144 | 14 | `public static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p)` | The benefit of removing this undo at the stated prices, without mutating a template. |
| 160 | 11 | `public static double railSteamShare(Game game)` | Capacity-weighted steam engines on the railway; default cities read no stacks. |
| 173 | 15 | `static void declareGoods(Game game, BuildingsTemplate t)` | Each possible old recipe's goods, for the owner's declaration and reload parity. |

