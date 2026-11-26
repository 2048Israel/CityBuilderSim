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
- First finding attempt (planner behaviour unchanged): invalid red proof.
  Initial entry and the final order were already held because the new
  fixture set owned land but omitted the planner's monthly land snapshot.
  MR1-trace diagnosed that prerequisite; the fixture now sets the snapshot
  explicitly and reports the first order's reason. The extended harness
  also asserts staffing, estimated earnings and cost so a prerequisite hold
  cannot make the recovery assertions vacuous.
- Recovery model `--fast`: green, compile 61 s, Stale 0 firm findings.
- `tools/batch-id.sh MR1` initially flags the remote branch because the
  orchestrator pre-pushed it at exactly origin/main's spine; no MR1 merge or
  published record exists. The first batch commit will make the remote tip
  distinct. This is branch setup, not reuse of a shipped batch id.

## Outstanding

Base measurements, finding harness, final rule, save/reload verification,
named checks and stranger's money review. If the default trace moves, the
ensemble is run on the final pushed model after the review's SHIP, against
the newest baseline (currently `docs/ensemble/0.9.210.md`).
