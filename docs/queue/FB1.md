# FB1 - three fixtures that read the city's path

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** MQ1, HX1, FPK1, below as QUEUE.md had them, and RN1-F, folded in from Pending on 2026-11-16; their briefs stand.
- **Files it expects to change:** `MonetaryCheck.java`, `HealthCheck.java`, `FoodProcessingCheck.java`.
- **Moves the playtest:** no: harnesses only.
- **Not beside:** FB4 (`HealthCheck`).
- **Known reds:** the 1 entries filed under these rows are filed under FB1 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### MQ1

MonetaryCheck's settled town on GW1's path: the shelf settles at month 46 but the town inflates 16-23% a year over the window, so a month's delay moves the dial rows 0.19-0.37 points against the 0.100 allowance (a fixture line, red). A settle test on the price index, not the shelf, so the rows are read on a steady town; MonetaryCheck green

Brief: [GW1 notes §4](GW1-notes.md)

### HX1

HealthCheck's fee twins restated: the fixture's drain leaves every cell clearing fifteen bills or not one (coverage 85.69% against 85.57%), so "its old first" (101.39 against 101.37 a year per 1,000) and "the households who skipped a bill ate with it" (the dear city 5.9 points hungrier, 15.3 of them short on the shelf) compare the twins' noise. A drain that leaves cells between one bill and fifteen, and a fixture line that the dear fee turns away a material share; whatever the claims then read is the finding

Brief: [GW1 notes §4](GW1-notes.md)

### FPK1

FoodProcessingCheck 5's meat bet sets its own farm-floor and ship prices so the Meat Works' profit sum is tested independently of the city's path.

Brief: [BS1 notes](BS1-notes.md#fpk1)

### RN1-F

HealthCheck's poor city at three prices never caused its general-care gap: on main the x1 and x15 twins serve 83.0% and read 86.20% coverage alike, a household above subsistence paying the dear bill by eating less (hunger 13% against 20%), and its four assertions pass by hundredths; RN1's larger twins tip the beds' one (99.66% against 99.69%) (0.9.63)

It waited: a fix: put the general-care households between the two bills, as WN3 did for senior care; the red passes by name until then (Jerus 11-04)

Brief: [RN1 notes, The gate's reds](RN1-notes.md)
