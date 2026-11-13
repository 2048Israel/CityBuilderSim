# AW2 â€” Allocated land kept through a normal load

## Finding and scope

RT3's policy investigation recorded WG2 at m2391 with 113,813,544 sq ft
allocated, 520,069,911.267 sq ft free, and 873,813,544 sq ft in its standing
and site footprint. The missing 760,000,000 sq ft has a precise source:
KeptCheck injects 20,000 of each of its first three templates through
BuildingManager.addStack(), without asking LandManager to allocate them
(lines 759, 760 and 834 on main 5a87e102). Those templates are House,
Studio Apartments and Convenience Store. Their catalogue plots total
8,000 + 25,000 + 5,000 sq ft; 20,000 times that is exactly the gap. The same
injection explains the nearly identical gaps in RT3's other fixtures.

LandManager.allocatedSqFt is the live counter. allocate()/allocateOn()
increase it, release() decreases it and clamps it at zero. Free ground and
construction gates read it. Game.readTheSave() instead replaced it with
BuildingManager.getTotalLandFootprint(), including standing buildings,
sites, shells and demolitions, less paving overlap. DataSave did not carry
the counter. Consequently the ordinary load changed this fixture's rules.

The production addStack callers were audited. The founding bank and yard
allocate their ground; municipal processBuildOrder() and private buildFor()
allocate before placing; paveRoads() intentionally reuses the standing
gravel road's ground, then releases only the difference on completion.
readTheSave() restores buildings before restoring land; loadBuildings() is
an older restore helper with no callers. No real-play placement path found
adds new buildings without allocating their ground. The unallocated sites
in this incident are KeptCheck's injection, which this batch leaves intact.
For cities built through normal player/model paths, the derived footprint
was already the right allocation. The visible repair concerns harness
cities and the WG2 save Jerus played, not evidence of ordinary play losing
its building allocations.

## Implementation and calls

â˜… Save the live counter as boxed DataSave.landAllocatedSqFt, writing it
in Game's snapshot and restoring it on the ordinary load path before the
works, runs, prepared ground and zoning. The counter is the source of truth
for continuing this city; silently changing it during loading is not a
repair to how the fixture was founded.

â˜… A missing key retains the previous footprint reconstruction; explicit
zero remains zero. No format bump: the new field has the existing load
behaviour as its absent-key default. No conversion or guessed subtraction
is added. Existing saves without the key cannot recover the old live
counter from their JSON alone. Slot 10 was inspected read-only: build
0.9.91, format 51, m2391, owned 633,883,455.2669102 sq ft, no allocation
key. This batch does not rewrite that save or any real slot 1â€“10.
Its SHA-256 is
`39df46892aeabfd60cccfaf9bd852f5e74bd4dacbb7cbe62fcd91f4273b80c24`
matching RT3's recorded prefix.

â˜… Keep the regression in SaveFileCheck, using GameFiles.scratch(). It
injects the same three sets of sites, proves the footprint exceeds owned
land while the live counter admits a house, and compares allocation, free
land, footprint, price, next quote and actual successful construction on
both sides of an ordinary save/load. It also checks absent-key fallback
and explicit zero. One normal month precedes injection so the founding
money constants have settled before construction invoices are compared;
the injection and allocation discrepancy are unchanged.

Codex builds this whole batch under WC1's explicit builder contract. No
KeptCheck fixture or HA1 conversion file is changed. VERSION, QUEUE,
changelog and gate records are left to the publisher.

## Verification

- Initial `./batch-check.sh --fast`: exit 0, compile/maps/Stale/tree green.
- Regression against the old loader: eight failed assertions, including
  760,069,000 vs 69,000 sq ft allocated, zero vs 2,982,568.603137 sq ft free,
  construction refusal after load, and explicit zero reconstructed as a
  footprint. Log: `target/checks/aw2-regression-before.log`.
- The first post-fix regression restored land and construction permission;
  two quote/treasury comparisons exposed the unplayed founding month's
  unsettled money constants. The fixture now plays one month before the
  same injection, as described above. The first named check used that
  earlier fixture and is superseded by the final runs below.
- The settled fixture passes all its assertions in the isolated runner:
  `target/checks/aw2-regression-settled.log`, exit 0.
- An overlapping local fast compile removed classes while the first named
  check was finishing, causing ReloadCheck class-loading errors and that
  run's maps/Stale launch failures. This was a local check invocation error,
  not a model red; subsequent batch checks run sequentially. Its log was
  `target/checks/aw2-run.txt` before the final picked run replaced it.

Final checks and retained dry run will be recorded here before readiness.

### Exact WG2 scratch-copy diagnostic

Copied slot 10's save/history/map, where present, to GameFiles.scratch();
the source paths were read only. Its old-key ordinary load reproduces
873,813,544 sq ft allocated and zero free. In the scratch city only, seeded
RT3's independently recorded 113,813,544 sq ft live counter (not derivable
from that JSON), wrote a new save and loaded it normally. Allocation and
free ground match exactly, including 520,069,911.267 sq ft free before the
order. Both the source city and its ordinary reload successfully order one
House and retain equal free ground afterwards. Exit 0, no failed
assertions; `target/checks/aw2-wg2-copy.log`. This validates new saves of
that live state; it does not claim to repair the old slot's missing field.

### Default trace and ensemble decision

Compared the final model with exact main `5a87e102`: compiled main's Game
and DataSave into an override class directory and used the otherwise
identical model/harness classes. Both default LongPlaytests exit 0. All
eight CSV traces are byte-identical: month, borrowing, housing, population,
bank, bonds, construction and labour. Main's source/harness are unchanged
by AW2 apart from the two overridden model classes and the footprint
method's prose. The comparison plus SHA-256 hashes is
`target/checks/aw2-traces-result.txt`; runs are `aw2-main.log` and
`aw2-branch.log` in the same folder. Run wrapper: `target/aw2-traces.sh`.
The byte-identical traces meet EN1: no ensemble run is required. The
unchanged stored baseline is `docs/ensemble/0.9.98.md`.
