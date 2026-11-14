# TreasuryFund.java - 1,240 lines · 163 methods · 15 constants · model

`ham/citybuildersim/TreasuryFund.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand, and the bank it took over in a rescue (0.7.14).
> 
> WHY. Until 0.7.14 the treasury could buy nothing but land, buildings, the
> vault's dollars and its own paper back, and a failed bank was put back on
> its feet with a gift - capital from the treasury for no shares, the owners
> keeping theirs, the hole "absorbed from outside" by creditors who were in
> fact the central bank's window. It had been on the list since 0.7.12
> (the-firms-sell-bonds.md: "the treasury as a buyer, with rescue-for-shares,
> comes in the next batch"). Jerus's answers, 2026-09-27:
> 
>   who absorbs a failed bank's hole  "City takes the shares ... but later
>                                      bank can dilute by issuing shares...
>                                      we dont want commercial to be owned by
>                                      government fully in the long run"
>   when the rescue happens           "Treasury setting, auto"
>   a standing bank under its minimum "Preferred shares ... and its a popup
>                                      message saying bank wants to issue you
>                                      shares or something"
>   the fund                          "Rule plus your hand"
>   how the city's stake comes down   "New issues only"
>   when the treasury can't pay       "Central bank advances it"
>   what goes into the fund           "dial, default 0, max 300%" - of "the
>                                      year's surplus"
>   where it invests                  "Home only"
> 
> WHAT IT HOLDS, and where each holding is kept - where the others' are:
> 
>   THE MARKET BOOK. Company shares, the bank's among them, on the register
>   (Equity's city holding, cityShares less the rescue book), and the
>   businesses' bonds (CorporateBond.city). Bought on the book by the rule
>   below and by the player's hand; never at an issue - not an offering
>   (Equity.offer()), not the bank's own new shares
>   (Bank.issuesOwnShares(), refused on the book by Exchange's clearing),
>   not a bond's bookbuild. That is Jerus's "new issues only": the city's
>   stake in anything falls as the company sells new shares to others.
> 
>   THE RESCUE BOOK. The bank's shares taken in a resolution
>   (Equity.takeAllForCity(); the register's cityRescue), and the senior
>   preferred and its warrants bought when a standing bank asked
>   (Bank.Preferred, TARP's terms). A separate book: outside the rule's
>   70/30 mix and outside its 10% limit, and the rule never sells it. The
>   hand may.
> 
>   ITS CASH, here. It earns nothing: the treasury's cash earns nothing in
>   this model (the bank pays deposit interest to households and sectors,
>   never to the city), and the fund's is the government's too.
> 
> WHAT IT IS WORTH is each holding at the mark every other holder uses -
> but for a share whose last trade is over a year old (0.7.48, C4): a
> share at the exchange's price (Exchange.price(), the last trade or fair
> value before one - what the households' shares are valued at) while that
> trade is no older than Exchange.STALE_MARK_MONTHS, and the register's fair
> value after (Exchange.cityMark()), a bond at
> the market's valuation (BondMarket.modelPrice(), what every participant
> bids around), the preferred at par, the price it is redeemed at, and the
> warrants at Black-Scholes (callValue()) - plus its cash. Game.fundValue()
> adds them up.
> 
> THE RULE (Norway's Government Pension Fund Global, where Jerus's numbers
> come from):
> ... (56 more lines in the source)

**Uses:** [FundLedger](FundLedger.md) (10), [Equity](Equity.md) (3), [SaveCopy](SaveCopy.md) (3), [Exchange](Exchange.md) (1)

**Used by (41):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [Bank](Bank.md), [BankScreen](BankScreen.md), [BondMarket](BondMarket.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DataSave](DataSave.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentPlayer](GovernmentPlayer.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [HomeRailCheck](HomeRailCheck.md), [Inbox](Inbox.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OwnerCheck](OwnerCheck.md), [PlanCheck](PlanCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [ShortSellingCheck](ShortSellingCheck.md), [SlopeCheck](SlopeCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 128 | the dials |
| 175 | the settings |
| 191 | the preferred offer |
| 199 | the rescues |
| 248 | the hand |
| 329 | the cost basis (0.7.39) |
| 346 | the record |
| 378 | the money |
| 398 | money in: the dial |
| 450 | the mix |
| 477 | money out: the transfer |
| 484 | · the withdrawal dial (0.7.48, C1) |
| 512 | · the limit on the holding (RB1) |
| 525 | · the idle cash (RB1) |
| 668 | what it receives |
| 746 | the rescue |
| 767 | the preferred offer |
| 796 | the hand |
| 910 | reading |
| 972 | the warrants' value |
| 1036 | save and load |

## Enum constants

| line | constant | says |
|---:|---|---|
| 180 | `TreasuryFund.RescueMode.AUTOMATIC` | The city steps in the month the bank fails - a new game's setting. |
| 182 | `TreasuryFund.RescueMode.BUTTON` | The bank waits frozen, carrying its hole at the central bank's window, until the player presses the Bank tab's button - an old save's, and the bare constructor city's. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 131 | `TreasuryFund.MAX_DIAL` | `3.0` | The most the dial takes: three times the year's surplus - Jerus's "max 300%"; past 100% the extra comes from the treasury's cash. |
| 134 | `TreasuryFund.EQUITY_WEIGHT` | `.70` | The share of the market book in company shares: 70%, the Government Pension Fund Global's strategic equity weight (nbim.no/en/investments); the rest is the businesses' bonds. |
| 137 | `TreasuryFund.REBALANCE_OVER` | `.74` | Rebalanced when its equity share passes this: 74%, the GPFG mandate's "If the equity share ... |
| 140 | `TreasuryFund.REBALANCE_UNDER` | `.04` | ...or falls this far under EQUITY_WEIGHT: four points, the same mandate's "more than four percentage points lower than the weight in the strategic benchmark index". |
| 143 | `TreasuryFund.RULE_PREMIUM` | `Exchange.SPREAD / 2` | The rule bids at the desk's ask: the cheapest price anybody stands ready to sell at; at fair value it met nobody (C3). |
| 146 | `TreasuryFund.OWNERSHIP_LIMIT` | `.10` | The most of any one company's shares the rule holds: 10%, the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares in an individual company" (14 May 2018). |
| 149 | `TreasuryFund.LIMIT_SALE_SHARE` | `.10` | The share of a market-book holding's excess over OWNERSHIP_LIMIT the rule offers a month, ahead of the companies' buybacks (RB1): a tenth (★est., spec-wealth 3.3), fast enough that a buyback cannot outrun it and slow ... |
| 152 | `TreasuryFund.IDLE_TRANSFER_SHARE` | `1.0 / 24` | The share of the fund's idle cash - the cash its caps leave no room for - transferred to the budget a month (RB1): a 24th, two years (★est., spec-wealth 3.3), so it is never a jolt. |
| 155 | `TreasuryFund.TRANSFER_RATE` | `.03` | Norway's fiscal rule, a year of the fund's whole value: 3% - transfers follow the fund's expected real return, 3% since 2017 (regjeringen.no). |
| 158 | `TreasuryFund.YEAR_MONTHS` | `12` | A year, in months: the transfer's twelfth and the surplus's year. |
| 161 | `TreasuryFund.WITHDRAWAL_STEP` | `TRANSFER_RATE / YEAR_MONTHS` | The withdrawal dial's step, a share of the fund's whole value a month: a twelfth of TRANSFER_RATE, so Norway's rule is one step (Jerus, 2026-10-05: "even 0 or 10% a month"). |
| 164 | `TreasuryFund.DEFAULT_WITHDRAWAL_STEPS` | `1` | A new city's and an older save's withdrawal: Norway's rule, the transfer paid since 0.7.14 to the bit. |
| 167 | `TreasuryFund.MAX_WITHDRAWAL_STEPS` | `40` | The most it withdraws: 10% of its value a month. |
| 170 | `TreasuryFund.VOLATILITY_MONTHS` | `12` | The months of the bank share's price history its volatility is read over, for the warrants' value: a year of the monthly prices the history keeps (HistorySave's share prices). |
| 173 | `TreasuryFund.OFFER_AGAIN_MONTHS` | `3` | A declined offer comes back after this many months while the bank is still under its minimum: a quarter (Jerus, round 2: "Quarter") - and the bank asks no sooner after an accepted one either, whose size was what it as... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 185 | `private double cash` |  |
| 186 | `private double dial` |  |
| 188 | `private int withdrawalSteps` | The withdrawal dial, in whole steps of WITHDRAWAL_STEP (0.7.48, C1). |
| 189 | `private RescueMode rescue` |  |
| 193 | `private boolean offerPending` |  |
| 194 | `private int offerMonth` |  |
| 195 | `private int declinedMonth` |  |
| 196 | `private int acceptedMonth` |  |
| 197 | `private int offersMade, offersAccepted, offersDeclined` |  |
| 202 | `private double rescueCost` | What the city paid for the rescue book's shares it still holds: their cost, reduced in proportion when the hand sells some. |
| 211 | `int month` |  |
| 212 | `double paid, fromCash, advanced, shortfall, exitCapital` |  |
| 213 | `double shares, householdsShares, householdsValue, worldShares, worldValue, fundShares, fundValue` |  |
| 214 | `double preferredCancelled, warrantsCancelled` |  |
| 246 | `private final List<Resolution> resolutions` |  |
| 261 | `boolean bond` |  |
| 262 | `int company` |  |
| 263 | `int bondId` |  |
| 264 | `boolean buy` |  |
| 265 | `double amount` |  |
| 266 | `int month` |  |
| 268 | `double limit` | The price a unit it is to post at: money a share, or a price a unit of face; 0 for the rule's (0.7.39). |
| 270 | `int postedMonth` | Once posted: the month, the units it asked for, the price it rested at, and what of it filled for how much. |
| 271 | `double units, price, filled, spent` |  |
| 324 | `private final List<HandOrder> hand` |  |
| 327 | `private final List<HandOrder> posted` | The player's orders on the books: posted at the last step, resting until the next (0.7.39). |
| 332 | `private FundLedger ledger` | What each holding cost, and everything the fund did: FundLedger. |
| 335 | `private boolean ledgerToSeed` | True after a save from before the ledger: Game seeds it once the city is back (Game.seedFundLedger()). |
| 349 | `private int lastPayInMonth` | The last year-end pay-in, and the year's transfers so far (the Finances page's "this year"). |
| 350 | `private double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash` |  |
| 351 | `private int transferYear` |  |
| 352 | `private double transfersThisYear, transferShortThisYear` |  |
| 355 | `private double paidInFromSurplus, paidInFromCash, handPaidIn, handDrawnOut` | Over the fund's life, for the page and the playtest. |
| 356 | `private double transfersPaid, transfersShort` |  |
| 358 | `private double transfersFromIdle` | ...and how much of the transfers were due on the idle cash (RB1). |
| 359 | `private double dividendsMarket, dividendsRescue, coupons, principal, bondFaceLost` |  |
| 361 | `private double lendingFees` | The lending fee the world paid it for the shares its market book lent a short (EM2; LF1: a counter of its own, and a row). |
| 362 | `private double preferredBought, preferredDividends, preferredRedeemed, warrantsBoughtBack, warrantSharesTaken` |  |
| 363 | `private double sharesBought, sharesSold, bondsBought, bondsSold, rescueSold` |  |
| 369 | `private double transferDue, transferPaid` | This month's working, cleared at the top of the month: the transfer (a budget line too - NationalAccounts carries it) and what came in and went out, which the Fund page reads after the month has closed - and so saved ... |
| 371 | `private double transferFromIdle` | ...and the part of the due that was struck on the idle cash (RB1, idlePartAt()). |
| 372 | `private double monthDividends, monthCoupons, monthPrincipal, monthBought, monthSold, monthLendingFees` |  |
| 376 | `private double toRaise, transferPaidLate` | Over the default withdrawal (C1): what the month's cash could not cover, sold from the market book at the step and paid at the next month's top - carried, so not cleared with the month - and what that payment was. |
| 1040 | `double cash, dial` |  |
| 1042 | `Integer withdrawalSteps` | The withdrawal dial's steps (0.7.48, C1): null in an older save, which reads Norway's rule. |
| 1043 | `String rescue` |  |
| 1044 | `boolean offerPending` |  |
| 1045 | `int offerMonth, declinedMonth, acceptedMonth, offersMade, offersAccepted, offersDeclined` |  |
| 1046 | `double rescueCost` |  |
| 1047 | `List<Resolution> resolutions` |  |
| 1048 | `List<HandOrder> hand` |  |
| 1050 | `List<HandOrder> posted` | The hand's orders resting on the books (0.7.39). |
| 1052 | `FundLedger ledger` | What each holding cost, and the record (0.7.39): absent from an older save, which Game seeds. |
| 1053 | `int lastPayInMonth, transferYear` |  |
| 1054 | `double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash, transfersThisYear, transferShortThisYear` |  |
| 1055 | `double[] life` |  |
| 1057 | `double[] month` | The month just closed: its transfer due and paid, and what came in and went out; slots 7 and 8 (0.7.48) what it sells for to pay next month, and what this month paid late; slot 9 the lending fees (LF1); slot 10 the pa... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 126 | 1115 | **type** `public final class TreasuryFund` | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand, and the bank it took over in a rescue (0.7.14). |

### the dials (lines 128-174)

### the settings (lines 175-190)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 6 | **type** `public enum RescueMode` | When the bank fails: resolved the month it fails, or frozen until the player presses the button. |

### the preferred offer (lines 191-198)

### the rescues (lines 199-247)

| line | len | member | says |
|---:|---:|---|---|
| 210 | 35 | **type** `public static final class Resolution implements Cloneable` | One resolution, as it happened: the month; what the city paid and how - from its cash, and what the central bank will advance; the hole and the capital to reopen; the shares it took, and from whom, at the price they l... |
| 216 | 1 | `Resolution()` _(in TreasuryFund.Resolution)_ |  |
| 219 | 7 | `Resolution copy()` _(in TreasuryFund.Resolution)_ | A copy that shares nothing with this one: the autosave's (UP1). |
| 227 | 1 | `public int month()` _(in TreasuryFund.Resolution)_ |  |
| 228 | 1 | `public double paid()` _(in TreasuryFund.Resolution)_ |  |
| 229 | 1 | `public double fromCash()` _(in TreasuryFund.Resolution)_ |  |
| 230 | 1 | `public double advanced()` _(in TreasuryFund.Resolution)_ |  |
| 231 | 1 | `public double shortfall()` _(in TreasuryFund.Resolution)_ |  |
| 232 | 1 | `public double exitCapital()` _(in TreasuryFund.Resolution)_ |  |
| 233 | 1 | `public double shares()` _(in TreasuryFund.Resolution)_ |  |
| 234 | 1 | `public double householdsShares()` _(in TreasuryFund.Resolution)_ |  |
| 235 | 1 | `public double householdsValue()` _(in TreasuryFund.Resolution)_ |  |
| 236 | 1 | `public double worldShares()` _(in TreasuryFund.Resolution)_ |  |
| 237 | 1 | `public double worldValue()` _(in TreasuryFund.Resolution)_ |  |
| 238 | 1 | `public double fundShares()` _(in TreasuryFund.Resolution)_ |  |
| 239 | 1 | `public double fundValue()` _(in TreasuryFund.Resolution)_ |  |
| 240 | 1 | `public double preferredCancelled()` _(in TreasuryFund.Resolution)_ |  |
| 241 | 1 | `public double warrantsCancelled()` _(in TreasuryFund.Resolution)_ |  |
| 243 | 1 | `public double ownersLost()` _(in TreasuryFund.Resolution)_ | What the old owners lost, at the last price: the households', the world's and the fund's own market book. |

### the hand (lines 248-328)

| line | len | member | says |
|---:|---:|---|---|
| 260 | 63 | **type** `public static final class HandOrder implements Cloneable` | One of the player's orders, waiting for the month's step: a company's shares (company, Equity's index) or a bond (bondId), to buy with this much money or to sell this many shares or this much face. |
| 273 | 1 | `HandOrder()` _(in TreasuryFund.HandOrder)_ |  |
| 276 | 7 | `HandOrder copy()` _(in TreasuryFund.HandOrder)_ | A copy that shares nothing with this one: the autosave's (UP1) - `filled` and `spent` grow as the order fills. |
| 284 | 3 | `HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month)` _(in TreasuryFund.HandOrder)_ |  |
| 288 | 9 | `HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month, double limit)` _(in TreasuryFund.HandOrder)_ |  |
| 298 | 1 | `public boolean bond()` _(in TreasuryFund.HandOrder)_ |  |
| 299 | 1 | `public int company()` _(in TreasuryFund.HandOrder)_ |  |
| 300 | 1 | `public int bondId()` _(in TreasuryFund.HandOrder)_ |  |
| 301 | 1 | `public boolean buy()` _(in TreasuryFund.HandOrder)_ |  |
| 303 | 1 | `public double amount()` _(in TreasuryFund.HandOrder)_ | Money to spend on a buy; shares or face to sell. |
| 305 | 1 | `public int month()` _(in TreasuryFund.HandOrder)_ | The month it was placed in. |
| 307 | 1 | `public double limit()` _(in TreasuryFund.HandOrder)_ | The price it posts at, a unit: 0 for fair value (a bond's: its value) - what the rule asks at; it bids a share RULE_PREMIUM over (0.7.48). |
| 309 | 1 | `public int postedMonth()` _(in TreasuryFund.HandOrder)_ | The month it was posted at the step, or -1 while it waits. |
| 311 | 1 | `public double units()` _(in TreasuryFund.HandOrder)_ | Posted: the units it asked for (shares, or face). |
| 313 | 1 | `public double price()` _(in TreasuryFund.HandOrder)_ | ...the price it rested at, a unit. |
| 315 | 1 | `public double filled()` _(in TreasuryFund.HandOrder)_ | ...what of it has filled, and what that cost or brought in. |
| 316 | 1 | `public double spent()` _(in TreasuryFund.HandOrder)_ |  |
| 318 | 4 | `public double reserved()` _(in TreasuryFund.HandOrder)_ | What a buy still holds of the fund's cash: its amount while it waits; posted, the units still to fill at its price. |

### the cost basis (0.7.39) (lines 329-345)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 1 | `public FundLedger getLedger()` | The fund's cost basis and record. |
| 341 | 1 | `public boolean needsLedgerSeed()` | True while a save from before 0.7.39 waits for its ledger to be seeded. |
| 344 | 1 | `void ledgerSeeded()` | The seed, done (Game.seedFundLedger()). |

### the record (lines 346-377)

### the money (lines 378-397)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 1 | `public double getCash()` | Its cash: what waits for the book, and what the transfer is paid from. |
| 384 | 1 | `void receive(double amount)` | Cash in: a dividend, a coupon, a sale, a pay-in. |
| 387 | 1 | `void pay(double amount)` | Cash out: a purchase, the transfer, a draw-out. |
| 390 | 1 | `public double getDial()` | The dial: a share of the year's budget surplus, 0 to MAX_DIAL. |
| 393 | 1 | `public void setDial(double d)` | ...set by the player (the Finances tab), clamped to 0-MAX_DIAL. |
| 395 | 1 | `public RescueMode getRescueMode()` |  |
| 396 | 1 | `public void setRescueMode(RescueMode mode)` |  |

### money in: the dial (lines 398-449)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 9 | `public static double[] payIn(double dial, double yearSurplus, double unused, double cash, double floor)` | WHAT THE DIAL TAKES AT A YEAR END, from the year's budget surplus and from the treasury's cash: {from the surplus, from cash}. |
| 428 | 4 | `public static double reservedFor(double dial, double surplusThisYearSoFar)` | WHAT THE ROLLOVER MUST LEAVE FOR THE FUND during a year: the dial's share of this calendar year's surplus so far, which the year-end pay-in will claim - so the dial's share is taken before the rollover nets (Jerus's e... |
| 434 | 9 | `void notePayIn(int month, double yearSurplus, double fromSurplus, double fromCash)` | A year-end pay-in, booked: the surplus's part and the cash's, off the treasury into the fund (the caller moves the treasury's cash). |
| 445 | 1 | `void notePaidInByHand(double amount)` | The player pays in by hand: the caller moves the treasury's cash. |
| 448 | 1 | `void noteDrawnOutByHand(double amount)` | ...and draws out. |

### the mix (lines 450-476)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 5 | `public static double sharesOver(double shares, double bonds, double cash)` | WHAT THE RULE SELLS OF ITS SHARES to rebalance, in money at its marks: the excess over EQUITY_WEIGHT of its market book and cash when its shares are more than REBALANCE_OVER of them; nothing otherwise. |
| 471 | 5 | `public static double bondsOver(double shares, double bonds, double cash)` | ...AND OF ITS BONDS: the excess over the rest of the weight when its shares are more than REBALANCE_UNDER under EQUITY_WEIGHT and its bonds over theirs - the mandate's lower trigger. |

### money out: the transfer (lines 477-483)

| line | len | member | says |
|---:|---:|---|---|
| 480 | 3 | `public static double transferOn(double value)` | A month's transfer on a fund of this value at Norway's rule, the default withdrawal: a twelfth of TRANSFER_RATE of it. |

### the withdrawal dial (0.7.48, C1) (lines 484-511)

| line | len | member | says |
|---:|---:|---|---|
| 487 | 1 | `public double getWithdrawal()` | The withdrawal, a share of the fund's whole value a month: whole steps of WITHDRAWAL_STEP, 0 to MAX_WITHDRAWAL_STEPS of them; at the default the very double transferOn() multiplies by. |
| 490 | 1 | `public int getWithdrawalSteps()` | ...in its steps. |
| 493 | 1 | `public void setWithdrawal(double share)` | ...set by the player (Finances > The city's fund > Rules & cash): rounded to whole steps and held at 0-MAX_WITHDRAWAL_STEPS (stepsFor()); a share that is not a number leaves Norway's rule. |
| 496 | 5 | `public static int stepsFor(double share)` | The whole steps a share a month comes to, as the dial takes it: rounded, and held at 0-MAX_WITHDRAWAL_STEPS; a share that is not a number, the default. |
| 503 | 3 | `public double withdrawalOn(double value)` | A month's withdrawal on a fund of this value at the dial in force: transferOn() at the default, to the bit. |
| 508 | 3 | `public static double withdrawalAt(double share, double value)` | ...and at a share of the caller's, as the dial would take it (stepsFor()): withdrawalOn() at the dial in force, to the bit - the preview's (PolicyPreview.fundWithdrawalAt()). |

### the limit on the holding (RB1) (lines 512-524)

| line | len | member | says |
|---:|---:|---|---|
| 521 | 3 | `public static double limitSale(double held, double inIssue)` | What the rule offers of a market-book holding a month (RB1): a tenth (LIMIT_SALE_SHARE) of what `held` is over OWNERSHIP_LIMIT of the `inIssue` shares - the companies' buybacks retire shares under the fund and lift it... |

### the idle cash (RB1) (lines 525-667)

| line | len | member | says |
|---:|---:|---|---|
| 528 | 3 | `private static double plus(double x)` | A figure that is a positive number, else 0: a NaN or a negative is nothing here. |
| 548 | 5 | `public static double idleCash(double cash, double reserve, double shareRoom, double bondRoom)` | The fund's idle cash (RB1): the cash its caps leave no room for, which would otherwise pile up (the probe: past the 10% limit on every company the rule had nowhere to put it). |
| 555 | 3 | `private static double idleIn(double value, double idle)` | The idle cash as the due reads it: held to 0 and the fund's value, a figure that is not a number nothing. |
| 568 | 7 | `public static double dueAt(int steps, double value, double idle)` | A month's transfer on a fund of this value and this much idle cash at `steps` of the dial (RB1): the dial's share of what is invested, and the larger of the dial and IDLE_TRANSFER_SHARE of the idle cash. |
| 577 | 5 | `public static double idlePartAt(int steps, double value, double idle)` | ...and the part of that due struck on the idle cash: 0 at a dial of nothing, or with none idle. |
| 584 | 3 | `public double dueOn(double value, double idle)` | A month's transfer on a fund of this value and idle cash at the dial in force: dueAt(). |
| 589 | 1 | `public boolean sellsToPay()` | True while the dial is over Norway's rule: what its cash cannot cover is sold from the market book to pay, and the rule buys nothing (a fund drawn past its expected return is being spent). |
| 592 | 1 | `public double getToRaise()` | What the month's cash could not cover over the default, which the step sells the market book for and the next month's top pays: 0 at or under the default. |
| 595 | 1 | `public double getTransferPaidLate()` | What this month's top paid of last month's toRaise, from what it sold: 0 at or under the default. |
| 617 | 30 | `double payTransfer(double value, double idle, int year)` | Pays the month's transfer on a fund worth `value`, `idle` of it cash its rule cannot invest (idleCash()), and returns what was paid. |
| 649 | 3 | `double payTransfer(double value, int year)` | The same with no idle cash: the dial's share of the whole value (fixtures; the city's step passes the idle cash). |
| 654 | 1 | `public double getTransferFromIdle()` | The part of this month's due struck on the idle cash (RB1): 0 with none idle, or at a dial of nothing. |
| 657 | 1 | `public double getTransfersFromIdle()` | ...and over the fund's life. |
| 660 | 1 | `public double getTransferDue()` | This month's transfer as due, and as paid. |
| 661 | 1 | `public double getTransferPaid()` |  |
| 663 | 1 | `public double getTransferShort()` | ...and what the cash could not cover of it this month. |
| 665 | 1 | `public double getTransfersThisYear()` | This calendar year's transfers, paid and unpaid. |
| 666 | 1 | `public double getTransferShortThisYear()` |  |

### what it receives (lines 668-745)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 8 | `void receiveDividend(double amount, double rescueShare)` | A dividend on its shares, split between its books by what each holds. |
| 685 | 7 | `void receiveLendingFee(String company, double amount, int month)` | The lending fee the world pays the fund for the shares its market book lends (EM2, Exchange.strikeLending()): cash in, income from the world - and, since LF1, the fund's own income counters (the month's and the life's... |
| 694 | 1 | `void receiveCoupon(double amount)` | A coupon on its bonds. |
| 697 | 1 | `void receivePrincipal(double amount)` | A bond's principal at its maturity. |
| 700 | 1 | `void noteBondLoss(double face)` | Face a default took off its bonds: a loss, no cash. |
| 703 | 3 | `void receivePreferredDividend(double amount)` | A preferred dividend the bank paid it. |
| 708 | 4 | `void receiveRedemption(double par, double warrants)` | The bank redeemed preferred at par, and bought warrants back at their value. |
| 714 | 1 | `void notePreferredBought(double par)` | The treasury bought preferred for the rescue book: its cost, for the record (the treasury paid). |
| 717 | 1 | `void noteWarrantShares(double n)` | Warrants exercised at their expiry: new shares into the rescue book, for nothing. |
| 720 | 6 | `void noteBought(double money, boolean bond)` | A trade on the book: shares or a bond bought or sold, and how much of a sale came out of the rescue book. |
| 726 | 6 | `void noteSold(double money, boolean bond)` |  |
| 734 | 5 | `void noteRescueSold(double sharesSoldFromRescue, double rescueHeldBefore, double money)` | The hand sold rescue-book shares: their cost comes off the book's cost in proportion, and the sale is counted. |
| 741 | 4 | `void startMonth()` | Clears the month's working. |

### the rescue (lines 746-766)

| line | len | member | says |
|---:|---:|---|---|
| 749 | 4 | `void noteResolution(Resolution r)` | A resolution happened: the shares are the rescue book's, at what the city paid. |
| 755 | 5 | `static Resolution newResolution(int month)` | Builds a resolution's record; Game fills it. |
| 762 | 1 | `public List<Resolution> getResolutions()` | Every resolution the city has made, oldest first. |
| 765 | 1 | `public double getRescueCost()` | What the city paid for the rescue book's shares it still holds. |

### the preferred offer (lines 767-795)

| line | len | member | says |
|---:|---:|---|---|
| 770 | 1 | `public boolean isOfferPending()` | True while the bank's offer waits for the player's answer. |
| 772 | 1 | `public int getOfferMonth()` | The month it was made, or -1. |
| 774 | 1 | `public int getDeclinedMonth()` | The month it was last declined, or -1. |
| 776 | 1 | `public int getAcceptedMonth()` | The month one was last accepted, or -1. |
| 779 | 4 | `public boolean mayOffer(int month)` | Whether the bank may ask again this month: nothing pending, and a quarter since the city last answered, either way. |
| 784 | 1 | `void noteOffered(int month)` |  |
| 785 | 3 | `void noteAccepted(int month, double par)` |  |
| 788 | 1 | `void noteDeclined(int month)` |  |
| 790 | 1 | `void noteLapsed()` | The bank is back over its minimum on its own: the offer lapses, answered by nobody. |
| 792 | 1 | `public int getOffersMade()` |  |
| 793 | 1 | `public int getOffersAccepted()` |  |
| 794 | 1 | `public int getOffersDeclined()` |  |

### the hand (lines 796-909)

| line | len | member | says |
|---:|---:|---|---|
| 799 | 1 | `void queue(HandOrder order)` | Queues one of the player's orders for the next step. |
| 802 | 1 | `public List<HandOrder> getHandOrders()` | The orders waiting for the next step, oldest first. |
| 805 | 1 | `public List<HandOrder> getPosted()` | The orders posted at the last step and resting until the next, oldest first (0.7.39). |
| 808 | 4 | `HandOrder cancel(int i)` | Cancels the order waiting at this place, before the step posts it (0.7.39). |
| 821 | 6 | `public double handReserve()` | WHAT THE HAND'S BUYS HOLD OF THE FUND'S CASH (0.7.39; the spec's B9): a waiting order's whole amount, and a posted one's units still to fill at its price. |
| 829 | 5 | `double cashForTheRule()` | The fund's cash the rule may spend: its cash less the hand's reserve, never under nothing. |
| 836 | 9 | `void notePosted(HandOrder o, String key, double units, double price, int month)` | A hand order posted at the step, `units` at `price` a unit: it rests until the next step, and the ledger opens its row. |
| 847 | 3 | `void noteDropped(HandOrder o, String key, double units, double price, String why, int month)` | A hand order that could not be posted, and why: the ledger's row. |
| 852 | 15 | `void handFilled(boolean bond, int which, boolean buy, double q, double cash)` | One of the hand's fills, on a company's book or a bond's: the posted orders on it, oldest first, take it. |
| 869 | 17 | `void noteSplit(int company, double k, int month)` | A company's shares split by k (a consolidation under one): the player's orders in them, waiting or on the book, count in the new shares and price in them; the ledger's row (0.7.39). |
| 888 | 10 | `void closePosted(boolean bond)` | The step withdrew every order on one market: the hand's posted there close, and what they did not fill lapses. |
| 900 | 9 | `List<HandOrder> takeHandOrders(boolean bonds)` | ...taken by the market that posts them: the share orders, or the bond orders. |

### reading (lines 910-971)

| line | len | member | says |
|---:|---:|---|---|
| 912 | 1 | `public int getLastPayInMonth()` |  |
| 913 | 1 | `public double getLastPayInYearSurplus()` |  |
| 914 | 1 | `public double getLastPayInFromSurplus()` |  |
| 915 | 1 | `public double getLastPayInFromCash()` |  |
| 916 | 1 | `public double getPaidInFromSurplus()` |  |
| 917 | 1 | `public double getPaidInFromCash()` |  |
| 918 | 1 | `public double getHandPaidIn()` |  |
| 919 | 1 | `public double getHandDrawnOut()` |  |
| 920 | 1 | `public double getTransfersPaid()` |  |
| 921 | 1 | `public double getTransfersShort()` |  |
| 922 | 1 | `public double getDividendsMarket()` |  |
| 923 | 1 | `public double getDividendsRescue()` |  |
| 924 | 1 | `public double getCoupons()` |  |
| 925 | 1 | `public double getPrincipal()` |  |
| 926 | 1 | `public double getBondFaceLost()` |  |
| 927 | 1 | `public double getPreferredBought()` |  |
| 928 | 1 | `public double getPreferredDividends()` |  |
| 929 | 1 | `public double getPreferredRedeemed()` |  |
| 930 | 1 | `public double getWarrantsBoughtBack()` |  |
| 931 | 1 | `public double getWarrantSharesTaken()` |  |
| 932 | 1 | `public double getSharesBought()` |  |
| 933 | 1 | `public double getSharesSold()` |  |
| 934 | 1 | `public double getBondsBought()` |  |
| 935 | 1 | `public double getBondsSold()` |  |
| 936 | 1 | `public double getRescueSold()` |  |
| 937 | 1 | `public double getMonthDividends()` |  |
| 938 | 1 | `public double getMonthCoupons()` |  |
| 939 | 1 | `public double getMonthLendingFees()` |  |
| 940 | 1 | `public double getLendingFees()` |  |
| 941 | 1 | `public double getMonthPrincipal()` |  |
| 942 | 1 | `public double getMonthBought()` |  |
| 943 | 1 | `public double getMonthSold()` |  |
| 946 | 5 | `public double getRescuesPaid()` | What every resolution paid for the rescue book, over the fund's life (0.7.39). |
| 957 | 3 | `public double getPutIn()` | WHAT THE CITY HAS PUT INTO ITS FUND, over its life (0.7.39): the dial's pay-ins and the hand's, what the rescues paid, and the preferred the treasury bought - each money the city's own that became the fund's. |
| 962 | 3 | `public double getTakenOut()` | ...AND WHAT IT HAS TAKEN OUT: the transfers paid to the budget and the hand's draw-outs. |
| 967 | 4 | `public boolean isEmpty()` | True when it holds nothing and has never been asked for anything: a fund that has not begun. |

### the warrants' value (lines 972-1035)

| line | len | member | says |
|---:|---:|---|---|
| 987 | 10 | `public static double callValue(double price, double strike, double rate, double sigma, double years)` | WHAT A CALL IS WORTH, Black and Scholes (1973): S N(d1) - K e^(-rT) N(d2), d1 = (ln(S/K) + (r + sigma^2/2) T) / (sigma sqrt T), d2 = d1 - sigma sqrt T. |
| 1002 | 9 | `public static double normalCdf(double x)` | The standard normal distribution function: Zelen and Severo's approximation, Abramowitz and Stegun (1964) 26.2.17, to within 7.5e-8. |
| 1018 | 17 | `public static double annualVolatility(double[] prices)` | A share's volatility, a year, from its monthly prices: the standard deviation of the last VOLATILITY_MONTHS monthly log returns, times the square root of twelve. |

### save and load (lines 1036-1240)

| line | len | member | says |
|---:|---:|---|---|
| 1039 | 41 | **type** `public static final class State implements Cloneable` | Everything the fund carries from one month to the next, by name. |
| 1065 | 14 | `State copy()` _(in TreasuryFund.State)_ | A copy that shares nothing with this state (UP1, 2026-10-22): toState() lists the fund's own resolutions and orders and holds a copy of its ledger. |
| 1081 | 33 | `public State toState()` |  |
| 1120 | 60 | `public void restore(State s)` | Puts a saved fund back. |
| 1182 | 26 | `public void reset()` | A fund that has not begun: nothing held, the dial at 0, the withdrawal at Norway's rule, the rescue on the button. |
| 1210 | 30 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM); share counts and the two dials do not move. |

