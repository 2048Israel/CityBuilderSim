# LandMarket.java - 1,064 lines · 53 methods · 11 constants · model

`ham/citybuildersim/LandMarket.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [LandParcel](LandParcel.md) (52), [CityLand](CityLand.md) (14), [Resource](Resource.md) (11), [GridOffers](GridOffers.md) (8), [LandManager](LandManager.md) (2), [World](World.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (20):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [ConversionCheck](ConversionCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md)

## Sections

| line | section |
|---:|---|
| 99 | · what the city pays |
| 145 | · THE CROWDING PREMIUM (0.7.55) |
| 280 | · what businesses pay |
| 333 | · the offers |
| 380 | PRICING |
| 523 | LISTING AN OFFER (0.7.57; on the block grid since 0.7.67) |
| 645 | THE LISTING |
| 885 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `LandMarket.OFFERS_A_SIDE` | `GridOffers.PLACES` | Offers standing on each side of the city: six, one a place (GridOffers.PLACES; Jerus, 2026-10-07, "six offers a side"). |
| 97 | `LandMarket.OFFERS` | `CityLand.SIDES * OFFERS_A_SIDE` | Offers standing in all: OFFERS_A_SIDE on each of the four sides, twenty-four. |
| 111 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands of US dollars. |
| 239 | `LandMarket.CROWDING_MIDPOINT` | `4_000` | People per square kilometre of the city's land at which the crowding premium is half way to its ceiling (0.7.55): about 60x the base. |
| 247 | `LandMarket.CROWDING_STEEPNESS` | `5.3` | How sharply the premium climbs through the midpoint: the curve's power (0.7.55). |
| 255 | `LandMarket.CROWDING_CEILING` | `120` | The most crowding can multiply the ground's price by, however crowded the city (0.7.55). |
| 262 | `LandMarket.FRESH_PRICE_SHARE` | `0.45` | What a square kilometre of fresh water sells for, against dry ground: 45% (the map mockup's figure, spec-land star 6). |
| 265 | `LandMarket.SEA_PRICE_SHARE` | `0.08` | ...and of sea: 8% (the mockup's), the reach a desalination plant needs and nothing else does. |
| 316 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 319 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 331 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 143 | `private double basePricePerSqFt` | The same base in LOCAL money, reformed with every other price and struck at the expected price level since 0.7.42 (seedConstants()) - what the inside price is struck from, and nothing else. |
| 336 | `private final LandParcel[] offers` | The twenty-four standing, one a place, side by side and place by place; null where a place waits empty. |
| 337 | `private int nextId` |  |
| 340 | `private CityLand land` | The city's land the offers stand round; none on a bare office, which lists nothing. |
| 343 | `private GridOffers places` | The offers' rectangles on the land's grid, as GridOffers lists and clips them; null on a bare office. |
| 346 | `private int takenFrom` | The place an offer was last taken from, listed first when the shelf is filled again (GridOffers.relist()'s order); -1 for none. |
| 349 | `private int month` | The month the office lists in, for each offer's record. |
| 356 | `private double marketPricePerSqFt` | Ground price per square foot right now, before any parcel's ore premium, in LOCAL money at the founding rate - the anchor the inside price is struck from (basePricePerSqFt), and since 0.7.6 nothing else. |
| 363 | `private double groundUsdPerSqFt` | ...and what the world asks for the same ground, in thousands of US dollars (0.7.6): every parcel listed from now on is priced off this, and it is the figure the history keeps as landPrice. |
| 373 | `private double crowding` | THE PRICE'S PARTS, AS STRUCK (0.7.55): the city's crowding - people per square kilometre of its land - the premium it came to, and the world's price level, so groundUsdPerSqFt is exactly BASE_PRICE_PER_SQ_FT x usPrice... |
| 374 | `private double crowdingPremium` |  |
| 375 | `private double usPriceLevel` |  |
| 378 | `private double salePricePerSqFt` | What businesses are charged. |
| 941 | `private boolean groundFromLocal` | True when the price state came back without a dollar ground price (an older save). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 979 | **type** `public class LandMarket` | The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's edge, or none while that side has no room - and what the ground costs. |

### what the city pays (lines 99-144)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1 | `public static double openingUsdPerSqFt()` | The ground's price a square foot at the founding, in thousands of US dollars: BASE_PRICE_PER_SQ_FT, before any premium - what an Insane city owes for its starting land (Founding.landBondUsd(), 0.7.14). |

### THE CROWDING PREMIUM (0.7.55) (lines 145-279)

### what businesses pay (lines 280-332)

### the offers (lines 333-379)

### PRICING (lines 380-522)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population)` | Re-prices the market and lists the next offer of any place without one. |
| 406 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices)` | ...and at the world's price level (0.7.55): US prices against the founding's, WorldEconomy.getPriceLevel() - the dollar's own inflation, which the world's price follows and the city's does not. |
| 411 | 23 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices, int month)` | ...in a month, which each offer listed now records (0.7.57). |
| 455 | 18 | `public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt)` | How dear inside land is, as a multiple of what the ground cost outside. |
| 479 | 1 | `public double getMarketPricePerSqFt()` | The inside price's anchor: the ground price per square foot in local money at the founding rate, in thousands. |
| 482 | 1 | `public double getBasePricePerSqFt()` | The local anchor itself, before the premiums: BASE_PRICE_PER_SQ_FT in today's unit at the expected price level (0.7.42, Game.restrikeMoneyConstants()) - what ExpectationsCheck reads. |
| 485 | 1 | `public double getGroundUsdPerSqFt()` | Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). |
| 488 | 1 | `public double getSalePricePerSqFt()` | What a business pays the city per square foot, in thousands. |
| 496 | 5 | `public static double peoplePerKm2(long population, double ownedSqFt)` | A city's crowding (0.7.55): its people per square kilometre of the land it owns - the measure the premium reads. |
| 508 | 5 | `public static double crowdingPremium(double peoplePerKm2)` | What crowding multiplies the ground's price by (0.7.55): 1 for an empty city, CROWDING_CEILING at the limit, half way at CROWDING_MIDPOINT. |
| 515 | 1 | `public double getCrowding()` | The crowding the ground was last priced at: people per square kilometre of the city's land (0.7.55); 0 until a city's first month on 0.7.55. |
| 518 | 1 | `public double getCrowdingPremium()` | ...the premium it came to: what crowding multiplies the ground's price by (0.7.55). |
| 521 | 1 | `public double getUsPriceLevel()` | ...and the world's price level the dollar price was struck at: US prices against the founding's (0.7.55). |

### LISTING AN OFFER (0.7.57; on the block grid since 0.7.67) (lines 523-644)

| line | len | member | says |
|---:|---:|---|---|
| 538 | 6 | `void attach(CityLand land)` | Puts the office on a city's land with nothing on its shelf: what LandManager does when a city is founded, converted, loaded or restated. |
| 546 | 1 | `public CityLand getLand()` | The city's land the offers stand round, or null on a bare office. |
| 549 | 7 | `void listMissing()` | Lists every place without an offer - the one an offer was just taken from first, then North 1 to West 6 - at the prices last struck; a place with no room waits empty. |
| 558 | 6 | `private void listIn(int i)` | Lists place i (side i / OFFERS_A_SIDE, place i % OFFERS_A_SIDE) and stands it, or leaves it waiting. |
| 566 | 7 | `private void restand()` | The offers standing, stood again on a grid of the land's: what a restore or a hand-made listing goes through. |
| 581 | 3 | `LandParcel measure(GridOffers.Rect r)` | An offer of a rectangle: its ground the plots of it the city does not own, counted on the world (CityLand.groundOf()), the fields on that ground (CityLand.fieldsOn()) and its forest's timber, priced at the ground's do... |
| 586 | 9 | `private LandParcel measure(GridOffers.Rect r, int id, int listedIn)` | ...under a given id, as listed in a given month. |
| 607 | 15 | `public int[] remeasureStanding()` | THE WORLD'S FIELDS CHANGED UNDER THE OFFERS (0.7.99, batch W1): every offer standing measured again on the world as it is now - the same place, rectangle, id and month listed; its ground counted, its fields the world'... |
| 624 | 1 | `public int getLevel()` | The city's block level now: LandGrid.levelFor() of every plot it owns - what a new offer's blocks are, or one finer (spec-grid star 2); 0 on a bare office. |
| 635 | 9 | `public static double price(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | An offer's price, in thousands of US dollars (spec-land star 6): the ground's dollar price a square foot on its dry ground, FRESH_PRICE_SHARE of it on its fresh water and SEA_PRICE_SHARE on its sea; and each resource ... |

### THE LISTING (lines 645-884)

| line | len | member | says |
|---:|---:|---|---|
| 650 | 5 | `public List<LandParcel> getListing()` | The offers standing, side by side and place by place. |
| 657 | 8 | `public List<LandParcel> offersOn(int side)` | One side's offers, place by place: 0 north, 1 east, 2 south, 3 west; a place waiting empty is left out. |
| 667 | 3 | `public LandParcel offerIn(int side, int place)` | The offer standing in one place, or null while it waits empty: no room on that side yet. |
| 678 | 8 | `public int emptyWithRoom()` | How many places wait empty though their side has room for them now: a listing GridOffers would stand against the offers standing (list() stands nothing). |
| 688 | 5 | `public int emptyOn(int side)` | How many of a side's places wait empty. |
| 694 | 4 | `public LandParcel find(int id)` |  |
| 705 | 4 | `public boolean nearer(LandParcel a, LandParcel b)` | Whether a is nearer the city than b (0.7.67): the nearer its nearest plot to the founding site, L-infinity, then the lower id - a tie's breaker for the best-offer rules. |
| 711 | 7 | `long reach(LandParcel p)` | How far an offer's nearest plot lies from the founding site, L-infinity, in plots: 0 when it holds the site's plot; 0 on a bare office. |
| 720 | 6 | `public static boolean bareGround(LandParcel p)` | Whether an offer is bare ground (0.7.58): it holds none of a resource the office prices - ore under it is in its price, and a buyer short of ground has not asked for it (Game.bestOffer(), A SHORTFALL IS MET WITH GROUND). |
| 728 | 9 | `public LandParcel cheapest()` | The cheapest offer standing, for a caller that just wants some land; the nearer on a tie. |
| 739 | 9 | `public LandParcel cheapestWithSea()` | The cheapest offer with any sea in it, the nearer on a tie - what a desalination plant needs (0.7.59, batch J2); null when none has any. |
| 755 | 13 | `public LandParcel bestFresh()` | The most fresh water a dollar (0.7.59, batch J2): the offer with the most square kilometres of lake or river a US dollar, the nearer on a tie; null when no offer holds any. |
| 778 | 4 | `public LandParcel bestValue()` | THE BEST VALUE (spec-land star 14): the most dry square kilometres a dollar, the nearer on a tie, never an offer that is mostly sea. |
| 783 | 9 | `private LandParcel bestDry(boolean notMostlySea)` |  |
| 799 | 15 | `public LandParcel richest(Resource r)` | The richest offer in a resource (spec-land star 14): the most of its sites a dollar, then the most of it, then the nearer; null when no offer holds any. |
| 822 | 9 | `public LandParcel cheapestWith(Resource r)` | The cheapest offer holding any of a resource, the nearer on a tie; null when none does (0.7.64, batch L). |
| 856 | 8 | `public double goingUsdPerSqFt()` | THE GOING RATE ON THIS LISTING (0.7.26): the median of the offers' dollar prices a square foot of dry ground - what the land office judges each offer against ("44% under the going rate"). |
| 872 | 12 | `public LandParcel take(int id)` | Takes an offer off the shelf. |

### SAVE AND RESTORE (lines 885-1064)

| line | len | member | says |
|---:|---:|---|---|
| 899 | 6 | `public double[][] getOffersState()` | The offers standing, one record each (LandParcel.offerRow()), side by side and place by place. |
| 907 | 1 | `public int getNextOfferId()` | The id the next offer listed will take: DataSave's nextOfferId. |
| 914 | 9 | `public void restoreOffers(double[][] rows, int nextOfferId)` | Saved offers back on the shelf, each in its own place, and the next id; a record of the wrong width, or a second one for a place, is dropped, and update() lists that place's next. |
| 925 | 14 | `public void putOffers(List<LandParcel> list)` | These offers on the shelf in place of whatever stood there, each in its own place (a second for a place is dropped), their rectangles stood on the land's grid; the next id past the largest. |
| 954 | 7 | `public int settleLocalPrices(double rate)` | AN OLDER SAVE'S QUOTE WAS LOCAL MONEY, and this reads it as US dollars at the rate of the day the save is loaded (0.7.6), when the price state came back without a dollar ground price - so the local quote the player sa... |
| 963 | 1 | `public long getBlockPlots()` | The side of the city's blocks now, in plots: 2^getLevel() (four plots, 120 m, for a new city); 0 on a bare office. |
| 993 | 4 | `public double[] getPriceState()` | THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them. |
| 998 | 20 | `public void restorePriceState(double[] state)` |  |
| 1019 | 13 | `public void reset()` |  |
| 1047 | 5 | `public void redenominate(double scale)` | The office's LOCAL prices in the new unit - the inside price and the anchor it is struck from - and nothing else. |
| 1060 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

