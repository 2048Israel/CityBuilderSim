# RB3: the fund's over-limit sale and the fixed point (2026-11-20)

Built by Claude (Opus 5.5), headless, on `batch/RB3` cut from origin/main 1557daaaf (0.9.173). The row is
QUEUE.md's RB3, found in FS13 (`docs/queue/FS13-notes.md`, ★g).

## What caused it

RB1's over-limit sale (`Exchange.postFundOverLimit()`) asked a tenth of the fund's excess over 10% each
month at fair value, posted after the desk and the leavers (the emigrants) and before the world and the
companies. Its comment said that at fair value the fund's ask is under the desk's and the households', so a
buyback, which takes the cheapest ask first, meets it. The leavers were left out of that claim. They ask the
desk's bid, fair value less half a spread, and rest at the market (`post()`). So:

- where the book last traded under fair value and the desk was closed, the leavers rested under the fund's ask;
- where the desk's bid stood, they rested at fair value, level with the fund's ask and ahead of it by time.

When the leavers offered more than the buyback's money bought, the buyback took theirs and the fund's ask
rested. The buyback still retired shares around the fund, so the fund's share of the company rose.

FS13's town, played again here without its world bid (a scratch diagnostic, not committed: FundCheck 9e's
fixture month by month). On 60 months of founding, Construction, m138 to m171:

- the desk was closed;
- the last trade was at the desk's bid (0.9900 of fair value);
- the leavers asked 230 to 400 shares a month at that bid, and the buyback bought about 200;
- the fund's ask at fair value (19.75 rising to 83.31 shares) rested unfilled 34 months running;
- the holding went from 10.27% to 11.17%, against the fixed point of 10.92%. On 84 months of founding it reached 11.14%.

## What changed (the model)

- **The price.** `Exchange.limitAsk()` asks the market's own price: the book's last trade, held between
  the desk's bid (fair value less `TreasuryFund.RULE_PREMIUM`) and fair value. Before the first trade it
  asks fair value. It also asks fair value on a last trade older than `Exchange.STALE_MARK_MONTHS`, the age
  past which the fund marks the holding at fair value (`markedAtFair()`), so it never sells under its own
  mark on a price nobody has paid for a year (REV-RB3's P3). That month the leavers can rest ahead of the
  fund, and their trade makes the next month's price current.
- **The place in the month.** The ask is posted after the desk and before the leavers, so at an equal
  price it stands first (the book is price-time, `OrderBook` line 95).
- **What follows.** A buyback meets the fund's ask before every other ask that the rules make, except one
  case: the world selling on its yield (`postWorld()`, never less than `FOREIGN_TOLERANCE` under fair
  value). Where the desk's bid stands and the last trade is at or under it, the fund's ask sells into that
  bid. The withdrawal's forced sale already takes that path (A21).
- **What the leavers now ask in one case** (REV-RB3's P3). A month the fund is over its limit, where the book
  last traded over fair value and no bid stands (the desk closed, or its bid taken), the leavers' unsold
  shares now rest at fair value, not at the last trade. The fund's ask at fair value is in the book before
  them, and `post()` rests a seller at the market (`price()`, the last trade bounded by that ask). So a
  buyer pays the leavers fair value, not the trade over it. Before RB3 they posted before the fund and
  rested at the last trade. `getLifePostedSellBy()`, the class's posted-sell diagnostic, counts their asks
  at the same lower price. With the desk closed and the last trade at or under fair value, the leavers'
  price is unchanged: the fund's ask is at the price they rest at, or over it.
- **The comments.** The comments of `postFundOverLimit()` and `postFund()`, the class header and
  `TreasuryFund`'s say all this, with RB3's case.
- **A new reading.** `getFundLimitAsked(int c)` gives one company's ask for the month (not saved, as the
  total is not), for FundCheck 9e.

## The calls

- **★ The limit sale asks the market. The fixed point does not bend** (the row's design question). The
  fixed point L x s / (s - b) is where the tenth sold a month balances the shares a buyback retires.
  An unsold tenth leaves no point of rest: every month the buyback runs, the holding rises by 1 / (1 - b),
  with nothing to stop it until the buyback itself stops, so 11.17% was only where that town's buybacks
  happened to end. "The line states the rest the rule gives" therefore has no number to state. The rule's
  purpose (the GPFG's 10%) is met only if the sale is made.
- **★ At the last trade, between the desk's bid and fair value.** Three prices were weighed:
  - *The desk's bid always* (A21's forced sale). It is simpler, but it gives up half a spread every month
    the book trades at fair value or over it, and up to the 10% buyback tolerance when a buyback is paying
    that. In those months RB1's fair-value ask already filled (FS13's town at m91 to m101: the sale filled
    while the last trade stood at 1.10 of fair value).
  - *The last trade with no floor.* A household's waterfall sale at its own borrowing price would set the
    fund's price.
  - *The clamp* (taken). Fair value while the book trades at or over it, as RB1. Under it, the price the
    market last paid, never under the price somebody stands ready to pay (the desk's bid). That is also
    the least any leaver asks, so no leaver's ask rests under the fund's.
- **★ Posted before the leavers, not after them.** At the desk's bid, or at a last trade the leavers rest
  at, the two asks are level, and the book's time priority decides. After them, the leavers stood first,
  which was FS13's failure with the desk open. A clamp under the leavers' price instead would be a fixed
  discount for nothing.
- **★ FundCheck 9e loses FS13's world bid.** FS13 caused a world bid at fair value for every ask at or
  under it each month because the model's sale did not fill, and queued this row for the model. With the
  model mended, that premise would hide the sale under test, and 9e's own heading says "the town plays as
  itself". Its fixture line ("a bid took every share the fund asked") becomes a law of the model: in every
  month the company bought back over the limit, the fund sold what it asked, or all that was retired. The
  second half is needed: on 72 months of founding, at m182, the buyback's money bought 8.94 shares, all
  of them the fund's, against 17.14 asked. A month the company split is left out of that count, since it
  is counted in two units. This moves no premise to let a change through. It removes a stand-in for the
  model, as FS13's notes asked.
- **Not queued: the world's yield sale under the desk's bid.** The fixed point's fastest b assumes the
  buyback pays the desk's bid at the least. In theory the world's sale can rest down to `FOREIGN_TOLERANCE`
  (10%) under fair value, and a buyback filled there retires more. It was not seen in any run here. It is
  the fixed point's premise, as it was before this batch, not a bug found.

## The checks

- **ExchangeCheck 7c (new): the condition, caused.** The town is 7b's company and its buyback, with 20 of
  its 100 owners leaving with 2,000 shares (section 3's leavers, more than the buyback's 84 a month). The
  fund holds L x s / (s - b) of the company at the fastest buyback, the last trade is seeded with
  `seedLastPrice`, and fair value is read off a twin's step. Five cases:
  - (a) Desk closed, last trade a spread under fair value. The fund sold its whole tenth (9.1912 shares)
    at the desk's bid. The leavers' asks at that price still rest (1,925.0161). The holding is
    (held - tenth) / (shares left) = 10.919118%, no higher than the fixed point it started at.
  - (b) Last trade inside the half-spread. The tenth sold at that trade's price.
  - (c) Desk open. The tenth sold at the desk's bid.
  - (d) Last trade over fair value. The ask is fair value, RB1's.
  - (e) (a) again, with a real trade in the book, a share at a spread under fair value in month 1
    (REV-RB3's P3). With the step 12 months on (`STALE_MARK_MONTHS`), the tenth sold at the desk's bid, as
    in (a). A month later the fund asked fair value, as its mark reads the holding: all 9.1912 shares rest
    there and none sold.
  - **Against main's Exchange** (this ExchangeCheck run with main's Exchange first on the classpath; the
    model is otherwise the same on 1557daaaf and the merged origin/main): every line of 7c fails except its
    four fixtures, (d) and (e)'s last, which are the other side of each threshold by design. That is 9 FAIL
    lines, for example "the fund sold the tenth ... 0.000000000 != 9.191176471" and "the fund's holding is
    the rule's ... 0.110118096 != 0.109191176".
  - **Against the branch without the stale clause:** (e)'s last line fails alone, the tenth sold at the
    desk's bid on the 13-month-old trade.
  - On the branch: 15 of 15 OK, and ExchangeCheck exits 0.
- **FundCheck 9e without the world bid.** On 72 months of founding: cap sale in 239 months, Construction
  bought back in 235, most held 10.2786% against 10.9191%, the fund short of its ask in 0 (its printed
  line now gives that count), and every line OK. FundCheck exits 0.
- **9e against main's Exchange** (REV-RB3's P2; the branch's FundCheck with main's Exchange first on the
  classpath, plus a copy of the per-company reading that changes nothing). Main held 10.2778% at most,
  under the fixed point, so 9e's fixed-point line passes on main and cannot catch the fund's ask going back
  to rest at fair value. Its unmet line does: "in every month the company bought back over the limit, it
  met the fund's ask first ... FAIL", with the fund short in 1 month. That month the ask rested while the
  buyback retired other shares. It is FundCheck's only red there (exit 1). So 9e's unmet line guards the
  sale on the town's own path, and 7c guards each price exactly.
- **9e's run on other founding counts** (the diagnostic, which uses the same fixture):

  | founding | branch: most held | branch: months short of the ask | main: most held | main: months short |
  |---|---|---|---|---|
  | 60 | 10.3060% | 0 of 169 | 11.1721% | 37 of 169 |
  | 72 | 10.2786% | 1 of 234 (m182, the buyback's whole money, above) | 10.2778% | 2 |
  | 84 | 10.3039% | 0 of 233 | 11.1413% | 34 |

  "Short of the ask" means sold less than asked in a month asked and bought back.
- **FundCheck section 8's price line** ("...at fair value" for the over-limit sale) now reads "at the
  market". Every ask and fill of the fund lies between the desk's bid and fair value. Its town's last
  trade is path; 7c holds each side exactly.
- **The default trace.** Main's default playtest (1557daaaf, `-Dplaytest.trace`, one JVM, both data files
  named) reports 0 of 41,190 company-months over the 10% and the most of any company 0.0%. The cap sale
  never fires, so nothing posts where the order moved. On this branch at that base (the same JVM line) all
  eight CSVs are byte-identical to main's. The logs differ only in their run times (595 s, 640 s) and the
  clock stamps of two lines.
- **The trace again, on the merged base** (after REV-RB3, with origin/main's OE1 and FS4 merged in and
  the stale clause added). Main's model is the merged classes with origin/main's Exchange and
  TreasuryFund first on the classpath. It reports 0 of 36,831 company-months over the 10% and the most of
  any company 4.0%. The branch's final model, the same JVM line, gives eight CSVs byte-identical to main's.
  The logs differ only in their run times (1,810 s, 2,372 s) and one object's identity hash in a printed
  land parcel. No ensemble (the contract, 4).
- `./batch-check.sh --fast`: compile OK, maps regenerated, Stale 0 firm findings, tree clean but for the
  generated docs.
- **The pick** (`sh tools/pick.sh origin/main HEAD`): 38 of 233 harnesses for the 12 files changed. Every one
  ran on the branch. Its reds are main's, as known-reds.txt or gate.txt records them:
  - BankCheck: "it pays its savers something" (FS9).
  - CopperRefiningCheck: "the rule cut an order in at least one month of the run".
  - YoungBankCheck: "a spell under Bank.CAPITAL_RATIO begins" (FS9).
  - KeptCheck: its four.
  TimingCheck's timing lines are soft. The run of all 38 at once hit its two-hour limit with 36 logs
  written. BankFailureCheck, GovernmentPlayerCheck, OwnerCheck and PrizeCheck then ran one at a time, each
  exiting 0. In the run of all 38, KeptCheck also failed section 103's Partners line ("the same picture as one
  built new, to the pixel", pixels that differ 0 4 0 0 0). That line is pixel- and path-sensitive (its
  comment; WG1-R's floor change moved it by 3) and cannot be reached from the fund's cap sale. Rerun alone
  it passed (0 0 0 0 0), with only the four known reds, so it has no known-reds row.

## The process

This batch makes no process change, so it has no line count.
