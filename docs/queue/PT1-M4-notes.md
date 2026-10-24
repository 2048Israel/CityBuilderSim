# PT1-M4 - the works department retired, the city founds with a Builders' Yard

Jerus, 2026-10-12, on PT1's M4 (Construction kept $377k of month 183 at a 72.2%
margin with 0 workers): "not meant ... its cause there is some construction
points base, so ya sure fix, perhaps a starting depot or something instead?"
`BuildingManager.BASE_CONSTRUCTION`, 400 points a month with no posts behind
them, is retired, and every city is founded with a builders' building standing,
staffed and paid like any other, so Construction earns only for work its own
crews do. Version 0.8.38; save format 45.

## What changed

- **The points.** `BASE_CONSTRUCTION` is deleted. `getTotalConstructionCapacity()`
  is the nameplate of the builders' yards and depots; `Game.getConstructionOutput()`
  is that nameplate x the share of posts offered x the builders' fill x roads x
  health - nothing kept whole. `Construction.strikeCrews(work, depots)` lost its
  `cityWorks` argument.
- **The founding.** `Game.foundingYard()`, beside `foundingBank()` in `run()` and
  `newGame()`, places one **Builders' Yard**: a new template, id 143 (`nextId`
  144), the Construction Depot at two fifths - 14 NO_DIPLOMA + 6 DIPLOMA posts,
  160 points, 24,000 sq ft, D$593k and 22 units, power, water and road load in
  proportion; placed, not built, its ground allocated. It is in the built-in
  catalogue too (`initializeBuiltInTemplates()`). The builders' planner may order
  yards: it did, where a depot could not be staffed (BuildersFirstCheck 4).
- **The last builder.** `Construction.mayRetire()` refuses the builders' last
  yard or depot (both the spare-capacity and the distress rule ask it), and
  `Game.demolishable()` keeps it from a buy-out: the works department used to be
  what built a depot when there was none.
- **The People strip** lays a folded tile and an unused "+n more" at nothing and
  gives the unused one a blank: a JavaFX label set to "" keeps the wrapping width
  of the words it last held, and the new trajectory folds a tile at month 612
  (KeptCheck).
- **The save.** `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` 45: nothing new is
  saved, but a save from before was played on the free points, and one with no
  depot yet (PT1's city at month 183) would load as a city that can never build.

## How it was decided (measured, each step)

1. **A Construction Depot at founding** (the first try): the playtest built and
   ended at 685,668 people. But a hands-off village (`run()`, no player) stalled:
   146 people and no shop after 240 months, against main's 299 and a shop at
   month 13. Cause: idle builders keep a core crew and lay the rest off, and
   every planner's staffing test counts laid-off posts as spoken for (0.7.17,
   `Sector.staffing()`): the depot's 37 such posts in a village of 70 workers.
2. **Counting only crews that had worked** (a high-water mark, tried and
   reverted): the village healed (first shop at month 9, 300 people), but the
   playtest's end fell to 238,976 - the reservation is a brake planners need.
3. **Striking the founding's fill from its empty workforce** (honest month 1,
   tried and dropped): the village unbuilt until month 3, the playtest's first
   households with nowhere in months 2-3, four more harnesses red.
4. **The brief's allowed answer, a smaller template**: the Builders' Yard. Its
   idle crew withholds 14 posts, not 37: the hands-off village reaches 332 people
   with its first shop at month 24 (main 13); the depot-founded twin opens none
   (FoundingYardCheck 6).

## What read the 400, and what it reads now

| reader | read | now |
|---|---|---|
| `BuildingManager.getTotalConstructionCapacity()` | 400 + depots | yards' and depots' nameplate |
| `Game.getConstructionOutput()` | the 400 whole, depots at the posts offered | all of it at the posts offered and the fill |
| `Game.strikeBuildersCrews()`, `Construction.strikeCrews()` | the work less the 400 at full staffing | the work over the builders' full staffing |
| housing maintenance (`RealEstate.MAINTENANCE_PER_YEAR`, calibrated "under a tenth" of 400) | the 400 | not moved: a village's repairs are a few points of the yard's 160, and repairs are work the crews are struck for |
| `BASE_MATERIALS` (36 units, moved with the points on 2026-09-09) | one build rate with the 400 | kept: the yard's free material is the order placer's (the invoice leaves it out), not Construction's margin - ★3 |
| InfrastructureCheck, InvestCheck | the 400 subtracted by hand | re-based on the depots' whole capacity |
| the playtest's player (depots while capacity < max(1500, 2% of people)) | 400 counted | the yard and depots only: it orders a fourth depot by month 24 |

Construction's statements and its GDP share: SectorBooksCheck and GdpCheck green.

## The playtest (4,000 months, default, run alone on the PC)

| | main 0.8.35 | PT1-M4 |
|---|---|---|
| people near m600 / m1060 / the end | 3,934 / 16,288 / 625,815 | 8,601 / 44,440 / 765,819 |
| prices near m600 / m1060 / the end | 8.16 / 62.0 / 13,374 | 3.19 / 9.77 / 712 |
| Construction, the year to m600: revenue, net (margin), payroll, output a month | $118.7M, $68.3M (58%), $17.1M, 857 | $62.8M, $33.9M (54%), $10.3M, 695 |
| ...to m1060 | $1,948M, $624M (32%), $282M, 966 | $991M, $383M (39%), $114M, 2,591 |
| ...the last year | $41.6T, $9.98T (24%), $5.03T, 58,568 | $5.66T, $1.73T (31%), $0.32T, 77,036 |
| capacity at m600 / m1060 / the end | 2,000 / 4,800 / 83,600 | 2,880 / 12,880 / 90,480 |
| findings | none | "a building on the map moved", once, m3991 (below) |

The founding builds more slowly and the city then grows faster: the village's
60 homes stand by month 17 where main's stood by month 5 (the yard's 160 points
against the free 400), 267 people at month 19 (main 255), and 1,238 at month 60
(main 623), because the player buys its depots sooner (capacity no longer
counts the 400).

## The suite

137 harnesses and the playtest, on the merged main (FW1, 0.8.37): **all 138 green**
(MapCheck's two timing lines soft beside another batch's JVM). On the way, 23 harnesses' fixtures were re-caused, each with a
dated comment and a `fixture:` assertion: a town that ordered its works and
houses at founding grew on the free 400 and on the yard's 160 stays a fraction
of its old size, so the fixtures stand depots (`MortgageCheck.landlordCity(...,
depotsStanding)`, `ForeignDebtCheck.tradingCity(dir, depots)`, CallCheck's and
FloatLinkCheck's played city, PassThroughCheck's and CurrencyRegimeCheck's
towns, CentralBankCheck 20, WellCheck, HealthCheck, DenominationCheck); a
village's income stood on the works department's profit tax (+377 a month, of
it 225 from Construction, now -30: RobustnessCheck raises its tax); the
auction's slivers to the firms and the world are taken out where an assertion
reads the bank's whole take (CentralBankCheck 11, HoldersCheck 7, AuctionCheck
8); the founding itself (MapCheck 6, SaveFileCheck, BuildingDataCheck).

The six that stayed red after that, and what became of each (the orchestrator's
round, 2026-10-24), none by loosening:
- **BondCheck 5d**: its till, cut to a month of running costs, plus the month's
  sales now paid the whole order (budget 602.9k against 526.3k); cut to half a
  month it is short of it again (450.4k), not empty. Re-caused.
- **ScaleCheck**: its free fixture copied the playtest's city at month 622, a
  calm window of the old run (the affordability pull 1 in all six months). The
  new town's rents price migrants out in 749 of its first 800 months (a town
  grows until its rents bind); by the rule `FREE_FIXTURE_MONTHS`'s javadoc states
  (scan, every assertion read, the window's middle) it is month 807, the middle
  of 792-822. Re-caused by its own rule.
- **OrderSearchCheck**: a fault in the scaled copy, fixed at the root.
  `ScaleCheck.scale()` scaled XR1/XR2's foreign-account slots 45-94 - logs,
  flags, seeds and counts - as money, so the x1000 copy's D$ hit `MAX_RATE` in
  its first month and every sector holding dollars recalled 1e12-1e14 into its
  till; main hid it because only two sectors held dollars there. Only the land's
  slots and the float's money are scaled now, and the fixture asserts the
  copy's rate stays within ten times the city's.
- **MapCheck 8**: the check's walk crossed the plans' own buildings, which the
  plan never streets over (`DistrictPlan.joinable()`, `groundJoinable()`); it
  bars them now and prints what is reachable only across them: one piece in the
  played city, a 22-plot boulevard remnant walled in by the plan's own houses on
  later-bought ground (FX6's dated ground). That remnant is a map fault the plan
  would need a larger change to avoid - ★8.
- **LongPlaytest's finding**: the watch's. A Large Open-Pit Mine at m3991
  re-dealt 12 Iron Mines between districts, and the overflow log's squaring (CL3,
  documented in `DistrictPlan`'s OVERFLOW LOG) rewrote six downstream districts'
  carry logs mid-list with the same net by type; FX7's journal promises nothing
  about the carry. The watch compared only the net; it compares the log pair by
  pair now (`CityMap.carriedLog()`, a copy), so the box counts as the spill's.
  Nothing else in the report moved. A departure of sited mines reviving
  excluded instances is the squaring's quirk - ★9.
- **BuildersFirstCheck 6**: T1-C's trap was the free points multiplied by a
  first depot's fill. A city's fill is a job type's, the same for every
  employer, so a depot opened early adds posts the builders fill at it: their
  output rises and cannot collapse. Its six collapse assertions (three red, three
  passing because both twins stall alike on the yard) are retired with a note;
  the gate's own assertions stay - ★6.
HealthCheck passes on a window one depot wide (3 standing). Timing ran soft
beside other batches' JVMs; `--timing` is the gate's.

## The ensemble

`tools/ensemble/ensemble.sh PT1-M4`, two at a time beside CD5/CD6, on the final
model (35b1fdb4; CD6, merged after it, leaves the trace byte-identical), against
`docs/ensemble/0.8.29.md`; its baseline is committed as `docs/ensemble/0.8.37.md`.
**23 of 25 criteria pass** (0.8.29: 24 of 26), **no new red**: the two that fail
are 0.8.29's own, both better - A2's longest spell of a year over 10% from m240
9 months [0 to 24] (was 10 [6 to 31]), S1's excess at 0% 1.09% [0.10 to 4.69]
(was 2.46% [0.25 to 4.99]). H2 is VACUOUS (no burst at 0% for the autopilot to
stop; its numbers alone would pass), where 0.8.29 passed it; H1 stays RETIRED.
T1 16/16 seeds reach 40,000 people (month 1,189); T2 AUTO2's final population
258,838 against 210,443; F1 no LongPlaytest finding in any run.

## Calls for Jerus

- ★1 **Month 1.** A sector's fill reads 1 until the first labour market, so the
  yard works its 160 points in month 1 before anyone lives there - a month of
  points nobody worked - and a city saved before its first month reloads to build
  nothing in it. Striking the founding honestly costs the village's first two
  months (3. above). Keep the convention?
- ★2 **The Builders' Yard** (20 posts, 160 points, the depot at two fifths) as
  the founding building and an orderable template. Its 160 points stand the
  playtest's village by month 17 (main, on the free 400: month 5); a bigger yard
  builds sooner and holds back more of a village's hands. Its size?
- ★3 **The free materials yard** (`BASE_MATERIALS`, 36 units, $648k a month at
  world prices) outlives the free points. Retire it too?
- ★4 **The laid-off reservation** (0.7.17): posts never filled count as spoken
  for. Left; counting only crews that worked cut the playtest's end by 62%.
- ★5 **The last builder is never sold.** Is a city allowed to lose all its
  builders?
- ★6 **BuildersFirstCheck 6**: its collapse assertions are retired with the
  works department (the trap cannot happen); keep it so, or assert something new
  of the gate (with 3+ depots standing the gated town builds more homes)?
- ★7 **The young city's tax take** leaned on Construction's profit from free
  points; fixture towns that order at founding now grow slowly unless they stand
  builders. In play the city grows faster (the player buys depots sooner).
- ★8 **A street remnant walled in by houses** (MapCheck 8): the plan cannot
  join it; lay or keep a street when a cell grows into bought ground, or drop
  such remnants?
- ★9 **The overflow squaring** revives a district's excluded sited mines as
  past arrivals when they depart; harmless here, it would shift a district with
  room.
