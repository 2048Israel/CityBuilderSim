# MC1 - a credit crunch no longer leaves households with no home

Built 2026-11-20 by Claude (Opus 5.5) on `batch/MC1`, cut from main bf048eb69 (0.9.167) and merged
with origin/main at 865a7171c (BI1, 0.9.168).

## The brief

CU2's ensemble (`docs/queue/CU2-notes.md`, lines 263-290) found SHOCK seed 13 with up to 174.3
households with no home (m738-776). The bank's leverage ratio bound, `canFundMortgage` refused
nearly every landlord's mortgage with `MORTGAGE_CAPITAL`, and Real Estate built 1 Low-Rise block in
m703-772 while its plan asked for 20-33 a month. The brief asked which rule should give:

- the bank's capital, rebuilt in a crunch;
- the insured mortgage, under the leverage rule;
- the landlords' own equity, while the bank is short.

It also asked for a harness whose own crunch does not depend on a played path.

## Findings

1. **On today's main the played crunch no longer strands anyone, but its cause remains.**
   I replayed main bf048eb69's SHOCK-13 in one JVM, through the ensemble's own driver, for 800 months:
   - no month had a household with no home;
   - the leverage ratio bound in 647 months;
   - in the 89 months the bank stood in its band, at most one mortgage was written in each;
   - from m265 to m315 the homes stood at 2,209 while the doubled-up households rose from 305 to 515.

   Migration's arrivals are capped by the placement's room (`Migration`, line 880;
   `FamilyModel.roomLeft`, the `Room` record at line 1914). The doubling valve takes up to
   `totalHouseholds/2 + 1`. So the frozen supply shows as doubling up first, and as households with
   no home only once the valve's room is spent. CU2's path spent it; today's does not.

2. **In the band, the insured door has no room at all.**
   `BusinessDebtManager.canFundMortgage` (line 2162) refuses on
   `insuredRationed && principal > min(capitalRoom(sector), insuredProjectRoom())`. Its helper is now
   `capitalShortFor`, line 2190. `insuredProjectRoom()` (line 2556) is BF1's room to the leverage
   *target*: `Bank.leverageHeadroom()` / `projectLeverageHeadroom()`, lines 5596-5606, clamp
   `equity/leverageTarget - exposure` at 0. So it is 0 anywhere under the target, and no insured
   mortgage is written in the band, where the rule's growth limit (`Bank.lendingGrowthLimit`,
   line 4970, `RATIONED_GROWTH × x/(1−x)`) still allows a share.

   That contradicts BF1's own note (`docs/queue/BF1-notes.md`, lines 215-220): "the leverage rule
   still rations insured mortgages through capitalRoom as before". BF1's bound was written for a bank
   *above* its target (lines 445-449). The uninsured `projectHeadroom()` (line 5587) has the same
   reach. This finding is queued as MC2; this batch does not change it (see Calls).

3. **Even with the band's share the landlords build nothing: a block is lumpy.**
   A probe of the played fixture, with the band's share restored in `insuredProjectRoom` (since
   reverted), gave these figures:

   | quantity | size |
   |---|---|
   | the share | about 40 a month |
   | one Low-Rise block's loan | about 300 |
   | the till | about 300 |
   | a block | 530-600 |
   | the landlords' debt | about 4,090 |

   HN1's door halving stops at one block, and a refused block is not bought, because the till holds
   only the down payment. It wrote 0 mortgages in 120 months, so mending finding 2 alone mends
   nothing here.

4. **The owners were asked for the down payment only.**
   `Game.consider()`'s equity block (line 7079) asked for `min(down, down − held)`. While the bank is
   short, the down payment buys nothing: the loan it is the down payment on never comes.

## What was built

- **`BusinessDebtManager.mortgageShortOfCapital(sector, shortfall)`** (line 2207) asks the door
  before the order goes to it: whether it would refuse that mortgage for the bank's capital and for
  nothing else. It records no refusal. It is false when there is nothing to borrow, the lender is
  shut, the landlord is banned from borrowing, or it is past its default point (the review's P2).
  The door's own tests are now the shared `mortgagePastDefaultPoint` (line 2187) and
  `capitalShortFor` (line 2194), so the two cannot drift apart.
- **`BusinessDebtManager.ownersAskOnMortgage(sector, cost, held)`** (line 2224) is what the owners
  are asked for on an order bought on a mortgage:
  - normally, what the till lacks on the down payment, as before;
  - while `mortgageShortOfCapital` says short, what it lacks on the whole order.

  The mortgage it asks about is the one the door is asked for once the down payment is in: the
  cost less the larger of the till and the down payment (the review's P1).
- **`Game.consider()`**, in the banner "THE OWNERS FIRST, ...AND THE WHOLE ORDER WHILE THE BANK IS
  SHORT" (line 7079), takes its ask from `ownersAskOnMortgage` (line 7099):
  - while the desk says the bank has no capital for the mortgage the order needs, the landlord's
    owners are asked for all the till lacks on the order, not only its down payment;
  - `Equity.offer` sells the new shares, households first and the world for the rest, behind its
    `quoteSupportsIssue` gate as before;
  - HN1's halving then finds an order the till covers outright.

  It is still a building bought with the owners' money, never an overdraft, and the bank's books and
  its rules are untouched.
- **`CreditCrunchCheck`**, registered after `HousingDoorCheck` in `AllChecks`, with its line in
  `docs/tree.md`:
  - **Section 1** builds the desk by hand: one branch, its cash and foreign deposits set so the
    leverage ratio stands in the band, then over the target. The test asked first agrees with the door
    in the band, over the target within the room, and over the target past it. It says no while the
    risk weights bind, while the lender is shut, with nothing to borrow, for a banned landlord and
    for one past its default point. It records nothing.
  - **Section 1b** checks the owners' ask:
    - in the band it is the whole order;
    - for a banned landlord, or one the risk weights leave unrationed, it is the down payment;
    - over the target, with the till empty and the bank's room (1,190,476) between the door's loan
      (1,091,746) and the loan on the cost less the till (1,298,135), it is the down payment only,
      and the door writes the mortgage on the rest.
  - **Sections 2-3** play the ensemble's seed 13, AUTO2 at 2%, to m150, from `GameFiles.scratch`.
    At m150 the landlords' homes are retired, blocks first and then houses, down to the doubling
    valve's last 12 households of room. From then on the bank is held by its restore setters
    (`setCash`), halfway up its leverage band, for 120 months.
  - The fixture asserts that every month was played, rationed on the leverage ratio and in the band,
    that the landlords asked while the bank was short, and that the cut left room for 12 households
    and less than one house more.
  - With the mend, the owners put money in, homes rise, and no month has more than one household
    with no home (LongPlaytest's watch).
  - Where the owners' issue fell short of the order, the door trimmed for the bank's capital what
    equity did not cover (7 trims), and left no such month marked as held for capital (see
    "HousingDoorCheck's capital trim" below).

## The harness, mend and control

The control is main's classes with only the new `BusinessDebtManager` and the harness compiled in,
that is, without the change to `Game`.

| run | the owners put in | homes | most with no home | result |
|---|---|---|---|---|
| mend (`target/checks/CreditCrunchCheck.log`) | 13,431 | 497 → 1,522 | 0.00 | green |
| control (`target/probe/control3.txt`) | 4,398 | 497 → 689 | 5.45 (m252) | red |

In the control:
- homes stood at 497 from m151 to m252 while the households went 930 → 959;
- from m228 on, one block was asked for every month, the bank was short, and none was raised;
- households with no home climbed from 0.3 at m234 to 5.45 at m252, before HN1's halving found
  blocks the till covered.

The control ran against the first form of the cut premise ("room ≤ MARGIN"), which both runs failed
on 12.1. The premise now reads "room ≥ MARGIN and less than one house more", which 12.1 meets. The
control's remaining red is the watch's.

## The review (REV-MC1, on 3251de681): not yet, one P1 and one P2, both mended

The review found no money finding: the issue is booked once, and the holders who buy owe nothing.
It judged the harness's crunch, the control and the call sound.

- **P1.** The pre-test was asked on `planCost − held`, the till before the down payment the same
  order raises. The door is asked on `planCost − max(held, down)`.
  - With the till under the down payment, the test priced a loan up to about 1.18 times the door's.
  - So a bank with room between the two (over its target, where BF1's room is finite, or with a
    little room in a keep-going month) read short.
  - The owners of a landlord *not* in a crunch were then asked for the whole order.
  - Mended in `ownersAskOnMortgage`, which asks on the door's own shortfall. The harness's order
    watch now asks on it too.
- **P2.** The pre-test skipped the door's two tests before the capital's: the ban, and the default
  point. A banned landlord, or one past its default point, got the whole-order ask in a crunch.
  - Mended: it is false for both, so it means exactly "refused for capital", as the review
    recommended.
- **Before the mends.** The harness was compiled against the desk as it stood before them (the old
  pre-test, and the ask on the cost less the till; `target/probe/before.txt`). Five lines fail:
  - the banned landlord's no shortage;
  - the past-default landlord's;
  - the banned landlord's owners asked for the down payment;
  - the down payment only, with the room between the two loans;
  - the door's mortgage on the rest.

  The mended desk passes them all.
- **The played crunch is unchanged by the mends:** the owners put in 13,431, homes went 497 → 1,522,
  and the most households with no home was 0.00. The order watch's own count of short months now
  reads 56 of the 91 months with an ask, where the till-priced test read 120 of 120.

## HousingDoorCheck's capital trim: caused in CreditCrunchCheck, its own line restated to MC1's law

After the review's mends, `HousingDoorCheck` (HN1's door, AUTO2 seed 12, months 431-900) failed one
fixture line: "the door trimmed an order it first refused for the bank's capital". It cut 9 orders,
each trimmed to what its own funds put down, and none was refused first for capital. It was green
at 3251de681, on a path that differs from the first changed equity ask on.

- **Why the trim is rare now.** MC1 asks the owners for the whole order whenever the door would
  refuse its mortgage for the bank's capital.
  - A trim first refused for capital now needs an issue that falls short of the order: the
    households' savings past their cushion, the world's dividend test, or the `quoteSupportsIssue`
    gate.
  - The path is live, but on seed 12's path every such issue raised enough for the bank's room.
- **First passed by name, then mended (the orchestrator, 17:15):** "A red the batch's own code
  causes is mended" (contract 2.3). The known-reds row is gone from the ship file.
- **No lever in HousingDoorCheck's own city.** Its fixture has no hand that holds the owners' issue
  short:
  - `Equity` and `Exchange` have no setters;
  - `tradeShares()` (month step 118, line 14141) strikes fair value again before
    `runPrivateInvestment()` (line 14853), so a quote set between months never reaches the order;
  - moving the households' savings would break its section 5 cash identity;
  - holding its bank in its band would replace HN1's natural path.
- ★ **The trim is caused where the issue falls short by construction: `CreditCrunchCheck` 3.** Its
  bank is held in its band (no insured room at all) and its city is small, so the owners raise only
  part of each order. The door then trims for capital what equity did not cover.
  - It made 7 such trims, for example m181: "raised 243; Built 1 Low-Rise Apartments (trimmed from
    2 - the bank is short of capital for a mortgage, so what its own funds buy outright)".
  - None was left marked as held for capital.
  - Both are asserted there: the trim (fixture), and REV-HN1-1's mark.
- ★ **HousingDoorCheck's line is restated to MC1's law, on its own path, reading the ask actually made.**
  - Its order watch reads the till before the owners' issue: the till at the door less the month's
    equity raised.
  - On that till, the desk's capital pre-test on the door's loan says which orders the door would have
    refused for capital.
  - For each of those, it reads what `consider()` offered the owners: `Equity.getOfferedThisMonth`,
    which adds the ask itself (Equity.java line 512).

  Section 3 asserts:
  - **(fixture)** the window holds such an order put to the owners;
  - **(the law)** each was offered whole: what the till lacked on the order, not only its down
    payment. An order behind a quote that supported no issue is counted apart.
  - **(REV-HN1-1, kept)** no month the door trimmed is left marked as held for capital.
- **REV-MC1-2's P1.** The first restatement (on f6e7c4750) asked the desk's own pre-test again at
  the door's hook, so it could not fail with `consider()` back to asking only the down payment. The
  line now reads the offer, and it fails there:
  - with `ownersAskOnMortgage` returning only the down-payment ask (`target/probe/hd-downonly.txt`),
    27 orders the door would have refused for capital were offered less than the till lacked, and
    none whole;
  - the first is m431: an order of 1,386, the till 419, offered 0;
  - 4 capital trims came back, and the line goes red: "...and each was offered to the owners whole".
- **REV-MC1-2's P3.** `CreditCrunchCheck` 3 counts a capital trim as one the owners' issue fell
  short of only when they put money in that month. A trim with nothing put in (a shut quote, or an
  issue nobody took) is counted apart: 7 of the first kind and 0 of the second.
- **HousingDoorCheck green, exit 0, at the mend.** 9 orders the door would have refused for capital,
  all 9 offered whole and none behind a shut quote. The first is m465: an order of 2,850, the till
  78, offered 2,773, put in 1,987; "Built 2 Low-Rise Apartments on an insured mortgage".

## Trace

The trace moves: `Game.consider()` asks for more equity whenever the desk is short of capital for a
landlord's mortgage. The ensemble against `docs/ensemble/0.9.167.md` was handed to the gate box at
3251de681 and cancelled before its runs started, when the review's mends moved the model. It ran
again at be09ec6ee, the mended model merged with CPB2 (0.9.170), on the gate box: 80 of 80 runs,
recorded as `docs/ensemble/MC1.md`. REV-MC1-2's mends since then touch only the two harnesses, so
they leave the trace where it is.

- **Batch verdict PASS:** hard criteria 10 of 10, 24 of 26 in all, none missing or short.
- **M1 and F1:** 0 months with a failed money audit and 0 of 16 runs with a LongPlaytest finding,
  in every policy.
- **Changed verdicts, all info:**
  - AUTO2 T2 and ZERO_SLACK T2 go FAIL to PASS: AUTO2's final population is 1,992,305 against
    0.9.167's 1,820,652.
  - AUTO2 A1 goes PASS to FAIL, 10 of 16 seeds in the 1-3% band where 12 are asked for. The median
    is 2.86% (2.88% at 0.9.170) and the highest seed 3.19% (3.31%).
- **A1 seed by seed, against CPB2's runs of 0.9.170 on the box:** seeds 3 (2.65% to 3.01%), 6
  (2.96% to 3.10%) and 7 (2.94% to 3.12%) go just over 3%, and seed 14 comes back in (3.17% to
  2.94%). Seeds 5, 8 and 9 were over before and still are.

## Checks

| check | result |
|---|---|
| `./batch-check.sh --fast` | compile, maps, Stale (0 firm) and tree green, before and after the merge of origin/main |
| `CreditCrunchCheck` | green before and after each merge and the review's mends (with the capital trim's two lines); 5 red on the desk before the mends |
| `MortgageCheck` | green, before and after the review's mends |
| `HousingDoorCheck` | green at 3251de681; after the review's mends one fixture line red, then restated to MC1's law (above): green; REV-MC1-2's line red on the down-payment-only control, green at the mend |
| `BankFailureCheck` | green, before and after the review's mends |
| `sh tools/pick.sh origin/main HEAD` | ALL (231 harnesses), because `AllChecks.java` changed; it says "review not needed" |

The mends mail says "review: needed - money" all the same, because the change sizes an equity issue
on the bank's capital rule.

## Calls

- ★ **The landlords' own equity is the rule that gives.**
  - The leverage rule is the bank's prudential floor, and the crunch is a real one.
  - The landlords are listed, so their owners can put in what the bank cannot lend. That is an issue
    of shares the city already makes (`Equity.offer`), only sized to the order rather than to a down
    payment that buys nothing while the loan is refused.
  - It touches neither the bank's books nor its rules, so no other borrower's credit moves.
  - It mends the lumpy block (finding 3), which a share of the bank's room cannot.
  - It reaches only a mortgage the desk would refuse for capital, so the trace moves only where the
    crunch already binds.
- **Not taken: the bank's capital rebuilt in a crunch.** It changes the bank's equity for every
  borrower in the city, not the landlords' door. It is a money rule with its own audit. It would also
  hide the crunch the leverage rule exists to cause.
- **Not taken: the insured mortgage under the leverage rule.**
  - Tried, and reverted: an in-band share in `insuredProjectRoom` and Bank's
    `leverageFloorHeadroom`.
  - It wrote no mortgage in the played fixture, because the share (about 40) is smaller than one
    block's loan (about 300).
  - BF1's band-zero (finding 2) is real but is a separate correction to BF1's rule, so it is queued
    as MC2.
- ★ **The fixture cuts homes to the doubling valve's last 12 households** rather than waiting for a
  played city to spend its valve.
  - Without the cut the fixture never strands anyone, with or without the mend: migration stops at
    the room, and HN1 buys a block about every 50 months.
  - 12 is enough room that a mended city builds before anyone is left out, and little enough that a
    month without homes shows within the 120 months.
- **The bank is held by `setCash`, the restore setter,** recomputed six times a month so equity and
  exposure settle on the ratio. The money audit is not asserted in this harness: the hold is a
  fixture's hand on the bank, as `BankFailureCheck`'s setters are.

## For Jerus

Nothing beyond the queued row: MC2, BF1's band share for insured (and uninsured) growth.
