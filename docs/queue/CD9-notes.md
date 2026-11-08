# CD9 notes ({next}, 2026-11-08): the foreign debt's follow-ups

The brief is `docs/queue/CD9.md`: CD8's three pending rows (CD8-F, CD8-I, CD8-R) and CD7's EARNED residual (CD7-E).
Built on main at ec2aa95f (LF1 merged); main's c143b9a3 (MT1) merged in before the checks, and 8c2cb9af (RE2, 0.9.54)
after the first gate found KeptCheck out of heap on the merge (1d14cbf8). Two slices on Sonnet 5.5 at xhigh, each
reviewed here as a stranger's: the curves, the coupon floor and the issue cost (c0007e9c); the one revaluation and the
coupon in EARNED (16c2aaea, with ScaleCheck's register added in review); a fixture fix in DebtMoneysCheck from the first
run (54e3988d); the fund's fold cap and KeptCheck's sale order from the gate (49366c39).

## 1. What it built

- **Each money's curve floors at its own rate (CD8-F).** `YieldCurve` takes the curve's `cheapest` forward; the old
  constructor and `shortRate()` pass `DebtManager.MIN_RATE`, so every domestic curve is the same to the bit.
  `DebtManager.worldCurveAt(code, shortRate)` floors at `worldRate(code)`; its one-slot cache keys on the floor as
  well as the rate. `foreignFactor`, `marketValue`, `valueOfAPoint` and `Game.selfPricedWorldCurve` pass the paper's
  money. The struck coupon floors at a new dial, `DebtManager.MIN_FOREIGN_COUPON` = 0: a money under zero sells a 0%
  coupon over par at a yield under zero, and the chip, the quote and the strike agree. `Debt.getYieldToMaturity()`
  bisects on [`EFFECTIVE_RATE_FLOOR`, 0] for a price over the payments' total (it returned 0 there before), on
  [0, 5] as before otherwise, so a positive yield is the same to the bit.
- **The issue cost converted for the US$ too (CD8-I).** `Game.issueCostIn(face, code)` is
  `costOfIssuance(face) / localPer(code)` for all eight moneys.
- **One revaluation figure (CD8-R).** `ForeignAccounts`' `lastDebtRate`, `lastRevaluation` and `lifetimeRevaluation`
  are gone; `takeForeignDebt()` sets the US$ stock alone. Their state slots 14, 15 and 24 are written 0 and read by
  nobody (a slot never shifts). Every reader reads CD7's booked line instead: `NationalAccounts.getDebtExchangeGain()`
  for the month, the new `Game.debtExchangeSinceFounding()` (the "debtExchange" series summed) for the run - the
  playtest's "the currency did $x to that debt over the run", the Trade screen's two lines, the Finances debt card's
  chip, the Summary's figure, ForeignDebtCheck and CurrencyCheck, their premises unmoved.
- **The coupon abroad in EARNED (CD7-E).** `EconomyManager.foreignInterest`, set from the month's
  `foreignInterestPaidThisMonth` where the budget is struck, is in `getExpenses()` beside the domestic interest and
  not in `getInterestAccrued()` (the cash paid it already). A flow, so saved: `DataSave.cityForeignInterest`, read
  back in the load path; SaveFileCheck, ReadPathCheck and ScaleCheck's registers know it. **No format bump**: an older
  save lacks the key, and its first month on screen shows EARNED without the coupon, as before CD9.
- **The fund's closed lots fold lists the newest 200 (found at the gate).** On CD9 merged with main's 8c2cb9af
  (RE2's workers in), KeptCheck ran out of its 2 GB heap in section 63. Measured choice by choice, the live heap sat
  near 1.1 GB until "the closed lots' fold opened": the CLOSED card's fold drew every lot the fund had ever closed,
  5,562 by month 2,408 (39,667 lines of the signature), and the kept page and the page built new took it to 2,318 MB.
  The merged city has 407 businesses' bonds where main's has 287, so more lots repaid; nothing in CD9 reads the
  fund, its city simply diverged further. A new dial, `FundScreen.CLOSED_FOLD_ROWS` = 200: the fold lists the newest
  200 under the card's 8, titled "the newest 208 closed lots (of 5562)", and a muted "and 5354 older, not listed";
  a fund with 208 or fewer reads "every closed lot (N)" as before. A player opening the fold had the same gigabyte.
- **The harnesses.** DebtMoneysCheck sections 11 (a money under zero: the floors against their constants, a franc
  bill and a 0% franc bond over par at the chip's rate, the audit; the US$ and the euro as before to the cent and the
  bit) and 12 (the US$ cost over the D$'s rate, not unconverted). DebtExchangeCheck section 13 (a calm month: the walk
  from EARNED leaves D$0.0000k of a D$552.67k coupon abroad, D$-552.67k without it); its section 3 walk now expects
  nothing of the coupon. CurrencyCheck, ForeignDebtCheck, SaveFileCheck 12b and ReadPathCheck read the new figures.
  KeptCheck sells the 20-year bond first (`SELL_ORDER`, section 3).

## 2. The calls (★; each takes the recommended way, BP1)

1. ★ **The coupon floors at 0%, the yield goes into the price**: a money under zero sells a zero coupon over par, as
   Germany's and Switzerland's issues did in 2015-21; no issuer paid a negative coupon. **Recommended: keep.**
2. ★ **The domestic floor stays the city's 0.5%** (`MIN_RATE`): the brief's item is the world curves'; the city's own
   curve, its auctions and its central bank are as they were. **Recommended: keep.**
3. ★ **The retired slots are written 0, not removed**: the state array is positional, and removing one would read every
   later slot of a saved city one place off (a format bump). **Recommended: keep.**
4. ★ **The US$ is floored at its own rate too**, so in years when the US rate is under 0.5% (2009-15, as the real
   rate was) a US$ bill is struck at the US rate plus the premium, not at 0.5%: the brief's "the chip, the quote and the
   strike agree", for the US$ as for the other seven. **Recommended: keep.**
5. ★ **The closed lots fold is capped, not paged**: the newest 200 under the card's 8, the older counted. A "show
   older" button as Activity's would reach every lot, at the cost of the same page again once pressed far enough.
   **Recommended: keep the cap**; a pager is a small batch if every closed lot should be reachable.
6. ★ **KeptCheck's reds left on the merge are its city's thin bond market, not fixed here** (section 3): the fixture
   cannot sell six pieces in a market that takes one, at any size. **Recommended: pass them by name** (Jerus,
   2026-11-04: a red caused outside the batch's own code) **and fix the fixture as CD9-K.**

## 3. Found on the way

- **The walk from EARNED to the budget still leaves the domestic coupons paid beyond the bank** (to the households,
  the firms, the world and the central bank's paper): EARNED never counted them, and FloatLinkCheck's `X_EARNED`
  asserts exactly that. In DebtExchangeCheck's city they are D$0, so the walk leaves nothing, as the brief asks. Not
  CD9's and not a fault; nothing queued.
- **The Government screen's "...of which paid abroad" row says "in dollars"** for coupons paid in any of the eight
  moneys (`GovernmentScreen` ~L2068, from before CD8; the figure is right, in D$). A screen's words, outside this
  brief: pending as CD9-G.
- **The fund's next two pages near KeptCheck's heap**: in the same city, the fund's Search with nothing typed lists
  every security in reach (15,829 lines, 1,376 MB live with the page built new) and Activity 9,719 lines (1,414 MB),
  against 2,048. They grow with the bond market as the fold did; KeptCheck's own comment knows Search's list is
  large. Not capped here: their lists are what the pages are for, and they pass.
- **KeptCheck's money sections had no paper to draw on the merge** (the gate's second finding, once the heap was
  fixed). At section 60, month 2,335, the city's bond market took one sale of the fixture's size and no more: after
  it, the bank's room was nil, the households' spare D$8.9T, the firms' D$0.1T and the world's nothing, against a
  smallest deal worth arranging of D$30.3T (`minimumIssueSize()`, 5% of a year's taxes); the month's GDP read
  D$-51.7T. The Note, sold first, took the one sale and the other five were refused as tiny; the Note fell due during
  section 64's watch, so 64's five pictures, 66's "a piece of paper to close" and 67's book row had nothing (7 NEW
  reds). `KeptCheck.SELL_ORDER` sells the 20-year bond first, the piece those sections draw on main, and `sellPaper`
  still returns PAPER's order; 64, 66 and 67 pass, their premise unmoved. The city then runs another way: at section
  71 (month ~2,941) the market again takes one sale, now a 12-month bill, so the Spending page's debt fold opens on
  nothing (197 lines, then 197), and section 69's Government Overview remakes 50 of its 374 nodes in one month. Both
  are the same thin market: each piece needs D$30.3T of bids, and without the bank's room the bids are D$9T. Not
  CD9's code: KeptCheck's sales assume a market main's city has. Pending as CD9-K, with the month's GDP under nought,
  which nothing here looked into.
- DebtMoneysCheck's fixture: a press hands the month's real (nil) exports back to the debt, so the window abroad is
  shut after it; a quote after a press opens it again (`open(g)`), as every other section does.

## 4. The checks, the trace and the ensemble

- **The harnesses the brief names, and their neighbours** (13, both data files named): DebtMoneysCheck red only at
  11(d), the fixture's (above, 54e3988d), then green (189 assertions); ForeignDebtCheck, CurrencyCheck,
  DebtExchangeCheck, SaveFileCheck, ReadPathCheck, TreasuryCheck, FloatLinkCheck, ScaleCheck, InterestAbroadCheck,
  WorldMoneysCheck, ExchangeRateCheck and DeskCheck green on the first run.
- **`./batch-check.sh --pick` on the merge with 8c2cb9af** (both data files named, 49366c39): 81 of 186 harnesses for
  46 files; `Game.java` left to the gate, LongPlaytest run in its place, green. **Exit 4 on KeptCheck alone**, the
  other 80 green. KeptCheck's five failing assertions: one of main's recorded word for word ("the kept header's
  signature equals a header built new... (18 differ)"); one of main's with its count moved ("each month hands back
  nine in ten of the nodes the first draw made, or more (207 of 233)", main 209 of 233, the Government Spending page
  in section 69); three of the thin market (section 3: section 69's Overview, "50 of 374", and section 71's Spending
  debt fold, twice). Main's other three recorded reds did not fire. A standalone run gave the same lines. Before the
  fold cap KeptCheck ran out of heap in section 63; with the cap and before the sale order, its reds were sections
  64-67's seven. Timing: 8 lines SOFT, not counted. Stale: 0 firm.
- **The trace moved**, so the ensemble: 80 runs on 49366c39, the merged model, against `docs/ensemble/0.9.54.md`,
  without the data files as that baseline was (EN2, queued, changes that): **23/26, as 0.9.54 scored**, the same
  three failing (A2, S1, R3). Median final populations: AUTO2 538,393 against 532,042; ZERO_SLACK 332,066 against
  349,699 (its worst seed the same 164,471); ZERO_HOT 9,254 to the person; SHOCK 21,942 against 20,528; HIGH 12,414
  against 12,402. S1's longest spell over 10% is 57 months against 129, A2's 11 against 12. Committed as
  `docs/ensemble/CD9.md`, replacing the run before the merge (6d2a732e against 0.9.50: 24/26, ZERO_SLACK moved by item
  2's converted cost, traced to seed 2, month 135, on main's c143b9a3).
- The full suite is the gate's (`Game.java`'s 156 readers).

## 5. For the next batches

- **Any screen of the moneys**: a world rate, a chip and a yield can now be under zero (the franc's and the euro's in
  their years). A piece's yield is `Debt.getYieldToMaturity()`, negative where its money is; a coupon never is.
- **WN4b** (the peg): the month's revaluation is `NationalAccounts.getDebtExchangeGain()` (gain positive), the run's
  `Game.debtExchangeSinceFounding()`; there is no other.

## 6. For the stranger's review

- `YieldCurve`: the old constructor is the new one at `MIN_RATE`; DebtMoneysCheck 11(d) asserts the US$ and the euro
  curves agree with the old construction to the bit at every tenor. Check that no domestic caller reaches the new
  constructor.
- `Debt.getYieldToMaturity()`: the bracket splits at a price equal to the payments' total (0 exactly). A yield under
  `EFFECTIVE_RATE_FLOOR` (-50%) would pin there; no money comes near it.
- `DebtManager.worldCurveAt()`'s cache has one slot keyed on (rate, floor): a desk reading two moneys in turn
  rebuilds each read. That is a cost, not a fault; the timing harnesses are the gate's.
- `EconomyManager.getExpenses()` now carries the coupon abroad. Its readers: `Game.getIncome()` (EARNED, the point),
  LongPlaytest's save round trip (why it is saved), and `getTotalIncome()`, which nothing outside the checks calls.
  The Government screen's "paid abroad" row is a part of the budget's interest line, which carried it already.
- The month's order: `setForeignInterest` is set in `strikeGovernmentBooks`, beside the budget's interest line,
  after the coupons abroad are paid and before `foreignInterestPaidThisMonth` resets the next month.

## 7. One in, one out

Not a process batch: no script, check rule or session rule changed.
