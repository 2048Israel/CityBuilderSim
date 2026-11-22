# CurveCheck.java - 897 lines · 42 methods · 18 constants · harnesses

`ham/citybuildersim/CurveCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
>      dial - at Standard and at Strict. (FS13: on a city whose inflation and expectation are
>      SET, the rule reading SHOCK over the target it expects, where Strict and Standard part.)
>   6. Every payment on its month: a term loan, a serial, a note and a dollar
>      term loan are worth the hand sum of their flows at their factors; the
>      solved yield reprices them flat to the cent; two 20-year loans with
>      different coupons yield differently on a steep curve.
>   7. The issue at the curve: on a steep city's curve the serial's booked
>      coupon prices its schedule at par, the term loan is worth its quoted
>      gross the moment it is booked, the note is worth at least what it raised.
>      (FS13: the steep city reads the target and expects it, its dial is held STEEP_GAP under
>      where that settles, its cash is set, and its control is the same dial held AT the settle.)
>   8. No money pump: issued and bought straight back, each kind, home and
>      abroad (the dollar legs where the window is open, and always on
>      ForeignDebtCheck's trading city, which is handed its exports and its
>      window asserted open: FS13), in the steep city and in a calm one,
>      the city is no richer to the rounding of its cash and the piece is gone.
>   9. BondMarket reads it: the market's benchmark is the city's curveRate().
>  10. The strike holds through the month and a save: the pair, the curve and
>      every piece's price survive a save and a load to the bit, and the pair
>      struck at a month's top is not moved by the month's end (FS13: the
>      month's end is made to move the expectation, by the smoothed inflation set).
>  11. The parts add up: the dial expected, the credit spread, the term
>      premium less the compression are the zero rate, in all four cities.
>  12. How often it fires in a real run: the months the 10-year sits under the
>      1-year and the months the dial is off the rule by more than a quarter
>      point, over 120 months of the playtest's founding (FS13: the pair is made
>      to move once, by the smoothed inflation set halfway).

**Uses:** [DebtManager](DebtManager.md) (39), [Game](Game.md) (31), [Debt](Debt.md) (19), [PriceLevel](PriceLevel.md) (12), [Expectations](Expectations.md) (9), [YieldCurve](YieldCurve.md) (9), [PriceIndex](PriceIndex.md) (3), [RestructureCheck](RestructureCheck.md) (3), [MonetaryCheck](MonetaryCheck.md) (2), [CentralBank](CentralBank.md) (2), [Sectors](Sectors.md) (2), [DebtQuote](DebtQuote.md) (2), [ForeignDebtCheck](ForeignDebtCheck.md) (2), [TempDirs](TempDirs.md) (1), [MoneyAudit](MoneyAudit.md) (1), [BondMarket](BondMarket.md) (1), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 151 | · fixtures |
| 327 | 1. calm prices as today |
| 359 | 2. hiking inverts |
| 395 | 3. behind is steep |
| 412 | 4. QE bends the long end |
| 432 | 5. the autopilot's gap is nothing |
| 508 | 6. every payment on its month |
| 563 | 7. the issue at the curve |
| 683 | 8. no money pump |
| 760 | 9. BondMarket reads it |
| 774 | 10. the strike holds through the month and a save |
| 824 | 11. the parts add up |
| 840 | 12. how often it fires in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 154 | `CurveCheck.TARGET` | `.02` | The target the study's cities aim at: the game's default, the figure the rule's neutral rate is struck on. |
| 157 | `CurveCheck.GDP_YEAR` | `5.4e9, TAX_YEAR = 1.1e9, DEBT = 3.4e9` | The study's economy and debt (spec section 3.3, debt-model.py): a year's output and a year's tax, in local money, and the term paper outstanding. |
| 160 | `CurveCheck.MONTHS` | `{ 3, 12, 24, 60, 120, 240, 360, 600 }` | The maturities section 1 asserts at, in months. |
| 163 | `CurveCheck.ROW` | `{ 1, 12, 24, 60, 120, 240, 360, 600 }` | Section 3.3's columns: 1 month, 1, 2, 5, 10, 20, 30 and 50 years. |
| 169 | `CurveCheck.CALM` | `new Study("calm",.03,.02,.80, 0, new double[] { 3.12, 3.12, 3.18, 3.35, 3.62,...` | The calm city: the dial at 3%, inflation at 2%, credibility .80, no holdings. |
| 172 | `CurveCheck.HIKING` | `new Study("hiking",.07,.06,.65, 0, new double[] { 7.12, 6.85, 6.50, 6.04, 6.0...` | ...the hiking city: the dial at 7% against inflation at 6%, credibility .65. |
| 175 | `CurveCheck.BEHIND` | `new Study("behind",.01,.05,.60, 0, new double[] { 1.12, 3.65, 4.46, 5.00, 5.3...` | ...the city behind: the dial at 1% against inflation at 5%, credibility .60. |
| 178 | `CurveCheck.QE` | `new Study("QE",.03,.02,.80,.25, new double[] { 3.12, 3.12, 3.15, 3.24, 3.37, ...` | ...and QE: the calm city with the central bank holding a quarter of the term paper. |
| 181 | `CurveCheck.CITIES` | `{ CALM, HIKING, BEHIND, QE }` | The four, in the study's order. |
| 283 | `CurveCheck.SEEN` | `4, RING = 5` | What the price index's save array carries of its ring (PriceIndex.toSaveArray()): the months it has seen at SEEN, then the WINDOW readings of the index from RING on, the last at (seen - 1) % WINDOW and the one a year ... |
| 286 | `CurveCheck.SHOCK` | `4 * Expectations.TOLERANCE` | How far over where the city expects to settle the inflation is set where a section needs the rule off its floor and Strict apart from Standard: the tolerance four times, past Strict's half point under the target and t... |
| 479 | `CurveCheck.RULE_FALLS_BY` | `.005` | The least the rule is expected to fall by, a fraction a year: half a point, which the shock (SHOCK over the target, at the rule's weight over one) is far over. |
| 566 | `CurveCheck.ASK` | `20_000 * PriceLevel.FACTOR` | What the issue sections borrow: small against what the founding city earns (its output is about 3,300 a month). |
| 569 | `CurveCheck.STEEP_GAP` | `.02` | How far under the dial the market expects to settle at the steep city's dial is held. |
| 572 | `CurveCheck.STEEP_10Y` | `.02` | How far over the 1-month the 10-year of the steep city's curve must stand, as a fraction a year (section 7 asks two points). |
| 575 | `CurveCheck.STEEP_CASH` | `10 * ASK` | What the steep city holds in cash, and what it can pay a buyback of any one piece it borrows out of: ten asks, over the three the fixture wants (FS13: set, as the played city's cash was a path's). |
| 686 | `CurveCheck.USD_ASK` | `1_000 * PriceLevel.FACTOR` | What the dollar legs borrow, in dollars. |
| 843 | `CurveCheck.SHOCKED_AT` | `60` | The press of the run (counting from its first, 0) before which the smoothed inflation is set over the target: halfway through its 120 months. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |
| 578 | `static double spanBeforeTheHold` | The steep city's 10-year over its 1-month with its dial held at where the market expects it to settle, no gap held: the condition's control (steepCity()). |
| 581 | `static boolean struckAtTheTarget` | Whether the pair struck on the steep city before its hold was the target and the target (steepCity(), section 7's fixture). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 823 | **type** `public class CurveCheck` | Proves the curve from the dial the market expects (CD1, spec-city-debt.md section 3): the expected dial's path, the zero rates it compounds to, the price of every payment at its own month's factor, and every issue and... |
| 81 | 4 | `static void check(String label, boolean ok)` |  |
| 86 | 10 | `static void close(String label, double actual, double expected, double tol)` |  |
| 98 | 10 | `static void closeRel(String label, double actual, double expected, double relTol)` | ...for a figure in money, where the tolerance is a share of it. |
| 110 | 9 | `static void same(String label, double actual, double expected)` | Two figures that must be the same number to the bit. |
| 120 | 4 | `static void quietly(Runnable r)` |  |
| 125 | 25 | `public static void main(String[] args) throws Exception` |  |

### fixtures (lines 151-326)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 1 | **type** `record Study(String name, double dial, double inflationNow, double credibility, double centralBankShare, do...` | The study's four cities: the dial, the inflation now, the credibility, the share of the term paper the central bank holds, and section 3.3's row in per cent. |
| 191 | 3 | `static double settle(Study s)` | Where a city expects inflation to settle: credibility x target + (1 - credibility) x pi_now. |
| 202 | 13 | `static DebtManager bare(Study s, boolean struck)` | A bare city of the study's: the target, the bank's money free so the floor is the dial, the economy and the debt the study priced, one domestic term loan carrying it (a quarter of it moved to the central bank in the Q... |
| 217 | 3 | `static double inflationIn(double now, double settle, double t)` | The inflation expected t months ahead: pi_inf + (pi_now - pi_inf) e^(-t / ADAPT_MONTHS), the spec's section 3.1. |
| 222 | 8 | `static double inflationFor(DebtManager dm, double rate)` | The inflation at which the city's own rule gives this rate: bisection on advisedPolicyRate(), which rises with inflation between the target and a rate of 100%. |
| 231 | 3 | `static String months(int m)` |  |
| 236 | 9 | `static void row(String who, DebtManager dm, double[] study)` | Prints the model's row beside the study's and asserts each point to the hundredth of a point. |
| 247 | 5 | `static void press(Game g)` | One press of a city, as the playtest steps it: a broke city steps rather than skips. |
| 253 | 3 | `static void pressQuietly(Game g)` |  |
| 258 | 3 | `static Game city(Path root, String label, int toMonth)` | The playtest's founding with the rule's hand on the dial, played on to this month. |
| 263 | 9 | `static Game city(Path root, String label, int toMonth, String...held)` | ...and with the sectors named held out of the investor's building from the founding's first month (BusinessInvestment.holdSector(), GW1: steepCity()'s fixture). |
| 274 | 3 | `static double inflationRead(Game g)` | The inflation the month's top reads: the year's, or the target before there is one (Game.strikeTheCurve()). |
| 294 | 7 | `static void setInflation(Game g, double inflation)` | THE INFLATION THE RULE READS, SET (FS13, 2026-11-20). |
| 306 | 7 | `static void setExpectation(Game g, double smoothed, double expected)` | ...AND WHAT THE CITY EXPECTS, SET (Expectations.toSaveArray(): credibility, smoothed inflation, expected inflation, ...). |
| 314 | 4 | `static Debt lastPiece(Game g)` |  |
| 319 | 7 | `static void cleanUp(Path root)` |  |

### 1. calm prices as today (lines 327-358)

| line | len | member | says |
|---:|---:|---|---|
| 328 | 30 | `static void calm()` |  |

### 2. hiking inverts (lines 359-394)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 34 | `static void hiking()` |  |

### 3. behind is steep (lines 395-411)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 15 | `static void behind()` |  |

### 4. QE bends the long end (lines 412-431)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 18 | `static void qe()` |  |

### 5. the autopilot's gap is nothing (lines 432-507)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 26 | `static void autopilot(Path root)` |  |
| 461 | 5 | `static void shock(Game c)` | The autopilot city's next press set up: the inflation the rule reads SHOCK over the target, and the city expecting the target (the pair the month's top strikes). |
| 468 | 9 | `static boolean strictnessMatters(Game c)` | Whether, at the inflation the price index reads now, Strict advises a rate more than a tenth of a point from Standard's, both off the ends of the dial. |
| 482 | 25 | `static void autopilotMonth(Game c, String at)` | One press of the autopilot city, read against what the month's top read just before it. |

### 6. every payment on its month (lines 508-562)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 6 | `static double handSum(Debt piece, IntToDoubleFunction factor)` |  |
| 516 | 46 | `static void pieces()` |  |

### 7. the issue at the curve (lines 563-682)

| line | len | member | says |
|---:|---:|---|---|
| 584 | 3 | `static double expectedToSettleAt(DebtManager dm)` | The dial the market expects, far out: the rule at the inflation it expects, where its path comes to rest (YieldCurve.expectedPath()). |
| 613 | 17 | `static Game steepCity(Path root, String label)` | A founded city with the rule's hand on the dial to month 38, then the dial held STEEP_GAP under the dial the market expects to settle at, by hand, and one press on: the market expects the dial to rise to where it sett... |
| 631 | 51 | `static void issueAtTheCurve(Game g)` |  |

### 8. no money pump (lines 683-759)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 14 | `static void noMoneyPump(Path root, Game steep) throws Exception` | today's figure, priced at the founding's level (EP1) |
| 703 | 9 | `static Game tradingCity(Path root) throws Exception` |  |
| 714 | 21 | `static void roundTrips(String city, Game g)` | Issues each kind and buys it straight back: the city ends no richer, the piece is off the books. |
| 736 | 23 | `static void roundTrip(String city, Game g, String kind, Runnable issue)` |  |

### 9. BondMarket reads it (lines 760-773)

| line | len | member | says |
|---:|---:|---|---|
| 761 | 12 | `static void bondMarket(Game g) throws Exception` |  |

### 10. the strike holds through the month and a save (lines 774-823)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 48 | `static void theStrike(Path root, Game g) throws Exception` |  |

### 11. the parts add up (lines 824-839)

| line | len | member | says |
|---:|---:|---|---|
| 825 | 14 | `static void theParts()` |  |

### 12. how often it fires in a real run (lines 840-897)

| line | len | member | says |
|---:|---:|---|---|
| 845 | 52 | `static void howOften(Path root)` |  |

