# Substations.java - 51 lines · 6 methods · 5 constants · model

`ham/citybuildersim/Substations.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The city's wires: one substation for each 100 MW of peak, and their own costs in the power bill.
> WHY. PW1 charged a flat 5.94 cents a kWh for wires that nothing built or maintained.
> PW5 (spec-electricity 2.7) uses real city buildings, paid through the existing construction,
> repair and utility payroll books; the tariff recovers those costs for their owner, the treasury.

**Uses:** [PowerDispatch](PowerDispatch.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [JobType](JobType.md) (2), [Game](Game.md) (1), [BuildingManager](BuildingManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1)

**Used by (16):** [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [ElectrolyserCheck](ElectrolyserCheck.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [LongPlaytest](LongPlaytest.md), [NewUnitCheck](NewUnitCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [ScaleCheck](ScaleCheck.md), [ServicesManager](ServicesManager.md), [SubstationCheck](SubstationCheck.md), [UtilitiesHandler](UtilitiesHandler.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 13 | `Substations.ID` | `155` | Permanent catalogue id, appended after Hydrogen Steel Works (PW5). |
| 15 | `Substations.SUBSTATION_MW` | `100` | MW carried by one district's lines, transformers and meters (spec 2.7's recommended size). |
| 17 | `Substations.REAL_RATE` | `.04` | Real annual cost of capital in the study's grid calculation (spec 2.7). |
| 19 | `Substations.LIFE_YEARS` | `40` | Years of a district grid's capital life (spec 2.7). |
| 21 | `Substations.TRANSMISSION_SHARE` | `PowerDispatch.TRANSMISSION_A_KWH /(PowerDispatch.TRANSMISSION_A_KWH + PowerDi...` | Transmission's share of the grid's costs, AEO2026 table 8, as in PW1's two billing classes. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 43 | **type** `public final class Substations` | The city's wires: one substation for each 100 MW of peak, and their own costs in the power bill. |
| 10 | 1 | `private Substations()` |  |
| 24 | 1 | `public static boolean isSubstation(BuildingsTemplate t)` |  |
| 27 | 4 | **type** `public record Costs(long count, double carryKw, double capital, double repairs, double payroll)` | One dispatch's monthly costs, in city money; capital is recovery, never a second cash payment. |
| 28 | 1 | `public double total()` _(in Substations.Costs)_ |  |
| 29 | 1 | `Costs scaled(double scale)` _(in Substations.Costs)_ |  |
| 33 | 15 | `public static Costs costs(Game game)` | Reads the same replacement cost, builders' tax, repairs rate and filled payroll as the city's books. |
| 50 | 1 | `public static double peak(double drawKw, double season)` | Peak kW asked at the plants, including the season and the existing grid losses. |

