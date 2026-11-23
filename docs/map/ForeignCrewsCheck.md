# ForeignCrewsCheck.java - 566 lines · 16 methods · 11 constants · harnesses

`ham/citybuildersim/ForeignCrewsCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
>   7. THE AUDIT: every month played here balanced to the cent and left nothing moving after it.
> 
> Every fixture causes its condition.

**Uses:** [BuildersShort](BuildersShort.md) (26), [Game](Game.md) (20), [BuildingManager](BuildingManager.md) (13), [ConstructionTrapCheck](ConstructionTrapCheck.md) (8), [Construction](Construction.md) (5), [MoneyAudit](MoneyAudit.md) (5), [ForeignAccounts](ForeignAccounts.md) (4), [DeathCareFirstCheck](DeathCareFirstCheck.md) (2), [TempDirs](TempDirs.md) (1), [Sectors](Sectors.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [GameFiles](GameFiles.md) (1), [Sector](Sector.md) (1), [PopulationManager](PopulationManager.md) (1)

## Sections

| line | section |
|---:|---|
| 149 | THE WALK |
| 203 | 1. A STRONG CURRENCY, UNDER THE KNEE |
| 226 | 2. TWO RATES PAST THE KNEE |
| 259 | 3. THE WEAKEST RATE |
| 279 | 4. DOMESTIC INFLATION |
| 311 | 5. THE EXPORT |
| 389 | 6. A 240-MONTH BACKLOG |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `ForeignCrewsCheck.MOST_ROADS` | `100` | The most single roads a walk adds after its jump: the jump stops JUMP_SHORT roads under the limit, so a handful reach it. |
| 73 | `ForeignCrewsCheck.JUMP_SHORT` | `3` | How many roads under the model's own figure a walk's jump stops, so the roads either side of the limit are walked one at a time. |
| 76 | `ForeignCrewsCheck.MOST_MONTHS` | `24` | The most months the export city is played for the queue at home to fall under one month of the sites, and again for a month that sells. |
| 79 | `ForeignCrewsCheck.SHARES` | `{ 0.05, 0.10 }` | The first shares section 2 sets the rate for: both under the cap, so the cap does not bind. |
| 82 | `ForeignCrewsCheck.INFLATION_SHARE` | `0.10` | The share section 4 sets the rate for before the city's level doubles: under the cap, so r can fall. |
| 85 | `ForeignCrewsCheck.LONG_MONTHS` | `240, HALF_MONTHS_QUEUED = 120` | The queue, in months of the sites, of the long backlog (twenty years) and of the half of it. |
| 88 | `ForeignCrewsCheck.FAR_OVER` | `5` | How many sites' months the contractor's first month must be over, for a backlog of twenty years: far over. |
| 91 | `ForeignCrewsCheck.PLAYED` | `6` | The months played in the long backlog, to read the pace each one. |
| 98 | `ForeignCrewsCheck.QUOTED` | `new java.util.HashMap<>()` | What each backlog fixture's order was quoted, struck just before it was placed (REV-FC2's P1): the foreign crews' share for its work past the threshold without the order (what FC2 quoted until the mend) and with it (t... |
| 101 | `ForeignCrewsCheck.ONE_MONTH` | `1` | The months queued within which, under the threshold, the backlog read at both paces must stay: one month. |
| 104 | `ForeignCrewsCheck.A_POINT` | `1` | The points within which two figures of one month are the same: the sites take whole points of a month struck in whole ones. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 107 | `static int checked` | The assertions made here (those ConstructionTrapCheck's helpers count are in its own total). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 500 | **type** `public class ForeignCrewsCheck` | The foreign crews always on offer (FC2): the threshold past which the city's orders go abroad, read at the exchange rate the city's money stands at and the city's own inflation; the handover of the queue past it; the ... |
| 109 | 1 | `static void ok(String label, boolean cond)` |  |
| 111 | 1 | `static void near(String label, double actual, double expected, double tol)` |  |
| 113 | 1 | `static void exact(String label, double actual, double expected)` |  |
| 116 | 6 | `static void within(String label, double actual, double expected, double tol)` | Two figures within `tol` of each other, absolutely: the points of a month, which are whole. |
| 123 | 25 | `public static void main(String[] args) throws Exception` |  |

### THE WALK (lines 149-202)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 2 | **type** `record Walk(int roads, double before, double at, boolean abroadBefore, boolean abroadAt, double thresholdAt...` | What a walk of single roads left: the roads added, the months queued at the last road not past the limit and at the first past it (the crossing), whether an order went abroad at each, the threshold at the crossing, wh... |
| 165 | 25 | `static Walk walk(Game g, double limit)` | Standing Paved Roads on the city, no month played: JUMP_SHORT roads under the model's own figure for `limit` months queued (ConstructionTrapCheck.roadsFor()), then one at a time until the months queued pass it. |
| 191 | 6 | `static void printWalk(String what, Walk w)` |  |
| 199 | 3 | `static double rateFor(BuildingManager bm, double share)` | The rate E at which the foreign crews' first share is `share`, from the base price now and the founding's: E = r p / (FOREIGN_EXCESS x_founding). |

### 1. A STRONG CURRENCY, UNDER THE KNEE (lines 203-225)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 20 | `static void strongCurrency(Game g)` |  |

### 2. TWO RATES PAST THE KNEE (lines 226-258)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 30 | `static void twoRates(Game g)` |  |

### 3. THE WEAKEST RATE (lines 259-278)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 17 | `static void weakestRate(Game g)` |  |

### 4. DOMESTIC INFLATION (lines 279-310)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 29 | `static void domesticInflation(Game g)` |  |

### 5. THE EXPORT (lines 311-388)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 75 | `static void theExport(Path root)` |  |

### 6. A 240-MONTH BACKLOG (lines 389-566)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 22 | `static Game backlog(Path root, String name, double months)` | A calm city with the landlords' Houses ordered on top of its own, enough that the queue at home is `months` of the sites (ConstructionTrapCheck.calm()'s own way of ordering them: the landlords given twice the quoted p... |
| 419 | 112 | `static void theBacklog(Path root)` |  |
| 533 | 33 | `static void afterTheFirstMonth(Game a, Game off)` | The city's workers after a's first month and the twin's, which had no foreign crews. |

