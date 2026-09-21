# MoneyAudit.java - 767 lines · 23 methods · 2 constants · model

`ham/citybuildersim/MoneyAudit.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> Where the money went this month, and whether it all went somewhere.
> 
> THE IDENTITY
> 
> Every dollar in the game sits in one of a handful of pools: the treasury,
> the six private sectors' cash, and the construction sector's order book (a
> build is paid for up front and earned as the work is done, so the unearned
> part is money the builder holds and has not yet booked). Everything else -
> households, the lender, other cities, the world - is OUTSIDE, and money only
> ever crosses that boundary in a known set of ways: wages out, shopping and
> rent in, imports out, exports in, loans in, repayments and interest out,
> pensions and subsidies out, fees and wage tax in.
> 
> So for one month tick:
> 
>     pooled(after) - pooled(before)  ==  inflows - outflows
> 
> and the difference between the two sides is the RESIDUAL. A residual is a
> dollar that appeared from nowhere or vanished into nothing, and every one of
> this codebase's money bugs - the quote that charged the city for a private
> investor's materials, the tax collected on money the sector kept, the VAT
> struck on sales nobody paid for - was a residual that nothing was measuring.
> 
> WHAT IT IS NOT
> 
> Not a second set of books. Every figure here is read off the statement the
> sector already wrote for the month, so this cannot disagree with the screen.
> What it CAN disagree with is the cash, and that is the point: a statement
> that says one thing while the bank balance moves by another is exactly what
> this catches. Internal flows - a shop paying a mill, a mill paying a mine,
> the city collecting a tax - are deliberately not listed. They cancel if both
> sides booked the same number, and show up as residual if they did not.
> 
> Struck by Game.nextMonth() every month, always, because it costs forty
> getter reads; asserted by LongPlaytest and MoneyCheck.

**Uses:** [Sectors](Sectors.md) (7), [Sector](Sector.md) (6), [Game](Game.md) (4), [Equity](Equity.md) (2), [EconomyManager](EconomyManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1)

**Used by (10):** [BankCheck](BankCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [ForeignAccounts](ForeignAccounts.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [OutsideCheck](OutsideCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 251 | `MoneyAudit.Scope.DOMESTIC` |  |
| 251 | `MoneyAudit.Scope.TRADE` |  |
| 251 | `MoneyAudit.Scope.INCOME` |  |
| 251 | `MoneyAudit.Scope.FINANCIAL` |  |
| 251 | `MoneyAudit.Scope.VALUATION` |  |
| 251 | `MoneyAudit.Scope.RESERVE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 265 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `public final int month` |  |
| 47 | `public final double before` |  |
| 48 | `public final double after` |  |
| 49 | `public final double inflows` |  |
| 50 | `public final double outflows` |  |
| 52 | `public final double residual` | (after - before) - (inflows - outflows). |
| 55 | `public final String detail` | Every pool and flow by name, for chasing a residual. |
| 68 | `public final double[] poolsAtOpen` | The pools as this month's strike found them, and as it left them. |
| 69 | `public final double[] poolsAtClose` |  |
| 74 | `public final double tradeIn` | Goods and services sold abroad. |
| 76 | `public final double tradeOut` | ...and bought abroad. |
| 78 | `public final double incomeIn` | Interest received from foreign borrowers. |
| 80 | `public final double incomeOut` | ...and paid to foreign lenders. |
| 82 | `public final double financialIn` | Capital arriving from abroad. |
| 84 | `public final double financialOut` | ...and leaving. |
| 86 | `public final double valuationIn` | Foreign claims written off or settled, in the city's favour. |
| 88 | `public final double valuationOut` | ...and against it. |
| 90 | `public final double reserveIn` | Reserves sold for local money - the financing item, below the line. |
| 92 | `public final double reserveOut` | ...and bought with it. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 40 | 728 | **type** `public final class MoneyAudit` | Where the money went this month, and whether it all went somewhere. |
| 43 | 137 | **type** `public static final class Result` | One month's strike. |
| 95 | 1 | `public double tradeBalance()` _(in MoneyAudit.Result)_ | Exports less imports. |
| 98 | 1 | `public double incomeBalance()` _(in MoneyAudit.Result)_ | What the city earns on foreign assets, less what it pays on foreign debts. |
| 101 | 1 | `public double currentAccount()` _(in MoneyAudit.Result)_ | The two together. |
| 104 | 1 | `public double financialAccount()` _(in MoneyAudit.Result)_ | Capital in less capital out. |
| 110 | 1 | `public double valuationChange()` _(in MoneyAudit.Result)_ | Claims forgiven or settled. |
| 119 | 1 | `public double foreignBalance()` _(in MoneyAudit.Result)_ | What the city's foreign position moved by this month. |
| 122 | 3 | `public double foreignIn()` _(in MoneyAudit.Result)_ | Everything crossing the edge that is foreign, in each direction. |
| 125 | 3 | `public double foreignOut()` _(in MoneyAudit.Result)_ |  |
| 130 | 1 | `public double reserveChange()` _(in MoneyAudit.Result)_ | What the treasury did to its own reserve stock this month. |
| 133 | 1 | `public double domesticIn()` _(in MoneyAudit.Result)_ | ...and everything crossing it that is not. |
| 134 | 1 | `public double domesticOut()` _(in MoneyAudit.Result)_ |  |
| 136 | 3 | `Result(int month, double before, double after, double inflows, double outflows, double residual)` _(in MoneyAudit.Result)_ |  |
| 140 | 4 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreign)` _(in MoneyAudit.Result)_ |  |
| 145 | 23 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreig...` _(in MoneyAudit.Result)_ |  |
| 170 | 4 | `public double relative()` _(in MoneyAudit.Result)_ | Residual as a share of what moved, so a $3 leak in a $3B city reads as 0. |
| 175 | 4 | `public String toString()` _(in MoneyAudit.Result)_ |  |
| 181 | 1 | `private MoneyAudit()` |  |
| 251 | 1 | **type** `public enum Scope` | WHICH SIDE OF WHICH BOUNDARY A FLOW CROSSES. |
| 254 | 3 | **type** `private interface Tagged` | Label, amount and scope, for one line of the month. |
| 255 | 1 | `double apply(String label, double amount, Scope scope)` _(in MoneyAudit.Tagged)_ |  |
| 267 | 8 | `private static String[] poolNames()` |  |
| 277 | 16 | `public static double[] pools(Game g)` | The pools, in POOL_NAMES order. |
| 295 | 5 | `public static double pooled(Game g)` | Every dollar the city and its businesses hold, plus the builder's order book. |
| 309 | 3 | `static Result strike(Game g, double before, double interestDue)` | Strikes the month. |
| 314 | 453 | `static Result strike(Game g, double before, double[] poolsBefore, double interestDue)` | As above, and with the opening pools the result can say which pool moved unexplained. |

