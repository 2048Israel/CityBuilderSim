# LandManager.java - 870 lines · 87 methods · 9 constants · model

`ham/citybuildersim/LandManager.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> Land is freed when what stood on it goes - a scrapped business's plot, a
> finished demolition (release()).
> 
> BUYING AND SELLING
> 
> The city buys from outside, an offer at a time (LandMarket), at a price
> that rises as the city crowds onto its land, and it stops "buy everything
> immediately" from being the obvious move. It sells to businesses at the
> land market's price, which falls while ground lies free and climbs as it
> fills, and the spread between the two is the city's margin.
> 
> The city does not pay itself for land it builds on: it already owns it, and
> charging its own budget would just move money from one pocket to the other
> while inflating GDP.
> 
> ON THE WORLD SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2 and
> 2.4). The city's land is a piece of the world now (CityLand): a centre
> round the founding site and its purchases - forty lanes pushed out until
> 0.7.66, whole blocks of a grid since 0.7.67 (spec-grid.md). What the city
> OWNS is the dry ground in them, a figure kept here in square feet as it
> always was and checked against the land's own on a load; their
> water is owned too (getFreshKm2(), getSeaKm2()) and builds nothing. The
> ore is no longer a pool of its own: the city holds the fields of the
> world in its ground, every resource's sites and amount as listed (O), and
> what it has taken out (E) is one figure a resource here, so what remains
> is O - E, worked out in the order the ground was bought (the centre
> first) - and the world's totals are kept to the tonne: what no city owns
> is the world's total less O.
> 
> IN SQUARE METRES ON THE SCREENS (0.7.68, the project's spec-grid.md 2.5
> and star 10). The model keeps square feet and prices a square foot; the
> player reads every area through areaWords() - square metres below a
> hundredth of a square kilometre, square kilometres from there - and every
> ground price a square metre (perM2()). The figures are the same ones,
> converted exactly, and nothing the model does reads the words.
> 
> IN DOLLARS, AT THE DAY'S RATE (0.7.6). The land office prices its parcels in
> US dollars (LandMarket), and what the city pays is the dollar price times
> the exchange rate on the day it buys - read live from the foreign accounts
> through the supplier Game hands in, so nothing here can hold a stale rate.
> A LandManager built bare, as the harnesses build one, reads the founding
> rate, at which every number is what it was. What a business pays the city
> is local money and does not read the rate at all.

**Uses:** [Resource](Resource.md) (17), [CityLand](CityLand.md) (16), [LandParcel](LandParcel.md) (5), [LandMarket](LandMarket.md) (3), [ForeignAccounts](ForeignAccounts.md) (3), [World](World.md) (3), [Founding](Founding.md) (2), [LandConversion](LandConversion.md) (1)

**Used by (37):** [Agriculture](Agriculture.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingVisual](BuildingVisual.md), [BusinessInvestment](BusinessInvestment.md), [CityNeeds](CityNeeds.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [GridCheck](GridCheck.md), [HealthCheck](HealthCheck.md), [HistoryScreen](HistoryScreen.md), [Inbox](Inbox.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [Money](Money.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [Resource](Resource.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 236 | · the ground |
| 324 | · ore |
| 415 | · the market |
| 526 | · ore |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 71 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 74 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 85 | `LandManager.SQ_FT_PER_KM2` | `SQ_M_PER_KM2 / SQ_M_PER_SQ_FT` | Square feet in a square kilometre: a million square metres over a square foot's, 10,763,910.4 (0.7.57). |
| 104 | `LandManager.M2_WORDS_BELOW` | `10_000` | Areas under this many square metres read in square metres (0.7.68): a hundredth of a square kilometre, so a building's plot reads "743 m\u00b2" (a House's 8,000 square feet) and not "0.000743 km\u00b2", while the grid... |
| 166 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres; since 0.7.67 the figure a new city's centre of whole blocks is drawn to hold, and it owns the dry plots drawn, a little more (CityLand.found(): 3,051,569 sq f... |
| 207 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 271 | `LandManager.FOREST_REGROWTH` | `1.0 / 240` | Forest's stored depletion falls by this share a month: 1/240, a twenty-year time constant, so 95% of what is cut grows back within a sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). |
| 840 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 210 | `private double defaultPricePerSqFt` | The same, in today's money - struck at the expected price level since 0.7.42 (seedConstants()). |
| 212 | `private double ownedSqFt` |  |
| 213 | `private double allocatedSqFt` |  |
| 216 | `private int blocksPurchased` | How many purchases the city has made: blocks once, parcels since, offers since 0.7.57 (DataSave's landBlocksPurchased). |
| 231 | `private double pricePerSqFt` | What businesses pay per square foot. |
| 234 | `private final LandMarket market` | Twenty-four offers standing, six a side, and what the ground costs. |
| 244 | `private CityLand land` |  |
| 247 | `private final java.util.function.LongSupplier seed` | The seed of the world the city stands on - Founding.getWorldSeed(), read when the land is founded. |
| 250 | `private final java.util.function.IntSupplier month` | The month, for each offer listed and each purchase made. |
| 258 | `private final double[] extracted` | What the city has taken out of its ground, a resource at a time in Resource's order: E, the third term of the world's conservation (spec-land 2.1) - what remains is the land's amount less this, worked out in acquisiti... |
| 261 | `private double[] worldTotals` | The world's totals and its sea's level, as stored when the city was founded or converted: so a load needs no pass over the world. |
| 262 | `private double worldSeaTheta` |  |
| 277 | `private final java.util.function.DoubleSupplier rate` | Local money per US dollar today - ForeignAccounts.getRate(), read live (the shape CentralBank reads the vault in). |
| 284 | `private final java.util.function.DoubleSupplier usPrices` | The world's price level - WorldEconomy.getPriceLevel(), US prices against the founding's - read live like the rate (0.7.55): what the world's dollar price for ground follows. |
| 337 | `private double ironMinedThisMonth` | Lifted this month, for the mining report. |
| 340 | `private double oilLiftedThisMonth` | The crude the wells lifted this month, in tonnes (0.7.62). |
| 343 | `private double landSalesThisMonth` | Monthly flows, for the government accounts. |
| 344 | `private double sqFtSoldThisMonth` |  |
| 345 | `private double landPurchasesThisMonth` |  |
| 346 | `private double sqFtBoughtBackThisMonth` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 806 | **type** `public class LandManager` | The city's land: what it owns, what is built on, and what it sells. |
| 82 | 1 | `public static double km2(double sqFt)` | An area in square feet, in square kilometres (0.7.13): what the land office shows in place of blocks. |
| 88 | 1 | `public static double sqFt(double km2)` | An area in square kilometres, in square feet: what an offer's dry ground adds to the city's (0.7.57). |
| 97 | 5 | `public static String km2Words(double sqFt)` | ...written to three significant figures, so a small area does not read "0.00", and the largest reads no more digits than a player compares plots by: 0.00929, 0.214, 2.79, 27.9, each with its unit - grouped from a thou... |
| 115 | 8 | `public static String areaWords(double sqFt)` | An area the player reads (0.7.68, spec-grid 2.5 and star 10): in square metres below M2_WORDS_BELOW - "743 m\u00b2", "7,430 m\u00b2" - and in square kilometres from it, km2Words(): "0.0301 km\u00b2", "1,760,000 km\u00... |
| 130 | 5 | `public static String partFigure(double partSqFt, double wholeSqFt)` | A part of an area as a bare figure in the unit its whole reads in (0.7.68): the "0.0273" of "0.0288 km\u00b2, 0.0273 dry", the "6,900" of "7,200 m\u00b2, 6,900 dry" - so a line never reads one unit and means another. |
| 137 | 5 | `static String threeFigures(java.math.BigDecimal v)` | A figure to three significant figures, grouped from a thousand: "0.0301", "76.2", "7,430", "1,760,000". |
| 150 | 1 | `public static double perM2(double perSqFt)` | A price a square foot, a square metre (0.7.68): a square metre is 1 / SQ_M_PER_SQ_FT = 10.76 square feet, so its ground costs that many times as much. |

### the ground (lines 236-323)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 3 | `public LandManager()` | A land office on its own, at the founding rate, on the default world - what the harnesses build. |
| 292 | 3 | `public LandManager(java.util.function.DoubleSupplier rate)` | The city's land office, converting at the rate this reads, at the founding's US prices. |
| 297 | 3 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices)` | ...and with the world's price level this reads (0.7.55): the city's. |
| 302 | 7 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices, java.util.function.LongS...` | ...on the world this seed reads, in the month this reads (0.7.57): the city's. |
| 310 | 1 | `private int month()` |  |
| 313 | 4 | `private double rate()` | The rate the office converts at today; the founding rate if the reading is not a price. |
| 319 | 4 | `private double usPrices()` | The world's price level today; the founding's 1 if the reading is not a price. |

### ore (lines 324-414)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 1 | `public double getOwnedSqFt()` | getters |
| 350 | 1 | `public double getAllocatedSqFt()` |  |
| 352 | 3 | `public double getAvailableSqFt()` |  |
| 357 | 3 | `public double getUtilisation()` | How full the city is. |
| 361 | 1 | `public double getAvailableBlocks()` |  |
| 362 | 1 | `public int getBlocksPurchased()` |  |
| 363 | 1 | `public double getPricePerSqFt()` |  |
| 365 | 1 | `public double getLandSalesThisMonth()` |  |
| 366 | 1 | `public double getSqFtSoldThisMonth()` |  |
| 367 | 1 | `public double getLandPurchasesThisMonth()` |  |
| 368 | 1 | `public double getSqFtBoughtBackThisMonth()` |  |
| 371 | 3 | `public double getNextBlockCost()` | What a block's worth of land costs at today's market rate, in thousands of local money. |
| 380 | 3 | `public double getAcquisitionCostPerSqFt()` | Ground price per square foot the city would pay today, in local money: the world's dollar price (getGroundUsdPerSqFt()) at today's rate, since 0.7.6. |
| 391 | 7 | `public double getOfficePricePerSqFt()` | What the land office would charge the city a square foot today, in local money (0.7.51): the best value on its listing (LandMarket.bestValue()) at today's rate, a square foot of its dry ground - or the acquisition cos... |
| 400 | 3 | `public double getGroundUsdPerSqFt()` | The same ground price in the money it is asked in: thousands of US dollars (0.7.6). |
| 411 | 1 | `public double getCrowding()` | THE PRICE'S PARTS (0.7.55), as the ground was last priced: the dollar price is LandMarket.openingUsdPerSqFt() x getUsPriceLevel() x getCrowdingPremium(), and the premium reads getCrowding() - the city's people per squ... |
| 412 | 1 | `public double getCrowdingPremium()` |  |
| 413 | 1 | `public double getUsPriceLevel()` |  |

### the market (lines 415-525)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 1 | `public LandMarket getMarket()` |  |
| 418 | 1 | `public java.util.List<LandParcel> getListing()` |  |
| 421 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and its purchases, whole blocks since 0.7.67 - founded round the world's founding site the first time it is asked for. |
| 431 | 9 | `CityLand land()` | The land, founded if it is not yet: a centre of whole blocks round the founding site of the seed's world holding at least the dry ground owned (CityLand.found()), nothing taken out of it - and since 0.7.67 the figure ... |
| 442 | 10 | `void install(CityLand land, double[] extracted, double[] totals, double seaTheta)` | Puts a city's land in place - founded, converted or saved - with what has been taken out of it and the world's totals and sea level; the office's shelf cleared for update() to list. |
| 454 | 1 | `public boolean hasCityLand()` | Whether the land has been founded or put back yet - a bare office's has not until something asks. |
| 457 | 1 | `public double getLandDrySqFt()` | The city's dry ground, centre and purchases, as the land measures it, in square feet: what getOwnedSqFt() is checked against on a load. |
| 460 | 1 | `public double getOwnedKm2()` | The city's whole area, dry ground and water, in square kilometres (0.7.57). |
| 463 | 1 | `public double getFreshKm2()` | ...its fresh water: lakes and the founding river. |
| 466 | 1 | `public double getSeaKm2()` | ...its sea. |
| 469 | 1 | `public double getForestKm2()` | ...and its forest, part of its dry ground. |
| 476 | 5 | `public void updateMarket(long population)` | Re-prices the market and refills the window. |
| 496 | 29 | `public double buyParcel(int parcelId, double availableCash, long population)` | Buys one listed offer. |

### ore (lines 526-870)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 1 | `public int getIronDeposits()` | The iron sites the city owns, centre and purchases: how many Iron Mines can stand. |
| 532 | 1 | `public double getIronReserveTonnes()` | The iron still in its ground, in tonnes: O - E. |
| 533 | 1 | `public double getIronMinedThisMonth()` |  |
| 536 | 1 | `public int getSites(Resource r)` | The sites of a resource the city owns, centre and purchases (0.7.62): how many of its mines or wells can stand. |
| 539 | 1 | `public int getOilSites()` | The oil sites the city owns (0.7.62): how many Oil Wells can stand. |
| 542 | 1 | `public double getOilReserveTonnes()` | The crude still in its ground, in tonnes: O - E. |
| 543 | 1 | `public double getOilLiftedThisMonth()` |  |
| 546 | 5 | `public double extractOil(double tonnes)` | Lifts crude out of the ground (0.7.62): what was there, as extractIron() does for ore. |
| 552 | 3 | `public boolean hasUnminedDeposit(int minesStanding)` |  |
| 564 | 5 | `public double extractIron(double tonnes)` | Lifts ore out of the ground. |
| 575 | 5 | `public double extract(Resource r, double amount)` | Takes up to `amount` of a resource out of the city's ground (0.7.57): what was there, and E rises by exactly that. |
| 582 | 1 | `public double getOwnedAmount(Resource r)` | What the city owns of a resource as listed - its centre's and every purchase's: O. |
| 585 | 1 | `public double getExtracted(Resource r)` | What it has taken out: E. |
| 588 | 1 | `public double getRemaining(Resource r)` | What remains in its ground: O - E, never below nothing. |
| 591 | 1 | `public double getWorldTotal(Resource r)` | What the world holds of it, as stored when the city was founded or converted: W. |
| 594 | 1 | `public double getUnowned(Resource r)` | What no city owns of it: W - O. |
| 597 | 1 | `public double getWorldSeaTheta()` | The world's sea level, as stored with its totals. |
| 600 | 1 | `public double[] getDepletionState()` | What has been taken out, every resource in Resource's order (DataSave's depletion). |
| 603 | 1 | `public double[] getWorldTotalsState()` | The world's totals as stored, every resource in Resource's order (DataSave's worldTotals). |
| 611 | 10 | `public double[] remainingByHolding(Resource r)` | What remains of a resource in each holding, in acquisition order - the centre, then each purchase (spec-land star 12): E is taken out of the first until it is worked out, then the next, so a holding's remainder is wha... |
| 630 | 3 | `public void restoreIron(int deposits, double reserveTonnes)` | A FIXTURE'S ORE: the city holds exactly `deposits` iron sites and `reserveTonnes` in the ground. |
| 635 | 13 | `public void restoreSites(Resource r, int deposits, double reserveTonnes)` | ...the same for any resource in fields (0.7.62): a fixture's oil, as restoreIron() is its ore. |
| 650 | 3 | `public double getMarginPerSqFt()` | Margin per square foot at the current sale price. |
| 663 | 3 | `public void setPricePerSqFt(double price)` | the load path can put back the price a saved month traded at before the market recomputes it, and so older callers still compile. |
| 677 | 5 | `public void setOwnedSqFt(double sqFt)` | The city's dry ground, set by hand. |
| 684 | 1 | `void restoreOwnedSqFt(double sqFt)` | The figure alone, the land as it stands: a load putting back what the save says, or a restatement that has just drawn the land to hold it. |
| 685 | 1 | `public void setAllocatedSqFt(double sqFt)` |  |
| 686 | 1 | `public void setBlocksPurchased(int blocks)` |  |
| 690 | 3 | `public boolean canAllocate(double sqFt)` | Is there room to put this up at all? |
| 700 | 7 | `public boolean allocate(double sqFt)` | Takes land out of the available pool. |
| 709 | 3 | `public void release(double sqFt)` | Frees land again: a scrapped business's plot (Game.retire()), and since 0.7.22 a finished demolition's ground (Game.settleConstructionControl()). |
| 714 | 3 | `public double priceFor(double sqFt)` | What a business pays the city for a plot this size. |
| 719 | 4 | `public void recordSale(double sqFt)` | Records a sale to a business. |
| 739 | 4 | `public void recordBuyback(double sqFt)` | Records the city buying a plot back from a business that scrapped what stood on it. |
| 749 | 3 | `public double buyBlock(double availableCash)` | Annexes one block. |
| 763 | 13 | `public double buyBlock(double availableCash, long population)` | Buys the cheapest thing on offer. |
| 783 | 4 | `public void endMonth()` | The month's end for the ground (0.7.57): forest's stored depletion grows back by FOREST_REGROWTH, then the month's flows are cleared. |
| 789 | 8 | `public void clearMonth()` | Called once a month, after the government accounts have read the flows. |
| 798 | 12 | `public void reset()` |  |
| 813 | 26 | `public void printLandInfo()` | The land to the log, in the player's units since 0.7.68 (areaWords(), prices a square metre) - nothing in the game calls it; a console's view of the office. |
| 842 | 4 | `static { ... }` |  |
| 855 | 7 | `public void redenominate(double scale)` | The city's own land prices and this month's land flows, in the new unit. |
| 865 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

