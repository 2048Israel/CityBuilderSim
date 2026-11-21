# MC2 - BF1's band share under the leverage target

Built by Claude (Opus 5.5) on `batch/MC2`, cut from origin/main ac224abf3 (0.9.172), 2026-11-20.
Found as MC1's finding 2 (`docs/queue/MC1-notes.md`, lines 33-45 and 275-281).

## What was found

BF1 bounded growth lending by the bank's room to its target. In Bank, `projectHeadroom()` (the
uninsured allowance), `leverageHeadroom()` and `projectLeverageHeadroom()` (the insured one) all
clamp `equity / target - exposure` at 0. BusinessDebtManager's `projectRoom()` read the first;
after the bank's settle it read `headroom()`, the carry trade's room, which is clamped the same way.
`insuredProjectRoom()` read the second pair.

So the room was 0 anywhere under the target. It overrode the growth limit's share:

- `canFundProject()` refused every building's loan;
- while the leverage ratio binds, `canFundMortgage()` refused every new mortgage.

`Bank.lendingGrowthLimit()` still allows each borrower a share there, `RATIONED_GROWTH × x/(1−x)`.

BF1's own words say otherwise:

- its note (`docs/queue/BF1-notes.md`, line 218): "the leverage rule still rations insured
  mortgages through capitalRoom as before";
- Bank's comment on its bound: "Growth commitments fit the target *even above it*".

Its review round, the source of the insured bound (lines 445-449), was a bank above its target
with "infinite old monthly growth". The bound was written for the side of the target where the
growth limit is none, and it reached the side where the growth limit rations.

## The band, and the share at each side of the target

**The band** here is MC1's word, and BusinessDebtManager's ("in its band, between its minimum and
its target"). It is the bank between its minimum and its target on the binding measure: the
leverage ratio between `LEVERAGE_RATIO_MIN` and `leverageTarget()` while the leverage ratio binds,
otherwise the risk-based ratio between `CAPITAL_RATIO` and `capitalTarget()`. It is the
REBUILDING stance, where `lendingGrowthLimit()` is finite and above 0. (Bank's payout band,
`PAYOUT_IN_BAND`, is another band: from the target to its top.)

**Under the target, in the band: the growth limit's share.**

- Each borrower: `capitalRoom()`, as before BF1. Its debt may grow `RATIONED_GROWTH × x/(1−x)` this
  month: 0 at the minimum, `RATIONED_GROWTH` (1%) halfway, rising toward the target. The debt is
  the uninsured debt, or the whole debt with the mortgages while the leverage ratio binds.
- The bank's shared allowance (new `Bank.bandShare()`): the same rate times the book the binding
  measure counts (the exposure, or the weighted book). This is what `lendingLimit()` takes, on
  the equity left once the month's known losses are taken off (`unsettledCreditLoss()`). Loans
  pending this month come off it, as over the target.
- It stops at the room to the minimum, `(equity − loss) / minimum − book` on each measure: BF1's
  construct, read at the minimum in place of the target (REV-MC2 P2). It binds from x ≈ .97, where
  the curve's share would take the bank past its minimum; at x = .99 the curve allows 99% of the
  exposure, and the room to the minimum is 31% of it.
- At or under the minimum: none (`keepGoingOnly`, and the curve's 0).

**At or over the target: BF1's room to the target, unchanged.** That is `equity / target −
exposure` on each measure, net of the month's known losses before the settle and of pending loans,
and for the uninsured within `headroom()`. `capitalRoom()` is unlimited there.

A bank whose month was struck over its target, its growth limit none, that the month's known
losses take under it before its book settles, reads BF1's room too: none (REV-MC2 P1). The band's
share is read only while the bank's growth limit, on its books before the settle, rations, so a
default never raises the room. After the settle nothing writes a growth loan until the next
month's rule is struck on the settled equity; under the target, that is the band's share.

**Why.** BF1's note and its comment both put the bound over the target, where nothing else limits
growth; under it, the capital rule's growth limit is the bank's rationing (WHAT IT LENDS, 0.7.8),
and a bound of 0 there made the band a stop.

## What changed

- `Bank.java`:
  - `lendingGrowthLimit()` is split into the curve, `growthLimitOn(equity)`. This is
    bit-identical: it is called on `equity()` behind the same two guards.
  - `projectHeadroom()` and the new `settledProjectHeadroom()` share `growthHeadroom(loss)`. That
    is the room to the target when the bank is at or over it on both measures, and
    `bandShare(equity − loss)` when it is under.
  - `bandShare()` stops at the room to the minimum, and is none while the bank's growth limit is
    none (REV-MC2 P2 and P1).
  - `leverageHeadroom()` and `projectLeverageHeadroom()` share `insuredHeadroom(loss)` in the
    same way, on the leverage measure.
  - The banner comment UNDER THE TARGET, THE BAND'S SHARE says why.
- `BusinessDebtManager.java`: `projectRoom()`'s settled branch reads `settledProjectHeadroom()` in
  place of `headroom()`. The two are equal at or over the target, since `headroom()`'s two target
  caps are the same distances. The javadoc of `projectRoom()` and `insuredProjectRoom()` changed.
  The orchestrator cleared this file first (FB15 adds one overload near `takeBackCapitalRefusal`,
  which this batch does not touch).
- `BankFailureCheck.java`: a new section 9, and the header's WHY.

Not changed:

- `headroom()`, the carry trade's room. Under the target it is still none: "domestic first".
- The bank's bond purchases, in the end. `BondMarket.bankBuys()` asks for equity at the target on
  the books before the settle, and `bankClaimRoom()` reads `projectHeadroom()`. A bank under its
  target buys nothing, as before. A bank over it that the month's default takes under it bid up to
  the band's share on the rule REV-MC2 reviewed (P3: the notes then said "moves nothing", which
  was wrong); since P1's guard its room there is none, as on main. Section 9's line 14 shows it.
- `lendingGrowthLimit()`'s curve, and the households' rule.

No saved field, no save format.

## The harness

`BankFailureCheck` section 9 is the bank's own harness. Its section 3 holds BF1's insured lines.
The section causes its condition with hand-built banks, held by their restore setters (`refresh`,
`setCash`, `setForeignDeposits`):

- one branch, its exposure its cash, funded from abroad;
- nothing weighs, so its leverage ratio binds;
- its ratio stands x of the way from `LEVERAGE_RATIO_MIN` to its target: x = 0 (the minimum), .5,
  .9 (just under) and 1.1 (just over).

The borrowers are a landlord owing an insured mortgage and a works owing a loan, under the bank's
rule as Game hands it to them.

It had 14 lines at the mends, 3 fixture lines and 11 assertions. REV-MC2 added 9, 3 fixture
lines and 6 assertions (12-17 below): 23 lines.

- With the mend: all 23 pass.
- On main's rule (main's `Bank.java` and `BusinessDebtManager.java` of 0.9.174 put back, with
  `settledProjectHeadroom()` returning `headroom()` as main's settled branch reads): **14 of the
  17 assertions FAIL**, 1-11 and 15-17, and the 6 fixture lines pass. 12-14 pass on main, which
  gives none there: they hold the mend to it against the rule REV-MC2 reviewed.
- On that rule (`Bank.java` at f5452977d, 207704ac8's merged with 0.9.174): **12-16 FAIL**. 17
  passes on it: it is the risk-weighted cover the review asked for.

What the 11 assertions prove:

1. The band's share rises from none at the minimum to `RATIONED_GROWTH` of the exposure halfway.
2. Just under the target, the insured room is the growth limit's share of the exposure, more than
   none.
3. ...and every building's loan reads the same share, before the settle and after.
4. Just under the target the door writes a mortgage within the landlords' share. The section
   writes it (`issueMortgage`) and their debt grows.
5. ...what it wrote comes off their share, and one past what is left is refused for the bank's
   capital.
6. Halfway down the band their share is `RATIONED_GROWTH` of their debt, and a mortgage within it
   is written.
7. Just under the target a building's loan within the works' share is lent, and one past it is
   refused.
8. The band's share is one allowance: another sector's loan this month leaves the rest of it.
9. ...so a mortgage within the landlords' own share but past that rest is refused, and one within
   the rest is written.
10. The month's known losses come off the band's share before the settle: the share is on the
    equity they leave. The expected figure is read from a second bank built with that equity.
11. On both sides of the target the door writes what the rule allows and refuses past it: the
    room to the target just over it (BF1's bound, kept), the band's share just under it.

What REV-MC2's lines prove:

12. A default never raises the room: of two banks over their target, struck lending freely, the
    one whose loss stays within its cushion keeps room to the target, and the one whose loss goes
    5% past it has none, insured and not (P1).
13. ...so the bank the default takes under its target writes no building's loan and no mortgage
    before its book settles; the mortgage is refused for the bank's capital.
14. ...and bids nothing for a works' issue that the other bank buys part of (P3, through
    `BondMarket.issue()` with fixed readings, as in section 3).
15. At x = .99, where the growth limit's share (99% of the exposure) passes the bank's minimum, the
    room is the room to the minimum (31%), insured and not (P2).
16. ...so the bank lent all but a hair of it (`BAND_EDGE`, a millionth of the room) stands over its
    minimum, the next month not one of keeping borrowers going, and a hair past it would stand
    under: the cap is the minimum's own edge. The two banks stand a margin either side of it,
    never on it, since the minimum's test is a strict < (REV-MC2-2's note).
17. A bank whose risk weights bind, halfway down its band on them and over its leverage target:
    its uninsured room is `RATIONED_GROWTH` of its weighted book, and a works' loan within the
    works' share is lent, one past it refused (P4: `growthLimitOn()`'s weighted branch and
    `bandShare()`'s weighted book).

Line 11's over-target half passes on main too, because the rule over the target is BF1's and
unchanged. The line fails on main by its band half. It is one line because the claim is about the
two sides at once. BF1's over-target lines in section 3 are untouched and pass.

Figures the section prints:

- the leverage ratio is .030000, .034688, .038438 and .040313, against a target of .039375;
- the growth limit is .010000 a month halfway and .090000 just under;
- just under, the landlords' share is 96,192 of the bank's 900,000;
- just over, the room to the target is 238,095;
- at x = .99 the growth limit is .99 a month, 9,900,000 of the exposure; the room to the minimum
  is 3,093,750; lent it less a millionth, the bank's leverage ratio is .030000007, and a
  millionth past it, .029999993.

## Checks

- `./batch-check.sh --fast`: compile OK, maps regenerated, Stale 0 firm.
- One at a time:
  - `BankFailureCheck`: the full harness, its 24 audited Random cities included. All passed.
  - `CreditCrunchCheck`: all passed.
  - `MortgageCheck`: all passed.
  - `LongPlaytest`: OK.
- `sh tools/pick.sh origin/main HEAD`: 50 of 232 harnesses (not ALL). Review: needed - money
  (`Bank.java`). The other 46 ran in one sequential call (`-Dcbs.parallel=1`): exit 3. The only reds are main's known reds, in 5 harnesses (BankCheck, HoldersCheck, KeptCheck, ShortSellingCheck, YoungBankCheck); BankCheck's and HoldersCheck's lines are the same, line for line, as FS17's gate run of ac224abf3. SaveSlotCheck's 1 timing line timed soft.

**The played crunch moves.** These are `CreditCrunchCheck`'s figures (MC1's played crunch: SHOCK
seed 13, the bank held halfway down its leverage band from m151 to m270), main's from FS17's kept
gate run of ac224abf3:

| over the 120 crunch months | main | MC2 |
|---|---|---|
| mortgages written | 0 | 6 |
| months the landlords asked / with the bank short | 91 / 56 | 38 / 31 |
| what the owners put in | 13,431 | 6,907 |
| homes, m151 to m270 | 497 to 1,522 | 497 to 1,192 |
| households at m270 / doubled up | 1,519 / 114 | 1,172 / 37 |
| the most households with no home | 0.00 | 0.00 |

On the merge with 0.9.174 (f5452977d) MC2's crunch reads: 6 mortgages; the landlords asked in 45 months, 35 of them with the bank short; the owners put in 6,950; homes 497 to 1,195; at m270, 1,179 households and 47 doubled up; nobody with no home. Main 0.9.174's own run of it is not at hand here (OE1's kept run is of an earlier dry run).

The paths split at m154, where MC2 wrote its first mortgage in the band (main's m160 has 949 households, MC2's 956). From m196 to m248 MC2's landlords ordered nothing. That was a hold for land, not credit: their decision line read "Holding: no land - needs 5,570 m², 3,280 m² free" (a probe print in the harness, run once and reverted). Once ground came free (m252) they built again, on owners' money and 3 mortgages (m254, m256, m259; the other 3 were m154, m166 and m195). Every assertion of the harness's sections 1-3 passes. Nobody is left with no home on either path; the smaller city is the smaller placement room the land hold left.

**The trace moves.** `LongPlaytest` on the branch, against the same gate's `LongPlaytest.log`,
first differs at m600 (the market line, then the power and works decisions from m608): the change acts only under the target (at or over it
the room is the same expressions, bit for bit), so the default city's bank stood under its target by then. So the 80-run ensemble is owed (below).

## Calls

- ★ **The band's share is a shared allowance beside each borrower's share, not no bound at all.**
  The literal "as before" (no shared bound under the target) was the other reading. BF1's shared
  allowance exists so the month's desks, loans and the bank's claims, do not each spend the same
  room, and so that losses known before the settle reach it. The band's share in money keeps both.
  In a played month each borrower's `capitalRoom()` binds first: the sum of each debt's share is
  at most the share of the exposure.
- ★ **The band's share stops at the room to the minimum** (REV-MC2 P2, the reviewer's recommended
  mend). The curve x/(1−x) was shaped in 0.7.8 to meet "no limit" at the target, and from x ≈ .97
  its share would take the bank past its minimum in one month, to lending nothing but to keep
  borrowers going the next. BF1's construct, the distance to a ratio on the equity the month's
  losses leave, read at the minimum, bounds it there and nowhere else. The other readings: a cap on
  the curve's top (Jerus's 0-1% a month for a bank short of capital), which would move the
  households' rule and every borrower's `capitalRoom()` too; or the distance to a point inside the
  band, which no rule names. Lent the whole of it, the bank stands at its minimum and lends no
  growth the next month until its earnings raise its ratio.
- ★ **A bank its month's rule found over its target reads none when the month's losses take it
  under, until its book settles** (REV-MC2 P1, the reviewer's mend). The band's share is read only
  while the bank's growth limit, on its books before the settle, is finite. The month's rule for
  such a bank left every borrower's `capitalRoom()` unlimited, so the shared allowance was the only
  bound: on the reviewed rule a 9,000 loss left 9,500 of room and a 10,000 loss 14.9M. With the cap
  at the minimum alone it would still have been the room to the minimum, opened by a loss.
- ★ **Under the target the share is not capped by `headroom()`'s strain term.** `capacity() ×
  EASY_STRAIN − weighted book` is the carry trade's room. EASY_STRAIN's own javadoc: "the carry
  trade is lent only the room below it". For a leverage-binding bank with a business book that
  term is 0 below about x = .8, so the share would be none in most of the band. Over the target,
  BF1's `min(headroom(), ...)` stands.
- ★ **The losses known before the settle come off the band's equity**, as they come off the
  distance over the target: `growthLimitOn(equity − loss)`. The month's rule (`capitalRoom()`)
  was struck on the equity at the top of the month; the shared allowance tightens after a default
  as BF1 meant ("the loss just booked must reach the growth desk", Game).
- ★ **"Just under" and "just over" are a tenth of the band either side of the target** (x = .9
  and 1.1). Nearer the target the share stops at the room to the minimum, which line 15 asserts
  at x = .99.
- **The harness is `BankFailureCheck`'s, not a new `MortgageBandCheck`.** The bank's harness holds
  BF1's insured lines, and the brief preferred it if it fit. FS9's sweep of its other sections is
  left to FS9.

## A finding mended: the room across the target

Toward the target from below the curve's share rose without bound: x/(1−x), shaped in 0.7.8 to
meet "no limit" over the target. Just over the target BF1's room starts from none. At x = .99 a
borrower could grow its debt 99% in the month, where at x = 1.01 the bank's room is 0.24% of its
exposure, (x − 1)(target − minimum)/target. The notes at 207704ac8 recommended leaving it. The
orchestrator and REV-MC2 (P2) said it is this batch's own behaviour, since main wrote nothing
there, and to mend it.

Mended by the cap at the minimum (Calls). The shared allowance now rises through the band with x
to at most the room to the minimum, 31% of the exposure just under the target. The step down at
the target remains, bounded: 31% at x = .99, 0.24% at x = 1.01, BF1's room over it. Each
borrower's own share (`capitalRoom()`, the curve on its debt) is not capped; where the cap binds,
the shared allowance holds them all to it together.

## For Jerus

Nothing: the finding above is mended, as REV-MC2 asked.

## The merge and the ensemble

Main moved to 29f1e6a3f (0.9.174, OE1) after the mends at 207704ac8. Merged as 3d90ab50d. Only the
generated indexes conflicted, and they were regenerated (f5452977d). Main touched none of this
batch's files.

On the merged tree:

- `./batch-check.sh --fast`: green;
- `BankFailureCheck`, `CreditCrunchCheck` and `MortgageCheck`, one at a time: all passed.

The ensemble begun on the gate box at f5452977d was stopped at REV-MC2's word (`ensemble.sh --stop
MC2`, 5 processes), before any result: it runs after the mends.

## REV-MC2 and the mends

REV-MC2 (money) on 207704ac8 said NOT YET: one P1 and three P2. All four are mended:

- **P1**, a loss raised the room: `bandShare()` is none while `lendingGrowthLimit()` is none
  (Calls). Lines 12-13.
- **P2**, the share past the minimum near the target: the cap at the room to the minimum (Calls,
  and the finding above). Lines 15-16.
- **P3**, the bond bid in the loss window: mended by P1, shown by line 14, and the notes' "moves
  nothing" corrected (What changed).
- **P4**, no bank bound on its risk weights: line 17.

Only `Bank.java` (`growthHeadroom`, `insuredHeadroom`, `bandShare`, the banner) and
`BankFailureCheck.java` changed in the round.

Main moved to e9dadb540 (FS4 shipped, and records) during the round: merged as 01ca18e1e, only
the generated indexes conflicting (taken from main and regenerated, a7ab9dfba). Main touched no
file of this batch's, nor `BondMarket`.

The round's checks, on that merge:

- `BankFailureCheck`: all 23 lines of section 9 and every other section pass. On the rule REV-MC2
  reviewed, 12-16 FAIL; on main's, 1-11 and 15-17 (The harness), measured again after line 16's
  margin (below).
- `./batch-check.sh --fast`: green; Stale 0 firm.
- `sh tools/pick.sh origin/main HEAD`: 50 of 234 (not ALL); review: needed - money (`Bank.java`).
  The 50 ran in one sequential call (`-Dcbs.parallel=1`, 5,744 s, 53 with the suite's own): 49
  passed. The reds: main's known reds (BankCheck, DistressBleedCheck, CarCheck), and one NEW line
  in KeptCheck, section 55's Bank's Profit page, "most months make no node at all (12 of 25)".
  SaveSlotCheck's timing line timed soft; CapitalFlowCheck's 1 and ScaleCheck's 2 path lines were
  observed.
- `CreditCrunchCheck`: passed, its crunch as on the merge before the mends: 6 mortgages; the
  landlords asked in 45 months, 35 with the bank short; the owners put in 6,950; homes 497 to
  1,195. The crunch's bank stands halfway down its band, where the cap does not bind.
- ★ **KeptCheck's line is load, not this batch: a known-reds row, FS5.** KeptCheck alone on the
  same merged tree, with main's `Bank.java` and `BusinessDebtManager.java` put back (and the
  `settledProjectHeadroom()` stub), gave 9 of 25, a FAIL too. KeptCheck's own note at
  `playedCity` gives this page "6 of 25 months quiet under a loaded PC, 17 of 25 under a quiet
  one" (KBP1). The kept gate logs on the PC give 14 to 25, each version the same on every rerun,
  and FS4's gate on the box passed it. The ship file's row names FS5, whose range section 55 is
  and which takes KBP1. The brief said FS1; FS1's study folded KeptCheck's rows into FS5, and FS4
  named FS5 for section 89's row the same way. On main's rule KeptCheck also failed five lines
  that MC2's path passes: KFP1's four (known-reds rows 47-50) and section 78's held slider.

REV-MC2-2 on e204cbb5a said SHIP, all four closed, with one harness note: line 16's bank ended
exactly at its minimum, where `lendsOnlyToKeepBorrowersGoing()` tests a strict <, so a rounding
bit could flip it. Taken: the bank is lent a millionth of the room under it and a millionth past
it, and the line asserts both sides. It passes; it fails on the reviewed rule as before, and now
on main's too (main's room is none, so a hair past it is still over the minimum).

## The ensemble

The completed pre-integration ensemble is **MC2m**, classes of `3599c9d15`
(0.9.174), against `docs/ensemble/0.9.174.md`, JAVA_OPTS empty. It ran on the
laptop (Jerusalem): the gate box already held ensemble FB15-174 and the nightly,
so the runner used the contract's overflow rule. `target/mc2-ensemble.txt` records
**80/80 runs, 194.3 minutes; 25/26 criteria, hard 10/10 PASS; no changed verdicts**.
No runs were missing or short. The informational A2 inflation-spell criterion
remained FAIL, as on the baseline; retired H1 is excluded from the count.

The retained baseline, including its data paths and raw summary, is archived at
`docs/queue/MC2m-ensemble.md`. The original baseline, score, snapshot, 80 CSVs,
per-run logs and timings remain untouched in `target/ensemble/MC2m/`. This is the
completed result of the old worker despite its later provider-limit failure;
it is not a reason to replay that snapshot. It predates FB15's 0.9.177 model and
does not establish the final merged model's ensemble verdict.

## Recovery and integration (MC2c2, 2026-11-21)

Recovered the same clean `batch/MC2` at `3599c9d15`; no branch recreation or
reset. Read the original MC2 brief retained as the launcher's `in-1.txt` and
the `out-3.jsonl` / `out-4.jsonl` final reports. The latter stopped at a provider
limit while the ensemble was still owned by its runner; the retained result
above establishes that it completed. REV-MC2-2's SHIP on `e204cbb5a` and
`6abca724a` remains settled, with its margin note already fixed at `3599c9d15`.

Merged main `cdd4a2a09` (0.9.177, FB15 plus OB1 and GB5). There were no source
conflicts: FB15's four-argument `BusinessDebtManager.takeBackCapitalRefusal()`
overload merged separately from MC2's `projectRoom()` change. `Bank.java` and
`BankFailureCheck.java` were unchanged by main. Conflicts were confined to
`docs/dials.md`, `docs/harnesses.md`, `docs/month-order.md`,
`docs/map/BusinessDebtManager.md` and `docs/map/README.md`; generated indexes
were taken from main and are regenerated on the merged source by `--fast`.
No R1g/PW1 hunks or KeptCheck rows were edited.

The merged model includes FB15's trace changes, so MC2m is historical evidence.
A final ensemble must compare the merged final model with the newest baseline,
currently `docs/ensemble/0.9.177.md`, on the orchestrator's GO. Before final-ensemble GO, no ensemble or
dry run had been started during recovery.


### Recovery checks and handoff

Merge commit: `f77e36309`, parents `3599c9d15` and `cdd4a2a09`. No new money
rule was introduced by integration: the only source difference from the
reviewed pre-merge MC2 files is FB15's separate refusal-mark overload, already
published on main. The recovery did not reopen REV-MC2-2's settled review.

On that merged model, using the contract's C:/ Earth/world data paths, one
named harness per call, with no other JVM owned by this session:

- `./batch-check.sh --fast`: exit 0, compile and regenerated maps green,
  Stale 0 firm findings (194 soft). `target/MC2c2-fast.txt`.
- `BankFailureCheck`: green, 1/1 harness passed; all 23 section 9 lines pass,
  including the margin either side of the minimum, and all 2,880 months of
  its 24 Random cities audit. `target/MC2c2-BankFailureCheck-suite.txt`.
- `MortgageCheck`: known-red-only, its two section 12 lean-month fixture
  lines, exactly those in main's `docs/notes/gate.txt` and FS9 rows of
  `docs/notes/known-reds.txt` (the HN1-A path no longer supplies a lean month
  within sixty). No new red. `target/MC2c2-MortgageCheck-suite.txt`.
- `CreditCrunchCheck`: known-red-only, its two section 2 held-bank fixture
  premises, exactly those in main's gate record. No new red. Section 4's
  FB15 door-price and refusal-mark/trimmed-order assertions all pass. Its
  played crunch now reports owners putting in 999, homes 264 to 439 and no
  household left with no home; these figures supersede the earlier .174
  crunch above for the merged model. `target/MC2c2-CreditCrunchCheck-suite.txt`.
- Every focused call also compiled, regenerated maps and reported Stale
  0 firm; no source changes followed the checks.

`sh tools/pick.sh origin/main HEAD`: **51 of 235, not ALL**, against
`cdd4a2a09`; the old pick was 50, with `OrderDoorCheck` newly included.
Review line: **needed - money: Bank.java**. Saves and threading do not change;
REV-MC2-2's SHIP remains applicable. The exact picked harnesses are:

```
AuctionCheck BankCheck BankFailureCheck BondCheck BooksCheck BuildCardCheck
BuybackCheck CallCheck CapitalFlowCheck CarCheck CentralBankCheck CoalMiningCheck
CopperMiningCheck CreditCheck CreditCrunchCheck DistressBleedCheck EquityCheck
ExchangeCheck ExpectationsCheck FittingCheck FloatLinkCheck ForestryCheck FundCheck
FundLedgerCheck HistoryCheck HoldersCheck InterestAbroadCheck InvestCheck KeptCheck
LongPlaytest MarketCheck MonetaryCheck MortgageCheck NewGameCheck NewUnitCheck
OrderDoorCheck OrderSearchCheck PolicyPreviewCheck QuarryingCheck ReadPathCheck
RefineryCheck SaveFileCheck SaveSlotCheck ScaleCheck SectorStatementCheck
ShortSellingCheck SupplierCreditCheck TimingCheck WantedPostsCheck WoodProductsCheck
YoungBankCheck
```

The orchestrator authorized only `--fast` and sequential focused checks while
TN1's full gate runs on the gate box. Before final-ensemble GO, no full pick, LongPlaytest, ensemble or
dry run was run during recovery; the historical full pick is not presented
as a fresh verdict on .177. The current pick is not ALL and MC2 changes no
publisher file relative to main, so contract 5.5 requires no dry run. The
provisional marker remains; final ensemble GO and then readiness GO are
requested from the orchestrator, with TN1's gate first.


## Final ensemble (MC2c2-final)

The orchestrator authorized the final merged model at `fe42a4567`, fresh label
`MC2c2-final`, against `docs/ensemble/0.9.177.md`. It ran on **gatebox**, eight
at once under the normal gate/nightly priority and yield rules, with empty
JAVA_OPTS. No fallback or restart occurred. The owned launcher stayed alive
until its normal exit 0; no other JVM was run by this session meanwhile.

**80/80 runs in 14.1 minutes; 25/26 counted criteria; M1/F1 hard 10/10 PASS;
no changed verdicts versus .177; no missing or short runs.** All five policies
had zero failed or unread money audits and zero runs with a LongPlaytest
finding; pairing was 64/64. The informational A2 inflation-spell criterion
remains FAIL: longest spells have median 20 months [5 to 39], against the
baseline's 15 [5 to 39]. H1 remains retired and excluded from the count. All
other counted criteria pass. The full score table and raw summary are in
the committed `docs/ensemble/MC2.md`; its publication version uses the publisher's next-version token,
while its source snapshot and comparison retain their actual .177 provenance.

Remote evidence folder:
`/home/jerus/cbs/tools/fe42a4567b200f98e1e4fcceee63ced7afbd4dbd/target/ensemble/MC2c2-final`.
The copied baseline, score, snapshot, 80 CSVs, logs and timings are retained at
`target/ensemble/MC2c2-final/`; launcher log `target/MC2c2-final-ensemble.txt`.
The snapshot names the box's `/home/jerus/cbs/main/data/earth-2.bin` and
`world-1.bin`, OpenJDK 21.0.13. Historical MC2m and its .174 comparison remain
untouched, as recorded above; they are not substituted for this result.

After the ensemble exited, merged latest main `5790d0506` (0.9.178, TN1).
There were no source conflicts and no changes to MC2's Bank,
BusinessDebtManager or BankFailureCheck from the ensemble snapshot. TN1
changes CityMap/CityRuns display, GatewayCheck/MainLineCheck and the version;
the orchestrator confirmed that it changes no model trace and does not require
a repeat ensemble. Conflicts were only `docs/dials.md`, `docs/harnesses.md`
and `docs/map/README.md`, taken from main for regeneration on the merged tree.


Final integration merge commit: `479b01676` (parents `fe42a4567` and
`5790d0506`). After the merge, `./batch-check.sh --fast` exited 0: compile
54 seconds, maps regenerated, Stale 0 firm (194 soft). Log:
`target/MC2c2-postTN1-fast.txt`. MC2's three source files and the historical
MC2m archive compare identically to `fe42a4567`.

The final baseline adds `EnsembleCheck` to the pick: **52 of 235, not ALL**
against main `5790d0506`, the previous 51 names listed above plus
`EnsembleCheck`. It ran as one focused call and passed, including runner and
driver compilation, policies and stored-baseline parsing. Its wrapper also
regenerated maps and reported Stale 0 firm; no source changed afterward.
Log: `target/MC2c2-EnsembleCheck-suite.txt`.

Final review line remains **needed - money: Bank.java**; the disjoint source
merge does not reopen REV-MC2-2's SHIP. No save or threading changes, publisher
edits, full pick or dry run were added. Contract 5.5 still requires no dry
run. Both ensemble comparisons are preserved, the final baseline is committed
before readiness, and readiness GO is requested with the provisional marker
still present on the pushed ship file. The orchestrator alone gates and
publishes; this recovery has neither edited main's records nor run ship.sh.


The required gameplay project write-up is
[The bank's share under its target](../../claude/mc2-band-share.md). It follows
the existing prose files under `claude/`; no `claude/index.md` exists in this
checkout to update. This publication-record completion changes docs only;
the existing checks, final ensemble and review were retained without reruns,
and the provisional marker remains pending readiness GO.


## Stopped gate and section 92 trace (2026-11-21)

The gate of ready `9533081e6f9512248cf8e7ae2f831fe4d1713e91` against main
`5790d0506e431b74be7db1010607fe2dc20bef50` stopped without publication.
The box kept `/home/jerus/cbs/runs/cbs-gate-MC2-eaV6vYID`, copied home as
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-MC2-eaV6vYID`. The check stage
took 868 seconds and exited 4; the suite reported 224 harnesses in 852
seconds, 213 passed and 11 failed. Gate exit 1. Main remains .178.

Per contract, the provisional marker was restored and pushed FIRST, at
`9b485c4ce`, and verified on origin. The former ready commit, completed
final ensemble and review are preserved. The kept run has not been modified
or run in. Read-only copies of its ship log, suite, KeptCheck/AuctionCheck
logs, unnamed lines, commit identities and integrity records are under
`target/MC2c2-gate-stop/`. The copied KeptCheck log's SHA-256 matches its
kept original. No JVM, unchanged suite, ensemble or source change was made
for this investigation.

The three unnamed lines are KeptCheck's exit 3, one actual assertion and
the aggregate `3 FAILED`. The actual new assertion is section 92's
`the city's books hold a piece of paper (the fixture's premise for a line's page and Close it now: 0)`.
The other two assertions are the already-recorded Spending/Healthcare
opened-page comparison and the software-renderer heap bound (1,778.2 MiB
live of 2,048). They are not three new fixture defects.

Concrete path on the kept run: section 92 begins at month 4327 (KeptCheck
log line 4797). `kp2bSets` calls `piecesOnTheBooks` (source 8645-8657), which
first reads the city's debt book. If empty, it computes
`max(minimumIssueSize() * 3, cash / 40)` and tries Term 10, Note 12, Serial
10, without playing a month. Log lines 4914-4916 record `Nothing issued.`
for all three; line 4917 fails the book-count premise. Consequently the
two subsequent Finances line/Close-it-now `nothingSet` watches are skipped.
This is not a failed redraw equality: those pages lack their fixture.

`KeptBench.financesSell` (3154) calls
`FinancesScreen.executeDebtLogic` (7109), using the real Game handlers and
the default auction cap. Each handler returns `Nothing issued.` on an
empty DebtQuote. The unchanged quote paths run Auction and either retain
zero sold face or turn a sub-minimum partial sale into an empty quote.
The actual kept log does not record the quote/bidders, so it does not
establish which rejection branch occurred at month 4327.

`Game.auctionBidders` (11952-11992) computes the bank's city-paper bid from
insolvency, equity, weighted book, pending allotments and deposit rate;
it does not call MC2's project room, growth headroom or band share. Its
other bidders use household spare savings, firms' idle wealth/paper and
hot-money holdings/return. Those methods and all issuance/helper files
compare unchanged with main. MC2 can move the played city's state, but
does not change this auction's valid refusal rule on fixed inputs.

There is retained negative-case evidence without another JVM: the same
gate's AuctionCheck passed `a city nobody bids on sells nothing, and says
short` and `...raises nothing, and pays no fee for it`, using
`Auction.Bidders.none()` (source 523-525). Thus expecting at least one
piece after three market-dependent offers is an uncaused fixture premise,
consistent with FS5's CD9-K class. This does not reconstruct the actual
month-4327 bidder breakdown or claim that the main played path also failed.

At fetched FS5 `d5f9cf8c6`, the exact `piecesOnTheBooks` helper already has
a fallback calling `booked(g, PAPER[3], ask)` when all three offers fail
(diff hunk main 8653, FS5 8518 onward), in addition to `sellPaper`'s
separate fallback. FS5's retained completed software run reached section
92 at month 4329 and sold a Term successfully; it did not exercise this
empty-three-sales fallback. Its eight-second isolated fixture proof covers
fund settlement/grid/renderer, not this paper helper. Neither is claimed
as proof of the exact new failing path. Coordination is through the
orchestrator; KeptCheck and KeptBench remain FS5-owned.

The proposed smallest fresh proof, if the orchestrator requires one, is a
single zero-month scratch-game/Monocle software-renderer probe (768 MiB,
45-second guard), fixing all auction bidders to zero through Game's
bidder test seam, then invoking the REAL `piecesOnTheBooks` helper and
checking that all three real handlers refuse and the helper returns no
paper. FS5's owner can separately validate its own fallback against that
same condition. It would prove the fixture gap, not replay month 4327.
The scope was sent for GO; it has not run. No known-red row, rejudge,
red override or renewed readiness was added pending that decision.
