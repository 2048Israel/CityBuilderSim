# TreasuryFund.java - 761 lines · 115 methods · 9 constants · model

`ham/citybuildersim/TreasuryFund.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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
> ... (24 more lines in the source)

**Used by (13):** [Bank](Bank.md), [BankScreen](BankScreen.md), [BondMarket](BondMarket.md), [DataSave](DataSave.md), [Exchange](Exchange.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 96 | the dials |
| 125 | the settings |
| 139 | the preferred offer |
| 147 | the rescues |
| 187 | the hand |
| 227 | the record |
| 249 | the money |
| 269 | money in: the dial |
| 321 | the mix |
| 348 | money out: the transfer |
| 385 | what it receives |
| 450 | the rescue |
| 471 | the preferred offer |
| 500 | the hand |
| 519 | reading |
| 558 | the warrants' value |
| 622 | save and load |

## Enum constants

| line | constant | says |
|---:|---|---|
| 130 | `TreasuryFund.RescueMode.AUTOMATIC` | The city steps in the month the bank fails - a new game's setting. |
| 132 | `TreasuryFund.RescueMode.BUTTON` | The bank waits frozen, carrying its hole at the central bank's window, until the player presses the Bank tab's button - an old save's, and the bare constructor city's. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `TreasuryFund.MAX_DIAL` | `3.0` | The most the dial takes: three times the year's surplus - Jerus's "max 300%"; past 100% the extra comes from the treasury's cash. |
| 102 | `TreasuryFund.EQUITY_WEIGHT` | `.70` | The share of the market book in company shares: 70%, the Government Pension Fund Global's strategic equity weight (nbim.no/en/investments); the rest is the businesses' bonds. |
| 105 | `TreasuryFund.REBALANCE_OVER` | `.74` | Rebalanced when its equity share passes this: 74%, the GPFG mandate's "If the equity share ... |
| 108 | `TreasuryFund.REBALANCE_UNDER` | `.04` | ...or falls this far under EQUITY_WEIGHT: four points, the same mandate's "more than four percentage points lower than the weight in the strategic benchmark index". |
| 111 | `TreasuryFund.OWNERSHIP_LIMIT` | `.10` | The most of any one company's shares the rule holds: 10%, the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares in an individual company" (14 May 2018). |
| 114 | `TreasuryFund.TRANSFER_RATE` | `.03` | What the fund pays the budget a year, of its whole value: 3%, Norway's fiscal rule - transfers follow the fund's expected real return, 3% since 2017 (regjeringen.no). |
| 117 | `TreasuryFund.YEAR_MONTHS` | `12` | A year, in months: the transfer's twelfth and the surplus's year. |
| 120 | `TreasuryFund.VOLATILITY_MONTHS` | `12` | The months of the bank share's price history its volatility is read over, for the warrants' value: a year of the monthly prices the history keeps (HistorySave's share prices). |
| 123 | `TreasuryFund.OFFER_AGAIN_MONTHS` | `3` | A declined offer comes back after this many months while the bank is still under its minimum: a quarter (Jerus, round 2: "Quarter") - and the bank asks no sooner after an accepted one either, whose size was what it as... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 135 | `private double cash` |  |
| 136 | `private double dial` |  |
| 137 | `private RescueMode rescue` |  |
| 141 | `private boolean offerPending` |  |
| 142 | `private int offerMonth` |  |
| 143 | `private int declinedMonth` |  |
| 144 | `private int acceptedMonth` |  |
| 145 | `private int offersMade, offersAccepted, offersDeclined` |  |
| 150 | `private double rescueCost` | What the city paid for the rescue book's shares it still holds: their cost, reduced in proportion when the hand sells some. |
| 159 | `int month` |  |
| 160 | `double paid, fromCash, advanced, shortfall, exitCapital` |  |
| 161 | `double shares, householdsShares, householdsValue, worldShares, worldValue, fundShares, fundValue` |  |
| 162 | `double preferredCancelled, warrantsCancelled` |  |
| 185 | `private final List<Resolution> resolutions` |  |
| 197 | `boolean bond` |  |
| 198 | `int company` |  |
| 199 | `int bondId` |  |
| 200 | `boolean buy` |  |
| 201 | `double amount` |  |
| 202 | `int month` |  |
| 225 | `private final List<HandOrder> hand` |  |
| 230 | `private int lastPayInMonth` | The last year-end pay-in, and the year's transfers so far (the Finances page's "this year"). |
| 231 | `private double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash` |  |
| 232 | `private int transferYear` |  |
| 233 | `private double transfersThisYear, transferShortThisYear` |  |
| 236 | `private double paidInFromSurplus, paidInFromCash, handPaidIn, handDrawnOut` | Over the fund's life, for the page and the playtest. |
| 237 | `private double transfersPaid, transfersShort` |  |
| 238 | `private double dividendsMarket, dividendsRescue, coupons, principal, bondFaceLost` |  |
| 239 | `private double preferredBought, preferredDividends, preferredRedeemed, warrantsBoughtBack, warrantSharesTaken` |  |
| 240 | `private double sharesBought, sharesSold, bondsBought, bondsSold, rescueSold` |  |
| 246 | `private double transferDue, transferPaid` | This month's working, cleared at the top of the month: the transfer (a budget line too - NationalAccounts carries it) and what came in and went out, which the Fund page reads after the month has closed - and so saved ... |
| 247 | `private double monthDividends, monthCoupons, monthPrincipal, monthBought, monthSold` |  |
| 626 | `double cash, dial` |  |
| 627 | `String rescue` |  |
| 628 | `boolean offerPending` |  |
| 629 | `int offerMonth, declinedMonth, acceptedMonth, offersMade, offersAccepted, offersDeclined` |  |
| 630 | `double rescueCost` |  |
| 631 | `List<Resolution> resolutions` |  |
| 632 | `List<HandOrder> hand` |  |
| 633 | `int lastPayInMonth, transferYear` |  |
| 634 | `double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash, transfersThisYear, transferShortThisYear` |  |
| 635 | `double[] life` |  |
| 637 | `double[] month` | The month just closed: its transfer due and paid, and what came in and went out. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 94 | 668 | **type** `public final class TreasuryFund` | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand, and the bank it took over in a rescue (0.7.14). |

### the dials (lines 96-124)

### the settings (lines 125-138)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 6 | **type** `public enum RescueMode` | When the bank fails: resolved the month it fails, or frozen until the player presses the button. |

### the preferred offer (lines 139-146)

### the rescues (lines 147-186)

| line | len | member | says |
|---:|---:|---|---|
| 158 | 26 | **type** `public static final class Resolution` | One resolution, as it happened: the month; what the city paid and how - from its cash, and what the central bank will advance; the hole and the capital to reopen; the shares it took, and from whom, at the price they l... |
| 164 | 1 | `Resolution()` _(in TreasuryFund.Resolution)_ |  |
| 166 | 1 | `public int month()` _(in TreasuryFund.Resolution)_ |  |
| 167 | 1 | `public double paid()` _(in TreasuryFund.Resolution)_ |  |
| 168 | 1 | `public double fromCash()` _(in TreasuryFund.Resolution)_ |  |
| 169 | 1 | `public double advanced()` _(in TreasuryFund.Resolution)_ |  |
| 170 | 1 | `public double shortfall()` _(in TreasuryFund.Resolution)_ |  |
| 171 | 1 | `public double exitCapital()` _(in TreasuryFund.Resolution)_ |  |
| 172 | 1 | `public double shares()` _(in TreasuryFund.Resolution)_ |  |
| 173 | 1 | `public double householdsShares()` _(in TreasuryFund.Resolution)_ |  |
| 174 | 1 | `public double householdsValue()` _(in TreasuryFund.Resolution)_ |  |
| 175 | 1 | `public double worldShares()` _(in TreasuryFund.Resolution)_ |  |
| 176 | 1 | `public double worldValue()` _(in TreasuryFund.Resolution)_ |  |
| 177 | 1 | `public double fundShares()` _(in TreasuryFund.Resolution)_ |  |
| 178 | 1 | `public double fundValue()` _(in TreasuryFund.Resolution)_ |  |
| 179 | 1 | `public double preferredCancelled()` _(in TreasuryFund.Resolution)_ |  |
| 180 | 1 | `public double warrantsCancelled()` _(in TreasuryFund.Resolution)_ |  |
| 182 | 1 | `public double ownersLost()` _(in TreasuryFund.Resolution)_ | What the old owners lost, at the last price: the households', the world's and the fund's own market book. |

### the hand (lines 187-226)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 28 | **type** `public static final class HandOrder` | One of the player's orders, waiting for the month's step: a company's shares (company, Equity's index) or a bond (bondId), to buy with this much money or to sell this many shares or this much face. |
| 204 | 1 | `HandOrder()` _(in TreasuryFund.HandOrder)_ |  |
| 206 | 8 | `HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month)` _(in TreasuryFund.HandOrder)_ |  |
| 215 | 1 | `public boolean bond()` _(in TreasuryFund.HandOrder)_ |  |
| 216 | 1 | `public int company()` _(in TreasuryFund.HandOrder)_ |  |
| 217 | 1 | `public int bondId()` _(in TreasuryFund.HandOrder)_ |  |
| 218 | 1 | `public boolean buy()` _(in TreasuryFund.HandOrder)_ |  |
| 220 | 1 | `public double amount()` _(in TreasuryFund.HandOrder)_ | Money to spend on a buy; shares or face to sell. |
| 222 | 1 | `public int month()` _(in TreasuryFund.HandOrder)_ | The month it was placed in. |

### the record (lines 227-248)

### the money (lines 249-268)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 1 | `public double getCash()` | Its cash: what waits for the book, and what the transfer is paid from. |
| 255 | 1 | `void receive(double amount)` | Cash in: a dividend, a coupon, a sale, a pay-in. |
| 258 | 1 | `void pay(double amount)` | Cash out: a purchase, the transfer, a draw-out. |
| 261 | 1 | `public double getDial()` | The dial: a share of the year's budget surplus, 0 to MAX_DIAL. |
| 264 | 1 | `public void setDial(double d)` | ...set by the player (the Finances tab), clamped to 0-MAX_DIAL. |
| 266 | 1 | `public RescueMode getRescueMode()` |  |
| 267 | 1 | `public void setRescueMode(RescueMode mode)` |  |

### money in: the dial (lines 269-320)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 9 | `public static double[] payIn(double dial, double yearSurplus, double unused, double cash, double floor)` | WHAT THE DIAL TAKES AT A YEAR END, from the year's budget surplus and from the treasury's cash: {from the surplus, from cash}. |
| 299 | 4 | `public static double reservedFor(double dial, double surplusThisYearSoFar)` | WHAT THE ROLLOVER MUST LEAVE FOR THE FUND during a year: the dial's share of this calendar year's surplus so far, which the year-end pay-in will claim - so the dial's share is taken before the rollover nets (Jerus's e... |
| 305 | 9 | `void notePayIn(int month, double yearSurplus, double fromSurplus, double fromCash)` | A year-end pay-in, booked: the surplus's part and the cash's, off the treasury into the fund (the caller moves the treasury's cash). |
| 316 | 1 | `void notePaidInByHand(double amount)` | The player pays in by hand: the caller moves the treasury's cash. |
| 319 | 1 | `void noteDrawnOutByHand(double amount)` | ...and draws out. |

### the mix (lines 321-347)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 5 | `public static double sharesOver(double shares, double bonds, double cash)` | WHAT THE RULE SELLS OF ITS SHARES to rebalance, in money at its marks: the excess over EQUITY_WEIGHT of its market book and cash when its shares are more than REBALANCE_OVER of them; nothing otherwise. |
| 342 | 5 | `public static double bondsOver(double shares, double bonds, double cash)` | ...AND OF ITS BONDS: the excess over the rest of the weight when its shares are more than REBALANCE_UNDER under EQUITY_WEIGHT and its bonds over theirs - the mandate's lower trigger. |

### money out: the transfer (lines 348-384)

| line | len | member | says |
|---:|---:|---|---|
| 351 | 3 | `public static double transferOn(double value)` | A month's transfer on a fund of this value: a twelfth of TRANSFER_RATE of it. |
| 360 | 15 | `double payTransfer(double value, int year)` | Pays the month's transfer on a fund worth `value`, from its cash only - "the rule never sells to pay it" - and returns what was paid; what the cash could not cover is simply not paid, and counted (getTransferShort()). |
| 377 | 1 | `public double getTransferDue()` | This month's transfer as due, and as paid. |
| 378 | 1 | `public double getTransferPaid()` |  |
| 380 | 1 | `public double getTransferShort()` | ...and what the cash could not cover of it this month. |
| 382 | 1 | `public double getTransfersThisYear()` | This calendar year's transfers, paid and unpaid. |
| 383 | 1 | `public double getTransferShortThisYear()` |  |

### what it receives (lines 385-449)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 8 | `void receiveDividend(double amount, double rescueShare)` | A dividend on its shares, split between its books by what each holds. |
| 398 | 1 | `void receiveCoupon(double amount)` | A coupon on its bonds. |
| 401 | 1 | `void receivePrincipal(double amount)` | A bond's principal at its maturity. |
| 404 | 1 | `void noteBondLoss(double face)` | Face a default took off its bonds: a loss, no cash. |
| 407 | 3 | `void receivePreferredDividend(double amount)` | A preferred dividend the bank paid it. |
| 412 | 4 | `void receiveRedemption(double par, double warrants)` | The bank redeemed preferred at par, and bought warrants back at their value. |
| 418 | 1 | `void notePreferredBought(double par)` | The treasury bought preferred for the rescue book: its cost, for the record (the treasury paid). |
| 421 | 1 | `void noteWarrantShares(double n)` | Warrants exercised at their expiry: new shares into the rescue book, for nothing. |
| 424 | 6 | `void noteBought(double money, boolean bond)` | A trade on the book: shares or a bond bought or sold, and how much of a sale came out of the rescue book. |
| 430 | 6 | `void noteSold(double money, boolean bond)` |  |
| 438 | 5 | `void noteRescueSold(double sharesSoldFromRescue, double rescueHeldBefore, double money)` | The hand sold rescue-book shares: their cost comes off the book's cost in proportion, and the sale is counted. |
| 445 | 4 | `void startMonth()` | Clears the month's working. |

### the rescue (lines 450-470)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 4 | `void noteResolution(Resolution r)` | A resolution happened: the shares are the rescue book's, at what the city paid. |
| 459 | 5 | `static Resolution newResolution(int month)` | Builds a resolution's record; Game fills it. |
| 466 | 1 | `public List<Resolution> getResolutions()` | Every resolution the city has made, oldest first. |
| 469 | 1 | `public double getRescueCost()` | What the city paid for the rescue book's shares it still holds. |

### the preferred offer (lines 471-499)

| line | len | member | says |
|---:|---:|---|---|
| 474 | 1 | `public boolean isOfferPending()` | True while the bank's offer waits for the player's answer. |
| 476 | 1 | `public int getOfferMonth()` | The month it was made, or -1. |
| 478 | 1 | `public int getDeclinedMonth()` | The month it was last declined, or -1. |
| 480 | 1 | `public int getAcceptedMonth()` | The month one was last accepted, or -1. |
| 483 | 4 | `public boolean mayOffer(int month)` | Whether the bank may ask again this month: nothing pending, and a quarter since the city last answered, either way. |
| 488 | 1 | `void noteOffered(int month)` |  |
| 489 | 3 | `void noteAccepted(int month, double par)` |  |
| 492 | 1 | `void noteDeclined(int month)` |  |
| 494 | 1 | `void noteLapsed()` | The bank is back over its minimum on its own: the offer lapses, answered by nobody. |
| 496 | 1 | `public int getOffersMade()` |  |
| 497 | 1 | `public int getOffersAccepted()` |  |
| 498 | 1 | `public int getOffersDeclined()` |  |

### the hand (lines 500-518)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 1 | `void queue(HandOrder order)` | Queues one of the player's orders for the next step. |
| 506 | 1 | `public List<HandOrder> getHandOrders()` | The orders waiting for the next step, oldest first. |
| 509 | 9 | `List<HandOrder> takeHandOrders(boolean bonds)` | ...taken by the market that posts them: the share orders, or the bond orders. |

### reading (lines 519-557)

| line | len | member | says |
|---:|---:|---|---|
| 521 | 1 | `public int getLastPayInMonth()` |  |
| 522 | 1 | `public double getLastPayInYearSurplus()` |  |
| 523 | 1 | `public double getLastPayInFromSurplus()` |  |
| 524 | 1 | `public double getLastPayInFromCash()` |  |
| 525 | 1 | `public double getPaidInFromSurplus()` |  |
| 526 | 1 | `public double getPaidInFromCash()` |  |
| 527 | 1 | `public double getHandPaidIn()` |  |
| 528 | 1 | `public double getHandDrawnOut()` |  |
| 529 | 1 | `public double getTransfersPaid()` |  |
| 530 | 1 | `public double getTransfersShort()` |  |
| 531 | 1 | `public double getDividendsMarket()` |  |
| 532 | 1 | `public double getDividendsRescue()` |  |
| 533 | 1 | `public double getCoupons()` |  |
| 534 | 1 | `public double getPrincipal()` |  |
| 535 | 1 | `public double getBondFaceLost()` |  |
| 536 | 1 | `public double getPreferredBought()` |  |
| 537 | 1 | `public double getPreferredDividends()` |  |
| 538 | 1 | `public double getPreferredRedeemed()` |  |
| 539 | 1 | `public double getWarrantsBoughtBack()` |  |
| 540 | 1 | `public double getWarrantSharesTaken()` |  |
| 541 | 1 | `public double getSharesBought()` |  |
| 542 | 1 | `public double getSharesSold()` |  |
| 543 | 1 | `public double getBondsBought()` |  |
| 544 | 1 | `public double getBondsSold()` |  |
| 545 | 1 | `public double getRescueSold()` |  |
| 546 | 1 | `public double getMonthDividends()` |  |
| 547 | 1 | `public double getMonthCoupons()` |  |
| 548 | 1 | `public double getMonthPrincipal()` |  |
| 549 | 1 | `public double getMonthBought()` |  |
| 550 | 1 | `public double getMonthSold()` |  |
| 553 | 4 | `public boolean isEmpty()` | True when it holds nothing and has never been asked for anything: a fund that has not begun. |

### the warrants' value (lines 558-621)

| line | len | member | says |
|---:|---:|---|---|
| 573 | 10 | `public static double callValue(double price, double strike, double rate, double sigma, double years)` | WHAT A CALL IS WORTH, Black and Scholes (1973): S N(d1) - K e^(-rT) N(d2), d1 = (ln(S/K) + (r + sigma^2/2) T) / (sigma sqrt T), d2 = d1 - sigma sqrt T. |
| 588 | 9 | `public static double normalCdf(double x)` | The standard normal distribution function: Zelen and Severo's approximation, Abramowitz and Stegun (1964) 26.2.17, to within 7.5e-8. |
| 604 | 17 | `public static double annualVolatility(double[] prices)` | A share's volatility, a year, from its monthly prices: the standard deviation of the last VOLATILITY_MONTHS monthly log returns, times the square root of twelve. |

### save and load (lines 622-761)

| line | len | member | says |
|---:|---:|---|---|
| 625 | 14 | **type** `public static final class State` | Everything the fund carries from one month to the next, by name. |
| 640 | 30 | `public State toState()` |  |
| 676 | 41 | `public void restore(State s)` | Puts a saved fund back. |
| 719 | 21 | `public void reset()` | A fund that has not begun: nothing held, the dial at 0, the rescue on the button. |
| 742 | 19 | `public void redenominate(double scale)` | Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM); share counts and the dial do not move. |

