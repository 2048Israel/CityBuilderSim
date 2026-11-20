# FS13 notes: the fund, the treasury and the trading city cause their conditions

Batch FS13, built 2026-11-20 by Claude (Opus, headless) on `batch/FS13` from
origin/main fa889c423 (0.9.168); origin/main b2f27a7bd (0.9.171: CPB2,
RN1-H-M, MC1) is merged in. Brief: `docs/studies/flaky-suite.md` 6.12 and its
table (`flaky-suite/by-section.md`, the FS13 rows), PK1's FundCheck half, and
the two FS13 rows of `docs/notes/known-reds.txt`.

The batch is harness-only. The diff is `TreasuryCheck`, `ForeignDebtCheck`,
`RestructureCheck`, `BuybackCheck`, `CurveCheck`, `FundCheck`,
`FundLedgerCheck`, two entries of `Observation.HARNESSES` (CurveCheck and
FundCheck; FS10's HomeRailCheck entry merged beside them) and the generated
maps. No model class is touched, so the model and the trace do not move
(`moves_playtest=no`, no ensemble owed).

## The known reds

Both FS13 rows go green and leave the file. Neither is a model fault.

| row | harness, line | cause | change |
|---|---|---|---|
| 80 (was 82) | FundCheck 9b: ...and the desk's bid meets it at the step: some of it sold, into the fund's cash | the desk posts its bid first in the step (`Exchange.postDesk()`), sized by its capital (`deskCanBuy`); the leavers' and the households' sales crossed it before the fund's ask was posted, last (`postFund()`): the desk's bid for 28,820 shares of Construction, read before the step, rested on no book after it, and the step sold 0.0000 of both holdings | `giveTheDeskRoom()`: the bank's cash raised (`Bank.setCash`, as `takeEquityTo()` lowers it) until the desk's bid is every share of each company that is not its own, the model's own limit, and once more; a hard fixture says so. The step now sells 0.7185 of each holding |
| 110 (was 114) | CurveCheck 8: the steep Note bought straight back leaves the city no richer | one ulp: +4.547e-13 on a cash of 3,580.98, a bare `<= 0` on summed dollars | `net <= MoneyAudit.tolerance(0, before)`, the label kept |

## The rows, by harness

**TreasuryCheck** (roll-a to roll-d; sections 1-6 and 9e KEEP).
- roll-a read a founded city played to its rollover months. The three twins
  now take the year at `TaxPolicy.MAX_INCOME_TAX`, so the year's surplus is
  the tax's (`Game.surplusOverLastYear()` reads the saved series and has no
  setter). The notes are sized at `NOTE_IN_SURPLUSES` (3) times the surplus at
  issue, and a netting already used is booked by `Rollover.restoreLedger`.
  The law calls `Rollover.netting(...)`: month 15, 802.39k falls due, surplus
  561.07k, nets 401.19k.
- roll-b's due months are derived from the pieces issued (`noteDueMonths`,
  `serialDueMonths`) and `fellDueIn` must be exactly {2, 4, 5, 6, 8, 10, 11}.
  A new fixture checks that the roads' quote exceeds the surplus, and
  `setTrade(0, cover)` shuts the window by the share of exports.
- roll-c/roll-d no longer press to month 60: `DebtManager.setTrade` sets
  next year's service share at `OPEN_SHARE` (.9) and `SHUT_SHARE` (1.1) of
  `WINDOW_SHUT_SERVICE_SHARE`, both sides of the model's constant, read at
  the due month (`setTrade` holds until the month's step 156, after
  `rollMaturities` at step 8).

**ForeignDebtCheck** (2, 8, 10) and **RestructureCheck** (the dollar pump).
- The new helper `earnsAbroad(dm, owed, stress)` sets the exports so that
  `solvencyStressAt` is the stress share given.
- Section 2 sets the dial `DIAL_OVER_THE_WORLD` (.01) over the world's rate
  by `setPolicyRate`; there is no borrowing loop. Section 8's premium is
  sized from the bond (`BOND_STRESS_SHARE` 1/8). A new fixture checks
  0 < premium < max/2, and the four-times-the-exports twin is the other side.
- Section 10 plays no month: the big sale's stress share is
  `BIG_SALE_STRESS_SHARE` (1/2), and the dial is over the small sale's quote.
- RestructureCheck's dollar city has `DOLLAR_STRESS_SHARE` (1/16) and holds
  the dial by `takeTheDial` over the world's quote.

**BuybackCheck** (3-7, 9, 10; 8 KEEP).
- `sellToAllHolders()` sets the savers' rate so the households take
  `HOUSEHOLDS_SHARE` (.25), and the bank's room to `BANK_SHARE` (.55) by
  `setWeightedBook`, AuctionCheck's bisection. The face is sized from
  `Auction.worldAt` + `firmsAt` at fair + `MAX_TAIL`/2, so the sale settles
  with the three holders. Each holds at least `A_PART` (.02), a new fixture
  in 3, 5a, 9 and 10.
- Section 4 is one play after the dial, with a check that the holding is
  within it; the guard loop of 24 is gone. Section 6 is a founded scratch
  city with `earnsAbroad`, no trading city.
- Section 7 refuses at `Math.nextDown` of the ladder's prices, with the cash
  at half the quote. The 36-month migration loop is gone.

**CurveCheck** (5, 7, 8, 10, 12; 9 and 11 KEEP).
- `setInflation` re-strikes `PriceIndex`'s ring through its save array.
  `setExpectation` sets `Expectations`' smoothed and expected values the same
  way. Fixtures check both offsets, and `SHOCK` is 4 x `Expectations.TOLERANCE`.
- Section 5 shocks before each press, and both rules stay inside
  `MIN/MAX_POLICY_RATE`.
- Section 7's steep city sets the target inflation and expectation, then
  strikes the curve. Its control is the dial at `expectedToSettleAt`.
- Section 8 is the tolerance row above, plus `earnsAbroad` and a fixture that
  the window is open.
- Section 10's Observation is now a hard fixture: "the end of the month
  moved the expectation", caused by `setExpectation` before one press.
- Section 12 strikes the expectation at `SHOCKED_AT` (60) and checks that it
  moved. No Observation is left; CurveCheck leaves `Observation.HARNESSES`.

**FundCheck** (PK1's base; 1, 3, 5, 6, 8, 8b, 9b, 9e; 11 KEEP).
- **The base:** the bank is built at the founding
  (`buildStack(Commercial Bank, 1, false)`, through construction like the
  town's other buildings; a fixture checks SUCCESS). The town plays
  `FOUNDING_MONTHS` = 72, a named stage of the founding (WG2's 73rd month is
  gone). The heading is now "a city with a bank built at its founding, owned
  by somebody other than the city"; `by-section.md:243` quotes the old one.
- **Sections 1 and 9b:** the rescue book is counted in the unit the month
  leaves (`getSplit`), not observed unsplit. Section 1's Observation is gone.
- **Section 3:** the later branch costs nothing (cash and materials 0), so
  the fees cover it by construction, with a new fixture on
  `laterBranchCost()`.
- **Section 5:** `handSavingsToTake` gives the households the cushion plus
  what the issue asks, through `SHARE_OF_EXCESS`.
- **Section 6:** `startThatLeavesUnder` (★e).
- **Section 8b:** the bid and ask straddle fair on the side away from the
  trade.
- **Section 8:** `emptyTheDesk()` hands the desk's inventory to the world, so
  the rule's bid rests. `handTheHouseholds()` gives them 15% of the largest
  company. Both Observations are now hard fixtures.
- **Section 9b:** the desk's room (the row above), and the `rule` twin's bank
  at `Payout.REBUILDING` (`takeEquityToOwn`) so it pays the rescue book no
  dividend. At 60 a D$35.8k dividend had turned the band's sale into a
  purchase (fr -0.0377).
- **Section 9e:** the world bids fair for the fund's limit asks each month
  (★g, RB3).
- No Observation is left; FundCheck leaves `Observation.HARNESSES`.

**FundLedgerCheck** (base; 1, 2, 4, 8; 5 and 6/7 KEEP).
- **The base:** the bank is built at the founding, `FOUNDING_MONTHS` = 72,
  and causes of death are held off as FundCheck's are. Without the hold, the
  84-month probe had 16 people and every cell under
  `HouseholdBalance.EMPTY_CELL`. The heading now says the bank is built at
  the founding; `by-section.md:250` quotes the old one.
- **`copy()`:** hands the households `HOUSEHOLD_HOLDING` (2 x
  `OWNERSHIP_LIMIT`) of the traded company and of the bank. The founded
  bank's shares stood 100% with the world.
- **`heldCompany`:** is the listed non-bank company with the largest market
  cap.
- **Section 4:** its copy of the power-of-ten formula is gone. The split is
  checked from both sides against `SPLIT_AT`.
- **Section 8:** plays on `handCopy()`, which empties the desk, and its two
  orders are sized from the room less half the rule's bid. Their margin had
  been 10.2% against the 10% cap.

## The calls (★)

- **★a. FundCheck and FundLedgerCheck keep their town.** The table suggested
  `ExchangeCheck.bankWith` or a village. Sections 5, 6/7 (FundLedger) and 9e
  and 11 (Fund) are KEEP identities on a played state, so the town stays.
  The bank is built by order, the months are a named constant, every
  section causes what it reads, and the proof is the probes: both harnesses
  are green at `FOUNDING_MONTHS` 60, 72 and 84. Recommend: keep.
- **★b. The tax lever for roll-a's surplus.** The surplus series has no
  setter, so the year is taxed at the model's maximum and the notes are
  sized from what that year made.
- **★c. `setTrade` wherever the window or the premium matters.** The table
  named `ForeignAccounts.takeMonth` and CapitalFlows. `setTrade` is the
  input `countryPremiumAt` reads, and it holds through the month's
  rollovers.
- **★d. Buyback's holders by the auction, not by hand.** The savers' rate,
  the bank's room and the sale's size are set, so the line settles through
  the model's auction with the three holders. Setting the holders on a hand
  piece would skip the settle that sections 3-10 assert.
- **★e. FundCheck 6 finds its start on reloads.** The month's move depends
  on where the bank starts: a bank started at half its minimum lost $55.8k,
  not the $71.8k the full bank lost, and ended over its minimum at 60. So
  the start is found on up to eight reloads of the same month, each moved
  by what the month left. No month is picked, and a bank no start can leave
  there fails the fixture. Recommend: keep. It is the closest thing here to
  a bounded play-until.
- **★f. FundCheck 9b's room is grown to the model's limit.**
  `giveTheDeskRoom` doubles the bank's equity until `deskCanBuy` covers
  every share not the desk's (at most 60 times, failing as a fixture).
- **★g. FundCheck 9e's fills are a premise.** The fixed point
  L x s / (s - b) assumes the cap sale sells its tenth a month. At 60 and 84
  the fund's limit ask on Construction rested unfilled: the last trade was
  under fair (0.01039 against 0.01050), the buyback took the cheaper asks
  first, and the holding reached 11.17% (m109; 11.14% at 84) against
  10.92%. The world now bids fair for those asks each month
  (`tradeForCheck`, as 9b already did), and the fixed-point law is asserted
  on that premise. The model question is queued as RB3 (below). The cap
  and idle counts stay hard checks, since they are caused by the placement
  at the limit and the pay-in past the rooms; the table's "wrap in
  Observation or drop" is not taken, per ★3.
- **★h. FundLedgerCheck's KEEP sections start from the caused copy.** Their
  bodies are untouched. Some still read the path and are green at all three
  probes: section 5's `heldCompany`/`holder` and the bank seller in
  `olderSaveWithARescueBook`, and 6/7's "the rule bought something".
  `companyAtTheCap` (section 8) still searches the companies, since a
  company's buyback has no setter, but it is a hard fixture now, not a
  silent `return c0`.
- **★i. Observation.java's two entries** are outside the harness files.
  ObservationCheck requires the change (★3). FS10's HomeRailCheck removal
  merged beside them.

## Left open

- **RB3, queued:** the fund's over-limit ask at fair rests when the company
  trades under fair. `Exchange.postFundOverLimit()`'s comment (about
  L1605-1620) says that at fair value the fund's ask is under the desk's and
  the households'. That fails once the last trade is under fair, so a buyback
  can outrun the tenth a month (the figures in ★g). A design question for
  its builder (the orchestrator, 2026-11-20): price the
  limit sale at the market's ask, or keep it at fair and let the fixed point
  bend.
- **ForeignDebtCheck section 7** is not a row of this sweep. Its bound (5%
  of the bond's principal) is path-sensitive: a dial change crossed it once
  while the slice worked.

## The fault lines

Each was temporary, then reverted and run green again.

- **TreasuryCheck:** `SHUT_SHARE = OPEN_SHARE` gave 8 FAILs, among them
  "fixture: the window is shut as it falls due, by that share of exports and
  no other reason".
- **ForeignDebtCheck:** the dial margin at -.01 and the quarter case set
  equal gave 6 FAILs, among them "...so the world's money starts cheaper
  than the city's own", "fixture: a city earning a quarter as much abroad is
  charged more for it" and "a month's tax, quoted cheaper abroad than the
  share of home, is borrowed abroad".
- **RestructureCheck:** `setTrade(0, ...)` failed "fixture: the dollar city
  earns abroad, and the world lends it for less than it lends itself".
- **BuybackCheck:** the world's stock at 1e12 (no appetite) failed
  "fixture: it is settled, and the households, the bank and the world each
  hold part of it" and the `A_PART` line.
- **CurveCheck:** `STEEP_GAP = 0` failed "fixture: its curve is steep - ...".
  A pump of .01 on `net` failed "the steep Note bought straight back leaves
  the city no richer (raised 525.82, net 0.01)". The tolerance still
  catches a cent.
- **FundCheck:** the old `/1000` branch cost failed "fixture: the town's
  fees would cover a second branch" (and the dilution fixtures).
  `giveTheDeskRoom` raising nothing failed "...and the desk's bid meets it
  at the step: some of it sold, into the fund's cash" (0.0000 and 0.0000).
- **FundLedgerCheck:** section 8's orders sized at plus the rule's bid gave
  3 FAILs, among them "...and both orders are posted whole".

## The pick, the review and the checks

**The pick** (`sh tools/pick.sh origin/main HEAD`): 39 of 232 harnesses for
22 files changed since b2f27a7b. Most are picked because `Observation.java`
changed. The pick is not ALL, so there is no dry run. The review line is
"not needed - no money, saves or threading in the diff".

**The base**, fa889c423, each harness run alone: Restructure, Curve,
Treasury, ForeignDebt, Buyback and FundLedger exit 0 (59-96 s). FundCheck
fails only row 82 (135 s).

**The probes:** FundCheck and FundLedgerCheck at `FOUNDING_MONTHS` 60, 84
and 72 are all green (logs in `target/`, not committed).

**`./batch-check.sh --fast`** on the merged tree: compile OK, maps
regenerated (committed), Stale 0 firm findings, the tree's 4 changed files
all generated docs.

**The branch** with 0.9.171 merged, each harness run alone:
ObservationCheck 28 s, TreasuryCheck 40 s (208 OK), BuybackCheck 27 s (400),
RestructureCheck 28 s (53), ForeignDebtCheck 45 s (110), CurveCheck 30 s
(230), FundCheck 39 s (308; 9e's 240 months in 2.4 s, the cap sale in 239,
the idle cash in 102) and FundLedgerCheck 27 s (137): every one exit 0, no
FAIL line.

**The go** (the orchestrator, 2026-11-20): harness-only, no review, pick 39,
so no dry run; RB3 is right as a design question for its builder, and the
desk's bid caused by a buyer is the sweep's method. FS16 also edits
`Observation.HARNESSES`: whoever gates second merges the one line by hand.
