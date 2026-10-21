# CurveCheck.java - 728 lines · 36 methods · 11 constants · harnesses

`ham/citybuildersim/CurveCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> Proves the curve from the dial the market expects (CD1, spec-city-debt.md
> section 3): the expected dial's path, the zero rates it compounds to, the
> price of every payment at its own month's factor, and every issue and
> buyback priced off the same curve. Not part of the game.
> 
> WHY THIS EXISTS. Until CD1 the city's curve sat on the dial in parallel
> (a premium that depended on the months and on nothing else) and a piece was
> priced at ONE flat rate, so the yield The book printed was the rate it had
> been handed and a hand on the dial bought cheap fifty-year money (spec
> section 1.2 and 1.4). The curve now drifts the dial to the rule over
> YieldCurve.DRIFT_MONTHS at the inflation the city expects, and each payment
> is discounted on its own month. Every claim of that design is a recurrence
> or a sum with a constant in it, so each is asserted against its constant on
> a fixture that causes the condition: the study's four cities (section 3.3)
> as bare DebtManagers, and Game cities for the month, the quotes, the save.
> 
> What it has to prove, a section each:
> 
>   1. Calm prices as today: on target with the dial at neutral the expected
>      dial is the dial at every month, the zero rate is the short rate plus
>      the term premium less the compression, section 3.3's calm row to the
>      hundredth, and a manager nobody has struck prices the same flat curve.
>   2. Hiking inverts: the gap between the dial and the rule closes by e in
>      DRIFT_MONTHS, the inflation expected closes by e in ADAPT_MONTHS and
>      settles on the rule at where it settles, the 10-year sits under the
>      1-year, the short end is unmoved, and the hiking row.
>   3. Behind is steep: the 1-year over the 1-month by more than two points,
>      the 10-year three points over the old parallel curve's, the row.
>   4. QE bends the long end: a quarter of the term paper held takes its
>      share of the term premium off the long rates and nothing off the short.
>   5. The autopilot's gap is nothing: in a Game city on the rule, the struck
>      pair is what the month's top read, the rule at the struck inflation IS
>      the dial, and the only gap expected to close is the floor over the
>      dial - at Standard and at Strict.
>   6. Every payment on its month: a term loan, a serial, a note and a dollar
>      term loan are worth the hand sum of their flows at their factors; the
>      solved yield reprices them flat to the cent; two 20-year loans with
>      different coupons yield differently on a steep curve.
>   7. The issue at the curve: on a steep city's curve the serial's booked
>      coupon prices its schedule at par, the term loan is worth its quoted
>      gross the moment it is booked, the note is worth at least what it raised.
>   8. No money pump: issued and bought straight back, each kind, home and
>      abroad (the dollar legs where the window is open, and always on
>      ForeignDebtCheck's trading city), in the steep city and in a calm one,
>      the city is no richer and the piece is gone.
>   9. BondMarket reads it: the market's benchmark is the city's curveRate().
>  10. The strike holds through the month and a save: the pair, the curve and
>      every piece's price survive a save and a load to the bit, and the pair
>      struck at a month's top is not moved by the month's end.
>  11. The parts add up: the dial expected, the credit spread, the term
>      premium less the compression are the zero rate, in all four cities.
>  12. How often it fires in a real run: the months the 10-year sits under the
>      1-year and the months the dial is off the rule by more than a quarter
>      point, over 120 months of the playtest's founding.

**Uses:** [DebtManager](DebtManager.md) (32), [Game](Game.md) (26), [Debt](Debt.md) (19), [YieldCurve](YieldCurve.md) (8), [Expectations](Expectations.md) (7), [MonetaryCheck](MonetaryCheck.md) (2), [CentralBank](CentralBank.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignDebtCheck](ForeignDebtCheck.md) (1), [RestructureCheck](RestructureCheck.md) (1), [BondMarket](BondMarket.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 145 | · fixtures |
| 275 | 1. calm prices as today |
| 307 | 2. hiking inverts |
| 343 | 3. behind is steep |
| 360 | 4. QE bends the long end |
| 380 | 5. the autopilot's gap is nothing |
| 420 | 6. every payment on its month |
| 475 | 7. the issue at the curve |
| 544 | 8. no money pump |
| 609 | 9. BondMarket reads it |
| 623 | 10. the strike holds through the month and a save |
| 673 | 11. the parts add up |
| 689 | 12. how often it fires in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 148 | `CurveCheck.TARGET` | `.02` | The target the study's cities aim at: the game's default, the figure the rule's neutral rate is struck on. |
| 151 | `CurveCheck.GDP_YEAR` | `5.4e9, TAX_YEAR = 1.1e9, DEBT = 3.4e9` | The study's economy and debt (spec section 3.3, debt-model.py): a year's output and a year's tax, in local money, and the term paper outstanding. |
| 154 | `CurveCheck.MONTHS` | `{ 3, 12, 24, 60, 120, 240, 360, 600 }` | The maturities section 1 asserts at, in months. |
| 157 | `CurveCheck.ROW` | `{ 1, 12, 24, 60, 120, 240, 360, 600 }` | Section 3.3's columns: 1 month, 1, 2, 5, 10, 20, 30 and 50 years. |
| 162 | `CurveCheck.CALM` | `new Study("calm",.03,.02,.80, 0, new double[] { 3.12, 3.12, 3.18, 3.35, 3.62,...` |  |
| 164 | `CurveCheck.HIKING` | `new Study("hiking",.07,.06,.65, 0, new double[] { 7.12, 6.85, 6.50, 6.04, 6.0...` |  |
| 166 | `CurveCheck.BEHIND` | `new Study("behind",.01,.05,.60, 0, new double[] { 1.12, 3.65, 4.46, 5.00, 5.3...` |  |
| 168 | `CurveCheck.QE` | `new Study("QE",.03,.02,.80,.25, new double[] { 3.12, 3.12, 3.15, 3.24, 3.37, ...` |  |
| 170 | `CurveCheck.CITIES` | `{ CALM, HIKING, BEHIND, QE }` |  |
| 478 | `CurveCheck.ASK` | `20_000` | What the issue sections borrow: small against what the founding city earns (its output is about 3,300 a month). |
| 547 | `CurveCheck.USD_ASK` | `1_000` | What the dollar legs borrow, in dollars. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `static int fails` |  |
| 72 | `static PrintStream out` |  |
| 73 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 69 | 660 | **type** `public class CurveCheck` | Proves the curve from the dial the market expects (CD1, spec-city-debt.md section 3): the expected dial's path, the zero rates it compounds to, the price of every payment at its own month's factor, and every issue and... |
| 75 | 4 | `static void check(String label, boolean ok)` |  |
| 80 | 10 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 10 | `static void closeRel(String label, double actual, double expected, double relTol)` | ...for a figure in money, where the tolerance is a share of it. |
| 104 | 9 | `static void same(String label, double actual, double expected)` | Two figures that must be the same number to the bit. |
| 114 | 4 | `static void quietly(Runnable r)` |  |
| 119 | 25 | `public static void main(String[] args) throws Exception` |  |

### fixtures (lines 145-274)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 1 | **type** `record Study(String name, double dial, double inflationNow, double credibility, double centralBankShare, do...` | The study's four cities: the dial, the inflation now, the credibility, the share of the term paper the central bank holds, and section 3.3's row in per cent. |
| 180 | 3 | `static double settle(Study s)` | Where a city expects inflation to settle: credibility x target + (1 - credibility) x pi_now. |
| 191 | 13 | `static DebtManager bare(Study s, boolean struck)` | A bare city of the study's: the target, the bank's money free so the floor is the dial, the economy and the debt the study priced, one domestic term loan carrying it (a quarter of it moved to the central bank in the Q... |
| 206 | 3 | `static double inflationIn(double now, double settle, double t)` | The inflation expected t months ahead: pi_inf + (pi_now - pi_inf) e^(-t / ADAPT_MONTHS), the spec's section 3.1. |
| 211 | 8 | `static double inflationFor(DebtManager dm, double rate)` | The inflation at which the city's own rule gives this rate: bisection on advisedPolicyRate(), which rises with inflation between the target and a rate of 100%. |
| 220 | 3 | `static String months(int m)` |  |
| 225 | 9 | `static void row(String who, DebtManager dm, double[] study)` | Prints the model's row beside the study's and asserts each point to the hundredth of a point. |
| 236 | 5 | `static void press(Game g)` | One press of a city, as the playtest steps it: a broke city steps rather than skips. |
| 242 | 3 | `static void pressQuietly(Game g)` |  |
| 247 | 9 | `static Game city(Path root, String label, int toMonth)` | The playtest's founding with the rule's hand on the dial, played on to this month. |
| 258 | 3 | `static double inflationRead(Game g)` | The inflation the month's top reads: the year's, or the target before there is one (Game.strikeTheCurve()). |
| 262 | 4 | `static Debt lastPiece(Game g)` |  |
| 267 | 7 | `static void cleanUp(Path root)` |  |

### 1. calm prices as today (lines 275-306)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 30 | `static void calm()` |  |

### 2. hiking inverts (lines 307-342)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 34 | `static void hiking()` |  |

### 3. behind is steep (lines 343-359)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 15 | `static void behind()` |  |

### 4. QE bends the long end (lines 360-379)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 18 | `static void qe()` |  |

### 5. the autopilot's gap is nothing (lines 380-419)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 15 | `static void autopilot(Path root)` |  |
| 398 | 21 | `static void autopilotMonth(Game c, String at)` | One press of the autopilot city, read against what the month's top read just before it. |

### 6. every payment on its month (lines 420-474)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 6 | `static double handSum(Debt piece, IntToDoubleFunction factor)` |  |
| 428 | 46 | `static void pieces()` |  |

### 7. the issue at the curve (lines 475-543)

| line | len | member | says |
|---:|---:|---|---|
| 487 | 8 | `static Game steepCity(Path root, String label)` | A founded city with the rule's hand on the dial to month 38, then the dial held two points under the rule by hand and one press on: the market expects the dial to rise to the rule, so the curve is steep. |
| 496 | 47 | `static void issueAtTheCurve(Game g)` |  |

### 8. no money pump (lines 544-608)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 7 | `static void noMoneyPump(Path root, Game steep) throws Exception` |  |
| 557 | 9 | `static Game tradingCity(Path root) throws Exception` |  |
| 568 | 18 | `static void roundTrips(String city, Game g)` | Issues each kind and buys it straight back: the city ends no richer, the piece is off the books. |
| 587 | 21 | `static void roundTrip(String city, Game g, String kind, Runnable issue)` |  |

### 9. BondMarket reads it (lines 609-622)

| line | len | member | says |
|---:|---:|---|---|
| 610 | 12 | `static void bondMarket(Game g) throws Exception` |  |

### 10. the strike holds through the month and a save (lines 623-672)

| line | len | member | says |
|---:|---:|---|---|
| 624 | 48 | `static void theStrike(Path root, Game g) throws Exception` |  |

### 11. the parts add up (lines 673-688)

| line | len | member | says |
|---:|---:|---|---|
| 674 | 14 | `static void theParts()` |  |

### 12. how often it fires in a real run (lines 689-728)

| line | len | member | says |
|---:|---:|---|---|
| 690 | 38 | `static void howOften(Path root)` |  |

