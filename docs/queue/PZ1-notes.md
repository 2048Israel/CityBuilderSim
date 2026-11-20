# PZ1 - PrizeCheck 12: one lab a field a month, caused again (notes)

The brief is `docs/queue/PZ1.md`. Harness only: no model, save or screen
line changes, so the batch is untagged and gates `--no-tag`.

## 1. The bisect

| commit | what | PrizeCheck, run singly |
|---|---|---|
| 17f8a4c05 | OP1, 0.9.161 | green: all 282 ok (detached worktree `.claude/worktrees/pz1-bisect-17f8a4c05`, removed after) |
| f733ce75d | GB1 | not run: `tools/`, `CLAUDE.md` and docs only, no source |
| 919c1fa41 | BO1-S, 0.9.162 | red, 1 FAILED of 282: the line. Run on this branch's base 42881af6a, whose source is 919c1fa41's (the diff is `QUEUE.md` and the brief) |

The first red merge is BO1-S. Both runs print the same OBSERVED line, section
20's "not reached" (a stake of NaN), which is not counted and not PZ1's.

## 2. The cause

The line read **none**, not two: `found: ENGINEERING 0+0 MEDICINE 0+0 ...`.
The model cannot order two labs of a field in a month: `Game.considerLabs`
(Game.java:4047) calls `orderLab` once a field a month, `orderLab`
(Game.java:4086) builds one (`buildFor(owners, lab, 1)`), and no planner
offers a lab (PrizeCheck 11). The red was the rule's other side.

The month's word under `Game.LABS_SLOT`, put in the failing line's detail:
"Holding: the owners put up $962,726 of the $3.3M Medical Research Lab needs";
month 174, the order's quote cost 973.31 and stake 2,303.63 (thousands), the
till 947.35 after the raise, the quote supporting an issue.

- `Game.orderLab`: the till is short of cost plus stake, so `equity.offer`
  asks the owners for the rest, and the order holds at
  `if (till.getCash() < cost)` (Game.java:4115). Never a loan, as designed.
- `Equity.offer`: the home raise is `households.subscribe` (Equity.java:515);
  the world takes the rest only on its yield test (Equity.java:526-533), and
  here it declined.
- `HouseholdBalance.subscribe` (HouseholdBalance.java:4849-4850): "The owner
  line takes no offering (BO1): its stakes dilute." BO1-S set
  `OwnerHousehold.IN_CITIES` true (OwnerHousehold.java:91; Game's initializer
  switches the line on, Game.java:16238), so the founders' shares and their
  dividends belong to the owner cell, and the trading households' savings
  covered 963 of the 3,277 the lab needed.

**Proved on the same tree** by a probe in `target/` (not committed): the
section's medical village built twice, `HouseholdBalance.setOwnerLine` set
right after `DiscoveryCheck.cityWithSchools`. Line off: month 174, MEDICINE
0+1, "Built Medical Research Lab - the purse pays for Insulin", 4,482.8
raised (cost 1,220.6, stake 3,262.2). Line on: the harness's figures exactly,
$962,726 raised, the order held.

## 3. What changed

- ★1 **The fixture restated, not the model mended.** The hold is the model's
  rule (owners first, never a loan) and BO1's (the owner line takes no
  offering), not a fault. The line's premise is a village that wants two
  labs, can staff two (`handMore` already hands the heads for two) and could
  pay for two; with the line on it could pay for none in that month, so the
  fixture no longer caused its condition. Not a known-reds name, as the
  brief says: PrizeCheck is nightly-only.
- The medical village is handed BF1's buffer, the one section 12's first
  three villages already get, scaled to the labs the model wants:
  `2 x wanted x (cost + stake) / HouseholdBalance.SHARE_OF_EXCESS`, credited to
  the owners who could take up an offering (subscribe's own test). A new
  fixture line checks the credit, the cost and the stake.
- `PrizeCheck.creditOwners()`: the BF1 loop as a helper; the first part calls
  it, unchanged in effect.
- The failing line's detail names the month's word under `LABS_SLOT`, as the
  section's first order line does, so a hold says why.
- Not changed, for the record: whether the owner families should take up a
  lab's raise is BO1's design ("its stakes dilute"), left as it is; no row.

## 4. The checks

- PrizeCheck, singly: all 283 ok (282 and the new fixture line). The first
  month orders one lab, the second the second (wanted 2: 2 labs), so the
  next line's "one more" branch is now caused as well, where before it passed
  on 0 then 1.
- The pick (`sh tools/pick.sh origin/main HEAD`): PrizeCheck,
  ResearchViewCheck, TimingCheck; review: not needed (no money, saves or
  threading). ResearchViewCheck and TimingCheck green, each run singly.
  `./batch-check.sh --fast` green.
- The default trace: unchanged; no model line moved, so no ensemble.
- No dry run: the pick is not ALL and the publisher is untouched.

## 5. Calls

- ★1 above: restate the fixture (the brief's second branch).
- The changelog heading takes the contract's untagged form (5.2, as GB1's),
  `### PZ1 — title — date, ...`, with no `{next}`.
- The bisect's logs are in this worktree's `target/` only
  (`prize-17f8a4c05.log`, `prize-main.txt`); no dry run, so no kept gate log.
