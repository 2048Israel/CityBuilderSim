# FS14 notes: the Exchange and the bond desk cause their conditions

Batch FS14, built 2026-11-20 by Claude (Opus, headless) on `batch/FS14` from
origin/main b2f27a7bd (0.9.171). Origin/main 1d8a1f243 (FS10, FS13, FS16,
GY1, 0.9.172) and ac224abf3 (FS17) are merged in. The brief is
`docs/studies/flaky-suite.md` 6.13 and its table
(`flaky-suite/by-section.md`, the 33 FS14 rows), with SX2 and the twelve FS14
rows of `docs/notes/known-reds.txt`.

The batch is harness-only. The diff is the eight harnesses (FloatLink, Desk,
Call, Holders, Bond, Equity, Exchange, ShortSelling, each `...Check`), one
line of `Observation.java` (HoldersCheck, BondCheck, DeskCheck,
FloatLinkCheck and CallCheck leave `Observation.HARNESSES`, ★3) and the
generated maps. No model class is touched, so **the model and the trace do
not move** (`moves_playtest=no`), and no ensemble is owed. Against the merge
base: 730 lines added and 230 removed under `src/` (Holders 192/53, Desk
137/46, FloatLink 121/16, Bond 111/56, ShortSelling 73/3, Call 62/39, Equity
17/5, Exchange 12/7, Observation 5/5).

## 1. The known-reds rows: all twelve go green

None of them is a model fault, so nothing is queued and the ship file's
`## known-reds` is empty: the twelve rows leave the file at the merge.

| row | harness, line | cause | change |
|---|---|---|---|
| 33 | ShortSelling: the cells traded shares with the world in the real run | SX2: on LX1's path section 7's 1,000-month city never buys shares from the world | the trade is caused in month 61 (`TRADE_ABROAD_MONTH`) through `Game.settleProbeForTest`: the ordinary cell holding most of the company the cells hold most of sells the world a block at the spread's middle and buys half of it back, as section 9 causes it; the 2,000 months' audit, register and shorts-off identities stay played |
| 99, 100 | ShortSelling: the short stands, lent by the cells and the fund, charged a fee this month, and has been paid a dividend on; the state carries a short before the fields are taken out of it | on HS2's path the world's own orders cover Construction's short in the month the fixture reads | the world's orders (`Exchange.setShortSelling(false)`, which stops `postShorts()` alone and is not saved) are held off while the fixture waits up to 8 months for its fee and a dividend, then put back on; the lending fee and the dividend on shorts are charged regardless |
| 68-70 | Holders 2: the households were paid their share of the coupon; into their savings; the bank's share is on the interest bill | a month's paper sale moves the coupon's split by 6e-6 between the shares read before the month and the shares the coupon is paid on | the shares are read at the coupon itself (`settleProbeForTest`, after the households take their share) |
| 71-74 | Holders 9: while the bank has term paper to sell it is all the central bank buys; onto its book at face; its gain; in the middle the households still hold some | the bank's term paper went within 3 months, the households' with it | the households' purse is set by hand (`setSpare`), the tills emptied (`setSectorCash(key, 0)`) so no business bids, the bank held under two of the dial's steps, the savings lifted again after the settle so no household sells the desk a sliver; the bank-first rule is asserted from both sides month by month |
| 75 | Holders 13c: the bank has existing paid paper and discount exceeding the cancelled sale's | the bank held no paid paper with that discount | the first sale is sized by the model's own quote (`firstSaleSize`) until the bank carries `BILL_FLOORS_CARRIED` (2) bill floors of discount |
| 95 | Holders 10: every till could pay its share, so nothing was cut | Real Estate's till at -0.0057 with a weight of nothing | the tills of the sectors that bid are set to the whole allotment; the premise reads only the sectors that bid (a weight of nothing owes nothing) |

## 2. What each harness causes now

The table's CAUSE rows, by harness. The KEEP rows (★4) are untouched in their
laws: FloatLink 3, 4, 6, 9; Call 7 (its fallback sale caused as 5's); Desk 2's
identity (its maturing month caused); Bond 1b, 4, 8; Equity 9's and Exchange
10's 240-month identities; ShortSelling 7's 2,000 months.

- **HoldersCheck** (slices A, B; Sonnet). 1: the households' savings set by
  hand (`setSpare`: every cell's savings at least `SHARE_CUSHION_MONTHS +
  SPARE_MONTHS` months of disposable income, `SPARE_MONTHS` 12). 2: the coupon's
  shares read at the coupon. 5: the dial at .30 and the holders as 1 left
  them. 6: the households by their savings and a deposit rate of nothing, the
  central bank by its dial (.30), the bank the residual buyer; its three
  Observations are assertions with the same labels. 7: written from 6's city.
  8: a dollar piece the section sells itself. 9: above (row 71-74). 10: the
  bidding tills. 11: the world's appetite by its stock, as before, its take
  now a fixture. 12, 13a: each sale's split is the dials' (`splitTheSale`: the
  deposit rate that makes the households bid `HOUSEHOLDS_AT_FAIR` (.75) of the
  issue at fair; the central bank's dial at nothing). 13c: row 75. Thirteen
  Observations go.
- **BondCheck** (slice C; Sonnet). 7a: the issuer's till set five million
  short, so the bond is caused. 2: the policy rate and prime set together in
  month 24 (`NEUTRAL_RATE`, `bank.prime()`), not stepped until the bank's two
  prices agree. 7: the bank topped up to its capital target by hand (a no-op
  on today's path). 7c: the loans cleared, the bond market emptied, loans
  issued to Retail and Real Estate and three tills set, so each premise is
  caused; ★ the "no company that owes anything bids" law reads at the yield
  the non-owing company bids at, not .30, where it was vacuous. 5b: the
  default a fixture. 5c: W over the ceiling after the claim (a fixture), Z's
  till set short by its shortfall and a quarter of the room. 5d: the whole
  order's till four times the order over the bills. Seven Observations go.
- **FloatLinkCheck** (slice D; Sonnet). `readyCity` (2, and every section
  selling in its town): the bank's room is capital put in its till by hand
  (`handBankRoom`, `ROOM_HANDED_OVER` 1.001 of the face), the sale's size
  against its fixed fee is the households' savings lifted (`liftSpare`,
  `SPARE_OVER_LEAST_SALE` 4 of the least sale the fee needs), the fee's
  threshold shown from both sides against `EFFECTIVE_OFF_COUPON`; the 60-month
  wait for room is gone. 8: the due pieces dated (`dueNextMonth`), the bank
  handed the plan's room (`handRolloverRoom`). 5 and 7 were already caused on
  main (the index path handed by `setPriceIndexSource`, the dial taken by
  hand); the header now says so. Two Observations go.
- **DeskCheck** (slice E; Sonnet). 2: the bill dated to fall due at the next
  press, so "the month a bill matured" is caused (the label's "N months before
  it" now prints 1, where it printed 2). 3, 4: the dial raised `DIAL_RISE` (5
  × `A_POINT`) after the sale, its price read from both sides first
  (`dialLowersIt`: at the floor, where it is and raised; the floor side is no
  lower, because the curve's floor is the bank's cost of funds where that is
  over the dial). 6: the two sales at set months four apart, of the same ask,
  the households' spare lifted back to the first's and the bank handed its
  room for each; one `DIAL_RISE` where it was raised five points at a time
  until a measured margin. 7: the dial's moves named (`DIAL_UP` .07 in month
  9, `DIAL_DOWN` .02 in month 17) and the past asserted told apart by
  `PAST_TOLD_APART` (2 points) on the shortest tenor. Its exit now counts
  `FloatLinkCheck.fails` (a fixture failing inside a borrowed builder printed
  FAIL there and counted nowhere). Three Observations go.
- **CallCheck** (slice E; Sonnet). 4: one `saleOf()` sale with the bank
  handed its room for `ASKS_OF_ROOM` (4) asks, where the ask was halved until
  both quotes filled (`askTheMarketTakes`, gone; nothing else used it); the
  tails and the coupon gap now run hard. 5: the same sale, the central bank's
  dial set (`CB_DIAL` .3) and its holding within `CB_BUYS_WITHIN_MONTHS` (1),
  a fixture failing past it, not up to 24 months; the call month moved by hand
  as before. 7: its fallback sale caused as 5's. Four Observations go.
- **ShortSellingCheck** (Opus). 7: row 33. 6: rows 99-100.
- **EquityCheck** 9 (Opus): "every share of it is held" counts every holder
  (households, foreign, dealer, the city) against the shares plus the short,
  to 1e-9 of the shares (a desk sliver of 0.000153 shares otherwise read as a
  gap). The two found counts ("went to the market on their own", "paid their
  owners") are printed, not asserted: sections 1-7 cause both (the study's
  "wrap or drop"; wrapping would add an Observation).
- **ExchangeCheck** 10 (Opus): the four found counts (traded on the book,
  splits, the desk sold to a buyback, the quote not the last trade) are
  printed, not asserted (sections 2c, 7 and 8 cause them); the quote law reads
  `quoteMisread == 0` alone. `bankWith` and every borrowed helper are
  untouched (FS13's FundCheck builds its bank with it).

## 3. The law failing on a fixture that breaks it

Each a temporary edit, run to FAIL, reverted (the final files byte-identical
to the good copies):

- Holders: section 2's shares read before the month (FAIL 0.219683 against
  0.219669); 9's second `setSpare` removed ("no household left and none sold
  the bank's desk any paper" and the lines after it); 9's ask at three purses;
  6's deposit rate at 1.0; 1's savings zeroed (1, cascading into 4 and 5);
  10's tills at `allotCash` × 1e-9 (8 FAILs); 12's deposit rate at fair (6);
  13a with no policy cut (11); 13c's `BILL_FLOORS_CARRIED` at 0 (row 75's FAIL
  returns).
- Bond: 2's prime + .02; 7's bank at half its target with the top-up off; 7c's
  principal exclusion removed from `BondMarket.Bidders` (a temporary model
  edit, reverted by `git checkout`; FAILs "no company that owes anything
  bids"); 5c's claim at 0; 5b's issuer without loans; 5d's till at -1e9 and at
  100 × the bills; 7a with no short; 5c's lending expected at twice the short.
- FloatLink: the bank's handing a no-op; the savings lift a no-op; 2's margin
  shifted; 5's index flipped each way; 7's fair raised 5; 8 without
  `dueNextMonth`.
- Desk: 2 without `dueNextMonth`; 3 and 4 with `DIAL_RISE` 0 (4's premise
  fails); 6 without `liftSpare`; 7 with `DIAL_UP` .02. Call: 4's gap law at
  twice the spread, and a room demand of a million times with no handing (with
  the handing it passed); 5 with `CB_DIAL` 0.
- Not broken: Call 5's sale without the handing alone; Desk 2's months count;
  Desk 6-7's rollover and save fixtures; Holders' "reopened sale quoted".

## 4. Still played, and why

- "Nobody leaves while the paper is watched" (Holders 9,
  `FloatLinkCheck.playedCity`): `Migration` has no setter that pins
  departures and the batch adds no model code. Each is a bounded play (36
  months, `DECLINE_WAIT_MONTHS`) to a town with no tier in decline, and a hard
  fixture that FAILs when the bound passes.
- Desk 3's "under carrying value" holds at any dial on today's path (a sale
  struck at the auction's price is carried above where the curve prices it):
  the dial deepens the gap and is shown to lower the price, and the line is a
  fixture that FAILs if it stops holding.
- Bond 5d's partial order (the budget 11.7k against a 14.7k order: the shops'
  economics, a thin margin), 5c's choice of W and Z (read from the town), and
  1b, 4 and 8's "every class held some" (the played bond market): fixtures,
  left as the brief said.
- FloatLink 7 keeps its real sales; `readyCity`'s `SALE_OF_REVENUE` cap is the
  town's tax, which nothing here sets: a fixture if it falls under the fee's
  least sale.
- Holders 10's "stop is over what the world pays" still reads a played yield;
  it was not a row.

## 5. Calls

- ★ Holders 2 reads the coupon's shares at the coupon (the probe after the
  households take their share), not the shares before the month: the coupon
  is paid on what is held when it is paid.
- ★ ShortSelling 6 holds the world's own short orders off while it waits,
  rather than building the short by hand: the flag is not saved, the twin's
  default is on, and only `postShorts()` reads it.
- ★ SX2's trade is caused on the shorts-on run alone, in one month, by the
  probe inside the settle; the shorts-off twin stays as played, so the
  2,000 months' identities are the same months as before.
- ★ Equity 9 and Exchange 10 print their found counts rather than wrapping
  them in Observations (the study allowed either; wrapping would put the two
  harnesses back on `Observation.HARNESSES`).
- ★ Bond 7c's law moved from .30 to the yield the non-owing company bids at,
  so it can fail (shown above); at .30 no company bid at all.
- ★ State set between months by hand: household savings (`setSpare`,
  `liftSpare`, raised never lowered), the bank's cash (`handBankRoom`,
  `bank.setCash`), the tills (`setSectorCash`), the dials. No setter was
  added to the model.
- Holders and Bond gained a `fixture(label, ok)` that says whether it held, so
  a section stops on a failed premise (counted) instead of reading past it;
  Desk and Call have the same.
- Every label `known-reds.txt` or `gate.txt` names is unchanged.

## 6. What the review changed

The five slices were reviewed as strangers' diffs: model files (none; Bond's
temporary model edit was confirmed reverted), borrowed helpers (FloatLink's
`template`, `readyCity`, `preparedCity`, `playedCity`, `worldWantsPaper`,
`SALE_OF_REVENUE`, `HELD_OVER_THE_WORLD`; Call's `hiking`, `calm`,
`calmZero`, `ROW_ZERO`; Desk's `salesText`; Bond's `closes`, `line`;
Exchange's `bankWith` and the rest; ShortSelling's `lineOff`: signatures and
behaviour kept, but for `readyCity`, `preparedCity` and `playedCity`, which
now cause their premises and whose one borrower is DeskCheck, in this sweep),
measured numbers in laws, and premises moved to get green.
Slice B had dropped the Observations' early returns, so a failed fixture would
have read on into a null; the review added `fixture()` and the `if
(!fixture(...)) return;` form at the six sites. Slice E left a garbled comment
in Desk 6; the review rewrote it.

## 7. The checks

- Each slice ran its harness alone, green before review (Desk's slice ran
  FloatLink and Call beside it).
- On the tree merged with 1d8a1f243 (60d3f0c45): `./batch-check.sh --fast`
  exit 0 (compile, maps, Stale 0 firm, tree: only the generated docs). Then,
  one name per call, ObservationCheck, FloatLink, Desk, Call, Holders, Bond,
  Equity, Exchange and ShortSelling (each `...Check`): each "suite OK", exit 0,
  no FAIL, OBSERVED or SKIPPED line in any log. All twelve FS14 known-reds
  lines print OK. Section 7's caused run: 712 months played (as before), the
  cells sold to the world in 322 months and bought from it in 8; month 61's
  trade, a cell selling the world Construction and buying half back, is over
  the audit's cent each way.
- Merged again with origin/main at ac224abf3 (FS17): `Observation.HARNESSES`
  resolved by hand to main's list less the five; `git diff origin/main` on
  `Observation.java` is those lines alone. FS17 touches none of the eight
  harnesses or the helpers they borrow. On that tree (e49d6f630): `--fast`
  exit 0 and ObservationCheck exit 0.
- `sh tools/pick.sh origin/main HEAD`: 45 of 232, not ALL, so no dry run is
  owed; the review line: "not needed - no money, saves or threading in the
  diff". The 36 picks outside the sweep come in through `Observation.java` or
  a helper they borrow, and no diff hunk falls inside any borrowed helper
  (FloatLink's `template`, `worldWantsPaper`, `fixtureFloater`,
  `fixtureLinker`, `askOf`, `closes`, `dueNextMonth`, `piecesOf`, `newest`,
  `SALE_OF_REVENUE`, `HELD_OVER_THE_WORLD`; Call's `hiking`, `calm`,
  `calmZero`, `ROW_ZERO`; Desk's `salesText`; Bond's `closes`, `line`;
  Exchange's `bankWith`, `couple`, `equity`, `exchange`, `savers`, `step`,
  `bookOf`, `withRecord`, `months`; ShortSelling's `lineOff`), so I did not
  run them (the brief: `--fast` and the named harnesses alone); the gate runs
  the 45.
- The model and the trace do not move: `git diff --stat origin/main` over
  `src/` is the eight harnesses and `Observation.java`, so no ensemble is
  owed.

## 8. Commits

71d8aee17 (ShortSelling 6-7, Equity 9, Exchange 10), 73f5fd4c2 (Holders 1-9),
0a0a73796 (Holders 10-15), 0d910896e (Bond), a4dc8eeb5 (FloatLink),
3155e74de (Desk, Call, `Observation.HARNESSES`), 3c900275a and 83d9ac8da (the
merges), 60d3f0c45 and e49d6f630 (the generated indexes), then these notes and
the ship file.
