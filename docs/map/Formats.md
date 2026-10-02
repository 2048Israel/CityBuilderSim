# Formats.java - 106 lines · 9 methods · 1 constants · model

`ham/citybuildersim/Formats.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The few formats a sector needs to describe itself, without the toolkit.
> 
> Sector.operations() hands the screens lines of text, and a sector class
> must not import JavaFX to write them. These are the same shapes
> UserInterface uses - money in dollars from a field in thousands, a
> percentage, a count with its unit - kept here so both sides print a tonne
> the same way.

**Uses:** [Good](Good.md) (1)

**Used by (32):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [CityNeeds](CityNeeds.md), [Construction](Construction.md), [DecisionLog](DecisionLog.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [Mining](Mining.md), [Money](Money.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [Rail](Rail.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [TradeCostCheck](TradeCostCheck.md), [YearBook](YearBook.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `Formats.INSTANCE` | `new Formats()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `private final NumberFormat whole` |  |
| 20 | `private final NumberFormat money` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 92 | **type** `public final class Formats` | The few formats a sector needs to describe itself, without the toolkit. |
| 22 | 4 | `private Formats()` |  |
| 28 | 6 | `public String cash(double thousands)` | A field in thousands, as dollars: 0.12 is "$120.00". |
| 51 | 10 | `public String amount(double thousands)` | The same field as the screens print money (0.7.20), Money.unitPrice()'s shape: cents only under a thousand dollars, where a price a unit or a point needs them; whole dollars from there to a million; a million or more ... |
| 63 | 3 | `private static String sign(double value, double shown, int decimals)` | "-" for a negative that still reads as something at `decimals` places, "" otherwise. |
| 75 | 8 | `public String rate(double rate)` | An exchange rate - local money per US dollar, a ratio and not money, so it is never read as thousands - written as every screen writes one (Money.fxRate() is this since 0.7.26, so the land office's receipt, written in... |
| 84 | 4 | `public String pct(double share)` |  |
| 89 | 4 | `public String count(double n)` |  |
| 95 | 5 | `public String units(double n, Good g)` | "1,200 tonnes", "1 unit". |
| 102 | 4 | `public static String plural(String unit)` | "tonnes", "units" - and "kg", which is its own plural (0.7.20). |

