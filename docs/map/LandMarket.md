# LandMarket.java - 1,010 lines · 50 methods · 16 constants · model

`ham/citybuildersim/LandMarket.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The land office's window: forty offers standing, ten on each side of the city - the next band of each lane - and what the ground costs.
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
> gone. The city stands on the world (CityLand), and each of its forty lanes
> has one offer standing: the next band of real ground, from the lane's
> frontier out to the radius that holds the offer's area (offerKm2(), spec-land
> star 2), with what the band really holds measured when it is listed. An offer
> is never rerolled; buying it moves its lane's frontier out and lists that
> lane's next band, and the other thirty-nine do not move. Priced by price():
> the ground at the world's dollar price, water cheaper, and each resource
> the city can use at a share of its world price (spec-land star 6).
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
> currency". The world sells the city its ground, as it always implicitly
> did, and is paid in its own money: the base and the premiums are dollar
> figures now, a parcel's DOLLAR price is what is frozen at listing, and
> what the treasury pays is that times the rate on the day it buys
> (LandParcel.localPrice(), Game.buyLandParcel()). At the founding rate of
> 1.00 every number is what it was. What BUSINESSES pay the city stays local
> money and is struck exactly as before (basePricePerSqFt), and a currency
> reform no longer touches the listing (redenominate()).
> 
> ...AND IT FOLLOWS THE DOLLAR'S OWN INFLATION (0.7.55)
> ... (7 more lines in the source)

**Uses:** [LandParcel](LandParcel.md) (46), [CityLand](CityLand.md) (15), [LandManager](LandManager.md) (6), [Resource](Resource.md) (6), [World](World.md) (5), [Founding](Founding.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (15):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 91 | · what the city pays |
| 137 | · THE CROWDING PREMIUM (0.7.55) |
| 272 | · what businesses pay |
| 325 | · how big an offer is |
| 351 | · the offers |
| 392 | PRICING |
| 535 | LISTING AN OFFER (0.7.57) |
| 626 | THE LISTING |
| 826 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `LandMarket.OFFERS_A_SIDE` | `CityLand.LANES` | Offers standing on each side of the city: ten, one a lane (spec-land star 1). |
| 89 | `LandMarket.OFFERS` | `CityLand.SIDES * OFFERS_A_SIDE` | Offers standing in all: OFFERS_A_SIDE on each of the four sides, forty. |
| 103 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands of US dollars. |
| 231 | `LandMarket.CROWDING_MIDPOINT` | `4_000` | People per square kilometre of the city's land at which the crowding premium is half way to its ceiling (0.7.55): about 60x the base. |
| 239 | `LandMarket.CROWDING_STEEPNESS` | `5.3` | How sharply the premium climbs through the midpoint: the curve's power (0.7.55). |
| 247 | `LandMarket.CROWDING_CEILING` | `120` | The most crowding can multiply the ground's price by, however crowded the city (0.7.55). |
| 254 | `LandMarket.FRESH_PRICE_SHARE` | `0.45` | What a square kilometre of fresh water sells for, against dry ground: 45% (the map mockup's figure, spec-land star 6). |
| 257 | `LandMarket.SEA_PRICE_SHARE` | `0.08` | ...and of sea: 8% (the mockup's), the reach a desalination plant needs and nothing else does. |
| 308 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 311 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 323 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 340 | `LandMarket.UNIT_FLOOR_KM2` | `LandManager.BLOCK_SQ_FT * LandManager.SQ_M_PER_SQ_FT / LandManager.SQ_M_PER_KM2` | The smallest unit an offer is a multiple of: one block (spec-land star 2), in square kilometres. |
| 343 | `LandMarket.UNIT_SHARE` | `0.01` | ...and the city's share it grows to: 1% of its whole area. |
| 346 | `LandMarket.PLOT_ODDS` | `0.55` | The share of offers that are a plot, 1 to 2.5 units: 55%. |
| 349 | `LandMarket.ROOM_ODDS` | `0.85` | ...and room to work, 2.5 to 5 units: the next 30% (to 85%); the rest, 15%, are a tract of 5 to 18. |
| 605 | `LandMarket.OFFER_STREAM` | `705_398_211_733L` | The stream offers' sizes are drawn from, against the world's other streams: the parcels' old seed. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `private double basePricePerSqFt` | The same base in LOCAL money, reformed with every other price and struck at the expected price level since 0.7.42 (seedConstants()) - what the inside price is struck from, and nothing else. |
| 354 | `private final LandParcel[] offers` | The forty standing, one a lane, side by side and lane by lane; null where a lane's next is not listed yet. |
| 355 | `private int nextId` |  |
| 358 | `private CityLand land` | The city's land the offers are bands of; none on a bare office, which lists nothing. |
| 361 | `private int month` | The month the office lists in, for each offer's record. |
| 368 | `private double marketPricePerSqFt` | Ground price per square foot right now, before any parcel's ore premium, in LOCAL money at the founding rate - the anchor the inside price is struck from (basePricePerSqFt), and since 0.7.6 nothing else. |
| 375 | `private double groundUsdPerSqFt` | ...and what the world asks for the same ground, in thousands of US dollars (0.7.6): every parcel listed from now on is priced off this, and it is the figure the history keeps as landPrice. |
| 385 | `private double crowding` | THE PRICE'S PARTS, AS STRUCK (0.7.55): the city's crowding - people per square kilometre of its land - the premium it came to, and the world's price level, so groundUsdPerSqFt is exactly BASE_PRICE_PER_SQ_FT x usPrice... |
| 386 | `private double crowdingPremium` |  |
| 387 | `private double usPriceLevel` |  |
| 390 | `private double salePricePerSqFt` | What businesses are charged. |
| 879 | `private boolean groundFromLocal` | True when the price state came back without a dollar ground price (an older save). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 76 | 935 | **type** `public class LandMarket` | The land office's window: forty offers standing, ten on each side of the city - the next band of each lane - and what the ground costs. |

### what the city pays (lines 91-136)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 1 | `public static double openingUsdPerSqFt()` | The ground's price a square foot at the founding, in thousands of US dollars: BASE_PRICE_PER_SQ_FT, before any premium - what an Insane city owes for its starting land (Founding.landBondUsd(), 0.7.14). |

### THE CROWDING PREMIUM (0.7.55) (lines 137-271)

### what businesses pay (lines 272-324)

### how big an offer is (lines 325-350)

### the offers (lines 351-391)

### PRICING (lines 392-534)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population)` | Re-prices the market and lists the next band of any lane without one. |
| 418 | 3 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices)` | ...and at the world's price level (0.7.55): US prices against the founding's, WorldEconomy.getPriceLevel() - the dollar's own inflation, which the world's price follows and the city's does not. |
| 423 | 23 | `public void update(double ownedSqFt, double allocatedSqFt, long population, double usPrices, int month)` | ...in a month, which each offer listed now records (0.7.57). |
| 467 | 18 | `public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt)` | How dear inside land is, as a multiple of what the ground cost outside. |
| 491 | 1 | `public double getMarketPricePerSqFt()` | The inside price's anchor: the ground price per square foot in local money at the founding rate, in thousands. |
| 494 | 1 | `public double getBasePricePerSqFt()` | The local anchor itself, before the premiums: BASE_PRICE_PER_SQ_FT in today's unit at the expected price level (0.7.42, Game.restrikeMoneyConstants()) - what ExpectationsCheck reads. |
| 497 | 1 | `public double getGroundUsdPerSqFt()` | Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). |
| 500 | 1 | `public double getSalePricePerSqFt()` | What a business pays the city per square foot, in thousands. |
| 508 | 5 | `public static double peoplePerKm2(long population, double ownedSqFt)` | A city's crowding (0.7.55): its people per square kilometre of the land it owns - the measure the premium reads. |
| 520 | 5 | `public static double crowdingPremium(double peoplePerKm2)` | What crowding multiplies the ground's price by (0.7.55): 1 for an empty city, CROWDING_CEILING at the limit, half way at CROWDING_MIDPOINT. |
| 527 | 1 | `public double getCrowding()` | The crowding the ground was last priced at: people per square kilometre of the city's land (0.7.55); 0 until a city's first month on 0.7.55. |
| 530 | 1 | `public double getCrowdingPremium()` | ...the premium it came to: what crowding multiplies the ground's price by (0.7.55). |
| 533 | 1 | `public double getUsPriceLevel()` | ...and the world's price level the dollar price was struck at: US prices against the founding's (0.7.55). |

### LISTING AN OFFER (0.7.57) (lines 535-625)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 4 | `void attach(CityLand land)` | Puts the office on a city's land with nothing on its shelf: what LandManager does when a city is founded, converted, loaded or restated. |
| 555 | 1 | `public CityLand getLand()` | The city's land the offers are bands of, or null on a bare office. |
| 558 | 6 | `void listMissing()` | Lists the next band of every lane without an offer, side by side and lane by lane, at the prices last struck. |
| 571 | 9 | `public LandParcel listNext(int side, int lane)` | The next band of one lane, as an offer: from the lane's frontier out to the radius that holds offerKm2() of ground, measured on the world, priced at the ground's dollar price struck last (price()), under a new id. |
| 582 | 3 | `public static double unitKm2(double cityKm2)` | The unit an offer is a multiple of, for a city of cityKm2 in all: one block, or UNIT_SHARE of the city when that is more (spec-land star 2). |
| 593 | 3 | `public double offerKm2(int id)` | How big offer `id` is, in square kilometres: the multiple its id draws times the city's unit now (spec-land star 2). |
| 598 | 5 | `static double sizeMultiple(long seed, int id)` | An offer's multiple of the unit, from the world's seed and its id: PLOT_ODDS between 1 and 2.5, up to ROOM_ODDS between 2.5 and 5, the rest between 5 and 18 - a mean of 3.81. |
| 616 | 9 | `public static double price(double[] km2, double[] amounts, double groundUsdPerSqFt, double usPriceLevel)` | An offer's price, in thousands of US dollars (spec-land star 6): the ground's dollar price a square foot on its dry ground, FRESH_PRICE_SHARE of it on its fresh water and SEA_PRICE_SHARE on its sea; and each resource ... |

### THE LISTING (lines 626-825)

| line | len | member | says |
|---:|---:|---|---|
| 631 | 5 | `public List<LandParcel> getListing()` | The offers standing, side by side and lane by lane. |
| 638 | 8 | `public List<LandParcel> offersOn(int side)` | One side's offers, lane by lane: 0 north, 1 east, 2 south, 3 west. |
| 648 | 3 | `public LandParcel offerIn(int side, int lane)` | The offer standing in one lane, or null when it has none yet. |
| 652 | 4 | `public LandParcel find(int id)` |  |
| 658 | 3 | `static boolean nearer(LandParcel a, LandParcel b)` | Whether a is nearer the city than b: the smaller inner radius, then the lower id. |
| 663 | 6 | `public static boolean bareGround(LandParcel p)` | Whether an offer is bare ground (0.7.58): it holds none of a resource the office prices - ore under it is in its price, and a buyer short of ground has not asked for it (Game.bestOffer(), A SHORTFALL IS MET WITH GROUND). |
| 671 | 9 | `public LandParcel cheapest()` | The cheapest offer standing, for a caller that just wants some land; the nearer on a tie. |
| 682 | 9 | `public LandParcel cheapestWithSea()` | The cheapest offer with any sea in it, the nearer on a tie - what a desalination plant needs (0.7.59, batch J2); null when none has any. |
| 698 | 13 | `public LandParcel bestFresh()` | The most fresh water a dollar (0.7.59, batch J2): the offer with the most square kilometres of lake or river a US dollar, the nearer on a tie; null when no offer holds any. |
| 721 | 4 | `public LandParcel bestValue()` | THE BEST VALUE (spec-land star 14): the most dry square kilometres a dollar, the nearer on a tie, never an offer that is mostly sea. |
| 726 | 9 | `private LandParcel bestDry(boolean notMostlySea)` |  |
| 742 | 15 | `public LandParcel richest(Resource r)` | The richest offer in a resource (spec-land star 14): the most of its sites a dollar, then the most of it, then the nearer; null when no offer holds any. |
| 765 | 9 | `public LandParcel cheapestWith(Resource r)` | The cheapest offer holding any of a resource, the nearer on a tie; null when none does (0.7.64, batch L). |
| 799 | 8 | `public double goingUsdPerSqFt()` | THE GOING RATE ON THIS LISTING (0.7.26): the median of the offers' dollar prices a square foot of dry ground - what the land office judges each offer against ("44% under the going rate"). |
| 815 | 10 | `public LandParcel take(int id)` | Takes an offer off the shelf. |

### SAVE AND RESTORE (lines 826-1010)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 6 | `public double[][] getOffersState()` | The offers standing, one record each (LandParcel.offerRow()), side by side and lane by lane. |
| 847 | 1 | `public int getNextOfferId()` | The id the next offer listed will take: DataSave's nextOfferId. |
| 854 | 9 | `public void restoreOffers(double[][] rows, int nextOfferId)` | Saved offers back on the shelf, each in its own lane, and the next id; a record of the wrong width, or a second one for a lane, is dropped, and update() lists that lane's next. |
| 865 | 12 | `public void putOffers(List<LandParcel> list)` | These offers on the shelf in place of whatever stood there, each in its own lane (a second for a lane is dropped); the next id past the largest. |
| 892 | 7 | `public int settleLocalPrices(double rate)` | AN OLDER SAVE'S QUOTE WAS LOCAL MONEY, and this reads it as US dollars at the rate of the day the save is loaded (0.7.6), when the price state came back without a dollar ground price - so the local quote the player sa... |
| 906 | 1 | `public double getMinSqFt()` | The unit offers are listed at now, in square feet: what the land office shows in square kilometres (0.7.13) - one block for a new city, 1% of the city's whole area past 0.929 km2 (0.7.57; until then the smallest parce... |
| 909 | 1 | `public double getMinBlocks()` | ...in blocks. |
| 912 | 1 | `public double getUnitKm2()` | ...in square kilometres: unitKm2() of the city's whole area, the land's own figure, so it is the same after a save. |
| 941 | 4 | `public double[] getPriceState()` | THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them. |
| 946 | 20 | `public void restorePriceState(double[] state)` |  |
| 967 | 11 | `public void reset()` |  |
| 993 | 5 | `public void redenominate(double scale)` | The office's LOCAL prices in the new unit - the inside price and the anchor it is struck from - and nothing else. |
| 1006 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). |

