# RoadCheck.java - 851 lines · 28 methods · 2 constants · harnesses

`ham/citybuildersim/RoadCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> look); the 0.7.99 one paves 87 gravel roads in 14 orders over its four
> thousand months (its report counts them). This is where the paving is
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
>   2. THE ADVICE TAKES THE LEAST OVER ITS LIFE, AND GRAVEL CAN STILL WIN:
>      with no ground free, the road the advice suggests is the candidate
>      whose order costs least over its life for the trips it will take off
>      over that life (0.7.101, Jerus's A4 and A19; its life a trip until
>      then), every candidate's figure recomputed; by their lives a trip,
>      with the land office's prices at nothing the gravel road is the
>      least, past the crossover the model's figures give the paved road,
>      and past the next the highway.
>   3. THE ROAD CARDS SAY THE SAME: bar 1 is the road's life a trip off the
>      road and bar 2 its ground a trip, to the bit, and the card tagged
>      cheapest is the road the advice ranks first among the three.
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
>   6. WHAT IT REFUSES: more than there are to pave; a Paved Road site set
>      to stop; short of the cash, with nothing moved; and while it paves
>      the Paved Road site is not stopped and a gravel road being paved is
>      not demolished.
>   7. A SAVE AND A LOAD, TO THE CENT: a paving in progress reads back, its
>      ground and the ledger to the bit, and the two cities play its
> ... (12 more lines in the source)

**Uses:** [BuildAdvice](BuildAdvice.md) (82), [Game](Game.md) (38), [BuildingsTemplate](BuildingsTemplate.md) (33), [LongPlaytest](LongPlaytest.md) (11), [ConstructionControl](ConstructionControl.md) (10), [BuildCard](BuildCard.md) (10), [CityNeeds](CityNeeds.md) (8), [BuildingManager](BuildingManager.md) (6), [BuildingType](BuildingType.md) (5), [GameFiles](GameFiles.md) (4), [BuildAdviceCheck](BuildAdviceCheck.md) (4), [Founding](Founding.md) (3), [MoneyAudit](MoneyAudit.md) (2), [Sector](Sector.md) (2), [LandManager](LandManager.md) (2), [LandMarket](LandMarket.md) (2), [Good](Good.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [EconomyManager](EconomyManager.md) (1), [RealEstate](RealEstate.md) (1), [GoodsMarket](GoodsMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 220 | 1. A ROAD'S LIFE |
| 304 | 2. THE ADVICE |
| 401 | 3. THE CARDS |
| 438 | 4. THE PAVING'S PRICE |
| 512 | 5. PLAYED |
| 575 | 6. REFUSED |
| 615 | 7. SAVE AND LOAD |
| 678 | 8. OFFERED |
| 731 | 9. THE TEST PLAYER |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 132 | `RoadCheck.GRAVEL` | `ConstructionControl.PAVE_FROM, PAVED = ConstructionControl.PAVE_TO, HIGHWAY =...` | The three roads by name: the paving's from and to, and the highway. |
| 136 | `RoadCheck.ROADS` | `BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS)` | The advice's measure for roads, which the sections here read the site, the units and the cards through. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `static int fails` |  |
| 94 | `static PrintStream out` |  |
| 95 | `static PrintStream quiet` |  |
| 139 | `static int monthsAudited` | The months audited, and the worst: a month passes the playtest's audit within a cent or 1e-7 of what moved. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 91 | 761 | **type** `public class RoadCheck` | The roads over their lives, and a gravel road paved (0.7.70, batch N1): BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to their own arithmetic in a played city. |
| 97 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 102 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 107 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 113 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 119 | 5 | `static void quietly(Runnable r)` |  |
| 125 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 141 | 6 | `static void month(Game g)` |  |
| 153 | 3 | `static Game town(Path root, String name)` | The fixture: a town of 1,200 houses on four gravel roads, every sector held, played three years - the road past NEEDS YOU's line, and four gravel roads to pave (calibrated: scratch-n1/fix-1200.txt). |
| 158 | 19 | `static Game town(Path root, String name, String road, int n)` | ...or on `n` roads of another kind: three Paved Roads carry what the four gravel roads do, and leave nothing to pave. |
| 179 | 9 | `static double[][] noGroundFree(Game g)` | The same town with no ground free: every road's ground bought at the land office's price. |
| 190 | 4 | `static void scale(Game g, double[][] base, double f)` | The land office's listing at f times its prices. |
| 195 | 4 | `static CityNeeds.Need roadNeed(Game g)` |  |
| 200 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1. A ROAD'S LIFE (lines 220-303)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 81 | `static void life(Path root)` |  |

### 2. THE ADVICE (lines 304-400)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 15 | `static boolean leastOrder(Game g, CityNeeds.Need need, BuildAdvice.Suggestion first)` | Whether the advice's road card is the least of its own candidates (0.7.101): the ranking walked as automatic building walks it - each card in turn left out (BuildAdvice.suggestFor()'s skip) - every card's figure its o... |
| 331 | 15 | `static String[] least(Game g, boolean roadsOnly)` | The least over its life a trip of every road and line the advice weighs, recomputed: {its name, its figure}. |
| 352 | 8 | `static double crossover(Game g, BuildingsTemplate a, BuildingsTemplate b)` | The land price a square foot, all of a road's ground at it, at which two roads cost the same a trip over their lives: (u_a k_b - u_b k_a) / (u_b A_a - u_a A_b), k a road's life but its ground, A its ground. |
| 361 | 39 | `static void advice(Path root)` |  |

### 3. THE CARDS (lines 401-437)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 34 | `static void cards(Path root)` |  |

### 4. THE PAVING'S PRICE (lines 438-511)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 71 | `static void pavePrice(Path root)` |  |

### 5. PLAYED (lines 512-574)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 60 | `static void played(Path root)` |  |

### 6. REFUSED (lines 575-614)

| line | len | member | says |
|---:|---:|---|---|
| 577 | 37 | `static void refused(Path root)` |  |

### 7. SAVE AND LOAD (lines 615-677)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 60 | `static void saveAndLoad(Path root) throws Exception` |  |

### 8. OFFERED (lines 678-730)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 50 | `static void offered(Path root)` |  |

### 9. THE TEST PLAYER (lines 731-851)

| line | len | member | says |
|---:|---:|---|---|
| 733 | 83 | `static void player(Path root)` |  |
| 818 | 6 | `static BuildAdvice.Suggestion roadCard(Game g)` | The advice's card for the road, the transit lines left out of its ranking as the player leaves them: BuildAdvice.suggestFor() at its own slack, the cash and the free ground as they stand. |
| 826 | 4 | `static int standingAndOnSite(Game g, BuildingsTemplate t)` | A building's count standing and on site. |
| 835 | 16 | `static boolean firstRoad(Game g, double[][] base, double f, String want)` | The test player's first road move at the land office's prices x f, against the advice's ranking recomputed: the least of the three roads over its life a trip, and the paving where it beats a new Paved Road and is less. |

