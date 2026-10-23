# InflationLinkedBond.java - 287 lines · 26 methods · 2 constants · model

`ham/citybuildersim/InflationLinkedBond.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> An inflation-linked bond: a REAL coupon every month on a principal that
> follows the city's price index, and at the end the indexed principal, never
> under the face - at 10 or 30 years, each sale its own line (CD5,
> spec-city-debt.md section 2.5; D25).
> 
> WHY: a nominal bond asks the lender to guess the inflation of thirty years
> and charges for the guess; a city whose central bank is believed less than
> it deserves pays dear for it (a nominal 10-year at 5.28% in the behind city,
> where a linked one pays a real 1.92% plus the inflation the city actually
> has). The linked bond hands the inflation back to the city that makes it:
> Canada sold Real Return Bonds from 1991 to 2022 and the US sells TIPS. The
> floor is TIPS': the face at the end is never under the original 100.
> 
> WHERE THE INDEXATION LIVES. The principal on the books (outstandingPrincipal,
> faceValue) STAYS THE ORIGINAL FACE: every holder's share is in face, the
> households', firms', world's and central bank's own books are in face too,
> and indexing those would touch five models. The indexation is in the flows -
> the coupon is the real coupon on faceValue x ratio, and the last month adds
> the uplift (faceValue x (max(1, ratio) - 1)) as interest to every holder,
> through the same coupon path as fixed paper, then the face as principal
> through the same principal path. So the uplift is a cash flow of the last
> month only; the months before it carry it on the city's books as an
> accrual, not as cash (below).
> 
> THE BOOKS (spec section 5.3, IFRS 9 B5.4.6). The effective rate is REAL and
> stays what the sale made it. Every month, before the month's books are
> struck, index() reads the price index: when the ratio of the index to its
> base has moved, the carrying value CATCHES UP - it becomes the present value
> of the known schedule (today's ratio held flat) at the unchanged real
> effective rate - and the change is the month's inflation uplift, an expense
> that is not cash (below nothing in a deflation month). The uplift paid in
> the last month is cash, and comes off the not-cash row when it is paid.
> 
> A line is never reopened: a linker reopened mid-life would need its index
> ratio carried into the price of the new face, and the price of a sale at
> ratio 1 against an indexed line is Jerus's to decide (left open).

**Uses:** [Debt](Debt.md) (1), [Game](Game.md) (1)

**Used by (3):** [DebtManager](DebtManager.md), [FloatLinkCheck](FloatLinkCheck.md), [Game](Game.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 46 | `InflationLinkedBond.MATURITIES` | `{ 10, 30 }` | The only terms an inflation-linked bond is issued at, in years (D25). |
| 49 | `InflationLinkedBond.REFUSAL` | `"Inflation-linked bonds are issued at 10 or 30 years."` | What the treasury is told when it asks for any other term. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private double realCoupon` | The real coupon, a year, as the sale set it; fixed. |
| 70 | `private double baseIndex` | The city's price index (PriceIndex.getIndex()) when the bond was sold: what the ratio is the index over. |
| 73 | `private double ratio` | The price index over its base, as the last indexing read it: 1 at the sale. |
| 76 | `private double lastUpliftOwn, lastUpliftPaidOwn` | The month's indexation as index() last booked it, and the uplift the last month paid, in the currency the piece is written in. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 43 | 245 | **type** `public class InflationLinkedBond extends Debt` | An inflation-linked bond: a REAL coupon every month on a principal that follows the city's price index, and at the end the indexed principal, never under the face - at 10 or 30 years, each sale its own line (CD5, spec... |
| 52 | 4 | `public static boolean isIssuable(int years)` | True for one of the two. |
| 58 | 7 | `public static int nearestYears(int years)` | The one of MATURITIES nearest this many years; the shorter on a tie. |
| 82 | 12 | `public InflationLinkedBond(double faceValue, int months, int monthStarted, double realCoupon, double baseIndex)` |  |
| 96 | 3 | `public double getRealCoupon()` | The real coupon, a year. |
| 101 | 3 | `public double getBaseIndex()` | The price index when the bond was sold. |
| 106 | 3 | `public double getRatio()` | The price index over its base, as the last indexing read it; 1 at the sale. |
| 111 | 3 | `public double getIndexedPrincipal()` | The principal as it now stands indexed: the face times the ratio, in local money. |
| 115 | 3 | `private double indexedPrincipal()` |  |
| 120 | 3 | `public double getLastUplift()` | What the last month's indexing added to the carrying value, in local money: the inflation uplift, an expense that is not cash; below nothing in a deflation month. |
| 125 | 3 | `public double getLastUpliftPaid()` | ...and the uplift the last month PAID in cash, in local money: nothing but in the bond's last month, when it is the indexed principal less the face (never under nothing: the floor). |
| 130 | 3 | `public double owedAtMaturity()` | What the bond will repay at the end at today's ratio: the face, or the indexed principal when that is more - the deflation floor (D25, as TIPS). |
| 135 | 3 | `public double ratioAt(double priceIndexLevel)` | The ratio the next indexing will read at this price level: the level over the base, or the ratio as it stands for a level that is not a positive number. |
| 149 | 11 | `double index(double priceIndexLevel)` | The month's indexing, handed the price index by DebtManager every month BEFORE amortise(). |
| 167 | 5 | `double amortise()` | This month's books (Debt.amortise()) and the uplift this month will pay: in the last month the indexed principal less the face, never under nothing - the deflation floor - and in any other month nothing. |
| 180 | 8 | `public void processMonth(Game game)` | The real coupon on the indexed principal, every month; the face, and the uplift as interest with the last coupon, in the last. |
| 190 | 3 | `public double getIssuePrice()` |  |
| 195 | 3 | `protected double principalOwed()` |  |
| 200 | 3 | `public int getMaturityMonth()` |  |
| 205 | 3 | `public boolean isMatured()` |  |
| 210 | 3 | `public String getType()` |  |
| 216 | 5 | `protected double couponOwed()` | This month's coupon: the real coupon on the indexed principal, a twelfth - and in the last month the uplift as well, faceValue x (max(1, ratio) - 1). |
| 229 | 9 | `protected double[] scheduleOwed()` | THE KNOWN SCHEDULE at today's ratio held flat: the real coupon on the indexed principal every month, and in the last also the face or the indexed principal if that is more. |
| 241 | 4 | `protected void redenominateSchedule(double scale)` | The last uplift figures are quantities of money, in the new unit (and a part bought back is the same bond, smaller). |
| 255 | 3 | `public double[] projectedFlows(double nextRatio, double[] monthlyGrowth)` | THE MARKET'S SCHEDULE: the ratio the next payment carries is {@code nextRatio} - the price index now over the base, because the next tick indexes on it (the index takes its month after the debts are paid) - and each l... |
| 267 | 8 | `public static double[] flowsFor(double face, double realCoupon, double nextRatio, double[] monthlyGrowth, int months)` | A linker's payments on a path of the price index: {@code face} of it, at this real coupon, the first payment's ratio {@code nextRatio} and each later month's grown by monthlyGrowth[i] (i months ahead; the last entry h... |
| 277 | 10 | `public static double[] ratiosFor(double nextRatio, double[] monthlyGrowth, int months)` | The index ratio each payment carries on that path: payment 0 the next ratio, each after it grown by its month's growth. |

