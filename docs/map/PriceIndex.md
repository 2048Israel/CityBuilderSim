# PriceIndex.java - 296 lines · 16 methods · 3 constants · model

`ham/citybuildersim/PriceIndex.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> What a month costs a household, against what it cost at founding.
> 
> WHY THIS EXISTS. LabourMarket.updateCostOfLiving() was being handed the
> EXCHANGE RATE as a stand-in for a price index - a placeholder written in
> phase 2 and flagged as one, on the reasoning that the world's own prices do
> not move so the rate is the only thing changing what an import costs. That
> was true while imports were the only price in the game that moved. It stopped
> being true the moment the shelf price became cost-plus, and it was never true
> of rent, which follows the unskilled wage and therefore follows wages
> chasing... the exchange rate. A loop with a placeholder in it.
> 
> A REAL INDEX IS A FIXED BASKET, PRICED REPEATEDLY. That is the whole idea and
> it is the part people get wrong: you do not re-weight as spending shifts,
> because then a household that switched to cheaper food would show no
> inflation while eating worse. The basket is fixed at founding and priced
> every month afterwards.
> 
> THE BASKET IS MEASURED, NOT INVENTED. The weights come from what this game's
> households actually spent in the month the index was based - food against
> rent - rather than from a constant somebody picked. A city whose families
> spend two thirds of their money on food has a food-weighted index, and it
> should, because that is whose cost of living this is.

**Used by (8):** [CurrencyCheck](CurrencyCheck.md), [Game](Game.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 66 | THE HIGH AND LOW WATER MARKS |
| 206 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 35 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 54 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `private int shoppingMonths` |  |
| 58 | `private double baseFood, baseRent` |  |
| 59 | `private double foodWeight` |  |
| 60 | `private boolean based` |  |
| 62 | `private double index` |  |
| 63 | `private final double[] history` |  |
| 64 | `private int monthsSeen` |  |
| 90 | `private double peak` |  |
| 91 | `private int peakMonth, troughMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 268 | **type** `public class PriceIndex` | What a month costs a household, against what it cost at founding. |

### THE HIGH AND LOW WATER MARKS (lines 66-205)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 58 | `public void takeMonth(double shelfPrice, double rentPrice, double foodSpend, double rentSpend, int month)` | Prices the basket for the month. |
| 162 | 1 | `public double getPeak()` | The dearest the basket has ever been, against founding. |
| 164 | 1 | `public int getPeakMonth()` | ...and the month it happened. |
| 166 | 1 | `public double getTrough()` | The cheapest it has ever been. |
| 167 | 1 | `public int getTroughMonth()` |  |
| 176 | 1 | `public double swing()` | Peak over trough - how far the level has travelled, in one number. |
| 179 | 1 | `public double getIndex()` | The basket now, against the basket at founding. |
| 181 | 1 | `public boolean isBased()` |  |
| 184 | 1 | `public double getFoodWeight()` | How the basket is split. |
| 185 | 1 | `public double getRentWeight()` |  |
| 196 | 6 | `public double inflation()` | Inflation over the last twelve months. |
| 204 | 1 | `public boolean hasRate()` | True once there is a year of readings and the rate means anything. |

### carrying (lines 206-296)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 24 | `public double[] toSaveArray()` |  |
| 233 | 35 | `public void restore(double[] saved)` |  |
| 269 | 11 | `public void reset()` |  |
| 289 | 6 | `public void redenominate(double scale)` | The basket's base prices, in the new unit. |

