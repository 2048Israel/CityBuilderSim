# CallTree.java - 330 lines · 18 methods · 11 constants · model

`ham/citybuildersim/CallTree.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> What a callable bond is worth, and what the right to call it costs: a Ho-Lee
> short-rate tree on monthly steps, fitted to the city's curve (CD4,
> docs/studies/spec-city-debt.md section 2.6; D22, D23, D24). Pure arithmetic -
> no game, no screen - so a fixture can price a call from a hand-built
> YieldCurve and Game and DebtManager price one from the city's.
> 
> WHY. Until CD4 the city could not sell callable paper at all; the only way to
> close a line early was to buy it back at the market's price, which a
> treasury that sold at 4% and now borrows at 2% does at a premium. A call is a
> right the city buys at the sale and pays for in the coupon, and what it is
> worth depends on how far rates can move and on the shape of the curve, so a
> flat add-on (the same points for a calm city and a volatile one, for a
> 10-year and a 30-year) would be wrong in every city but one. The tree is the
> smallest model that prices it from the curve the game already has and from
> one measured number, the volatility of the city's own rate.
> 
> THE TREE. At node (i, j) - i months on, j of the steps up - the short rate
> is r(i, j) = theta(i) + sigma x sqrt(dt) x (2j - i), each step up or down
> with probability a half, and a month's discount at a node is 1 / (1 + r /
> 12), the curve's own convention (YieldCurve.factor(m) = (1 + z(m)/12)^-m).
> theta(i) is fitted one step at a time by forward induction on Arrow-Debreu
> prices (the price today of a unit paid at one node): each theta is the one
> that makes the tree's price of a zero-coupon payment i + 1 months away the
> curve's factor(i + 1), to rounding. So a bond with no call prices on the
> tree exactly as on the curve - the check that the tree is the curve - and
> the call is the only thing the tree adds. Backward through the tree, from the
> first call month on, the city calls whenever the bond is worth more than the
> call price: the value at a node is the smaller of what the bond is worth
> going on and the call price.

**Uses:** [YieldCurve](YieldCurve.md) (6), [LongTermBond](LongTermBond.md) (1), [Auction](Auction.md) (1)

**Used by (4):** [CallCheck](CallCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md)

## Sections

| line | section |
|---:|---|
| 40 | THE DIALS (spec-city-debt.md section 9, D22-D24) |
| 77 | WHICH BONDS MAY BE CALLABLE, AND FROM WHEN |
| 95 | THE VOLATILITY (D23) |
| 128 | THE TREE |
| 250 | PRICING ON IT |
| 310 | THE STATIC FORMS: a curve and a volatility, a tree fitted for the job |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `CallTree.RATE_VOL_FLOOR` | `0.0075` | The least volatility of the short rate the tree is priced at, a year (D23): 0.75 points, so a calm city that has hardly moved its rate still pays for the option. |
| 48 | `CallTree.VOL_MONTHS` | `60` | How many months of the city's own rate the volatility is measured over (D23): five years of monthly changes. |
| 51 | `CallTree.VOL_MIN_CHANGES` | `12` | The fewest monthly changes the volatility is measured from: under a year's worth (13 months of the rate) the city has no history worth the name and prices at the floor. |
| 54 | `CallTree.FIRST_CALL_10Y` | `5` | A callable 10-year may first be called this many years after it is sold (D22). |
| 57 | `CallTree.FIRST_CALL_LONG` | `10` | ...and a callable 20-, 30- or 50-year this many (D22). |
| 60 | `CallTree.CALLABLE_MIN_YEARS` | `10` | The shortest term a bond may be sold callable at, in years (D22, Jerus: "any bond of ten years or more"). |
| 63 | `CallTree.CALL_PRICE` | `100` | What a call pays, per 100 of the principal called: par (D22). |
| 66 | `CallTree.WORTH_CALLING` | `0.03` | A call is worth making when the present value it saves passes this share of the face (D24): 3%, the threshold US municipal refunding practice uses. |
| 69 | `CallTree.REFUSAL` | `"Only bonds of 10 years or more can be sold callable, at 10, 20, 30 or 50 yea...` | What the treasury is told when it asks for a callable bond that is not one of the terms that may be. |
| 72 | `CallTree.COUPON_TOLERANCE` | `1e-12` | The bisection finds a coupon to a hundred-millionth of a point: far under the eighth of a point a coupon is rounded to. |
| 75 | `CallTree.MAX_FIT_STEPS` | `80` | The most Newton steps the fit of one theta takes before it settles for the bisection's bracket. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 133 | `private final int steps` | The tree's months. |
| 135 | `private final double sigma` | The volatility the tree was fitted at, a year. |
| 137 | `private final double[][] rate` | rate[i][j]: the short rate at node (i, j), i = 0 .. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 293 | **type** `public final class CallTree` | What a callable bond is worth, and what the right to call it costs: a Ho-Lee short-rate tree on monthly steps, fitted to the city's curve (CD4, docs/studies/spec-city-debt.md section 2.6; D22, D23, D24). |

### THE DIALS (spec-city-debt.md section 9, D22-D24) (lines 40-76)

### WHICH BONDS MAY BE CALLABLE, AND FROM WHEN (lines 77-94)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 3 | `public static boolean canBeCallable(int years)` | True for a term a bond may be sold callable at: one of the issuable terms, ten years or more. |
| 90 | 4 | `public static int firstCallYears(int years)` | Years from the sale to the first call (D22): 5 for a 10-year, 10 for a 20-, 30- or 50-year; -1 for a term that may not be callable. |

### THE VOLATILITY (D23) (lines 95-127)

| line | len | member | says |
|---:|---:|---|---|
| 109 | 18 | `public static double volatility(double[] rates)` | The volatility of the short rate, a year, measured from a history of it: the standard deviation (of the sample) of the last VOL_MONTHS monthly changes, times the square root of twelve, and never under RATE_VOL_FLOOR. |

### THE TREE (lines 128-249)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 5 | `private CallTree(int steps, double sigma, double[][] rate)` |  |
| 146 | 3 | `public int steps()` | The months the tree reaches: the longest schedule it prices. |
| 151 | 3 | `public double sigma()` | The volatility it was fitted at, a year. |
| 156 | 3 | `public double rateAt(int i, int j)` | The short rate at node (i, j), a year: theta(i) plus the step times (2j - i). |
| 161 | 3 | `public static CallTree fit(YieldCurve curve, double sigma, int steps)` | A tree fitted to the curve, for a bond of this many months, at this volatility. |
| 170 | 3 | `public static CallTree fit(YieldCurve curve, double spread, double sigma, int steps)` | ...fitted to the curve with a spread added to every month's zero rate (YieldCurve.factorAtSpread()): what a sale that cleared a tail over fair is priced on, exactly as the non-call bond's is (YieldCurve.pvAtSpread()). |
| 183 | 27 | `public static CallTree fit(IntToDoubleFunction factor, double sigma, int steps)` | A tree fitted to a discount factor for every month: factor(m) for m = 1 .. |
| 221 | 22 | `private static double solveTheta(double[] q, double[] shape, double target, double guess, double lowestShape)` | The theta that prices a zero-coupon payment a month further out at the target: the root of f(theta) = sum q_j / (1 + (theta + shape_j)/12) - target, which falls as theta rises. |
| 244 | 5 | `private static double sumAt(double[] q, double[] shape, double theta)` |  |

### PRICING ON IT (lines 250-309)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 18 | `public double price(double[] flows, int firstCallAhead, double callPrice)` | What flows paid at months 1, 2, ... |
| 287 | 5 | `public double zeroCouponPrice(int m)` | What the tree prices a single unit paid m months from now at, no call: the zero-coupon price, which is the curve's factor(m) it was fitted to. |
| 299 | 10 | `public double optionSpread(YieldCurve curve, int months, int firstCallAhead)` | The coupon a monthly-paying bullet of this many months needs to be worth its call price - par - with the call from firstCallAhead on, less the coupon it needs with none (the curve's par yield): what the option costs t... |

### THE STATIC FORMS: a curve and a volatility, a tree fitted for the job (lines 310-330)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 3 | `public static double price(YieldCurve curve, double sigma, double[] flows, int firstCallAhead, double callPrice)` | price(), on a tree fitted to the curve at this volatility for just these flows. |
| 320 | 4 | `public static double price(YieldCurve curve, double spread, double sigma, double[] flows, int firstCallAhead, double callPrice)` | ...on the curve with a spread added to every month's zero rate. |
| 326 | 4 | `public static double optionSpread(YieldCurve curve, double sigma, int months, int firstCallAhead)` | optionSpread(), on a tree fitted to the curve at this volatility for a bond of this many months. |

