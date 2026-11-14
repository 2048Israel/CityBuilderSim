# Formats.java - 165 lines · 12 methods · 3 constants · model

`ham/citybuildersim/Formats.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The few formats a sector needs to describe itself, without the toolkit.
> 
> Sector.operations() hands the screens lines of text, and a sector class
> must not import JavaFX to write them. These are the same shapes
> UserInterface uses - money in dollars from a field in thousands, a
> percentage, a count with its unit - kept here so both sides print a tonne
> the same way.

**Uses:** [Currency](Currency.md) (4), [Good](Good.md) (1)

**Used by (57):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [CarCheck](CarCheck.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [Construction](Construction.md), [CopperMining](CopperMining.md), [CopperRefining](CopperRefining.md), [DecisionLog](DecisionLog.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FloorView](FloorView.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Forestry](Forestry.md), [FormatsCheck](FormatsCheck.md), [Freight](Freight.md), [FundScreen](FundScreen.md), [Game](Game.md), [HeavyIndustry](HeavyIndustry.md), [HistoryScreen](HistoryScreen.md), [IndustryView](IndustryView.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [Money](Money.md), [MoneySignCheck](MoneySignCheck.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md), [Oil](Oil.md), [OilView](OilView.md), [Pieces](Pieces.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [Quarrying](Quarrying.md), [RealEstate](RealEstate.md), [RefineryView](RefineryView.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [TradeCostCheck](TradeCostCheck.md), [WellCheck](WellCheck.md), [YearBook](YearBook.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md)

## Sections

| line | section |
|---:|---|
| 108 | · readable percentages and changes (SX1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |
| 20 | `Formats.LARGE_PERCENT` | `10` | Past this absolute proportion (1,000%), show a multiple or a compound yearly change (SX1). |
| 38 | `Formats.WHOLE_DOLLARS_MOST` | `0x1p53` | The most dollars cash() prints whole (0.7.54): 2^53, about nine quadrillion, the last whole number a double holds exactly. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `private final NumberFormat whole` |  |
| 23 | `private final NumberFormat money` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 15 | 151 | **type** `public final class Formats` | The few formats a sector needs to describe itself, without the toolkit. |
| 25 | 4 | `private Formats()` |  |
| 41 | 7 | `public String cash(double thousands)` | A field in thousands, as dollars: 0.12 is "$120.00"; past WHOLE_DOLLARS_MOST, amount()'s "$1,300.0Q". |
| 69 | 12 | `public String amount(double thousands)` | The same field as the screens print money (0.7.20), Money.unitPrice()'s shape: cents only under a thousand dollars, where a price a unit or a point needs them; whole dollars from there to a million; a million or more ... |
| 83 | 3 | `private static String sign(double value, double shown, int decimals)` | "-" for a negative that still reads as something at `decimals` places, "" otherwise. |
| 95 | 8 | `public String rate(double rate)` | An exchange rate - local money per US dollar, a ratio and not money, so it is never read as thousands - written as every screen writes one (Money.fxRate() is this since 0.7.26, so the land office's receipt, written in... |
| 104 | 3 | `public String pct(double share)` |  |

### readable percentages and changes (SX1) (lines 108-165)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 4 | `public String percentage(double share, String pattern)` | A proportion, with the caller's ordinary precision; large ratios are multiples of the base. |
| 122 | 10 | `public String change(double gain, int months, String pattern)` | A relative change: a multiple includes the starting value (a 1,100% gain is ×12). |
| 134 | 13 | `private String multiple(double ratio)` | A dimensionless multiple with three significant figures and a named scale, never a wall of digits. |
| 148 | 4 | `public String count(double n)` |  |
| 154 | 5 | `public String units(double n, Good g)` | "1,200 tonnes", "1 unit". |
| 161 | 4 | `public static String plural(String unit)` | "tonnes", "units" - and "kg", which is its own plural (0.7.20). |

