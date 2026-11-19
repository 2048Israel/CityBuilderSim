# RN1-H-M — the old Industrial Bakery that moved

Queued by RN1-H on 2026-11-19, on the orchestrator's instruction. This is
a real layout finding reached by the moved played city, not a flaky-suite
exception and not a change to the housing planner's requirement.

## The caused case

Run the default `LongPlaytest` on RN1-H model `39acf3894`, with main
`3c4661a9d` (SZ1, 0.9.155) merged, no seed or policy override, and the
real `earth-2.bin` / `world-1.bin` named. Its one finding at m2400 is:

> an old works moved — district (0,0), Industrial Bakery: 1 of the 1
> standing at m601 is not where it stood at m2400. The district holds 4
> of the type now against 1 then; its model count was 2 then and never
> fell below 1 in between. None of the missing old box moved off a run.

The hard watch expects a departure to take the newest of a type. It
protects the minimum of the old drawn count, the old district model
count and the lowest district model count since then. In this case that
minimum is still one. It excludes boxes displaced by a later run and
counts districts reached by a spill separately. Neither exclusion
accounts for this bakery.

`CityMap`, `DistrictPlan` and the watch are unchanged from main in the
RN1-H diff. No map code is read by the economic model. All monthly money
and household audits, reload comparisons and MoneyFixtures assertions
pass; there are zero unplaced households. The default check exits 1 for
this sole map finding. The run's wrapper exits 4 because it is new.

## Kept evidence and the work

The complete eight CSVs and report are preserved at
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-RN1-H-39acf389/`, prefix
`trace`. `report.txt` lines 389-392 contain the finding. `suite.txt`,
`snapshot.txt` and `sha256.txt` retain the verdict, source/base and hashes.
The model has been merged onto `ddcb3400e` since this run; HL1-R's kept
share cap leaves main's trace unchanged. The saved evidence remains the
exact earlier source pair above.

Reproduce this city, retain the m601 old box and district inputs, and
replay district (0,0) to the first month that moves it. Trace the
district's dated ground, runs, arrivals/departures, carry and placement
history, including the type's 2-to-1 contraction. Fix the cause and add a
small deterministic map/plan harness that causes that event. Keep the
old-works hard finding and its retention premise; do not relax its count,
broaden its run/spill exclusions, change the seed or suppress the watch.
The old FX7/MF2/DM2 reports describe related historical classes, but the
cause of this bakery's movement is not yet established.

Expected source scope: `CityMap` / `DistrictPlan` and a causal section in
`MapCheck` or `PlanCheck`, after checking their map pages. A fix should not
move the economic model. Keep off overlapping map batches until their
owners coordinate the files.

## Named red

RN1-H takes HS1's report-only `LongPlaytest` FAIL prefix byte for byte
from `b338ee4ba`, so the common judge can name this finding and no other.
The exact line for this retained run is:

```text
FAIL an old works moved                                 1 time(s), months 2400-2400
```

The ship file names this line to RN1-H-M. A generic `(no FAIL line)`
approval would hide unrelated future findings and is not used.
