# RecipeChange.java - 66 lines · 5 methods · 0 constants · model

`ham/citybuildersim/RecipeChange.java` - generated 2026-11-16 by CodeMap; line numbers are as of that run.

> One discovery's signed undo, relative to a template's immutable modern figures.
> 
> WHY (R2d; R1c calls 4 and 8): whole recipes could not keep the binder beside
> the tractor. Additive changes preserve both, with one final rounding.
> Physical units are the template's; money is priced once on read.

**Uses:** [Good](Good.md) (8), [JobType](JobType.md) (4), [ResearchCatalog](ResearchCatalog.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1), [PriceLevel](PriceLevel.md) (1), [Research](Research.md) (1)

**Used by (4):** [BuildingCatalog](BuildingCatalog.md), [BuildingsTemplate](BuildingsTemplate.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 52 | **type** `public record RecipeChange(String code, String since, String piece, String source, Map<Good, Double> makes,...` | One discovery's signed undo, relative to a template's immutable modern figures. |
| 21 | 20 | `static RecipeChange read(JsonObject o, BuildingsTemplate t)` | Reads a sourced undo; bad entries cost that change, never half its fields. |
| 42 | 9 | `private static Map<Good, Double> goods(JsonObject o, String key)` |  |
| 52 | 5 | `private static double finite(JsonElement e)` |  |
| 58 | 3 | `private static double number(JsonObject o, String key)` |  |
| 63 | 3 | `public boolean knownAt(int month, Research found)` | Known on history's date or an earlier discovery here. |

