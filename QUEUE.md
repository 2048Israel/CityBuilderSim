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
| I9 | Forestry, with the 100-ha woodlot (running) | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| CC1 | Childcare at real demand (running) | project: `claude/decisions-2026-10-10.md` |
| I10 | Wood products | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I11 | The materials pictograms' model | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I12 | The materials pictograms drawn | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I13 | Cards and pages for the materials buildings | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
| I14 | The materials on the map (0.8.0) | [spec-materials §5](docs/studies/spec-materials.md#5-build-order) |
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
| FR0 | The map's network: gateways, the main line and yards, rail drawn as rail, access roads, junctions by rule | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR1 | Trains on the map, as the ships are | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR2 | The Freight sector: Haulage's trucks carry the city leg, vans for goods only | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR3 | Ports as a landlord port, and the Bulk Berth | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR4 | The freight screens, and Build's Infrastructure and Freight tabs | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| ET1 | Earth 2 in the game, and drawn: earth-2.bin read, slope's greys, snow and the shallow sea on the map, real depth for E4's guess | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
| ET2 | Buildable by slope: buildings on flat and foothill only, a foothill site's preparation as builders' work, mines, quarries and wells on any ground | [spec-earth2-terrain §10](docs/studies/spec-earth2-terrain.md#10-batches) |
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

## Pending

Not done, and waiting on Jerus.

| item | what is left | why it waits | brief |
|---|---|---|---|
| PT1-M1 | One Boutique opening moved the policy rate to 23% | his decision on the model; a harness first | [M1](docs/queue/PT1.md#m1) |
| PT1-M2 | The ground runs out and the city stops, quietly | his choice of three | [M2](docs/queue/PT1.md#m2) |
| PT1-M3 | The advice and automatic building disagree on the same page | his word on the wording | [M3](docs/queue/PT1.md#m3) |
| PT1-M4 | Construction earns with nobody working | is it meant? | [M4](docs/queue/PT1.md#m4) |
| FR-D | The freight study's 22 decisions | his word, each or "all rec"; FR0 to FR4 follow the recommendations until then | [spec-freight §8](docs/studies/spec-freight-and-infrastructure.md#8--decisions) |
| FR5 | Trucks drawn on the map | his call (D21), optional | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| FR6 | The railway carries the city's own bulk | his call (D4), after FR2 is measured | [spec-freight §7](docs/studies/spec-freight-and-infrastructure.md#7-batches) |
| ET-D | The Earth 2 study's 21 decisions | his word, each or "all rec"; ET1 to ET6 follow the recommendations until then | [spec-earth2-terrain §11](docs/studies/spec-earth2-terrain.md#11--decisions) |
| ET-R | The release `earth-2` on GitHub: earth-2.bin, `tools/earth/earth-2.sha256` and `tools/earth/ATTRIBUTION-2.txt` | only he makes releases, on GitHub's website; ET1 cannot ship without it. The baked file (135,965,097 bytes, sha256 6ccb4794...fa8c3, the hash in `tools/earth/earth-2.sha256`) is on his PC in `Downloads\earth-2-release`, in five parts that `join-earth-2.bat` joins and checks; `tools/earth/bake-earth-2.bat` bakes another (about 140 GB downloaded), and whichever file is released defines the sha256 | [spec-earth2-terrain §2.8](docs/studies/spec-earth2-terrain.md#28-the-bake-and-the-release) |
| EG1 | Deposits where the geology says: oil by known petroleum, iron on shields and recorded deposits, fewer and bigger fields (why his Nova Scotia cities had oil, then iron, all round them) | his call (D20); with the industrial calls, after research and freight | [spec-earth2-terrain §8.5](docs/studies/spec-earth2-terrain.md#85-the-recommendation-eg1-deposits-where-the-geology-says-d20-your-call) |
| I7-K | A works on imported feed is ordered, loses money and is sold back: a Lime Kiln seven times in the playtest (stone, 0.7.131), a Scrap Copper Refinery six times (copper, 0.7.133) | his call on I4's ★2 (weigh one more works at the prices after its own supply), and the shed rule's wait (IDLE_MONTHS 6 against FLEET_DELIVERY_MONTHS 8); Jerus, 2026-10-12: the industrial calls are reviewed after research and freight, "cause research and how freighting works will change alot of stuff" | `runs/fixI7-notes.md` ★1, in the project's `claude/todo.md` |
| FX1-2 | A sector raises shares before its own credit test and keeps them when the order falls through (FX1's item 2, built on `batch/FX1-with-preflight`, e821d7f) | it turns HealthCheck's hunger gap red, 5.2 points against 5, the shelf's half of it the weather the check already touches on main (6.0 in other years); his call among four: read the money half, a longer window, accept red, or a store planner for Retail | `runs/fixFX1-notes.md` ★7, in the project's `claude/todo.md` |
