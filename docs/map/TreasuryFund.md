# TreasuryFund.java - 962 lines · 138 methods · 9 constants · model

`ham/citybuildersim/TreasuryFund.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
> WHAT IT IS WORTH is each holding at the mark every other holder uses: a
> share at the exchange's price (Exchange.price(), the last trade or fair
> value before one - what the households' shares are valued at), a bond at
> the market's valuation (BondMarket.modelPrice(), what every participant
> bids around), the preferred at par, the price it is redeemed at, and the
> warrants at Black-Scholes (callValue()) - plus its cash. Game.fundValue()
> adds them up.
> 
> THE RULE (Norway's Government Pension Fund Global, where Jerus's numbers
> come from):
> 
>   MONEY IN: the dial, a share of the year's budget surplus, paid once a
>   year at the calendar's year end (Game.fundYearEnd() says why that
> ... (36 more lines in the source)

**Uses:** [FundLedger](FundLedger.md) (10), [Equity](Equity.md) (3)

**Used by (18):** [Bank](Bank.md), [BankScreen](BankScreen.md), [BondMarket](BondMarket.md), [ChartCheck](ChartCheck.md), [DataSave](DataSave.md), [Exchange](Exchange.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 108 | the dials |
| 137 | the settings |
| 151 | the preferred offer |
| 159 | the rescues |
| 199 | the hand |
| 271 | the cost basis (0.7.39) |
| 288 | the record |
| 310 | the money |
| 330 | money in: the dial |
| 382 | the mix |
| 409 | money out: the transfer |
| 446 | what it receives |
| 511 | the rescue |
| 532 | the preferred offer |
| 561 | the hand |
| 675 | reading |
| 735 | the warrants' value |
| 799 | save and load |

## Enum constants

| line | constant | says |
|---:|---|---|
| 142 | `TreasuryFund.RescueMode.AUTOMATIC` | The city steps in the month the bank fails - a new game's setting. |
| 144 | `TreasuryFund.RescueMode.BUTTON` | The bank waits frozen, carrying its hole at the central bank's window, until the player presses the Bank tab's button - an old save's, and the bare constructor city's. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 111 | `TreasuryFund.MAX_DIAL` | `3.0` | The most the dial takes: three times the year's surplus - Jerus's "max 300%"; past 100% the extra comes from the treasury's cash. |
| 114 | `TreasuryFund.EQUITY_WEIGHT` | `.70` | The share of the market book in company shares: 70%, the Government Pension Fund Global's strategic equity weight (nbim.no/en/investments); the rest is the businesses' bonds. |
| 117 | `TreasuryFund.REBALANCE_OVER` | `.74` | Rebalanced when its equity share passes this: 74%, the GPFG mandate's "If the equity share ... |
| 120 | `TreasuryFund.REBALANCE_UNDER` | `.04` | ...or falls this far under EQUITY_WEIGHT: four points, the same mandate's "more than four percentage points lower than the weight in the strategic benchmark index". |
| 123 | `TreasuryFund.OWNERSHIP_LIMIT` | `.10` | The most of any one company's shares the rule holds: 10%, the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares in an individual company" (14 May 2018). |
| 126 | `TreasuryFund.TRANSFER_RATE` | `.03` | What the fund pays the budget a year, of its whole value: 3%, Norway's fiscal rule - transfers follow the fund's expected real return, 3% since 2017 (regjeringen.no). |
| 129 | `TreasuryFund.YEAR_MONTHS` | `12` | A year, in months: the transfer's twelfth and the surplus's year. |
| 132 | `TreasuryFund.VOLATILITY_MONTHS` | `12` | The months of the bank share's price history its volatility is read over, for the warrants' value: a year of the monthly prices the history keeps (HistorySave's share prices). |
| 135 | `TreasuryFund.OFFER_AGAIN_MONTHS` | `3` | A declined offer comes back after this many months while the bank is still under its minimum: a quarter (Jerus, round 2: "Quarter") - and the bank asks no sooner after an accepted one either, whose size was what it as... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 147 | `private double cash` |  |
| 148 | `private double dial` |  |
| 149 | `private RescueMode rescue` |  |
| 153 | `private boolean offerPending` |  |
| 154 | `private int offerMonth` |  |
| 155 | `private int declinedMonth` |  |
| 156 | `private int acceptedMonth` |  |
| 157 | `private int offersMade, offersAccepted, offersDeclined` |  |
| 162 | `private double rescueCost` | What the city paid for the rescue book's shares it still holds: their cost, reduced in proportion when the hand sells some. |
| 171 | `int month` |  |
| 172 | `double paid, fromCash, advanced, shortfall, exitCapital` |  |
| 173 | `double shares, householdsShares, householdsValue, worldShares, worldValue, fundShares, fundValue` |  |
| 174 | `double preferredCancelled, warrantsCancelled` |  |
| 197 | `private final List<Resolution> resolutions` |  |
| 212 | `boolean bond` |  |
| 213 | `int company` |  |
| 214 | `int bondId` |  |
| 215 | `boolean buy` |  |
| 216 | `double amount` |  |
| 217 | `int month` |  |
| 219 | `double limit` | The price a unit it is to post at: money a share, or a price a unit of face; 0 for the rule's (0.7.39). |
| 221 | `int postedMonth` | Once posted: the month, the units it asked for, the price it rested at, and what of it filled for how much. |
| 222 | `double units, price, filled, spent` |  |
| 266 | `private final List<HandOrder> hand` |  |
| 269 | `private final List<HandOrder> posted` | The player's orders on the books: posted at the last step, resting until the next (0.7.39). |
| 274 | `private FundLedger ledger` | What each holding cost, and everything the fund did: FundLedger. |
| 277 | `private boolean ledgerToSeed` | True after a save from before the ledger: Game seeds it once the city is back (Game.seedFundLedger()). |
| 291 | `private int lastPayInMonth` | The last year-end pay-in, and the year's transfers so far (the Finances page's "this year"). |
| 292 | `private double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash` |  |
| 293 | `private int transferYear` |  |
| 294 | `private double transfersThisYear, transferShortThisYear` |  |
| 297 | `private double paidInFromSurplus, paidInFromCash, handPaidIn, handDrawnOut` | Over the fund's life, for the page and the playtest. |
| 298 | `private double transfersPaid, transfersShort` |  |
| 299 | `private double dividendsMarket, dividendsRescue, coupons, principal, bondFaceLost` |  |
| 300 | `private double preferredBought, preferredDividends, preferredRedeemed, warrantsBoughtBack, warrantSharesTaken` |  |
| 301 | `private double sharesBought, sharesSold, bondsBought, bondsSold, rescueSold` |  |
| 307 | `private double transferDue, transferPaid` | This month's working, cleared at the top of the month: the transfer (a budget line too - NationalAccounts carries it) and what came in and went out, which the Fund page reads after the month has closed - and so saved ... |
| 308 | `private double monthDividends, monthCoupons, monthPrincipal, monthBought, monthSold` |  |
| 803 | `double cash, dial` |  |
| 804 | `String rescue` |  |
| 805 | `boolean offerPending` |  |
| 806 | `int offerMonth, declinedMonth, acceptedMonth, offersMade, offersAccepted, offersDeclined` |  |
| 807 | `double rescueCost` |  |
| 808 | `List<Resolution> resolutions` |  |
| 809 | `List<HandOrder> hand` |  |
| 811 | `List<HandOrder> posted` | The hand's orders resting on the books (0.7.39). |
| 813 | `FundLedger ledger` | What each holding cost, and the record (0.7.39): absent from an older save, which Game seeds. |
| 814 | `int lastPayInMonth, transferYear` |  |
| 815 | `double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash, transfersThisYear, transferShortThisYear` |  |
| 816 | `double[] life` |  |
| 818 | `double[] month` | The month just closed: its transfer due and paid, and what came in and went out. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 106 | 857 | **type** `public final class TreasuryFund` | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand, and the bank it took over in a rescue (0.7.14). |

### the dials (lines 108-136)

### the settings (lines 137-150)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 6 | **type** `public enum RescueMode` | When the bank fails: resolved the month it fails, or frozen until the player presses the button. |

### the preferred offer (lines 151-158)

### the rescues (lines 159-198)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 26 | **type** `public static final class Resolution` | One resolution, as it happened: the month; what the city paid and how - from its cash, and what the central bank will advance; the hole and the capital to reopen; the shares it took, and from whom, at the price they l... |
| 176 | 1 | `Resolution()` _(in TreasuryFund.Resolution)_ |  |
| 178 | 1 | `public int month()` _(in TreasuryFund.Resolution)_ |  |
| 179 | 1 | `public double paid()` _(in TreasuryFund.Resolution)_ |  |
| 180 | 1 | `public double fromCash()` _(in TreasuryFund.Resolution)_ |  |
| 181 | 1 | `public double advanced()` _(in TreasuryFund.Resolution)_ |  |
| 182 | 1 | `public double shortfall()` _(in TreasuryFund.Resolution)_ |  |
| 183 | 1 | `public double exitCapital()` _(in TreasuryFund.Resolution)_ |  |
| 184 | 1 | `public double shares()` _(in TreasuryFund.Resolution)_ |  |
| 185 | 1 | `public double householdsShares()` _(in TreasuryFund.Resolution)_ |  |
| 186 | 1 | `public double householdsValue()` _(in TreasuryFund.Resolution)_ |  |
| 187 | 1 | `public double worldShares()` _(in TreasuryFund.Resolution)_ |  |
| 188 | 1 | `public double worldValue()` _(in TreasuryFund.Resolution)_ |  |
| 189 | 1 | `public double fundShares()` _(in TreasuryFund.Resolution)_ |  |
| 190 | 1 | `public double fundValue()` _(in TreasuryFund.Resolution)_ |  |
| 191 | 1 | `public double preferredCancelled()` _(in TreasuryFund.Resolution)_ |  |
| 192 | 1 | `public double warrantsCancelled()` _(in TreasuryFund.Resolution)_ |  |
| 194 | 1 | `public double ownersLost()` _(in TreasuryFund.Resolution)_ | What the old owners lost, at the last price: the households', the world's and the fund's own market book. |

### the hand (lines 199-270)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 54 | **type** `public static final class HandOrder` | One of the player's orders, waiting for the month's step: a company's shares (company, Equity's index) or a bond (bondId), to buy with this much money or to sell this many shares or this much face. |
| 224 | 1 | `HandOrder()` _(in TreasuryFund.HandOrder)_ |  |
| 226 | 3 | `HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month)` _(in TreasuryFund.HandOrder)_ |  |
| 230 | 9 | `HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month, double limit)` _(in TreasuryFund.HandOrder)_ |  |
| 240 | 1 | `public boolean bond()` _(in TreasuryFund.HandOrder)_ |  |
| 241 | 1 | `public int company()` _(in TreasuryFund.HandOrder)_ |  |
| 242 | 1 | `public int bondId()` _(in TreasuryFund.HandOrder)_ |  |
| 243 | 1 | `public boolean buy()` _(in TreasuryFund.HandOrder)_ |  |
| 245 | 1 | `public double amount()` _(in TreasuryFund.HandOrder)_ | Money to spend on a buy; shares or face to sell. |
| 247 | 1 | `public int month()` _(in TreasuryFund.HandOrder)_ | The month it was placed in. |
| 249 | 1 | `public double limit()` _(in TreasuryFund.HandOrder)_ | The price it posts at, a unit: 0 for fair value (a bond's: its value), the rule's own price. |
| 251 | 1 | `public int postedMonth()` _(in TreasuryFund.HandOrder)_ | The month it was posted at the step, or -1 while it waits. |
| 253 | 1 | `public double units()` _(in TreasuryFund.HandOrder)_ | Posted: the units it asked for (shares, or face). |
| 255 | 1 | `public double price()` _(in TreasuryFund.HandOrder)_ | ...the price it rested at, a unit. |
| 257 | 1 | `public double filled()` _(in TreasuryFund.HandOrder)_ | ...what of it has filled, and what that cost or brought in. |
| 258 | 1 | `public double spent()` _(in TreasuryFund.HandOrder)_ |  |
| 260 | 4 | `public double reserved()` _(in TreasuryFund.HandOrder)_ | What a buy still holds of the fund's cash: its amount while it waits; posted, the units still to fill at its price. |

### the cost basis (0.7.39) (lines 271-287)

| line | len | member | says |
|---:|---:|---|---|
| 280 | 1 | `public FundLedger getLedger()` | The fund's cost basis and record. |
| 283 | 1 | `public boolean needsLedgerSeed()` | True while a save from before 0.7.39 waits for its ledger to be seeded. |
| 286 | 1 | `void ledgerSeeded()` | The seed, done (Game.seedFundLedger()). |

### the record (lines 288-309)

### the money (lines 310-329)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 1 | `public double getCash()` | Its cash: what waits for the book, and what the transfer is paid from. |
| 316 | 1 | `void receive(double amount)` | Cash in: a dividend, a coupon, a sale, a pay-in. |
| 319 | 1 | `void pay(double amount)` | Cash out: a purchase, the transfer, a draw-out. |
| 322 | 1 | `public double getDial()` | The dial: a share of the year's budget surplus, 0 to MAX_DIAL. |
| 325 | 1 | `public void setDial(double d)` | ...set by the player (the Finances tab), clamped to 0-MAX_DIAL. |
| 327 | 1 | `public RescueMode getRescueMode()` |  |
| 328 | 1 | `public void setRescueMode(RescueMode mode)` |  |

### money in: the dial (lines 330-381)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 9 | `public static double[] payIn(double dial, double yearSurplus, double unused, double cash, double floor)` | WHAT THE DIAL TAKES AT A YEAR END, from the year's budget surplus and from the treasury's cash: {from the surplus, from cash}. |
| 360 | 4 | `public static double reservedFor(double dial, double surplusThisYearSoFar)` | WHAT THE ROLLOVER MUST LEAVE FOR THE FUND during a year: the dial's share of this calendar year's surplus so far, which the year-end pay-in will claim - so the dial's share is taken before the rollover nets (Jerus's e... |
| 366 | 9 | `void notePayIn(int month, double yearSurplus, double fromSurplus, double fromCash)` | A year-end pay-in, booked: the surplus's part and the cash's, off the treasury into the fund (the caller moves the treasury's cash). |
| 377 | 1 | `void notePaidInByHand(double amount)` | The player pays in by hand: the caller moves the treasury's cash. |
| 380 | 1 | `void noteDrawnOutByHand(double amount)` | ...and draws out. |

### the mix (lines 382-408)

| line | len | member | says |
|---:|---:|---|---|
| 390 | 5 | `public static double sharesOver(double shares, double bonds, double cash)` | WHAT THE RULE SELLS OF ITS SHARES to rebalance, in money at its marks: the excess over EQUITY_WEIGHT of its market book and cash when its shares are more than REBALANCE_OVER of them; nothing otherwise. |
| 403 | 5 | `public static double bondsOver(double shares, double bonds, double cash)` | ...AND OF ITS BONDS: the excess over the rest of the weight when its shares are more than REBALANCE_UNDER under EQUITY_WEIGHT and its bonds over theirs - the mandate's lower trigger. |

### money out: the transfer (lines 409-445)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 3 | `public static double transferOn(double value)` | A month's transfer on a fund of this value: a twelfth of TRANSFER_RATE of it. |
| 421 | 15 | `double payTransfer(double value, int year)` | Pays the month's transfer on a fund worth `value`, from its cash only - "the rule never sells to pay it" - and returns what was paid; what the cash could not cover is simply not paid, and counted (getTransferShort()). |
| 438 | 1 | `public double getTransferDue()` | This month's transfer as due, and as paid. |
| 439 | 1 | `public double getTransferPaid()` |  |
| 441 | 1 | `public double getTransferShort()` | ...and what the cash could not cover of it this month. |
| 443 | 1 | `public double getTransfersThisYear()` | This calendar year's transfers, paid and unpaid. |
| 444 | 1 | `public double getTransferShortThisYear()` |  |

### what it receives (lines 446-510)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 8 | `void receiveDividend(double amount, double rescueShare)` | A dividend on its shares, split between its books by what each holds. |
| 459 | 1 | `void receiveCoupon(double amount)` | A coupon on its bonds. |
| 462 | 1 | `void receivePrincipal(double amount)` | A bond's principal at its maturity. |
| 465 | 1 | `void noteBondLoss(double face)` | Face a default took off its bonds: a loss, no cash. |
| 468 | 3 | `void receivePreferredDividend(double amount)` | A preferred dividend the bank paid it. |
| 473 | 4 | `void receiveRedemption(double par, double warrants)` | The bank redeemed preferred at par, and bought warrants back at their value. |
| 479 | 1 | `void notePreferredBought(double par)` | The treasury bought preferred for the rescue book: its cost, for the record (the treasury paid). |
| 482 | 1 | `void noteWarrantShares(double n)` | Warrants exercised at their expiry: new shares into the rescue book, for nothing. |
| 485 | 6 | `void noteBought(double money, boolean bond)` | A trade on the book: shares or a bond bought or sold, and how much of a sale came out of the rescue book. |
| 491 | 6 | `void noteSold(double money, boolean bond)` |  |
| 499 | 5 | `void noteRescueSold(double sharesSoldFromRescue, double rescueHeldBefore, double money)` | The hand sold rescue-book shares: their cost comes off the book's cost in proportion, and the sale is counted. |
| 506 | 4 | `void startMonth()` | Clears the month's working. |

### the rescue (lines 511-531)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 4 | `void noteResolution(Resolution r)` | A resolution happened: the shares are the rescue book's, at what the city paid. |
| 520 | 5 | `static Resolution newResolution(int month)` | Builds a resolution's record; Game fills it. |
| 527 | 1 | `public List<Resolution> getResolutions()` | Every resolution the city has made, oldest first. |
| 530 | 1 | `public double getRescueCost()` | What the city paid for the rescue book's shares it still holds. |

### the preferred offer (lines 532-560)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 1 | `public boolean isOfferPending()` | True while the bank's offer waits for the player's answer. |
| 537 | 1 | `public int getOfferMonth()` | The month it was made, or -1. |
| 539 | 1 | `public int getDeclinedMonth()` | The month it was last declined, or -1. |
| 541 | 1 | `public int getAcceptedMonth()` | The month one was last accepted, or -1. |
| 544 | 4 | `public boolean mayOffer(int month)` | Whether the bank may ask again this month: nothing pending, and a quarter since the city last answered, either way. |
| 549 | 1 | `void noteOffered(int month)` |  |
| 550 | 3 | `void noteAccepted(int month, double par)` |  |
| 553 | 1 | `void noteDeclined(int month)` |  |
| 555 | 1 | `void noteLapsed()` | The bank is back over its minimum on its own: the offer lapses, answered by nobody. |
| 557 | 1 | `public int getOffersMade()` |  |
| 558 | 1 | `public int getOffersAccepted()` |  |
| 559 | 1 | `public int getOffersDeclined()` |  |

### the hand (lines 561-674)

| line | len | member | says |
|---:|---:|---|---|
| 564 | 1 | `void queue(HandOrder order)` | Queues one of the player's orders for the next step. |
| 567 | 1 | `public List<HandOrder> getHandOrders()` | The orders waiting for the next step, oldest first. |
| 570 | 1 | `public List<HandOrder> getPosted()` | The orders posted at the last step and resting until the next, oldest first (0.7.39). |
| 573 | 4 | `HandOrder cancel(int i)` | Cancels the order waiting at this place, before the step posts it (0.7.39). |
| 586 | 6 | `public double handReserve()` | WHAT THE HAND'S BUYS HOLD OF THE FUND'S CASH (0.7.39; the spec's B9): a waiting order's whole amount, and a posted one's units still to fill at its price. |
| 594 | 5 | `double cashForTheRule()` | The fund's cash the rule may spend: its cash less the hand's reserve, never under nothing. |
| 601 | 9 | `void notePosted(HandOrder o, String key, double units, double price, int month)` | A hand order posted at the step, `units` at `price` a unit: it rests until the next step, and the ledger opens its row. |
| 612 | 3 | `void noteDropped(HandOrder o, String key, double units, double price, String why, int month)` | A hand order that could not be posted, and why: the ledger's row. |
| 617 | 15 | `void handFilled(boolean bond, int which, boolean buy, double q, double cash)` | One of the hand's fills, on a company's book or a bond's: the posted orders on it, oldest first, take it. |
| 634 | 17 | `void noteSplit(int company, double k, int month)` | A company's shares split by k (a consolidation under one): the player's orders in them, waiting or on the book, count in the new shares and price in them; the ledger's row (0.7.39). |
| 653 | 10 | `void closePosted(boolean bond)` | The step withdrew every order on one market: the hand's posted there close, and what they did not fill lapses. |
| 665 | 9 | `List<HandOrder> takeHandOrders(boolean bonds)` | ...taken by the market that posts them: the share orders, or the bond orders. |

### reading (lines 675-734)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 1 | `public int getLastPayInMonth()` |  |
| 678 | 1 | `public double getLastPayInYearSurplus()` |  |
| 679 | 1 | `public double getLastPayInFromSurplus()` |  |
| 680 | 1 | `public double getLastPayInFromCash()` |  |
| 681 | 1 | `public double getPaidInFromSurplus()` |  |
| 682 | 1 | `public double getPaidInFromCash()` |  |
| 683 | 1 | `public double getHandPaidIn()` |  |
| 684 | 1 | `public double getHandDrawnOut()` |  |
| 685 | 1 | `public double getTransfersPaid()` |  |
| 686 | 1 | `public double getTransfersShort()` |  |
| 687 | 1 | `public double getDividendsMarket()` |  |
| 688 | 1 | `public double getDividendsRescue()` |  |
| 689 | 1 | `public double getCoupons()` |  |
| 690 | 1 | `public double getPrincipal()` |  |
| 691 | 1 | `public double getBondFaceLost()` |  |
| 692 | 1 | `public double getPreferredBought()` |  |
| 693 | 1 | `public double getPreferredDividends()` |  |
| 694 | 1 | `public double getPreferredRedeemed()` |  |
| 695 | 1 | `public double getWarrantsBoughtBack()` |  |
| 696 | 1 | `public double getWarrantSharesTaken()` |  |
| 697 | 1 | `public double getSharesBought()` |  |
| 698 | 1 | `public double getSharesSold()` |  |
| 699 | 1 | `public double getBondsBought()` |  |
| 700 | 1 | `public double getBondsSold()` |  |
| 701 | 1 | `public double getRescueSold()` |  |
| 702 | 1 | `public double getMonthDividends()` |  |
| 703 | 1 | `public double getMonthCoupons()` |  |
| 704 | 1 | `public double getMonthPrincipal()` |  |
| 705 | 1 | `public double getMonthBought()` |  |
| 706 | 1 | `public double getMonthSold()` |  |
| 709 | 5 | `public double getRescuesPaid()` | What every resolution paid for the rescue book, over the fund's life (0.7.39). |
| 720 | 3 | `public double getPutIn()` | WHAT THE CITY HAS PUT INTO ITS FUND, over its life (0.7.39): the dial's pay-ins and the hand's, what the rescues paid, and the preferred the treasury bought - each money the city's own that became the fund's. |
| 725 | 3 | `public double getTakenOut()` | ...AND WHAT IT HAS TAKEN OUT: the transfers paid to the budget and the hand's draw-outs. |
| 730 | 4 | `public boolean isEmpty()` | True when it holds nothing and has never been asked for anything: a fund that has not begun. |

### the warrants' value (lines 735-798)

| line | len | member | says |
|---:|---:|---|---|
| 750 | 10 | `public static double callValue(double price, double strike, double rate, double sigma, double years)` | WHAT A CALL IS WORTH, Black and Scholes (1973): S N(d1) - K e^(-rT) N(d2), d1 = (ln(S/K) + (r + sigma^2/2) T) / (sigma sqrt T), d2 = d1 - sigma sqrt T. |
| 765 | 9 | `public static double normalCdf(double x)` | The standard normal distribution function: Zelen and Severo's approximation, Abramowitz and Stegun (1964) 26.2.17, to within 7.5e-8. |
| 781 | 17 | `public static double annualVolatility(double[] prices)` | A share's volatility, a year, from its monthly prices: the standard deviation of the last VOLATILITY_MONTHS monthly log returns, times the square root of twelve. |

### save and load (lines 799-962)

| line | len | member | says |
|---:|---:|---|---|
| 802 | 18 | **type** `public static final class State` | Everything the fund carries from one month to the next, by name. |
| 821 | 32 | `public State toState()` |  |
| 859 | 46 | `public void restore(State s)` | Puts a saved fund back. |
| 907 | 24 | `public void reset()` | A fund that has not begun: nothing held, the dial at 0, the rescue on the button. |
| 933 | 29 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM); share counts and the dial do not move. |

