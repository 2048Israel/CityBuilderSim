# LandParcel.java - 298 lines · 37 methods · 3 constants · model

`ham/citybuildersim/LandParcel.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> One offer on the market, as the land office lists it: the next band of one lane of one side of the city, with what it holds - its area dry, fresh, sea and forest, and the sites and amounts of the seven resources - and its price.
> 
> WHAT IT IS FOR
> 
> Land used to be one fungible number bought a block at a time at a price that
> rose 2% per block. That is a slider, not a market: there was never a decision
> to make beyond "yes" or "later".
> 
> A listing is a decision. Forty offers stand at once, ten on each side of the
> city, all different sizes and differently priced, some wet, some with ore
> under them. Buying the cheapest ground and buying the one with the ore are
> different moves, and the player has to weigh them against a treasury.
> 
> A PLACE SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2). Until then
> an offer was a size, a price and some iron drawn by a generator seeded with
> its id, with no coordinates; the square footage joined the city's pool. Now
> it is real ground: the band of its lane from r1 to r2 plots from the
> founding site (CityLand), measured when it is listed, every field whose
> centre lies in it included whole: all its sites and all its tonnes, its
> price theirs at the in-ground price (from 0.7.58 to 0.7.63 each site whose
> own centre did, with its share of its field; whole again since 0.7.64).
> What the city's square feet grow by is
> its DRY ground (getSizeSqFt()); its water is owned too and priced lower.
> 
> An offer is IMMUTABLE once listed. What is listed stays listed at the price
> it was listed at: the city can save up for the big one without it drifting
> out of reach, and a reload cannot reroll it.
> 
> PRICED IN US DOLLARS (0.7.6). Jerus: "when you buy land, make it so that it
> costs USD not domestic currency". The seller is the world, as it always
> implicitly was, and the world is paid in its own money: the dollar price is
> what is fixed at listing, and what the treasury pays in local money is
> that price times the rate on the day it buys (localPrice()) - so a weak
> currency makes the same ground dear and a strong one cheap.

**Uses:** [CityLand](CityLand.md) (35), [LandManager](LandManager.md) (6), [Resource](Resource.md) (6)

**Used by (18):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [TreasuryCheck](TreasuryCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 210 | ITS RECORDS (DataSave's landOffers and landPurchases) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `LandParcel.MOSTLY_SEA` | `0.70` | The share of an offer's area that, when sea, tags it "mostly sea": 70% - listed, because sea is cheap, but never the best value (spec-land 2.2). |
| 215 | `LandParcel.OFFER_FIELDS` | `5 + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide an offer's record is: id, side, lane, r1, r2, its five areas, its sites, its amounts, its price and the month it was listed - 26. |
| 218 | `LandParcel.PURCHASE_FIELDS` | `7 + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide a purchase's record is: side, lane, r1, r2, the month, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 28. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `private final int id` |  |
| 42 | `private final int side, lane` |  |
| 43 | `private final double r1, r2` |  |
| 44 | `private final double[] km2` |  |
| 45 | `private final int[] sites` |  |
| 46 | `private final double[] amounts` |  |
| 52 | `private final double priceUsd` | What the city pays, in thousands of US DOLLARS since 0.7.6. |
| 55 | `private final int listedMonth` | The month it was listed in. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 260 | **type** `public final class LandParcel` | One offer on the market, as the land office lists it: the next band of one lane of one side of the city, with what it holds - its area dry, fresh, sea and forest, and the sites and amounts of the seven resources - and... |
| 67 | 16 | `public LandParcel(int id, int side, int lane, double r1, double r2, double[] km2, int[] sites, double[] amounts, double priceUs...` | An offer: its id, its side (0 north, 1 east, 2 south, 3 west) and lane (0 to 9, left to right facing out), the band's radii in plots, its five areas in square kilometres (CityLand.TOTAL, DRY, FRESH, SEA, FOREST), each... |
| 89 | 3 | `public LandParcel(int id, double sizeSqFt, double priceUsd, double ironTonnes, int deposits)` | Dry ground with no place - what a harness hands the office to price by hand: `sizeSqFt` of it, its iron as given, on the first lane of the north side at no radius. |
| 93 | 7 | `private static double[] dryOnly(double sqFt)` |  |
| 101 | 5 | `private static int[] ironOnly(int deposits)` |  |
| 107 | 5 | `private static double[] ironAmount(double tonnes)` |  |
| 113 | 1 | `public int getId()` |  |
| 116 | 1 | `public int getSide()` | Its side: 0 north, 1 east, 2 south, 3 west. |
| 119 | 1 | `public int getLane()` | Its lane on that side, 0 to 9, left to right facing out. |
| 122 | 1 | `public double getR1()` | Where its band starts, in plots from the founding site: its lane's frontier when it was listed. |
| 125 | 1 | `public double getR2()` | Where it ends: the lane's frontier once it is bought. |
| 128 | 1 | `public int getListedMonth()` | The month it was listed in. |
| 131 | 1 | `public double getKm2(int area)` | One of its areas, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA or FOREST. |
| 134 | 1 | `public double getKm2()` | Its whole area, in square kilometres. |
| 137 | 1 | `public double getDryKm2()` | Its dry ground, in square kilometres. |
| 140 | 1 | `public int getSites(Resource r)` | Its sites of a resource. |
| 143 | 1 | `public double getAmount(Resource r)` | Its amount of a resource, in the resource's unit. |
| 146 | 1 | `public double getSizeSqFt()` | What the city's ground grows by when it is bought: its dry ground, in square feet. |
| 149 | 1 | `public double getPriceUsd()` | The listed price, in thousands of US dollars. |
| 152 | 1 | `public double getIronTonnes()` | Its iron ore, in tonnes: the fields of iron whose centres lie in it. |
| 155 | 1 | `public int getDeposits()` | Its iron sites: how many more mines it lets the city stand. |
| 157 | 1 | `public boolean hasIron()` |  |
| 160 | 1 | `public boolean isMostlySea()` | Whether it is mostly sea: more than MOSTLY_SEA of its area. |
| 163 | 1 | `public String where()` | Its side and lane as the office names them: "North 3", the lane counted 1 to 10. |
| 169 | 1 | `public double localPrice(double rate)` | What the treasury pays for it in local money at this rate - local money per US dollar, ForeignAccounts.getRate() on the day it buys. |
| 177 | 4 | `public double getUsdPerSqFt()` | Its dollars a square foot of DRY ground (0.7.57): what the office ranks offers by. |
| 183 | 3 | `public double getDryKm2PerUsd()` | Its dry square kilometres a thousand US dollars: what the office's best value is the most of. |
| 188 | 3 | `public double getBlocks()` | How many city blocks' worth of dry ground, for a player who thinks in blocks. |
| 198 | 11 | `public String describe()` | A one-line label for the listing. |

### ITS RECORDS (DataSave's landOffers and landPurchases) (lines 210-298)

| line | len | member | says |
|---:|---:|---|---|
| 221 | 13 | `public double[] offerRow()` | Its record as an offer standing. |
| 236 | 9 | `public static LandParcel fromOfferRow(double[] row)` | An offer from its record; null when the record is the wrong width. |
| 247 | 15 | `double[] purchaseRow(int month, double paidLocal)` | Its record as a purchase, the month it was bought in and what the treasury paid for it in local money. |
| 264 | 9 | `static LandParcel fromPurchaseRow(double[] row)` | The offer a purchase's record bought; null when the record is the wrong width. |
| 275 | 1 | `static int purchaseMonth(double[] row)` | The month a purchase's record was bought in. |
| 278 | 1 | `static double purchasePaid(double[] row)` | What the treasury paid for it, in local money. |
| 280 | 6 | `private int contents(double[] row, int i)` |  |
| 287 | 6 | `private static int read(double[] row, int i, double[] km2, int[] sites, double[] amounts)` |  |
| 295 | 3 | `public boolean same(LandParcel o)` | Whether two offers are the same, field for field. |

