# Roads, islands and ships: the prototypes

Not game code. Python 3 with numpy and Pillow; each script takes the game's `src/main/resources/buildings.json` (0.7.79) as its first argument.

| file | what |
|---|---|
| `proto2.py` | **the district plan** of `spec-roads-and-ports.md` §2: cells, bands, growth order, homes and estate layouts, the surface ladder (`plan_ladder`), placement, the one-network join, the checks, the picture |
| `roads_proto.py` | helpers `proto2.py` imports (types from `buildings.json`, MapCheck's `JERUS_COUNTS`, the test ground, colours); its own `Plan` is the first try, a plain block grid, kept because it showed the grid wastes ground round big buildings |
| `hw.py` | highways on corridors (§2.7): the spine, the sea rule, interchanges |
| `islands.py` | landmasses, the two-port rule, routes on the sea grid, the offing (§3, §4) |
| `boards_render.py` | draws every picture on the study's boards and writes `facts.json` |
| `capacity.py`, `capacity_flat.py` | how full a district the plan can draw (§2.10) |
| `facts.json` | the figures the boards and the spec quote |

Run: `python3 boards_render.py buildings.json out/` (about 30 s), `python3 capacity_flat.py buildings.json` (about 90 s).
