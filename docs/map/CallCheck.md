# CallCheck.java - 870 lines · 32 methods · 5 constants · harnesses

`ham/citybuildersim/CallCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
> 
> How the played sections get theirs (FS14), one clause each:
> 
> 4. both auctions clear at fair because the sale is one of saleOf()'s size and the bank is handed its room for the section's sales
>    (FloatLinkCheck.handBankRoom(), ASKS_OF_ROOM of them), not because an ask was found by halving it until both quotes filled; the
>    tails and the coupon gap are then asserted against Auction.COUPON_STEP and the tree's option spread.
> 5. the callable line is sold the same way (one saleOf(), the bank handed its room), and the central bank holds part of it by its dial
>    (CB_DIAL, set) and the months its holdings step takes (at most CB_BUYS_WITHIN_MONTHS pressed, a fixture failing past them), not by
>    waiting up to 24 months; the call month is moved by hand (moveFirstCallForTest()).
> 7. the callable line it saves is section 4's, or, run alone, one sold as section 5's is.

**Uses:** [CallTree](CallTree.md) (69), [Game](Game.md) (25), [Auction](Auction.md) (19), [LongTermBond](LongTermBond.md) (19), [YieldCurve](YieldCurve.md) (13), [DebtManager](DebtManager.md) (11), [Debt](Debt.md) (8), [Founding](Founding.md) (6), [DebtQuote](DebtQuote.md) (6), [MoneyAudit](MoneyAudit.md) (4), [GameFiles](GameFiles.md) (3), [FloatLinkCheck](FloatLinkCheck.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Sectors](Sectors.md) (2), [Bank](Bank.md) (2), [CapitalFlows](CapitalFlows.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [Sector](Sector.md) (1), [CentralBank](CentralBank.md) (1), [TreasuryCheck](TreasuryCheck.md) (1)

**Used by (1):** [FloatLinkCheck](FloatLinkCheck.md)

## Sections

| line | section |
|---:|---|
| 132 | THE FIXTURE CURVES: the study's calm city, and a hiking one |
| 171 | 1. THE TREE IS THE CURVE |
| 215 | 2. THE OPTION |
| 284 | 3. THE VOLATILITY |
| 342 | A PLAYED CITY (BuybackCheck's: the households, the bank and the world have the money to bid) |
| 446 | 4. THE SALE |
| 617 | 5. THE CALL |
| 736 | 6. WORTH CALLING |
| 825 | 7. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 137 | `CallCheck.ROW_MONTHS` | `{ 12, 24, 60, 120, 240, 360, 600 }` | The study's calm city (spec section 3.3): the 1-year zero rate and the zero rate at 2, 5, 10, 20, 30 and 50 years. |
| 138 | `CallCheck.ROW_ZERO` | `{.0312,.0318,.0335,.0362,.0402,.0427,.0462 }` |  |
| 425 | `CallCheck.ASKS_OF_ROOM` | `4` | FS14: the asks of section 4 the bank is handed its room for: the callable sale, the non-call one, the reopening of the callable line and the 20-year quote. |
| 428 | `CallCheck.CB_DIAL` | `.3` | FS14: the central bank's dial in section 5: a share of the city's term paper to hold, 30%. |
| 431 | `CallCheck.CB_BUYS_WITHIN_MONTHS` | `1` | FS14: the most months section 5 presses, after the dial is set, for the central bank to hold part of the line: its holdings step moves it in the first. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `static int fails` |  |
| 67 | `static PrintStream out` |  |
| 68 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 807 | **type** `public class CallCheck` | Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be sold callable, its coupon carries the right on a Ho-Lee tree fitted to the ... |
| 70 | 4 | `static void check(String label, boolean ok)` |  |
| 75 | 9 | `static void near(String label, double actual, double expected, double tol)` |  |
| 85 | 4 | `static void quietly(Runnable r)` |  |
| 90 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 98 | 4 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit: the audit is returned. |
| 103 | 3 | `static boolean closes(MoneyAudit.Result r)` |  |
| 107 | 5 | `static boolean wanted(String[] args, int section)` |  |
| 113 | 18 | `public static void main(String[] args) throws Exception` |  |

### THE FIXTURE CURVES: the study's calm city, and a hiking one (lines 132-170)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 10 | `static double calmZero(int m)` | The zero rate the study's calm row gives at m months: straight lines between its points, flat at the short rate up to a year. |
| 153 | 4 | `static YieldCurve calm()` | The calm city's curve: a flat dial at the 1-year rate and the shape that takes the zero rate along the study's row. |
| 159 | 6 | `static YieldCurve hiking()` | A hiking city's curve: the dial at 7% closing on 3% with a two-year time constant, so the short end is over the long end, and a modest term premium on top. |
| 167 | 3 | `static double[] bullet(double coupon, int months)` | A bullet's payments per 100 at a coupon, month 1 first. |

### 1. THE TREE IS THE CURVE (lines 171-214)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 39 | `static void treeIsTheCurve()` |  |

### 2. THE OPTION (lines 215-283)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 64 | `static void theOption()` |  |

### 3. THE VOLATILITY (lines 284-341)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 6 | `static double[] zigzag(double start, double step, int changes)` | A rate history whose monthly changes are +step, -step, +step, ... |
| 296 | 45 | `static void theVolatility()` |  |

### A PLAYED CITY (BuybackCheck's: the households, the bank and the world have the money to bid) (lines 342-445)

| line | len | member | says |
|---:|---:|---|---|
| 356 | 26 | `static Game playedCity(String label)` | FOUR DEPOTS STANDING SINCE PT1-M4, beside the four it orders. |
| 393 | 7 | `static double keepBankOverDesk(Game city)` | FC2, 2026-11-23: THE BANK'S CAPITAL KEPT OVER ITS SHARE DESK'S BOOK, between months, by what its equity lacks put in its till (Bank.setCash, as FloatLinkCheck.handBankRoom() hands its room): what the desk holds could ... |
| 402 | 7 | `static void worldWantsPaper(Game city)` | The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. |
| 411 | 5 | `static List<LongTermBond> callables(Game g)` | Every callable bond on the city's books. |
| 418 | 5 | `static int bonds(Game g)` | Every piece the city's own bond lines hold (callable or not). |
| 434 | 4 | `static boolean fixture(String label, boolean ok)` | FS14: a fixture assertion that counts its failure and says whether it held, so a section stops where what follows would read nonsense. |
| 440 | 5 | `static void roomFor(Game city, double face, String what)` | FS14: the bank handed its room for this face of the city's paper (FloatLinkCheck.handBankRoom(): capital put in its till, between months), and the room asserted. |

### 4. THE SALE (lines 446-616)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 4 | `static double saleOf(Game city, double spares)` | A sale of this many times what the households could take, and of no more than that many times a third of a year's tax revenue (EM1, 2026-10-29). |
| 463 | 141 | `static void theSale(Game city)` |  |
| 606 | 10 | `static boolean differentCallDateIsAnotherLine(Game city)` | A callable line built with another first call date is not the line a standard callable sale reopens. |

### 5. THE CALL (lines 617-735)

| line | len | member | says |
|---:|---:|---|---|
| 621 | 98 | `static void theCall()` |  |
| 721 | 7 | `static double[] snapshot(Game g, Debt d)` | Everything a refused call must leave as it was. |
| 730 | 5 | `static double tills(Game g)` | Every sector's till, added up. |

### 6. WORTH CALLING (lines 736-824)

| line | len | member | says |
|---:|---:|---|---|
| 740 | 79 | `static void worthCalling(Game city)` |  |
| 821 | 3 | `static void reprice(Game g)` | The market priced as the city stands (Game keeps that private): a quote of a bill books nothing and prices it first. |

### 7. THE SAVE (lines 825-870)

| line | len | member | says |
|---:|---:|---|---|
| 829 | 41 | `static void theSave(Game city) throws Exception` |  |

