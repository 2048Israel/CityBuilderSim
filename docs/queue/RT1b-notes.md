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
