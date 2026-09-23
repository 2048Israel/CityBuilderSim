# LandManager.java - 498 lines · 45 methods · 5 constants · model

`ham/citybuildersim/LandManager.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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
> 
> IN DOLLARS, AT THE DAY'S RATE (0.7.6). The land office prices its parcels in
> US dollars (LandMarket), and what the city pays is the dollar price times
> the exchange rate on the day it buys - read live from the foreign accounts
> through the supplier Game hands in, so nothing here can hold a stale rate.
> A LandManager built bare, as the harnesses build one, reads the founding
> rate, at which every number is what it was. What a business pays the city
> is local money and does not read the rate at all.

**Uses:** [LandParcel](LandParcel.md) (4), [LandMarket](LandMarket.md) (3), [ForeignAccounts](ForeignAccounts.md) (3)

**Used by (11):** [BuildScreen](BuildScreen.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [ReadPathCheck](ReadPathCheck.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 155 | · ore |
| 220 | · the market |
| 282 | · ore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 61 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres. |
| 93 | `LandManager.COST_GROWTH_PER_BLOCK` | `.02` | Each block bought makes the next this much dearer - annexing outward. |
| 105 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 468 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 108 | `private double defaultPricePerSqFt` | The same, in today's money. |
| 110 | `private double ownedSqFt` |  |
| 111 | `private double allocatedSqFt` |  |
| 113 | `private int blocksPurchased` |  |
| 128 | `private double pricePerSqFt` | What businesses pay per square foot. |
| 131 | `private final LandMarket market` | Nine plots on offer, and what the next one costs. |
| 137 | `private final java.util.function.DoubleSupplier rate` | Local money per US dollar today - ForeignAccounts.getRate(), read live (the shape CentralBank reads the vault in). |
| 165 | `private int ironDeposits` | Deposit sites the city owns. |
| 168 | `private double ironReserveTonnes` | Ore still in the ground across all of them, in tonnes. |
| 171 | `private double ironMinedThisMonth` | Lifted this month, for the mining report. |
| 174 | `private double landSalesThisMonth` | Monthly flows, for the government accounts. |
| 175 | `private double sqFtSoldThisMonth` |  |
| 176 | `private double landPurchasesThisMonth` |  |
| 177 | `private double sqFtBoughtBackThisMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 454 | **type** `public class LandManager` | The city's land: what it owns, what is built on, and what it sells. |
| 140 | 3 | `public LandManager()` | A land office on its own, at the founding rate - what the harnesses build. |
| 145 | 3 | `public LandManager(java.util.function.DoubleSupplier rate)` | The city's land office, converting at the rate this reads. |
| 150 | 4 | `private double rate()` | The rate the office converts at today; the founding rate if the reading is not a price. |

### ore (lines 155-219)

| line | len | member | says |
|---:|---:|---|---|
| 180 | 1 | `public double getOwnedSqFt()` | getters |
| 181 | 1 | `public double getAllocatedSqFt()` |  |
| 183 | 3 | `public double getAvailableSqFt()` |  |
| 188 | 3 | `public double getUtilisation()` | How full the city is. |
| 192 | 1 | `public double getAvailableBlocks()` |  |
| 193 | 1 | `public int getBlocksPurchased()` |  |
| 194 | 1 | `public double getPricePerSqFt()` |  |
| 196 | 1 | `public double getLandSalesThisMonth()` |  |
| 197 | 1 | `public double getSqFtSoldThisMonth()` |  |
| 198 | 1 | `public double getLandPurchasesThisMonth()` |  |
| 199 | 1 | `public double getSqFtBoughtBackThisMonth()` |  |
| 202 | 3 | `public double getNextBlockCost()` | What a block's worth of land costs at today's market rate, in thousands of local money. |
| 211 | 3 | `public double getAcquisitionCostPerSqFt()` | Ground price per square foot the city would pay today, in local money: the world's dollar price (getGroundUsdPerSqFt()) at today's rate, since 0.7.6. |
| 216 | 3 | `public double getGroundUsdPerSqFt()` | The same ground price in the money it is asked in: thousands of US dollars (0.7.6). |

### the market (lines 220-281)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 1 | `public LandMarket getMarket()` |  |
| 223 | 1 | `public java.util.List<LandParcel> getListing()` |  |
| 230 | 4 | `public void updateMarket(int population)` | Re-prices the market and refills the window. |
| 249 | 32 | `public double buyParcel(int parcelId, double availableCash, int population)` | Buys one listed parcel. |

### ore (lines 282-498)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 1 | `public int getIronDeposits()` |  |
| 285 | 1 | `public double getIronReserveTonnes()` |  |
| 286 | 1 | `public double getIronMinedThisMonth()` |  |
| 288 | 3 | `public boolean hasUnminedDeposit(int minesStanding)` |  |
| 300 | 6 | `public double extractIron(double tonnes)` | Lifts ore out of the ground. |
| 307 | 4 | `public void restoreIron(int deposits, double reserveTonnes)` |  |
| 313 | 3 | `public double getMarginPerSqFt()` | Margin per square foot at the current sale price. |
| 326 | 3 | `public void setPricePerSqFt(double price)` | the load path can put back the price a saved month traded at before the market recomputes it, and so older callers still compile. |
| 330 | 1 | `public void setOwnedSqFt(double sqFt)` |  |
| 331 | 1 | `public void setAllocatedSqFt(double sqFt)` |  |
| 332 | 1 | `public void setBlocksPurchased(int blocks)` |  |
| 336 | 3 | `public boolean canAllocate(double sqFt)` | Is there room to put this up at all? |
| 346 | 7 | `public boolean allocate(double sqFt)` | Takes land out of the available pool. |
| 355 | 3 | `public void release(double sqFt)` | Frees land again. |
| 360 | 3 | `public double priceFor(double sqFt)` | What a business pays the city for a plot this size. |
| 365 | 4 | `public void recordSale(double sqFt)` | Records a sale to a business. |
| 380 | 4 | `public void recordBuyback(double sqFt)` | Records the city buying a plot back from a business that scrapped what stood on it. |
| 390 | 3 | `public double buyBlock(double availableCash)` | Annexes one block. |
| 404 | 13 | `public double buyBlock(double availableCash, int population)` | Buys the cheapest thing on offer. |
| 419 | 7 | `public void clearMonth()` | Called once a month, after the government accounts have read the flows. |
| 427 | 10 | `public void reset()` |  |
| 439 | 28 | `public void printLandInfo()` | printers |
| 470 | 4 | `static { ... }` |  |
| 483 | 7 | `public void redenominate(double scale)` | The city's own land prices and this month's land flows, in the new unit. |
| 493 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

