# LandParcel.java - 390 lines · 52 methods · 4 constants · model

`ham/citybuildersim/LandParcel.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [CityLand](CityLand.md) (39), [LandManager](LandManager.md) (6), [Resource](Resource.md) (6), [GridOffers](GridOffers.md) (3)

**Used by (33):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [ConversionCheck](ConversionCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [GlobeFrame](GlobeFrame.md), [IslandNetCheck](IslandNetCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [OilCheck](OilCheck.md), [PortCheck](PortCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [TreasuryCheck](TreasuryCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 264 | ITS RECORDS (DataSave's landOffers and landHoldings, SAVE_FORMAT 32) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 68 | `LandParcel.MOSTLY_SEA` | `0.70` | The share of an offer's area that, when sea, tags it "mostly sea": 70% - listed, because sea is cheap, but never the best value (spec-land 2.2). |
| 276 | `LandParcel.RECT_FIELDS` | `7` | Where a record keeps the rectangle: side, place, level, x0, y0, x1, y1 - seven. |
| 279 | `LandParcel.OFFER_FIELDS` | `1 + RECT_FIELDS + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide an offer's record is: its id, its rectangle (side, place, level, x0, y0, x1, y1), its five areas, its sites, its amounts, its price and the month it was listed - 29. |
| 282 | `LandParcel.PURCHASE_FIELDS` | `RECT_FIELDS + 3 + CityLand.AREAS + 2 * CityLand.KINDS + 2` | How wide a holding's record is: its rectangle, the month bought, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 31 (spec-grid 3, M3). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 49 | `private final int id` |  |
| 50 | `private final int side, place, level` |  |
| 51 | `private final long x0, y0, x1, y1` |  |
| 52 | `private final double[] km2` |  |
| 54 | `private final double[] relief` | Its dry ground by relief and its shallow sea (CityLand.RELIEF_FLAT to RELIEF_SHALLOW), km2: flat all of its dry ground where the listing was not counted by relief (ET1). |
| 55 | `private final int[] sites` |  |
| 56 | `private final double[] amounts` |  |
| 62 | `private final double priceUsd` | What the city pays, in thousands of US DOLLARS since 0.7.6. |
| 65 | `private final int listedMonth` | The month it was listed in. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 47 | 344 | **type** `public final class LandParcel` | One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, with what its ground holds - its area dry, fresh, sea and forest, and t... |
| 78 | 4 | `public LandParcel(int id, int side, int place, int level, long x0, long y0, long x1, long y1, double[] km2, int[] sites, double...` | An offer: its id, its side (0 north, 1 east, 2 south, 3 west), its place on that side (0 to 5, left to right facing out), the level of its blocks and its rectangle of plots [x0, x1) x [y0, y1), its five areas in squar... |
| 89 | 21 | `public LandParcel(int id, int side, int place, int level, long x0, long y0, long x1, long y1, double[] km2, double[] relief, in...` | ...with its dry ground by relief and its shallow sea (ET1, spec-earth2-terrain 3.3): CityLand.RELIEF_AREAS figures in square kilometres, the four reliefs adding up to its DRY area. |
| 116 | 3 | `public LandParcel(int id, double sizeSqFt, double priceUsd, double ironTonnes, int deposits)` | Dry ground with no place - what a harness hands the office to price by hand: `sizeSqFt` of it, its iron as given, in North's first place on no ground of the grid (an empty rectangle). |
| 120 | 7 | `private static double[] dryOnly(double sqFt)` |  |
| 128 | 5 | `private static int[] ironOnly(int deposits)` |  |
| 134 | 5 | `private static double[] ironAmount(double tonnes)` |  |
| 140 | 1 | `public int getId()` |  |
| 143 | 1 | `public int getSide()` | Its side: 0 north, 1 east, 2 south, 3 west. |
| 146 | 1 | `public int getPlace()` | Its place on that side, 0 to 5, left to right facing out (GridOffers.PLACES). |
| 149 | 1 | `public int getLevel()` | The level of its blocks: 2^level plots a side (LandGrid). |
| 152 | 1 | `public long getX0()` | Its rectangle of plots, half-open: its west edge... |
| 155 | 1 | `public long getY0()` | ...its north edge... |
| 158 | 1 | `public long getX1()` | ...one past its east edge... |
| 161 | 1 | `public long getY1()` | ...and one past its south edge. |
| 164 | 1 | `public boolean contains(long x, long y)` | Whether its rectangle holds plot (x, y). |
| 167 | 1 | `public long blocksAcross()` | Its blocks across its side. |
| 170 | 1 | `public long blocksDeep()` | ...and deep, outward. |
| 173 | 1 | `GridOffers.Rect rect()` | Its rectangle as GridOffers stands it. |
| 176 | 1 | `public int getListedMonth()` | The month it was listed in. |
| 179 | 1 | `public double getKm2(int area)` | One of its areas, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA or FOREST. |
| 182 | 1 | `public double getKm2()` | Its whole area, in square kilometres. |
| 185 | 1 | `public double getReliefKm2(int relief)` | Its dry ground by relief, or its shallow sea, in square kilometres (CityLand.RELIEF_FLAT, RELIEF_FOOTHILL, RELIEF_STEEP, RELIEF_VERY_STEEP; RELIEF_SHALLOW): the four reliefs add up to getDryKm2(). |
| 188 | 1 | `public double[] reliefRow()` | Its relief as a record (DataSave's landOffersRelief and landHoldingsRelief): CityLand.RELIEF_AREAS figures, a copy. |
| 191 | 1 | `public double getDryKm2()` | Its dry ground, in square kilometres. |
| 194 | 1 | `public int getSites(Resource r)` | Its sites of a resource. |
| 197 | 1 | `public double getAmount(Resource r)` | Its amount of a resource, in the resource's unit. |
| 200 | 1 | `public double getSizeSqFt()` | What the city's ground grows by when it is bought: its dry ground, in square feet. |
| 203 | 1 | `public double getPriceUsd()` | The listed price, in thousands of US dollars. |
| 206 | 1 | `public double getIronTonnes()` | Its iron ore, in tonnes: the fields of iron whose centres lie on its ground. |
| 209 | 1 | `public int getDeposits()` | Its iron sites: how many more mines it lets the city stand. |
| 211 | 1 | `public boolean hasIron()` |  |
| 214 | 1 | `public boolean isMostlySea()` | Whether it is mostly sea: more than MOSTLY_SEA of its area. |
| 217 | 1 | `public String where()` | Its side and place as the office names them: "North 3", the place counted 1 to 6. |
| 223 | 1 | `public double localPrice(double rate)` | What the treasury pays for it in local money at this rate - local money per US dollar, ForeignAccounts.getRate() on the day it buys. |
| 231 | 4 | `public double getUsdPerSqFt()` | Its dollars a square foot of DRY ground (0.7.57): what the office ranks offers by. |
| 237 | 3 | `public double getDryKm2PerUsd()` | Its dry square kilometres a thousand US dollars: what the office's best value is the most of. |
| 242 | 3 | `public double getBlocks()` | How many city blocks' worth of dry ground, for a player who thinks in blocks. |
| 252 | 11 | `public String describe()` | A one-line label for the listing. |

### ITS RECORDS (DataSave's landOffers and landHoldings, SAVE_FORMAT 32) (lines 264-390)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 10 | `public double[] offerRow()` | Its record as an offer standing. |
| 297 | 3 | `public static LandParcel fromOfferRow(double[] row)` | An offer from its record; null when the record is the wrong width. |
| 302 | 9 | `public static LandParcel fromOfferRow(double[] row, double[] relief)` | ...with its ground by relief (reliefRow(), landOffersRelief's row for it): all flat when there is none, or it is the wrong width (ET1). |
| 313 | 11 | `double[] purchaseRow(int month, double paidLocal)` | Its record as a holding: the month it was bought in and what the treasury paid for it in local money. |
| 326 | 3 | `static LandParcel fromPurchaseRow(double[] row)` | The offer a holding's record bought; null when the record is the wrong width. |
| 331 | 9 | `static LandParcel fromPurchaseRow(double[] row, double[] relief)` | ...with its ground by relief (reliefRow(), landHoldingsRelief's row for it): all flat when there is none, or it is the wrong width (ET1). |
| 342 | 1 | `static int purchaseMonth(double[] row)` | The month a holding's record was bought in. |
| 345 | 1 | `static double purchasePaid(double[] row)` | What the treasury paid for it, in local money. |
| 348 | 5 | `static double[] keptRelief(double[] row)` | A relief record as it reads: a copy of `row` when it is CityLand.RELIEF_AREAS figures, none of them negative or not a number, else null. |
| 355 | 5 | `static double[] flatRelief(double dryKm2)` | The relief of ground that is all flat: its dry area flat, nothing else (what a record with no relief reads as). |
| 361 | 10 | `private int rectInto(double[] row, int i)` |  |
| 372 | 6 | `private int contents(double[] row, int i)` |  |
| 379 | 6 | `private static int read(double[] row, int i, double[] km2, int[] sites, double[] amounts)` |  |
| 387 | 3 | `public boolean same(LandParcel o)` | Whether two offers are the same, field for field: its record and its ground by relief (ET1). |

