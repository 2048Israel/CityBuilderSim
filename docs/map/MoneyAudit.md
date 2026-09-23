# MoneyAudit.java - 894 lines · 24 methods · 2 constants · model

`ham/citybuildersim/MoneyAudit.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> Where the money went this month, and whether it all went somewhere.
> 
> THE IDENTITY
> 
> Every dollar in the game sits in one of a handful of pools, as pools()
> reads them: the treasury (plus what a buyback between two presses has paid
> its holders outside the pools and the next month has not yet declared,
> since 0.7.1); every sector's cash, in Sectors.KEYS order; the
> construction sector's order book (a build is paid for up front and earned
> as the work is done, so the unearned part is money the builder holds and
> has not yet booked); and the bank's cash, plus what it owes the central
> bank's window and less what it still owes the treasury for the city's paper
> (sold between two presses, paid for at the next settle). Everything else -
> households, other cities, the world and, since 0.7.0, the central bank - is
> OUTSIDE, and money only ever crosses that boundary in a known set of ways:
> wages out, shopping and rent in, imports out, exports in, loans in,
> repayments and interest out, pensions and subsidies out, fees and wage tax
> in, the city's paper bought by households in and its coupons, principal and
> buybacks paid to them out (0.7.1) - and money the central bank makes in,
> and money paid back to it, which it destroys, out (Scope.MONEY).
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
> Struck by Game.nextMonth() every month, always, because it costs a few
> hundred getter reads; asserted by LongPlaytest and MoneyCheck, and its
> central bank lines by CentralBankCheck.

**Uses:** [Sectors](Sectors.md) (7), [Sector](Sector.md) (6), [Game](Game.md) (4), [Equity](Equity.md) (2), [EconomyManager](EconomyManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1), [CentralBank](CentralBank.md) (1)

**Used by (14):** [BankCheck](BankCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CurrencyCheck](CurrencyCheck.md), [ForeignAccounts](ForeignAccounts.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [OutsideCheck](OutsideCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 288 | `MoneyAudit.Scope.DOMESTIC` |  |
| 288 | `MoneyAudit.Scope.TRADE` |  |
| 288 | `MoneyAudit.Scope.INCOME` |  |
| 288 | `MoneyAudit.Scope.FINANCIAL` |  |
| 288 | `MoneyAudit.Scope.VALUATION` |  |
| 288 | `MoneyAudit.Scope.RESERVE` |  |
| 288 | `MoneyAudit.Scope.MONEY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 302 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `public final int month` |  |
| 56 | `public final double before` |  |
| 57 | `public final double after` |  |
| 58 | `public final double inflows` |  |
| 59 | `public final double outflows` |  |
| 61 | `public final double residual` | (after - before) - (inflows - outflows). |
| 64 | `public final String detail` | Every pool and flow by name, for chasing a residual. |
| 78 | `public final double[] poolsAtOpen` | The pools as this month's strike found them, and as it left them. |
| 79 | `public final double[] poolsAtClose` |  |
| 84 | `public final double tradeIn` | Goods and services sold abroad. |
| 86 | `public final double tradeOut` | ...and bought abroad. |
| 88 | `public final double incomeIn` | Interest received from foreign borrowers. |
| 90 | `public final double incomeOut` | ...and paid to foreign lenders. |
| 92 | `public final double financialIn` | Capital arriving from abroad. |
| 94 | `public final double financialOut` | ...and leaving. |
| 96 | `public final double valuationIn` | Foreign claims written off or settled, in the city's favour. |
| 98 | `public final double valuationOut` | ...and against it. |
| 100 | `public final double reserveIn` | Reserves sold for local money - the financing item, below the line. |
| 102 | `public final double reserveOut` | ...and bought with it. |
| 107 | `public final double moneyIn` | Money the central bank made and paid into the pools: advances, interest on reserves, the remittance. |
| 109 | `public final double moneyOut` | ...and money paid back to it, which it destroyed: repayments, and the window's and the advances' interest. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 49 | 846 | **type** `public final class MoneyAudit` | Where the money went this month, and whether it all went somewhere. |
| 52 | 155 | **type** `public static final class Result` | One month's strike. |
| 112 | 1 | `public double moneyMade()` _(in MoneyAudit.Result)_ | What the month did to M0, as the audit saw it cross the edge. |
| 115 | 1 | `public double tradeBalance()` _(in MoneyAudit.Result)_ | Exports less imports. |
| 118 | 1 | `public double incomeBalance()` _(in MoneyAudit.Result)_ | What the city earns on foreign assets, less what it pays on foreign debts. |
| 121 | 1 | `public double currentAccount()` _(in MoneyAudit.Result)_ | The two together. |
| 124 | 1 | `public double financialAccount()` _(in MoneyAudit.Result)_ | Capital in less capital out. |
| 130 | 1 | `public double valuationChange()` _(in MoneyAudit.Result)_ | Claims forgiven or settled. |
| 139 | 1 | `public double foreignBalance()` _(in MoneyAudit.Result)_ | What the city's foreign position moved by this month. |
| 142 | 3 | `public double foreignIn()` _(in MoneyAudit.Result)_ | Everything crossing the edge that is foreign, in each direction. |
| 145 | 3 | `public double foreignOut()` _(in MoneyAudit.Result)_ |  |
| 150 | 1 | `public double reserveChange()` _(in MoneyAudit.Result)_ | What the treasury did to its own reserve stock this month. |
| 158 | 1 | `public double domesticIn()` _(in MoneyAudit.Result)_ | ...and everything crossing it that is not. |
| 159 | 1 | `public double domesticOut()` _(in MoneyAudit.Result)_ |  |
| 161 | 3 | `Result(int month, double before, double after, double inflows, double outflows, double residual)` _(in MoneyAudit.Result)_ |  |
| 165 | 4 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreign)` _(in MoneyAudit.Result)_ |  |
| 170 | 25 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreig...` _(in MoneyAudit.Result)_ |  |
| 197 | 4 | `public double relative()` _(in MoneyAudit.Result)_ | Residual as a share of what moved, so a $3 leak in a $3B city reads as 0. |
| 202 | 4 | `public String toString()` _(in MoneyAudit.Result)_ |  |
| 208 | 1 | `private MoneyAudit()` |  |
| 288 | 1 | **type** `public enum Scope` | WHICH SIDE OF WHICH BOUNDARY A FLOW CROSSES. |
| 291 | 3 | **type** `private interface Tagged` | Label, amount and scope, for one line of the month. |
| 292 | 1 | `double apply(String label, double amount, Scope scope)` _(in MoneyAudit.Tagged)_ |  |
| 304 | 8 | `private static String[] poolNames()` |  |
| 314 | 47 | `public static double[] pools(Game g)` | The pools, in POOL_NAMES order. |
| 367 | 5 | `public static double pooled(Game g)` | Every dollar in the pools: the city's, its businesses', the builders' order book, and the bank's - plus what it owes the window, less what it owes for the city's paper. |
| 381 | 3 | `static Result strike(Game g, double before, double interestDue)` | Strikes the month. |
| 386 | 508 | `static Result strike(Game g, double before, double[] poolsBefore, double interestDue)` | As above, and with the opening pools the result can say which pool moved unexplained. |

