# LandManager.java - 525 lines · 47 methods · 7 constants · model

`ham/citybuildersim/LandManager.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Used by (12):** [BuildScreen](BuildScreen.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [ReadPathCheck](ReadPathCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 182 | · ore |
| 247 | · the market |
| 309 | · ore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 51 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 54 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 88 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres. |
| 120 | `LandManager.COST_GROWTH_PER_BLOCK` | `.02` | Each block bought makes the next this much dearer - annexing outward. |
| 132 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 495 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `private double defaultPricePerSqFt` | The same, in today's money. |
| 137 | `private double ownedSqFt` |  |
| 138 | `private double allocatedSqFt` |  |
| 140 | `private int blocksPurchased` |  |
| 155 | `private double pricePerSqFt` | What businesses pay per square foot. |
| 158 | `private final LandMarket market` | Nine plots on offer, and what the next one costs. |
| 164 | `private final java.util.function.DoubleSupplier rate` | Local money per US dollar today - ForeignAccounts.getRate(), read live (the shape CentralBank reads the vault in). |
| 192 | `private int ironDeposits` | Deposit sites the city owns. |
| 195 | `private double ironReserveTonnes` | Ore still in the ground across all of them, in tonnes. |
| 198 | `private double ironMinedThisMonth` | Lifted this month, for the mining report. |
| 201 | `private double landSalesThisMonth` | Monthly flows, for the government accounts. |
| 202 | `private double sqFtSoldThisMonth` |  |
| 203 | `private double landPurchasesThisMonth` |  |
| 204 | `private double sqFtBoughtBackThisMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 481 | **type** `public class LandManager` | The city's land: what it owns, what is built on, and what it sells. |
| 62 | 1 | `public static double km2(double sqFt)` | An area in square feet, in square kilometres (0.7.13): what the land office shows in place of blocks. |
| 70 | 6 | `public static String km2Words(double sqFt)` | ...written to three significant figures, so the smallest plot the office sells - one block, 0.00929 square kilometres - does not read "0.00", and the largest reads no more digits than a player compares plots by: 0.009... |
| 167 | 3 | `public LandManager()` | A land office on its own, at the founding rate - what the harnesses build. |
| 172 | 3 | `public LandManager(java.util.function.DoubleSupplier rate)` | The city's land office, converting at the rate this reads. |
| 177 | 4 | `private double rate()` | The rate the office converts at today; the founding rate if the reading is not a price. |

### ore (lines 182-246)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 1 | `public double getOwnedSqFt()` | getters |
| 208 | 1 | `public double getAllocatedSqFt()` |  |
| 210 | 3 | `public double getAvailableSqFt()` |  |
| 215 | 3 | `public double getUtilisation()` | How full the city is. |
| 219 | 1 | `public double getAvailableBlocks()` |  |
| 220 | 1 | `public int getBlocksPurchased()` |  |
| 221 | 1 | `public double getPricePerSqFt()` |  |
| 223 | 1 | `public double getLandSalesThisMonth()` |  |
| 224 | 1 | `public double getSqFtSoldThisMonth()` |  |
| 225 | 1 | `public double getLandPurchasesThisMonth()` |  |
| 226 | 1 | `public double getSqFtBoughtBackThisMonth()` |  |
| 229 | 3 | `public double getNextBlockCost()` | What a block's worth of land costs at today's market rate, in thousands of local money. |
| 238 | 3 | `public double getAcquisitionCostPerSqFt()` | Ground price per square foot the city would pay today, in local money: the world's dollar price (getGroundUsdPerSqFt()) at today's rate, since 0.7.6. |
| 243 | 3 | `public double getGroundUsdPerSqFt()` | The same ground price in the money it is asked in: thousands of US dollars (0.7.6). |

### the market (lines 247-308)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 1 | `public LandMarket getMarket()` |  |
| 250 | 1 | `public java.util.List<LandParcel> getListing()` |  |
| 257 | 4 | `public void updateMarket(int population)` | Re-prices the market and refills the window. |
| 276 | 32 | `public double buyParcel(int parcelId, double availableCash, int population)` | Buys one listed parcel. |

### ore (lines 309-525)

| line | len | member | says |
|---:|---:|---|---|
| 311 | 1 | `public int getIronDeposits()` |  |
| 312 | 1 | `public double getIronReserveTonnes()` |  |
| 313 | 1 | `public double getIronMinedThisMonth()` |  |
| 315 | 3 | `public boolean hasUnminedDeposit(int minesStanding)` |  |
| 327 | 6 | `public double extractIron(double tonnes)` | Lifts ore out of the ground. |
| 334 | 4 | `public void restoreIron(int deposits, double reserveTonnes)` |  |
| 340 | 3 | `public double getMarginPerSqFt()` | Margin per square foot at the current sale price. |
| 353 | 3 | `public void setPricePerSqFt(double price)` | the load path can put back the price a saved month traded at before the market recomputes it, and so older callers still compile. |
| 357 | 1 | `public void setOwnedSqFt(double sqFt)` |  |
| 358 | 1 | `public void setAllocatedSqFt(double sqFt)` |  |
| 359 | 1 | `public void setBlocksPurchased(int blocks)` |  |
| 363 | 3 | `public boolean canAllocate(double sqFt)` | Is there room to put this up at all? |
| 373 | 7 | `public boolean allocate(double sqFt)` | Takes land out of the available pool. |
| 382 | 3 | `public void release(double sqFt)` | Frees land again. |
| 387 | 3 | `public double priceFor(double sqFt)` | What a business pays the city for a plot this size. |
| 392 | 4 | `public void recordSale(double sqFt)` | Records a sale to a business. |
| 407 | 4 | `public void recordBuyback(double sqFt)` | Records the city buying a plot back from a business that scrapped what stood on it. |
| 417 | 3 | `public double buyBlock(double availableCash)` | Annexes one block. |
| 431 | 13 | `public double buyBlock(double availableCash, int population)` | Buys the cheapest thing on offer. |
| 446 | 7 | `public void clearMonth()` | Called once a month, after the government accounts have read the flows. |
| 454 | 10 | `public void reset()` |  |
| 466 | 28 | `public void printLandInfo()` | printers |
| 497 | 4 | `static { ... }` |  |
| 510 | 7 | `public void redenominate(double scale)` | The city's own land prices and this month's land flows, in the new unit. |
| 520 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

