# PriceIndex.java - 694 lines · 32 methods · 14 constants · model

`ham/citybuildersim/PriceIndex.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

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
> inflation while eating worse. The basket is fixed - after SETTLING_MONTHS
> of real shopping, not at founding (see there) - and priced every month
> afterwards, for REBASE_MONTHS; then it is struck again on what the city
> spends by then and CHAINED to the old one, so the level runs on unbroken
> (0.7.43; for good until then - see THE BASKET IS CHAINED below).
> 
> THE BASKET IS MEASURED, NOT INVENTED. The weights come from what this game's
> households actually spent - over the trailing twelve months since 0.7.43,
> in the base month alone until then - rather than from a constant somebody
> picked. A city whose families spend two thirds of their money on food has
> a food-weighted index, and it should, because that is whose cost of living
> this is.

**Used by (18):** [ChartModel](ChartModel.md), [CityNeeds](CityNeeds.md), [CurrencyCheck](CurrencyCheck.md), [Expectations](Expectations.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [GroceryCheck](GroceryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 60 | WHAT IS IN THE BASKET (0.7.43; the project's spec-inflation.md 2.8) |
| 153 | THE HIGH AND LOW WATER MARKS |
| 302 | THE BASKET IS CHAINED (0.7.43) |
| 497 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 39 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 58 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |
| 88 | `PriceIndex.GROCERIES` | `0, RENT = 1, MEALS = 2, LUXURY = 3, SERVICES = 4` | The components, in the order every array here keeps them. |
| 91 | `PriceIndex.COMPONENTS` | `5` | How many there are. |
| 94 | `PriceIndex.COMPONENT_NAMES` | `{ "groceries", "rent", "meals", "luxury", "services" }` | Their names, for the year book and the screens. |
| 97 | `PriceIndex.HEALTH_FEE` | `0, TUITION = 1, FARE = 2, ACCOUNT_FEE = 3` | The fee lines inside SERVICES, in the order the fee arrays here keep them. |
| 100 | `PriceIndex.FEE_LINES` | `4` | How many there are. |
| 103 | `PriceIndex.WEIGHT_MONTHS` | `12` | Months of spending a basket's weights are struck on: the trailing year. |
| 106 | `PriceIndex.REBASE_MONTHS` | `120` | Months between one basket and the next: ten years. |
| 109 | `PriceIndex.LUXURY_WEIGHT_CAP` | `.15` | The most of the basket luxury may be, the excess spread pro rata over the rest: 15%. |
| 500 | `PriceIndex.SLOTS_BEFORE_CHAIN` | `5 + WINDOW + 5` | The slots a save carried before the chained basket (0.7.43): the two-component basket, the ring, the marks and the settling count. |
| 503 | `PriceIndex.CHAIN_MARKER` | `743` | What follows them in a save from 0.7.43 on, so an older array - which ends there - is told apart from one that goes on. |
| 506 | `PriceIndex.CHAIN_TAIL_0743` | `4 + COMPONENTS * 3 + FEE_LINES * 3 + 1 + WEIGHT_MONTHS *(COMPONENTS + FEE_LINES)` | The chained basket's slots behind the marker as 0.7.43 wrote them: the link, its month, the pending flag, the bases, weights and last prices, the fee lines', and the trailing year. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 111 | `private int shoppingMonths` |  |
| 114 | `private double baseFood, baseRent` | The two-component basket a save from before 0.7.43 carried: read once, to strike the level the first chain-link starts from. |
| 115 | `private double foodWeight` |  |
| 116 | `private boolean based` |  |
| 119 | `private final double[] base` | Each component's price at the link, its weight, its last price seen, and its price over its base this month. |
| 120 | `private final double[] weight` |  |
| 121 | `private final double[] last` |  |
| 122 | `private final double[] relative` |  |
| 125 | `private double link` | The level the basket was linked at - 1 for a city's first - and the month it was. |
| 126 | `private int linkedAt` |  |
| 129 | `private boolean linkPending` | True on a save from before 0.7.43 until its first month links the basket. |
| 137 | `private final double[] componentLink` | Each component's own level at the link, chained (0.7.45): what its relative is multiplied by to give its price against founding, unbroken across every link (getComponentLevel()). |
| 140 | `private final double[] feeBase` | Each fee line's price at the link, its share of the fees then, and its last price seen. |
| 141 | `private final double[] feeShare` |  |
| 142 | `private final double[] feeLast` |  |
| 145 | `private final double[][] spendRing` | The trailing year of what the households spent on each component, and on each fee line, as rings; how many months they hold. |
| 146 | `private final double[][] feeRing` |  |
| 147 | `private int ringMonths` |  |
| 149 | `private double index` |  |
| 150 | `private final double[] history` |  |
| 151 | `private int monthsSeen` |  |
| 177 | `private double peak` |  |
| 178 | `private int peakMonth, troughMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 662 | **type** `public class PriceIndex` | What a month costs a household, against what it cost at founding. |

### WHAT IS IN THE BASKET (0.7.43; the project's spec-inflation.md 2.8) (lines 60-152)

### THE HIGH AND LOW WATER MARKS (lines 153-301)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 6 | `public void takeMonth(double shelfPrice, double rentPrice, double foodSpend, double rentSpend, int month)` | Prices the basket for the month: groceries and rent alone, the two components the index had until 0.7.43 - meals, luxury and services unpriced and unspent on. |
| 210 | 91 | `public void takeMonth(double[] prices, double[] spends, double[] fees, double[] feeSpends, int month)` | Prices the basket for the month. |

### THE BASKET IS CHAINED (0.7.43) (lines 302-496)

| line | len | member | says |
|---:|---:|---|---|
| 317 | 11 | `private double levelOnThisBasket()` | The level this month on the basket in force: the link times each component's price over its base, weighted. |
| 330 | 9 | `private double feeLevel()` | The services price: each fee line's price over its price at the link, weighted by its share of the fees then - 1 at the link, and with no fees, for ever. |
| 345 | 60 | `private void relink(int month, double level)` | Strikes a new basket: the weights from the trailing year's spending (this month's alone with no year yet), luxury's capped, each component based at its price now - and the link, the level the basket opens at. |
| 407 | 1 | `public double getPeak()` | The dearest the basket has ever been, against founding. |
| 409 | 1 | `public int getPeakMonth()` | ...and the month it happened. |
| 411 | 1 | `public double getTrough()` | The cheapest it has ever been. |
| 412 | 1 | `public int getTroughMonth()` |  |
| 421 | 1 | `public double swing()` | Peak over trough - how far the level has travelled, in one number. |
| 424 | 1 | `public double getIndex()` | The basket now, against the basket at founding. |
| 426 | 1 | `public boolean isBased()` |  |
| 429 | 1 | `public double getFoodWeight()` | How the basket is split: groceries and rent, as the basket in force weighs them - fixed at a link, re-weighted only at the next (0.7.43; never until then). |
| 430 | 1 | `public double getRentWeight()` |  |
| 433 | 1 | `public double getWeight(int component)` | One component's weight in the basket in force (COMPONENTS order). |
| 436 | 1 | `public double getRelative(int component)` | One component's price this month over its price at the link: 1 at the link, and for a component not in the basket. |
| 439 | 1 | `public double getBase(int component)` | One component's price at the link (the services' fee index: 1). |
| 442 | 1 | `public double getLink()` | The level the basket in force was linked at: 1 for a city's first basket. |
| 445 | 1 | `public int getLinkedMonth()` | The month the basket in force was linked: the base month, or the last chain-link. |
| 448 | 1 | `public double getFeeShare(int line)` | One fee line's share of the services price (FEE_LINES order). |
| 451 | 1 | `public boolean isLinkPending()` | True on a save from before 0.7.43 until its first month links the five-component basket. |
| 459 | 1 | `public double getComponentLevel(int component)` | One component's own price level against founding, chained across every link (0.7.45; COMPONENTS order): its level at the link times its relative - so a basket struck again does not reset it to 1, as the relative does. |
| 462 | 1 | `public boolean isLuxuryCapped()` | True when the basket in force had luxury's weight held at LUXURY_WEIGHT_CAP - what the households spent on it was more (0.7.45). |
| 473 | 6 | `public double inflation()` | Inflation over the last twelve months. |
| 481 | 1 | `public boolean hasRate()` | True once there is a year of readings and the rate means anything. |
| 491 | 5 | `public int monthsUntilRate()` | How many more months before hasRate() (0.7.20): the settling months still to come, then the readings after the basket is fixed - for the header, which says when the first rate comes rather than printing a placeholder ... |

### carrying (lines 497-694)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 45 | `public double[] toSaveArray()` |  |
| 554 | 43 | `public void restore(double[] saved)` |  |
| 599 | 32 | `private void restoreChain(double[] saved)` | The chained basket behind the marker, or a link pending on an older save; the array is read whole or not at all. |
| 633 | 16 | `private void clearChain()` | Nothing linked, nothing spent: a basket still to be struck. |
| 650 | 12 | `public void reset()` |  |
| 676 | 17 | `public void redenominate(double scale)` | The basket's base prices, in the new unit. |

