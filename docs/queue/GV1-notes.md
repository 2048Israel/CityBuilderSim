# GV1 - the government's wealth, and the test player's January ({next}, 2026-11-10)

The brief is GV1's row in `QUEUE.md`, with GV2 folded in, and
`docs/studies/spec-wealth.md` sections 2.7 and 3.2. Branch `batch/GV1`
starts from ee6130b1. Written and checked by Codex; the orchestrator gates
and merges it. Nothing here changes a live player's policy automatically.

## Decisions taken, with their recommendations

- The five estimated bands are the brief's: below 0.1 broke, 0.1 to below
  0.5 thin, 0.5 through 2 sound, above 2 through 5 rich, above 5 hoarding.
  Recommendation taken: keep the exact 0.5 and 2 endpoints inside the
  test player's quiet band; its rule says *under* and *past* those bounds.
- Recommendation taken: require twelve recorded revenue months and positive
  revenue. An unfinished year is not a forecast; zero receipts do not turn
  the government infinitely rich. Screens explain an unavailable ratio and
  notices and the test player retain their state without acting on it.
- The stock is treasury cash plus the whole fund at its existing marks plus
  the vault at the current local-money rate, less the city's outstanding
  principal. Fund cash is counted once. Existing getters own every figure.
- Recommendation taken: show an illustrative one-percentage-point cut to
  the largest of the four tax families, and a five-point rise to pensions
  or EI, whichever moves the larger monthly payment to current recipients.
  Detached `TaxPolicy` copies and `PolicyPreview` provide the amounts; the
  existing controls still require the player to choose and apply a policy.
- Recommendation taken: buyback advice opens an existing CD4 ticket, never
  deals by itself. Cash must exceed the debt, and the actual offered price
  must yield more than idle cash (zero). Discount bills qualify despite no
  coupon. The line says coupons stopped, not an invented monthly payment
  on a bill. Affordable partial purchases retain CD4's unsettled-piece rule.
- Recommendation taken: retain the current wealth band in a live notice's
  existing saved key, not another save field. The first measured year is a
  read baseline. Crossing settles it and raises one unread notice; the
  live baseline survives the two-year cull. No save-format change.
- GV2 is the player's rule only, shared through `GovernmentPlayer` and
  `LongPlaytest.runPlayer` by the playtest, ensemble and WealthRun. The scripted
  income/property-tax moves and WealthRun's separate prototype are removed.
- Recommendation taken: act after each successfully completed January.
  The prototype's December modulo preceded January's newly opening policy
  gates; this order lets the current January's existing setters apply. The
  first eligible January is month 13. Each January is considered once per
  city, even across long skips; loading resumes with the next played month.
- The rich-side steps are the brief's: profit tax down five points, sales
  down two, pension and EI replacement up five, withdrawal to its maximum.
  Recommendation taken: the prototype's poor-side ceilings (profit 30%,
  sales 15%) and pension floor (30%); EI's existing default is its floor,
  and withdrawal falls one literal dial step toward zero. A lever already
  beyond that player's bound is left there rather than moved the wrong way.
  Historical gates and model caps still hold. No dividend or new lever.

## Verification and the review

Checks name the PC's earth-2 and world-1 by absolute C:/ paths and use
scratch cities only. The first named run passed GovernmentViewCheck,
GovernmentPlayerCheck, InboxCheck, BuybackCheck and the 2,400-month
LongPlaytest. The reviewed discount-bill fixture also passed separately.

The Government overview was rendered at 1456 by 900 for an actual played
city, a hoarding city with all three doors active, an unfinished revenue
year and a full year with no receipts. The card and its amounts fit without
clipping. Clicking the three links opened the buyback ticket, tax page and
pensions page. A repeated draw made no new nodes and reused 278; the kept
and freshly built page signatures matched. Evidence is in this worktree's
`target/checks/GV1-*.png` and `GV1-visual.log`.

The January player changes the default trace, as its two-year harness
demonstrates with two actual tax moves across a scratch save/load. The
required ensemble is running on the final production model at bf20a92b;
the snapshot's dirty files are harnesses, registrations, notes and maps.
The pre-score `./batch-check.sh --pick` exited 0: eleven of 200 registered
harnesses passed in 132 seconds, maps regenerated, zero firm Stale
findings. Selected: DiscoveryCheck, EnsembleCheck, GovernmentPlayerCheck,
GovernmentViewCheck, InboxCheck, LandmassCheck, LongPlaytest,
PlaytestFixCheck, PolicyDatesCheck, PrizeCheck and TimingCheck. The latter
checks the timing machinery; no full suite or `--timing` run was made.
WealthRun's standalone source also compiled against the completed model.
The review's synchronization flag is the weak per-city review map's lock;
this batch starts no threads. Source whitespace and LF checks passed.
After storing the ensemble score, the final `--pick` again exited 0:
all eleven selected harnesses passed in 70 seconds, zero firm Stale
findings, and regeneration left no generated map changes.

The premium fixture initially assumed 1.01 times face was short of the
whole quote; it was not. It now restores the deliberately stale market
after the purity test and places cash strictly between the quoted whole
price and the larger of face and the quoted quarter. In the passing run:
whole 1006.631, quarter 251.658, face 1000, cash 1003.316. Both original
assertions stand: the premise occurs and advice offers an affordable part.
Only the fixture changed after the ensemble snapshot, not production code.

## The ensemble, and GV1-E left for review

`docs/ensemble/GV1.md` is the complete score and summary: all 80 runs
finished, none missing or short, 12.9 minutes, 20 of 26 criteria passed
against main's 0.9.89 score of 24/26. Every policy's money audit and
playtest-finding criterion passed; no run reported an accounting finding.
AUTO2 A1 passed (median inflation 2.64%, 13/16 seeds in band), and every
AUTO2 seed reached 40,000 people. Main's A2 and A3 remain red and worsen:
inflation spells reach 55 months and deflation spells 17.

Four criteria newly fail, and are not restated:

| criterion | this batch | main / requirement |
|---|---|---|
| ZERO_HOT H2, recovery after restoring autopilot | 10/16 seeds below their zero-rate excess; median -0.14 points versus twin | at least 12/16; main 15/16 |
| AUTO2 T2, median final population | 268,306 | at least 75% of main's 618,792 |
| ZERO_SLACK T2 | 173,386 | at least 75% of main's 409,599 |
| SHOCK T2 | 23,388 | at least 75% of main's 34,660 |

**Recommendation taken:** keep the requested rule, preserve these findings
and leave calibration as GV1-E in Pending for the gate/Jerus to review.
Do not alter an ensemble threshold or quietly substitute the old player's
taxes. This is a material economic regression, not an all-green ensemble.
A separate review of the rule against section 3.2 found no caller or
algorithm mismatch. Compared with section 2.7's prototype, the specified
stock now includes marked holdings and the vault less debt, EI also rises,
the poor-side withdrawal steps down instead of resetting to Norway's rule,
and January gates apply before the move. Its older measured city sizes are
therefore not a reproduction target. The score alone does not identify
which channel caused the smaller towns. A calibration study should read
the wealth band, each lever family and its recipient flows by year,
especially the three failing policies, before choosing a change. No
calibration or unrelated household, migration or monetary fix is included.

Review caught the zero-coupon/yield distinction and the premium-price
partial-buyback case; neither weakens the existing dealing rules. The
original `Game.quoteBuyback` still reprices as before; its factored
`quoteBuybackAtRest` lets advice read the same auction without changing the
curve or interest. No production money flow was changed for GV1's screens.

For the screen check: Government > Overview, the wealth line and its
component tooltip; partial-year and no-revenue text; the three doors,
including a discount bill and paper with the central bank holding a share.
The figures must update in place without moving the scroll.

## Second gate send-back, fixture runner (2026-11-10)

MapCheck, KeptCheck and FoodProcessingCheck were run on detached main
cd7aac62 and the submitted GV1 model. Main exited 3. GV1 exited 4 and
reproduced all five assertions sent back, plus two further Map assertions
and four further Kept assertions. The added January rule ran inside the
borrowed fixture runner, changing the policy supplied by those harnesses.
Its split profit/sales cuts broke FoodProcessingCheck's restore premise;
its tax cuts and fund withdrawal also changed the Map and Kept cities.
The advice's TaxPolicy copies remain detached.

The shared monthly loop now has an explicit `runPlayer` entry point used
by LongPlaytest's actual player, Ensemble and WealthRun. Their January
decision stays after the same completed month. Borrowed `run` calls keep
their supplied policy. GovernmentPlayerCheck now exercises both paths,
including a rich fixture crossing January with every lever unchanged.
The three sent-back harnesses retain every premise and assertion.

The complete 2,400-month AUTO2 seed-0 CSV is byte-identical before and
after the fix and to the accepted ensemble's stored CSV. All three SHA-256
hashes are `8398d0928c88049cb2674216738e4a94258b4fde8edf6942a22c03c81fc641fd`.
The automated players retain the accepted January rule and model path,
so the accepted 20/26 ensemble and GV1-E still apply. The 80-run ensemble
was not rerun. WealthRun's standalone driver compiled successfully.

The final named run of MapCheck, KeptCheck, FoodProcessingCheck,
InboxCheck, GovernmentViewCheck and GovernmentPlayerCheck exited 3.
All GV1-only failures passed. A direct comparison found the same three
Map assertions, one Kept assertion and zero FoodProcessing assertions red
on main and the fixed branch. Maps regenerated and Stale had zero firm
findings. The comparison logs are preserved under
`target/gv1-sendback-trace/`, alongside the before/after trace CSVs.
The final `./batch-check.sh --pick` exited 0, with all eleven selected
harnesses passing in 132 seconds, maps regenerated and zero firm Stale
findings.

The following exact assertion lines were red on main cd7aac62.

```text
MapCheck | a yard laid across a district's buildings moves off it the boxes under it and no others: 53 of the plan before's 590 boxes lie under the yard and the railway, and 60 are not in the plan after (each of the rest stands where it stood) FAIL
MapCheck | ...and the layout watch (LongPlaytest, as the map draws the district) counts them apart and flags none: 53 boxes left a district in the yard's month because a run laid in it lies across them, 7 are counted as moved FAIL
MapCheck | the highways reaching a street: a reserve that appears in a district already built moves off it the boxes it lies across or beside the highways laid with it and no others: 86 of the plan before's 590 boxes lie across, 87 are not in the plan after FAIL
KeptCheck | the BuybackLadder on Close it now: no month draws it again with nothing changed in it (2) FAIL
```
