# MR1 — Manufacturing's re-order after a bleed sale

Base: `ded8345fc` (0.9.210, save format 66). The AQ1 measurements in the
brief predate the 1900 default (R1g) and QR1's operating-loss distress gate;
MR1 re-measures PopulationCheck's modern fixture and today's default first.
`MR1-trace`, a Codex diagnostic sub-session in a detached worktree at that
base, owns those traces and reports findings here; no model edits delegated.
MemWatch committed memory before its start: 30.3% (2026-11-26 07:57 local).

## WHY and the call

The real-options account supports waiting for evidence after a loss, rather
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

★ Manufacturing remembers its own recently closed running results,
excluding plant-sale scrap receipts as AQ1 does. A loss holds expansion until
a full year of consecutive months covering operating costs; interest alone
does not restart that clock. This is a model dial for sustained recovery,
not an empirical claim that every real firm waits exactly a year. A new
sector may enter before it has a record. The existing new-works estimate,
staffing, land and credit tests still decide what a recovered sector orders.
The count belongs in Manufacturing's existing SectorState extras hook, which
already travels through DataSave and Game's load path. No shared save field
or money-transfer formula is needed.

The implementation uses `Manufacturing.afterBank()` so it observes the final
statement once, at the same monthly close as the existing operating-loss
history. The capped count is initially satisfied for first entry, reset on
any negative running result, and advanced on nonnegative running results.
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
covers both recent losses and a bleed, which already requires losses.
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
overshoot law, with an added positive-target guard. The existing controlled
dear-city fixture sets rent burden to twice the model's target, keeps jobs
and no home draw, and proves the actual target is lower than the unpulled
draw; MR1 adds that effect as an explicit assertion. The fixture is not moved
and the model's affordability/crime/senior-care effects are not hidden.
Printed figures distinguish raw draw and actual target. The pending probe
will identify this particular played city's pulls without guessing from the
old senior-care comment. PopulationCheck is rerun after this correction.

## Checks and measurements

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

### Final acceptance verdict

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

★ The brief explicitly owes the final model an 80-run ensemble after the
stranger's SHIP. Keep that full comparison, against the newest baseline,
rather than add another exploratory laptop prefix. A proposed first-400
comparison was prepared but not run: the helper's original turn ended
before its late authorization arrived, and I revoked its automatic resume
before starting final named checks. The source-only preparation also found
that `LongPlaytest.TARGET_MONTHS` changes watch windows and skip truncation,
so shortening the horizon would not prove an identical input path.
No unchanged-final-default claim is made from the baseline measurements.
The early recovery hold can change staffing refusals/WantedPosts even when
the old planner was already refusing to build; the ensemble is the required
final trajectory evidence. Review's SHIP is the go for it, per the brief's
final ordering, with main merged first. No extra ensemble request is owed.
- `tools/batch-id.sh MR1` initially flags the remote branch because the
  orchestrator pre-pushed it at exactly origin/main's spine; no MR1 merge or
  published record exists. The first batch commit will make the remote tip
  distinct. This is branch setup, not reuse of a shipped batch id.

## Outstanding

Request stranger's money/saves review on the pushed provisional ship file,
including the PopulationCheck law correction. After its SHIP, merge main
and run the brief's ensemble on the
final pushed model against the newest baseline (currently
`docs/ensemble/0.9.210.md`). The pick is not ALL, so no dry run is owed.
