# ForeignCrewsCheck.java - 921 lines · 26 methods · 17 constants · harnesses

`ham/citybuildersim/ForeignCrewsCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The foreign crews always on offer (FC2): the threshold past which the city's orders go abroad, read at the exchange
> rate the city's money stands at and the city's own inflation; the handover of the queue past it; the supply curve
> their month climbs; and the export of the builders' spare points - held on the calm city of ConstructionTrapCheck
> (its landlords' Houses queued at 0.8 of the knee), whose roads are stood one at a time until the model's own bound is
> crossed.
> 
> WHY. Jerus, 2026-11-22 (docs/queue/FC2.md): "make it a imported/exported good". FC1's contractor came only under a
> floor of the month, at a price that read no exchange rate; FC2 has him always on offer at p (1 + r), p the city's own
> base price for a point and r = E x / p, x FOREIGN_EXCESS of the world's base price for the same work and E the rate
> the goods are struck at, capped where the threshold is a year past the knee. That is a price that must answer the
> currency and not the city's inflation, and a threshold that must stand exactly where the premium reaches it. The model
> is BuildersShort (the arithmetic, THE FOREIGN CREWS banner), BuildingManager (foreignTerms() and what reads it), Game
> (foreignCrewsOffer(), settleForeignCrews()) and sectors.Construction (his book, his bill, the sales abroad).
> ConstructionTrapCheck holds the trap, the price's shape and the money of FC1 and FC2's first cut; this holds FC2's own
> bounds, each caused through the model's inputs - the rate, the queue, the level, the roads - on both sides of its edge.
> 
> What this has to prove:
>   1. A STRONG CURRENCY, UNDER THE KNEE: at the strongest D$ the threshold is the knee and not a thousandth of a month
>      over it; with the queue under the knee no order goes abroad, at every road walked an order goes abroad exactly when
>      the months queued pass the threshold, never within the knee, and the first road past it sends it abroad.
>   2. TWO RATES PAST THE KNEE: the rate at which the first share is 5% and then 10% gives that share (FOREIGN_EXCESS E x
>      over p, independently computed) and the threshold BuildersShort.thresholdMonths() of it; setting the rate leaves the
>      queue under the new threshold; the road before the crossing is not abroad with its premium within the share and the
>      crossing road is abroad with its premium past it; the second crossing is at more months than the first.
>   3. THE WEAKEST RATE: the first share is the cap, the threshold exactly SURE_MONTHS past the knee; never abroad within
>      it, abroad at the first road past it.
>   4. DOMESTIC INFLATION: the city's own level doubled (as the month re-strikes it) moves neither the world's price of a
>      point (E x) nor x; p rises, so r falls and the threshold falls - never under the knee.
>   5. THE EXPORT: a month in which the sites took less than the builders' month sells the rest abroad at the price read
>      before it (the month re-strikes), never more than the sites' month nor starving a site, booked as the builders'
>      exports the month after in the audit's TRADE scope and the national accounts; the price is something to the knee
>      and nothing past it, and a month played past it sells nothing.
>   6. A 240-MONTH BACKLOG: the offer hands him exactly the overflow read before it and leaves the queue at home at the
>      threshold's months of the sites; his month is far over the sites'; half the backlog is a smaller month at a smaller
>      excess; the backlog read at both paces stays within a month under the threshold, which stays within SURE_MONTHS of
>      the knee; and his crews take no worker from the city (a twin with the price switched off for its first month).
>   7. THE BUILDING THE THRESHOLD FALLS IN (REV-FC2, the second review): a Coal Power Plant, a year of the sites' month
>      many times over, ordered with the queue under the threshold is split at it - its points past the threshold abroad,
>      point for point, priced at the premium for its part at home and the crews' share for the rest, and the queue at home
>      left at the threshold, not years past it; a month on, the queue at home is under it and the next order is built at
>      home. Each site claims its own work on his book, and his month falls on every claim alike; a cancel takes the
>      cancelled site's own off it: nothing for a Middle School quoted at home, the plant's own and not the Houses' ordered
>      after it; the claims survive a save, and a save without them shares his book by the sites' points owed. A site that
>      finished claims none of his book when Houses quoted at home go on it later, in the city and its loaded twin alike,
>      and a cancel after it takes the same share in both (the third review). A works on foothill, split, hands him its
>      pad's preparation in its building's share, and the queue counts the preparation owed on its site.
>   8. A CURRENCY REFORM: a reform dividing the money by 1,000 divides his book's price a point by 1,000, and the offer's,
>      and his next bill is his points at it - not a thousandfold abroad at the old unit's price.
>   9. THE AUDIT: every month played here balanced to the cent and left nothing moving after it.
> 
> Every fixture causes its condition.

**Uses:** [Game](Game.md) (47), [BuildersShort](BuildersShort.md) (29), [BuildingManager](BuildingManager.md) (23), [Construction](Construction.md) (10), [ConstructionControl](ConstructionControl.md) (10), [ConstructionTrapCheck](ConstructionTrapCheck.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (8), [MoneyAudit](MoneyAudit.md) (5), [ForeignAccounts](ForeignAccounts.md) (4), [Fitout](Fitout.md) (4), [BuildingsStacks](BuildingsStacks.md) (4), [GameFiles](GameFiles.md) (3), [DeathCareFirstCheck](DeathCareFirstCheck.md) (2), [Sector](Sector.md) (2), [TempDirs](TempDirs.md) (1), [Sectors](Sectors.md) (1), [CityLand](CityLand.md) (1), [PopulationManager](PopulationManager.md) (1)

## Sections

| line | section |
|---:|---|
| 182 | THE WALK |
| 236 | 1. A STRONG CURRENCY, UNDER THE KNEE |
| 259 | 2. TWO RATES PAST THE KNEE |
| 292 | 3. THE WEAKEST RATE |
| 312 | 4. DOMESTIC INFLATION |
| 344 | 5. THE EXPORT |
| 422 | 6. A 240-MONTH BACKLOG |
| 569 | 7. THE BUILDING THE THRESHOLD FALLS IN |
| 864 | 8. A CURRENCY REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 82 | `ForeignCrewsCheck.MOST_ROADS` | `100` | The most single roads a walk adds after its jump: the jump stops JUMP_SHORT roads under the limit, so a handful reach it. |
| 85 | `ForeignCrewsCheck.JUMP_SHORT` | `3` | How many roads under the model's own figure a walk's jump stops, so the roads either side of the limit are walked one at a time. |
| 88 | `ForeignCrewsCheck.MOST_MONTHS` | `24` | The most months the export city is played for the queue at home to fall under one month of the sites, and again for a month that sells. |
| 91 | `ForeignCrewsCheck.SHARES` | `{ 0.05, 0.10 }` | The first shares section 2 sets the rate for: both under the cap, so the cap does not bind. |
| 94 | `ForeignCrewsCheck.INFLATION_SHARE` | `0.10` | The share section 4 sets the rate for before the city's level doubles: under the cap, so r can fall. |
| 97 | `ForeignCrewsCheck.LONG_MONTHS` | `240, HALF_MONTHS_QUEUED = 120` | The queue, in months of the sites, of the long backlog (twenty years) and of the half of it. |
| 100 | `ForeignCrewsCheck.FAR_OVER` | `5` | How many sites' months the contractor's first month must be over, for a backlog of twenty years: far over. |
| 103 | `ForeignCrewsCheck.PLAYED` | `6` | The months played in the long backlog, to read the pace each one. |
| 110 | `ForeignCrewsCheck.QUOTED` | `new java.util.HashMap<>()` | What each backlog fixture's order was quoted, struck just before it was placed (REV-FC2's P1): the foreign crews' share for its work past the threshold without the order (what FC2 quoted until the mend) and with it (t... |
| 113 | `ForeignCrewsCheck.SCHOOL` | `"Middle School", PLANT = "Coal Power Plant"` | Section 7's buildings: one quoted at home, and one far bigger than the threshold's months of the sites, which it splits. |
| 116 | `ForeignCrewsCheck.HOUSES` | `200` | ...and the Houses ordered after the plant, abroad whole: work of another site's on his book beside it (at the least; housesToOutlast()). |
| 119 | `ForeignCrewsCheck.HOUSE_MONTHS` | `3` | ...as many as outlast this many months of everything the city's crews and his could build on their site: the month on, the cancels' month and one more. |
| 122 | `ForeignCrewsCheck.WORKS` | `"Water Treatment Plant"` | ...and the works ordered on foothill ground at the section's end, split at the threshold with its pad's preparation. |
| 125 | `ForeignCrewsCheck.MOST_MONTHS_TO_FINISH` | `120` | The months the section plays at most for the Houses' site to finish with the plant's work on his book. |
| 128 | `ForeignCrewsCheck.REFORM` | `1000` | Section 8's reform: the money divided by a thousand, the reviewer's case. |
| 131 | `ForeignCrewsCheck.ONE_MONTH` | `1` | The months queued within which, under the threshold, the backlog read at both paces must stay: one month. |
| 134 | `ForeignCrewsCheck.A_POINT` | `1` | The points within which two figures of one month are the same: the sites take whole points of a month struck in whole ones. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 137 | `static int checked` | The assertions made here (those ConstructionTrapCheck's helpers count are in its own total). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 843 | **type** `public class ForeignCrewsCheck` | The foreign crews always on offer (FC2): the threshold past which the city's orders go abroad, read at the exchange rate the city's money stands at and the city's own inflation; the handover of the queue past it; the ... |
| 139 | 1 | `static void ok(String label, boolean cond)` |  |
| 141 | 1 | `static void near(String label, double actual, double expected, double tol)` |  |
| 143 | 1 | `static void exact(String label, double actual, double expected)` |  |
| 146 | 6 | `static void within(String label, double actual, double expected, double tol)` | Two figures within `tol` of each other, absolutely: the points of a month, which are whole. |
| 153 | 28 | `public static void main(String[] args) throws Exception` |  |

### THE WALK (lines 182-235)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 2 | **type** `record Walk(int roads, double before, double at, boolean abroadBefore, boolean abroadAt, double thresholdAt...` | What a walk of single roads left: the roads added, the months queued at the last road not past the limit and at the first past it (the crossing), whether an order went abroad at each, the threshold at the crossing, wh... |
| 198 | 25 | `static Walk walk(Game g, double limit)` | Standing Paved Roads on the city, no month played: JUMP_SHORT roads under the model's own figure for `limit` months queued (ConstructionTrapCheck.roadsFor()), then one at a time until the months queued pass it. |
| 224 | 6 | `static void printWalk(String what, Walk w)` |  |
| 232 | 3 | `static double rateFor(BuildingManager bm, double share)` | The rate E at which the foreign crews' first share is `share`, from the base price now and the founding's: E = r p / (FOREIGN_EXCESS x_founding). |

### 1. A STRONG CURRENCY, UNDER THE KNEE (lines 236-258)

| line | len | member | says |
|---:|---:|---|---|
| 238 | 20 | `static void strongCurrency(Game g)` |  |

### 2. TWO RATES PAST THE KNEE (lines 259-291)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 30 | `static void twoRates(Game g)` |  |

### 3. THE WEAKEST RATE (lines 292-311)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 17 | `static void weakestRate(Game g)` |  |

### 4. DOMESTIC INFLATION (lines 312-343)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 29 | `static void domesticInflation(Game g)` |  |

### 5. THE EXPORT (lines 344-421)

| line | len | member | says |
|---:|---:|---|---|
| 346 | 75 | `static void theExport(Path root)` |  |

### 6. A 240-MONTH BACKLOG (lines 422-568)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 22 | `static Game backlog(Path root, String name, double months)` | A calm city with the landlords' Houses ordered on top of its own, enough that the queue at home is `months` of the sites (ConstructionTrapCheck.calm()'s own way of ordering them: the landlords given twice the quoted p... |
| 452 | 116 | `static Game theBacklog(Path root)` |  |

### 7. THE BUILDING THE THRESHOLD FALLS IN (lines 569-863)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 4 | `static ConstructionControl.Refund refundOf(Game g, BuildingsTemplate t)` | The refund the month's end made for t's site, or null. |
| 578 | 10 | `static boolean cityOrders(Game g, BuildingsTemplate t, long n)` | n of t ordered by the city, quietly, its cash and ground given first; false if the order was not placed. |
| 589 | 135 | `static void theSplit(Path root)` |  |
| 730 | 7 | `static long housesToOutlast(Game g, BuildingsTemplate house, double houseEach)` | The Houses section 7 orders after the plant: HOUSES, doubled until their site owes more than HOUSE_MONTHS of everything the city's crews (at every post, before the repairs) and his (his month at his book with them on ... |
| 739 | 5 | `static double monthOfEveryCrew(Game g, double moreOnHisBook)` | Everything the city's crews (at every post, before the repairs) and his (his month at his book, and this more on it) could build in a month. |
| 746 | 4 | `static double roomAtHome(Game g)` | The points the queue at home has under the threshold: its months under it, in the sites' month at every post. |
| 757 | 68 | `static void claimOfAFinishedSite(Path root, Game g)` | REV-FC2, the third review (P2-1): the Houses' site finishes with the work its orders handed him on his book beside a second plant's; a load reads no claim for a site that owes nothing, and the city must read none eith... |
| 832 | 25 | `static void partsAbroadOwedOnSite(Game g)` | REV-FC2, the third review (P2-2): a works on foothill ground, split at the threshold, hands him its pad's preparation in the share its building's points are (Game.abroadWork()); the preparation is owed on its site wit... |
| 858 | 5 | `static double sum(java.util.Map<Integer, Double> m)` |  |

### 8. A CURRENCY REFORM (lines 864-921)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 20 | `static void theReform(Game a)` |  |
| 888 | 33 | `static void afterTheFirstMonth(Game a, Game off)` | The city's workers after a's first month and the twin's, which had no foreign crews. |

