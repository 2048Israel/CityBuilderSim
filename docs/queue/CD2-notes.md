# CD2 - the lineup and the auction: notes

*0.8.15, 2026-10-21, overnight (Jerus's chain: CL2, CD1, CL3, CD2, FX5). The
batch is `docs/studies/spec-city-debt.md` §8's CD2 row: §2.1-2.3 and 2.7
(bills, bonds in reopened lines, the amortizing bond, dollar paper), §4 (the
auction and the sales nobody presses), its screen, Borrow as an auction (§7
board 4, D31), the households at the auction's yield (D32), and first the
study's model run again with the firms (D33). Planned while CD1 was built;
built on CD1's branch (unshipped, waiting for Jerus on H1 and A2) merged with
main at 9f1b328 (CL2, 0.8.13), so it is the version after CD1's 0.8.14.*

**{{STATE}}**

## 1. What it does

- **The study first (D33).** `debt-model.py` runs the auction with the
  households at the auction's yield out of their savings (D32) and the firms
  by `OutwardInvestment`'s rule; the spec's §4.2-4.3 and the boards follow.
  The tight city's D$300M 10-year now stops at 3.882% (a 28 bp tail; 4.50%
  without the firms) where it stopped at 4.602% capped and 8.632% filled; a
  tail over a point now takes the bank's room gone and an issue past what the
  households and the firms take near fair (D$600M there: short at the cap,
  4.997% filled).
- **The lineup (§2).** Bills at 1, 3, 6 and 12 months; bonds at 2, 5, 10, 20,
  30 and 50 years, the coupon the stop rounded down to an eighth (D2), sold
  into lines that reopen while they have nine tenths of their term left (D6,
  D14; `Debt.reopen`); the amortizing bond at 5 to 30 years in fives on one
  coupon, the stop; dollar paper bills and bonds only. The term loan's
  grossed-up face, the serial's final-maturity coupon, the 40-year term, the
  dollar serial and the underwriter's 0.75% on the city's paper are gone
  (D21; the businesses' bonds keep theirs).
- **The auction (§4; `Auction`, pure).** The households first, by their rule
  at the auction's yield out of savings past the cushion (D32, no half-issue
  cap); the firms by `OutwardInvestment`'s rule (D33); the world by
  `CapitalFlows`' rule (D7); the bank its room at fair. One clearing yield
  (D5) by bisection; the tail, the cover; capped at fair + one point by
  default, at a yield, or Fill it (D20, D27); a capped sale that falls short
  sells what was bid. **Priced on the curve**: a sale is the curve's value of
  its flows at the tail as a spread (`YieldCurve.pvAtSpread`), so a sale at
  fair is worth what a buyback pays and the round trip loses only the fee.
  A short sale under the minimum issue is no sale.
- **The quote is the deal.** The quote functions keep their names, run the
  auction on `Game.auctionBidders()` with the issue priced in, and carry the
  result; the ForCash solver ends on the smallest face that covers. The
  settle pays each bidder's allotment.
- **The holders.** The firms hold what they buy on a paper book per sector
  (`OutwardInvestment`), paid coupons and principal pro rata, sold to the
  bank's desk when the spread goes, when a sector borrows, or on demand after
  its dollars. The world holds what it buys on `CapitalFlows`' paper book,
  outside the bank's room (★2): bought from abroad (a financial inflow), its
  coupons an income debit, its principal and its departures financial
  outflows, sold to the bank's desk as the hot money leaves or runs. The
  central bank's dial reaches the firms and the world last.
- **The sales nobody presses (§4.5).** The rollover and automatic building
  each sell at their own cap, saved (D28). A short rollover pays the rest of
  the maturity from the cash and then the advances, and the inbox names it
  (D29). A short automatic-building sale keeps its ask open, offered again
  each month, the order placed when what was raised and the spare cash cover
  it, withdrawn (the money stays) if the order drops out (D30). The time
  skip's report lists every automatic sale.
- **The tally (D33).** Every booked sale counted and saved (`AuctionTally`);
  LongPlaytest prints "Auctions: ...".
- **The screens (D31).** Borrow › At home as board 4: bills, bonds and
  amortizing as tiles, the terms as columns of fair yield (a bond term says
  which line it reopens), the cap as three chips, the four bids against yield
  drawn, the result line by line, the red card for a sale that would not
  cover. {{SLICE9}}
- **The save.** `SAVE_FORMAT` 42 and `OLDEST_LOADABLE_FORMAT` 42: an old
  term loan's coupon would read wrongly (the standing rule; saves never matter
  before publication).

## 2. ★ calls (for Jerus)

1. **★1 The firms bid at `OUT_SPEED` of the gap**, their rule as written: a
   firm moves a twenty-fifth of the way to its target a month, at an auction
   as anywhere. Bidding the whole gap would make them the largest bidder in a
   city with idle cash.
2. **★2 The world holds the paper it buys**, outside the bank's room (D7
   needs a holder there), against `CapitalFlows`' note that hot money is one
   channel funding the bank. Its stock rule still ignores the paper it holds
   (§6).
3. **★3 The class names stay**: `LongTermBond` is now any bond of 2 to 50
   years, `MediumTermBond` the amortizing bond, `ShortTermTBill` the bill; the
   type keys ("Term", "Serial", "Note") too. A rename is mechanical.
4. **★4 The bank's room** (D33's open question): over the real runs no tail
   passed a point (§3), so the 20% weight on city paper is not yet biting.
5. **★5 The study's firms hold D$1.5B idle**, the study's figure; the tails
   hold without them.
6. **★6 The bank's room is its risk capital, and a bank not yet capitalised
   takes the rest.** Measured on `NewGameCheck`'s city: `Bank.capacity()`
   and even `capitalLimit()` (the leverage ratio binds while a young bank's
   own cash is its exposure) left it 173k of room against a D$70M first bond
   until month 13, and at the founding the bank has no equity. So the bank
   bids equity / `CAPITAL_RATIO` less its weighted book, over `RISK_CITY`
   (D20: "its room is its capital"), funded at the window where its deposits
   fall short, as the residual buyer always was; a bank with no equity yet
   takes the rest at fair, as before CD2; a bank in resolution bids nothing.
   The alternative is the leverage ratio applied here too, and young cities
   borrowing only from their households for a year.
7. **★7 Two harness statements restated to the new mechanism**, premises
   kept: `NewGameCheck` 11's "the cash within a granule of the ask" is now
   minimality (the smallest face that covers; D2's rounded coupon makes
   proceeds a sawtooth in face) and names a short sale as one;
   `HoldersCheck` 1 and 9 read the auction's allotment for the households'
   share.

## 3. The checks, the trace and the ensemble

{{CHECKS}}

## 4. Who wrote what, and what review changed

Every slice by Sonnet 5.5 at xhigh, reviewed by Claude (Opus 5.5), which
wrote the study rerun, the briefs, the merges, the record and these notes.

| slice | what | review changed |
|---|---|---|
| 1 | the study rerun (D33) | Claude's own |
| 2 | `Auction`, `AuctionCheck` 1-5 | nothing; five mutations each red |
| 3 | the lineup and the quotes on the auction, the settle by allotment, the save | sent back twice: the bank's room was `capacity()` (a young city sold nothing) and the sale was priced at one flat yield against a buyback on the curve (a 2 bp pump); both decided by Claude (★6, the curve pricing); the ForCash solver made minimal |
| 4 | the firms' paper book | merged onto the ★6 rule; no MoneyAudit line, both ends are pools |
| 5 | the world's paper book | the central bank's dial reaches the world last (added by the slice; kept) |
| 6 | the caps, D29, D30, the time skip | its fixtures re-tuned after the merge with 7's tiny-sale rule; a tiny sale recorded as nothing sold |
| 7 | the tally, the tiny sale, `AuctionCheck` 7, 8, 11 | the tiny-sale rule kept to short sales |
| 8 | Borrow as an auction | the bank's room bar on the ★6 rule at the merge |
| 9 | the caps and the open asks on the screens | {{S9REVIEW}} |

## 5. The night's conditions

The PC ran out of memory at 02:00 (1 GB free of 31.6, 21 Claude processes):
one suite died with CreateProcess error 1450 and was rerun once it ran
alone; the orchestrator archived the idle sessions. From then on one heavy
run at a time beside CL3, one Sonnet subagent at a time, and the ensemble at
two runners.

## 6. Not built, and left

- **The world's stock rule ignores its paper**: `CapitalFlows.takeMonth()`
  aims the hot money at its full target while the auction counts the stock
  and the paper together, so the world's whole exposure can pass its rule's
  target by what it holds in paper. Rare in play (the world bought nothing in
  the real runs); its fix belongs with ★2.
- The discount the world earns rides inside its principal on the financial
  account, not part of it as income.
- The lender's and the investor's affordability still count a sector's
  dollars abroad and not its city paper (`Game` ~4622, 5034, 5143).
- A premium at a reopening is not amortised (the discount clamps at
  nothing): CD3's effective interest replaces it.
- {{LEFT}}
- **CD2-C** (Pending, Jerus by eye): the Borrow page in a played city (a
  short sale's red card, Fill it, a reopening's column), the rollover's and
  automatic building's cap chips and the open asks, and the seven calls.
