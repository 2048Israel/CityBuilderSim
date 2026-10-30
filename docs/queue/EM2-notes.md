# EM2 - a share quoted at the middle of its book, and short selling ({next}, 2026-10-30)

The brief: [EM2.md](EM2.md). Jerus, 2026-10-29: "share price should reflect
mid price, and if no ask or bid then last traded. also, we need to implement
short selling cause ive seen bubbles form in game, are that last traded is
448k but current bids are 4k for example." Built on main after HD2 (0.8.76),
in the money lane; Opus with Sonnet slices (A: the harnesses on the quote; B:
the shorts; C1: two fixtures the quote moved; C2: the save, the screens, the
playtest's lines).

## 1. The bubble, found on main

Main's default playtest (0.8.76, 4,000 months) with a counter added and
nothing else: the months in which a share's last trade stood more than ten
times its best bid, per company.

| company | months | longest spell | worst | the book in the worst month |
|---|---:|---:|---|---|
| Business Services | 231 | 167 | 1,816x at m996 | last 18.80, ask 18.80, bid .0104, fair .0103 |
| Forestry | 206 | 61 | 117x at m2842 | last 294.2, ask 294.2, bid 2.51, fair 2.48 |
| Materials | 48 | 48 | 109x at m2418 | last 611.5, ask 611.5, bid 5.63, fair 5.57 |
| Bank | 43 | 11 | 158x at m3767 | last 11,330, ask 11,330, bid 71.6, fair 70.9 |
| Retail | 41 | 21 | 209x at m1298 | last 7.23, ask 7.23, bid .0345, fair .0342 |
| Industry | 40 | 23 | 1,310,406x at m3843 | last 1,326, ask 1,326, bid .0010, fair .0010 |
| Freight | 22 | 7 | 3,496x at m830 | last 35.31, ask 35.31, bid .0101, fair .0100 |
| Cement & Concrete, Construction, Real Estate | 5, 1, 1 | | | |

638 company-months in all; and the last trade, the price every holder and
screen read, stood over ten times FAIR VALUE in 3,111 company-months (2,959
without the bank's own shares; Business Services 857, Forestry 822). Most
months have no bid at all at the month's end (Business Services 2,609 of
4,000).

**The mechanism.** In every worst month the best ask IS the last trade. A
company's value collapses (Business Services' fair value went from about
18.80 to .0103); the bids follow it, but the asks rest at the last trade,
because a seller who takes the market (`Exchange.post()`, the cells
rebalancing under their cushion) rests "at the market", which was the last
trade; and at each step the book is emptied before anybody posts, so the
next month's sellers see the stale trade again and re-post at it. Nobody
buys at it, so it never trades away. The middle of such a book is still
about 900 times its value (18.80 and .0104 average to 9.4), so the quote
alone does not end it: a seller who names a price near value does.

## 2. The quote

`Exchange.price(c)` is the quote: the middle of the best bid and the best
ask while both rest; with one side empty, the last trade - held inside that
side, never over the ask that stands nor under the bid (★1b); with neither,
the last trade; fair value before the first trade, held the same way. Every
reader of the price reads it: trading (a price-taker's "market"), the
households, the world, the companies' buyback orders, new issues, the bank's
strike, the desk's mark, the fund's mark, the splits and the screens.
`hasTraded()` and the age of the last trade read the book's last trade, now
also `lastTrade(c)`, and `quoteIsLastTrade(c)` says when the quote is it.

- **The city's mark (★1).** A quote set by a bid and an ask, or held inside
  one, is current however old the last trade; STALE_MARK_MONTHS marks a
  holding at fair value only when the quote is a last trade older than a
  year.
- **A rebalancing cell waits at the middle.** A cell under its cushion that
  takes the market now asks at the quote, which with the desk's two sides
  resting is the middle, over the desk's bid: it no longer pays the desk's
  spread at the step, and meets the cells that buy at the middle instead
  (MarketCheck 3a re-premised on it). A cell short of money still sells into
  the bids in the waterfall, as before.
- **`Equity.lastPrice`** is the last offering's price, not the exchange's
  last trade: a new issue is sold at `exchange.price(c)`, so it follows the
  quote through its caller, and nothing in it changed.

## 3. Short selling

- **Who (★3).** The world sells short, as a participant of its own on the
  book (`Exchange.WORLD_SHORT`); the households' cells and the fund's market
  book lend (pro rata to what they hold; never the fund's rescue book). The
  bank's own shares are never shorted.
- **When and at what (★4).** In the step, after the world and the companies
  have posted and before the cells: where the quote stands more than
  `SHORT_BAND` (.20) over fair value, an ask at fair value plus that band, a
  limit order (it names its price; it does not rest at the market), for
  `FOREIGN_SPEED` of the gap over the shares outstanding, under the cap and
  what the lenders hold. It covers with a bid at fair value once the quote is
  within `BAND` of it.
- **The lending.** Struck at each step: the shares short are lent by the
  cells and the fund pro rata to what each holds; the world pays them
  `LENDING_FEE` (.5%) a year on their value at the quote the book last stood
  at, rising linearly over `HARD_TO_BORROW` (.50 of the lendable shares lent)
  by up to `HARD_TO_BORROW_FEE` (10%). The cells' part goes into their
  savings (`HouseholdBalance.creditLendingFee`, investment income, no
  dividend), the fund's into its cash (`TreasuryFund.receiveLendingFee`).
- **The dividend.** The lent shares stay the lender's, so every holder is
  paid per share on what it holds; the company pays on its shares in issue,
  the short on what it is short (`Equity.payDividend`'s `shortPays`).
- **The limits, each a dial.** `INITIAL_MARGIN` 1.5 (Regulation T: the
  proceeds and half again held as collateral), `MAINTENANCE_MARGIN` .30
  (FINRA 4210 for shorts): under it a margin call buys in all that is short;
  `MAX_SHORT_INTEREST` .20 of the shares outstanding. A recall: where the
  lenders hold less than is short, the difference is bought in. Both
  buy-ins are price-takers, taking the cheapest asks up to the break price
  (the collateral over the shares) and resting the rest at the market (the
  review's change: slice B bid at the break price, which a holder selling
  into a bid over value was paid in full - a recall with the quote at fair
  value bought at 1.8 times it).
- **The register stays whole.** The holdings add to the shares in issue
  plus what is short; what is lent adds to what is short
  (ShortSellingCheck 3, every month of its real run).
- **MoneyAudit.** Two credit lines, both income from the world: `+ equity
  DividendsPaidByShorts` (the holders were paid on more shares than the till
  paid for) and `+ fund LendingFee` (the fund's cash is a pool; the cells'
  part goes from the world to the households, both outside the pools). The
  trades are the lines they always were: the short sells and buys abroad.
- **Saved.** `Exchange.State` gains the positions, collateral, the lending
  book, the month's fees, the life counters and the life's dividends paid by
  shorts: new keys only, so an old save reads nothing short and needs no
  format bump. ShortSellingCheck 6 saves in the middle of a short and plays
  the month after in both cities.

## 4. The playtest, before and after

The default playtest, 4,000 months, main (0.8.76 with the counter) against
the final model (`LongPlaytest`'s BUBBLES, SHORTS and MARKET lines):

| | main | EM2 |
|---|---:|---:|
| company-months a last trade stood over 10x the best bid | 638 (Business Services 231, longest 167) | 85 (Retail 18, longest 5) |
| ...the price read stood over 10x fair value (main: the last trade; EM2: the quote), the bank apart | 2,959 | none |
| ...the bank's own shares (never shorted) | 152 | 107 |
| company-months the short's ask stood | - | 5,552 |
| shares sold short, margin calls, recalls, fees | - | none |
| people at m4,004 | 675,985 | 692,385 |
| GDP a month at m4,004 | $5.6T | $12.5T |
| P/E low/median/high, m600 | 18.8 / 22.2 / 188.7 | 18.9 / 22.4 / 40.9 |
| P/E, m4000 | 13.8 / 22.0 / 22.4 | 18.0 / 19.3 / 21.4 |
| turnover, % of cap a year, m4000 | 2.4 | 2.2 |

**No short opens in a real run.** The short asks at fair value plus
`SHORT_BAND`, and nobody in the default market bids that high: the cells
and the world bid at most fair value plus half `SPREAD`, a company's buyback
fair value plus `BUYBACK_TOLERANCE` (.10). Only the player's hand pays more.
The ask is what does the work: standing on the book it puts the quote at
the middle, near value, and the sellers who take the market then rest
there, not at the stale print. The fill, the fee, the dividend on the
short, the margin call and the recall run in ShortSellingCheck's fixtures
and its town with a short opened by hand (sections 3 to 6), and in the
audit's own month.

**The ensemble** (`docs/ensemble/EM2.md`, 80 runs, two at a time, against
`docs/ensemble/0.8.76.md`): 23 of 25 criteria, as the baseline; the same two
fail - A2, the longest spell of inflation over 10% from m240, 11 months [3 to
20] against 10 [6 to 17], and S1, ZERO_SLACK's inflation against its twin,
its longest spell 44 months against 60. AUTO2's final population 271,853
against 280,956 (T2 holds: at least 75%).

## 5. HD2-S: ScaleCheck's growing town

(filled in below)

## 6. The calls (★; each the recommended way, BP1)

- ★1 **The stale mark only where the quote is an old last trade** (the
  brief's recommendation, taken): a bid and an ask resting are somebody ready
  to trade now.
- ★1b **The last trade held inside the side that stands.** His words: "if no
  ask or bid then last traded". Taken as written where neither side rests;
  where one does, the last trade never stands over the ask nor under the bid,
  since a price over an ask anybody may take is not a price. It matters when
  the short's ask stands alone: the 448k print over a 4k ask is then quoted
  at the ask.
- ★2 **The companies' bonds keep their last trade** (the brief: he named
  shares). Pending: EM2-B.
- ★3 **The world alone sells short; the cells and the fund lend.** The brief
  named the households, the fund, the world and the desk. One seller that
  names a price near value is what the bubble needs, and the world has it
  with no new account: each of the others needs a margin account in machinery
  the world does not have - the cells are averaged households whose holdings
  follow people between cells, leave with the emigrants and sell in the
  waterfall; the fund's rule is a long 70/30 mix with ledger lots; the desk's
  short is a liability the bank's securities line (`markToMarket`, longs
  only) and its capital ratios do not carry. Pending: EM2-H.
- ★4 **The short reads value, not the return, and its band is .20.** The
  brief: a holder whose expected return is under the others' past EM1's band.
  A cash-box company at fair value returns its money's rate, under the
  yardstick, so a return rule would short it at value; the short sells where
  the price is over value instead. `SHORT_BAND` .20 rather than `BAND` .05:
  over `BUYBACK_TOLERANCE` (.10), so a short never sells into a company's bid
  for its own shares, and past the fee, the dividend and the margin's risk (a
  short sold at 1.2 times value is called at 1.38).
- ★5 **A margin call and a recall buy in at the market.** The cheapest asks
  first, up to the break price, the rest resting at the quote - not a bid at
  the break price, which the holders' rule for a bid over value fills in
  full (slice B's way; the review's change).
- ★6 **The bank's own shares are never shorted**: they trade by its capital
  policy (Bank.buybackRoom(), Bank.issuesOwnShares()).
- ★7 **The dials where the brief gave a range or none**: `LENDING_FEE` .005
  (the brief's .3% to 1%), `HARD_TO_BORROW` .50 and `HARD_TO_BORROW_FEE` .10,
  `MAX_SHORT_INTEREST` .20 of the shares outstanding.

## 7. The harnesses

Re-premised on the quote, no assertion's claim or tolerance moved:
ExchangeCheck (the price is the quote; a guard that the quote differed from
the last trade in 869 of 1,648 company-months), MarketCheck (3a's cells
meet the desk on either side of a one-sided book; 3b with shorts off, since
its stale ask is the case), FundCheck (the fixture company the households
hold 15% of; the stale mark on an emptied book, and a new line: a bid and an
ask resting mark at the quote), FundLedgerCheck (the fund's first holding
under the cap, which no split now raises), BuildCardCheck (the seeded price
on an emptied book), CapitalFlowCheck (the second month's twin is at a loss
under the quote, so the loss shields part of the coupon from the profit tax:
named aside and asserted, `max(pre-tax x rate, 0)`). New: ShortSellingCheck,
seven sections. SaveFileCheck and ReadPathCheck carry the new fields.

**The screens** (FundScreen, SectorScreen, PeopleScreen, BankScreen,
TradeScreen, FundView) say "the quote" where they said "the last trade",
with BID / ASK and LAST beside it, and the sector screen a SHORT figure when
anything is short. Compiled, not yet looked at by eye: for the screen check.
