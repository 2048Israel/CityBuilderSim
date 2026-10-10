# MoneyAudit.java - 1,153 lines · 27 methods · 5 constants · model

`ham/citybuildersim/MoneyAudit.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
> has not yet booked); the bank's cash, plus what it owes the central
> bank's window and less what it still owes the treasury for the city's paper
> (sold between two presses, paid for at the next settle); and, since 0.7.14,
> the city's fund's cash (TreasuryFund). Everything else -
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

**Uses:** [Sectors](Sectors.md) (8), [Sector](Sector.md) (8), [Game](Game.md) (4), [SupplierCredit](SupplierCredit.md) (2), [Trade](Trade.md) (2), [Equity](Equity.md) (2), [EconomyManager](EconomyManager.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1), [BondMarket](BondMarket.md) (1), [CentralBank](CentralBank.md) (1)

**Used by (33):** [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [EducationCheck](EducationCheck.md), [ForeignAccounts](ForeignAccounts.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GroceryCheck](GroceryCheck.md), [HistoryCheck](HistoryCheck.md), [HoldersCheck](HoldersCheck.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [PortCheck](PortCheck.md), [RefineryCheck](RefineryCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SkipReportCheck](SkipReportCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 381 | HOW CLOSE IS CLOSE ENOUGH, AT ANY SIZE (0.7.54) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 294 | `MoneyAudit.Scope.DOMESTIC` |  |
| 294 | `MoneyAudit.Scope.TRADE` |  |
| 294 | `MoneyAudit.Scope.INCOME` |  |
| 294 | `MoneyAudit.Scope.FINANCIAL` |  |
| 294 | `MoneyAudit.Scope.VALUATION` |  |
| 294 | `MoneyAudit.Scope.RESERVE` |  |
| 294 | `MoneyAudit.Scope.MONEY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 309 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank, and since 0.7.14 the city's fund (on the end, so every other pool keeps its place). |
| 430 | `MoneyAudit.CENT` | `.01` | A harness's cent: 0.01 of a unit of a thousand dollars, the least a money comparison has allowed. |
| 433 | `MoneyAudit.RELATIVE_TOLERANCE` | `1e-12` | ...and the share of the figures compared that their own rounding is allowed past it: a part in a trillion. |
| 446 | `MoneyAudit.ULP_STEPS` | `64` | ...and the least a floor allows, in a double's steps at the size of the figures compared (0.7.64; 8 in 0.7.63): the most an identity held to a floor under a cent was measured to miss by, in steps of the figures it is ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `public final int month` |  |
| 57 | `public final double before` |  |
| 58 | `public final double after` |  |
| 59 | `public final double inflows` |  |
| 60 | `public final double outflows` |  |
| 62 | `public final double residual` | (after - before) - (inflows - outflows). |
| 65 | `public final String detail` | Every pool and flow by name, for chasing a residual. |
| 79 | `public final double[] poolsAtOpen` | The pools as this month's strike found them, and as it left them. |
| 80 | `public final double[] poolsAtClose` |  |
| 85 | `public final double tradeIn` | Goods and services sold abroad. |
| 87 | `public final double tradeOut` | ...and bought abroad. |
| 89 | `public final double incomeIn` | Interest received from foreign borrowers. |
| 91 | `public final double incomeOut` | ...and paid to foreign lenders. |
| 93 | `public final double financialIn` | Capital arriving from abroad. |
| 95 | `public final double financialOut` | ...and leaving. |
| 97 | `public final double valuationIn` | Foreign claims written off or settled, in the city's favour. |
| 99 | `public final double valuationOut` | ...and against it. |
| 101 | `public final double reserveIn` | Reserves sold for local money - the financing item, below the line. |
| 103 | `public final double reserveOut` | ...and bought with it. |
| 108 | `public final double moneyIn` | Money the central bank made and paid into the pools: advances, interest on reserves, the remittance. |
| 110 | `public final double moneyOut` | ...and money paid back to it, which it destroyed: repayments, and the window's and the advances' interest. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1104 | **type** `public final class MoneyAudit` | Where the money went this month, and whether it all went somewhere. |
| 53 | 160 | **type** `public static final class Result` | One month's strike. |
| 113 | 1 | `public double moneyMade()` _(in MoneyAudit.Result)_ | What the month did to M0, as the audit saw it cross the edge. |
| 116 | 1 | `public double tradeBalance()` _(in MoneyAudit.Result)_ | Exports less imports. |
| 119 | 1 | `public double incomeBalance()` _(in MoneyAudit.Result)_ | What the city earns on foreign assets, less what it pays on foreign debts. |
| 122 | 1 | `public double currentAccount()` _(in MoneyAudit.Result)_ | The two together. |
| 125 | 1 | `public double financialAccount()` _(in MoneyAudit.Result)_ | Capital in less capital out. |
| 131 | 1 | `public double valuationChange()` _(in MoneyAudit.Result)_ | Claims forgiven or settled. |
| 140 | 1 | `public double foreignBalance()` _(in MoneyAudit.Result)_ | What the city's foreign position moved by this month. |
| 143 | 3 | `public double foreignIn()` _(in MoneyAudit.Result)_ | Everything crossing the edge that is foreign, in each direction. |
| 146 | 3 | `public double foreignOut()` _(in MoneyAudit.Result)_ |  |
| 151 | 1 | `public double reserveChange()` _(in MoneyAudit.Result)_ | What the treasury did to its own reserve stock this month. |
| 159 | 1 | `public double domesticIn()` _(in MoneyAudit.Result)_ | ...and everything crossing it that is not. |
| 160 | 1 | `public double domesticOut()` _(in MoneyAudit.Result)_ |  |
| 162 | 3 | `Result(int month, double before, double after, double inflows, double outflows, double residual)` _(in MoneyAudit.Result)_ |  |
| 166 | 4 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreign)` _(in MoneyAudit.Result)_ |  |
| 171 | 25 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreig...` _(in MoneyAudit.Result)_ |  |
| 198 | 3 | `public double moved()` _(in MoneyAudit.Result)_ | What crossed the edge this month, both ways: the size the residual is read against (MoneyAudit.tolerance(), 0.7.54). |
| 203 | 4 | `public double relative()` _(in MoneyAudit.Result)_ | Residual as a share of what moved, so a $3 leak in a $3B city reads as 0. |
| 208 | 4 | `public String toString()` _(in MoneyAudit.Result)_ |  |
| 214 | 1 | `private MoneyAudit()` |  |
| 294 | 1 | **type** `public enum Scope` | WHICH SIDE OF WHICH BOUNDARY A FLOW CROSSES. |
| 297 | 3 | **type** `private interface Tagged` | Label, amount and scope, for one line of the month. |
| 298 | 1 | `double apply(String label, double amount, Scope scope)` _(in MoneyAudit.Tagged)_ |  |
| 311 | 9 | `private static String[] poolNames()` |  |
| 322 | 58 | `public static double[] pools(Game g)` | The pools, in POOL_NAMES order. |

### HOW CLOSE IS CLOSE ENOUGH, AT ANY SIZE (0.7.54) (lines 381-1153)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 3 | `public static double tolerance(double scale)` | How far two money figures of about this size may miss and still agree: CENT, or RELATIVE_TOLERANCE of the size, whichever is more. |
| 460 | 6 | `public static double tolerance(double floor, double scale)` | ...for a comparison held to a floor of its own: the floor while RELATIVE_TOLERANCE of the size is under a cent, and past that the larger of the two - and since 0.7.63 the floor never under ULP_STEPS of a double's step... |
| 472 | 5 | `public static double pooled(Game g)` | Every dollar in the pools: the city's, its businesses', the builders' order book, and the bank's - plus what it owes the window, less what it owes for the city's paper - and the city's fund's (0.7.14). |
| 486 | 3 | `static Result strike(Game g, double before, double interestDue)` | Strikes the month. |
| 491 | 662 | `static Result strike(Game g, double before, double[] poolsBefore, double interestDue)` | As above, and with the opening pools the result can say which pool moved unexplained. |

