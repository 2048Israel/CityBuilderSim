# UP8 - the 10B month's change: the month after a big month, and the walk once the copies are full

The orchestrator, 2026-10-29, during UP5: MapCheck's "at 10 billion a month's change takes no more than 5 ms of the
wall clock" (`RECONCILE_MS`) is red on main at the gates (WN2's: main 6.6 ms in its paired rounds; MF1's: 6.0).
"Find what in the month's change grew since MP1 (0.8.61), and make it fast at its cause, with no budget moved."
UP5 measured it and found the causes; what fixes them is this batch. A map batch: nothing in the model reads the map.

## What UP5 found (UP5-notes section 5)

1. **Nothing grew since MP1.** MapCheck 5's first year on fresh 10B copies, every tag built and run alone in turn:
   the worst bounded month 5.76 (0.8.61), 4.12 (0.8.64), 3.21 (0.8.66), 3.17 (0.8.68), 4.64 ms (0.8.70); the median
   month 1.4-1.8 on each. Quiet, the line passes on main by a millisecond or so; a gate's load takes it over.
2. **The worst month is the first +0.01% month after the +0.05% one**, on every copy, warm JIT or not: about 3.4-3.9
   ms against 1.3-1.6 for the months after it (7 ms on a cold JIT). Timed phase by phase on main: the placing 0.7-1.0,
   `advanceCursors()` 0.1-0.6, the runs and shore 1.1-1.8, `logChanges()` 1.6-1.8 (its later months 0.24-0.9), no
   collector pause inside it. A list of the dirty districts in place of the walk of all 29,255 was tried and bought
   nothing in this month (ABBA, four JVMs a side): the dirty districts' own work is the cost, not the walk.
3. **Once the copies are full the month costs ten times more**, on every tag since 0.8.61: from about the third year of
   +0.01% months the 10B month's change is 12-15 ms every month (5B: 6 ms), 70% of it `CityMap.place()`'s walk to the
   end of the districts and 18% `roomiest()`'s pass over all of them for what no district has room for. MapCheck's one
   year never reaches it; a city of billions that stops buying land does.

## The items

1. **The month after a big month**: find what the dirty districts' work is (how many a month marks, how many of their
   148 types moved) and do only that: a district's log compares and copies the types its month moved; the placing and
   the cursors likewise where they walk what did not change. Hold every district's log ending on its counts after
   every month (a check over all of them, run outside the timed months).
2. **The walk once the copies are full**: `place()`'s walk and `roomiest()` kept against the districts' room (MP1
   tried a tree for `roomiest()` and took it out because MapCheck's year never asked it; with a fixture that grows the
   copy until it is full, it is asked every month). A MapCheck section 5 fixture that runs the copy into that state and
   times its month, with no new budget unless Jerus sets one.
3. Time each ABBA against main (`docs/queue/UP5-notes.md` section 5 has the probe's shape) and give the before and after.

Done when the 10B line's worst bounded month is under 5 ms with room under a gate's load (paired with main's), the
full copy's month figures are in the notes, and no budget or premise moved.

**WN3's gate, 2026-10-30** (0.8.75): the 10B line was red again, and `ship.sh` judged it main's, since main
is over the 5 ms budget too. But WN3 moved it: main 6.92 ms, the merge 8.97 ms (+29.6%), medians of 3 paired
rounds. The FX-side 5B line went the other way (main 33.0 ms, the merge 27.0 ms). Find what WN3 added to the
10B month as part of this batch's causes.
