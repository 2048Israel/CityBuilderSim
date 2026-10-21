# CD2 - the lineup and the auction: notes

*0.8.15, 2026-10-21, overnight (Jerus's chain: CL2, CD1, CL3, CD2, FX5). The
batch is `docs/studies/spec-city-debt.md` §8's CD2 row: §2.1-2.3 and 2.7
(bills, bonds in reopened lines, the amortizing bond, dollar paper), §4 (the
auction and the sales nobody presses), its screen, Borrow as an auction (§7
board 4, D31), the households at the auction's yield (D32), and first the
study's model run again with the firms (D33). Planned while CD1 was built;
built on CD1's branch (unshipped, waiting for Jerus on H1 and A2) merged with
main at 9f1b328 (CL2, 0.8.13), so it is the version after CD1's 0.8.14.
CL3 shipped to main as 0.8.14 while CD2 was built: when CD1 rebases it
becomes 0.8.15 and CD2 0.8.16, renumbered at that merge (GameVersion, the
changelog's heading, `docs/ensemble/`, CD2-C's row).*

**Work in progress, not ready to ship**: it carries CD1's unshipped commits, and `--timing` was not run (07:45 came first). Built, checked and recorded otherwise: see §3.

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
  cover. The rollover's card and automatic building's panel carry the same cap chips (one `CapChips` piece for all four places); the panel lists the open asks with Withdraw; the build screen's funding card is the auction's preview at the builder's cap and a short one offers a higher cap, Fill it, or not borrowing.
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

**AuctionCheck** (new, 11 sections, 350 assertions, about 20 s): each
bidder's rule against its own constants; one clearing yield; cover and tail;
capped and filled; the coupon and the price; priced on the curve; the lineup
in a city (a line opens, reopens, and past nine tenths does not); the quote
is the deal and the settle pays the allotment; the rollover's own cap and its
short sale paid in full (D28, D29); the builder's own cap and the open ask
(D28, D30); and a real run (D33). Five mutations of `Auction` each went red.
`HoldersCheck` 10 and 11 (new) hold the firms' and the world's books: the
settle, coupons, principal, the desk, a run, save and load, the central bank
reaching them last, and each book equal to its holder's share of the paper
at every step.

**The suite** (`Batch check.bat`, the baseline on the merge of CD1 and main):
125 run, 124 passed, the one red StaleCheck's harness count for the new
AuctionCheck, fixed. Main's red, ScaleCheck's "...and it builds" (SC1),
passes here as it did on CD1. On the final model (9e828b81's model, before
the words and the maps): **126 harnesses run in 792 s, all passed**, Stale 0
firm findings. The final check with the maps (`Batch check.bat --maps`, at 89610f6c): **126 harnesses, all passed**, the maps regenerated, Stale 0 firm findings - in 14,046 s, the PC loaded past its memory beside CL3 and the night's other work. **`--timing` not run**: it is the first thing left.

**D33's count.** No tail over a point anywhere it was counted: AuctionCheck
11's 1,200 months of the automatic builder's city booked 82 sales, D$40.0B
of D$40.0B asked, every one at fair (largest tail 0 bp, none short); the
default playtest books one domestic sale in 4,000 months (D$2.77B, at fair)
and Rotterdam none (both borrow abroad). The bank's room (★4) is not where a
played city's sales stall: they clear at fair. The tail is the player's,
when Borrow asks for more than the households, the firms and the bank's
capital will take near fair.

**The trace.** Both playtests are CD1's to the month through their m769 /
m765 checkpoints, then move with the playtest's own borrowing: the default
city's one domestic sale (a 20-year bond at the market's coupon where CD1
booked a term loan's grossed-up face at a third of it) and both cities'
dollar bonds at m1092/m1088, m2177/m2173 and m3298/m3294 (now a bond at the
world's coupon, near par, where they were term loans). Every month passed
the audit and every reload matched, on both.

| | CD1 (0.8.14) | CD2 |
|---|---|---|
| default: final population | 619,699 | 573,349 |
| default: GDP a month at the end | $7.6T | $3.7T |
| default: auctions | - | 1 sale, D$2.77B, at fair |
| Rotterdam: final population | 834,678 | 761,080 |
| Rotterdam: GDP a month at the end | $12.8T | $11.3T |
| Rotterdam: auctions | - | none |

A single run swings a long way with its path (FR2-notes §4); the ensemble is
the measure.

**The ensemble** (label cd2, 2 runners at once, 80 of 80 runs in 13.9
minutes, against CD1's `docs/ensemble/0.8.13.md`, to become 0.8.14.md when
CD1 rebases): **23/26** (one vacuous) against CD1's 22/26; stored as
`docs/ensemble/0.8.15.md`. **A2 passes** again (the longest spell over 10%
from m240 a median 7 months [1 to 10], against CD1's 7 [3 to 12]). **S1**
stays red, its median nearer the line (1.85% over the twin against 2.36%)
but one seed's spell over 10% 17 months against 12. **R3** stays red, 3
seeds over a point against 4. **H1** stays red by CD1's design (★9 there: a
dial held at 0% no longer buys cheap long money); H2 vacuous with it. AUTO2's
final population is 217,480 against 235,932 (92%, T2 passes); every run's
audit closed and no run had a LongPlaytest finding.

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
| 9 | the caps and the open asks on the screens | nothing; the rollover's and the Bank tab's words for the new lineup by Claude after it |

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
- The build screen offers no "keep the ask open" for a player's own order (`AutoBuilder` opens asks for its own orders only).
- The decision log still says "note" for a bill (ChartCheck reads its text).
- **CD2-C** (Pending, Jerus by eye): the Borrow page in a played city (a
  short sale's red card, Fill it, a reopening's column), the rollover's and
  automatic building's cap chips and the open asks, and the seven calls.
