# The changelog's paragraphs for 0.8.1 to 0.8.10 - to paste into the project

For the claude.ai project "Civic Ledger: Java Game", file `claude/changelog.md`.
Paste everything below the line at the very top of that file, above its
newest block, as it stands: newest first, one block per version, TM1 and the
two docs passes (no version) in the order they shipped. Drafted 2026-10-20 by
the review desk from `GameVersion.java`'s entries, the notes in `docs/queue/`
and the docs passes' drafts in `docs/notes/pending.md`, whose changelog
blocks for 0.8.1 and 0.8.4-0.8.10 these replace.

---

### Docs pass 2026-10-19 — over FX3, TM1, FR2, FR7, FR3, FR5, FR6 and FX4 — shipped, no tag

The daily docs pass by a fresh session over the eight batches since the 2026-10-18 pass (71cf051..4b27f49).
Comments and one log line's text only: the freight sector model's comments, Game's, the timing helper's and the
interface's; every harness header against its sections (NewGameCheck 15, RailDrawCheck 20-21, TruckRouteCheck's
run order); the map's comments for FR5-FR7 (the Rail Depot's fan, kerb corners and interchanges, trucks, the
legend's 52 entries); docs/tree.md's entries for FR2-FR7's files, 143 buildings, the Rail sector named; the maps
regenerated. Flagged for a code batch: `Freight.getKept()` may read low since FR6; the Rail build card still says
"Owned by the rail sector"; the plane legend's 52 entries against LEGEND_ROWS 17; MapCheck's trackSurplus() labels.
Suite 118 of 120 on its tree: ScaleCheck as on main, StaleCheck fixed in its last commit and green on the merge.

### 0.8.10 — FX4, a new city after a reformed one founds at unit 1 — 2026-10-19, shipped, see docs/queue/FX4-notes.md

Not a regression of the freight batches (0.8.4 does the same): the denomination was a final field that
`Game.buildWorld()`, New Game, the load path and the constructor share, never reset, so a city founded in a window
that had loaded a reformed city kept its unit and money constants, parity read 0.0000 from month 2 and the rate ran
from 1.00 to 0.04. `buildWorld()` now resets the denomination and re-seeds the fifteen money constants at unit 1
first (`seedMoneyConstants()`, the load path's list made one method). Diagnosed on scratch copies of his autosave
and slot 2, never written. NewGameCheck 15. Gate: 120 harnesses, 119 passed, ScaleCheck's "...and it builds" main's
red; the trace byte-identical.

### 0.8.9 — FR6, the railway carries the city's own bulk, and the port yard — 2026-10-19, shipped, see docs/queue/FR6-notes.md

Of every bulk tonne bought at home the siding's part (.75) is offered the railway after the boundary's trade, on
the room the track, sets, stations and sidings leave, billed "Rail freight" at the railway's quote; with a station
and a sea terminal standing, .45 of the berths' bulk rides the port yard quay to door. City tonnes burn a third of a
truck's diesel, join the revenue the quote is struck over and order no track. The trucks carry a third fewer city
legs in the default playtest, 71% fewer on the Earth; the Earth playtest's city ends 23.5% smaller (FR6-C). A reset
rail net clears its hub across a save. HomeRailCheck new. Gate: 120 run, 119 passed, ScaleCheck's line main's.

### 0.8.8 — FR5, trucks on the map — 2026-10-19, shipped, see docs/queue/FR5-notes.md

Haulage's loads drawn as trucks at the near view alone, while Settings' "Trucks on the map" is on (default on;
D21): `TruckSchedule` in TrainSchedule's shape, from Freight's saved tally of the month's truck tonnes by load
(import blue, export orange, empty grey, home violet), one truck per N loads past 1,000 a month; on the highways
from the world line and on FR7's access roads to each gate; `TruckShapes` the body and colour. The model gains one
tally nothing in it reads; the trace byte-identical. TruckCheck and TruckRouteCheck new. Suite: 119 run beside FR6,
117 passed - ScaleCheck's line, and StaleCheck's README harness count, put right.

### 0.8.7 — FR3, the landlord port and the Bulk Berth — 2026-10-19, shipped, see docs/queue/FR3-notes.md

The city builds and keeps the sea terminals; Freight's third division, Ports (`sectors/PortDivision.java`), works
them on its payroll, bills every shipper's sea tonnes "Port handling" at the kind's quote, and pays the city port
dues the same month (a revenue line, "Port dues"). The dues' default is the annuity on the kind's berths over 30
years at the city's own rate, times the new dial `TaxPolicy.portDues`. The Bulk Berth (id 142, 2.5 Mt a year,
D$60M), Handysize and Supramax calls; a berth is ordered when its dues would cover it. Found: every id now crosses a
save (an id past a gap, 142 before 141, was lost). LandlordPortCheck new. Suite: 117 run, 116 passed, ScaleCheck's
line main's.

### 0.8.6 — FR7, track that goes somewhere, and roads for two needs — 2026-10-19, shipped, see docs/queue/FR7-notes.md

From his Halifax city at 0.8.3: track to dead ends, gravel everywhere, highways nearly never. The Rail Depot (id
141, 50,000 t a month) is a railway's first order, a station before any spur or line; goods through a station only;
the overbuild while the wagon sets lagged was real and is gone. The highways carry the through trips and the freight
gates' trucks, the streets the rest: two measures, two NEEDS YOU rows; a gravel road's repairs rise past 200
vehicles a day. The map: gravel grey, track only on a train's way, interchanges' ground, access roads to every
yard, terminal and Truck Depot, kerb corners. RailDepotCheck new. Suite: 116 run, 115 passed; ScaleCheck's
ten-billion copy builds no home (FR7-C, his call).

### 0.8.5 — FR2, the Freight sector — 2026-10-18, shipped, see docs/queue/FR2-notes.md

Freight in Rail's slot of `Sectors.KEYS`, its railway the Rail division. Haulage, its trucks, bills the city leg of
every bulk tonne once, to its buyer or exporter, at a quote its own books set; the trucks (D$215k, from the Heavy
Truck Plant, id 140, or the world) sized to the plant at nameplate, kept in Truck Depots of 100 (id 139); a bulk
works short of them runs at .6 + .4 x the share they carry. Vans carry goods only. The playtests move from month 3
and pass every audit and reload. SAVE_FORMAT 40 and OLDEST_LOADABLE_FORMAT 40: older saves no longer load.
HaulageCheck new. Suite: 115 run, 113 passed - ScaleCheck's rents-pull lines and CopperRefiningCheck's Fabrication
Shop line (FR2-C, his review).

### TM1 — soft timing, two batches' checks at once — 2026-10-18, shipped untagged, see docs/queue/TM1-notes.md

`Timing` is the one helper every timing assertion goes through (21 in 8 harnesses): under `-Dcbs.timing=soft` a
figure over its budget says SOFT and is not counted, so two batches' suites run side by side on the PC. The check
scripts set it by themselves while another batch's JVM or the game runs (`tools/jvms.ps1`), and `--timing` waits
for the other batches and runs the timing harnesses alone and hard, as `ship.sh`'s gate now does. TimingCheck new.
No game change.

### 0.8.4 — FX3, the freight map's faults, again — 2026-10-18, shipped, see docs/queue/FX3-notes.md

From his eye check of 0.8.3. One yard layout read by both the drawing and the trains (`TileRaster.yardLayout()`):
the fan lies on the line's longest clear stretch, a line crossing the yard is drawn through its throat, and every
train comes in along the line onto a siding, no longer across the drawn ones. A 45-degree highway is one
carriageway with its centre line, not a stair of stacks; a road two plots wide is one road, so no signal or
roundabout on every plot. The map's alone; the trace byte-identical. RailDrawCheck 21, MainLineCheck 12,
JunctionCheck 10 and 11.

### 0.8.3 — FX2, the freight map's faults — 2026-10-18, shipped, see docs/queue/FX2-notes.md

From his eye check of 0.8.1 and 0.8.2 on his Halifax city. The line to the world starts where an arm's track ends
on dry ground at the city's edge and runs out over land: his gate arm waited at the harbour, so the line began on
open water and every train ran out to it and back, a V over the sea; corridor 2's north arm now carries it, and the
main line, station and trains' way run from that arm. A highway is drawn only once it crosses an arterial at a ramp
(the stub alone in a block is gone); a ramp mid-highway is no longer its end; the world line's label stays in view.
Slices by Sonnet 5.5 at xhigh, reviewed by Claude. No save change; the trace identical but its run-time line. Gate:
112 of 113, EarthTerrainCheck 8 and 9's timings, red at the baseline too with his game running.

### 0.8.2 — FR1, trains on the map, as the ships are — 2026-10-18, shipped, see docs/queue/FR1-notes.md

The railway tallies its railed tonnes by kind of cargo, landed and shipped (saved, never billed), and
`TrainSchedule`, in BoatSchedule's shape, turns them into trains: unit trains of 8,000 t, mixed of 1,500 t,
intermodal of 100 boxes both ways, past 30 a month each drawn train standing for several. Each runs from the world
line along the main line and corridors through a yard's ladder onto a siding, stands and runs back out, a
locomotive at each end; drawn from L1 in, each wagon's mark from 8 px a plot, in the ships' four colours. The
freight station is a container yard when the railway moved boxes. Found: in a steel city the mixed train is the
common one. No DeepSeek key on the PC: Claude wrote it, the last slice by Sonnet 5.5. Playtests byte-identical.
Gate: 113 of 113, earth-1.bin named.

### Docs pass 2026-10-18 — over GS1, RV1, DeepSeek and FR0 — shipped, no tag

The docs pass by a fresh session over 5a7a66b..b0c777c. Comments only: TileRaster's header and banners for yards
and junctions, BuildingVisual's TRANSPORT class, MapView's world line and legend rows, CityRuns' gateway and main
line, CityMap's sidecar FORMAT 8, Gateways' WHY; three harness headers; docs/tree.md's 112 harnesses, README's
tools/deepseek/; 0.8.1's record into docs/notes/pending.md. Flagged: tools.Stale does not see a record's
components; MapCheck 1, 8 and 14 print joined titles, so docs/harnesses.md shows no heading for them; TileRaster's
RAIL_DASH unread since 0.8.1. Gate: 113 of 113, earth-1.bin named.

### 0.8.1 — FR0, the map's network, the first freight batch — 2026-10-18, shipped, see docs/queue/FR0-notes.md

The land's gateway: the bearing with the longest run of land from the founding site (on the Earth, toward the
nearest larger place along land), none for an island founding; each net's spine laid toward it, its arm grown
first, and the world's highway and line drawn on past the city's ground, "to the world". The main line from the
rail gateway to the freight station, double with a Freight Line. Rail drawn as rail (ballast, sleepers, two rails;
level crossings; an arterial over the main line a bridge); yards as fans of eight sidings with a shed, ore or boxes
and wagons, in an eleventh class, TRANSPORT; junctions by rule (signals, roundabouts, diamonds, stacks). Slices by
DeepSeek V4 Pro through tools/deepseek, reviewed by Claude. Found: half of twelve generated worlds' default sites
have no gateway. Map sidecar FORMAT 8; no save change (39); playtests byte-identical. Gate: 113 of 113 on the PC.
