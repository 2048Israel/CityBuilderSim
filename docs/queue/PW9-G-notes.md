# PW9-G — wires ready with the power: notes

**Resume here (2026-11-28):** the mend is 2043a414a + 61db49b7e, the harness 56e6aec80; the neighbours rerun on it (target/h-loop2.txt), then the g2 measurement, `--fast`, the maps, the ship file.
The measuring observer is `target/PW9-G-measure/observer.patch` (+ `PW9GMeasureCheck.java`), never committed:
`git apply` it, run `-Dpw9g.main=true ... ./batch-check.sh PW9GMeasureCheck`, then `git checkout` it away.

## What is left of the finding on 0.9.218 (main 39fcbee71, after ASH1)

Measured with PW9's observer of the real LongPlaytest player, ported (labels g0-1900, g0-today), against PW9's
final r2 (before ASH1). Both runs exit 0, save nothing.

| | default 1900, m2-440: r2 | now | modern equipment, m2-180: r2 | now |
|---|---|---|---|---|
| months below 95% current energy | 0 | 0 | 8 (min .72736, m127) | 0 (min .99683, m175) |
| dispatches wire-bound (offered > carry, short of asked) | 1 | 0 | 31 (dispatch 123-174), 12.61 GWh unserved | 1 (dispatch 175, 37.2 MW offered, 15 MW carry), 0.029 GWh |
| plants behind the founding 10 MW | 1 Diesel | none | 31 Small Steam, 19.5 MW offered | 6 units, 3.7 MW offered, to m124 |
| grid orders (pass: units) | 155 1@409, open m440 | 173 1@224, 6@343, 1@355, 1@380, 1@385; 60 MW | 155 1@119, open m175 | 173 1@90 (open m125), 1@138 (open m177), 1@175 |
| generator orders | Diesel 1@417, 2@429 | Diesel 1@224, 2@339, 1@384 | Steam 31 @101-129 | Steam 2@77, 1@84, 3@92, 1@94, 2@129, 9@135; Diesel 1@134 (open m168) |
| asked / delivered GWh | 723.75 / 687.52 | 1,316.16 / 1,250.35 | 570.75 / 530.24 | 591.78 / 562.17 |
| generator / grid spend | $10.507M / not kept | $7.783M / $11.219M | $4.213M / not kept | $5.397M / $4.067M + $40.9k land |
| population at the end | 8,245 | 14,656 | 10,471 | 10,734 |

What remains, the modern-equipment city: GRID wanted one unit and ordered none in 13 passes (pre114-116, 120-122,
126-129, 132-134), each `Cut.BUILDERS` "to preserve High school's quoted first completion"; POWER ordered a 21 MW
Diesel at pre134 and 9 Small Steam at pre135. From m168 the plants' firm supply is 36.5 MW (BuildAdvice POWER)
behind 15 MW of wires (20 MW from m177); the July peak of m175-176 (15.4 MW half-block) passed the carry before the
unit ordered at pre138 opened (m177). GRID's figure at m168 read 11.7 MW of peak and at m175 15.4 MW: December's season
(1.012) against July's (1.193), read for the present month whatever the Ahead (the inherited reader the brief names).
The default 1900 city's first Diesel and first Distribution Substation were ordered in the same pass (pre224).

Evidence: `target/PW9-G-measure/g0-{1900,today}-{months,events,passes}.csv`, `analysis.txt` (kept in this worktree).
Note: the measuring slice ran `pkill -f python` once to clear a hung heredoc of its own; it may have ended another
session's python process (reported to the orchestrator with "mends at").

## The law and the mend


**The law, as built (2043a414a, 61db49b7e):** a utility orders no generation past what its wires will carry when the plant
opens; it orders the wires with the generation where they can be ordered, and holds the excess generation where
they cannot. At every POWER order `AutoBuilder.step` reads two shares at the order's own dated `Ahead` (the power
planner's `binding().ahead()`: its opening and the load then), through `AutoBuilder.wiresCarry`:

1. *the plants' share*: the firm supply of the plants standing and on site that will have opened by then, over the
   peak load then (`BuildAdvice.supplyDemand(POWER)` at `WorksValue.openBy`);
2. *the wires' share*: the carry standing, and the units on site the builders open by then, over the same load at
   the same date (`supplyDemand(GRID)`, the same months, no power factor). The units are read with the plant order's
   own construction points on the same crews (`WorksValue.openByAfter`), so the share falls as the order grows.

Where the plants already serve the load, nothing changes. Where they do not and the wires carry less than the load,
GRID's own `step` runs once, nested, dated at least at the plant's opening (`wiresAt`, through
`BuildAdvice.later()` in `suggestionFor`), so the wires are placed in the same pass, before the plants. Then each unit
of the order is allowed if, with it on the crews, the wires still carry all the load at the date, or if without it the
plants still serve less of the load than the wires carry (`AutoBuilder.wiresAllow`). Both tests fall as the count
grows, so the units allowed are the first ones: the order is cut (`Cut.WIRES`) to them, or, where the plants
already reach the wires' share, held whole with the WIRES line, and the next template is tried.

- ★ **The law is load-based, read at the order's date.** Rejected: firm kW ≤ carry kW. Firm supply carries the
  planner's reserve and the peak hour, so wires sized to it would be overbuilt by 1.15-1.46x (the reserve and
  peak-hour factors in this tree). A distribution planner sizes the wires to the peak load they deliver; the
  dispatch already caps every half-block at min(load, carry), so generation past the load never flows.
- ★ **The wires go through GRID's own gates.** The nested step is GRID's ordinary step (the builders' cut and the
  protected first completion, payback, ground, money), only dated later. Where it is held (the modern city's High
  school's quoted completion, `Cut.BUILDERS`), the protected law stands and the generation is cut instead: the
  brief's "or hold the excess generation order". The nested step opens no bond ask (D30); a bond for the wires is
  GRID's own step's, at its own place in the pass, so the debt law still sees one ask per measure.
- ★ **The reserve is exempt.** An order for plants that already serve the load at their date is a reserve or
  spread order. It is not flow, so it is not cut. GRID's own step orders the wires for that load.
- ★ **GRID's peak for a future date is the highest month of the native dispatch's season over the order's window.**
  The window runs from the opening (`ceil(BusinessInvestment.leadFor(months))`) to the horizon's end (`months`), at
  most twelve months. The season is `PowerDispatch.season(month, latitude)`, so a southern city's year is turned
  six months, as the dispatch has it. `NOW` reads the dispatch's own month exactly. Rejected: the single month at
  `months`, which reads December for wires that open in May and must carry July. This is the reader the brief
  named: at m168 it read 11.7 MW against the 15.4 MW July peak of m175.
- ★ **The wires are counted by their opening, with the plant's points on the crews.** Rejected, in turn, by
  PowerWiresCheck 3 (a town of two depots, the wires held for a care centre's quoted completion):
  - the wires on site counted whole: 30 small steam opened six months before their wires;
  - their opening read before the plant's points: each plant slowed the wires it was matched to (3(b), 8 wire-bound
    months of 16).
  `openByAfter` is `openBy` with `BuildingManager.siteUnitMonthsAfter`, the reading the crew guard's `pendingDelay`
  already makes. It is a pure reader: nothing is held, so no held figure gains a writer. `AutoBuilder.orderPoints`
  is the points reading `pendingDelay` made inline, factored out unchanged.
- ★ **No new catalogue id.** ASH1's 5 MW Distribution Substation (173) serves the small town, and the 100 MW
  Substation (155) the loads past it, chosen by the advice's ranking as GRID already does. No plant is resized.
- **Held figures:** no new writer. The nested step places through `place()`, so `buildStack` raises the AP1 scope's
  site stamp, HeldPosts (AP5) and the warrant (AP7) as it does for any order.
- **Saves:** unchanged. `Cut.WIRES`, `wiresAt` and `wiresStepped` live only inside one pass; `Cut` is not saved.
- **A harness's door:** `AutoBuilder.wiresOffForTest` (static, never saved), so PowerWiresCheck runs one fixture with
  the law on and off.

### The crews: the law on against off (PowerWiresCheck 3, 16 native months, two depots)

| | 3(a) plants short of the wires' share: on | off | 3(b) plants at it: on | off |
|---|---|---|---|---|
| plant units ordered | 24 | 38 | 16 | 21 |
| their construction points on the crews | 6,456 | 10,222 | 4,304 | 5,649 |
| wire-bound months | 1 | 3 | 8 | 8 |
| plant-bound months | 12 | 10 | 0 | 0 |
| energy served of asked | .9017 | .9255 | .9818 | .9817 |

- **The cost, 3(a).** With the law on the town serves 2.4 points less of its energy over the 16 months. The whole gap
  is months 14-15: on had 9-10 plants standing against off's 12-13. The plants the law allowed were still being
  built; the cut did not leave them short of the wires (by the last month they offer 15,947 kW of the 15,000 carried).
  Off's extra units take a bigger share of the two depots, since the crews' weight is units × points^0.7, the inherited
  ConstructionControl law. So off's first plants open sooner, and the protected care centre pays for it: under the
  law it is further along in every month from month 4. The harness asserts these four facts and prints the cost.
  It does not hold on's delivery to off's, because off's lead is bought with crews taken from a protected completion.
- **What the law does not do, 3(b).** The wires' opening is a forecast read at the order. In 3(b) the units on site
  were forecast to open by the plants' dates, and none opened in 16 months under either law. The orders that came after
  (the care centre's, the grid's own, more plants each month) shared the same two depots. The law holds 5 of 21 units
  and delivery is the same. Closing this would mean protecting the wires' quoted completion the way the crew guard
  protects a lacked measure's. That changes which promises the crew guard keeps, so it is not this batch's: it is a
  queue row if the review wants it.

### After the first mend (g1, 2043a414a, the same observer)

| | default 1900, m2-440: main | g1 | modern equipment, m2-180: main | g1 |
|---|---|---|---|---|
| months below 95% current energy | 0 | 0 | 0 (min .99683) | 0 (min 1.0) |
| dispatches wire-bound | 0 | 0 | 1, 0.029 GWh unserved | 0 |
| grid orders | 173 1@224, 6@343, 1@355, 1@380, 1@385 | 173 1@224, 1@355, 1@381 (25 MW) | 173 1@90, 1@138, 1@175 | 173 1@77 (open m105), 1@128 with the Diesel (open m170), 1@166, 1@171 |
| generator orders | Diesel 1@224, 2@339, 1@384 | Diesel 1@224 | Steam 18, Diesel 1@134 (open m168) | Steam 2@77, 1@85, 3@92, 2@110, 1@127, 7@129, 1@134; Diesel 1@128 (open m165) |
| asked / delivered GWh | 1,316.16 / 1,250.35 | 1,246.5 asked | 591.78 / 562.17 | 593.39 / 563.72 |
| generator / grid spend | $7.783M / $11.219M | $2.02M / $3.15M | $5.397M / $4.067M + $40.9k | $5.310M + $6.7k land / $5.941M |
| population at the end | 14,656 | 13,080 | 10,734 | 11,032 |
| WIRES cuts | - | 0 | - | 0 |

The modern city's first unit is ordered with the first steam (pre77, open m105), and its second with the Diesel
(pre128). The Diesel opened m165 and that unit m170; the load in between stayed within the 15 MW standing, so no
month was short of wires (the law is the load's at the plant's date, not the plant's opening). GRID was held by
BUILDERS for the High school in 22 passes (106-127), nested and its own, and POWER in 28; no plant was ever cut.
The default 1900 trace moves from m253 by one person: at pre224 the wires are now placed before the Diesel,
which changes the stack order, and the city takes a different path. On main a pending industrial load spiked the
plan to 47 MW at m380 and ordered three more Diesels and seven more units; on g1 that load never came. So the
default trace moves, and the ensemble is this batch's.

## The checks

- **PowerWiresCheck** (new, in AllChecks after TownGridCheck), sections:
  1. the wires' physics;
  2. the wires ordered with the power, law on and off the same month by month where nothing holds the wires;
  3. (a) cut and (b) held behind wires held for a care centre's quoted completion, law on against off;
  4. (a) wires that carry the load, (b) the spread's order: not cut;
  5. the grid's season at its date and latitude (45, -35, NaN);
  6. the money audit to the cent.

  Red by mutation, each restored: M1 the door forced (9 red, the new crews assertions among them); M2 no nested
  step; M3 `wiresAllow` true; M4 the wires without the order's points (3(b)'s fewer units); M5 the old season reader
  (11-12 red).
- **SubstationCheck** "the same order machinery": the fixture's edge was this month's season. It is now the season
  `gridSeason` reads at the unit's own opening, and the grid is read there. The law it asserts, the least units that
  cover the peak, is unchanged.
- **WorksFinanceCheck** "an early street cannot cover a later highway's saved promise": its 290 MW town stood on
  the founding 10 MW, so the wind reserve was held for the wires before the crew guard it isolates was asked. It now
  gets `EssentialWorksCheck.serveGrid`, as that file's isolated orders do, with a fixture line of its own.

## For the manual

- The automatic builder orders the wires with the plants. A power station whose load the grid would not carry when it
  opens brings its substation in the same month, ahead of it.
- Where the wires cannot come (the builders are keeping another service's promised completion, or there is no money or
  ground), the station order is cut to what the wires will carry by then. The builder's line says "(N wanted; M the wires will
  carry by then)", or holds the station whole when the plants already serve all the wires carry.
- The grid's forecast reads the year's busiest month between the opening and the horizon, at the city's latitude: a
  southern city's peak is its own winter.
