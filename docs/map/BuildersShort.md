# BuildersShort.java - 371 lines · 20 methods · 8 constants · model

`ham/citybuildersim/BuildersShort.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a
> premium that rises with the months of work queued at the pace the builders leave the sites - and, since FC2, the
> foreign crews always on offer beside them: their price for a point, the months past which the city's overflow goes
> to them, their month and what the city's spare points sell for abroad. Pure arithmetic: BuildingManager reads it for
> every price (orderPriceMultiplier(), nonMaterialPricePerPoint(), foreignPricePerPoint()), Game for the contractor's
> month (foreignCrewsOffer()), and the screens and the harnesses call these functions and never copy them.
> 
> WHY. Jerus's 2026-11-01 report on his own city: "in a sufficiently big city, on a specific sequence of events, a
> city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
> The month's repairs take the builders' points before the sites (Game.getBuildingOutput()), and when they take them
> all nothing advances, the builders' own depot waits in the same queue for points the repairs never leave
> (Construction.plan(): "the repairs take every crew it has"), and the price of a point did not read the queue, so the
> orders kept coming at the old price. Now the price reads it - every order is dearer for the work already queued.
> 
> WHY THE CONTRACTOR IS ALWAYS ON OFFER (FC2). FC1's contractor came only to a rescue: under FOREIGN_FLOOR (5%) of
> the builders' month left for the sites, at FOREIGN_PRICE (2.0) the city's price, a quarter of the month
> (FOREIGN_CAPACITY_SHARE), with no exchange rate in it. PW5's test town (2026-11-22) had its builders spend every
> point on upkeep a little over the floor, and nothing came. Jerus, 2026-11-22: "make it a imported/exported good, if
> currency is strong, it could be cheaper than regular construction, so make it so that the base cost is still in
> domestic currency but the excess is in foreign currency". So a foreign point costs p + E x (p the city's own base
> price, x an excess in the world's money at the world's price level, E the D$ the goods are struck at): an order's
> work goes abroad only where the queue's premium is over E x / p, which is nothing up to the knee, so the city's
> builders are never priced out; a strong D$ lowers the threshold and a weak one raises it, never past
> premium(KNEE_MONTHS + SURE_MONTHS) (Jerus: "at a certain premium they do go for it"). The overflow past it is the
> contractor's, whose month grows with the work so that the backlog settles at the threshold, at an excess that rises
> with the month he takes (a supply curve, under CEILING); and the builders sell abroad the points no site took
> while their queue is under the knee, at the world's base and their own excess (docs/queue/FC2.md).
> 
> AN ORDER'S OWN BUILDINGS (BA1). With no cap on orders (BusinessInvestment.MAX_CONCURRENT_ORDERS and MAX_ORDER_MONTHS
> went), an order of six hundred homes would take years of the queue at the price before it, every home at the price of
> the first. Now each of its buildings is priced on the queue the ones before it leave, as each order of a run already is
> (Game.buildRunInvoice()): an order of n pays the mean of the premium over the n queues it stands in
> (meanPremium(), multiplier()). One building is priced exactly as FC1 priced it.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (1)

**Used by (14):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [CityNeeds](CityNeeds.md), [Construction](Construction.md), [ConstructionControl](ConstructionControl.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [FitoutCheck](FitoutCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [Game](Game.md), [GapOrderCheck](GapOrderCheck.md), [InvestCheck](InvestCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 127 | THE FOREIGN CREWS, ALWAYS ON OFFER (FC2, 2026-11-22) |
| 299 | THE CHOICE TO FINISH SOONER (FC2b, 2026-11-24) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `BuildersShort.KNEE_MONTHS` | `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` | Months of work queued at the sites' pace at or under which a point carries no premium: the backlog the builders themselves expand at (BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING); three city-months in four in t... |
| 50 | `BuildersShort.HALF_MONTHS` | `24` | Months past the knee at which the premium is half its ceiling. |
| 53 | `BuildersShort.CEILING` | `0.8` | The most the premium adds to the price of a point's work, as a share of it: what an order pays when the builders have no points left for sites. |
| 81 | `BuildersShort.EXACT_SUM_MOST` | `64` | The largest order whose premium is the exact mean over its buildings (meanPremium()); a larger one reads the curve's integral, the same figure to the precision meanPremium() states. |
| 143 | `BuildersShort.FOREIGN_EXCESS` | `0.15` | The foreign crews' excess over a local builder's price for the same work at parity, as a share of the base, at the curve's first point: 15%. |
| 157 | `BuildersShort.SURE_MONTHS` | `12` | The months of backlog past KNEE_MONTHS at which the overflow goes abroad whatever the currency (N, Jerus's "at a certain premium they do go for it"): a year, the time an owner takes to tender work abroad and have the ... |
| 166 | `BuildersShort.SUPPLY_HALF` | `2.0` | The foreign crews' month, as a multiple of the builders' month at every post, at which their excess is half way from its first point to CEILING: twice the city's own trade. |
| 248 | `BuildersShort.AT_THRESHOLD` | `1e-9` | The months within which the queue at home reads as at the threshold (REV-FC2, the second review): an order that crosses it leaves the queue there to the last bits of a double, read back from the points (homeUnits()), ... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 334 | **type** `public final class BuildersShort` | The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a premium that rises with the months of work queued at the pace the builders leave the sites - and, since ... |
| 40 | 1 | `private BuildersShort()` |  |
| 59 | 7 | `public static double monthsQueued(double queuePoints, double sitesAtEveryPost)` | The months of work queued at the pace the sites get: queue over sites. |
| 72 | 7 | `public static double premium(double months)` | What a point's work carries over its price at that many months queued, as a share of it: none at or under KNEE_MONTHS, then CEILING * x^2 / (1 + x^2) for x the months past the knee over HALF_MONTHS - no slope at the k... |
| 98 | 17 | `public static double meanPremium(double fromMonths, double stepMonths, long n)` | The mean premium over an order of {@code n} buildings, each priced on the queue the ones before it leave (BA1): the building i (from 0) stands in {@code fromMonths + i * stepMonths} months queued, where {@code stepMon... |
| 117 | 5 | `private static double antiderivative(double months)` | The area under premium() from the knee to that many months queued; nothing at or under the knee. |
| 123 | 3 | `private static double clamp(double premium)` |  |

### THE FOREIGN CREWS, ALWAYS ON OFFER (FC2, 2026-11-22) (lines 127-298)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 1 | `public static double capShare()` | The cap on the foreign crews' excess share at the curve's first point: the premium at SURE_MONTHS past the knee. |
| 177 | 5 | `public static double excessShare(double worldPricePerPoint, double basePricePerPoint)` | The foreign crews' excess share at the curve's first point: E x / p, x FOREIGN_EXCESS of the world's base price for the work, so FOREIGN_EXCESS times {@code worldPricePerPoint} (E times that base, in the city's money)... |
| 188 | 5 | `public static double exportShare(double worldPricePerPoint, double basePricePerPoint)` | ...and the city's own excess when its builders sell a point abroad, as a share of the world's base, by the same rule turned round: FOREIGN_EXCESS of the city's base price p in the city's money, over E times the world'... |
| 200 | 5 | `public static double thresholdMonths(double r)` | The months queued at the city's own pace past which an order's work goes abroad: where premium() reaches the share {@code r} - KNEE_MONTHS + HALF_MONTHS sqrt(r / (CEILING - r)), premium() turned round. |
| 210 | 5 | `public static double overflow(double queueAtHome, double sitesAtEveryPost, double thresholdMonths)` | The queue's work past the threshold, in points: what the city's own builders hold over {@code thresholdMonths} of their month for the sites ({@code sitesAtEveryPost}), nothing under it. |
| 222 | 5 | `public static double foreignMonth(double work, double thresholdMonths, double floorPoints)` | The foreign crews' month, in points, for {@code work} on their book: enough crews to build it in the threshold's months - so the work abroad waits no longer than the work at home at the threshold, and the backlog read... |
| 234 | 8 | `public static double excessAt(double firstShare, double foreignMonth, double buildersMonth)` | The foreign crews' excess share at {@code foreignMonth} points a month (the supply curve): {@code firstShare} (the curve's first point, excessShare()) and the rest of the way to CEILING times m / (1 + m), m their mont... |
| 262 | 7 | `public static double homeUnits(double fromMonths, double stepMonths, long n, double thresholdMonths)` | How much of an order of {@code n} buildings (at least one) stands within {@code thresholdMonths}, in buildings (REV-FC2, the second review): the building i (from 0) takes the months {@code fromMonths + i * stepMonths}... |
| 279 | 14 | `public static double meanShare(double fromMonths, double stepMonths, long n, double thresholdMonths, double foreignShare)` | The mean share an order of {@code n} buildings carries over its base price with the foreign crews on offer: the building i (from 0) stands in {@code fromMonths + i * stepMonths} months queued (meanPremium()); its work... |
| 295 | 3 | `public static double multiplier(double fromMonths, double stepMonths, long n, double thresholdMonths, double foreignShare)` | ...and the multiple an order's non-material price is struck at: one and meanShare(). |

### THE CHOICE TO FINISH SOONER (FC2b, 2026-11-24) (lines 299-371)

| line | len | member | says |
|---:|---:|---|---|
| 316 | 8 | `public static double sitePace(double points, double thresholdMonths, double floorPoints, double capPoints)` | The foreign crews' month on one site handed them, in points: the site's points over {@code thresholdMonths} (FC2's own rule for his book: the work abroad waits no longer than the work at home at the threshold), never ... |
| 330 | 5 | `public static double monthsAbroad(double points, double pace)` | The whole months the foreign crews take to build {@code points} at {@code pace} a month: the ceiling of their quotient (a ten-billionth over is not another month), at least one for any points. |
| 346 | 12 | `public static double broughtForward(double perMonth, double monthlyRate, double monthsAbroad, double monthsHome)` | What a stream of {@code perMonth} from the month it starts is worth today when its start comes forward from {@code monthsHome} to {@code monthsAbroad}, discounted month by month at {@code monthlyRate} (BA1's carry (1 ... |
| 367 | 4 | `public static double interestSaved(double cost, double monthlyRate, double monthsAbroad, double monthsHome)` | The interest on the months saved: broughtForward(cost * i, i, Wa, Wh) - the interest a cost carries a month while the site stands unbuilt, for each month it comes sooner, discounted as broughtForward() does. |

