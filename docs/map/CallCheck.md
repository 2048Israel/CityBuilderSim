# CallCheck.java - 743 lines · 28 methods · 2 constants · harnesses

`ham/citybuildersim/CallCheck.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md
> sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be
> sold callable, its coupon carries the right on a Ho-Lee tree fitted to the
> curve at the volatility of the city's own rate, and from its first call date
> the city may call the whole line at 100.
> 
> What this has to prove:
> 
> 1. The tree is the curve: on a calm flat curve and on a hiking, inverted
>    one, every month's zero-coupon price from the tree is the curve's
>    factor to 1e-9, and a non-call 10- and 30-year bullet prices at the
>    curve's own present value to 1e-6 per 100, at the floor of the volatility
>    and above it; with a spread on the curve as well.
> 2. The option: a callable bond is never worth more than the same bond that
>    cannot be called, and the same when its first call is past its maturity;
>    its spread is positive, grows with the volatility, with an earlier first
>    call and with the term, and a bond at the par coupon plus the spread is
>    worth 100. The four figures beside the study's are printed, not asserted.
> 3. The volatility: a history with known monthly changes gives their
>    standard deviation times the square root of twelve, over the last
>    VOL_MONTHS only; a quiet one gives RATE_VOL_FLOOR; so does a young one;
>    a month not counted breaks a change; a played city measures its own.
> 4. The sale: a played city sells a callable 10-year whose coupon is over a
>    non-call 10-year's by the spread the tree gives (within a coupon step),
>    callable from FIRST_CALL_10Y years on; a non-call sale does not reopen it
>    and a callable one does; a 5-year callable is refused; and a city played
>    for two years that never asked for the option has no callable piece.
> 5. The call: before the first call month it refuses and changes nothing; at
>    it, with the central bank holding part of the line, every holder is paid
>    its face, the treasury's cash falls by the principal, the gain booked is
>    the carrying value less the principal, and the money audit closes that
>    month and the next.
> 6. Worth calling, caused from both sides: a line at today's par yield is not
>    worth calling and the same line with rates pushed down is, and the
>    threshold is WORTH_CALLING of the face on either side of it.
> 7. The save: a callable line saved and loaded keeps its flag, its first call
>    month, its coupon and its market value.
> 
> Each fixture causes its condition rather than finding a city in it. Run with
> section numbers (CallCheck 1 2 3) to run just those.

**Uses:** [CallTree](CallTree.md) (69), [Game](Game.md) (22), [LongTermBond](LongTermBond.md) (19), [Auction](Auction.md) (17), [YieldCurve](YieldCurve.md) (13), [DebtManager](DebtManager.md) (11), [Debt](Debt.md) (8), [Founding](Founding.md) (6), [DebtQuote](DebtQuote.md) (6), [MoneyAudit](MoneyAudit.md) (4), [GameFiles](GameFiles.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Sectors](Sectors.md) (2), [CapitalFlows](CapitalFlows.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [CentralBank](CentralBank.md) (1), [Bank](Bank.md) (1), [TreasuryCheck](TreasuryCheck.md) (1)

**Used by (1):** [FloatLinkCheck](FloatLinkCheck.md)

## Sections

| line | section |
|---:|---|
| 122 | THE FIXTURE CURVES: the study's calm city, and a hiking one |
| 161 | 1. THE TREE IS THE CURVE |
| 205 | 2. THE OPTION |
| 274 | 3. THE VOLATILITY |
| 332 | A PLAYED CITY (BuybackCheck's: the households, the bank and the world have the money to bid) |
| 381 | 4. THE SALE |
| 508 | 5. THE CALL |
| 618 | 6. WORTH CALLING |
| 701 | 7. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 127 | `CallCheck.ROW_MONTHS` | `{ 12, 24, 60, 120, 240, 360, 600 }` | The study's calm city (spec section 3.3): the 1-year zero rate and the zero rate at 2, 5, 10, 20, 30 and 50 years. |
| 128 | `CallCheck.ROW_ZERO` | `{.0312,.0318,.0335,.0362,.0402,.0427,.0462 }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |
| 57 | `static PrintStream out` |  |
| 58 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 690 | **type** `public class CallCheck` | Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be sold callable, its coupon carries the right on a Ho-Lee tree fitted to the ... |
| 60 | 4 | `static void check(String label, boolean ok)` |  |
| 65 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 75 | 4 | `static void quietly(Runnable r)` |  |
| 80 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 88 | 4 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit: the audit is returned. |
| 93 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |
| 97 | 5 | `static boolean wanted(String[] args, int section)` |  |
| 103 | 18 | `public static void main(String[] args) throws Exception` |  |

### THE FIXTURE CURVES: the study's calm city, and a hiking one (lines 122-160)

| line | len | member | says |
|---:|---:|---|---|
| 131 | 10 | `static double calmZero(int m)` | The zero rate the study's calm row gives at m months: straight lines between its points, flat at the short rate up to a year. |
| 143 | 4 | `static YieldCurve calm()` | The calm city's curve: a flat dial at the 1-year rate and the shape that takes the zero rate along the study's row. |
| 149 | 6 | `static YieldCurve hiking()` | A hiking city's curve: the dial at 7% closing on 3% with a two-year time constant, so the short end is over the long end, and a modest term premium on top. |
| 157 | 3 | `static double[] bullet(double coupon, int months)` | A bullet's payments per 100 at a coupon, month 1 first. |

### 1. THE TREE IS THE CURVE (lines 161-204)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 39 | `static void treeIsTheCurve()` |  |

### 2. THE OPTION (lines 205-273)

| line | len | member | says |
|---:|---:|---|---|
| 209 | 64 | `static void theOption()` |  |

### 3. THE VOLATILITY (lines 274-331)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 6 | `static double[] zigzag(double start, double step, int changes)` | A rate history whose monthly changes are +step, -step, +step, ... |
| 286 | 45 | `static void theVolatility()` |  |

### A PLAYED CITY (BuybackCheck's: the households, the bank and the world have the money to bid) (lines 332-380)

| line | len | member | says |
|---:|---:|---|---|
| 336 | 21 | `static Game playedCity(String label)` |  |
| 359 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |
| 368 | 5 | `static List<LongTermBond> callables(Game g)` | Every callable bond on the city's books. |
| 375 | 5 | `static int bonds(Game g)` | Every piece the city's own bond lines hold (callable or not). |

### 4. THE SALE (lines 381-507)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 110 | `static void theSale(Game city)` |  |
| 497 | 10 | `static boolean differentCallDateIsAnotherLine(Game city)` | A callable line built with another first call date is not the line a standard callable sale reopens. |

### 5. THE CALL (lines 508-617)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 89 | `static void theCall()` |  |
| 603 | 7 | `static double[] snapshot(Game g, Debt d)` | Everything a refused call must leave as it was. |
| 612 | 5 | `static double tills(Game g)` | Every sector's till, added up. |

### 6. WORTH CALLING (lines 618-700)

| line | len | member | says |
|---:|---:|---|---|
| 622 | 73 | `static void worthCalling(Game city)` |  |
| 697 | 3 | `static void reprice(Game g)` | The market priced as the city stands (Game keeps that private): a quote of a bill books nothing and prices it first. |

### 7. THE SAVE (lines 701-743)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 38 | `static void theSave(Game city) throws Exception` |  |

