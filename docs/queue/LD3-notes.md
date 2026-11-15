# LD3 — LD2's wage on in every harness

Built from main `8a67da4d` (QB1, 0.9.106), on 2026-11-14. The brief is
`docs/queue/LD2-notes.md`, section 6. Every check names the PC's
`earth-2.bin` and `world-1.bin`. No entry in the starting known-reds registry
names LD2 or LD3. DeathRecordCheck's orphan-section law already belongs to
OX1, and stays distinct from the fixture work here.

## What changed

The seven remaining harness assignments to `WantedPosts.carryKeptOut` are
removed. `Game.repriceLabour()` always takes the supply against existing
posts, and the switch is removed from WantedPosts. ScaleCheck already uses
LD2's wage following ST3, with QB1's month-225 fixture.

CM2's replay-copy section and its files are untouched.

## Checks and fixture decisions

The initial focused run enables the wage before further fixture changes.
`target/ld3-first-checks/` retains its logs. All eight harnesses finish in
291 seconds, exit 3, with only the existing OX1 assertion red. None of the
seven passing harnesses reports a missed observation.

| Harness | Reached condition with LD2's wage | Repair already on main |
|---|---|---|
| HoldersCheck 6 | Household, bank and central-bank paper round-trip, and the next settle and coupons agree | GW1 reapplies the Heavy Industry hold to the twin, `4adf2e12` |
| DeskCheck 8 | Gain line worth 512.3 against 517.7 carried, loss line worth 522.8 against 517.7 carried. The closing gains are +5.4 and -5.1 | BA1 searches the sale dial and the final cut between the two coupons, and FloatLinkCheck holds its fixture's sectors |
| AutoBuildCheck 4 | Both loaded and live cities place 17 orders during 36 months with identical cash, debt, ground, people, buildings and order logs, worst drift zero | R0b repairs this same 36-month no-order fixture by holding its death causes from founding. WN3 supplies the literal-world cities. The current path also includes HW1's works rewrite and QB1's quoted borrowing |
| CurveCheck 7, 9 | At m39 the 1-month is 1.0000%, 1-year 2.0612%, 10-year 3.3914%. The hand-held dial is exactly two points below its rule and the issue-at-curve laws execute | WG1's going-rate minimum, WN3-R's world band and GW1's held Manufacturing and Wood Products |
| OrderSearchCheck 2 | The natural copy judges 138 orders, largest 6,318 against 16 slices, with 31 below-slice searches. With controlled boundaries it records 33. The twin records 17 | EM1's existing m500 copy, BA1's build-ahead path, and BF1's explicit controlled boundaries and retained natural counts |
| CurrencyRegimeCheck 9 | Both pegged cities buy US$1,365k and owe D$1,331.2119k, then both settle that exact bill with their recorded payer | BA1 searches the depot count, XR5 makes the peg buy its pressure, and GW1 repairs the draw-path peg fixture |
| ScaleCheck 6 | QB1's m225 own-town fixture satisfies the scale, price, unemployment, policy and register laws in all six played months | ST3 already removed this harness's wage opt-out. QB1 re-picked only its month from 172 to 225 |
| DeathRecordCheck 3 | The original m48 fixture reaches the known rehire fault. A second pair at m60 causes the same closing and checks its same-month excess in every one of 24 months | LD3 adds the paired measurement while retaining the m48 hard law for OX1 |

LD3 restores hard fixture assertions for AutoBuildCheck's post-load orders,
CurveCheck's steep curve in both consumers, and CurrencyRegimeCheck's
purchase. Their former Observation guards could skip these requested
conditions. The conditions and all dependent laws retain their bounds.

### The original HoldersCheck save gap

The exact original model `524af54a` was unpacked under `target/ld2/`, compiled
with the PC's two data files, and its unmodified wage-on fixture run through
section 6. The diagnostic leaves the model untouched and stops after that
section. It reproduces LD2's figures exactly.

| At the next settle | Live city | Loaded twin |
|---|---|---|
| Households' paper purchase | 55.037203 | 0.002938 |
| Coupons to households | 0.276167 | 0.125011 |

The live city calls `holdSector(HEAVY_INDUSTRY)` before its first month.
The old loaded twin never reapplies that hold. On the same model and fixture,
adding only that call after the load makes both comparisons pass to 1e-9
and both audits close. The apparent missing state is
`BusinessInvestment.held`, the harness-only set of held sectors.
GW1 already supplies that exact call on main. LD3 adds a hard assertion
that both twins hold the sector before the next settle.

★ Keep the harness-only hold out of player saves. The brief's presumed
missing game-save field proves to be this fixture hold. Consequently there
is no DataSave field or loader addition, no format bump, and no invented
save conversion. SaveFileCheck and ReadPathCheck are run with the final
changes. The existing save coverage continues to check real game state.
Orchestrator agreed on cbs-bus at 22.55 on 2026-11-14 that the hold parity
assertion is the correct mend and this field stays unsaved.
Evidence is in `target/ld3-historical-focused.log`,
`target/ld3-hold.log` and `target/ld3-candidates.log`. Copying each of the
bank's, households', Game's, Exchange's, EconomyManager's and national
accounts' primitive scalars back into the old twin leaves the failure.
Reapplying only the held sector fixes it.

### DeathRecordCheck and OX1

Unchanged main `8a67da4d` Game, WantedPosts and DeathRecordCheck were compiled
into `target/main-deaths/` ahead of the unchanged support classes. Its old
wage-off harness still fails the same orphan-section assertion, with the
closed town peaking at 19.1 and its open twin at 14.1 when the adults return
to work. `target/ld3-main-deaths.log` retains the full run. With the wage on,
the m48 peaks are 19.07 and 13.25, and the closed town's adults at home fall
to zero at the spike. This is the registered OX1 rehire fault. Its original
fixture and exact hard assertion are retained.

★ Add the paired layoff measurement at m60, preserving all buildings, the
closing, the 24-month window and the one-orphan allowance. The wave has
60 adults at home and 28.640 children with them. Under the old rule it would
orphan 28.640 children against 13.245 before, so it still exceeds the
original doubling premise. The greatest simultaneous excess against the
open twin is 0.923 across all 24 months. It compares each month with its
twin's same month. The m48 OX1 case remains separately visible.

The month scan is in `target/ld3-deaths.log`. Earlier candidates m36, m42,
m48 and m54 have maximum simultaneous excesses 11.941, 11.938, 5.820 and
9.854. M60 is the first sampled month satisfying the complete paired law.
M72 also holds at 0.891, while m84 onward loses the original doubling
premise. No failed premise was relaxed.

★ Keep the fixtures already repaired on main whenever they still cause their
conditions. LD2's old red figures describe its earlier model and are checked
against the current path before another seed, month or city is selected.

## Verification

The initial `./batch-check.sh --fast` passes compilation, maps and tree checks,
with zero firm Stale findings. The final focused run finishes seven harnesses in 257 seconds, exit 3.
HoldersCheck, AutoBuildCheck, CurveCheck, CurrencyRegimeCheck, SaveFileCheck
and ReadPathCheck pass. DeathRecordCheck passes all new paired layoff laws
and retains only OX1's original red. Its new window records 28.640 children
with 60 laid-off adults and maximum same-month excess 0.923. The builder's
post-load window places 17 orders in both twins with zero drift.

`target/checks/ld3-mended.txt` and `target/checks/suite.txt` retain that run.
The final local `./batch-check.sh --pick` selects ALL, 202 harnesses,
including its direct nightly picks, and compiles successfully. Orchestrator
requested its cancellation at 23.02 on 2026-11-14 because CM2's full dry run
was already running and concurrent suites had timed KeptCheck out the night
before. The local run is incomplete. Only LD3's verified process tree was
ended, including its detached MSYS batch-check and JVM children.

Per that direction, the coordinated fresh-clone dry run will execute the
same full pick for LD3. The request `LD3 dry run?` was sent on cbs-bus, and
LD3 waits for the go before starting it.

No process script, check machinery or standing rule changes. The process
one-in, one-out count is zero added and zero removed.
