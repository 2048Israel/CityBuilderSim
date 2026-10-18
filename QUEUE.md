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
| FX3 | The freight map's faults Jerus's second eye check found: the second yard's ladders off the line, a train across the sidings and a track into a yard's side, a 45-degree highway drawn as a staircase, track along a street drawn as a crossing on every plot | [FX3](docs/queue/FX3.md) |
| TM1 | Two batches' checks at once on the PC: timing assertions soft while another batch or the game runs, hard at the ship gate; batch-check.sh in Git Bash | [TM1](docs/queue/TM1.md) |
| FR2 | The Freight sector: Haulage's trucks carry the city leg, vans for goods only | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR3 | Ports as a landlord port, and the Bulk Berth | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR4 | The freight screens, and Build's Infrastructure and Freight tabs | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR5 | Trucks drawn on the map, near zoom only, with a Settings toggle on by default (Jerus, 2026-10-12) | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR6 | The railway carries the city's own bulk (D4), measured after FR2, with the port yard (D7) | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
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
| FX1-2 | Merge the held `batch/FX1-with-preflight` (e821d7f): a sector's credit test before its share sale; HealthCheck 13 accepted red (Jerus, 2026-10-12: "accept the red"), so it joins the gate's known reds | `runs/fixFX1-notes.md` ★7, in the project's `claude/todo.md` |
| CC1-S | Keep care essential even when it is not financially self-supporting; investigate why seeds 4 and 5 still stall around 3,000 people despite care, school and pensions | `runs/fixCC1-notes.md` ★ |

## Pending

Not done, and waiting on Jerus.

| item | what is left | why it waits | brief |
|---|---|---|---|
| FR0-L | What FR0 did not build: access roads from every terminal and yard to the nearest ramp (D17), sidings into the bulk works (D14), the cloverleaf's and trumpet's reserved ground (D19; every highway crossing is a stack meanwhile), kerb corners and estate T's | each needs the district plans to route or keep ground the runs cannot see (the runs are laid before the plans); his call whether they are a batch, and whether FR2's Truck Depot should come first | [FR0-notes §4](docs/queue/FR0-notes.md#4-not-built) |
| FR0-C | FR0 checked by eye on the PC: the rail, yards and junctions at the near view (his check of 0.8.3, 2026-10-18, found the yards' and the diagonal highway's faults: FX3); headless PNGs of MapCheck's yard city are beside the notes. (EarthTerrainCheck 8's screen time is settled: 27.8 ms on the PC in the ship's suite, 2026-10-18.) And his calls on the batch's stars (the gateway arm grown first, the yards' looks, TRANSPORT's colour). His eye check of 2026-10-18 found the world line over the sea and its label clipped, a highway stub alone in a block and a glyph on a highway: FX2 (0.8.3) settled the first three and found the glyph a diamond, by FR0's rule | his calls | [FR0-notes §2, §5](docs/queue/FR0-notes.md), [FX2-notes](docs/queue/FX2-notes.md) |
| FR1-C | FR1 checked by eye on the PC: the trains' pace on the boats' clock, and the legend's train; headless PNGs of MapCheck's yard city are beside the notes. And his calls on the batch's stars: past 30 trains a month each drawn train stands for several, a locomotive at each end, no trains in a city with no gateway, bulk kept off the station while it handles boxes. His eye check of 2026-10-18 found a train over open sea in a V: FX2 (0.8.3) runs the trains in from where the track meets the city's edge on land | his calls | [FR1-notes §2, §5](docs/queue/FR1-notes.md), [FX2-notes](docs/queue/FX2-notes.md) |
| FX2-C | FX2 checked by eye on the PC: his Halifax railway's line to the world now leaves north from the city's north-west and its trains run in on corridor 2's track; his highway gains a line south; a highway stub that reaches no arterial ramp is not drawn; headless PNGs of his city are in `docs/queue/FX2/`. And his calls on the batch's stars (FX2-notes §2) | his calls (his eye check on 2026-10-18 confirmed the line to the world and the highway; what else it found is FX3) | [FX2-notes §2, §5](docs/queue/FX2-notes.md) |
| EG1 | Deposits where the geology says: oil by known petroleum, iron on shields and recorded deposits, fewer and bigger fields (why his Nova Scotia cities had oil, then iron, all round them) | decided (Jerus, 2026-10-12, "all rec" on D20): its own batch after his review of the industrial calls, which comes after research and freight; it waits here until that review | [spec-earth2-terrain §8.5](docs/studies/spec-earth2-terrain.md#85-the-recommendation-eg1-deposits-where-the-geology-says-d20-your-call) |
| CC1-M | The city map did not survive a save once: LongPlaytest -Dplaytest.childcare at m1,184 (slot 3), a run the map sidecar has not met before; not childcare's - nothing in the model reads the map | a bug for a batch of its own, MapCheck's area | `runs/fixCC1-notes.md` 7 |
| I13-L | Two notes the I13 item carried that its brief did not build: the landlords' card asking the mortgage lender's test (C1's note: a door on Game round Mortgage.decide()), and the land office's Ore card, still iron's alone (I6's note) | neither is a materials card; his call whether they are a batch | `runs/fixI13-notes.md` 7 |
| I14-C | The materials on the map checked by eye on the PC - a pan over an industrial city at the near view: the pits on their sites, a Large Open-Pit Mine as twenty, the works' campuses, the plantations as forest, a zone's tint; headless PNGs of a fixture city are beside the notes. And his calls on the batch's stars: a plantation drawn on a cell's 31 x 31 plots of its 105 x 105 (★3), the arc furnaces and mini-mills not gathered into steel's campus (★4), sited works grey in the far views (★6) | his PC check (spec-materials §5's I14 row), and his calls | [fixI14-notes §8](docs/queue/I14-notes/fixI14-notes.md) |
| W1-P | DistrictPlan can leave a street piece walled in by buildings and mines over a dense iron cluster (join() never walks over a building): MapCheck's dry place one cell south, the x 10,000 copy, district (1, -1); no fixture stands there, and I14's drawing did not meet it | Jerus expects the Freight batches may partly fix street layout; reassess after Freight to see whether a separate join() fix is still needed | [fixI14-notes §7](docs/queue/I14-notes/fixI14-notes.md) |
| I12-C | The nine materials pictures checked by eye on the PC at 1,389 x 868 - the probe's words and nine headless PNGs are beside the notes; and copper drawn in tonnes of the good, not of copper contained as the mockups' line says (★2: the model holds no copper content for a good) | his PC check (spec-materials §5's I12 row), and his call on ★2 | [fixI12-notes §8](docs/queue/I12-notes/fixI12-notes.md) |
