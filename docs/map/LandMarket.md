# LandMarket.java - 1,608 lines · 98 methods · 14 constants · model

`ham/citybuildersim/LandMarket.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [LandParcel](LandParcel.md) (88), [CityLand](CityLand.md) (39), [World](World.md) (17), [Resource](Resource.md) (13), [GridOffers](GridOffers.md) (11), [LandManager](LandManager.md) (8), [LandSellers](LandSellers.md) (5), [PriceLevel](PriceLevel.md) (2), [Landmasses](Landmasses.md) (2), [LandFactors](LandFactors.md) (1), [SitePreparation](SitePreparation.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (33):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CoalMiningCheck](CoalMiningCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [GlobeFrame](GlobeFrame.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandNationsCheck](LandNationsCheck.md), [LandScreen](LandScreen.md), [LandmassCheck](LandmassCheck.md), [Landmasses](Landmasses.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 99 | · what the city pays |
| 147 | · THE CROWDING PREMIUM (0.7.55) |
| 282 | · what businesses pay |
| 335 | · the offers |
| 382 | PRICING |
| 528 | LISTING AN OFFER (0.7.57; on the block grid since 0.7.67) |
| 771 | · a price for each class of ground (ET3; spec-earth2-terrain 5.1, 5.2) |
| 925 | THE LISTING |
| 1210 | THE PLAYER'S GROUND, DRAFTED INTO THE OFFICE (BL1, 2026-10-26) |
| 1338 | A QUOTE AWAY FROM THE SCREEN'S THREAD (WN5b, 2026-11-02) |
| 1391 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `LandMarket.OFFERS_A_SIDE` | `GridOffers.PLACES` | Offers standing on each side of the city: six, one a place (GridOffers.PLACES; Jerus, 2026-10-07, "six offers a side"). |
| 97 | `LandMarket.OFFERS` | `CityLand.SIDES * OFFERS_A_SIDE` | Offers standing in all: OFFERS_A_SIDE on each of the four sides, twenty-four. |
| 113 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007 * PriceLevel.FACTOR` | Ground price per square foot before any premium, in thousands of US dollars. |
| 241 | `LandMarket.CROWDING_MIDPOINT` | `4_000` | People per square kilometre of the city's land at which the crowding premium is half way to its ceiling (0.7.55): about 60x the base. |
| 249 | `LandMarket.CROWDING_STEEPNESS` | `5.3` | How sharply the premium climbs through the midpoint: the curve's power (0.7.55). |
| 257 | `LandMarket.CROWDING_CEILING` | `120` | The most crowding can multiply the ground's price by, however crowded the city (0.7.55). |
| 264 | `LandMarket.FRESH_PRICE_SHARE` | `0.45` | What a square kilometre of fresh water sells for, against dry ground: 45% (the map mockup's figure, spec-land star 6). |
| 267 | `LandMarket.SEA_PRICE_SHARE` | `0.08` | ...and of sea: 8% (the mockup's), the reach a desalination plant needs and nothing else does. |
| 318 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 321 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 333 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 754 | `LandMarket.PRICE_STEP_USD` | `5 * PriceLevel.FACTOR` | The step an offer's price is rounded to, in thousands of US dollars: US$5k, as the parcels were (nobody wants to compare US$103,847 against US$98,211). |
| 986 | `LandMarket.RANK_TIE` | `1e-12` | How close two ranks (rates of ground a dollar, or prices that carry a sum) must be to be one rank: a part in a trillion. |
| 1349 | `LandMarket.QUOTER_MS` | `16` | The most making a quoter may take on the screen's thread, in milliseconds: a frame at 60 Hz. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 145 | `private double basePricePerSqFt` | The same base in LOCAL money, reformed with every other price and struck at the expected price level since 0.7.42 (seedConstants()) - what the inside price is struck from, and nothing else. |
| 338 | `private final LandParcel[] offers` | The twenty-four standing, one a place, side by side and place by place; null where a place waits empty. |
| 339 | `private int nextId` |  |
| 342 | `private CityLand land` | The city's land the offers stand round; none on a bare office, which lists nothing. |
| 345 | `private GridOffers places` | The offers' rectangles on the land's grid, as GridOffers lists and clips them; null on a bare office. |
| 348 | `private int takenFrom` | The place an offer was last taken from, listed first when the shelf is filled again (GridOffers.relist()'s order); -1 for none. |
| 351 | `private int month` | The month the office lists in, for each offer's record. |
| 358 | `private double marketPricePerSqFt` | Ground price per square foot right now, before any parcel's ore premium, in LOCAL money at the founding rate - the anchor the inside price is struck from (basePricePerSqFt), and since 0.7.6 nothing else. |
| 365 | `private double groundUsdPerSqFt` | ...and what the world asks for the same ground, in thousands of US dollars (0.7.6): every parcel listed from now on is priced off this, and it is the figure the history keeps as landPrice. |
| 375 | `private double crowding` | THE PRICE'S PARTS, AS STRUCK (0.7.55): the city's crowding - people per square kilometre of its land - the premium it came to, and the world's price level, so groundUsdPerSqFt is exactly BASE_PRICE_PER_SQ_FT x usPrice... |
| 376 | `private double crowdingPremium` |  |
| 377 | `private double usPriceLevel` |  |
| 380 | `private double salePricePerSqFt` | What businesses are charged. |
| 625 | `private LandSellers sellers` | The sellers of the city's ground, null for none. |
| 681 | `private Landmasses landmasses` | The city's ground by landmass (ET5), which splits each offer's ground by linked and unlinked; null on an office with none, a bare one's harness-made. |
| 1242 | `private final List<LandParcel> drafts` | The drafts standing now, in the order the player's ground was cut; each a LandParcel under a negative id. |
| 1245 | `private int nextDraftId` | The id the next draft takes: -1, then -2, and so on - a counter of its own, never nextId, so no offer's number moves for a draft. |
| 1376 | `private final LandMarket frozen` |  |
| 1484 | `private boolean groundFromLocal` | True when the price state came back without a dollar ground price (an older save). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 1523 | **type** `public class LandMarket` | The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's edge, or none while that side has no room - and what the ground costs. |

### what the city pays (lines 99-146)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1 | `public static double openingUsdPerSqFt()` | The ground's price a square foot at the founding, in thousands of US dollars: BASE_PRICE_PER_SQ_FT, before any premium - what an Insane city owes a square foot of its starting land (Founding.landBondUsd(), 0.7.14; sin... |

### THE CROWDING PREMIUM (0.7.55) (lines 147-281)

### what businesses pay (lines 282-334)

### the offers (lines 335-381)

### PRICING (lines 382-527)

| line | len | member | says |
|---:|---:|---|---|
| 399 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population)` | Re-prices the market and lists the next offer of any place without one. |
| 408 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices)` | ...and at the world's price level (0.7.55): US prices against the founding's, WorldEconomy.getPriceLevel() - the dollar's own inflation, which the world's price follows and the city's does not. |
| 413 | 26 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices, int month)` | ...in a month, which each offer listed now records (0.7.57). |
| 460 | 18 | `public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt)` | How dear inside land is, as a multiple of what the ground cost outside. |
| 484 | 1 | `public double getMarketPricePerSqFt()` | The inside price's anchor: the ground price per square foot in local money at the founding rate, in thousands. |
| 487 | 1 | `public double getBasePricePerSqFt()` | The local anchor itself, before the premiums: BASE_PRICE_PER_SQ_FT in today's unit at the expected price level (0.7.42, Game.restrikeMoneyConstants()) - what ExpectationsCheck reads. |
| 490 | 1 | `public double getGroundUsdPerSqFt()` | Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). |
| 493 | 1 | `public double getSalePricePerSqFt()` | What a business pays the city per square foot, in thousands. |
| 501 | 5 | `public static double peoplePerKm2(long population, double ownedSqFt)` | A city's crowding (0.7.55): its people per square kilometre of the land it owns - the measure the premium reads. |
| 513 | 5 | `public static double crowdingPremium(double peoplePerKm2)` | What crowding multiplies the ground's price by (0.7.55): 1 for an empty city, CROWDING_CEILING at the limit, half way at CROWDING_MIDPOINT. |
| 520 | 1 | `public double getCrowding()` | The crowding the ground was last priced at: people per square kilometre of the city's land (0.7.55); 0 until a city's first month on 0.7.55. |
| 523 | 1 | `public double getCrowdingPremium()` | ...the premium it came to: what crowding multiplies the ground's price by (0.7.55). |
| 526 | 1 | `public double getUsPriceLevel()` | ...and the world's price level the dollar price was struck at: US prices against the founding's (0.7.55). |

### LISTING AN OFFER (0.7.57; on the block grid since 0.7.67) (lines 528-770)

| line | len | member | says |
|---:|---:|---|---|
| 543 | 9 | `void attach(CityLand land)` | Puts the office on a city's land with nothing on its shelf: what LandManager does when a city is founded, converted, loaded or restated. |
| 554 | 1 | `public CityLand getLand()` | The city's land the offers stand round, or null on a bare office. |
| 557 | 7 | `void listMissing()` | Lists every place without an offer - the one an offer was just taken from first, then North 1 to West 6 - at the prices last struck; a place with no room waits empty. |
| 566 | 6 | `private void listIn(int i)` | Lists place i (side i / OFFERS_A_SIDE, place i % OFFERS_A_SIDE) and stands it, or leaves it waiting. |
| 574 | 7 | `private void restand()` | The offers standing, stood again on a grid of the land's: what a restore or a hand-made listing goes through. |
| 589 | 3 | `LandParcel measure(GridOffers.Rect r)` | An offer of a rectangle: its ground the plots of it the city does not own, counted on the world (CityLand.groundOf()), the fields on that ground (CityLand.fieldsOn()) and its forest's timber, priced at the ground's do... |
| 594 | 22 | `private LandParcel measure(GridOffers.Rect r, int id, int listedIn)` | ...under a given id, as listed in a given month. |
| 628 | 1 | `public LandSellers getSellers()` | The nations that sell the ground the offers stand on, or null where none does (a generated world, no world file, the literals). |
| 631 | 3 | `void setSellers(LandSellers sellers)` | Hands the office the sellers of its land (Game, before the founding's listing and in the load path); null for none. |
| 636 | 3 | `void syncMonth(int month)` | Sets the month the office prices in: the load path's, where the office has not been through update() yet and a quote's land factors move with the month. |
| 647 | 32 | `private double priceBySellers(GridOffers.Rect r, double[] km2, List<LandParcel.Sale> sold)` | An offer's price with sellers: each part of its ground (LandSellers.split()) priced raw at the dollar formula of its own km2, relief and fields (forest's timber from the part's own forest, as measure() does for the wh... |
| 684 | 3 | `void setLandmasses(Landmasses landmasses)` | Hands the office the city's landmasses (LandManager's constructor): every offer listed or restored from now on is split against them. |
| 693 | 6 | `void restateOffers()` | Splits every offer standing again against the linked landmasses as they are now (ET5): what a land change or a change to the linked set asks, since an offer's ground is frozen at its listing but which of it is unlinke... |
| 711 | 15 | `public int[] remeasureStanding()` | THE WORLD'S FIELDS CHANGED UNDER THE OFFERS (0.7.99, batch W1): every offer standing measured again on the world as it is now - the same place, rectangle, id and month listed; its ground counted, its fields the world'... |
| 728 | 1 | `public int getLevel()` | The city's block level now: LandGrid.levelFor() of every plot it owns - what a new offer's blocks are, or one finer (spec-grid star 2); 0 on a bare office. |
| 739 | 3 | `public static double price(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | An offer's price, in thousands of US dollars (spec-land star 6): the ground's dollar price a square foot on its dry ground, FRESH_PRICE_SHARE of it on its fresh water and SEA_PRICE_SHARE on its sea; and each resource ... |
| 744 | 4 | `private static double rawPrice(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | price() before its rounding (WN5: the parts of an offer are priced raw, added at their sellers' factors and rounded once). |
| 757 | 8 | `private static double rawPlusResources(double groundUsd, double[] amounts, double usPriceLevel)` | The resources' share of an offer's price added to the ground's, in thousands of US dollars (price()'s second half), before its rounding to PRICE_STEP_USD (rounded()). |
| 767 | 3 | `private static double rounded(double usd)` | A price rounded to PRICE_STEP_USD. |

### a price for each class of ground (ET3; spec-earth2-terrain 5.1, 5.2) (lines 771-924)

| line | len | member | says |
|---:|---:|---|---|
| 782 | 11 | `public static double reliefPrice(int relief, double going, double base, double prep)` | THE RESIDUAL PRICE of a square foot of dry ground of one relief (ET3, spec-earth2-terrain 5.1), in the money the three prices are given in: `going` is flat ground's price (the market times crowding, times scarcity ins... |
| 795 | 3 | `public static double prepUsdPerSqFt(double usPriceLevel)` | What preparing a square foot of foothill costs at the world's price level, in thousands of US dollars: SitePreparation's dollars a m2, over a thousand, a square foot's worth of m2 (LandManager.SQ_M_PER_SQ_FT), at the ... |
| 800 | 4 | `private static boolean allFlat(double[] relief)` | Whether a ground's relief row has no foothill, steep or very steep ground in it: a null row, a row of another width, and every generated world's. |
| 806 | 4 | `static boolean allFlat(LandParcel p)` | ...for an offer: its own relief (LandParcel.getReliefKm2()). |
| 819 | 3 | `public static double price(double[] km2, double[] relief, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | An offer's price with its dry ground by relief (ET3, spec-earth2-terrain 5.2), in thousands of US dollars: flat ground at the dollar price a square foot, foothill at its residual price (reliefPrice(): that price less ... |
| 824 | 10 | `private static double rawPrice(double[] km2, double[] relief, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | price() with relief before its rounding (WN5). |
| 836 | 1 | `public double getFlatUsdPerSqFt()` | The ground flat at the going dollar price, in thousands of US dollars a square foot: what the office asks for flat ground now (getGroundUsdPerSqFt()). |
| 839 | 3 | `public double getFoothillUsdPerSqFt()` | ...for foothill: the going price less its preparation, never under the base price (reliefPrice()). |
| 844 | 3 | `public double getSteepUsdPerSqFt()` | ...for steep and very steep ground: the base price (reliefPrice()), never over the flat price. |
| 854 | 4 | `public static double buildableKm2(LandParcel p)` | An offer's ground that a building can stand on, in square kilometres (ET3): its flat and its foothill, which buildings may use (LandManager, BUILDABLE BY SLOPE); the steep and very steep ground is for works. |
| 860 | 3 | `private static double wholeBuildableKm2(LandParcel p)` | An offer's buildable ground whether or not it is linked (buildableKm2() before ET5), square kilometres. |
| 865 | 5 | `public static double buildableSqFt(LandParcel p)` | ...in square feet. |
| 872 | 4 | `public double priceWithPrepUsd(LandParcel p)` | An offer's price with the preparation of its foothill added, in thousands of US dollars (ET3, spec 5.2): what a buyer who builds on all of it pays to have it ready. |
| 884 | 6 | `public double buildableKm2PerUsd(LandParcel p)` | The buildable square kilometres a thousand US dollars, counting preparation (ET3, spec-earth2-terrain 5.2): what the pickers rank offers by in place of the dry square kilometres a dollar. |
| 892 | 4 | `public static boolean anyBuildable(Iterable<LandParcel> offers)` | Whether any of these offers has ground a building can stand on (buildableKm2()): when none has, the pickers rank by dry ground as they always did, so a city in the mountains still has a best. |
| 898 | 3 | `public double rankPerUsd(LandParcel p, boolean byBuildable)` | What the pickers rank an offer by: buildableKm2PerUsd() where `byBuildable`, else its dry square kilometres a dollar (getDryKm2PerUsd()) - anyBuildable() says which. |
| 908 | 4 | `public static boolean hasLinkedDry(LandParcel p)` | Whether any of an offer's dry ground is on ground that is room (ET5): all but an offer wholly across the water from every landmass the city has linked, which ranks at nothing and is the best of a shelf only when the w... |
| 914 | 5 | `private static double linkedDryKm2PerUsd(LandParcel p)` | An offer's dry square kilometres a thousand dollars, less what of its dry ground is on a landmass the city has not linked (ET5); LandParcel.getDryKm2PerUsd() where none is. |
| 921 | 3 | `public static double groundKm2(double[] km2)` | A piece of ground's square kilometres as the office charges them (0.7.106, out of price()): its dry ground, FRESH_PRICE_SHARE of its fresh water and SEA_PRICE_SHARE of its sea - what the ground's price a square foot i... |

### THE LISTING (lines 925-1209)

| line | len | member | says |
|---:|---:|---|---|
| 930 | 5 | `public List<LandParcel> getListing()` | The offers standing, side by side and place by place. |
| 937 | 8 | `public List<LandParcel> offersOn(int side)` | One side's offers, place by place: 0 north, 1 east, 2 south, 3 west; a place waiting empty is left out. |
| 947 | 3 | `public LandParcel offerIn(int side, int place)` | The offer standing in one place, or null while it waits empty: no room on that side yet. |
| 958 | 8 | `public int emptyWithRoom()` | How many places wait empty though their side has room for them now: a listing GridOffers would stand against the offers standing (list() stands nothing). |
| 968 | 5 | `public int emptyOn(int side)` | How many of a side's places wait empty. |
| 975 | 5 | `public LandParcel find(int id)` | The offer standing under an id, or the draft (BL1: a negative id); null when neither. |
| 989 | 1 | `public static boolean beats(double rate, double than)` | ...a rate that beats another by more than a tie (rates are never under nothing). |
| 992 | 1 | `public static boolean tied(double a, double b)` | ...two rates that are one rank. |
| 1000 | 4 | `public boolean nearer(LandParcel a, LandParcel b)` | Whether a is nearer the city than b (0.7.67): the nearer its nearest plot to the founding site, L-infinity, then the lower id - a tie's breaker for the best-offer rules. |
| 1006 | 7 | `long reach(LandParcel p)` | How far an offer's nearest plot lies from the founding site, L-infinity, in plots: 0 when it holds the site's plot; 0 on a bare office. |
| 1023 | 7 | `public static boolean bareGround(LandParcel p)` | Whether an offer is bare ground (0.7.58): it holds none of a resource the office prices - ore under it is in its price, and a buyer short of ground has not asked for it (Game.bestOffer(), A SHORTFALL IS MET WITH GROUND). |
| 1032 | 9 | `public LandParcel cheapest()` | The cheapest offer standing, for a caller that just wants some land; the nearer on a tie. |
| 1043 | 9 | `public LandParcel cheapestWithSea()` | The cheapest offer with any sea in it, the nearer on a tie - what a desalination plant needs (0.7.59, batch J2); null when none has any. |
| 1059 | 13 | `public LandParcel bestFresh()` | The most fresh water a dollar (0.7.59, batch J2): the offer with the most square kilometres of lake or river a US dollar, the nearer on a tie; null when no offer holds any. |
| 1089 | 4 | `public LandParcel bestValue()` | THE BEST VALUE (spec-land star 14): the most dry square kilometres a dollar, the nearer on a tie, never an offer that is mostly sea. |
| 1094 | 17 | `private LandParcel bestDry(boolean notMostlySea)` |  |
| 1120 | 16 | `public LandParcel richest(Resource r)` | The richest offer in a resource (spec-land star 14): the most of its sites a dollar, then the most of it, then the nearer; null when no offer holds any. |
| 1144 | 11 | `public LandParcel cheapestWith(Resource r)` | The cheapest offer holding any of a resource, the nearer on a tie; null when none does (0.7.64, batch L). |
| 1180 | 8 | `public double goingUsdPerSqFt()` | THE GOING RATE ON THIS LISTING (0.7.26): the median of the offers' dollar prices a square foot of dry ground - what the land office judges each offer against ("44% under the going rate"). |
| 1196 | 13 | `public LandParcel take(int id)` | Takes an offer off the shelf. |

### THE PLAYER'S GROUND, DRAFTED INTO THE OFFICE (BL1, 2026-10-26) (lines 1210-1337)

| line | len | member | says |
|---:|---:|---|---|
| 1255 | 21 | `public List<LandParcel> draft(List<long[]> rects)` | The player's ground drafted into the office: each {x0, y0, x1, y1} a rectangle of plots on the city's blocks (whole blocks of getLevel(), half-open), measured as an offer of it is (measure(): the ground the city does ... |
| 1278 | 1 | `public List<LandParcel> getDrafts()` | The drafts standing, in the order drafted (none of them the office lists: getListing() never holds one). |
| 1284 | 7 | `public LandParcel quote(long x0, long y0, long x1, long y1)` | What a rectangle of plots would be drafted as, nothing kept and no id (0): the hover's figure for the block under the pointer. |
| 1292 | 3 | `private static boolean sameRect(LandParcel p, long x0, long y0, long x1, long y1)` |  |
| 1297 | 5 | `private LandParcel measureDraft(long x0, long y0, long x1, long y1, int id)` | A rectangle measured as an offer of it, under `id`: its side and place as GridOffers sees a point at its centre from the site (sidePlace()), so a holding bought from it records a side and place like any. |
| 1304 | 8 | `private void remeasureDrafts()` | Every draft measured again at the price struck now, its id kept; one whose ground the city now wholly owns is dropped. |
| 1319 | 18 | `private LandParcel takeDraft(int id)` | A draft bought: it leaves its shelf, and every standing offer whose free ground its rectangle meets leaves the office's, unbought - its place emptied as take() empties one - because an offer keeps its listed price and... |

### A QUOTE AWAY FROM THE SCREEN'S THREAD (WN5b, 2026-11-02) (lines 1338-1390)

| line | len | member | says |
|---:|---:|---|---|
| 1357 | 12 | `public Quoter quoter()` | A copy of this office for quoting on another thread, or null on a bare office (no land) and where the land cannot be copied. |
| 1375 | 15 | **type** `public static final class Quoter` | The office as it stood when quoter() was called, for quoting blocks on any thread: its answer is the office's quote() of that moment, to the bit (the same code, on a copy of the land). |
| 1378 | 3 | `private Quoter(LandMarket frozen)` _(in LandMarket.Quoter)_ |  |
| 1383 | 3 | `public synchronized LandParcel quote(long x0, long y0, long x1, long y1)` _(in LandMarket.Quoter)_ | LandMarket.quote() of the office as it stood: any thread, one call at a time (the copy's land is not made for two readers at once). |
| 1388 | 1 | `public int month()` _(in LandMarket.Quoter)_ | The month the office priced in when it was copied. |

### SAVE AND RESTORE (lines 1391-1608)

| line | len | member | says |
|---:|---:|---|---|
| 1405 | 6 | `public double[][] getOffersState()` | The offers standing, one record each (LandParcel.offerRow()), side by side and place by place. |
| 1413 | 6 | `public double[][] getOffersReliefState()` | The offers' ground by relief (ET1), a record each in getOffersState()'s order (LandParcel.reliefRow()): DataSave's landOffersRelief. |
| 1424 | 10 | `public double[][] getOffersSellersState()` | The offers' sellers (WN5), a record each in getOffersState()'s order (LandParcel.sellersRow()): DataSave's landOffersSellers. |
| 1436 | 1 | `public int getNextOfferId()` | The id the next offer listed will take: DataSave's nextOfferId. |
| 1443 | 3 | `public void restoreOffers(double[][] rows, int nextOfferId)` | Saved offers back on the shelf, each in its own place, and the next id; a record of the wrong width, or a second one for a place, is dropped, and update() lists that place's next. |
| 1448 | 3 | `public void restoreOffers(double[][] rows, double[][] relief, int nextOfferId)` | ...with their ground by relief (ET1; getOffersReliefState(), a row for each of `rows` by its place): a key a save has not, or a row of the wrong width, reads the offer all flat. |
| 1453 | 10 | `public void restoreOffers(double[][] rows, double[][] relief, double[][] sellerRows, int nextOfferId)` | ...and their sellers (WN5; getOffersSellersState(), a row for each of `rows` by its place): none for a key a save has not, a row missing or not a record of sellers - an older save's offers were listed with no factor, ... |
| 1465 | 17 | `public void putOffers(List<LandParcel> list)` | These offers on the shelf in place of whatever stood there, each in its own place (a second for a place is dropped), their rectangles stood on the land's grid; the next id past the largest. |
| 1497 | 7 | `public int settleLocalPrices(double rate)` | AN OLDER SAVE'S QUOTE WAS LOCAL MONEY, and this reads it as US dollars at the rate of the day the save is loaded (0.7.6), when the price state came back without a dollar ground price - so the local quote the player sa... |
| 1506 | 1 | `public long getBlockPlots()` | The side of the city's blocks now, in plots: 2^getLevel() (four plots, 120 m, for a new city); 0 on a bare office. |
| 1536 | 4 | `public double[] getPriceState()` | THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them. |
| 1541 | 20 | `public void restorePriceState(double[] state)` |  |
| 1562 | 14 | `public void reset()` |  |
| 1591 | 5 | `public void redenominate(double scale)` | The office's LOCAL prices in the new unit - the inside price and the anchor it is struck from - and nothing else. |
| 1604 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

