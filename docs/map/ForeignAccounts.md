# ForeignAccounts.java - 3,488 lines · 225 methods · 49 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
> the US$ value of its eight balances (getReservesUsd(), THE VAULT IN EIGHT) - so everything local about it, what it is worth, what can be
> sold, the import cover and the net position, is those dollars at today's
> rate. When the currency moves the dollars stay put, and the move in their
> local value is booked beside the vault as a revaluation (revalueVault()),
> in the dollar debt's shape: not cash, and not an audit flow. A new city
> ... (33 more lines in the source)

**Uses:** [WorldMoneys](WorldMoneys.md) (7), [MoneyAudit](MoneyAudit.md) (2), [PriceLevel](PriceLevel.md) (1)

**Used by (55):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DeskCheck](DeskCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [InterestAbroadCheck](InterestAbroadCheck.md), [InterventionCheck](InterventionCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandNationsCheck](LandNationsCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OutwardInvestment](OutwardInvestment.md), [PartnersCheck](PartnersCheck.md), [PartnersView](PartnersView.md), [PassThroughCheck](PassThroughCheck.md), [PegAnchorCheck](PegAnchorCheck.md), [PolicyScreen](PolicyScreen.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [Rollover](Rollover.md), [SaveFileCheck](SaveFileCheck.md), [SavedCities](SavedCities.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [VaultMoneysCheck](VaultMoneysCheck.md), [VaultTable](VaultTable.md), [VaultView](VaultView.md), [WorldMoneys](WorldMoneys.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 100 | · the rate |
| 112 | WHAT MOVES THE RATE |
| 248 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 290 | WHAT XR1 RETIRED (2026-10-21) |
| 382 | THE FLOAT (XR1, 2026-10-21) |
| 642 | THE WORLD REACHES THE D$ (WN4, 2026-10-30; XR3 folded in) |
| 767 | A RESERVE DEFENDS A CURRENCY |
| 1185 | THE REGIMES (XR2, 2026-10-22) |
| 1688 | · the month's account |
| 1696 | · the position |
| 1698 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1751 | THE VAULT IN EIGHT (WN4, 2026-10-30; spec-nations 5.2-5.4) |
| 2042 | · THE VAULT EARNS (XR4, 2026-10-31; spec-exchange-rates 3.8) |
| 2290 | · reading |
| 2292 | WHAT THE CITY OWES ABROAD |
| 2412 | BUYING AND SELLING THE RESERVE |
| 2537 | THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) |
| 2685 | · what the currency did to the vault |
| 2773 | WHAT THE TRADE TAB READS (0.7.35) |
| 2862 | · carrying |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1278 | `ForeignAccounts.Regime.FLOAT` |  |
| 1278 | `ForeignAccounts.Regime.LEAN` |  |
| 1278 | `ForeignAccounts.Regime.BAND` |  |
| 1278 | `ForeignAccounts.Regime.PEG` |  |
| 1285 | `ForeignAccounts.EdgePayer.CENTRAL_BANK` |  |
| 1285 | `ForeignAccounts.EdgePayer.TREASURY` |  |
| 1292 | `ForeignAccounts.BreakCause.NONE` |  |
| 1292 | `ForeignAccounts.BreakCause.VAULT` |  |
| 1292 | `ForeignAccounts.BreakCause.DOUBT` |  |
| 1809 | `ForeignAccounts.Mix.ALL_USD` |  |
| 1809 | `ForeignAccounts.Mix.MATCH_TRADE` |  |
| 1809 | `ForeignAccounts.Mix.WORLD_MIX` |  |
| 1810 | `ForeignAccounts.Mix.CHOSEN` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 131 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's deficit. |
| 134 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the month's deficit deep reserves can absorb. |
| 172 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 174 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 226 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 323 | `ForeignAccounts.MIN_TRADE` | `50 * PriceLevel.FACTOR` | Below this much trade a month, the exchange rate is not a real price. |
| 414 | `ForeignAccounts.TREND_LEVEL` | `1 / 6.0` | How much of a month's jump in parity the trend takes at once: Holt's level, so a steady inflation gap passes into the rate one for one and a single month's spike moves it a sixth of itself. |
| 417 | `ForeignAccounts.TREND_SLOPE` | `1 / 24.0` | ...and how fast its slope learns, a twenty-fourth of the level's change a month: about two years. |
| 420 | `ForeignAccounts.PERSISTENCE` | `2.5` | How many years the market expects a real rate gap to last, so a sustained point of gap holds the D$ about 2.5% dearer (or cheaper, when negative). |
| 423 | `ForeignAccounts.GAP_CAP` | `.35` | The most a real rate gap can move the level either way, a soft cap (tanh): markets do not believe a seventeen-point gap lasts. |
| 426 | `ForeignAccounts.GAP_IMPACT` | `.6` | The share of a change in the gap's level that the rate shows in its month: Kearns and Manners' 1.5% a point on the day (R16). |
| 429 | `ForeignAccounts.GAP_BUILD_HALF_LIFE` | `9` | ...and the rest builds with this half-life, in months, so the peak comes within one to three years: delayed overshooting (R15). |
| 432 | `ForeignAccounts.BALANCE_WEIGHT` | `.5` | How far a year's overall balance, against a year's GDP, moves the level: a deficit of a tenth of GDP cheapens the D$ 5%. |
| 435 | `ForeignAccounts.BALANCE_CAP` | `.20` | ...at most this far either way. |
| 438 | `ForeignAccounts.RISK_PREMIUM` | `.10` | An empty vault's discount on the D$, falling to nothing at THIN_COVER months of imports, which is its safe cover: one dial, one meaning (the oldest rule of thumb, R31). |
| 441 | `ForeignAccounts.MARKET_SPEED` | `.5` | How much of a change in the balance's and the cover's levels reaches the rate in its month. |
| 444 | `ForeignAccounts.MARKET_SIGMA` | `.022` | The market's own swing a month, about 8% a year: a real float's (Jerus, star 1). |
| 447 | `ForeignAccounts.MARKET_HALF_LIFE` | `42` | ...fading by half in this many months, the half-life of real currencies' gaps from parity (Rogoff, R1). |
| 450 | `ForeignAccounts.OPEN_ENOUGH` | `.10` | The openness, trade over GDP, at which the market and the fundamentals reach the rate in full; a less open city feels them in proportion. |
| 453 | `ForeignAccounts.INTERVENTION_IMPACT` | `2.0` | How far a purchase of dollars moves the rate: 2% per 1% of a year's GDP, a sale the other way (the IMF's 1.7-2.0%, R33; Jerus, star 7). |
| 456 | `ForeignAccounts.INTERVENTION_LINEAR` | `.10` | ...linear up to this share of a year's GDP and logarithmic past it, I x s0 x (1 + ln(s / s0)), so each further dollar moves the rate less (XI1, GD0 4.5: Jerus's Rotterdam bought 4.5 years of output in a month and the ... |
| 459 | `ForeignAccounts.INTERVENTION_HALF_LIFE` | `6` | ...fading by half in this many months. |
| 682 | `ForeignAccounts.RISK_OFF_BASE` | `.10` | A small open money's loading on the risk-off: +10% weaker in a risk-off of size one (the study's 3.5; the Swiss franc's and the yen's are the other way, WorldMoneys.FRANC and YEN). |
| 685 | `ForeignAccounts.HAVEN_COVER` | `6` | The months of import cover at which the vault's pull on the loading is whole: six, twice THIN_COVER (est.). |
| 688 | `ForeignAccounts.HAVEN_PULL` | `.10` | The most a deep vault takes off the loading (est.: with a surplus, six months of cover turns a small open money into a haven). |
| 691 | `ForeignAccounts.SURPLUS_PULL` | `.05` | What a year in surplus (the current plus financial account over the trailing year) takes off the loading (est.): six months of cover and a surplus read -0.05, the Swiss franc's. |
| 694 | `ForeignAccounts.EXPOSURE_WEIGHT` | `.5` | How much hot money and debt abroad add to the loading, per year's GDP of them (est.: a fifth of a year's GDP adds ten points). |
| 697 | `ForeignAccounts.RISK_OFF_LOADING_MIN` | `-.08` | The least the loading can be: the yen's, the most a haven rises (WorldMoneys.YEN). |
| 700 | `ForeignAccounts.RISK_OFF_LOADING_MAX` | `.30` | The most the loading can be (est.: the emerging floats' is .20). |
| 1298 | `ForeignAccounts.BAND_WIDTH` | `.03` | Where a band opens, this share either side of its centre: the study's board 2 (est.; Singapore's band is thought to be about 2% either side, R36). |
| 1301 | `ForeignAccounts.MAX_BAND_WIDTH` | `.25` | The widest a band can be set, a share either side of the centre. |
| 1304 | `ForeignAccounts.MAX_BAND_SLOPE` | `.25` | The steepest a band's centre can crawl, a log a year, either way. |
| 1307 | `ForeignAccounts.VAULT_FLOOR` | `1` | Months of imports the vault must keep under a peg or a band: under it the hold gives way (the usual rule of thumb is three months of cover; the study's prototype breaks at one). |
| 1310 | `ForeignAccounts.OVERSHOOT` | `.25` | A broken peg lands at the shadow plus this share of the gap from the peg again - past the shadow on the weak side where the D$ was weaker than the peg, on the strong side where it was stronger (XR5; the Swiss franc, 2... |
| 1313 | `ForeignAccounts.BREAK_BASE` | `.001` | A peg's chance of breaking by doubt in a month at no gap at all (0.1%). |
| 1316 | `ForeignAccounts.BREAK_SLOPE` | `.03` | ...rising by this much for each BREAK_GAP the shadow sits past the peg beyond the first BREAK_GAP (3% a month at a 20% gap). |
| 1319 | `ForeignAccounts.BREAK_GAP` | `.10` | ...the gap, a log, under which doubt adds nothing to BREAK_BASE (10%). |
| 1322 | `ForeignAccounts.PEG_SALT` | `0x5DEECE66D1B54A33L` | The salt that makes the peg's draws a stream of their own (odd). |
| 1531 | `ForeignAccounts.THIN_HOLD_MONTHS` | `24` | A vault that would hold a peg or a band for fewer months than this against a steady gap of BREAK_GAP is thin, and choosing one warns (XR5; Jerus, "Warn on a thin vault"): two years, the median peg's life among 125 cou... |
| 1790 | `ForeignAccounts.COFER_SHARES` | `{.567,.206,.050,.045,.021,.027,.021,.002 }` | The share of the world's reserves each of the eight holds, IMF COFER 2026Q2 [R23], in WorldMoneys.EIGHT's order, normalised when used: the US$ 56.7%, euro 20.6%, yen 5.0%, pound 4.5%, yuan 2.1%, Canadian dollar 2.7%, ... |
| 1793 | `ForeignAccounts.MONEYS` | `8` | How many moneys the vault keeps a balance in. |
| 1796 | `ForeignAccounts.MONEY_NAME` | `{ "the US dollar", "the euro", "the yen", "the pound", "the yuan", "the Canad...` | The money's name as a sentence has it ("the euro"), in WorldMoneys.EIGHT's order. |
| 1799 | `ForeignAccounts.MONEY_PLURAL` | `{ "US dollars", "euros", "yen", "pounds", "yuan", "Canadian dollars", "Austra...` | ...and what a purchase of it is ("euros"), for a button's words. |
| 1802 | `ForeignAccounts.MONEY_MARK` | `{ "US$", "€", "¥", "£", "CN¥", "C$", "A$", "CHF " }` | ...and the mark a figure in it is written with ("€"), in the eight's order: the same marks the trade screen prints (CD8: the decision log names the money a piece is sold in by it). |
| 2173 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the float reads. |
| 2817 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the spec's B9): the drawer... |
| 2820 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 2836 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb - and since XR1 the cover ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 189 | `private double minRate` | The same three, in TODAY's money. |
| 190 | `private double maxRate` |  |
| 191 | `private double parityBase` |  |
| 229 | `private double parity` | Save slot 18. |
| 231 | `private double localInflation, worldInflation` |  |
| 287 | `private double rate` |  |
| 288 | `private double lastAbsorption` |  |
| 343 | `private double minTrade` | The same floor, in TODAY's money. |
| 372 | `private double realRateDifferential` | The real rate gap the month was handed (Game.realRateDifferential()): the city's real rate less the world's, a year - the dial less the inflation it expects, against the world's base rate less its realised inflation. |
| 472 | `private double trendLevel, trendSlope` | The float's state, every figure a log and all of it saved (slots 45-70): parity's trend (a level and a slope, from the first reprice), the real gap's level and how much of it the rate shows, the balance's and the cove... |
| 473 | `private boolean trendStarted` |  |
| 474 | `private double gapTarget, gapReached` |  |
| 475 | `private double balanceReached, coverReached` |  |
| 476 | `private double market` |  |
| 477 | `private double interventionFelt` |  |
| 478 | `private long marketSeed` |  |
| 479 | `private boolean marketSeeded` |  |
| 480 | `private long marketDraws` |  |
| 481 | `private double gdpTrailing` |  |
| 483 | `private final double[] lastContrib` | The five contributions last month (trend, gap, balance, cover, intervention): this month's move by force is the change from them. |
| 485 | `private final double[] moves` | This month's move by force (trend, gap, balance, cover, intervention, market, held); they add to the month's log move. |
| 487 | `private double lastWouldBeMove` | The month's fall against parity's trend before the defence leaned on it (repriceCurrency(), step 7). |
| 495 | `private double dealtSinceReprice` | What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: the player deals between months, and the next month's startMonth() clears boughtThisMonth and soldThisMonth before any r... |
| 703 | `private double worldCycle, worldRiskOff, worldExposure` | What the world is this month, handed before the reprice (setWorld()): the dollar cycle, the risk-off level, and the city's exposure (the hot money held and the debt abroad), local money. |
| 705 | `private double totReached, totStep` | The city's own terms of trade reached (a log, positive weaker), the piece each of the next totLeft reprices adds, and how many of them are left. |
| 706 | `private int totLeft` |  |
| 708 | `private double worldReached, lastWorldContrib, moveWorld` | The world's level as the last reprice struck it (cycle, the loading x risk-off, terms of trade; a log, positive weaker, ungated), gate() of it as last month's contribution, and the move it made this month. |
| 710 | `private double worldLoading` | The risk-off loading the last reprice used. |
| 858 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 889 | `private boolean pinned` |  |
| 1279 | `public final String words` |  |
| 1286 | `public final String words` |  |
| 1293 | `public final String words` |  |
| 1330 | `private Regime regime` | The regime in force. |
| 1332 | `private EdgePayer edgePayer` | Who pays at the strong edge. |
| 1334 | `private double bandCentre, bandSlope, bandWidth` | The band: its centre now (D$ per US$), the log a year the centre crawls, and the share either side the edges sit. |
| 1336 | `private double pegRate` | The peg, D$ per unit of its anchor (D$ per US$ for the US$). |
| 1338 | `private int pegAnchor` | The money of the eight the peg is kept in, as an index into WorldMoneys.EIGHT (WN4b): 0 is the US$, which every city and save had. |
| 1340 | `private double breakFelt` | What a break put on the rate and has not yet faded, in logs, positive weaker, negative where the shadow was the stronger: the overshoot (repriceCurrency(), step 6). |
| 1349 | `private double holdFelt` | What the hold's own dealing - a peg's, a band's edges' - still puts on the rate and has not yet faded, in logs, positive weaker (a sale negative): the sign of interventionFelt and breakFelt (XR5, THE HOLD PAYS THE PRE... |
| 1351 | `private double lastShadowLog` | The log of the shadow rate at the last reprice, and whether that reprice held the rate away from it (a peg in force, a band at an edge). |
| 1352 | `private boolean heldLastMonth` |  |
| 1354 | `private long pegDraws` | How many draws the peg's chance has taken: a stream apart from the market's. |
| 1356 | `private double doubtShare, lastHazard, lastPegGap` | The month's break chance as the last reprice left it (what the hot money takes of itself home the next month), the last reprice's hazard, and the shadow's log gap from the peg then. |
| 1358 | `private double edgeBoughtUsd, edgeBoughtLocal` | The strong edge's dollars the vault took at the last reprice and their local price at the rate before it: the month's, cleared as the next reprice opens. |
| 1360 | `private double edgeBoughtUsdLifetime` | ...and since founding. |
| 1362 | `private double edgeOwedLocal` | The local price still owed for the dollars the edge bought, and who owes it as it stood when they were bought: settled at the top of the next month (Game.settleTheEdge()). |
| 1363 | `private EdgePayer edgeOwedPayer` |  |
| 1365 | `private int breaks` | Pegs broken since founding, and the last one's cause, jump (the log move of the rate that month) and shadow rate; whether the last reprice broke one. |
| 1366 | `private BreakCause lastBreakCause` |  |
| 1367 | `private double lastBreakJump, lastBreakShadow` |  |
| 1368 | `private boolean brokeThisMonth` |  |
| 1690 | `private double exports` |  |
| 1691 | `private double imports` |  |
| 1692 | `private double foreignInterest` |  |
| 1693 | `private double financialIn` |  |
| 1694 | `private double financialOut` |  |
| 1812 | `public final String words, inWords` | The chip's words, and what a decision's line adds when a purchase went in by it ("" for the default). |
| 1820 | `private final double[] vault` | What the vault holds of each money, in that money's own units. |
| 1822 | `private final double[] usdPer` | US$ to the unit of each, as Game handed them (1 for the US$, 0 until handed), and whether this month has a rate for it. |
| 1823 | `private final boolean[] rated` |  |
| 1824 | `private Mix mix` |  |
| 1826 | `private final double[] chosen` | The player's eight shares under CHOSEN, and the city's trade by money under MATCH_TRADE (Game hands it once a month): each sums to 1, the trade's to 0 when there is none. |
| 1827 | `private final double[] tradeShares` |  |
| 1829 | `private double crossUsd` | The month's move of the vault across the moneys in US$, whether revalueVault() has booked it, and the money that moved it most (its index, its move against the US$ as a fraction, the US$ it moved). |
| 1830 | `private boolean crossBooked` |  |
| 1831 | `private int crossDriver` |  |
| 1832 | `private double crossDriverMove, crossDriverUsd` |  |
| 2063 | `private double interestUsd, interestUsdLifetime` | What the vault earned this month and since founding, in US$ at the month's cross rates (negative where a bank's rate was below zero). |
| 2065 | `private int interestDriver` | The money that earned (or lost) the most this month by US$ (an index, -1 for none) and the US$ it did so, signed. |
| 2066 | `private double interestDriverUsd` |  |
| 2068 | `private double interestBaseUsd` | The US$ the vault had in the moneys that earned this month, before the interest: what the month's yield is struck on. |
| 2129 | `private double cumulativeBalance` | THE RECORD. |
| 2130 | `private double forgiven` |  |
| 2133 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 2134 | `private double currentTrailing` |  |
| 2141 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the rate can be struck on the OVERALL balance, and gate() can tell a city that trades from one that does not. |
| 2142 | `private double financialGrossTrailing` |  |
| 2156 | `private double lifetimeExports` | SINCE FOUNDING. |
| 2157 | `private double lifetimeImports` |  |
| 2158 | `private double lifetimeInterest` |  |
| 2159 | `private double lifetimeFinancial` |  |
| 2160 | `private double importsTrailing` |  |
| 2161 | `private int monthsOfHistory` |  |
| 2223 | `private double openness` | How much of the economy actually crosses the border. |
| 2323 | `private double foreignDebt` | local value of USD paper outstanding |
| 2324 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 2407 | `private double repudiated` |  |
| 2426 | `private double boughtThisMonth` |  |
| 2427 | `private double soldThisMonth` |  |
| 2579 | `private double landUsdOpen, landVaultUsdOpen` | The month in progress: dollars paid for land, and the part of them that came out of the vault. |
| 2586 | `private double landLocalOpen, landVaultLocalOpen` | ...and their local price on the day, all of it and the vault's part: the part of the budget's land line bought abroad (the rest of that line is plots bought back from businesses, in local money), and the part of it no... |
| 2588 | `private double landUsd, landVaultUsd, landLocal, landVaultLocal` | The month struck - what the screens show between two presses. |
| 2590 | `private double landUsdLifetime, landVaultUsdLifetime` | ...and since founding (slots 33-34). |
| 2705 | `private double lastVaultRate` |  |
| 2706 | `private double lastVaultRevaluation` |  |
| 2764 | `private double lifetimeIntervention` | What has been bought into the vault less what has been taken out of it, since founding, in the local money each cost or fetched: the treasury's purchases and sales, the defence's sales (0.7.2), the land office's dolla... |
| 2771 | `private double lastValuation` |  |
| 2802 | `private boolean monthCounted` | Whether the month's flows are the month's: false only after a founding or a load of an older save. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 98 | 3391 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 100-111)

### WHAT MOVES THE RATE (lines 112-289)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 1 | `public double getMinRate()` |  |
| 194 | 1 | `public double getMaxRate()` |  |
| 197 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 246 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates, and the level ratio parity is struck from. |
| 279 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 282 | 1 | `public double getLocalInflation()` | The two inflations the month was handed - the halves of the real rate differential, for the forces page. |
| 283 | 1 | `public double getWorldInflation()` |  |
| 285 | 1 | `public double getParity()` |  |

### WHAT XR1 RETIRED (2026-10-21) (lines 290-381)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 375 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 380 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |

### THE FLOAT (XR1, 2026-10-21) (lines 382-641)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 3 | `public static double gapLevelFor(double gap)` | Where a real rate gap puts the D$ against parity's trend, in logs: negative is dearer (the rate is D$ per US$, so a lower rate is a dearer D$), the gap's sign the other way round, and never past GAP_CAP either way. |
| 510 | 5 | `public double balanceTarget()` | Where the overall balance puts the D$ in logs, positive weaker: minus BALANCE_WEIGHT times the year's current plus financial account over the year's GDP, at most BALANCE_CAP either way - nothing before a GDP is known. |
| 517 | 3 | `public double coverTarget()` | Where the vault's cover puts the D$ in logs, positive weaker: RISK_PREMIUM times how far the cover falls short of THIN_COVER months of imports - the whole premium on an empty vault, none with no import bill to cover. |
| 522 | 4 | `public double gate()` | How much of the market and the fundamentals reaches the rate: nothing while the city trades no more than minTrade a month, else its openness over OPEN_ENOUGH, at most 1. |
| 528 | 3 | `private double trendLog()` | Parity's trend in logs: where the trend stands once started, parity's own log before the first reprice. |
| 533 | 1 | `public double getParityTrend()` | Parity's trend as a rate, local money per US dollar: the Holt level of parity, followed one for one; parity itself before the first reprice. |
| 536 | 1 | `public double getTrendSlope()` | The trend's slope, a log a month. |
| 539 | 1 | `public double getGapTarget()` | The real rate gap's level the market has priced, in logs (gapLevelFor() of the gap it was handed). |
| 542 | 1 | `public double getGapReached()` | ...and how much of it the rate shows so far: GAP_IMPACT of each change at once, the rest building. |
| 545 | 1 | `public double getBalanceReached()` | The balance's level as reached, in logs. |
| 548 | 1 | `public double getCoverReached()` | The cover's premium as reached, in logs. |
| 551 | 1 | `public double getMarket()` | The market's own swing, in logs, ungated; the rate carries gate() of it. |
| 554 | 1 | `public double getInterventionFelt()` | What the interventions still felt put on the rate, in logs, positive weaker: the treasury's purchases and the defence's sales, fading by half in INTERVENTION_HALF_LIFE months. |
| 557 | 3 | `public double getFairLevel()` | Where the fundamentals put the D$ now, local money per US dollar: the trend and gate() of the real gap's, the balance's and the cover's levels reached and the world's (WN4: the dollar cycle, the risk-off and the terms... |
| 562 | 4 | `public double deviationFromFair()` | How far the rate sits from the fundamentals' level: positive is weaker than it. |
| 568 | 1 | `public double getMoveTrend()` | Last month's move from parity's trend, in logs, positive weaker. |
| 570 | 1 | `public double getMoveGap()` | ...from the real rate gap. |
| 572 | 1 | `public double getMoveBalance()` | ...from the balance. |
| 574 | 1 | `public double getMoveCover()` | ...from the vault's cover. |
| 576 | 1 | `public double getMoveIntervention()` | ...from interventions, the treasury's and the defence's. |
| 578 | 1 | `public double getMoveMarket()` | ...from the market: what is left of the shadow rate's move once the five and the world's are taken, so the seven and the hold always add to the month's move, through a pin or a guard. |
| 580 | 1 | `public double getMoveWorld()` | ...from the world (WN4): the dollar's own swing against every money, money running to safety, and the prices of what the city sells abroad - gate() times the change in the world's level (THE WORLD REACHES THE D$). |
| 582 | 1 | `public double getMoveHeld()` | ...from the hold (XR2): the part of the move the vault's hold of a peg or a band's edge made, and in a month a peg gave way the peg giving way. |
| 585 | 5 | `public double getLastMove()` | Last month's move in the rate, in logs, positive weaker: the seven moves added (the six and the hold, and the world's). |
| 592 | 1 | `public double getLastWouldBeMove()` | The month's fall before the defence leaned on it: the move of the market and the fundamentals alone, parity's trend and the interventions left out - positive is a fall the vault, if it holds dollars, sells against (re... |
| 595 | 1 | `public long getMarketSeed()` | The seed the market's path is drawn from (52 bits, so it survives a double in the save). |
| 598 | 1 | `public boolean isMarketSeeded()` | Whether the market is drawn at all: a city is seeded at founding (Game.buildWorld()); a bare ForeignAccounts has a quiet market. |
| 601 | 1 | `public long getMarketDraws()` | How many draws the path has taken: one a month the rate is not pinned. |
| 604 | 1 | `public double getGdpTrailing()` | A year of the city's output as a rolling monthly mean, local money, the figure the balance and the interventions are weighed against. |
| 610 | 6 | `public void setMarketSeed(long seed)` | Seeds the market: one seed, one path (the nations study's D9). |
| 624 | 4 | `public void quietMarket()` | Quiets the market, beside pinRate() and for the same reason: FOR FIXTURES THAT MEASURE SOMETHING ELSE. |
| 630 | 3 | `private double draw()` | The market's next normal draw, a function of the seed and the draws taken - the same seed, the same path, however the months are grouped. |
| 635 | 6 | `private static long mix(long seed, long draws)` | SplitMix64's finaliser over seed x golden-gamma + draws: neighbouring draws are unrelated seeds. |

### THE WORLD REACHES THE D$ (WN4, 2026-10-30; XR3 folded in) (lines 642-766)

| line | len | member | says |
|---:|---:|---|---|
| 719 | 5 | `public void setWorld(double cycle, double riskOff, double exposureLocal)` | What the world is this month: handed before repriceCurrency(), by Game, from the world's moneys. |
| 732 | 6 | `public void stepTermsOfTrade(double exportPriceLevel)` | The city's terms of trade in a January: exportPriceLevel is the export-weighted ln of the world prices of what it sells, each against its normal (Game's, from PartnerTrade's exports by good over the year that finished... |
| 744 | 7 | `public double riskOffLoading()` | The D$'s loading on the risk-off from the city's traits now: RISK_OFF_BASE, less HAVEN_PULL for a vault of up to HAVEN_COVER months of imports, less SURPLUS_PULL for a trailing year in surplus (current plus financial ... |
| 753 | 1 | `public double getWorldCycle()` | The dollar cycle as last handed, a log. |
| 756 | 1 | `public double getWorldRiskOff()` | The risk-off level as last handed. |
| 759 | 1 | `public double getWorldLoading()` | The risk-off loading the last reprice used (riskOffLoading() as it stood then). |
| 762 | 1 | `public double getTermsOfTradeReached()` | The city's own terms of trade reached, a log, positive weaker. |
| 765 | 1 | `public double getWorldLevel()` | The world's level as the last reprice struck it, in logs, positive weaker, before the gate. |

### A RESERVE DEFENDS A CURRENCY (lines 767-1184)

| line | len | member | says |
|---:|---:|---|---|
| 812 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 818 | 5 | `private double dollarsFor(double deficitUsd)` | The most the central bank sells against a month's deficit: the vault's capacity times the deficit, at most the vault; nothing on a month with no deficit. |
| 831 | 3 | `private double holdUsd(double lift, double annual)` | The dollars holding the rate takes to put this lift (logs, either sign) on it against a year's GDP (annual): negative a sale, positive a purchase, by the interventions' rule (interventionLocalFor(), XI1) at the month'... |
| 840 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 861 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 863 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 865 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 868 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 884 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 892 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 907 | 262 | `public void repriceCurrency()` | Moves the rate on the month just taken: THE FLOAT, in nine steps. |
| 1171 | 1 | `private double clampRate(double r)` | A rate inside the numerical guards. |
| 1174 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 1177 | 1 | `public double getRate()` | Local currency per US dollar. |
| 1180 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 1183 | 1 | `public double toUsd(double local)` | ...and the other way. |

### THE REGIMES (XR2, 2026-10-22) (lines 1185-1687)

| line | len | member | says |
|---:|---:|---|---|
| 1277 | 5 | **type** `public enum Regime` | The four ways a city can hold its D$ (XR2; the study's 3.6). |
| 1280 | 1 | `Regime(String words)` _(in ForeignAccounts.Regime)_ |  |
| 1284 | 5 | **type** `public enum EdgePayer` | Who pays the local price of the dollars the vault buys at the strong edge (star 6). |
| 1287 | 1 | `EdgePayer(String words)` _(in ForeignAccounts.EdgePayer)_ |  |
| 1291 | 5 | **type** `public enum BreakCause` | Why a peg broke: holding it would take the vault under its floor (XR5: the sale the hold needs, not the month's deficit), or doubt itself did it (star 4). |
| 1294 | 1 | `BreakCause(String words)` _(in ForeignAccounts.BreakCause)_ |  |
| 1325 | 3 | `public static double hazard(double gap)` | A peg's chance of breaking in a month by doubt, given the log of the shadow over the peg (pass it at least 0): BREAK_BASE, plus BREAK_SLOPE for each BREAK_GAP past BREAK_GAP. |
| 1381 | 17 | `public void setRegime(Regime r)` | Chooses the regime. |
| 1404 | 4 | `public void repeg(double newPeg)` | A devaluation (a higher peg, a weaker D$) or a revaluation, under a peg; nothing under another regime. |
| 1416 | 7 | `public void setPegAnchor(int k)` | Chooses the money the peg is kept in (WN4b): one of the eight, by its index in WorldMoneys.EIGHT. |
| 1425 | 3 | `private double anchorUsd()` | The US$ price of the peg's anchor (WN4b): 1 for the US$; else the price Game last handed, 1 for a money with none, which cannot happen (setPegAnchor() and restore() never leave a non-US anchor without a rate). |
| 1430 | 3 | `private double heldRate()` | The rate the peg holds this month, D$ per US$ (WN4b): the peg over its anchor's US$ price; the peg itself for the US$. |
| 1435 | 5 | `public void setBand(double centre, double slopeAYear, double width)` | Sets the band: its centre (D$ per US$), the log a year it crawls and the share either side the edges sit, each inside its limits. |
| 1442 | 3 | `public void setEdgePayer(EdgePayer p)` | Chooses who pays for the dollars the vault buys at the strong edge. |
| 1446 | 1 | `public Regime getRegime()` |  |
| 1447 | 1 | `public EdgePayer getEdgePayer()` |  |
| 1449 | 1 | `public double getPegRate()` | The rate the peg holds this month, D$ per US$ (WN4b): the peg in its anchor's money over the anchor's US$ price; the peg itself for the US$. |
| 1451 | 1 | `public int getPegAnchor()` | The money the peg is kept in, an index into WorldMoneys.EIGHT (0 is the US$; WN4b). |
| 1453 | 1 | `public String getPegAnchorCode()` | ...and its code ("EUR"). |
| 1455 | 1 | `public double getPegPerAnchor()` | The peg itself, D$ per unit of its anchor (D$ per US$ for the US$; WN4b): what a devaluation changes, and what does not move as the anchor does against the US$. |
| 1457 | 1 | `public String regimeWords()` | The regime in words for a decision's line or a screen (WN4b): under a peg, whose ("Peg to the euro"); else the regime's own. |
| 1458 | 1 | `public double getBandCentre()` |  |
| 1460 | 1 | `public double getBandSlope()` | The log a year the band's centre crawls. |
| 1462 | 1 | `public double getBandWidth()` | The share either side of the centre the edges sit. |
| 1464 | 1 | `public double bandWeakEdge()` | The band's weak edge, D$ per US$ (a higher rate is a weaker D$): the centre and its width. |
| 1466 | 1 | `public double bandStrongEdge()` | ...and its strong edge. |
| 1473 | 1 | `public double getShadowRate()` | Where the float's rule put the D$ at the last reprice, D$ per US$: the rate itself unless that reprice held the rate away from it (a peg in force, a band at an edge). |
| 1476 | 1 | `public boolean isHeld()` | Whether the last reprice held the rate away from the shadow: a peg in force, a band that dealt at an edge and still feels it (XR5: its support fades, so inside the band the rate is the shadow plus holdFelt). |
| 1479 | 1 | `public double getHoldFelt()` | What the hold's dealing still puts on the log rate, positive weaker (a sale negative); 0 under Lean and Float (XR5). |
| 1488 | 5 | `public double currencyGap()` | How far the D$ sits past its target, a share, positive weaker: the shadow over the peg under a peg, over the band's centre under a band, and nothing under Lean and Float, which have no target to aim at (a call for Jer... |
| 1495 | 1 | `public double getLastHazard()` | A peg's chance of breaking by doubt at the last reprice (0 outside a peg). |
| 1497 | 1 | `public double getLastPegGap()` | ...and the log of the shadow over the peg then. |
| 1499 | 1 | `public double getDoubtShare()` | The share of the hot money that takes itself home in the coming month: the break chance the last reprice left, when the peg held with the shadow past it (Game hands it to CapitalFlows.setDoubt()). |
| 1501 | 1 | `public boolean brokeThisMonth()` | Whether the last reprice broke a peg. |
| 1503 | 1 | `public int getBreaks()` | Pegs broken since founding. |
| 1504 | 1 | `public BreakCause getLastBreakCause()` |  |
| 1506 | 1 | `public double getLastBreakJump()` | The last break's jump in the rate, in logs, that month. |
| 1508 | 1 | `public double getLastBreakShadow()` | ...and where the shadow stood then, D$ per US$. |
| 1511 | 3 | `public double vaultFloorUsd()` | The dollars the hold must not let the vault fall under: VAULT_FLOOR months of the trailing import bill at the rate; nothing with no imports. |
| 1516 | 5 | `public double breakLanding()` | Where a peg that broke now would land, D$ per US$: the shadow plus OVERSHOOT of its gap again, past it on whichever side it is (as the break itself does it); the rate itself outside a peg. |
| 1523 | 6 | `public double monthsToFloor()` | How many months the vault lasts at the last reprice's sales (the hold's, XR5: what holding the rate took), to its floor, under a peg or a band that sold; infinity when it sold nothing. |
| 1547 | 10 | `public double monthsHoldingGap(double gap)` | How many months the vault, above its floor, would hold the rate against a steady gap this wide (a log, the shadow weaker than the rate held), entered with no support (XR5, THE HOLD PAYS THE PRESSURE): the first month ... |
| 1559 | 3 | `public double dealEffect(double localPurchase)` | What a purchase of dollars for this much local money would put on the float's log rate (positive weaker; a sale the other way), at the real size: the interventions' channel, bounded past INTERVENTION_LINEAR of a year'... |
| 1564 | 1 | `public double yearsOfOutput(double local)` | This much local money as years of the city's output (12 x gdpTrailing); 0 with no GDP. |
| 1570 | 1 | `public double dealtYears()` | The net dealing since the last reprice as years of the city's output, a purchase positive: what the next reprice feels (THE FLOAT, step 5), and the sum the bound reads, not the deal in hand (XI2: several pieces each u... |
| 1573 | 1 | `public double yearsOfOutputWith(double localPurchase)` | ...and what it would be with one more deal of this much local money (a purchase positive, a sale negative) on top of it (XI2). |
| 1580 | 5 | `public double dealEffectOnTop(double localPurchase)` | What one more deal adds to the float's log rate on top of what has been dealt since the last reprice (XI2): the interventions' rule of the sum less the rule of what is dealt already, so a piece past the bound moves it... |
| 1587 | 1 | `public double dealFactor(double localPurchase)` | The factor, 1 or more, that one more deal of this size moves the float's rate by, whichever way (dealEffectOnTop(), exponentiated; the sign says weaker or stronger). |
| 1594 | 6 | `public double rateAfterDeal(double localPurchase)` | Where one more deal would leave the rate in its month, D$ per US$, the regime holding it (XI2): a peg stays at the peg (the float's rate behind it moves, and the vault deals to hold it - XR5), a band stops at its edge... |
| 1602 | 6 | `public boolean regimeHoldsDeal(double localPurchase)` | Whether the regime keeps this deal's move off the rate (XI2): a peg, whatever the deal, and a band once the move would carry the rate past an edge. |
| 1610 | 5 | `public static double interventionMove(double local, double annual)` | What dealing this much local money puts on the float's log rate against a year's GDP (annual), a purchase positive: INTERVENTION_IMPACT x local / annual up to INTERVENTION_LINEAR of the year, and past it INTERVENTION_... |
| 1617 | 5 | `public static double interventionLocalFor(double lift, double annual)` | ...and its inverse: the local money that puts this lift (logs, either sign) on the rate against a year's GDP. |
| 1624 | 1 | `public double getEdgeBoughtUsd()` | The dollars the strong edge bought at the last reprice, and their local price at the rate before it. |
| 1625 | 1 | `public double getEdgeBoughtLocal()` |  |
| 1627 | 1 | `public double getEdgeBoughtUsdLifetime()` | ...since founding, in dollars. |
| 1629 | 1 | `public double getEdgeOwedLocal()` | The local price still owed for them, and by whom: settled at the top of the next month. |
| 1630 | 1 | `public EdgePayer getEdgeOwedPayer()` |  |
| 1638 | 9 | `private void edgeBuy(double usd)` | The vault takes these dollars at once, at the month's rate before the reprice moves it (as sellToDefend() sells, so revalueVault() stays clean), and the local price is owed to its payer at the top of the next month. |
| 1653 | 5 | `public double takeEdgeOwed()` | Hands over what is owed for the dollars the edge bought, and clears it: called once, at the top of the month inside the audit's window. |
| 1667 | 5 | `public void noteEdgePaidByTreasury(double local)` | The treasury paid this much of what the edge owed, out of its cash: a reserve purchase of its own, so it joins the month's purchases (and MoneyAudit's "- treasury BoughtReserves" declares it) and the lifetime dealing. |
| 1683 | 4 | `public void noteEdgePaidByCentralBank(double local)` | The central bank paid this much of what the edge owed, in new money (CentralBank.buyDollarsForVault()): the lifetime dealing alone, the mirror of the defence's sales, which it has counted since 0.7.2 (FX8, 2026-10-22;... |

### the month's account (lines 1688-1695)

### the position (lines 1696-1697)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 1698-1750)

### THE VAULT IN EIGHT (WN4, 2026-10-30; spec-nations 5.2-5.4) (lines 1751-2041)

| line | len | member | says |
|---:|---:|---|---|
| 1805 | 1 | `public static int moneyIndex(String code)` | Which of the eight a code is, or -1 for a code that is none of them (or null). |
| 1808 | 10 | **type** `public enum Mix` | Where a dollar that comes into the vault goes: ALL_USD, today's behaviour and the default; MATCH_TRADE, the city's trade by money; WORLD_MIX, COFER's shares; CHOSEN, the player's eight. |
| 1813 | 4 | `Mix(String words, String inWords)` _(in ForeignAccounts.Mix)_ |  |
| 1834 | 5 | `{ ... }` |  |
| 1841 | 1 | `public double getHolding(int k)` | What the vault holds of one money, in its own units; 0 for an index that is none of the eight. |
| 1844 | 4 | `public double getHoldingUsd(int k)` | ...and what that is in US$ at the day's cross rate (the last one handed for a money that has none this month). |
| 1857 | 5 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself: the eight balances at the day's cross rates, summed. |
| 1864 | 1 | `public double getMoneyUsd(int k)` | US$ to the unit of a money as Game last handed it; NaN for one it has not been handed (and for an index that is none of the eight). |
| 1867 | 1 | `public boolean hasRate(int k)` | Whether the money has a rate this month, so a purchase can go into it: always the US$; the others once Game has handed a rate (a loaded city's are the rates it was saved with). |
| 1870 | 30 | `public void setMoneyRates(double[] usdPerUnit)` | Hands the eight's US$ per unit, in WorldMoneys.EIGHT's order (the US$'s entry is read as 1): the top of the month, right after the moneys step. |
| 1902 | 1 | `public Mix getReserveMix()` | The mix a dollar coming into the vault goes in by. |
| 1904 | 1 | `public void setReserveMix(Mix to)` |  |
| 1907 | 1 | `public double[] getChosenShares()` | The player's eight shares under CHOSEN (a copy; sums to 1). |
| 1910 | 1 | `public void setChosenShares(double[] shares)` | Sets the eight shares under CHOSEN, normalised to sum to 1: a negative or NaN share reads 0, and all zero means all US$. |
| 1913 | 15 | `public void shiftChosen(int k, double points)` | Moves one money's chosen share by this many points (0.05 is five): the rest give or take it in proportion; with nothing else held, the freed points go to the US$ (to the euro when it is the US$ that was moved). |
| 1930 | 1 | `public double[] getTradeShares()` | The city's trade by money, the eight's shares Game handed (a copy; sums to 1, or all 0 where the city has traded nothing). |
| 1933 | 1 | `public void setTradeShares(double[] shares)` | Game's once a month, right after the moneys step: the city's trade by money under MATCH_TRADE, normalised; all zero reads all US$. |
| 1936 | 14 | `private static void normaliseInto(double[] into, double[] source, boolean usdIfNone)` | Fills a share array from a source: finite positive entries kept, the rest 0, scaled to sum to 1; with none, all zero - or the US$ alone for the player's. |
| 1952 | 23 | `public double[] mixShares()` | The shares a dollar coming in is split by now (sums to 1): the mix's, less the moneys with no rate this month, whose share goes to the US$; all US$ when nothing is left. |
| 1977 | 8 | `private void vaultAdd(double usd)` | Puts these dollars into the vault by the mix. |
| 1987 | 9 | `private void vaultAddInto(double usd, String code)` | Puts these dollars into one money's balance; a code that is none of the eight goes by the mix, and a money with no rate this month goes to the US$. |
| 2001 | 28 | `private void vaultRemove(double usd)` | Takes these dollars out of the vault from the largest holding by US$ worth, then the next (a tie leaves the one earlier in the eight's order first). |
| 2031 | 1 | `public double getLastVaultCrossUsd()` | The month's move of the vault across the moneys, in US$: what it gained or lost because the eight moved against each other (the cross rates handed at the top of the month; a purchase after them adds nothing). |
| 2034 | 1 | **type** `public record CrossDriver(String code, int index, double move)` | The money that moved it most, and how far it moved against the US$ in the month: a fraction of the US$ a unit costs, so -0.091 is a euro down 9.1% against the dollar. |
| 2037 | 4 | `public CrossDriver getLastVaultCrossDriver()` | ...or null when nothing moved it. |

### THE VAULT EARNS (XR4, 2026-10-31; spec-exchange-rates 3.8) (lines 2042-2289)

| line | len | member | says |
|---:|---:|---|---|
| 2076 | 19 | `public void earnVaultInterest(double[] annualRates)` | The month's interest on the vault's eight balances, each at its central bank's rate (annual, a fraction; WorldMoneys.EIGHT's order): balance x rate / 12 in the money's own units, kept in the vault. |
| 2096 | 4 | `private void clearInterest()` |  |
| 2102 | 1 | `public double getVaultInterestUsd()` | What the vault earned this month, in US$ at the month's cross rates (0 in a month the world did not hand it). |
| 2105 | 1 | `public double getVaultInterestUsdLifetime()` | ...and since founding. |
| 2108 | 1 | `public double getVaultInterest()` | The month's interest in local money, at this month's rate as getReserves() converts. |
| 2111 | 1 | `public int getVaultInterestDriver()` | The money that earned (or lost) the most this month by US$, as an index into WorldMoneys.EIGHT; -1 where nothing was earned. |
| 2114 | 1 | `public double getVaultInterestDriverUsd()` | ...and the US$ it earned, signed (0 where there is no driver). |
| 2117 | 1 | `public double getVaultYield()` | The vault's US$-weighted mean rate this month, a fraction a year: the month's interest times twelve over the US$ the earning balances were worth; 0 for an empty vault or a month nothing was earned. |
| 2184 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 2200 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 2203 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 2204 | 1 | `public double monthlyCurrentAccount()` |  |
| 2206 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 2225 | 1 | `public double getOpenness()` |  |
| 2227 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 2241 | 48 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 2290-2291)

### WHAT THE CITY OWES ABROAD (lines 2292-2411)

| line | len | member | says |
|---:|---:|---|---|
| 2330 | 5 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 2337 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 2338 | 1 | `public double getForeignDebtUsd()` |  |
| 2348 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 2351 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 2354 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 2357 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 2360 | 1 | `public double tradeBalance()` | Exports less imports. |
| 2363 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 2365 | 1 | `public double getFinancialIn()` |  |
| 2366 | 1 | `public double getFinancialOut()` |  |
| 2367 | 1 | `public double financialAccount()` |  |
| 2370 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 2376 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 2379 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 2389 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 2392 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 2401 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 2410 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 2412-2536)

| line | len | member | says |
|---:|---:|---|---|
| 2436 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 2447 | 1 | `public void forgetDealing()` | Forgets what has been dealt since the last reprice: the founders' dollars (Game.buildWorld()), an endowment the market never saw bought, and a fixture's vault, set up as a holding rather than a purchase under test. |
| 2450 | 1 | `public double getDealtSinceReprice()` | What the treasury has dealt since the last reprice, local money, a purchase of dollars positive: what the next reprice feels (THE FLOAT, step 5). |
| 2465 | 1 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 2471 | 7 | `public void buyReserves(double localAmount, String code)` | ...into one money of the eight, the player's named purchase (WN4, THE VAULT IN EIGHT): a code that is none of them, or null, goes by the mix; a money with no rate this month, to the US$. |
| 2502 | 11 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 2518 | 12 | `public double sellReserves(double localAmount, String code)` | ...out of one money of the eight, the player's named sale (WN4, THE VAULT IN EIGHT): the clamp is what that money alone holds at today's rate, and no other money gives up anything. |
| 2532 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |
| 2535 | 1 | `public double sellableReserves(String code)` | ...of one money of the eight alone (0 for a code that is none of them). |

### THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) (lines 2537-2684)

| line | len | member | says |
|---:|---:|---|---|
| 2600 | 7 | `public double buyAndSpendDollarsForLand(double usd)` | Buys exactly these dollars at today's rate and pays them to the land's seller, in one movement: the vault is untouched, and nothing a reserve purchase would leave behind is left (see THE LAND OFFICE IS PAID IN DOLLARS). |
| 2615 | 14 | `public double spendReservesOnLand(double usd)` | Pays the land's seller out of the vault: at most what it holds, and everything it holds empties it exactly. |
| 2638 | 22 | `public double spendReservesOnLand(double usd, String code)` | ...out of the money the seller is paid in first (WN5): a part of an offer sold by a nation whose money is one of the eight is paid from the vault's holding of that money, up to what it holds at today's rate, and the r... |
| 2662 | 7 | `public void strikeLandMonth()` | Strikes the land's month where the government's books strike its local cost. |
| 2671 | 1 | `public double getLandUsdThisMonth()` | Dollars paid for land in the month last struck, both ways. |
| 2673 | 1 | `public double getLandUsdFromVaultThisMonth()` | ...of which out of the vault. |
| 2675 | 1 | `public double getLandLocalThisMonth()` | What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. |
| 2677 | 1 | `public double getLandLocalFromVaultThisMonth()` | ...and the vault's part of it - the part of the budget's land line no cash paid. |
| 2679 | 1 | `public double getLandUsdLifetime()` | Dollars paid for land since founding, both ways. |
| 2681 | 1 | `public double getLandUsdFromVaultLifetime()` | ...of which out of the vault. |
| 2683 | 1 | `public double getLandUsdPending()` | Dollars paid for land since the last strike - the month in progress. |

### what the currency did to the vault (lines 2685-2772)

| line | len | member | says |
|---:|---:|---|---|
| 2715 | 8 | `public void revalueVault()` | Values the vault at today's rate. |
| 2725 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 2727 | 1 | `public double getBoughtThisMonth()` |  |
| 2728 | 1 | `public double getSoldThisMonth()` |  |
| 2730 | 1 | `public double getLifetimeExports()` |  |
| 2731 | 1 | `public double getLifetimeImports()` |  |
| 2732 | 1 | `public double getLifetimeInterest()` |  |
| 2733 | 1 | `public double getLifetimeFinancial()` |  |
| 2745 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 2766 | 1 | `public double getLifetimeIntervention()` |  |
| 2769 | 1 | `public double valuationChange()` | This month's part of that. |

### WHAT THE TRADE TAB READS (0.7.35) (lines 2773-2861)

| line | len | member | says |
|---:|---:|---|---|
| 2805 | 1 | `public boolean isMonthCounted()` | True once the month's flows are the month's - a month taken since the founding, or a load of a save that carried them - and not nothing. |
| 2808 | 1 | `public double getLifetimeTradeBalance()` | Exports less imports since founding: the record on trade alone, without the income or the capital. |
| 2823 | 5 | `public static int parityLevel(double deviation)` | The verdict on a deviation from parity (deviationFromParity()), either side: 0 near, 1 at PARITY_WATCH or more, 2 at PARITY_FAR or more. |
| 2839 | 4 | `public static int coverLevel(double months)` | The verdict on months of cover: 2 under THIN_COVER, 1 under COMFORTABLE_COVER, 0 at or over it. |
| 2850 | 6 | `public double coverWith(double localChange)` | The cover the vault would give with this much local money's worth of dollars bought into it at today's rate (negative: sold out of it) - the Exchange card's "what it would do". |
| 2858 | 3 | `public double toCover(double months)` | What it would cost in local money, at today's rate, to bring the vault up to this many months of the trailing import bill; nothing when it is there already. |

### carrying (lines 2862-3488)

| line | len | member | says |
|---:|---:|---|---|
| 2874 | 239 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 3114 | 185 | `public void restore(double[] saved)` |  |
| 3301 | 4 | `private static int ordinalOr(double saved, int length, int fallback)` | A saved ordinal, or the fallback's when it is not a whole number inside 0 to length - 1. |
| 3306 | 95 | `public void reset()` |  |
| 3428 | 59 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

