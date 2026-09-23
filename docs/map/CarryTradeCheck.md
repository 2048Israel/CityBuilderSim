# CarryTradeCheck.java - 297 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/CarryTradeCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The carry trade: the other side of hot money, and the bank's first borrower.
> 
> Jerus's framing, and it is the right one: "foreign borrow from the bank and
> convert to usd to do stuff with it, aka effectively having outflow of
> currency... its basically the opposite of hot money". Hot money comes here
> because the city pays more than the world. The carry goes the other way: when
> the city pays LESS than the world, the trade is to owe the cheap currency and
> hold the dear one, and every dollar of it is local currency SOLD.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Is the direction right? Hot money's condition is cityRate > worldRate;
>      this one's is the reverse, and a check that passes under both is
>      checking nothing. Section 1 asserts the sign by building both cities.
> 
>   2. Is it the spread net of the premium, again? A foreigner who owes this
>      currency is short it, and a currency that might jump is one nobody
>      wants to be short of. The same country premium that keeps hot money
>      out keeps the carry out.
> 
>   3. Is the bank's book really the bound? The target is headroom, not
>      output - Jerus's call, and the arithmetic behind it is in
>      CapitalFlows.carryTakeMonth. If the cap does not bind, the bank can be
>      lent past its own capacity by people who do not live here.
> 
>   4. DOES IT REACH THE BORDER? This is the one that matters. Hot money spent
>      its entire life moving money in and out of the city without ever
>      appearing on the balance of payments, because it moved after the audit
>      struck; the two errors cancelled and the residual stayed $0.00 for
>      years. A carry trade that does not reach the financial account is that
>      bug again, and section 5 is the assertion that would have caught it.

**Uses:** [CapitalFlows](CapitalFlows.md) (22), [DebtManager](DebtManager.md) (6), [ForeignAccounts](ForeignAccounts.md) (4), [MoneyAudit](MoneyAudit.md) (3), [Game](Game.md) (2), [GameFiles](GameFiles.md) (1)

## Sections

| line | section |
|---:|---|
| 76 | · 1. it is the opposite sign |
| 110 | · 2. the bank's book is the bound |
| 134 | · 3. it builds and unwinds gradually |
| 168 | · 4. it survives a save |
| 194 | · 5. and it reaches the border |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |
| 44 | `static PrintStream out` |  |
| 45 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 257 | **type** `public class CarryTradeCheck` | The carry trade: the other side of hot money, and the bank's first borrower. |
| 47 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 52 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 63 | 7 | `static CapitalFlows settle(double lendingRate, double premium, double headroom, int months)` | Run a stock to rest against a fixed rate, premium and headroom. |
| 71 | 217 | `public static void main(String[] args) throws Exception` |  |
| 290 | 7 | `static MoneyAudit.Result bop(double exports, double imports, double borrowedOut)` | A month at the city's edge, with an outflow on the financial account. |

