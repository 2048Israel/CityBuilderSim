# Formats.java - 56 lines · 6 methods · 1 constants · model

`ham/citybuildersim/Formats.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The few formats a sector needs to describe itself, without the toolkit.
> 
> Sector.operations() hands the screens lines of text, and a sector class
> must not import JavaFX to write them. These are the same shapes
> UserInterface uses - money in dollars from a field in thousands, a
> percentage, a count with its unit - kept here so both sides print a tonne
> the same way.

**Uses:** [Good](Good.md) (1)

**Used by (16):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BusinessServices](BusinessServices.md), [Construction](Construction.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [Mining](Mining.md), [Rail](Rail.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [TradeCostCheck](TradeCostCheck.md)

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
| 15 | 42 | **type** `public final class Formats` | The few formats a sector needs to describe itself, without the toolkit. |
| 22 | 4 | `private Formats()` |  |
| 28 | 6 | `public String cash(double thousands)` | A field in thousands, as dollars: 0.12 is "$120.00". |
| 35 | 4 | `public String pct(double share)` |  |
| 40 | 4 | `public String count(double n)` |  |
| 46 | 5 | `public String units(double n, Good g)` | "1,200 tonnes", "1 unit". |
| 52 | 4 | `private static String plural(String unit)` |  |

