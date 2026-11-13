# DeskCheck.java - 1,280 lines · 31 methods · 0 constants · harnesses

`ham/citybuildersim/DeskCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The debt desk's figures (CD6, spec-city-debt.md sections 7 and 8): the figures the
> debt screens used to work out for themselves, held to the model that now gives
> them, and what the desk, a line's page and Your rate read beside them.
> 
> WHY THIS EXISTS. Before the new screens, the old ones stopped working out model
> figures (CLAUDE.md: every model figure through a public getter, never recomputed
> in the screen), and three of those figures were not what their labels said. A
> linked bond's last month counted its inflation uplift as "coupon a month"; the
> Government tab's grid summed each live piece's last expense where the budget's
> line holds the interest of a piece that fell due, and a dollar piece's booked at
> the month's rate; the Close it now page worked out its own premium, honest line
> and call gain. Each is a read, or a sum struck beside one already struck, and the
> model's month does not move.
> 
> What this has to prove:
> 
> 1. A linked bond's last month: its coupon is the coupon proper (the real coupon on
>    the indexed principal) and the uplift is its own getter, the face times the
>    ratio less one; the old measure, getMonthlyInterestExpense(), is the two; a
>    month earlier the uplift was nothing; the manager's sums are the pieces'; and
>    the press pays the uplift read before it, with the coupon.
> 2. The interest by kind is the budget's line: the five kinds' interest booked add
>    up to the line, the month a bill matured (the bill is gone from the list and
>    its interest is not, so the old sum of the live pieces falls short of the line
>    by exactly the bill's) and a month the D$ moved after the strike (a dollar
>    piece's expense read at the new rate is not the one the line holds); a saved
>    and loaded city has each kind's figure back.
> 3. A buyback with a gain, and the call: the journal's premium foots to the gain on
>    carrying value; the honest line's four figures are what the buyback then does
>    to the piece's coupon and what the price costs again at today's rate; the
>    call's gain, read before the call, is what the books then take.
> 4. The desk is the model's figures (slice B): each line of BookDesk is the ledger's own
>    (price, yield, worth, carried, the rate the books charge it at, a linker's real yield),
>    the totals are the ledger's and the bars by kind add up to them; what a point takes off
>    a line is what the curve built a point higher takes off its payments, and a floater's
>    duration is its month to the next reset, a ten-year line's most of its life.
> 5. A line's next six months on the books are the months it then books: Debt.booksAhead()
>    read, then six presses, a line sold under par, an amortizing bond across an anniversary
>    slice, a bill to maturity (closing to nothing), a line never sold, a floater and a linker
>    in its last month (the uplift in the coupon).
> 6. A line remembers its price and its sales: one price entry a month since the sale, the
>    last just what the ledger reads; a reopening is a second sale on one line; a part bought
>    back keeps both; a change of unit scales the sales' money and not the per-100 path; the
>    central bank's sales and the rollover's read as such; saved and loaded, the same, and
>    the next month adds one entry.
> 7. The curve's past: 26 months with the dial moved partway, the curve a year and two years
>    ago as the harness read it then, none beyond what is kept; saved and loaded, the same;
>    the rule and the dial the market expects; Your rate's nominal par yield against the
>    linked line's real coupon and breakeven, and the ten-year rate as its four parts; and
>    the TRACE GUARD, twin cities one of which records and one of which does not, alike to
>    the bit for the 26 months.
> 8. What closing a line now would book (Jerus, 2026-10-23: the book is green or red, in D$
>    and per cent): a line at a gain and a line at a loss, both caused (a line sold at par and
>    the rates raised, one sold at the higher rates and the rates cut) and both held by the bank
>    alone, which offers at fair; each line's ifClosedNow() and ifClosedNowShare() read from the
>    desk, then the line bought back whole and the gain the buyback booked (the deal's, and the
>    month's gain on debt retired) is the figure and its share; a dollar line's figure is in D$
>    at the month's actual rate; and the book's share is its total gain over its total carrying
>    value.
> 
> ... (4 more lines in the source)

**Uses:** [DebtManager](DebtManager.md) (57), [Debt](Debt.md) (54), [PriceLevel](PriceLevel.md) (40), [FloatLinkCheck](FloatLinkCheck.md) (37), [Game](Game.md) (22), [LongTermBond](LongTermBond.md) (22), [BookDesk](BookDesk.md) (19), [Founding](Founding.md) (14), [YieldCurve](YieldCurve.md) (9), [NationalAccounts](NationalAccounts.md) (5), [InflationLinkedBond](InflationLinkedBond.md) (5), [FloatingRateNote](FloatingRateNote.md) (5), [Buyback](Buyback.md) (5), [Auction](Auction.md) (3), [Observation](Observation.md) (3), [MediumTermBond](MediumTermBond.md) (3), [ShortTermTBill](ShortTermTBill.md) (2), [WantedPosts](WantedPosts.md) (1), [Rollover](Rollover.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (1):** [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 145 | 1. A LINKED BOND'S LAST MONTH |
| 216 | 2. THE INTEREST BY KIND IS THE BUDGET'S LINE |
| 348 | 3. A BUYBACK WITH A GAIN: ITS JOURNAL, ITS HONEST LINE, AND THE CALL |
| 421 | A HELPER FOR THE CASES THAT HOLD AT EVERY PIECE: THE WORST GAP, REPORTED ONCE |
| 450 | 4. THE DESK IS THE MODEL'S FIGURES |
| 650 | 5. A LINE'S NEXT SIX MONTHS ON THE BOOKS |
| 769 | 6. A LINE REMEMBERS ITS PRICE AND ITS SALES |
| 983 | 7. THE CURVE'S PAST |
| 1147 | 8. WHAT CLOSING A LINE NOW WOULD BOOK, GREEN OR RED |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |
| 80 | `static int asserts` |  |
| 81 | `static PrintStream out` |  |
| 82 | `static PrintStream quiet` |  |
| 427 | `int n` |  |
| 428 | `double worst` |  |
| 429 | `String at` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1204 | **type** `public class DeskCheck` | The debt desk's figures (CD6, spec-city-debt.md sections 7 and 8): the figures the debt screens used to work out for themselves, held to the model that now gives them, and what the desk, a line's page and Your rate re... |
| 84 | 5 | `static void check(String label, boolean ok)` |  |
| 90 | 10 | `static void near(String label, double actual, double expected, double tol)` |  |
| 101 | 4 | `static void quietly(Runnable r)` |  |
| 107 | 3 | `static void press(Game g)` | A month, pressed with its printing kept out of the log. |
| 111 | 5 | `static boolean wanted(String[] args, int section)` |  |
| 117 | 27 | `public static void main(String[] args) throws Exception` |  |

### 1. A LINKED BOND'S LAST MONTH (lines 145-215)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 66 | `static void lastMonthCoupon()` |  |

### 2. THE INTEREST BY KIND IS THE BUDGET'S LINE (lines 216-347)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 5 | `static int onBooks(DebtManager dm, String type)` |  |
| 227 | 5 | `static double kindsSum(DebtManager dm)` | What the five kinds' interest booked add up to. |
| 234 | 5 | `static double liveSum(DebtManager dm)` | The old sum: every piece still on the list, its last interest expense read now. |
| 241 | 8 | `static Debt sold(Game g, String what, Runnable sale)` | Sells one piece through its handler and says whether it went on the books as a new piece. |
| 250 | 85 | `static void interestByKind() throws Exception` |  |
| 337 | 10 | `static boolean restoresZeros(DebtManager dm)` | DebtManager.restoreInterestByKind() with nothing and with the wrong length: zeros, not an error. |

### 3. A BUYBACK WITH A GAIN: ITS JOURNAL, ITS HONEST LINE, AND THE CALL (lines 348-420)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 68 | `static void buybackAndCall()` |  |

### A HELPER FOR THE CASES THAT HOLD AT EVERY PIECE: THE WORST GAP, REPORTED ONCE (lines 421-449)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 23 | **type** `static final class Worst` | The worst gap over a set of cases, reported as one assertion with how many cases it covers. |
| 431 | 9 | `void add(double actual, double expected, String where)` _(in DeskCheck.Worst)_ |  |
| 441 | 7 | `void report(String label, double tol)` _(in DeskCheck.Worst)_ |  |

### 4. THE DESK IS THE MODEL'S FIGURES (lines 450-649)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 5 | `static YieldCurve aPointHigher(YieldCurve base)` | The curve a point higher, built the way the market builds one: its forwards each up a point, the shape the same, no spread and no ceiling to bind. |
| 461 | 4 | `static BookDesk.Line lineOf(BookDesk desk, Debt d)` |  |
| 466 | 183 | `static void theDesk() throws Exception` |  |

### 5. A LINE'S NEXT SIX MONTHS ON THE BOOKS (lines 650-768)

| line | len | member | says |
|---:|---:|---|---|
| 654 | 114 | `static void booksAhead()` |  |

### 6. A LINE REMEMBERS ITS PRICE AND ITS SALES (lines 769-982)

| line | len | member | says |
|---:|---:|---|---|
| 774 | 3 | `static String saleText(Debt.Sale s)` | What a sale holds, for comparing a piece with itself before and after. |
| 778 | 5 | `static java.util.List<String> salesText(Debt d)` |  |
| 784 | 191 | `static void remembers() throws Exception` |  |
| 977 | 5 | `static boolean recordedEarlier(Debt d)` | A month earlier than the path's last adds nothing to it. |

### 7. THE CURVE'S PAST (lines 983-1146)

| line | len | member | says |
|---:|---:|---|---|
| 988 | 5 | `static double[] tenorsNow(DebtManager dm)` | The city's zero rates at the curve's tenors, read now. |
| 994 | 5 | `static double[] worldNow(DebtManager dm)` |  |
| 1001 | 17 | `static Game twin(String label, boolean recording)` | A played city with a paper of every kind a sale can make, the book recording or not (a twin's). |
| 1020 | 5 | `static double[] fingerprint(Game g)` | The figures the month computes, for holding one city to its twin's. |
| 1026 | 120 | `static void theCurvesPast() throws Exception` |  |

### 8. WHAT CLOSING A LINE NOW WOULD BOOK, GREEN OR RED (lines 1147-1280)

| line | len | member | says |
|---:|---:|---|---|
| 1152 | 9 | `static LongTermBond bankHeldAtPar(Game g, double face)` | A bank-held two-year line sold at par at today's curve: its coupon the curve's two-year par yield, carried at its face, settled. |
| 1162 | 118 | `static void whatClosingBooks()` |  |

