# MoneyAudit.java - 1,662 lines · 42 methods · 8 constants · model

`ham/citybuildersim/MoneyAudit.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

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
> THE HOUSEHOLDS' IDENTITY (HL1, 2026-11-11)
> 
> The households are still outside the pools, but their savings are now
> reconciled against the DOMESTIC lines each month, a second strike beside the
> first (Households, householdsHeld()): the change in what they hold is what
> the pools paid them, less what they paid the pools, and the residual is money
> a household gained or lost that no line names. So a DOMESTIC line must be a
> household's - a line with no counterparty is not one, and is Scope.BOOK, as
> the write-offs' pools-side halves are - and a payment to a household, or from
> one to the world, has a line on each side, a pair, as the bonds' have. It is
> struck and kept (Game.getLastHouseholdsAudit()) and IS asserted now, by
> LongPlaytest.audit() and MoneyCheck, at the pools' own tolerance:
> 
>     held(after) - held(before) == (toHouseholds - fromHouseholds) + own
> 
> Two kinds of term close it besides the pools' DOMESTIC lines. (K) Money in
> transit goes INTO householdsHeld(): the pools pay or are credited in one
> press, the households are handed or charged the same money at their next
> ... (30 more lines in the source)

**Uses:** [Sector](Sector.md) (13), [Sectors](Sectors.md) (11), [Game](Game.md) (8), [Trade](Trade.md) (4), [EconomyManager](EconomyManager.md) (2), [SupplierCredit](SupplierCredit.md) (2), [Equity](Equity.md) (2), [OldMetal](OldMetal.md) (2), [PopulationManager](PopulationManager.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [PriceLevel](PriceLevel.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1), [Healthcare](Healthcare.md) (1), [Education](Education.md) (1), [BondMarket](BondMarket.md) (1), [CentralBank](CentralBank.md) (1)

**Used by (63):** [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [CoalMiningCheck](CoalMiningCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [EducationCheck](EducationCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [FittingCheck](FittingCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignAccounts](ForeignAccounts.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [FoundingYardCheck](FoundingYardCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HistoryCheck](HistoryCheck.md), [HoldersCheck](HoldersCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [MaterialsCheck](MaterialsCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [PortCheck](PortCheck.md), [PrizeCheck](PrizeCheck.md), [QuarryingCheck](QuarryingCheck.md), [RefineryCheck](RefineryCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [ShortSellingCheck](ShortSellingCheck.md), [SkipReportCheck](SkipReportCheck.md), [SlopeCheck](SlopeCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TreasuryCheck](TreasuryCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 680 | HOW CLOSE IS CLOSE ENOUGH, AT ANY SIZE (0.7.54) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 583 | `MoneyAudit.Scope.DOMESTIC` |  |
| 583 | `MoneyAudit.Scope.TRADE` |  |
| 583 | `MoneyAudit.Scope.INCOME` |  |
| 583 | `MoneyAudit.Scope.FINANCIAL` |  |
| 583 | `MoneyAudit.Scope.VALUATION` |  |
| 583 | `MoneyAudit.Scope.RESERVE` |  |
| 583 | `MoneyAudit.Scope.MONEY` |  |
| 583 | `MoneyAudit.Scope.BOOK` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `MoneyAudit.Result.NONE` | `new Result(0, 0, 0, 0, 0, 0)` |  |
| 310 | `MoneyAudit.Households.NONE` | `new Households(0, 0, 0, 0, 0, 0, "", null)` |  |
| 439 | `MoneyAudit.PUBLIC_PAYROLLS` | `{ "- bank Payroll", "- utilities UtilityPayroll", "- care Payroll", "- school...` | The strike's own labels for the public payrolls, whose sum the live public wages are set against (households()). |
| 444 | `MoneyAudit.OWN_LINES` | `{ "+ households Unfunded", "- households UnsettledIncome", "+ households Unse...` | The households' nine own lines (HL1) by the label households() notes them under, in the order it adds them; the first character is the sign each enters the own sum with. |
| 598 | `MoneyAudit.POOL_NAMES` | `poolNames()` | The pools, by name: the city, every sector in the registry's order, the builders' order book, the bank, and since 0.7.14 the city's fund (on the end, so every other pool keeps its place). |
| 737 | `MoneyAudit.CENT` | `.01 * PriceLevel.FACTOR` | A harness's cent: 0.01 of a unit of a thousand dollars at today's prices, times PriceLevel.FACTOR - the least a money comparison has allowed, priced at the founding's level (EP1, the ship gate's review: the cent staye... |
| 740 | `MoneyAudit.RELATIVE_TOLERANCE` | `1e-12` | ...and the share of the figures compared that their own rounding is allowed past it: a part in a trillion. |
| 753 | `MoneyAudit.ULP_STEPS` | `64` | ...and the least a floor allows, in a double's steps at the size of the figures compared (0.7.64; 8 in 0.7.63): the most an identity held to a floor under a cent was measured to miss by, in steps of the figures it is ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 101 | `public final int month` |  |
| 102 | `public final double before` |  |
| 103 | `public final double after` |  |
| 104 | `public final double inflows` |  |
| 105 | `public final double outflows` |  |
| 107 | `public final double residual` | (after - before) - (inflows - outflows). |
| 110 | `public final String detail` | Every pool and flow by name, for chasing a residual. |
| 119 | `public final java.util.Map<String, Double> figures` | The figures behind detail's lines, as the model recorded them (EP2): label to amount, in the order the lines were struck, a label struck twice keeping its first amount, and nothing for an amount of zero (detail prints... |
| 133 | `public final double[] poolsAtOpen` | The pools as this month's strike found them, and as it left them. |
| 134 | `public final double[] poolsAtClose` |  |
| 139 | `public final double tradeIn` | Goods and services sold abroad. |
| 141 | `public final double tradeOut` | ...and bought abroad. |
| 143 | `public final double incomeIn` | Interest received from foreign borrowers. |
| 145 | `public final double incomeOut` | ...and paid to foreign lenders. |
| 147 | `public final double financialIn` | Capital arriving from abroad. |
| 149 | `public final double financialOut` | ...and leaving. |
| 151 | `public final double valuationIn` | Foreign claims written off or settled, in the city's favour. |
| 153 | `public final double valuationOut` | ...and against it. |
| 155 | `public final double reserveIn` | Reserves sold for local money - the financing item, below the line. |
| 157 | `public final double reserveOut` | ...and bought with it. |
| 162 | `public final double moneyIn` | Money the central bank made and paid into the pools: advances, interest on reserves, the remittance. |
| 164 | `public final double moneyOut` | ...and money paid back to it, which it destroyed: repayments, and the window's and the advances' interest. |
| 169 | `public final double toHouseholds` | The sum of every DOMESTIC debit ("- ...") the strike recorded: what the pools paid households this month. |
| 171 | `public final double fromHouseholds` | The sum of every DOMESTIC credit ("+ ...") the strike recorded: what households paid the pools this month. |
| 312 | `public final int month` |  |
| 314 | `public final double before` | The households' savings as householdsHeld() read them at the top of the month. |
| 316 | `public final double after` | ...and at the strike. |
| 318 | `public final double toHouseholds` | What the pools paid households this month: Result.toHouseholds. |
| 320 | `public final double fromHouseholds` | What households paid the pools this month: Result.fromHouseholds. |
| 326 | `public final double own` | The households' own lines, signed: money their own settling made or lost with no pool on the other side. |
| 328 | `public final String detail` | The own lines by name, one to a line that is not zero, as Result.detail prints the pools'. |
| 330 | `public final java.util.Map<String, Double> figures` | The figures behind detail's lines, unrounded, label to amount, as Result.figures are (the label's sign is its direction; PublicWagesRestruck's amount is signed). |
| 332 | `public final double residual` | (after - before) - (toHouseholds - fromHouseholds) - own. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 95 | 1568 | **type** `public final class MoneyAudit` | Where the money went this month, and whether it all went somewhere. |
| 98 | 198 | **type** `public static final class Result` | One month's strike. |
| 174 | 1 | `public double moneyMade()` _(in MoneyAudit.Result)_ | What the month did to M0, as the audit saw it cross the edge. |
| 177 | 1 | `public double tradeBalance()` _(in MoneyAudit.Result)_ | Exports less imports. |
| 180 | 1 | `public double incomeBalance()` _(in MoneyAudit.Result)_ | What the city earns on foreign assets, less what it pays on foreign debts. |
| 183 | 1 | `public double currentAccount()` _(in MoneyAudit.Result)_ | The two together. |
| 186 | 1 | `public double financialAccount()` _(in MoneyAudit.Result)_ | Capital in less capital out. |
| 192 | 1 | `public double valuationChange()` _(in MoneyAudit.Result)_ | Claims forgiven or settled. |
| 201 | 1 | `public double foreignBalance()` _(in MoneyAudit.Result)_ | What the city's foreign position moved by this month. |
| 204 | 3 | `public double foreignIn()` _(in MoneyAudit.Result)_ | Everything crossing the edge that is foreign, in each direction. |
| 207 | 3 | `public double foreignOut()` _(in MoneyAudit.Result)_ |  |
| 212 | 1 | `public double reserveChange()` _(in MoneyAudit.Result)_ | What the treasury did to its own reserve stock this month. |
| 220 | 1 | `public double domesticIn()` _(in MoneyAudit.Result)_ | ...and everything crossing it that is not. |
| 221 | 1 | `public double domesticOut()` _(in MoneyAudit.Result)_ |  |
| 223 | 3 | `Result(int month, double before, double after, double inflows, double outflows, double residual)` _(in MoneyAudit.Result)_ |  |
| 227 | 4 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreign)` _(in MoneyAudit.Result)_ |  |
| 232 | 5 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreig...` _(in MoneyAudit.Result)_ |  |
| 238 | 5 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreig...` _(in MoneyAudit.Result)_ |  |
| 244 | 29 | `Result(int month, double before, double after, double inflows, double outflows, double residual, String detail, double[] foreig...` _(in MoneyAudit.Result)_ |  |
| 275 | 4 | `public double figure(String label)` _(in MoneyAudit.Result)_ | The figure detail printed for this label, unrounded; 0 when no such line was struck (EP2). |
| 281 | 3 | `public double moved()` _(in MoneyAudit.Result)_ | What crossed the edge this month, both ways: the size the residual is read against (MoneyAudit.tolerance(), 0.7.54). |
| 286 | 4 | `public double relative()` _(in MoneyAudit.Result)_ | Residual as a share of what moved, so a $3 leak in a $3B city reads as 0. |
| 291 | 4 | `public String toString()` _(in MoneyAudit.Result)_ |  |
| 309 | 60 | **type** `public static final class Households` | One month's strike of the HOUSEHOLDS' identity (HL1, 2026-11-11), the second the audit makes, beside the pools': |
| 334 | 13 | `Households(int month, double before, double after, double toHouseholds, double fromHouseholds, double own, String detail, java....` _(in MoneyAudit.Households)_ |  |
| 349 | 4 | `public double figure(String label)` _(in MoneyAudit.Households)_ | The figure detail printed for this own line, unrounded; 0 when no such line was struck. |
| 355 | 3 | `public double moved()` _(in MoneyAudit.Households)_ | What crossed between the pools and the households this month, both ways: the size the residual is read against. |
| 360 | 3 | `public double relative()` _(in MoneyAudit.Households)_ | Residual as a share of what moved, as Result.relative() reads the pools' (what moved is held to at least 1, so a month nothing moved in reads 0, not noise). |
| 364 | 4 | `public String toString()` _(in MoneyAudit.Households)_ |  |
| 380 | 3 | `public static double householdsHeld(Game g)` | The households' savings, as the identity above reads them (HL1, 2026-11-11): HouseholdBalance's total LESS what a buyback between two presses has already paid them and the next month has not yet declared - the same ti... |
| 394 | 36 | `static double householdsInTransit(Game g)` | What the pools have paid, or been paid, that the households' savings have not yet felt (HL1, 2026-11-11), signed as it adds to what they hold: the pools pay or are credited in ONE press, and the households are handed ... |
| 432 | 5 | `private static double publicWages(Game g)` | The public wages as the households are handed them live: everyone's wages less the sectors' own payrolls, what is left being the bank's, utilities', care's, schools', research's, safety's and transit's. |
| 453 | 31 | `static Households households(Game g, double before, Result pools)` | Strikes the households' identity for the month: householdsHeld() now, against the opening figure, the sums the pools' strike recorded, and the households' OWN lines (HL1): faults of the model's, named and measured her... |
| 486 | 7 | `private static double ownLine(StringBuilder detail, java.util.Map<String, Double> figures, String label, double amount, int sign)` | One own line: noted as Result.detail's lines are when it is not zero, and its share of the own sum returned (sign 1 adds the amount, -1 takes it off). |
| 494 | 1 | `private MoneyAudit()` |  |
| 583 | 1 | **type** `public enum Scope` | WHICH SIDE OF WHICH BOUNDARY A FLOW CROSSES. |
| 586 | 3 | **type** `private interface Tagged` | Label, amount and scope, for one line of the month. |
| 587 | 1 | `double apply(String label, double amount, Scope scope)` _(in MoneyAudit.Tagged)_ |  |
| 600 | 9 | `private static String[] poolNames()` |  |
| 611 | 68 | `public static double[] pools(Game g)` | The pools, in POOL_NAMES order. |

### HOW CLOSE IS CLOSE ENOUGH, AT ANY SIZE (0.7.54) (lines 680-1662)

| line | len | member | says |
|---:|---:|---|---|
| 756 | 3 | `public static double tolerance(double scale)` | How far two money figures of about this size may miss and still agree: CENT, or RELATIVE_TOLERANCE of the size, whichever is more. |
| 767 | 6 | `public static double tolerance(double floor, double scale)` | ...for a comparison held to a floor of its own: the floor while RELATIVE_TOLERANCE of the size is under a cent, and past that the larger of the two - and since 0.7.63 the floor never under ULP_STEPS of a double's step... |
| 779 | 5 | `public static double pooled(Game g)` | Every dollar in the pools: the city's, its businesses', the builders' order book, and the bank's - plus what it owes the window, less what it owes for the city's paper - and the city's fund's (0.7.14). |
| 793 | 3 | `static Result strike(Game g, double before, double interestDue)` | Strikes the month. |
| 798 | 847 | `static Result strike(Game g, double before, double[] poolsBefore, double interestDue)` | As above, and with the opening pools the result can say which pool moved unexplained. |
| 1647 | 8 | `static double oldMetalBoughtOfHouseholds(Sectors sectors)` | What the Scrap Yards' struck statements bought of the households' old steel (0.7.123; OldMetal): their input billed by Trade.HOUSEHOLDS, which nothing else is. |
| 1657 | 5 | `static double oldMetalInTransit(Sectors sectors)` | What the yards have bought of the households and paid into their savings (HouseholdBalance.creditScrappedCars()) that their books have not struck yet: in the savings, not yet out of the pools. |

