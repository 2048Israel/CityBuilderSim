# LandMarket.java - 1,807 lines · 111 methods · 14 constants · model

`ham/citybuildersim/LandMarket.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's edge, or none while that side has no room - and what the ground costs.
> 
> WHY A LISTING RATHER THAN A PRICE
> 
> Buying land used to be a button with a number on it. There was no decision in
> it - the price only went up, so the answer was always "buy now or buy later",
> and the only thing the player could get wrong was timing.
> 
> Many offers at once is a decision. They are different sizes at different
> prices, some wet and some with ore under them, which is worth far more than
> the ground but costs more up front. Buying the cheap one, buying the big one
> and buying the one with the ore are three different plays.
> 
> ON THE WORLD SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2). The
> nine parcels the office drew from a generator seeded with their ids are
> gone. The city stands on the world (CityLand). From 0.7.57 to 0.7.66 each of
> forty lanes of wedges had one offer standing, the next band of its lane,
> sized by a multiple drawn from the offer's id.
> 
> ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.2).
> Jerus, 2026-10-07: six offers a side, never rerolled. Each side has six
> places, left to right facing out, and each place one offer standing: a
> rectangle of whole blocks of the city's level against its edge, listed by
> GridOffers (the seed, the rectangle, the clip, the trim, 2:1), or none
> while the side has no room for it, listed again after every purchase and
> every month until it has (an empty place, null). Its ground is every plot of
> the rectangle the city does not own yet, counted plot by plot when it is
> listed (CityLand.groundOf(): exact at every size), and its fields whole
> (CityLand.fieldsOn()). An offer is never rerolled; buying it lists its
> place's next - the innermost free ground of its lane - and the other
> twenty-three do not move, because standing rectangles never meet. Priced by
> price(): the ground at the world's dollar price, water cheaper, and each
> resource the city can use at a share of its world price (spec-land star 6).
> 
> TWO PRICES, MOVED BY DIFFERENT THINGS
> 
> What the CITY pays for a new parcel is a function of how CROWDED the city is
> (since 0.7.55): its people per square kilometre of the land it owns. A city
> packed onto its ground is quoted dear for the next tract, one spread over
> plenty is quoted cheap - and two cities equally crowded are quoted the same,
> whatever their size. Until 0.7.55 it was the city's SIZE, every block owned
> and every thousand residents, which at ten billion people priced land at
> about 19,500 times today's (the crowding premium, below). It does not care
> whether the city is full.
> 
> What BUSINESSES pay the city per square foot is the opposite: pure supply
> against demand inside the city limits. A city with empty blocks sells cheap; a
> city with nothing spare sells dear. The player no longer sets this by hand -
> it is a market now, and the way to make land cheap is to go and buy some.
> 
> WHAT IS LISTED STAYS LISTED
> 
> An offer's price is fixed the moment it appears and never moves, so a player
> can save up for the expensive one without it drifting away. Offers are saved
> whole (SAVE AND RESTORE, below), so reloading a save cannot reroll them into
> something better either.
> 
> WHAT THE CITY PAYS IS US DOLLARS (0.7.6)
> 
> Jerus: "when you buy land, make it so that it costs USD not domestic
> ... (17 more lines in the source)

**Uses:** [LandParcel](LandParcel.md) (93), [CityLand](CityLand.md) (45), [World](World.md) (19), [GridOffers](GridOffers.md) (18), [Resource](Resource.md) (13), [LandSellers](LandSellers.md) (9), [LandManager](LandManager.md) (8), [Landmasses](Landmasses.md) (4), [PriceLevel](PriceLevel.md) (2), [LandGrid](LandGrid.md) (2), [LandFactors](LandFactors.md) (1), [SitePreparation](SitePreparation.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (32):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [ER1Check](ER1Check.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [GlobeFrame](GlobeFrame.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandNationsCheck](LandNationsCheck.md), [LandScreen](LandScreen.md), [LandmassCheck](LandmassCheck.md), [Landmasses](Landmasses.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [OilCheck](OilCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 101 | · what the city pays |
| 149 | · THE CROWDING PREMIUM (0.7.55) |
| 284 | · what businesses pay |
| 337 | · the offers |
| 384 | PRICING |
| 530 | LISTING AN OFFER (0.7.57; on the block grid since 0.7.67) |
| 823 | · a price for each class of ground (ET3; spec-earth2-terrain 5.1, 5.2) |
| 977 | THE LISTING |
| 1262 | THE PLAYER'S GROUND, DRAFTED INTO THE OFFICE (BL1, 2026-10-26) |
| 1498 | A QUOTE AWAY FROM THE SCREEN'S THREAD (WN5b, 2026-11-02) |
| 1590 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `LandMarket.OFFERS_A_SIDE` | `GridOffers.PLACES` | Offers standing on each side of the city: six, one a place (GridOffers.PLACES; Jerus, 2026-10-07, "six offers a side"). |
| 99 | `LandMarket.OFFERS` | `CityLand.SIDES * OFFERS_A_SIDE` | Offers standing in all: OFFERS_A_SIDE on each of the four sides, twenty-four. |
| 115 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007 * PriceLevel.FACTOR` | Ground price per square foot before any premium, in thousands of US dollars. |
| 243 | `LandMarket.CROWDING_MIDPOINT` | `4_000` | People per square kilometre of the city's land at which the crowding premium is half way to its ceiling (0.7.55): about 60x the base. |
| 251 | `LandMarket.CROWDING_STEEPNESS` | `5.3` | How sharply the premium climbs through the midpoint: the curve's power (0.7.55). |
| 259 | `LandMarket.CROWDING_CEILING` | `120` | The most crowding can multiply the ground's price by, however crowded the city (0.7.55). |
| 266 | `LandMarket.FRESH_PRICE_SHARE` | `0.45` | What a square kilometre of fresh water sells for, against dry ground: 45% (the map mockup's figure, spec-land star 6). |
| 269 | `LandMarket.SEA_PRICE_SHARE` | `0.08` | ...and of sea: 8% (the mockup's), the reach a desalination plant needs and nothing else does. |
| 320 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 323 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 335 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 806 | `LandMarket.PRICE_STEP_USD` | `5 * PriceLevel.FACTOR` | The step an offer's price is rounded to, in thousands of US dollars: US$5k, as the parcels were (nobody wants to compare US$103,847 against US$98,211). |
| 1038 | `LandMarket.RANK_TIE` | `1e-12` | How close two ranks (rates of ground a dollar, or prices that carry a sum) must be to be one rank: a part in a trillion. |
| 1509 | `LandMarket.QUOTER_MS` | `16` | The most making a quoter may take on the screen's thread, in milliseconds: a frame at 60 Hz. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 147 | `private double basePricePerSqFt` | The same base in LOCAL money, reformed with every other price and struck at the expected price level since 0.7.42 (seedConstants()) - what the inside price is struck from, and nothing else. |
| 340 | `private final LandParcel[] offers` | The twenty-four standing, one a place, side by side and place by place; null where a place waits empty. |
| 341 | `private int nextId` |  |
| 344 | `private CityLand land` | The city's land the offers stand round; none on a bare office, which lists nothing. |
| 347 | `private GridOffers places` | The offers' rectangles on the land's grid, as GridOffers lists and clips them; null on a bare office. |
| 350 | `private int takenFrom` | The place an offer was last taken from, listed first when the shelf is filled again (GridOffers.relist()'s order); -1 for none. |
| 353 | `private int month` | The month the office lists in, for each offer's record. |
| 360 | `private double marketPricePerSqFt` | Ground price per square foot right now, before any parcel's ore premium, in LOCAL money at the founding rate - the anchor the inside price is struck from (basePricePerSqFt), and since 0.7.6 nothing else. |
| 367 | `private double groundUsdPerSqFt` | ...and what the world asks for the same ground, in thousands of US dollars (0.7.6): every parcel listed from now on is priced off this, and it is the figure the history keeps as landPrice. |
| 377 | `private double crowding` | THE PRICE'S PARTS, AS STRUCK (0.7.55): the city's crowding - people per square kilometre of its land - the premium it came to, and the world's price level, so groundUsdPerSqFt is exactly BASE_PRICE_PER_SQ_FT x usPrice... |
| 378 | `private double crowdingPremium` |  |
| 379 | `private double usPriceLevel` |  |
| 382 | `private double salePricePerSqFt` | What businesses are charged. |
| 666 | `private LandSellers sellers` | The sellers of the city's ground, null for none. |
| 722 | `private Landmasses landmasses` | The city's ground by landmass (ET5), which splits each offer's ground by linked and unlinked; null on an office with none, a bare one's harness-made. |
| 1294 | `private final List<LandParcel> drafts` | The drafts standing now, in the order the player's ground was cut; each a LandParcel under a negative id. |
| 1297 | `private final Map<DraftRect, DraftGround> draftGround` | Draft geometry belongs to this office's owner thread; workers fill only their own copied office (WN5c). |
| 1298 | `private boolean asynchronousDrafts` |  |
| 1299 | `private DraftRequest latestDraft` |  |
| 1302 | `private int nextDraftId` | The id the next draft takes: -1, then -2, and so on - a counter of its own, never nextId, so no offer's number moves for a draft. |
| 1426 | `private final Quoter copy` |  |
| 1427 | `private final CityLand land` |  |
| 1428 | `private final LandGrid grid` |  |
| 1429 | `private final long owned` |  |
| 1430 | `private final LandSellers sellers` |  |
| 1431 | `private final Landmasses landmasses` |  |
| 1432 | `private final int linkedVersion` |  |
| 1433 | `private final List<long[]> rects` |  |
| 1434 | `private volatile boolean cancelled` |  |
| 1435 | `private boolean finished` |  |
| 1536 | `private final LandMarket frozen` |  |
| 1537 | `private final CityLand original` |  |
| 1538 | `private final LandGrid grid` |  |
| 1539 | `private final long owned` |  |
| 1540 | `private final int level` |  |
| 1572 | `private final Quoter source` |  |
| 1573 | `private final DraftGround ground` |  |
| 1683 | `private boolean groundFromLocal` | True when the price state came back without a dollar ground price (an older save). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 88 | 1720 | **type** `public class LandMarket` | The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's edge, or none while that side has no room - and what the ground costs. |

### what the city pays (lines 101-148)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 1 | `public static double openingUsdPerSqFt()` | The ground's price a square foot at the founding, in thousands of US dollars: BASE_PRICE_PER_SQ_FT, before any premium - what an Insane city owes a square foot of its starting land (Founding.landBondUsd(), 0.7.14; sin... |

### THE CROWDING PREMIUM (0.7.55) (lines 149-283)

### what businesses pay (lines 284-336)

### the offers (lines 337-383)

### PRICING (lines 384-529)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population)` | Re-prices the market and lists the next offer of any place without one. |
| 410 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices)` | ...and at the world's price level (0.7.55): US prices against the founding's, WorldEconomy.getPriceLevel() - the dollar's own inflation, which the world's price follows and the city's does not. |
| 415 | 26 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices, int month)` | ...in a month, which each offer listed now records (0.7.57). |
| 462 | 18 | `public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt)` | How dear inside land is, as a multiple of what the ground cost outside. |
| 486 | 1 | `public double getMarketPricePerSqFt()` | The inside price's anchor: the ground price per square foot in local money at the founding rate, in thousands. |
| 489 | 1 | `public double getBasePricePerSqFt()` | The local anchor itself, before the premiums: BASE_PRICE_PER_SQ_FT in today's unit at the expected price level (0.7.42, Game.restrikeMoneyConstants()) - what ExpectationsCheck reads. |
| 492 | 1 | `public double getGroundUsdPerSqFt()` | Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). |
| 495 | 1 | `public double getSalePricePerSqFt()` | What a business pays the city per square foot, in thousands. |
| 503 | 5 | `public static double peoplePerKm2(long population, double ownedSqFt)` | A city's crowding (0.7.55): its people per square kilometre of the land it owns - the measure the premium reads. |
| 515 | 5 | `public static double crowdingPremium(double peoplePerKm2)` | What crowding multiplies the ground's price by (0.7.55): 1 for an empty city, CROWDING_CEILING at the limit, half way at CROWDING_MIDPOINT. |
| 522 | 1 | `public double getCrowding()` | The crowding the ground was last priced at: people per square kilometre of the city's land (0.7.55); 0 until a city's first month on 0.7.55. |
| 525 | 1 | `public double getCrowdingPremium()` | ...the premium it came to: what crowding multiplies the ground's price by (0.7.55). |
| 528 | 1 | `public double getUsPriceLevel()` | ...and the world's price level the dollar price was struck at: US prices against the founding's (0.7.55). |

### LISTING AN OFFER (0.7.57; on the block grid since 0.7.67) (lines 530-822)

| line | len | member | says |
|---:|---:|---|---|
| 545 | 12 | `void attach(CityLand land)` | Puts the office on a city's land with nothing on its shelf: what LandManager does when a city is founded, converted, loaded or restated. |
| 559 | 1 | `public CityLand getLand()` | The city's land the offers stand round, or null on a bare office. |
| 562 | 7 | `void listMissing()` | Lists every place without an offer - the one an offer was just taken from first, then North 1 to West 6 - at the prices last struck; a place with no room waits empty. |
| 571 | 6 | `private void listIn(int i)` | Lists place i (side i / OFFERS_A_SIDE, place i % OFFERS_A_SIDE) and stands it, or leaves it waiting. |
| 579 | 7 | `private void restand()` | The offers standing, stood again on a grid of the land's: what a restore or a hand-made listing goes through. |
| 594 | 3 | `LandParcel measure(GridOffers.Rect r)` | An offer of a rectangle: its ground the plots of it the city does not own, counted on the world (CityLand.groundOf()), the fields on that ground (CityLand.fieldsOn()) and its forest's timber, priced at the ground's do... |
| 599 | 3 | `private LandParcel measure(GridOffers.Rect r, int id, int listedIn)` | ...under a given id, as listed in a given month. |
| 604 | 2 | **type** `private record DraftGround(GridOffers.Rect rect, CityLand.Ground ground, int[] sites, double[] amounts, Lis...` | Geometry and seller parts of unowned ground; once made, only read, including after a worker hands it back. |
| 608 | 1 | **type** `private record Linkage(int version, double dry, double buildable, int largest)` | A measured landmass split at a linked-set version: numbers only, no reference to the live landmass books. |
| 611 | 1 | **type** `private record DraftRect(long x0, long y0, long x1, long y1)` | Measured geometry is keyed by its plots; a level change changes the parcel's label, never these plots (WN5c review). |
| 613 | 1 | `private static DraftRect draftRect(GridOffers.Rect r)` |  |
| 615 | 11 | `private DraftGround readGround(GridOffers.Rect r)` |  |
| 628 | 3 | `private LandParcel priceGround(DraftGround measured, int id, int listedIn)` | Prices retained geometry at THIS office's month and price, without walking the seller plots again (WN5c). |
| 632 | 25 | `private LandParcel priceGround(DraftGround measured, int id, int listedIn, boolean linkage)` |  |
| 669 | 1 | `public LandSellers getSellers()` | The nations that sell the ground the offers stand on, or null where none does (a generated world, no world file, the literals). |
| 672 | 4 | `void setSellers(LandSellers sellers)` | Hands the office the sellers of its land (Game, before the founding's listing and in the load path); null for none. |
| 678 | 3 | `void syncMonth(int month)` | Sets the month the office prices in: the load path's, where the office has not been through update() yet and a quote's land factors move with the month. |
| 689 | 31 | `private double priceBySellers(List<LandSellers.Part> parts, List<LandParcel.Sale> sold)` | An offer's price with sellers: each part of its ground (LandSellers.split()) priced raw at the dollar formula of its own km2, relief and fields (forest's timber from the part's own forest, as measure() does for the wh... |
| 725 | 4 | `void setLandmasses(Landmasses landmasses)` | Hands the office the city's landmasses (LandManager's constructor): every offer listed or restored from now on is split against them. |
| 735 | 16 | `void restateOffers()` | Splits every offer standing again against the linked landmasses as they are now (ET5): what a land change or a change to the linked set asks, since an offer's ground is frozen at its listing but which of it is unlinke... |
| 763 | 15 | `public int[] remeasureStanding()` | THE WORLD'S FIELDS CHANGED UNDER THE OFFERS (0.7.99, batch W1): every offer standing measured again on the world as it is now - the same place, rectangle, id and month listed; its ground counted, its fields the world'... |
| 780 | 1 | `public int getLevel()` | The city's block level now: LandGrid.levelFor() of every plot it owns - what a new offer's blocks are, or one finer (spec-grid star 2); 0 on a bare office. |
| 791 | 3 | `public static double price(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | An offer's price, in thousands of US dollars (spec-land star 6): the ground's dollar price a square foot on its dry ground, FRESH_PRICE_SHARE of it on its fresh water and SEA_PRICE_SHARE on its sea; and each resource ... |
| 796 | 4 | `private static double rawPrice(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | price() before its rounding (WN5: the parts of an offer are priced raw, added at their sellers' factors and rounded once). |
| 809 | 8 | `private static double rawPlusResources(double groundUsd, double[] amounts, double usPriceLevel)` | The resources' share of an offer's price added to the ground's, in thousands of US dollars (price()'s second half), before its rounding to PRICE_STEP_USD (rounded()). |
| 819 | 3 | `private static double rounded(double usd)` | A price rounded to PRICE_STEP_USD. |

### a price for each class of ground (ET3; spec-earth2-terrain 5.1, 5.2) (lines 823-976)

| line | len | member | says |
|---:|---:|---|---|
| 834 | 11 | `public static double reliefPrice(int relief, double going, double base, double prep)` | THE RESIDUAL PRICE of a square foot of dry ground of one relief (ET3, spec-earth2-terrain 5.1), in the money the three prices are given in: `going` is flat ground's price (the market times crowding, times scarcity ins... |
| 847 | 3 | `public static double prepUsdPerSqFt(double usPriceLevel)` | What preparing a square foot of foothill costs at the world's price level, in thousands of US dollars: SitePreparation's dollars a m2, over a thousand, a square foot's worth of m2 (LandManager.SQ_M_PER_SQ_FT), at the ... |
| 852 | 4 | `private static boolean allFlat(double[] relief)` | Whether a ground's relief row has no foothill, steep or very steep ground in it: a null row, a row of another width, and every generated world's. |
| 858 | 4 | `static boolean allFlat(LandParcel p)` | ...for an offer: its own relief (LandParcel.getReliefKm2()). |
| 871 | 3 | `public static double price(double[] km2, double[] relief, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | An offer's price with its dry ground by relief (ET3, spec-earth2-terrain 5.2), in thousands of US dollars: flat ground at the dollar price a square foot, foothill at its residual price (reliefPrice(): that price less ... |
| 876 | 10 | `private static double rawPrice(double[] km2, double[] relief, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | price() with relief before its rounding (WN5). |
| 888 | 1 | `public double getFlatUsdPerSqFt()` | The ground flat at the going dollar price, in thousands of US dollars a square foot: what the office asks for flat ground now (getGroundUsdPerSqFt()). |
| 891 | 3 | `public double getFoothillUsdPerSqFt()` | ...for foothill: the going price less its preparation, never under the base price (reliefPrice()). |
| 896 | 3 | `public double getSteepUsdPerSqFt()` | ...for steep and very steep ground: the base price (reliefPrice()), never over the flat price. |
| 906 | 4 | `public static double buildableKm2(LandParcel p)` | An offer's ground that a building can stand on, in square kilometres (ET3): its flat and its foothill, which buildings may use (LandManager, BUILDABLE BY SLOPE); the steep and very steep ground is for works. |
| 912 | 3 | `private static double wholeBuildableKm2(LandParcel p)` | An offer's buildable ground whether or not it is linked (buildableKm2() before ET5), square kilometres. |
| 917 | 5 | `public static double buildableSqFt(LandParcel p)` | ...in square feet. |
| 924 | 4 | `public double priceWithPrepUsd(LandParcel p)` | An offer's price with the preparation of its foothill added, in thousands of US dollars (ET3, spec 5.2): what a buyer who builds on all of it pays to have it ready. |
| 936 | 6 | `public double buildableKm2PerUsd(LandParcel p)` | The buildable square kilometres a thousand US dollars, counting preparation (ET3, spec-earth2-terrain 5.2): what the pickers rank offers by in place of the dry square kilometres a dollar. |
| 944 | 4 | `public static boolean anyBuildable(Iterable<LandParcel> offers)` | Whether any of these offers has ground a building can stand on (buildableKm2()): when none has, the pickers rank by dry ground as they always did, so a city in the mountains still has a best. |
| 950 | 3 | `public double rankPerUsd(LandParcel p, boolean byBuildable)` | What the pickers rank an offer by: buildableKm2PerUsd() where `byBuildable`, else its dry square kilometres a dollar (getDryKm2PerUsd()) - anyBuildable() says which. |
| 960 | 4 | `public static boolean hasLinkedDry(LandParcel p)` | Whether any of an offer's dry ground is on ground that is room (ET5): all but an offer wholly across the water from every landmass the city has linked, which ranks at nothing and is the best of a shelf only when the w... |
| 966 | 5 | `private static double linkedDryKm2PerUsd(LandParcel p)` | An offer's dry square kilometres a thousand dollars, less what of its dry ground is on a landmass the city has not linked (ET5); LandParcel.getDryKm2PerUsd() where none is. |
| 973 | 3 | `public static double groundKm2(double[] km2)` | A piece of ground's square kilometres as the office charges them (0.7.106, out of price()): its dry ground, FRESH_PRICE_SHARE of its fresh water and SEA_PRICE_SHARE of its sea - what the ground's price a square foot i... |

### THE LISTING (lines 977-1261)

| line | len | member | says |
|---:|---:|---|---|
| 982 | 5 | `public List<LandParcel> getListing()` | The offers standing, side by side and place by place. |
| 989 | 8 | `public List<LandParcel> offersOn(int side)` | One side's offers, place by place: 0 north, 1 east, 2 south, 3 west; a place waiting empty is left out. |
| 999 | 3 | `public LandParcel offerIn(int side, int place)` | The offer standing in one place, or null while it waits empty: no room on that side yet. |
| 1010 | 8 | `public int emptyWithRoom()` | How many places wait empty though their side has room for them now: a listing GridOffers would stand against the offers standing (list() stands nothing). |
| 1020 | 5 | `public int emptyOn(int side)` | How many of a side's places wait empty. |
| 1027 | 5 | `public LandParcel find(int id)` | The offer standing under an id, or the draft (BL1: a negative id); null when neither. |
| 1041 | 1 | `public static boolean beats(double rate, double than)` | ...a rate that beats another by more than a tie (rates are never under nothing). |
| 1044 | 1 | `public static boolean tied(double a, double b)` | ...two rates that are one rank. |
| 1052 | 4 | `public boolean nearer(LandParcel a, LandParcel b)` | Whether a is nearer the city than b (0.7.67): the nearer its nearest plot to the founding site, L-infinity, then the lower id - a tie's breaker for the best-offer rules. |
| 1058 | 7 | `long reach(LandParcel p)` | How far an offer's nearest plot lies from the founding site, L-infinity, in plots: 0 when it holds the site's plot; 0 on a bare office. |
| 1075 | 7 | `public static boolean bareGround(LandParcel p)` | Whether an offer is bare ground (0.7.58): it holds none of a resource the office prices - ore under it is in its price, and a buyer short of ground has not asked for it (Game.bestOffer(), A SHORTFALL IS MET WITH GROUND). |
| 1084 | 9 | `public LandParcel cheapest()` | The cheapest offer standing, for a caller that just wants some land; the nearer on a tie. |
| 1095 | 9 | `public LandParcel cheapestWithSea()` | The cheapest offer with any sea in it, the nearer on a tie - what a desalination plant needs (0.7.59, batch J2); null when none has any. |
| 1111 | 13 | `public LandParcel bestFresh()` | The most fresh water a dollar (0.7.59, batch J2): the offer with the most square kilometres of lake or river a US dollar, the nearer on a tie; null when no offer holds any. |
| 1141 | 4 | `public LandParcel bestValue()` | THE BEST VALUE (spec-land star 14): the most dry square kilometres a dollar, the nearer on a tie, never an offer that is mostly sea. |
| 1146 | 17 | `private LandParcel bestDry(boolean notMostlySea)` |  |
| 1172 | 16 | `public LandParcel richest(Resource r)` | The richest offer in a resource (spec-land star 14): the most of its sites a dollar, then the most of it, then the nearer; null when no offer holds any. |
| 1196 | 11 | `public LandParcel cheapestWith(Resource r)` | The cheapest offer holding any of a resource, the nearer on a tie; null when none does (0.7.64, batch L). |
| 1232 | 8 | `public double goingUsdPerSqFt()` | THE GOING RATE ON THIS LISTING (0.7.26): the median of the offers' dollar prices a square foot of dry ground - what the land office judges each offer against ("44% under the going rate"). |
| 1248 | 13 | `public LandParcel take(int id)` | Takes an offer off the shelf. |

### THE PLAYER'S GROUND, DRAFTED INTO THE OFFICE (BL1, 2026-10-26) (lines 1262-1497)

| line | len | member | says |
|---:|---:|---|---|
| 1312 | 24 | `public List<LandParcel> draft(List<long[]> rects)` | The player's ground drafted into the office: each {x0, y0, x1, y1} a rectangle of plots on the city's blocks (whole blocks of getLevel(), half-open), measured as an offer of it is (measure(): the ground the city does ... |
| 1338 | 1 | `public List<LandParcel> getDrafts()` | The drafts standing, in the order drafted (none of them the office lists: getListing() never holds one). |
| 1344 | 7 | `public LandParcel quote(long x0, long y0, long x1, long y1)` | What a rectangle of plots would be drafted as, nothing kept and no id (0): the hover's figure for the block under the pointer. |
| 1352 | 3 | `private static boolean sameRect(LandParcel p, long x0, long y0, long x1, long y1)` |  |
| 1357 | 25 | `private LandParcel measureDraft(long x0, long y0, long x1, long y1, int id)` | A rectangle measured as an offer of it, under `id`: its side and place as GridOffers sees a point at its centre from the site (sidePlace()), so a holding bought from it records a side and place like any. |
| 1383 | 3 | `private boolean linkedAsMeasured(DraftGround measured)` |  |
| 1388 | 14 | `private void remeasureDrafts()` | Every draft priced again NOW, its geometry retained while its unowned plots are unchanged; bought ground is dropped. |
| 1407 | 16 | `public DraftRequest requestDrafts(List<long[]> rects)` | Starts a picked-ground measurement on the owner (FX) thread. |
| 1425 | 32 | **type** `public static final class DraftRequest implements Runnable` | A one-use worker job. |
| 1437 | 10 | `private DraftRequest(Quoter copy, CityLand land, LandSellers sellers, Landmasses landmasses, List<long[]> rects)` _(in LandMarket.DraftRequest)_ |  |
| 1449 | 1 | `public void cancel()` _(in LandMarket.DraftRequest)_ | Obsolete jobs still queued cost no seller walk; a running job can finish only on its private copy. |
| 1451 | 5 | `public void run()` _(in LandMarket.DraftRequest)_ |  |
| 1459 | 13 | `public boolean acceptDrafts(DraftRequest request)` | Owner (FX) thread only: rejects changed ground, then prices the returned geometry in the CURRENT month. |
| 1479 | 18 | `private LandParcel takeDraft(int id)` | A draft bought: it leaves its shelf, and every standing offer whose free ground its rectangle meets leaves the office's, unbought - its place emptied as take() empties one - because an offer keeps its listed price and... |

### A QUOTE AWAY FROM THE SCREEN'S THREAD (WN5b, 2026-11-02) (lines 1498-1589)

| line | len | member | says |
|---:|---:|---|---|
| 1517 | 12 | `public Quoter quoter()` | A copy of this office for quoting on another thread, or null on a bare office (no land) and where the land cannot be copied. |
| 1535 | 34 | **type** `public static final class Quoter` | The office as it stood when quoter() was called, for quoting blocks on any thread: its answer is the office's quote() of that moment, to the bit (the same code, on a copy of the land). |
| 1542 | 7 | `private Quoter(LandMarket frozen, CityLand original)` _(in LandMarket.Quoter)_ |  |
| 1551 | 9 | `public synchronized MeasuredQuote measure(long[] rect)` _(in LandMarket.Quoter)_ | Immutable measured ground for an FX-thread price, using only this quoter's private office. |
| 1562 | 3 | `public synchronized LandParcel quote(long x0, long y0, long x1, long y1)` _(in LandMarket.Quoter)_ | LandMarket.quote() of the office as it stood: any thread, one call at a time (the copy's land is not made for two readers at once). |
| 1567 | 1 | `public int month()` _(in LandMarket.Quoter)_ | The month the office priced in when it was copied. |
| 1571 | 9 | **type** `public static final class MeasuredQuote` | A worker's immutable geometry and its source, published through the screen's owner dispatcher. |
| 1575 | 4 | `private MeasuredQuote(Quoter source, DraftGround ground)` _(in LandMarket.MeasuredQuote)_ |  |
| 1582 | 7 | `public LandParcel priceQuote(MeasuredQuote quote)` | FX thread only. |

### SAVE AND RESTORE (lines 1590-1807)

| line | len | member | says |
|---:|---:|---|---|
| 1604 | 6 | `public double[][] getOffersState()` | The offers standing, one record each (LandParcel.offerRow()), side by side and place by place. |
| 1612 | 6 | `public double[][] getOffersReliefState()` | The offers' ground by relief (ET1), a record each in getOffersState()'s order (LandParcel.reliefRow()): DataSave's landOffersRelief. |
| 1623 | 10 | `public double[][] getOffersSellersState()` | The offers' sellers (WN5), a record each in getOffersState()'s order (LandParcel.sellersRow()): DataSave's landOffersSellers. |
| 1635 | 1 | `public int getNextOfferId()` | The id the next offer listed will take: DataSave's nextOfferId. |
| 1642 | 3 | `public void restoreOffers(double[][] rows, int nextOfferId)` | Saved offers back on the shelf, each in its own place, and the next id; a record of the wrong width, or a second one for a place, is dropped, and update() lists that place's next. |
| 1647 | 3 | `public void restoreOffers(double[][] rows, double[][] relief, int nextOfferId)` | ...with their ground by relief (ET1; getOffersReliefState(), a row for each of `rows` by its place): a key a save has not, or a row of the wrong width, reads the offer all flat. |
| 1652 | 10 | `public void restoreOffers(double[][] rows, double[][] relief, double[][] sellerRows, int nextOfferId)` | ...and their sellers (WN5; getOffersSellersState(), a row for each of `rows` by its place): none for a key a save has not, a row missing or not a record of sellers - an older save's offers were listed with no factor, ... |
| 1664 | 17 | `public void putOffers(List<LandParcel> list)` | These offers on the shelf in place of whatever stood there, each in its own place (a second for a place is dropped), their rectangles stood on the land's grid; the next id past the largest. |
| 1696 | 7 | `public int settleLocalPrices(double rate)` | AN OLDER SAVE'S QUOTE WAS LOCAL MONEY, and this reads it as US dollars at the rate of the day the save is loaded (0.7.6), when the price state came back without a dollar ground price - so the local quote the player sa... |
| 1705 | 1 | `public long getBlockPlots()` | The side of the city's blocks now, in plots: 2^getLevel() (four plots, 120 m, for a new city); 0 on a bare office. |
| 1735 | 4 | `public double[] getPriceState()` | THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them. |
| 1740 | 20 | `public void restorePriceState(double[] state)` |  |
| 1761 | 14 | `public void reset()` |  |
| 1790 | 5 | `public void redenominate(double scale)` | The office's LOCAL prices in the new unit - the inside price and the anchor it is struck from - and nothing else. |
| 1803 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

