# YieldCurve.java - 278 lines · 19 methods · 2 constants · model

`ham/citybuildersim/YieldCurve.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The city's curve: a discount factor for every month to fifty years, built
> from the dial the market expects, the credit spreads, and the shape the term
> premium and the central bank's holdings give the long end (CD1,
> docs/studies/spec-city-debt.md section 3). Pure arithmetic - no game, no
> screen - so a fixture can build one from numbers and DebtManager builds one
> from the city.
> 
> WHY. The curve this replaced sat on the dial in parallel: DebtManager's
> priceAt() was the floor plus the two credit spreads plus a premium that
> depended on the months and on nothing else, so a city at 7% on the dial
> with prices rising 6% had a 50-year rate 1.6 points over its 10-year, and a
> city holding the dial at 1% by hand against 5% inflation sold fifty-year
> money at 2.62% (spec section 1.4). And marketValue() discounted a piece's
> whole schedule at ONE flat rate, the curve read at its last month, so the
> yield The book printed was the rate it had been handed: solving a schedule
> for the yield that gives back the price it was priced at returns the rate it
> was priced at (spec section 1.2). Here every payment is discounted at its
> own month's factor, and a piece's yield is a result.
> 
> THE CONSTRUCTION. The dial the market expects, month by month (p, from
> expectedPath()), plus today's credit spreads, clamped at the ceiling and the
> floor of rates, is the forward rate f(t) for month t. Compounded over the
> months to m it is the zero rate before the term shape, z0(m); the term
> premium less the compression is added on top (the shape, supplied by the
> caller); and the discount factor is d(m) = (1 + z(m)/12)^(-m). Month 0 is
> this month, so f(0) is the short rate the city is quoted today, to the bit.

**Uses:** [DebtManager](DebtManager.md) (2), [Expectations](Expectations.md) (1)

**Used by (8):** [AuctionCheck](AuctionCheck.md), [CallCheck](CallCheck.md), [CallTree](CallTree.md), [CurveCheck](CurveCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md)

## Sections

| line | section |
|---:|---|
| 61 | · the paths |
| 94 | · building one |
| 178 | · reads |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 39 | `YieldCurve.DRIFT_MONTHS` | `6` | The time constant of the dial's expected return to the rule, in months (D15, Jerus's to move): the gap between where the dial stands and where the rule would put it closes by a factor of e in this many. |
| 42 | `YieldCurve.HORIZON` | `600` | The months the curve is built to: fifty years, the longest term loan; the curve holds its last zero rate past it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private final double[] path` | The expected dial p(t), t = 0 .. |
| 47 | `private final double[] forward` | The forward f(t) = credit-spread-laden, clamped. |
| 49 | `private final double[] baseZero` | z0(m), m = 0 .. |
| 51 | `private final double[] shape` | The shape the caller gave, m = 0 .. |
| 53 | `private final double[] zero` | z(m) = z0(m) + shape(m), m = 0 .. |
| 55 | `private final double[] factor` | d(m), m = 0 .. |
| 57 | `private final double[] annuity` | The running sum of d(1..m), m = 0 .. |
| 58 | `private final double effectiveSpread` |  |
| 59 | `private final boolean flat` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 243 | **type** `public final class YieldCurve` | The city's curve: a discount factor for every month to fifty years, built from the dial the market expects, the credit spreads, and the shape the term premium and the central bank's holdings give the long end (CD1, do... |

### the paths (lines 61-93)

| line | len | member | says |
|---:|---:|---|---|
| 76 | 10 | `public static double[] expectedPath(double floor, DoubleUnaryOperator rule, double inflationNow, double expectedInflation)` | The dial the market expects, month by month: p(t) = R(t) + (floor - R(0)) x e^(-t / DRIFT_MONTHS), where R(t) is the rule at the inflation expected t months ahead, pi(t) = pi_inf + (pi_now - pi_inf) x e^(-t / Expectat... |
| 88 | 5 | `public static double[] flatPath(double floor)` | A dial that never moves: the floor every month. |

### building one (lines 94-177)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 40 | `public YieldCurve(double[] path, double gdpSpread, double revenueSpread, double ceilingNow, IntToDoubleFunction shape)` | The curve from a path, the two credit spreads, the rate the two cannot pass, and the shape. |
| 161 | 3 | `public static double shortRate(double floor, double gdpSpread, double revenueSpread, double ceilingNow)` | The short rate the city is quoted: the floor and the two credit spreads added in that order, held between the cheapest money (DebtManager.MIN_RATE) and the ceiling. |
| 166 | 4 | `public static YieldCurve of(double[] path, double spreads, double ceilingGap, IntToDoubleFunction shape)` | A curve from a path, one number for the credit spreads and the room between the path's first month and the ceiling - the fixture's way in; DebtManager hands the two spreads and the ceiling to the constructor. |
| 172 | 5 | `private static double discount(double z, int m)` | (1 + z/12)^(-m); a rate that discounts nothing (as Debt.presentValue treats one) gives 1. |

### reads (lines 178-278)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 3 | `public double path(int t)` | The dial expected in month t ahead (0 this month); clamped to the path. |
| 186 | 3 | `public double forward(int t)` | The forward rate for month t ahead (0 this month, the short rate); clamped to the path. |
| 191 | 3 | `public double zero(int m)` | The zero rate for m months, a nominal annual rate: the compounded forwards plus the shape. |
| 196 | 3 | `public double baseZero(int m)` | The compounded forwards over m months, before the shape (z0). |
| 201 | 3 | `public double shape(int m)` | The shape at m months: the term premium less the compression the caller gave (0 up to a year and at 0). |
| 206 | 5 | `public double factor(int m)` | The factor a payment m months away is discounted by: (1 + z(m)/12)^(-m); 1 now; past fifty years at the last zero rate. |
| 213 | 6 | `public double pv(double[] flows)` | The present value of flows paid at months 1, 2, ...: each at its own month's factor. |
| 228 | 6 | `public double pvAtSpread(double[] flows, double spread)` | The present value of flows paid at months 1, 2, ... |
| 242 | 4 | `public double factorAtSpread(int m, double spread)` | The factor a payment m months away is discounted by with a spread added to its zero rate: (1 + (zero(m) + spread)/12)^(-m); 1 now. |
| 248 | 3 | `public double effectiveSpread()` | The spread the forward actually carries this month: the two credit spreads, or the room under the ceiling if less. |
| 258 | 3 | `public double expectedDialOver(int m)` | The dial the market expects, averaged over m months, and nothing else in the rate: z0(m) less the spread. |
| 267 | 6 | `public double parYield(int months)` | The annual coupon a monthly-paying bullet of this term needs to price at par on this curve: (1 - d(n)) / (the sum of d(1..n) / 12). |
| 275 | 3 | `public boolean isFlat()` | True when every forward is the same number: the path is flat (a city on target with the dial at the rule, or one nobody struck), and the curve is the old parallel one. |

