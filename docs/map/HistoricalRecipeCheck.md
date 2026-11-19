# HistoricalRecipeCheck.java - 743 lines · 31 methods · 2 constants · harnesses

`ham/citybuildersim/HistoricalRecipeCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Each sourced 1900 undo runs before its entry, knowledge alone does not fit
> equipment, independent pieces compose, and the default city stays modern.
> WHY (R2d): catalogue dates alone left 1900 templates running modern figures.
> Every row fixture stands the affected template, with all other discoveries
> known and pieces fitted, and crosses this entry's actual month. This harness
> also compiles against pre-R2d code, so its before failures are model failures.

**Uses:** [Good](Good.md) (78), [Game](Game.md) (26), [BuildingsTemplate](BuildingsTemplate.md) (23), [RecipeCheck](RecipeCheck.md) (21), [JobType](JobType.md) (15), [Refining](Refining.md) (15), [RefineryFlow](RefineryFlow.md) (15), [ResearchCatalog](ResearchCatalog.md) (14), [EquipmentCheck](EquipmentCheck.md) (11), [Recipes](Recipes.md) (10), [BuildingsStacks](BuildingsStacks.md) (9), [Resource](Resource.md) (8), [Trade](Trade.md) (6), [PolicyDates](PolicyDates.md) (5), [FittingCheck](FittingCheck.md) (4), [DataSave](DataSave.md) (4), [Deposit](Deposit.md) (4), [BuildCard](BuildCard.md) (4), [Traffic](Traffic.md) (3), [RefineryView](RefineryView.md) (3), [OilView](OilView.md) (3), [ResearchView](ResearchView.md) (3), [Equipment](Equipment.md) (2), [Sector](Sector.md) (2), [CopperMining](CopperMining.md) (2), [Forms1900Check](Forms1900Check.md) (2), [PriceLevel](PriceLevel.md) (1), [Freight](Freight.md) (1), [Founding](Founding.md) (1), [EquipmentFit](EquipmentFit.md) (1)... and 5 more

**Used by (3):** [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [NewUnitCheck](NewUnitCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md)

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
| 25 | 719 | **type** `public final class HistoricalRecipeCheck` | Each sourced 1900 undo runs before its entry, knowledge alone does not fit equipment, independent pieces compose, and the default city stays modern. |
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
| 110 | 42 | `static void rows(JsonObject data) throws Exception` |  |
| 153 | 45 | `static void composition() throws Exception` |  |
| 199 | 12 | `static void defaults(JsonObject data) throws Exception` |  |
| 212 | 17 | `static void clock() throws Exception` |  |
| 231 | 30 | `static void railFuel() throws Exception` |  |
| 262 | 38 | `static void depotFuel() throws Exception` |  |
| 301 | 27 | `static void cracking() throws Exception` |  |
| 329 | 25 | `static void supersedingProcess() throws Exception` |  |
| 356 | 33 | `static void completedPiece() throws Exception` |  |
| 391 | 29 | `static void savedPriceChoice() throws Exception` |  |
| 422 | 27 | `static void utilityUnits(JsonObject data) throws Exception` |  |
| 451 | 28 | `static void oneRounding(JsonObject data) throws Exception` |  |
| 480 | 36 | `static void recoveryDepletion() throws Exception` |  |
| 517 | 26 | `static void boundedCrackingMean() throws Exception` |  |
| 544 | 44 | `static void thermalAppraisal() throws Exception` |  |
| 591 | 12 | `static Map<Good, Double> thermalProducts(double litres)` | Expected thermal yields: the study's 0.24 petrol, leaving the modern difference as fuel oil. |
| 604 | 6 | `static double thermalSpread(double[] values)` |  |
| 611 | 4 | `static void priceForBothReaders(Game g, Good good, double price)` |  |
| 616 | 67 | `static void thermalReaders() throws Exception` |  |
| 685 | 30 | `static void compositeResearchReader() throws Exception` |  |
| 716 | 27 | `public static void main(String[] args) throws Exception` |  |

