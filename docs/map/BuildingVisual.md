# BuildingVisual.java - 290 lines · 16 methods · 23 constants · model

`ham/citybuildersim/BuildingVisual.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> or well stands on the sites of the resource whose good it makes. Two types
> are named by their permanent ids, home daycare (15) and home care (22),
> drawn as homes because they are homes (spec-land star 10).
> 
> WHAT A TILE DRAWS (0.7.64, batch L2; J3b's visual counts gone): one
> building for every model building, of its type, on the type's own land
> (FOOTPRINTS below) - a Low-Rise Apartments block one block of 2 x 3 plots,
> not a street of houses; a kind the city has none of is not drawn.

**Uses:** [BuildingType](BuildingType.md) (13), [Resource](Resource.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [TilePainter](TilePainter.md) (2), [World](World.md) (1), [LandManager](LandManager.md) (1)

**Used by (8):** [CityMap](CityMap.md), [Game](Game.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 37 | THE TEN CLASSES (the mockup's TYPES, in its order) |
| 83 | · road kinds |
| 100 | ONE TYPE |
| 188 | FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `BuildingVisual.HOME` | `0` | Homes: houses, and the flats drawn darker. |
| 44 | `BuildingVisual.SHOP` | `1` | Shops: convenience and grocery stores, the bank's branches, boutiques, diners. |
| 46 | `BuildingVisual.OFFICE` | `2` | Offices: business services. |
| 48 | `BuildingVisual.INDUSTRY` | `3` | Industry: bakeries to steel, construction, the railway and the car plants. |
| 50 | `BuildingVisual.FARM` | `4` | Farms: on open grass only. |
| 52 | `BuildingVisual.UTILITY` | `5` | Utilities: power, water and the transit depots. |
| 54 | `BuildingVisual.SCHOOL` | `6` | Schools, colleges and universities. |
| 56 | `BuildingVisual.HEALTH` | `7` | Health: clinics, hospitals, care and the cemeteries. |
| 58 | `BuildingVisual.SAFETY` | `8` | Safety: police and the jails. |
| 60 | `BuildingVisual.MINE` | `9` | Mines and wells: on their resource's sites. |
| 63 | `BuildingVisual.CLASSES` | `10` | How many classes: ten - what the pyramid sums a node's buildings by (spec-land 2.5). |
| 66 | `BuildingVisual.CLASS_NAMES` | `{ "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities", "Schools", "...` | The classes' names, as the legend writes them (the mockup's). |
| 70 | `BuildingVisual.FILL` | `{ 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e, 0xf...` | Each class's fill, 0xAARRGGBB - the mockup's TYPES. |
| 74 | `BuildingVisual.EDGE` | `{ 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d, 0xf...` | ...and its edge, drawn from 6 px a plot (the mockup's). |
| 78 | `BuildingVisual.FLATS_FILL` | `0xffd07a44` | Flats' fill: the mockup's FLATS, a darker orange than a house. |
| 81 | `BuildingVisual.FLATS_EDGE` | `0xff7e4119` | ...and their edge. |
| 86 | `BuildingVisual.NOT_A_ROAD` | `0` | Not a road. |
| 88 | `BuildingVisual.GRAVEL` | `1` | A gravel road: no freight grade. |
| 90 | `BuildingVisual.PAVED` | `2` | A paved road: a freight grade under one. |
| 92 | `BuildingVisual.HIGHWAY` | `3` | An elevated highway: a freight grade of one. |
| 95 | `BuildingVisual.DRAWN_AS_HOMES` | `{ 15, 22 }` | The permanent ids of the two care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Daycare (15) and Home Care Service (22) - each still drawn, one for one, on its own land (0... |
| 98 | `BuildingVisual.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. |
| 248 | `BuildingVisual.ORDERS` | `java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>())` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 258 | **type** `public final class BuildingVisual` | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map pla... |
| 35 | 1 | `private BuildingVisual()` |  |

### THE TEN CLASSES (the mockup's TYPES, in its order) (lines 37-82)

### road kinds (lines 83-99)

### ONE TYPE (lines 100-187)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 12 | **type** `public record Type(int id, BuildingType category, int cls, boolean flats, int road, Resource site, boolean ...` | One building type as the map draws it. |
| 124 | 1 | `public boolean drawn()` _(in BuildingVisual.Type)_ | Whether it is drawn as a building: not a road. |
| 127 | 1 | `public int fill()` _(in BuildingVisual.Type)_ | Its fill. |
| 130 | 1 | `public int edge()` _(in BuildingVisual.Type)_ | Its edge. |
| 134 | 24 | `public static Type of(BuildingsTemplate t)` | A template as the map draws it. |
| 160 | 15 | `public static int classOf(BuildingType c)` | A category's class (the mockup's ten). |
| 180 | 7 | `public static Type[] table(List<BuildingsTemplate> templates)` | Every type of a list of templates, indexed by id (null where no template has that id): the table the map is drawn from. |

### FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) (lines 188-290)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 6 | `public static int[] footprint(Type t)` | The whole plots a type is drawn on, {across, down}: its land in plots, round(sqrt(plots)) across and round(plots / across) down, at least one each way and never past a tile - within half a plot a side of its own groun... |
| 219 | 6 | `public static int cells(Type t)` | The whole plots a type is drawn on: its footprint's, or a road's own plots rounded (a Gravel Road 46, a Paved Road 26, an Elevated Highway 7) - what a district's room and a tile's are kept in. |
| 233 | 3 | `public static int[] rankedTypes(Type[] types)` | The order buildings are dealt to a district's tiles and placed on a tile: the largest footprint first (cells()), then by type id - every drawn type of the table by rank. |
| 238 | 3 | `public static int[] cellsById(Type[] types)` | ...and each type's whole plots, by id (cells()): the table's order and plots worked out once a table, the painter's every tile. |
| 243 | 4 | `public static int[][] footprintsById(Type[] types)` | ...and each type's footprint by id, {across[], down[]} (footprint()). |
| 251 | 18 | `private static int[][] orderOf(Type[] types)` | A table's {order, ranks, cells, across, down}, kept by the table itself. |
| 270 | 10 | `private static int[] rank(Type[] types)` |  |
| 282 | 3 | `public static int[] placeRanks(Type[] types)` | ...and each type's rank in it, by id (-1 for a road or no type). |
| 287 | 3 | `public static double plotSide(Type t)` | The side a one-plot building is drawn at, as a share of its plot: the square root of its land in plots, a House's 0.91 and a Convenience Store's 0.72, so its drawn area is its own; a whole plot at a plot or more. |

