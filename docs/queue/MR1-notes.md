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
- Runner correction: MR1-trace launched the original target-only observer
  directly before my follow-up made the batch-check wrapper explicit. It
  delegates the real LongPlaytest and is a mechanism probe, not a claimed
  batch-check pass. It remains the sole diagnostic JVM until completion;
  subsequent diagnostics and all acceptance checks use one named
  `./batch-check.sh` invocation at a time. No timeout is extended.
- Population law correction `--fast`: green, compile 61 s, Stale 0 firm
  findings, maps regenerated. Full PopulationCheck remains to be rerun.
- First finding attempt (planner behaviour unchanged): invalid red proof.
  Initial entry and the final order were already held because the new
  fixture set owned land but omitted the planner's monthly land snapshot.
  MR1-trace diagnosed that prerequisite; the fixture now sets the snapshot
  explicitly and reports the first order's reason. The extended harness
  also asserts staffing, estimated earnings and cost so a prerequisite hold
  cannot make the recovery assertions vacuous.
- Recovery model `--fast`: green, compile 61 s, Stale 0 firm findings.
- `sh tools/pick.sh origin/main HEAD` at `1c5d2e8c6`: BuildingDataCheck,
  CopperRefiningCheck, GdpCheck, ManufacturingCheck, MaterialsCheck,
  TimingCheck (6 of 257). Its automatic review line says no review is needed;
  the brief requires a stranger's money review and the diff also persists
  sector history, so MR1 explicitly requests money **and saves** review.
  With this pick no dry run is owed. The final pick is recomputed before
  mailing mends.
- `tools/batch-id.sh MR1` initially flags the remote branch because the
  orchestrator pre-pushed it at exactly origin/main's spine; no MR1 merge or
  published record exists. The first batch commit will make the remote tip
  distinct. This is branch setup, not reuse of a shipped batch id.

## Outstanding

Finish the base default trace and Population attribution; prove the finding
red before the gate; run save/reload verification and named checks, then
request stranger's money/saves review. If the default trace moves, the
ensemble is run on the final pushed model after the review's SHIP, against
the newest baseline (currently `docs/ensemble/0.9.210.md`).
