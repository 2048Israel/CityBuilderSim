# FloatingRateNote.java - 218 lines · 20 methods · 2 constants · model

`ham/citybuildersim/FloatingRateNote.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> A floating-rate note: a coupon every month on the whole face, the month's
> policy rate plus a margin fixed at the sale, and the face at the end - at 2
> or 5 years, each sale its own line (CD5, spec-city-debt.md section 2.4).
> 
> WHY: a fixed bond hands the dial's risk to the lender, and a lender who
> fears the dial moving charges for it in the coupon. The treasury that wants
> to carry the dial's risk itself sells paper whose coupon follows it - the US
> Treasury has sold floaters on its own bill rate since 2014 - and here the
> index is the dial the player moves (Jerus: "floating-rate notes on the
> policy rate"). The margin is what prices the note at 100 on the curve (the
> term premium and the credit spread its life carries, and the gap between
> the expected dial and the floor), so the note sells near par whatever the
> dial does, and the cost of the credit is whatever the dial then does to the
> coupon. Nothing sells one unasked: the default playtest holds none, and a
> city that never sells one has every figure it had.
> 
> THE BOOKS (spec section 5.3): the effective rate resets with the coupon, as
> a floating liability's does. The schedule the books use is THE KNOWN ONE -
> today's coupon held flat every month and the face at the end (IFRS 9
> B5.4.5's convention for a floating liability) - and when the dial has moved
> DebtManager hands the new one to reset(), which solves the effective rate
> again on the carrying value as it stands. No catch-up: the carrying value
> does not jump, the rate that accretes it changes. The MARKET's schedule is
> another thing - the coupons at the dial it expects - and is
> projectedFlows(); it prices the note (DebtManager.pricingFlows()) and the
> buyback offers on it.
> 
> Domestic paper only: there are no dollar floaters (section 2.7).

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (6):** [BookDesk](BookDesk.md), [DebtManager](DebtManager.md), [DeskCheck](DeskCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [Game](Game.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `FloatingRateNote.MATURITIES` | `{ 2, 5 }` | The only terms a floating-rate note is issued at, in years (D25). |
| 41 | `FloatingRateNote.REFUSAL` | `"Floating-rate notes are issued at 2 or 5 years."` | What the treasury is told when it asks for any other term. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 59 | `private double margin` | The margin over the policy rate, a year, fixed at the sale; it may be nothing or below. |
| 62 | `private double index` | The policy rate, a year, as the last reset read it: the dial the coupon now follows. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 35 | 184 | **type** `public class FloatingRateNote extends Debt` | A floating-rate note: a coupon every month on the whole face, the month's policy rate plus a margin fixed at the sale, and the face at the end - at 2 or 5 years, each sale its own line (CD5, spec-city-debt.md section ... |
| 44 | 4 | `public static boolean isIssuable(int years)` | True for one of the two. |
| 50 | 7 | `public static int nearestYears(int years)` | The one of MATURITIES nearest this many years; the shorter on a tie. |
| 68 | 11 | `public FloatingRateNote(double faceValue, int months, int monthStarted, double margin, double index)` |  |
| 81 | 3 | `public double getMargin()` | The margin over the policy rate, a year. |
| 86 | 3 | `public double getIndex()` | The policy rate, a year, as the last reset read it. |
| 91 | 3 | `public double couponRate()` | The coupon, a year, at the dial as the last reset read it: the index and the margin, never under nothing. |
| 96 | 3 | `public static double couponAt(double policyRate, double margin)` | A floater's coupon, a year, at a dial and a margin: their sum, never under nothing. |
| 101 | 3 | `public double getCouponRate()` | ...the same figure under the name LongTermBond's gives it. |
| 111 | 5 | `void reset(double policyRate)` | The month's dial, handed in by DebtManager every month BEFORE amortise(): when it has moved, the coupon with it, and the effective rate is solved again on the new known schedule at the carrying value as it stands (Deb... |
| 119 | 8 | `public void processMonth(Game game)` | A coupon on the whole face at the month's dial, every month; the face in the last. |
| 129 | 3 | `public double getIssuePrice()` |  |
| 134 | 3 | `protected double principalOwed()` |  |
| 139 | 3 | `public int getMaturityMonth()` |  |
| 144 | 3 | `public boolean isMatured()` |  |
| 149 | 3 | `public String getType()` |  |
| 155 | 3 | `protected double couponOwed()` | This month's coupon: the face at the coupon rate the last reset read, a twelfth. |
| 166 | 9 | `protected double[] scheduleOwed()` | THE KNOWN SCHEDULE: today's coupon held flat every month, and the face in the last. |
| 178 | 8 | `protected double[] couponScheduleOwed()` | Today's coupon, held flat: the dial moves it at each reset, and the books ahead do not guess where. |
| 196 | 4 | `public double[] projectedFlows(double[] policyPath)` | THE MARKET'S SCHEDULE: each coupon at the dial the market expects when it is paid, the face added to the last. |
| 207 | 11 | `public static double[] flowsFor(double face, double margin, double[] policyPath, int months)` | A floater's payments on a path of the dial: {@code face} of it, at this margin, for this many months - the one arithmetic projectedFlows(), DebtManager.fairMargin() and the auction's schedule share. |

