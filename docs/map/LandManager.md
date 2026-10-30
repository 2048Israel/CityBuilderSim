# LandManager.java - 3,113 lines · 276 methods · 13 constants · model

`ham/citybuildersim/LandManager.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

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
>               LAW; a zoned sector also builds on its zone's, freeFor());
>               since ET2 a building's is less, too, the steep ground only
>               a works may stand on (BUILDABLE BY SLOPE)
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
> ... (5 more lines in the source)

**Uses:** [Resource](Resource.md) (74), [CityLand](CityLand.md) (71), [World](World.md) (31), [Zoning](Zoning.md) (28), [Deposit](Deposit.md) (20), [LandParcel](LandParcel.md) (8), [LandMarket](LandMarket.md) (6), [Landmasses](Landmasses.md) (6), [Bridges](Bridges.md) (5), [HydroSites](HydroSites.md) (3), [ForeignAccounts](ForeignAccounts.md) (3), [Founding](Founding.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (1), [GridOffers](GridOffers.md) (1), [LandConversion](LandConversion.md) (1), [SitePreparation](SitePreparation.md) (1)

**Used by (86):** [Agriculture](Agriculture.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingVisual](BuildingVisual.md), [BusinessInvestment](BusinessInvestment.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [EconomyManager](EconomyManager.md), [ExtractionSector](ExtractionSector.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [GridCheck](GridCheck.md), [HealthCheck](HealthCheck.md), [HistoryScreen](HistoryScreen.md), [HydroCheck](HydroCheck.md), [Inbox](Inbox.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureManager](InfrastructureManager.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LandConversion](LandConversion.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [Landmasses](Landmasses.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [Money](Money.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OreView](OreView.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Resource](Resource.md), [RoadCheck](RoadCheck.md), [RunWorks](RunWorks.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [SitePreparation](SitePreparation.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [Zoning](Zoning.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 287 | · the ground |
| 388 | THE LINKED VIEW OF THE GROUND (ET5, 2026-10-25; the ET5 design, D6-D7) |
| 560 | · ore |
| 680 | · the market |
| 799 | · ore |
| 855 | · the oil by grade, as it is worked out (0.7.79, batch O3; spec-oil 2.2) |
| 1014 | THE TWO OIL POOLS (0.7.93, batch O10b; Jerus, 2026-10-09: "each oil |
| 1349 | THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) |
| 2065 | BUILDABLE BY SLOPE (ET2, 2026-10-24; ET4, 2026-10-25; spec-earth2-terrain 3.2, 4.2, 4.3, 4.4) |
| 2311 | A PRICE FOR EACH CLASS OF GROUND (ET3, 2026-10-25; spec-earth2-terrain 5.1, 5.2) |
| 2487 | ZONES AND THE FOREST LAW (0.7.107, batch Z1; Jerus, 2026-10-08, the |
| 2564 | · the ground, by kind |
| 2617 | · the zones |
| 2785 | · the forest's wood |
| 2805 | · clearing |
| 2873 | · the salvage cut and the planted stands (0.7.138, batch I9) |
| 2918 | · the save |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2157 | `LandManager.Use.BUILDING` |  |
| 2157 | `LandManager.Use.WORKS` |  |
| 2157 | `LandManager.Use.RUN` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `LandManager.BLOCK_SQ_FT` | `100000` | One city block, in square feet. |
| 79 | `LandManager.SQ_M_PER_SQ_FT` | `0.09290304` | One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. |
| 82 | `LandManager.SQ_M_PER_KM2` | `1_000_000` | Square metres in a square kilometre. |
| 93 | `LandManager.SQ_FT_PER_KM2` | `SQ_M_PER_KM2 / SQ_M_PER_SQ_FT` | Square feet in a square kilometre: a million square metres over a square foot's, 10,763,910.4 (0.7.57). |
| 112 | `LandManager.M2_WORDS_BELOW` | `10_000` | Areas under this many square metres read in square metres (0.7.68): a hundredth of a square kilometre, so a building's plot reads "743 m\u00b2" (a House's 8,000 square feet) and not "0.000743 km\u00b2", while the grid... |
| 176 | `LandManager.STARTING_SQ_FT` | `3000000` | Land the city starts with - thirty blocks, about 69 acres; since 0.7.67 the figure a new city's centre of whole blocks is drawn to hold, and it owns the dry plots drawn, a little more (CityLand.found(): 3,051,569 sq f... |
| 185 | `LandManager.FOUNDING_FRESH_UNITS` | `60_000` | A new city's fresh water, in units a month: what one Water Treatment Plant treats, its 60,000 units of 10,000 gallons (buildings.json id 10's production1) - Jerus's A24 (0.7.106, 2026-10-10: "found with fresh water fo... |
| 188 | `LandManager.FOUNDING_FRESH_KM2` | `FOUNDING_FRESH_UNITS / UtilitiesHandler.FRESH_UNITS_PER_KM2` | ...as lake and river ground in its centre, in square kilometres: FOUNDING_FRESH_UNITS over what one yields a month (UtilitiesHandler.FRESH_UNITS_PER_KM2), 0.4934 km2 (CityLand.found()). |
| 240 | `LandManager.DEFAULT_PRICE_PER_SQ_FT` | `.001` | Opening sale price, $1/sq ft - a 43% margin on what the city pays. |
| 327 | `LandManager.FOREST_REGROWTH` | `1.0 / 240` | Forest's stored depletion falls by this share a month: 1/240, a twenty-year time constant, so 95% of what is cut grows back within a sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). |
| 1045 | `LandManager.TWO_POOLS_FORMAT` | `35` | The first save format that carries the two pools: its depletion's oil the ground pool's, and the offshore pool's E beside it. |
| 2562 | `LandManager.CLEARING_ROUNDING` | `1e-9` | A share of the city's dry ground that is a rounding and not ground: what the month's clearing ignores past the built ground's own (settleClearing()). |
| 3083 | `LandManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 243 | `private double defaultPricePerSqFt` | The same, in today's money - struck at the expected price level since 0.7.42 (seedConstants()). |
| 245 | `private double ownedSqFt` |  |
| 246 | `private double allocatedSqFt` |  |
| 249 | `private double worksSqFt` | The part of allocatedSqFt that works stand on (ET2, BUILDABLE BY SLOPE): the mines, quarries and wells that may stand on any ground (Game.standsAnywhere()). |
| 252 | `private double runsSqFt` | The part of allocatedSqFt that highways and railway lines stand on (ET4, BUILDABLE BY SLOPE): a third use of the one pool, spread over every class of ground in the city's proportions (Game.isRun()). |
| 255 | `private double preparedSqFt` | The foothill prepared so far for buildings, square feet (ET2): it never falls, a pad once cut staying cut. |
| 258 | `private int blocksPurchased` | How many purchases the city has made: blocks once, parcels since, offers since 0.7.57 (DataSave's landBlocksPurchased). |
| 273 | `private double pricePerSqFt` | What businesses pay per square foot. |
| 276 | `private final LandMarket market` | Twenty-four offers standing, six a side, and what the ground costs. |
| 279 | `private final Landmasses landmasses` | The city's ground by landmass and the landmasses it has linked (ET5): derived from the land and the Earth's data, never saved here; a bare one is a single landmass, and every figure below is then the old one. |
| 282 | `private final Bridges bridges` | The city's long bridges, built and building (ET6): saved whole, and the joins the linked set is closed under besides the ports (Landmasses.Joins). |
| 285 | `private final HydroSites hydro` | The hydro sites on the city's ground and on the landmasses it has linked (HY1): derived from the land and the Earth's data, never saved here; a generated world has none. |
| 295 | `private CityLand land` |  |
| 298 | `private final java.util.function.Supplier<World.Key> worldKey` | The world the city stands on - Founding.getWorldKey() (its seed's, Founding.getWorldSeed(), until 0.7.115), read when the land is founded. |
| 301 | `private final java.util.function.IntSupplier month` | The month, for each offer listed and each purchase made. |
| 311 | `private final double[] extracted` | What the city has taken out of its ground, a resource at a time in Resource's order: E, the third term of the world's conservation (spec-land 2.1) - what remains is the land's amount less this, worked out in acquisiti... |
| 314 | `private double extractedAtSea` | E of the offshore pool (0.7.93, THE TWO OIL POOLS): what the platform wells have lifted from the oil under the city's sea. |
| 317 | `private double[] worldTotals` | The world's totals and its sea's level, as stored when the city was founded or converted: so a load needs no pass over the world. |
| 318 | `private double worldSeaTheta` |  |
| 333 | `private final java.util.function.DoubleSupplier rate` | Local money per US dollar today - ForeignAccounts.getRate(), read live (the shape CentralBank reads the vault in). |
| 340 | `private final java.util.function.DoubleSupplier usPrices` | The world's price level - WorldEconomy.getPriceLevel(), US prices against the founding's - read live like the rate (0.7.55): what the world's dollar price for ground follows. |
| 573 | `private double ironMinedThisMonth` | Lifted this month, for the mining report: since 0.7.124 the tonnes of ground taken out, a magnetite works' rock as dug. |
| 576 | `private double oilLiftedThisMonth` | The crude the wells lifted this month, in tonnes (0.7.62) - both pools' since 0.7.93. |
| 579 | `private final double[] liftedByGrade` | ...by grade, in Deposit.Grade's order (0.7.79): what extractOil()'s walk of the fields found it was - and since 0.7.93 extractOilAtSea()'s of the sea's. |
| 582 | `private double landSalesThisMonth` | Monthly flows, for the government accounts. |
| 583 | `private double sqFtSoldThisMonth` |  |
| 584 | `private double landPurchasesThisMonth` |  |
| 585 | `private double sqFtBoughtBackThisMonth` |  |
| 870 | `private double[] oilRunEnds` | Where each run of one grade ends, tonnes from the first of the ground pool's oil, and its grade's ordinal. |
| 871 | `private byte[] oilRunGrades` |  |
| 873 | `private double[] seaRunEnds` | ...and the offshore pool's (0.7.93). |
| 874 | `private byte[] seaRunGrades` |  |
| 876 | `private double[] seaByHolding` | Each holding's part of the offshore pool, tonnes in acquisition order, and the pool's whole (0.7.93): laid out with the runs. |
| 877 | `private double oilOwnedAtSea` |  |
| 879 | `private CityLand oilRunsLand` | What the runs were laid out for: the land, its stamp and what each holding listed. |
| 880 | `private long oilRunsKey` |  |
| 896 | `double[] ends` |  |
| 897 | `byte[] grades` |  |
| 898 | `int n` |  |
| 1111 | `private final java.util.Map<Resource, long[]> seaSitesKept` | The sea sites by resource, kept with the ground they were counted on. |
| 1112 | `private CityLand seaSitesLand` |  |
| 1143 | `private final java.util.Map<Resource, java.util.List<SeaField>> seaFieldsKept` | The sea fields by resource, kept with the ground they were listed on and its key. |
| 1144 | `private final java.util.Map<Resource, Long> seaFieldsKey` |  |
| 1145 | `private CityLand seaFieldsLand` |  |
| 1396 | `private final double[][] extractedByType` | What has been taken out of each typed resource by type, in Resource.typesOf()'s order, once a works has dug it by type; null while E by type is the walk's reading of E. |
| 1400 | `final long key` |  |
| 1401 | `final java.util.List<CityLand.Piece> pieces` |  |
| 1402 | `final long[] sites` |  |
| 1403 | `final double[] amounts` |  |
| 1405 | `final double[] ends` | Where each run of one type ends, tonnes from the first of the resource's, and its type's index in Resource.typesOf(). |
| 1406 | `final int[] types` |  |
| 1437 | `private final TypedGround[] typedKept` | The ground by type, kept by resource with the land it was laid out on. |
| 1438 | `private CityLand typedLand` |  |
| 1818 | `private final long[] unlinkedSitesBy` | The sites of each resource on fields centred on a landmass the city has not linked, by Resource's ordinal (ET5 D9): worked out by refreshUnlinkedSites() when the land changes, never by a read. |
| 1821 | `private final long[][] unlinkedSitesByType` | ...and by type, in Resource.typesOf()'s order (null for a resource with no field). |
| 2338 | `private java.util.function.DoubleSupplier preparation` | The foothill's preparation a square foot, thousands of local money (setPreparationPerSqFt()); null while the owner has wired none. |
| 2536 | `private final Zoning zoning` | The city's zones and its forest law (0.7.107): empty for a new city - the ground free for anyone, as it always was. |
| 2539 | `private double clearedSqFt` | Forest cleared for building, in square feet of ground (C's area, Z7): never regrowing, never un-cleared. |
| 2542 | `private double clearedM3` | ...and its wood, in cubic metres: the cut-over wood it took from E and the standing wood it took out (C). |
| 2545 | `private double forestClearedThisMonth` | The standing wood cleared this month, cubic metres: what the month cleared for Forestry's salvage cut since materials I9 (takeSalvage() hands it over), lost until then. |
| 2553 | `private double salvagePendingM3` | The standing wood cleared and not yet sold, cubic metres (0.7.138, batch I9): what Forestry's next salvage cut takes (takeSalvage()) - the month's clearing, and a clearing between two presses or past the month's marke... |
| 2556 | `private double plantedGrowthThisMonth` | The planted stands' growth this month, cubic metres (0.7.138, batch I9): Forestry's plantations and woodlots, taken off E at the month's end with the regrowth (THE PLANTED STANDS). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 3041 | **type** `public class LandManager` | The city's land: what it owns, what is built on, and what it sells. |
| 90 | 1 | `public static double km2(double sqFt)` | An area in square feet, in square kilometres (0.7.13): what the land office shows in place of blocks. |
| 96 | 1 | `public static double sqFt(double km2)` | An area in square kilometres, in square feet: what an offer's dry ground adds to the city's (0.7.57). |
| 105 | 5 | `public static String km2Words(double sqFt)` | ...written to three significant figures, so a small area does not read "0.00", and the largest reads no more digits than a player compares plots by: 0.00929, 0.214, 2.79, 27.9, each with its unit - grouped from a thou... |
| 123 | 8 | `public static String areaWords(double sqFt)` | An area the player reads (0.7.68, spec-grid 2.5 and star 10): in square metres below M2_WORDS_BELOW - "743 m\u00b2", "7,430 m\u00b2" - and in square kilometres from it, km2Words(): "0.0301 km\u00b2", "1,760,000 km\u00... |
| 138 | 5 | `public static String partFigure(double partSqFt, double wholeSqFt)` | A part of an area as a bare figure in the unit its whole reads in (0.7.68): the "0.0273" of "0.0288 km\u00b2, 0.0273 dry", the "6,900" of "7,200 m\u00b2, 6,900 dry" - so a line never reads one unit and means another. |
| 145 | 5 | `static String threeFigures(java.math.BigDecimal v)` | A figure to three significant figures, grouped from a thousand: "0.0301", "76.2", "7,430", "1,760,000". |
| 158 | 1 | `public static double perM2(double perSqFt)` | A price a square foot, a square metre (0.7.68): a square metre is 1 / SQ_M_PER_SQ_FT = 10.76 square feet, so its ground costs that many times as much. |
| 191 | 3 | `public static CityLand foundingLand(long worldSeed)` | The land a new city is founded with on the world of this seed (0.7.106): the centre land() founds - STARTING_SQ_FT's rings and FOUNDING_FRESH_KM2 of the founding lake - drawn apart from any city, for what is priced on... |
| 196 | 4 | `public static CityLand foundingLand(World.Key worldKey)` | ...on the world a key names (0.7.115). |

### the ground (lines 287-387)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 3 | `public LandManager()` | A land office on its own, at the founding rate, on the default world - what the harnesses build. |
| 348 | 3 | `public LandManager(java.util.function.DoubleSupplier rate)` | The city's land office, converting at the rate this reads, at the founding's US prices. |
| 353 | 3 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices)` | ...and with the world's price level this reads (0.7.55): the city's. |
| 358 | 4 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices, java.util.function.LongS...` | ...on the world this seed reads, in the month this reads (0.7.57): the city's until 0.7.115, a harness's since (the city's reads its key, below). |
| 364 | 9 | `public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices, java.util.function.Suppl...` | ...on the world this key reads (0.7.115, batch E2): the city's, Founding.getWorldKey(). |
| 374 | 1 | `private int month()` |  |
| 377 | 4 | `private double rate()` | The rate the office converts at today; the founding rate if the reading is not a price. |
| 383 | 4 | `private double usPrices()` | The world's price level today; the founding's 1 if the reading is not a price. |

### THE LINKED VIEW OF THE GROUND (ET5, 2026-10-25; the ET5 design, D6-D7) (lines 388-559)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 3 | `private double linkedOwned()` | The dry ground the city owns that is room for something: the owned less the unlinked landmasses' dry ground, square feet; the owned itself while nothing is unlinked. |
| 417 | 3 | `private double linkedAllocated()` | The ground built on that is on linked ground: the allocated less the ports standing on unlinked landmasses, square feet; the allocated itself while nothing is unlinked. |
| 422 | 3 | `private double linkedRelief(int relief)` | The owned ground of one relief (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP) on linked landmasses, square feet; reliefSqFt() itself while nothing is unlinked. |
| 427 | 3 | `private double linkedForest()` | The forest on linked landmasses, square feet; getForestSqFt() itself while nothing is unlinked. |
| 432 | 3 | `private double linkedSlopeSqFt()` | The steep and very steep ground on linked landmasses, square feet. |
| 437 | 1 | `public double getLinkedOwnedSqFt()` | The dry ground that is room for something (ET5): the owned less the unlinked landmasses' - getOwnedSqFt() while nothing is unlinked. |
| 440 | 1 | `public double getLinkedAllocatedSqFt()` | The built ground that is on linked landmasses (ET5): getAllocatedSqFt() while nothing is unlinked. |
| 443 | 1 | `public double getLinkedForestSqFt()` | The forest on linked landmasses (ET5): getForestSqFt() while nothing is unlinked. |
| 446 | 1 | `public double getUnlinkedFreeSqFt()` | The dry ground the city owns on landmasses it has not linked and nothing stands on, square feet (ET5): 0 while nothing is unlinked. |
| 449 | 1 | `public String getUnlinkedNames()` | The unlinked landmasses' names for a sentence, "Island 2" or "Island 2 and Island 3"; empty while nothing is unlinked. |
| 452 | 1 | `public Landmasses getLandmasses()` | The city's ground by landmass (ET5): its books, which it has linked, and what is unlinked. |
| 455 | 1 | `public HydroSites getHydroSites()` | The city's hydro sites (HY1): where a river and a drop lie on the ground it owns, and which of them are on linked landmasses. |
| 458 | 1 | `public Bridges getBridges()` | The city's long bridges (ET6): where each runs, between which landmasses, and whether it stands. |
| 465 | 6 | `void landChanged()` | The land changed (ET5): counted by landmass again, the offers standing split against the linked set again, the sites of the fields across the water worked out again. |
| 477 | 7 | `void linkChanged()` | The linked set may have moved (ET5): the linked set and the unlinked ground worked out again, the sites across the water, the offers standing split against them. |
| 491 | 10 | `void portsChanged()` | A sea terminal's placement moved (ET5, slice B): ordered, stopped, restarted, demolished or fitted to the buildings. |
| 507 | 3 | `public boolean canAllocateOn(int landmass, double sqFt)` | Whether a sea terminal's ground fits an island no port links (ET5): its room is that island's own flat and foothill ground less the ports already on it (Landmasses.portRoomSqFt()), not the linked pool's - the only bui... |
| 517 | 3 | `public void allocateOn(int landmass, double sqFt)` | Takes a terminal's ground on an island no port links: the allocated ground rises by it and nothing else - no zone's ground, no forest cleared, no pad prepared (the pad is priced in the quote and owed on the site; ET5-... |
| 522 | 3 | `public Landmasses.State landmassState()` | DataSave's landmasses (ET5): where the terminals stand; null when none stands off home, as an older save reads. |
| 527 | 3 | `public Bridges.State bridgeState()` | DataSave's bridges (ET6): every bridge built or building; null when the city has none, as an older save reads. |
| 532 | 4 | `public void restoreBridges(Bridges.State s)` | Puts the bridges back (Game's load path, before the placements, which close the linked set over them): fitted to the buildings' counts. |
| 538 | 5 | `public void restoreLandmasses(Landmasses.State s)` | Puts the placements back (Game's load path, after the buildings and the land): fitted to the buildings' counts, and the linked set and the unlinked sums worked out again. |
| 550 | 9 | `LandParcel buyRectForTest(long x0, long y0, long x1, long y1, int month)` | Harnesses only (ET5 D17): the city buys a rectangle of plots as if an offer of it had been listed and bought - its ground counted as an offer's is (LandMarket.measure()), added to the books through buyParcel()'s own s... |

### ore (lines 560-679)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 1 | `public double getOwnedSqFt()` | getters |
| 589 | 1 | `public double getAllocatedSqFt()` |  |
| 598 | 3 | `public double getAvailableSqFt()` | The ground free for anyone: what is not built on, less what the forest law keeps from building and what the zones hold (0.7.107, ZONES AND THE FOREST LAW) - with no zones and the law at 0 and 0, owned less allocated, ... |
| 603 | 3 | `public double getUtilisation()` | How full the city is. |
| 616 | 9 | `public double getUsedShare()` | The share of the city's ground built on, unclamped (ET2): allocated over owned, as it always was - and where the city owns steep ground, the share of the ground a building may use, the built ground less the works on t... |
| 626 | 1 | `public double getAvailableBlocks()` |  |
| 627 | 1 | `public int getBlocksPurchased()` |  |
| 628 | 1 | `public double getPricePerSqFt()` |  |
| 630 | 1 | `public double getLandSalesThisMonth()` |  |
| 631 | 1 | `public double getSqFtSoldThisMonth()` |  |
| 632 | 1 | `public double getLandPurchasesThisMonth()` |  |
| 633 | 1 | `public double getSqFtBoughtBackThisMonth()` |  |
| 636 | 3 | `public double getNextBlockCost()` | What a block's worth of land costs at today's market rate, in thousands of local money. |
| 645 | 3 | `public double getAcquisitionCostPerSqFt()` | Ground price per square foot the city would pay today, in local money: the world's dollar price (getGroundUsdPerSqFt()) at today's rate, since 0.7.6. |
| 656 | 7 | `public double getOfficePricePerSqFt()` | What the land office would charge the city a square foot today, in local money (0.7.51): the best value on its listing (LandMarket.bestValue()) at today's rate, a square foot of its dry ground - or the acquisition cos... |
| 665 | 3 | `public double getGroundUsdPerSqFt()` | The same ground price in the money it is asked in: thousands of US dollars (0.7.6). |
| 676 | 1 | `public double getCrowding()` | THE PRICE'S PARTS (0.7.55), as the ground was last priced: the dollar price is LandMarket.openingUsdPerSqFt() x getUsPriceLevel() x getCrowdingPremium(), and the premium reads getCrowding() - the city's people per squ... |
| 677 | 1 | `public double getCrowdingPremium()` |  |
| 678 | 1 | `public double getUsPriceLevel()` |  |

### the market (lines 680-798)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 1 | `public LandMarket getMarket()` |  |
| 683 | 1 | `public java.util.List<LandParcel> getListing()` |  |
| 686 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and its purchases, whole blocks since 0.7.67 - founded round the world's founding site the first time it is asked for. |
| 699 | 9 | `CityLand land()` | The land, founded if it is not yet: a centre of whole blocks round the founding site of the seed's world holding at least the dry ground owned (CityLand.found()), nothing taken out of it - and since 0.7.67 the figure ... |
| 710 | 13 | `void install(CityLand land, double[] extracted, double[] totals, double seaTheta)` | Puts a city's land in place - founded, converted or saved - with what has been taken out of it and the world's totals and sea level; the office's shelf cleared for update() to list. |
| 725 | 1 | `public boolean hasCityLand()` | Whether the land has been founded or put back yet - a bare office's has not until something asks. |
| 728 | 1 | `public double getLandDrySqFt()` | The city's dry ground, centre and purchases, as the land measures it, in square feet: what getOwnedSqFt() is checked against on a load. |
| 731 | 1 | `public double getOwnedKm2()` | The city's whole area, dry ground and water, in square kilometres (0.7.57). |
| 734 | 1 | `public double getFreshKm2()` | ...its fresh water: lakes and the founding river. |
| 737 | 1 | `public double getSeaKm2()` | ...its sea. |
| 740 | 1 | `public double getForestKm2()` | ...and its forest, part of its dry ground. |
| 747 | 5 | `public void updateMarket(long population)` | Re-prices the market and refills the window. |
| 767 | 31 | `public double buyParcel(int parcelId, double availableCash, long population)` | Buys one listed offer. |

### ore (lines 799-854)

| line | len | member | says |
|---:|---:|---|---|
| 802 | 1 | `public int getIronDeposits()` | The iron sites the city owns, centre and purchases: how many Iron Mines can stand. |
| 805 | 1 | `public double getIronReserveTonnes()` | The iron still in its ground, in tonnes: O - E. |
| 806 | 1 | `public double getIronMinedThisMonth()` |  |
| 809 | 5 | `public int getSites(Resource r)` | The sites of a resource the city owns, centre and purchases (0.7.62): how many of its mines or wells can stand. |
| 816 | 1 | `public int getOilSites()` | The oil sites the city owns (0.7.62): how many Oil Wells can stand. |
| 819 | 1 | `public double getOilReserveTonnes()` | The crude still in its ground, in tonnes: O - E - since 0.7.93 both pools', the ground's and the sea's. |
| 821 | 1 | `public double getOilLiftedThisMonth()` | The crude the wells lifted this month, both pools' (0.7.93). |
| 830 | 7 | `public double extractOil(double tonnes)` | Lifts crude out of the ground (0.7.62): what was there, as extractIron() does for ore - and since 0.7.79 (batch O3) of which grades: the walk of the city's oil from E to E + lifted (oilRuns()) adds each grade's part t... |
| 843 | 8 | `public double extractOilAtSea(double tonnes)` | ...and out of the offshore pool, the platform wells' (0.7.93, THE TWO OIL POOLS): what was there, its E rising by exactly that, graded by the walk of the sea's oil from its E (oilRunsAtSea()). |
| 853 | 1 | `public double[] getOilLiftedByGrade()` | The crude the wells lifted this month by grade, tonnes, in Deposit.Grade's order (0.7.79): the refinery's local crude is of this mix (Refining's crude mix). |

### the oil by grade, as it is worked out (0.7.79, batch O3; spec-oil 2.2) (lines 855-1013)

| line | len | member | says |
|---:|---:|---|---|
| 883 | 5 | `private static long groundKey(CityLand l, double[] listed)` | A key for what the ground holds of a resource: the land's stamp and every holding's listed amount, bit for bit. |
| 890 | 3 | `private static boolean atSea(World world, CityLand.Held f)` | Whether a field the city holds is the sea's: its centre plot in the sea (World.depthAt() over 0), as getSites(r, false) counts it. |
| 895 | 46 | **type** `private static final class Runs` | Runs of one grade as they are laid out: each run's end and grade's ordinal, a run that continues the last one's grade merged into it. |
| 900 | 12 | `void add(double end, byte grade)` _(in LandManager.Runs)_ |  |
| 919 | 21 | `double lay(java.util.List<CityLand.Held> fields, World world, boolean sea, double amount, double upTo)` _(in LandManager.Runs)_ | One holding's part of a pool, `amount` tonnes from `upTo`: its fields of the pool (the sea's when `sea`, else the dry ones) in order, each as far as the part goes, and past them MEDIUM - the ground's oil by fiat. |
| 942 | 33 | `private void layOilRuns()` |  |
| 977 | 4 | `public double[][] oilRuns()` | The runs of the ground pool's oil, each {end, grade's ordinal} in tonnes from its first, in the order it is worked out (0.7.79; the one pool's until 0.7.93). |
| 983 | 4 | `public double[][] oilRunsAtSea()` | ...and the offshore pool's (0.7.93): the sea fields', in the order they are worked out. |
| 988 | 5 | `private static double[][] runsOf(double[] ends, byte[] grades)` |  |
| 995 | 18 | `private void gradeTheLift(boolean atSea, double from, double amount)` | Adds the grades of a pool's oil - the offshore pool's when `atSea` - from `from` to `from + amount` tonnes into the month's liftedByGrade; past the last run (a rounding), MEDIUM. |

### THE TWO OIL POOLS (0.7.93, batch O10b; Jerus, 2026-10-09: "each oil (lines 1014-1348)

| line | len | member | says |
|---:|---:|---|---|
| 1048 | 4 | `public double getOilOwnedAtSea()` | The offshore pool's tonnes as listed: each holding's sea fields', at most what it listed (O of the sea's oil). |
| 1054 | 3 | `public double getOilOwnedOnGround()` | ...and the ground pool's: what the city owns of oil less the sea's - its dry fields and any oil by fiat. |
| 1059 | 1 | `public double getOilExtractedOnGround()` | What the land wells have lifted from the ground pool (its E). |
| 1062 | 1 | `public double getOilExtractedAtSea()` | ...and the platform wells from the offshore pool. |
| 1065 | 3 | `public double getOilLeftOnGround()` | The crude left in the ground pool, never below nothing: what the land wells can still lift. |
| 1070 | 3 | `public double getOilLeftAtSea()` | ...and in the offshore pool: what the platform wells can still lift. |
| 1081 | 10 | `public void restoreOilPools(Double atSea)` | Puts the offshore pool's E back from a save (Game's load path, after the land is in place): `atSea` as saved, in format TWO_POOLS_FORMAT and later. |
| 1101 | 8 | `public int getSites(Resource r, boolean dry)` | The sites of a resource the city owns on dry ground or in the sea (0.7.79, batch O3; spec-oil 2.6): a field's sites are the sea's when its centre plot is sea (World.depthAt() over 0), else dry - a lake or the river co... |
| 1114 | 24 | `private long seaSites(CityLand l, Resource r)` |  |
| 1140 | 1 | **type** `public record SeaField(Deposit field, int sites, double depth)` | A field of a resource whose centre is in the sea, the sites of it the city owns, and its depth there in metres (0.7.91). |
| 1156 | 42 | `public java.util.List<SeaField> seaFields(Resource r)` | The fields of a resource the city owns whose centre is in the sea (0.7.91, batch O10; spec-oil 2.7): each field once, its owned sites summed over the holdings that hold them (CityLand.heldFields()), in the order the h... |
| 1199 | 3 | `public boolean hasUnminedDeposit(int minesStanding)` |  |
| 1211 | 5 | `public double extractIron(double tonnes)` | Lifts ore out of the ground. |
| 1225 | 5 | `public double extractIron(Deposit.FieldType type, double tonnes, boolean more)` | ...of one type (0.7.124, batch I5; THE GROUND BY TYPE): up to `tonnes` of the type's ground taken out (extract(r, type, amount)) - rock, for a magnetite works, three tonnes of it to a tonne of concentrate - and kept f... |
| 1236 | 10 | `public double extract(Resource r, double amount)` | Takes up to `amount` of a resource out of the city's ground (0.7.57): what was there, and E rises by exactly that. |
| 1248 | 1 | `public double getOwnedAmount(Resource r)` | What the city owns of a resource as listed - its centre's and every purchase's: O. |
| 1251 | 4 | `public double getExtracted(Resource r)` | What it has taken out: E - oil's both pools' since 0.7.93, and forest's the wood cleared for building with it since 0.7.107 (C, never regrowing), so W = unowned + remaining + taken out holds. |
| 1257 | 5 | `public double getRemaining(Resource r)` | What remains in its ground: O - E, never below nothing - oil's each pool's, added (0.7.93); forest's O - C - E (0.7.107). |
| 1264 | 1 | `public double getWorldTotal(Resource r)` | What the world holds of it, as stored when the city was founded or converted: W. |
| 1267 | 1 | `public double getUnowned(Resource r)` | What no city owns of it: W - O. |
| 1270 | 1 | `public double getWorldSeaTheta()` | The world's sea level, as stored with its totals. |
| 1273 | 1 | `public double[] getDepletionState()` | What has been taken out, every resource in Resource's order (DataSave's depletion) - oil's the ground pool's since 0.7.93, the offshore pool's getOilExtractedAtSea(). |
| 1276 | 1 | `public double[] getWorldTotalsState()` | The world's totals as stored, every resource in Resource's order (DataSave's worldTotals). |
| 1284 | 14 | `public double[] remainingByHolding(Resource r)` | What remains of a resource in each holding, in acquisition order - the centre, then each purchase (spec-land star 12): E is taken out of the first until it is worked out, then the next, so a holding's remainder is wha... |
| 1300 | 12 | `private double[] oilRemainingByHolding(double[] held)` | ...oil's since 0.7.93: each pool's E taken out of its own part of each holding in turn, the two parts' remainders added. |
| 1321 | 3 | `public void restoreIron(int deposits, double reserveTonnes)` | A FIXTURE'S ORE: the city holds exactly `deposits` iron sites and `reserveTonnes` in the ground. |
| 1326 | 22 | `public void restoreSites(Resource r, int deposits, double reserveTonnes)` | ...the same for any resource in fields (0.7.62): a fixture's oil, as restoreIron() is its ore. |

### THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) (lines 1349-2064)

| line | len | member | says |
|---:|---:|---|---|
| 1399 | 36 | **type** `private static final class TypedGround` | A resource's ground as laid out by type: what it was laid out on (the land's stamp and every holding's listing), its pieces, its sites and tonnes by type, and its runs of one type in the order it is worked out. |
| 1408 | 26 | `TypedGround(long key, java.util.List<CityLand.Piece> pieces, Resource r)` _(in LandManager.TypedGround)_ |  |
| 1441 | 17 | `private TypedGround typed(Resource r)` | A resource's ground by type, laid out again when the land, or what a holding lists of the resource's sites or tonnes, has changed. |
| 1460 | 3 | `private static int typeIndex(Resource r, Deposit.FieldType type)` | The index of a type among a resource's (Resource.typesOf()), or -1 when it is not one of them. |
| 1465 | 8 | `public int getSites(Resource r, Deposit.FieldType type)` | The sites of a resource the city owns of one type (0.7.110): every type's for ANY (null) and for a resource of one type; none for a type not its own. |
| 1475 | 7 | `public double getOwnedAmount(Resource r, Deposit.FieldType type)` | What the city owns of a resource of one type, as listed (0.7.110): O of the type, the types adding up to getOwnedAmount(r). |
| 1491 | 17 | `public double[] getExtractedByType(Resource r)` | What has been taken out of a resource by type, in Resource.typesOf()'s order (0.7.110): as kept once a works has dug it by type, else the walk's reading of E (typeRuns()): of the first E tonnes in the order the ground... |
| 1510 | 7 | `public double getExtracted(Resource r, Deposit.FieldType type)` | What has been taken out of a resource of one type (0.7.110): its part of E (getExtractedByType()); E itself for ANY (null) and for a resource of one type. |
| 1519 | 7 | `public double getRemaining(Resource r, Deposit.FieldType type)` | What remains of a resource of one type, never below nothing (0.7.110): its owned tonnes less what has been taken out of it; getRemaining(r) for ANY (null) and a resource of one type. |
| 1534 | 6 | `public double[][] typeRuns(Resource r)` | The runs of a resource's ground of one type, each {end, type's index in Resource.typesOf()} in tonnes from its first, in the order it is worked out (0.7.110): what E by type is read from while every tonne has come out... |
| 1548 | 11 | `public double extract(Resource r, Deposit.FieldType type, double amount)` | Takes up to `amount` of a resource of one type out of the city's ground (0.7.110): what was there of the type, its E and the resource's rising by exactly that, so W holds. |
| 1561 | 8 | `private double[] byTypeKept(Resource r)` | A typed resource's E by type, kept from now on: the walk's reading of E the first time. |
| 1577 | 22 | `private void takeInWalkOrder(Resource r, double lifted)` | A dig on ANY once the resource keeps its E by type: `lifted` tonnes taken in the order the ground is worked out from each type's next unworked tonne, the earliest first, a run at a time - so a city that only ever dug ... |
| 1601 | 15 | `private static double[] nextUnworked(TypedGround g, int t, double taken)` | Type t's next unworked tonne as {its place in the walk, the tonnes to the end of its run}, `taken` of the type gone; null when the type is worked out. |
| 1618 | 11 | `private double[] typedRemainingByHolding(Resource r, double[] held)` | ...remainingByHolding() once the resource keeps its E by type: each type's E taken out of its own pieces in turn, holding by holding. |
| 1631 | 10 | `public double[][] getDepletionByTypeState()` | E by type, every typed resource's as kept, in Resource's order (DataSave's depletionByType): null while every one is the walk's reading of E, so a save carries it only once a works has dug by type. |
| 1648 | 12 | `public void restoreDepletionByType(double[][] saved)` | Puts E by type back from a save (Game's load path, after the land and E are in place): each typed resource's as saved; a resource the save carries none for - every resource of a save from before 0.7.110 - is the walk'... |
| 1662 | 1 | **type** `public record Claim(Deposit.FieldType type, int sites, int count)` | Standing works on a resource's sites, for fits(): the type they stand on (null for ANY), the sites each takes, and how many stand. |
| 1674 | 25 | `public boolean fits(Resource r, Deposit.FieldType type, java.util.List<Claim> standing, int extra)` | Whether a works of `extra` sites on `type` (null for ANY) fits on the city's ground of a resource beside the standing works (0.7.110; THE GROUND BY TYPE): the standing packed first fit decreasing onto the pieces (City... |
| 1712 | 27 | `public int[][] packed(Resource r, java.util.List<Claim> standing)` | Where fits()'s packing puts the standing works (0.7.131, batch I7): for each claim of `standing`, in its order, the sites its works take of each of the resource's types, in Resource.typesOf()'s order, summed over its ... |
| 1741 | 23 | `private static boolean placeInto(Resource r, java.util.List<CityLand.Piece> pieces, int[] free, int[] w, boolean spread, int[] ...` | place(), noting the sites it takes of each type in `byType`; a placement that fails whole takes nothing, a spread what there was. |
| 1766 | 4 | `private static int packingOrder(int[] a, int[] b)` | The packing's order: works on a type before works on ANY, the larger first within each. |
| 1778 | 20 | `private static boolean place(Resource r, java.util.List<CityLand.Piece> pieces, int[] free, int[] w, boolean spread)` | Places a works {type's index or -1, sites} on the first piece of its type with room, in acquisition order - ANY on the first type's pieces, then the next type's - taking its sites; or, `spread`, takes its sites from a... |
| 1804 | 3 | `int freeSites(CityLand.Piece p)` | The sites a piece of the ground offers a works to stand on (ET5 D9): its sites, or none where its field is centred on a plot of a landmass the city has not linked - ground across the water is owned and counted, but no... |
| 1809 | 7 | `private boolean pieceUnlinked(CityLand.Piece p)` | Whether a piece of the ground is a field centred on a plot of a landmass the city has not linked (always false while nothing is unlinked, and for ground by fiat, which has no field). |
| 1824 | 18 | `private void refreshUnlinkedSites()` | Works the sites across the water out again: every resource's pieces, the sites of those centred on an unlinked landmass, in all and by type. |
| 1844 | 3 | `public double getMarginPerSqFt()` | Margin per square foot at the current sale price. |
| 1857 | 3 | `public void setPricePerSqFt(double price)` | the load path can put back the price a saved month traded at before the market recomputes it, and so older callers still compile. |
| 1871 | 5 | `public void setOwnedSqFt(double sqFt)` | The city's dry ground, set by hand. |
| 1878 | 1 | `void restoreOwnedSqFt(double sqFt)` | The figure alone, the land as it stands: a load putting back what the save says, or a restatement that has just drawn the land to hold it. |
| 1879 | 1 | `public void setAllocatedSqFt(double sqFt)` |  |
| 1880 | 1 | `public void setBlocksPurchased(int blocks)` |  |
| 1884 | 3 | `public boolean canAllocate(double sqFt)` | Is there room to put this up at all? |
| 1889 | 3 | `public boolean canAllocate(double sqFt, String sector)` | ...for one sector's building (0.7.107): on the ground free for anyone and what its zone holds (freeFor()); a key with no zone, "" or null, the first. |
| 1894 | 3 | `public boolean canAllocate(double sqFt, String sector, boolean works)` | ...for a works that may stand anywhere when `works` (ET2): on any dry ground, steep ground too (freeFor(sector, works)); a building's, as above, on flat and foothill only. |
| 1899 | 3 | `public boolean canAllocate(double sqFt, String sector, Use use)` | ...for what the ground is to be used for (ET4): a building's on flat and foothill, a works's on any dry ground, a run's on every class in the city's proportions (freeFor(sector, use)). |
| 1909 | 3 | `public boolean allocate(double sqFt)` | Takes land out of the available pool. |
| 1919 | 3 | `public boolean allocate(double sqFt, String sector)` | ...for one sector's building (0.7.107): its zone's held ground first, recorded as the zone's used, then the ground free for anyone; and where the bare ground runs out it lies on open forest and clears it (clearFor(), ... |
| 1929 | 3 | `public boolean allocate(double sqFt, String sector, boolean works)` | ...a works's when `works` (ET2): the ground it takes is counted in worksSqFt, which stands on steep ground first (split()); a building's raises preparedSqFt by what foothillToPrepareSqFt() answered just before, read b... |
| 1940 | 15 | `public boolean allocate(double sqFt, String sector, Use use)` | ...for what the ground is to be used for (ET4): a works's is counted in worksSqFt, a run's - a highway's or a railway line's - in runsSqFt, which stands on every class of ground in the city's proportions (split()). |
| 1957 | 3 | `public void release(double sqFt)` | Frees land again: a scrapped business's plot (Game.retire()), and since 0.7.22 a finished demolition's ground (Game.settleConstructionControl()). |
| 1966 | 3 | `public void release(double sqFt, String sector)` | ...a sector's (0.7.107): out of its zone's used ground first, which the zone then holds again for it (spec-materials 2.15); the rest back to everyone. |
| 1971 | 3 | `public void release(double sqFt, String sector, boolean works)` | ...a works's when `works` (ET2): its ground leaves worksSqFt too, never below nothing. |
| 1979 | 8 | `public void release(double sqFt, String sector, Use use)` | ...for what the ground was used for (ET4): a works's leaves worksSqFt, a run's runsSqFt, never below nothing, and the two together never above the ground built. |
| 1989 | 3 | `public double priceFor(double sqFt)` | What a business pays the city for a plot this size. |
| 2000 | 3 | `public double priceFor(double sqFt, String sector)` | ...one sector's business (0.7.107, Z3): the part its zone holds at the zone's price (groundPriceFor()), the rest at the going price - the zone's ground first, as allocate() takes it. |
| 2010 | 3 | `public double priceFor(double sqFt, String sector, boolean works)` | ...a plot priced by the ground it takes (ET3, spec-earth2-terrain 5.1, 5.2): each class part of it (plotByRelief(): where allocate() would put it, buildings on flat then foothill, `works` on steep ground first) at tha... |
| 2015 | 8 | `public double priceFor(double sqFt, String sector, Use use)` | ...for what the ground is to be used for (ET4): a run's plot is priced at the city's proportions of every class (plotByRelief(sqFt, Use.RUN)). |
| 2030 | 3 | `public double buybackPriceFor(double sqFt, String sector)` | What the city pays a sector for a plot it gives back (0.7.107): the part out of its zone's used ground at the zone's price - the zone's ground trades at the zone's price both ways - the rest at the going price. |
| 2039 | 3 | `public double buybackPriceFor(double sqFt, String sector, boolean works)` | ...a plot priced by the ground it frees (ET3): the part that comes back by the reverse of the split (releasedByRelief()), each class at its price, the zone's share applying to every class alike. |
| 2044 | 8 | `public double buybackPriceFor(double sqFt, String sector, Use use)` | ...for what the ground was used for (ET4): a run's comes back at the city's proportions of every class (releasedByRelief(sqFt, Use.RUN)). |
| 2054 | 4 | `public void recordSale(double sqFt)` | Records a sale to a business. |
| 2060 | 4 | `public void recordSale(double sqFt, double price)` | ...at the price it was sold at (0.7.107: a zone's part at its price, priceFor(sqFt, sector)). |

### BUILDABLE BY SLOPE (ET2, 2026-10-24; ET4, 2026-10-25; spec-earth2-terrain 3.2, 4.2, 4.3, 4.4) (lines 2065-2310)

| line | len | member | says |
|---:|---:|---|---|
| 2157 | 1 | **type** `public enum Use` | What a square foot of ground is used for (ET2, ET4): a building's, a works's (a mine, a quarry, a well: Game.standsAnywhere()), or a run's (a highway or a railway line: Game.isRun()). |
| 2160 | 3 | **type** `private record Split(double flat, double foothill, double slope, double works, double worksOnSlope, double ...` | One reading of the ground by relief and by use (split()), square feet: flat, foothill and slope are the owned ground, the runs' part of each (ET4) taken off before the rest is placed. |
| 2165 | 3 | `private double reliefSqFt(int relief)` | The city's dry ground of one relief, square feet (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP), centre and purchases; 0 before the land is founded, which reads flat. |
| 2170 | 3 | `private double slopeSqFt()` | The steep and very steep ground the city owns, square feet. |
| 2182 | 24 | `private Split split(double allocated, double works, double runs)` | What the ground is used for if `allocated` square feet were taken, `works` of them by works and `runs` by highways and railway lines (the pool as it stands, or after an allocation): the runs first, over flat, foothill... |
| 2207 | 1 | `private Split split()` |  |
| 2210 | 1 | `public double getFlatSqFt()` | The city's flat ground, foothill and steep ground (steep and very steep together), square feet: CityLand's relief, centre and purchases. |
| 2211 | 1 | `public double getFoothillSqFt()` |  |
| 2212 | 1 | `public double getSlopeSqFt()` |  |
| 2215 | 1 | `public double getWorksSqFt()` | The part of the built ground that works stand on, square feet (never more than is built). |
| 2218 | 1 | `public double getWorksOnSlopeSqFt()` | ...the works' part on the steep ground: what no building could use. |
| 2221 | 1 | `public double getWorksOnFoothillSqFt()` | ...and on the foothill, which works take after the slope. |
| 2224 | 1 | `public double getBuildingsOnFoothillSqFt()` | The foothill buildings stand on now, square feet: what the pads are cut in. |
| 2227 | 1 | `public double getRunsSqFt()` | The ground the standing highways and railway lines hold, square feet, whatever the terrain (ET4): the figure a save keeps (DataSave's landRunsSqFt). |
| 2230 | 1 | `public double getRunsOnFlatSqFt()` | ...the runs' part on the flat, on the foothill and on the steep ground: their ground over the classes in the city's proportions. |
| 2231 | 1 | `public double getRunsOnFoothillSqFt()` |  |
| 2232 | 1 | `public double getRunsOnSlopeSqFt()` |  |
| 2235 | 5 | `public double getSlopeShare()` | The share of the city's dry ground that is steep or very steep (ET4): the part of a run's ground that falls on the steep; 0 in a city with neither foothill nor steep ground. |
| 2242 | 1 | `public double getPreparedSqFt()` | The foothill prepared so far, square feet: never falls. |
| 2245 | 4 | `public double idleSlopeSqFt()` | The steep ground no works stands on yet, square feet: not free for a building, free for a works. |
| 2258 | 3 | `public double foothillToPrepareSqFt(double sqFt, String sector, boolean works)` | The foothill a building of this size would have to prepare, square feet: what stands on foothill after it less what is prepared already, never below nothing - read BEFORE allocate(), changing nothing, so the quote and... |
| 2263 | 3 | `public double foothillToPrepareSqFt(double sqFt, String sector, Use use)` | ...for what the ground is to be used for (ET4): only a building's is anything. |
| 2272 | 4 | `public double foothillToPrepareSqFt(double sqFt, String sector, boolean works, double groundAhead, double worksAhead, double pr...` | ...for an order placed after others in a run (Game.buildRunInvoice()), which have taken their ground and prepared their foothill by then: `groundAhead` square feet, `worksAhead` of them works's, and `preparedAhead` th... |
| 2278 | 6 | `public double foothillToPrepareSqFt(double sqFt, String sector, Use use, double groundAhead, double worksAhead, double runsAhea...` | ...with the runs ahead too (ET4): `runsAhead` of the ground ahead is highways' and railway lines', which take their share of the flat from the buildings. |
| 2292 | 7 | `public double foothillDisplacedByRunSqFt(double sqFt, double groundAhead, double worksAhead, double runsAhead)` | The foothill a run of this size would push buildings onto, square feet (ET4): the rise in what buildings stand on past the flat when the run takes its share of it, after the `groundAhead` square feet (`worksAhead` of ... |
| 2301 | 1 | `void restoreWorks(double sqFt)` | Puts the works' ground back (Game's load path): the saved figure, or the standing works' land where a save has none. |
| 2304 | 1 | `void restoreRuns(double sqFt)` | ...and the runs' ground (ET4): the saved figure, or the standing highways' and railway lines' land where a save has none. |
| 2307 | 3 | `void restorePrepared(Double sqFt)` | ...and the foothill prepared, null where a save has none: what the buildings on foothill stand on now, so a city saved before is not charged for pads it already has. |

### A PRICE FOR EACH CLASS OF GROUND (ET3, 2026-10-25; spec-earth2-terrain 5.1, 5.2) (lines 2311-2486)

| line | len | member | says |
|---:|---:|---|---|
| 2345 | 1 | `public void setPreparationPerSqFt(java.util.function.DoubleSupplier perSqFt)` | Wires the preparation of a square foot of foothill, in thousands of local money: the figure the quote charges (Game.preparationCost()), so the price of foothill and the quote read one number. |
| 2352 | 7 | `public double getPreparationPerSqFt()` | What preparing a square foot of foothill costs, thousands of local money: the wired figure (setPreparationPerSqFt()), else SitePreparation's dollars a m2 at the founding, in the local unit and price level - the base p... |
| 2361 | 3 | `public double getFoothillPricePerSqFt()` | What a business pays for a square foot of foothill, thousands of local money: the flat price less the preparation, never under the base price (LandMarket.reliefPrice()). |
| 2366 | 3 | `public double getSteepPricePerSqFt()` | ...and of steep and very steep ground, which only works use: the base price, never over the flat price. |
| 2371 | 8 | `public double reliefPricePerSqFt(int relief)` | The price a square foot of ground of one relief (World.FLAT, FOOTHILL, STEEP, VERY_STEEP) is sold at: flat's is getPricePerSqFt(). |
| 2381 | 3 | `private boolean flatOnly()` | Whether the city owns neither foothill nor steep ground: then every price by class is the one old price. |
| 2386 | 1 | `public boolean isFlatOnly()` | Whether the city owns neither foothill nor steep ground (ET3): every figure by class is then the one flat price's, to the bit - the books read it to keep a flat city's statements as they were. |
| 2394 | 11 | **type** `public record ReliefUse(double buildingsFlat, double buildingsFoothill, double worksFlat, double worksFooth...` | The ground in use by class, as the split has it (ET3, BUILDABLE BY SLOPE), square feet: the buildings' on flat and on foothill, the works' on flat, on foothill and on the steep and very steep ground, and (ET4) the run... |
| 2397 | 1 | `public double buildings()` _(in LandManager.ReliefUse)_ | The buildings' ground, flat and foothill. |
| 2400 | 1 | `public double works()` _(in LandManager.ReliefUse)_ | The works' ground on every class. |
| 2403 | 1 | `public double runs()` _(in LandManager.ReliefUse)_ | The runs' ground on every class (ET4). |
| 2407 | 5 | `public ReliefUse reliefUse()` | The ground in use by use and class now (ReliefUse), read off the one split(): nothing stored. |
| 2414 | 5 | `private static double[] classUse(Split s)` | The ground in use on each class under a reading of the split, square feet: {flat, foothill, slope} - works and runs on steep ground, works, runs and buildings on foothill, the rest of the built ground on flat. |
| 2421 | 5 | `private static double[] classParts(double sqFt, double foothill, double slope)` | A change of ground in use as the square feet of `sqFt` on {flat, foothill, slope}: foothill and slope held between nothing and the plot, flat the rest, so the three sum to `sqFt`. |
| 2432 | 3 | `public double[] plotByRelief(double sqFt, boolean works)` | The square feet of a plot of this size, taken now, on {flat, foothill, slope} (ET3): the ground in use after the allocation less before it (split()), buildings on flat first and then foothill, `works` on steep ground ... |
| 2441 | 7 | `public double[] plotByRelief(double sqFt, Use use)` | ...for what the ground is to be used for (ET4): a run's is its ground times each class's share of the owned ground - its own pro-rata parts, which split() takes off the classes - and NOT the ground in use after the al... |
| 2450 | 6 | `private double[] runParts(double sqFt)` | A run's ground as {flat, foothill, slope}: `sqFt` times each class's share of the owned dry ground, summing to `sqFt`; all on flat where the city owns none. |
| 2462 | 3 | `public double[] releasedByRelief(double sqFt, boolean works)` | ...and of a plot this size given back (release(), asked before it): the reverse of the split - the ground in use now less the ground in use after the release - so a building's comes off the foothill first, a works's o... |
| 2467 | 10 | `public double[] releasedByRelief(double sqFt, Use use)` | ...for what the ground was used for (ET4): a run's comes back at the same shares it took (plotByRelief(sqFt, Use.RUN)). |
| 2479 | 7 | `private double pricedByClass(double[] parts, double sqFt, double inZone, String sector)` | A plot's parts {flat, foothill, slope} at their class prices, the zone's share on every class: `inZone` of its `sqFt` at the sector's price share (Z3). |

### ZONES AND THE FOREST LAW (0.7.107, batch Z1; Jerus, 2026-10-08, the (lines 2487-2563)

| line | len | member | says |
|---:|---:|---|---|
| 2559 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |

### the ground, by kind (lines 2564-2616)

| line | len | member | says |
|---:|---:|---|---|
| 2567 | 1 | `public double getForestSqFt()` | The city's forest ground, in square feet: the forest class of its dry ground as drawn, cleared or standing (getForestKm2()). |
| 2570 | 1 | `public double getBareSqFt()` | ...its bare ground: the dry ground owned less the forest, never below nothing. |
| 2573 | 1 | `public double getProtectedForestSqFt()` | The forest the law protects: never cut, never built on (Z6). |
| 2576 | 1 | `public double getWorkingForestSqFt()` | ...the working forest: cut, never built on. |
| 2579 | 1 | `public double getOpenForestSqFt()` | ...and the open forest: cut or built on - all of it with the law at 0 and 0. |
| 2582 | 1 | `public double getForestClearedSqFt()` | Forest cleared for building, square feet (C): built on, or built on once. |
| 2585 | 1 | `public double getForestClearedM3()` | ...and the wood that stood or had been cut on it, cubic metres. |
| 2588 | 1 | `public double getForestClearedThisMonth()` | The standing wood cleared this month, cubic metres: Forestry's salvage cut since 0.7.138 (batch I9; takeSalvage()). |
| 2591 | 1 | `public double getSalvagePending()` | The standing wood cleared and not yet sold as Forestry's salvage cut, cubic metres (0.7.138): what takeSalvage() would hand over, before the month's floor. |
| 2593 | 3 | `private double protectedSqFt(Zoning z)` |  |
| 2598 | 3 | `private double keptFromBuildingSqFt(Zoning z)` | What the law keeps from building: the protected and the working forest. |
| 2602 | 3 | `private double openSqFt(Zoning z)` |  |
| 2607 | 1 | `public double getBuildableSqFt()` | The ground that may be built on: the dry ground owned less what the law keeps. |
| 2610 | 1 | `public double getFreeSqFt()` | Free ground, for anyone and the zones together: buildable less built, never below nothing. |
| 2612 | 4 | `private double freeSqFt(Zoning z)` |  |

### the zones (lines 2617-2784)

| line | len | member | says |
|---:|---:|---|---|
| 2620 | 15 | `private double[] heldByZone(Zoning z)` | What each zone holds, square feet, in Zoning.zones()'s order: its want - its size less what it has used - out of the free ground, every want scaled alike when the ground is short (Z10). |
| 2636 | 5 | `private double heldTotal(Zoning z)` |  |
| 2642 | 7 | `private double heldBy(Zoning z, String sector)` |  |
| 2650 | 3 | `private double freeForAnyone(Zoning z)` |  |
| 2660 | 7 | `private double freeForAnyone(Zoning z, Use use)` | ...for a works that may stand anywhere (ET2): free less what the zones hold, on any dry ground; for a building the same less the steep ground no works stands on yet (idleSlopeSqFt()), never below nothing; for a run (E... |
| 2674 | 9 | `private double runRoom(double groundAhead, double worksAhead, double runsAhead)` | The largest run, square feet, the buildings and the works still fit beside (ET4; BUILDABLE BY SLOPE, THE ROOM FOR A RUN): the smaller of the ground still unbuilt and what the flat and foothill the run leaves hold of t... |
| 2684 | 1 | `private double runRoom()` |  |
| 2692 | 4 | `public boolean canAllocateRunAfter(double sqFt, String sector, double groundAhead, double worksAhead, double runsAhead)` | Whether a run of this size can go ahead after `groundAhead` square feet of orders before it, `worksAhead` of them works's and `runsAhead` runs' (ET4; Game.runAhead()): the ground still unbuilt and the works' ground as... |
| 2698 | 1 | `public double heldBy(String sector)` | The free ground a sector's zone holds for it, square feet: its size less what it has used, as far as the free ground goes (Z10); 0 with no zone. |
| 2701 | 1 | `public double usedBy(String sector)` | ...and what its sector has built in it (used). |
| 2704 | 4 | `public double zoneSizeOf(String sector)` | What a sector's zone is to be, square feet: its share of the dry ground or its square kilometres; 0 with no zone. |
| 2710 | 1 | `public double heldByZones()` | What all the zones hold together. |
| 2713 | 1 | `public double freeForAnyone()` | The ground free for anyone: free, less what the zones hold - and since ET2, for a building, less the steep ground only a works may stand on (idleSlopeSqFt()). |
| 2716 | 1 | `public double freeForAnyone(boolean works)` | ...for a works that may stand anywhere when `works` (ET2): on any dry ground, steep ground too; false is freeForAnyone(). |
| 2719 | 1 | `public double freeForAnyone(Use use)` | ...for what the ground is to be used for (ET4): a run's is the works' less what the buildings' room leaves it. |
| 2722 | 1 | `public double freeFor(String sector)` | The ground a sector may build on: free for anyone, and what its zone holds for it. |
| 2725 | 1 | `public double freeFor(String sector, boolean works)` | ...a works's when `works` (ET2): today's figure, any dry ground; false is freeFor(sector). |
| 2728 | 1 | `public double freeFor(String sector, Use use)` | ...for what the ground is to be used for (ET4): a run's is the works' figure or the room the buildings leave it (runRoom()), whichever is less; a building's, in a city with neither foothill nor steep ground. |
| 2730 | 8 | `private double freeFor(Zoning z, String sector, Use use)` |  |
| 2740 | 4 | `public double groundPriceFor(String sector)` | What a square foot of a sector's zone is sold and assessed at: its price share of the going business price (Z3, Z4); the going price with no zone. |
| 2746 | 4 | `public double groundPriceFor(String sector, int relief)` | ...on ground of one relief (ET3, World.FLAT to World.VERY_STEEP): the zone's price share of that class's price (reliefPricePerSqFt()); the class's price with no zone. |
| 2756 | 8 | `public java.util.Map<String, double[]> zoneLand()` | The zones' ground, for the investors' month (Game.refreshLand(), BusinessInvestment.setZoneLand()): each zoned sector's {held square feet, price share}; empty with no zones. |
| 2771 | 2 | **type** `public record GroundUnder(double freeForAnyone, double heldByZones, double protectedSqFt, double workingSqF...` | THE GROUND UNDER ANOTHER ZONING (0.7.107), for PolicyPreview's before and after: a staged copy's free ground for anyone, a sector's held and free ground, and the forest's three kinds - the land as it stands, read thro... |
| 2775 | 6 | `public GroundUnder groundUnder(Zoning z)` | ...the whole of it. |
| 2783 | 1 | `public double heldUnder(Zoning z, String sector)` | ...and one sector's held ground under it. |

### the forest's wood (lines 2785-2804)

| line | len | member | says |
|---:|---:|---|---|
| 2788 | 1 | `public double getProtectedWood()` | The wood the law keeps standing, cubic metres: the protected share of the forest's full stock, from the fullest forest first, never more than stands (Z6, Z8). |
| 2790 | 4 | `private double protectedWood(Zoning z)` |  |
| 2801 | 3 | `public double cuttableForest()` | THE CUTTABLE FOREST (Z8, Z13), cubic metres: what stands less the protected wood - the working and the open forest's. |

### clearing (lines 2805-2872)

| line | len | member | says |
|---:|---:|---|---|
| 2815 | 20 | `private void clearFor(double sqFt, double fromZone, String sector)` | Clears the open forest an allocation lies on (Z7), asked before it is made: the zone's part on what its zone holds of the bare ground, the rest on the bare ground free for anyone - the bare and cleared ground not buil... |
| 2843 | 7 | `private void settleClearing()` | The month's floor (Z7): the cleared ground at least what the built ground past the bare ground needs, as far as the open forest goes - the same as allocate() leaves it, and what ground set by hand (a fixture, an older... |
| 2858 | 14 | `private void clear(double sqFt)` | Clears this much open forest, as far as it goes: cut-over ground first - its wood moves from E to C - then standing forest, whose wood is taken out into C, never past what may be cut (the protected wood stands on grou... |

### the salvage cut and the planted stands (0.7.138, batch I9) (lines 2873-2917)

| line | len | member | says |
|---:|---:|---|---|
| 2882 | 6 | `public double takeSalvage()` | THE SALVAGE CUT (Z7; spec-materials 2.11): the standing wood a building cleared, handed to Forestry once, to be sold as its month's sawlogs and pulpwood - the month's floor settled first (settleClearing()), so ground ... |
| 2900 | 3 | `public void setPlantedGrowth(double m3)` | The planted stands' growth this month, cubic metres, as Forestry's month's end sets it (Forestry.endOfMonth()); cleared with the month's flows. |
| 2905 | 1 | `public double getPlantedGrowth()` | ...as set this month. |
| 2908 | 3 | `public double regrowthDue()` | The wood the month's end will grow back (pure): the regrowth of what was cut, FOREST_REGROWTH of E, and the planted stands' growth as set - never more than was cut. |
| 2913 | 4 | `public double regrowthDue(double planted)` | ...with `planted` cubic metres of planted growth in place of the month's (pure): what Forestry's planting weighs with its stands on site counted as grown. |

### the save (lines 2918-3113)

| line | len | member | says |
|---:|---:|---|---|
| 2921 | 9 | `public Zoning.State zoningState()` | DataSave's zoning (0.7.107): the zones, the law and the forest cleared; null when nothing is set and nothing cleared, as an older save reads. |
| 2932 | 6 | `public void restoreZoning(Zoning.State s)` | Puts them back (Game's load path, after the land and its allocation): null, none, 0 and 0, nothing cleared. |
| 2954 | 4 | `public void recordBuyback(double sqFt)` | Records the city buying a plot back from a business that scrapped what stood on it. |
| 2960 | 4 | `public void recordBuyback(double sqFt, double price)` | ...at the price it was bought back at (0.7.107: a zone's part at its price, buybackPriceFor()). |
| 2970 | 3 | `public double buyBlock(double availableCash)` | Annexes one block. |
| 2984 | 13 | `public double buyBlock(double availableCash, long population)` | Buys the cheapest thing on offer. |
| 3007 | 6 | `public void endMonth()` | The month's end for the ground (0.7.57): forest's stored depletion grows back by FOREST_REGROWTH, then the month's flows are cleared. |
| 3015 | 11 | `public void clearMonth()` | Called once a month, after the government accounts have read the flows. |
| 3027 | 26 | `public void reset()` |  |
| 3056 | 26 | `public void printLandInfo()` | The land to the log, in the player's units since 0.7.68 (areaWords(), prices a square metre) - nothing in the game calls it; a console's view of the office. |
| 3085 | 4 | `static { ... }` |  |
| 3098 | 7 | `public void redenominate(double scale)` | The city's own land prices and this month's land flows, in the new unit. |
| 3108 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

