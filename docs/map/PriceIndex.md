# PriceIndex.java - 313 lines · 17 methods · 3 constants · model

`ham/citybuildersim/PriceIndex.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
> inflation while eating worse. The basket is fixed once - after
> SETTLING_MONTHS of real shopping, not at founding (see there) - and priced
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
| 67 | THE HIGH AND LOW WATER MARKS |
| 223 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 36 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 55 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private int shoppingMonths` |  |
| 59 | `private double baseFood, baseRent` |  |
| 60 | `private double foodWeight` |  |
| 61 | `private boolean based` |  |
| 63 | `private double index` |  |
| 64 | `private final double[] history` |  |
| 65 | `private int monthsSeen` |  |
| 91 | `private double peak` |  |
| 92 | `private int peakMonth, troughMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 284 | **type** `public class PriceIndex` | What a month costs a household, against what it cost at founding. |

### THE HIGH AND LOW WATER MARKS (lines 67-222)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 60 | `public void takeMonth(double shelfPrice, double rentPrice, double foodSpend, double rentSpend, int month)` | Prices the basket for the month. |
| 165 | 1 | `public double getPeak()` | The dearest the basket has ever been, against founding. |
| 167 | 1 | `public int getPeakMonth()` | ...and the month it happened. |
| 169 | 1 | `public double getTrough()` | The cheapest it has ever been. |
| 170 | 1 | `public int getTroughMonth()` |  |
| 179 | 1 | `public double swing()` | Peak over trough - how far the level has travelled, in one number. |
| 182 | 1 | `public double getIndex()` | The basket now, against the basket at founding. |
| 184 | 1 | `public boolean isBased()` |  |
| 187 | 1 | `public double getFoodWeight()` | How the basket is split. |
| 188 | 1 | `public double getRentWeight()` |  |
| 199 | 6 | `public double inflation()` | Inflation over the last twelve months. |
| 207 | 1 | `public boolean hasRate()` | True once there is a year of readings and the rate means anything. |
| 217 | 5 | `public int monthsUntilRate()` | How many more months before hasRate() (0.7.20): the settling months still to come, then the readings after the basket is fixed - for the header, which says when the first rate comes rather than printing a placeholder ... |

### carrying (lines 223-313)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 24 | `public double[] toSaveArray()` |  |
| 250 | 35 | `public void restore(double[] saved)` |  |
| 286 | 11 | `public void reset()` |  |
| 306 | 6 | `public void redenominate(double scale)` | The basket's base prices, in the new unit. |

