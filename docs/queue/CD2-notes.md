# CD2 - the lineup and the auction: notes (DRAFT, the plan)

*Draft of 2026-10-20, written while CD1 is built: nothing here is built yet.
The batch is `docs/studies/spec-city-debt.md` §8's CD2 row: §2.1-2.3 and 2.7
(bills, bonds in reopened lines, the amortizing bond, dollar paper), §4 (the
auction and the sales nobody presses), its screen, Borrow as an auction (§7
board 4, D31), the households at the auction's yield (D32), and first the
study's model run again with the firms (D33). It builds on CD1's curve: the
fair yield every auction starts from is CD1's par yield with the issue on the
books. Branch `batch/CD2`; it starts from main with CD1 in it when the
orchestrator says GO.*

## 1. The model rerun, first (D33)

Done in a scratch copy while waiting, so the figures below are the plan's
evidence; committed after GO with the study.

- **`debt-model.py`**: the households bid by their rule at the auction's yield,
  limited by their savings past the cushion and the issue, not half of it (D32,
  `households_at()`); the firms bid by `OutwardInvestment`'s rule with the
  city's paper beside the deposit and abroad (`firms_at()`): a share of their
  idle wealth, `APPETITE` (40) per unit of what the paper pays over the better
  of the deposit and the world, never past `MAX_SHARE` (.9), less what they
  hold, at `OUT_SPEED` (.04) of the gap, as their dollars move. The
  households stay first at the stop (D13's priority, D32's amount). The
  study's city gets `firm_idle` D$1.5B (a quarter of a year's output, the
  study's own figure, ★5) and `firm_paper` 0.
- **What it moves** (the 10-year, D$300M; the curve and the instruments
  unchanged to the digit):

| city | before: stop, tail, cover | after: stop, tail, cover | fills after (households, firms, world, bank) |
|---|---|---|---|
| calm | 3.602%, 0, 1.49x | 3.602%, 0, 1.87x | 126.1M, 1.3M, 0.3M, 172.3M |
| hiking | 6.049%, 0, 1.49x | 6.049%, 0, 1.87x | 93.0M, 1.5M, 1.7M, 203.9M |
| calm, the bank's room nearly gone, capped | 4.602%, 100 bp, 0.83x, D$247.8M sold | **3.882%, 28 bp, 1.21x, all sold** | 142.9M, 45.0M, 12.4M, 99.7M |
| the same, filled | 8.632%, 503 bp | 3.882%, 28 bp | as above |
| the boards' hiking reopening, room nearly gone, filled | 11.23%, 519 bp, D$208.6M raised | **6.356%, 32 bp, D$292.4M raised** | 111.4M, 44.5M, 44.4M, 99.8M |

  D32's interpolation (about 4.5%, a tail under a point) was right in sign;
  with the firms the tail is under a third of a point. The households carry
  most of it (at 3.88% they take 48% of the issue), so it holds at
  `firm_idle` 0 too (about 4.5%). The round trip stays a loss (no pump).
- **What changes in the study**: §4.2's firms line and §4.3's table and
  sentences; `book-data.py`'s auction board (the firms as a fourth bidder in
  the bids chart and the result, the red card now on an issue past every
  rule, since D$300M no longer fails); the boards rebuilt; the README's line.

## 2. The design, decided here

**The lineup** (§2). The class names stay (`ShortTermTBill`, `LongTermBond`,
`MediumTermBond`, ★3) and their javadocs say what each is now:
- *Bills*, 1, 3, 6, 12 months, at a discount, each its own line.
- *Bonds*, 2, 5, 10, 20, 30, 50 years (`LongTermBond.MATURITIES`), a coupon
  every month on the face and the face at the end; **the coupon the auction's
  stop rounded down to an eighth** (`Auction.COUPON_STEP`, D2), so a new line
  sells just under 100. **Lines reopen** (D6): a sale at a term goes into the
  domestic bond of that original term with at least nine tenths of it left
  (`Auction.BENCHMARK_LIFE` .9, D14), at its coupon and date, at the stop's
  price; past that a new line. `Debt.reopen(face, proceeds, yield)` adds the
  face, the principal, what the buyers owe at the settle, and the discount (or
  premium) to accrete; the issue yield blends by face (CD3 replaces it with
  effective interest). A line is named "3½% Jan 2211".
- *The amortizing bond*, 5 to 30 years in fives (`MediumTermBond`), slices as
  today; its one coupon is the stop itself, which prices the schedule at 100
  at one yield. Never reopened.
- *Dollar paper*, bills and bonds only, the world's curve as today, sold to
  the world, not at auction; the dollar serial goes.
- **What goes:** the term loan's pricing (`longBondCouponYield()`,
  `faceValueOfLongBond()` and its kin), the serial's final-maturity coupon, the
  40-year term, the underwriter's 0.75% on the city's paper (D21; the
  businesses' bonds keep theirs). The fixed cost and the minimum issue stay.

**The auction** (`Auction.java`, new, pure: no `Game`). One clearing yield
(D5). The caller hands it a snapshot of the bidders and the ask; it returns
the result, and nothing else computes one.
- *The bidders, each by its own rule in the game:* the households, first, by
  `HouseholdBalance`'s appetite at the auction's yield over deposits, out of
  savings past the cushion, at most the issue (D32: `MAX_HOUSEHOLD_PAPER_SHARE`
  goes); the firms by `OutwardInvestment`'s rule (D33, ★1); the world by
  `CapitalFlows`' rule, its target stock at the yield's excess return less what
  is here, at `ARRIVAL_SPEED` (D7); the bank its room (`Bank.capacity()` less
  its weighted book, over `RISK_CITY`) at fair plus its funding past the floor
  (§4.2). The central bank never bids; it rolls its own at issue outside the
  auction, as today (D8).
- *Clearing:* the stop is the lowest yield at or over fair where the bids
  cover the ask, found by bisection (every bid is non-decreasing in the
  yield); never over the cap. Everyone is filled at the stop, the households
  first and in full, the rest pro rata. **The tail** is the stop over fair;
  **the cover** what was bid at the cap, each bidder at most the ask, over it.
- *The cap* (D20, D27): fair + `MAX_TAIL` (one point) by default, a yield the
  player names, or **Fill it**, whose ceiling is the world's rate plus
  `CapitalFlows.MAX_SPREAD`; a capped auction that does not cover sells what
  was bid, and a filled one past every rule's ceiling too, and the result says
  so before the button.
- *The tally* (D33): every auction's stop, tail, and short, counted and
  saved; LongPlaytest prints the count of tails over a point and of short
  sales over the run.

**The quote is the deal** (`DebtQuote`'s rule, kept). `quoteTBill`,
`quoteLongBond`, `quoteMediumBond` and the `...ForCash` solvers keep their
names, run the auction on the city as it stands with the issue priced into the
credit spread, and return a `DebtQuote` carrying the `Auction.Result`; each
gains an overload taking the cap, and the old signature reads the caller's own
setting. `handle...()` books the record it is handed.

**The holders.** At the sale the treasury is paid the proceeds as today, and
the piece records each bidder's allotment; at the settle, the month after,
each pays its own: the households their allotment (no longer
`paperShareAt()` at the issue yield, `householdsTakeTheirShare()`), the firms
from their tills into a paper book on `OutwardInvestment` (`Debt`'s firm share,
coupons and principal pro rata, sold to the bank's desk when a sector needs
the money or the spread has gone, at `HOME_SPEED`), the world in dollars into
a paper book on `CapitalFlows` (★2: the world holds what it buys, part of its
stock; its coupons go abroad; a sudden stop sells its paper to the bank's
desk), and the bank the rest. Every flow declared to `MoneyAudit` on both
sides.

**The sales nobody presses** (§4.5). The rollover and automatic building
each get their own cap or Fill it (D28), saved, default fair + 1 point. A
short rollover pays the rest of the maturity from the cash, then the central
bank's advances, and the inbox names it (D29). A short automatic-building
sale keeps its ask open, offered again each month at its cap, the order placed
when what was raised plus the spare cash covers it, withdrawn (the inbox says
so) if the order drops out (D30). The time skip's report lists every automatic
sale. "Same structure" rolls a bill into a bill, a bond into its term's
benchmark line, an amortizing piece into a new amortizing line of its years.

**The screens** (D31). Borrow › At home as board 4: the three kinds as tiles,
the terms as columns of today's fair yield, the face, the cap as three chips
(fair + 1 point, a yield, Fill it), the bids against yield with the ask and
the cap drawn, the result line by line (stop and tail, cover, coupon and
price, each bidder's fill, raised), and the red card for a sale that would
not cover. The rollover's card and automatic building's panel get the cap
chips; the panel lists the open asks with Withdraw; the build screen's
funding card is the auction's preview for the 6-month bill or the 20-year
line, a short one offering a higher cap, Fill it, keep the ask open, or not
borrowing. Every figure through a public getter.

**The save.** The allotments, the paper books of the firms and the world, the
two caps, the open asks and the tally are new; an old save's pieces would
read wrongly (a term loan's coupon), so `SAVE_FORMAT` + 1 and
`OLDEST_LOADABLE_FORMAT` raised to it (the standing rule; saves never matter
before publication).

## 3. The slices, in the order they are built

Each slice a brief can pin down goes to a Sonnet 5.5 subagent at xhigh
(CLAUDE.md); Claude reviews each diff as a stranger's and commits.
Milestones push.

| # | slice | files | by |
|---|---|---|---|
| 0 | GO: fetch, merge main with CD1, build, the baseline check | - | Claude |
| 1 | the study rerun (§1), the spec's §4.2-4.3, the boards rebuilt | `docs/studies/` | Claude |
| 2 | `Auction` and `AuctionCheck` §1-5 on fixtures: each bidder's rule against its constants, single price, cover and tail, capped and filled, the coupon and the reopening price | `Auction.java`, `AuctionCheck.java`, `AllChecks` | Sonnet |
| 3 | the lineup and the quotes on the auction: bills, bonds and lines, amortizing, dollar paper, the term loan gone, fees, D32 in `HouseholdBalance`, the settle by allotment, the save bump; the existing harnesses on the new API, premises kept | `Game`, `Debt`, the three paper classes, `DebtQuote`, `DebtManager`, `HouseholdBalance`, `DataSave`, `GameVersion`, the harnesses | Sonnet |
| 4 | the firms' paper book | `OutwardInvestment`, `Debt`, `Game`'s holder split, `MoneyAudit`, `DataSave` | Sonnet |
| 5 | the world's paper book | `CapitalFlows`, `ForeignAccounts`, `Debt`, `Game`, `MoneyAudit`, `DataSave` | Sonnet |
| 6 | the sales nobody presses: the two caps, D29, D30's open asks, the time skip | `Rollover`, `AutoBuilder`, `Game`, `TimeSkipReport`, `DataSave` | Sonnet |
| 7 | `AuctionCheck` §6-11: the lineup, booking and the settle, the audit, the rollover short, the open ask, the real run's count | `AuctionCheck.java` | Sonnet |
| 8 | Borrow as an auction | `ui/FinancesScreen` | Sonnet |
| 9 | the cap chips and the open asks: the rollover card, the builder's panel, the build screen's funding card | `ui/FinancesScreen`, `ui/BuildScreen`, the builder's panel | Sonnet |
| 10 | the playtests re-baselined (default and Earth), the ensemble once against the newest `docs/ensemble/` baseline, the suite, Stale, the maps, the notes, the row out, CD2-C under Pending, the version, `--timing` last and alone | - | Claude |

2 and 3 can run side by side (3 codes against 2's signatures, fixed in both
briefs); 4 and 5 after 3; 6 after 3; 8 and 9 after 6.

## 4. The harnesses it moves

New: `AuctionCheck`. On the new API, premises unchanged: `CreditCheck`,
`RestructureCheck` (issue at the stop, buy back at fair: still no pump),
`NewGameCheck`, `FundCheck`, `LandCheck`, `MapCheck`, `BankCheck`,
`CentralBankCheck`, `HoldersCheck` (the households' paper still sums to
`householdPrincipal()`), `TreasuryCheck`, `ForeignDebtCheck`, `SaveFileCheck`,
`ReadPathCheck`, `AutoBuildCheck`, `ChartCheck`, `GdpCheck`,
`LandlordPortCheck`, `LongPlaytest`.

## 5. ★ calls (proposed, for Jerus)

1. **★1 The firms bid at `OUT_SPEED` of the gap**, their rule as written: a
   firm moves a twenty-fifth of the way to its target a month, at an auction
   as anywhere. Bidding the whole gap at once would make them the largest
   bidder in a city with idle cash, and most of an issue theirs.
2. **★2 The world holds the paper it buys** (D7 needs a holder outside the
   bank's room), against `CapitalFlows`' note that hot money is one channel
   funding the bank. In a sudden stop it sells its paper to the bank's desk.
3. **★3 The class names stay**: `LongTermBond` is now any bond of 2 to 50
   years, `MediumTermBond` the amortizing bond, `ShortTermTBill` the bill. A
   rename is mechanical and can ride a later batch with no change in play.
4. **★4 The bank's room** (D33's open question): if the real run's tails over
   a point are frequent, city paper's 20% weight against the bank's capital
   (`Bank.RISK_CITY`; a real bank holds its own government's at 0%) is next.
   The model says a tail over a point needs the bank's room gone and the
   households' savings thin together.
5. **★5 The study's firms hold D$1.5B idle**, the study's own figure; the
   tails above hold at nothing.
6. **★6 A city with no room at its bank** (no branch, so `Bank.capacity()`
   is 0, or a bank in resolution): the auction has no bank in it, and sells
   what the households, the firms and the world bid. Today the bank took the
   rest by construction, funded at the central bank's window. To be measured
   at GO on a fresh city and the playtest's first years before it is
   settled; if a young city cannot borrow for its first works on it, the
   proposal is that the window stands behind a branchless bank's bid at fair
   for a capped amount, said on the card.

## 6. The risk to tonight

CD2 is the largest of the six CD batches and starts after CL2 and CD1 ship.
If 07:45 comes first, the branch is pushed as work in progress at the last
green milestone, these notes say which slices are in, and the rest is the
next session's, in the order of §3.
