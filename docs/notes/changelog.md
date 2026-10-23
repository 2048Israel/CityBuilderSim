# The changelog - what shipped, newest first

The live changelog since 2026-10-20 (Jerus: the record "in GitHub", so no
session has to paste into the claude.ai project). One block per version, at
most 12 lines (CLAUDE.md, "Each batch is told once"), in the house shape:
`### version - batch, title - date, state, see notes`, then one paragraph.
A batch or a docs pass adds its block at the top in its own merge.

The older record: the project's `claude/changelog.md` ("Civic Ledger: Java
Game") holds the blocks from 2026-09-11 up to the batch before I12, and
`changelog-archive.md` beside this file the blocks before 2026-09-11. The
blocks from 0.7.143 to 0.8.10 below waited in `pending.md` for a paste that
never came, and were moved here on 2026-10-20.

---

### 0.8.26 — UP4, the People page kept — 2026-10-23, ready, see docs/studies/spec-screens-in-place.md

The study UP4 timed every rail screen's redraw at month 4,000 through the pulse's own passes: 29-68 ms, the chrome
12-27 of it, while 90-99% of a page's nodes show the same thing month to month. Its pattern, ui/Kept, runs a screen's
own drawing code over the nodes it made last month (by scope and order), its boxes setting their children in one call
where they differ, so nothing kept is styled, measured or synced again and the scroll never moves. The People page is
the pilot (keepMenu, drawPage, the Pieces helpers it reaches; Mirror, Strip, Pair, SegmentBar and Waterfall told their
figures): at month 4,101 its redraw went from 53.0 to 32.1 ms (the page's part 34.7 to 12.8; the rest is the chrome),
and at 50x the FX thread from 50% to 41% busy, frames p95 51 to 20 ms. Its renders are the same pixels before and after.
KeptCheck (new, with ui/KeptBench; no window, skipped where JavaFX cannot start) draws the page kept and new for 25
months and compares them node by node and picture by picture. The rest is KP1-KP7 after UP2, which takes the chrome
(Pending UP4-C). No model change; TRACE_LINE

### 0.8.25 — UP1, the autosave off the clock — 2026-10-23, ready, see docs/queue/UP1-notes.md

The autosave held the window's thread 170 ms every 12 months at month 4,000 (31% of it at 50x), the history two
thirds of it. Now the save is filled on the FX thread and its snapshot written on one writer thread (SaveWriter)
while the months go on: the autosave costs the clock 4-5 ms at months 1,000 to 4,000 (it was 50-176 ms), and its
share of the FX thread at 50x is 0.0%. The save holds no live object (the setters copy: the inbox, the build log,
the bond market, the exchange, the construction control, the fund, the bank's preferred), the history is written
compact and streamed, and an autosave comes at most once in 30 s of real time (Jerus's call). A load, a numbered
save, a skip and Quit wait for the writer. ui/UiProbe is the window's probe, run by hand. SaveSlotCheck 7c new: the
background files equal the old way's exactly. No save-format change; the trace is byte-identical, so no ensemble.

### 0.8.24 — FX7, the runs dated — 2026-10-22, ready, see docs/queue/FX7-notes.md

LongPlaytest's one red since AN1, "an old works moved", was the map's: a district's plan replayed its whole history on
the city's runs as they stood today, so when the runs laid something across built ground (the founding district's Rail
Depot yard came and went a dozen times from m1,128, a highway arm from m2,131) or took it away, every arrival was
placed again and 100-260 of its ~300 boxes moved. Now the runs are dated as the land is: the map journals each plot
whose highway, track or yard kind changed, month by month, and keeps it in the sidecar (FORMAT 11); a plan replays its
history on the runs of each month, a box under a run laid later stays where it stands (both are drawn), and
ground a run leaves is room again. The playtest's watch: 14 of 14 old works stand, no finding. MapCheck 2d and PlanCheck
15 new. The trace is byte-identical, so no ensemble. Interchange reserves stay undated; Jerus's calls in the notes.

### 0.8.23 — FX8, XR2's loose ends off the default path — 2026-10-22, ready, see docs/queue/FX8-notes.md

Five things the orchestrator's review of XR2 found, none on the default path. The central bank keeps nothing, restated:
equity less the vault, plus what it paid in new money for the vault's dollars (the strong edge's default payer), is the
remittance due less the loss carried; the old statement was false by that term under a peg or a band, and never asserted
under one. Leaving a peg between two presses clears its doubt, so the hot money no longer leaves at the dead peg's hazard
for one more month. The hot money's month (arrived, departed, on doubt) is saved, so Policy's "of which ... left on doubt"
survives a load. Trade's "net bought into the vault" counts the central bank's edge purchases, as it counted its defence's
sales. HoldersCheck pins the capital-flows array at its 19 slots. CurrencyRegimeCheck 5, 9 and 11 restated and extended,
12 and 13 new, each red on 0.8.22's model. Under Lean all of it is off: the playtest's trace is unchanged but for its run
time, so no ensemble. Saved, no format bump.

### 0.8.22 — XR2, the currency's regimes — 2026-10-22, ready, see docs/queue/XR2-notes.md

Policy › Money › The currency chooses how the D$ is held (★3): Float; Lean against falls, XR1's float and the default;
a Band whose edges the vault holds, its centre crawling; or a Peg to the US$, held while the vault meets the month's whole
balance. Beside every regime runs the shadow rate, where the float would be. A peg breaks (★4) when the vault would fall
under a month of imports, or by doubt at a chance that rises past a 10% gap, landing at the shadow plus a quarter of the
gap; the hot money leaves at that chance, and credibility loses half its way to the floor. Under a peg the dial is tied to
the US's rate with a spread to defend (★5); the autopilot aims at inflation, the currency or a mix (★13); the strong
edge's dollars are paid by the central bank in new money or by the treasury (★6). Trade › The currency's seventh force is
the hold. Lean computes every figure it did: the playtest's trace is unchanged, so no ensemble. CurrencyRegimeCheck
(new): 1,000 seeded pegs break at their summed hazard, median life 7.5 years, median break 31% (the study's 7.4 and 31%).
Saved, no format bump. ★ for Jerus: does a band break; a float's currency aim; the dent's size (XR2-notes §4).

### 0.8.21 — AN1, the price level's anchor — 2026-10-22, ready, see docs/queue/AN1-notes.md

The default city no longer hyperinflates under XR1's float. The city's goods - the dock's imports and exports, the band
its own goods trade in, a building's materials - are struck at a D$ that follows the D$ slowly: a 36th of the gap a month
in logs (EconomyManager.PASS_THROUGH), half of a move in two years, the world's own level at once; the D$, its parity and
every conversion of money are XR1's. Measured first on the default playtest, 17 runs (AN1-notes §2): the lag anchors from
a 24th a month slower, not at a 12th, and not on the grocery shelf alone; a shelf that falls as readily as it rises does
not anchor and adds nothing beside it. The playtest: prices 8.2 / 62 / 13,374 times founding near m600 / m1060 / the end
(0.8.19 13.8 / 12,225 / 2.9e9; 0.8.18 4.7 / 15 / 1,036), 625,815 people (10,980; 573,349), the D$ never at its guard.
Saved (DataSave.struckRate), no format bump. PassThroughCheck (new); CurrencyCheck 4 restated to the lean as built
(XR1-C ★1, "Keep as built"); three fixtures re-caused. Ensemble 24/26 (0.8.19 18/25). ★ for Jerus: the dial's value, land at the D$
itself, the map's "an old works moved" in a city that grows again (AN1-notes §5).

### Docs pass 2026-10-22 — over FR4, EG1, CL2, CL3, CD1, FX6, CD2, FX5, TD1, XR1 and CL4 — shipped, no tag

The daily docs pass by a fresh session over the eleven batches since the 2026-10-19 pass (abfca4fb..01f9bf98).
Comments, indexes and the record only. FX5 was 0.8.15 in seven comments (its number before CD1, FX6 and CD2 merged
first) and CD2 0.8.15 in docs/tree.md: both are their shipped numbers. What CD2 retired outside the files it touched:
the term loan, the underwriting spread on the city's paper (Rollover, BondMarket, Game's issue costs, Founding,
DebtManager's curve); XR1's retired push in CentralBank's defence; FR4's strip and port dues door said as done.
docs/tree.md has entries for YieldCurve, FreightView, TradeBand, TrackCurves, StreetCurves, TempDirs and CapChips,
the reshaped map, Earth and plan files' batches, 126 harnesses; README counts 127 run and lists tools/ensemble/; the changelog's stray separator gone; the maps
regenerated. Suite on its tree 121 of 127: main's six reds, nothing else (CurrencyCheck 4, MonetaryCheck's
allowance, HealthCheck's hunger line, SectorBooksCheck's imports by good, CentralBankCheck's spiral, LongPlaytest).

### 0.8.20 — CL4, slanted avenues and curved streets — 2026-10-21, ready, see docs/queue/CL4-notes.md

Four avenues at 45 degrees run out from the lattice node nearest the founding site, laid by every district's plan as
arterials wherever they cross an open cell - as the cell opens, or as ground is bought later - and drawn as one smooth
band, with roundabouts where five roads meet; a lattice street meets one only 8 steps from the next junction. The
shore's streets are drawn as curves (FX5's TrackCurves), and past 15 km a third of the long-block homes cells are
crescents, their cross street a quarter circle. No building that stands moves: the playtest's layout watch moved 0 of
171,104 boxes. Two faults on main fixed in commits of their own: the near view threw at a depot with no track (FX5's,
0.8.18), and the interchanges' loops were never drawn at the near view in the game (FR7's, MapTiles.copy()). The map's
alone: the trace is 0.8.19's, no ensemble. ★ for Jerus: six calls (CL4-notes §2), above all ★2 and ★3.

### 0.8.19 — XR1, the D$ as a float that follows its parity — 2026-10-21, ready, see docs/queue/XR1-notes.md

**The default city now hyperinflates unless the player stops it** (Jerus: "ship it as is, im going to test it to see
if player led behaviour can prevent hyperinflation"). The D$ is a level now: parity's trend, on the basket with rent
held to a third, followed one for one; where the real rate (60% of a change at once, at most 35%), the year's balance
and the vault's cover hold it; and a seeded market of its own, about 8% a year. Buying dollars now cheapens the D$, at
2% per 1% of a year's GDP, and the central bank's defence leans against a month's new fall the same way. Trade › The
currency is the study's board 1. With the rate following prices and prices following the rate, the default playtest
ends at 2.9 billion times founding's prices with the D$ at its guard, 10,980 people (main: 1,036 times, 573,349);
Rotterdam 284,646 (main 761,080). Under the autopilot the ensemble's cities live (213,000 at the median) at 4.5% a
year: 18/25 against 0.8.17's 23/25. Save format 43; older saves are refused. ★ for Jerus: XR1-C, the anchor's leads.

### TD1, the checks' temp folders deleted when they finish — 2026-10-21, shipped, no tag

Every harness, `GameFiles.scratch` and `LongPlaytest` made its temp folders
with `Files.createTempDirectory` straight into %TEMP% and never removed one:
about 95 a suite run, 37 GB on the PC in a week, the disk nearly full. All 93
calls now go through the new `TempDirs.create(prefix)`, the same call into
`%TEMP%\citybuildersim-tmp\<pid>`, which a shutdown hook deletes when the JVM
exits; the next JVM's first call deletes any folder there whose process is gone
(a run killed hard). Deleting never follows a link or leaves that folder. The
model is untouched: no ensemble, no version. CLAUDE.md now forbids
`Files.createTempDirectory` in a harness, and has a session delete its
`%TEMP%\cbs-<id>*` folders once its batch ships (24 left behind held 5 GB).

### 0.8.18 — FX5, the map's rail and roads finished — 2026-10-21, ready, see docs/queue/FX5-notes.md

Rail curves at its corners and the trains follow them: a new `TrackCurves` rounds every route's turn (3 plots) and
the tiles draw the same arcs, with the yard ladders as S curves, so no car swings on the spot. Trains are their real
length (a unit train 80 wagons, 1.24 km), only onto a siding that holds them and no yard's standing wagons, one a
siding, easing in and out; yards are laid along their line as long as the longest train the ground allows (48 plots
for a unit train, 33, 31, 15), across a cell's ring street, and drawn whole across tiles. A station alone now lays its
main line (his Halifax depot had no rails). One junction a crossing, mostly signals (a roundabout only where five
roads meet); the diagonal highway joins the grid as one carriageway; the world lines' labels apart; "Track" is
"Unmade street". The map's alone: the trace is 0.8.17's, no ensemble; the map's sidecar FORMAT 10, no save change.
★ for Jerus: eleven calls (FX5-notes §2), above all ★3, ★5 and ★2.

### 0.8.17 — CD2, the lineup and the auction — 2026-10-21, ready, see docs/queue/CD2-notes.md

The city sells bills (1-12 months), bonds (2-50 years, the coupon the clearing yield rounded down to an eighth, in
lines that reopen) and the amortizing bond at auction (spec-city-debt §2, §4); the term loan and the 0.75% spread
go. The households bid first at the auction's yield out of their savings (D32), the firms by their own rule (D33),
the world as hot money, the bank its risk capital (★6); one clearing yield, capped at fair + a point or Fill it,
priced on the curve so no round trip makes money. The firms and the world hold what they buy. The rollover and
automatic building each sell at their own cap: a short rollover is paid from cash and the advances, a short build
sale keeps its ask open. Borrow is the auction's screen. The study's model, rerun with the firms first: the tight
city's tail 28 bp, not 503. Save format 42. Traces move (default 573,349 people against 619,699, Rotterdam 761,080
against 834,678; one domestic sale between them, at fair); ensemble 23/25 against 22/25, A2 green. AuctionCheck.

### 0.8.16 — FX6, a district that buys land inside itself keeps its buildings where they stand — 2026-10-21, ready, see docs/queue/FX6-notes.md

Land bought inside a district no longer deals its buildings out again. A district's plan read its owned ground as
of today, so a purchase replayed every arrival on the larger ground; on the playtest's city at month 3,998, 2,069
of district (-2,-2)'s 2,946 buildings moved. The plan's ground is now dated, each plot the month the city bought
it, from the land's own records (`Input.boughtAt`). The plan starts on the ground its first batch stands on and
adds the land bought before each arrival's month: what stands never moves, and new arrivals take the new ground.
LongPlaytest is green again (its layout watch: 0 moves in 48 month pairs). The map's alone: the trace is
byte-identical to 0.8.15's, so no ensemble; no save change. ★1 for Jerus: ground bought inside a cell already
open gets no new streets, so less of it is built on (FX6-C). Harness: MapCheck 2c, PlanCheck 12.

### 0.8.15 — CD1, the curve from the dial the market expects — 2026-10-21, shipped, see docs/queue/CD1-notes.md

The city's curve no longer sits on the dial in parallel (spec-city-debt §3, D9). The market expects the dial to drift
back to what the city's rule would set at the inflation it expects, over six months (DRIFT_MONTHS, D15); each month's
forward is that plus the credit spreads, the zero rate compounds them and carries the term premium less QE. A hiking
city's curve inverts, a dial held low by hand no longer buys cheap long money, and a calm city prices as before: the
study's four cities to the hundredth. Every payment of every piece is discounted at its own month's factor, so a
piece's yield is solved from its price; every issue is priced the same way (the serial's coupon at par on the curve,
the term loan's face on its factors), at home and abroad, and no round trip makes money. The short end is unchanged.
Your rate shows the thirty-year rate in its new parts and where the market expects the dial. No save-format bump.
The traces move (default 619,699 people against 677,355; Rotterdam 834,678 against 711,024); the ensemble scores
22/25, H1 retired and A2 shipped as noise at its threshold, by Jerus's word (2026-10-21). Harness: CurveCheck.

### 0.8.14 — CL3, buildings that stay where they were put — 2026-10-21, shipped, see docs/queue/CL3-notes.md

The map's alone; the playtest's trace is byte-identical. Each district keeps a log of its count changes month by
month, saved in the map's sidecar (FORMAT 9; an older one loads into today's plans, box for box), and its plan
places the buildings in the order they came: the first batch as before, then each month's departures (the newest
leave, their lots freed) and arrivals (a month's types dealt in turn), each at the first place it fits, a cell
opened for the use of what opens it. Farms, power and water works, yards, car plants and truck depots open cells
from the district's far side; homes keep off mines; a cell's squares and boulevard are decided as it opens. A
district is full at PLAN_FILL (0.68) of its plots, and what spills keeps its month. His 1.7M city over a year:
41 buildings moved against 5,080; the playtest's ring gone (industry 7.0 km from the founding site, homes 10.2,
where they stood at 9.8 and 7.5). The playtest's city, out of ground, now turns away its newest houses at the edge
rather than its works: 3,570 packed without a street against 450. PlanCheck 11, MapCheck's sidecar sections and
LongPlaytest's layout watch are new.

### 0.8.13 — CL2, streets not a grid inside the blocks — 2026-10-20, shipped, see docs/queue/CL2-notes.md

The map's alone. Each homes cell, long blocks or squared (a boulevard's interior too), takes one of three street
layouts by a hash of the world's seed and its place: the comb as before, dropped crossings (T junctions, bends,
merged blocks, now and then a small green hole), or brick (the cross streets at alternating lines), a third each
(Jerus, the CL1 study's star 1). A cell a highway, railway, yard or reserve crosses keeps the comb: varied pieces
joined along a 45-degree highway drew a staircase. A merge in a varied cell takes up the stubs it leaves or is not
made. On his 1.7M city: dead ends 155 and buildings placed 17,987, both as before; a squared cell's streets 27.5%
of its ground against 30.6%. At the city's scale it still reads as a grid (CL4 changes that). PlanCheck 10 is
new; the trace is byte-identical to 0.8.12's, so no ensemble (EN1).


### 0.8.12 — EG1, deposits where the geology says — 2026-10-20, shipped, see docs/queue/EG1-notes.md

On the real Earth the fields now lie where the geology puts them (spec-earth2-terrain §8.5), so Jerus's Nova Scotia
cities no longer sit in a ring of oil and iron. Oil is drawn by each province's known petroleum a km2, with a tenth
of its class's weight where none is known; 40 estimated hand rows fill the US provinces and the Scotian Shelf, which
earth-1's USGS figures leave at nought. Iron keeps the shields' weight, a tenth elsewhere, and twenty in 96 recorded
iron districts (the Pilbara to Wabana). The Sydney coalfield reads its basin. An Earth field is three times wider and
a third as many, and a cluster stays on its own province's ground. Within 150 km of Halifax: iron 21 fields (417
before), oil on land none (53). earth-1.bin is unchanged. A generated world is drawn to the bit as before: the default
trace is byte-identical; Rotterdam's moves from month 1,464, when its city buys an iron field (711,024 people at the
end against 588,872). Save format 41 refuses an Earth save only. Harness: EarthDepositsCheck.

### EN1 — the ensemble only when the trace moves, in the tree, with a stored baseline — 2026-10-20, shipped untagged, see docs/queue/EN1-notes.md

The 80-run ensemble (16 seeds x 5 monetary policies) now runs only for a batch whose playtest trace moves (Jerus: "yes, do
that"), once, against the newest stored score, and main is never rerun. Its runner, until now only in the workbench zip
and in sessions' scratchpads, is `tools/ensemble/`: the driver; `ensemble.sh`, which snapshots `target/classes` and runs
five at a time on the PC (one under the cloud's lock); and the scorer, which reads pandas from `~/.cache/cbs-ensemble/py`.
`docs/ensemble/0.8.9.md` holds the first stored score: FR6's final run, still main's model. The scorer had counted 26
of its 27 criteria (A3's verdict was numpy's bool). Two premises out of date are restated: A2 from m240, past the
founding's rent boom, and H2 against the twin, as H1 is. So 0.8.9 scores 25/27, and S1 and R3 wait on Jerus (EN1-C),
with R3's fix proposed as RN1. A harness, EnsembleCheck, keeps the driver compiling against the tree. No game change.

### 0.8.11 — FR4, the freight screens — 2026-10-20, shipped, see docs/queue/FR4-notes.md

The last freight batch draws what FR2-FR7 built. Build's "Roads & transit" is Infrastructure again and holds the
ports and the Strategic Reserve, out of Industry; "Rail" is Freight (the rungs, the Rail Depot, the Truck Depot);
a berth's card says when it pays back, from the advice's own verdict. Sectors › Freight opens on a division strip
(All in legs, Haulage, Rail, Ports) and its Income page lists the shippers by division; every works' Operations
page shows How its tonnes move (each good's legs by mode, the bill by division, what a siding would save; Iron &
Steel's pellet plant by truck and with a siding). Infrastructure's railway and freight pages are one Freight page,
with a Ports page beside it; Policy has the port dues dial with its preview; Trade draws its band in legs. The
manual's freight pages are brought to 0.8.11. New pure models FreightView and TradeBand with their harnesses; one
saved tally (each sector's moves, read by nothing in the model). The trace is byte-identical to 0.8.10's.

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

### 0.8.0 — the merges after I14: the other assistants and the tools — 2026-10-13 to 2026-10-17, no version bump

MapCheck's bounds read on the wall clock where the thread's CPU clock is too coarse (Windows); AllChecks -f writes
and reads its logs in UTF-8; ship.sh takes its merge back when main moved on origin during the checks. ChatGPT and
Gemini given briefs of their own (openai-dontread.md, gemini-notes.md), their work on branches, never main:
ChatGPT's current-code handoffs for R0 (docs/studies/research-1900.md) and CD1-H (spec-city-debt.md 8.1); Gemini's
GS1, ten of Stale's soft findings, six corrected in review, and the Prose fixes rules it taught. RV1: tools.Names
(the names a branch's new text uses that the tree does not declare, run by ship.sh on their branches) and the
assistant-review subagent. DeepSeek as the batch's coder (Jerus, 2026-10-17): tools/deepseek/agent.py and
docs/deepseek.md, CLAUDE.md's step 3; tried on an FR0 slice (6fd8dda, reverted, then restored as FR0's slice G).

### 0.8.0 — I14, the release that finishes industry: the materials on the map — 2026-10-13, shipped, see docs/queue/I14-notes/fixI14-notes.md

The oil chain (0.7.76-0.7.98) and the five materials chains (0.7.110-0.7.143) are whole, and now drawn as
themselves: every type of iron, oil, coal, stone and copper stands on its resource's sites by its type of field, on
as many as it takes (a Large Open-Pit Mine on twenty hematite sites, counted once) - coal's, stone's and copper's
first types had been placed nowhere. Each kind has a look of its own (open pits in the ore's colour, a shaft,
quarries, a leach plant, the wash plant, camps, planted forest), each processing industry's works one campus in its
colours (the arc furnaces keep their places), and a zone's districts are tinted in its sector's colour. Found: a
footprint of a tile's 32 plots threw the district's plan; capped at a cell's 31. MapCheck 15 holds it. Sidecar
FORMAT 7; no save change; playtests byte-identical. Gate: BuildMenuCheck 139, Stale 0 firm, MapCheck and PlanCheck
green; EarthTerrainCheck 8's Earth screen bound red, as on main on the same machine.

### 0.7.143 — I12, the materials pictograms drawn — 2026-10-13, shipped, see docs/queue/I12-notes/fixI12-notes.md

The nine materials industries' Operations pages open on their month as a picture, the refinery's pictogram made
general (spec-materials 2.13): a mine's fields as vessels filled to what is left, or a works' feeds by where they
came from, imports hatched; the works in ranks, each with its run and in a word the gate one more would fail; the
tank of what left the works under the industry's roof; who took it; the power and the month's note - every ribbon
one scale, in tonnes, the forest's in cubic metres. New model class IndustryPicture (pure), painted by
ui/SectorScreen with the refinery's painter. IndustryViewCheck 12 holds it: ribbons to scale, every label's figure
the view's, nothing off the frame at 1,389 x 868. A words probe and nine headless renders are in
docs/queue/I12-notes/. No save change. Gate: suite 109 of 109, BuildMenuCheck 139, Stale 0 firm, playtests
byte-identical.
