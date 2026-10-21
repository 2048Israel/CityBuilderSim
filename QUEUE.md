# The queue

The batches waiting to be done, in order, and nothing else: one line each and
a link to its brief. Jerus, 2026-10-11: "a proper md file with just the
batches queued, just indexed and as soon as a batch is done its gone from
that md fully ... just minimal info, just with links or something, not actual
stuff".

- **The top row is next.** A new batch goes on the end unless Jerus places it.
- **Done: delete the row**, in the merge that ships it. What shipped is the
  changelog's to say; this file keeps no history.
- **Not done, or done in part: delete the row and put what was NOT done under
  Pending**, with why - it failed, it waits on Jerus's decision, a caveat, a
  bug.
- **A Pending row leaves when Jerus reviews it**: back into the queue, or
  dropped. Nobody else moves one.
- **No detail here.** The brief holds it - a study in `docs/studies/`, a note
  in `docs/queue/`, or a file in the project, named.

## Queued

| batch | what | brief |
|---|---|---|
| FR4 | The freight screens, and Build's Infrastructure and Freight tabs | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| EG1 | Deposits where the geology says: oil by known petroleum, iron on shields and recorded deposits, fewer and bigger fields, the Sydney coalfield and the Scotian Shelf by hand rows (Jerus, 2026-10-20: "Queue it next after FR4", ahead of the industrial review it waited on) | [spec-earth2-terrain §8.5](docs/studies/spec-earth2-terrain.md#85-the-recommendation-eg1-deposits-where-the-geology-says-d20-your-call) |
| CL1 | Study: the city's layout - streets not a grid, buildings that stay where they were put, uses that do not ring; its batches queued next when it finishes (Jerus, 2026-10-20: "study, and then add teh batch") | [CL1](docs/queue/CL1.md) |
| CD1 | The city's debt: the curve from the dial the market expects, every payment priced on its month | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD2 | The city's debt: bills, bonds in reopened lines, the amortizing bond, sold at auction | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD3 | The city's debt: effective interest, carrying value, gain or loss on retirement | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD4 | The city's debt: the buyback as a reverse auction, callable lines and the call | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD5 | The city's debt: floating-rate notes and inflation-linked bonds | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| CD6 | The city's debt: the screens - the book as a desk, a line's page, Close it now, Borrow as an auction, Your rate | [spec-city-debt §8](docs/studies/spec-city-debt.md#8-batches) |
| R0 | Research: the 1900 economy and the calendar from 1900 | [research-1900](docs/studies/research-1900.md), [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R0b | Research: causes of death in Health - typhoid, summer diarrhoea, outbreaks, TB, home births, pneumonia, the 1918 influenza | [research-1900 §2.7](docs/studies/research-1900.md#27-health) |
| R0c | Research: the 1900 mechanics - horses on farms and road freight, lamp oil and heat as goods, steam recipes | [research-1900 §4](docs/studies/research-1900.md#4-new-mechanics-a-1900-city-needs) |
| HY1 | Hydro Station, a template that needs a river and a drop on the Earth's map; not research, it goes with the electricity run | [research-1900 §4](docs/studies/research-1900.md#4-new-mechanics-a-1900-city-needs) |
| R1 | Research: recipes and equipment as data | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R2 | Research: the catalogue and the world | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R3 | Research: posts and discoveries | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R4 | Research: prizes and the Research Lab | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| R5 | Research: the screens | [research-design §9](docs/studies/research-design.md#9-for-the-implementer) |
| PT1 | Eleven small fixes to screens, words and the log, from a played city | [PT1](docs/queue/PT1.md#pt1) |
| PT1-M2 | The "nowhere to build" notice again each year the businesses wait on ground (Jerus, 2026-10-12: "a notice repeated") | [M2](docs/queue/PT1.md#m2) |
| PT1-M3 | The advice card carries automatic building's payback verdict in its own words (Jerus, 2026-10-12: "yes do rec") | [M3](docs/queue/PT1.md#m3) |
| PT1-M4 | The works department's free 400 points retired for a starting depot, so Construction earns only for work its people do (Jerus, 2026-10-12) | [M4](docs/queue/PT1.md#m4) |
| ET1 | Earth 2 in the game, and drawn: earth-2.bin read, slope's greys, snow and the shallow sea on the map, real depth for E4's guess | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| ET2 | Buildable by slope: buildings on flat and foothill only, a foothill site's preparation as builders' work, mines, quarries and wells on any ground; streets and runs refuse ice as buildings do (E7 ★10) | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| ET3 | A price for each class of ground, and the land line by class | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| ET4 | Highways and rail priced by slope: cuts, viaducts, tunnels | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| ET5 | Landmasses and the island rule | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| ET6 | Bridges: short crossings to 1,020 m over fresh or shallow water, long bridges as all-or-nothing orders that link landmasses | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| FW1 | A refused load (a save below the floor or from a newer build) still rebuilds the world first: `loadGameSave()` calls `buildWorld()` before reading, so from the pause menu the city in play would be replaced by a blank one though nothing loads; reachable by code only, the load list's button is off | `todo-archive.md`, FOUND ON THE WAY IN 0.7.107 TO 0.7.109 (P7); `runs/fixP7-notes.md` §7 |
| FW2 | Two sheets read stale after a load: the bank's sheet re-reads its deposits (the borrowings' split moves $131.9M in the test player's city at month 364) and the books' sheet reads cash as it stands but debt as last pushed (47 of 408 sector-months in city600 differ by more than $1k) | `todo-archive.md`, FOUND ON THE WAY IN 0.7.70 TO 0.7.84 (F-S1-1, F-S1-2); `runs/fixS1-notes.md`, `fixS2-notes.md` §7 |
| FW3 | Road tiles cut by a river count their whole grid: a gravel-only tile is dealt road its hub's bank cannot hold; count the grid from the hub's side | `todo-archive.md`, FOUND ON THE WAY IN 0.7.70 TO 0.7.84; `runs/fixN3-notes.md` §7 |
| FW4 | Automatic building is fragile in a young city (`-Dplaytest.autobuild=true`: an order refused for $0.28M at month 60 left 2,626 people; how often a young city meets one is unmeasured), and PlanCheck with a save named fails "every district keeps every check" on both his saves (not in the suite) | `todo-archive.md`, FOUND ON THE WAY IN 0.7.87 TO 0.7.92; `runs/fixRD4-notes.md` §7 |
| FW5 | Two reads miss litres, and one check counts them twice: the railway draws its diesel before the strike wipes the rows (his 1008: 659M of 1,725M L), `Markets.draw()` records the forecourts' and vans' fuel before `startMonth()` wipes the trades, and OilCheck 6's "L shipped" adds exportBound and exported (seen, not fixed) | `todo-archive.md`, FOUND ON THE WAY IN 0.7.93 TO 0.7.98; `runs/fixO12-notes.md` §2, `fixO14-notes.md` ★O14-4 and §7 |
| FW6 | A reload is not exact a month on: 12 of 229 series differed by at most 8.3e-5 at 0.7.49 (sector net incomes, `bankDeposits`, the sectors' energy ratio, Retail's purchase budget the candidates), city2400 off 0.23k and his old city -7,447k at 0.7.64 | `todo-archive.md`, FOUND ON THE WAY IN 0.7.46 TO 0.7.49 (the ★2 residual) and 0.7.55 TO 0.7.64 (left by the batches); `runs/fixA-notes.md`, `fixK-notes.md` §7 |
| FW7 | `AllChecks -q` hides a second failing assertion inside a known-red harness (HealthCheck's upkeep check showed only as "HealthCheck FAILED"): a per-label baseline for the known reds would catch it | `todo-archive.md`, FOUND ON THE WAY IN 0.7.42 TO 0.7.45; `runs/ui23-notes.md` §9 |
| FW8 | WHAT IS BITING has no zoning flag (a zone short of what it wants would be one); the map's tint of a zone's districts is I14's | `todo-archive.md`, FOUND ON THE WAY IN 0.7.107 TO 0.7.109 and 0.7.117 TO 0.7.120; `runs/fixZ2-notes.md` §7 |
| FW9 | ScaleCheck's copies lose about 0.85% a month because the founding care and police endowments do not scale, and the scaler leaves the bond market, the exchange, the equity register and the fund unscaled | `todo-archive.md`, FOUND ON THE WAY IN 0.7.55 TO 0.7.64 and 0.7.53 TO 0.7.54; `runs/fixH2-notes.md` ★9 |
| FW10 | A full-screen chart with a wrapped legend is a row too tall: `TimeChart.heightBesidePlot()` assumes a one-row toolbar, so the foot is cut | `todo-archive.md`, FOUND ON THE WAY IN 0.7.40 AND 0.7.41; `runs/ui21-notes.md` §8 |
| CC1-S | Keep care essential even when it is not financially self-supporting; investigate why seeds 4 and 5 still stall around 3,000 people despite care, school and pensions | `runs/fixCC1-notes.md` ★ |
| CC1-M | The city map did not survive a save once: LongPlaytest -Dplaytest.childcare at m1,184 (slot 3), a run the map sidecar has not met before; not childcare's - nothing in the model reads the map; MapCheck's area (Jerus, 2026-10-18: its own batch) | `runs/fixCC1-notes.md` 7 |
| I2b | The whole-cost test (A) on every other sector's orders, through `Game.Slices`, as SpreadPlanner's users got it at I2 (Jerus said "every order"; Jerus, 2026-10-18: yes) | [spec-materials §1](docs/studies/spec-materials.md#1-summary-for-jerus) |
| ZT1 | One palette for zones: Policy > Zoning colours a zone by its sector, as the map's tint does, so a zone is one colour on both (Jerus, 2026-10-18) | [ZT1](docs/queue/ZT1.md) |
| I13-L | The landlords' card asks the mortgage lender's test, and the land office's Ore card covers every ore, not iron's alone (Jerus, 2026-10-18) | [I13-L](docs/queue/I13-L.md) |
| SC1 | ScaleCheck's "...and it builds" read on a city that needs homes: its fixture city has 6,768 places for 5,734 people, so neither it nor its copy builds (Jerus, 2026-10-20: "Queue a check fix") | [SC1](docs/queue/SC1.md) |
| FR8 | The trucks' share dealt per industry, the newest demand last, so a giant works' arrival shortens its own sector, not the town's (Jerus, 2026-10-20, on FR2's ★5: "pool per industry") | [FR8](docs/queue/FR8.md) |
| FR9 | One truck, one whole trip: in on the highway, off at a ramp, along the streets and down the access road to its works' gate, a district's trips by its tonnes (Jerus, 2026-10-20: "Queue it") | [FR9](docs/queue/FR9.md) |
| TM2 | The ship gate's timing waits for a quiet minute, not one empty look, so it never starts in the gap between another runner's JVMs (Jerus, 2026-10-20: "Queue a small fix"), and DeepSeek's `agent.py` timing soft beside other runs ("Add it to TM2") | [TM2](docs/queue/TM2.md) |
| FW11 | No street piece walled in by buildings: over a dense iron cluster `DistrictPlan.join()` never walks over a building, so an enclosed piece stays cut off (was W1-P; Jerus, 2026-10-20: "Queue the fix") | [FW11](docs/queue/FW11.md) |
| FX5 | The map's rail and roads finished: curved rail corners and trains on them, real-length trains with yards and depots that hold them, trains only onto clear sidings, one roundabout a wide crossing, mostly signals, tidy diagonal ends, the legend's "Railway", the world lines' labels apart, a railway that is a depot alone drawn with its main line (Jerus, 2026-10-20, on FX3's and FR7's renders and his new city) | [FX5](docs/queue/FX5.md) |

## Pending

Not done, and waiting on Jerus.

| item | what is left | why it waits | brief |
|---|---|---|---|
| FR7-C | Settled 2026-10-20: ScaleCheck's "...and it builds" is SC1 ("Queue a check fix"); the renders checked ("Look right"; the overlapping world-line labels into FX5); ★7, ★8 and ★9 kept ("Keep all"); in his new Halifax city at month 477 the streets, gravel and paved, kerb corners, signals and the access roads checked ("Looks right"), and its first Rail Depot found drawn with no track, world line or trains, a fault, into FX5 ("Into FX5"). Left, in the game: a works' siding and the main line once the city has a Rail Spur or Freight Line; a cloverleaf's loops once two highways cross; both NEEDS YOU rows (STREETS and HIGHWAYS) in a big city. Not measured on his Halifax city: the legend's surplus. Left in the code: the access road sees only its own district (no ramp of its own, none where the cells have not opened) | his eye check | [FR7-notes](docs/queue/FR7-notes.md) |
| FR5-C | FR5's renders (`docs/queue/FR5/`) checked 2026-10-20 (Jerus: "Look right"), and its calls kept but ★5 and ★6, which FR9's whole trips replace ("Keep the rest"). In the game, his new Halifax city at month 477: the access roads' trucks, the legend's line, none a level out or with the setting off, checked ("Looks right", 2026-10-20). Left: trucks on a highway, once the city has one, and the frame rate panning a big city at the near view (not timed on the PC's game). Not built: trucks on the streets between works (FR9 takes the trips); the depots as where trucks start the day; a truck's hover | his eye check | [FR5-notes](docs/queue/FR5-notes.md) |
| FR0-C | FR0 checked by eye on the PC: the rail, yards and junctions at the near view (his check of 0.8.3, 2026-10-18, found the yards' and the diagonal highway's faults: FX3); headless PNGs of MapCheck's yard city are beside the notes. (EarthTerrainCheck 8's screen time is settled: 27.8 ms on the PC in the ship's suite, 2026-10-18.) His calls on the batch's stars (the gateway arm grown first, the yards' looks, TRANSPORT's colour) are taken: all as built (Jerus, 2026-10-18, "rec all"). His eye check of 2026-10-18 found the world line over the sea and its label clipped, a highway stub alone in a block and a glyph on a highway: FX2 (0.8.3) settled the first three and found the glyph a diamond, by FR0's rule. His second eye check found the second yard's fans off its line, the diagonal highway as blocks and strips of junction marks: FX3 (0.8.4) settled all three (a line crossing a yard joins it at the throat; a 45-degree stretch is never a stack; a road two plots wide is one road), and found the marks at his arterials' crossings FR0's signals, drawn as meant | his eye check of 0.8.4 | [FR0-notes §2, §5](docs/queue/FR0-notes.md), [FX2-notes](docs/queue/FX2-notes.md), [FX3-notes](docs/queue/FX3-notes.md) |
| FR1-C | FR1 checked by eye on the PC: the trains' pace on the boats' clock, and the legend's train; headless PNGs of MapCheck's yard city are beside the notes. His calls on the batch's stars (past 30 trains a month each drawn train stands for several, a locomotive at each end, no trains in a city with no gateway, bulk kept off the station while it handles boxes) are taken: all as built (Jerus, 2026-10-18). His eye check of 2026-10-18 found a train over open sea in a V: FX2 (0.8.3) runs the trains in from where the track meets the city's edge on land. His second found a train across a yard's sidings: FX3 (0.8.4) routes the trains as the yard is drawn, onto sidings along its line. A train standing over the yard's own standing wagons (★6): settled 2026-10-20, batch FX5 (Jerus: "Queue the fix") | his eye check | [FR1-notes §2, §5](docs/queue/FR1-notes.md), [FX2-notes](docs/queue/FX2-notes.md), [FX3-notes](docs/queue/FX3-notes.md) |
| FX2-C | FX2 checked by eye on the PC: his Halifax railway's line to the world now leaves north from the city's north-west and its trains run in on corridor 2's track; his highway gains a line south; a highway stub that reaches no arterial ramp is not drawn; headless PNGs of his city are in `docs/queue/FX2/`. His calls on the batch's stars (FX2-notes §3) are taken: all as built (Jerus, 2026-10-18) | the rest of his eye check (his check on 2026-10-18 confirmed the line to the world and the highway; what else it found FX3 (0.8.4) settled) | [FX2-notes §2, §5](docs/queue/FX2-notes.md) |
