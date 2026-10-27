# ET6 - Bridges ({next}, 2026-10-26)

The batch's record. Its brief is `QUEUE.md`'s ET6 row and `docs/studies/spec-earth2-terrain.md` §6 (shallow sea and
bridges), §10's ET6 row and the §11 decisions D12-D16 and D19, with the ET5-C row's "two linked landmasses in one piece".
Started on Jerus's word of 2026-10-26 ("when i get back at 7pm we gotta start more batches"), beside CC1-S, and kept off
the files MS2 (at the gate) and CC1-S were changing. Designed by Opus; the code by three Sonnet 5.5 slices at xhigh, each
reviewed by Opus: A the short crossings, B the long bridge orders (model, save, card), C the map.

## 1. Short crossings (slice A; D12)

- **`World.bridgeable(cls)`**, FRESH or SHALLOW, is the one question every crossing rule asks: `CityRuns.clear()`, the
  district plan's street and arterial bridges (`DistrictPlan`: the avenues, `cutAt`, the lay codes, the water runs, the
  joins, the plan code's BRIDGE bit) and the painter's decks (`TilePainter`, `TileRaster`). A mix of fresh and shallow
  water is one crossing; deep sea (SALT) anywhere in it stops the run, which turns along the shore as before. `salt()`
  is untouched: the coast, the ports, the sea routes and the sea's look still read SHALLOW as the sea.
- **The reach**: a highway and a railway span up to **34 plots, 1,020 m** (`TilePainter.MAX_BRIDGE[HIGHWAY]`,
  `RAIL_BRIDGE`; 14 before), which holds Halifax's Macdonald and MacKay bridges (about 805-820 m of water on earth-2); a
  street stays at 6 plots, 180 m.
- ★1 **An arterial keeps 14 plots (420 m)**, its own dial now (`DistrictPlan.ARTERIAL_BRIDGE`; it was the highway's).
  D12 widens the highway and the rail only; a district's arterial is a street. Recommend yes.
- ★2 **A 45-degree step onto dry ground keeps its corner off the sea of either depth**, so a highway never shaves a
  shallow coast; a corner beside a step over the water may be water of either kind, never deep. Recommend yes.
- `CrossingCheck` (new, 82 assertions): the highway and rail at the cap and one past it, the mixed span, a deep plot in
  the span or at the landing, the street and arterial limits through the plan, the dials. 28 of 32 hand reverts went
  red; four (the avenue pruner's counter, `cutAt`, the two join tests) no fixture reached, and they mirror the fresh rule.

## 2. Long bridges (slice B; D13, D14, D16)

- **Three templates**, ids 145 **Highway Bridge**, 146 **Rail Bridge**, 147 **Road Bridge** (the `"bridge"` flag,
  `BuildingsTemplate.bridgeKind()`; INFRASTRUCTURE, on the Roads & transit strip; no capacity, not a run). The template is
  the two landings (a plot of deck at each end, 19,375 sq ft of land); the span is billed per order. Rail and Road date
  from 1900 (the Forth Bridge stood by 1890); the Highway Bridge is undated, as the Elevated Highway is.
- **The order** is toward a far landmass the card picks (`chooseBridgeLandmass`, one order's; the rule: the first
  landmass no port links, else the first that is not home). **The survey** (`BridgeSurvey`, pure, over a small `Ground`
  read from the World and the land, never the map): the shortest 8-neighbour chain of fresh or shallow plots from the
  city's own dry ground on the far landmass to its own flat or foothill ground on a linked one, deep sea a wall, capped
  at **36 km** (D13, the Hangzhou Bay Bridge), searched to 45 km so a refusal can say how long. All or nothing: no
  crossing is `NO_BRIDGE` before licences, land and money, so no bond is offered for an impossible bridge. The refusals
  are the study's four in its words, and a fifth (★4). The survey is kept against the far landmass, the linked set and
  the land's stamp.
- **The price** (`BridgeWorks`): length x deck width x a rate a m2 that rises with depth, plus the landings. The base
  is ET4's deck, US DOT's $250 a sq ft (about $2,690 a m2), to 5 m of water and on fresh water; the deep rate is the
  Confederation Bridge's. ★5 **Its cost is C$1.3 billion** (1993-97, 12.9 km, 11 m deck): Wikipedia's figure as built,
  about 30% over the C$840 million projected; the C$1.0 billion some pages give (Library and Archives Canada's exhibit)
  is the earlier estimate. In 2025 US dollars (Canada's CPI x1.79, US$0.72) about US$11,800 a m2 at 35 m; linear in
  depth between. Widths: highway 23.2 m (ET4's), road 12.2 m, rail the highway's metre x1.42 (the game's own Freight
  Line over Elevated Highway): rail above highway above road. Measured: a 12.9 km Road Bridge at 30 m US$1.62 billion
  against the Confederation's US$1.68 billion; a 1 km Highway Bridge at 5 m US$62.4 million; a 36 km Highway Bridge at
  10 m US$3.51 billion, 2.2 times Hangzhou Bay's (2008 Chinese costs, not fitted). The span's dollars become points at
  the template's founding price a point and are owed on the site as ET4's run works are, so a long bridge holds the
  builders for years.
- **The link** (§6.5): `Bridges` keeps every bridge built or building (template, kind, both landings and landmasses,
  length, deepest and mean depth, the path's corner plots, on site or standing), fitted to the buildings' counts at the
  month's settle as ET5's port rows are, and is ET5's empty `Landmasses.Joins`: a **standing** bridge from a linked
  landmass links the far one, through the same path a port's link takes (the closure, `linkChanged()`, the offers and
  HY1's sites). This takes ET5-C's "two linked landmasses in one piece" (★ET5-C7) as far as the model goes. Saved as
  `DataSave.bridges`, absent with none; no format bump (an older save has no bridge).
- ★6 **A standing bridge cannot be demolished** ("A bridge stands for good: taking one down is not built yet"); a
  stopped shell can. Recommend yes, as ET5's ★6 refuses unlinking.
- ★7 **The far end must be the city's own ground** (the study's "or bought with the order" is not built). Recommend yes:
  a link only matters where the city owns ground.
- **The card**: the far landmass's chooser, the Bridge line ("To Island 2: 13.3 km over water up to 21 m deep ..."), the
  refusals, and `showNoBridgeMenu`; NO_BRIDGE beside NO_RIVER in every place.
- **On the Earth** (BridgeCheck 7, earth-2.bin named): a city at Borden-Carleton owning ground at Cape Jourimain surveys
  **13.30 km** against the Confederation Bridge's real 12.9, deepest 21 m, mean 13 m, three corner plots, in 0.2 s. The
  Rotterdam delta to Goeree-Overflakkee: 3.67 km over fresh water, 31 ms. A synthetic 36 km bay is 0.3 s; the worst
  case, a search run out to 45 km, 1.1-1.7 s.
- ★4 **A fifth refusal, "No landing"**: water that ends only at ground that is no landing (unowned, steep, or on an
  unlinked landmass). Recommend yes: the study's four do not cover it.
- `BridgeCheck` (new, seven sections): the survey on synthetic grids (straight and diagonal channels, the mixed span,
  the deep wall and the detour, the landings' relief, every refusal word for word), the price, the order in a played
  two-landmass city (through `setBridgeGroundForTest`, since the fixture's strait is open water past 45 km), demolition,
  the save, a generated world, the Earth. Mutations (salt bridgeable, the cap, the landing rule, the joins, the save
  key) each turned it red.

## 3. On the map (slice C; §6.6)

- The game hands the map the **standing** bridges (`Game.getBridgePicture()`, `CityMap.BridgeRun`: kind and corner
  plots, landings first and last), beside the landmasses wherever the map is reconciled, drafted or read from its
  sidecar; null while none stands, and then nothing in the new code runs.
- `CityRuns` (banner LONG BRIDGES) lays each whole: a highway bridge's plots are the highways', a rail bridge's the
  railway's, from landing to landing, never grown, taken back or moved, and not counted in the nets' plots; a road bridge
  is a paved street deck that joins no net. `DistrictPlan` sees the plots as run plots, so no building, field or yard
  goes on a landing or the deck; the painter draws them as any run over water, with `TileRaster.DECK`. The list's stamp
  is folded into the runs' signature, the plans' frame hash and the sidecar's land stamp.
- `BridgeMapCheck` (new, 63 assertions, six sections): laid whole and drawn as deck, rail and road, the landings clear,
  never moved, no bridge no change (the same plots, raster bytes and sidecar stamp as before ET6), the stamp moves.
  Mutations (the deck, the clearance, the two stamps) each turned it red.
- No arm is grown to a landing: a bridge whose island has no highway yet stands and draws, unjoined (ET6-C).

## 4. The trace, the ensemble, the checks

- **The default playtest's trace moved, on the map only.** Every economic line is byte-identical to main's (79d7c9b4);
  two lines of the layout watch (CL3) moved: 286,746 boxes compared became 286,706, and the ring at m4000 12,055 homes'
  boxes 12,053 and the outer kinds 351 at 13.91 km 350 at 13.89. The cause is D12 on a generated world: its highways now
  bridge lakes and rivers 15 to 34 plots wide, so the districts beside them lay out differently. The study foresaw the
  redraw ("generated maps redraw some wider rivers with a bridge") but not that the playtest's report watches the layout.
  ★3 Kept as decided (D12 for every world), not held to 14 on generated worlds. Recommend yes. Slices B and C move
  nothing more: the merged tree's trace is slice A's to the byte but for the run-time figures (no generated world can
  order a bridge: refusal 4).
- **The ensemble** (EN1, as the trace moved): `tools/ensemble/ensemble.sh et6` on abf933d4, 80 of 80 runs in 9 minutes,
  23 of 25 criteria, the same two fails as the 0.8.37 baseline with the same figures (A2's longest spell 9 months, S1
  1.09%), AUTO2's final population 258,838 as the baseline's, T1 16/16. Committed as `docs/ensemble/ET6.md`.
- **`--earth rotterdam`**, main (79d7c9b4) against the branch: the same, but the same two layout-watch lines (193,031
  boxes compared became 193,079; homes' boxes 6,810 6,813, the outer kinds 334 335). The city owns no ground off home,
  so no bridge is ever ordered. Both runs end with the same finding of main's (an old works moved, month 4000).
- **GatewayCheck 17, Halifax on the Earth** (runs only with earth-2.bin named, so the slices' own runs never reached it):
  its fixture held that Halifax's railway gate arm waits at the harbour. With ET6 the railway bridges the harbour's
  shallow water, which is the point of D12, so the arm runs on to the city's edge. ★8 The fixture now holds that (FR0's
  walk crosses none of the city's own water), and section 16's generated coast keeps the arm that waits at the sea,
  which still passes. Recommend yes: the world line's checks at his site still run and pass.
- **`./batch-check.sh --pick`**, earth-2.bin named: 95 of 149 harnesses and LongPlaytest. The first run's only new red
  was GatewayCheck 17 (above); after it, see the final run's line in the ship file's report.

## 5. Not done (ET6-C)

- **The automatic builders never order a bridge**: the AutoBuilder, the advice and the test player (`buildFor` refuses a
  bridge; the advice's ROADS candidates give it no unit, as it carries nothing). CC1-S was in those files tonight, and
  the traces stay still. As ET5's ★5 for ports.
- **A far landing bought with the order** (★7), **demolishing a bridge and unlinking** (★6), **the span's upkeep** (the
  template's repairs are the landings'; the span's maintenance is not modelled).
- **The map**: no arm is grown to a landing, so a bridge joins the island's highways only where a net already reaches
  it; trains and sidings do not use a rail bridge; the map's legend counts of highway and rail plots include the bridges'.
- **The deep gap crossed by one span** (D15: later, with research).
- **The words**: the Earth's North American mainland is named "New York's" by the data (ET1-C call 2), so a bridge from
  Prince Edward Island reads "To New York's landmass".
- **The screens by eye**: the Build card's chooser, Bridge line and refusal page, and a bridge on the map, were not looked
  at (the game was not opened; no slot written).
