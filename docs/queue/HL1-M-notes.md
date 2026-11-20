# HL1-M - the railway the map draws is the railway the city holds

MapCheck 1's hard assertion called a city with one Rail Depot and no Rail Spur wrong: 83 railway plots drawn,
no model track, `trackSurplus()` 0. The map was right and the law was short a term. The 83 are the depot's own
main line, which `CityRuns` lays to the city's edge for any city with a station whatever track the model has
(FX5, Jerus 2026-10-20). The law now counts that line as the station's, exactly and only where a station stands.
Untagged: the map's runs (one accessor nothing in the model calls) and a harness change; no model file. The
default playtest's trace is main's, byte for byte (below).

## Found (lines are `origin/main` 2677b1580, before this batch)

1. **The law.** `MapCheck.Drawn.sameAs()` (811-813) summed the model's track (each track type's count x
   `BuildingVisual.cells()`) and asked `rail + trackSurplus == track`, the drawn plots plus what the runs could
   not lay. Section 1's hard assertion (the 30-month samples, 679, and the last check of `playedCity`) rests on it.
2. **The case.** `CityRuns.layRail()` (2765) grows the main line for any city with a station - a yard laid, active
   or idle - to the city's edge whatever the track: `lineTo = !sizes.isEmpty() ? max(trackPlots, rail.plots +
   MAIN_LINE_REACH) : idle ? max(trackPlots, rail.plots) : trackPlots` (2779), and the long comment above it
   (2574-2581) says why: "a yard is no track, and a depot alone is how the model rails freight (Jerus,
   2026-10-20: 'there is a railwawy depot but no rails', FX5); the model's track short of the line lays no siding".
   `layTo()` (4232) then sets `trackSurplus = max(0, trackPlots - (rail.plots - quayPlots()))`, which is 0 whenever
   the line is longer than the track. So a city holding a Rail Depot and fewer track plots than that line draws
   `line` plots against `track`, and `rail + 0 == track` cannot be true. On every city MapCheck had met the
   model's track (a Rail Spur 72 plots, a Freight Line 248) was longer than the line, or there was no station, so the
   law had never been asked.
3. **HL1's town** (m121: one Rail Depot, no Rail Spur, 1,609 buildings of 27 kinds and every road kind agreeing,
   83 plots drawn, `trackSurplus` 0): exactly that case. The 83 is the line's length on that town's ground; the
   fresh canonical map drew the same 83 because the runs lay the same line from the same counts.
4. **Not the depot's footprint or sidings.** The yard is painted as a building (its sidings are
   `TileRaster.DEPOT_TRACKS`, the building's own pixels); `railLaid` counts only plots of the runs' net
   (`in.fixed`). The fixture below finds nothing else laid: every arm is a spine's, no siding, no branch, no quay
   spur, and the painter draws every plot the runs hold (132 of 132).
5. **A second fault, found in passing.** `Drawn.drawn()` counted a yard longer than a tile once per tile its box
   reaches (a Rail Depot is YARD_LONG x 7, FX5 Y2): the fixture's one depot drew as 3 buildings. `TilePainter`
   marks the later tiles `Painted.bpart` ("a yard whose box spans tiles is counted on the tile holding its first
   plot") and the raster below already skips them; `drawn()` did not. HL1's town did not hit it (its depot stood
   inside one tile), so its building kinds agreed.

## Which it is: the depot's, not the runs' budget (the call is settled by the code)

The two readings were "the extra geometry is the depot's" or "the runs draw track the model does not hold". The
second is a reversal of Jerus's FX5 decision, written into `layRail()`'s comment and asserted by `MainLineCheck`
(1291-1318: a city with one Rail Depot and no track lays the main line to the city's edge, no siding or branch, surplus 0; a
station with 40 plots of track runs the line past them and lays no siding; the control with no station and no track lays
none; from 1320 the idle yard holds the line). So the code's own comments and its own check settle it: the line is the
station's. The law is the one that changes.
★ The accounting form (not the call): `CityRuns.stationLine(track)` is `max(0, rail.plots - quayPlots() - track)`,
the mirror of `trackSurplus()`, a pure accessor over what the runs hold, nothing saved (a saved field would have
bumped `CityMap.FORMAT` for a number the law can ask for); the quay's spurs stay outside it, as in `trackSurplus()`.

## What was built

- `CityRuns.stationLine(long track)`: the railway's plots past the model's track, the station's own main line, with
  its WHY (the other half of `trackSurplus()`; laid = track - trackSurplus + stationLine).
- `MapCheck.Drawn`: `trackOf()` (the model's track plots, once), `trackAccounted(track)` and the three fields it
  needs (`runs`, `laid`, `station`). The law, still exact, no tolerance and no path observation:
  `rail == laid` (the painter draws every plot the runs lay and no other) **and** `rail + trackSurplus == track +
  stationLine(track)` **and** `stationLine == 0 || a station stands` (a yard laid, active or idle). `sameAs()` ends
  in it; section 1's assertion and sections 6 and 15 take it unchanged. `rail()`'s raster check (section 8) carries
  the same term. `drawn()` skips `bpart` tiles. Section 1's printed month lines now give the track account.
- **Section 25** (`MapCheck.depotAloneRails`, on a one-district `DepotCity`, no Game, no save slot): nothing standing
  (no yard, no line); one Rail Depot and no track (132 plots, `stationLine` all of it, only spine arms, no siding,
  branch or quay spur, law exact); the law is no slacker (no station standing, one plot short or one over: each
  false); one Rail Spur (72 < 132: the track is the line's first plots, the station's is the 60 past it, surplus 0);
  4 Rail Spurs (288 > 132: the station's line 0, the surplus 156 = 288 - 132, the old count exactly); the depot and
  the track gone (the yard idle holds the line, still the station's); `YARD_IDLE_MONTHS` on (yard and line taken up,
  law exact on nothing). It also states the premise the old law lacked: plots drawn + surplus (132) is not the
  model's track (0). Section 20's two-district city lays a 4-plot line, shorter than a spur, so the new section builds
  the city the track can meet from below.

## The trace

The default playtest (`LongPlaytest`, seed 0, 2,400 months, `-Dplaytest.trace=<prefix>`, one JVM each, run one after
the other) on `origin/main` 2677b1580 built in its own folder, and on this branch's classes (the same commit plus
the batch, snapshotted before the harness was last touched; the model files are the same either way). All eight
trace files are **byte for byte identical** (`cmp`): month 965,996 bytes, borrow 2,205, house 897,482, pop 444,487,
bank 899,230, bonds 5,275,413, build 698,136, labour 781,494; 2,405 rows in the month file, one SHA-256 for both
(`b32736658001175ed240...`). The reports differ in 18 lines, all of them runtime or identity: the wall clock stamps
of two log lines (14:29:31 against 14:57:21), "2405 months (200 years) in 1594s" against 1601s, the CL3 line's
"the windows took 25.3 s" against 26.5 s, and one `LandParcel@28dcca0c` object hash inside a Step's `toString`.
(HS2's 0.9.167 merged after the trace; it is not this batch's, and the batch's own delta, one accessor and a
harness, is the same on it.) The ensemble is not owed: the trace did not move. As expected, since nothing in the model reads the map and the one
added main-line method, `CityRuns.stationLine()`, has no caller outside MapCheck. Files: `target/hl1-trace/{main,mine}-*`.

## Checks

- `./batch-check.sh --fast` (after both merges of main, 0.9.166 and 0.9.167): compile OK, maps regenerated, Stale 0
  firm findings, exit 0.
- `./batch-check.sh MapCheck` (it picks BridgeMapCheck, which borrows its fixtures; run again on the final code and
  the merged main): suite OK, 2 harnesses, "All checks passed", exit 0. MapCheck's section 1 prints the track
  account every 30th month (all four samples: 0 plots of track, the model's 0, the station's line 0: that city has no
  station); its hard assertion line "the map draws exactly what the city has ... every plot of its track" is OK;
  section 8's two raster checks (with the `stationLine` term) OK; section 25: 11 assertions, all OK (the numbers
  above). The one `OBSERVED - not reached` (section 4's second bought block) is main's, in a section this batch did
  not touch. Its 13 timing assertions are soft (builders' runs time soft).
- `./batch-check.sh MainLineCheck` (the harness that asserts the station's line, whose accounting this law now
  agrees with): suite OK, exit 0.
- The pick (`sh tools/pick.sh origin/main HEAD`): 26 of 230, not ALL, so no dry run is owed. Review line: "not
  needed - no money, saves or threading in the diff". Picked: BondCheck BridgeMapCheck CapitalFlowCheck
  CrossingCheck EarthTerrainCheck GatewayCheck GlobeCheck HealthCheck IslandNetCheck IslandRailCheck JunctionCheck
  LandCheck MainLineCheck MapCheck MiningCheck PartnerCallsCheck PlanCheck PolicyPreviewCheck PriceLevelCheck
  PortCheck QuayCraneCheck QuayRailCheck RailDrawCheck RunWorksCheck TimingCheck TruckRouteCheck. Those I ran: the
  three above; the rest read `CityRuns` or MapCheck's fixtures without calling `stationLine()` or `Drawn`, and the
  gate runs them.
- The fixture causes the condition: section 25's premise check shows the old count (plots drawn + surplus = 132)
  is not the model's track (0), so the old law is red on it, and the three negative variants (no station, a plot
  short, a plot over) show the new law is exact, not slack. No relaxed tolerance, no path observation.
- Not run: a dry run (the pick is not ALL), an ensemble (the trace did not move), HL1's own 120-month town (it
  lives on HL1's held branch; the fixture is its case on a city of the same kind, and its numbers - 83 against 132 -
  are the line's length on that ground).
- Not proved here, and not changed: a harbour city. A quay spur is an arm of the rail net (`QUAY`), counted by
  neither `trackSurplus()` nor `stationLine()`; whether the painter counts its plots in `railLaid` on a painted
  `CityMap` is asserted by no harness (`QuayRailCheck` lays on `CityRuns` alone), and MapCheck's cities have no
  harbour. The old law had the same gap. It is named, not mended: it needs a fixture of its own, and a finding
  that is not yet one.

## Calls

- ★ Depot's, not the runs' (above): settled by FX5's comment and `MainLineCheck`; taken without a recommendation to
  take.
- ★ `stationLine()` derived, not saved; the quay's spurs outside it as in `trackSurplus()` (above).
- ★ `drawn()` skips `bpart` tiles: a harness count made to agree with the painter's own and the raster's; it changes
  nothing for a city whose yards stand inside a tile (HL1's), and nothing that passed before can fail by it.
- The law's third clause (`stationLine > 0` needs a station) is the whole of "the extra track is the depot's": with
  no yard laid the old law is back, exactly.
- Not touched: the model, `TilePainter`, `CityMap`, `Game`, a save, `SAVE_FORMAT`, `CityMap.FORMAT`, QUEUE.md.
- MapCheck's one `OBSERVED - not reached` (the second bought block, section 4's) is main's and was there before;
  nothing observed was added.
- Fixtures: `DepotCity` and the sections it feeds use `CityLand.restore` + `CityMap.canonical`, no `Game`, no save
  slot, no scratch folder.
