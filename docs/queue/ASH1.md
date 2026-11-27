# ASH1 - power for a small town

Jerus, desk chat, 2026-11-27. He sent his findings from governing Ashbourne
(slot 2, 0.9.215, months 193-241) with "idk, see if anything worth fixing or if
even needs fixing". The desk chat sorted them, and he said "Yes, send them".
The findings are in `docs/queue/ASH-findings.md`, a copy of his file; this
batch takes findings 1, 2, 9 and 8.

Ashbourne is his save. Read it only through copies on scratch, never his slots
or saves folder. Each condition is caused on a fixture of the batch's own:
Ashbourne's figures are the evidence, not the test.

## What he saw

- **Finding 1, a bug: the POWER row says the opposite of what happens.**
  - At m193 the overview's POWER row said "204% served at the peak hour", in
    green.
  - The dispatch carried 10,000 kW, the founding grid, against a 12,713 kW
    peak: 4.6% of the month unserved and a power ratio of 0.924. Every
    sector's output was scaled down by that ratio.
  - The cause, checked by the desk chat on origin/main: `CityNeeds.measure()`
    (CityNeeds.java ~449-451) reads POWER only from
    `BuildAdvice.supplyDemand(POWER)`, the plants' firm supply against the
    peak. The wires (`BuildAdvice.Kind.GRID`) are measured only on
    Build > Utilities.
- **Finding 2, design: the grid has one step, too big for a small town.**
  - Past the free 10 MW, the only unit is the Substation (id 155,
    `Substations.SUBSTATION_MW` = 100). It costs Cr 22.1M and 132,000 points,
    153% of a 6,166-person town's GDP.
  - Grids of 1916 grew in distribution substations of a few MVA.
- **Finding 9, design: the posts carry nothing.**
  - The Substation's 140 posts (DIPLOMA 100, COLLEGE_ENGINEERING 30,
    UNIV_HIGHTECH_ENG 10) take about 9% of Ashbourne's diploma holders.
  - `Substations.costs()` carries n x SUBSTATION_MW x 1000 whatever the
    staffing.
  - Real distribution substations are largely unattended.
- **Finding 8, minor: the months-left runs high.** The POWER row's months-left
  for the substation said about 28 when the site's pace gave 17-18, and about
  16 when it gave about 12.

## What to build

1. **The row reads the lesser of the plants and the wires.**
   - It is red when the wires shed load, and NEEDS YOU counts it.
   - The figure comes through a public getter that Build > Utilities already
     uses, never recomputed in the screen.
2. **A grid step a small town can take.**
   - Size it from real cases (check reality, docs/contract.md): about 10 MW at
     about a tenth of the cost, or capacity priced per MW.
   - Its id is new and permanent in `buildings.json` (`BuildingDataCheck`).
   - The auto-builder and the advice consider it.
3. **The posts scale with what the grid carries,** or the small unit has few,
   after the real cases.
4. **The months-left reads the site's real pace,** the same figure the
   construction card uses.

## The checks

- The row is red on a fixture with plants over the peak and wires under it,
  and green when both are over.
- A town grows past 10 MW in small steps.
- The posts follow the load.
- The months-left matches the site's pace on a caused build.
- The money audit stays exact to the cent.

## Order

- **Placement.** Next free lane: it waits on nothing in flight.
- **PW9-G after ASH1.** PW9-G (queued) orders wires with the plants, and it
  touches the same Substation and wires. ASH1 goes first, so that PW9-G orders
  the small unit too.
- **The manual is not this batch's.** An edition comes only at a milestone or
  at Jerus's request (docs/orchestrator.md). The notes carry a "For the
  manual" section, and `docs/manual.*` stays untouched.

Versioned. Default behaviour moves on purpose, since the auto-builder will
build the small unit: an 80-run ensemble against the newest baseline, its
verdict in the notes. The catalogue moves: a stranger's review at "mends at".
