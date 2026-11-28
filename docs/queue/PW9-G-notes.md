# PW9-G — wires ready with the power: notes

**Resume here (2026-11-28):** the law 2043a414a + 61db49b7e; REV-PW9-G's seven P1s mended in c9168e6fe, 626bec463,
5bc768cc8 and 9b39ee4a0 (the promise narrowed to power's own orders, re-dated with the wires), with PowerWiresCheck's
sections; g4 measured on 5bc768cc8 (law on, off, the promise off), g5 on 9b39ee4a0 (narrowed). Next: REV-PW9-G's second look.
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


**The law, as first built (2043a414a, 61db49b7e; since REV-PW9-G read at every month from the opening to the date, below):** a utility orders no generation past what its wires will carry when the plant
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
- **Saves:** one new field, no format bump. `AutoBuilder.State.wiresDue` (the promise's months, REV-PW9-G 3) is written
  by Gson with the rest of the builder's state; an old save has none and reads an empty set, which is what a city with
  no plant waiting on its wires has. `Cut.WIRES`, `wiresAt` and `wiresStepped` live only inside one pass.
- **A harness's door:** `AutoBuilder.wiresOffForTest` (static, never saved), so PowerWiresCheck runs one fixture with
  the law on and off, and EssentialWorksCheck's deadline section isolates power's deadline from it. It holds off the
  promise too (`wiresBind`). `wiresDue` is the builder's own state, not a held figure: nothing caches a reading of it.

## REV-PW9-G's seven P1s and their mends (c9168e6fe, 626bec463, 5bc768cc8, 9b39ee4a0; PowerWiresCheck 3, 6-12)

1. **Reoffer.** A funded POWER ask was placed by `reoffer()` straight to its count, past the wires. It now goes through
   `wiresCarry` before the crew guard, as the step's order does: the wires short of it are ordered first (GRID's
   own step, nested), and the plant is cut `Cut.WIRES` to what they carry, or held with the ask's own line, "Power:
   the ask for N X has $... raised; ...". The ask stays open and keeps what it raised. Section 7 (three asks: wires
   ordered first, cut, held) is red with the reoffer block deleted (M7).
2. **The frontier.** The law read the plan's binding date alone, so wires opening between the plant's commissioning
   and that date were credited at months they were not yet up. `Wires.dates(m)` now runs from the month m opens by
   (`AutoBuilder.wait`) to the date, at most the year before it, as the plan's frontier runs (`WorksValue.frontier`).
   At each month the plants read that month's own peak block (`BuildAdvice.powerAheadAt`), and the wires that month's
   season (`BuildAdvice.gridInMonth`). Section 8, and 3(a)'s count now read by the harness's own frontier (9 of 19),
   are red with the date alone (M8: 11 of 19 in 3(a), 7 of 7 against 2 in 8). Section 8's town stands on its
   founding plants at the carry, so it asserts the plants offered past the carry before the wires open: 6,406
   kW-months under the law against 8,164 with it off.
3. **The promise.** A wire date the law accepted was not kept against later orders. A placed plant now records the
   first month it relies on the wires on site (`AutoBuilder.wiresDue`, saved in `State.wiresDue`), where the grid's
   first unit, the plant on the crews, is read open by then. Until that month passes, the crew guard keeps it against
   power's own later orders as it keeps a lacked measure's quoted first completion (`pendingDelay`): a power order,
   short now or not, is held where it opens the grid's soonest first unit later and that unit then falls past the
   earliest due: "Power waits to preserve the opening of the wires Power's plants were ordered with." The town's
   other works are never held for it (the orchestrator's call on the second look): a street, care or school order is
   placed whole, and where it puts the wires past a due, the due moves to their new month (`AutoBuilder.redateWires`,
   read around every placement), the plants' generation waiting with its wires. FERC Order 2003-A's coupled milestones
   are the precedent: a generator waits for its interconnection, its commercial operation re-dated with the upgrade's
   in-service date, and the region's other works do not wait for the generator. Section 9: a plant placed with due
   +23 months; 2 more Steam that slow the unit from 21.2 to 22.4 months are placed; 3 more, the unit then read at 24.7
   months and pushed to 25.3, are held, 0 of 3; the unit opens by its month; the due is gone the month after; a save
   round trip keeps it. Its second town has General care at its line now in Walk-in Clinics, the reserve at its most
   (50%), and 71 houses by hand on the crews, which leave the wires read open in the due's own month (26). The pass
   places one clinic, whole, the wires then read open in month 28, and the due is 28. The town's power is served now
   (plants stood): short now, its plants' quoted first completion held the clinic by the crew guard's own law (PW3's).
   Red with `wiresBind` false (M9), with only a unit read on time protected (M9b), in 3(b) with every promise
   recorded (M9c), with the promise binding every order but the grid's (M9n: the clinic held, 0 of 1) and with no
   re-date (M9r: the due stays at 26).
   - The first form (c9168e6fe) counted the units open by each due with and without the order. It protected nothing
     once the queue's own reading had the unit past it (both none), and section 9's first fixture placed a plant
     order that slowed it further. The second (626bec463) recorded every due: 3(b)'s two starved depots read the
     unit thousands of months out, recorded dues 260 to 5,324 months on, and held every plant order that slowed it,
     leaving the plants short of the carry the standing wires already had (13,628 of 15,000 kW).
4. **A rushed grid site.** Its later units were read without the plant's points while its first was read with them.
   A rush does not shield a site from a new order: in section 10 three rushed units open in months 8, 15 and 26
   alone, and 15, 34 and 59 with 100 plants (26,900 points) on the crews. The no-plant reading (10.6, 21.5, 32.4)
   then credits a unit 9 months early. Every unit is now read as the first, the crew guard's reading with the plant
   on the crews (`siteUnitMonthsAfter`): it credits no unit early, red under the old reading (M10). That reading is
   slow for a rushed site (26, 52 and 79 months against 10, 21 and 32 with a 40-plant order), so the law holds more
   plants behind a rushed grid site than the rush needs; it is the guard's own quote, and BuildingManager's to sharpen.
5. **An abroad grid site.** The crews' quote timed all of a site's points at the city's crews. A site with part handed
   to the foreign crews now opens no unit before that part can be built at its pace (`BuildingManager.abroadUnitFloor`),
   and its units are read one by one. Section 11 is red with the floor at 0 (M11: a unit credited 38 months early).
6. **EssentialWorksCheck's starvation witness.** The wires' repairs subtracted from the builders' output left a log
   that passed with no site output. That section isolates power's deadline, so the wires' law is now held off for it
   by the harness's door (`AutoBuilder.wiresOffForTest`, in its main()), and FC2's own two lines are restored as they
   were: output over repairs, and site output at the last pass above 0 (103).
7. **A cancelled grid site.** Sites the player is cancelling are left a shell and carry nothing. The wires' reader
   (`AutoBuilder.building()`) drops them, for the plants and the grid alike. Section 12 is red with them counted (M12:
   7 of 7 placed, cut NONE, against 0 of 7, cut WIRES).

- ★ **The frontier reads each month's own season, not the window's.** The plan's window (the busiest month from the
  opening to the horizon) is the date's reading; a month before it is that month's dispatch. Rejected: the window at
  every month, which cut plants for a July peak in a March they would stand through.
- ★ **The promise is the wires' first completion, kept as the crew guard keeps one.** One law for both: a unit read
  late is held from opening later still, and a promise the queue reads broken when it is made is none.
- ★ **The promise binds power's own later orders alone, short now or not** (the second look's call, as recommended
  after g4). A plant that put its predecessor's wires past their month would leave both behind them. A street, a care
  centre or a school does not wait for a generator; where it makes the wires late, the due moves with them. Re-dated
  rather than the plant's site held: the builder has no hold on a site's crews but the player's hand (the order, the
  rushes, the shells), and a plant that opens before its wires serves only what they carry, as a generator waits for
  its interconnection. Every order still waits, as before, for a service short now (the crew guard's own law).
- **Left as it was: a due protects the grid's soonest unit on site.** Once the unit a plant relied on opens, a due
  not yet passed protects the next one, so power's orders that slow it are held until the due's month, which the
  re-date can carry to the wires' quoted opening. The crews' quote is slow in a small town: section 9's second town
  opened its first unit before month 20 against month 28 quoted. Each due would need the unit it relied on; not
  this batch's, and power's orders alone are bound.
- ★ **The abroad floor is the wires' reader's alone.** `WorksValue.openBy`, which the power plan and the plants'
  share read, is PW3's and unchanged: a plant site part abroad is read as the city's crews would build it. Changing
  it would move the power plan itself, which is not this batch's. Cancelled sites are dropped for both (`building()`).

### After the mends (g4, 5bc768cc8 merged with main's CBK3; g5, 9b39ee4a0, the promise narrowed; the same observer, one tree)

Main's own figures below (g0) are a city CBK3 has since changed: on this tree the 1900 city leaves g0's at month 18
(295 people against 294), before any power order. So the law is measured against itself held off (`-Dpw9g.off=true`,
`AutoBuilder.wiresOffForTest`), the same tree in every other line, and against the promise alone held off (`wiresBind`
false, measuring only).

| | 1900, m2-440: off | promise off | on, wide | narrowed | today, m2-180: off | promise off | on, wide | narrowed |
|---|---|---|---|---|---|---|---|---|
| the same city as off to | - | m259 | m134 | m259 | - | m107 | m85 | m85 |
| months below 95% current energy | 0 (min .9562) | 1 (.9472) | 1 (.9216, m296) | 1 (.9472) | 0 | 0 | 0 | 0 |
| months generated < asked / wire-bound | 2 / 0 | 6 / 0 | 5 / 2 (m419-420) | 6 / 0 | 0 / 0 | 0 / 0 | 0 / 0 | 0 / 0 |
| unserved GWh of asked | 1.13 of 2,917.9 | 3.32 of 2,890.2 | 2.48 of 3,064.5 | 3.32 of 2,890.2 | 0 | 0 | 0 | 0 |
| carry at the end | 55 MW | 115 MW | 85 MW | 115 MW | 30 MW | 25 MW | 25 MW | 25 MW |
| generator / grid spend | $5.32M / $7.19M | $9.81M / $19.22M | $21.99M / $14.13M | $9.81M / $19.22M | $3.60M / $3.80M | $3.52M / $2.59M | $4.37M / $3.87M | $4.51M / $2.60M |
| population at the end | 22,773 | 23,446 | 23,638 | 23,446 | 16,523 | 16,303 | 15,707 | 15,967 |

- **The population with the promise narrowed (g5).** The 1900 city is promise-off's month for month to month 440
  (23,446, 673 above the law held off): the promise held nothing there. The modern city ends at 15,967, 556 under the
  law held off (3.4%, against 4.9% with the wide form), with energy served in full in every month. The promise held
  Power's own order alone, in three passes (months 79-81: the Small Steam Station promise-off placed at month 79,
  ordered with the four of month 85), and no street, care centre or school. The city parts from the law held off at
  month 85 and stays level with it or above it to month 130 (7,345 against 7,307 at month 120). The gap opens in the
  last forty months, as the city grows fastest, as promise-off's does: a path, not power. Of the 556, 220 are the
  frontier's (promise off, 16,303), and 336 follow the three held passes, against 596 for the wide promise's 31 holds.

- **The population with the wide promise (g4), named.** The 1900 city has no fall: it ends 865 people above the law held off. The modern city
  ends 816 under it (4.9%), with energy served in full in every month of all three runs. 596 of those people are the
  promise's. From month 80 to 91 it held Streets for 12 passes, Childcare 8, General care 5, and the schools 6, to keep
  the Distribution Substation ordered with the steam of months 72-79 on its date; the city parts from the law held off
  at month 85. The other 220 are the rest of the law's, the plants cut for their wires: with the promise alone off the city
  parts at month 107. The fall
  measured before the mends (g1, g2: 13,080 against main's 14,656, 1900) was a path, not power. g2 served all its
  energy in every month, as main did. The two cities parted at month 253, after the month-224 order was placed wires
  first, and by month 339 main's city asked for two Diesels and six Distribution Substations that g2's, ahead of its
  plan, never did ($13.8M more on power over the run).
- **The wide promise bound every automatic order, as REV-PW9-G 3 asked, and cost the modern city 3.7% of its people.**
  Recommended for the second look: narrow it to power's own orders, the plants that would strand their predecessors'
  wires. The frontier already reads the wires' slip at every later plant order, and a street or a school does not
  wait on a substation's date the way it waits on a lacked service's completion. Done in 9b39ee4a0, the orchestrator's
  call (the ★ above, "binds power's own later orders alone"): 2.1% against promise-off at g5.
- **The 1900 city's wire-bound months, m419-420.** The peak steps from 30 to 52 MW in one month, as a large pending
  site opens, on 83 MW of plants standing behind 50 MW of wires. The seven Distribution Substations ordered with the
  Diesel at month 418 open from month 420. These are standing plants under a load step, the class of 3(b): the law
  placed no plant that made them, and the grid's own plan, at its own date, had not ordered for that site.
- **What the law costs the 1900 city (promise off, the g3 path).** At month 256 the wires the two Diesels needed were
  ordered first and took the cash: 4 of 7 Distribution Substations, with the city at 242% of a year's GDP against its
  240% limit. So the Diesels were held for money until month 269, and opened at 284 against 271. The plants were short
  in months 289-301: 2.2 GWh more unserved than off (0.11% of the energy asked against 0.04%), one month at 94.7%.
- **What the law buys it.** At month 353 (the same path) the nested step ordered 11 Distribution Substations with two
  Diesels. It sized them to the plan's load at the plants' date, 38 months on: 101.9 MW of peak, 52.2 MW of it from
  sites pending then (`WorksValue.pendingPowerLoad`). The grid's own step, at its own nearer date, would have ordered
  none. Once the order's 72,600 points were on the crews, those sites read as opening past the date (0.8 MW pending by
  then), and the city ends with 115 MW of wires against a 27 MW peak. The power plan reads the same forecast and sized
  the two Diesels to it (PW3's, unchanged here), so the wires follow the plants' own load, as the law says. Reading an
  order's load with the order's own points on the crews is the plan's question, for plants and wires alike: PW11, queued.

### Before the review: the model of 3fd0da7e5 (g2, the same observer)

| | default 1900, m2-440: main | g2 | modern equipment, m2-180: main | g2 |
|---|---|---|---|---|
| months below 95% current energy | 0 | 0 | 0 (min .99683) | 0 (min 1.0) |
| dispatches wire-bound | 0 | 0 | 1, 0.029 GWh unserved | 0, nothing unserved |
| grid orders (pass: units) | 173 1@224, 6@343, 1@355, 1@380, 1@385 | 173 1@224, 1@355, 1@381 (25 MW) | 173 1@90, 1@138, 1@175 | 173 1@77 (open m103), 1@127 with the Diesel (open m170), 1@169 |
| generator orders | Diesel 1@224, 2@339, 1@384 | Diesel 1@224 | Steam 18, Diesel 1@134 (open m168) | Steam 12 (77-131), Diesel 1@127 (open m163) |
| asked / delivered GWh | 1,316.16 / 1,250.35 | 1,246.52 / 1,184.19 | 591.78 / 562.17 | 588.07 / 558.66 |
| generator / grid spend | $7.783M / $11.219M | $2.020M / $3.151M | $5.397M / $4.067M + $40.9k | $4.059M + $7.0k land / $3.926M |
| population at the end | 14,656 | 13,080 | 10,734 | 10,296 |
| POWER passes cut (BUILDERS / WIRES) | - | 0 / 0 | - | 20 / 17 |

The 1900 city is g1's to the person: its one Diesel's wires are standing when it is ordered. In the modern city the
wires' opening is now read behind the builders' queue, so 17 power passes were cut for the wires: 12 steam against
g1's 17, $1.25M less on plants than g1 and $1.34M less than main, with no energy short in any month. Its population
ends 4% under main's and 7% under g1's. Power was served in full every month, so that is the path and not the power:
the ensemble judges it.

### The crews: the law on against off (PowerWiresCheck 3 on 5bc768cc8, 16 native months, two depots)

| | 3(a) plants short of the wires' share: on | off | 3(b) plants at it: on | off |
|---|---|---|---|---|
| plant units ordered | 25 | 38 | 12 | 21 |
| their construction points on the crews | 6,725 | 10,222 | 3,228 | 5,649 |
| wire-bound months | 1 | 3 | 8 | 8 |
| plant-bound months | 12 | 10 | 0 | 0 |
| energy served of asked | .8888 | .9255 | .9818 | .9817 |

- **The cost, 3(a).** With the law on the town serves 3.7 points less of its energy over the 16 months (2.4 before the
  frontier: it reads the wires short in months before the date as well, and allows 9 of 19 where the date alone
  allowed 11). In the months on serves less, it has fewer plants standing than off. The plants the law allowed were still being
  built; the cut did not leave them short of the wires (by the last month they offer 15,947 kW of the 15,000 carried).
  Off's extra units take a bigger share of the two depots, since the crews' weight is units × points^0.7, the inherited
  ConstructionControl law. So off's first plants open sooner, and the protected care centre pays for it: under the
  law it is further along in every month from month 4. The harness asserts these four facts and prints the cost.
  It does not hold on's delivery to off's, because off's lead is bought with crews taken from a protected completion.
- **What 3(b)'s wire-bound months are** (corrected: the first notes read them as the wires' forecast overtaken by the
  orders after it, which was wrong). The town's 15 standing plants (18,858 kW firm) stand above its 15,000 kW carry
  before any order, so every month its peak passes the carry is wire-bound with the law on or off: the same 8 months
  in both, law on leaving no more unserved in any of them. The grid is first lacked now in the run's month 6-7 and
  ordered then, the protected care centre holding it until then, and its first unit (6,600 points) cannot open in
  16 months at the builders the centre leaves. The law placed no plant that made those months. PowerWiresCheck 3(b)
  now asserts each of these. PW9-G-1 (the wires' quoted opening kept against later orders) is mended here, as REV-PW9-G
  asked: the promise, above.

## The checks

- **PowerWiresCheck** (new, in AllChecks after TownGridCheck), 97 assertions, sections:
  1. the wires' physics;
  2. the wires ordered with the power, law on and off the same month by month where nothing holds the wires;
  3. (a) cut, to the count the harness's own frontier allows (9 of 19; the date alone, 11), and (b) held, behind
     wires held for a care centre's quoted completion, law on against off, (b)'s wire-bound months shown the
     fixture's own;
  4. (a) wires that carry the load, (b) the spread's order: not cut;
  5. the grid's season at its date and latitude (45, -35, NaN);
  6. the allowance a search: a count is allowed only if every smaller one is;
  7. a funded ask (REV-PW9-G 1): the wires ordered first, the plants cut, or held with the ask's line;
  8. the frontier (2): a grid unit opening between the plant's commissioning and its date;
  9. the promise (3): placed, held against power's own orders, kept, expired, saved; and in a second town a care
     order placed whole past it, the due moved to the wires' new month;
  10. a rushed grid site (4), 11. an abroad grid site (5): no unit credited before it opens;
  12. a cancelled grid site (7): no carry read from it;
  13. the money audit to the cent over all 387 months run.

  Red by mutation, each restored: M1 the door forced and M2 no nested step (section 2); M3 `allows` true (3(a)
  uncut, 3(b) placed); M4 the wires without the order's points (section 8); M5 the old season reader (section 5);
  M7 no reoffer block (section 7, four lines); M8 the date alone (3(a), 8); M9 `wiresBind` false, M9b only a unit
  read on time kept (section 9); M9c every promise recorded (3(b)); M9n the promise binding every order but the
  grid's and M9r no re-date (section 9's second town); M10 a rushed site's later units read alone
  (section 10, 9 months over); M11 no abroad floor (section 11, 38 months over); M12 cancelling sites counted
  (section 12).
- **SubstationCheck** "the same order machinery": the fixture's edge was this month's season. It is now the season
  `gridSeason` reads at the unit's own opening, and the grid is read there. The law it asserts, the least units that
  cover the peak, is unchanged.
- **SubstationCheck**'s 100 MW case (no distribution unit on the menu) also keeps power plants off it. Read at its
  opening, the edge leaves the grid not lacked now. The town's power (8% served) was ordered in the first pass, and
  its quoted completion held the long Substation order at the crew guard: "Power grid waits to preserve Power's quoted
  first completion". That is the existing law, so the case is the grid's step alone.
- **EssentialWorksCheck** "pending capacity keeps its deadline through a real mid-construction save and load": a
  100+ MW town on the founding 10 MW, behind 34 pending diesels. Counted by their opening, the wires ordered with the
  Oil-Fired Plant could not open by its date, so the plant was held. That is the law, and the section isolates power's
  deadline. The first mend stood three Substations and subtracted their repairs from FC2's two fixture lines, which
  REV-PW9-G 6 showed then passed with no site output. The section now runs with the wires' law held off by the
  harness's door (`AutoBuilder.wiresOffForTest`, set and cleared around it in main()), so its town is the one FC2
  built and its two lines are FC2's own: output over repairs, and site output at the last pass (103). Every PW3
  assertion in the section is green, as it was.
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
- A station's wires keep their date against the next stations: while they are building for a station that relies on
  them, the builder holds a power order that would make them late ("waits to preserve the opening of the wires
  Power's plants were ordered with"). It never holds a street, a care centre or a school for them; where one makes
  the wires late, the stations wait for their wires.
  A station whose money was raised by a bond is placed by the same law as any other.
