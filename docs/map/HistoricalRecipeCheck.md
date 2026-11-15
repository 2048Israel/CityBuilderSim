# HistoricalRecipeCheck.java - 702 lines · 30 methods · 2 constants · harnesses

`ham/citybuildersim/HistoricalRecipeCheck.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> Each sourced 1900 undo runs before its entry, knowledge alone does not fit
> equipment, independent pieces compose, and the default city stays modern.
> WHY (R2d): catalogue dates alone left 1900 templates running modern figures.
> Every row fixture stands the affected template, with all other discoveries
> known and pieces fitted, and crosses this entry's actual month. This harness
> also compiles against pre-R2d code, so its before failures are model failures.

**Uses:** [Good](Good.md) (65), [Game](Game.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (22), [RecipeCheck](RecipeCheck.md) (20), [JobType](JobType.md) (15), [Refining](Refining.md) (15), [RefineryFlow](RefineryFlow.md) (15), [ResearchCatalog](ResearchCatalog.md) (13), [EquipmentCheck](EquipmentCheck.md) (11), [Recipes](Recipes.md) (10), [BuildingsStacks](BuildingsStacks.md) (9), [Resource](Resource.md) (8), [PolicyDates](PolicyDates.md) (5), [FittingCheck](FittingCheck.md) (4), [DataSave](DataSave.md) (4), [Deposit](Deposit.md) (4), [BuildCard](BuildCard.md) (4), [RefineryView](RefineryView.md) (3), [OilView](OilView.md) (3), [ResearchView](ResearchView.md) (3), [Equipment](Equipment.md) (2), [Trade](Trade.md) (2), [CopperMining](CopperMining.md) (2), [Forms1900Check](Forms1900Check.md) (2), [PriceLevel](PriceLevel.md) (1), [Freight](Freight.md) (1), [Traffic](Traffic.md) (1), [Sector](Sector.md) (1), [Founding](Founding.md) (1), [EquipmentFit](EquipmentFit.md) (1)... and 5 more

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
| 25 | 678 | **type** `public final class HistoricalRecipeCheck` | Each sourced 1900 undo runs before its entry, knowledge alone does not fit equipment, independent pieces compose, and the default city stays modern. |
| 30 | 6 | `static { ... }` |  |
| 37 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 43 | 5 | `static void silent(Runnable action)` |  |
| 49 | 1 | `static void at(Game g, int m) throws Exception` |  |
| 51 | 6 | `static void allExcept(Game g, String except)` |  |
| 58 | 1 | `static void run(Game g)` |  |
| 60 | 3 | `static void fitted(BuildingsTemplate t, BuildingsStacks stack)` |  |
| 64 | 18 | `static Map<String, Double> figures(BuildingsTemplate t)` |  |
| 84 | 5 | `static double extra(Object t, String method, double beforeValue)` | Missing pre-R2d read methods expose main's actual modern value, not a compile failure. |
| 90 | 15 | `static boolean matchesUndo(Map<String, Double> old, Map<String, Double> modern, JsonObject d)` |  |
| 106 | 3 | `static Game town(String label, boolean historical)` |  |
| 110 | 41 | `static void rows(JsonObject data) throws Exception` |  |
| 152 | 45 | `static void composition() throws Exception` |  |
| 198 | 12 | `static void defaults(JsonObject data) throws Exception` |  |
| 211 | 17 | `static void clock() throws Exception` |  |
| 230 | 30 | `static void railFuel() throws Exception` |  |
| 261 | 27 | `static void cracking() throws Exception` |  |
| 289 | 25 | `static void supersedingProcess() throws Exception` |  |
| 316 | 33 | `static void completedPiece() throws Exception` |  |
| 351 | 29 | `static void savedPriceChoice() throws Exception` |  |
| 382 | 27 | `static void utilityUnits(JsonObject data) throws Exception` |  |
| 411 | 28 | `static void oneRounding(JsonObject data) throws Exception` |  |
| 440 | 36 | `static void recoveryDepletion() throws Exception` |  |
| 477 | 26 | `static void boundedCrackingMean() throws Exception` |  |
| 504 | 44 | `static void thermalAppraisal() throws Exception` |  |
| 551 | 12 | `static Map<Good, Double> thermalProducts(double litres)` | Expected thermal yields: the study's 0.24 petrol, leaving the modern difference as fuel oil. |
| 564 | 6 | `static double thermalSpread(double[] values)` |  |
| 571 | 4 | `static void priceForBothReaders(Game g, Good good, double price)` |  |
| 576 | 67 | `static void thermalReaders() throws Exception` |  |
| 645 | 30 | `static void compositeResearchReader() throws Exception` |  |
| 676 | 26 | `public static void main(String[] args) throws Exception` |  |

