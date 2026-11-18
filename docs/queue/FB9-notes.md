# FB9 - the map's renderer and MapCheck's fixtures

Built on main c17b92f0 (0.9.148), on batch/FB9. Rows M6-F, BF1-M and DM3-R.
Moves the playtest: no. The model reads nothing of the map; the change is
TilePainter's and TileRaster's drawing and MapCheck's fixtures, CityMap and
DistrictPlan unchanged, so the default trace is unmoved and no ensemble ran.
No process file changes (scripts, checks' machinery, session rules): 0 lines.

## What was found

One cause behind BF1-M, DM3-R's surface loss and MapCheck 1's red. A mine
can be born on a site after a cell's street was laid across it: a site record
the plan replays at its month (DM2). The plan keeps that street, as FX7 keeps
a street under a run laid later and MF2 the cells' streets under a reserve.
TilePainter.standMines() then stood the mine on the whole of its site first,
and the streets' loop skipped every plot a building stood on. The street's
plots were drawn as the mine: their surface counted by the plan and drawn by
nobody, the street beyond the mine a piece the city's own ground joins, and
the buildings beside it beyond REACH of any drawn street.

- Main's played city (MapCheck's, 0.9.148): a mine born at m66 on six plots
  of a cell's arterial. Paved 153 of 156 drawn; 1,010 road plots in 2
  pieces (998 and a 12-plot stub the ground joins). BF1-M's 67-plot piece
  on BF1's path and DM3-R's 1.5 plots on HW1's were the same arterial.
- "every 30th month" (MapCheck 1): the same mine. The record read it as a
  tile kept stale from month 31 (a fresh paint drew 92); main's painter
  compiled ahead of the branch brings the red back and the branch's clears
  it, so it is the street under the mine.

## What changed

- TilePainter, THE STREET ACROSS A MINE: a plan street on a plot a mine
  stands on, with no run of the city's there, is drawn as the street, the
  mine on the rest of its site. A highway over a mine stands over it as before.
- TileRaster, the same banner: a building is drawn over its whole box, and
  a mine's box is its site's bounds, so the street plots inside it are kept
  and put back after the mine is drawn, before its mark.
- MapCheck 8d, A MINE BORN ON STREETS: the cause on any path. A copy of the
  played city's ground with a house for every dry plot it owns, so its plans
  fill and lay streets across the iron field. Then the played city's road,
  and an iron mine born at 100: 36 plots, 11 of them streets at 99. It checks
  the plans keep them, the map draws them (11 road plots in the mine's box),
  and their pixels inside the box are the street's, as the same tile draws
  them with no mine, at 3 and 6 px a plot. Then networkOn() on the copy.
- MapCheck 2h-c: the mine copy has a house for every owned dry plot (more
  than the plans' room, CityMap.PLAN_FILL), so the fields are built on last
  and the mine is born under houses: 25 of 2,798 boxes on its future ground,
  all on an unmined field. Main's 2,000 houses left every field bare (0 of
  2,000, the known red).
- MapCheck 14 (M6-F): besides the 200 scattered boxes, all kept with their
  oracle, a box is laid inside one free plot of each offer. It takes that
  offer alone and misses every other. One box is laid across the two offers
  whose free ground lies nearest, and it takes several. Each laid box is
  judged by the same plot-by-plot oracle, so the coverage premise is now a
  hard check again (an Observation since b7353364), caused by the laid
  boxes alone, whatever the scatter hits.
- MapCheck's exit: LM1's canvas starts the JavaFX toolkit. main() now stops it
  (Platform.exit(), then joins its thread) before System.exit(), as KeptCheck
  ends.

## Tried and dropped

Taking the street up in DistrictPlan when the mine is born (commit 75d67016,
reverted in c82aa62f). It made the played city exact. But it moved boxes:
the month's join kept a dated FW11 way over the buildings at x=73. And it
left boxes beside a mine out of reach of every street: PlanCheck 14 (g), 12
of 402 rim districts of the dense city, outOfReach 1-11 each, which is MF2's
finding for a reserve. The painter's mend moves no building, widens no
bridge and relaxes no rule, and the plans stay main's.

## Every mended assertion fails with its mend removed

target/fb9-debug/src/ham/citybuildersim/FB9Drive.java runs MapCheck.playedCity()
(section 1), 2h-c, the played city's networkOn() and 8d.

- Main's TilePainter and TileRaster ahead of the classpath (fb9-ctl2.txt):
  5 fails. "every 30th month"; the played city's network (2 pieces) and
  surface (paved 153 of 156); 8d's drawn streets (0 road plots in the mine's
  box); the copy's surface (gravel 43 of 46).
- Main's TileRaster alone (fb9-ctl3.txt): 8d's pixels fail, 11 of 11
  street plots overpainted at 3 and 6 px. The branch: 0 fails (fb9-drv2/3).
- 2h-c's fixture with main's 2,000 houses is main's known red.
- M6-F: on this path the scatter alone happens to take and miss all 24
  offers, so dropping the laid boxes does not fail here. On GDP1's path it
  left two untaken. The hard check now asks the laid boxes alone to take and
  miss every offer.

## The known reds filed under FB9

- MapCheck network, surface, "every 30th month": the cause above, green.
- MapCheck 2h-c fixture: green (25 boxes on the mine's future ground).
- MapCheck REACH (six buildings, HW1's path): green on main and the branch
  (987 of 987 within 4 plots; the 8d copy 2,742 of 2,742). The class is
  buildings beside a mine's covered street, which the mend draws; HW1's
  path is not main's and was not reproduced.
- MapCheck widest view (CU2's 256-plot city): green; CU2's path is not
  main's and was not reproduced.
- MapCheck Thread-1 NPE: not on main's gate log or the branch's. The toolkit
  is now stopped before the exit, as KeptCheck does. A JavaFX shutdown
  hook running under System.exit() is the likely source. With no stack, it
  is not proved.
- LongPlaytest "a building on the map moved" (m2389, BF1/HW1's combined
  default): green at main's gate and in the nightly (873de942, 4,000
  months). FB9 changes no plan, and the watch reads the plans' boxes, so it
  is main's. The retained m2388-2389 case lived on a path main no longer
  plays and was not caused here. The watch is unchanged.

The judge removes FB9's cover at the ship; a line that came back would be a
NEW red.

## Calls

- ★ Where a mine is born on a street, the map now draws the street through
  the mine, the mine on the rest of its site. The other ways either moved
  buildings or stranded them out of reach (above). Recommended: as built.
- ★ DM3-R's m2389 movement and REACH case, and CU2's widest view, were not
  reproduced on main's path. Recommended: close them with this batch. If one
  returns, it gets a row of its own with the path that shows it.

## Checks

- `./batch-check.sh --pick`, 28 harnesses one at a time (-Dcbs.parallel=1):
  exit 3, known reds only: PlanCheck 6's two lines (PX2), HealthCheck
  (HP2) and PriceLevelCheck, all in main's gate.txt. MapCheck: all checks
  passed, no line after its verdict. Section 14 laid 25 boxes, all as
  every plot under them says; the laid boxes alone take 24 and miss 24 of
  24 offers. Not ALL, so no dry run.
- The first pick, on the dropped DistrictPlan mend: exit 4, PlanCheck 14
  (g) NEW. That is why it was dropped.
- `./batch-check.sh --fast`: green. Probes and controls are in
  target/fb9-debug and target/fb9-control*, logs in target/fb9-*.txt.
