# HY1 - the Hydro Station ({next}, 2026-10-26)

The batch's record. Its brief is the queue's row and `docs/studies/research-1900.md`
§4 (the Hydro power line) and §11 recommendation 4: Canada's power was
mostly hydro from 1900 (133,000 kW by 1900, over 90% of capacity 1920-50
[P1.12]), and the game had only coal and wind. The requirement: a Hydro
Station template that needs a river and a drop on the Earth's map. Started
on Jerus's word of 2026-10-26, beside the map session (BL1, MS2), and kept
off the map's files. Designed by Opus; the code by a Sonnet 5.5 slice at
xhigh, reviewed by Opus.

## 1. The design

Each ★ is a call, with the recommendation taken (BP1: the recommended way
is built and named here; Jerus can overturn one later).

### 1.1 What the Earth's data can say

The brief named earth-2's heights. **Earth-2 has no heights on land**:
spec-earth's E8 ruled them out of play, and ET1 kept only the slope - each
3" cell's shares of land at or above foothill (10% grade), steep (30%) and
very steep (60%), drawn a plot at a time as `World.reliefAt()` - beside the
sea's depth. The rivers are GRWL's centrelines, 30 m wide and over, each
vertex with its width in metres (`EarthData.rivers()`). So a river's own
fall, which is what a real head is, is not in the data. Its banks are.

★1 **The drop is read from the banks.** At every plot along a river's
centreline, walk out from each bank `REACH` plots (10, 300 m) across the
stream and add up each plot's rise: 30 m times its class's least grade (flat
0, foothill 0.10, steep 0.30, very steep 0.60; water and ice read flat). The
head there is the lower bank's rise - a dam backs the river up no higher
than its lower abutment. Foothill banks give 30 m, steep 90 m, very steep
180 m at most. Below `HEAD_MIN` (10 m) there is no drop. Recommended: it is
the one reading the data supports, it asks for a valley (both banks), not a
river under one hill, and it under-reads rather than over-reads (each class
at its least grade).

★2 **One site per cell, the best point, owned with its dam.** The world is
cut into `CELL` squares (128 plots, 3.84 km, four tiles). A cell's site is
its best point on any river (the most mean power; ties to the lower y, then
the lower x). The site is the city's when the plot at that point is the
city's - as a field goes whole with its centre (CityLand, spec-land ★12) -
and its dam stands on a linked landmass (ET5's rule for works). Defined on
the world, not on the ground owned: buying more of a river never merges two
sites into one. Recommended: cascades about 4 km apart is a fair reading of
a river in hills, and it leaves no way for a purchase to lower what the city
had.

**What a site yields.** Mean flow from width by hydraulic geometry: GRWL's
widths are mean-flow widths, and across the world's rivers width goes as
w = 7.2 Q^0.5 (Moody and Troutman 2002, quoted from memory, not re-read):
Q = (w / 7.2)^2 m3/s, the width capped (★2b). Mean power
P = 0.85 x 9.81 x Q x H kW (water to wire 85%). A 30 m river under foothill
banks gives 4.3 MW, a 60 m one 17 MW, a 200 m one 190 MW; under steep banks
three times as much.

★2b **The width is capped at 550 m**, the width the formula gives
Niagara's mean flow (about 5,800 m3/s). The design said 1,000 m; the slice
found GRWL keeps reservoirs and lake-like reaches as river: Shawinigan's
best reach reads 1,088 m wide, 3.4 GW at one site, about 740 stations.
Recommended: no river in Canada carries much more than Niagara past a dam
site, and a wider reach is water standing, not flow. It still reads a
reservoir at Niagara's flow; the slot limit binds only on small rivers, and
the gate that matters is a site at all.

### 1.2 The station

★3 **One 10 MW template; a site holds as many as its flow allows.** A
station is one generating set and its share of the dam: 10 MW nameplate, at
a 45% capacity factor (IRENA's world figure for hydro in recent years:
renewablewatch.in's summary of *Renewable Power Generation Costs in 2022*),
so 4.5 MW on average and `production1` 4,560 (the Wind Farm's arithmetic:
kW x 730 h / 720 kWh). A site holds floor(P / 4.5 MW) stations - its slots.
10 MW is a 1900 station: Shawinigan's first was about 7.5 MW (c. 1901),
Niagara's Adams station 1895 ten sets of about 3.7 MW each. Recommended:
the station's output stays one number, as every building's is, and the
river's size shows as how many fit.

| | Coal Power Plant (id 9) | Wind Farm (id 41) | **Hydro Station (id 144)** |
|---|---|---|---|
| nameplate, capacity factor | 325 MW, 86% | 20 MW, 40% | **10 MW, 45%** |
| `production1` | 280,000 | 8,100 | **4,560** |
| cash | 1,071,816 | 23,240 ($31.8M, $1,590/kW) | **21,920 ($30.0M, $3,000/kW)** |
| construction points | 120,000 | 1,800 | **2,450** (the coal plant's points to cash) |
| construction materials | 19,848 | 431 | **406** (both plants' 1.85% of cash) |
| own draw, water | 15, 400 | 1, 0 | **20, 0** (it takes nothing from the river) |
| jobs | 68 | 7 | **5**: 2 no diploma, 2 diploma, 1 engineer |
| land | 2,000,000 sq ft | 1,742,400 | **435,600** (10 acres: dam wings, powerhouse, yard) |
| stands on | flat and foothill | flat and foothill | **any ground, steep too** (★6) |
| `since` | - | - | **1900** (it stood before: Appleton 1882, Niagara 1895) |

Cost: $3,000 a kW, against IRENA's world average of about $2,135 (2021,
rising; transrivers.org's summary), since small stations and Canada's run
dearer. The cash is the Wind Farm's cash per dollar, 23,240 for $31.8M.

★4 **Real costs, and so the advice rarely picks it.** Per unit of average
power the station costs 4.8 of cash, the Wind Farm 2.9, the coal plant 3.8,
and the game has no intermittency (the wind farm's 40% is firm). So the
build advice, which weighs every power candidate by its life cost per unit
served, will choose wind over hydro almost everywhere, and the automatic
builder follows the advice. Recommended: keep the real figures. Hydro's day
is the 1900 city, when research (`since`) leaves coal and hydro alone and
the electricity run makes coal plants buy coal at 1900's 6.75 times the
coal a kWh; then it wins as it did in Canada.

★5 **A generated world has no hydro.** Its ground is all flat (D19), and so
is an earth-1 file's: no drop anywhere, no site, and the card says why.
Recommended: the alternative, drops drawn by the generator, is the map's
work and invents what the Earth's cities get from data.

★6 **It stands on any ground, steep too,** as a mine does
(`Game.standsAnywhere()`): a dam's wings stand on the valley's sides.
Recommended.

★7 **A station standing is never stopped by a site lost** (ground sold
back, a landmass unlinked): only new orders are held to the slots free.
Recommended: a dam does not stop when the deed changes hands.

### 1.3 The game's side

- **The order:** `Game`'s gate refuses a Hydro Station past the slots free
  (the city's sites' slots less the stations standing, on site and ahead in
  the run) as `NO_RIVER`, before land and money - the way a desalination
  plant with no sea is refused `NO_COAST`. The card's verdict `NO_RIVER`
  says "no river with a drop owned".
- **The advice and the automatic builder:** a Hydro Station is a power
  candidate only while a slot is free, and its count is held to the slots
  free; the automatic builder orders what the advice's card names, so it
  never orders one the city has no river for.
- **Derived, never saved** (as `Landmasses`): the sites are a pure function
  of the saved rectangles and the frozen Earth data, worked out again at
  every land change and at load (`LandManager.landChanged()`). No save
  field, no format bump.
- **The map:** the station is drawn as any building. A dam drawn across the
  river is left as **HY1-C**, for after MS2, because the map session is
  changing the ground's drawing.

### 1.4 The harness

`HydroCheck`: (1) the physics against its own constants; (2) sites on
synthetic ground - a valley, one bank only, a head under the least, a river
across three cells, a best point not owned, a tie; (3) a generated-world
city: no site, an order refused `NO_RIVER`, the advice offers none, the
automatic builder orders none in a run; (4) sites set by fiat: orders up to
the slots go ahead, past them `NO_RIVER`, each station adds its
`production1` to the grid, and it stands on steep ground; (5) on earth-2
when the file is named: real hydro towns against flat ones, and how often
a site turns up.

## 2. What was built

One Sonnet 5.5 slice at xhigh, reviewed by Opus; the cap changed at review.

- **`HydroSites`** (new, 440 lines): the physics (`dischargeM3s`,
  `meanKw`, `bankRiseM`, `headM`), the core `scan()` over a relief and
  segments with nothing of the Earth in it, `recount()` from the Earth's
  rivers (`EarthData.riversIn()` of the 1-degree tiles round each cell the
  city owns ground in, through `EarthTerrain.projection()`, which was
  already public: EarthTerrain is untouched), `refresh()` against the
  linked landmasses, `setForTest()` and `sitesOnWorld()` for the harness.
- **`LandManager`**: the sites recounted in `landChanged()`, refreshed in
  `linkChanged()` and where a port moves the linked set, cleared in its
  reset (the slice added the last two: without them a port linking an
  island would not link its sites, and a new city would keep the old one's).
- **The template**: `"hydro": true` (`BuildingsTemplate.isHydro()`,
  `BuildingCatalog`), id 144 at the end of `buildings.json` and of the
  built-in list, with §1.2's figures and sources.
- **`Game`**: `standsAnywhere()` takes the Hydro Station; `hydroSlots()`,
  `hydroCommitted()`, `hydroSlotsFree()`, `hasRiverFor()`;
  `BuildResult.NO_RIVER` (last in the enum) in `buildFor`, `buildStack` and
  the run's refusals, counting the run's hydro orders ahead.
- **`BuildCard`**: `VerdictKind.NO_RIVER` (last) and a `Hydro` line - slots
  free of total, sites, the best site's width and head. **`BuildScreen`**:
  NO_RIVER beside each of NO_COAST's five places, `showNoRiverMenu` (a
  river comes with land: the Land office), and the card's river line.
- **`BuildAdvice`**: a hydro candidate's unit is 0 while no slot is free,
  and its count held to the slots free. `AutoBuilder` is unchanged: it
  orders only what the advice names, through `buildStack`.
- **Harnesses**: `HydroCheck` new (§1.4); `BuildingDataCheck` compares the
  flag, dates id 144 1900 and counts 145 buildings; `RunWorksCheck`'s works
  rule takes the Hydro Station.

A city founded at Shawinigan owns no site at founding: the falls lie
outside its first ground. It has to buy the river, as a mine buys its ore.
