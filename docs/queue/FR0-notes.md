# FR0 - the map's network: notes

*0.8.1, 2026-10-18. The first freight batch (`docs/studies/spec-freight-and-infrastructure.md` §4 and §7; decisions D13 to D19 and D22, Jerus 2026-10-12: "do rec"). The map's alone: nothing in the model reads it, and the playtest's trace is byte-identical. Each slice was drafted by DeepSeek V4 Pro through `tools/deepseek` and reviewed by Claude; what the review changed is in the commits.*

## 1. What shipped

- **Gateways** (`Gateways`, `CityRuns`, `CityMap`; GatewayCheck). The land's offing: the bearing, every 5 degrees, with the longest run of land from the founding site out to 192 km. On the Earth it is the bearing toward the nearest larger place along land. An island founding (no land run of 10 km) has none. Each net's spine is laid toward it: the row or column nearer the bearing, with the gateway's arm grown first until it waits at the city's edge, and taken back last. The bearing is kept in the map's sidecar (FORMAT 8). From where that arm's heading leaves the city's ground, the world's highway and line run on faded to the view's edge, labelled "to the world ›".
- **The main line** (`CityRuns`; MainLineCheck). It runs from the rail gateway to the freight station, the yard nearest the gateway's arm end, through each corridor's interchange. It is double track where the city has a Freight Line.
- **Rail drawn as rail** (`TileRaster`; RailDrawCheck 1-10):
  - from 6 px a plot, ballast, sleepers every quarter plot and two steel rails, or four on the main line when it is double;
  - from 4 px, a centre line and a sleeper tick a plot;
  - at L1, a dark line ticked every block; at L2, a plain dark line;
  - at a level crossing, the rails show through the street, with crossbucks and half barriers;
  - an arterial or boulevard over the main line is a bridge, never a level crossing (D18).
- **Yards as yards, and the TRANSPORT class** (`BuildingVisual`, `TileRaster`, `Game.mapRail()`; RailDrawCheck 11-18). A new eleventh class, TRANSPORT, takes the railway's yards, the sea terminals and the transit networks out of Industry and Utilities. A Rail Terminal's 24 x 24 yard is drawn as:
  - a fan of eight sidings, closing to the line in a ladder at each end;
  - a freight shed on one side;
  - three ore stockpiles on the other, or, at the freight station of a railway carrying goods, rows of boxes and two gantries;
  - wagons on as many sidings as the fleet's share of its wagon sets gives.
- **Junctions by rule** (`TileRaster`; JunctionCheck). From 6 px a plot:
  - signals where arterials cross;
  - roundabouts where boulevards cross;
  - diamonds where a street passes beneath a ramp;
  - stacks where highways cross.

  From 4 px, a highway's end comes down to the ground. None of these is a building (D19).
- **The far view and the legend** (`MapTiles`, `DistrictPicture`, `LandMap`, `ui/MapView`; D22). At the far view, highways are lightened, the railway is dark, and ports and yards are dots in TRANSPORT's colour. In the legend, the railway is four entries and the gateway has its own.

## 2. Calls taken where the spec left room (★)

1. **★1 The gateway is found from the founding site alone**: a radius of none, so the land must run 10 km from the site. It is found once, at the first highway, track or yard. A map drawn canonically at today's size therefore finds the gateway the month-by-month map found. Found with the city's radius, a canonical draw of a large city could find none where the young city had one.
2. **★2 Ties go by the site's hash, not by loop order.** On a continent every bearing runs the full 192 km, and the first bearing would have sent every inland city's gateway due east. With the hash, 24 all-land sites give 20 bearings.
3. **★3 The gateway's arm grows first, strictly**, until it waits at the city's edge. Jerus's city x 1 now has one highway of 41 plots (1.2 km) toward its gateway where it had two stubs of half that, and its first track is the main line out. The weaker reading of the spec ("first" as a tie-break only) would keep the two stubs.
4. **★4 What a yard handles comes from saved state, not from tonnes by kind**, which the model does not keep. The freight station is a container yard when the railway carries any goods (`Rail.getCarried()`); every other yard is a bulk yard. FR1 adds tonnes by kind for the trains, and could refine this.
5. **★5 Wagons stand on `round(fleet / setsNeeded * 8)` of the eight sidings.** A tile is painted again only when that whole number of sidings, or the goods flag, changes.
6. **★6 TRANSPORT's colour is #5e7d8c**, a slate between offices' blue and utilities' teal. The sea terminals keep their own near-black apron.
7. **★7 Junctions are drawn from 6 px a plot**, the 8-px raster. Below that they would be a pixel or two. A highway's end is drawn from 4 px: there its open half is the paved road's, because a highway and a paved road are both a whole plot wide at 4 px.

## 3. Found on the way

- **Half of twelve generated worlds' default founding sites have no gateway**: their land runs less than 10 km from the site, so they are coastal. Such a city has no world line, single track and no main line. The spec's §9 item 6 ("an island city's trade still pays a lorry's long haul") applies to them as much as to islands.
- **Finding a gateway costs 55 to 80 ms**, once per map: SeaRoutes' grid of the sea along 72 rays of 192 km (GatewayCheck 13 prints 0.4 s, but most of that is the world's own first read).
- **EarthTerrainCheck 8's screen (MapCheck.SCREEN_MS, 80 ms) is red on the cloud machine before this batch too.** The 80 ms budget was set from 38 ms measured on Jerus's PC. The commit before FR0 (49b8725) measured 65, 84, 89, 94, 95 and 98 ms there; the baseline suite's one green run was luck. FR0's branch measured 91 to 121 ms inside the harness, about 13 ms more on the mean. The same screen in a probe of its own (one map, the harness's 3 warm and 5 timed rounds, five runs each) measures level: 87 ms before, 86 after, once the freight station is kept against the runs' version (a tile's input asked it of thousands of yards) and the track's neighbours are read once a plot. It is for the PC's run to say whether the 80 ms holds there.
- **A diamond's street is an arterial**, which lies on a cell's ring: the tile's first row or column. The first draw showed half diamonds, because the tile across the edge never saw the ramp. JunctionCheck 8 now holds it.
- **DeepSeek's first draft of the main line counted a stretch's steps from the stretch, not from the arm.** It was wrong on any arm that bends. MainLineCheck 10 holds it.

## 4. Not built

Waiting on Jerus under Pending as **FR0-L**. Each needs the district plans to route or keep ground that the runs cannot see: the runs are laid city-wide before the plans, and nothing in the runs knows where a works or a cell's streets stand.

- **Access roads (D17).** A street of a new ACCESS role, from every sea terminal and yard to the nearest highway ramp within two districts, else to the arterial grid, paved first from the road plots. It crosses districts, and the plans lay streets one district at a time.
- **Sidings into the bulk works (D14).** A Rail Spur's plots laid into the estate cell of the largest bulk works within a district of the line. The works' place is the plan's, decided after the runs.
- **The cloverleaf's and trumpet's ground (D19).** A 19 x 19 or 11 x 11 plot reserve laid with the corridor before cells open round it. Every highway crossing is drawn as a stack meanwhile, which is the spec's rule where the ground is short.
- **Kerb corners and estate T's.** These are drawing only, and small; they were left to keep the slice to the junctions that read at a glance.

## 5. To check by eye (FR0-C)

- **The world line and its label on the expanded map.** `ui/MapView.worldLines()` is JavaFX and was compiled but not run in the cloud.
- **The rail, yards and junctions at the near view**, on his own city.
- **Headless renders** of MapCheck's yard city at 8 px a plot, by the study's `RenderToday` harness, are in `FR0-notes/`.
