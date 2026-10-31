# RoadCheck.java - 1,199 lines · 35 methods · 2 constants · harnesses

`ham/citybuildersim/RoadCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The roads over their lives, and a gravel road paved (0.7.70, batch N1):
> BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to
> their own arithmetic in a played city.
> 
> WHY. Jerus: "the game still recommends gravel roads, even when i think
> paved roads are better, also ... make it an option to upgrade from gravel
> to paved, but not from paved to highway, and that the build menu allows
> and recommends this if better, total cost is higher than just building
> paved." The advice priced a road's ground from 0.7.51, but Build's road
> cards priced a trip without it and the test player ranked the roads by
> their founding cash cost - gravel first everywhere (runs/fixN1-notes.md).
> Now one figure, a road's cost over its life a trip it takes off the road,
> ranks the advice, draws the cards' first bar and orders the test player's
> roads; and a gravel road can be paved, at a price above a Paved Road's
> less the gravel's own, on its own ground, open while the works go on.
> The 0.7.70 playtest never paved (a new road was cheaper there at every
> look); the 0.7.99 one paved 87 gravel roads in 14 orders over its four
> thousand months, and the 0.7.106 one, its player ordering roads by need
> since 0.7.103, 10 in 2 (its report counts them). This is where the paving is
> played to order, each fixture causing it.
> 
> What this has to prove:
>   1. A ROAD'S LIFE, BY THE MODEL'S OWN RULES: every road and line weighed
>      for the road is its quote, its ground at landValue(), and running()
>      for LIFE_MONTHS at the real rate - each part recomputed here, to the
>      bit; the repairs in running() are what the month's maintenance bill
>      charges the city for one more such road - since 0.7.103 (batch P4,
>      Jerus's A11) each road kind at its own share a year of its price
>      (BuildingsTemplate.repairsPerYear(): gravel 3%, paved 2%, the
>      highway 1.25%; a line the house's 1%), so gravel's share of its
>      price is the dearest and the highway's the least, and gravel's lead
>      a trip it carries over paved shrinks from nearly half to a seventh.
>  1b. (FR7 2.2, Jerus's star 5, 2026-10-18) A GRAVEL ROAD'S REPAIRS RISE WITH
>      THE TRAFFIC ON ITS STREET: its flat share up to 200 vehicles a day,
>      then that share times (vehicles / 200) squared - struck from the dials,
>      the paved road's and the highway's flat, the month's bill the same
>      on a quiet street and up by the wear on a busy one.
>   2. WITHIN THE STREETS, THE KIND FOLLOWS THE LIFE COST AND THE LOAD (FR7,
>      Jerus's star 4; this section's one-pool ladder - gravel, then paved,
>      then a highway by land price - is retired by his decision): on a
>      quiet street the gravel road is the least over its life where ground
>      is cheap and the paved road where it is dear; on a busy one the paved
>      road is the least on the same ground and is the advice's card, every
>      candidate's figure recomputed (0.7.101, Jerus's A4 and A19).
>   3. THE ROAD CARDS SAY THE SAME: bar 1 is the road's life a trip off the
>      road and bar 2 its ground a trip, to the bit, and the card tagged
>      cheapest is the street the advice ranks first (the highway has a
>      group of its own since FR7).
>   4. THE PAVING'S PRICE: a Paved Road's work and the take-up at
>      DEMOLITION_SHARE of the gravel road's, the material of a Paved Road
>      less a gravel road's, no ground, a Paved Road's wait; a gravel road
>      and its paving cost more than a Paved Road built outright. Since
>      0.7.83 (batch O6) plus its surface's bitumen, a Paved Road's 64.25 t,
>      in the quote - as a new Paved Road's and an Elevated Highway's are.
>   5. PAVING, ONE ROAD AND MANY, PLAYED: a paving of one and a paving of
>      three behind a new Paved Road; the treasury pays the quote; each
>      gravel road carries its traffic until its own Paved Road opens, the
>      new one first; the network's capacity is the roads standing every
>      month; the land ledger is the footprint every month and frees the
>      ground between the two roads as each opens; the money audit closes.
> ... (36 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (113), [InfrastructureManager](InfrastructureManager.md) (45), [Game](Game.md) (44), [BuildingsTemplate](BuildingsTemplate.md) (40), [CityNeeds](CityNeeds.md) (31), [LongPlaytest](LongPlaytest.md) (14), [ConstructionControl](ConstructionControl.md) (11), [BuildCard](BuildCard.md) (10), [BuildingType](BuildingType.md) (9), [BuildingManager](BuildingManager.md) (7), [GameFiles](GameFiles.md) (4), [LandManager](LandManager.md) (4), [BuildAdviceCheck](BuildAdviceCheck.md) (4), [Founding](Founding.md) (3), [MoneyAudit](MoneyAudit.md) (2), [Sector](Sector.md) (2), [LandMarket](LandMarket.md) (2), [EconomyManager](EconomyManager.md) (2), [Good](Good.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [TempDirs](TempDirs.md) (1), [RealEstate](RealEstate.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 248 | 1. A ROAD'S LIFE |
| 350 | 1b. GRAVEL'S REPAIRS RISE WITH ITS TRAFFIC |
| 397 | 2. THE ADVICE |
| 515 | 3. THE CARDS |
| 552 | 4. THE PAVING'S PRICE |
| 626 | 5. PLAYED |
| 689 | 6. REFUSED |
| 729 | 7. SAVE AND LOAD |
| 792 | 8. OFFERED |
| 862 | 9. THE TEST PLAYER |
| 977 | 10. THE THROUGH TRIPS, WITH NO HIGHWAY |
| 1079 | 11. A LARGE CITY'S NEEDS YOU LISTS BOTH |
| 1158 | 12. A HIGHWAY GIVES NO ACCESS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 156 | `RoadCheck.GRAVEL` | `ConstructionControl.PAVE_FROM, PAVED = ConstructionControl.PAVE_TO, HIGHWAY =...` | The three roads by name: the paving's from and to, and the highway. |
| 160 | `RoadCheck.ROADS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS)` | The advice's measure for roads, which the sections here read the site, the units and the cards through. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 117 | `static int fails` |  |
| 118 | `static PrintStream out` |  |
| 119 | `static PrintStream quiet` |  |
| 163 | `static int monthsAudited` | The months audited, and the worst: a month passes the playtest's audit within a cent or 1e-7 of what moved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 115 | 1085 | **type** `public class RoadCheck` | The roads over their lives, and a gravel road paved (0.7.70, batch N1): BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to their own arithmetic in a played city. |
| 121 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 126 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 131 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 137 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 143 | 5 | `static void quietly(Runnable r)` |  |
| 149 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 165 | 6 | `static void month(Game g)` |  |
| 177 | 3 | `static Game town(Path root, String name)` | The fixture: a town of 1,200 houses on four gravel roads, every sector held, played three years - the road past NEEDS YOU's line, and four gravel roads to pave (calibrated: scratch-n1/fix-1200.txt). |
| 182 | 19 | `static Game town(Path root, String name, String road, int n)` | ...or on `n` roads of another kind: three Paved Roads carry what the four gravel roads do, and leave nothing to pave. |
| 203 | 9 | `static double[][] noGroundFree(Game g)` | The same town with no ground free: every road's ground bought at the land office's price. |
| 214 | 4 | `static void scale(Game g, double[][] base, double f)` | The land office's listing at f times its prices. |
| 219 | 4 | `static CityNeeds.Need roadNeed(Game g)` |  |
| 224 | 23 | `public static void main(String[] args) throws Exception` |  |

### 1. A ROAD'S LIFE (lines 248-349)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 99 | `static void life(Path root)` |  |

### 1b. GRAVEL'S REPAIRS RISE WITH ITS TRAFFIC (lines 350-396)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 38 | `static void gravelWear(Path root)` | FR7 2.2 (Jerus's star 5, 2026-10-18): a Gravel Road's repairs rise with the load on its street. |

### 2. THE ADVICE (lines 397-514)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 15 | `static boolean leastOrder(Game g, CityNeeds.Need need, BuildAdvice.Suggestion first)` | Whether the advice's road card is the least of its own candidates (0.7.101): the ranking walked as automatic building walks it - each card in turn left out (BuildAdvice.suggestFor()'s skip) - every card's figure its o... |
| 424 | 15 | `static String[] least(Game g, boolean roadsOnly)` | The least over its life a trip of every road and line the advice weighs, recomputed: {its name, its figure}. |
| 445 | 8 | `static double crossover(Game g, BuildingsTemplate a, BuildingsTemplate b)` | The land price a square foot, all of a road's ground at it, at which two roads cost the same a trip over their lives: (u_a k_b - u_b k_a) / (u_b A_a - u_a A_b), k a road's life but its ground, A its ground. |
| 463 | 51 | `static void advice(Path root)` | FR7 (Jerus's stars 4 and 5, 2026-10-18) RETIRED THIS SECTION'S LADDER, by his decision and not to let a change through: the road was one pool with one winner, and the advice named gravel below the gravel-to-paved cros... |

### 3. THE CARDS (lines 515-551)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 34 | `static void cards(Path root)` |  |

### 4. THE PAVING'S PRICE (lines 552-625)

| line | len | member | says |
|---:|---:|---|---|
| 554 | 71 | `static void pavePrice(Path root)` |  |

### 5. PLAYED (lines 626-688)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 60 | `static void played(Path root)` |  |

### 6. REFUSED (lines 689-728)

| line | len | member | says |
|---:|---:|---|---|
| 691 | 37 | `static void refused(Path root)` |  |

### 7. SAVE AND LOAD (lines 729-791)

| line | len | member | says |
|---:|---:|---|---|
| 731 | 60 | `static void saveAndLoad(Path root) throws Exception` |  |

### 8. OFFERED (lines 792-861)

| line | len | member | says |
|---:|---:|---|---|
| 795 | 5 | `static java.util.Set<BuildingsTemplate> everyRoadAndLine(Game g)` | Every road and line in the Infrastructure category (Roads & transit until FR4): what the advice's paving-only ranking leaves out, as automatic building walks past roads the ground cannot hold. |
| 809 | 52 | `static void offered(Path root)` | FR7 2.2 (Jerus's star 5, 2026-10-18) retired the premise of the old section 8: that paving beats a new Paved Road only past a CROSSOVER OF THE LAND PRICE above today's. |

### 9. THE TEST PLAYER (lines 862-976)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 77 | `static void player(Path root)` |  |
| 943 | 6 | `static BuildAdvice.Suggestion roadCard(Game g)` | The advice's card for the road, the transit lines left out of its ranking as the player leaves them: BuildAdvice.suggestFor() at its own slack, the cash and the free ground as they stand. |
| 951 | 4 | `static int standingAndOnSite(Game g, BuildingsTemplate t)` | A building's count standing and on site. |
| 960 | 16 | `static boolean firstRoad(Game g, double[][] base, double f, String want)` | The test player's first road move at the land office's prices x f, against the advice's ranking recomputed: the least of the three roads over its life a trip, and the paving where it beats a new Paved Road and is less. |

### 10. THE THROUGH TRIPS, WITH NO HIGHWAY (lines 977-1078)

| line | len | member | says |
|---:|---:|---|---|
| 980 | 1 | `static double areaOf(double halves)` | A built-up area, in square feet, whose through share is the dials' halfway: THROUGH_HALF_AREA_KM2 times `halves`. |
| 992 | 84 | `static void throughStrain()` | FR7 2.1 (Jerus's star 4, 2026-10-18): a city with no highway carries its through trips on the streets, and strains them. |
| 1077 | 1 | `static boolean bitsEqual(double a, double b)` |  |

### 11. A LARGE CITY'S NEEDS YOU LISTS BOTH (lines 1079-1157)

| line | len | member | says |
|---:|---:|---|---|
| 1089 | 68 | `static void bothNeeds(Path root)` | FR7 2.1: the streets and the highways are two needs, each its own measure and line on NEEDS YOU, and in a large city both come up. |

### 12. A HIGHWAY GIVES NO ACCESS (lines 1158-1199)

| line | len | member | says |
|---:|---:|---|---|
| 1165 | 34 | `static void noAccess(Path root)` | FR7 2.1: a highway is a carriageway nothing fronts, so its capacity is not the streets'. |

