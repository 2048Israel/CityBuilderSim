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
| [spec-globe.md](spec-globe.md) | 2026-10-11 | The Earth's map as a globe, ice and desert on the ground, panning with W, A, S and D, and a minimap with district pictures; prototypes in `globe/` and `minimap/` | E7 to E9, MM1 and MM2 (planned) |
| [research-1900.md](research-1900.md) | 2026-10-11 | Research from a 1900 start: what a 1900 city was and how it worked, the 1900 economy, the world to 1950, the Policy levers on Canada's dates, and a 1900-1950 catalogue of 48; the model's 1900 files in `research-model/` | R0, R0b, R0c, then R1 to R5 (planned, after every other batch) |
| [research-design.md](research-design.md) | 2026-10-10 | Research: grants buy posts, prizes draw labs, recipes and equipment, the world on history's dates, a 1951-1995 catalogue of 40; the model in `research-model/`, five mockups in `mockups-research/` | R1 to R5 (planned; its order replaced by research-1900's) |
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

Everything above but `spec-globe.md` was written in the claude.ai project
"Civic Ledger: Java Game" and moved here on 2026-10-11, byte for byte, with
the same names and folders it had under the project's `claude/`. So:

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
