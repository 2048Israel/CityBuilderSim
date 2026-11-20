# PZ1 - PrizeCheck 12: two labs a field in a month

Found 2026-11-20 by GB2's first nightly on the gate box (main 919c1fa41,
0.9.162), and the same on the laptop: `PrizeCheck` section 12, "investors
build a lab while the purse pays for it: owners first, never a loan, one a
field a month, only in a city founded the 1900 way", fails its line "...yet
the investors order one that month, never two: at most one lab a field a
month" (1 FAILED of 282). PrizeCheck runs in the nightly tier only, so no
gate has run it since the last green tier at 873de9427; 35 first-parent
merges since, OP1 (0.9.161) and BO1-S (0.9.162, the owner families on) the
latest. GB2's output: `.claude/worktrees/cl-GB2/target/GB2-prize.txt`, while
that worktree stands.

## What is wanted

1. Find the first merge where it is red (start with 17f8a4c05, OP1, against
   919c1fa41, BO1-S: section 12 says "owners first", and BO1-S switched the
   owner families on), and the cause, with the lines.
2. If the model orders two labs of a field in one month, that is a fault:
   mend it, so one lab a field a month holds whoever orders. If only the
   moved city causes the line, restate the fixture so it causes its
   condition again (not a known-reds name: a nightly line no gate reads
   would stay red unseen).
3. PrizeCheck green, run singly; the harnesses the mend touches; the trace
   checked: if the mend moves it, the ensemble per `docs/contract.md`.
