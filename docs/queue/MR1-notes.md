# MR1 — Manufacturing's re-order after a bleed sale

Base: `ded8345fc` (0.9.210, save format 66). The AQ1 measurements in the
brief predate the 1900 default (R1g) and QR1's operating-loss distress gate;
MR1 re-measures PopulationCheck's modern fixture and today's default first.
`MR1-trace`, a Codex diagnostic sub-session in a detached worktree at that
base, owns those traces and reports findings here; no model edits delegated.
MemWatch committed memory before its start: 30.3% (2026-11-26 07:57 local).

## WHY and the call

The real-options account supports waiting for evidence after sustained distress, rather
than buying sunk capacity on one newly favourable estimate. Pindyck,
[Irreversibility, Uncertainty, and Investment (1990)](https://www.nber.org/papers/w3307),
explains why a firm can value postponement while learning about prices and
costs. His [capacity-choice model (1986)](https://www.nber.org/papers/w1980)
jointly values installed capacity, utilization and future expansion under
uncertainty. Bansak, Morin and Starr,
[Technology, Capital Spending, and Capacity Utilization (2004), pp. 4–6](https://www.federalreserve.gov/pubs/feds/2004/200430/200430pap.pdf),
distinguish meeting a temporary shock with existing plant from a lasting
change warranting the fixed costs and construction lag of new capacity.
These sources support the direction of the rule, not a universal number of
months, nor a rule that divestiture always prohibits investment.
The U.S. Treasury's [2009 GM restructuring account](https://home.treasury.gov/news/press-releases/tg179)
gives a concrete loss-driven capacity reduction: a smaller plant footprint
and lower costs aimed to restore viability while leaving room for a market
rebound. It does not describe a ban on productive technology investment.

★ Manufacturing arms recovery only when its operating-loss streak reaches
`BusinessInvestment.DISTRESS_LOSS_MONTHS` while it is lent (AQ1's runway).
It then holds expansion until a full year of consecutive months covering
operating costs, excluding plant-sale scrap receipts as AQ1 does. An active
recovery resets on a running loss; a short loss spell before it is armed or
after it is completed leaves ordering unchanged. Interest alone does not
restart that clock. This is a model dial for sustained recovery,
not an empirical claim that every real firm waits exactly a year. A new
sector may enter before it has a record. The existing new-works estimate,
staffing, land and credit tests still decide what a recovered sector orders.
The count belongs in Manufacturing's existing SectorState extras hook, which
already travels through DataSave and Game's load path. No shared save field
or money-transfer formula is needed.

The implementation uses `Manufacturing.afterBank()` so it observes the final
statement once, at the same monthly close as the existing operating-loss
history. The dial means no hold is active, including first entry. Once the
lent loss runway arms it, the count resets on a negative running result and
advances on nonnegative running results. `afterBank()` precedes
`recordOperatingResult()`: on a losing close it reads the existing streak
through the previous month, so the trigger is that streak at runway minus
one, plus this losing close, with principal above zero. This phase dependency
is documented in the method; no second loss history or saved flag is added.
An empty business can therefore re-enter after the same window; it is not
locked out forever after a complete liquidation. Opening new plant does not
erase this history. `plan()` returns a no-order decision with the count
until the dial is met. It does not change the lender or retirement rules.

The scalar is saved and restored by Manufacturing's `SectorState.extras`,
already carried in DataSave and restored by Game; it is not a time series and
needs no HistorySave field. Missing history remains unknown in an older
save, rather than reconstructed from closing balances. No saved value's
meaning changes, no conversion is added and no format bump is owed under
the old-saves rule. Reset restores first-entry status. The count is not
money and does not redenominate.
The source review also checked full liquidation: EconomyManager banks every
sector each month, with no active-plant filter, and Sector always invokes
`afterBank()`. The empty Manufacturing in the base population trace closes
zero operating results from m41 through m122, so the recovery clock can
advance after its last plant goes.

Other calls: the bleed's current predicate forgets the problem at the first
paying month (AQ1's measured one-month delay), so it is not a recovery rule.
A sale-only timer would add a retirement event to shared code and could
expire while the business still lost money; the observed running record
retains the sustained lent losses after a bleed even when the immediate
loss predicate has cleared. The first submission's any-one-loss activation
was rejected by REV-MR1; a self-funded short spell must not start recovery.
Per-plant profitability or an additional utilization cutoff would require a
different allocation of the sector's books. This batch keeps the sector as
the accounting unit and changes Manufacturing alone, as its row asks.
No conservation, save or population threshold is relaxed.

## PopulationCheck's current finding and law (★)

On this base the old accepted Manufacturing re-order is absent in the modern
fixture, but the decade line still fails. MR1-trace found that its denominator
is the reconstructed **unpulled** jobs/homes draw, 556, whereas the target
migration actually used is 445: 431 residents are 96.9% of that target.
`play()` already returns `Migration.getLastTarget()` as tuple entry 11 and
the overshoot assertion already uses it. The raw reconstruction also reads
the adult share after arrivals, rather than at the actual calculation.

Reality first: households need not fill toward the same nominal employment
opportunity when housing costs discourage them. Ganong and Shoag,
[Why Has Regional Income Convergence in the U.S. Declined? (2017)](https://www.nber.org/papers/w23609),
model and document how rising housing prices deter low-skill migration.
This supports retaining affordability's effect, not forcing extra arrivals
to satisfy an unpulled target. It does not calibrate the game's pulls.

★ Keep the exact played fixture, the one-month inertia assertion, the 80%
decade share, the independent raw ratio and every overshoot/conservation
check. Apply the decade share to the same actual target already used by the
overshoot law, with an added positive-target guard. REV-MR1 accepted this
law and rejected the first added dear-city assertion as redundant. `play()`
now returns the played city's own job/home draws, their job/home pulls and
affordability pull (tuple entries 12–16), all from the same migration call.
The played-city assertion reconciles target = job draw × jobs pull + home
draw × homes pull and requires its actual affordability pull below one.
The fixture is not moved, and no controlled-fixture difference is claimed
as evidence about the played city. Printed figures show those inputs.

## Checks and measurements

### REV-MR1 mends (after NOT SHIP on 62df472c3)

- **P1, trigger:** the original any-one-loss activation was too broad. It
  is replaced by AQ1's existing operating-loss runway while lent. The twelve
  paying months are unchanged. Inactive recovery leaves a short spell alone;
  active recovery resets on a running loss and disarms after twelve paying
  closes. A fixture compares the actual order (build/template/quantity/reason)
  before and throughout a short unlent spell, then throughout lent losses
  below the runway; all must match. The last runway loss arms the old bleed
  fixture. One isolated loss after completed recovery also leaves its order
  unchanged.
- **P1, played-city evidence:** remove the redundant added dear-city effect
  line. Carry and reconcile the actual played city's draws/pulls, requiring
  its recorded affordability pull below one. Correct the comment that had
  attributed a controlled-fixture difference to the played city.
- **P2, loaded controls:** set the planner's land snapshot after each load.
  Both loaded holds must explicitly name `operating recovery:`; after the
  next paying close the loaded plant must actually order. This removes the
  former no-land vacuity.
- First revised `--fast`: 0, compile 58 s, Stale 0 firm findings. Revised
  ManufacturingCheck: 0 (`target/MR1-review-ManufacturingCheck.txt`), including
  the new unchanged-order controls and both loaded hold reasons.
- Revised PopulationCheck: 0 (`target/MR1-review-PopulationCheck.txt`), with
  the played-city reconciliation and affordability premise.

### Revised-path re-measurement (31f796999)

`MR1-remeasure`, a fresh diagnostic Codex session (MemWatch commit 45.8%
before start), ran exactly two named invocations, serially, with the revised
Manufacturing model on the unchanged base. All temporary instrumentation
and generated files were restored to detached `ded8345fc`; all its jobs ended.

Modern PopulationCheck fixture, through m122: first recovery arm is **m40**,
the previous saved operating-loss count 23 plus this losing close reaching
24. Principal is 486.653699484 at m39, 408.864466743 at the sale callback,
zero at m40 close; its only Shop is sold then. Recovery is 0 at m40, 1 at
m41, through 11 at m51, and 12 at m52. There are exactly twelve recovery
holds, m40–51, **zero holds before qualifying distress**, and none later.
No accepted Manufacturing re-order follows through m122. The day-zero
houses/shops/depots, Real Estate hold and FX pin were unchanged.

The played m121 tuple `[target, jobDraw, homeDraw, jobsPull, homesPull,
affordability]` is `[444.89474827672313, 231.12461002178142, 325,
0.7870857574205539, 0.8091687985821512, 0.7870857574205539]`. Its reconciled
target is 444.89474827672313, residual **0.0**; population is 431, end-state
raw draw 556.23199550631 versus migration-call raw draw 556.1246100217815.
Thus the new played-city assertion establishes the actual rent effect at
the actual calculation, rather than borrowing a controlled-fixture result.
Evidence: helper `target/MR1-remeasure-population-flow.csv` and related files.

1900 default, observed through m400 with `TARGET_MONTHS=2400` and the same
full-run input schedule: findings 0, **no recovery holds**, recovery remains
12 throughout, including the twelve negative closes m314–325 with principal
0. Eight baseline/model CSVs match in **2,796 rows / 207,906 common state and
flow cells**, zero differences. The custom trace matches all 400 monthly
states and 73 events after excluding only the new recovery scalar. The full
baseline CSV SHA256 was verified unchanged. This named LongPlaytest run is
a diagnostic prefix, **not a full LongPlaytest acceptance pass**.

Inference, separately from observation: the complete base run to m2405 has
no later operating-loss window. The revised guard is inactive on the sole
loss spell and cannot alter the identical path thereafter; no new full
2400-month laptop baseline/model run was made. The requested full ensemble
still follows the reviewer's SHIP.

### Revised acceptance verdict (31f796999)

The review mends were checked with `--fast` (0, compile 58 s), followed by
ManufacturingCheck 0 and PopulationCheck 0. Those two fresh results were
reused when completing the unchanged pick; they were not replayed merely
to put the names into one invocation. All remaining named calls ran serially
with the shared Earth/world files and `-Dcbs.parallel=1`.

| picked harness | exit |
|---|---:|
| ArrivalMixCheck | 0 |
| BooksCheck | 0 |
| BuildingDataCheck | 0 |
| CopperRefiningCheck | 3, registered FS8 only |
| GdpCheck | 0 |
| ManufacturingCheck | 0 |
| MaterialsCheck | 0 |
| PopulationCheck | 0 |
| TimingCheck | 0 |

The additional save checks also pass: SaveFileCheck 0, ReloadCheck 0,
ReadPathCheck 0. Evidence is `target/MR1-review-<Harness>.txt`, with the
fresh Copper log copied to `target/MR1-review-CopperRefiningCheck-harness.log`.
No source, conservation, save assertion or time limit was changed during
these checks. The revised source diff was reviewed by the builder against
62df472c3; all three requested mends are present. All acceptance and
diagnostic jobs have exited; helpers have ended. Final post-check fetch
still names main `ded8345fc`, so no merge or repeat is required.

`sh tools/pick.sh origin/main HEAD` remains nine of 257 harnesses. Its
automatic money/saves/threading review line is still "not needed"; the
brief's manual money/saves review requirement and the same REV-MR1
reviewer's re-read take precedence. The provisional marker remains.

The original measurements and first-submission check record below remain
historical evidence. New-path measurements and the revised pick follow the
mends before the second review request.

- Base `./batch-check.sh --fast`: green; compile 60 s, maps unchanged,
  Stale 0 firm findings, tree 0 changed.
- Base `./batch-check.sh PopulationCheck`: exit 3, the existing decade red,
  431 people against the assertion target 556 (77%; its current model target
  is 445). The old m73/two-shop sequence is gone: a Shop is ordered m6,
  stands m15, loses on operations m17–40 and is sold at m40, the 24th loss.
  No Manufacturing plant or site remains through m122. Works proposals from
  m108 are refused by bank capital. The current red must be traced on its
  own cause, not attributed to AQ1's old measurements.
- Corrected PopulationCheck on the **unchanged main model**: exit 0, full
  normal harness after the helper's attribution observer, using only the
  PopulationCheck source from `c9381343e` on `ded8345fc`. At m121 the played
  city has 431 residents; end-state raw draw 556.231996, actual calculation's
  pre-pull draw 556.124610 (jobs 231.124610, homes 325), rent/jobs pull
  0.787085757, crime pull 1, senior pull 1.028056715, homes pull 0.809168799;
  the actual target is 444.894748. Last completion is a Boutique at m85,
  none m86–122: the red is not a late Manufacturing capacity jump. The
  observer's source changes were restored. No OX2/SW1/MKR1/PB1 mend is taken.
- Base 1900 default, real LongPlaytest via the observer: exit 0, city phase
  2,292 s, ends m2405 because final player actions pass the requested 2400.
  Manufacturing has zero distress sales; its only 12 operating-loss months
  are m314–325, with no principal during that spell. At m2400: population
  6,864,565, jobs 2,937,353; Manufacturing holds 159 Shops, 841 Machine Works,
  309 Fabrication Works, 2 Wire & Cable Works, 4 sites and no principal.
  **Neither current played path reproduces AQ1's old bleed-then-reorder of
  remaining plant. The caused two-shop bleed fixture carries MR1's finding.**
- Runner correction: MR1-trace launched the original target-only observer
  directly before my follow-up made the batch-check wrapper explicit. It
  delegates the real LongPlaytest and is a mechanism probe, not a claimed
  batch-check pass. It remains the sole diagnostic JVM until completion;
  subsequent diagnostics and all acceptance checks use one named
  `./batch-check.sh` invocation at a time. No timeout is extended.
- Population law correction `--fast`: green, compile 61 s, Stale 0 firm
  findings, maps regenerated. Full PopulationCheck remains to be rerun.
- Final save-boundary harness additions `--fast`: green, compile 61 s,
  Stale 0 firm findings. The finding now loads a real scratch-disk save at
  zero recovery immediately after the bleed and again one month below the
  dial, then checks the next close reaches the same boundary.
- First finding attempt (planner behaviour unchanged): invalid red proof.
  Initial entry and the final order were already held because the new
  fixture set owned land but omitted the planner's monthly land snapshot.
  MR1-trace diagnosed that prerequisite; the fixture now sets the snapshot
  explicitly and reports the first order's reason. The extended harness
  also asserts staffing, estimated earnings and cost so a prerequisite hold
  cannot make the recovery assertions vacuous.
- Valid red proof on the unchanged base planner: the two files from
  `f23016b4c` add only the dial and finding; `batch-check.sh ManufacturingCheck`
  exits 4 with precisely the four recovery holds red (loss-making market,
  first paying month, scrap-inflated statement, paying months below the
  dial). First entry, the lent one-of-two Shop sale, remaining one Shop,
  at-dial expansion and interest-only recovery all pass. The earlier land
  setup failure is absent; this is the finding red on main behaviour.
- Recovery model `--fast`: green, compile 61 s, Stale 0 firm findings.
- `sh tools/pick.sh origin/main HEAD` at `1c5d2e8c6`: BuildingDataCheck,
  CopperRefiningCheck, GdpCheck, ManufacturingCheck, MaterialsCheck,
  TimingCheck (6 of 257). Its automatic review line says no review is needed;
  the brief requires a stranger's money review and the diff also persists
  sector history, so MR1 explicitly requests money **and saves** review.
  With this pick no dry run is owed. The final pick is recomputed before
  mailing mends.
- Final model acceptance checks so far: ManufacturingCheck 0 (including
  both disk-save boundaries), PopulationCheck 0, BuildingDataCheck 0.
- MR1-log, a fresh read-only Codex log helper (MemWatch commit 34.4% before
  start), confirmed CopperRefiningCheck is actively playing its seed-1
  LongPlaytest child, not stalled. At 08:58:49 AST it was at m720/2400,
  CPU advancing, no child exception/timeout/red. The parent harness already
  has the exact registered FS8 Rotterdam order-cut red (0 months; []),
  `target/checks/CopperRefiningCheck.log:113`; no exemption or owner is changed.
  The helper started no JVM and ended with no jobs. Its child-log evidence
  is the scratch `copperrefiningcheck-rodmills.../seed1.log` it reported by bus.
- CopperRefiningCheck hit AllChecks' **outer 1,740-second wall guard** at
  09:21:13 AST (1,740.1 s), before its child's own 28-minute guard. The only
  additional real failure is `FAIL: timed out after 1740 s`; `exit 1` is
  synthetic. No other model-law failure appears in retained output. The
  still-advancing child was last observed at m1800, CPU 1,226.33 s, at
  09:12:31; its scratch log was removed when the tree ended. All PIDs ended.
  FS8 explicitly owns this Copper section-11 played audit and moving it to
  nightly (`docs/studies/flaky-suite.md:423–428`, by-harness row 182).
  ★ Name this laptop wall-time class to FS8 in the provisional ship file,
  exactly as the standing moved-city/load rule says; change no limit or
  model/check premise and add no blanket `exit 1` exemption. Preserve
  `target/MR1-CopperRefiningCheck-suite.txt` and
  `target/MR1-CopperRefiningCheck-harness.log` before the next named run.
  Canonical kept-log judgment, `CBS_REJUDGE=1 sh ./batch-check.sh --judge
  target/MR1-CopperRefiningCheck-suite.txt`, reports **known red only** with
  this row and the pre-existing Rotterdam FS8 row. No harness is replayed.
  The captured canonical judgment exits **3**.
  The provisional registry check also confirms the old MR1 PopulationCheck
  exemption is retired (`! PopulationCheck ...`).

### First-submission acceptance verdict (62df472c3; superseded by REV-MR1)

On model `3968aa6e0` over `ded8345fc`, run strictly serial through Git Bash
with the shared Earth/world files and `-Dcbs.parallel=1`:

| check | exit | evidence |
|---|---:|---|
| final `--fast` | 0 | `target/MR1-final-fast.txt`; compile 61 s, Stale 0 firm, maps regenerated |
| ManufacturingCheck | 0 | `target/MR1-ManufacturingCheck.txt` |
| PopulationCheck | 0 | `target/MR1-PopulationCheck.txt` |
| BuildingDataCheck | 0 | `target/MR1-BuildingDataCheck.txt` |
| ArrivalMixCheck | 0 | `target/MR1-ArrivalMixCheck.txt` |
| BooksCheck | 0 | `target/MR1-BooksCheck.txt` |
| GdpCheck | 0 | `target/MR1-GdpCheck.txt` |
| MaterialsCheck | 0 | `target/MR1-MaterialsCheck.txt` |
| CopperRefiningCheck, kept-log rejudge | 3 | `target/MR1-CopperRefiningCheck-rejudge.txt`; exact FS8 classes above, original suite and log retained |
| TimingCheck | 0 | `target/MR1-TimingCheck.txt` |
| SaveFileCheck | 0 | `target/MR1-SaveFileCheck.txt` |
| ReloadCheck | 0 | `target/MR1-ReloadCheck.txt` |
| ReadPathCheck | 0 | `target/MR1-ReadPathCheck.txt` |

All acceptance JVMs have exited. The final source diff was reviewed by the
builder: Manufacturing alone changes gameplay; no shared month, lender,
transfer, conservation or save-check code changes. The two helpers were
read-only apart from restored diagnostic instrumentation; none authored a
model diff. No save slot outside scratch files was written. No published
record or VERSION was edited. The final post-check fetch still names
`ded8345fc` as main, so no main merge or repeated checks are needed.

## Final trajectory verification and review sequence

REV-MR1 on `9b20bdc0d` returned **SHIP** (2026-11-26): all three requested
mends accepted, money and saves sound. Its nonblocking P2 remains recorded:
`afterBank()` reads principal before `settleBusinessCredit()`. If the first
loan arrives on exactly the 24th operating-loss close and the next close
pays, the bleed can sell while the recovery record never arms. Current
measured cities do not take that edge (the modern city already owes 486.65
at m39; the default's twelve losses are unlent).

★ Leave that P2 as expressly permitted. `runPrivateInvestment()` runs
retirement and insolvency before calling the expansion planners; a sale can
repay the first loan, so adding a check in `plan()` alone is not complete.
A reliable observation belongs after credit settles and before retirement
can erase the principal. Wiring that event/phase and adding an independent
late-first-loan fixture while preserving the existing pre-runway controls
is more than the permitted few lines/one fixture line. Do not add mutation
to the nominal retirement-measure query to avoid that wiring. No source
change is made after the reviewed model for this optional point.

SHIP authorizes the ensemble and final marker removal without another dry
run: the pick remains nine, not ALL. `origin/main` was fetched and merged
before the ensemble; it remained `ded8345fc`.

★ The brief explicitly owes the final model an 80-run ensemble after the
reviewer's SHIP, against `docs/ensemble/0.9.210.md` (or a newer main baseline
if one ships). The first submission prepared but did not run a default
prefix comparison. REV-MR1 requested re-measurement of the corrected
activation, so the revised-prefix evidence above now supplies it. It keeps
`TARGET_MONTHS=2400`: reducing that value would change the watch windows
and skip truncation and would not preserve the input schedule.

The revised default prefix is identical and never arms recovery. The full
baseline has no later qualifying loss window; subsequent equivalence is an
inference from that inactive guard, not a new full-model LongPlaytest pass.
The explicit ensemble remains owed and will check all seeds, including
ones where qualifying distress does occur. Review's SHIP is its go, with
main merged and the final model pushed first; no extra ensemble question
is owed. The revised pick remains nine harnesses, so no dry run is owed.
- `tools/batch-id.sh MR1` initially flags the remote branch because the
  orchestrator pre-pushed it at exactly origin/main's spine; no MR1 merge or
  published record exists. The first batch commit will make the remote tip
  distinct. This is branch setup, not reuse of a shipped batch id.

### AP5 merged while the ensemble was running

AP5 shipped as 0.9.211 while the box was playing MR1. Merge main through
`a21ccb679` (AP5 plus queue-only records); no model source conflicts. The
three conflicts were generated `docs/dials.md`, `docs/harnesses.md` and
`docs/map/README.md`: take main, then regenerate them with `--fast`.
MR1's three source files remain byte-identical to reviewed `9b20bdc0d`.

★ Retain the ensemble's kept gameplay snapshot `84234b69c` after this
runtime-only merge. AP5's final notes, lines 131-150, record all eight
default CSVs md5-identical to `ded8345fc`, including after its final
review mends `bf889e983`; its held-versus-bare labour readings also agree.
AP5 changes labour caches, not their formulas or order, and does not touch
Manufacturing. This is not a claim about the merged model's timing. The
newest ensemble baseline remains `docs/ensemble/0.9.210.md`. The contract's
repeat after two trace-moving batches does not apply to this exact cache.

Post-merge serial validation: `--fast` **0** (compile 60 s; Stale 0 firm),
ManufacturingCheck **0**, PopulationCheck **0**, in
`target/MR1-post-AP5-{fast,ManufacturingCheck,PopulationCheck}.txt` and
`target/MR1-post-AP5-verdicts.txt`. The helper read the logs; no helper JVM
or model edit was used. Generated harness listings retain their generator's
empty numbered comment lines; the MR1 source diff has no whitespace errors.


## Final acceptance

The authorized box ensemble completed **80/80**, with no missing or short
runs. **M1 and F1 pass in every policy, 10/10 hard criteria**, as on 0.9.210;
there are no money-audit failures or LongPlaytest findings. Its kept files
are `target/ensemble/MR1/`, with the launcher log
`target/MR1-ensemble.txt`. The actual classes snapshot is
`84234b69c400aebf92f2640029b53acd92f608fe`, over `ded8345fc`, played on
gatebox with OpenJDK 21.0.13 and the box's
`/home/jerus/cbs/main/data/earth-2.bin` and `world-1.bin`. The committed
report is [MR1's baseline](../ensemble/MR1.md), scored against
[0.9.210](../ensemble/0.9.210.md). The version token alone replaces the
report heading and introductory model version; the actual snapshot,
data, host and JSON provenance are preserved.

The box first queued MR1 behind AP5's dry run. The first eight runs began
about 11:38 AST; AP5's publication gate then took priority, after which
MR1 resumed the same snapshot. No laptop fallback was used; MR1 started
only the permitted serial fast and named-harness JVMs on this laptop.
AP5's runtime-only merge and the post-merge checks are
recorded above; the ensemble is a gameplay comparison, not an AP5 timing
measurement.

Scores beside the required baseline (definitions, thresholds and all
unscored readings remain in the two linked reports):

All 26 criterion verdicts are unchanged. Some range endpoints move:
AUTO2's minimum final population rises from 2,985,818 to 3,585,360, and
ZERO_SLACK's from 1,728,034 to 3,231,842; their medians are unchanged.
No per-seed mechanism is inferred from these aggregate readings.

- 0.9.210: criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
- MR1: criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
- MR1: batch verdict PASS (hard criteria passed 10/10)

| policy / criterion | 0.9.210: reading; verdict | MR1: reading; verdict |
|---|---|---|
| AUTO2 / A1 | 2.96% [2.35% to 3.22%]; FAIL | 2.96% [2.35% to 3.22%]; FAIL (10/16 in band) |
| AUTO2 / A2 | 13 [7 to 22]; FAIL | 13 [7 to 22]; FAIL |
| AUTO2 / A3 | 0 [0 to 3]; 99.98% [96.34% to 100.00%]; PASS | 0 [0 to 3]; 99.98% [96.34% to 100.00%]; PASS |
| ZERO_SLACK / S1 | 2.45% [1.24% to 4.41%]; PASS | 2.45% [1.24% to 4.41%]; PASS (16/16 seeds within) |
| ZERO_HOT / H2 | -0.72% [-4.15% to 4.23%]; PASS | -0.72% [-4.10% to 4.23%]; PASS (15/16 below) |
| SHOCK / K1 | 18.25% [14.68% to 21.64%]; PASS | 18.25% [14.68% to 21.18%]; PASS |
| SHOCK / K2 | 2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.97%]; PASS | 2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.61%]; PASS |
| HIGH / R1 | -3.48% [-5.81% to -1.74%]; PASS | -3.48% [-5.52% to -1.74%]; PASS |
| HIGH / R2 | 1.42% [-0.01% to 5.98%]; PASS | 1.42% [-0.01% to 5.82%]; PASS |
| HIGH / R3 | -3.14% [-5.65% to -2.03%]; PASS | -3.14% [-7.22% to -2.03%]; PASS (0 above +1 pt) |
| ALL / T1 | 16/16 (m464 [441 to 531]); PASS | 16/16 (m464 [441 to 531]); PASS |
| AUTO2 / T2 | 7,395,916 [2,985,818 to 9,226,936]; PASS | 7,395,916 [3,585,360 to 9,226,936]; PASS |
| AUTO2 / M1 | 0 (0); PASS | 0 (0); PASS |
| AUTO2 / F1 | 0/16 []; PASS | 0/16 []; PASS |
| ZERO_SLACK / T2 | 5,303,088 [1,728,034 to 5,742,799]; PASS | 5,303,088 [3,231,842 to 5,742,799]; PASS |
| ZERO_SLACK / M1 | 0 (0); PASS | 0 (0); PASS |
| ZERO_SLACK / F1 | 0/16 []; PASS | 0/16 []; PASS |
| ZERO_HOT / T2 | 21,333 [9,488 to 31,953]; PASS | 21,333 [9,488 to 31,953]; PASS |
| ZERO_HOT / M1 | 0 (0); PASS | 0 (0); PASS |
| ZERO_HOT / F1 | 0/16 []; PASS | 0/16 []; PASS |
| SHOCK / T2 | 684,858 [382,125 to 1,312,702]; PASS | 684,858 [395,974 to 1,312,702]; PASS |
| SHOCK / M1 | 0 (0); PASS | 0 (0); PASS |
| SHOCK / F1 | 0/16 []; PASS | 0/16 []; PASS |
| HIGH / T2 | 245,141 [88,689 to 397,454]; PASS | 245,141 [90,281 to 397,454]; PASS |
| HIGH / M1 | 0 (0); PASS | 0 (0); PASS |
| HIGH / F1 | 0/16 []; PASS | 0/16 []; PASS |
| ALL / pairing | 64/64; PASS | 64/64; PASS |

H1 remains retired and excluded from the count. Informational scores do
not waive a law or alter the M1/F1 ship rule. PopulationCheck's original
80% bound is green against the target its played city actually used; the
old MR1 known-red row is absent from the ship file. Copper's scoped FS8
timeout row remains, with the traced evidence above.

The reviewer SHIP is the readiness go. Committing the baseline adds
EnsembleCheck to the original nine picked harnesses: the final pick is
**ten**, not ALL. The named EnsembleCheck passes **0**, with its kept log
`target/MR1-EnsembleCheck-final.txt` and exit file alongside. No dry run
is owed. The provisional marker is removed last in its own
commit, after these records are pushed; the publisher runs the fresh pick
on the main it merges onto. The optional late-first-loan P2 remains
documented above as expressly permitted, with its reason for deferral.
