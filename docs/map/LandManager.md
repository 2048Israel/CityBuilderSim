# LandManager.java - 2,192 lines · 179 methods · 13 constants · model

`ham/citybuildersim/LandManager.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

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
>   available   the difference - what can still be built on; since 0.7.107
>               less what the forest law keeps from building and what the
>               zones hold, the ground free for anyone (ZONES AND THE FOREST
>               LAW; a zoned sector also builds on its zone's, freeFor())
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
> is the world's total less O. (Since 0.7.93 oil's E is two pools' - THE TWO
> OIL POOLS; since 0.7.107 forest's carries the wood cleared for building,
> C; since 0.7.110 a typed resource's is kept by type once a works digs by
> type - THE GROUND BY TYPE.)
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
> ... (3 more lines in the source)

**Uses:** [Resource](Resource.md) (71), [CityLand](CityLand.md) (50), [Zoning](Zoning.md) (25), [World](World.md) (22), [Deposit](Deposit.md) (19), [LandParcel](LandParcel.md) (5), [LandMarket](LandMarket.md) (3), [ForeignAccounts](ForeignAccounts.md) (3), [Founding](Founding.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (1), [LandConversion](LandConversion.md) (1)

**Used by (65):** [Agriculture](Agriculture.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingVisual](BuildingVisual.md), [BusinessInvestment](BusinessInvestment.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ExtractionSector](ExtractionSector.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [GridCheck](GridCheck.md), [HealthCheck](HealthCheck.md), [HistoryScreen](HistoryScreen.md), [Inbox](Inbox.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [Money](Money.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Resource](Resource.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [Zoning](Zoning.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 267 | · the ground |
| 366 | · ore |
| 467 | · the market |
| 583 | · ore |
| 635 | · the oil by grade, as it is worked out (0.7.79, batch O3; spec-oil 2.2) |
| 794 | THE TWO OIL POOLS (0.7.93, batch O10b; Jerus, 2026-10-09: "each oil |
| 1127 | THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) |
| 1708 | ZONES AND THE FOREST LAW (0.7.107, batch Z1; Jerus, 2026-10-08, the |
| 1773 | · the ground, by kind |
| 1823 | · the zones |
| 1927 | · the forest's wood |
| 1947 | · clearing |
| 2013 | · the save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 74 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 77 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 80 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 91 | `LandManager.SQ_FT_PER_KM2` | `SQ_M_PER_KM2 / SQ_M_PER_SQ_FT` | Square feet in a square kilometre: a million square metres over a square foot's, 10,763,910.4 (0.7.57). |
| 110 | `LandManager.M2_WORDS_BELOW` | `10_000` | Areas under this many square metres read in square metres (0.7.68): a hundredth of a square kilometre, so a building's plot reads "743 m\u00b2" (a House's 8,000 square feet) and not "0.000743 km\u00b2", while the grid... |
| 174 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres; since 0.7.67 the figure a new city's centre of whole blocks is drawn to hold, and it owns the dry plots drawn, a little more (CityLand.found(): 3,051,569 sq f... |
| 183 | `LandManager.FOUNDING_FRESH_UNITS` | `60_000` | A new city's fresh water, in units a month: what one Water Treatment Plant treats, its 60,000 units of 10,000 gallons (buildings.json id 10's production1) - Jerus's A24 (0.7.106, 2026-10-10: "found with fresh water fo... |
| 186 | `LandManager.FOUNDING_FRESH_KM2` | `FOUNDING_FRESH_UNITS / UtilitiesHandler.FRESH_UNITS_PER_KM2` | ...as lake and river ground in its centre, in square kilometres: FOUNDING_FRESH_UNITS over what one yields a month (UtilitiesHandler.FRESH_UNITS_PER_KM2), 0.4934 km2 (CityLand.found()). |
| 238 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 307 | `LandManager.FOREST_REGROWTH` | `1.0 / 240` | Forest's stored depletion falls by this share a month: 1/240, a twenty-year time constant, so 95% of what is cut grows back within a sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). |
| 825 | `LandManager.TWO_POOLS_FORMAT` | `35` | The first save format that carries the two pools: its depletion's oil the ground pool's, and the offshore pool's E beside it. |
| 1771 | `LandManager.CLEARING_ROUNDING` | `1e-9` | A share of the city's dry ground that is a rounding and not ground: what the month's clearing ignores past the built ground's own (settleClearing()). |
| 2162 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 241 | `private double defaultPricePerSqFt` | The same, in today's money - struck at the expected price level since 0.7.42 (seedConstants()). |
| 243 | `private double ownedSqFt` |  |
| 244 | `private double allocatedSqFt` |  |
| 247 | `private int blocksPurchased` | How many purchases the city has made: blocks once, parcels since, offers since 0.7.57 (DataSave's landBlocksPurchased). |
| 262 | `private double pricePerSqFt` | What businesses pay per square foot. |
| 265 | `private final LandMarket market` | Twenty-four offers standing, six a side, and what the ground costs. |
| 275 | `private CityLand land` |  |
| 278 | `private final java.util.function.Supplier<World.Key> worldKey` | The world the city stands on - Founding.getWorldKey() (its seed's, Founding.getWorldSeed(), until 0.7.115), read when the land is founded. |
| 281 | `private final java.util.function.IntSupplier month` | The month, for each offer listed and each purchase made. |
| 291 | `private final double[] extracted` | What the city has taken out of its ground, a resource at a time in Resource's order: E, the third term of the world's conservation (spec-land 2.1) - what remains is the land's amount less this, worked out in acquisiti... |
| 294 | `private double extractedAtSea` | E of the offshore pool (0.7.93, THE TWO OIL POOLS): what the platform wells have lifted from the oil under the city's sea. |
| 297 | `private double[] worldTotals` | The world's totals and its sea's level, as stored when the city was founded or converted: so a load needs no pass over the world. |
| 298 | `private double worldSeaTheta` |  |
| 313 | `private final java.util.function.DoubleSupplier rate` | Local money per US dollar today - ForeignAccounts.getRate(), read live (the shape CentralBank reads the vault in). |
| 320 | `private final java.util.function.DoubleSupplier usPrices` | The world's price level - WorldEconomy.getPriceLevel(), US prices against the founding's - read live like the rate (0.7.55): what the world's dollar price for ground follows. |
| 379 | `private double ironMinedThisMonth` | Lifted this month, for the mining report: since 0.7.124 the tonnes of ground taken out, a magnetite works' rock as dug. |
| 382 | `private double oilLiftedThisMonth` | The crude the wells lifted this month, in tonnes (0.7.62) - both pools' since 0.7.93. |
| 385 | `private final double[] liftedByGrade` | ...by grade, in Deposit.Grade's order (0.7.79): what extractOil()'s walk of the fields found it was - and since 0.7.93 extractOilAtSea()'s of the sea's. |
| 388 | `private double landSalesThisMonth` | Monthly flows, for the government accounts. |
| 389 | `private double sqFtSoldThisMonth` |  |
| 390 | `private double landPurchasesThisMonth` |  |
| 391 | `private double sqFtBoughtBackThisMonth` |  |
| 650 | `private double[] oilRunEnds` | Where each run of one grade ends, tonnes from the first of the ground pool's oil, and its grade's ordinal. |
| 651 | `private byte[] oilRunGrades` |  |
| 653 | `private double[] seaRunEnds` | ...and the offshore pool's (0.7.93). |
| 654 | `private byte[] seaRunGrades` |  |
| 656 | `private double[] seaByHolding` | Each holding's part of the offshore pool, tonnes in acquisition order, and the pool's whole (0.7.93): laid out with the runs. |
| 657 | `private double oilOwnedAtSea` |  |
| 659 | `private CityLand oilRunsLand` | What the runs were laid out for: the land, its stamp and what each holding listed. |
| 660 | `private long oilRunsKey` |  |
| 676 | `double[] ends` |  |
| 677 | `byte[] grades` |  |
| 678 | `int n` |  |
| 889 | `private final java.util.Map<Resource, long[]> seaSitesKept` | The sea sites by resource, kept with the ground they were counted on. |
| 890 | `private CityLand seaSitesLand` |  |
| 921 | `private final java.util.Map<Resource, java.util.List<SeaField>> seaFieldsKept` | The sea fields by resource, kept with the ground they were listed on and its key. |
| 922 | `private final java.util.Map<Resource, Long> seaFieldsKey` |  |
| 923 | `private CityLand seaFieldsLand` |  |
| 1174 | `private final double[][] extractedByType` | What has been taken out of each typed resource by type, in Resource.typesOf()'s order, once a works has dug it by type; null while E by type is the walk's reading of E. |
| 1178 | `final long key` |  |
| 1179 | `final java.util.List<CityLand.Piece> pieces` |  |
| 1180 | `final long[] sites` |  |
| 1181 | `final double[] amounts` |  |
| 1183 | `final double[] ends` | Where each run of one type ends, tonnes from the first of the resource's, and its type's index in Resource.typesOf(). |
| 1184 | `final int[] types` |  |
| 1215 | `private final TypedGround[] typedKept` | The ground by type, kept by resource with the land it was laid out on. |
| 1216 | `private CityLand typedLand` |  |
| 1756 | `private final Zoning zoning` | The city's zones and its forest law (0.7.107): empty for a new city - the ground free for anyone, as it always was. |
| 1759 | `private double clearedSqFt` | Forest cleared for building, in square feet of ground (C's area, Z7): never regrowing, never un-cleared. |
| 1762 | `private double clearedM3` | ...and its wood, in cubic metres: the cut-over wood it took from E and the standing wood it took out (C). |
| 1765 | `private double forestClearedThisMonth` | The standing wood cleared this month, cubic metres: Forestry's salvage cut from materials I9, lost until then. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 2122 | **type** `public class LandManager` | The city's land: what it owns, what is built on, and what it sells. |
| 88 | 1 | `public static double km2(double sqFt)` | An area in square feet, in square kilometres (0.7.13): what the land office shows in place of blocks. |
| 94 | 1 | `public static double sqFt(double km2)` | An area in square kilometres, in square feet: what an offer's dry ground adds to the city's (0.7.57). |
| 103 | 5 | `public static String km2Words(double sqFt)` | ...written to three significant figures, so a small area does not read "0.00", and the largest reads no more digits than a player compares plots by: 0.00929, 0.214, 2.79, 27.9, each with its unit - grouped from a thou... |
| 121 | 8 | `public static String areaWords(double sqFt)` | An area the player reads (0.7.68, spec-grid 2.5 and star 10): in square metres below M2_WORDS_BELOW - "743 m\u00b2", "7,430 m\u00b2" - and in square kilometres from it, km2Words(): "0.0301 km\u00b2", "1,760,000 km\u00... |
| 136 | 5 | `public static String partFigure(double partSqFt, double wholeSqFt)` | A part of an area as a bare figure in the unit its whole reads in (0.7.68): the "0.0273" of "0.0288 km\u00b2, 0.0273 dry", the "6,900" of "7,200 m\u00b2, 6,900 dry" - so a line never reads one unit and means another. |
| 143 | 5 | `static String threeFigures(java.math.BigDecimal v)` | A figure to three significant figures, grouped from a thousand: "0.0301", "76.2", "7,430", "1,760,000". |
| 156 | 1 | `public static double perM2(double perSqFt)` | A price a square foot, a square metre (0.7.68): a square metre is 1 / SQ_M_PER_SQ_FT = 10.76 square feet, so its ground costs that many times as much. |
| 189 | 3 | `public static CityLand foundingLand(long worldSeed)` | The land a new city is founded with on the world of this seed (0.7.106): the centre land() founds - STARTING_SQ_FT's rings and FOUNDING_FRESH_KM2 of the founding lake - drawn apart from any city, for what is priced on... |
| 194 | 4 | `public static CityLand foundingLand(World.Key worldKey)` | ...on the world a key names (0.7.115). |

### the ground (lines 267-365)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 3 | `public LandManager()` | A land office on its own, at the founding rate, on the default world - what the harnesses build. |
| 328 | 3 | `public LandManager(java.util.function.DoubleSupplier rate)` | The city's land office, converting at the rate this reads, at the founding's US prices. |
| 333 | 3 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices)` | ...and with the world's price level this reads (0.7.55): the city's. |
| 338 | 4 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices, java.util.function.LongS...` | ...on the world this seed reads, in the month this reads (0.7.57): the city's until 0.7.115, a harness's since (the city's reads its key, below). |
| 344 | 7 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices, java.util.function.Suppl...` | ...on the world this key reads (0.7.115, batch E2): the city's, Founding.getWorldKey(). |
| 352 | 1 | `private int month()` |  |
| 355 | 4 | `private double rate()` | The rate the office converts at today; the founding rate if the reading is not a price. |
| 361 | 4 | `private double usPrices()` | The world's price level today; the founding's 1 if the reading is not a price. |

### ore (lines 366-466)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 1 | `public double getOwnedSqFt()` | getters |
| 395 | 1 | `public double getAllocatedSqFt()` |  |
| 404 | 3 | `public double getAvailableSqFt()` | The ground free for anyone: what is not built on, less what the forest law keeps from building and what the zones hold (0.7.107, ZONES AND THE FOREST LAW) - with no zones and the law at 0 and 0, owned less allocated, ... |
| 409 | 3 | `public double getUtilisation()` | How full the city is. |
| 413 | 1 | `public double getAvailableBlocks()` |  |
| 414 | 1 | `public int getBlocksPurchased()` |  |
| 415 | 1 | `public double getPricePerSqFt()` |  |
| 417 | 1 | `public double getLandSalesThisMonth()` |  |
| 418 | 1 | `public double getSqFtSoldThisMonth()` |  |
| 419 | 1 | `public double getLandPurchasesThisMonth()` |  |
| 420 | 1 | `public double getSqFtBoughtBackThisMonth()` |  |
| 423 | 3 | `public double getNextBlockCost()` | What a block's worth of land costs at today's market rate, in thousands of local money. |
| 432 | 3 | `public double getAcquisitionCostPerSqFt()` | Ground price per square foot the city would pay today, in local money: the world's dollar price (getGroundUsdPerSqFt()) at today's rate, since 0.7.6. |
| 443 | 7 | `public double getOfficePricePerSqFt()` | What the land office would charge the city a square foot today, in local money (0.7.51): the best value on its listing (LandMarket.bestValue()) at today's rate, a square foot of its dry ground - or the acquisition cos... |
| 452 | 3 | `public double getGroundUsdPerSqFt()` | The same ground price in the money it is asked in: thousands of US dollars (0.7.6). |
| 463 | 1 | `public double getCrowding()` | THE PRICE'S PARTS (0.7.55), as the ground was last priced: the dollar price is LandMarket.openingUsdPerSqFt() x getUsPriceLevel() x getCrowdingPremium(), and the premium reads getCrowding() - the city's people per squ... |
| 464 | 1 | `public double getCrowdingPremium()` |  |
| 465 | 1 | `public double getUsPriceLevel()` |  |

### the market (lines 467-582)

| line | len | member | says |
|---:|---:|---|---|
| 469 | 1 | `public LandMarket getMarket()` |  |
| 470 | 1 | `public java.util.List<LandParcel> getListing()` |  |
| 473 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and its purchases, whole blocks since 0.7.67 - founded round the world's founding site the first time it is asked for. |
| 486 | 9 | `CityLand land()` | The land, founded if it is not yet: a centre of whole blocks round the founding site of the seed's world holding at least the dry ground owned (CityLand.found()), nothing taken out of it - and since 0.7.67 the figure ... |
| 497 | 12 | `void install(CityLand land, double[] extracted, double[] totals, double seaTheta)` | Puts a city's land in place - founded, converted or saved - with what has been taken out of it and the world's totals and sea level; the office's shelf cleared for update() to list. |
| 511 | 1 | `public boolean hasCityLand()` | Whether the land has been founded or put back yet - a bare office's has not until something asks. |
| 514 | 1 | `public double getLandDrySqFt()` | The city's dry ground, centre and purchases, as the land measures it, in square feet: what getOwnedSqFt() is checked against on a load. |
| 517 | 1 | `public double getOwnedKm2()` | The city's whole area, dry ground and water, in square kilometres (0.7.57). |
| 520 | 1 | `public double getFreshKm2()` | ...its fresh water: lakes and the founding river. |
| 523 | 1 | `public double getSeaKm2()` | ...its sea. |
| 526 | 1 | `public double getForestKm2()` | ...and its forest, part of its dry ground. |
| 533 | 5 | `public void updateMarket(long population)` | Re-prices the market and refills the window. |
| 553 | 29 | `public double buyParcel(int parcelId, double availableCash, long population)` | Buys one listed offer. |

### ore (lines 583-634)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 1 | `public int getIronDeposits()` | The iron sites the city owns, centre and purchases: how many Iron Mines can stand. |
| 589 | 1 | `public double getIronReserveTonnes()` | The iron still in its ground, in tonnes: O - E. |
| 590 | 1 | `public double getIronMinedThisMonth()` |  |
| 593 | 1 | `public int getSites(Resource r)` | The sites of a resource the city owns, centre and purchases (0.7.62): how many of its mines or wells can stand. |
| 596 | 1 | `public int getOilSites()` | The oil sites the city owns (0.7.62): how many Oil Wells can stand. |
| 599 | 1 | `public double getOilReserveTonnes()` | The crude still in its ground, in tonnes: O - E - since 0.7.93 both pools', the ground's and the sea's. |
| 601 | 1 | `public double getOilLiftedThisMonth()` | The crude the wells lifted this month, both pools' (0.7.93). |
| 610 | 7 | `public double extractOil(double tonnes)` | Lifts crude out of the ground (0.7.62): what was there, as extractIron() does for ore - and since 0.7.79 (batch O3) of which grades: the walk of the city's oil from E to E + lifted (oilRuns()) adds each grade's part t... |
| 623 | 8 | `public double extractOilAtSea(double tonnes)` | ...and out of the offshore pool, the platform wells' (0.7.93, THE TWO OIL POOLS): what was there, its E rising by exactly that, graded by the walk of the sea's oil from its E (oilRunsAtSea()). |
| 633 | 1 | `public double[] getOilLiftedByGrade()` | The crude the wells lifted this month by grade, tonnes, in Deposit.Grade's order (0.7.79): the refinery's local crude is of this mix (Refining's crude mix). |

### the oil by grade, as it is worked out (0.7.79, batch O3; spec-oil 2.2) (lines 635-793)

| line | len | member | says |
|---:|---:|---|---|
| 663 | 5 | `private static long groundKey(CityLand l, double[] listed)` | A key for what the ground holds of a resource: the land's stamp and every holding's listed amount, bit for bit. |
| 670 | 3 | `private static boolean atSea(World world, CityLand.Held f)` | Whether a field the city holds is the sea's: its centre plot in the sea (World.depthAt() over 0), as getSites(r, false) counts it. |
| 675 | 46 | **type** `private static final class Runs` | Runs of one grade as they are laid out: each run's end and grade's ordinal, a run that continues the last one's grade merged into it. |
| 680 | 12 | `void add(double end, byte grade)` _(in LandManager.Runs)_ |  |
| 699 | 21 | `double lay(java.util.List<CityLand.Held> fields, World world, boolean sea, double amount, double upTo)` _(in LandManager.Runs)_ | One holding's part of a pool, `amount` tonnes from `upTo`: its fields of the pool (the sea's when `sea`, else the dry ones) in order, each as far as the part goes, and past them MEDIUM - the ground's oil by fiat. |
| 722 | 33 | `private void layOilRuns()` |  |
| 757 | 4 | `public double[][] oilRuns()` | The runs of the ground pool's oil, each {end, grade's ordinal} in tonnes from its first, in the order it is worked out (0.7.79; the one pool's until 0.7.93). |
| 763 | 4 | `public double[][] oilRunsAtSea()` | ...and the offshore pool's (0.7.93): the sea fields', in the order they are worked out. |
| 768 | 5 | `private static double[][] runsOf(double[] ends, byte[] grades)` |  |
| 775 | 18 | `private void gradeTheLift(boolean atSea, double from, double amount)` | Adds the grades of a pool's oil - the offshore pool's when `atSea` - from `from` to `from + amount` tonnes into the month's liftedByGrade; past the last run (a rounding), MEDIUM. |

### THE TWO OIL POOLS (0.7.93, batch O10b; Jerus, 2026-10-09: "each oil (lines 794-1126)

| line | len | member | says |
|---:|---:|---|---|
| 828 | 4 | `public double getOilOwnedAtSea()` | The offshore pool's tonnes as listed: each holding's sea fields', at most what it listed (O of the sea's oil). |
| 834 | 3 | `public double getOilOwnedOnGround()` | ...and the ground pool's: what the city owns of oil less the sea's - its dry fields and any oil by fiat. |
| 839 | 1 | `public double getOilExtractedOnGround()` | What the land wells have lifted from the ground pool (its E). |
| 842 | 1 | `public double getOilExtractedAtSea()` | ...and the platform wells from the offshore pool. |
| 845 | 3 | `public double getOilLeftOnGround()` | The crude left in the ground pool, never below nothing: what the land wells can still lift. |
| 850 | 3 | `public double getOilLeftAtSea()` | ...and in the offshore pool: what the platform wells can still lift. |
| 861 | 10 | `public void restoreOilPools(Double atSea)` | Puts the offshore pool's E back from a save (Game's load path, after the land is in place): `atSea` as saved, in format TWO_POOLS_FORMAT and later. |
| 881 | 6 | `public int getSites(Resource r, boolean dry)` | The sites of a resource the city owns on dry ground or in the sea (0.7.79, batch O3; spec-oil 2.6): a field's sites are the sea's when its centre plot is sea (World.depthAt() over 0), else dry - a lake or the river co... |
| 892 | 24 | `private long seaSites(CityLand l, Resource r)` |  |
| 918 | 1 | **type** `public record SeaField(Deposit field, int sites, double depth)` | A field of a resource whose centre is in the sea, the sites of it the city owns, and its depth there in metres (0.7.91). |
| 934 | 42 | `public java.util.List<SeaField> seaFields(Resource r)` | The fields of a resource the city owns whose centre is in the sea (0.7.91, batch O10; spec-oil 2.7): each field once, its owned sites summed over the holdings that hold them (CityLand.heldFields()), in the order the h... |
| 977 | 3 | `public boolean hasUnminedDeposit(int minesStanding)` |  |
| 989 | 5 | `public double extractIron(double tonnes)` | Lifts ore out of the ground. |
| 1003 | 5 | `public double extractIron(Deposit.FieldType type, double tonnes, boolean more)` | ...of one type (0.7.124, batch I5; THE GROUND BY TYPE): up to `tonnes` of the type's ground taken out (extract(r, type, amount)) - rock, for a magnetite works, three tonnes of it to a tonne of concentrate - and kept f... |
| 1014 | 10 | `public double extract(Resource r, double amount)` | Takes up to `amount` of a resource out of the city's ground (0.7.57): what was there, and E rises by exactly that. |
| 1026 | 1 | `public double getOwnedAmount(Resource r)` | What the city owns of a resource as listed - its centre's and every purchase's: O. |
| 1029 | 4 | `public double getExtracted(Resource r)` | What it has taken out: E - oil's both pools' since 0.7.93, and forest's the wood cleared for building with it since 0.7.107 (C, never regrowing), so W = unowned + remaining + taken out holds. |
| 1035 | 5 | `public double getRemaining(Resource r)` | What remains in its ground: O - E, never below nothing - oil's each pool's, added (0.7.93); forest's O - C - E (0.7.107). |
| 1042 | 1 | `public double getWorldTotal(Resource r)` | What the world holds of it, as stored when the city was founded or converted: W. |
| 1045 | 1 | `public double getUnowned(Resource r)` | What no city owns of it: W - O. |
| 1048 | 1 | `public double getWorldSeaTheta()` | The world's sea level, as stored with its totals. |
| 1051 | 1 | `public double[] getDepletionState()` | What has been taken out, every resource in Resource's order (DataSave's depletion) - oil's the ground pool's since 0.7.93, the offshore pool's getOilExtractedAtSea(). |
| 1054 | 1 | `public double[] getWorldTotalsState()` | The world's totals as stored, every resource in Resource's order (DataSave's worldTotals). |
| 1062 | 14 | `public double[] remainingByHolding(Resource r)` | What remains of a resource in each holding, in acquisition order - the centre, then each purchase (spec-land star 12): E is taken out of the first until it is worked out, then the next, so a holding's remainder is wha... |
| 1078 | 12 | `private double[] oilRemainingByHolding(double[] held)` | ...oil's since 0.7.93: each pool's E taken out of its own part of each holding in turn, the two parts' remainders added. |
| 1099 | 3 | `public void restoreIron(int deposits, double reserveTonnes)` | A FIXTURE'S ORE: the city holds exactly `deposits` iron sites and `reserveTonnes` in the ground. |
| 1104 | 22 | `public void restoreSites(Resource r, int deposits, double reserveTonnes)` | ...the same for any resource in fields (0.7.62): a fixture's oil, as restoreIron() is its ore. |

### THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) (lines 1127-1707)

| line | len | member | says |
|---:|---:|---|---|
| 1177 | 36 | **type** `private static final class TypedGround` | A resource's ground as laid out by type: what it was laid out on (the land's stamp and every holding's listing), its pieces, its sites and tonnes by type, and its runs of one type in the order it is worked out. |
| 1186 | 26 | `TypedGround(long key, java.util.List<CityLand.Piece> pieces, Resource r)` _(in LandManager.TypedGround)_ |  |
| 1219 | 17 | `private TypedGround typed(Resource r)` | A resource's ground by type, laid out again when the land, or what a holding lists of the resource's sites or tonnes, has changed. |
| 1238 | 3 | `private static int typeIndex(Resource r, Deposit.FieldType type)` | The index of a type among a resource's (Resource.typesOf()), or -1 when it is not one of them. |
| 1243 | 7 | `public int getSites(Resource r, Deposit.FieldType type)` | The sites of a resource the city owns of one type (0.7.110): every type's for ANY (null) and for a resource of one type; none for a type not its own. |
| 1252 | 7 | `public double getOwnedAmount(Resource r, Deposit.FieldType type)` | What the city owns of a resource of one type, as listed (0.7.110): O of the type, the types adding up to getOwnedAmount(r). |
| 1268 | 17 | `public double[] getExtractedByType(Resource r)` | What has been taken out of a resource by type, in Resource.typesOf()'s order (0.7.110): as kept once a works has dug it by type, else the walk's reading of E (typeRuns()): of the first E tonnes in the order the ground... |
| 1287 | 7 | `public double getExtracted(Resource r, Deposit.FieldType type)` | What has been taken out of a resource of one type (0.7.110): its part of E (getExtractedByType()); E itself for ANY (null) and for a resource of one type. |
| 1296 | 7 | `public double getRemaining(Resource r, Deposit.FieldType type)` | What remains of a resource of one type, never below nothing (0.7.110): its owned tonnes less what has been taken out of it; getRemaining(r) for ANY (null) and a resource of one type. |
| 1311 | 6 | `public double[][] typeRuns(Resource r)` | The runs of a resource's ground of one type, each {end, type's index in Resource.typesOf()} in tonnes from its first, in the order it is worked out (0.7.110): what E by type is read from while every tonne has come out... |
| 1325 | 11 | `public double extract(Resource r, Deposit.FieldType type, double amount)` | Takes up to `amount` of a resource of one type out of the city's ground (0.7.110): what was there of the type, its E and the resource's rising by exactly that, so W holds. |
| 1338 | 8 | `private double[] byTypeKept(Resource r)` | A typed resource's E by type, kept from now on: the walk's reading of E the first time. |
| 1354 | 22 | `private void takeInWalkOrder(Resource r, double lifted)` | A dig on ANY once the resource keeps its E by type: `lifted` tonnes taken in the order the ground is worked out from each type's next unworked tonne, the earliest first, a run at a time - so a city that only ever dug ... |
| 1378 | 15 | `private static double[] nextUnworked(TypedGround g, int t, double taken)` | Type t's next unworked tonne as {its place in the walk, the tonnes to the end of its run}, `taken` of the type gone; null when the type is worked out. |
| 1395 | 11 | `private double[] typedRemainingByHolding(Resource r, double[] held)` | ...remainingByHolding() once the resource keeps its E by type: each type's E taken out of its own pieces in turn, holding by holding. |
| 1408 | 10 | `public double[][] getDepletionByTypeState()` | E by type, every typed resource's as kept, in Resource's order (DataSave's depletionByType): null while every one is the walk's reading of E, so a save carries it only once a works has dug by type. |
| 1425 | 12 | `public void restoreDepletionByType(double[][] saved)` | Puts E by type back from a save (Game's load path, after the land and E are in place): each typed resource's as saved; a resource the save carries none for - every resource of a save from before 0.7.110 - is the walk'... |
| 1439 | 1 | **type** `public record Claim(Deposit.FieldType type, int sites, int count)` | Standing works on a resource's sites, for fits(): the type they stand on (null for ANY), the sites each takes, and how many stand. |
| 1451 | 25 | `public boolean fits(Resource r, Deposit.FieldType type, java.util.List<Claim> standing, int extra)` | Whether a works of `extra` sites on `type` (null for ANY) fits on the city's ground of a resource beside the standing works (0.7.110; THE GROUND BY TYPE): the standing packed first fit decreasing onto the pieces (City... |
| 1489 | 27 | `public int[][] packed(Resource r, java.util.List<Claim> standing)` | Where fits()'s packing puts the standing works (0.7.131, batch I7): for each claim of `standing`, in its order, the sites its works take of each of the resource's types, in Resource.typesOf()'s order, summed over its ... |
| 1518 | 23 | `private static boolean placeInto(Resource r, java.util.List<CityLand.Piece> pieces, int[] free, int[] w, boolean spread, int[] ...` | place(), noting the sites it takes of each type in `byType`; a placement that fails whole takes nothing, a spread what there was. |
| 1543 | 4 | `private static int packingOrder(int[] a, int[] b)` | The packing's order: works on a type before works on ANY, the larger first within each. |
| 1555 | 20 | `private static boolean place(Resource r, java.util.List<CityLand.Piece> pieces, int[] free, int[] w, boolean spread)` | Places a works {type's index or -1, sites} on the first piece of its type with room, in acquisition order - ANY on the first type's pieces, then the next type's - taking its sites; or, `spread`, takes its sites from a... |
| 1577 | 3 | `public double getMarginPerSqFt()` | Margin per square foot at the current sale price. |
| 1590 | 3 | `public void setPricePerSqFt(double price)` | the load path can put back the price a saved month traded at before the market recomputes it, and so older callers still compile. |
| 1604 | 5 | `public void setOwnedSqFt(double sqFt)` | The city's dry ground, set by hand. |
| 1611 | 1 | `void restoreOwnedSqFt(double sqFt)` | The figure alone, the land as it stands: a load putting back what the save says, or a restatement that has just drawn the land to hold it. |
| 1612 | 1 | `public void setAllocatedSqFt(double sqFt)` |  |
| 1613 | 1 | `public void setBlocksPurchased(int blocks)` |  |
| 1617 | 3 | `public boolean canAllocate(double sqFt)` | Is there room to put this up at all? |
| 1622 | 3 | `public boolean canAllocate(double sqFt, String sector)` | ...for one sector's building (0.7.107): on the ground free for anyone and what its zone holds (freeFor()); a key with no zone, "" or null, the first. |
| 1632 | 3 | `public boolean allocate(double sqFt)` | Takes land out of the available pool. |
| 1642 | 10 | `public boolean allocate(double sqFt, String sector)` | ...for one sector's building (0.7.107): its zone's held ground first, recorded as the zone's used, then the ground free for anyone; and where the bare ground runs out it lies on open forest and clears it (clearFor(), ... |
| 1654 | 3 | `public void release(double sqFt)` | Frees land again: a scrapped business's plot (Game.retire()), and since 0.7.22 a finished demolition's ground (Game.settleConstructionControl()). |
| 1663 | 4 | `public void release(double sqFt, String sector)` | ...a sector's (0.7.107): out of its zone's used ground first, which the zone then holds again for it (spec-materials 2.15); the rest back to everyone. |
| 1669 | 3 | `public double priceFor(double sqFt)` | What a business pays the city for a plot this size. |
| 1678 | 5 | `public double priceFor(double sqFt, String sector)` | ...one sector's business (0.7.107, Z3): the part its zone holds at the zone's price (groundPriceFor()), the rest at the going price - the zone's ground first, as allocate() takes it. |
| 1690 | 5 | `public double buybackPriceFor(double sqFt, String sector)` | What the city pays a sector for a plot it gives back (0.7.107): the part out of its zone's used ground at the zone's price - the zone's ground trades at the zone's price both ways - the rest at the going price. |
| 1697 | 4 | `public void recordSale(double sqFt)` | Records a sale to a business. |
| 1703 | 4 | `public void recordSale(double sqFt, double price)` | ...at the price it was sold at (0.7.107: a zone's part at its price, priceFor(sqFt, sector)). |

### ZONES AND THE FOREST LAW (0.7.107, batch Z1; Jerus, 2026-10-08, the (lines 1708-1772)

| line | len | member | says |
|---:|---:|---|---|
| 1768 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |

### the ground, by kind (lines 1773-1822)

| line | len | member | says |
|---:|---:|---|---|
| 1776 | 1 | `public double getForestSqFt()` | The city's forest ground, in square feet: the forest class of its dry ground as drawn, cleared or standing (getForestKm2()). |
| 1779 | 1 | `public double getBareSqFt()` | ...its bare ground: the dry ground owned less the forest, never below nothing. |
| 1782 | 1 | `public double getProtectedForestSqFt()` | The forest the law protects: never cut, never built on (Z6). |
| 1785 | 1 | `public double getWorkingForestSqFt()` | ...the working forest: cut, never built on. |
| 1788 | 1 | `public double getOpenForestSqFt()` | ...and the open forest: cut or built on - all of it with the law at 0 and 0. |
| 1791 | 1 | `public double getForestClearedSqFt()` | Forest cleared for building, square feet (C): built on, or built on once. |
| 1794 | 1 | `public double getForestClearedM3()` | ...and the wood that stood or had been cut on it, cubic metres. |
| 1797 | 1 | `public double getForestClearedThisMonth()` | The standing wood cleared this month, cubic metres: lost until Forestry's salvage cut (I9). |
| 1799 | 3 | `private double protectedSqFt(Zoning z)` |  |
| 1804 | 3 | `private double keptFromBuildingSqFt(Zoning z)` | What the law keeps from building: the protected and the working forest. |
| 1808 | 3 | `private double openSqFt(Zoning z)` |  |
| 1813 | 1 | `public double getBuildableSqFt()` | The ground that may be built on: the dry ground owned less what the law keeps. |
| 1816 | 1 | `public double getFreeSqFt()` | Free ground, for anyone and the zones together: buildable less built, never below nothing. |
| 1818 | 4 | `private double freeSqFt(Zoning z)` |  |

### the zones (lines 1823-1926)

| line | len | member | says |
|---:|---:|---|---|
| 1826 | 15 | `private double[] heldByZone(Zoning z)` | What each zone holds, square feet, in Zoning.zones()'s order: its want - its size less what it has used - out of the free ground, every want scaled alike when the ground is short (Z10). |
| 1842 | 5 | `private double heldTotal(Zoning z)` |  |
| 1848 | 7 | `private double heldBy(Zoning z, String sector)` |  |
| 1856 | 4 | `private double freeForAnyone(Zoning z)` |  |
| 1862 | 1 | `public double heldBy(String sector)` | The free ground a sector's zone holds for it, square feet: its size less what it has used, as far as the free ground goes (Z10); 0 with no zone. |
| 1865 | 1 | `public double usedBy(String sector)` | ...and what its sector has built in it (used). |
| 1868 | 4 | `public double zoneSizeOf(String sector)` | What a sector's zone is to be, square feet: its share of the dry ground or its square kilometres; 0 with no zone. |
| 1874 | 1 | `public double heldByZones()` | What all the zones hold together. |
| 1877 | 1 | `public double freeForAnyone()` | The ground free for anyone: free, less what the zones hold. |
| 1880 | 1 | `public double freeFor(String sector)` | The ground a sector may build on: free for anyone, and what its zone holds for it. |
| 1882 | 4 | `private double freeFor(Zoning z, String sector)` |  |
| 1888 | 4 | `public double groundPriceFor(String sector)` | What a square foot of a sector's zone is sold and assessed at: its price share of the going business price (Z3, Z4); the going price with no zone. |
| 1898 | 8 | `public java.util.Map<String, double[]> zoneLand()` | The zones' ground, for the investors' month (Game.refreshLand(), BusinessInvestment.setZoneLand()): each zoned sector's {held square feet, price share}; empty with no zones. |
| 1913 | 2 | **type** `public record GroundUnder(double freeForAnyone, double heldByZones, double protectedSqFt, double workingSqF...` | THE GROUND UNDER ANOTHER ZONING (0.7.107), for PolicyPreview's before and after: a staged copy's free ground for anyone, a sector's held and free ground, and the forest's three kinds - the land as it stands, read thro... |
| 1917 | 6 | `public GroundUnder groundUnder(Zoning z)` | ...the whole of it. |
| 1925 | 1 | `public double heldUnder(Zoning z, String sector)` | ...and one sector's held ground under it. |

### the forest's wood (lines 1927-1946)

| line | len | member | says |
|---:|---:|---|---|
| 1930 | 1 | `public double getProtectedWood()` | The wood the law keeps standing, cubic metres: the protected share of the forest's full stock, from the fullest forest first, never more than stands (Z6, Z8). |
| 1932 | 4 | `private double protectedWood(Zoning z)` |  |
| 1943 | 3 | `public double cuttableForest()` | THE CUTTABLE FOREST (Z8, Z13), cubic metres: what stands less the protected wood - the working and the open forest's. |

### clearing (lines 1947-2012)

| line | len | member | says |
|---:|---:|---|---|
| 1957 | 20 | `private void clearFor(double sqFt, double fromZone, String sector)` | Clears the open forest an allocation lies on (Z7), asked before it is made: the zone's part on what its zone holds of the bare ground, the rest on the bare ground free for anyone - the bare and cleared ground not buil... |
| 1985 | 7 | `private void settleClearing()` | The month's floor (Z7): the cleared ground at least what the built ground past the bare ground needs, as far as the open forest goes - the same as allocate() leaves it, and what ground set by hand (a fixture, an older... |
| 2000 | 12 | `private void clear(double sqFt)` | Clears this much open forest, as far as it goes: cut-over ground first - its wood moves from E to C - then standing forest, whose wood is taken out into C, never past what may be cut (the protected wood stands on grou... |

### the save (lines 2013-2192)

| line | len | member | says |
|---:|---:|---|---|
| 2016 | 7 | `public Zoning.State zoningState()` | DataSave's zoning (0.7.107): the zones, the law and the forest cleared; null when nothing is set and nothing cleared, as an older save reads. |
| 2025 | 5 | `public void restoreZoning(Zoning.State s)` | Puts them back (Game's load path, after the land and its allocation): null, none, 0 and 0, nothing cleared. |
| 2046 | 4 | `public void recordBuyback(double sqFt)` | Records the city buying a plot back from a business that scrapped what stood on it. |
| 2052 | 4 | `public void recordBuyback(double sqFt, double price)` | ...at the price it was bought back at (0.7.107: a zone's part at its price, buybackPriceFor()). |
| 2062 | 3 | `public double buyBlock(double availableCash)` | Annexes one block. |
| 2076 | 13 | `public double buyBlock(double availableCash, long population)` | Buys the cheapest thing on offer. |
| 2097 | 5 | `public void endMonth()` | The month's end for the ground (0.7.57): forest's stored depletion grows back by FOREST_REGROWTH, then the month's flows are cleared. |
| 2104 | 10 | `public void clearMonth()` | Called once a month, after the government accounts have read the flows. |
| 2115 | 17 | `public void reset()` |  |
| 2135 | 26 | `public void printLandInfo()` | The land to the log, in the player's units since 0.7.68 (areaWords(), prices a square metre) - nothing in the game calls it; a console's view of the office. |
| 2164 | 4 | `static { ... }` |  |
| 2177 | 7 | `public void redenominate(double scale)` | The city's own land prices and this month's land flows, in the new unit. |
| 2187 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

