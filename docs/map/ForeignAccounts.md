# ForeignAccounts.java - 2,028 lines · 123 methods · 26 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> The city's dealings with the rest of the world: the balance of payments, the
> reserve position, and the exchange rate.
> 
> ==================== WHY THIS IS NEARLY EMPTY ====================
> 
> Almost nothing here is calculated. The balance of payments is read straight
> off MoneyAudit, which has been computing it since the day it was written
> without anybody noticing - it tracks every flow crossing the city's edge and
> reconciles them to the cent every month.
> 
> What was missing was not the arithmetic but the BOUNDARY. "Outside the pools"
> bundled households, who are domestic and simply not modelled as a pool,
> together with the rest of the world. Tag those apart - see MoneyAudit.Scope -
> and the foreign subset of a list that already balances IS the balance of
> payments. This class is the running total of it.
> 
> That is the same move the bank rewrite kept making: find the identity that is
> already there rather than inventing a parallel mechanism that can drift from
> it. There is no second set of books here to disagree with the first.
> 
> ==================== PHASE ONE OF FOUR ====================
> 
> Jerus's plan, in order:
> 
>   1. THIS. The accounting only. Split the boundary, count the reserve, show
>      it on a screen. The exchange rate exists and is pinned at 1.00, and
>      NOTHING BEHAVES DIFFERENTLY - the point is to prove the split is honest
>      against MoneyAudit before anything depends on it.
>   2. The rate moves, on reserve adequacy. Import prices and export revenues
>      start converting through it, and wages get a cost-of-living drift.
>   3. Foreign debt: USD instruments beside the existing ones. This is what
>      closes the circular-capital hole, because the treasury finally has a
>      lender that is not its own bank.
>   4. Capital flows: the carry trade in, sudden stops out, straight into the
>      bank's deposits.
> 
> ALL FOUR ARE BUILT, and the list is kept as the plan it was. The rate
> floats (repriceCurrency(), THE FLOAT); the treasury borrows in
> dollars (WHAT THE CITY OWES ABROAD, and DebtManager's foreign paper); the
> hot money and the carry trade are CapitalFlows.
> 
> See claude/foreign-exchange-design.md.
> 
> ==================== WHAT A RESERVE IS, TODAY ====================
> 
> TWO NUMBERS, which were one number until the split (TWO NUMBERS THAT WERE
> ONE NUMBER, below).
> 
> THE VAULT is a holding: the US dollars the treasury chose to buy and has
> not yet sold. The treasury buying and selling moves it (buyReserves(),
> sellReserves()), since 0.7.2 the central bank selling it to defend the
> currency (A DEFENCE THAT SPENDS, at repriceCurrency()), and since 0.7.6
> the land office paid out of it when the player says so
> (spendReservesOnLand()); it cannot go below zero, and it is KEPT IN DOLLARS -
> reservesUsd - so everything local about it, what it is worth, what can be
> sold, the import cover and the net position, is those dollars at today's
> rate. When the currency moves the dollars stay put, and the move in their
> local value is booked beside the vault as a revaluation (revalueVault()),
> in the dollar debt's shape: not cash, and not an audit flow. A new city
> opens with the founders' dollars in it - US$25M on the default founding
> ... (27 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (31):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 94 | · the rate |
| 106 | WHAT MOVES THE RATE |
| 242 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 284 | WHAT XR1 RETIRED (2026-10-21) |
| 376 | THE FLOAT (XR1, 2026-10-21) |
| 627 | A RESERVE DEFENDS A CURRENCY |
| 870 | · the month's account |
| 878 | · the position |
| 880 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1105 | · reading |
| 1107 | WHAT THE CITY OWES ABROAD |
| 1239 | BUYING AND SELLING THE RESERVE |
| 1337 | THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) |
| 1453 | · what the currency did to the vault |
| 1525 | WHAT THE TRADE TAB READS (0.7.35) |
| 1614 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 125 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's deficit. |
| 128 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the month's deficit deep reserves can absorb. |
| 166 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 168 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 220 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 317 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 408 | `ForeignAccounts.TREND_LEVEL` | `1 / 6.0` | How much of a month's jump in parity the trend takes at once: Holt's level, so a steady inflation gap passes into the rate one for one and a single month's spike moves it a sixth of itself. |
| 411 | `ForeignAccounts.TREND_SLOPE` | `1 / 24.0` | ...and how fast its slope learns, a twenty-fourth of the level's change a month: about two years. |
| 414 | `ForeignAccounts.PERSISTENCE` | `2.5` | How many years the market expects a real rate gap to last, so a sustained point of gap holds the D$ about 2.5% dearer (or cheaper, when negative). |
| 417 | `ForeignAccounts.GAP_CAP` | `.35` | The most a real rate gap can move the level either way, a soft cap (tanh): markets do not believe a seventeen-point gap lasts. |
| 420 | `ForeignAccounts.GAP_IMPACT` | `.6` | The share of a change in the gap's level that the rate shows in its month: Kearns and Manners' 1.5% a point on the day (R16). |
| 423 | `ForeignAccounts.GAP_BUILD_HALF_LIFE` | `9` | ...and the rest builds with this half-life, in months, so the peak comes within one to three years: delayed overshooting (R15). |
| 426 | `ForeignAccounts.BALANCE_WEIGHT` | `.5` | How far a year's overall balance, against a year's GDP, moves the level: a deficit of a tenth of GDP cheapens the D$ 5%. |
| 429 | `ForeignAccounts.BALANCE_CAP` | `.20` | ...at most this far either way. |
| 432 | `ForeignAccounts.RISK_PREMIUM` | `.10` | An empty vault's discount on the D$, falling to nothing at THIN_COVER months of imports, which is its safe cover: one dial, one meaning (the oldest rule of thumb, R31). |
| 435 | `ForeignAccounts.MARKET_SPEED` | `.5` | How much of a change in the balance's and the cover's levels reaches the rate in its month. |
| 438 | `ForeignAccounts.MARKET_SIGMA` | `.022` | The market's own swing a month, about 8% a year: a real float's (Jerus, star 1). |
| 441 | `ForeignAccounts.MARKET_HALF_LIFE` | `42` | ...fading by half in this many months, the half-life of real currencies' gaps from parity (Rogoff, R1). |
| 444 | `ForeignAccounts.OPEN_ENOUGH` | `.10` | The openness, trade over GDP, at which the market and the fundamentals reach the rate in full; a less open city feels them in proportion. |
| 447 | `ForeignAccounts.INTERVENTION_IMPACT` | `2.0` | How far a purchase of dollars moves the rate: 2% per 1% of a year's GDP, a sale the other way (the IMF's 1.7-2.0%, R33; Jerus, star 7). |
| 450 | `ForeignAccounts.INTERVENTION_HALF_LIFE` | `6` | ...fading by half in this many months. |
| 988 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the float reads. |
| 1569 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the spec's B9): the drawer... |
| 1572 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 1588 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb - and since XR1 the cover ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 183 | `private double minRate` | The same three, in TODAY's money. |
| 184 | `private double maxRate` |  |
| 185 | `private double parityBase` |  |
| 223 | `private double parity` | Save slot 18. |
| 225 | `private double localInflation, worldInflation` |  |
| 281 | `private double rate` |  |
| 282 | `private double lastAbsorption` |  |
| 337 | `private double minTrade` | The same floor, in TODAY's money. |
| 366 | `private double realRateDifferential` | The real rate gap the month was handed (Game.realRateDifferential()): the city's real rate less the world's, a year - the dial less the inflation it expects, against the world's base rate less its realised inflation. |
| 462 | `private double trendLevel, trendSlope` | The float's state, every figure a log and all of it saved (slots 45-70): parity's trend (a level and a slope, from the first reprice), the real gap's level and how much of it the rate shows, the balance's and the cove... |
| 463 | `private boolean trendStarted` |  |
| 464 | `private double gapTarget, gapReached` |  |
| 465 | `private double balanceReached, coverReached` |  |
| 466 | `private double market` |  |
| 467 | `private double interventionFelt` |  |
| 468 | `private long marketSeed` |  |
| 469 | `private boolean marketSeeded` |  |
| 470 | `private long marketDraws` |  |
| 471 | `private double gdpTrailing` |  |
| 473 | `private final double[] lastContrib` | The five contributions last month (trend, gap, balance, cover, intervention): this month's move by force is the change from them. |
| 475 | `private final double[] moves` | This month's move by force (trend, gap, balance, cover, intervention, market); they add to the month's log move. |
| 477 | `private double lastWouldBeMove` | The month's fall against parity's trend before the defence leaned on it (repriceCurrency(), step 7). |
| 485 | `private double dealtSinceReprice` | What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: the player deals between months, and the next month's startMonth() clears boughtThisMonth and soldThisMonth before any r... |
| 706 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 737 | `private boolean pinned` |  |
| 872 | `private double exports` |  |
| 873 | `private double imports` |  |
| 874 | `private double foreignInterest` |  |
| 875 | `private double financialIn` |  |
| 876 | `private double financialOut` |  |
| 932 | `private double reservesUsd` |  |
| 944 | `private double cumulativeBalance` | THE RECORD. |
| 945 | `private double forgiven` |  |
| 948 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 949 | `private double currentTrailing` |  |
| 956 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the rate can be struck on the OVERALL balance, and gate() can tell a city that trades from one that does not. |
| 957 | `private double financialGrossTrailing` |  |
| 971 | `private double lifetimeExports` | SINCE FOUNDING. |
| 972 | `private double lifetimeImports` |  |
| 973 | `private double lifetimeInterest` |  |
| 974 | `private double lifetimeFinancial` |  |
| 975 | `private double importsTrailing` |  |
| 976 | `private int monthsOfHistory` |  |
| 1038 | `private double openness` | How much of the economy actually crosses the border. |
| 1130 | `private double foreignDebt` | local value of USD paper outstanding |
| 1131 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 1132 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 1133 | `private double lastRevaluation` |  |
| 1134 | `private double lifetimeRevaluation` |  |
| 1234 | `private double repudiated` |  |
| 1253 | `private double boughtThisMonth` |  |
| 1254 | `private double soldThisMonth` |  |
| 1379 | `private double landUsdOpen, landVaultUsdOpen` | The month in progress: dollars paid for land, and the part of them that came out of the vault. |
| 1386 | `private double landLocalOpen, landVaultLocalOpen` | ...and their local price on the day, all of it and the vault's part: the part of the budget's land line bought abroad (the rest of that line is plots bought back from businesses, in local money), and the part of it no... |
| 1388 | `private double landUsd, landVaultUsd, landLocal, landVaultLocal` | The month struck - what the screens show between two presses. |
| 1390 | `private double landUsdLifetime, landVaultUsdLifetime` | ...and since founding (slots 33-34). |
| 1473 | `private double lastVaultRate` |  |
| 1474 | `private double lastVaultRevaluation` |  |
| 1516 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 1523 | `private double lastValuation` |  |
| 1554 | `private boolean monthCounted` | Whether the month's flows are the month's: false only after a founding or a load of an older save. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1937 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 94-105)

### WHAT MOVES THE RATE (lines 106-283)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 1 | `public double getMinRate()` |  |
| 188 | 1 | `public double getMaxRate()` |  |
| 191 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 240 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates, and the level ratio parity is struck from. |
| 273 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 276 | 1 | `public double getLocalInflation()` | The two inflations the month was handed - the halves of the real rate differential, for the forces page. |
| 277 | 1 | `public double getWorldInflation()` |  |
| 279 | 1 | `public double getParity()` |  |

### WHAT XR1 RETIRED (2026-10-21) (lines 284-375)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 369 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 374 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |

### THE FLOAT (XR1, 2026-10-21) (lines 376-626)

| line | len | member | says |
|---:|---:|---|---|
| 488 | 3 | `public static double gapLevelFor(double gap)` | Where a real rate gap puts the D$ against parity's trend, in logs: negative is dearer (the rate is D$ per US$, so a lower rate is a dearer D$), the gap's sign the other way round, and never past GAP_CAP either way. |
| 500 | 5 | `public double balanceTarget()` | Where the overall balance puts the D$ in logs, positive weaker: minus BALANCE_WEIGHT times the year's current plus financial account over the year's GDP, at most BALANCE_CAP either way - nothing before a GDP is known. |
| 507 | 3 | `public double coverTarget()` | Where the vault's cover puts the D$ in logs, positive weaker: RISK_PREMIUM times how far the cover falls short of THIN_COVER months of imports - the whole premium on an empty vault, none with no import bill to cover. |
| 512 | 4 | `public double gate()` | How much of the market and the fundamentals reaches the rate: nothing while the city trades no more than minTrade a month, else its openness over OPEN_ENOUGH, at most 1. |
| 518 | 3 | `private double trendLog()` | Parity's trend in logs: where the trend stands once started, parity's own log before the first reprice. |
| 523 | 1 | `public double getParityTrend()` | Parity's trend as a rate, local money per US dollar: the Holt level of parity, followed one for one; parity itself before the first reprice. |
| 526 | 1 | `public double getTrendSlope()` | The trend's slope, a log a month. |
| 529 | 1 | `public double getGapTarget()` | The real rate gap's level the market has priced, in logs (gapLevelFor() of the gap it was handed). |
| 532 | 1 | `public double getGapReached()` | ...and how much of it the rate shows so far: GAP_IMPACT of each change at once, the rest building. |
| 535 | 1 | `public double getBalanceReached()` | The balance's level as reached, in logs. |
| 538 | 1 | `public double getCoverReached()` | The cover's premium as reached, in logs. |
| 541 | 1 | `public double getMarket()` | The market's own swing, in logs, ungated; the rate carries gate() of it. |
| 544 | 1 | `public double getInterventionFelt()` | What the interventions still felt put on the rate, in logs, positive weaker: the treasury's purchases and the defence's sales, fading by half in INTERVENTION_HALF_LIFE months. |
| 547 | 3 | `public double getFairLevel()` | Where the fundamentals put the D$ now, local money per US dollar: the trend and gate() of the real gap's, the balance's and the cover's levels reached - the rate without the market and the interventions. |
| 552 | 4 | `public double deviationFromFair()` | How far the rate sits from the fundamentals' level: positive is weaker than it. |
| 558 | 1 | `public double getMoveTrend()` | Last month's move from parity's trend, in logs, positive weaker. |
| 560 | 1 | `public double getMoveGap()` | ...from the real rate gap. |
| 562 | 1 | `public double getMoveBalance()` | ...from the balance. |
| 564 | 1 | `public double getMoveCover()` | ...from the vault's cover. |
| 566 | 1 | `public double getMoveIntervention()` | ...from interventions, the treasury's and the defence's. |
| 568 | 1 | `public double getMoveMarket()` | ...from the market: what is left once the five are taken, so the six always add to the month's move, through a pin or a guard. |
| 571 | 5 | `public double getLastMove()` | Last month's move in the rate, in logs, positive weaker: the six moves added. |
| 578 | 1 | `public double getLastWouldBeMove()` | The month's fall before the defence leaned on it: the move of the market and the fundamentals alone, parity's trend and the interventions left out - positive is a fall the vault, if it holds dollars, sells against (re... |
| 581 | 1 | `public long getMarketSeed()` | The seed the market's path is drawn from (52 bits, so it survives a double in the save). |
| 584 | 1 | `public boolean isMarketSeeded()` | Whether the market is drawn at all: a city is seeded at founding (Game.buildWorld()); a bare ForeignAccounts has a quiet market. |
| 587 | 1 | `public long getMarketDraws()` | How many draws the path has taken: one a month the rate is not pinned. |
| 590 | 1 | `public double getGdpTrailing()` | A year of the city's output as a rolling monthly mean, local money, the figure the balance and the interventions are weighed against. |
| 596 | 5 | `public void setMarketSeed(long seed)` | Seeds the market: one seed, one path (the nations study's D9). |
| 609 | 4 | `public void quietMarket()` | Quiets the market, beside pinRate() and for the same reason: FOR FIXTURES THAT MEASURE SOMETHING ELSE. |
| 615 | 3 | `private double draw()` | The market's next normal draw, a function of the seed and the draws taken - the same seed, the same path, however the months are grouped. |
| 620 | 6 | `private static long mix(long seed, long draws)` | SplitMix64's finaliser over seed x golden-gamma + draws: neighbouring draws are unrelated seeds. |

### A RESERVE DEFENDS A CURRENCY (lines 627-869)

| line | len | member | says |
|---:|---:|---|---|
| 672 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 678 | 4 | `private double dollarsFor(double deficitUsd)` | The most the central bank sells against a month's deficit: the vault's capacity times the deficit, at most the vault; nothing on a month with no deficit. |
| 688 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 709 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 711 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 713 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 716 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 732 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 740 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 749 | 108 | `public void repriceCurrency()` | Moves the rate on the month just taken: THE FLOAT, in nine steps. |
| 859 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 862 | 1 | `public double getRate()` | Local currency per US dollar. |
| 865 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 868 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 870-877)

### the position (lines 878-879)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 880-1104)

| line | len | member | says |
|---:|---:|---|---|
| 999 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 1015 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 1018 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 1019 | 1 | `public double monthlyCurrentAccount()` |  |
| 1021 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 1040 | 1 | `public double getOpenness()` |  |
| 1042 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 1056 | 48 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 1105-1106)

### WHAT THE CITY OWES ABROAD (lines 1107-1238)

| line | len | member | says |
|---:|---:|---|---|
| 1140 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 1150 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 1151 | 1 | `public double getForeignDebtUsd()` |  |
| 1154 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 1155 | 1 | `public double getLifetimeRevaluation()` |  |
| 1165 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 1175 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 1178 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 1181 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 1184 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 1187 | 1 | `public double tradeBalance()` | Exports less imports. |
| 1190 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 1192 | 1 | `public double getFinancialIn()` |  |
| 1193 | 1 | `public double getFinancialOut()` |  |
| 1194 | 1 | `public double financialAccount()` |  |
| 1197 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 1203 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 1206 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 1216 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 1219 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 1228 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 1237 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 1239-1336)

| line | len | member | says |
|---:|---:|---|---|
| 1263 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 1274 | 1 | `public void forgetDealing()` | Forgets what has been dealt since the last reprice: the founders' dollars (Game.buildWorld()), an endowment the market never saw bought, and a fixture's vault, set up as a holding rather than a purchase under test. |
| 1277 | 1 | `public double getDealtSinceReprice()` | What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: what the next reprice feels (THE FLOAT, step 5). |
| 1292 | 7 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 1323 | 10 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1335 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) (lines 1337-1452)

| line | len | member | says |
|---:|---:|---|---|
| 1400 | 7 | `public double buyAndSpendDollarsForLand(double usd)` | Buys exactly these dollars at today's rate and pays them to the land's seller, in one movement: the vault is untouched, and nothing a reserve purchase would leave behind is left (see THE LAND OFFICE IS PAID IN DOLLARS). |
| 1415 | 13 | `public double spendReservesOnLand(double usd)` | Pays the land's seller out of the vault: at most what it holds, and everything it holds empties it exactly. |
| 1430 | 7 | `public void strikeLandMonth()` | Strikes the land's month where the government's books strike its local cost. |
| 1439 | 1 | `public double getLandUsdThisMonth()` | Dollars paid for land in the month last struck, both ways. |
| 1441 | 1 | `public double getLandUsdFromVaultThisMonth()` | ...of which out of the vault. |
| 1443 | 1 | `public double getLandLocalThisMonth()` | What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. |
| 1445 | 1 | `public double getLandLocalFromVaultThisMonth()` | ...and the vault's part of it - the part of the budget's land line no cash paid. |
| 1447 | 1 | `public double getLandUsdLifetime()` | Dollars paid for land since founding, both ways. |
| 1449 | 1 | `public double getLandUsdFromVaultLifetime()` | ...of which out of the vault. |
| 1451 | 1 | `public double getLandUsdPending()` | Dollars paid for land since the last strike - the month in progress. |

### what the currency did to the vault (lines 1453-1524)

| line | len | member | says |
|---:|---:|---|---|
| 1477 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1484 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1486 | 1 | `public double getBoughtThisMonth()` |  |
| 1487 | 1 | `public double getSoldThisMonth()` |  |
| 1489 | 1 | `public double getLifetimeExports()` |  |
| 1490 | 1 | `public double getLifetimeImports()` |  |
| 1491 | 1 | `public double getLifetimeInterest()` |  |
| 1492 | 1 | `public double getLifetimeFinancial()` |  |
| 1504 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1518 | 1 | `public double getLifetimeIntervention()` |  |
| 1521 | 1 | `public double valuationChange()` | This month's part of that. |

### WHAT THE TRADE TAB READS (0.7.35) (lines 1525-1613)

| line | len | member | says |
|---:|---:|---|---|
| 1557 | 1 | `public boolean isMonthCounted()` | True once the month's flows are the month's - a month taken since the founding, or a load of a save that carried them - and not nothing. |
| 1560 | 1 | `public double getLifetimeTradeBalance()` | Exports less imports since founding: the record on trade alone, without the income or the capital. |
| 1575 | 5 | `public static int parityLevel(double deviation)` | The verdict on a deviation from parity (deviationFromParity()), either side: 0 near, 1 at PARITY_WATCH or more, 2 at PARITY_FAR or more. |
| 1591 | 4 | `public static int coverLevel(double months)` | The verdict on months of cover: 2 under THIN_COVER, 1 under COMFORTABLE_COVER, 0 at or over it. |
| 1602 | 6 | `public double coverWith(double localChange)` | The cover the vault would give with this much local money's worth of dollars bought into it at today's rate (negative: sold out of it) - the Exchange card's "what it would do". |
| 1610 | 3 | `public double toCover(double months)` | What it would cost in local money, at today's rate, to bring the vault up to this many months of the trailing import bill; nothing when it is there already. |

### carrying (lines 1614-2028)

| line | len | member | says |
|---:|---:|---|---|
| 1626 | 154 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 1781 | 108 | `public void restore(double[] saved)` |  |
| 1890 | 58 | `public void reset()` |  |
| 1975 | 52 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

