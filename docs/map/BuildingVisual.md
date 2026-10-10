# BuildingVisual.java - 343 lines · 16 methods · 30 constants · model

`ham/citybuildersim/BuildingVisual.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map places it from the outside of the city in.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5 and 2.6,
> star 10). Jerus's map mockup (city-map.html) drew ten kinds of building -
> homes, shops, offices, industry, farms, utilities, schools, health, safety,
> and mines and wells - each with its fill, its edge and its footprint (the
> mockup's star 8), and the game has its building types in eighteen
> categories. This is the one table between them, so the tile painter, the
> raster, the districts' pyramid and the legend J4 draws all read one answer.
> It is the picture only: nothing in the model reads it.
> 
> WHAT IT DECIDES, AND FROM WHAT. A type's category gives its class; three
> things are read off the template rather than its name, as the house rule
> asks (BuildingsTemplate's care field): a road is an INFRASTRUCTURE type with
> a road capacity and no transit, its kind by its freight grade (none gravel,
> some paved, all a highway); flats are the homes with more than one dwelling
> (the apartment types, the mockup's star 9 by type, not by people); a mine
> or well stands on the sites of the resource whose good it makes. One type
> is named by its permanent id, home care (22), drawn as a home because it
> is run from homes (spec-land star 10); id 15 was the home daycare until
> 0.7.71 made it the Small Childcare Centre, a centre, drawn as care.
> 
> WHAT A TILE DRAWS (0.7.64, batch L2; J3b's visual counts gone): one
> building for every model building, of its type, on the type's own land
> (FOOTPRINTS below) - a Low-Rise Apartments block one block of 2 x 3 plots,
> not a street of houses; a kind the city has none of is not drawn.
> 
> RAIL AS TRACK (0.7.72, batch N3). Jerus: "rail you add must connect to
> the rail network, and rail terminals prefer to sit near mines." The
> railway's lines - the Rail Spur and the Freight Line, a RAIL type that
> is not a terminal - are drawn as track, their own land in plots laid
> along a line (track()), as a road's are; a Rail Terminal (by its
> permanent id, RAIL_TERMINALS) is a building, a yard beside its track.

**Uses:** [BuildingType](BuildingType.md) (14), [Resource](Resource.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [Refining](Refining.md) (3), [TilePainter](TilePainter.md) (2), [World](World.md) (1), [LandManager](LandManager.md) (1), [StrategicReserve](StrategicReserve.md) (1)

**Used by (13):** [ChildcareCheck](ChildcareCheck.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [CityShore](CityShore.md), [DistrictPlan](DistrictPlan.md), [Game](Game.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PlanCheck](PlanCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 45 | THE TEN CLASSES (the mockup's TYPES, in its order) |
| 109 | · road kinds |
| 129 | ONE TYPE |
| 241 | FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `BuildingVisual.HOME` | `0` | Homes: houses, and the flats drawn darker. |
| 52 | `BuildingVisual.SHOP` | `1` | Shops: convenience and grocery stores, the bank's branches, boutiques, diners. |
| 54 | `BuildingVisual.OFFICE` | `2` | Offices: business services. |
| 56 | `BuildingVisual.INDUSTRY` | `3` | Industry: bakeries to steel, construction, the railway and the car plants. |
| 58 | `BuildingVisual.FARM` | `4` | Farms: on open grass only. |
| 60 | `BuildingVisual.UTILITY` | `5` | Utilities: power, water and the transit depots. |
| 62 | `BuildingVisual.SCHOOL` | `6` | Schools, colleges and universities. |
| 64 | `BuildingVisual.HEALTH` | `7` | Health: clinics, hospitals, care and the cemeteries. |
| 66 | `BuildingVisual.SAFETY` | `8` | Safety: police and the jails. |
| 68 | `BuildingVisual.MINE` | `9` | Mines and wells: on their resource's sites. |
| 71 | `BuildingVisual.CLASSES` | `10` | How many classes: ten - what the pyramid sums a node's buildings by (spec-land 2.5). |
| 74 | `BuildingVisual.CLASS_NAMES` | `{ "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities", "Schools", "...` | The classes' names, as the legend writes them (the mockup's). |
| 78 | `BuildingVisual.FILL` | `{ 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e, 0xf...` | Each class's fill, 0xAARRGGBB - the mockup's TYPES. |
| 82 | `BuildingVisual.EDGE` | `{ 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d, 0xf...` | ...and its edge, drawn from 6 px a plot (the mockup's). |
| 86 | `BuildingVisual.FLATS_FILL` | `0xffd07a44` | Flats' fill: the mockup's FLATS, a darker orange than a house. |
| 89 | `BuildingVisual.FLATS_EDGE` | `0xff7e4119` | ...and their edge. |
| 92 | `BuildingVisual.CAMPUS_FILL` | `0xff4a3f5c` | The refinery's units (0.7.97, campus()): mockup 3's refinery, #4a3f5c, a darker violet than industry's, so the campus reads as one... |
| 95 | `BuildingVisual.CAMPUS_EDGE` | `0xff7a6a90` | ...edged in its #7a6a90. |
| 98 | `BuildingVisual.TERMINAL_FILL` | `0xff2e3640` | A sea terminal's apron (0.7.97, berth()): mockup 3's quays' grey, #2e3640... |
| 101 | `BuildingVisual.TERMINAL_EDGE` | `0xff4a5866` | ...edged in its #4a5866. |
| 104 | `BuildingVisual.TANKS_FILL` | `0xff2a3540` | A tank farm's tanks (0.7.97, the refiners' Tank Farm and the city's Strategic Reserve): mockup 3's tanks, #2a3540... |
| 107 | `BuildingVisual.TANKS_EDGE` | `0xff7f8ca6` | ...ringed in its #7f8ca6. |
| 112 | `BuildingVisual.NOT_A_ROAD` | `0` | Not a road. |
| 114 | `BuildingVisual.GRAVEL` | `1` | A gravel road: no freight grade. |
| 116 | `BuildingVisual.PAVED` | `2` | A paved road: a freight grade under one. |
| 118 | `BuildingVisual.HIGHWAY` | `3` | An elevated highway: a freight grade of one. |
| 121 | `BuildingVisual.DRAWN_AS_HOMES` | `{ 22 }` | The permanent ids of the care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Care Service (22) - still drawn, one for one, on its own land (0.7.64). |
| 124 | `BuildingVisual.RAIL_TERMINALS` | `{ 64 }` | The permanent ids of the rail types drawn as a yard beside the track, not as track (0.7.72): the Rail Terminal (64). |
| 127 | `BuildingVisual.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. |
| 301 | `BuildingVisual.ORDERS` | `java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>())` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 303 | **type** `public final class BuildingVisual` | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map pla... |
| 43 | 1 | `private BuildingVisual()` |  |

### THE TEN CLASSES (the mockup's TYPES, in its order) (lines 45-108)

### road kinds (lines 109-128)

### ONE TYPE (lines 129-240)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 13 | **type** `public record Type(int id, BuildingType category, int cls, boolean flats, int road, Resource site, boolean ...` | One building type as the map draws it. |
| 170 | 1 | `public boolean drawn()` _(in BuildingVisual.Type)_ | Whether it is drawn as a building: not a road, nor a railway line (0.7.72), nor at sea (0.7.91). |
| 173 | 1 | `public int fill()` _(in BuildingVisual.Type)_ | Its fill: its class's, the flats darker, and since 0.7.97 the refinery's units, a terminal and a tank farm their own (still of their class's in the blocks and the legend's ten). |
| 176 | 1 | `public int edge()` _(in BuildingVisual.Type)_ | Its edge. |
| 180 | 31 | `public static Type of(BuildingsTemplate t)` | A template as the map draws it. |
| 213 | 15 | `public static int classOf(BuildingType c)` | A category's class (the mockup's ten). |
| 233 | 7 | `public static Type[] table(List<BuildingsTemplate> templates)` | Every type of a list of templates, indexed by id (null where no template has that id): the table the map is drawn from. |

### FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) (lines 241-343)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 6 | `public static int[] footprint(Type t)` | The whole plots a type is drawn on, {across, down}: its land in plots, round(sqrt(plots)) across and round(plots / across) down, at least one each way and never past a tile - within half a plot a side of its own groun... |
| 272 | 6 | `public static int cells(Type t)` | The whole plots a type is drawn on: its footprint's, or a road's or a railway line's own plots rounded (a Gravel Road 46, a Paved Road 26, an Elevated Highway 7; a Rail Spur 72, a Freight Line 248) - what a district's... |
| 286 | 3 | `public static int[] rankedTypes(Type[] types)` | The order buildings are dealt to a district's tiles and placed on a tile: the largest footprint first (cells()), then by type id - every drawn type of the table by rank. |
| 291 | 3 | `public static int[] cellsById(Type[] types)` | ...and each type's whole plots, by id (cells()): the table's order and plots worked out once a table, the painter's every tile. |
| 296 | 4 | `public static int[][] footprintsById(Type[] types)` | ...and each type's footprint by id, {across[], down[]} (footprint()). |
| 304 | 18 | `private static int[][] orderOf(Type[] types)` | A table's {order, ranks, cells, across, down}, kept by the table itself. |
| 323 | 10 | `private static int[] rank(Type[] types)` |  |
| 335 | 3 | `public static int[] placeRanks(Type[] types)` | ...and each type's rank in it, by id (-1 for a road or no type). |
| 340 | 3 | `public static double plotSide(Type t)` | The side a one-plot building is drawn at, as a share of its plot: the square root of its land in plots, a House's 0.91 and a Convenience Store's 0.72, so its drawn area is its own; a whole plot at a plot or more. |

