# SubstationCheck.java - 360 lines · 23 methods · 3 constants · harnesses

`ham/citybuildersim/SubstationCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5).
> WHY. A flat wires charge and unlimited wires passed PW1's price checks. These fixtures change real city stock,
> the dispatch's demand input, and automation's demand input; each rule also fails against the pre-PW5 model.

**Uses:** [Game](Game.md) (23), [Substations](Substations.md) (22), [UtilitiesHandler](UtilitiesHandler.md) (22), [PowerDispatch](PowerDispatch.md) (15), [PriceLevel](PriceLevel.md) (10), [Good](Good.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (7), [IndustryViewCheck](IndustryViewCheck.md) (6), [BuildingManager](BuildingManager.md) (4), [GameFiles](GameFiles.md) (3), [JobType](JobType.md) (3), [BuildAdvice](BuildAdvice.md) (3), [PowerPriceCheck](PowerPriceCheck.md) (3), [SaveFileCheck](SaveFileCheck.md) (2), [LongPlaytest](LongPlaytest.md) (2), [IndustryView](IndustryView.md) (2), [BuildingType](BuildingType.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [BuildCard](BuildCard.md) (1), [AutoBuilder](AutoBuilder.md) (1), [CityCalendar](CityCalendar.md) (1), [RailCheck](RailCheck.md) (1)

**Used by (15):** [CopperRefiningCheck](CopperRefiningCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FitoutCheck](FitoutCheck.md), [HaulageCheck](HaulageCheck.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HomeRailCheck](HomeRailCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [NewUnitCheck](NewUnitCheck.md), [OrderDoorCheck](OrderDoorCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md)

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
| 15 | 346 | **type** `public final class SubstationCheck` | Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5). |
| 21 | 1 | `static void check(String label, boolean ok)` |  |
| 22 | 1 | `static boolean near(double a, double b)` |  |
| 23 | 1 | `static void quietly(Runnable work)` |  |
| 26 | 7 | `static void wiresFor(Game g, double peakKw)` | Physical wires for fixtures isolating generation, prices or fitting rather than grid scarcity. |
| 35 | 7 | `static void generationWires(Game g)` | Enough wires for every standing generator's nameplate, including the founding grid. |
| 44 | 5 | `static void peakWires(Game g)` | Districts for the standing draw before steam/electrolyser savings, at its highest season; unused generation needs no wires. |
| 51 | 17 | `static void buildersForSite(Game g, int requested)` | Real depots restoring the site budget consumed by standing wire repairs, with the fixture's own minimum too. |
| 69 | 13 | `static Game city(String label, int substations)` |  |
| 84 | 14 | `static UtilitiesHandler dispatch(Game g, double peak)` | A fully staffed coal row removes generation scarcity; the city stock still supplies the physical wires. |
| 99 | 3 | `static double generated(UtilitiesHandler u, int half)` |  |
| 103 | 22 | `static void carry()` |  |
| 126 | 4 | `static JsonObject costs(UtilitiesHandler.Book book)` |  |
| 130 | 1 | `static double number(JsonObject o, String key)` |  |
| 132 | 25 | `static void tariff()` |  |
| 158 | 21 | `static void reform()` |  |
| 180 | 25 | `static void workedTariff()` |  |
| 206 | 22 | `static void order()` |  |
| 229 | 15 | `static void saved() throws Exception` |  |
| 245 | 3 | **type** `record WireFinding(long districts, double peak, double cents, double energyCents, double ratio, double dies...` |  |
| 250 | 11 | `static String gridReading(Game g)` | Current stock and both real dispatch snapshots, for a fixture that loses its intended condition. |
| 262 | 6 | `static boolean gridCovered(Game g)` |  |
| 270 | 47 | `static void smallTownFinding()` | PW5-F1: whole districts have a utilization cost; omitted wires cause a different, physical squeeze. |
| 318 | 35 | `static WireFinding townFinding(String label, Game g)` |  |
| 354 | 6 | `public static void main(String[] args) throws Exception` |  |

