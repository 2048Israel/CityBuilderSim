# RT1b - RT1 money review follow-ups

Started from main e8ac5a53, on batch/RT1b. No known-reds entries currently
name RT1b or RT1. The scope is the three holder-ledger subtractions and
HoldersCheck 14's measured split; no concurrent batch's source file is edited.

## Implementation and calls

The sectors' buyback/principal ledger and both household principal and
buyback/purchase ledgers use Debt.moved(), the same relative rounding rule
as the paper's holder fields. Cash, income and investing flows retain their
original formulas. No saved field or format changes.

The fixtures cause a full take to leave positive rounding under main's
subtraction, exercise each entry point at today's and founding scales,
retain partial holdings, and bound each cash credit to its price share.
The desk fixture recalls paper through the live Game wiring across two
pieces and compares the transferred face with Bank.getCityBook()'s actual
increase. It also checks the individual pieces, unrelated holders, sector
ledger, cash and pooled money. Verification and mutation evidence follow.

★ Use the existing relative rule, without a new floor or a full-take
shortcut: legitimate remainders stay at both price levels.
★ Invoke the real desk through OutwardInvestment.recall(), rather than
adding a test seam in Game, which R0f is changing concurrently.

## Focused verification

`batch-check.sh --fast`: exit 0; compile, maps, Stale (0 firm) and tree pass.
`batch-check.sh HoldersCheck`: exit 0, all assertions pass, including every
played month's audit. Logs: target/checks/rt1b-fast.txt,
rt1b-holders.txt, and HoldersCheck.log.

The focused section-14 runner is retained under target/rt1b-probes:

- Final code: exit 0 (target/checks/rt1b-floor-final.txt).
- The original e8ac5a53 OutwardInvestment and HouseholdBalance compiled over
  the same fixture classes: exit 6. Full sector principal and buyback takes
  fail at both scales; full cell principal and buyback takes fail at scale 1.
  Partial holdings and every cash/income assertion still pass. This directly
  demonstrates all three requested regressions on main's code, without
  changing a premise (rt1b-floor-main.txt).
- A temporary Game copy divides the desk's per-piece pro-rata transfer by
  two, retaining its bank booking and price. Exit 12; the independent book
  comparison fails on partial and full takes at both scales (0.306423 versus
  0.612846, and 1.225691 versus 2.451382 at scale 1). The tracked Game is
  untouched (rt1b-floor-broken-split.txt).

The existing section-14 boundary checks keep a remainder ten times the
relative floor and snap one at half the floor. The new controls also retain
quarter-take rests and bound each holder's cash and reported flow within
four ulps of its pro-rata price. The cash formulas themselves are unchanged.

The actual `tools/pick.sh` selection on 226500c2 is 39 of 202, not ALL
(the initial estimate sent to the starter was corrected). Its automatic
money-review flag says no; the starter was nevertheless sent the explicit
money review request, `RT1b mends at 226500c21a9fb21b35abb464c884290d7cf0fdcc`.
R0f's dry run was active at that time, so all subsequent work stayed at one
heavy JVM at a time; no full suite or ensemble was run alongside its gate.

The trace baseline is an untouched source archive of e8ac5a53 under
`target/RT1b-main`, compiled independently (exit 0). It is the default
2,400-month playtest, with the same Earth/world inputs and 2 GiB heap as the
candidate. Both emit the eight CSVs at target/checks/rt1b-main-* and
rt1b-final-*; the pair runs serially. This is a default trace comparison,
not a second monetary ensemble baseline. The provisional ship file was
pushed before gate work (a24f6f35).

## Stranger review

The starter reports REV-RT1b-1 on e8ac5a53..226500c2: **SHIP**. Its finding:
all three ledger moves preserve units and cash formulas; the real-desk
fixture compares transferred face with the bank's independently booked
increase. Source remains at the reviewed 226500c2 implementation.

## Default trace

The full main/candidate pair finished: both exit 1 for the same registered
LongPlaytest finding (DM3-R), twelve boxes moving in district (-1,-1) at
m2389. All eight closed CSVs are byte-identical, with hashes retained in
`target/checks/rt1b-trace-comparison.json`. The 524 report lines also match
apart from the overall and layout-watch elapsed times and two narration
clock prefixes (`rt1b-report-diff.txt`, `rt1b-report-comparison.json`).
The two completed direct-Java reports were CP1252; they were decoded and
rewritten as UTF-8 through Git Bash for comparison. CSV bytes were untouched.

The ensemble is skipped under batch.md section 6 because the trace is
unchanged. Every money-audit fixture and all six sidecar reloads pass in both
reports. The selected check is running serially with cbs.parallel=1.

## Final selection and gate handoff

The local `batch-check.sh --pick` selected the same 39 harnesses and ran
serially while R0f's gate was active. Before it completed, the starter
explicitly stopped that duplicate run and granted the dry-run slot
(cbs-bus 20261115-012315-820-orchestrator-b763): the dry run runs the same
selection. Its partial log is target/checks/rt1b-pick.txt, not a completed
verdict. Only its own verified PowerShell process tree (PID 13368, command
naming that RT1b log) was ended, by PID and descendants, never by image.

The required `RT1b dry run?` message acknowledged that go. Main remains
e8ac5a53; neither concurrent batch has shipped, so no main merge is needed.
The launcher gate below supplies the completed final selection verdict.
No process scripts or suite machinery were changed by RT1b.

## Retained launcher verdict

**Green:** `sh tools/gate.sh RT1b --dry-run`, launcher exit **0**, checks
exit **3**. The launcher's normal versioned route ran the full gate tier
plus the four directly selected nightly harnesses (AuctionCheck,
EducationCheck, PrizeCheck, ShortSellingCheck): **191 harnesses, 188 passed,
3 registered red harnesses, no NEW red**. This is broader than the local
39-harness pick the starter asked to stop and supplies its final verification.

- DeathRecordCheck: the registered orphan-section line (OX1).
- MapCheck: its three registered geometry lines (map completeness, connected
  streets, and street surface).
- LongPlaytest: the same default layout movement paired against untouched
  main above. Every money fixture and the closing paper-books checks pass.

Nine timing assertions were SOFT, as required by the normal route. Five
played-path assertions were OBSERVED (MonetaryCheck 1, HealthCheck 1,
MapCheck 2, PrizeCheck 1), with their dependent laws skipped by the existing
mechanism. No premise, assertion or allowance was changed to get this verdict.
Compile, maps and Stale passed (0 firm findings). No known-reds entry was
added or transferred; none belongs to RT1b or RT1 in the current registry.

Kept run: `/tmp/cbs-gate-RT1b-ylIvHZQ9` (ship.log, checks/suite.txt,
checks/NAME.log, selection, checks.complete, checks.sha256). Its two commits:

- Main: `e8ac5a53b1bbc604ce984dbe7b366a69d00e3fff`.
- Branch: `3f755a84084cdb47226ee0dfef90151c13c58ed0`.

The recorded selection is `full nightly AuctionCheck EducationCheck
PrizeCheck ShortSellingCheck version=0.9.110`. The dry run left the clone's
main head unchanged. A final fetch confirms origin/main is still the same
commit. Since this kept run only these notes and the ship file advance;
source and fixtures remain the reviewed 226500c2 implementation. The ship
file names the run for reuse, and its provisional marker is removed last.
