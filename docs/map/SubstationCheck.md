# SubstationCheck.java - 518 lines · 30 methods · 3 constants · harnesses

`ham/citybuildersim/SubstationCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5).
> WHY. A flat wires charge and unlimited wires passed PW1's price checks. These fixtures change real city stock,
> the dispatch's demand input, and automation's demand input; each rule also fails against the pre-PW5 model.

**Uses:** [Substations](Substations.md) (37), [Game](Game.md) (28), [UtilitiesHandler](UtilitiesHandler.md) (27), [JobType](JobType.md) (18), [PowerDispatch](PowerDispatch.md) (17), [PriceLevel](PriceLevel.md) (10), [BuildingManager](BuildingManager.md) (9), [BuildAdvice](BuildAdvice.md) (9), [Good](Good.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (7), [IndustryViewCheck](IndustryViewCheck.md) (6), [GameFiles](GameFiles.md) (4), [WorksValue](WorksValue.md) (4), [SaveFileCheck](SaveFileCheck.md) (3), [PowerPriceCheck](PowerPriceCheck.md) (3), [LongPlaytest](LongPlaytest.md) (3), [BuildingType](BuildingType.md) (2), [AutoBuilder](AutoBuilder.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [IndustryView](IndustryView.md) (2), [RecipeCheck](RecipeCheck.md) (2), [FittingCheck](FittingCheck.md) (2), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (2), [BuildCard](BuildCard.md) (1), [CityCalendar](CityCalendar.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [RailCheck](RailCheck.md) (1)

**Used by (17):** [CopperRefiningCheck](CopperRefiningCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [HaulageCheck](HaulageCheck.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HomeRailCheck](HomeRailCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [OrderDoorCheck](OrderDoorCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ScaleCheck](ScaleCheck.md), [TownGridCheck](TownGridCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `SubstationCheck.CARRY` | `Substations.SUBSTATION_MW * 1000` |  |
| 18 | `SubstationCheck.JSON` | `new Gson()` |  |
| 19 | `SubstationCheck.OUT` | `System.out, QUIET = new PrintStream(OutputStream.nullOutputStream())` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 16 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 504 | **type** `public final class SubstationCheck` | Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5). |
| 21 | 1 | `static void check(String label, boolean ok)` |  |
| 22 | 1 | `static boolean near(double a, double b)` |  |
| 23 | 1 | `static void quietly(Runnable work)` |  |
| 26 | 7 | `static void wiresFor(Game g, double peakKw)` | Physical wires for fixtures isolating generation, prices or fitting rather than grid scarcity. |
| 35 | 7 | `static void generationWires(Game g)` | Enough wires for every standing generator's nameplate, including the founding grid. |
| 44 | 5 | `static void peakWires(Game g)` | Districts for the standing draw before steam/electrolyser savings, at its highest season; unused generation needs no wires. |
| 50 | 13 | `static Game city(String label, int substations)` |  |
| 65 | 14 | `static UtilitiesHandler dispatch(Game g, double peak)` | A fully staffed coal row removes generation scarcity; the city stock still supplies the physical wires. |
| 80 | 3 | `static double generated(UtilitiesHandler u, int half)` |  |
| 84 | 22 | `static void carry()` |  |
| 107 | 4 | `static JsonObject costs(UtilitiesHandler.Book book)` |  |
| 111 | 1 | `static double number(JsonObject o, String key)` |  |
| 113 | 40 | `static void tariff()` |  |
| 154 | 21 | `static void reform()` |  |
| 176 | 25 | `static void workedTariff()` |  |
| 203 | 2 | **type** `record Ordered(double supply, double peak, long districtBefore, long districtAfter, long districtRepeated, ...` | What one fixture's passes ordered: the standing grid's supply and peak, then the sites of each unit before the edge, past it, and after a repeated pass. |
| 207 | 18 | `static Ordered orderAt(Game g, BuildAdvice.Measure grid, double over)` | A grid at its edge (.99 of one district's carry asked, nothing ordered), then `over` of it asked: what the automatic builder orders, and that a repeat orders no more. |
| 226 | 34 | `static void order()` |  |
| 261 | 55 | `static void saved() throws Exception` |  |
| 318 | 22 | **type** `record Staffing(double share, long[] offered, long[] withheld, long jobs, double[] fill, long[] electricity...` | A city's grid staffing and its pay as the month left them, what a reload must count and pay the same (REV-ASH1 1): read through the model's getters, never recomputed. |
| 319 | 10 | `static Staffing of(Game g)` _(in SubstationCheck.Staffing)_ |  |
| 330 | 4 | `public boolean equals(Object o)` _(in SubstationCheck.Staffing)_ | Every figure equal, the doubles to the bit (== on finite values). |
| 334 | 1 | `public int hashCode()` _(in SubstationCheck.Staffing)_ |  |
| 335 | 4 | `public String toString()` _(in SubstationCheck.Staffing)_ |  |
| 346 | 22 | `static void heldGridPosts()` | ASH1 finding 9: the grid units' raw posts are held for a pass with the sectors' (BuildingManager.HeldPosts.grid), keyed on the same count of edits, so a stack that gains a Substation inside a scope is offered at the s... |
| 369 | 3 | **type** `record WireFinding(long districts, double peak, double cents, double energyCents, double ratio, double dies...` |  |
| 374 | 11 | `static String gridReading(Game g)` | Current stock and both real dispatch snapshots, for a fixture that loses its intended condition. |
| 386 | 6 | `static boolean gridCovered(Game g)` |  |
| 394 | 47 | `static void smallTownFinding()` | PW5-F1: whole districts have a utilization cost; omitted wires cause a different, physical squeeze. |
| 442 | 35 | `static WireFinding townFinding(String label, Game g)` |  |
| 478 | 24 | `static void historicalTownFinding()` |  |
| 503 | 5 | `static void bondTownWires()` |  |
| 509 | 9 | `public static void main(String[] args) throws Exception` |  |

