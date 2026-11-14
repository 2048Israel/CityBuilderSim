# LandNationsCheck.java - 1,436 lines · 54 methods · 8 constants · harnesses

`ham/citybuildersim/LandNationsCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> Land bought from the nation it lies in, checked (WN5; docs/studies/spec-nations.md 6.2-6.4, D21, D22): the land table, each nation's land factor and
> how it moves, the seller of every plot of a real-Earth world and the territorial sea, an offer's ground split by its sellers, the price each part
> is struck at, the payment in each seller's money, the save, how often it fires in a real run, and the words the land office writes of it.
> 
> WHY THIS EXISTS. Land was priced by one formula for the whole world and bought from nobody; WN5 made a plot at Windsor on the Detroit bank dearer
> than the one across the river, and paid each part to its own nation in its own money. Three slices built it (the table and the two models, the
> office and the purse, this) and each could be right and the whole wrong: a factor that is 1.0 for Canada and still moves it, a seller read at a
> plot's centre and a split that counts another ground, a price that is today's to the bit in Canada and a cent off in a city of its own, a purse
> that takes the dollars from the largest holding and not the seller's. So this holds each against fixtures that CAUSE the condition (a city founded
> on the river, one in Canada's reference, one at the Strait, a generated world) and against the model's own constants, never a figure typed; and
> where the model's rule is a search (the 12 nautical miles) the answer is a brute-force search over the data.
> 
> WHAT IT HAS TO PROVE:
> 
>   1. THE TABLE. land-2024.json has a line for every nation of the world file; the 25 rows of tools/world/land.tsv read their published figure
>      (a price an acre times acres a hectare, then the tsv's own rate to US$) with mark 3; every other line is the fit's, mark 1, and no dearer
>      than the dearest published; the fit's elasticity is positive.
>   2. CANADA IS 1. level() and factorAt() of the reference are exactly 1.0 in months 0, 1, 600 and 1,488 on a bare table and on both paths; a number
>      outside 1..N is 1.0.
>   3. THE FOUNDING, AND HISTORICAL'S 2024. factorAt(n, 0) is level(n) for every nation; on the Historical path in January 2024 it is the published
>      ratio to Canada's, for every nation whose ratio lies inside the floor and ceiling (the round trip the founding level was built for).
>   4. RANDOM MOVES IT, WITHIN ITS BOUNDS. On a Random path the factors differ from Historical's and between two seeds, one seed gives them twice,
>      and every factor stays between FACTOR_FLOOR and FACTOR_CEILING in every January and July from 1900 to 2100, on all three paths.
>   5. THE SELLERS. The host at Windsor, Halifax and the Strait is the world market's host; the plots either side of the Detroit River sell through
>      Canada and the United States; an area, no one and a number past the file are the host's; and the territorial sea: a walk off the Moroccan coast
>      reads Morocco within TERRITORIAL_KM of its land and the host beyond, every plot as a brute-force search over the landmass layer says.
>   6. THE SPLIT. An offer across the river keeps parts whose areas add up to its own and whose dollars add up to its price; split() of a rectangle
>      across it counts, class by class, what CityLand counts whole; a block of Canadian ground away from the river is one part and reads no plot.
>   7. THE PRICE. Halifax's offers, every offer wholly Canadian at Windsor, and every offer of a generated world are priced by the one-part formula to
>      the bit; an offer wholly the United States' is dearer than that by its factor, to a step of rounding.
>   8. PAYING. A split offer bought from the vault takes Canada's part from the C$ holding and the United States' from the US$, nothing else; converted,
>      the treasury pays usd x rate; the receipt names the sellers; the tally keeps km2 and dollars by nation; a Canadian offer and a generated world's
>      name no seller.
>   9. THE SAVE. The Windsor city after its purchases, saved and loaded in another folder, has the same offers (sellers and prices) and the same
>      tally; a save without the three keys loads with no seller named and nothing bought; a generated city's save carries none of them.
>  10. HOW OFTEN IT FIRES. The Windsor city played 120 months, buying the offers that name a foreign seller and the cheapest: how many offers were
>      listed split, wholly foreign and wholly the host's, and how many purchases named a seller; the mechanic fires.
>  11. THE WORDS. LandSellersView on the Windsor offers and on hand-made ones: the split of 62 and 38, three sellers whose rounding must still add to
>      100, a host's own offer saying nothing, a wholly foreign offer naming its seller, and (WN5b) a split whose shares round differently on their
>      own (three equal thirds, 50.5 and 49.5) reading the same percents on the card and on the receipt (LandSellersView.receiptLine()).
>  12. THE HOVER'S QUOTE AWAY FROM THE SCREEN'S THREAD (WN5b). Blocks straddling the Detroit River and the Strait are quoted by two sellers or more;
>      the cost of a cold quote by level is measured and printed (what moved the hover's quote off the screen's thread); a LandMarket.Quoter made
>      here and used on another thread gives the office's quote to the bit, block by block; it is the office as it stood when it was made, a purchase
>      since not seen by it; making one takes no more than a frame; and the map's source quotes through one on its own thread.
>  13. PICKED GROUND (WN5c). A click by level and monthly pricing of 1, 10 and 50 picks are measured at the river and coast.
>      Worker results never change the live office before acceptance; a month that passes during pricing still gets today's
>      price, to the bit. Retained splits read no seller plots in the month. Changed ground and cancelled requests are rejected.
> 
> Every premise is asserted before what rests on it. Without the world file (-Dcbs.world) nothing here runs; without the Earth's data (-Dcbs.earth)
> sections 1 to 4 run and 5 to 13 say so and are skipped. Sections 12 and 13 hold frame budgets through Timing.within (TimingCheck owns the mechanism).

**Uses:** [LandParcel](LandParcel.md) (116), [LandFactors](LandFactors.md) (33), [WorldData](WorldData.md) (26), [LandMarket](LandMarket.md) (23), [LandSellers](LandSellers.md) (22), [Nations](Nations.md) (21), [LandSellersView](LandSellersView.md) (18), [Game](Game.md) (12), [Founding](Founding.md) (11), [CityLand](CityLand.md) (11), [World](World.md) (11), [LandMap](LandMap.md) (9), [GameFiles](GameFiles.md) (8), [CityCalendar](CityCalendar.md) (6), [Resource](Resource.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [Timing](Timing.md) (4), [EarthData](EarthData.md) (3), [EarthSite](EarthSite.md) (2), [EarthProj](EarthProj.md) (2), [LandTable](LandTable.md) (1), [LandManager](LandManager.md) (1), [Landmasses](Landmasses.md) (1)

**Used by (1):** [PartnersCheck](PartnersCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | THE FIXTURES |
| 193 | MAIN |
| 251 | 1. THE TABLE |
| 317 | 2. CANADA IS 1 |
| 348 | 3. THE FOUNDING, AND HISTORICAL'S 2024 |
| 392 | 4. RANDOM MOVES IT, WITHIN ITS BOUNDS |
| 439 | 5. THE SELLERS |
| 589 | 6. THE SPLIT |
| 673 | 7. THE PRICE |
| 719 | 8. PAYING |
| 822 | 9. THE SAVE |
| 897 | 10. HOW OFTEN IT FIRES IN A REAL RUN |
| 964 | 11. THE WORDS |
| 1091 | 12. THE HOVER'S QUOTE AWAY FROM THE SCREEN'S THREAD |
| 1269 | 13. PICKED GROUND MEASURED AWAY, PRICED IN THE CURRENT MONTH |

## Enum constants

| line | constant | says |
|---:|---|---|
| 955 | `LandNationsCheck.Kind.HOME` |  |
| 955 | `LandNationsCheck.Kind.SPLIT` |  |
| 955 | `LandNationsCheck.Kind.FOREIGN` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `LandNationsCheck.out` | `System.out` |  |
| 86 | `LandNationsCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 89 | `LandNationsCheck.SEED_A` | `20261101L, SEED_B = 20261102L` | The seeds the Random paths are drawn from. |
| 96 | `LandNationsCheck.WINDSOR_LAT` | `42.315, WINDSOR_LON = - 83.04, DETROIT_LAT = 42.335, DETROIT_LON = - 83.045` | Where the cities stand: Windsor on the Detroit River (Canada), Detroit across it (the United States), Halifax (Canada, the reference), the Strait of Gibraltar. |
| 97 | `LandNationsCheck.HALIFAX_LAT` | `44.64, HALIFAX_LON = - 63.57, GIBRALTAR_LAT = 36.14, GIBRALTAR_LON = - 5.35` |  |
| 100 | `LandNationsCheck.ACRES_PER_HECTARE` | `ham.citybuildersim.tools.LandTable.ACRES_PER_HECTARE` | A hectare in acres: the unit the USDA and Statistics Canada give their prices in (tools.LandTable converts by the same figure). |
| 902 | `LandNationsCheck.RUN_YEARS` | `10` | How many years the run plays, a look and some purchases at the start of each. |
| 1096 | `LandNationsCheck.RIVER_LAT` | `42.32, RIVER_LON = - 83.04, COAST_LAT = 35.95, COAST_LON = - 5.45` | Where the hover is held: a spot on the Detroit River at Windsor, and one off the coast at the Strait of Gibraltar. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `static int fails` |  |
| 138 | `final Game g` |  |
| 139 | `final GameFiles files` |  |
| 476 | `int salt, kept, given, wrong` |  |
| 477 | `boolean givenOwnedByMorocco` |  |
| 478 | `final StringBuilder bad` |  |
| 479 | `double lastKeptKm` |  |
| 480 | `boolean givenThenKept` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 1355 | **type** `public class LandNationsCheck` | Land bought from the nation it lies in, checked (WN5; docs/studies/spec-nations.md 6.2-6.4, D21, D22): the land table, each nation's land factor and how it moves, the seller of every plot of a real-Earth world and the... |
| 102 | 4 | `static void check(String label, boolean ok)` |  |
| 107 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 112 | 5 | `static void quietly(Runnable r)` |  |
| 118 | 5 | `static<T> T quietlyGet(Supplier<T> s)` |  |
| 125 | 3 | `static boolean near(double a, double b, double rel)` | Two figures within a relative gap (and equal when both are 0). |
| 130 | 1 | `static int januaryOf(int year)` | The game month of January of a year. |

### THE FIXTURES (lines 132-192)

| line | len | member | says |
|---:|---:|---|---|
| 137 | 31 | **type** `static final class City` | A city on a fixture world: its game and its folder. |
| 141 | 8 | `City(String label, Founding founding)` _(in LandNationsCheck.City)_ |  |
| 151 | 8 | `City(String label, City played) throws IOException` _(in LandNationsCheck.City)_ | Copy a played fixture through slot 10 in two scratch folders; neither folder is a player's save folder. |
| 160 | 1 | `LandSellers sellers()` _(in LandNationsCheck.City)_ |  |
| 162 | 1 | `LandMarket office()` _(in LandNationsCheck.City)_ |  |
| 164 | 1 | `List<LandParcel> offers()` _(in LandNationsCheck.City)_ |  |
| 166 | 1 | `void months(int n)` _(in LandNationsCheck.City)_ |  |
| 170 | 14 | **type** `record Founded(List<LandParcel> offers, double groundUsd, double usLevel)` | A city as it stood when founded: its offers and the numbers they were priced at (the office's, which move with the month). |
| 171 | 3 | `static Founded of(City c)` _(in LandNationsCheck.Founded)_ |  |
| 176 | 7 | `double formula(LandParcel p)` _(in LandNationsCheck.Founded)_ | The one-part dollar formula on an offer's own ground, at the numbers it was listed by. |
| 185 | 1 | `static Founding onEarth(double lat, double lon)` |  |
| 187 | 5 | `static long[] plotOf(World w, double lat, double lon)` |  |

### MAIN (lines 193-250)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 47 | `public static void main(String[] args) throws Exception` |  |
| 245 | 5 | `static void finish(long started)` |  |

### 1. THE TABLE (lines 251-316)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 61 | `static void theTable(WorldData file, LandFactors f) throws IOException` |  |

### 2. CANADA IS 1 (lines 317-347)

| line | len | member | says |
|---:|---:|---|---|
| 321 | 26 | `static void canadaIsOne(WorldData file, Nations hist, Nations rand)` |  |

### 3. THE FOUNDING, AND HISTORICAL'S 2024 (lines 348-391)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 39 | `static void theFounding(WorldData file, Nations hist, Nations rand)` |  |

### 4. RANDOM MOVES IT, WITHIN ITS BOUNDS (lines 392-438)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 42 | `static void randomMovesIt(WorldData file, Nations hist, Nations randA, Nations randB)` |  |

### 5. THE SELLERS (lines 439-588)

| line | len | member | says |
|---:|---:|---|---|
| 443 | 30 | `static void theSellers(WorldData file, City windsor, City halifax, City strait)` |  |
| 475 | 7 | **type** `static final class Walk` | What a walk of plots found: how many sea plots it read, how many of a foreign nation's sea were kept and how many given to the host, and the plots that disagreed with the search. |
| 483 | 17 | `static void territorialSea(WorldData file, City strait)` |  |
| 506 | 50 | `static Walk walk(WorldData file, LandSellers sellers, World w, double lat0, double lon0, double lat1, double lon1, int samples)` | Walks `samples` points from (lat0, lon0) to (lat1, lon1), takes the plot under each and checks its seller against the rule written out the slow way: the owner at the plot's blend point sells it; a sea plot of a foreig... |
| 561 | 21 | `static double nearestLandKm(WorldData file, EarthData data, LandSellers sellers, double lat, double lon, int seller)` | The least great-circle distance, in kilometres, from the centre of the 15" cell holding a point to the centre of a land cell (the landmass layer's) whose owner sells through `seller`, looking at every cell of a box th... |
| 583 | 5 | `static double greatCircleKm(double radius, double lat1, double lon1, double lat2, double lon2)` |  |

### 6. THE SPLIT (lines 589-672)

| line | len | member | says |
|---:|---:|---|---|
| 593 | 73 | `static void theSplit(WorldData file, City windsor)` |  |
| 667 | 5 | `static long[] sum(List<LandSellers.Part> parts)` |  |

### 7. THE PRICE (lines 673-718)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 4 | `static LandParcel detroitQuote(City windsor)` | A block of the United States' ground in Detroit, quoted by the Windsor office: a draft of it, nothing kept. |
| 683 | 35 | `static void thePrice(WorldData file, Founded windsorF, City windsor, Founded halifaxF, City halifax, Founded generatedF, City g...` |  |

### 8. PAYING (lines 719-821)

| line | len | member | says |
|---:|---:|---|---|
| 723 | 93 | `static void paying(WorldData file, City windsor, City halifax, City generated)` |  |
| 817 | 4 | `static LandParcel.Sale part(LandParcel p, int seller)` |  |

### 9. THE SAVE (lines 822-896)

| line | len | member | says |
|---:|---:|---|---|
| 826 | 55 | `static void theSave(WorldData file, City windsor) throws IOException` |  |
| 883 | 13 | `static Game loadCopy(GameFiles from, String text, String label) throws IOException` | A save's text written to slot 1 of a folder of its own, beside whatever else the city's saves folder holds, and loaded into a fresh game. |

### 10. HOW OFTEN IT FIRES IN A REAL RUN (lines 897-963)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 50 | `static void howOften(WorldData file)` |  |
| 955 | 1 | **type** `enum Kind` |  |
| 957 | 6 | `static Kind kind(LandParcel p, int host)` |  |

### 11. THE WORDS (lines 964-1090)

| line | len | member | says |
|---:|---:|---|---|
| 968 | 114 | `static void theWords(WorldData file, City windsor, Founded founded, LandParcel detroit)` |  |
| 1084 | 6 | `private static int[] percentsIn(Pattern percent, String line)` | The whole percents a line shows, in order: the digits before each "%". |

### 12. THE HOVER'S QUOTE AWAY FROM THE SCREEN'S THREAD (lines 1091-1268)

| line | len | member | says |
|---:|---:|---|---|
| 1099 | 4 | `static long[] blockAround(World w, double lat, double lon, int level)` | The block of `level` holding a point, as the map's hover cuts it (LandMap.blockAt(), blockRect()): {x0, y0, x1, y1}. |
| 1104 | 1 | `static LandParcel quoteOf(LandMarket office, long[] r)` |  |
| 1106 | 1 | `static LandParcel quoteOf(LandMarket.Quoter quoter, long[] r)` |  |
| 1109 | 1 | `static boolean bits(double a, double b)` | Two figures the same to the bit. |
| 1112 | 14 | `static boolean sameQuote(LandParcel a, LandParcel b, int[] parts)` | Whether two quotes of one block are the same to the bit: the price, the area of every kind, and the sellers one by one (seller, area, dollars). |
| 1128 | 1 | **type** `record Cost(int level, double km, double ms, long plots, int sellers)` | What one cold quote of a block cost: its level, its side in km, its milliseconds, the plots the sellers read for it, and the sellers it named. |
| 1131 | 3 | `static String costLine(Cost c)` | A cost as a line of the table: level L (km across): N ms, P plots read, S sellers. |
| 1136 | 9 | `static Cost costOf(City c, double lat, double lon, int level)` | The block of `level` round a point quoted once by the city's office, timed, with the plots LandSellers read beside it. |
| 1147 | 16 | `static List<LandParcel> quotedElsewhere(LandMarket.Quoter quoter, List<long[]> blocks) throws Exception` | The quotes of blocks asked of a quoter on a thread of its own, in the order given; the thread is stopped after. |
| 1164 | 105 | `static void theHover(WorldData file, City windsor, City strait) throws Exception` |  |

### 13. PICKED GROUND MEASURED AWAY, PRICED IN THE CURRENT MONTH (lines 1269-1436)

| line | len | member | says |
|---:|---:|---|---|
| 1274 | 10 | `static List<long[]> pickedRects(City c, double lat, double lon, int level, int count)` | Individual rectangles supplied directly so the 50-pick case cannot collapse into one rectangle. |
| 1285 | 5 | `static void reprice(City c, int month)` |  |
| 1291 | 5 | `static void frameTime(String label, double ms)` |  |
| 1297 | 6 | `static boolean sameDraft(LandParcel a, LandParcel b)` |  |
| 1304 | 96 | `static void thePicks(City windsor, City strait) throws Exception` |  |
| 1402 | 34 | `static void picksLifecycle(City c, ExecutorService worker, String where) throws Exception` | Exercise the screen's Picks path with a held worker queue and an owner-only completion queue. |

