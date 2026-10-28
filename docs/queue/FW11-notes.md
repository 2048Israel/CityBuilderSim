# FW11 - no street piece walled in by buildings (0.8.64, 2026-10-28)

The batch's record. Jerus, 2026-10-20: "Queue the fix" (W1-P). Small fix batch (BP1 item 7):
Sonnet 5.5 (xhigh) wrote it whole, Opus reviews at the gate. The map's alone: nothing in the
model reads it.

## 1. The fault, seen

MapCheck's played city (PT1-M4's model), district (0, 0), drawn as the map draws it (the
carry from the districts before it): two street pieces, 198 plots and a 22-plot boulevard
remnant at frame x 32..39, y 60..64 (world x 371744..371751, y 344380..344384). It lies in
the iron blob: field sites all round it, ground the city does not own to its east, one
house (type 0, at 34,60) standing on it. The way to the main piece runs south over the
field, east along row 72 and through four houses (x 52..55) to the street at x 56. No
building-free way exists, so `joinFromSmall()` marks the piece parted; MapCheck's walk
crosses buildings ("a piece buildings wall in is the plans' to join") and reaches it.
**PlanCheck never saw it**: its districts come from `planInput(d)`, which has no carry; the
case stands in the drawn plan, whose input (`CityMap.PlanJob.in`) has the carry and what the
district reads of the districts before it. Section 17 plans from the jobs' inputs.

## 2. The calls (each ★ took the recommended way)

**★1 Which of the brief's three ways: the first, "a building that would close a ring moves
to the next lot".** A piece with no way over free ground now looks for the way that crosses
the fewest plots of buildings (`wallJoin()`, a breadth-first walk by buildings crossed, over
the ground `groundJoinable()` allows and over building plots too), and that whole way is
kept clear of buildings (`Builder.keepClear`, barred in `ground()` like an avenue's outer
plots); `make()` then plans the district again, at most `WALL_TRIES` = 3 times. The
buildings that stood on the way find other lots in the district if it has room, and go on to
the next district as any building a district has no place for if it has not; the join lays
the street over what is free. Not the second way (a building-free way round): none exists,
the piece is walled by buildings and ground not owned. Not the third (drop the piece): the
house at 34,60 stands on it, so dropping the street also moves the house, and the pieces in
the other fixtures are streets with many buildings on them.
**Tried and dropped: giving the closing buildings up at the end of `complete()`** (the join
takes their plots, they are added to the overflow and its log). It worked on the played city
(227 of 231 houses placed, 4 more carried) but failed MapCheck 2d's fixture, a half-empty
district whose premise is "its plan places every one of them": the houses went to the next
district though this one had room. Planning again puts them on other lots first, and leaves
the overflow and its log (whose pairs the next district reads in month order) to the
planner that already keeps them.
**★2 The bounds:** `WALL_MOST` = 16 plots of buildings (two lattice blocks) on the way, and
`WALL_TRIES` = 3 plans. A piece needing more, or still walled in after three, is left apart
as before (PlanCheck's one-network rule still names it). A building on a site (a mine, a
well) is never on the way.
**★3 Not covered:** `joinOut()`, the pieces joined to the districts before; no city here
meets that case.

## 3. Checks

- **PlanCheck 17 (new)**: MapCheck 1's city (`MapCheck.playedCity`, its prose silenced), each
  district planned from its job's input: fixture (a district planned again), every check
  kept, none left with a piece the ground does not part, at most 3 plans and plots kept
  clear only in a district planned again, a pure function (boxes, streets, ways); and the
  same districts with no log (the plan with no events) keep every check and every building
  placed or left out once. **Red on main**, shown with `WALL_MOST` = 0 (the walk finds
  nothing; sections 1 to 16 green, 17's three assertions red: the fixture, the checks, none
  left); green with it. `Tally` counts the districts planned again; the report line prints
  where it happens.
- **MapCheck 8, a recorded red pinned to this batch, is green** (the played city: 251 road
  plots in 1 piece, 227 buildings all within 4 plots of a street); MapCheck as a whole has
  no FAIL. The gate records MapCheck with that one red fewer.
- **The count, before and after.** MapCheck's played city: 1 of 2 districts met the case,
  0 after (24 plots kept clear, one plan again; 231 of 750 houses placed before, 227 now,
  the packed 265 to 269). PlanCheck's own cities (sections 5 and 6, 20 districts): 0 before,
  0 after. Three more MapCheck fixtures met it without a check to say so, and moved: 2c's
  full founding-site district places 2,996 houses of 3,007 (11 fewer), 2d's 764 boxes of 770
  at the yard again (6 fewer), and the river fixture's pieces fall from 4 to 3. All still
  green; the houses left out go on as overflow.
- **Trace**: the default playtest (4,000 months) on main and here, all eight
  `-Dplaytest.trace` files byte-identical; the report differs only in its two run-time
  figures (110 s and 98 s, the layout watch's 13.6 s and 12.2 s). The layout watch's counts
  are the same (286,706 boxes compared, 0 moved outside the spill's districts, 1 inside): the
  playtest's city meets no case. No ensemble (EN1).
- **Timing**, four runs of PlanCheck alternating with main's own tree (the same machine, the
  same minute): the plan medians (x1 city 6.5 to 9.0 ms main, 6.6 to 8.8 here; the dense
  screen 4.4 to 6.0 and 4.6 to 6.3; the slowest 4.4 to 9.7 and 4.2 to 8.3) are inside the
  noise, and so are MapCheck's screen and picture timings and EarthTerrainCheck's Rotterdam
  screen in three alternating runs (47.5, 74.4 and 42.5 ms main, 49.2, 59.0 and 50.5 here;
  that screen is paint and raster on plans already made). The extra walk runs only for a piece
  that has no free way, and a plan is made twice only in a district that meets the case. No
  budget or premise moved.
- **Save and sidecar**: the plans are never saved (the sidecar holds the districts, their
  logs, the journal and the runs; an older one is drawn into today's plans). `CityMap.FORMAT`
  stays 13, `SAVE_FORMAT` unchanged, no conversion code.

## 4. What a player sees, and what to watch

A stub of boulevard that ended in an iron field, joined to nothing, is now joined to the city
by a street, and a few houses that stood where the street runs stand on other lots, or at the
city's edge if the district was full. A district's plan is a pure function of its inputs, so
when ground is bought and the wall appears or goes the street and those houses can change
lots from one month to the next; the playtest's layout watch counts such moves and, as on
main, saw none outside the spill's districts.
