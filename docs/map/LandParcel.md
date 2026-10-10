# LandParcel.java - 338 lines · 45 methods · 4 constants · model

`ham/citybuildersim/LandParcel.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, with what its ground holds - its area dry, fresh, sea and forest, and the sites and amounts of the seven resources - and its price.
> 
> WHAT IT IS FOR
> 
> Land used to be one fungible number bought a block at a time at a price that
> rose 2% per block. That is a slider, not a market: there was never a decision
> to make beyond "yes" or "later".
> 
> A listing is a decision. Up to twenty-four offers stand at once, six on
> each side of the city, holding different ground at different prices, some
> wet, some with ore under them. Buying the cheapest ground and buying the
> one with the ore are different moves, and the player has to weigh them
> against a treasury.
> 
> A PLACE SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2). Until then
> an offer was a size, a price and some iron drawn by a generator seeded with
> its id, with no coordinates. From 0.7.57 to 0.7.66 it was the band of one
> of forty lanes of wedges from r1 to r2 plots out, its ground sampled.
> 
> WHOLE BLOCKS SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.2). It is
> a rectangle of the block grid (LandGrid, GridOffers): its side, its place on
> that side (0 to 5, left to right facing out), the level of its blocks and
> its plots [x0, x1) x [y0, y1). Its ground is the rectangle's plots the city
> does not own yet, counted plot by plot when it is listed (CityLand.groundOf():
> the books follow the map, exact at every size), and it holds every field
> whose centre plot lies on that ground, whole (spec-land star 12), and the
> sites there of a field the city holds only part of. What the city's square
> feet grow by is its DRY ground (getSizeSqFt()); its water is owned too and
> priced lower.
> 
> An offer is IMMUTABLE once listed. What is listed stays listed at the price
> it was listed at: the city can save up for the big one without it drifting
> out of reach, and a reload cannot reroll it. Its ground cannot change while
> it stands either: standing offers never meet, and a purchase claims only its
> own rectangle's plots.
> 
> PRICED IN US DOLLARS (0.7.6). Jerus: "when you buy land, make it so that it
> costs USD not domestic currency". The seller is the world, as it always
> implicitly was, and the world is paid in its own money: the dollar price is
> what is fixed at listing, and what the treasury pays in local money is
> that price times the rate on the day it buys (localPrice()) - so a weak
> currency makes the same ground dear and a strong one cheap.

**Uses:** [CityLand](CityLand.md) (35), [LandManager](LandManager.md) (6), [Resource](Resource.md) (6), [GridOffers](GridOffers.md) (3)

**Used by (24):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildScreen](BuildScreen.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [ConversionCheck](ConversionCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [PortCheck](PortCheck.md), [ScaleCheck](ScaleCheck.md), [TreasuryCheck](TreasuryCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 243 | ITS RECORDS (DataSave's landOffers and landHoldings, SAVE_FORMAT 32) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `LandParcel.MOSTLY_SEA` | `0.70` | The share of an offer's area that, when sea, tags it "mostly sea": 70% - listed, because sea is cheap, but never the best value (spec-land 2.2). |
| 248 | `LandParcel.RECT_FIELDS` | `7` | Where a record keeps the rectangle: side, place, level, x0, y0, x1, y1 - seven. |
| 251 | `LandParcel.OFFER_FIELDS` | `1 + RECT_FIELDS + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide an offer's record is: its id, its rectangle (side, place, level, x0, y0, x1, y1), its five areas, its sites, its amounts, its price and the month it was listed - 29. |
| 254 | `LandParcel.PURCHASE_FIELDS` | `RECT_FIELDS + 3 + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide a holding's record is: its rectangle, the month bought, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 31 (spec-grid 3, M3). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `private final int id` |  |
| 50 | `private final int side, place, level` |  |
| 51 | `private final long x0, y0, x1, y1` |  |
| 52 | `private final double[] km2` |  |
| 53 | `private final int[] sites` |  |
| 54 | `private final double[] amounts` |  |
| 60 | `private final double priceUsd` | What the city pays, in thousands of US DOLLARS since 0.7.6. |
| 63 | `private final int listedMonth` | The month it was listed in. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 292 | **type** `public final class LandParcel` | One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, with what its ground holds - its area dry, fresh, sea and forest, and t... |
| 76 | 19 | `public LandParcel(int id, int side, int place, int level, long x0, long y0, long x1, long y1, double[] km2, int[] sites, double...` | An offer: its id, its side (0 north, 1 east, 2 south, 3 west), its place on that side (0 to 5, left to right facing out), the level of its blocks and its rectangle of plots [x0, x1) x [y0, y1), its five areas in squar... |
| 101 | 3 | `public LandParcel(int id, double sizeSqFt, double priceUsd, double ironTonnes, int deposits)` | Dry ground with no place - what a harness hands the office to price by hand: `sizeSqFt` of it, its iron as given, in North's first place on no ground of the grid (an empty rectangle). |
| 105 | 7 | `private static double[] dryOnly(double sqFt)` |  |
| 113 | 5 | `private static int[] ironOnly(int deposits)` |  |
| 119 | 5 | `private static double[] ironAmount(double tonnes)` |  |
| 125 | 1 | `public int getId()` |  |
| 128 | 1 | `public int getSide()` | Its side: 0 north, 1 east, 2 south, 3 west. |
| 131 | 1 | `public int getPlace()` | Its place on that side, 0 to 5, left to right facing out (GridOffers.PLACES). |
| 134 | 1 | `public int getLevel()` | The level of its blocks: 2^level plots a side (LandGrid). |
| 137 | 1 | `public long getX0()` | Its rectangle of plots, half-open: its west edge... |
| 140 | 1 | `public long getY0()` | ...its north edge... |
| 143 | 1 | `public long getX1()` | ...one past its east edge... |
| 146 | 1 | `public long getY1()` | ...and one past its south edge. |
| 149 | 1 | `public boolean contains(long x, long y)` | Whether its rectangle holds plot (x, y). |
| 152 | 1 | `public long blocksAcross()` | Its blocks across its side. |
| 155 | 1 | `public long blocksDeep()` | ...and deep, outward. |
| 158 | 1 | `GridOffers.Rect rect()` | Its rectangle as GridOffers stands it. |
| 161 | 1 | `public int getListedMonth()` | The month it was listed in. |
| 164 | 1 | `public double getKm2(int area)` | One of its areas, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA or FOREST. |
| 167 | 1 | `public double getKm2()` | Its whole area, in square kilometres. |
| 170 | 1 | `public double getDryKm2()` | Its dry ground, in square kilometres. |
| 173 | 1 | `public int getSites(Resource r)` | Its sites of a resource. |
| 176 | 1 | `public double getAmount(Resource r)` | Its amount of a resource, in the resource's unit. |
| 179 | 1 | `public double getSizeSqFt()` | What the city's ground grows by when it is bought: its dry ground, in square feet. |
| 182 | 1 | `public double getPriceUsd()` | The listed price, in thousands of US dollars. |
| 185 | 1 | `public double getIronTonnes()` | Its iron ore, in tonnes: the fields of iron whose centres lie on its ground. |
| 188 | 1 | `public int getDeposits()` | Its iron sites: how many more mines it lets the city stand. |
| 190 | 1 | `public boolean hasIron()` |  |
| 193 | 1 | `public boolean isMostlySea()` | Whether it is mostly sea: more than MOSTLY_SEA of its area. |
| 196 | 1 | `public String where()` | Its side and place as the office names them: "North 3", the place counted 1 to 6. |
| 202 | 1 | `public double localPrice(double rate)` | What the treasury pays for it in local money at this rate - local money per US dollar, ForeignAccounts.getRate() on the day it buys. |
| 210 | 4 | `public double getUsdPerSqFt()` | Its dollars a square foot of DRY ground (0.7.57): what the office ranks offers by. |
| 216 | 3 | `public double getDryKm2PerUsd()` | Its dry square kilometres a thousand US dollars: what the office's best value is the most of. |
| 221 | 3 | `public double getBlocks()` | How many city blocks' worth of dry ground, for a player who thinks in blocks. |
| 231 | 11 | `public String describe()` | A one-line label for the listing. |

### ITS RECORDS (DataSave's landOffers and landHoldings, SAVE_FORMAT 32) (lines 243-338)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 10 | `public double[] offerRow()` | Its record as an offer standing. |
| 269 | 9 | `public static LandParcel fromOfferRow(double[] row)` | An offer from its record; null when the record is the wrong width. |
| 280 | 11 | `double[] purchaseRow(int month, double paidLocal)` | Its record as a holding: the month it was bought in and what the treasury paid for it in local money. |
| 293 | 9 | `static LandParcel fromPurchaseRow(double[] row)` | The offer a holding's record bought; null when the record is the wrong width. |
| 304 | 1 | `static int purchaseMonth(double[] row)` | The month a holding's record was bought in. |
| 307 | 1 | `static double purchasePaid(double[] row)` | What the treasury paid for it, in local money. |
| 309 | 10 | `private int rectInto(double[] row, int i)` |  |
| 320 | 6 | `private int contents(double[] row, int i)` |  |
| 327 | 6 | `private static int read(double[] row, int i, double[] km2, int[] sites, double[] amounts)` |  |
| 335 | 3 | `public boolean same(LandParcel o)` | Whether two offers are the same, field for field. |

