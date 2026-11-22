# CarryTradeCheck.java - 430 lines · 5 methods · 1 constants · harnesses

`ham/citybuildersim/CarryTradeCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
> 
>   5. DOES MONEY RUNNING HOME REPAY IT? (XR4, section 6) In a risk-off the
>      borrowers buy the city's money back: the dial's share of the book is
>      repaid on top of the ordinary month, inside the month's repaid and the
>      change returned, and a book told a level of 0 is the old book to the bit.
> 
> FIXTURES (FS9). Sections 1-4 and 6 run on bare CapitalFlows with the spread, the premium and
> the bank's headroom handed to the rule. Section 5 is a built city, played AUDITED_MONTHS with
> an audit each (the identities: the money still balances every month, and what the bank lends
> crosses the border), and what it needs of the town is caused and read against the rule: the
> policy rate set to its floor (DebtManager.MIN_POLICY_RATE) so the city is cheaper than the
> world, the bank's branches stood at once and capitalised through its own door (buildStack
> with no construction, Bank.openBranches() and injectCapital()) so its capital is there from the
> first month, and the rule's own reading of month one asserted - a spread over nought and a
> headroom and a target over nought - instead of the months it was seen lending on one path.
> WHY: the played town moved under the month its branches opened and its book first grew.

**Uses:** [CapitalFlows](CapitalFlows.md) (38), [DebtManager](DebtManager.md) (15), [ForeignAccounts](ForeignAccounts.md) (4), [MoneyAudit](MoneyAudit.md) (3), [Game](Game.md) (2), [TempDirs](TempDirs.md) (1), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [Bank](Bank.md) (1)

## Sections

| line | section |
|---:|---|
| 91 | · 1. it is the opposite sign |
| 125 | · 2. the bank's book is the bound |
| 149 | · 3. it builds and unwinds gradually |
| 183 | · 4. it survives a save |
| 209 | · 5. and it reaches the border |
| 341 | · 6. a risk-off sends it home (XR4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 420 | `CarryTradeCheck.AUDITED_MONTHS` | `36` | The months the built city is played with an audit each: the book builds a share of its gap a month (CARRY_BORROW_SPEED), so a longer play audits more of its build. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |
| 59 | `static PrintStream out` |  |
| 60 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 56 | 375 | **type** `public class CarryTradeCheck` | The carry trade: the other side of hot money, and the bank's first borrower. |
| 62 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 67 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 78 | 7 | `static CapitalFlows settle(double lendingRate, double premium, double headroom, int months)` | Run a stock to rest against a fixed rate, premium and headroom. |
| 86 | 332 | `public static void main(String[] args) throws Exception` |  |
| 423 | 7 | `static MoneyAudit.Result bop(double exports, double imports, double borrowedOut)` | A month at the city's edge, with an outflow on the financial account. |

