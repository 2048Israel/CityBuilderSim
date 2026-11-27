# SubstationCheck.java - 479 lines · 26 methods · 3 constants · harnesses

`ham/citybuildersim/SubstationCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5).
> WHY. A flat wires charge and unlimited wires passed PW1's price checks. These fixtures change real city stock,
> the dispatch's demand input, and automation's demand input; each rule also fails against the pre-PW5 model.

**Uses:** [Substations](Substations.md) (38), [Game](Game.md) (27), [UtilitiesHandler](UtilitiesHandler.md) (26), [PowerDispatch](PowerDispatch.md) (17), [JobType](JobType.md) (13), [PriceLevel](PriceLevel.md) (10), [BuildingManager](BuildingManager.md) (9), [BuildAdvice](BuildAdvice.md) (9), [Good](Good.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (7), [IndustryViewCheck](IndustryViewCheck.md) (6), [GameFiles](GameFiles.md) (4), [WorksValue](WorksValue.md) (4), [SaveFileCheck](SaveFileCheck.md) (3), [PowerPriceCheck](PowerPriceCheck.md) (3), [LongPlaytest](LongPlaytest.md) (3), [AutoBuilder](AutoBuilder.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [IndustryView](IndustryView.md) (2), [RecipeCheck](RecipeCheck.md) (2), [FittingCheck](FittingCheck.md) (2), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (2), [BuildingType](BuildingType.md) (1), [BuildCard](BuildCard.md) (1), [CityCalendar](CityCalendar.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [RailCheck](RailCheck.md) (1)

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
| 15 | 465 | **type** `public final class SubstationCheck` | Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5). |
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
| 113 | 39 | `static void tariff()` |  |
| 153 | 21 | `static void reform()` |  |
| 175 | 25 | `static void workedTariff()` |  |
| 202 | 2 | **type** `record Ordered(double supply, double peak, long districtBefore, long districtAfter, long districtRepeated, ...` | What one fixture's passes ordered: the standing grid's supply and peak, then the sites of each unit before the edge, past it, and after a repeated pass. |
| 206 | 18 | `static Ordered orderAt(Game g, BuildAdvice.Measure grid, double over)` | A grid at its edge (.99 of one district's carry asked, nothing ordered), then `over` of it asked: what the automatic builder orders, and that a repeat orders no more. |
| 225 | 34 | `static void order()` |  |
| 260 | 42 | `static void saved() throws Exception` |  |
| 308 | 21 | `static void heldGridPosts()` | ASH1 finding 9: the grid units' raw posts are held for a pass with the sectors' (BuildingManager.HeldPosts.grid), keyed on the same count of edits, so a stack that gains a Substation inside a scope is offered at the s... |
| 330 | 3 | **type** `record WireFinding(long districts, double peak, double cents, double energyCents, double ratio, double dies...` |  |
| 335 | 11 | `static String gridReading(Game g)` | Current stock and both real dispatch snapshots, for a fixture that loses its intended condition. |
| 347 | 6 | `static boolean gridCovered(Game g)` |  |
| 355 | 47 | `static void smallTownFinding()` | PW5-F1: whole districts have a utilization cost; omitted wires cause a different, physical squeeze. |
| 403 | 35 | `static WireFinding townFinding(String label, Game g)` |  |
| 439 | 24 | `static void historicalTownFinding()` |  |
| 464 | 5 | `static void bondTownWires()` |  |
| 470 | 9 | `public static void main(String[] args) throws Exception` |  |

