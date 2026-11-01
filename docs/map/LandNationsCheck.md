# LandNationsCheck.java - 1,027 lines · 37 methods · 7 constants · harnesses

`ham/citybuildersim/LandNationsCheck.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

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
>      100, a host's own offer saying nothing, a wholly foreign offer naming its seller.
> 
> Every premise is asserted before what rests on it. Without the world file (-Dcbs.world) nothing here runs; without the Earth's data (-Dcbs.earth)
> sections 1 to 4 run and 5 to 11 say so and are skipped. No timing is asserted (TimingCheck owns them).

**Uses:** [LandParcel](LandParcel.md) (81), [LandFactors](LandFactors.md) (33), [WorldData](WorldData.md) (25), [LandSellers](LandSellers.md) (21), [Nations](Nations.md) (21), [LandSellersView](LandSellersView.md) (15), [Game](Game.md) (11), [Founding](Founding.md) (9), [CityLand](CityLand.md) (8), [World](World.md) (8), [GameFiles](GameFiles.md) (7), [CityCalendar](CityCalendar.md) (6), [LandMarket](LandMarket.md) (5), [ForeignAccounts](ForeignAccounts.md) (4), [EarthData](EarthData.md) (3), [Resource](Resource.md) (2), [EarthSite](EarthSite.md) (2), [EarthProj](EarthProj.md) (2), [LandTable](LandTable.md) (1)

## Sections

| line | section |
|---:|---|
| 118 | THE FIXTURES |
| 169 | MAIN |
| 225 | 1. THE TABLE |
| 291 | 2. CANADA IS 1 |
| 322 | 3. THE FOUNDING, AND HISTORICAL'S 2024 |
| 366 | 4. RANDOM MOVES IT, WITHIN ITS BOUNDS |
| 413 | 5. THE SELLERS |
| 563 | 6. THE SPLIT |
| 647 | 7. THE PRICE |
| 693 | 8. PAYING |
| 796 | 9. THE SAVE |
| 871 | 10. HOW OFTEN IT FIRES IN A REAL RUN |
| 938 | 11. THE WORDS |

## Enum constants

| line | constant | says |
|---:|---|---|
| 929 | `LandNationsCheck.Kind.HOME` |  |
| 929 | `LandNationsCheck.Kind.SPLIT` |  |
| 929 | `LandNationsCheck.Kind.FOREIGN` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `LandNationsCheck.out` | `System.out` |  |
| 76 | `LandNationsCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 79 | `LandNationsCheck.SEED_A` | `20261101L, SEED_B = 20261102L` | The seeds the Random paths are drawn from. |
| 82 | `LandNationsCheck.WINDSOR_LAT` | `42.30, WINDSOR_LON = - 83.03, DETROIT_LAT = 42.335, DETROIT_LON = - 83.045` | Where the cities stand: Windsor on the Detroit River (Canada), Detroit across it (the United States), Halifax (Canada, the reference), the Strait of Gibraltar. |
| 83 | `LandNationsCheck.HALIFAX_LAT` | `44.64, HALIFAX_LON = - 63.57, GIBRALTAR_LAT = 36.14, GIBRALTAR_LON = - 5.35` |  |
| 86 | `LandNationsCheck.ACRES_PER_HECTARE` | `ham.citybuildersim.tools.LandTable.ACRES_PER_HECTARE` | A hectare in acres: the unit the USDA and Statistics Canada give their prices in (tools.LandTable converts by the same figure). |
| 876 | `LandNationsCheck.RUN_YEARS` | `10` | How many years the run plays, a look and some purchases at the start of each. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `static int fails` |  |
| 124 | `final Game g` |  |
| 125 | `final GameFiles files` |  |
| 450 | `int salt, kept, given, wrong` |  |
| 451 | `boolean givenOwnedByMorocco` |  |
| 452 | `final StringBuilder bad` |  |
| 453 | `double lastKeptKm` |  |
| 454 | `boolean givenThenKept` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 956 | **type** `public class LandNationsCheck` | Land bought from the nation it lies in, checked (WN5; docs/studies/spec-nations.md 6.2-6.4, D21, D22): the land table, each nation's land factor and how it moves, the seller of every plot of a real-Earth world and the... |
| 88 | 4 | `static void check(String label, boolean ok)` |  |
| 93 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 98 | 5 | `static void quietly(Runnable r)` |  |
| 104 | 5 | `static<T> T quietlyGet(Supplier<T> s)` |  |
| 111 | 3 | `static boolean near(double a, double b, double rel)` | Two figures within a relative gap (and equal when both are 0). |
| 116 | 1 | `static int januaryOf(int year)` | The game month of January of a year. |

### THE FIXTURES (lines 118-168)

| line | len | member | says |
|---:|---:|---|---|
| 123 | 21 | **type** `static final class City` | A city on a fixture world: its game and its folder. |
| 127 | 8 | `City(String label, Founding founding)` _(in LandNationsCheck.City)_ |  |
| 136 | 1 | `LandSellers sellers()` _(in LandNationsCheck.City)_ |  |
| 138 | 1 | `LandMarket office()` _(in LandNationsCheck.City)_ |  |
| 140 | 1 | `List<LandParcel> offers()` _(in LandNationsCheck.City)_ |  |
| 142 | 1 | `void months(int n)` _(in LandNationsCheck.City)_ |  |
| 146 | 14 | **type** `record Founded(List<LandParcel> offers, double groundUsd, double usLevel)` | A city as it stood when founded: its offers and the numbers they were priced at (the office's, which move with the month). |
| 147 | 3 | `static Founded of(City c)` _(in LandNationsCheck.Founded)_ |  |
| 152 | 7 | `double formula(LandParcel p)` _(in LandNationsCheck.Founded)_ | The one-part dollar formula on an offer's own ground, at the numbers it was listed by. |
| 161 | 1 | `static Founding onEarth(double lat, double lon)` |  |
| 163 | 5 | `static long[] plotOf(World w, double lat, double lon)` |  |

### MAIN (lines 169-224)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 45 | `public static void main(String[] args) throws Exception` |  |
| 219 | 5 | `static void finish(long started)` |  |

### 1. THE TABLE (lines 225-290)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 61 | `static void theTable(WorldData file, LandFactors f) throws IOException` |  |

### 2. CANADA IS 1 (lines 291-321)

| line | len | member | says |
|---:|---:|---|---|
| 295 | 26 | `static void canadaIsOne(WorldData file, Nations hist, Nations rand)` |  |

### 3. THE FOUNDING, AND HISTORICAL'S 2024 (lines 322-365)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 39 | `static void theFounding(WorldData file, Nations hist, Nations rand)` |  |

### 4. RANDOM MOVES IT, WITHIN ITS BOUNDS (lines 366-412)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 42 | `static void randomMovesIt(WorldData file, Nations hist, Nations randA, Nations randB)` |  |

### 5. THE SELLERS (lines 413-562)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 30 | `static void theSellers(WorldData file, City windsor, City halifax, City strait)` |  |
| 449 | 7 | **type** `static final class Walk` | What a walk of plots found: how many sea plots it read, how many of a foreign nation's sea were kept and how many given to the host, and the plots that disagreed with the search. |
| 457 | 17 | `static void territorialSea(WorldData file, City strait)` |  |
| 480 | 50 | `static Walk walk(WorldData file, LandSellers sellers, World w, double lat0, double lon0, double lat1, double lon1, int samples)` | Walks `samples` points from (lat0, lon0) to (lat1, lon1), takes the plot under each and checks its seller against the rule written out the slow way: the owner at the plot's blend point sells it; a sea plot of a foreig... |
| 535 | 21 | `static double nearestLandKm(WorldData file, EarthData data, LandSellers sellers, double lat, double lon, int seller)` | The least great-circle distance, in kilometres, from the centre of the 15" cell holding a point to the centre of a land cell (the landmass layer's) whose owner sells through `seller`, looking at every cell of a box th... |
| 557 | 5 | `static double greatCircleKm(double radius, double lat1, double lon1, double lat2, double lon2)` |  |

### 6. THE SPLIT (lines 563-646)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 73 | `static void theSplit(WorldData file, City windsor)` |  |
| 641 | 5 | `static long[] sum(List<LandSellers.Part> parts)` |  |

### 7. THE PRICE (lines 647-692)

| line | len | member | says |
|---:|---:|---|---|
| 652 | 4 | `static LandParcel detroitQuote(City windsor)` | A block of the United States' ground in Detroit, quoted by the Windsor office: a draft of it, nothing kept. |
| 657 | 35 | `static void thePrice(WorldData file, Founded windsorF, City windsor, Founded halifaxF, City halifax, Founded generatedF, City g...` |  |

### 8. PAYING (lines 693-795)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 93 | `static void paying(WorldData file, City windsor, City halifax, City generated)` |  |
| 791 | 4 | `static LandParcel.Sale part(LandParcel p, int seller)` |  |

### 9. THE SAVE (lines 796-870)

| line | len | member | says |
|---:|---:|---|---|
| 800 | 55 | `static void theSave(WorldData file, City windsor) throws IOException` |  |
| 857 | 13 | `static Game loadCopy(GameFiles from, String text, String label) throws IOException` | A save's text written to slot 1 of a folder of its own, beside whatever else the city's saves folder holds, and loaded into a fresh game. |

### 10. HOW OFTEN IT FIRES IN A REAL RUN (lines 871-937)

| line | len | member | says |
|---:|---:|---|---|
| 878 | 50 | `static void howOften(WorldData file)` |  |
| 929 | 1 | **type** `enum Kind` |  |
| 931 | 6 | `static Kind kind(LandParcel p, int host)` |  |

### 11. THE WORDS (lines 938-1027)

| line | len | member | says |
|---:|---:|---|---|
| 942 | 85 | `static void theWords(WorldData file, City windsor, Founded founded, LandParcel detroit)` |  |

