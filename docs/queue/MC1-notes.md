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

- **`BusinessDebtManager.mortgageShortOfCapital(sector, shortfall)`** (line 2202) asks
  `canFundMortgage`'s capital test before the order goes to the door. It records no refusal, and it
  is false when there is nothing to borrow or the lender is shut. The door's own test is now the
  shared `capitalShortFor`, so the two cannot drift apart.
- **`Game.consider()`**, in the banner "THE OWNERS FIRST, ...AND THE WHOLE ORDER WHILE THE BANK IS
  SHORT" (line 7081):
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
    risk weights bind, while the lender is shut, and with nothing to borrow. It records nothing.
  - **Sections 2-3** play the ensemble's seed 13, AUTO2 at 2%, to m150, from `GameFiles.scratch`.
    At m150 the landlords' homes are retired, blocks first and then houses, down to the doubling
    valve's last 12 households of room. From then on the bank is held by its restore setters
    (`setCash`), halfway up its leverage band, for 120 months.
  - The fixture asserts that every month was played, rationed on the leverage ratio and in the band,
    that the landlords asked while the bank was short, and that the cut left room for 12 households
    and less than one house more.
  - With the mend, the owners put money in, homes rise, and no month has more than one household
    with no home (LongPlaytest's watch).

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

## Trace

The trace moves: `Game.consider()` asks for more equity whenever the desk is short of capital for a
landlord's mortgage. The ensemble against `docs/ensemble/0.9.167.md` (`docs/ensemble/MC1.md`) is
waiting for the orchestrator's go.

## Checks

| check | result |
|---|---|
| `./batch-check.sh --fast` | compile, maps, Stale (0 firm) and tree green, before and after the merge of origin/main |
| `CreditCrunchCheck` | green before and after the merge |
| `MortgageCheck` | green |
| `HousingDoorCheck` | green |
| `BankFailureCheck` | see below |
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
