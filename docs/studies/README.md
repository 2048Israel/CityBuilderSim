# Design studies

A design study is read-only work done before a batch: it reads the code,
measures what is wrong, prototypes the fix outside the game and writes down
the decisions Samuel (Jerus) has to make. Since 2026-10-11 the studies live
here, each a note with its prototypes and boards beside it (Samuel: "just put
them on github no? thats where studies will be"). The batches a study plans go
in the project's `claude/todo.md`, and the batch write-ups stay in the project
as before.

Newest first. The last column is what the study led to, by batch and version,
where it has been built.

| study | date | what | batches |
|---|---|---|---|
| [spec-central-bank.md](spec-central-bank.md) | 2026-10-20 | The central bank: the rate as a target range moved in quarter points at decisions every two months (unscheduled past 3 points), an overnight rate between reserves' rate and the window read from the bank's funding, and the remittance as the Fed's income statement with its deferred asset; the remittance puzzle explained from Jerus's saves (the window's interest with cash, the treasury's own overdraft interest without); copies of Halifax played 20 years on in each rule; the model, the data and five boards in `central-bank/` | CB1 to CB3 (proposed, not queued) |
| [spec-nations.md](spec-nations.md) | 2026-10-20 | The world as 196 nations: what each makes and uses, its money, its borders and its path (Historical from 2000, or Random from a seed); each good's price in each nation from one spatial price equilibrium, solved each January on Jerus's partner rule (measured: about 50 ms), the city's month by the slopes (Canada, the US or Portugal by netback), the band from the world, the vault in eight moneys, land bought from its nation; the prototype, the render and four boards in `nations/` | WN0 to WN7 (queued) |
| [spec-city-layout.md](spec-city-layout.md) | 2026-10-20 | The city's layout (CL1): why the map is a grid, why buildings move (each district packed again from its counts) and why the uses ring and the ring moves out (the bands' order and the spill of what a district cannot draw); streets varied inside the blocks, slanted avenues and curves, and a log of arrivals so buildings stay and quarters keep their use; costs measured on Jerus's cities; prototypes and renders in `city-layout/` | CL2 to CL4 (planned) |
| [spec-earth2-terrain.md](spec-earth2-terrain.md) | 2026-10-12 | Earth 2: slope measured from NASADEM over 90 m in four classes (three greys, and snow as earth-1's ice), foothill site preparation as builders' work and a land price for each class, highways and rail by slope, a shallow-sea class, short crossings and long bridge orders (all or nothing) that link landmasses, real depth in place of E4's guess, and why Samuel's Nova Scotia cities had oil and iron all round them; `earth-2.bin` baked (136 MB; the release is Samuel's), its pipeline in `tools/earth/`, prototypes, figures and the mockups in `earth2/` | ET1 to ET6 (queued); EG1 (his call) |
| [spec-city-debt.md](spec-city-debt.md) | 2026-10-12 | The city's debt priced and sold as a treasury does: a curve from the dial the market expects, bills, bonds in reopened lines, an amortizing bond, floating and linked notes, callables, the auction, effective interest, the buyback as a reverse auction; the model and five boards in `city-debt/` | CD1 to CD6 (queued) |
| [spec-freight-and-infrastructure.md](spec-freight-and-infrastructure.md) | 2026-10-11 | Freight as one sector with three divisions (trucks for the city leg, the railway, ports run as a landlord port), the Bulk Berth, Build › Infrastructure, and the map's network: gateways, the main line and yards, rail drawn as rail, trains, access roads, junctions by rule; "today" renders, their harness and the mockups in `freight/` | FR0 to FR6 (queued, FR5 and FR6 his call) |
| [spec-globe.md](spec-globe.md) | 2026-10-11 | The Earth's map as a globe, ice and desert on the ground, panning with W, A, S and D, and a minimap with district pictures; prototypes in `globe/` and `minimap/` | E7 to E9, MM1 and MM2 (planned) |
| [research-1900.md](research-1900.md) | 2026-10-11 | Research from a 1900 start: what a 1900 city was and how it worked, the 1900 economy, the world to 1950, the Policy levers on Canada's dates, and a 1900-1950 catalogue of 48; the model's 1900 files in `research-model/` | R0, R0b, R0c, then R1 to R5 (planned, after every other batch) |
| [research-design.md](research-design.md) | 2026-10-10 | Research: grants buy posts, prizes draw labs, recipes and equipment, the world on history's dates, a 1951-1995 catalogue of 40; the model in `research-model/`, five mockups in `mockups-research/` | R1 to R5 (planned; its order replaced by research-1900's) |
| [spec-materials.md](spec-materials.md) | 2026-10-08 | The materials chains and zoning as a build spec on the code: two bases, field types, the planner's whole cost, captive use, the works' own power, old metal, coal's wash, forestry; the build order in its §5 | I1 to I14 (from 0.7.110) |
| [spec-oil.md](spec-oil.md) | 2026-10-08 | Oil and ports as a build spec on the code: the refinery as units, the spread rule, wells and platforms, terminals and boats | O1 to O14 (0.7.76 to 0.7.98) |
| [spec-roads-and-ports.md](spec-roads-and-ports.md) | 2026-10-08 | A district's streets by need, highways on corridors, islands and the two-port rule, routes at sea; prototypes in `roads-prototype/` | RD1 to RD6 (from 0.7.87), O13 (0.7.97) |
| [spec-earth.md](spec-earth.md) | 2026-10-08 | The world as planet Earth (the projection, the data, the ground, deposits on real geology, founding on a spot) and buying land from the map; prototypes in `earth-prototype.py` | E1 to E6 (from 0.7.111) |
| [spec-zoning.md](spec-zoning.md) | 2026-10-08 | Zones by sector and the forest law, and what they do to forestry; the forest model in `zoning-forest-model.py`; boards in the artifact "Zoning Study" | Z1 (0.7.107), Z2 (0.7.109) |
| [spec-leaderboards.md](spec-leaderboards.md) | 2026-10-08 | Rankings: the city against real nations and cities, today and projected, in three moneys; the arithmetic in `leaderboards-model.py`; boards in the canvas "Rankings Study" | not built (0.7.123) |
| [materials-chains-research.md](materials-chains-research.md) | 2026-10-08 | Iron redone, and coal, copper, wood and stone: two sectors a resource, whatever earns most, field kinds, freight in the bands; seven mockups in `mockups-materials/` | I1 to I14, the plan for 0.8.0 (I4 shipped as 0.7.123) |
| [oil-and-ports-research.md](oil-and-ports-research.md) | 2026-10-08 | The refinery as many units and the spread rule, land wells and platforms, terminals and boats, tank farms and pipelines; four mockups in `mockups-oil/` | the oil and ports batches (0.7.76 to 0.7.98) |
| [spec-sector-statements.md](spec-sector-statements.md) | 2026-10-07 | A sector's summary and its real statements; boards in the canvas "Sector Statements Study" | S1 (0.7.74), S2 (0.7.75) |
| [spec-grid.md](spec-grid.md) | 2026-10-07 | The land as a block grid: six offers a side, 120 m blocks, older cities converted | M1 to M5 (from 0.7.65) |
| [spec-land.md](spec-land.md) | 2026-10-06 | The land, the water, the world's deposits, the painted map and fuel | J1a to J4 and K (0.7.56 to 0.7.62) |
| [spec-scale.md](spec-scale.md) | 2026-10-06 | Can a city of 5 to 10 billion people run? Yes, after named fixes | 0.7.53 to 0.7.55 |
| [spec-inflation.md](spec-inflation.md) | 2026-10-04 | Inflation that answers to money: the anchor, wages, groceries at a price | P2 and P3 (0.7.42, 0.7.43) |

## The ones that moved here

Everything above but `spec-central-bank.md`, `spec-nations.md` and `spec-city-layout.md` (written here, 2026-10-20), `spec-earth2-terrain.md` and `spec-city-debt.md` (written here, 2026-10-12), `spec-globe.md`, `spec-materials.md` and `spec-oil.md`
was written in the claude.ai project "Civic Ledger: Java Game" and moved here
on 2026-10-11, byte for byte, with the same names and folders it had under the
project's `claude/`. The two build specs were written in the cloud session's
`runs/` folder and copied here the same day, byte for byte: a comment that
cites `runs/spec-materials.md` or `runs/spec-oil.md` means the copy here. So:

- **Inside those notes, `claude/<name>`** means this folder when the file is
  here (`claude/research-model/` is `research-model/`, `claude/mockups-oil/`
  is `mockups-oil/`), and the project otherwise: the batch write-ups, the
  briefs, `claude/todo.md`, `claude/index.md` and the analyses
  (`why-the-builders-stall.md`, `refinery-margins.py` and the like) stayed
  there.
- **A comment in the code that cites "the project's spec-land.md"** (or any of
  these) means the copy here. Those comments were written before the move and
  are left as they are; the next docs pass that passes one may point it here.
- **Paths under `/home/claude/`** (`scratch-grid/`, `scratch-land/`,
  `cbs-proto/` and the like) were the sandboxes the studies ran in, and are
  gone. Where a prototype outlived its sandbox it is here.
- **The canvases** ("Sector Statements Study", "Rankings Study", "Zoning
  Study") are artifacts on claude.ai, not files.

The prototypes are design aids, not game code: Python with numpy and Pillow
(the globe's coasts also need `global-land-mask`, the Earth prototype
`pyproj`), standalone HTML mockups, and one Java timing. Nothing in the game or
the harnesses reads this folder, and `tools.Stale` does not scan it.
