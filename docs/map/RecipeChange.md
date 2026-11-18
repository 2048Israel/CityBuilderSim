# RecipeChange.java - 68 lines · 5 methods · 0 constants · model

`ham/citybuildersim/RecipeChange.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> One discovery's signed undo, relative to a template's immutable modern figures.
> 
> WHY (R2d; R1c calls 4 and 8): whole recipes could not keep the binder beside
> the tractor. Additive changes preserve both, with one final rounding.
> Physical units are the template's; money is priced once on read.

**Uses:** [Good](Good.md) (8), [JobType](JobType.md) (4), [ResearchCatalog](ResearchCatalog.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1), [PriceLevel](PriceLevel.md) (1), [Research](Research.md) (1)

**Used by (5):** [BuildingCatalog](BuildingCatalog.md), [BuildingsTemplate](BuildingsTemplate.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 54 | **type** `public record RecipeChange(String code, String since, String piece, String after, String source, Map<Good, ...` | One discovery's signed undo, relative to a template's immutable modern figures. |
| 21 | 22 | `static RecipeChange read(JsonObject o, BuildingsTemplate t)` | Reads a sourced undo; bad entries cost that change, never half its fields. |
| 44 | 9 | `private static Map<Good, Double> goods(JsonObject o, String key)` |  |
| 54 | 5 | `private static double finite(JsonElement e)` |  |
| 60 | 3 | `private static double number(JsonObject o, String key)` |  |
| 65 | 3 | `public boolean knownAt(int month, Research found)` | Known on history's date or an earlier discovery here. |

