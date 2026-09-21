# LandManager.java - 444 lines · 41 methods · 5 constants · model

`ham/citybuildersim/LandManager.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The city's land: what it owns, what is built on, and what it sells.
> 
> This is the piece that changes what the player's job is. Before it, buildings
> appeared wherever they were wanted and the only limits were cash, materials
> and construction capacity - all of which the private sector eventually
> supplies for itself. Land is the one input only the city controls, so it is
> the lever that makes a city government necessary rather than decorative.
> 
> THREE NUMBERS
> 
>   owned       every square foot the city has annexed
>   allocated   what is standing on, or being built on
>   available   the difference - what can still be built on
> 
> Land is never freed, because buildings are never demolished. When demolition
> exists, release() is where it hooks in.
> 
> BUYING AND SELLING
> 
> The city buys from outside in blocks, at a price that rises as it expands -
> annexing further out costs more, and it stops "buy everything immediately"
> from being the obvious move. It sells to businesses at a price the player
> sets, and the spread between the two is the city's margin. Setting the price
> too high prices businesses out of building at all, which is a real decision
> rather than a free revenue dial.
> 
> The city does not pay itself for land it builds on: it already owns it, and
> charging its own budget would just move money from one pocket to the other
> while inflating GDP.

**Uses:** [LandParcel](LandParcel.md) (4), [LandMarket](LandMarket.md) (3)

**Used by (11):** [BuildScreen](BuildScreen.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [ReadPathCheck](ReadPathCheck.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 125 | · ore |
| 181 | · the market |
| 231 | · ore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 53 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres. |
| 85 | `LandManager.COST_GROWTH_PER_BLOCK` | `.02` | Each block bought makes the next this much dearer - annexing outward. |
| 97 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 416 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 100 | `private double defaultPricePerSqFt` | The same, in today's money. |
| 102 | `private double ownedSqFt` |  |
| 103 | `private double allocatedSqFt` |  |
| 105 | `private int blocksPurchased` |  |
| 120 | `private double pricePerSqFt` | What businesses pay per square foot. |
| 123 | `private final LandMarket market` | Ten plots on offer, and what the next one costs. |
| 135 | `private int ironDeposits` | Deposit sites the city owns. |
| 138 | `private double ironReserveTonnes` | Ore still in the ground across all of them, in tonnes. |
| 141 | `private double ironMinedThisMonth` | Lifted this month, for the mining report. |
| 144 | `private double landSalesThisMonth` | Monthly flows, for the government accounts. |
| 145 | `private double sqFtSoldThisMonth` |  |
| 146 | `private double landPurchasesThisMonth` |  |
| 147 | `private double sqFtBoughtBackThisMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 37 | 408 | **type** `public class LandManager` | The city's land: what it owns, what is built on, and what it sells. |

### ore (lines 125-180)

| line | len | member | says |
|---:|---:|---|---|
| 150 | 1 | `public double getOwnedSqFt()` | getters |
| 151 | 1 | `public double getAllocatedSqFt()` |  |
| 153 | 3 | `public double getAvailableSqFt()` |  |
| 158 | 3 | `public double getUtilisation()` | How full the city is. |
| 162 | 1 | `public double getAvailableBlocks()` |  |
| 163 | 1 | `public int getBlocksPurchased()` |  |
| 164 | 1 | `public double getPricePerSqFt()` |  |
| 166 | 1 | `public double getLandSalesThisMonth()` |  |
| 167 | 1 | `public double getSqFtSoldThisMonth()` |  |
| 168 | 1 | `public double getLandPurchasesThisMonth()` |  |
| 169 | 1 | `public double getSqFtBoughtBackThisMonth()` |  |
| 172 | 3 | `public double getNextBlockCost()` | What a block's worth of land costs at today's market rate, in thousands. |
| 177 | 3 | `public double getAcquisitionCostPerSqFt()` | Ground price per square foot the city would pay today. |

### the market (lines 181-230)

| line | len | member | says |
|---:|---:|---|---|
| 183 | 1 | `public LandMarket getMarket()` |  |
| 184 | 1 | `public java.util.List<LandParcel> getListing()` |  |
| 191 | 4 | `public void updateMarket(int population)` | Re-prices the market and refills the window. |
| 202 | 28 | `public double buyParcel(int parcelId, double availableCash, int population)` | Buys one listed parcel. |

### ore (lines 231-444)

| line | len | member | says |
|---:|---:|---|---|
| 233 | 1 | `public int getIronDeposits()` |  |
| 234 | 1 | `public double getIronReserveTonnes()` |  |
| 235 | 1 | `public double getIronMinedThisMonth()` |  |
| 237 | 3 | `public boolean hasUnminedDeposit(int minesStanding)` |  |
| 249 | 6 | `public double extractIron(double tonnes)` | Lifts ore out of the ground. |
| 256 | 4 | `public void restoreIron(int deposits, double reserveTonnes)` |  |
| 262 | 3 | `public double getMarginPerSqFt()` | Margin per square foot at the current sale price. |
| 275 | 3 | `public void setPricePerSqFt(double price)` | the load path can put back the price a saved month traded at before the market recomputes it, and so older callers still compile. |
| 279 | 1 | `public void setOwnedSqFt(double sqFt)` |  |
| 280 | 1 | `public void setAllocatedSqFt(double sqFt)` |  |
| 281 | 1 | `public void setBlocksPurchased(int blocks)` |  |
| 285 | 3 | `public boolean canAllocate(double sqFt)` | Is there room to put this up at all? |
| 295 | 7 | `public boolean allocate(double sqFt)` | Takes land out of the available pool. |
| 304 | 3 | `public void release(double sqFt)` | Frees land again. |
| 309 | 3 | `public double priceFor(double sqFt)` | What a business pays the city for a plot this size. |
| 314 | 4 | `public void recordSale(double sqFt)` | Records a sale to a business. |
| 329 | 4 | `public void recordBuyback(double sqFt)` | Records the city buying a plot back from a business that scrapped what stood on it. |
| 339 | 3 | `public double buyBlock(double availableCash)` | Annexes one block. |
| 353 | 13 | `public double buyBlock(double availableCash, int population)` | Buys the cheapest thing on offer. |
| 368 | 7 | `public void clearMonth()` | Called once a month, after the government accounts have read the flows. |
| 376 | 10 | `public void reset()` |  |
| 388 | 27 | `public void printLandInfo()` | printers |
| 418 | 4 | `static { ... }` |  |
| 429 | 7 | `public void redenominate(double scale)` | The city's own land prices and this month's land flows, in the new unit. |
| 439 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

