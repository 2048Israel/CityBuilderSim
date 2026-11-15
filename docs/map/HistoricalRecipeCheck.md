# HistoricalRecipeCheck.java - 429 lines · 21 methods · 2 constants · harnesses

`ham/citybuildersim/HistoricalRecipeCheck.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> Each sourced 1900 undo runs before its entry, knowledge alone does not fit
> equipment, independent pieces compose, and the default city stays modern.
> WHY (R2d): catalogue dates alone left 1900 templates running modern figures.
> Every row fixture stands the affected template, with all other discoveries
> known and pieces fitted, and crosses this entry's actual month. This harness
> also compiles against pre-R2d code, so its before failures are model failures.

**Uses:** [Good](Good.md) (27), [Game](Game.md) (18), [BuildingsTemplate](BuildingsTemplate.md) (16), [RecipeCheck](RecipeCheck.md) (13), [JobType](JobType.md) (11), [Recipes](Recipes.md) (10), [ResearchCatalog](ResearchCatalog.md) (8), [EquipmentCheck](EquipmentCheck.md) (8), [BuildingsStacks](BuildingsStacks.md) (6), [FittingCheck](FittingCheck.md) (4), [DataSave](DataSave.md) (4), [PolicyDates](PolicyDates.md) (3), [Equipment](Equipment.md) (2), [Trade](Trade.md) (2), [RefineryFlow](RefineryFlow.md) (2), [Forms1900Check](Forms1900Check.md) (2), [PriceLevel](PriceLevel.md) (1), [Freight](Freight.md) (1), [Traffic](Traffic.md) (1), [Sector](Sector.md) (1), [Refining](Refining.md) (1), [Founding](Founding.md) (1), [EquipmentFit](EquipmentFit.md) (1), [BuildingType](BuildingType.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 28 | `HistoricalRecipeCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 29 | `HistoricalRecipeCheck.month` |  |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 26 | `static int fails, checks` |  |
| 27 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 405 | **type** `public final class HistoricalRecipeCheck` | Each sourced 1900 undo runs before its entry, knowledge alone does not fit equipment, independent pieces compose, and the default city stays modern. |
| 30 | 6 | `static { ... }` |  |
| 37 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 43 | 5 | `static void silent(Runnable action)` |  |
| 49 | 1 | `static void at(Game g, int m) throws Exception` |  |
| 51 | 6 | `static void allExcept(Game g, String except)` |  |
| 58 | 1 | `static void run(Game g)` |  |
| 60 | 3 | `static void fitted(BuildingsTemplate t, BuildingsStacks stack)` |  |
| 64 | 17 | `static Map<String, Double> figures(BuildingsTemplate t)` |  |
| 83 | 5 | `static double extra(BuildingsTemplate t, String method, double beforeValue)` | Missing pre-R2d read methods expose main's actual modern value, not a compile failure. |
| 89 | 15 | `static boolean matchesUndo(Map<String, Double> old, Map<String, Double> modern, JsonObject d)` |  |
| 105 | 3 | `static Game town(String label, boolean historical)` |  |
| 109 | 41 | `static void rows(JsonObject data) throws Exception` |  |
| 151 | 45 | `static void composition() throws Exception` |  |
| 197 | 12 | `static void defaults(JsonObject data) throws Exception` |  |
| 210 | 17 | `static void clock() throws Exception` |  |
| 229 | 30 | `static void railFuel() throws Exception` |  |
| 260 | 27 | `static void cracking() throws Exception` |  |
| 288 | 25 | `static void supersedingProcess() throws Exception` |  |
| 315 | 33 | `static void completedPiece() throws Exception` |  |
| 350 | 29 | `static void savedPriceChoice() throws Exception` |  |
| 381 | 27 | `static void utilityUnits(JsonObject data) throws Exception` |  |
| 409 | 20 | `public static void main(String[] args) throws Exception` |  |

