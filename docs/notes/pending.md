# Pending for the project record

Lines the docs pass could not write to the project itself. The next Cowork
session moves them over and empties this file. Newest at the top. It holds
changelog and todo lines for the claude.ai project, which the sessions that
write them cannot reach.

## 0.8.4 to 0.8.10 and TM1 (2026-10-18/19) - for `claude/changelog.md`

Written by the docs pass of 2026-10-19 from each batch's notes in docs/queue/, newest first.

**0.8.10 - a new city after a reformed one founds at unit 1 (FX4).** Not a regression of the freight batches: the
denomination was a final field that `Game.buildWorld()`, the door New Game, the load path and the constructor share,
never reset, so a city founded in a window that had loaded a reformed city kept its unit and money constants, and the
rate ran toward a parity of 1e-7. `buildWorld()` now resets the denomination and re-seeds the fifteen money constants
at unit 1 first (`seedMoneyConstants()`, the load path's list made one method). Diagnosed on scratch copies of his
autosave and slot 2, never written. Gate: 120 harnesses, 119 passed, ScaleCheck's "...and it builds" main's red;
the trace byte-identical to main's. See docs/queue/FX4-notes.md.

**0.8.9 - the railway carries the city's own bulk, with the port yard (FR6).** Of every bulk tonne bought at home the
siding's part (.75) is offered the railway after the boundary's trade, on the room the track, sets, stations and
sidings leave; with a station and a sea terminal standing, .45 of the berths' bulk rides the port yard quay to door.
City tonnes burn a third of a truck's diesel and join the revenue the quote is struck over; the planner weighs a
works' legs at the trucks' and the railway's quotes. A reset rail net clears its hub. HomeRailCheck new. Gate on the
merged tree: 120 run, 119 passed, ScaleCheck's line main's. See docs/queue/FR6-notes.md.

**0.8.8 - trucks on the map (FR5).** Haulage's loads drawn as trucks at the near view alone, while the setting is on
(default on; Jerus's D21): `TruckSchedule` in TrainSchedule's shape from Freight's saved tally of the month's truck
tonnes by load, one truck per N loads to 1,000, on the highways from the world line and on FR7's access roads to each
gate; `TruckShapes` the body and colour. The model gains one tally nothing in it reads; the trace byte-identical.
TruckCheck and TruckRouteCheck new. Suite: 119 run beside FR6, 117 passed - ScaleCheck's line, and StaleCheck's
README harness count, put right. See docs/queue/FR5-notes.md.

**0.8.7 - the landlord port and the Bulk Berth (FR3).** The city builds and keeps the sea terminals; Freight's third
division, Ports (`sectors/PortDivision.java`), works them on its payroll, bills every shipper's sea tonnes at the
kind's quote, and pays the city port dues the same month (a revenue line, a Government screen row). The dues' default
is the annuity on the kind's terminals at the 30-year rate, times the new dial `TaxPolicy.portDues`. The Bulk Berth
(id 142), Handysize and Supramax ships, a berth that pays back on the dues. LandlordPortCheck new. Suite: 117 run, 116
passed, ScaleCheck's line main's. See docs/queue/FR3-notes.md.

**0.8.6 - track that goes somewhere, and roads for two needs (FR7).** The Rail Depot (id 141, 50,000 t a month); a
station before any spur or line, each rung against its own shortfall; goods through a station only. The overbuild was
real and is gone. The highways carry the through trips and the freight gates' road load, the streets the rest: two
measures, two NEEDS YOU rows; a gravel road's repairs rise past 200 vehicles a day; gravel drawn grey; interchanges'
ground (InterchangeGround) and access roads to every yard, terminal and Truck Depot. RailDepotCheck new. Suite: 116
run, 115 passed; --timing alone 7 of 8; ScaleCheck's ten-billion copy builds no home (FR7-C, Jerus's call). See
docs/queue/FR7-notes.md.

**0.8.5 - the Freight sector (FR2).** Freight in Rail's slot of `Sectors.KEYS`, its railway the Rail division;
Haulage, its trucks, bills the city leg of every bulk tonne once at a quote its own books set; the Truck Depot (id
139), TRUCKS and the Heavy Truck Plant (id 140). SAVE_FORMAT 40 and OLDEST_LOADABLE_FORMAT 40. HaulageCheck new.
Suite: 115 run, 113 passed - ScaleCheck (FR2-C) and CopperRefiningCheck's Fabrication Shop line. See
docs/queue/FR2-notes.md.

**0.8.4 - the freight map's faults (FX3).** From Jerus's eye check of 0.8.3: one yard layout read by the drawing and
the trains (`TileRaster.yardLayout()`), the fan on its line's longest clear stretch; diagonal highways drawn whole;
junctions by run; one roundabout on a crossing of wide boulevards. The map's alone; the trace byte-identical. Held by
RailDrawCheck 21, MainLineCheck 12 and JunctionCheck 10 and 11. See docs/queue/FX3-notes.md.

**TM1 (untagged) - soft timing.** `Timing` is the one helper every timing assertion goes through: under
`-Dcbs.timing=soft` a figure over its budget says SOFT and is not counted, so two batches' suites run side by side;
the check scripts set it while another batch's JVM or the game runs (`tools/jvms.ps1`), and `--timing` runs the
timing harnesses alone and hard, as the ship gate does. TimingCheck new. No game change. See docs/queue/TM1-notes.md.

## 0.8.4 to 0.8.10 - for `claude/todo.md`

- Section 0, under the manual's entry: its headline numbers moved - the build 0.8.10, SAVE_FORMAT 40 (FR2, the
  oldest loadable 40), 143 buildings (the Truck Depot 139, the Heavy Truck Plant 140, the Rail Depot 141, the Bulk
  Berth 142), 119 harnesses; the sectors still twenty-four, Freight in Rail's place, with its Haulage and Ports
  divisions; a new dial, the port dues (`TaxPolicy.portDues`, FR3).
- What each batch left open is under Pending in QUEUE.md (FR2-C, FR7-C, FR3-C, FR5-C, FR6-C, FX3-C, TM1-C).

## Jerus's answers of 2026-10-18 - for `claude/todo.md`

- CONFIRMs closed ("rec all"): FR0's, FR1's and FX2's stars, all as built; I14 ★3, ★4, ★6, ★7 as built; I12 ★2
  (copper in tonnes of the good) as built. Left: whether I12 ★5's three lines read well, with his eye check.
- CONFIRM, the zone palettes: one, the map's per sector, and Zoning reads it - batch ZT1, queued (docs/queue/ZT1.md).
- CD1's design call: the curve of spec-city-debt §3.2 (§8.1 records it). research-1900 §11 and research-design §8: all rec.
- Queued: CC1-M (the map lost on a save) as its own batch, and I2b (the whole-cost test on every order). Settled: the
  plantation's size (the Woodlot), a 10B city's books (sampled), spec-scale ★3 (batch I's crowding premium).
- QUEUE.md's FX1-2 row removed: `batch/FX1-with-preflight` (e821d7f) was already in main.

## 0.8.1 (batch FR0, 2026-10-18) - for `claude/changelog.md`

**0.8.1 - the map's network (FR0), the first freight batch.** The land's gateway: the bearing with the longest run of
land from the founding site (on the Earth, toward the nearest larger place along land), ties by the site's hash, none
for an island founding; each net's spine laid toward it, its arm grown first, and the world's highway and line drawn
on past the city's ground, "to the world". The main line from the rail gateway to the freight station, double with a
Freight Line. Rail drawn as rail (ballast, sleepers and two rails, four on a double main line; level crossings; an
arterial over the main line a bridge), yards as fans of eight sidings with a shed, ore or boxes and wagons, in an
eleventh class, TRANSPORT; junctions by rule (signals, roundabouts, diamonds, stacks, a highway's end); the far view's
skeleton and the legend. Each slice drafted by DeepSeek V4 Pro through tools/deepseek and reviewed by Claude. Found:
half of twelve generated worlds' default sites have no gateway (their land runs under 10 km); a diamond on a tile's
edge was drawn in half (JunctionCheck 8). Map sidecar FORMAT 8; no save change (39); playtests byte-identical. Gate:
113 of 113 on the merge on the PC, earth-1.bin named, EarthTerrainCheck 8's screen 27.8 ms against 80. See
docs/queue/FR0-notes.md.

## 0.8.1 - for `claude/todo.md`

- ~~FR0, the map's network~~ - done 2026-10-18 (0.8.1); what it did not build is FR0-L under QUEUE.md's Pending
  (access roads D17, sidings into the bulk works D14, the cloverleaf's and trumpet's ground D19, kerb corners).
- CHECK (Jerus, on the PC): FR0-C - the world line and its label on the expanded map (JavaFX, never run in the
  cloud), the rail, yards and junctions at the near view, beside docs/queue/FR0-notes/*.png; and his calls on the
  stars (the gateway arm grown first, ★3; the yards' looks, ★4-5; TRANSPORT's colour #5e7d8c, ★6).
- ~~FOUND: EarthTerrainCheck 8's Earth screen bound red on the cloud machine~~ - settled 2026-10-18: 27.8 ms on the
  PC in FR0's ship suite against MapCheck.SCREEN_MS's 80; a shared VM's timing, not the batches'.
- FOUND (the FR0 docs pass): tools.Stale does not see a record's components, so a comment naming one reads as an
  unresolved member (gemini-notes.md's Prose fixes says so; CityMap's Type.campus(), sites(), shore() and sea() are
  four of its soft leads). Teach Stale the record header, or leave the soft list as it is.
- FOUND (the FR0 docs pass): MapCheck's sections 1, 8 and 14 print titles joined with + (1's with MONTHS), so
  docs/harnesses.md shows no heading for them - 8 and 14 want one literal each, 1 a reading in tools.Maps (the
  I14 pass's line named 14 alone). And MapCheck.CPU_STEP_MS's sentence calls 0.1 ms "a twentieth of the quickest
  stamp timed", where the comment above it gives the quickest stamp as 0.4 ms: a quarter, or another stamp?
- FOUND (the FR0 docs pass), TileRaster, none of it behaviour: its header draws a track plot below 4 px in
  RAIL_DARK, but raster()'s plotColour() still fills RAIL_LINE there (the view never asks raster() below 4 px; a
  harness can) - which is meant?; RAIL_DASH is read by nothing since 0.8.1; FR0's three one-line dial banners at
  the top take in older dials under them (JUNCTIONS over TICKS_FROM to LINES_FROM, YARDS over SMALL_MOST to
  PACKED_DIM), a move of declarations; and CityMap's RailPicture, railPicture(), wagonTracksOf() and
  freightStation() sit inside THE OIL AT SEA, between Pipe and AtSea (left for FR1, which reworks them).
- Section 0, under the manual's entry: the build is 0.8.1; the harnesses are 112 (113 run with the playtest), 108
  at 0.8.0; save format still 39 - for the manual's next edition (a milestone).

## 0.8.0, after I14 (2026-10-13 to 2026-10-17, no version bump) - for `claude/changelog.md`

**0.8.0, the merges after I14 - the other assistants and the tools.** MapCheck's bounds read on the wall clock where
the thread's CPU clock is too coarse (Windows); AllChecks -f writes and reads its logs in UTF-8; ship.sh takes its
merge back when main moved on origin during the checks. ChatGPT and Gemini given briefs of their own
(openai-dontread.md, gemini-notes.md), their work on branches, never main: ChatGPT's current-code handoffs for R0
(docs/studies/research-1900.md) and CD1-H (spec-city-debt.md 8.1); Gemini's GS1, ten of Stale's soft findings, six
corrected in review, and the Prose fixes rules it taught. RV1: tools.Names (the names a branch's new text uses that
the tree does not declare, run by ship.sh on their branches) and the assistant-review subagent. DeepSeek as the
batch's coder (Jerus, 2026-10-17): tools/deepseek/agent.py and docs/deepseek.md, CLAUDE.md's step 3; tried on an FR0
slice (6fd8dda, reverted, then restored as FR0's slice G).

## 0.8.0 (batch I14, 2026-10-13) - for `claude/changelog.md`

**0.8.0 - the release that finishes industry; the materials on the map (I14).** The oil chain (0.7.76-0.7.98) and
the five materials chains (0.7.110-0.7.143) are whole, and now drawn as themselves: every type of iron, oil, coal,
stone and copper stands on its resource's sites by its type of field, on as many as it takes (a Large Open-Pit Mine
on twenty hematite sites, counted once), laid as LandManager.fits() packs them - coal's, stone's and copper's first
types had been placed nowhere. Each kind has a look of its own (open pits in the ore's colour, a shaft, quarries, a
leach plant, the wash plant, camps, planted forest), each processing industry's works one campus in its colours (the
arc furnaces keep their places), and a zone's districts are tinted in its sector's colour with the legend naming it.
Found on the way: a footprint of a tile's 32 plots threw the district's plan; capped at a cell's 31. MapCheck 15
holds it, a screen over the industry 32 ms of thread CPU. Sidecar FORMAT 7; no save change; playtests byte-identical.
Gate: BuildMenuCheck 139, Stale 0 firm, MapCheck and PlanCheck green; EarthTerrainCheck 8's Earth screen bound red, as
on main on the same machine. See docs/queue/I14-notes/fixI14-notes.md.

## 0.8.0 - for `claude/todo.md`

- ~~I14 (UI), 0.8.0: the map and the release~~ - done 2026-10-13 (0.8.0): with it I5's 20-site works drawn as one
  mine, I6's coal mines and preparation plant and I7's Dimension Stone Quarry drawn as plain MINING, and I9's ★10
  (coal's, stone's and copper's first types placed nowhere); W1's note stays open (below).
- CHECK (Jerus, on the PC): the materials on the map - a pan over an industrial city, a zone's tint - beside
  docs/queue/I14-notes/png/ (QUEUE.md Pending, I14-C; fixI14-notes §8).
- CONFIRM: a plantation drawn on a cell's 31 x 31 plots of its 105 x 105 (I14 ★3); the arc furnaces and mini-mills
  not gathered into steel's campus (★4); sited works grey in the far views (★6); no toggle for the zones' tint (★7).
- W1's walled street piece over a dense iron cluster stays open (QUEUE.md Pending, W1-P).
- CONFIRM: the map's zone tint is LandMap.ZONE_COLOURS, a palette apart from ZoningScreen.ZONE_COLOURS, so a zone's
  colour on the map is not its colour on Policy > Zoning (the I14 docs pass) - one palette, or two on purpose?
- FOUND (the cloud orchestrator, 2026-10-13): EarthTerrainCheck 8's Earth screen bound (MapCheck.SCREEN_MS, 80 ms)
  went red on the cloud machine mid-session, on main without I12 too (81 ms at d4e12dc; 105-109 ms on the batches'
  trees), having passed in the session's first two full runs: a timing on a shared VM, not a batch's. On the PC it
  should pass; if it does not, the bound or the Earth screen's raster wants a look. MapCheck 14's title is two joined
  strings, so docs/harnesses.md shows no heading for it (the docs pass, left: outside I14's change).
- Section 0, under the manual's entry: the build is 0.8.0, the release that finishes industry (save format still 39,
  harnesses still 108) - for the manual's next edition (a milestone).

## 0.7.143 (batch I12, 2026-10-13) - for `claude/changelog.md`

**0.7.143 - the materials pictograms drawn (I12).** The nine materials industries' Operations pages open on their
month as a picture, the refinery's pictogram made general (spec-materials 2.13; the study's mockups 1 to 5): a mine's
fields as vessels filled to what is left, or a works' feeds by where they came from, imports hatched; the works in
ranks, each with its run and in a word the gate one more would fail, a flow that skips a rank drawn as a band through
its column; the tank of what left the works under the industry's roof (pit, camp, furnace, kiln, mill); who took it;
the power and the month's note - every ribbon one scale, in tonnes, the forest's in cubic metres - and a strip of cells
under it. New model class IndustryPicture (pure: the month traced end to end, then laid out), painted by
ui/SectorScreen with the refinery's painter. IndustryViewCheck 12 holds it: ribbons to scale, each node the view's
figures, every label's figure the view's, nothing off the frame at 1,389 x 868. A words probe of every string and nine
headless renders are in docs/queue/I12-notes/. No save change. Gate: suite 109 of 109, BuildMenuCheck 139, Stale 0 firm, both playtests 8/8 traces byte-identical.

## 0.7.143 - for `claude/todo.md`

- CHECK (Jerus, on the PC at 1,389 x 868): the nine materials Operations pictures by eye, beside
  docs/queue/I12-notes/png/ and fixI12-words.txt (QUEUE.md Pending, I12-C).
- CONFIRM: copper drawn in tonnes of the good, not of copper contained as the mockups say (I12 ★2: the model holds no
  copper content); the three lines over a works (I12 ★5).
- Section 0, under the manual's entry: the build is 0.7.143 (the manual's header says 0.7.135; save format still 39,
  harnesses still 108); the nine materials industries' Operations pages open on a picture now, for its next edition.
