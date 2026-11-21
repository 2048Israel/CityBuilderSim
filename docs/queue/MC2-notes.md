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
- At or under the minimum: none (`keepGoingOnly`, and the curve's 0).

**At or over the target: BF1's room to the target, unchanged.** That is `equity / target −
exposure` on each measure, net of the month's known losses before the settle and of pending loans,
and for the uninsured within `headroom()`. `capitalRoom()` is unlimited there.

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
- The bank's bond purchases. `BondMarket` bounds them by `spareCapital()`, which is negative under
  the target, so `bankClaimRoom()` reading the share moves nothing.
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

It has 14 lines: 3 fixture lines and 11 assertions.

- With the mend: all pass.
- On main's rule (main's `Bank.java` and `BusinessDebtManager.java` put back, with
  `settledProjectHeadroom()` returning `headroom()` as main's settled branch reads): **all 11
  assertions FAIL**, and the 3 fixture lines pass.

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

Line 11's over-target half passes on main too, because the rule over the target is BF1's and
unchanged. The line fails on main by its band half. It is one line because the claim is about the
two sides at once. BF1's over-target lines in section 3 are untouched and pass.

Figures the section prints:

- the leverage ratio is .030000, .034688, .038438 and .040313, against a target of .039375;
- the growth limit is .010000 a month halfway and .090000 just under;
- just under, the landlords' share is 96,192 of the bank's 900,000;
- just over, the room to the target is 238,095.

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

The paths split at m154, where MC2 wrote its first mortgage in the band (main's m160 has 949 households, MC2's 956). From m196 to m248 MC2's landlords ordered nothing. That was a hold for land, not credit: their decision line read "Holding: no land - needs 5,570 m², 3,280 m² free" (a probe print in the harness, run once and reverted). Once ground came free (m252) they built again, on owners' money and 3 mortgages (m254, m256, m259; the other 3 were m154, m166 and m195). Every assertion of the harness's sections 1-3 passes. Nobody is left with no home on either path; the smaller city is the smaller placement room the land hold left.

**The trace moves.** `LongPlaytest` on the branch, against the same gate's `LongPlaytest.log`,
first differs at m600 (the market line, then the power and works decisions from m608): the change acts only under the target (at or over it
the room is the same expressions, bit for bit), so the default city's bank stood under its target by then. So the 80-run ensemble is owed, against `docs/ensemble/0.9.171.md`; asked for in the mends mail.

## Calls

- ★ **The band's share is a shared allowance beside each borrower's share, not no bound at all.**
  The literal "as before" (no shared bound under the target) was the other reading. BF1's shared
  allowance exists so the month's desks, loans and the bank's claims, do not each spend the same
  room, and so that losses known before the settle reach it. The band's share in money keeps both.
  In a played month each borrower's `capitalRoom()` binds first: the sum of each debt's share is
  at most the share of the exposure.
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
  and 1.1). Nearer the target the curve rises without bound (below), so a closer point would
  assert a share larger than any played month's.
- **The harness is `BankFailureCheck`'s, not a new `MortgageBandCheck`.** The bank's harness holds
  BF1's insured lines, and the brief preferred it if it fit. FS9's sweep of its other sections is
  left to FS9.

## A finding left as it is: the room across the target is not monotone

Toward the target from below, the share rises without bound: x/(1−x), shaped in 0.7.8 to meet "no
limit" over the target. Just over the target, BF1's room starts from none.

At x = .99 a borrower may grow its debt 99% in the month. At x = 1.01 the bank's room is 0.24% of
its exposure: (x − 1)(target − minimum)/target. The game played the curve this way from 0.7.8 to
BF1, with no bound over the target at all.

A bank that lends a large share near its target lowers its own ratio, and so its share the next
month. Recommended: leave it. The other choice would be a cap on the curve's top, which is Jerus's
brief's (0-1% a month a bank short of capital, `RATIONED_GROWTH`) and the households' rule too. So
it is not queued. If the orchestrator wants it decided, it is a question for Jerus with those two
options.

## For Jerus

Nothing beyond the finding above.
