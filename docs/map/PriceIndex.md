# PriceIndex.java - 835 lines · 42 methods · 15 constants · model

`ham/citybuildersim/PriceIndex.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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

**Used by (30):** [BankScreen](BankScreen.md), [CentralBankCheck](CentralBankCheck.md), [CentralBankScreen](CentralBankScreen.md), [ChartModel](ChartModel.md), [CityNeeds](CityNeeds.md), [CpiCoverageFinding](CpiCoverageFinding.md), [CpiReferenceFinding](CpiReferenceFinding.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [Expectations](Expectations.md), [ExpectationsCheck](ExpectationsCheck.md), [Game](Game.md), [GroceryCheck](GroceryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [PassThroughCheck](PassThroughCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [ReloadCheck](ReloadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [StepsCheck](StepsCheck.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 60 | WHAT IS IN THE BASKET (0.7.43; the project's spec-inflation.md 2.8) |
| 179 | THE HIGH AND LOW WATER MARKS |
| 343 | THE BASKET IS CHAINED (0.7.43) |
| 623 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `PriceIndex.WINDOW` | `13` | Months of index kept, so a year-on-year rate can be struck. |
| 39 | `PriceIndex.MIN_BASE` | `1e-9` | Below this the basket is not worth pricing - a city with no shops. |
| 58 | `PriceIndex.SETTLING_MONTHS` | `24` | Months of real shopping before the basket is fixed. |
| 93 | `PriceIndex.GROCERIES` | `0, RENT = 1, MEALS = 2, LUXURY = 3, SERVICES = 4, NEW_CARS = 5, PETROL = 6, L...` | The components, in the order every array here keeps them. |
| 96 | `PriceIndex.COMPONENTS` | `9` | How many there are. |
| 99 | `PriceIndex.COMPONENT_NAMES` | `{ "groceries", "rent", "meals", "luxury", "services", "new cars", "petrol", "...` | Their names, for the year book and the screens. |
| 102 | `PriceIndex.HEALTH_FEE` | `0, TUITION = 1, FARE = 2, ACCOUNT_FEE = 3` | The fee lines inside SERVICES, in the order the fee arrays here keep them. |
| 105 | `PriceIndex.FEE_LINES` | `4` | How many there are. |
| 108 | `PriceIndex.WEIGHT_MONTHS` | `12` | Months of spending a basket's weights are struck on: the trailing year. |
| 111 | `PriceIndex.REBASE_MONTHS` | `120` | Months between one basket and the next: ten years. |
| 115 | `PriceIndex.LUXURY_WEIGHT_CAP` | `.15` | Historical display API only; no expenditure cap is applied. |
| 154 | `PriceIndex.RENT_CAP` | `1 / 3.0` | The most of the basket rent may be in the level the currency's parity is struck from: a third, about its share in real consumer price indexes (Jerus, 2026-10-21, XR1, "Basket, rent capped"). |
| 626 | `PriceIndex.SLOTS_BEFORE_CHAIN` | `5 + WINDOW + 5` | The slots a save carried before the chained basket (0.7.43): the two-component basket, the ring, the marks and the settling count. |
| 629 | `PriceIndex.CHAIN_MARKER` | `743` | What follows them in a save from 0.7.43 on, so an older array - which ends there - is told apart from one that goes on. |
| 632 | `PriceIndex.CHAIN_TAIL_0743` | `4 + COMPONENTS * 3 + FEE_LINES * 3 + 1 + WEIGHT_MONTHS *(COMPONENTS + FEE_LINES)` | The chained basket's slots behind the marker as 0.7.43 wrote them: the link, its month, the pending flag, the bases, weights and last prices, the fee lines', and the trailing year. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 117 | `private int shoppingMonths` |  |
| 120 | `private double baseFood, baseRent` | The two-component basket a save from before 0.7.43 carried: read once, to strike the level the first chain-link starts from. |
| 121 | `private double foodWeight` |  |
| 122 | `private boolean based` |  |
| 125 | `private final double[] base` | Each component's price at the link, its weight, its last price seen, and its price over its base this month. |
| 126 | `private final double[] weight` |  |
| 127 | `private final double[] last` |  |
| 128 | `private final double[] relative` |  |
| 131 | `private double link` | The level the basket was linked at - 1 for a city's first - and the month it was. |
| 132 | `private int linkedAt` |  |
| 135 | `private boolean linkPending` | True on a save from before 0.7.43 until its first month links the basket. |
| 143 | `private final double[] componentLink` | Each component's own level at the link, chained (0.7.45): what its relative is multiplied by to give its price against founding, unbroken across every link (getComponentLevel()). |
| 163 | `private double parityLink` | The level the capped basket was linked at (XR1): the capped level on the basket just left at this month's prices, or the index's own level at the first basing and on a link pending, so the capped level runs on unbroke... |
| 166 | `private final double[] feeBase` | Each fee line's price at the link, its share of the fees then, and its last price seen. |
| 167 | `private final double[] feeShare` |  |
| 168 | `private final double[] feeLast` |  |
| 171 | `private final double[][] spendRing` | The trailing year of what the households spent on each component, and on each fee line, as rings; how many months they hold. |
| 172 | `private final double[][] feeRing` |  |
| 173 | `private int ringMonths` |  |
| 175 | `private double index` |  |
| 176 | `private final double[] history` |  |
| 177 | `private int monthsSeen` |  |
| 203 | `private double peak` |  |
| 204 | `private int peakMonth, troughMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 803 | **type** `public class PriceIndex` | What a month costs a household, against what it cost at founding. |

### WHAT IS IN THE BASKET (0.7.43; the project's spec-inflation.md 2.8) (lines 60-178)

### THE HIGH AND LOW WATER MARKS (lines 179-342)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 6 | `public void takeMonth(double shelfPrice, double rentPrice, double foodSpend, double rentSpend, int month)` | Prices the basket for the month: groceries and rent alone, the two components the index had until 0.7.43 - meals, luxury and services unpriced and unspent on. |
| 236 | 106 | `public void takeMonth(double[] prices, double[] spends, double[] fees, double[] feeSpends, int month)` | Prices the basket for the month. |

### THE BASKET IS CHAINED (0.7.43) (lines 343-622)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 7 | `static boolean goodsBought(double[] prices, double[] spends)` | Whether the month bought a good beside groceries and rent at a price (HG1): meals, luxury, new cars, petrol, lamp oil or home coal - the half of a basing that carries prices from outside the rent-wage loop when the ci... |
| 376 | 5 | `private boolean groceriesJoin()` | Whether groceries join the basket this month (HG1): it was struck with none in its weights, and now every month of the trailing year (WEIGHT_MONTHS) bought them at a price - so they come in at a whole year's weight, l... |
| 382 | 11 | `private double levelOnThisBasket()` |  |
| 400 | 10 | `private double[] cappedWeights()` | The weights of the basket in force with rent held to RENT_CAP (XR1): rent at the cap and every other weight scaled so the components add to what they did - when rent weighs more than the cap and the others weigh somet... |
| 412 | 10 | `private double cappedLevelOnThisBasket()` | The level this month on the basket in force with rent capped: parityLink times each component's relative, weighted by cappedWeights() - parityLink with no weights yet. |
| 424 | 9 | `private double feeLevel()` | The services price: each fee line's price over its price at the link, weighted by its share of the fees then - 1 at the link, and with no fees, for ever. |
| 439 | 68 | `private void relink(int month, double level)` | Strikes a new basket: the weights from the trailing year's spending (this month's alone with no year yet), each component based at its price now - and the link, the level the basket opens at. |
| 509 | 1 | `public double getPeak()` | The dearest the basket has ever been, against founding. |
| 511 | 1 | `public int getPeakMonth()` | ...and the month it happened. |
| 513 | 1 | `public double getTrough()` | The cheapest it has ever been. |
| 514 | 1 | `public int getTroughMonth()` |  |
| 523 | 1 | `public double swing()` | Peak over trough - how far the level has travelled, in one number. |
| 526 | 1 | `public double getIndex()` | The basket now, against the basket at founding. |
| 528 | 1 | `public boolean isBased()` |  |
| 531 | 1 | `public double getFoodWeight()` | How the basket is split: groceries and rent, as the basket in force weighs them - fixed at a link, re-weighted only at the next (0.7.43; never until then). |
| 532 | 1 | `public double getRentWeight()` |  |
| 535 | 1 | `public double getWeight(int component)` | One component's weight in the basket in force (COMPONENTS order). |
| 538 | 1 | `public double getRelative(int component)` | One component's price this month over its price at the link: 1 at the link, and for a component not in the basket. |
| 541 | 1 | `public double getBase(int component)` | One component's price at the link (the services' fee index: 1). |
| 544 | 1 | `public double getComponentPrice(int component)` | The price used in the current relative, for comparisons at a common physical basket. |
| 547 | 1 | `public double getLink()` | The level the basket in force was linked at: 1 for a city's first basket. |
| 556 | 1 | `public double getParityLevel()` | The currency's parity level (XR1): the basket with rent held to RENT_CAP, chained and priced as the index is - the level the capped basket was linked at times each component's price over its base, weighted by getParit... |
| 559 | 1 | `public double getParityWeight(int component)` | One component's weight in the basket parity is struck from: the basket in force with rent held to RENT_CAP (COMPONENTS order; they add to what the basket's do). |
| 562 | 1 | `public boolean isRentCapped()` | True when the basket in force has rent over RENT_CAP, so the level parity is struck from holds it down to the cap (XR1). |
| 565 | 1 | `public int getLinkedMonth()` | The month the basket in force was linked: the base month, or the last chain-link. |
| 568 | 1 | `public double getFeeShare(int line)` | One fee line's share of the services price (FEE_LINES order). |
| 571 | 1 | `public double getFeeBase(int line)` | One fee's price at the link, for comparisons with the same quantities across baskets. |
| 574 | 1 | `public double getFeePrice(int line)` | One fee's latest price used by the index. |
| 577 | 1 | `public boolean isLinkPending()` | True on a save from before 0.7.43 until its first month links the current basket. |
| 585 | 1 | `public double getComponentLevel(int component)` | One component's own price level against founding, chained across every link (0.7.45; COMPONENTS order): its level at the link times its relative - so a basket struck again does not reset it to 1, as the relative does. |
| 588 | 1 | `public boolean isLuxuryCapped()` | Historical display API: actual reference expenditures are never capped. |
| 599 | 6 | `public double inflation()` | Inflation over the last twelve months. |
| 607 | 1 | `public boolean hasRate()` | True once there is a year of readings and the rate means anything. |
| 617 | 5 | `public int monthsUntilRate()` | How many more months before hasRate() (0.7.20): the settling months still to come, then the readings after the basket is fixed - for the header, which says when the first rate comes rather than printing a placeholder ... |

### carrying (lines 623-835)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 50 | `public double[] toSaveArray()` |  |
| 685 | 46 | `public void restore(double[] saved)` |  |
| 733 | 38 | `private void restoreChain(double[] saved)` | The chained basket behind the marker, or a link pending on an older save; the array is read whole or not at all. |
| 773 | 17 | `private void clearChain()` | Nothing linked, nothing spent: a basket still to be struck. |
| 791 | 12 | `public void reset()` |  |
| 817 | 17 | `public void redenominate(double scale)` | The basket's base prices, in the new unit. |

