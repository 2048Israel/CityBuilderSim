# BuildingVisual.java - 503 lines · 22 methods · 57 constants · model

`ham/citybuildersim/BuildingVisual.java` - generated 2026-10-13 by CodeMap; line numbers are as of that run.

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
> 
> THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; spec-materials 5's
> I14 row). The nine materials industries drew as two kinds: a mine on the
> first site of its resource or, for every other mine, quarry, camp and
> plantation, a plain grey "Mines and wells" box, and every works a plain
> violet "Industry" box. Now a type standing on a resource's sites says so
> from the template's own site (BuildingsTemplate.site(): its resource,
> the type of field and how many sites it takes - a Large Open-Pit Mine
> twenty), and each kind of building has a look of its own (LOOKS): an open
> pit, an underground mine, a quarry, a leach plant, a coal wash plant, a
> logging camp, planted forest - and each processing industry's works, of
> one colour and placed as one campus (GROUPS: the refinery's units since
> 0.7.97, then steel's, cement's, copper's and wood's). Still the picture
> only: nothing in the model reads it.

**Uses:** [BuildingType](BuildingType.md) (14), [Resource](Resource.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (6), [Sectors](Sectors.md) (5), [Deposit](Deposit.md) (5), [Refining](Refining.md) (3), [TileRaster](TileRaster.md) (3), [TilePainter](TilePainter.md) (2), [World](World.md) (1), [LandManager](LandManager.md) (1), [StrategicReserve](StrategicReserve.md) (1)

**Used by (16):** [ChildcareCheck](ChildcareCheck.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [CityShore](CityShore.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [LandMap](LandMap.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [PlanCheck](PlanCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 59 | THE TEN CLASSES (the mockup's TYPES, in its order) |
| 123 | · the materials' looks (0.8.0, batch I14) |
| 186 | · road kinds |
| 209 | ONE TYPE |
| 401 | FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `BuildingVisual.HOME` | `0` | Homes: houses, and the flats drawn darker. |
| 66 | `BuildingVisual.SHOP` | `1` | Shops: convenience and grocery stores, the bank's branches, boutiques, diners. |
| 68 | `BuildingVisual.OFFICE` | `2` | Offices: business services. |
| 70 | `BuildingVisual.INDUSTRY` | `3` | Industry: bakeries to steel, construction, the railway and the car plants. |
| 72 | `BuildingVisual.FARM` | `4` | Farms: on open grass only. |
| 74 | `BuildingVisual.UTILITY` | `5` | Utilities: power, water and the transit depots. |
| 76 | `BuildingVisual.SCHOOL` | `6` | Schools, colleges and universities. |
| 78 | `BuildingVisual.HEALTH` | `7` | Health: clinics, hospitals, care and the cemeteries. |
| 80 | `BuildingVisual.SAFETY` | `8` | Safety: police and the jails. |
| 82 | `BuildingVisual.MINE` | `9` | Mines and wells: on their resource's sites. |
| 85 | `BuildingVisual.CLASSES` | `10` | How many classes: ten - what the pyramid sums a node's buildings by (spec-land 2.5). |
| 88 | `BuildingVisual.CLASS_NAMES` | `{ "Homes", "Shops", "Offices", "Industry", "Farms", "Utilities", "Schools", "...` | The classes' names, as the legend writes them (the mockup's). |
| 92 | `BuildingVisual.FILL` | `{ 0xffeaa572, 0xfff2c94c, 0xff5b8fd6, 0xff9076ba, 0xffe0cb84, 0xff2ea89e, 0xf...` | Each class's fill, 0xAARRGGBB - the mockup's TYPES. |
| 96 | `BuildingVisual.EDGE` | `{ 0xff9a5a2c, 0xff987718, 0xff2c5590, 0xff55407a, 0xffa99550, 0xff17635d, 0xf...` | ...and its edge, drawn from 6 px a plot (the mockup's). |
| 100 | `BuildingVisual.FLATS_FILL` | `0xffd07a44` | Flats' fill: the mockup's FLATS, a darker orange than a house. |
| 103 | `BuildingVisual.FLATS_EDGE` | `0xff7e4119` | ...and their edge. |
| 106 | `BuildingVisual.CAMPUS_FILL` | `0xff4a3f5c` | The refinery's units (0.7.97, campus()): mockup 3's refinery, #4a3f5c, a darker violet than industry's, so the campus reads as one... |
| 109 | `BuildingVisual.CAMPUS_EDGE` | `0xff7a6a90` | ...edged in its #7a6a90. |
| 112 | `BuildingVisual.TERMINAL_FILL` | `0xff2e3640` | A sea terminal's apron (0.7.97, berth()): mockup 3's quays' grey, #2e3640... |
| 115 | `BuildingVisual.TERMINAL_EDGE` | `0xff4a5866` | ...edged in its #4a5866. |
| 118 | `BuildingVisual.TANKS_FILL` | `0xff2a3540` | A tank farm's tanks (0.7.97, the refiners' Tank Farm and the city's Strategic Reserve): mockup 3's tanks, #2a3540... |
| 121 | `BuildingVisual.TANKS_EDGE` | `0xff7f8ca6` | ...ringed in its #7f8ca6. |
| 126 | `BuildingVisual.PLAIN` | `0` | A type's look: as its class draws it, a box of its class's colours (every type but the materials'). |
| 128 | `BuildingVisual.PIT` | `1` | ...an open pit on its sites: benches stepping down to its floor, in its resource's colour (the iron mines, the surface coal mines, the sulphide copper mines). |
| 130 | `BuildingVisual.SHAFT` | `2` | ...an underground mine on its site: the site's yard and spoil, its headframe in the middle (the coking coal's mine). |
| 132 | `BuildingVisual.QUARRY` | `3` | ...a quarry on its sites: straight benches cut in the stone (the three quarries). |
| 134 | `BuildingVisual.LEACH` | `4` | ...a leach plant on its site: its heaps in rows and its pond (the oxide copper's Leach & SX-EW Plant). |
| 136 | `BuildingVisual.WASH` | `5` | ...a coal wash plant: a dark works with its conveyor (the Coal Preparation Plant). |
| 138 | `BuildingVisual.CAMP` | `6` | ...a logging camp: a clearing and its log stacks (Forestry's crews). |
| 140 | `BuildingVisual.STAND` | `7` | ...planted forest: trees in rows on its ground (the Tree Plantation and the Woodlot). |
| 142 | `BuildingVisual.WORKS` | `8` | ...a processing works: its industry's own colours (GROUP_FILL), placed with the rest of its industry's as one campus. |
| 145 | `BuildingVisual.LOOKS` | `9` | How many looks. |
| 148 | `BuildingVisual.LOOK_NAMES` | `{ "", "Open pit", "Underground mine", "Quarry", "Leach plant", "Coal wash pla...` | Each look as the legend names it (PLAIN and WORKS by their class and their industry instead). |
| 152 | `BuildingVisual.LOOK_FILL` | `{ 0, 0xff6e5a4a, 0xffa39783, 0xffcfc8b4, 0xff5fae93, 0xff3a3a42, 0xffb58c5c, ...` | Each look's fill, 0xAARRGGBB: a pit's and a quarry's from their resource's colour (pitColours()), the rest their own - a shaft's yard, the heaps' green, the wash plant's coal, the camp's sawdust, the plantation's dark... |
| 155 | `BuildingVisual.LOOK_EDGE` | `{ 0, 0xff3d3129, 0xff5c5347, 0xff8d8673, 0xff2f6f5b, 0xff17171b, 0xff6d4c2a, ...` | ...and its edge from 6 px a plot. |
| 158 | `BuildingVisual.NO_GROUP` | `0` | A works' campus (GROUPS): none... |
| 160 | `BuildingVisual.REFINERY` | `1` | ...the refinery's units (0.7.97, campus())... |
| 162 | `BuildingVisual.STEEL` | `2` | ...Iron & Steel's works (sectors.HeavyIndustry: the arc furnaces and mini-mills, the Scrap Yard, the integrated route)... |
| 164 | `BuildingVisual.CEMENT` | `3` | ...Cement & Concrete's kilns and cement plants... |
| 166 | `BuildingVisual.COPPER_WORKS` | `4` | ...Copper Refining's smelters, refineries and rod mills... |
| 168 | `BuildingVisual.WOOD` | `5` | ...and Wood Products' sawmills and mills. |
| 171 | `BuildingVisual.GROUPS` | `6` | How many groups, NO_GROUP among them. |
| 174 | `BuildingVisual.GROUP_SECTORS` | `{ null, null, Sectors.HEAVY_INDUSTRY, Sectors.CEMENT_CONCRETE, Sectors.COPPER...` | The sector each processing works' group is (Sectors' keys), by group; none for NO_GROUP and the refinery's, which campus() reads. |
| 178 | `BuildingVisual.GROUP_NAMES` | `{ "", "Refinery", "Iron & steel works", "Cement works", "Copper works", "Wood...` | Each group's works as the legend names them. |
| 181 | `BuildingVisual.GROUP_FILL` | `{ 0, CAMPUS_FILL, 0xff6b4a42, 0xffb4b0a6, 0xffb0663a, 0xff8a6a44 }` | Each group's fill: the refinery's CAMPUS_FILL, steel's rust, cement's grey, copper's own and the mills' timber. |
| 184 | `BuildingVisual.GROUP_EDGE` | `{ 0, CAMPUS_EDGE, 0xffa0705c, 0xff76726a, 0xff6e3b1d, 0xff5a4128 }` | ...and its edge from 6 px a plot. |
| 189 | `BuildingVisual.NOT_A_ROAD` | `0` | Not a road. |
| 191 | `BuildingVisual.GRAVEL` | `1` | A gravel road: no freight grade. |
| 193 | `BuildingVisual.PAVED` | `2` | A paved road: a freight grade under one. |
| 195 | `BuildingVisual.HIGHWAY` | `3` | An elevated highway: a freight grade of one. |
| 198 | `BuildingVisual.DRAWN_AS_HOMES` | `{ 22 }` | The permanent ids of the care types drawn in the homes' colour, because they are run from homes (spec-land star 10): Home Care Service (22) - still drawn, one for one, on its own land (0.7.64). |
| 201 | `BuildingVisual.RAIL_TERMINALS` | `{ 64 }` | The permanent ids of the rail types drawn as a yard beside the track, not as track (0.7.72): the Rail Terminal (64). |
| 204 | `BuildingVisual.NOT_GATHERED` | `{ 11, 12 }` | The permanent ids of the works drawn in their group's colours but placed as any industry, not gathered into its campus (0.8.0, batch I14): Iron & Steel's Electric Arc Furnace (11) and Steel Mini-Mill (12), the scrap r... |
| 207 | `BuildingVisual.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | Square feet in a plot: 30 m squared in square feet, 9,687.5 - what a template's land is turned into plots by. |
| 361 | `BuildingVisual.PIT_DUST` | `0xffb8a58a` | The dust a pit's benches are blended toward: dry spoil, #b8a58a. |
| 364 | `BuildingVisual.PIT_SHADE` | `0xff2a2620` | ...and the shade its floor is: #2a2620. |
| 461 | `BuildingVisual.ORDERS` | `java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>())` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 449 | **type** `public final class BuildingVisual` | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which kind, whether it stands on a resource's sites, and whether the map pla... |
| 57 | 1 | `private BuildingVisual()` |  |

### THE TEN CLASSES (the mockup's TYPES, in its order) (lines 59-122)

### the materials' looks (0.8.0, batch I14) (lines 123-185)

### road kinds (lines 186-208)

### ONE TYPE (lines 209-400)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 26 | **type** `public record Type(int id, BuildingType category, int cls, boolean flats, int road, Resource site, boolean ...` | One building type as the map draws it. |
| 261 | 1 | `public int campusGroup()` _(in BuildingVisual.Type)_ | The campus it is placed in (0.8.0): its group where it is gathered, else NO_GROUP - what the city map and the district plan place by. |
| 264 | 1 | `public boolean drawn()` _(in BuildingVisual.Type)_ | Whether it is drawn as a building: not a road, nor a railway line (0.7.72), nor at sea (0.7.91). |
| 267 | 4 | `public int fill()` _(in BuildingVisual.Type)_ | Its fill: its class's, the flats darker, and since 0.7.97 the refinery's units, a terminal and a tank farm their own (still of their class's in the blocks and the legend's ten); since 0.8.0 a works its group's and a m... |
| 273 | 4 | `public int edge()` _(in BuildingVisual.Type)_ | Its edge. |
| 279 | 1 | `public boolean materials()` _(in BuildingVisual.Type)_ | Whether it is one of the materials industries' own (0.8.0): drawn by a look of its own, not its class's box. |
| 283 | 43 | `public static Type of(BuildingsTemplate t)` | A template as the map draws it. |
| 337 | 10 | `static int lookOf(BuildingsTemplate t, Resource site, Deposit.FieldType field, int group)` | A type's look (0.8.0, batch I14), off the template as the house rule asks: on a resource's sites, iron's, coal's and copper's mines an open pit - but coking coal's, which is mined underground, a shaft, and copper's ox... |
| 349 | 4 | `static int lookFill(Type t)` | A look's fill for a type: a pit's floor and a quarry's stone from its resource's colour, the rest the look's own. |
| 355 | 4 | `static int lookEdge(Type t)` | ...and its edge: a pit's rim. |
| 367 | 4 | `public static int[] pitColours(Resource r)` | A pit's colours from its resource's: {its floor, its benches, its rim} - the ore's colour darkened toward PIT_SHADE, blended halfway with PIT_DUST, and the dust - so an iron pit reads red, a coal pit black and a coppe... |
| 373 | 15 | `public static int classOf(BuildingType c)` | A category's class (the mockup's ten). |
| 393 | 7 | `public static Type[] table(List<BuildingsTemplate> templates)` | Every type of a list of templates, indexed by id (null where no template has that id): the table the map is drawn from. |

### FOOTPRINTS: THE MODEL'S OWN LAND (0.7.64, batch L2) (lines 401-503)

| line | len | member | says |
|---:|---:|---|---|
| 424 | 6 | `public static int[] footprint(Type t)` | The whole plots a type is drawn on, {across, down}: its land in plots, round(sqrt(plots)) across and round(plots / across) down, at least one each way and never past a tile - within half a plot a side of its own groun... |
| 432 | 6 | `public static int cells(Type t)` | The whole plots a type is drawn on: its footprint's, or a road's or a railway line's own plots rounded (a Gravel Road 46, a Paved Road 26, an Elevated Highway 7; a Rail Spur 72, a Freight Line 248) - what a district's... |
| 446 | 3 | `public static int[] rankedTypes(Type[] types)` | The order buildings are dealt to a district's tiles and placed on a tile: the largest footprint first (cells()), then by type id - every drawn type of the table by rank. |
| 451 | 3 | `public static int[] cellsById(Type[] types)` | ...and each type's whole plots, by id (cells()): the table's order and plots worked out once a table, the painter's every tile. |
| 456 | 4 | `public static int[][] footprintsById(Type[] types)` | ...and each type's footprint by id, {across[], down[]} (footprint()). |
| 464 | 18 | `private static int[][] orderOf(Type[] types)` | A table's {order, ranks, cells, across, down}, kept by the table itself. |
| 483 | 10 | `private static int[] rank(Type[] types)` |  |
| 495 | 3 | `public static int[] placeRanks(Type[] types)` | ...and each type's rank in it, by id (-1 for a road or no type). |
| 500 | 3 | `public static double plotSide(Type t)` | The side a one-plot building is drawn at, as a share of its plot: the square root of its land in plots, a House's 0.91 and a Convenience Store's 0.72, so its drawn area is its own; a whole plot at a plot or more. |

