# ForeignAccounts.java - 2,563 lines · 166 methods · 35 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

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
> floats (repriceCurrency(), THE FLOAT), held as the city chooses since XR2
> (THE REGIMES: Float, Lean, Band, Peg); the treasury borrows in
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
> ... (31 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (34):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [DeskCheck](DeskCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutwardInvestment](OutwardInvestment.md), [PassThroughCheck](PassThroughCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 98 | · the rate |
| 110 | WHAT MOVES THE RATE |
| 246 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 288 | WHAT XR1 RETIRED (2026-10-21) |
| 380 | THE FLOAT (XR1, 2026-10-21) |
| 635 | A RESERVE DEFENDS A CURRENCY |
| 999 | THE REGIMES (XR2, 2026-10-22) |
| 1312 | · the month's account |
| 1320 | · the position |
| 1322 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1547 | · reading |
| 1549 | WHAT THE CITY OWES ABROAD |
| 1681 | BUYING AND SELLING THE RESERVE |
| 1779 | THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) |
| 1895 | · what the currency did to the vault |
| 1974 | WHAT THE TRADE TAB READS (0.7.35) |
| 2063 | · carrying |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1047 | `ForeignAccounts.Regime.FLOAT` |  |
| 1047 | `ForeignAccounts.Regime.LEAN` |  |
| 1047 | `ForeignAccounts.Regime.BAND` |  |
| 1047 | `ForeignAccounts.Regime.PEG` |  |
| 1054 | `ForeignAccounts.EdgePayer.CENTRAL_BANK` |  |
| 1054 | `ForeignAccounts.EdgePayer.TREASURY` |  |
| 1061 | `ForeignAccounts.BreakCause.NONE` |  |
| 1061 | `ForeignAccounts.BreakCause.VAULT` |  |
| 1061 | `ForeignAccounts.BreakCause.DOUBT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 108 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 129 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's deficit. |
| 132 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the month's deficit deep reserves can absorb. |
| 170 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 172 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 224 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 321 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 412 | `ForeignAccounts.TREND_LEVEL` | `1 / 6.0` | How much of a month's jump in parity the trend takes at once: Holt's level, so a steady inflation gap passes into the rate one for one and a single month's spike moves it a sixth of itself. |
| 415 | `ForeignAccounts.TREND_SLOPE` | `1 / 24.0` | ...and how fast its slope learns, a twenty-fourth of the level's change a month: about two years. |
| 418 | `ForeignAccounts.PERSISTENCE` | `2.5` | How many years the market expects a real rate gap to last, so a sustained point of gap holds the D$ about 2.5% dearer (or cheaper, when negative). |
| 421 | `ForeignAccounts.GAP_CAP` | `.35` | The most a real rate gap can move the level either way, a soft cap (tanh): markets do not believe a seventeen-point gap lasts. |
| 424 | `ForeignAccounts.GAP_IMPACT` | `.6` | The share of a change in the gap's level that the rate shows in its month: Kearns and Manners' 1.5% a point on the day (R16). |
| 427 | `ForeignAccounts.GAP_BUILD_HALF_LIFE` | `9` | ...and the rest builds with this half-life, in months, so the peak comes within one to three years: delayed overshooting (R15). |
| 430 | `ForeignAccounts.BALANCE_WEIGHT` | `.5` | How far a year's overall balance, against a year's GDP, moves the level: a deficit of a tenth of GDP cheapens the D$ 5%. |
| 433 | `ForeignAccounts.BALANCE_CAP` | `.20` | ...at most this far either way. |
| 436 | `ForeignAccounts.RISK_PREMIUM` | `.10` | An empty vault's discount on the D$, falling to nothing at THIN_COVER months of imports, which is its safe cover: one dial, one meaning (the oldest rule of thumb, R31). |
| 439 | `ForeignAccounts.MARKET_SPEED` | `.5` | How much of a change in the balance's and the cover's levels reaches the rate in its month. |
| 442 | `ForeignAccounts.MARKET_SIGMA` | `.022` | The market's own swing a month, about 8% a year: a real float's (Jerus, star 1). |
| 445 | `ForeignAccounts.MARKET_HALF_LIFE` | `42` | ...fading by half in this many months, the half-life of real currencies' gaps from parity (Rogoff, R1). |
| 448 | `ForeignAccounts.OPEN_ENOUGH` | `.10` | The openness, trade over GDP, at which the market and the fundamentals reach the rate in full; a less open city feels them in proportion. |
| 451 | `ForeignAccounts.INTERVENTION_IMPACT` | `2.0` | How far a purchase of dollars moves the rate: 2% per 1% of a year's GDP, a sale the other way (the IMF's 1.7-2.0%, R33; Jerus, star 7). |
| 454 | `ForeignAccounts.INTERVENTION_HALF_LIFE` | `6` | ...fading by half in this many months. |
| 1067 | `ForeignAccounts.BAND_WIDTH` | `.03` | Where a band opens, this share either side of its centre: the study's board 2 (est.; Singapore's band is thought to be about 2% either side, R36). |
| 1070 | `ForeignAccounts.MAX_BAND_WIDTH` | `.25` | The widest a band can be set, a share either side of the centre. |
| 1073 | `ForeignAccounts.MAX_BAND_SLOPE` | `.25` | The steepest a band's centre can crawl, a log a year, either way. |
| 1076 | `ForeignAccounts.VAULT_FLOOR` | `1` | Months of imports the vault must keep under a peg or a band: under it the hold gives way (the usual rule of thumb is three months of cover; the study's prototype breaks at one). |
| 1079 | `ForeignAccounts.OVERSHOOT` | `.25` | A broken peg lands at the shadow plus this share of the gap from the peg again, and fades as the market's swings do (RatesModel, the study's prototype). |
| 1082 | `ForeignAccounts.BREAK_BASE` | `.001` | A peg's chance of breaking by doubt in a month at no gap at all (0.1%). |
| 1085 | `ForeignAccounts.BREAK_SLOPE` | `.03` | ...rising by this much for each BREAK_GAP the shadow sits past the peg beyond the first BREAK_GAP (3% a month at a 20% gap). |
| 1088 | `ForeignAccounts.BREAK_GAP` | `.10` | ...the gap, a log, under which doubt adds nothing to BREAK_BASE (10%). |
| 1091 | `ForeignAccounts.PEG_SALT` | `0x5DEECE66D1B54A33L` | The salt that makes the peg's draws a stream of their own (odd). |
| 1430 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the float reads. |
| 2018 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the spec's B9): the drawer... |
| 2021 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 2037 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb - and since XR1 the cover ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 187 | `private double minRate` | The same three, in TODAY's money. |
| 188 | `private double maxRate` |  |
| 189 | `private double parityBase` |  |
| 227 | `private double parity` | Save slot 18. |
| 229 | `private double localInflation, worldInflation` |  |
| 285 | `private double rate` |  |
| 286 | `private double lastAbsorption` |  |
| 341 | `private double minTrade` | The same floor, in TODAY's money. |
| 370 | `private double realRateDifferential` | The real rate gap the month was handed (Game.realRateDifferential()): the city's real rate less the world's, a year - the dial less the inflation it expects, against the world's base rate less its realised inflation. |
| 467 | `private double trendLevel, trendSlope` | The float's state, every figure a log and all of it saved (slots 45-70): parity's trend (a level and a slope, from the first reprice), the real gap's level and how much of it the rate shows, the balance's and the cove... |
| 468 | `private boolean trendStarted` |  |
| 469 | `private double gapTarget, gapReached` |  |
| 470 | `private double balanceReached, coverReached` |  |
| 471 | `private double market` |  |
| 472 | `private double interventionFelt` |  |
| 473 | `private long marketSeed` |  |
| 474 | `private boolean marketSeeded` |  |
| 475 | `private long marketDraws` |  |
| 476 | `private double gdpTrailing` |  |
| 478 | `private final double[] lastContrib` | The five contributions last month (trend, gap, balance, cover, intervention): this month's move by force is the change from them. |
| 480 | `private final double[] moves` | This month's move by force (trend, gap, balance, cover, intervention, market, held); they add to the month's log move. |
| 482 | `private double lastWouldBeMove` | The month's fall against parity's trend before the defence leaned on it (repriceCurrency(), step 7). |
| 490 | `private double dealtSinceReprice` | What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: the player deals between months, and the next month's startMonth() clears boughtThisMonth and soldThisMonth before any r... |
| 714 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 745 | `private boolean pinned` |  |
| 1048 | `public final String words` |  |
| 1055 | `public final String words` |  |
| 1062 | `public final String words` |  |
| 1099 | `private Regime regime` | The regime in force. |
| 1101 | `private EdgePayer edgePayer` | Who pays at the strong edge. |
| 1103 | `private double bandCentre, bandSlope, bandWidth` | The band: its centre now (D$ per US$), the log a year the centre crawls, and the share either side the edges sit. |
| 1105 | `private double pegRate` | The peg, D$ per US$. |
| 1107 | `private double breakFelt` | What a break put on the rate and has not yet faded, in logs, positive weaker: the overshoot (repriceCurrency(), step 6). |
| 1109 | `private double lastShadowLog` | The log of the shadow rate at the last reprice, and whether that reprice held the rate away from it (a peg in force, a band at an edge). |
| 1110 | `private boolean heldLastMonth` |  |
| 1112 | `private long pegDraws` | How many draws the peg's chance has taken: a stream apart from the market's. |
| 1114 | `private double doubtShare, lastHazard, lastPegGap` | The month's break chance as the last reprice left it (what the hot money takes of itself home the next month), the last reprice's hazard, and the shadow's log gap from the peg then. |
| 1116 | `private double edgeBoughtUsd, edgeBoughtLocal` | The strong edge's dollars the vault took at the last reprice and their local price at the rate before it: the month's, cleared as the next reprice opens. |
| 1118 | `private double edgeBoughtUsdLifetime` | ...and since founding. |
| 1120 | `private double edgeOwedLocal` | The local price still owed for the dollars the edge bought, and who owes it as it stood when they were bought: settled at the top of the next month (Game.settleTheEdge()). |
| 1121 | `private EdgePayer edgeOwedPayer` |  |
| 1123 | `private int breaks` | Pegs broken since founding, and the last one's cause, jump (the log move of the rate that month) and shadow rate; whether the last reprice broke one. |
| 1124 | `private BreakCause lastBreakCause` |  |
| 1125 | `private double lastBreakJump, lastBreakShadow` |  |
| 1126 | `private boolean brokeThisMonth` |  |
| 1314 | `private double exports` |  |
| 1315 | `private double imports` |  |
| 1316 | `private double foreignInterest` |  |
| 1317 | `private double financialIn` |  |
| 1318 | `private double financialOut` |  |
| 1374 | `private double reservesUsd` |  |
| 1386 | `private double cumulativeBalance` | THE RECORD. |
| 1387 | `private double forgiven` |  |
| 1390 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 1391 | `private double currentTrailing` |  |
| 1398 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the rate can be struck on the OVERALL balance, and gate() can tell a city that trades from one that does not. |
| 1399 | `private double financialGrossTrailing` |  |
| 1413 | `private double lifetimeExports` | SINCE FOUNDING. |
| 1414 | `private double lifetimeImports` |  |
| 1415 | `private double lifetimeInterest` |  |
| 1416 | `private double lifetimeFinancial` |  |
| 1417 | `private double importsTrailing` |  |
| 1418 | `private int monthsOfHistory` |  |
| 1480 | `private double openness` | How much of the economy actually crosses the border. |
| 1572 | `private double foreignDebt` | local value of USD paper outstanding |
| 1573 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 1574 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 1575 | `private double lastRevaluation` |  |
| 1576 | `private double lifetimeRevaluation` |  |
| 1676 | `private double repudiated` |  |
| 1695 | `private double boughtThisMonth` |  |
| 1696 | `private double soldThisMonth` |  |
| 1821 | `private double landUsdOpen, landVaultUsdOpen` | The month in progress: dollars paid for land, and the part of them that came out of the vault. |
| 1828 | `private double landLocalOpen, landVaultLocalOpen` | ...and their local price on the day, all of it and the vault's part: the part of the budget's land line bought abroad (the rest of that line is plots bought back from businesses, in local money), and the part of it no... |
| 1830 | `private double landUsd, landVaultUsd, landLocal, landVaultLocal` | The month struck - what the screens show between two presses. |
| 1832 | `private double landUsdLifetime, landVaultUsdLifetime` | ...and since founding (slots 33-34). |
| 1915 | `private double lastVaultRate` |  |
| 1916 | `private double lastVaultRevaluation` |  |
| 1965 | `private double lifetimeIntervention` | What has been bought into the vault less what has been taken out of it, since founding, in the local money each cost or fetched: the treasury's purchases and sales, the defence's sales (0.7.2), the land office's dolla... |
| 1972 | `private double lastValuation` |  |
| 2003 | `private boolean monthCounted` | Whether the month's flows are the month's: false only after a founding or a load of an older save. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 96 | 2468 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 98-109)

### WHAT MOVES THE RATE (lines 110-287)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 1 | `public double getMinRate()` |  |
| 192 | 1 | `public double getMaxRate()` |  |
| 195 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 244 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates, and the level ratio parity is struck from. |
| 277 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 280 | 1 | `public double getLocalInflation()` | The two inflations the month was handed - the halves of the real rate differential, for the forces page. |
| 281 | 1 | `public double getWorldInflation()` |  |
| 283 | 1 | `public double getParity()` |  |

### WHAT XR1 RETIRED (2026-10-21) (lines 288-379)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 373 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 378 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |

### THE FLOAT (XR1, 2026-10-21) (lines 380-634)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 3 | `public static double gapLevelFor(double gap)` | Where a real rate gap puts the D$ against parity's trend, in logs: negative is dearer (the rate is D$ per US$, so a lower rate is a dearer D$), the gap's sign the other way round, and never past GAP_CAP either way. |
| 505 | 5 | `public double balanceTarget()` | Where the overall balance puts the D$ in logs, positive weaker: minus BALANCE_WEIGHT times the year's current plus financial account over the year's GDP, at most BALANCE_CAP either way - nothing before a GDP is known. |
| 512 | 3 | `public double coverTarget()` | Where the vault's cover puts the D$ in logs, positive weaker: RISK_PREMIUM times how far the cover falls short of THIN_COVER months of imports - the whole premium on an empty vault, none with no import bill to cover. |
| 517 | 4 | `public double gate()` | How much of the market and the fundamentals reaches the rate: nothing while the city trades no more than minTrade a month, else its openness over OPEN_ENOUGH, at most 1. |
| 523 | 3 | `private double trendLog()` | Parity's trend in logs: where the trend stands once started, parity's own log before the first reprice. |
| 528 | 1 | `public double getParityTrend()` | Parity's trend as a rate, local money per US dollar: the Holt level of parity, followed one for one; parity itself before the first reprice. |
| 531 | 1 | `public double getTrendSlope()` | The trend's slope, a log a month. |
| 534 | 1 | `public double getGapTarget()` | The real rate gap's level the market has priced, in logs (gapLevelFor() of the gap it was handed). |
| 537 | 1 | `public double getGapReached()` | ...and how much of it the rate shows so far: GAP_IMPACT of each change at once, the rest building. |
| 540 | 1 | `public double getBalanceReached()` | The balance's level as reached, in logs. |
| 543 | 1 | `public double getCoverReached()` | The cover's premium as reached, in logs. |
| 546 | 1 | `public double getMarket()` | The market's own swing, in logs, ungated; the rate carries gate() of it. |
| 549 | 1 | `public double getInterventionFelt()` | What the interventions still felt put on the rate, in logs, positive weaker: the treasury's purchases and the defence's sales, fading by half in INTERVENTION_HALF_LIFE months. |
| 552 | 3 | `public double getFairLevel()` | Where the fundamentals put the D$ now, local money per US dollar: the trend and gate() of the real gap's, the balance's and the cover's levels reached - the rate without the market and the interventions. |
| 557 | 4 | `public double deviationFromFair()` | How far the rate sits from the fundamentals' level: positive is weaker than it. |
| 563 | 1 | `public double getMoveTrend()` | Last month's move from parity's trend, in logs, positive weaker. |
| 565 | 1 | `public double getMoveGap()` | ...from the real rate gap. |
| 567 | 1 | `public double getMoveBalance()` | ...from the balance. |
| 569 | 1 | `public double getMoveCover()` | ...from the vault's cover. |
| 571 | 1 | `public double getMoveIntervention()` | ...from interventions, the treasury's and the defence's. |
| 573 | 1 | `public double getMoveMarket()` | ...from the market: what is left of the shadow rate's move once the five are taken, so the six and the hold always add to the month's move, through a pin or a guard. |
| 575 | 1 | `public double getMoveHeld()` | ...from the hold (XR2): the part of the move the vault's hold of a peg or a band's edge made, and in a month a peg gave way the peg giving way. |
| 578 | 5 | `public double getLastMove()` | Last month's move in the rate, in logs, positive weaker: the seven moves added. |
| 585 | 1 | `public double getLastWouldBeMove()` | The month's fall before the defence leaned on it: the move of the market and the fundamentals alone, parity's trend and the interventions left out - positive is a fall the vault, if it holds dollars, sells against (re... |
| 588 | 1 | `public long getMarketSeed()` | The seed the market's path is drawn from (52 bits, so it survives a double in the save). |
| 591 | 1 | `public boolean isMarketSeeded()` | Whether the market is drawn at all: a city is seeded at founding (Game.buildWorld()); a bare ForeignAccounts has a quiet market. |
| 594 | 1 | `public long getMarketDraws()` | How many draws the path has taken: one a month the rate is not pinned. |
| 597 | 1 | `public double getGdpTrailing()` | A year of the city's output as a rolling monthly mean, local money, the figure the balance and the interventions are weighed against. |
| 603 | 6 | `public void setMarketSeed(long seed)` | Seeds the market: one seed, one path (the nations study's D9). |
| 617 | 4 | `public void quietMarket()` | Quiets the market, beside pinRate() and for the same reason: FOR FIXTURES THAT MEASURE SOMETHING ELSE. |
| 623 | 3 | `private double draw()` | The market's next normal draw, a function of the seed and the draws taken - the same seed, the same path, however the months are grouped. |
| 628 | 6 | `private static long mix(long seed, long draws)` | SplitMix64's finaliser over seed x golden-gamma + draws: neighbouring draws are unrelated seeds. |

### A RESERVE DEFENDS A CURRENCY (lines 635-998)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 686 | 4 | `private double dollarsFor(double deficitUsd)` | The most the central bank sells against a month's deficit: the vault's capacity times the deficit, at most the vault; nothing on a month with no deficit. |
| 696 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 717 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 719 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 721 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 724 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 740 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 748 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 763 | 220 | `public void repriceCurrency()` | Moves the rate on the month just taken: THE FLOAT, in nine steps. |
| 985 | 1 | `private double clampRate(double r)` | A rate inside the numerical guards. |
| 988 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 991 | 1 | `public double getRate()` | Local currency per US dollar. |
| 994 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 997 | 1 | `public double toUsd(double local)` | ...and the other way. |

### THE REGIMES (XR2, 2026-10-22) (lines 999-1311)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 5 | **type** `public enum Regime` | The four ways a city can hold its D$ (XR2; the study's 3.6). |
| 1049 | 1 | `Regime(String words)` _(in ForeignAccounts.Regime)_ |  |
| 1053 | 5 | **type** `public enum EdgePayer` | Who pays the local price of the dollars the vault buys at the strong edge (star 6). |
| 1056 | 1 | `EdgePayer(String words)` _(in ForeignAccounts.EdgePayer)_ |  |
| 1060 | 5 | **type** `public enum BreakCause` | Why a peg broke: the vault could not pay the month's deficit, or doubt itself did it (star 4). |
| 1063 | 1 | `BreakCause(String words)` _(in ForeignAccounts.BreakCause)_ |  |
| 1094 | 3 | `public static double hazard(double gap)` | A peg's chance of breaking in a month by doubt, given the log of the shadow over the peg (pass it at least 0): BREAK_BASE, plus BREAK_SLOPE for each BREAK_GAP past BREAK_GAP. |
| 1139 | 12 | `public void setRegime(Regime r)` | Chooses the regime. |
| 1153 | 4 | `public void repeg(double newPeg)` | A devaluation (a higher peg, a weaker D$) or a revaluation, under a peg; nothing under another regime. |
| 1159 | 5 | `public void setBand(double centre, double slopeAYear, double width)` | Sets the band: its centre (D$ per US$), the log a year it crawls and the share either side the edges sit, each inside its limits. |
| 1166 | 3 | `public void setEdgePayer(EdgePayer p)` | Chooses who pays for the dollars the vault buys at the strong edge. |
| 1170 | 1 | `public Regime getRegime()` |  |
| 1171 | 1 | `public EdgePayer getEdgePayer()` |  |
| 1172 | 1 | `public double getPegRate()` |  |
| 1173 | 1 | `public double getBandCentre()` |  |
| 1175 | 1 | `public double getBandSlope()` | The log a year the band's centre crawls. |
| 1177 | 1 | `public double getBandWidth()` | The share either side of the centre the edges sit. |
| 1179 | 1 | `public double bandWeakEdge()` | The band's weak edge, D$ per US$ (a higher rate is a weaker D$): the centre and its width. |
| 1181 | 1 | `public double bandStrongEdge()` | ...and its strong edge. |
| 1188 | 1 | `public double getShadowRate()` | Where the float's rule put the D$ at the last reprice, D$ per US$: the rate itself unless that reprice held the rate away from it (a peg in force, a band at an edge). |
| 1191 | 1 | `public boolean isHeld()` | Whether the last reprice held the rate away from the shadow: a peg in force, a band at an edge. |
| 1200 | 5 | `public double currencyGap()` | How far the D$ sits past its target, a share, positive weaker: the shadow over the peg under a peg, over the band's centre under a band, and nothing under Lean and Float, which have no target to aim at (a call for Jer... |
| 1207 | 1 | `public double getLastHazard()` | A peg's chance of breaking by doubt at the last reprice (0 outside a peg). |
| 1209 | 1 | `public double getLastPegGap()` | ...and the log of the shadow over the peg then. |
| 1211 | 1 | `public double getDoubtShare()` | The share of the hot money that takes itself home in the coming month: the break chance the last reprice left, when the peg held with the shadow past it (Game hands it to CapitalFlows.setDoubt()). |
| 1213 | 1 | `public boolean brokeThisMonth()` | Whether the last reprice broke a peg. |
| 1215 | 1 | `public int getBreaks()` | Pegs broken since founding. |
| 1216 | 1 | `public BreakCause getLastBreakCause()` |  |
| 1218 | 1 | `public double getLastBreakJump()` | The last break's jump in the rate, in logs, that month. |
| 1220 | 1 | `public double getLastBreakShadow()` | ...and where the shadow stood then, D$ per US$. |
| 1223 | 3 | `public double vaultFloorUsd()` | The dollars the hold must not let the vault fall under: VAULT_FLOOR months of the trailing import bill at the rate; nothing with no imports. |
| 1228 | 5 | `public double breakLanding()` | Where a peg that broke now would land, D$ per US$: the shadow plus OVERSHOOT of its gap again (as the break itself does it); the rate itself outside a peg. |
| 1235 | 6 | `public double monthsToFloor()` | How many months the vault lasts at the last reprice's sales, to its floor, under a peg or a band that sold; infinity when it sold nothing. |
| 1243 | 3 | `public double dealEffect(double localPurchase)` | What a purchase of dollars for this much local money would put on the float's log rate (positive weaker; a sale the other way), at the real size: the interventions' channel. |
| 1248 | 1 | `public double getEdgeBoughtUsd()` | The dollars the strong edge bought at the last reprice, and their local price at the rate before it. |
| 1249 | 1 | `public double getEdgeBoughtLocal()` |  |
| 1251 | 1 | `public double getEdgeBoughtUsdLifetime()` | ...since founding, in dollars. |
| 1253 | 1 | `public double getEdgeOwedLocal()` | The local price still owed for them, and by whom: settled at the top of the next month. |
| 1254 | 1 | `public EdgePayer getEdgeOwedPayer()` |  |
| 1262 | 9 | `private void edgeBuy(double usd)` | The vault takes these dollars at once, at the month's rate before the reprice moves it (as sellToDefend() sells, so revalueVault() stays clean), and the local price is owed to its payer at the top of the next month. |
| 1277 | 5 | `public double takeEdgeOwed()` | Hands over what is owed for the dollars the edge bought, and clears it: called once, at the top of the month inside the audit's window. |
| 1291 | 5 | `public void noteEdgePaidByTreasury(double local)` | The treasury paid this much of what the edge owed, out of its cash: a reserve purchase of its own, so it joins the month's purchases (and MoneyAudit's "- treasury BoughtReserves" declares it) and the lifetime dealing. |
| 1307 | 4 | `public void noteEdgePaidByCentralBank(double local)` | The central bank paid this much of what the edge owed, in new money (CentralBank.buyDollarsForVault()): the lifetime dealing alone, the mirror of the defence's sales, which it has counted since 0.7.2 (FX8, 2026-10-22;... |

### the month's account (lines 1312-1319)

### the position (lines 1320-1321)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 1322-1546)

| line | len | member | says |
|---:|---:|---|---|
| 1441 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 1457 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 1460 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 1461 | 1 | `public double monthlyCurrentAccount()` |  |
| 1463 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 1482 | 1 | `public double getOpenness()` |  |
| 1484 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 1498 | 48 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 1547-1548)

### WHAT THE CITY OWES ABROAD (lines 1549-1680)

| line | len | member | says |
|---:|---:|---|---|
| 1582 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 1592 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 1593 | 1 | `public double getForeignDebtUsd()` |  |
| 1596 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 1597 | 1 | `public double getLifetimeRevaluation()` |  |
| 1607 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 1617 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 1620 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 1623 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 1626 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 1629 | 1 | `public double tradeBalance()` | Exports less imports. |
| 1632 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 1634 | 1 | `public double getFinancialIn()` |  |
| 1635 | 1 | `public double getFinancialOut()` |  |
| 1636 | 1 | `public double financialAccount()` |  |
| 1639 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 1645 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 1648 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 1658 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 1661 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 1670 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 1679 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 1681-1778)

| line | len | member | says |
|---:|---:|---|---|
| 1705 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 1716 | 1 | `public void forgetDealing()` | Forgets what has been dealt since the last reprice: the founders' dollars (Game.buildWorld()), an endowment the market never saw bought, and a fixture's vault, set up as a holding rather than a purchase under test. |
| 1719 | 1 | `public double getDealtSinceReprice()` | What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: what the next reprice feels (THE FLOAT, step 5). |
| 1734 | 7 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 1765 | 10 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1777 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) (lines 1779-1894)

| line | len | member | says |
|---:|---:|---|---|
| 1842 | 7 | `public double buyAndSpendDollarsForLand(double usd)` | Buys exactly these dollars at today's rate and pays them to the land's seller, in one movement: the vault is untouched, and nothing a reserve purchase would leave behind is left (see THE LAND OFFICE IS PAID IN DOLLARS). |
| 1857 | 13 | `public double spendReservesOnLand(double usd)` | Pays the land's seller out of the vault: at most what it holds, and everything it holds empties it exactly. |
| 1872 | 7 | `public void strikeLandMonth()` | Strikes the land's month where the government's books strike its local cost. |
| 1881 | 1 | `public double getLandUsdThisMonth()` | Dollars paid for land in the month last struck, both ways. |
| 1883 | 1 | `public double getLandUsdFromVaultThisMonth()` | ...of which out of the vault. |
| 1885 | 1 | `public double getLandLocalThisMonth()` | What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. |
| 1887 | 1 | `public double getLandLocalFromVaultThisMonth()` | ...and the vault's part of it - the part of the budget's land line no cash paid. |
| 1889 | 1 | `public double getLandUsdLifetime()` | Dollars paid for land since founding, both ways. |
| 1891 | 1 | `public double getLandUsdFromVaultLifetime()` | ...of which out of the vault. |
| 1893 | 1 | `public double getLandUsdPending()` | Dollars paid for land since the last strike - the month in progress. |

### what the currency did to the vault (lines 1895-1973)

| line | len | member | says |
|---:|---:|---|---|
| 1919 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1926 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1928 | 1 | `public double getBoughtThisMonth()` |  |
| 1929 | 1 | `public double getSoldThisMonth()` |  |
| 1931 | 1 | `public double getLifetimeExports()` |  |
| 1932 | 1 | `public double getLifetimeImports()` |  |
| 1933 | 1 | `public double getLifetimeInterest()` |  |
| 1934 | 1 | `public double getLifetimeFinancial()` |  |
| 1946 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1967 | 1 | `public double getLifetimeIntervention()` |  |
| 1970 | 1 | `public double valuationChange()` | This month's part of that. |

### WHAT THE TRADE TAB READS (0.7.35) (lines 1974-2062)

| line | len | member | says |
|---:|---:|---|---|
| 2006 | 1 | `public boolean isMonthCounted()` | True once the month's flows are the month's - a month taken since the founding, or a load of a save that carried them - and not nothing. |
| 2009 | 1 | `public double getLifetimeTradeBalance()` | Exports less imports since founding: the record on trade alone, without the income or the capital. |
| 2024 | 5 | `public static int parityLevel(double deviation)` | The verdict on a deviation from parity (deviationFromParity()), either side: 0 near, 1 at PARITY_WATCH or more, 2 at PARITY_FAR or more. |
| 2040 | 4 | `public static int coverLevel(double months)` | The verdict on months of cover: 2 under THIN_COVER, 1 under COMFORTABLE_COVER, 0 at or over it. |
| 2051 | 6 | `public double coverWith(double localChange)` | The cover the vault would give with this much local money's worth of dollars bought into it at today's rate (negative: sold out of it) - the Exchange card's "what it would do". |
| 2059 | 3 | `public double toCover(double months)` | What it would cost in local money, at today's rate, to bring the vault up to this many months of the trailing import bill; nothing when it is there already. |

### carrying (lines 2063-2563)

| line | len | member | says |
|---:|---:|---|---|
| 2075 | 179 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 2255 | 137 | `public void restore(double[] saved)` |  |
| 2394 | 4 | `private static int ordinalOr(double saved, int length, int fallback)` | A saved ordinal, or the fallback's when it is not a whole number inside 0 to length - 1. |
| 2399 | 75 | `public void reset()` |  |
| 2501 | 61 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

