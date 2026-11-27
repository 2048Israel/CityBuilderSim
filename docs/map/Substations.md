# Substations.java - 80 lines · 8 methods · 7 constants · model

`ham/citybuildersim/Substations.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The city's wires: the district Substation (100 MW) and the small distribution substation (5 MW), and their own costs in the power bill.
> WHY. PW1 charged a flat 5.94 cents a kWh for wires that nothing built or maintained.
> PW5 (spec-electricity 2.7) uses real city buildings, paid through the existing construction,
> repair and utility payroll books; the tariff recovers those costs for their owner, the treasury.
> ASH1: the only step past the free 10 MW was a 100 MW unit at 153% of a 6,166-person town's GDP,
> so a city distribution substation of a few MW (Portland and the Midwest, 1924) lets the wires grow with the town.

**Uses:** [PowerDispatch](PowerDispatch.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [JobType](JobType.md) (2), [Game](Game.md) (1), [BuildingManager](BuildingManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1)

**Used by (19):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [ElectrolyserCheck](ElectrolyserCheck.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [NewUnitCheck](NewUnitCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [ScaleCheck](ScaleCheck.md), [ServicesManager](ServicesManager.md), [SubstationCheck](SubstationCheck.md), [TownGridCheck](TownGridCheck.md), [UtilitiesHandler](UtilitiesHandler.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 15 | `Substations.ID` | `155` | Permanent catalogue id, appended after Hydrogen Steel Works (PW5). |
| 17 | `Substations.SUBSTATION_MW` | `100` | MW carried by one district's lines, transformers and meters (spec 2.7's recommended size). |
| 19 | `Substations.DISTRIBUTION_ID` | `173` | Permanent catalogue id of the distribution substation, appended after the Small Steam Station (ASH1). |
| 21 | `Substations.DISTRIBUTION_MW` | `5` | MW carried by one city distribution substation, as the 1924 Portland (3,000 kW, 6,000 planned) and Midwest (3 x 1,000 kVA) stations. |
| 23 | `Substations.REAL_RATE` | `.04` | Real annual cost of capital in the study's grid calculation (spec 2.7). |
| 25 | `Substations.LIFE_YEARS` | `40` | Years of a district grid's capital life (spec 2.7). |
| 27 | `Substations.TRANSMISSION_SHARE` | `PowerDispatch.TRANSMISSION_A_KWH /(PowerDispatch.TRANSMISSION_A_KWH + PowerDi...` | Transmission's share of the grid's costs, AEO2026 table 8, as in PW1's two billing classes. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 70 | **type** `public final class Substations` | The city's wires: the district Substation (100 MW) and the small distribution substation (5 MW), and their own costs in the power bill. |
| 12 | 1 | `private Substations()` |  |
| 31 | 1 | `public static boolean isSubstation(BuildingsTemplate t)` | A grid unit, the district Substation or a distribution substation. |
| 34 | 5 | `public static double unitKw(BuildingsTemplate t)` | The kW one unit of this template carries; 0 for anything that is not a grid unit. |
| 44 | 3 | `public static double carryKw(double foundingKw, long substations, long distribution)` | The kW the wires carry. |
| 49 | 4 | **type** `public record Costs(long count, double carryKw, double capital, double repairs, double payroll)` | One dispatch's monthly costs, in city money; capital is recovery, never a second cash payment. |
| 50 | 1 | `public double total()` _(in Substations.Costs)_ |  |
| 51 | 1 | `Costs scaled(double scale)` _(in Substations.Costs)_ |  |
| 55 | 22 | `public static Costs costs(Game game)` | Reads the same replacement cost, builders' tax, repairs rate and filled payroll as the city's books. |
| 79 | 1 | `public static double peak(double drawKw, double season)` | Peak kW asked at the plants, including the season and the existing grid losses. |

